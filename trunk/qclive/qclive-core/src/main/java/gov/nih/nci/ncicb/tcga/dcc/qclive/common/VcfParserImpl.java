package gov.nih.nci.ncicb.tcga.dcc.qclive.common;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFileHeader;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.VcfFileDataLineValidatorImpl.VcfColumns;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Parser for VCF files.
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class VcfParserImpl implements VcfParser {

    private VcfFile vcf;
    private String filename;
    private File vcfFileInProcessing;
    private Integer currentLineNumber = 0;
    private List<String> errors; 
    private BufferedReader reader;
    private Set<String> vcfIds;
    private boolean isStreamClosed = true;    
    
	
	/**
	 * Constructor to initialize state of the parser
	 * @param vcfFile file to parse
	 */
	public VcfParserImpl(final File vcfFile){
		// make sure the object is created properly
		if(vcfFile != null){ 								
			vcf = new VcfFile();
			filename = vcfFile.getName();
			vcfFileInProcessing = vcfFile;	
			errors = new ArrayList<String>();
			vcfIds = new HashSet<String>();
		}else{			
			throw new IllegalArgumentException(" Unable to create VcfParser , vcfFile must not be null");
		}
	}

    public Boolean parseHeaders() throws IOException {
        return parse(true);
    }

	 /**
     * Parses the given file as a VCF file.
     * @return true if parse was successful, false if not
     * @throws IOException if an error occurs while reading the file
     */
	public Boolean parse() throws IOException {
        return parse(false);
    }

    private Boolean parse(boolean headersOnly) throws IOException {
        boolean isValid = true;
        FileReader fileReader = new FileReader(vcfFileInProcessing);
        reader = new BufferedReader(fileReader);
        try {                                   
            String currentLine;
            currentLineNumber = 0;
            boolean foundColumnHeader = false;
            while((currentLine = reader.readLine()) != null) {
                currentLineNumber++;

                final String trimmedCurrentLine = currentLine.trim();
                if(!currentLine.equals(trimmedCurrentLine)) {

                    isValid = false;
                    errors.add("[" + filename + "] Header on line " + currentLineNumber + " must not start or end with whitespace");
                }

                // Further parsing is done on the trimmed line so as to be able to report errors besides leading or trailing whitespace
                if (trimmedCurrentLine.startsWith("##")) {
                    handleHeader(trimmedCurrentLine.substring(2)); // remove ## before parsing
                    if (foundColumnHeader) {
                        isValid = false;
                        errors.add("[" + filename + "] Found header declaration on line " + currentLineNumber + " after column header line");
                    }
                } else if (trimmedCurrentLine.startsWith("#")) {
                    if (foundColumnHeader) {
                        isValid = false;
                        errors.add("[" + filename + "] Multiple column headers found; there should only be one");
                    }
                    handleColumnHeader(vcf, trimmedCurrentLine);
                    foundColumnHeader = true;

                } else {
                    if (trimmedCurrentLine.contains("\t")) {
                        if (!foundColumnHeader) {
                            errors.add("[" + filename + "] Missing column header line");
                            isValid &= false;
                        }
                        // need to backtrack because this line is a data line
                        currentLineNumber--;

                        if (!headersOnly) {
                            addVcfId(trimmedCurrentLine);
                            String dataLine = null;

                            while((dataLine = reader.readLine()) != null) {
                                addVcfId(dataLine);
                            }
                        }
                        
                        break;
                    } else {
                        if (trimmedCurrentLine.length() < 1) {
                            errors.add("[" + filename + "] Header line " + currentLineNumber + " is blank");
                            isValid &= false;
                        } else {
                            errors.add("[" + filename + "] Header on line " + currentLineNumber + " must start with ##");
                            isValid &= false;
                            handleHeader(trimmedCurrentLine);
                        }
                    }
                }
            }            
            
        } finally {
            IOUtils.closeQuietly(reader);
            isStreamClosed = true;
        }
        return isValid;
    }		

    @Override
    public VcfFile getVcfFile() {
        return vcf;
    }
    /**
     * Reterns the error list
     * @return list of errors found during processing
     */
    public List<String> getErrors() {
        return this.errors;
    }

    @Override
    public Integer getCurrentLineNumber() {
        return currentLineNumber;
    }
    
    @Override
    public void close(){
    	if (reader != null){
    		IOUtils.closeQuietly(reader);    		 
    	}
    	if (vcfIds != null && vcfIds.size() > 0){
    		vcfIds.clear();
    	}
    	currentLineNumber = 0;
    }
    
   @Override
    public Set<String> getVcfIds() {
		return vcfIds;
	}   
    
    /**
     * Returns next data line.
     * @return next data line of processing vcf file 
     * @throws IOException 
     */
    public String[] getNextDataLine() throws IOException {
        String nextLineString;
        String[] nextLine = null;

        nextLineString = null;
        
        if (isStreamClosed){        	
        	 if (currentLineNumber <= 0){
        		 parse();
        	 }
        	 FileReader fileReader = new FileReader(vcfFileInProcessing);            
             reader = new BufferedReader(fileReader);
             isStreamClosed = false;
             fastForwardHeader();
        }                                                        
        
        if ((nextLineString = reader.readLine()) != null) {
            if (nextLineString.startsWith("#")) {
                // we found a header, so report error and skip to next line
                errors.add("[" + filename + "] Header line found in file body at line " + currentLineNumber);
                currentLineNumber++;
                nextLine = getNextDataLine();
            } else if (nextLineString.trim().length() == 0) {
                // blank line, skip to next line
                currentLineNumber++;
                nextLine = getNextDataLine();
            } else {
                nextLine = nextLineString.split("\\t");
                if (vcf.getColumnHeader() != null && nextLine.length != vcf.getColumnHeader().size()) {
                    // report error but will return the data anyway
                    errors.add("[" + filename + "] Line " + currentLineNumber + " did not contain expected number of columns");
                }
                currentLineNumber++;
            }

        }else{
        	// reached the end, done
        	IOUtils.closeQuietly(reader);
            isStreamClosed = true;
            currentLineNumber = 0;
        }
        return nextLine;
    }
    /**
     * Parses a value from a header into key/value pairs
     * @param lineNum the line number for this header
     * @param headerValue the value for the header, to parse
     * @param headerName the name of the header for which the valueMap is being parsed
     * @return key/value pairs in a Map
     */
    public Map<String, String> parseValueMap(final int lineNum, final String headerValue, final String headerName) {

        Map<String, String> headerMap = new HashMap<String, String>();

        // values may be quoted strings containing commas and equals signs! so need to parse from start rather than splitting
        // or values may be enclosed within <>
        int indexOfEquals = headerValue.indexOf("=");
        int currentIndex = 0;
        while (indexOfEquals != -1) {

            String key = headerValue.substring(currentIndex, indexOfEquals);
            String value = null;

            int startOfValue = indexOfEquals + 1;
            if (startOfValue > headerValue.length()-1) {
                // the value is an empty string and at the end of the value...
                value = "";
                currentIndex = headerValue.length(); // we are at the end of the value
            } else {
                if ((headerValue.charAt(startOfValue) == '"')) {
                    int endOfValue = headerValue.indexOf('"', indexOfEquals+2);
                    if (endOfValue == -1) {
                        // this line is messed up
                        errors.add("[" + filename + "] Header " + headerName + ": Missing closing quotation marks for value of " + key + " on line " + lineNum);
                        currentIndex = headerValue.length(); // skip the rest of the line
                    } else {
                        value = headerValue.substring(startOfValue, endOfValue+1); // include closing quote in value
                        currentIndex = endOfValue+2; // skip comma
                    }
                } else if (headerValue.charAt(startOfValue) == '<') {
                    int endOfValue = headerValue.indexOf('>', indexOfEquals+2);
                    if (endOfValue == -1) {
                        // this line is messed up
                        errors.add("[" + filename + "] Header " + headerName + ": Missing closing > for value of " + key + " on line " + lineNum);
                        currentIndex = headerValue.length(); // skip the rest of the line
                    } else {
                        value = headerValue.substring(startOfValue, endOfValue+1); // include closing > in value
                        currentIndex = endOfValue+2; // skip comma
                    }
                } else {
                    // not double-quoted, so look for next comma to find end of value
                    int nextComma = headerValue.indexOf(",", startOfValue);
                    if (nextComma == -1) {
                        // this is the last token
                        value = headerValue.substring(startOfValue);
                        currentIndex = headerValue.length();
                    } else {
                        value = headerValue.substring(startOfValue, nextComma);
                        currentIndex = nextComma+1;
                    }
                    if (value.contains("=")) {
                        errors.add("[" + filename + "] Header " + headerName + ": Value for '" + key + "' on line " + lineNum + " contains '=' but is not double-quoted");
                    }
                }
            }
            if (headerMap.containsKey(key)){
                errors.add("[" + filename + "] Header " + headerName + ": Key '" + key + "' on line " + lineNum + " is a duplicate key");
            }
            headerMap.put(key, value);
            indexOfEquals = headerValue.indexOf("=", currentIndex);
        }

        if (currentIndex < headerValue.length()) {
            errors.add("[" + filename + "] Header " + headerName + ": Value map on line " + lineNum + " is improperly formatted near '" + headerValue.substring(currentIndex) + "'");
        }
        return headerMap;
    }	    
    
    private void fastForwardHeader() throws IOException{
    	for ( int i = 0 ; i < currentLineNumber; i ++){
    		reader.readLine();
    	}
    }
    


    private void handleColumnHeader(final VcfFile vcf, String line) {
        line = line.substring(1); // remove first character which is #
        String[] headers = line.split("\t");
        List<String> columnHeaders = Arrays.asList(headers);
        vcf.setColumnHeader(columnHeaders);
    }


    // ## has been removed before being passed in
    private void handleHeader(final String line) {
        VcfFileHeader header = null;

        int indexOfEquals = line.indexOf("=");
        if (indexOfEquals == -1) {
            errors.add("[" + filename + "] Header line " + currentLineNumber + " is improperly formatted: missing '='");
            // set name to entire line, since no equals sign
            header = new VcfFileHeader(line);
        } else {

            String headerType = line.substring(0, indexOfEquals);
            header = new VcfFileHeader(headerType);

            String headerValue = line.substring(indexOfEquals+1);
            if (headerValue.startsWith("<") && headerValue.endsWith(">")) {
                // value is a map, so parse it -- remove angle brackets first
                header.setValueMap(parseValueMap(currentLineNumber, headerValue.substring(1, headerValue.length() - 1), headerType));
            } else {
                // simple value
                header.setValue(headerValue);
            }
        }
        header.setLineNumber(currentLineNumber);
        vcf.addHeader(header);
    }
    
    private void addVcfId(String dataLine){
		 String [] lineSplt = dataLine.split("\\t");                                                        
        if (lineSplt.length >= VcfFile.VALID_COLUMN_LINE.length) {                            	 
            vcfIds.add(lineSplt[VcfColumns.ID_POS.colPos()]);
        }
	}
    
   
    
}
