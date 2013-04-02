/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedContentNavigator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Validator for the SDRF file of a miRNA archive
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class MiRnaSeqSdrfValidator extends RnaSeqSdrfValidator {

    /**
     * The expected WIG file extension
     */
    public final static String WIG_FILE_EXTENSION = ".wig";

    private static final String DERIVED_DATA_FILE_COLUMN_NAME = "Derived Data File";
    private static final String DATA_TRANSFORMATION_NAME_COLUMN_NAME = "Data Transformation Name";
    private static final String DATA_TRANSFORMATION_NAME_MIRNA = "Quantification_miRNA";
    private static final String DATA_TRANSFORMATION_NAME_ISOFORM = "Quantification_miRNA_Isoform";
    private static final String DATA_TRANSFORMATION_NAME_COVERAGE = "coverage";

    /**
     * For each barcode in the SDRF, there must be a reference to a mirna file and an isoform file.
     * Wig files are optional for now.
     *
     * @param context the context
     * @param sdrfNavigator a tab delimited content navigator to the SDRF file
     * @return <code>true</code> if the mirna and isoform files are found for each barcode in the SDRF file, <code>false</code> otherwise
     */
    @Override
    protected boolean validateDataFilesForBarcodes(final QcContext context, final TabDelimitedContentNavigator sdrfNavigator) {

        boolean isValid = true;

        // First, build a map of all the barcodes that have each type of file

        final Map<String, String> mirnaFileForBarcodes = new HashMap<String, String>();
        final Map<String, String> isoformFileForBarcodes = new HashMap<String, String>();
        final Map<String, String> wigFileForBarcodes = new HashMap<String, String>();

        final Integer extractNameIndex = sdrfNavigator.getHeaderIDByName(EXTRACT_NAME_COLUMN_NAME);
        final List<Integer> dataFileIndexes = sdrfNavigator.getHeaderIdsForName(DERIVED_DATA_FILE_COLUMN_NAME);

        for (int rowNumber=1; rowNumber<sdrfNavigator.getNumRows(); rowNumber++) {
            //TODO: check what to do when extract is a uuid instead of a barcode
            final String extractName = sdrfNavigator.getValueByCoordinates(extractNameIndex, rowNumber);

            for (final Integer dataFileIndex : dataFileIndexes) {

                final String filename = sdrfNavigator.getValueByCoordinates(dataFileIndex, rowNumber);

                if (filename.endsWith(MiRNASeqFileValidator.MIRNA_FILE_EXTENSION)) {
                    mirnaFileForBarcodes.put(extractName, filename);
                } else if (filename.endsWith(MiRNASeqIsoformFileValidator.ISOFORM_FILE_EXTENSION)) {
                    isoformFileForBarcodes.put(extractName, filename);
                } else if (filename.endsWith(WIG_FILE_EXTENSION)) {
                    wigFileForBarcodes.put(extractName, filename);
                } else {
                    // find Data Transformation Name for this file
                    final Integer dataTransformationNameIndex = dataFileIndex - 1; // The column just before the 'Derived Data File' column

                    if (sdrfNavigator.getHeaders().get(dataTransformationNameIndex).equals(DATA_TRANSFORMATION_NAME_COLUMN_NAME)) {

                        final String dataTransformationName = sdrfNavigator.getValueByCoordinates(dataTransformationNameIndex, rowNumber);

                        if (dataTransformationName.contains(DATA_TRANSFORMATION_NAME_MIRNA)
                                && !dataTransformationName.contains(DATA_TRANSFORMATION_NAME_ISOFORM)) {

                        	context.addError(MessageFormat.format(
                                    MessagePropertyType.FILE_EXTENSION_ERROR,
                                    rowNumber + NUM_HEADERS,
                                    dataTransformationNameIndex + 1, // 1-based
                                    DATA_TRANSFORMATION_NAME_MIRNA,
                                    MiRNASeqFileValidator.MIRNA_FILE_EXTENSION));

                            mirnaFileForBarcodes.put(extractName, filename);
                            isValid = false;

                        } else if (dataTransformationName.contains(DATA_TRANSFORMATION_NAME_ISOFORM)) {

                        	context.addError(MessageFormat.format(
                        			MessagePropertyType.FILE_EXTENSION_ERROR,
                        			rowNumber+NUM_HEADERS,
                        			dataTransformationNameIndex + 1, // 1-based
                        			DATA_TRANSFORMATION_NAME_ISOFORM,
                        			MiRNASeqIsoformFileValidator.ISOFORM_FILE_EXTENSION));

                            isoformFileForBarcodes.put(extractName, filename);
                            isValid = false;

                        } else if (dataTransformationName.toLowerCase().contains(DATA_TRANSFORMATION_NAME_COVERAGE)) {

                        	context.addError(MessageFormat.format(
                        			MessagePropertyType.FILE_EXTENSION_ERROR,
                        			rowNumber + NUM_HEADERS,
                        			dataTransformationNameIndex + 1, // 1-based
                        			DATA_TRANSFORMATION_NAME_COVERAGE,
                        			WIG_FILE_EXTENSION));

                            wigFileForBarcodes.put(extractName, filename);
                            isValid = false;

                        } else {
                        	context.addWarning(MessageFormat.format(
                        			MessagePropertyType.NO_TRANSFORMATION_NAME_FOR_FILE_WARNING,
                        			(rowNumber + NUM_HEADERS),
                        			dataFileIndex,
                        			filename));
                        }

                    } else {
                    	context.addError(MessageFormat.format(
                        		MessagePropertyType.COLUMN_PRECEDENCE_ERROR,
                        		(rowNumber+NUM_HEADERS),
                        		dataFileIndex,
                        		DATA_TRANSFORMATION_NAME_COLUMN_NAME,
                        		DERIVED_DATA_FILE_COLUMN_NAME));

                        isValid = false;
                    }
                }
            }
        }

        // Then, iterate through the barcodes and make sure each has the 2 required files

        for (int rowNumber=1; rowNumber<sdrfNavigator.getNumRows(); rowNumber++) {

            final String extractName = sdrfNavigator.getValueByCoordinates(extractNameIndex, rowNumber);

            if (!mirnaFileForBarcodes.containsKey(extractName)) {
            	context.addError(MessageFormat.format(
            			MessagePropertyType.MISSING_REQUIRED_VALUE_ERROR,
            			MiRNASeqFileValidator.MIRNA_FILE_EXTENSION,
            			extractName));
                isValid = false;
            }

            if (!isoformFileForBarcodes.containsKey(extractName)) {
            	context.addError(MessageFormat.format(
            			MessagePropertyType.MISSING_REQUIRED_VALUE_ERROR,
            			MiRNASeqIsoformFileValidator.ISOFORM_FILE_EXTENSION,
            			extractName));
                isValid = false;
            }

            if (!wigFileForBarcodes.containsKey(extractName)) {
                // just a warning for wig files, it is not required
            	context.addWarning(MessageFormat.format(
            			MessagePropertyType.VALUE_NOT_PROVIDED_WARNING,
            			"wig file",
            			extractName));
            }
        }

        return isValid;
    }
}
