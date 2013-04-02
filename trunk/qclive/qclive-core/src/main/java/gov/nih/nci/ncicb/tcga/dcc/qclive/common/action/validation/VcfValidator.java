/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.MetaDataBean;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFileHeader;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.VcfParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.VcfParserImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractArchiveFileProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.VcfFileDataLineValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.VcfFileHeaderValidator;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.ANGULAR_BRACKETS_PATTERN;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.CENTER_VALUE_PATTERN;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.GENOMES_PATTERN;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.GENOME_DESCRIPTION_PATTERN;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.HEADER_TYPE_ASSEMBLY;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.HEADER_TYPE_CENTER;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.HEADER_TYPE_GENE_ANNO;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.HEADER_TYPE_INDIVIDUAL;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.HEADER_TYPE_SAMPLE;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.HEADER_TYPE_SAMPLE_REQUIRED_KEYS_LIST;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.HEADER_TYPE_SAMPLE_REQUIRED_KEYS_LIST_POST_UUID_TRANSITION;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.HEADER_TYPE_VCFPROCESSLOG;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.QUOTES_PATTERN;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.SAMPLE_KEY_GENOMES;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.SAMPLE_KEY_GENOME_DESCRIPTION;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.SAMPLE_KEY_INDIVIDUAL;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.SAMPLE_KEY_MIXTURE;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.SAMPLE_KEY_SAMPLE_NAME;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.SAMPLE_KEY_SAMPLE_TCGA_BARCODE;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.SAMPLE_KEY_SAMPLE_UUID;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.URL_PATTERN;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.VCF_PROCESS_LOG_HEADER_TAG_VALUES_DEFAULT_SEPARATOR;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.VCF_PROCESS_LOG_HEADER_TAG_VALUES_SPECIAL_SEPARATOR;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.VCF_PROCESS_LOG_INPUTVCF_TAG;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.VCF_PROCESS_LOG_MERGE_TAG_PREFIX;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.VCF_PROCESS_LOG_MISSING_IDENTIFIER;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.VCF_PROCESS_LOG_REQUIRED_TAGS_WHEN_MULTIPLE_INPUTVCF_VALUES;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.VCF_PROCESS_LOG_SPECIAL_TAGS;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.VCF_PROCESS_LOG_TAGS_WITH_SAME_NUMBER_OF_MULTIPLE_VALUES;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.VCF_PROCESS_LOG_TAG_PREFIXES_TO_EXCLUDE_FROM_NUMBER_OF_VALUES_VALIDATION;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.VCF_PROCESS_LOG_TAG_PREFIXES_TO_EXCLUDE_FROM_VALUES_DUPLICATES_VALIDATION;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.WHITESPACE_PATTERN;

/**
 * Validator for Variant Call Format files.
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public abstract class VcfValidator extends AbstractArchiveFileProcessor<Boolean> {

    private String tcgaVcfVersion;
    private VcfFileHeaderValidator vcfFileHeaderValidator;
    private QcLiveBarcodeAndUUIDValidator qcLiveBarcodeAndUUIDValidator;

    /**
     * Validates the given VCF file.
     *
     * @param file    the VCf file to process
     * @param context the qc context
     * @return true if validation passed, false if anything failed
     * @throws gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException
     *          if there is an error while reading or parsing the file
     */
    @Override
    protected Boolean processFile(final File file, final QcContext context) throws ProcessorException {

        // we need a new data line validator for each file, because it stores flags
        final VcfFileDataLineValidator vcfFileDataLineValidator = getVcfFileDataLineValidator();
        context.setFile(file);
        final VcfParser vcfParser = new VcfParserImpl(file);
        try {
            boolean fileIsValid = false;
            fileIsValid = vcfParser.parse();
            final VcfFile vcf = vcfParser.getVcfFile();
            checkFileFormat(vcfParser.getVcfFile());
            fileIsValid = validateHeaders(vcf, context) && fileIsValid;

            // extract vcf Ids from the file           
            if (vcfParser.getVcfIds() == null || vcfParser.getVcfIds().size() == 0) {

                fileIsValid = false;
                addErrorMessage(context, new StringBuilder("VCF error: file [").append(file.getName())
                        .append("] does not contain any VCF IDs").toString());
            }

            fileIsValid = validateColumnLine(vcf, context) && fileIsValid;
            fileIsValid = vcfFileHeaderValidator.validateSampleColumnHeader(vcf, context) && fileIsValid;

            String[] dataLine;
            Set<String> previousVcfIds = new HashSet<String>();
            while ((dataLine = vcfParser.getNextDataLine()) != null) {
                fileIsValid &= vcfFileDataLineValidator.validateDataLine(dataLine, vcf, vcfParser.getCurrentLineNumber(), context, vcfParser.getVcfIds(), previousVcfIds);
            }

            //post-data header validation
            fileIsValid = postDataValidation(vcf, context, vcfFileDataLineValidator) && fileIsValid;

            // check if parser recorded errors that were caught before additional validation
            if (vcfParser.getErrors().size() > 0) {
                context.addErrors(vcfParser.getErrors());
                return false;
            } else {
                return fileIsValid;
            }

        } catch (IOException e) {
            throw new ProcessorException("Error parsing VCF " + file.getName() + ": " + e.getMessage(), e);
        } catch (UnsupportedFileException ue) {
            throw new ProcessorException("Error parsing VCF " + file.getName() + ": " + ue.getMessage(), ue);
        } finally {
            if (vcfParser != null) {
                vcfParser.close();
            }
        }
    }

    private boolean validateFirstHeader(final VcfFile vcf, final QcContext context) {
        boolean isValid;
        final VcfFileHeader firstHeader = vcf.getHeader(1);
        if (firstHeader != null && firstHeader.getName().equalsIgnoreCase(VcfFile.HEADER_TYPE_FILEFORMAT)) {
            isValid = true;
        } else {
            addErrorMessage(context, new StringBuilder().append("First line of VCF file must contain the '")
                    .append(VcfFile.HEADER_TYPE_FILEFORMAT).append("' header").toString());
            isValid = false;
        }
        final List<VcfFileHeader> fileHeaderList = vcf.getHeadersForType(VcfFile.HEADER_TYPE_FILEFORMAT);
        if (fileHeaderList != null && fileHeaderList.size() > 1) {
            addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                    .append(VcfFile.HEADER_TYPE_FILEFORMAT).append("' is duplicated.").toString());
            isValid = false;
        }
        return isValid;
    }

    /**
     * execute post-data header validation
     *
     *
     * @param vcf     the vcf file object
     * @param context qc context
     * @param vcfFileDataLineValidator
     * @return true if validation passes
     */
    private boolean postDataValidation(final VcfFile vcf,
                                       final QcContext context,
                                       final VcfFileDataLineValidator vcfFileDataLineValidator) {
        boolean isValid = true;
        isValid = isValidVcfProcessLogHeader(vcf, context) && isValid;

        if (vcfFileDataLineValidator.isFoundInfoDataRequiringGeneAnnoInfoHeader()) {
            isValid = headerExistsAndHasNonBlankValue(vcf, HEADER_TYPE_GENE_ANNO, context) && isValid;
        }

        if (vcfFileDataLineValidator.isFoundChromDataRequiringAssemblyHeader()
                || vcfFileDataLineValidator.isFoundAltDataRequiringAssemblyHeader()) {
            isValid = validateTcgaAssemblyHeader(vcf, HEADER_TYPE_ASSEMBLY, context) && isValid;
        }

        isValid = validateTcgaCenterHeader(vcf, HEADER_TYPE_CENTER, context) && isValid;

        if (context.isCenterConvertedToUUID()) {
            isValid = validateTcgaSampleHeaderPostUuidTransition(vcf, HEADER_TYPE_SAMPLE, context) && isValid;
        } else {
            isValid = validateTcgaSampleHeader(vcf, HEADER_TYPE_SAMPLE, context) && isValid;
        }

        return isValid;

    }


    /**
     * Return <code>true</code> if the vcfProcessLog header value is valid, <code>false</code> otherwise.
     *
     * @param vcf     the vcf file
     * @param context the context
     * @return <code>true</code> if the vcfProcessLog header value is valid, <code>false</code> otherwise
     */
    private boolean isValidVcfProcessLogHeader(final VcfFile vcf,
                                               final QcContext context) {

        boolean result = false;
        final List<VcfFileHeader> vcfProcessLogHeaderList = vcf.getHeadersForType(HEADER_TYPE_VCFPROCESSLOG);

        if (vcfProcessLogHeaderList != null && vcfProcessLogHeaderList.size() > 0) {

            final VcfFileHeader vcfProcessLogHeader = vcfProcessLogHeaderList.get(0);

            if (vcfProcessLogHeader.getValueMap() != null && vcfProcessLogHeader.getValueMap().size() > 0) {
                // Map value
                result = isValidVcfProcessLogHeaderValueMap(vcfProcessLogHeader.getValueMap(), context);

            } else if (vcfProcessLogHeader.getValue() != null && !StringUtils.isBlank(vcfProcessLogHeader.getValue())) {
                // Simple value (not a map)
                result = false;
                addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                        .append(HEADER_TYPE_VCFPROCESSLOG)
                        .append("' is not surrounded by angle brackets (<>)").toString());

            } else {
                // No value
                result = false;
                addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                        .append(HEADER_TYPE_VCFPROCESSLOG)
                        .append("' has no value.").toString());
            }

        } else {
            // Missing header
            result = false;
            addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                    .append(HEADER_TYPE_VCFPROCESSLOG)
                    .append("' is missing.").toString());
        }

        return result;
    }

    /**
     * Return <code>true</code> if the given Map of values is valid for a vcfProcessLog header, <code>false</code> otherwise.
     *
     * @param valueMap a non null Map of values
     * @param context  the context
     * @return <code>true</code> if the given Map of values is valid for a vcfProcessLog header, <code>false</code> otherwise
     */
    private boolean isValidVcfProcessLogHeaderValueMap(final Map<String, String> valueMap,
                                                       final QcContext context) {

        boolean result = true;

        String tagValue;
        String trimmedTagKey;
        boolean isTagValueSurroundedByBrackets;
        String valueWithoutSurroundingBrackets;

        String[] tagSubValues;
        Map<String, String[]> tagSubValuesMap = new HashMap<String, String[]>();

        for (final String tagKey : valueMap.keySet()) {

            tagValue = valueMap.get(tagKey);
            trimmedTagKey = tagKey.trim();
            isTagValueSurroundedByBrackets = isSurroundedByAngleBrackets(tagValue, trimmedTagKey, context);
            result = isTagValueSurroundedByBrackets && result;

            if (isTagValueSurroundedByBrackets) {

                valueWithoutSurroundingBrackets = tagValue.substring(1, tagValue.length() - 1);
                tagSubValues = getTagSubValues(valueWithoutSurroundingBrackets, trimmedTagKey);

                tagSubValuesMap.put(trimmedTagKey, tagSubValues);
            }

        }

        result = isValidTagsSubValues(tagSubValuesMap, context) && result;

        return result;
    }

    /**
     * Return <code>true</code> if the sub values for each tag are valid, <code>false</code> otherwise.
     *
     * @param tagSubValuesMap the map of values per tag
     * @param context         the context
     * @return <code>true</code> if the sub values for each tag are valid, <code>false</code> otherwise
     */
    private boolean isValidTagsSubValues(final Map<String, String[]> tagSubValuesMap,
                                         final QcContext context) {
        boolean result = true;

        Integer referenceNumberOfValuesPerNonMergeTag = null;
        boolean sameNumberOfSubValuesForNonMergeTags = true;

        for (final String tagName : tagSubValuesMap.keySet()) {

            final String[] tagSubValues = tagSubValuesMap.get(tagName);

            // Validating that there are not leading or trailing whitespace in sub values
            result = isValidTagsSubValuesWhitespace(tagSubValues, tagName, context) && result;

            // Validating that the number of values per tag is the same.
            // Note: 'Merge*' tags are excluded from this requirements.
            if (sameNumberOfSubValuesForNonMergeTags && belongToNumberOfValuesRequirements(tagName)) {

                final Integer currentNumberOfValuesPerTag = tagSubValues.length;

                if (referenceNumberOfValuesPerNonMergeTag != null) {

                    sameNumberOfSubValuesForNonMergeTags = currentNumberOfValuesPerTag == referenceNumberOfValuesPerNonMergeTag;

                    if (!sameNumberOfSubValuesForNonMergeTags) {
                        result = false;
                        addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                                .append(HEADER_TYPE_VCFPROCESSLOG)
                                .append("' tags (other than 'Merge*') don't have the same number of values.").toString());
                    }

                } else {
                    referenceNumberOfValuesPerNonMergeTag = currentNumberOfValuesPerTag;
                }
            }

            // Validating that there are no duplicates in multiple values
            result = isValidTagsSubValuesNoDuplicate(tagSubValues, tagName, context) && result;
        }

        if (tagSubValuesMap.containsKey(VCF_PROCESS_LOG_INPUTVCF_TAG)) {

            final String[] inputVCFSubValues = tagSubValuesMap.get(VCF_PROCESS_LOG_INPUTVCF_TAG);

            final Map<String, String[]> mergeTagsSubValuesMap = new HashMap<String, String[]>();
            for (final String tagName : tagSubValuesMap.keySet()) {

                if (tagName.startsWith(VCF_PROCESS_LOG_MERGE_TAG_PREFIX)) {
                    // Add to mergeTagsSubValuesMap
                    mergeTagsSubValuesMap.put(tagName, tagSubValuesMap.get(tagName));
                }
            }

            if (inputVCFSubValues.length == 1) {

                // Validate that if InputVCF has 1 sub-value, then Merge* tags have no values (besides the missing identifier)
                result = isValidMergeTagsWhenSingleInputVCFValue(mergeTagsSubValuesMap, context) && result;

            } else { //InputVCF values > 1

                // Validate that if InputVCF has more than 1 sub-value, all Merge* tags are present
                result = isValidMergeTagsWhenMultipleInputVCFValues(mergeTagsSubValuesMap, context) && result;

                // Validate that if MergeSoftware, MergeParam and MergeVer contain multiple values (or the missing identifier),
                // there are the same number of values for each

                Integer referenceNumberOfValuesPerMergeTag = null;
                boolean sameNumberOfSubValuesForMergeTag = true;

                for (final String mergeTagName : VCF_PROCESS_LOG_TAGS_WITH_SAME_NUMBER_OF_MULTIPLE_VALUES) {

                    if (sameNumberOfSubValuesForMergeTag) {

                        final String[] mergeTagValues = mergeTagsSubValuesMap.get(mergeTagName);

                        if (mergeTagValues != null) {

                            if (mergeTagValues.length > 1
                                    || (mergeTagValues.length == 1 && !VCF_PROCESS_LOG_MISSING_IDENTIFIER.equals(mergeTagValues[0]))) {

                                // The value is not the missing identifier
                                final Integer currentNumberOfValuesPerMergeTag = mergeTagValues.length;

                                if (referenceNumberOfValuesPerMergeTag != null) {

                                    sameNumberOfSubValuesForMergeTag = currentNumberOfValuesPerMergeTag == referenceNumberOfValuesPerMergeTag;

                                    if (!sameNumberOfSubValuesForMergeTag) {
                                        result = false;
                                        addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                                                .append(HEADER_TYPE_VCFPROCESSLOG)
                                                .append("' : 'Merge*' tags don't have the same number of values.")
                                                .toString());
                                    }

                                } else {
                                    referenceNumberOfValuesPerMergeTag = currentNumberOfValuesPerMergeTag;
                                }
                            }
                        }
                    }
                }
            }

        }

        return result;
    }

    /**
     * Return <code>true</code> if all Merge* tags are present, <code>false</code> otherwise.
     *
     * @param mergeTagsSubValuesMap the Merge* tags to validate
     * @param context               the context
     * @return <code>true</code> if all Merge* tags are present, <code>false</code> otherwise
     */
    private boolean isValidMergeTagsWhenMultipleInputVCFValues(final Map<String, String[]> mergeTagsSubValuesMap,
                                                               final QcContext context) {
        boolean result = true;

        for (final String requiredTagName : VCF_PROCESS_LOG_REQUIRED_TAGS_WHEN_MULTIPLE_INPUTVCF_VALUES) {

            if (!mergeTagsSubValuesMap.containsKey(requiredTagName)) {

                result = false;
                addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                        .append(HEADER_TYPE_VCFPROCESSLOG)
                        .append("' : tag '")
                        .append(VCF_PROCESS_LOG_INPUTVCF_TAG)
                        .append("' has multiple values, but tag '")
                        .append(requiredTagName)
                        .append("' is missing.")
                        .toString());
            }
        }

        return result;
    }

    /**
     * Return <code>true</code> if Merge* tags have no values (besides the missing identifier), <code>false</code> otherwise.
     *
     * @param mergeTagsSubValuesMap the Merge* tags to validate
     * @param context               the context
     * @return <code>true</code> if Merge* tags have no values (besides the missing identifier), <code>false</code> otherwise
     */
    private boolean isValidMergeTagsWhenSingleInputVCFValue(final Map<String, String[]> mergeTagsSubValuesMap,
                                                            final QcContext context) {

        boolean result = true;

        for (final String mergeTagName : mergeTagsSubValuesMap.keySet()) {

            final String[] mergeTagSubValues = mergeTagsSubValuesMap.get(mergeTagName);

            if (mergeTagSubValues.length == 1) {

                if (!VCF_PROCESS_LOG_MISSING_IDENTIFIER.equals(mergeTagSubValues[0])) {
                    result = false;
                    addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                            .append(HEADER_TYPE_VCFPROCESSLOG)
                            .append("' : tag '")
                            .append(VCF_PROCESS_LOG_INPUTVCF_TAG)
                            .append("' has only 1 value, but tag '")
                            .append(mergeTagName)
                            .append("' has 1 value that is not the missing identifier ('.').")
                            .toString());

                }

            } else {
                result = false;
                addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                        .append(HEADER_TYPE_VCFPROCESSLOG)
                        .append("' : tag '")
                        .append(VCF_PROCESS_LOG_INPUTVCF_TAG)
                        .append("' has only 1 value, but tag '")
                        .append(mergeTagName)
                        .append("' has 1 value or more (Found ")
                        .append(mergeTagSubValues.length)
                        .append(").").toString());
            }
        }

        return result;
    }

    /**
     * Return <code>true</code> if there are no duplicates in multiple values for some tags that have that requirement, <code>false</code> otherwise.
     *
     * @param tagSubValues the tag sub-values to validate
     * @param tagName      the tag name the sub-values are assigned to
     * @param context      the context
     * @return <code>true</code> if there are no duplicates in multiple values for some tags that have that requirement, <code>false</code> otherwise
     */
    private boolean isValidTagsSubValuesNoDuplicate(final String[] tagSubValues,
                                                    final String tagName,
                                                    final QcContext context) {
        boolean result = true;
        final Set<String> uniqueTagSubValues = new HashSet<String>();
        for (final String tagSubValue : tagSubValues) {

            if (belongToDuplicateRequirements(tagName)) {

                final boolean unique = uniqueTagSubValues.add(tagSubValue);

                if (!unique) {
                    result = false;
                    addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                            .append(HEADER_TYPE_VCFPROCESSLOG)
                            .append("' : tag '")
                            .append(tagName)
                            .append("' has duplicate values ('")
                            .append(tagSubValue)
                            .append("')").toString());
                }
            }
        }
        return result;
    }

    /**
     * Return <code>true</code> if there are not leading or trailing whitespace in sub values, <code>false</code> otherwise
     *
     * @param tagSubValues the tag sub-values to validate
     * @param tagName      the tag name the sub-values are assigned to
     * @param context      the context
     * @return <code>true</code> if there are not leading or trailing whitespace in sub values, <code>false</code> otherwise.
     */
    private boolean isValidTagsSubValuesWhitespace(final String[] tagSubValues,
                                                   final String tagName,
                                                   final QcContext context) {

        boolean result = true;

        for (final String tagSubValue : tagSubValues) {

            final String trimmedTagSubValue = tagSubValue.trim();

            if (trimmedTagSubValue.isEmpty()) {

                result = false;
                addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                        .append(HEADER_TYPE_VCFPROCESSLOG)
                        .append("' : tag '")
                        .append(tagName)
                        .append("' has no value or contains only whitespace '")
                        .append(tagSubValue)
                        .append("')").toString());

            } else if (!tagSubValue.equals(trimmedTagSubValue)) {

                result = false;
                addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                        .append(HEADER_TYPE_VCFPROCESSLOG)
                        .append("' : tag '")
                        .append(tagName)
                        .append("' has leading and/or trailing space in '")
                        .append(tagSubValue)
                        .append("')").toString());
            }
        }

        return result;
    }

    /**
     * Return <code>true</code> if the given tag is a tag that belongs to the list of tags tag that must have the same number of values, <code>false</code>.
     *
     * @param tagName the tag name
     * @return <code>true</code> if the given tag is a tag that belongs to the list of tags tag that must have the same number of values, <code>false</code>
     */
    private boolean belongToNumberOfValuesRequirements(final String tagName) {

        boolean result = true;

        for (final String tagPrefixToExclude : VCF_PROCESS_LOG_TAG_PREFIXES_TO_EXCLUDE_FROM_NUMBER_OF_VALUES_VALIDATION) {
            result = result && !tagName.startsWith(tagPrefixToExclude);
        }

        return result;
    }

    /**
     * Return <code>true</code> if the given tag is a tag for which there must be no duplicates in sub values, <code>false</code>.
     *
     * @param tagName the tag name
     * @return <code>true</code> if the given tag is a tag for which there must be no duplicates in sub values, <code>false</code>
     */
    private boolean belongToDuplicateRequirements(final String tagName) {

        boolean result = true;

        for (final String tagPrefixToExclude : VCF_PROCESS_LOG_TAG_PREFIXES_TO_EXCLUDE_FROM_VALUES_DUPLICATES_VALIDATION) {
            result = result && !tagName.startsWith(tagPrefixToExclude);
        }

        return result;
    }

    /**
     * Return an Array of sub-values contained in the given value, assuming they are comma separated.
     *
     * @param value   the value to parse
     * @param tagName the name of the tag the value is associated to
     * @return an Array of sub-values contained in the given value, assuming they are comma separated
     */
    private String[] getTagSubValues(final String value, String tagName) {

        String[] result = null;

        if (value != null) {

            if (VCF_PROCESS_LOG_SPECIAL_TAGS.contains(tagName)) {
                result = value.split(VCF_PROCESS_LOG_HEADER_TAG_VALUES_SPECIAL_SEPARATOR, -1);
            } else {
                result = value.split(VCF_PROCESS_LOG_HEADER_TAG_VALUES_DEFAULT_SEPARATOR, -1);
            }
        }

        return result;
    }

    /**
     * Return <code>false</code> if the given value is not surrounded by angle brackets, or if there are < or > inside the surrounding brackets, <code>true</code> otherwise.
     * <p/>
     * Note: it will also
     *
     * @param value   the value to validate
     * @param key     the key associated to the value
     * @param context the context
     * @return <code>false</code> if the given value is not surrounded by angle brackets, or if there are < or > inside the surrounding brackets, <code>true</code> otherwise
     */
    private boolean isSurroundedByAngleBrackets(final String value,
                                                final String key,
                                                final QcContext context) {
        boolean result = true;

        if (value != null) {

            if (!value.startsWith("<")) {
                result = false;
                addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                        .append(HEADER_TYPE_VCFPROCESSLOG)
                        .append("': '")
                        .append(key)
                        .append("' tag is missing opening angle bracket (<) in '")
                        .append(value)
                        .append("' value.")
                        .toString());
            }

            if (!value.endsWith(">")) {
                result = false;
                addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                        .append(HEADER_TYPE_VCFPROCESSLOG)
                        .append("': '")
                        .append(key)
                        .append("' tag is missing closing angle bracket (>) in '")
                        .append(value)
                        .append("' value.")
                        .toString());
            }

            if (result) { // Value is surrounded by angle brackets
                final String valueStrippedOfSurroundingBrackets = value.substring(1, value.length() - 1);

                if (valueStrippedOfSurroundingBrackets.indexOf("<") != -1
                        || valueStrippedOfSurroundingBrackets.indexOf(">") != -1) {
                    // This test is required to catch cases where map values were incorrectly mapped 
                    // because of mismatch in number of opening and closing angle brackets

                    result = false;
                    addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                            .append(HEADER_TYPE_VCFPROCESSLOG)
                            .append("': '")
                            .append(key)
                            .append("' tag has < or > inside '")
                            .append(value)
                            .append("' value besides the requested opening and closing brackets.")
                            .toString());
                }
            }

        } else {
            // Tag has null value. Should not happen but just in case.
            result = false;
            addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                    .append(HEADER_TYPE_VCFPROCESSLOG)
                    .append("': '")
                    .append(key)
                    .append("' tag has a null value.")
                    .toString());
        }

        return result;
    }

    /**
     * construct common part of the vcf validator error messages
     *
     * @param context
     * @param errorMessage
     */
    protected void addErrorMessage(final QcContext context, final String errorMessage) {
        context.addError((context.getFile() != null ? "[" + context.getFile().getName() + "] " : "") + errorMessage);
    }

    /**
     * construct common part of the vcf validator warning messages
     *
     * @param context
     * @param errorMessage
     */
    protected void addWarningMessage(final QcContext context, final String errorMessage) {
        context.addWarning((context.getFile() != null ? "[" + context.getFile().getName() + "] " : "") + errorMessage);
    }

    /**
     * Return <code>true</code> if the header with the given name exists and has a non-blank value
     *
     * @param vcf        the vcf file
     * @param headerName the header name
     * @param context    the context
     * @return <code>true</code> if the header with the given name exists and has a non-blank value
     */
    private boolean headerExistsAndHasNonBlankValue(final VcfFile vcf,
                                                    final String headerName,
                                                    final QcContext context) {
        boolean isValid;
        final List<VcfFileHeader> vcfProcessLogHeaderList = vcf.getHeadersForType(headerName);
        if (vcfProcessLogHeaderList != null && vcfProcessLogHeaderList.size() == 1) {
            final VcfFileHeader vcfProcessLogHeader = vcfProcessLogHeaderList.get(0);
            if (!StringUtils.isBlank(vcfProcessLogHeader.getValue())) {
                isValid = true;
            } else {
                if (vcfProcessLogHeader.getValueMap() != null && vcfProcessLogHeader.getValueMap().size() > 0) {
                    isValid = true;
                } else {
                    addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                            .append(headerName)
                            .append("' has no value.").toString());
                    isValid = false;
                }
            }
        } else if (vcfProcessLogHeaderList.size() > 1) {
            addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                    .append(headerName)
                    .append("' is duplicated.").toString());
            isValid = false;
        } else {
            addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                    .append(headerName)
                    .append("' is missing.").toString());
            isValid = false;
        }
        return isValid;
    }

    protected boolean validateTcgaAssemblyHeader(final VcfFile vcf,
                                                 final String headerName,
                                                 final QcContext context) {
        boolean isValid;
        final List<VcfFileHeader> vcfAssemblyHeaderList = vcf.getHeadersForType(headerName);
        if (vcfAssemblyHeaderList != null && vcfAssemblyHeaderList.size() == 1) {
            final VcfFileHeader vcfAssemblyHeader = vcfAssemblyHeaderList.get(0);
            if (!StringUtils.isBlank(vcfAssemblyHeader.getValue())) {
                isValid = true;
            } else {
                addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                        .append(headerName)
                        .append("' has no value.").toString());
                isValid = false;
            }
        } else if (vcfAssemblyHeaderList.size() > 1) {
            addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                    .append(headerName)
                    .append("' is duplicated.").toString());
            isValid = false;
        } else {
            addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                    .append(headerName)
                    .append("' is missing.").toString());
            isValid = false;
        }
        return isValid;
    }

    private boolean validateTcgaCenterHeader(final VcfFile vcf,
                                             final String headerName,
                                             final QcContext context) {
        boolean isValid = true;
        final List<VcfFileHeader> vcfCenterHeaderList = vcf.getHeadersForType(headerName);
        if (vcfCenterHeaderList != null && vcfCenterHeaderList.size() > 0) {
            final VcfFileHeader vcfCenterHeader = vcfCenterHeaderList.get(0);
            if (!CENTER_VALUE_PATTERN.matcher(vcfCenterHeader.getValue()).matches()) {
                addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                        .append(headerName)
                        .append("' has incorrect value. Value must be enclosed in double quotes or have no white space.").toString());
                isValid = false;
            }
        } else {
            addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                    .append(headerName)
                    .append("' is missing.").toString());
            isValid = false;
        }
        return isValid;
    }

    private boolean validateTcgaSampleHeader(final VcfFile vcf,
                                             final String headerName,
                                             final QcContext context) {
        boolean isValid = true;
        final List<VcfFileHeader> vcfSampleHeaderList = vcf.getHeadersForType(headerName);
        final List<String> uuids = new ArrayList<String>();
        final List<String> barcodes = new ArrayList<String>();
        if (vcfSampleHeaderList != null) {
            for (VcfFileHeader sampleFileHeader : vcfSampleHeaderList) {
                Map<String, String> valueMap = sampleFileHeader.getValueMap();
                if (valueMap == null) {
                    addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                            .append(headerName)
                            .append("' must be in the format <key1=value1,key2=value2,...>.").toString());
                    isValid = false;
                } else {
                    if (!valueMap.keySet().containsAll(HEADER_TYPE_SAMPLE_REQUIRED_KEYS_LIST)) {
                        addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                                .append(headerName)
                                .append("' must have all of 'ID', 'SampleName', 'Individual', 'File', 'Platform', 'Source', 'Accession' keys.").toString());
                        isValid = false;
                    }
                    isValid = validateSampleHeaderValues(valueMap, headerName, context) && isValid;
                    // store the uuids/barcodes for batch validation
                    final String sampleNameValue = valueMap.get(SAMPLE_KEY_SAMPLE_NAME);
                    if (context.isStandaloneValidator() && sampleNameValue != null) {
                        if (qcLiveBarcodeAndUUIDValidator.isUUID(sampleNameValue)) {
                            uuids.add(sampleNameValue);
                        } else {
                            barcodes.add(sampleNameValue);
                        }
                    }
                }
            }
            isValid &= batchValidateSampleNameValues(barcodes, uuids, context);

        }
        return isValid;
    }

    protected boolean validateSampleHeaderValues(final Map<String, String> valueMap, final String headerName, final QcContext context) {
        boolean isValid = true;

        // Perform syntax validation on all sample header values contained in the value map
        for (final String key : valueMap.keySet()) {
            isValid = validateSampleHeaderValue(key, valueMap.get(key), context) && isValid;
        }

        final Set<Integer> numTokens = new HashSet<Integer>();
        isValid = validateSampleKeyGenomes(valueMap.get(SAMPLE_KEY_GENOMES), numTokens, headerName, context) && isValid;
        isValid = validateSampleKeyMixture(valueMap.get(SAMPLE_KEY_MIXTURE), numTokens, headerName, context) && isValid;
        isValid = validateSampleKeyGenomeDescription(valueMap.get(SAMPLE_KEY_GENOME_DESCRIPTION), numTokens, headerName, context) && isValid;
        isValid = validateSampleNameValue(valueMap.get(SAMPLE_KEY_SAMPLE_NAME), context) && isValid;

        if (numTokens.size() > 1) {
            addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                    .append(headerName)
                    .append("' must have same number of tokens for Genomes, Mixture and Genome_Description.").toString());
            isValid = false;
        }
        return (isValid);
    }

    private boolean validateTcgaSampleHeaderPostUuidTransition(final VcfFile vcf,
                                                               final String headerName,
                                                               final QcContext context) {
        boolean isValid = true;
        final List<VcfFileHeader> vcfSampleHeaderList = vcf.getHeadersForType(headerName);
        final List<VcfFileHeader> vcfTcgaIndividualHeaderList = vcf.getHeadersForType(HEADER_TYPE_INDIVIDUAL);
        String individualValue = null;
        if (vcfTcgaIndividualHeaderList != null && vcfTcgaIndividualHeaderList.size() > 0) {
            individualValue = vcfTcgaIndividualHeaderList.get(0).getValue();
        }
        if (vcfSampleHeaderList != null) {
            final List<String[]> sampleUuidAndSampleTcgaBarcodePairs = new ArrayList<String[]>();
            for (final VcfFileHeader sampleFileHeader : vcfSampleHeaderList) {
                final Map<String, String> valueMap = sampleFileHeader.getValueMap();
                if (valueMap == null) {
                    addErrorMessage(context, new StringBuilder()
                            .append("VCF header validation error: header '")
                            .append(headerName)
                            .append("' must be in the format <key1=value1,key2=value2,...>.").toString());
                    isValid = false;
                } else {
                    if (!valueMap.keySet().containsAll(HEADER_TYPE_SAMPLE_REQUIRED_KEYS_LIST_POST_UUID_TRANSITION)) {
                        addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                                .append(headerName)
                                .append("' must have all of 'ID', 'SampleUUID', 'SampleTCGABarcode', 'File', 'Platform', 'Source', 'Accession' keys.").toString());
                        isValid = false;
                    }
                    isValid = validateSampleHeaderValuesPostUuidTransition(individualValue, valueMap, headerName, context) && isValid;

                    // Store the SampleUUID and SampleTCGABarcode pair for batch validation
                    final String sampleUUID = valueMap.get(SAMPLE_KEY_SAMPLE_UUID);
                    final String sampleTcgaBarcode = valueMap.get(SAMPLE_KEY_SAMPLE_TCGA_BARCODE);
                    if (StringUtils.isNotBlank(sampleUUID) && StringUtils.isNotBlank(sampleTcgaBarcode)) {
                        final String[] sampleUuidAndSampleTcgaBarcodePair = new String[2];
                        sampleUuidAndSampleTcgaBarcodePair[0] = sampleUUID;
                        sampleUuidAndSampleTcgaBarcodePair[1] = sampleTcgaBarcode;
                        sampleUuidAndSampleTcgaBarcodePairs.add(sampleUuidAndSampleTcgaBarcodePair);
                    }
                }
            }

            isValid = batchValidateSampleUuidAndSampleTcgaBarcode(sampleUuidAndSampleTcgaBarcodePairs, context) && isValid;

        }

        return isValid;
    }

    /**
     * Return <code>true</code> if the individual header value is valid, <code>false</code> otherwise.
     *
     * @param metadata        the MetaDataBean value
     * @param sampleUUID      the sampleUUID value
     * @param individualValue the individual Value
     * @param context         the context
     * @return <code>true</code> if the individual header value is valid, <code>false</code> otherwise
     */
    protected boolean validateTcgaIndividualHeaderUuidTransition(final MetaDataBean metadata, final String sampleUUID,
                                                                 final String sampleTcgaBarcode,
                                                                 final String individualValue, final QcContext context) {
        boolean isValid = true;
        if (metadata != null) {
            final String samplePatientBarcode = metadata.getPatientBuiltBarcode();
            if (individualValue != null && !StringUtils.equalsIgnoreCase(individualValue, samplePatientBarcode)) {
                isValid = false;
                addErrorMessage(context, new StringBuilder().append("VCF header validation error: ")
                        .append("the sampleUUID '")
                        .append(sampleUUID)
                        .append("' in header '")
                        .append(HEADER_TYPE_SAMPLE)
                        .append("' matching patient barcode '")
                        .append(samplePatientBarcode)
                        .append("' does not match the header '")
                        .append(HEADER_TYPE_INDIVIDUAL)
                        .append("' value '")
                        .append(individualValue)
                        .append("'.").toString());
            }
        } else {
            // if no meta data just compare the barcodes...
            final String participantTcgaBarcode = qcLiveBarcodeAndUUIDValidator.getPatientBarcode(sampleTcgaBarcode);
            if (individualValue != null && !StringUtils.equalsIgnoreCase(individualValue, participantTcgaBarcode)) {
                isValid &= false;
                context.addError(new StringBuffer().append("Value of ")
                        .append(HEADER_TYPE_INDIVIDUAL).append(" header '")
                        .append(individualValue)
                        .append("' does not match the participant barcode '")
                        .append(participantTcgaBarcode)
                        .append("' associated with sampleTcgaBarcode '")
                        .append(sampleTcgaBarcode)
                        .append("' in the ").append(HEADER_TYPE_SAMPLE).append(" header.").toString());
            }
        }
        return isValid;
    }


    /**
     * Batch validation of SampleUUID/SampleTCGABarcode pairs through Web Service call for use by the Standalone validator
     *
     * @param sampleUuidAndSampleTcgaBarcodePairs
     *                  a list of SampleUUID/SampleTCGABarcode pairs
     * @param qcContext the qcLive context in which eventual errors are stored
     * @return <code>true</code> if all given pairs are valid, <code>false</code> otherwise
     */
    private boolean batchValidateSampleUuidAndSampleTcgaBarcode(final List<String[]> sampleUuidAndSampleTcgaBarcodePairs,
                                                                final QcContext qcContext) {

        boolean result = true;

        if (qcContext.isStandaloneValidator()) {
            result = qcLiveBarcodeAndUUIDValidator.batchValidateSampleUuidAndSampleTcgaBarcode(sampleUuidAndSampleTcgaBarcodePairs, qcContext);
        }

        return result;
    }


    protected boolean validateSampleHeaderValuesPostUuidTransition(final String individualValue,
                                                                   final Map<String, String> valueMap,
                                                                   final String headerName,
                                                                   final QcContext context) {
        boolean isValid = true;

        // Perform syntax validation on all sample header values contained in the value map
        for (final String key : valueMap.keySet()) {
            isValid = validateSampleHeaderValue(key, valueMap.get(key), context) && isValid;
        }

        final Set<Integer> numTokens = new HashSet<Integer>();
        isValid = validateSampleKeyGenomes(valueMap.get(SAMPLE_KEY_GENOMES), numTokens, headerName, context) && isValid;
        isValid = validateSampleKeyMixture(valueMap.get(SAMPLE_KEY_MIXTURE), numTokens, headerName, context) && isValid;
        isValid = validateSampleKeyGenomeDescription(valueMap.get(SAMPLE_KEY_GENOME_DESCRIPTION), numTokens, headerName, context) && isValid;

        if (numTokens.size() > 1) {
            addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                    .append(headerName)
                    .append("' must have same number of tokens for Genomes, Mixture and Genome_Description.").toString());
            isValid = false;
        }

        final String sampleUUID = valueMap.get(SAMPLE_KEY_SAMPLE_UUID);
        final boolean isValidSampleUUID = validateSampleHeaderSampleUUIDValue(sampleUUID, context);
        isValid &= isValidSampleUUID;

        if (isValidSampleUUID) {
            MetaDataBean metadata = null;
            try {
                if (StringUtils.isNotBlank(sampleUUID)) {
                    metadata = qcLiveBarcodeAndUUIDValidator.getMetadata(sampleUUID);
                }
            } catch (CommonBarcodeAndUUIDValidator.CommonBarcodeAndUUIDValidatorException e) {
                isValid = false;
                addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                        .append(HEADER_TYPE_SAMPLE)
                        .append("'. Could not retrieve metadata for Sample UUID '")
                        .append(sampleUUID).append("'.").toString());
            }
            final String sampleTcgaBarcode = valueMap.get(SAMPLE_KEY_SAMPLE_TCGA_BARCODE);
            final String individual = valueMap.get(SAMPLE_KEY_INDIVIDUAL);

            final boolean isValidSampleTcgaBarcode = validateSampleHeaderSampleTcgaBarcode(sampleTcgaBarcode, sampleUUID, context);
            isValid &= isValidSampleTcgaBarcode;

            isValid = validateTcgaIndividualHeaderUuidTransition(metadata, sampleUUID, sampleTcgaBarcode, individualValue, context) && isValid;
        }
        return isValid;
    }

    /**
     * Validates that SampleTcgaBarcode is the correct barcode for the given SampleUUID
     *
     * @param sampleTcgaBarcode SampleTcgaBarcode value to validate
     * @param sampleUUID        SampleUUID that SampleTcgaBarcode is associated to
     * @param qcContext         the context
     * @return <code>true</code> if SampleTcgaBarcode is the correct barcode for the given SampleUUID, <code>false</code> otherwise
     */
    protected boolean validateSampleHeaderSampleTcgaBarcode(final String sampleTcgaBarcode,
                                                            final String sampleUUID,
                                                            final QcContext qcContext) {
        boolean result;

        if (StringUtils.isNotBlank(sampleTcgaBarcode)) {

            final String errorMessage = qcLiveBarcodeAndUUIDValidator.validateBarcodeFormat(sampleTcgaBarcode, qcContext.getFile().getName(), "Aliquot");
            result = errorMessage == null;

            if (result) { // Barcode is an aliquot

                result = qcLiveBarcodeAndUUIDValidator.validateUUIDBarcodeMapping(sampleUUID, sampleTcgaBarcode);

                if (!result) {
                    qcContext.addError(new StringBuffer().append("SampleUUID '")
                            .append(sampleUUID)
                            .append("' and SampleTCGABarcode '")
                            .append(sampleTcgaBarcode)
                            .append("' do not match.").toString());
                }

            } else {
                qcContext.addError(errorMessage);
            }

        } else {
            result = false;
            // context doesn't need to be updated as this error must have been captured earlier
        }

        return result;
    }

    /**
     * Validates the SAMPLE header SampleUUID value:
     * <p/>
     * - has valid UUID format
     * - is a UUID for an aliquot
     * <p/>
     * Note:
     * - errors are added to the context
     * - errors about empty or null value are not added to the context as they are caught by earlier validation
     *
     * @param sampleUUID the SampleUUID value to validate
     * @param context    the context
     * @return <code>true</code> if valid, <code>false</code> otherwise
     */
    protected boolean validateSampleHeaderSampleUUIDValue(final String sampleUUID,
                                                          final QcContext context) {
        boolean result;

        if (StringUtils.isNotBlank(sampleUUID)) {

            final String fileName = context.getFile().getName();
            result = qcLiveBarcodeAndUUIDValidator.validateUuid(sampleUUID, context, fileName, true);

            if (result) {// UUID has the right format and has been received by DCC

                result = qcLiveBarcodeAndUUIDValidator.isAliquotUUID(sampleUUID);

                if (!result) {
                    final String errorMessage = "The uuid '" + sampleUUID + "' is not assigned to an aliquot";
                    context.addError(errorMessage);
                }
            }
        } else {
            result = false;
            // context doesn't need to be updated as this error must have been captured earlier
        }

        return result;
    }

    protected boolean validateSampleHeaderIndividualValue(final MetaDataBean metadata, final String individual,
                                                          final String sampleUUID, final String sampleTcgaBarcode,
                                                          final QcContext context) {
        Boolean isValid = true;
        if (StringUtils.isNotBlank(individual) && StringUtils.isNotBlank(sampleUUID)) {
            if (metadata != null) {
                final String participantBarcode = metadata.getPatientBuiltBarcode();
                if (!individual.equalsIgnoreCase(participantBarcode)) {
                    isValid &= false;
                    context.addError(new StringBuffer().append("Individual Value '")
                            .append(individual)
                            .append("' does not match the participant barcode '")
                            .append(participantBarcode)
                            .append("' associated with sampleUUID '")
                            .append(sampleUUID)
                            .append("'.").toString());
                }
            }
            final String participantTcgaBarcode = qcLiveBarcodeAndUUIDValidator.getPatientBarcode(sampleTcgaBarcode);
            if (!individual.equalsIgnoreCase(participantTcgaBarcode)) {
                isValid &= false;
                context.addError(new StringBuffer().append("Individual Value '")
                        .append(individual)
                        .append("' does not match the participant barcode '")
                        .append(participantTcgaBarcode)
                        .append("' associated with sampleTcgaBarcode '")
                        .append(sampleTcgaBarcode)
                        .append("'.").toString());
            }
        }
        return isValid;
    }


    private boolean validateSampleNameValue(final String value, final QcContext context) {
        if (!context.isStandaloneValidator() && value != null) {
            return qcLiveBarcodeAndUUIDValidator.validateBarcodeOrUuid(value, context, context.getFile().getName(), true);
        }
        return true;
    }


    /**
     * validate batch of uuids and barcodes.
     * Batch validation is only for standalone validator
     *
     * @param barcodes
     * @param uuids
     * @param context
     * @return
     */
    private boolean batchValidateSampleNameValues(final List<String> barcodes, final List<String> uuids, final QcContext context) {
        Boolean isValid = true;
        if (context.isStandaloneValidator()) {

            if (barcodes.size() > 0) {
                isValid &= qcLiveBarcodeAndUUIDValidator.batchValidate(barcodes, context, context.getFile().getName(), true);
            }
            if (uuids.size() > 0) {
                isValid &= qcLiveBarcodeAndUUIDValidator.batchValidate(uuids, context, context.getFile().getName(), true);
            }
        }
        return isValid;
    }

    private boolean validateSampleHeaderValue(final String key, final String value, final QcContext context) {

        final Matcher whiteSpaceMatcher = WHITESPACE_PATTERN.matcher(value);
        final Matcher quotesMatcher = QUOTES_PATTERN.matcher(value);
        final Matcher angularBracketsMatcher = ANGULAR_BRACKETS_PATTERN.matcher(value);
        boolean isValid = true;

        if (value != null && !value.isEmpty()) {
            if (value.split(",").length > 1) {
                if (angularBracketsMatcher.matches()) {
                    final String[] values = value.substring(1, value.length() - 1).split(",");
                    for (final String listValue : values) {
                        if (!validateSampleHeaderValue(key, listValue, context)) { // recursive!
                            isValid = false;
                        }
                    }
                } else {
                    addErrorMessage(context, new StringBuilder().
                            append("VCF header validation error: SAMPLE header value '")
                            .append(value).append("' for key '").append(key).
                                    append("' is comma separated, and must be contained within angle '<>' brackets.").
                                    toString());
                    isValid = false;
                }
            } else if (whiteSpaceMatcher.find()) {
                if (!quotesMatcher.matches()) {
                    addErrorMessage(context, new StringBuilder().
                            append("VCF header validation error: SAMPLE header value '")
                            .append(value).append("' for key '").append(key).
                                    append("' contains whitespace and must be enclosed in double quotes").toString());
                    isValid = false;
                }
            }
        } else {
            addErrorMessage(context, new StringBuilder().append("VCF header validation error: SAMPLE header value '")
                    .append(value).append("' for key '").append(key).
                            append("' could not be parsed because it is either null or empty").toString());
            isValid = false;
        }

        return isValid;
    }

    private boolean validateSampleKeyGenomes(final String value, final Set<Integer> numTokens, final String headerName, final QcContext context) {
        boolean isValid = true;
        if (value != null) {
            if (GENOMES_PATTERN.matcher(value).matches()) {
                String extractedValue = value.substring(1, value.lastIndexOf('>'));
                numTokens.add(extractedValue.split(",", -1).length);
            } else {
                addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                        .append(headerName)
                        .append("' must have '")
                        .append(SAMPLE_KEY_GENOMES)
                        .append("' enclosed within angular brackets and cannot contain whitespace or nested angular brackets.").toString());
                isValid = false;
            }
        }
        return isValid;
    }

    private boolean validateSampleKeyGenomeDescription(final String value, final Set<Integer> numTokens, final String headerName, final QcContext context) {
        boolean isValid = true;
        if (value != null) {
            if (GENOME_DESCRIPTION_PATTERN.matcher(value).matches()) {
                String extractedValue = value.substring(1, value.lastIndexOf('>'));
                numTokens.add(extractedValue.split(",", -1).length);
            } else {
                addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                        .append(headerName)
                        .append("' must have '")
                        .append(SAMPLE_KEY_GENOME_DESCRIPTION)
                        .append("' enclosed within angular brackets and the values must be enclosed in double quotes.").toString());
                isValid = false;
            }
        }
        return isValid;
    }

    private boolean validateSampleKeyMixture(final String value, final Set<Integer> numTokens, final String headerName, final QcContext context) {
        boolean isValid = true;
        if (value != null) {
            if (ANGULAR_BRACKETS_PATTERN.matcher(value).matches()) {
                String extractedValue = value.substring(1, value.lastIndexOf('>'));
                String[] values = extractedValue.split(",", -1);
                numTokens.add(values.length);
                Float total = new Float(0);
                for (String val : values) {
                    try {
                        float f = Float.parseFloat(val);
                        total = total + f;
                        if (!(f >= 0.0 && f <= 1.0)) {
                            addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                                    .append(headerName)
                                    .append("' has a floating point value '")
                                    .append(val)
                                    .append("' that is not between 0.0 and 1.0 inclusive in '")
                                    .append(SAMPLE_KEY_MIXTURE)
                                    .append("'").toString());
                            isValid = false;
                        }
                    } catch (NumberFormatException nfe) {
                        addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                                .append(headerName)
                                .append("' has an invalid floating point number value '")
                                .append(val)
                                .append("' in '")
                                .append(SAMPLE_KEY_MIXTURE)
                                .append("'").toString());
                        isValid = false;
                    }
                }
                if (total != 1.0) {
                    addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                            .append(headerName)
                            .append("' must have all values in '")
                            .append(extractedValue)
                            .append("' sum upto 1.0 in '")
                            .append(SAMPLE_KEY_MIXTURE)
                            .append("'").toString());
                    isValid = false;
                }
            } else {
                addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                        .append(headerName)
                        .append("' must have '")
                        .append(SAMPLE_KEY_MIXTURE)
                        .append("' enclosed within angular brackets.").toString());
                isValid = false;
            }
        }
        return isValid;
    }

    /**
     * validate a column line from a vcf file
     *
     * @param vcf     the vcf file object
     * @param context qc context
     * @return true if validation passes
     */
    private boolean validateColumnLine(VcfFile vcf, QcContext context) {
        boolean valid = true;
        if (vcf.getColumnHeader() != null) {
            final List<String> columns = vcf.getColumnHeader();
            for (int i = 0; i < VcfFile.VALID_COLUMN_LINE.length; i++) {
                if (columns.size() <= i || !VcfFile.VALID_COLUMN_LINE[i].equals(columns.get(i))) {
                    valid = false;
                    addErrorMessage(context, new StringBuilder().append("VCF Column header validation error: column at position ").
                            append(i + 1).append(" should be '").append(VcfFile.VALID_COLUMN_LINE[i]).append("'").toString());
                }
            }
            return valid;
        } else {
            return false;
        }
    }

    /**
     * Method to validate the headers of the VCF file according to the spec.
     *
     * @param vcf     the VcfFile object
     * @param context the QcContext  @return true if the headers pass all validation rules, false otherwise
     * @return true if headers are valid, false if not
     */
    private boolean validateHeaders(final VcfFile vcf, final QcContext context) {

        boolean headersAreValid = validateFirstHeader(vcf, context);
        headersAreValid = validateTcgaVersion(vcf, context) && headersAreValid;

        headersAreValid = validateRequiredHeaders(vcf, vcfFileHeaderValidator, context) && headersAreValid;
        for (int i = 1; i <= vcf.getNumberOfHeaders(); i++) {
            headersAreValid = vcfFileHeaderValidator.validate(vcf.getHeader(i), context) && headersAreValid;
            checkDuplicatedHeader(vcf, vcf.getHeader(i).getName(), context);
        }

        return headersAreValid;
    }

    /**
     * check Duplicate Headers
     *
     * @param vcf
     * @param header
     * @param context
     */
    private void checkDuplicatedHeader(final VcfFile vcf, final String header, final QcContext context) {
        final List<VcfFileHeader> vcfFileHeaders = vcf.getHeadersForType(header);
        if (vcfFileHeaders.size() > 1) {
            for (final VcfFileHeader fileHeader : vcfFileHeaders) {
                if (fileHeader.getValue() != null) {
                    addWarningMessage(context, new StringBuilder().append("VCF header validation error: header '")
                            .append(header).append("' is duplicated.").toString());
                }
            }
        }
    }

    /**
     * validate existence of required headers
     *
     * @param vcf
     * @param headerValidator
     * @param context
     * @return true if required headers exist
     */
    private boolean validateRequiredHeaders(final VcfFile vcf, final VcfFileHeaderValidator headerValidator,
                                            final QcContext context) {
        boolean isValid = true;
        final List<String> headers = headerValidator.getRequiredHeaderTypes();
        if (headers != null) {
            for (final String header : headers) {
                if (vcf.getHeadersForType(header).size() < 1) {
                    addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                            .append(header).append("' is missing.").toString());
                    isValid = false;
                }
                if (vcf.getHeadersForType(header).size() > 1) {
                    addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                            .append(header).append("' is duplicated.").toString());
                    isValid = false;
                }
            }
        }
        return isValid;
    }

    protected void checkFileFormat(final VcfFile vcf) throws UnsupportedFileException {
        final List<VcfFileHeader> tcgaVersionHeader = vcf.getHeadersForType(VcfFile.HEADER_TYPE_TCGA_VERSION);
        if (tcgaVersionHeader == null || tcgaVersionHeader.size() < 1) {
            throw new UnsupportedFileException("General VCF file is not supported. Only TCGA VCF file is supported.");
        }
    }

    protected boolean validateTcgaVersion(final VcfFile vcf, final QcContext context) {
        boolean isValid = true;
        List<VcfFileHeader> tcgaVersionHeaders = vcf.getHeadersForType(VcfFile.HEADER_TYPE_TCGA_VERSION);
        if (tcgaVersionHeaders == null || tcgaVersionHeaders.size() < 1) {
            // not a TCGA file, throw an error message
            addErrorMessage(context, new StringBuilder().append("##tcgaversion header value must be specified").toString());
            isValid = false;
        }
        if (tcgaVersionHeaders.size() > 1) {
            addErrorMessage(context, new StringBuilder().append("VCF header validation error: header '")
                    .append(VcfFile.HEADER_TYPE_TCGA_VERSION).append("' is duplicated.").toString());
            isValid = false;
        }
        final String tcgaVersionValue = tcgaVersionHeaders.get(0).getValue();
        if (tcgaVersionValue == null || !tcgaVersionValue.equals(getTcgaVcfVersion())) {
            addErrorMessage(context, new StringBuilder().append("Invalid ##tcgaversion header value ")
                    .append(tcgaVersionValue).append(". Must be ").append(getTcgaVcfVersion())
                    .toString());
            isValid = false;
        }
        return isValid;
    }


    /**
     * Figures out what to return based on results of each file validation.  In this case, will return true if
     * all files returned true, and false otherwise.
     *
     * @param results the results of each processFile call
     * @param context the qc context
     * @return true if all files were valid, false otherwise
     */
    @Override
    protected Boolean getReturnValue(final Map<File, Boolean> results, final QcContext context) {
        return !results.containsValue(false);
    }


    /**
     * Figures out what to return if this is the wrong kind of archive for this processing.  In this case: true.
     *
     * @param archive the input archive
     * @return true
     */
    @Override
    protected Boolean getDefaultReturnValue(final Archive archive) {
        return true;
    }

    /**
     * @return the file extension of the files this class processes (.vcf)
     */
    @Override
    protected String getFileExtension() {
        return ".vcf";
    }

    /**
     * Returns true so will run on any archive type.
     *
     * @param archive the input archive
     * @return true if this processor can process this archive, false if not
     */
    @Override
    protected boolean isCorrectArchiveType(final Archive archive) {
        return true; // run on any archive
    }

    /**
     * Gets the name of the processor, in descriptive English.
     *
     * @return the descriptive name of this processor
     */
    @Override
    public String getName() {
        return "Variant Call Format file validator";
    }

    public String getTcgaVcfVersion() {
        return tcgaVcfVersion;
    }

    /**
     * Sets the value used to validate the tcgaversion header, if it exists.
     *
     * @param tcgaVcfVersion the expected tcgaversion value
     */
    public void setTcgaVcfVersion(final String tcgaVcfVersion) {
        this.tcgaVcfVersion = tcgaVcfVersion;
    }

    public void setVcfFileHeaderValidator(final VcfFileHeaderValidator vcfFileHeaderValidator) {
        this.vcfFileHeaderValidator = vcfFileHeaderValidator;
    }

    /**
     * Gets the VcfFileDataLineValidator for use with a new VCF file.  Ideally this
     * method will return a new instance.
     *
     * @return VcfFileDataLineValidator instance ready for use by a new VCF file validation call
     */
    public abstract VcfFileDataLineValidator getVcfFileDataLineValidator();

    public QcLiveBarcodeAndUUIDValidator getQcLiveBarcodeAndUUIDValidator() {
        return qcLiveBarcodeAndUUIDValidator;
    }

    public void setQcLiveBarcodeAndUUIDValidator(final QcLiveBarcodeAndUUIDValidator qcLiveBarcodeAndUUIDValidator) {
        this.qcLiveBarcodeAndUUIDValidator = qcLiveBarcodeAndUUIDValidator;
    }

    public VcfFileHeaderValidator getVcfFileHeaderValidator() {
        return vcfFileHeaderValidator;
    }

}
