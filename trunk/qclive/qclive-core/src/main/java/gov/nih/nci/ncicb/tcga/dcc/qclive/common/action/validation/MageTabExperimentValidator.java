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
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.DataMatrix;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.DataMatrixParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedContentNavigator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractSdrfHandler;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ExperimentQueries;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Validates a mage-tab experiment, by making sure all files and archives in SDRF are present.  Note, SDRF must already
 * be set in Experiment object, or else this will fail.  MageTabExperimentChecker is where the SDRF is instantiated.
 *
 * @author Jessica Walton
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class MageTabExperimentValidator extends AbstractSdrfHandler<Experiment, Boolean> {

    /**
     * If a column name in the SDRF contains this string, that means it contains references to data matrix files
     */
    public static final String MATRIX_FILE_COLUMN_DESIGNATOR = "Data Matrix File";
    private Processor<DataMatrix, Boolean> matrixValidator;
    private DataMatrixParser matrixParser;
    private ManifestParser manifestParser;
    // flag that indicates whether or not the validator is running remotely
    private boolean isRemote;
    private ExperimentQueries experimentQueries;

    /**
     * Validates a CGCC experiment by checking SDRF references.  Checks that all archives referenced in Archive Name
     * columns are part of the experiment (either as uploaded or existing available archives).  Columns that refer to
     * data matrix files will also have these files validated against each other.
     * <p/>
     * If a non-CGCC experiment is passed in, will return true immediately.
     *
     * @param experiment the experiment to validate
     * @param context    the context for this QC call
     * @return true if the experiment is a valid CGCC experiment or if it is not a CGCC experiment, false if it is an
     *         invalid CGCC experiment
     * @throws ProcessorException if an error prevents validation from completing
     */
    protected Boolean doWork(final Experiment experiment, final QcContext context) throws ProcessorException {
        context.setExperiment(experiment);
        if (!context.experimentRequiresMageTab()) {
            return true;
        }

        Archive mageTabArchive = null;
        for (final Archive archive : experiment.getArchivesForStatus(Archive.STATUS_UPLOADED)) {
            if(Archive.TYPE_MAGE_TAB.equals(archive.getArchiveType())){
                mageTabArchive = archive;
            }
        }

        final TabDelimitedContent sdrf = experiment.getSdrf();
        if (sdrf == null) {
            throw new ProcessorException(new StringBuilder().append("SDRF for '").append(experiment.getName()).append("' is not set").toString());
        }

        context.setArchiveInProgress(mageTabArchive);
        final TabDelimitedContentNavigator sdrfNavigator = new TabDelimitedContentNavigator();
        sdrfNavigator.setTabDelimitedContent(sdrf);
        context.setSdrf(sdrf);
        boolean isValid = true;
        for (int i = 0; i < sdrfNavigator.getHeaders().size(); i++) {
            final String header = sdrfNavigator.getHeaders().get(i);
            if (FILE_COLUMN_NAMES.contains(header)) {
                final Map<String, Integer> commmentCols = getFileCommentColumns(sdrfNavigator, i);
                if (!AbstractSdrfValidator.checkFileCommentColumns(context, i, header, commmentCols)) {
                    isValid = false;
                } else {
                    // don't validate column if any comment columns are missing -- will fail
                    boolean columnvalid = validateColumn(experiment, i, commmentCols, sdrfNavigator, context, header);
                    isValid = columnvalid && isValid;
                }
            }
        }

        if (isValid) {
            warnForDroppedArchives(experiment, sdrfNavigator, context);
        }

        if (!isValid || context.getErrorsByArchiveName(mageTabArchive).size() > 0) {
            // set all uploaded archives to invalid because this validation failed, and that applies to all archives
            for (final Archive archive : experiment.getArchivesForStatus(Archive.STATUS_UPLOADED)) {
                archive.setDeployStatus(Archive.STATUS_INVALID);
            }
        }

        return isValid;
    }

    /*
    Validates a file column.
     */

    private boolean validateColumn(final Experiment experiment,
                                   final int fileCol, final Map<String, Integer> commentColumns,
                                   final TabDelimitedContentNavigator sdrfNavigator,
                                   final QcContext context, final String columnName)
            throws ProcessorException {
        boolean isValid = true;
        int protocolColumn = findProtocolColumnForFileColumn(sdrfNavigator, fileCol);
        // keep track of already-checked files so don't repeat if the same one is referenced all through
        // key is filename, val is archive name
        final Map<String, String> validatedFiles = new HashMap<String, String>();
        // save data matrix file objects for further checking, key is protocol + data type
        Map<String, List<DataMatrix>> matricesForProtocolType = new HashMap<String, List<DataMatrix>>();
        // for each file in column...
        for (int i = 1; i < sdrfNavigator.getNumRows(); i++) {
            final String fileName = sdrfNavigator.getValueByCoordinates(fileCol, i);
            final String archiveName = sdrfNavigator.getValueByCoordinates(commentColumns.get(COMMENT_ARCHIVE_NAME), i);
            // only check if file name and archive name aren't "blank"
            if (!fileName.equals("->") && !archiveName.equals("->")) {
                // has this file for this archive already been checked?
                if (!archiveName.equals(validatedFiles.get(fileName))) {
                    // look for the archive with the given name in the experiment
                    Archive archive = experiment.getArchive(archiveName);
                    if (archive == null) {
                        if (!context.isNoRemote()) {
                        	context.addError(MessageFormat.format(
                        			MessagePropertyType.EXPERIMENT_PROCESSING_ERROR, 
                        			experiment,
                        			new StringBuilder().append("Archive '").append(archiveName).
                        			append("' is referenced in the SDRF but is not one of the latest or uploaded archives").toString()));
                            isValid = false;
                        } else {
                            context.addWarning(new StringBuilder().append("Archive '").append(archiveName).
                                    append("' is listed in the SDRF but is not in the current submission group.  If this archive is not found in the DCC database at submission, archive will fail validation.").toString());
                        }
                    } else {
                        // check in the archive deploy directory for this file
                        final File file = new File(archive.getDeployDirectory(), fileName);
                        if (!file.exists() && !isRemote) {
                        	context.addError(MessageFormat.format(
                        			MessagePropertyType.EXPERIMENT_PROCESSING_ERROR, 
                        			experiment,
                        			new StringBuilder().append("File '").append(fileName).append("' is listed in the SDRF for archive '").append(archiveName).
                        			append("' but is not present in the archive").toString()));
                            isValid = false;
                        }else if (!file.exists() && isRemote){
                        	 context.addWarning(new StringBuilder().append("Archive '").append(archiveName).
                                     append(" The file '").append(fileName).append("' is listed in SDRF and is not in the current submission group. " +
                                     		" The remote validator is not able to verify this case, however DCC will attempt to locate this file and validate upon submission.").toString());
                        }

                        // is this archive being replaced?  if so, check for this file in the MANIFEST of the revised archive
                        Archive revisedArchive = experiment.getRevisedArchiveFor(archive);
                        if (revisedArchive != null) {
                            // this is an archive that is being replaced, so make sure this file was included in the updated archive,
                            // since it is referenced in the SDRF
                            if (!checkManifestForFile(revisedArchive, fileName)) {
                                isValid = false;
                                context.addError(MessageFormat.format(
                            			MessagePropertyType.EXPERIMENT_PROCESSING_ERROR, 
                            			experiment,
                            			new StringBuilder().append("File '").append(fileName).append("' is in the SDRF for archive '").
                            			append(archive.getRealName()).append("' but is not listed in the MANIFEST of the revised archive '").
                            			append(revisedArchive.getRealName()).append("'").toString()));
                            }
                        }
                    }                       
                    // if we know the archive for this file, make a matrix for validating
                    if (columnName.contains(MATRIX_FILE_COLUMN_DESIGNATOR) 
                    		&& archive != null
                    		 // skip if the file does not exist locally             
                    		&& (new File(archive.getDeployDirectory(), fileName)).exists()) {
                    	
                        // make data matrix and validate it
                        DataMatrix matrix = makeMatrixObject(fileName, archive, context);
                        if (matrix == null) {
                            context.addError(MessageFormat.format(
                                     MessagePropertyType.EXPERIMENT_PROCESSING_ERROR,
                                     experiment,
                                     new StringBuilder().append("File '").append(fileName).append(". Internal Error. Cannot create SDRF matrix object").toString()));

                            isValid = false;
                        } else {
                            context.setArchive(archive);
                            if (matrixValidator.execute(matrix, context)) {
                                // save all matrix files for this column, by data type, so can compare them to each other
                                // use the protocol, if any, in the key, because files from different protocols in the same column should
                                // not be compared against each other
                                final String dataType = sdrfNavigator.getValueByCoordinates(commentColumns.get(COMMENT_DATA_TYPE), i);
                                final String protocol = protocolColumn == -1 ? null : sdrfNavigator.getValueByCoordinates(protocolColumn, i);
                                final String hashKey = dataType + ":" + protocol;
                                List<DataMatrix> matrixes = matricesForProtocolType.get(hashKey);
                                if (matrixes == null) {
                                    matrixes = new ArrayList<DataMatrix>();
                                    matricesForProtocolType.put(hashKey, matrixes);
                                }
                                matrixes.add(matrix);
                            } else {
                                isValid = false;
                            }
                        }
                    }
                    validatedFiles.put(fileName, archiveName);
                }
            }
        }
        return isValid;
    }

    private boolean checkManifestForFile(final Archive revisedArchive, final String fileName) throws ProcessorException {
        final File archiveDirectory = new File(revisedArchive.getDeployDirectory());
        final File manifest = new File(archiveDirectory, ManifestValidator.MANIFEST_FILE);
        try {
            Map<String, String> manifestEntries = manifestParser.parseManifest(manifest);
            return manifestEntries.containsKey(fileName);
        } catch (IOException e) {
            throw new ProcessorException(e);
        } catch (ParseException e) {
            throw new ProcessorException(e);
        }
    }

    private DataMatrix makeMatrixObject(final String fileName, final Archive archive, final QcContext context) {
        DataMatrix matrix;
        try {
            matrix = matrixParser.parse(fileName, archive.getDeployDirectory());
        }
        catch (DataMatrixParser.DataMatrixParseError dataMatrixParseError) {
        	context.addError(MessageFormat.format(
        			MessagePropertyType.DATA_MATRIX_PROCESSING_ERROR, 
        			fileName,
        			dataMatrixParseError.getMessage()));
            return null;
        }
        catch (IOException e) {
        	context.addError(MessageFormat.format(
        			MessagePropertyType.DATA_MATRIX_PROCESSING_ERROR, 
        			fileName,
        			e.getMessage()));
            return null;
        }
        return matrix;
    }

    public String getName() {
        return "Mage-Tab experiment validation";
    }

    public Processor<DataMatrix, Boolean> getMatrixValidator() {
        return matrixValidator;
    }

    public void setMatrixValidator(final Processor<DataMatrix, Boolean> matrixValidator) {
        this.matrixValidator = matrixValidator;
    }

    public DataMatrixParser getMatrixParser() {
        return matrixParser;
    }

    public void setMatrixParser(final DataMatrixParser matrixParser) {
        this.matrixParser = matrixParser;
    }

    protected void warnForDroppedArchives(final Experiment experiment,
                                          final TabDelimitedContentNavigator sdrfNavigator,
                                          final QcContext qcContext) {


        if (experimentQueries != null) { // True when not running Soundcheck with -noremote

            final Map<Archive, List<FileInfo>> latestDataFiles = experimentQueries.getExperimentDataFiles(experiment.getName());

            // check Available archives that are still part of the experiment (i.e. that are not being replaced by the current upload)
            for (final Archive archive : experiment.getArchivesForStatus(Archive.STATUS_AVAILABLE)) {

                final List<FileInfo> archiveFilesFromDb = latestDataFiles.get(archive);
                final Set<String> archiveFilesFromSDRF = getFileNamesInArchiveFromSDRF(archive, sdrfNavigator);

                if (archiveFilesFromDb != null && archiveFilesFromDb.size() > 0) {

                    if (archiveFilesFromSDRF == null || archiveFilesFromSDRF.size() == 0) {
                        qcContext.addWarning("Currently available archive " + archive.getRealName() + " is not referenced in the SDRF. If this archive should no longer be available, please contact the DCC.");
                    }
                }

            }
        }
        
    }

    public void setManifestParser(final ManifestParser manifestParser) {
        this.manifestParser = manifestParser;
    }

    public ManifestParser getManifestParser() {
        return manifestParser;
    }

    public boolean isRemote() {
        return isRemote;
    }

    public void setRemote(boolean isRemote) {
		this.isRemote = isRemote;
	}

    public void setExperimentQueries(final ExperimentQueries experimentQueries) {
        this.experimentQueries = experimentQueries;
    }

    public ExperimentQueries getExperimentQueries() {
        return experimentQueries;
    }

}

