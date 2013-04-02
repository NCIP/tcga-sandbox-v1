package gov.nih.nci.ncicb.tcga.dcc.qclive.common;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface for VcfParser.
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface VcfParser {

    /**
     * Parses the given file as a VCF file.
     *
     * @return true if parse was successful, false if not
     * @throws IOException if an error occurs while reading the file
     */
    public Boolean parse() throws IOException;

    /**
     * Parses only the headers of the VCF file.
     *
     * @return if parse was successful
     */
    public Boolean parseHeaders() throws IOException;

    /**
     * Returns the VcfFile that has already been parsed.
     * @return The parsed VcfFile
     */
    public VcfFile getVcfFile();

    /**
     * Gets the next line of data (starting with the first data line, excluding any headers).  Wil return null if there
     * is no more data.  Will skip any blank lines or header lines (start with #) found in the data block.
     *
     * @return the next data line in array form, with each item representing a different data value, or null if no more data
     * @throws IOException if there is an error reading the next line
     */
    public String[] getNextDataLine() throws IOException;

    /**
     * Gets all errors recorded so far by this parser.
     * @return errors, if any
     */
    public List<String> getErrors();

    /**
     * Gets the current line number the parser is working on.  If getNextDataLine was just called, this will return
     * the line number of that data line.
     *
     * @return line number of last fetched data line
     */
    public Integer getCurrentLineNumber();

    /**
     * Parses a value from a header into key/value pairs
     * @param lineNum the line number for this header
     * @param headerValue the value for the header, to parse
     * @param headerName the name of the header for which the valueMap is being parsed
     * @return key/value pairs in a Map
     */
    public Map<String, String> parseValueMap(final int lineNum, final String headerValue, final String headerName);
    
    /**
     * Returns a set of VCF ids found in the file
     * @return a collection of unique vcf ids
     */
    public Set<String> getVcfIds();
    
    /**
     * Closes the parser
     */
    public void close();
}


