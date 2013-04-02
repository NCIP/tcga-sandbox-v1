/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.ClinicalMetaQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.SQLProcessingCleaner;
import gov.nih.nci.ncicb.tcga.dcc.common.generation.FileGenerator;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileClinical;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetClinical;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.TumorNormalClassifierI;
import org.apache.log4j.Level;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Gathers information about clinical 1 files from the Oracle Portal database.
 * In the process of creating archives, this class will write temporary clinical output files
 * to disk using information from Oracle.  The File Packager will then incorporate those
 * files into the new archive.
 */
public class DAMQueriesClinicalBiotab extends DAMBaseQueriesProcessor implements DataAccessMatrixQueries {

    //Location where temp files will be written out - set by spring xml
    private String tempfileDirectory;
    private final ProcessLogger logger = new ProcessLogger();
    private ClinicalMetaQueries clinicalMetaQueries;
    private String controlDiseaseAbbreviation;
    private DAMUtilsI damUtils;
    private FileInfoQueries fileInfoQueries;
    private FileGenerator fileGenerator;
    private CommonBarcodeAndUUIDValidator commonBarcodeAndUUIDValidator;

    public void setTempfileDirectory(final String tempfileDirectory) {
        this.tempfileDirectory = tempfileDirectory;
        if (this.tempfileDirectory != null && this.tempfileDirectory.endsWith("/")) {
            this.tempfileDirectory = this.tempfileDirectory.substring(0, this.tempfileDirectory.length() - 1);
        }
    }

    /**
     * Returns list of all datasets for a disease type.
     * Each dataset will become a cell in the DAM.
     *
     * @param diseaseType the disease type
     * @return list of all datasets for disease type
     */
    public List<DataSet> getDataSetsForDiseaseType(
            final String diseaseType) throws DataAccessMatrixQueries.DAMQueriesException {
        final List<DataSet> dataSets = new ArrayList<DataSet>();

        ResultSet rs = null;
        final String sql = "select distinct substr(sample_barcode, 0, 15) as samplename, sb.batch_id, ai.date_added, ai.archive_id \n" +
                "from patient_archive pa, sample s, patient p, portion, analyte, aliquot, archive_info ai, shipped_biospecimen sb\n" +
                "where pa.patient_id=p.patient_id " +
                "and pa.archive_id=ai.archive_id " +
                "and ai.is_latest=1 " +
                "and  p.patient_id = s.patient_id " +
                "and s.sample_id=portion.sample_id " +
                "and portion.portion_id=analyte.portion_id " +
                "and analyte.analyte_id=aliquot.analyte_id " +
                "and aliquot.uuid = sb.uuid " +
                "and sb.is_viewable=1 " +
                "order by samplename";
        final Connection dbh = getConnection();
        PreparedStatement sth = null;
        try {
            //noinspection JDBCResourceOpenedButNotSafelyClosed
            sth = dbh.prepareStatement(sql);
            logger.logToLogger(Level.DEBUG, "executing query for getAllDataSetsForDisease (clinical) " + System.currentTimeMillis());
            rs = sth.executeQuery();
            logger.logToLogger(Level.DEBUG, "finished executing query for getAllDataSetsForDisease (clinical) " + System.currentTimeMillis());
            while (rs.next()) {
                // loop through results once, assign to two datasets, all and protected.
                DataSet dataSet = new DataSetClinical();
                dataSet.setArchiveId(rs.getInt("archive_id"));
                dataSet.setDiseaseType(diseaseType);

                //special "platformtype" id for clinical - for use by UI, not related to any ID in database
                dataSet.setPlatformTypeId(DataAccessMatrixQueries.CLINICAL_PLATFORMTYPE);
                //always sort last
                dataSet.setPlatformTypeSortOrder(99);

                //special "center" for clinical -again, not directly related to db
                dataSet.setCenterId(DataAccessMatrixQueries.CLINICAL_BIOTAB_CENTER);
                //empty string for level
                dataSet.setLevel(DataAccessMatrixQueries.LEVEL_CLINICAL);
                dataSet.setProtected(false);

                Integer batchId = rs.getInt(2);
                if (rs.wasNull()) {
                    batchId = null;
                }
                String batch = "Unclassified";
                if (batchId != null) {
                    batch = "Batch " + batchId;
                }
                dataSet.setBatch(batch);
                dataSet.setSample(rs.getString(1));
                dataSet.setAvailability("A");
                Date date = rs.getDate("date_added");
                dataSet.setDateAdded(date);
                // add to the appropriate list
                dataSets.add(dataSet);
            }
        }
        catch (SQLException e) {
            logger.logToLogger(Level.ERROR, ProcessLogger.stackTracePrinter(e));
            throw new DataAccessMatrixQueries.DAMQueriesException(e);
        }
        finally {
            SQLProcessingCleaner.cleanUpResultSet(rs);
            SQLProcessingCleaner.cleanUpStatement(sth);
            SQLProcessingCleaner.cleanUpConnection(dbh);
        }
        return dataSets;
    }

    @Override
    public List<DataSet> getDataSetsForControls(final List<String> diseaseTypes) throws DAMQueriesException {
        if (controlDiseaseAbbreviation == null) {
            return null;
        } else {
            DiseaseContextHolder.setDisease(controlDiseaseAbbreviation);
            return getDataSetsForDiseaseType(controlDiseaseAbbreviation);
        }
    }

    @Override
    public List<DataFile> getFileInfoForSelectedDataSets(
            final List<DataSet> selectedDataSets, final boolean consolidateFiles) throws DataAccessMatrixQueries.DAMQueriesException {
        final List<DataFile> ret = new ArrayList<DataFile>();
        final Map<String, List<DataSet>> dataSetsGroupedByDisease = damUtils.groupDataSetsByDisease(selectedDataSets);
        for(final String diseaseType : dataSetsGroupedByDisease.keySet()) {
            final List<DataSet> singleDiseaseDataSets = dataSetsGroupedByDisease.get(diseaseType);
            if(singleDiseaseDataSets != null && singleDiseaseDataSets.size() > 0) {
                DiseaseContextHolder.setDisease(diseaseType);
                ret.addAll(getFileInfoForSelectedDataSetsSingleDisease(diseaseType,singleDiseaseDataSets, consolidateFiles));
            }
        }
        return ret;
    }

    /**
     * Given a list of selected datasets (cells), returns a list of data files that are needed to get
     * the requested data.
     *
     * @param selectedDataSets the datasets the user selected
     * @param consolidateFiles whether data for selected data sets should be consolidated -- IGNORED IN THIS IMPLEMENTATION
     * @return the datafiles representing the datasets
     */
    private List<DataFile> getFileInfoForSelectedDataSetsSingleDisease(final String diseaseAbbreviation,
                                                                       final List<DataSet> selectedDataSets,
                                                                       final boolean consolidateFiles) throws DataAccessMatrixQueries.DAMQueriesException {
        final List<DataFile> dataFiles = new ArrayList<DataFile>();
        String diseaseType = selectedDataSets.size() > 0 ? selectedDataSets.get( 0 ).getDiseaseType() : null;

        // 1. get sample lists from data sets
        final Set<String> uniquePatients = new HashSet<String>();
        final Set<String> samples = new HashSet<String>();
        Date dateAdded = null;
        for (final DataSet selectedDataSet : selectedDataSets) {
            if (selectedDataSet instanceof DataSetClinical &&
                selectedDataSet.getPlatformTypeId().equals(DataAccessMatrixQueries.CLINICAL_PLATFORMTYPE) &&
                        selectedDataSet.getCenterId().equals(DataAccessMatrixQueries.CLINICAL_BIOTAB_CENTER)) {
                // biotab files are for patients. Get the patient barcode from samples barcode
                uniquePatients.add(getCommonBarcodeAndUUIDValidator().getPatientBarcode(selectedDataSet.getSample()));
                samples.add(selectedDataSet.getSample());

                // get the LATEST date for all these data sets
                if (dateAdded == null || selectedDataSet.getDateAdded().after(dateAdded)) {
                    dateAdded = selectedDataSet.getDateAdded();
                }
            }
        }
        if(uniquePatients.size() > 0){
            try{
                final List<String> patients = new ArrayList<String>(uniquePatients);
                final List<String> bcrXMLFilesLocation = fileInfoQueries.getBCRXMLFileLocations(patients);
                final List<String> biotabFilesLocation = fileGenerator.generate(diseaseAbbreviation,bcrXMLFilesLocation);

                for(String biotabFileLocation: biotabFilesLocation){
                    final File biotabFile = new File(biotabFileLocation);
                    DataFileClinical dataFile = new DataFileClinical();
                    dataFile.setSamples(samples);
                    dataFile.setPatientsFromSamples(samples);
                    dataFile.setPlatformTypeId(DataAccessMatrixQueries.CLINICAL_PLATFORMTYPE);
                    dataFile.setCenterId(DataAccessMatrixQueries.CLINICAL_BIOTAB_CENTER);

                    dataFile.setSize( biotabFile.length());
                    dataFile.setFileName(biotabFile.getName());
                    dataFile.setDisplaySample( LEVEL23_SAMPLE_PHRASE );
                    dataFile.setDiseaseType(diseaseType);
                    dataFile.setDateAdded(dateAdded);
                    dataFile.setPath(biotabFileLocation);
                    dataFiles.add(dataFile);

                }
            }catch(Exception e){
                throw new DataAccessMatrixQueries.DAMQueriesException(e.getMessage());
            }
        }
        return dataFiles;
    }


    /**
     * Given a list of fileinfo objects, adds path information to those objects.
     * Returns nothing.
     * For clinical, generates the temp files from data fetched from the database.
     *
     * @param selectedFiles the files that were selected
     */
    public void addPathsToSelectedFiles( final List<DataFile> selectedFiles ) throws DataAccessMatrixQueries.DAMQueriesException {
        // DO nothing the paths are already set.
    }

    private boolean checkForFileBarcodeColumn(final ClinicalMetaQueries.ClinicalFile clinicalFile) {
        for (final ClinicalMetaQueries.ClinicalFileColumn column : clinicalFile.columns) {
            if (clinicalFile.byPatient && column.columnName.equals("bcr_patient_barcode")) {
                return true;
            } else if (!clinicalFile.byPatient && column.columnName.equals("bcr_sample_barcode")) {
                return true;
            }
        }
        return false;
    }


    public void setClinicalMetaQueries( final ClinicalMetaQueries clinicalMetaQueries ) {
        this.clinicalMetaQueries = clinicalMetaQueries;
    }

    public void setControlDiseaseAbbreviation(final String controlDiseaseAbbreviation) {
        this.controlDiseaseAbbreviation = controlDiseaseAbbreviation;
    }

    public DAMUtilsI getDamUtils() {
        return damUtils;
    }

    public void setDamUtils(DAMUtilsI damUtils) {
        this.damUtils = damUtils;
    }

    public FileInfoQueries getFileInfoQueries() {
        return fileInfoQueries;
    }

    public void setFileInfoQueries(FileInfoQueries fileInfoQueries) {
        this.fileInfoQueries = fileInfoQueries;
    }

    public FileGenerator getFileGenerator() {
        return fileGenerator;
    }

    public void setFileGenerator(FileGenerator fileGenerator) {
        this.fileGenerator = fileGenerator;
    }

    public CommonBarcodeAndUUIDValidator getCommonBarcodeAndUUIDValidator() {
        return commonBarcodeAndUUIDValidator;
    }

    public void setCommonBarcodeAndUUIDValidator(CommonBarcodeAndUUIDValidator commonBarcodeAndUUIDValidator) {
        this.commonBarcodeAndUUIDValidator = commonBarcodeAndUUIDValidator;
    }
}
