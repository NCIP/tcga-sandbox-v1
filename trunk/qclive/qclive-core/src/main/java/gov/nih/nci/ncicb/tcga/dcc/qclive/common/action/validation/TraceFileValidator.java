/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractArchiveFileProcessor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeTumorValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DirectoryListerImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidator;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Validates any .tr files in an archive.  Checks that the format is tab-delimited, with the first field an integer
 * trace ID and the second field a valid barcode.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class TraceFileValidator extends AbstractArchiveFileProcessor<Boolean> {

    public static final String TR_EXTENSION = ".tr";
    private final QcLiveBarcodeAndUUIDValidator qcLiveBarcodeAndUUIDValidator;
    private Pattern nonDigitPattern = Pattern.compile("\\D+");
    private BarcodeTumorValidator barcodeTumorValidator;

    public TraceFileValidator(final QcLiveBarcodeAndUUIDValidator qcLiveBarcodeAndUUIDValidator) {
        this.qcLiveBarcodeAndUUIDValidator = qcLiveBarcodeAndUUIDValidator;
    }

    protected Boolean getReturnValue(final Map<File, Boolean> results, final QcContext context) {
        return !results.values().contains(false);
    }

    protected Boolean processFile(final File file, final QcContext context) throws ProcessorException {
        try {
            return validateTraceFile(file, context);
        }
        catch (IOException e) {
            throw new ProcessorException(new StringBuilder().append("I/O error while reading ").append(file.getName()).append(": ").append(e.getMessage()).toString());
        }
    }

    protected Boolean getDefaultReturnValue(final Archive archive) {
        return true;
    }

    protected String getFileExtension() {
        return TR_EXTENSION;
    }

    protected boolean isCorrectArchiveType(final Archive archive) throws ProcessorException {
        if (archive.getExperimentType().equals(Experiment.TYPE_GSC)) {
            boolean hasTrs = DirectoryListerImpl.getFilesByExtension(archive.getDeployDirectory(), getFileExtension()).length > 0;
            if (archive.getArchiveType().equals(Archive.TYPE_LEVEL_1)) {
                // GSC level 1: ok
                return true;
            } else if (hasTrs) {
                // GSC not level 1: problem
                throw new ProcessorException(new StringBuilder().append("Only ").append(Archive.TYPE_LEVEL_1).
                        append(" ").append(Experiment.TYPE_GSC).append(" archives may contain ").
                        append(TR_EXTENSION).append(" files").toString());
            }
        }
        return false;
    }

    private boolean validateTraceFile(final File traceFile,
                                      final QcContext context) throws IOException, ProcessorException {
        boolean valid = true;
        FileReader fReader = new FileReader(traceFile);
        BufferedReader bufferedReader = new BufferedReader(fReader);
        try {
            final boolean barcodeMustExist = true;
            String line;
            int lineNum = 1;

            // Get barcode validity for all barcodes so that the validation can be batched when the standalone is being used
            final Map<String, Boolean> barcodeValidityMap = getBarcodeValidity(traceFile, barcodeMustExist, traceFile.getName(), context);

            while ((line = bufferedReader.readLine()) != null) {
                final String[] fields = line.split("\\t");
                if (fields.length < 2 || fields.length > 4) {
                    throw new ProcessorException(new StringBuilder().append("Unknown format for file ").append(traceFile.getName()).append(". Each line should have a trace ID and then an aliquot barcode, separated by a tab.").toString());
                } else {
                    String traceId = fields[0];
                    String barcode = fields[1];
                    // make sure trace ID is an integer
                    if (nonDigitPattern.matcher(traceId).matches() && lineNum > 1) {
                        // if not the first line, a non-integer is an error
                    	context.addError(MessageFormat.format(
                    			MessagePropertyType.TRACE_FILE_VALIDATION_ERROR, 
                    			traceFile.getName(),
                                new StringBuilder().append(" line ").append(lineNum).append(" contains an invalid trace ID: '").append(fields[0]).append("'").toString()));
                        context.getArchive().setDeployStatus(Archive.STATUS_INVALID);
                        valid = false;
                    }

                    // if barcode not valid and not first line, is an error
                    if (lineNum > 1) {

                        boolean barcodeIsValid;
                        if(context.isStandaloneValidator()) { // Retrieve the result from the Map of batched validation
                            barcodeIsValid = barcodeValidityMap.get(barcode);
                        } else {
                            barcodeIsValid = qcLiveBarcodeAndUUIDValidator.validate(barcode, context, traceFile.getName(), barcodeMustExist);
                        }

                        if(!barcodeIsValid) {
                            context.addError(MessageFormat.format(
                                    MessagePropertyType.TRACE_FILE_VALIDATION_ERROR,
                                    traceFile.getName(),
                                    new StringBuilder().append(" line ").append(lineNum).append(" contains an invalid barcode: ").append(fields[1]).toString()));
                            context.getArchive().setDeployStatus(Archive.STATUS_INVALID);
                            valid = false;
                        }
                    }

                    if (!barcodeTumorValidator.barcodeIsValidForTumor(barcode, context.getArchive().getTumorType())) {
                    	context.addError(MessageFormat.format(
                    			MessagePropertyType.TRACE_FILE_VALIDATION_ERROR, 
                    			traceFile.getName(),
                    			new StringBuilder().append("File contains a barcode that does not belong to the ").append(context.getArchive().getTumorType()).
                    			append(" disease set: ").append(barcode).toString()));
                    }
                }

                lineNum++;
            }

        } finally {
            bufferedReader.close();
            fReader.close();
            bufferedReader = null;
            fReader = null;
        }
        return valid;
    }

    /**
     * Read from the given trace {@link File}, validate the barcode found on each line and store the result in a {@link Map} of barcode -> validity
     *
     * @param traceFile the trace {@link File} to read from
     * @param barcodeMustExist <code>true</code> if the barcode must exist in the DB, <code>false</code> otherwise
     * @param traceFilename the name of the trace file
     * @param context the context
     * @return a {@link Map} of barcode -> validity
     * @throws IOException
     */
    private Map<String, Boolean> getBarcodeValidity(final File traceFile,
                                                    final boolean barcodeMustExist,
                                                    final String traceFilename,
                                                    final QcContext context) throws IOException {

        final Map<String, Boolean> result = new HashMap<String, Boolean>();

        BufferedReader bufferedReader = null;
        try {
            if(context.isStandaloneValidator()) {

                final FileReader fileReader = new FileReader(traceFile);
                //noinspection IOResourceOpenedButNotSafelyClosed
                bufferedReader = new BufferedReader(fileReader);
                String line;
                String barcode;
                List<String> barcodes = new LinkedList<String>();

                // Read the buffer and get all the barcodes
                while ((line = bufferedReader.readLine()) != null) {

                    final String[] fields = line.split("\\t");

                    if (fields.length >= 2) {

                        barcode = fields[1];
                        barcodes.add(barcode);
                    }
                }

                // batch the validation of the barcodes and store the results in the Map
                result.putAll(qcLiveBarcodeAndUUIDValidator.batchValidateReportIndividualResults(barcodes, context, traceFilename, barcodeMustExist));
            }
        } finally {
            IOUtils.closeQuietly(bufferedReader);
        }

        return result;
    }

    public String getName() {
        return "sample-trace relationship file validation";
    }

    public void setBarcodeTumorValidator(final BarcodeTumorValidator barcodeTumorValidator) {
        this.barcodeTumorValidator = barcodeTumorValidator;
    }
}
