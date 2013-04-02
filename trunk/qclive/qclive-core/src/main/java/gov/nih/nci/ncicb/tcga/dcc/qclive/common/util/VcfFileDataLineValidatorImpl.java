/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFileHeader;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validator for Variant Call Format files.
 * Validates the Data Line of a Vcf file
 * Separates the logic needed to validate various elements of a data line from the
 * VcfValidator
 *
 * @author srinivasand
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class VcfFileDataLineValidatorImpl implements VcfFileDataLineValidator {

    // NOTE: THESE ARE ZERO-BASED POSITIONS, NOT ONE-BASED!
   public enum VcfColumns { CHROM_NAME(0), REF_POS(1), ID_POS(2), REF_ALLELE_SEQ(3), ALT_POS(4), QUAL(5), FILTER_POS(6), INFO_POS(7), FORMAT(8);
        private final int position;
        VcfColumns(int position) {
            this.position = position;
        }
        public int colPos() {
            return position;
        }
    }

    public static final String KEY_VALUE_SEPARATOR = "=";
    private static final String DATA_VALUE_BLANK = ".";
    public static final String DATA_VALUE_SEPARATOR = ";";
    private static final String DATA_VALUE_FILTER_RESERVED = "0";
    public static final String SAMPLE_DATA_SEPARATOR = ":";
    protected static final String SAMPLE_DATA_FIELD_VALUES_SEPARATOR = ",";
    private static final String UNDETERMINED_NUMBER = ".";
    private static final Integer EXPECTED_VALUES_NUMBER_WHEN_UNDETERMINED = -1;

    private static final String GENOTYPE_UNPHASED_SEPARATOR = "/";
    private static final String GENOTYPE_PHASED_SEPARATOR = "|";
    private static final String GENOTYPE_PHASED_SEPARATOR_FOR_SPLIT = "\\|"; // When used in String.split(), it needs to be escaped

    private static final String HEADER_KEY_ID = "ID";
    private static final String GENOTYPE_ID_NAME = "GT";
    private static final String FORMAT_HEADER_TYPE_NAME = "Type";
    private static final String FORMAT_HEADER_NUMBER_NAME = "Number";

    private static final String CHROM_NAME_REGEXP = "MT|[XY]|[1-9]|1[0-9]|2[0-2]";
    private static final String ID_REGEXP = "[^\\s;]+|\\.";    
    private static final String REFERENCE_ALLELE_SEQ_REGEXP = "[ACGTN]+";
    private static final String FILTER_REGEXP = "PASS|\\.";
    private static final String ALT_REGEXP = "[ACGTN]+|\\.|<([^\\s,<>]+)>";
    private static final String STRING_FORMAT_REGEXP = "[^\"; ]*";
    private static final Integer ALT_PATTERN_ID_GROUP = 1;

    private static final String MITOCHONDRION_CHROMOSOM_NAME = "MT";
    private static final String FEMALE_CHROMOSOM_NAME = "X";
    private static final String MALE_CHROMOSOM_NAME = "Y";

    private static final int NUMBER_OF_REFERENCE_ALLELES = 1;

    // regex for other columns go here

    // Use a cash of pre-compiled regex patterns (RegExp -> Pattern), for performance improvement
    private static final HashMap<String, Pattern> PATTERN_HASHTABLE = new HashMap<String, Pattern>();
    static {
        PATTERN_HASHTABLE.put(CHROM_NAME_REGEXP, Pattern.compile(CHROM_NAME_REGEXP));
        PATTERN_HASHTABLE.put(ID_REGEXP, Pattern.compile(ID_REGEXP));
        PATTERN_HASHTABLE.put(REFERENCE_ALLELE_SEQ_REGEXP, Pattern.compile(REFERENCE_ALLELE_SEQ_REGEXP));
        PATTERN_HASHTABLE.put(FILTER_REGEXP, Pattern.compile(FILTER_REGEXP));
        PATTERN_HASHTABLE.put(ALT_REGEXP, Pattern.compile(ALT_REGEXP));
        PATTERN_HASHTABLE.put(STRING_FORMAT_REGEXP, Pattern.compile(STRING_FORMAT_REGEXP));
        // regex for other columns go here
    }

    /** List of mandatory format fields **/
    private static final List<String> MANDATORY_FORMAT_FIELDS = Arrays.asList(GENOTYPE_ID_NAME,
            TcgaVcfFileDataLineValidatorImpl.FORMAT_VALUE_DP, TcgaVcfFileDataLineValidatorImpl.FORMAT_VALUE_BQ, TcgaVcfFileDataLineValidatorImpl.FORMAT_VALUE_SS);

    /**
     * Mandatory format fields (either one or the other)
     **/
    private static final String MANDATORY_FORMAT_FIELDS_EITHER = TcgaVcfFileDataLineValidatorImpl.FORMAT_VALUE_AD;
    private static final String MANDATORY_FORMAT_FIELDS_OR = TcgaVcfFileDataLineValidatorImpl.FORMAT_VALUE_DP4;

    @Override
    public Boolean validateDataLine(final String[] dataLine, final VcfFile vcf,
                                    final Integer lineNum,
                                    final QcContext context,
                                    final Set<String> vcfIds,
                                    final Set<String> previousVcfIds) {
    	
        boolean isValid = true;

        if (dataLine.length < VcfFile.VALID_COLUMN_LINE.length) {
            // don't even try to validate the line if there are values missing...
            isValid = false;
        } else {
            String chromosomeValue = dataLine[VcfColumns.CHROM_NAME.colPos()];
            String altValue = dataLine[VcfColumns.ALT_POS.colPos()];
            Integer numAltAlleles = altValue.equals(".") ? 0 : altValue.split(",").length;
            int numGenotypes = calculateNumberOfGenotypes(numAltAlleles, chromosomeValue);

            isValid = validateReferencePosition(dataLine[VcfColumns.REF_POS.colPos()], lineNum, context) && isValid;
            isValid = validateId(dataLine[VcfColumns.ID_POS.colPos()], previousVcfIds, lineNum, context) && isValid;
            isValid = validateChromName(chromosomeValue, lineNum, context) && isValid;
            isValid = validateReferenceAlleleSeq( dataLine[VcfColumns.REF_ALLELE_SEQ.colPos()], lineNum, context ) && isValid;
            isValid = validateQual( dataLine[VcfColumns.QUAL.colPos()], lineNum, context ) && isValid;
            isValid = validateAltValue(dataLine[VcfColumns.ALT_POS.colPos()], vcf.getHeadersForType(VcfFile.HEADER_TYPE_ALT),
                    lineNum, context) && isValid;
            isValid = validateFilter(dataLine[VcfColumns.FILTER_POS.colPos()], vcf.getHeadersForType(VcfFile.HEADER_TYPE_FILTER),
                    lineNum, context) && isValid;
            isValid = validateFormatAndSamples(dataLine, vcf.getFormatHeaders(), numAltAlleles, numGenotypes, lineNum, context) && isValid;            
            
            isValid = validateInfoValue(dataLine[VcfColumns.INFO_POS.colPos()], vcf.getHeadersForType(VcfFile.HEADER_TYPE_INFO),
                    numGenotypes, numAltAlleles,
                    lineNum, context,vcfIds) && isValid;               
        }
                
        return isValid;
    }

    @Override
    public boolean isFoundInfoDataRequiringGeneAnnoInfoHeader() {

        // The non-TCGA VCF does not require the 'geneAnno' header
        return false;
    }

    @Override
    public boolean isFoundChromDataRequiringAssemblyHeader() {
        // The non-TCGA VCF does not require the 'assembly' header
        return false;
    }

    @Override
    public boolean isFoundAltDataRequiringAssemblyHeader() {
        // The non-TCGA VCF does not require the 'assembly' header
        return false;
    }

    /**
     * Uses the number of alt alleles, with the assumption that there is only 1 reference allele, and calculates the
     * number of possible genotypes by this formula
     *
     * G = ((A + R) (A + R + 1))/2
     *
     * where A = number of alt alleles and R = number of reference alleles
     *
     * Note: if chromosome is "Y" then number of genotypes is just A + R.
     *
     * @param numberOfAltAlleles number of alt alleles for this line
     * @param chromosomeValue the chromosome value for the line
     * @return the number of possible genotypes
     */
    protected Integer calculateNumberOfGenotypes(final Integer numberOfAltAlleles, final String chromosomeValue) {
        if (MALE_CHROMOSOM_NAME.equals(chromosomeValue)) {
            return numberOfAltAlleles + NUMBER_OF_REFERENCE_ALLELES;
        } else {
            return ((numberOfAltAlleles + NUMBER_OF_REFERENCE_ALLELES) * (numberOfAltAlleles + NUMBER_OF_REFERENCE_ALLELES + 1 )) / 2;
        }
    }
    

    /**
     * Validates the format column as well as the optional samples column.
     * Returns <code>true</code> if the format and sample(s) columns are valid, <code>false</code> otherwise.
     * Errors and warning encountered during validation are placed in the context.
     *
     * @param dataLine the data line
     * @param formatHeaders the format headers
     * @param numAltAlleles number of alternate allele
     * @param numGenotypes number of genotypes
     * @param lineNum the line number the data line comes from
     * @param context the context
     * @return <code>true</code> if the format and sample(s) columns are valid, <code>false</code> otherwise
     */
    protected boolean validateFormatAndSamples(final String[] dataLine,
                                               final List<VcfFileHeader> formatHeaders,
                                               final Integer numAltAlleles,
                                               final Integer numGenotypes,
                                               final Integer lineNum,
                                               final QcContext context) {
        boolean result = true;

        if(dataLine.length >= VcfColumns.FORMAT.colPos() + 1) {

            final String formatData = dataLine[VcfColumns.FORMAT.colPos()];
            final List<String> samplesData = VcfFile.getSamplesColumns(Arrays.asList(dataLine));

            final String[] formatDataFields = validateFormatData(formatData, formatHeaders, lineNum, context);

            if(formatDataFields != null) {
                final String chromName = dataLine[VcfColumns.CHROM_NAME.colPos()];
                result = validateSampleData(samplesData, formatData, formatHeaders, numAltAlleles, numGenotypes, chromName, lineNum, context) && result;
            } else {
                // Format data is invalid
                result = false;
            }
        }

        return result;
    }

    /**
     * Return <code>true</code> if the samples data is valid, <code>false></code> otherwise
     *
     *
     * @param samplesData the samples data
     * @param formatData the format data
     * @param formatHeaders the format headers
     * @param numAltAlleles number of alternate allele
     * @param numGenotypes number of genotypes
     * @param chromName the chrom name
     * @param lineNum the number of the line the data is coming from
     * @param context the context
     * @return <code>true</code> if the samples data is valid, <code>false></code> otherwise
     */
    private boolean validateSampleData(final List<String> samplesData,
                                       final String formatData,
                                       final List<VcfFileHeader> formatHeaders,
                                       final Integer numAltAlleles,
                                       final Integer numGenotypes,
                                       final String chromName,
                                       final Integer lineNum,
                                       final QcContext context) {

        boolean result = true;

        for(int i=0; i<samplesData.size(); i++) {

            final String sampleData = samplesData.get(i);
            final String[] sampleDataFields = sampleData.split(SAMPLE_DATA_SEPARATOR, -1);
            final String[] formatDataFields = formatData.split(SAMPLE_DATA_SEPARATOR, -1);
            if(isSampleColumnContainsAllData(sampleData,formatData)) {

                for(int fieldIndex=0; fieldIndex<sampleDataFields.length; fieldIndex++) {

                    final String sampleDataField = sampleDataFields[fieldIndex];

                    final String formatId = formatDataFields[fieldIndex];
                    final boolean isGenotype = GENOTYPE_ID_NAME.equals(formatId);
                    final VcfFileHeader formatHeader = getVcfFileHeaderByFormatId(formatHeaders, formatId, getSampleColumnName(i + 1), lineNum, context);
                    final String formatType = formatHeader.getValueMap().get(FORMAT_HEADER_TYPE_NAME);
                    final String formatNumber = formatHeader.getValueMap().get(FORMAT_HEADER_NUMBER_NAME);
                    result = validateSSFormatID(sampleDataField, formatId, getSampleColumnName(i + 1), lineNum, context) && result;
                    result = validateTEFormatID(sampleDataField, formatId, getSampleColumnName(i + 1), lineNum, context) && result;
                    result = validateSampleDataField(sampleDataField,
                            formatType, formatNumber, numAltAlleles, numGenotypes, isGenotype, getSampleColumnName(i + 1), lineNum, context) && result;
                }

            } else {
                result = false;
                addErrorMessage(getSampleColumnName(i+1), sampleData,
                        new StringBuilder("The number of fields in the sample column (")
                                .append(sampleDataFields.length)
                                .append(") is different from the number of fields in the format column (")
                                .append(formatDataFields.length)
                                .append(").")
                                .toString(),
                        lineNum, context);
            }
        }

        return result;
    }

    protected boolean isSampleColumnContainsAllData(final String sampleData, final String formatData){
        final String[] sampleDataFields = sampleData.split(SAMPLE_DATA_SEPARATOR, -1);
        final String[] formatDataFields = formatData.split(SAMPLE_DATA_SEPARATOR, -1);

        return (sampleDataFields.length == formatDataFields.length);
    }
    
    /**
     * validate SS format sub-field
     * @param sampleDataField
     * @param formatId
     * @param lineNum
     * @param context
     * @return true if validation passes
     */
    protected boolean validateSSFormatID(
            final String sampleDataField, final String formatId, final String sampleColumnName,
            final Integer lineNum, final QcContext context) {
        //NO SS Format validation necessary for general vcf files
        return true;
    }

    /**
     * validate TE format sub-field
     * @param sampleDataField The sample field that corresponds to the formatId. This is a <code>SAMPLE_DATA_FIELD_VALUES_SEPARATOR</code> list of values
     * @param formatId The formatID from the FORMAT column
     * @param lineNum The line number for logging purposes
     * @param context The QcContext for logging purposes
     * @return true if validation passes
     */
    protected boolean validateTEFormatID(
            final String sampleDataField, final String formatId, final String sampleColumnName,
            final Integer lineNum, final QcContext context) {
        //NO TE Format validation necessary for general vcf files
        return true;
    }

    /**
     * Validate a sample data field.
     *
     * @param sampleDataField the non blank (!= dot) data field value
     * @param formatType the format type
     * @param formatNumber the format number
     * @param numAltAlleles number of alternate allele
     * @param numGenotypes number of genotypes
     * @param isGenotype <code>true</code> if it is a genotype, <code>false</code> otherwise
     * @param sampleColumnName the sample column name
     * @param lineNum the number of the line the data is coming from
     * @param context the context
     * @return <code>true</code> if the samples data field is valid, <code>false></code> otherwise
     */
    private boolean validateSampleDataField(final String sampleDataField,
                                            final String formatType,
                                            final String formatNumber,
                                            final Integer numAltAlleles,
                                            final Integer numGenotypes,
                                            final boolean isGenotype,
                                            final String sampleColumnName,
                                            final Integer lineNum,
                                            final QcContext context) {
        boolean result = true;

        final String[] sampleDataFieldValues = sampleDataField.split(SAMPLE_DATA_FIELD_VALUES_SEPARATOR, -1);
        final Integer expectedNumberOfValues = getExpectedNumberOfValues(formatNumber, numAltAlleles, numGenotypes);

        // Validate number of values
        if(expectedNumberOfValues != null && expectedNumberOfValues != sampleDataFieldValues.length) {
            result = false;
            addErrorMessage(sampleColumnName, sampleDataField, "Incorrect number of values. Expected " + expectedNumberOfValues
                    +  " but found " + sampleDataFieldValues.length + ".", lineNum, context);
        }

        // Validate values type
        for(final String value : sampleDataFieldValues) {

            if ("Integer".equals(formatType) && !(isBlankDataValue(value) || isInteger(value))) {
                result = false;
                addErrorMessage(sampleColumnName, value, "Should be an integer.", lineNum, context);

            } else if ("Float".equals(formatType) && !(isBlankDataValue(value) || isFloat(value))) {
                result = false;
                addErrorMessage(sampleColumnName, value, "Should be a floating point number.", lineNum, context);

            } else if ("Character".equals(formatType) && value.trim().length() != 1) {
                result = false;
                addErrorMessage(sampleColumnName, value, "Defined as a Character but found value '" + value + "'.", lineNum, context);

            } else if ("String".equals(formatType) && !isStringFormatType(value)) {
                result = false;
                addErrorMessage(sampleColumnName, value, "String cannot contain whitespace, semi-colon, or quote, but found '" + value + "'.", lineNum, context);
            }
        }

        // Extra validation if it is a genotype
        if(isGenotype) {

            if(sampleDataField != null &&
                    (sampleDataField.contains(GENOTYPE_UNPHASED_SEPARATOR)
                            || sampleDataField.contains(GENOTYPE_PHASED_SEPARATOR))) {
                // Should be bound by the number of altAlleles and refAllele
                // it is assumed that the number of refAlleles is 1
                result = validateNumAllelesInSample(sampleDataField, numAltAlleles, sampleColumnName, lineNum, context) && result;

            } else { // It should be a single allele
                result = isAlleleValueValid(sampleDataField, numAltAlleles, sampleColumnName, sampleDataField, lineNum, context) && result;
            }

        }

        return result;
    }

    /**
     * Return <code>true</code> if the sample data field contains less than or equal alleles than
     * that allowed by the combination of REF and ALT alleles and each allele is valid,
     * <code>false</code> otherwise
     *
     * @param sampleDataField the sample data field to validate
     * @param numAltAlleles the number of alternate alleles
     * @param sampleColumnName the sample column name
     * @param lineNum the number of the line on which the sample data field was found
     * @param context the context
     * @return <code>true</code> if the sample data field contains less than or equal alleles than
     * that allowed by the combination of REF and ALT alleles and each allele is valid,
     * <code>false</code> otherwise
     */
    private boolean validateNumAllelesInSample(final String sampleDataField,
                                              final Integer numAltAlleles,
                                              final String sampleColumnName,
                                              final Integer lineNum,
                                              final QcContext context) {

        boolean result = true;

        String[] alleleValues = sampleDataField.split(GENOTYPE_UNPHASED_SEPARATOR, -1);
        if(alleleValues.length == 1) { // Not an unphased genotype
            alleleValues = sampleDataField.split(GENOTYPE_PHASED_SEPARATOR_FOR_SPLIT, -1);
        }

        Integer numAllelesAllowed = numAltAlleles + NUMBER_OF_REFERENCE_ALLELES;
        if(alleleValues.length <= numAllelesAllowed) {

            for(final String alleleValue : alleleValues) {
                result = isAlleleValueValid(alleleValue, numAltAlleles, sampleColumnName, sampleDataField, lineNum, context);
            }

        } else {
            result = false;
            addErrorMessage(sampleColumnName, sampleDataField,
                    "Genotype field cannot contain more than " + numAllelesAllowed + " alleles, but found " + alleleValues.length + ".", lineNum, context);
        }
        return result;
    }

    /**
     * Return <code>true</code> if the value is a format of type 'String', as defined by the header Type
     * 
     * @param value the value to validate
     * @return <code>true</code> if the value is a format of type 'String', as defined by the header Type
     */
    private boolean isStringFormatType(final String value) {
        return match(STRING_FORMAT_REGEXP, value);
    }

    /**
     * Return <code>true</code> if the allele value is valid, <code>false</code> otherwise
     *
     * @param alleleValueAsString the allele value, as String
     * @param numAltAlleles the number of alternate alleles
     * @param sampleColumnName the sample column name
     * @param sampleDataField the sample data field
     * @param lineNum the line number
     * @param context the context   @return <code>true</code> if the allele value is valid, <code>false</code> otherwise
     */
    private boolean isAlleleValueValid(final String alleleValueAsString,
                                       final Integer numAltAlleles,
                                       final String sampleColumnName,
                                       final String sampleDataField,
                                       final Integer lineNum,
                                       final QcContext context) {

        boolean result = true;

        if(!DATA_VALUE_BLANK.equals(alleleValueAsString)) {

            try {
                final Integer alleleValue = Integer.valueOf(alleleValueAsString);

                if(!isAlleleValueInRange(alleleValue, numAltAlleles)) {
                    result = false;
                    addErrorMessage(sampleColumnName, sampleDataField, "Allele value falls outside of expected range (expected 0 <= value <= " + numAltAlleles +"). Found '" + alleleValueAsString + "'.", lineNum, context);
                }

            } catch (final NumberFormatException e) {

                result = false;
                addErrorMessage(sampleColumnName, sampleDataField, "Allele is not a number, found: '" + alleleValueAsString + "'.", lineNum, context);
            }
        }

        return result;
    }

    /**
     * Return <code>true</code> if the allele value is in the expected range, <code>false</code> otherwise
     *
     * @param alleleValue the allele value
     * @param numAltAlleles the number of alternate alleles
     * @return <code>true</code> if the allele value is in the expected range, <code>false</code> otherwise
     */
    private boolean isAlleleValueInRange(final Integer alleleValue, final Integer numAltAlleles) {
        return alleleValue >= 0 && alleleValue <= numAltAlleles;
    }

    /**
     * Return the expected number of values for a field wit the given 'Number'
     * @param number the number (can be any of Integer >= 0, "A", "G", ".")
     * @param numAltAlleles number of alternate allele
     * @param numGenotypes number of genotypes
     * @return the expected number of values for a field wit the given 'Number'
     */
    private Integer getExpectedNumberOfValues(final String number,
                                              final Integer numAltAlleles,
                                              final Integer numGenotypes) {

        Integer result = null;

        if("A".equals(number)) {
            result = numAltAlleles;
        } else if ("G".equals(number)) {
            result = numGenotypes;
        } else {

            try {
                result = Integer.valueOf(number);
            } catch (final NumberFormatException e) {
                // this should already have been reported by header validation = we can't validate this value though
                // Note: this covers the case where number == "."
            }
        }

        return result;
    }

    /**
     * Return the name of the sample column, given its number
     *
     * @param sampleNumber sample number
     * @return the name of the sample column
     */
    private String getSampleColumnName(final int sampleNumber) {
        return VcfFile.HEADER_TYPE_SAMPLE + " #" + sampleNumber;
    }

    /**
     * Validate the given format data and return an array of format id fields if valid, <code>null</code> otherwise
     * 
     * @param formatData the format data to validate
     * @param formatHeaders the format headers
     * @param lineNum the line number the format data comes from
     * @param context the context
     * @return an array of format id fields if valid, <code>null</code> otherwise
     */
    private String[] validateFormatData(final String formatData,
                                        final List<VcfFileHeader> formatHeaders,
                                        final Integer lineNum,
                                        final QcContext context) {
        String[] result = null;
        boolean isValid = false;

        if(formatData != null) {

            if(formatData.length() != 0) {

                result = formatData.split(SAMPLE_DATA_SEPARATOR, -1);
                final List<String> resultAsList = Arrays.asList(result);

                // Are there any missing mandatory format fields?
                final List<String> missingMandatoryFormatFields = getMissingMandatoryFormatFields(resultAsList);
                if (missingMandatoryFormatFields.size() > 0) {
                    final String reason = new StringBuilder("The following mandatory format fields are missing: ")
                            .append(StringUtil.convertListToDelimitedString(missingMandatoryFormatFields, ','))
                            .toString();

                    addErrorMessage(VcfFile.HEADER_TYPE_FORMAT, formatData, reason, lineNum, context);
                }

                // Only one of the following two format field is mandatory
                if(!resultAsList.contains(MANDATORY_FORMAT_FIELDS_EITHER)
                        && !resultAsList.contains(MANDATORY_FORMAT_FIELDS_OR)) {
                    final String reason = new StringBuilder("Either ").
                            append(MANDATORY_FORMAT_FIELDS_EITHER)
                            .append(" or ")
                            .append(MANDATORY_FORMAT_FIELDS_OR)
                            .append(" must appear in the format fields.")
                            .toString();

                    addErrorMessage(VcfFile.HEADER_TYPE_FORMAT, formatData, reason, lineNum, context);
                }

                // Change default validity to true.
                // If one or more format Id is invalid, it will be set to false.
                isValid = true;

                for(final String formatId : result) {

                    // Make sure that all format field is not blank ('.') and is defined as a FORMAT header
                    if(!DATA_VALUE_BLANK.equals(formatId)) {
                        final VcfFileHeader vcfFileHeader = getVcfFileHeaderByFormatId(formatHeaders, formatId, VcfFile.HEADER_TYPE_FORMAT, lineNum, context);

                        if(vcfFileHeader == null) {
                            isValid = false;
                            addErrorMessage(VcfFile.HEADER_TYPE_FORMAT, formatData, "Format data contains a field that has not been defined in the headers: '" + formatId + "'.", lineNum, context);
                        }

                    } else {
                        isValid = false;
                        addErrorMessage(VcfFile.HEADER_TYPE_FORMAT, formatData, "The format data field is blank ('.').", lineNum, context);
                    }
                }

                // Verify that if "GT" format header is defined then it is the first field found in the format data
                if(getVcfFileHeaderByFormatId(formatHeaders, GENOTYPE_ID_NAME, VcfFile.HEADER_TYPE_FORMAT, lineNum, context) != null && result.length >= 1 && !GENOTYPE_ID_NAME.equals(result[0])) {

                    isValid = false;
                    addErrorMessage(VcfFile.HEADER_TYPE_FORMAT, formatData, "If 'GT' is defined in the format headers, it must be the first field in the format data.",
                            lineNum, context);
                }

            } else {
                addErrorMessage(VcfFile.HEADER_TYPE_FORMAT, formatData, "Format data can not be empty.", lineNum, context);
            }

        } else {
            addErrorMessage(VcfFile.HEADER_TYPE_FORMAT, formatData, "Format data can not be null.", lineNum, context);
        }

        if(!isValid) {
            result = null;
        }

        return result;
    }

    /**
     * Return the list of missing mandatory format fields for the given format data fields
     *
     * @param formatDataFields the format data fields to validate, as a list
     * @return the list of missing mandatory format fields for the given format data (empty if none)
     */
    private List<String> getMissingMandatoryFormatFields(final List<String> formatDataFields) {

        final List result = new ArrayList();

        for(final String mandatoryFormatField : MANDATORY_FORMAT_FIELDS) {
            if(!formatDataFields.contains(mandatoryFormatField)) {
                result.add(mandatoryFormatField);
            }
        }

        return result;
    }

    /**
     * Lookup the format headers and return the header with the given format Id if any, <code>null</code> otherwise
     *
     * Assumptions:
     *
     * - formatHeaders is != null
     * - each <code>VcfFileHeader.valueMap</code> in formatHeaders is != null and has an "ID" key
     * - that "ID" key is unique across format <code>VcfFileHeader</code>. If more than 1 is found the first one will be returned and a warning generated.
     *
     *
     * @param formatHeaders the format headers to look up into
     * @param formatId the format Id of the header to return
     * @param columnName the column name the format Id was found in
     * @param lineNum the number of the line the format Id was found in
     * @param context the context
     * @return the header with the given format Id if any, <code>null</code> otherwise
     */
    private VcfFileHeader getVcfFileHeaderByFormatId(final List<VcfFileHeader> formatHeaders,
                                                     final String formatId,
                                                     final String columnName,
                                                     final Integer lineNum,
                                                     final QcContext context) {

        VcfFileHeader result = null;
        final List<VcfFileHeader> matchingFormatHeaders = new ArrayList<VcfFileHeader>();

        if(formatHeaders != null) {

            for(final VcfFileHeader vcfFileHeader : formatHeaders) {

                if(vcfFileHeader != null && vcfFileHeader.getValueMap() != null) {

                    final String vcfFileHeaderFormatId = vcfFileHeader.getValueMap().get(HEADER_KEY_ID);

                    if(vcfFileHeaderFormatId != null && vcfFileHeaderFormatId.equals(formatId)) {
                        matchingFormatHeaders.add(vcfFileHeader);
                    }
                }
            }

            if(matchingFormatHeaders.size() >= 1) {
                // If there are one or more matching headers, pick the first found
                result = matchingFormatHeaders.get(0);
            }

            if(matchingFormatHeaders.size() > 1) {
                // There was multiple headers found for the given Id, record a warning. That validation should be done at the header validation level.
                addWarningMessage(columnName, formatId,
                        "Multiple FORMAT headers were found for the following ID: " + formatId + ". The first one will be chosen.",
                        lineNum, context);
            }
        }

        return result;
    }

    /**
     * Validate chromosome name; must be in [1-22], X, Y, MT.
     *
     * @param chromName: chromosome name
     * @return true if chromosome name is valid, false ow
     */
    protected boolean validateChromName(final String chromName, final Integer lineNum, final QcContext context) {
        final boolean isValid = match(CHROM_NAME_REGEXP, chromName);
        if ( ! isValid ) {
            addErrorMessage(VcfFile.HEADER_TYPE_CHROM, chromName, null, lineNum, context);
        }
        return isValid;
    }

    /**
     * Method to validate the reference position. The second field must be a reference position and
     * the data type of the field must be Integer. The value is mandatory
     * @param referencePositionValue the value of the POS column for this line
     * @param lineNum
     * @param context
     * @return
     */
    protected boolean validateReferencePosition(final String referencePositionValue, final Integer lineNum, final QcContext context) {
        boolean isValid = true;
        try {
            final BigInteger referencePosition = new BigInteger(referencePositionValue);
            if(referencePosition.compareTo(new BigInteger("0")) != 1) {
                addErrorMessage("POS", referencePositionValue, "The value of the reference position must be > 0", lineNum, context);
                isValid = false;
            }
        } catch(NumberFormatException nfe) {
            addErrorMessage("POS", referencePositionValue, "The value of the reference position must be an integer", lineNum, context);
            isValid = false;
        }
        return isValid;
    }

    /*
     */

    /**
     * Method to validate the ID.
     *
     * @param id The value of the ID column. May be semicolon delimited or . to signify no id
     * @param previousVcfIds
     *@param lineNum
     * @param context   @return
     */
    protected boolean validateId(final String id,
                                 final Set<String> previousVcfIds,
                                 final Integer lineNum,
                                 final QcContext context) {

        boolean isValid = true;
        final String[] ids = id.split(DATA_VALUE_SEPARATOR, -1);
        for(int i = 0; i < ids.length; i++) {
            if(!match(ID_REGEXP, ids[i])) {
                addErrorMessage("ID", ids[i], null, lineNum, context);
                isValid = false;
            } else if(!DATA_VALUE_BLANK.equals(ids[i])) {
                if(!previousVcfIds.add(ids[i])) {
                    addErrorMessage("ID", ids[i], "ID must be unique in the file", lineNum, context);
                    isValid = false;
                }
            }
        }
        return isValid;
    }

    /**
     * Method to validate the FILTER column value
     * @param filter The value of the filter column for the specified data line
     * @param filterDefs A <code>List</code> of <code>VcfFileHeader</code> defined in the header
     * @param lineNum The line number within the file of this cell
     * @param context The <code>QcContext</code> used to report errors/warnings
     * @return <code>true</code> if validation succeeds, <code>false</code> otherwise
     */
    protected boolean validateFilter(final String filter, final List<VcfFileHeader> filterDefs, final Integer lineNum, final QcContext context) {
        boolean isValid = true;
        final String[] filters = filter.split(DATA_VALUE_SEPARATOR, -1);
        for(int i = 0; i < filters.length; i++) {
            if(DATA_VALUE_FILTER_RESERVED.equals(filters[i])) {
                addErrorMessage(VcfFile.HEADER_TYPE_FILTER, filters[i], "The value of the Filter cannot be a reserved string", lineNum, context);
                isValid = false;
            } else if(!match(FILTER_REGEXP, filters[i]) && !isValidFilterCode(filterDefs, filters[i])) {
                addErrorMessage(VcfFile.HEADER_TYPE_FILTER, filters[i], "The value of the Filter must be PASS or correspond to an ID in the FILTER headers", lineNum, context);
                isValid = false;
            }
        }
        return isValid;
    }

    protected boolean validateAltValue(final String altValueString, final List<VcfFileHeader> altHeaders, final Integer lineNum, final QcContext context) {
        boolean isValid = true;

        String[] altValues = altValueString.split(",");
        for (final String altValue : altValues) {

            Matcher altMatcher = getPatternForRegexp(getAltRegex()).matcher(altValue);
            if (!altMatcher.matches()) {

                addErrorMessage(VcfFile.HEADER_TYPE_ALT, altValue, null, lineNum, context);
                isValid = false;

            } else {
                processAltValueSvAltChromId(altValue);
                if (altMatcher.group(ALT_PATTERN_ID_GROUP) != null) {
                    final String altId = altMatcher.group(ALT_PATTERN_ID_GROUP);

                    boolean foundId = false;
                    if (altHeaders != null && altId != null) {
                        for (final VcfFileHeader header : altHeaders) {
                            if (header.getValueMap() != null && altId.equals(header.getValueMap().get("ID"))) {
                                foundId = true;
                            }
                        }
                    }
                    if (!foundId) {
                        addErrorMessage(VcfFile.HEADER_TYPE_ALT, altId, "ID not defined in ALT headers", lineNum, context);
                        isValid = false;
                    }
                }
            }
        }
        return isValid;
    }

    /**
     * Validates the info value using the given INFO headers.
     *
     * @param infoString the value of the info column for this line
     * @param infoHeaders the INFO headers found in the file
     * @param numGenotypes the number of genotype values on this line
     * @param numAltAlleles the number of alt alleles on this line
     * @param lineNumber the line this value is on
     * @param context the qccontext
     * @return true of the info value represents a valid value according to the headers
     */
    public boolean validateInfoValue(final String infoString, final List<VcfFileHeader> infoHeaders,
                                     final Integer numGenotypes, final Integer numAltAlleles,
                                     final int lineNumber, final QcContext context,final Set<String> vcfIds) {
        boolean isValid = true;
        if (infoString.equals(".")) {
            isValid = true;
        } else {
            String[] infos = infoString.split(";");
            for (final String info : infos) {

                String[] idAndValue = info.split("=");
                String infoId = idAndValue[0];
                processInfoDataId(infoId);
                String infoValue = idAndValue.length > 1 ? idAndValue[1] : null;
                boolean foundId = false;
                if (infoHeaders != null) {
                    for (final VcfFileHeader infoHeader : infoHeaders) {
                        if (infoHeader.getValueMap().get("ID").equals(infoId)) {
                            final String infoType = infoHeader.getValueMap().get("Type");
                            final String infoNumber = infoHeader.getValueMap().get("Number");
                            isValid = infoValueIsValid(infoId, infoValue, infoType, infoNumber, numGenotypes, numAltAlleles, lineNumber, context) && isValid;
                            isValid = validateInfoKey(infoId, infoValue, lineNumber, context,vcfIds) && isValid;
                            foundId = true;
                        }
                    }
                }
                if (!foundId) {
                    addErrorMessage(VcfFile.HEADER_TYPE_INFO, infoId, "ID not defined in headers", lineNumber, context);
                    isValid = false;
                }
                //validate relationships between elements in INFO
                isValid &= validateInfoLineRelationships(infoString,lineNumber,context);
            }

        }
        return isValid;
    }

    /**
     * validate relationship between various elements in an info line
     * @param infoLine to validate
     * @param lineNum for the line to validate
     * @param context the qccontext
     * @returns true if Info line relationship are valid, false otherwise
     */
    protected boolean validateInfoLineRelationships(final String infoLine,
    		final Integer lineNum, 
    		final QcContext context){
    	
        // This implementation does nothing
    	return true;
    }
    
    /**
     * Process the given info data Id
     *
     * @param infoDataId the info data Id
     */
    protected void processInfoDataId(final String infoDataId) {
        // This implementation does nothing
    }

    private boolean infoValueIsValid(final String infoId, final String infoValue, final String infoType, final String infoNumber,
                                     final Integer numGenotypes, final Integer numAltAlleles,
                                     final int lineNumber, final QcContext context) {
        boolean isValid = true;
        if (infoType == null || infoNumber == null) {
            isValid = false;
        } else {

            String[] values = infoValue == null ? new String[0] : infoValue.split(",");
            Integer numberExpectedValues;
            if (infoNumber.equals("A")) {
                numberExpectedValues = numAltAlleles;
            } else if (infoNumber.equals("G")) {
                numberExpectedValues = numGenotypes;
            } else if(UNDETERMINED_NUMBER.equals(infoNumber)) {
                numberExpectedValues = EXPECTED_VALUES_NUMBER_WHEN_UNDETERMINED;
            } else {
                try {
                    numberExpectedValues = Integer.valueOf(infoNumber);
                } catch (NumberFormatException e) {
                    // this should already have been reported by header validation = we can't validate this value though
                    return false;
                }
            }
            if (numberExpectedValues != EXPECTED_VALUES_NUMBER_WHEN_UNDETERMINED && values.length != numberExpectedValues) {
                isValid = false;
                addErrorMessage(VcfFile.HEADER_TYPE_INFO, infoId, "Incorrect number of values.  Expected " + numberExpectedValues +  " but found " + values.length, lineNumber, context);
            }
            for (final String value : values) {
                if (!isBlankDataValue(value)) {
                    if (infoType.equals("Integer") && !isInteger(value)) {
                        addErrorMessage(VcfFile.HEADER_TYPE_INFO, value, "Should be an integer", lineNumber, context);
                        isValid = false;
                    } else if (infoType.equals("Float") && !isFloat(value)) {
                        addErrorMessage(VcfFile.HEADER_TYPE_INFO, value, "Should be a floating point number", lineNumber, context);
                        isValid = false;
                    } else if (infoType.equals("Flag") && infoValue != null) {
                        addErrorMessage(VcfFile.HEADER_TYPE_INFO, infoId, "Defined as a Flag so should not have a value but found '" + infoValue + "'", lineNumber, context);
                        isValid = false;
                    } else if (infoType.equals("Character") && value.trim().length() != 1) {
                        addErrorMessage(VcfFile.HEADER_TYPE_INFO, infoId, "Defined as a Character but found value '" + value + "'", lineNumber, context);
                        isValid = false;
                    } else if (infoType.equals("String") && !isStringFormatType(value)) {
                        addErrorMessage(VcfFile.HEADER_TYPE_INFO, infoId, "String cannot contain whitespace, semi-colon, or quote, but found '" + value + "'", lineNumber, context);
                        isValid = false;
                    }
                }
            }
        }

        return isValid;
    }

    /**
     * Method to validate an INFO key occurring in a body data line
     * @param key The INFO key (the part preceding the = sign)
     * @param value The INFO value(s) (the part after the = sign)
     * @return
     */
    protected boolean validateInfoKey(final String key, final String value, final Integer lineNum, final QcContext context,final Set<String>vcfIds) {
        // there is no logic for vcf files for this. look for tcga vcf file specific validations
        // in TcgaVcfFileDataLineValidatorImpl.validateInfoKey
        return true;
    }

    /**
     * Method to track the presence of <ID> in the ALT column value
     * @param altValue The value of an individual filed in the ALT column
     * @return
     */
    protected void processAltValueSvAltChromId(final String altValue) {
        // do nothing for general vcf files
    }

    private boolean isFloat(final String value) {
        try {
            Float.valueOf(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isInteger(final String value) {
        try {
            Integer.valueOf(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    protected boolean isBlankDataValue(final String value) {
        return DATA_VALUE_BLANK.equals(value);
    }


    protected void addErrorMessage(final String columnName, final String foundValue, final String reason, final Integer lineNumber, final QcContext context) {
        final StringBuilder error = new StringBuilder();
        if (context.getFile() != null) {
            error.append("[").append(context.getFile().getName()).append("] ");
        }
        context.addError(error.append("VCF data validation error on line ").append(lineNumber).append(": ").
                append(columnName).append(" value '").append(foundValue).append("' is not valid.").
                append(reason != null ? " " + reason : "").toString());
    }

    /**
     * Adds a VCF data validation warning to the context
     *
     * @param columnName the name of the column header under which the data being validated was found
     * @param foundValue the value found
     * @param reason the reason for the warning
     * @param lineNumber the number of the line at which the value was found
     * @param context the context
     */
    private void addWarningMessage(final String columnName,
                                   final String foundValue,
                                   final String reason, 
                                   final Integer lineNumber,
                                   final QcContext context) {

        context.addWarning(new StringBuilder().append("VCF data validation warning on line ").append(lineNumber).append(": ").
                append(columnName).append(" value '").append(foundValue).append("'.").
                append(reason != null ? " " + reason : "").toString());
    }

    
    /**
     * Validate reference allele sequencee; must be ~ [ACGTN]+
     *
     * @param allele_seq: reference allele sequence
     * @param lineNum: lineNum
     * @param context: context
     * @return true if reference allele sequence is valid, false ow
     */
    protected boolean validateReferenceAlleleSeq(final String allele_seq, final Integer lineNum, final QcContext context) {
        final boolean isValid = match(REFERENCE_ALLELE_SEQ_REGEXP, allele_seq);
        if ( ! isValid ) {
            addErrorMessage(VcfFile.HEADER_TYPE_REF, allele_seq, null, lineNum, context);
        }
        return isValid;
    }

    /**
     * Validate qual; value can be '.' or integer >= 0
     *
     * @param qual: qual
     * @param lineNum: lineNum
     * @param context: context
     * @return true if reference allele sequence is valid, false ow
     */
    protected boolean validateQual(final String qual, final Integer lineNum, final QcContext context) {
        boolean isValid = true;
        if ( ! qual.equals(".") ) {
            try {
                final Integer i = Integer.valueOf(qual);
                if ( i < 0 ) {
                    isValid = false;
                }
            }
            catch (NumberFormatException e) {
                isValid = false;
            }
        }
        if ( ! isValid ) {
            addErrorMessage(VcfFile.HEADER_TYPE_QUAL, qual, "Qual must be '.' or integer >= 0", lineNum, context);
        }
        return isValid;
    }

    /**
     * Return <code>true</code> if the given regular expression has at least one match with the given input
     *
     * @param regexp the regular expression
     * @param input  the input to parse
     * @return <code>true</code> if the given regular expression has at least one match with the given input
     */
    protected boolean match(final String regexp, final String input) {

        return getPatternForRegexp(regexp).matcher(input).matches();
    }

    /**
     * Retrieve the pre-compiled Pattern for the given regular expression. If no Pattern can be retrieved, then create a new one.
     *
     * @param regexp the regular expression
     * @return the pre-compiled Pattern for the given regular expression
     */
    protected Pattern getPatternForRegexp(final String regexp) {

        // Retrieve pre-compiled Pattern if exists, ow generate new pattern
        Pattern result = PATTERN_HASHTABLE.get(regexp);
        if ( result == null ) {
            result = Pattern.compile(regexp);
            PATTERN_HASHTABLE.put( regexp, result);
        }
        return result;
    }

    /**
     * Given a list of filter file headers already parsed value maps, return <code>true</code>
     * if the filter is in the ID element of a valuemap, otherwise <code>false</code>
     * @param filterDefs A <code>List<VcfFileHeader></code> filter headers preparsed with valuemaps
     * @param filter A filter value occuring in the data line
     * @return <code>true</code> if filter value is a value in the filter header value map coresponding to the ID key, <code>false</code> otherwise
     */
    private boolean isValidFilterCode(final List<VcfFileHeader> filterDefs, final String filter) {
        boolean isValid = false;
        for(VcfFileHeader filterHeader : filterDefs) {
            final Map<String,String> valueMap = filterHeader.getValueMap();
            if(valueMap != null && filter.equals(valueMap.get(HEADER_KEY_ID))) {
                isValid = true;
            }
        }
        return isValid;
    }

    /**
     * Get regex for alternate allele seq in VCF file.
     * Override this method to use a different regex in a subclasse
     * @return regex
     */
    protected String getAltRegex() {
        return ALT_REGEXP;
    }
}
