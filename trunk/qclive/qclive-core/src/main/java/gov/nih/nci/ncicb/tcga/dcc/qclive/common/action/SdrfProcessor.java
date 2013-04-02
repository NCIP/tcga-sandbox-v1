/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationCategory;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItemType;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ShippedBiospecimenQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.BeanException;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.common.service.annotations.AnnotationService;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BCRID;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedContentNavigator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessageFormat;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRIDProcessor;
import org.springframework.dao.DataAccessException;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Processor for SDRF files.  Adds biospecimen-to-file associations in the database.  Assumes SDRF has already been
 * successfully validated, and that files referred to in SDRF are already represented in database.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class SdrfProcessor extends AbstractSdrfHandler<Archive, Archive> {

    public static final String SDRF_BIOSPECIMEN_TYPE_SHIPPED_PORTION = "Shipped Portion";

    private static final String COMMENT_TCGA_BIOSPECIMEN_TYPE = "Comment [TCGA Biospecimen Type]";
    private BCRIDProcessor bcrIdProcessor;
    private BCRIDProcessor diseaseBcrIdProcessor;
    private FileInfoQueries fileInfoQueries;
    private ArchiveQueries archiveQueries;
    private ShippedBiospecimenQueries commonShippedBiospecimenQueries, diseaseShippedBiospecimenQueries;
    private Processor<Archive, Archive> droppedBarcodeFinder;
    private AnnotationService annotationService;
    private CommonBarcodeAndUUIDValidator barcodeAndUUIDValidator;
    public final static String PROTEIN_SDRF_PLATFORM_CODE = "MDA_RPPA_Core";
    private static final List<String> PROTEIN_FILE_COLUMN_NAMES = Arrays.asList(
            "Array Design File", "Annotations File",
            "Image File", "Array Data File",
            "Derived Array Data File", "Derived Array Data Matrix File");
    private UUIDService uuidService;


    public enum SDRFType {NON_PROTEIN_SDRF, PROTEIN_SDRF}

    public void setBarcodeAndUUIDValidator(final CommonBarcodeAndUUIDValidator barcodeAndUUIDValidator) {
        this.barcodeAndUUIDValidator = barcodeAndUUIDValidator;
    }

    public void setCommonShippedBiospecimenQueries(final ShippedBiospecimenQueries commonShippedBiospecimenQueries) {
        this.commonShippedBiospecimenQueries = commonShippedBiospecimenQueries;
    }

    public void setDiseaseShippedBiospecimenQueries(final ShippedBiospecimenQueries diseaseShippedBiospecimenQueries) {
        this.diseaseShippedBiospecimenQueries = diseaseShippedBiospecimenQueries;
    }


    protected Archive doWork(final Archive archive, final QcContext context) throws ProcessorException {
        // if this is not a mage-tab archive, return immediately
        if (!archive.getArchiveType().equals(Archive.TYPE_MAGE_TAB)) {
            return archive;
        }
        context.setArchive(archive);
        final TabDelimitedContent sdrf = archive.getSdrf();
        if (sdrf == null) {
            archive.setDeployStatus(Archive.STATUS_IN_REVIEW);
            throw new IllegalStateException("Archive object does not have SDRF variable set");
        }
        final TabDelimitedContentNavigator navigator = new TabDelimitedContentNavigator();
        navigator.setTabDelimitedContent(sdrf);
        try {
            SDRFType sdrfType = getSdrfType(archive);
            final List<String> extractNames = getColumnValues(getSampleIdColumnName(sdrfType), archive, navigator);
            List<String> biospecimenTypes = null;
            Integer biospecimenTypeColumn = navigator.getHeaderIDByName(COMMENT_TCGA_BIOSPECIMEN_TYPE);
            if (biospecimenTypeColumn != null && biospecimenTypeColumn >= 0) {
                biospecimenTypes = getColumnValues(COMMENT_TCGA_BIOSPECIMEN_TYPE, archive, navigator);
            }
            for (final String fileColumnName : getFileColumnNames(sdrfType)) {
                final List<Integer> fileColumns = navigator.getHeaderIdsForName(fileColumnName);
                for (final Integer fileColumn : fileColumns) {
                    if (fileColumn != -1) {
                        processColumn(archive, navigator, navigator.getColumnValues(fileColumn), extractNames, biospecimenTypes,
                                getFileCommentColumns(navigator, fileColumn), fileColumnName, context);
                    }
                }
            }

            // need to do this here, after all the file-to-barcode associations have been added from the SDRF
            for (final Archive experimentArchive : context.getExperiment().getArchives()) {
                if (!experimentArchive.getArchiveType().equals(Archive.TYPE_MAGE_TAB)) {
                    droppedBarcodeFinder.execute(experimentArchive, context);
                }
            }

            return archive;
        } catch (DataAccessException e) {
            context.getArchive().setDeployStatus(Archive.STATUS_IN_REVIEW);
            context.addError(MessageFormat.format(
                    MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
                    archive,
                    e.getMessage()));
            throw new Processor.ProcessorException(e.getMessage(), e);
        }
    }

    private List<String> getFileColumnNames(final SDRFType sdrfType) {
        return sdrfType.equals(SdrfProcessor.SDRFType.PROTEIN_SDRF) ? PROTEIN_FILE_COLUMN_NAMES : FILE_COLUMN_NAMES;
    }

    /**
     * Determine the type of a given SDRF archive based on archive platform.
     *
     * @param archive the SDRF archive name
     * @return SDRFType the SDRF archive type; PROTEIN_SDRF if platform == "MDA_RPPA_Core", NON_PROTEIN_SDRF o.w.
     */
    public SDRFType getSdrfType(final Archive archive) throws ProcessorException {

        if (archive.getPlatform() == null) {
            throw new ProcessorException("Archive has null platform");
        }
        return (archive.getPlatform().equalsIgnoreCase(PROTEIN_SDRF_PLATFORM_CODE) ? SDRFType.PROTEIN_SDRF : SDRFType.NON_PROTEIN_SDRF);
    }

    /**
     * Determine the column holding biospecimen id for given SDRF type.
     *
     * @param sdrfType SDRF type
     * @return Column name  column holding biospecimen id for given SDRF type
     */
    public String getSampleIdColumnName(final SDRFType sdrfType) throws ProcessorException {

        switch (sdrfType) {
            case NON_PROTEIN_SDRF:
                return "Extract Name";
            case PROTEIN_SDRF:
                return "Sample Name";
            default:
                throw new ProcessorException("Invalid SDRF type");
        }
    }

    private List<String> getColumnValues(final String columnName, final Archive archive,
                                         final TabDelimitedContentNavigator navigator) throws ProcessorException {
        final int column = navigator.getHeaderIDByName(columnName);
        if (column == -1) {
            archive.setDeployStatus(Archive.STATUS_IN_REVIEW);
            throw new ProcessorException(new StringBuilder().append("SDRF does not contain required column '").append(columnName).append("'").toString());
        }
        return navigator.getColumnValues(column);
    }

    /**
     * This adds the association between barcodes and files according to what is shown in the SDRF.
     * For each barcode in the extractNames list,
     *
     * @param mageTabArchive   the mage-tab archive the SDRF was found in
     * @param navigator        the SDRF navigator
     * @param fileNames        the list of file names from the column
     * @param extractNames     a list of extract names (in order from the SDRF column)
     * @param biospecimenTypes a list of values for the Biospecimen Type column - may be null if no such column
     * @param commentColumns   map of comment column locations for this file column
     * @param columnName       the name of the file column
     * @param context          the qc context
     * @throws gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException
     *          if there is an unrecoverable error
     */
    protected void processColumn(final Archive mageTabArchive,
                                 final TabDelimitedContentNavigator navigator,
                                 final List<String> fileNames,
                                 final List<String> extractNames,
                                 final List<String> biospecimenTypes,
                                 final Map<String, Integer> commentColumns,
                                 final String columnName,
                                 final QcContext context) throws ProcessorException {
        final Map<String, Long> archiveIdMap = new HashMap<String, Long>();
        for (int i = 0; i < fileNames.size(); i++) {
            final String fileName = fileNames.get(i);
            // if file name for this row/column is "blank" then skip
            if (!fileName.equals("->")) {
                String archiveName = null;
                if (commentColumns.get(COMMENT_ARCHIVE_NAME) != null) {
                    archiveName = navigator.getValueByCoordinates(commentColumns.get(COMMENT_ARCHIVE_NAME), i + 1);
                } else {
                    // if no archive name column, that means file is in mage-tab archive itself
                    archiveName = mageTabArchive.getRealName();
                }
                String includeForAnalysis = null;
                if (commentColumns.get(COMMENT_INCLUDE_FOR_ANALYSIS) != null) {
                    includeForAnalysis = navigator.getValueByCoordinates(commentColumns.get(COMMENT_INCLUDE_FOR_ANALYSIS), i + 1);
                }
                String dataLevel = "0"; // if no level specified, default is 0
                if (commentColumns.get(COMMENT_DATA_LEVEL) != null) {
                    dataLevel = navigator.getValueByCoordinates(commentColumns.get(COMMENT_DATA_LEVEL), i + 1);
                }
                String biospecimenType = null;
                if (biospecimenTypes != null) {
                    biospecimenType = biospecimenTypes.get(i);
                }
                
                final String extractName = extractNames.get(i);
                if (includeForAnalysis == null || includeForAnalysis.equalsIgnoreCase("yes")) {
                    // first check if the barcode is really a barcode -- it might be a control sample not from TCGA
                    if (isTcgaSample(extractName, biospecimenType, this.getSdrfType(mageTabArchive))) {
                        // look up the archive ID
                        Long archiveId = archiveIdMap.get(archiveName);
                        if (archiveId == null) {
                            archiveId = archiveQueries.getArchiveIdByName(archiveName);
                            archiveIdMap.put(archiveName, archiveId);
                        }
                        // if still null, there is a problem
                        if (archiveId == null) {
                            context.getArchive().setDeployStatus(Archive.STATUS_IN_REVIEW);
                            context.addError(MessageFormat.format(
                                    MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
                                    archiveName,
                                    "Archive is referred to in the SDRF but was not found in the database"));
                        } else {
                            // if archive was found, get file id
                            final Long fileId = fileInfoQueries.getFileId(fileName, archiveId);
                            if (fileId == null) {
                                // if something is not in the manifest but is in the sdrf, then
                                // if validator fails or isn't called, this could happen, and is an error
                                context.getArchive().setDeployStatus(Archive.STATUS_IN_REVIEW);
                                context.addError(MessageFormat.format(
                                        MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
                                        archiveName,
                                        new StringBuilder().append("File '").append(fileName).append("' is referred to in the SDRF but was not found in the database").toString()));
                            } else {
                                if (biospecimenType != null && biospecimenType.equalsIgnoreCase(SDRF_BIOSPECIMEN_TYPE_SHIPPED_PORTION)) {
                                    addShippedBiospecimenToFileAssociation(archiveName, extractName, fileId, context);
                                } else {
                                    addBiospecimenToFileAssociations(archiveName, columnName, extractName, archiveId, fileId, context);
                                }

                            }
                        }
                    }
                } else {
                    // include for analysis is no -> this means "do not use" flag is true
                    String barcode = extractName;
                    try {
                        if (barcodeAndUUIDValidator.validateUUIDFormat(extractName)) {
                            barcode = uuidService.getLatestBarcodeForUUID(extractName);
                        }

                        final DccAnnotation newAnnotation = annotationService.addAnnotation(context.getArchive().getTheTumor().getTumorId(),
                                DccAnnotationItemType.ALIQUOT_TYPE_ID, barcode,
                                DccAnnotationCategory.DCC_ANNOTATION_DNU_ID,
                                "SDRF in " + context.getArchive().getRealName() + " flagged aliquot to be excluded for analysis based on file '" + fileName + "'.",
                                "DCC");
                        // since this is auto-added, set to curated immediately
                        annotationService.curate(newAnnotation);

                    } catch (AnnotationQueries.AnnotationQueriesException e) {
                        // if the exception is because the annotation already exists, just ignore it and no need to warn
                        if (!e.getMessage().startsWith("The following annotation is not unique: ")) {
                            // we don't want this to interrupt processing, just note it and keep going
                            context.addExceptionToLog(e);
                            context.addWarning("Failed to add 'Do Not Use' annotation for '" + extractName + "'");
                        }
                    } catch (BeanException be) {
                        // we don't want this to interrupt processing, just note it and keep going
                        context.addExceptionToLog(be);
                        context.addWarning("Failed to add 'Do Not Use' annotation for '" + extractName + "'");
                    }
                }
            }
        }
    }

    /**
     * Adds shipped-biospecimen-to-file relationship to common and disease databases.
     *
     * @param archiveName the archive we are processing
     * @param uuid        the uuid representing the shipped biospecimen
     * @param fileId      the ID of the file that contains data for the biospecimen
     * @param context     the QcContext
     * @throws ProcessorException if the UUID doesn't represent a shipped biospecimen
     */
    private void addShippedBiospecimenToFileAssociation(String archiveName, String uuid, Long fileId, QcContext context) throws ProcessorException {
        Long biospecimenId = commonShippedBiospecimenQueries.getShippedBiospecimenIdForUUID(uuid);
        if (biospecimenId == null) {
            context.getArchive().setDeployStatus(Archive.STATUS_IN_REVIEW);
            throw new ProcessorException(MessageFormat.format(
                    MessagePropertyType.ARCHIVE_PROCESSING_ERROR, archiveName,
                    new StringBuilder().append("UUID '").append(uuid).append("' does not represent a shipped biospecimen").toString()));
        }
        commonShippedBiospecimenQueries.addFileRelationship(biospecimenId, fileId);
        diseaseShippedBiospecimenQueries.addFileRelationship(biospecimenId, fileId);
    }

    /**
     * Adds biospecimen to file relationship for aliquots.
     *
     * @param archiveName the archive we are processing
     * @param columnName  the header for the column the file was found in the SDRF
     * @param extractName the extract name value -- expected for now to be an aliquot barcode
     * @param archiveId   this doesn't seem to be used!
     * @param fileId      the ID of the file
     * @param context     the QcContext
     * @throws ProcessorException if the extractName is not an aliquot barcode
     */
    private void addBiospecimenToFileAssociations(final String archiveName, final String columnName, final String extractName,
                                                  final Long archiveId, final Long fileId, final QcContext context) throws ProcessorException {

        try {
            // Check the extract name is an UUID or barcode
            if (barcodeAndUUIDValidator.validateUUIDFormat(extractName)) {
                addBiospecimenToFileAssociation(archiveName, extractName, fileId, columnName, context);

            } else {

                // the entries in the array : 0=bcrId, 1=bcrToFileId
                // an array needs to be passed since the bcrId and bcrToFileId for the new database row is required for
                // saving the data in disease schema
                int[] bcrAndBcrFileId = {-1, -1};
                BCRID bcrID = bcrIdProcessor.parseAliquotBarcode(extractName);
                bcrID.setFullID(extractName);
                bcrIdProcessor.addFileAssociation(fileId, bcrID, columnName, archiveId, false, bcrAndBcrFileId, context.getArchive().getTheTumor());
                diseaseBcrIdProcessor.addFileAssociation(fileId, bcrID, columnName, archiveId, true, bcrAndBcrFileId, context.getArchive().getTheTumor());
            }
        } catch (ParseException e) {
            context.getArchive().setDeployStatus(Archive.STATUS_IN_REVIEW);
            context.addError(MessageFormat.format(
                    MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
                    archiveName,
                    e.getMessage()));
            throw new ProcessorException(e.getMessage(), e);
        } catch (UUIDException e) {
            context.getArchive().setDeployStatus(Archive.STATUS_IN_REVIEW);
            context.addError(MessageFormat.format(
                    MessagePropertyType.ARCHIVE_PROCESSING_ERROR,
                    archiveName,
                    e.getMessage()));
            throw new ProcessorException(e.getMessage(), e);
        }
    }

    private void addBiospecimenToFileAssociation(final String archiveName, final String uuid,
                                                 final Long fileId, final String columnName,
                                                 final QcContext context) throws ProcessorException {
        final Long biospecimenId = bcrIdProcessor.getBiospecimenIdForUUID((uuid == null) ? null : uuid.toLowerCase());
        if (biospecimenId == null) {
            context.getArchive().setDeployStatus(Archive.STATUS_IN_REVIEW);
            throw new ProcessorException(MessageFormat.format(
                    MessagePropertyType.ARCHIVE_PROCESSING_ERROR, archiveName,
                    new StringBuilder().append("UUID '").append(uuid).append("' wasn't loaded in the database").toString()));
        }
        Integer biospecimenFileId = bcrIdProcessor.addFileAssociation(fileId, biospecimenId.intValue(), columnName, false, -1);
        diseaseBcrIdProcessor.addFileAssociation(fileId, biospecimenId.intValue(), columnName, true, biospecimenFileId);
    }

    /**
     * Determines if the sample/extract is a TCGA biospecimen.  If not that means it is a control of some kind.
     *
     * @param extractName     the sample/extract name from the SDRF (may be a UUID or a barcode or a control name)
     * @param biospecimenType the value for the row's Comment [TCGA Biospecimen Type] column -- may be null if that column is not present
     * @return true if this represents a TCGA biospecimen, false if a control
     */
    protected boolean isTcgaSample(final String extractName, final String biospecimenType, final SDRFType sdrfType) {
        // if it is a UUID and the biospecimen type is not null, or if extractName starts with TCGA (for now)
        return (sdrfType == SDRFType.PROTEIN_SDRF ?
                biospecimenType != null && !biospecimenType.equals("->") && barcodeAndUUIDValidator.validateUUIDFormat(extractName) :
                extractName.startsWith("TCGA") || (barcodeAndUUIDValidator.validateUUIDFormat(extractName)));
    }


    public String getName() {
        return "SDRF processor";
    }

    public void setBcrIdProcessor(final BCRIDProcessor bcrIdProcessor) {
        this.bcrIdProcessor = bcrIdProcessor;
    }

    public void setDiseaseBcrIdProcessor(final BCRIDProcessor diseaseBcrIdProcessor) {
        this.diseaseBcrIdProcessor = diseaseBcrIdProcessor;
    }

    public void setFileInfoQueries(final FileInfoQueries fileInfoQueries) {
        this.fileInfoQueries = fileInfoQueries;
    }

    public void setArchiveQueries(final ArchiveQueries archiveQueries) {
        this.archiveQueries = archiveQueries;
    }

    public void setDroppedBarcodeFinder(final Processor<Archive, Archive> droppedBarcodeFinder) {
        this.droppedBarcodeFinder = droppedBarcodeFinder;
    }

    public void setAnnotationService(final AnnotationService annotationService) {
        this.annotationService = annotationService;
    }

    public void setUuidService(final UUIDService uuidService) {
        this.uuidService = uuidService;
    }

}
