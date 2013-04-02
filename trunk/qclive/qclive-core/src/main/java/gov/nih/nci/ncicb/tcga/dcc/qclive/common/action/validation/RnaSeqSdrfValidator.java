/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedContentNavigator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validator for RNASeq SDRFs.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class RnaSeqSdrfValidator extends AbstractSdrfValidator {

    // v2 platforms
    public static final String ILLUMINAGA_RNASEQ_V2 = "IlluminaGA_RNASeqV2";
    public static final String ILLUMINAGA_HISEQ_V2 = "IlluminaHiSeq_RNASeqV2";
    public static final String V1_EXON_FILE_EXTENSION = "*.exon.quantification.txt";
    public static final String V2_EXON_FILE_EXTENSION = "*.exon_quantification.txt";
    public static final String V1_JUNCTION_FILE_EXTENSION = "*.spljxn.quantification.txt";
    public static final String V2_JUNCTION_FILE_EXTENSION = "*.junction_quantification.txt";

    private static final String ASSAY_NAME_COLUMN_HEADER = "Assay Name";
    private static final String DERIVED_DATA_FILE_COLUMN_NAME = "Derived Data File";
    private static final String MATERIAL_TYPE_COLUMN_NAME = "Material Type";
    private static final String PROTOCOL_REF_COLUMN_NAME = "Protocol REF";
    private static final String COMMENT_NCBI_SRA_EXPERIMENT_ACCESSION_COLUMN_NAME = "Comment [NCBI SRA Experiment Accession]";
    private static final String COMMENT_NCBI_DB_GAP_EXPERIMENT_ACCESSION_COLUMN_NAME = "Comment [NCBI dbGAP Experiment Accession]";
    private static final String ANNOTATION_REF_COLUMN_NAME = "Annotation REF";
    private static final String DATA_TRANSFORMATION_NAME_COLUMN_NAME = "Data Transformation Name";

    // key = column name, value = is it a required column
    private static final Map<String, Boolean> CHECK_COLUMNS = new HashMap<String, Boolean>();
    private static final Pattern PROTOCOL_PATTERN = Pattern.compile("([\\w\\.\\-]+):([\\w\\.\\-]+):([\\w\\.\\-]+):([0-9]+)");
    private static final Pattern SXA_PATTERN = Pattern.compile("SRX[0-9]{6}");
    private static final Pattern DBGAP_ACCESSION_PATTERN = Pattern.compile("ph.[0-9]{6}\\.v[0-9]+\\.p[0-9]+");
    //    private static final Pattern HTTP_URL_PATTERN = Pattern.compile("https?://[\\w\\d:#@%/;$()~_?\\+-=\\\\.&]+");
    private static final String MATERIAL_TYPE_TOTAL_RNA = "Total RNA";

    static {
        CHECK_COLUMNS.put(EXTRACT_NAME_COLUMN_NAME, true);
        CHECK_COLUMNS.put(MATERIAL_TYPE_COLUMN_NAME, true);
        CHECK_COLUMNS.put(PROTOCOL_REF_COLUMN_NAME, true);
        CHECK_COLUMNS.put(ASSAY_NAME_COLUMN_HEADER, true);
        CHECK_COLUMNS.put(COMMENT_NCBI_SRA_EXPERIMENT_ACCESSION_COLUMN_NAME, true);
        CHECK_COLUMNS.put(ANNOTATION_REF_COLUMN_NAME, true);
        CHECK_COLUMNS.put(DATA_TRANSFORMATION_NAME_COLUMN_NAME, true);
        CHECK_COLUMNS.put("Derived Data File REF", true);
        CHECK_COLUMNS.put("Comment [Genome reference]", true);
        CHECK_COLUMNS.put(COMMENT_NCBI_DB_GAP_EXPERIMENT_ACCESSION_COLUMN_NAME, true);
        CHECK_COLUMNS.put("Comment [TCGA Include for Analysis]", true);
        CHECK_COLUMNS.put("Comment [TCGA Data Type]", true);
        CHECK_COLUMNS.put("Comment [TCGA Data Level]", true);
        CHECK_COLUMNS.put(DERIVED_DATA_FILE_COLUMN_NAME, true);
        CHECK_COLUMNS.put("Comment [TCGA Archive Name]", true);
        CHECK_COLUMNS.put(AbstractSdrfValidator.BARCODE_COMMENT_COLUMN_NAME, false);
    }

    @Override
    protected Collection<String> getAllowedSdrfHeaders() throws ProcessorException {
        return CHECK_COLUMNS.keySet();
    }

    @Override
    protected Map<String, Boolean> getColumnsToCheck() {
        return CHECK_COLUMNS;
    }

    @Override
    protected boolean validateFileHeaderAndLevel(final QcContext context, final String header, final int row, final String level) {
        // nothing to do here for now
        return true;
    }

    @Override
    protected boolean runSpecificValidations(final QcContext context, final TabDelimitedContentNavigator sdrfNavigator) {
        boolean isValid = validateDataFilesForBarcodes(context, sdrfNavigator);
        isValid &= compareExtractAndAssayNames(context, sdrfNavigator);

        return isValid;
    }



    /*
     * Assay Name values must contain the Extract Name.
     */
    protected boolean compareExtractAndAssayNames(final QcContext context, final TabDelimitedContentNavigator sdrfNavigator) {
        boolean areTheSame = true;
        // Assay Name should contain a barcode which is the same as the Extract Name barcode
        //TODO: check what to do when extract is a uuid instead of a barcode
        final List<String> extractNames = sdrfNavigator.getColumnValues(sdrfNavigator.getHeaderIDByName(EXTRACT_NAME_COLUMN_NAME));
        final List<Integer> assayNameColumns = sdrfNavigator.getHeaderIdsForName(ASSAY_NAME_COLUMN_HEADER);
        for (int i = 0; i < extractNames.size(); i++) {
            for (final int assayNameColumn : assayNameColumns) {
                final String assayName = sdrfNavigator.getValueByCoordinates(assayNameColumn, i + 1); // coordinates are 1-based, not 0-based
                if (!assayName.contains(extractNames.get(i))) {
                    context.addError(MessageFormat.format(
                            MessagePropertyType.LINE_ERROR,
                            (i + NUM_HEADERS + ZERO_INDEX_OFFSET),
                            assayNameColumn,
                            ASSAY_NAME_COLUMN_HEADER,
                            new StringBuilder().append(EXTRACT_NAME_COLUMN_NAME).append(" (").append(extractNames.get(i)).append(")").toString(),
                            assayName));
                    areTheSame = false;
                }
            }
        }
        return areTheSame;
    }

    /**
     * Return <code>true</code> if references to all required files for Level 3 data are present, <code>false</code> otherwise.
     * <p/>
     * For each aliquot represented in the SDRF, if there is Level 3 data,
     * then there must be a reference to a Level 3 gene file, an exon file, and a splice junction file. Wig files are optional.
     *
     * @param context       the qclive context
     * @param sdrfNavigator a {@link TabDelimitedContentNavigator} for the SDRF
     * @return <code>true</code> if references to all required files for Level 3 data are present, <code>false</code> otherwise
     */
    protected boolean validateDataFilesForBarcodes(final QcContext context, final TabDelimitedContentNavigator sdrfNavigator) {
        boolean isValid = true;

        // first build a map of all the barcodes that have each type of file
        final Map<String, String> geneFileForBarcodes = new HashMap<String, String>();
        final Map<String, String> exonFileForBarcodes = new HashMap<String, String>();
        final Map<String, String> junctionFileForBarcodes = new HashMap<String, String>();
        final Map<String, String> wigFileForBarcodes = new HashMap<String, String>();

        // rnaseqv2 files
        final Map<String, String> rsemGeneNormalized = new HashMap<String, String>();
        final Map<String, String> rsemGeneResults = new HashMap<String, String>();
        final Map<String, String> rsemIsoformNormalized = new HashMap<String, String>();
        final Map<String, String> rsemIsoformResults = new HashMap<String, String>();

        // List to store the aliquots that will need to be checked for required and optional files
        final List<String> aliquotsWithLevel3Data = new ArrayList<String>();

        final Integer extractNameIndex = sdrfNavigator.getHeaderIDByName(EXTRACT_NAME_COLUMN_NAME);
        final List<Integer> dataFileIndexes = sdrfNavigator.getHeaderIdsForName(DERIVED_DATA_FILE_COLUMN_NAME);
        for (int i = 1; i < sdrfNavigator.getNumRows(); i++) {
            final String extractName = sdrfNavigator.getValueByCoordinates(extractNameIndex, i);
            for (final Integer dataFileIndex : dataFileIndexes) {
                final Map<String, Integer> commentColumns = getFileCommentColumns(sdrfNavigator, dataFileIndex);
                final Integer dataLevelColumn = commentColumns.get(COMMENT_DATA_LEVEL);
                final String dataLevel = sdrfNavigator.getValueByCoordinates(dataLevelColumn, i);
                if ("Level 3".equals(dataLevel)) {

                    aliquotsWithLevel3Data.add(extractName);

                    final String filename = sdrfNavigator.getValueByCoordinates(dataFileIndex, i);

                    final WildcardFileFilter exonFilter = new WildcardFileFilter(Arrays.asList(RNASeqExonFileValidator.EXON_FILE_EXTENSION.split(",")));
                    final WildcardFileFilter junctionFilter = new WildcardFileFilter(Arrays.asList(RNASeqJunctionFileValidator.JUNCTION_FILE_EXTENSION.split(",")));

                    if (filename.endsWith(RNASeqGeneFileValidator.GENE_FILE_EXTENSION)) {
                        geneFileForBarcodes.put(extractName, filename);
                    } else if (exonFilter.accept(new File(filename))) {
                        exonFileForBarcodes.put(extractName, filename);
                    } else if (junctionFilter.accept(new File(filename))) {
                        junctionFileForBarcodes.put(extractName, filename);
                    } else if (filename.endsWith("wig")) {
                        wigFileForBarcodes.put(extractName, filename);
                    } else if (filename.endsWith(RNASeqRSEMGeneNormalizedFileValidator.RSEM_GENE_NORMAL_FILE_EXTENSION)) {
                        rsemGeneNormalized.put(extractName, filename);
                    } else if (filename.endsWith(RNASeqRSEMIsoformFileValidator.RSEM_ISOFORM_RESULTS_FILE_EXTENSION)) {
                        rsemIsoformResults.put(extractName, filename);
                    } else if (filename.endsWith(RNASeqRSEMIsoformNormalizedFileValidator.RSEM_ISOFORM_NORMAL_FILE_EXTENSION)) {
                        rsemIsoformNormalized.put(extractName, filename);
                    } else if (filename.endsWith(RNASeqRSEMGeneResultsFileValidator.RSEM_GENES_RESULTS_FILE_EXTENSION)) {
                        rsemGeneResults.put(extractName, filename);
                    } else {
                        // find Data Transformation Name for this file
                        if (sdrfNavigator.getHeaders().get(dataFileIndex - 1).equals(DATA_TRANSFORMATION_NAME_COLUMN_NAME)) {
                            final String dataTransformationName = sdrfNavigator.getValueByCoordinates(dataFileIndex - 1, i);
                            if (dataTransformationName.toLowerCase().contains("gene")) {
                                context.addError(MessageFormat.format(
                                        MessagePropertyType.FILE_EXTENSION_ERROR,
                                        (i + NUM_HEADERS),
                                        dataFileIndex,
                                        "gene",
                                        RNASeqGeneFileValidator.GENE_FILE_EXTENSION));
                                geneFileForBarcodes.put(extractName, filename);
                                isValid = false;
                            } else if (dataTransformationName.toLowerCase().contains("exon")) {
                                context.addError(MessageFormat.format(
                                        MessagePropertyType.FILE_EXTENSION_ERROR,
                                        (i + NUM_HEADERS),
                                        dataFileIndex,
                                        "exon",
                                        RNASeqExonFileValidator.EXON_FILE_EXTENSION));
                                exonFileForBarcodes.put(extractName, filename);
                                isValid = false;
                            } else if (dataTransformationName.toLowerCase().contains("splice")) {
                                context.addError(MessageFormat.format(
                                        MessagePropertyType.FILE_EXTENSION_ERROR,
                                        (i + NUM_HEADERS),
                                        dataFileIndex,
                                        "splice",
                                        RNASeqJunctionFileValidator.JUNCTION_FILE_EXTENSION));
                                junctionFileForBarcodes.put(extractName, filename);
                                isValid = false;
                            } else if (dataTransformationName.toLowerCase().contains("coverage")) {
                                context.addError(MessageFormat.format(
                                        MessagePropertyType.FILE_EXTENSION_ERROR,
                                        (i + NUM_HEADERS),
                                        dataFileIndex,
                                        "coverage",
                                        ".wig"));
                                wigFileForBarcodes.put(extractName, filename);
                                isValid = false;
                            } else {
                                context.addWarning(MessageFormat.format(
                                        MessagePropertyType.NO_TRANSFORMATION_NAME_FOR_FILE_WARNING,
                                        (i + NUM_HEADERS),
                                        dataFileIndex,
                                        filename));
                            }
                        } else {
                            context.addError(MessageFormat.format(
                                    MessagePropertyType.COLUMN_PRECEDENCE_ERROR,
                                    (i + NUM_HEADERS),
                                    dataFileIndex,
                                    DATA_TRANSFORMATION_NAME_COLUMN_NAME,
                                    DERIVED_DATA_FILE_COLUMN_NAME));
                            isValid = false;
                        }
                    }
                }
            }
        }

        /**
         * check to make sure that v1 and v2 have the correct files and do not overlap
         */
        if (isRNASeqV2Archive(context.getArchive().getPlatform())) {
            isValid &= checkRnaSeqV2Files(
                    aliquotsWithLevel3Data,
                    context,
                    exonFileForBarcodes,
                    junctionFileForBarcodes,
                    rsemGeneNormalized,
                    rsemGeneResults,
                    rsemIsoformNormalized,
                    rsemIsoformResults);
        } else {
            isValid &= checkRnaSeqV1Files(
                    aliquotsWithLevel3Data,
                    context,
                    geneFileForBarcodes,
                    exonFileForBarcodes,
                    junctionFileForBarcodes,
                    wigFileForBarcodes);
        }
        return isValid;
    }

    protected boolean isRNASeqV2Archive(final String platform) {
        if (StringUtils.isNotEmpty(platform)) {
            return platform.contains(ILLUMINAGA_RNASEQ_V2) || platform.contains(ILLUMINAGA_HISEQ_V2);
        } else {
            return false;
        }
    }

    /**
     * Check aliquots for v2 and make sure that each of them has all of the required files
     */
    protected boolean checkRnaSeqV2Files(List<String> aliquotsWithLevel3Data,
                                         QcContext context,
                                         Map<String, String> exonFileForBarcodes,
                                         Map<String, String> junctionFileForBarcodes,
                                         Map<String, String> rsemGeneNormalized,
                                         Map<String, String> rsemGeneResults,
                                         Map<String, String> rsemIsoformNormalized,
                                         Map<String, String> rsemIsoformResults
    ) {
        boolean isValid = true;
        for (final String aliquot : aliquotsWithLevel3Data) {

            if (!exonFileForBarcodes.containsKey(aliquot)) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.MISSING_REQUIRED_VALUE_ERROR,
                        V2_EXON_FILE_EXTENSION, aliquot));
                isValid = false;
            }
            if (!junctionFileForBarcodes.containsKey(aliquot)) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.MISSING_REQUIRED_VALUE_ERROR,
                        V2_JUNCTION_FILE_EXTENSION,
                        aliquot));
                isValid = false;
            }
            if (!rsemGeneNormalized.containsKey(aliquot)) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.MISSING_REQUIRED_VALUE_ERROR,
                        RNASeqRSEMGeneNormalizedFileValidator.RSEM_GENE_NORMAL_FILE_EXTENSION,
                        aliquot));
                isValid = false;
            }
            if (!rsemGeneResults.containsKey(aliquot)) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.MISSING_REQUIRED_VALUE_ERROR,
                        RNASeqRSEMGeneResultsFileValidator.RSEM_GENES_RESULTS_FILE_EXTENSION,
                        aliquot));
                isValid = false;
            }
            if (!rsemIsoformNormalized.containsKey(aliquot)) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.MISSING_REQUIRED_VALUE_ERROR,
                        RNASeqRSEMIsoformNormalizedFileValidator.RSEM_ISOFORM_NORMAL_FILE_EXTENSION,
                        aliquot));
                isValid = false;
            }
            if (!rsemIsoformResults.containsKey(aliquot)) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.MISSING_REQUIRED_VALUE_ERROR,
                        RNASeqRSEMIsoformFileValidator.RSEM_ISOFORM_RESULTS_FILE_EXTENSION,
                        aliquot));
                isValid = false;
            }

        }

        return isValid;
    }

    // iterate through the aliquots that have Level 3 data
    // and make sure that each of them has all the 3 required files
    protected boolean checkRnaSeqV1Files(List<String> aliquotsWithLevel3Data,
                                         QcContext context,
                                         Map<String, String> geneFileForBarcodes,
                                         Map<String, String> exonFileForBarcodes,
                                         Map<String, String> junctionFileForBarcodes,
                                         Map<String, String> wigFileForBarcodes) {
        boolean isValid = true;


        for (final String aliquot : aliquotsWithLevel3Data) {

            if (!geneFileForBarcodes.containsKey(aliquot)) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.MISSING_REQUIRED_VALUE_ERROR,
                        RNASeqGeneFileValidator.GENE_FILE_EXTENSION,
                        aliquot));
                isValid = false;
            }

            if (!exonFileForBarcodes.containsKey(aliquot)) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.MISSING_REQUIRED_VALUE_ERROR,
                        V1_EXON_FILE_EXTENSION,
                        aliquot));
                isValid = false;
            }

            if (!junctionFileForBarcodes.containsKey(aliquot)) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.MISSING_REQUIRED_VALUE_ERROR,
                        V1_JUNCTION_FILE_EXTENSION,
                        aliquot));
                isValid = false;
            }

            if (!wigFileForBarcodes.containsKey(aliquot)) {
                // just a warning for wig files, is not required
                context.addWarning(MessageFormat.format(
                        MessagePropertyType.VALUE_NOT_PROVIDED_WARNING,
                        "wig file",
                        aliquot));
            }
        }

        return isValid;

    }

    @Override
    protected boolean getDataRequired() {
        // this means that a column can contain all "->" and that is ok
        return false;
    }

    /**
     * Checks the values for certain columns to make sure they follow the spec.
     *
     * @param columnName the column header
     * @param value      the value
     * @param lineNum    the line where the value was from
     * @param context    the qc context
     * @return if the value is valid
     */
    @Override
    protected boolean validateColumnValue(final String columnName, final String value, final int lineNum, final QcContext context) {
        if (MATERIAL_TYPE_COLUMN_NAME.equals(columnName) && !MATERIAL_TYPE_TOTAL_RNA.equals(value)) {
            context.addError(MessageFormat.format(
                    MessagePropertyType.LINE_VALUE_ERROR,
                    lineNum,
                    MATERIAL_TYPE_COLUMN_NAME,
                    MATERIAL_TYPE_TOTAL_RNA,
                    value));
            return false;
        } else if (columnName.equals(PROTOCOL_REF_COLUMN_NAME)) {
            final Matcher protocolMatcher = PROTOCOL_PATTERN.matcher(value);
            if (!protocolMatcher.matches()) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.LINE_VALUE_FORMAT_ERROR,
                        lineNum,
                        PROTOCOL_REF_COLUMN_NAME,
                        "domain:protocol:platform:version",
                        value));
                return false;
            }
        } else if (columnName.equals(COMMENT_NCBI_SRA_EXPERIMENT_ACCESSION_COLUMN_NAME)) {
            if (!SXA_PATTERN.matcher(value).matches()) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.LINE_VALUE_ERROR,
                        lineNum,
                        COMMENT_NCBI_SRA_EXPERIMENT_ACCESSION_COLUMN_NAME,
                        "a null (->) or a valid SRA accession",
                        value));
                return false;
            }

        } else if (columnName.equals(COMMENT_NCBI_DB_GAP_EXPERIMENT_ACCESSION_COLUMN_NAME)) {
            if (!DBGAP_ACCESSION_PATTERN.matcher(value).matches()) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.LINE_VALUE_ERROR,
                        lineNum,
                        COMMENT_NCBI_DB_GAP_EXPERIMENT_ACCESSION_COLUMN_NAME,
                        "a null (->) or a valid dbGaP experiment accession",
                        value));
                return false;
            }
        } else if (columnName.equals(ANNOTATION_REF_COLUMN_NAME)) {
            if (!urlIsValid(value)) {
                context.addError(MessageFormat.format(
                        MessagePropertyType.LINE_VALUE_ERROR,
                        lineNum,
                        COMMENT_NCBI_DB_GAP_EXPERIMENT_ACCESSION_COLUMN_NAME,
                        "a complete and valid URL",
                        value));
            }
        }
        return true;
    }

    protected boolean urlIsValid(final String value) {
        return ConstantValues.HTTP_URL_PATTERN.matcher(value).matches();
    }

}
