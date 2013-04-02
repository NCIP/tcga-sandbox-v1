/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.SQLProcessingCleaner;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.util.BeanToTextExporter;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileMetadata;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetClinical;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.ErrorInfo;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DAMQueriesMetadata extends DAMBaseQueriesProcessor implements DataAccessMatrixQueries {

    private AnnotationQueries annotationQueries;
    private DAMUtilsI damUtils;
    private String tempFileDirectory;
    public final static String SAMPLE_ANNOTATION_FILENAME = "sampleAnnotation.txt";

    public void setAnnotationQueries(AnnotationQueries annotationQueries) {
        this.annotationQueries = annotationQueries;
    }

    public void setTempFileDirectory(String tempFileDirectory) {
        this.tempFileDirectory = tempFileDirectory;
    }

    private static final String FILE_INFO_SQL =
            "select distinct f.file_id, f.file_name, f.file_size " +
                    "from archive_info a, file_info f, file_to_archive f2a " +
                    "where a.archive_id= ? " +
                    "and a.archive_id=f2a.archive_id " +
                    "and f2a.file_id=f.file_id " +
                    "and f.file_name like ? ";

    // look for files in the latest mage-tab archive with the same center, platform, and disease as the data archive
    private static final String NEW_FORMAT_FILE_INFO_SQL =
            "select distinct f.file_id, f.file_name, f.file_size " +
                    "from archive_info data_a, archive_info mage_a, file_info f, file_to_archive f2a, archive_type at " +
                    "where data_a.archive_id=? and " +
                    "data_a.center_id=mage_a.center_id and data_a.platform_id=mage_a.platform_id and " +
                    "data_a.disease_id=mage_a.disease_id and mage_a.is_latest=1 and " +
                    "mage_a.archive_id=f2a.archive_id and f2a.file_id=f.file_id and f.file_name like ? and " +
                    "mage_a.archive_type_id=at.archive_type_id and at.archive_type='mage-tab'";

    private static final String FILE_PATH_QUERY = "select file_location_url from file_to_archive f2a, archive_info ai" +
            " where f2a.file_id = ? and f2a.archive_id = ai.archive_id and ai.is_latest=1";

    //don't show any cells in matrix for metadata files
    public List<DataSet> getDataSetsForDiseaseType(
            final String diseaseType) throws DataAccessMatrixQueries.DAMQueriesException {
        return new ArrayList<DataSet>();
    }

    @Override
    public List<DataSet> getDataSetsForControls(final List<String> diseaseTypes) throws DAMQueriesException {
        // don't show meta-data in matrix
        return new ArrayList<DataSet>();
    }

    /**
     * Build the needed data file objects for the selected data sets.
     * This method groups the datasets by disease and then calls
     * getFileInfoForSelectedDataSetsSingleDisease to retrieve the
     * data files associated with a single disease
     *
     * @param selectedDataSets the data sets the user selected
     * @param consolidateFiles whether data for selected data sets should be consolidated -- IGNORED IN THIS IMPLEMENTATION
     * @return data files needed to represent the given data sets
     * @throws DataAccessMatrixQueries.DAMQueriesException
     *
     */
    @Override
    public List<DataFile> getFileInfoForSelectedDataSets(
            final List<DataSet> selectedDataSets, final boolean consolidateFiles) throws DataAccessMatrixQueries.DAMQueriesException {
        final List<DataFile> ret = new ArrayList<DataFile>();
        final Map<String, List<DataSet>> dataSetsGroupedByDisease = damUtils.groupDataSetsByDisease(selectedDataSets);
        for(final String diseaseType : dataSetsGroupedByDisease.keySet()) {
            final List<DataSet> singleDiseaseDataSets = dataSetsGroupedByDisease.get(diseaseType);
            if(singleDiseaseDataSets != null && singleDiseaseDataSets.size() > 0) {
                DiseaseContextHolder.setDisease(diseaseType);
                ret.addAll(getFileInfoForSelectedDataSetsSingleDisease(singleDiseaseDataSets, consolidateFiles));
            }
        }
        return ret;
    }

    /**
     * Build the needed data file objects for the selected data sets.
     * This method retrieves the datafiles for datasets of a single disease
     * @param selectedDataSets the data sets the user selected
     * @param consolidateFiles whether data for selected data sets should be consolidated -- IGNORED IN THIS IMPLEMENTATION
     * @return data files needed to represent the given data sets
     * @throws DataAccessMatrixQueries.DAMQueriesException
     *
     */
    private List<DataFile> getFileInfoForSelectedDataSetsSingleDisease(
            final List<DataSet> selectedDataSets, final boolean consolidateFiles) throws DataAccessMatrixQueries.DAMQueriesException {
        final List<DataFile> ret = new LinkedList<DataFile>();
        final Connection conn = getConnection();
        PreparedStatement stmt = null;
        PreparedStatement newFormatStmt = null;
        try {
            stmt = conn.prepareStatement(FILE_INFO_SQL);
            newFormatStmt = conn.prepareStatement(NEW_FORMAT_FILE_INFO_SQL);

            // map of archive IDs we already have metafiles for
            final Map<Integer, Integer> archives = new HashMap<Integer, Integer>();
            // save metadata file ids so don't add the same one twice
            final Map<Integer, Integer> metaDataFileIds = new HashMap<Integer, Integer>();

            for (final DataSet ds : selectedDataSets) {
                if (ds instanceof DataSetClinical) {
                    continue;
                }
                if (archives.get(ds.getArchiveId()) == null) {
                    // we don't care what level. For any ds, we will find the appropriate sdrf and idf files.
                    // Check both classic and new format, in case the dataset contains data from both types.
                    makeDataFilesForCenterPlatformBatch(stmt, ds, "%.sdrf%", ret, metaDataFileIds);
                    makeDataFilesForCenterPlatformBatch(newFormatStmt, ds, "%.sdrf%", ret, metaDataFileIds);
                    makeDataFilesForCenterPlatformBatch(stmt, ds, "%.idf%", ret, metaDataFileIds);
                    makeDataFilesForCenterPlatformBatch(newFormatStmt, ds, "%.idf%", ret, metaDataFileIds);
                    //to make sure we don't add it again
                    archives.put(ds.getArchiveId(), ds.getArchiveId());
                }
            }
        } catch (SQLException e) {
            new ErrorInfo(e); //logs self
            throw new DataAccessMatrixQueries.DAMQueriesException(e);
        } finally {
            SQLProcessingCleaner.cleanUpStatement(stmt);
            SQLProcessingCleaner.cleanUpStatement(newFormatStmt);
            SQLProcessingCleaner.cleanUpConnection(conn);
        }
        return ret;
    }

    protected DataFile makeSampleAnnotation(final List<DataSet> selectedDataSets) {
        final DataFileMetadata dataFile = new DataFileMetadata();
        final List<String> selectedSamples = new LinkedList<String>();
        for (final DataSet ds : selectedDataSets) {
            selectedSamples.add(ds.getSample());
        }
        dataFile.setPlatformTypeId("METADATA");
        dataFile.setCenterId("SAMPLE ANNOTATION");
        dataFile.setDisplaySample(LEVEL23_SAMPLE_PHRASE);
        dataFile.setProtected(false);
        //size estimation: (9 tabs + 100ish char ) * number of lines
        dataFile.setSize((9 + 100) * annotationQueries.getAllAnnotationsCountForSamples(selectedSamples));
        dataFile.setFileName(SAMPLE_ANNOTATION_FILENAME);
        dataFile.setSamples(selectedSamples);
        dataFile.setPatientsFromSamples(selectedSamples);
        return dataFile;
    }

    protected String addSampleAnnotation(final List<String> samples) throws IOException {
        final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        final String path = getTmpFilePath();
        final Writer out = new FileWriter(path);
        final List<DccAnnotation> allAnnotationList = annotationQueries.getAllAnnotationsForSamples(samples);
        try {
            BeanToTextExporter.beanListToText("tab", out, getAnnotationSampleHeaders(), allAnnotationList, dateFormat);
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return path;
    }

    protected String getTmpFilePath() throws IOException {
        if (tempFileDirectory == null) {
            throw new IOException("No tempFileDirectory specified");
        }
        if (!(new File(tempFileDirectory)).exists()) {
            throw new IOException("Directory does not exist " + tempFileDirectory);
        }
        return tempFileDirectory + File.separator + java.util.UUID.randomUUID() + ".annotationSample.txt";
    }

    private Map<String, String> getAnnotationSampleHeaders() {
        final Map<String, String> columns = new LinkedHashMap<String, String>();
        columns.put("id", "ID");
        columns.put("diseases", "Disease");
        columns.put("itemTypes", "Item Type");
        columns.put("items", "Item Barcode");
        columns.put("annotationCategory.annotationClassification", "Annotation Classification");
        columns.put("annotationCategory", "Annotation Category");
        columns.put("notes", "Annotation Notes");
        columns.put("dateCreated", "Date Created");
        columns.put("createdBy", "Created By");
        columns.put("status", "Status");
        return columns;
    }

    private void makeDataFilesForCenterPlatformBatch(final PreparedStatement stmt, final DataSet ds,
                                                     final String ext, final List<DataFile> ret,
                                                     final Map<Integer, Integer> metaDataFileIds) throws SQLException {
        stmt.setInt(1, ds.getArchiveId());
        stmt.setString(2, ext);
        ResultSet rs = stmt.executeQuery();
        try {
            while (rs.next()) {
                int fileId = rs.getInt("file_id");
                if (!metaDataFileIds.containsKey(fileId)) {
                    DataFileMetadata df = new DataFileMetadata();
                    df.setPlatformTypeId("METADATA");
                    df.setCenterId(ds.getCenterId());
                    df.setPlatformId(ds.getPlatformId());
                    df.setDiseaseType(ds.getDiseaseType()); // set the diseasetype since datafiles can span diseases
                    df.setProtected(false);  //always public
                    df.setBarcodes(df.getBarcodes());
                    df.setDisplaySample("selected_samples");

                    df.setFileId(rs.getString("file_id"));
                    df.setFileName(rs.getString("file_name"));
                    df.setSize(rs.getLong("file_size"));
                    ret.add(df);
                    metaDataFileIds.put(fileId, fileId);
                }
            }
        } finally {
            SQLProcessingCleaner.cleanUpResultSet(rs);
        }
    }

    public void addPathsToSelectedFiles(
            final List<DataFile> selectedFiles) throws DataAccessMatrixQueries.DAMQueriesException {
        try {
            final SimpleJdbcTemplate jdbc = new SimpleJdbcTemplate(getDataSource());
            for (final DataFile fileInfo : selectedFiles) {
                if (fileInfo instanceof DataFileMetadata) {
                    DiseaseContextHolder.setDisease(fileInfo.getDiseaseType());
                    fileInfo.setPath(jdbc.queryForObject(FILE_PATH_QUERY, String.class, fileInfo.getFileId()));
                }
            }
        } catch (DataAccessException e) {
            new ErrorInfo(e); //logs itself
            throw new DataAccessMatrixQueries.DAMQueriesException(e);
        }
    }

    public DAMUtilsI getDamUtils() {
        return damUtils;
    }

    public void setDamUtils(DAMUtilsI damUtils) {
        this.damUtils = damUtils;
    }

}
