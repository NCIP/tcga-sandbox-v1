/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwoThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetLevelTwoThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.util.DataSetReducerI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.ErrorInfo;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Abstract parent class for CGCC level 2 and 3 queries, for shared functionality.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public abstract class DAMQueriesCGCCLevelTwoAndThree extends DAMBaseQueriesProcessor implements DataAccessMatrixQueries {
    protected String tempfileDirectory;
    protected DataSetReducerI dataSetReducer;
    protected final ProcessLogger logger = new ProcessLogger();
    private DAMQueriesCGCCLevelTwoThreeList dccListQueries;
    protected static final int SIXTY_FOUR_MEGS = 64 * 1024;
    private DAMUtilsI damUtils;
    protected TransactionTemplate transactionTemplate;

    public DAMUtilsI getDamUtils() {
        return damUtils;
    }

    public void setDamUtils(DAMUtilsI damUtils) {
        this.damUtils = damUtils;
    }
    
// query used by getDataSetsForDiseaseType
    protected static final String GET_DATASETS_QUERY = "select distinct e.experiment_id, hr.bestbarcode " +
            "from hybrid_ref_data_set hr_ds, data_set ds, hybridization_ref hr, experiment e " +
            "where e.base_name=? and e.data_deposit_batch=? and e.data_revision=? " +
            "and e.experiment_id=ds.experiment_id and hr_ds.hybridization_ref_id=hr.hybridization_ref_id " +
            "and ds.data_set_id=hr_ds.data_set_id and data_level=? and use_in_dam=1 and load_complete=1";


    protected static final String GET_FILE_INFO_QUERY =
                    " select distinct h.hybridization_ref_id, " +
                    "    h.bestbarcode, " +
                    "    ds.data_set_id, " +
                    "    ds.source_file_type, " +
                    "    ds.access_level," +
                    "    f.file_size, " +
                    "    ds.experiment_id\n" +
                    "    from " +
                    "    data_set ds," +
                    "    data_Set_file df," +
                    "    hybrid_ref_data_set hd, " +
                    "    hybridization_Ref h," +
                    "    file_info f," +
                    "    shipped_biospecimen_file bf," +
                    "    shipped_biospecimen b" +
                    "    where ds.load_complete = 1 and ds.use_in_DAM = 1" +
                    "    and   ds.experiment_id in (EXPERIMENT_ID_PLACEHOLDER)" +
                    "    and   ds.data_set_id = hd.data_Set_id" +
                    "    and   hd.hybridization_ref_id=h.hybridization_ref_id" +
                    "    and   h.bestbarcode in (BESTBARCODE_PLACEHOLDER) " +
                    "    and   ds.data_set_id = df.data_set_id " +
                    "    and   df.file_id=f.file_id" +
                    "    and   f.file_id=bf.file_id" +
                    "    and   h.bestbarcode  = b.built_barcode" +
                    "    and   bf.shipped_biospecimen_id=b.shipped_biospecimen_id" +
                    "    and   b.built_barcode in (BESTBARCODE_PLACEHOLDER)" ;


    private int maxInClauseSize = 1000;

    /**
     * Sets the location where temp files (data files before they are packaged) will be written.  Directory must
     * already exist -- will not be created.
     * @param tempfileDirectory the full path to the temp file directory
     */
    public void setTempfileDirectory(final String tempfileDirectory) {
        this.tempfileDirectory = tempfileDirectory;
        if (this.tempfileDirectory != null && this.tempfileDirectory.endsWith(File.separator)) {
            this.tempfileDirectory = this.tempfileDirectory.substring(0, this.tempfileDirectory.length() - 1);
        }
    }

    protected ProcessLogger getLogger() {
        return logger;
    }

    /**
     * Sets the queries object used to query the DCC database to get the initial list of datasets.
     * @param levelTwoThreeListQueries the queries object
     */
    public void setLevelTwoThreeList(final DAMQueriesCGCCLevelTwoThreeList levelTwoThreeListQueries) {
        dccListQueries = levelTwoThreeListQueries;
    }

    public DAMQueriesCGCCLevelTwoThreeList getDccListQueries() {
        return dccListQueries;
    }

    /**
     * Returns list of all datasets for a disease type.
     * Each dataset will become a cell in the DAM.
     *
     * @param diseaseType the disease type
     * @return a list of data sets for this disease type
     */
    public List<DataSet> getDataSetsForDiseaseType(final String diseaseType)
            throws DataAccessMatrixQueries.DAMQueriesException {
        return getDataSetsForDiseaseType(diseaseType, false);
    }

    /**
     * Return a {@link List} of {@link DataSet} for a disease type.
     * Each {@link DataSet} will become a cell in the Data Access Matrix.
     *
     * @param diseaseType the disease type
     * @param forControl <code>true</code> to only include {@link DataSet} for control samples, <code>false</code> otherwise
     * @return a {@link List} of {@link DataSet} for a disease type
     * @throws DAMQueriesException
     */
    public List<DataSet> getDataSetsForDiseaseType(final String diseaseType,
                                                   final boolean forControl) throws DAMQueriesException {

        logger.logToLogger(Level.DEBUG, " LEVEL " + getDataLevel() + " getDataSetsForDiseaseType ");
        List<DataSet> dataSetList = buildInitialList(diseaseType, forControl);
        Map<String, List<DataSetLevelTwoThree>> datasetsByArchive = groupDatasetsByArchive(dataSetList);

        try {
            // check each group of datasets (by archive)
            for (final List<DataSetLevelTwoThree> datasets : datasetsByArchive.values()) {
                // first get all loaded barcodes for this archive
                final Map<String, Integer> loadedBarcodes = queryBarcodesForArchive(datasets);
                // now, iterate through all datasets for this group and see if the barcodes are loaded
                for (final DataSetLevelTwoThree dataset : datasets) {
                    dataset.setDiseaseType(diseaseType);
                    boolean datasetHasData = false;
                    for (final String barcode : dataset.getBarcodes()) {
                        if (loadedBarcodes.get(barcode) != null) {
                            // this barcode has data for this dataset
                            dataset.setExperimentID(loadedBarcodes.get(barcode));
                            datasetHasData = true;
                        }
                    }
                    if (!datasetHasData) {
                        dataset.setAvailability(DataAccessMatrixQueries.AVAILABILITY_PENDING);
                    }
                }
            }
            dataSetList = dataSetReducer.reduceLevelTwoThree(dataSetList, getDataLevel());
        }
        catch (DataAccessException ex) {
            // general data access exceptions mean something is wrong with the database/network/something
            logger.logToLogger(Level.ERROR, ProcessLogger.stackTracePrinter(ex));
            throw new DAMQueriesException(ex);
        }

        return dataSetList;
    }

    private Map<String, Integer> queryBarcodesForArchive(final List<DataSetLevelTwoThree> datasets) {
        final Map<String, Integer> loadedBarcodes = new HashMap<String, Integer>();
        getJdbcTemplate().query(GET_DATASETS_QUERY,
                        new Object[] { datasets.get(0).getDepositBaseName(), datasets.get(0).getDepositBatch(),
                        datasets.get(0).getDataRevision(), getDataLevel() },
                new RowCallbackHandler() {
                    public void processRow(final ResultSet resultSet) throws SQLException {
                        loadedBarcodes.put(resultSet.getString(2), resultSet.getInt(1));
                    }
                }
        );
        return loadedBarcodes;
    }

    protected Map<String, List<DataSetLevelTwoThree>> groupDatasetsByArchive(final List<DataSet> dataSetList) {
        Map<String, List<DataSetLevelTwoThree>> datasetsByArchive = new HashMap<String, List<DataSetLevelTwoThree>>();
        for (final DataSet ds : dataSetList) {
            DataSetLevelTwoThree dataset = (DataSetLevelTwoThree) ds;
            String archive = dataset.getDepositBaseName() + "." + dataset.getDepositBatch() + "." + dataset.getDataRevision();
            List<DataSetLevelTwoThree> datasets = datasetsByArchive.get(archive);
            if (datasets == null) {
                datasets = new ArrayList<DataSetLevelTwoThree>();
                datasetsByArchive.put(archive, datasets);
            }
            datasets.add(dataset);
        }
        return datasetsByArchive;
    }

    /**
     * Given a list of selected datasets (cells), returns additional information
     * pertaining to sample files.
     * The input fields that will be used for querying are: platformId, centerId, sample.
     *
     * @param selectedDataSets the list of selected data sets
     * @param consolidateFiles whether the files should be one per barcode or one per type (with all barcodes in one)
     * @return a list of data files
     */
    public List<DataFile> getFileInfoForSelectedDataSets(final List<DataSet> selectedDataSets,
                                                         final boolean consolidateFiles)
            throws DataAccessMatrixQueries.DAMQueriesException {

        final List<DataFile> result = new ArrayList<DataFile>();
        final Map<String, List<DataSet>> diseaseToDataSetMap = getDamUtils().groupDataSetsByDisease(selectedDataSets);

        if(diseaseToDataSetMap != null) {
            for(final String diseaseType : diseaseToDataSetMap.keySet()) {

                final List<DataSet> singleDiseaseDataSets = diseaseToDataSetMap.get(diseaseType);
                if(singleDiseaseDataSets != null && singleDiseaseDataSets.size() > 0) {

                    setDiseaseInContext(diseaseType);
                    result.addAll(getFileInfoForSelectedDataSetsSingleDisease(singleDiseaseDataSets, consolidateFiles));
                }
            }
        }

        return result;
    }

    /**
     * Return a {@link List} of {@link DataFile} associated with the given {@link DataSet}s.
     *
     * Note: it is assumed that those {@link DataSet}s are all for 1 single disease type.
     *
     * @param selectedDataSets the {@link DataSet}s
     * @param consolidateFiles whether data for selected data sets should be consolidated into as few files as possible
     * or put in one file per sample
     * @return a {@link List} of {@link DataFile} associated with the given {@link DataSet}s
     * @throws DAMQueriesException
     */
    private List<DataFile> getFileInfoForSelectedDataSetsSingleDisease(final List<DataSet> selectedDataSets,
                                                                           boolean consolidateFiles) {
        final List<DataFile> result = new ArrayList<DataFile>();

        if(selectedDataSets != null && selectedDataSets.size() > 0) {

            final Map<String, List<DataSetLevelTwoThree>> datasetsForBasename = groupDataSetsByBaseName(selectedDataSets);

            for (final String depositBaseName : datasetsForBasename.keySet()) {
                makeDataFilesForBaseName(result, datasetsForBasename.get(depositBaseName), consolidateFiles);
            }

            estimateFileSizes(result);

            // Retrieve the disease type (should be the same across all DataSets)
            String diseaseType = null;
            final DataSet firstDataSet = selectedDataSets.get(0);
            if(firstDataSet != null) {
                diseaseType = firstDataSet.getDiseaseType();
            }

            // Set the disease type for each DataFile
            for(final DataFile dataFile : result) {
                dataFile.setDiseaseType(diseaseType);
            }
        }

        return result;
    }

    /**
     * Set the {@link DiseaseContextHolder} disease type
     *
     * @param diseaseType the disease type
     */
    protected void setDiseaseInContext(final String diseaseType) {
        DiseaseContextHolder.setDisease(diseaseType);
    }

    private void makeDataFilesForBaseName(
            final List<DataFile> dataFiles,
            final List<DataSetLevelTwoThree> dataSets, final boolean consolidateFiles) {
        // will hold all experiment ids for this basename
        final Set<Integer> experimentIds = new HashSet<Integer>();
        final Set<String> barcodes = new HashSet<String>();
        final Map<String, DataFileLevelTwoThree> dataFilesBySourceFileType = new HashMap<String, DataFileLevelTwoThree>();
        for (final DataSetLevelTwoThree dataSet : dataSets) {
            if ((experimentIds.size() + barcodes.size() + 1 + dataSet.getBarcodes().size()) > maxInClauseSize ) {
                // need to run the query now, before the number of items in the in clause gets too big!
                makeOneBatchOfDataFilesForBaseName(dataFiles, dataSets, consolidateFiles, experimentIds, barcodes, dataFilesBySourceFileType);
                experimentIds.clear();
                barcodes.clear();
            }
            experimentIds.add(dataSet.getExperimentID());
            barcodes.addAll(dataSet.getBarcodes());
        }
        // run the last batch (or only one, if the total number never approached the max in clause size)
        makeOneBatchOfDataFilesForBaseName(dataFiles, dataSets, consolidateFiles, experimentIds, barcodes, dataFilesBySourceFileType);
    }

    private void makeOneBatchOfDataFilesForBaseName(
            final List<DataFile> dataFiles,
            final List<DataSetLevelTwoThree> dataSets,
            final boolean consolidateFiles,
            final Set<Integer> experimentIds,
            final Set<String> barcodes,
            final Map<String, DataFileLevelTwoThree> dataFilesBySourceFileType) {
        List<Object> bindVals = new ArrayList<Object>();
        String experimentIdInClause = makeQuestionMarkListForInClause(experimentIds, bindVals);
        String barcodeInClause = makeQuestionMarkListForInClause(barcodes, bindVals);
        // add extra bind values
        for (final Object barcode : barcodes) {
            bindVals.add(barcode);
        }

        String query = GET_FILE_INFO_QUERY.replace("EXPERIMENT_ID_PLACEHOLDER", experimentIdInClause);
        query = query.replace("BESTBARCODE_PLACEHOLDER", barcodeInClause);
        final DataSetLevelTwoThree representativeDataSet = dataSets.get(0);

        getJdbcTemplate().query(query, bindVals.toArray(), new RowCallbackHandler() {
            public void processRow(final ResultSet rs) throws SQLException {
                final String underscore = "__";
                //ds.data_set_id
                final int dataSetID = rs.getInt(3);

                final int experimentId = rs.getInt(7);

                //ds.source_file_type
                final String sourceFileType = rs.getString(4);
                //ds.access_level
                final String accessLevel = rs.getString(5);
                //hr.hybridization_ref_id
                final long hybrefId = rs.getLong(1);
                //hr.bestbarcode
                final String barcode = rs.getString(2);
                final String sampleId = getSampleFromBarcode(barcode);
                final long fileSize = rs.getLong(6);

                DataFileLevelTwoThree file = dataFilesBySourceFileType.get(sourceFileType);
                // make a new file if we aren't consolidating -- one per barcode
                if (file == null || !consolidateFiles) {
                    file = makeDataFile(sourceFileType, accessLevel, representativeDataSet);
                    file.setConsolidated(consolidateFiles);
                    file.setExperimentId(experimentId);

                    if (consolidateFiles) {
                        file.setDisplaySample(LEVEL23_SAMPLE_PHRASE);
                        file.setFileName(new StringBuilder().append(representativeDataSet.getCenterName()).
                                append(underscore).append(representativeDataSet.getPlatformName()).append(underscore).
                                append(file.getSourceFileType()).append(".txt").toString());
                    } else {
                        file.setDisplaySample(sampleId);
                        file.setFileName(new StringBuilder().append(representativeDataSet.getCenterName()).
                                append(underscore).append(representativeDataSet.getPlatformName()).append(underscore).
                                append(barcode).append(underscore).append(file.getSourceFileType()).append(".txt").toString());
                    }
                    dataFiles.add(file);
                    dataFilesBySourceFileType.put(sourceFileType, file);
                }
                file.addDataSetID(dataSetID);
                file.setSize(file.getSize()+fileSize);
                file.getBarcodes().add(barcode);
                file.getHybRefIds().add(hybrefId);

                if (!file.getSamples().contains(sampleId)) {
                    file.getSamples().add(sampleId);
                }
            }
        });
    }

    protected String getSampleFromBarcode(final String barcode) {
        return barcode.substring(0, 15);
    }

    private Map<String, List<DataSetLevelTwoThree>> groupDataSetsByBaseName(final List<DataSet> selectedDataSets) {
        final Map<String, List<DataSetLevelTwoThree>> datasetsForBasename = new HashMap<String, List<DataSetLevelTwoThree>>();
        // sort the datasets by basename
        for (final DataSet dataSet : selectedDataSets) {
            if (shouldHandleDataSet(dataSet)) {
                final DataSetLevelTwoThree dataSetSample = (DataSetLevelTwoThree) dataSet;
                List<DataSetLevelTwoThree> datasets = datasetsForBasename.get(dataSetSample.getDepositBaseName());
                if (datasets == null) {
                    datasets = new ArrayList<DataSetLevelTwoThree>();
                    datasetsForBasename.put(dataSetSample.getDepositBaseName(), datasets);
                }
                datasets.add(dataSetSample);
            }
        }
        return datasetsForBasename;
    }

    protected boolean shouldHandleDataSet(final DataSet dataSet) {
        return dataSet instanceof DataSetLevelTwoThree && String.valueOf(getDataLevel()).equals(dataSet.getLevel());
    }

    /**
     * Given a list of fileinfo objects, adds path information to those objects.
     * Generates the files in a temporary location.
     * Returns nothing.
     *
     * @param selectedFiles the list of selected data files
     */
    public void addPathsToSelectedFiles(final List<DataFile> selectedFiles) throws DataAccessMatrixQueries.DAMQueriesException {
        if (tempfileDirectory == null) {
            throw new DataAccessMatrixQueries.DAMQueriesException("No tempfileDirectory specified");
        }
        if (!(new File(tempfileDirectory)).exists()) {
            throw new DataAccessMatrixQueries.DAMQueriesException("Directory does not exist " + tempfileDirectory);
        }
        for (final DataFile df : selectedFiles) { //for each file selected from the tree
            if (shouldGenerateFile(df)) {
                DiseaseContextHolder.setDisease(df.getDiseaseType());
                final DataFileLevelTwoThree file = (DataFileLevelTwoThree) df;

                // the temporary file to write to.
                // the bean expects this to be set before leaving this method.
                final String uniqueName = getUniqueFilename(file);
                final String path = tempfileDirectory + File.separator + uniqueName;
                file.setPath(path);
                long starttime_writer;
                long endtime_writer;
                Writer writerPointer = null; // other writer reference needs to be final for inner class
                Writer writer = null;
                try {
                    starttime_writer = System.currentTimeMillis();
                    logger.logToLogger(Level.DEBUG, "Level" + getDataLevel() + ": Creating writer at " + starttime_writer);
                    //noinspection IOResourceOpenedButNotSafelyClosed
                    writer = new BufferedWriter(new FileWriter(path), SIXTY_FOUR_MEGS);
                    writerPointer = writer;
                    generateFile((DataFileLevelTwoThree)df, writer);
                    writer.flush();
                    writer.close();
                    writerPointer = null;
                    endtime_writer = System.currentTimeMillis();
                    logger.logToLogger(Level.DEBUG, "Level" + getDataLevel() + ": Closed writer at " + endtime_writer);
                    logger.logToLogger(Level.DEBUG, "Level" + getDataLevel() + ": Total millisecs during which writer was alive: " + (endtime_writer - starttime_writer));

                } catch (IOException e) {
                    new ErrorInfo(e); //logs itself
                    throw new DAMQueriesException(e);
                }                
                catch (DataAccessException e) {
                    new ErrorInfo(e);
                    throw new DAMQueriesException(e);
                }
                finally {
                    if (writerPointer != null) {
                        //will only happen if some exception was thrown, in which case we don't care
                        //whether the buffer has been flushed - just make sure the stream is closed
                        try {
                            writerPointer.close();
                        } catch (IOException e) {
                            logger.logToLogger(Level.WARN, "Could not close writer: was already closed.");
                        }
                    }
                    IOUtils.closeQuietly(writer);
                }
            }
        }
    }

    /**
     * Should generate the given file content using the writer.
     * @param dataFile the data file to generate
     * @param writer the writer to write content to
     * @throws IOException if there is an error writing the file
     */
    protected abstract void generateFile(final DataFileLevelTwoThree dataFile, final Writer writer) throws IOException;

    /**
     * Generates a unique filename, uuid plus the source file
     * type of the given file.
     * @param dataFile the file to generate a unique name for
     * @return unique name
     */
    protected String getUniqueFilename(final DataFileLevelTwoThree dataFile) {
        return java.util.UUID.randomUUID() +" - "+ dataFile.getSourceFileType();
    }



    /**
     * Decides if this DAO object should generate the given file or not.  Default implementation just checks the
     * file's data level against the DAO's stated data level.
     * @param dataFile the data file
     * @return true if this DAO should generate this file, false if not
     */
    protected boolean shouldGenerateFile(final DataFile dataFile) {
        return dataFile instanceof DataFileLevelTwoThree && String.valueOf(getDataLevel()).equals(dataFile.getLevel());
    }


    /**
     * Adds size estimates to each of the files in the list
     * @param dataFiles the list of files whose size to estimate
     */
    protected void estimateFileSizes(final List<DataFile> dataFiles) {
        for (final DataFile df : dataFiles) {
            long averageLineSize = getAverageLineSize((DataFileLevelTwoThree) df);
            long numberOfLines = getNumberOfLinesForFile((DataFileLevelTwoThree) df);
            long size = numberOfLines * averageLineSize;
            df.setSize(size);
        }
    }

    /**
     * Returns the expected number of lines (including headers) for this file.
     * @param df the data file
     * @return the expected number of lines in this file, including headers
     */
    protected abstract long getNumberOfLinesForFile(final DataFileLevelTwoThree df);

    /**
     * Gets the estimate for the size of one line of this data file
     * @param datafile the data file
     * @return average line size
     */
    protected abstract long getAverageLineSize(DataFileLevelTwoThree datafile);

    private DataFileLevelTwoThree makeDataFile( final String sourceFileType, final String accessLevel,
                                                final DataSetLevelTwoThree representativeDataSet) {
        final DataFileLevelTwoThree file = DataFileLevelThree.makeInstance(getDataLevel());
        file.setSourceFileType(sourceFileType);
        file.setAccessLevel(accessLevel);
        file.setBarcodes(new TreeSet<String>()); // barcode collection is treeset, so will be kept in sorted order
        file.setHybRefIds(new HashSet<Long>()); // set, so unique values only
        file.setFileId(file.getSourceFileType());
        file.setPlatformTypeId(representativeDataSet.getPlatformTypeId());
        file.setPlatformId(representativeDataSet.getPlatformId());
        file.setPlatformName(representativeDataSet.getPlatformName());
        file.setCenterId(representativeDataSet.getCenterId()); // THIS IS THE DCC CENTERID, NOT DP CENTERID        
        file.setCenterName(representativeDataSet.getCenterName()); // the name for the "file"
        return file;
    }

    protected String makeQuestionMarkListForInClause(final Collection things, final List<Object> queryBindValues) {
        StringBuilder questionMarks = new StringBuilder();
        boolean isFirst = true;
        for (final Object thing : things) {
            queryBindValues.add(thing);
            if (!isFirst) {
                questionMarks.append(", ");
            }
            questionMarks.append("?");
            isFirst = false;
        }
        return questionMarks.toString();
    }

    protected abstract List<DataSet> buildInitialList(String diseaseType, boolean forControls) throws DataAccessMatrixQueries.DAMQueriesException;
    protected abstract int getDataLevel();

    public void setDataSetReducer(final DataSetReducerI dataSetReducer) {
        this.dataSetReducer = dataSetReducer;
    }

    public void setMaxInClauseSize(final int maxInClauseSize) {
        this.maxInClauseSize = maxInClauseSize;
    }

    public void setTransactionTemplate(final TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    public TransactionTemplate getTransactionTemplate() {
        return transactionTemplate;
    }

}
