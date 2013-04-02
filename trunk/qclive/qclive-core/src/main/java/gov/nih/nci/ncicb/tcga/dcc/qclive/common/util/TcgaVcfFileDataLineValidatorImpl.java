/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validator for TCGA specific version of the Variant Call Format files.
 * Validates the Data Line of a Vcf file
 * Separates the logic needed to validate various elements of a data line from the
 * VcfValidator
 *
 * Note: not thread-safe.  Boolean flags should be reset if you are going to reuse this.
 *
 * @author srinivasand
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class TcgaVcfFileDataLineValidatorImpl extends VcfFileDataLineValidatorImpl {

    private static final String INFO_KEY_VT = "VT";
    private static final String INFO_KEY_SVTYPE = "SVTYPE";
    private static final String INFO_KEY_RGN = "RGN";
    private static final String INFO_KEY_SID = "SID";
    private static final String INFO_KEY_GENE = "GENE";
    private static final String INFO_KEY_MATEID = "MATEID";
    private static final String INFO_KEY_PAIRID = "PARID";
    public static final String INFO_KEY_EVENT = "EVENT";
    private static final String INFO_VALUE_SNP = "SNP";
    private static final String INFO_VALUE_INS = "INS";
    private static final String INFO_VALUE_DEL = "DEL";  
    private static final String INFO_VALUE_BND = "BND";
    private static final String INFO_VALUE_FND = "FND";
    public static final String FORMAT_VALUE_SS = "SS";
    public static final String FORMAT_VALUE_DP = "DP";
    public static final String FORMAT_VALUE_BQ = "BQ";
    public static final String FORMAT_VALUE_AD = "AD";
    public static final String FORMAT_VALUE_DP4 = "DP4";
    private static final String FORMAT_VALUE_TE = "TE";
    private static final String INFO_KEY_VLS = "VLS";
    
    private static final String SAMPLE_VALUE_EXON = "exon";
    private static final String SAMPLE_VALUE_NA = "na";
    
    /**
     * Regular expression for INFO data Id that require the 'geneAnno' header
     */
    private static final String INFO_DATA_ID_REQUIRING_GENE_ANNO_HEADER_REGEXP = "^SID|GENE|RGN$";


    /**
     * Regular expression for CHROM data Id that require the 'assembly' header
     */
    private static final String CHROM_DATA_ID_REQUIRING_ASSEMBLY_HEADER_REGEXP = "^<[aA-zZ0-9]+>$";

    /**
     * Regular expression for SS value in TCGA VCF files
     */
    private static final Pattern TCGA_VCF_FORMAT_SS_VALUE_REGEXP = Pattern.compile("^[\\.0-5]$");

    /**
     * Regular expression for SS value in TCGA VCF files
     */
    private static final Pattern TCGA_VCF_FORMAT_TE_VALUE_REGEXP = Pattern.compile("SIL|MIS|NSNS|NSTP|FSH|NA");

    private static final Pattern TCGA_VCF_INFO_RGN_VALUE_REGEXP = Pattern.compile("(5_utr|3_utr|exon|intron|ncds|sp|,)+");
    private static final Pattern TCGA_VCF_INFO_VT_VALUE_REGEXP = Pattern.compile(INFO_VALUE_SNP + "|" + INFO_VALUE_INS + "|" + INFO_VALUE_DEL);
    private static final Pattern TCGA_VCF_INFO_SVTYPE_VALUE_REGEXP = Pattern.compile(INFO_VALUE_BND + "|" + INFO_VALUE_FND);
    private static final Pattern TCGA_VCF_INFO_VLS_VALUE_REGEXP = Pattern.compile("[0-5]");
    private final Map<String, RegexMessagePair> infoKeyRegexValidators = new HashMap<String, RegexMessagePair>() {{
        put(INFO_KEY_RGN, new RegexMessagePair(TCGA_VCF_INFO_RGN_VALUE_REGEXP, "is RGN and should be in 5_utr, 3_utr, exon, intron, ncds, sp but found '%s'"));
        put(INFO_KEY_VT, new RegexMessagePair(TCGA_VCF_INFO_VT_VALUE_REGEXP, "is VT and should have one of SNP, INS or DEL but found '%s'"));
        put(INFO_KEY_SVTYPE, new RegexMessagePair(TCGA_VCF_INFO_SVTYPE_VALUE_REGEXP, "SVTYPE should have one of BND,FND but found '%s'"));
        put(INFO_KEY_VLS, new RegexMessagePair(TCGA_VCF_INFO_VLS_VALUE_REGEXP, "VLS should be one of 0, 1, 2, 3, 4, 5 but found '%s'"));
    }};

    private static final Pattern TCGA_VCF_INFO_RNASEQ_ANNOTATIONS_REGEXP =
            Pattern.compile(INFO_DATA_ID_REQUIRING_GENE_ANNO_HEADER_REGEXP);

    private static final String TCGA_VCF_CHROM_NAME_REGEXP = "(MT|[XY]|[1-9]|1[0-9]|2[0-2]|<[^\\s<>]+>)";
    private static final String SVALT_POS_REGEX = "[0-9]+";
    private static final String SVALT_SEQ_REGEX = "([ACGTN]+|\\.)";
    private static final String SVALT_CHROMID_REGEXP = "<[^\\s<>]+>";

    private static final String ALT_VALUES_SEPARATOR = ",";


    /**
     * This boolean will be set to <code>true</code> *AFTER* validateDataLine() has been called
     * if info data for the data line requires the 'geneAnno' INFO header
     */
    private boolean foundInfoDataRequiringGeneAnnoInfoHeader = false;

    /**
     * This boolean will be set to <code>true</code> *AFTER* validateDataLine() has been called
     * if info data for the data line requires the 'assembly' INFO header
     */
    private boolean foundChromDataRequiringAssemblyHeader = false;

    /**
     * This boolean will be set to <code>true</code> *AFTER* validateDataLine() has been called if
     * ALT column contains chr in <ID> format and requires the ##assembly header
     */
    private boolean foundAltDataRequiringAssemblyHeader = false;

    /**
     * Overrides the base class method and provides additional validations as necessary for
     * TCGA VCF specific files. Where base class validations are different for TCGA specific VCF files,
     * override those methods. For new methods, add the method, add a validation in this method.
     *
     * @param dataLine The line of data to be validated
     * @param vcf The <code>VcfFile</code> information containing contextual information this method refers to
     * @param lineNum The line number of the line of data
     * @param context The <code>QcContext</code> to which to log errors and warnings encountered during the validation process
     * @param previousVcfIds a {@link Set} of all the VCF Ids encountered on the previous data lines
     * @return <code>Boolean</code> true if validation was successful, false otherwise
     */
    @Override
    public Boolean validateDataLine(final String[] dataLine,
                                    final VcfFile vcf,
                                    final Integer lineNum,
                                    final QcContext context,
                                    final Set<String> vcfFileId,
                                    final Set<String> previousVcfIds) {
        boolean isValid = true;
        isValid = super.validateDataLine(dataLine, vcf, lineNum, context, vcfFileId, previousVcfIds) && isValid;
        isValid = validateDPKey(dataLine, lineNum, context) && isValid;
        isValid = validateTeCount(dataLine, lineNum, context) && isValid;
        isValid = validateRGNkey(dataLine, lineNum, context) && isValid;
        isValid = validateSvAltInfoValues(dataLine, lineNum, context) && isValid;
        isValid = validateRNASeqAnnotations(dataLine, lineNum, context) && isValid;
        return isValid;
    }
    
    /**
     * Counts the number of comma-separated values in the INFO field for the following keys: "SID", "GENE" and "RGN",
     * plus the number of comma-separated values for the "TE" field in each sample column.
     * The number for each should be the same, otherwise validation will fail.
     * @param dataLine A data line containing values for each of the columns in the data line
     * @param lineNum The line number of the data line for logging purposes
     * @param context The <code>QcContext</code> for logging purposes
     * @return <code>true</code> is validation was successful, <code>false</code> otherwise
     */
    protected boolean validateTeCount(final String[] dataLine,final Integer lineNum,
			final QcContext context) {
    	boolean isValid = true;   
    	if (dataLine.length >= VcfFileDataLineValidatorImpl.VcfColumns.FORMAT.colPos() + 1) {
            final String infoString = dataLine[VcfColumns.INFO_POS.colPos()];
            final String formatValue = dataLine[VcfColumns.FORMAT.colPos()];
            
	         // check if RGN,SID or GENE are present in INFO and TE in FORMAT
	         if (StringUtils.isNotEmpty(VcfFileValidatorUtil.getInfoValue(INFO_KEY_RGN, infoString))
	        		 	|| StringUtils.isNotEmpty(VcfFileValidatorUtil.getInfoValue(INFO_KEY_SID, infoString))
	        		 	|| StringUtils.isNotEmpty(VcfFileValidatorUtil.getInfoValue(INFO_KEY_GENE, infoString))
	            		|| VcfFileValidatorUtil.isExistsFormatKey(formatValue,FORMAT_VALUE_TE)){
	            
	        	 List<Integer> valCount = new ArrayList<Integer>();  
	        	  
	        	 String rgnValues = VcfFileValidatorUtil.getInfoValue(INFO_KEY_RGN, infoString);
	        	 String sidValues = VcfFileValidatorUtil.getInfoValue(INFO_KEY_SID, infoString);
	        	 String geneValues = VcfFileValidatorUtil.getInfoValue(INFO_KEY_GENE, infoString);
	        	 String teValues = "";
	        	 // get a count for RGN,SID,GENE and TE
	        	 
	        	 if (StringUtils.isNotEmpty(rgnValues)){
	        		 valCount.add(rgnValues.split(",").length);
	        		 
	        	 }
	        	 if (StringUtils.isNotEmpty(sidValues)){	        		
	        		 valCount.add(sidValues.split(",").length);
	        		 
	        	 }
	        	 if (StringUtils.isNotEmpty(geneValues)){
	        		 valCount.add(geneValues.split(",").length);	        		
	        	 }
	        	 
	        	 if(VcfFileValidatorUtil.isExistsFormatKey(formatValue,FORMAT_VALUE_TE)){
	        		 final List<String> sampleColumnValues = VcfFile.getSamplesColumns(Arrays.asList(dataLine));
     	        	 for (String sampleColumn:sampleColumnValues){
     	        		final String sampleTEValue = VcfFileValidatorUtil.getSampleValue(FORMAT_VALUE_TE, formatValue, sampleColumn);
     	        		teValues += "  " + sampleTEValue;
     	        		// split sample TE values
     	        		String[] sampleTEValues = sampleTEValue.split(",",-1);        	        		
     	        		valCount.add(sampleTEValues.length);
     	        	 }
	        	 }
 	        	 //must be at least one element
	        	 int controlValue = valCount.get(0);
	        	 boolean isValidNumberOfElements = true;
	        	 //check if the number of elements is different
	        	 for (int i = 0 ; i < valCount.size() ; i ++){
	        		Integer elementValue = valCount.get(i);	        	 
	        		if  (controlValue != elementValue){
	        			isValidNumberOfElements = false;
	        			break;
	        		}
	        	 }
	        	 
	        	 if (!isValidNumberOfElements){
	        		 
	        		 StringBuilder allValues = new StringBuilder();
	        		 allValues.append("VCF data validation error on line " + lineNum);
	        		 	        		 
	        		 allValues.append(". if any one of RGN,SID,GENE(in INFO) or TE(in FORMAT) are present, they should all have the " +
	        		 		"same number of values. Instead found: ");
	        		 if (StringUtils.isNotEmpty(rgnValues)){
	        			 allValues.append("RGN=" + rgnValues);
		        		 
		        	 }
		        	 if (StringUtils.isNotEmpty(sidValues)){
		        		 allValues.append(" SID=" + sidValues);
		        		 
		        	 }
		        	 if (StringUtils.isNotEmpty(geneValues)){
		        		 allValues.append(" GENE=" + geneValues);    		
		        	 }
		        	 if (StringUtils.isNotEmpty(teValues)){
		        		 allValues.append(" TE=" + teValues);
		        	 }		        	 	        		 	        		 	        		  
	        		 	        		
	        	     context.addError(allValues.toString());    
	        	     isValid = false;
                             
	        	 }	        	 	            
	    	}
    	}
    	
    	return isValid;
    }
    
    /**
     * If a data line has both RGN in INFO and TE in the sample columns, then:
	 * If RGN value is not "exon" then TE must be "NA". 
	 * This is true even if the values are comma separated.
     * @param dataLine A data line containing values for each of the columns in the data line
     * @param lineNum The line number of the data line for logging purposes
     * @param context The <code>QcContext</code> for logging purposes
     * @return <code>true</code> is validation was successful, <code>false</code> otherwise
     */
    protected boolean validateRGNkey(String[] dataLine, Integer lineNum,
			QcContext context) {
    	boolean isValid = true;    	
    	
    	if (dataLine.length >= VcfFileDataLineValidatorImpl.VcfColumns.FORMAT.colPos() + 1) {
            final String infoString = dataLine[VcfColumns.INFO_POS.colPos()];
            final String formatValue = dataLine[VcfColumns.FORMAT.colPos()];
                       
            // check if RGN is present in INFO and TE in FORMAT
            if (StringUtils.isNotEmpty(VcfFileValidatorUtil.getInfoValue(INFO_KEY_RGN, infoString))
            		&& VcfFileValidatorUtil.isExistsFormatKey(formatValue,FORMAT_VALUE_TE)){
            	
            	// get RGN value
            	String rgnValue = VcfFileValidatorUtil.getInfoValue(INFO_KEY_RGN, infoString);
            	
            	// split RGN into tokens
            	String [] rgnaValues = rgnValue.split(",",-1);
            	
            	for ( int i = 0 ; i < rgnaValues.length ; i ++){
            		String rgnaToken = rgnaValues[i];
            		// if RGN is not exon, then TE must be "NA"            	
                	if (!rgnaToken.equalsIgnoreCase(SAMPLE_VALUE_EXON)){
                		final List<String> sampleColumnValues = VcfFile.getSamplesColumns(Arrays.asList(dataLine));
        	        	for (String sampleColumn:sampleColumnValues){
        	        		final String sampleTEValue = VcfFileValidatorUtil.getSampleValue(FORMAT_VALUE_TE, formatValue, sampleColumn);
        	        		
        	        		// split sample TE values
        	        		String[] sampleTEValues = sampleTEValue.split(",",-1);        	        		
        	        		if (!sampleTEValues[i].equalsIgnoreCase(SAMPLE_VALUE_NA)){
        	        			
        	        			addErrorMessage(FORMAT_VALUE_TE, sampleTEValue,
                                        " if INFO value for RGN is not 'exon' then SAMPLE value for TE must be NA ",
                                        lineNum, context);
                                isValid = false;
        	        		}
        	        	}
                	}    
            	}            	            	        	           
            }                                   
    	}
    	
    	return isValid;
	}	   
    
    /**
     * INFO part of data line contains an Integer value for DP
     * FORMAT part of the data line contains DP hence sample lines should have an integer value specified
     * In that case, if both INFO and FORMAT are specified, then the total across all samples for the data line
     * must be equal to the value specified in the INFO part.
     * @param dataLine A data line containing values for each of the columns in the data line
     * @param lineNum The line number of the data line for logging purposes
     * @param context The <code>QcContext</code> for logging purposes
     * @return <code>true</code> is validation was successful, <code>false</code> otherwise
     */
    protected boolean validateDPKey(final String[] dataLine, final Integer lineNum, final QcContext context) {
        // since everything has checked out to this point, all keys and values should follow
        // proper syntax and all we do here is semantic validation
        boolean isValid = true;
        if (dataLine.length >= VcfFileDataLineValidatorImpl.VcfColumns.FORMAT.colPos() + 1) {
            final String infoValue = dataLine[VcfColumns.INFO_POS.colPos()];
            Integer dpValue = new Integer(0);
            final String infoDpValue = VcfFileValidatorUtil.getInfoValue(FORMAT_VALUE_DP, infoValue);
            if(!StringUtils.isEmpty(infoDpValue) && !isBlankDataValue(infoDpValue)) {
                try {
                    dpValue = Integer.parseInt(infoDpValue);
                } catch(NumberFormatException nfe) {
                    addErrorMessage(VcfFile.HEADER_TYPE_INFO, infoDpValue, "The value of DP must be an Integer", lineNum, context);
                    return false;
                }
            } else {
                return true; // cant validate something that does not exist
            }

            Integer sampleDpTotalValue = new Integer(0);
            final String formatValue = dataLine[VcfColumns.FORMAT.colPos()];
            if(!StringUtils.isEmpty(formatValue)) {
                if(VcfFileValidatorUtil.isExistsFormatKey(formatValue, FORMAT_VALUE_DP)) {
                    final List<String> sampleValues = VcfFile.getSamplesColumns(Arrays.asList(dataLine));
                    for(final String sampleValue : sampleValues) {
                        // validate only if sample column contains all data
                        if(isSampleColumnContainsAllData(sampleValue,formatValue)){
                            final String sampleDpValue = VcfFileValidatorUtil.getSampleValue(FORMAT_VALUE_DP, formatValue, sampleValue);
                            if (!isBlankDataValue(sampleDpValue)) {
                                try {
                                    sampleDpTotalValue = sampleDpTotalValue + Integer.parseInt(sampleDpValue);
                                } catch(NumberFormatException nfe) {
                                    // do not add the error message. It is already captured in validateSampleData
                                    isValid = false;
                                }
                            } else {
                                // if any SAMPLE column has dp of '.' then we can't validate this, so just return true
                                return true;
                            }
                        }else{
                            // do not add the error message. It is already captured in validateSampleData
                            isValid = false;
                        }
                    }
                } else {
                    return true; // novalidation if DP not specified in FORMAT
                }
            } else {
                return true; // novalidation if DP not specified in FORMAT
            }
            if(isValid && dpValue != null && sampleDpTotalValue != null && !(dpValue.compareTo(sampleDpTotalValue) == 0)) {
                final String value = new StringBuilder().append(" DP of ").
                                append(dpValue).
                                append(" and sample DP total of ").
                                append(sampleDpTotalValue).toString();
                addErrorMessage(VcfFile.HEADER_TYPE_INFO, value, "The value for DP in INFO column and the total of the values for DP in the sample columns must be equal if DP is specified in FORMAT column", lineNum, context);
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * This method checks the INFO column value for a data line and ensures that SVTYPE is present when an SV_ALT
     * value is provided fro the ALT column of the same data line. 
     * 
     * @param dataline - the VCF data line to be validated
     * @param lineNum - the line number where the data line is located in the VCF file
     * @param context - qclive context
     * @return true if the SVTYPE is present in the INFO column when the ALT column contains SV_ALT formatted value, false
     * otherwise
     */
    protected boolean validateSvAltInfoValues(final String[] dataline, final Integer lineNum, final QcContext context) {

    	boolean isValid = true;
    	
    	// Get the ALT column value from the dataline and evaluate it against the SV_ALT regex
        final String[] altValues = dataline[VcfColumns.ALT_POS.colPos()].split(ALT_VALUES_SEPARATOR, -1);

        for(final String altValue : altValues) {

            final Matcher svAltMatcher = getPatternForRegexp(makeSvAltRegexp(false)).matcher(altValue);
            if(svAltMatcher.matches()) {
                // We matched the SV_ALT regex, check the INFO column to ensure it contains the SVTYPE key
                final String infoValue = dataline[VcfColumns.INFO_POS.colPos()];
                if(!infoValue.contains(INFO_KEY_SVTYPE)) {
                    StringBuilder errorMessage = new StringBuilder()
                    .append("Must specify '")
                    .append(INFO_KEY_SVTYPE)
                    .append("' when using SV_ALT values for column '")
                    .append(VcfFile.HEADER_TYPE_ALT).append("'");
                    addErrorMessage(VcfFile.HEADER_TYPE_INFO, infoValue, errorMessage.toString(), lineNum, context);
                    isValid = false;
                }
            }
        }

    	return isValid;
    }
    
    /**
     * Method to validate INFO key / values      
     * @param key The key occurring in a INFO column
     * @param value The value of a key occurring in a INFO column
     * @return <code>true</code> if valid, <code>false</code> otherwise
     */
    @Override
    protected boolean validateInfoKey(final String key, final String value, final Integer lineNum, final QcContext context,final Set<String>vcfIds) {
        boolean isValid = true;
        final RegexMessagePair regexMessagePair = infoKeyRegexValidators.get(key);
        if(regexMessagePair != null) {
            if((value == null) || !regexMessagePair.getRegexPattern().matcher(value).matches()) {
                addErrorMessage(VcfFile.HEADER_TYPE_INFO, key, String.format(regexMessagePair.getMessage(), value), lineNum, context);
                isValid = false;
            }
        } else if (key.equals(INFO_KEY_MATEID) || key.equals(INFO_KEY_PAIRID)){
	       	if(value == null || !validateComplexRearrangementId(value,vcfIds)) {
	             addErrorMessage(VcfFile.HEADER_TYPE_INFO, key, "MATEID and PAIRID should refer to valid IDs", lineNum, context);
	             isValid = false;
	        }
        }
        
        return isValid;
    }

    /**
     * Process the given info data Id
     *
     * @param infoDataId the info data Id
     */
    @Override
    protected void processInfoDataId(final String infoDataId) {

        if(match(INFO_DATA_ID_REQUIRING_GENE_ANNO_HEADER_REGEXP, infoDataId)) {
            setFoundInfoDataRequiringGeneAnnoInfoHeader(true);
        }
    }

    /**
     * Validate chromosome name; must be in [1-22], X, Y, MT or <ID></>.
     *
     * @param chromName: chromosome name
     * @return true if chromosome name is valid, false ow
     */
    @Override
    protected boolean validateChromName(final String chromName, final Integer lineNum, final QcContext context) {
        if(match(CHROM_DATA_ID_REQUIRING_ASSEMBLY_HEADER_REGEXP, chromName)) {
            setFoundChromDataRequiringAssemblyHeader(true);
        }
        final boolean isValid = match(TCGA_VCF_CHROM_NAME_REGEXP, chromName);
        if ( ! isValid ) {
            addErrorMessage(VcfFile.HEADER_TYPE_CHROM, chromName, null, lineNum, context);
        }
        return isValid;
    }

    @Override
    protected void processAltValueSvAltChromId(final String altValue) {
        if(getPatternForRegexp(makeSvAltRegexp(true)).matcher(altValue).matches()) {
            setFoundAltDataRequiringAssemblyHeader(true);
        }
    }

    @Override
    public boolean isFoundInfoDataRequiringGeneAnnoInfoHeader() {
        return foundInfoDataRequiringGeneAnnoInfoHeader;
    }

    public void setFoundInfoDataRequiringGeneAnnoInfoHeader(final boolean foundInfoDataRequiringGeneAnnoInfoHeader) {
        this.foundInfoDataRequiringGeneAnnoInfoHeader = foundInfoDataRequiringGeneAnnoInfoHeader;
    }

    @Override
    public boolean isFoundChromDataRequiringAssemblyHeader() {
        return foundChromDataRequiringAssemblyHeader;
    }

    public void setFoundChromDataRequiringAssemblyHeader(final boolean foundChromDataRequiringAssemblyHeader) {
        this.foundChromDataRequiringAssemblyHeader = foundChromDataRequiringAssemblyHeader;
    }

    @Override
    public boolean isFoundAltDataRequiringAssemblyHeader() {
        return foundAltDataRequiringAssemblyHeader;
    }

    public void setFoundAltDataRequiringAssemblyHeader(final boolean foundAltDataRequiringAssemblyHeader) {
        this.foundAltDataRequiringAssemblyHeader = foundAltDataRequiringAssemblyHeader;
    }

    /**
     * validate SS format sub-field
     * @param sampleDataField
     * @param formatId
     * @param lineNum
     * @param context
     * @return true if validation passes
     */
    @Override
    protected boolean validateSSFormatID(
            final String sampleDataField, final String formatId, final String sampleColumnName,
            final Integer lineNum, final QcContext context) {
        boolean isValid = true;
        if (FORMAT_VALUE_SS.equals(formatId)){
            final String[] sampleDataFieldValues = sampleDataField.split(SAMPLE_DATA_FIELD_VALUES_SEPARATOR, -1);
            for(final String value : sampleDataFieldValues) {
                if (!TCGA_VCF_FORMAT_SS_VALUE_REGEXP.matcher(value).matches()){
                    isValid = false;
                    addErrorMessage(sampleColumnName, value, "Should be one of [.,0,1,2,3,4,5]", lineNum, context);
                }
            }
        }
        return isValid;
    }

    /**
     * validate TE format sub-field
     * @param sampleDataField The sample field that corresponds to the formatId. This is a <code>SAMPLE_DATA_FIELD_VALUES_SEPARATOR</code> list of values
     * @param formatId The formatID from the FORMAT column
     * @param lineNum The line number for logging purposes
     * @param context The QcContext for logging purposes
     * @return true if validation passes
     */
    @Override
    protected boolean validateTEFormatID(
            final String sampleDataField, final String formatId, final String sampleColumnName,
            final Integer lineNum, final QcContext context) {
        boolean isValid = true;
        if (FORMAT_VALUE_TE.equals(formatId)){
            final String[] sampleDataFieldValues = sampleDataField.split(SAMPLE_DATA_FIELD_VALUES_SEPARATOR, -1);
            for(final String value : sampleDataFieldValues) {
                if (!TCGA_VCF_FORMAT_TE_VALUE_REGEXP.matcher(value).matches()){
                    isValid = false && isValid;
                    addErrorMessage(sampleColumnName, value, "Should be one of [SIL,MIS,NSNS,NSTP,FSH,NA]", lineNum, context);
                }
            }
        }
        return isValid;
    }

    /**
     * validate rna seq annotations values
     * @param dataLine
     * @param lineNum
     * @param context
     * @return true if the validation passes
     */
    protected boolean validateRNASeqAnnotations(final String[] dataLine, final Integer lineNum, final QcContext context) {
        boolean isValid = true;

        if (dataLine.length >= VcfFileDataLineValidatorImpl.VcfColumns.FORMAT.colPos() + 1) {
            final String infoValue = dataLine[VcfColumns.INFO_POS.colPos()];
            final String infoSidValue = VcfFileValidatorUtil.getInfoValue(INFO_KEY_SID, infoValue);
            final String infoGenValue = VcfFileValidatorUtil.getInfoValue(INFO_KEY_GENE, infoValue);
            final String infoRgnValue = VcfFileValidatorUtil.getInfoValue(INFO_KEY_RGN, infoValue);
            if(StringUtils.isNotEmpty(infoSidValue)) {

            }

        }
        return isValid;
    }

    @Override
    protected boolean validateInfoLineRelationships(final String infoLine,
    		final Integer lineNum, 
    		final QcContext context){
        boolean isValid = true;

        // note these are all case-sensitive!
        boolean containsSvType = VcfFileValidatorUtil.containsInfoKey(INFO_KEY_SVTYPE, infoLine, true);
        boolean containsMateId = VcfFileValidatorUtil.containsInfoKey(INFO_KEY_MATEID, infoLine, true);
        boolean containsParId = VcfFileValidatorUtil.containsInfoKey(INFO_KEY_PAIRID, infoLine, true);
        boolean containsEvent = VcfFileValidatorUtil.containsInfoKey(INFO_KEY_EVENT, infoLine, true);

        if (containsMateId && !containsSvType) {
        	String reason = INFO_KEY_SVTYPE + " must be present whenever " + INFO_KEY_MATEID + " is found";
        	addErrorMessage(VcfFile.HEADER_TYPE_INFO,INFO_KEY_MATEID, reason, lineNum, context);
        	isValid = false;
        }
        if(containsParId && !containsSvType) {
        	String reason = INFO_KEY_SVTYPE + " must be present whenever " + INFO_KEY_PAIRID + " is found";
        	addErrorMessage(VcfFile.HEADER_TYPE_INFO,INFO_KEY_PAIRID, reason, lineNum, context);
        	isValid = false;
        }
        if(containsEvent && !containsSvType) {
            addErrorMessage(VcfFile.HEADER_TYPE_INFO, INFO_KEY_EVENT, INFO_KEY_SVTYPE + " must be present whenever " + INFO_KEY_EVENT + " is found",
                    lineNum, context);
            isValid = false;
        }
    	return isValid;
    }
    
    /**
     * Check id in value field ( can be a comma delimited String) against a set of IDs 
     * @param value value to check
     * @param vcfIds a set where to check the ids
     * @return
     */
    protected boolean validateComplexRearrangementId (final String value,final Set<String> vcfIds){    	
    	boolean isValidId = true;    	
    	if (StringUtils.isNotEmpty(value)){    		
    		if (value.contains(",")){
    			String [] idValues= value.split(",",-1);
    			for (String idValue:idValues){
    				if(!vcfIds.contains(idValue)){
        				isValidId = false;
        				break;
        			}
    			}
    		}else{
    			if(!vcfIds.contains(value)){
    				isValidId = false;
    			}
    		}    		
    	}else{
    		isValidId = false;
    	}
    	
    	return isValidId;    	
    }

    /**
     * Get regex for alternate allele seq in VCF file
     * @return regex
     */

    @Override
    protected String getAltRegex() {
        return super.getAltRegex() + "|" + makeSvAltRegexp(false);
    }

    private String makeSvAltRegexp(final boolean forSvAltChromId) {
        final String SVALT_REGEX_FORM1 = new StringBuilder().append(SVALT_SEQ_REGEX)
                                    .append("\\[")
                                    .append(forSvAltChromId ? SVALT_CHROMID_REGEXP : TCGA_VCF_CHROM_NAME_REGEXP)
                                    .append(":")
                                    .append(SVALT_POS_REGEX)
                                    .append("\\[").toString(); // seq[chr:pos[
        final String SVALT_REGEX_FORM2 = new StringBuilder().append(SVALT_SEQ_REGEX)
                                    .append("\\]")
                                    .append(forSvAltChromId ? SVALT_CHROMID_REGEXP : TCGA_VCF_CHROM_NAME_REGEXP)
                                    .append(":")
                                    .append(SVALT_POS_REGEX)
                                    .append("\\]").toString(); // seq]chr:pos]
        final String SVALT_REGEX_FORM3 = new StringBuilder().append("\\]")
                                    .append(forSvAltChromId ? SVALT_CHROMID_REGEXP : TCGA_VCF_CHROM_NAME_REGEXP)
                                    .append(":")
                                    .append(SVALT_POS_REGEX)
                                    .append("\\]")
                                    .append(SVALT_SEQ_REGEX).toString(); // ]chr:pos]seq
        final String SVALT_REGEX_FORM4 = new StringBuilder().append("\\[")
                                    .append(forSvAltChromId ? SVALT_CHROMID_REGEXP : TCGA_VCF_CHROM_NAME_REGEXP)
                                    .append(":")
                                    .append(SVALT_POS_REGEX)
                                    .append("\\[")
                                    .append(SVALT_SEQ_REGEX).toString(); // [chr:pos[seq
        final String SVALT_REGEXP = "(" + SVALT_REGEX_FORM1 + "|" + SVALT_REGEX_FORM2 + "|" + SVALT_REGEX_FORM3 + "|" + SVALT_REGEX_FORM4 + ")";
        return SVALT_REGEXP;
    }

    private class RegexMessagePair {
        private Pattern regexPattern = null;
        private String message = null;
        public RegexMessagePair(Pattern regexPattern, String message) {
            this.regexPattern = regexPattern;
            this.message = message;
        }

        public Pattern getRegexPattern() {
            return regexPattern;
        }

        public String getMessage() {
            return message;
        }
    }
}
