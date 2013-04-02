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
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.common.util.SourceFileTypeFinder;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwo;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwoConsolidated;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwoThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetLevelTwoThree;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Gathers information about level 2 files from the Oracle Portal database. In the process of creating archives, this
 * class will write temporary clinical output files to disk using information from Oracle.  The File Packager will then
 * incorporate those files into the new archive.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class DAMQueriesLevel2 extends DAMQueriesCGCCLevelTwoAndThree implements DataAccessMatrixQueries {

    // query to get average length of a value
    private static final String AVERAGE_VALUE_LENGTH_QUERY = "select avg_col_len from avg_col_len where table_name = 'HYBRIDIZATION_VALUE' and column_name = 'VALUE'";

    // estimated length of value field, in case avg_col_len table not populated
    private static final int ESTIMATED_AVERAGE_DATA_LENGTH = 9;
    private static final int BATCH_SIZE = 1000;
    // query to get number of hyb data groups for a data set
    private static final String COUNT_DATA_GROUPS_QUERY = "select count(hybridization_data_group_id) from hybridization_data_group where data_set_id = ?";

    private static final String TEMP_TABLE_NAME = "TMPHYBREF";
    private static final String TEMP_COLUMN_NAME = "HYBRIDIZATION_REF_ID";
    private static final String TEMP_INSERT_SQL = "INSERT INTO " + TEMP_TABLE_NAME + "(" + TEMP_COLUMN_NAME + ") VALUES(?)";
    //private static final String TEMP_DELETE_SQL = "TRUNCATE TABLE " + TEMP_TABLE_NAME;
    private static final String DS_TEMP_TABLE_NAME = "TMPDATASET";
    private static final String DS_TEMP_COLUMN_NAME = "DATA_SET_ID";
    private static final String DS_TEMP_INSERT_SQL = "INSERT INTO " + DS_TEMP_TABLE_NAME + "(" + DS_TEMP_COLUMN_NAME + ") VALUES(?)";

    private static final String DATA_SET_IN_CLAUSE_PLACEHOLDER = "DATA_SET_IN_CLAUSE";
    protected static final String HYBRIDIZATION_VALUE_QUERY_HINT = "/*+ FULL(hv) PARALLEL(hv, 4) */";
    private static final String HINT_PLACE_HOLDER = "HINT_PLACE_HOLDER";
    private static final String CHROMOSOME_HEADER = "Chromosome";
    private static final String POSITION_HEADER = "Position";

    private int minExpectedRowsToUseHintQuery;
    /* query to get data for writing to files.  note in clauses are placeholders that must be replaced by appropriate number of question marks!

    THE FOLLOWING BLOCK MUST BE CHANGED CAREFULLY!  WE ARE USING INTEGERS FOR THE COLUMN NAMES SO WE CAN RETRIEVE THE DATA FROM THE DB
    EFFICIENTLY WITHOUT MAKING A CALL TO getColumnIndex() IN THE ORACLE JDBC DRIVER.

    IF YOU ARE GOING TO CHANGE THE SELECT, PLEASE LOOK TO MAKE SURE THE ORDER OF THE COLUMN HAS NOT CHANGED!
    */

    private static final String HYBRIDIZATION_VALUE_QUERY = "select " + HINT_PLACE_HOLDER + " distinct p.probe_name, p.chromosome, p.start_position, p.end_position, " +
            "hv.hybridization_ref_id, hg.group_column_name, hv.value " +
            "from hybridization_data_group hg, hybridization_value hv, probe p, " + TEMP_TABLE_NAME + " t, " + DS_TEMP_TABLE_NAME + " td " +
            "where hg.data_set_id = td." + DS_TEMP_COLUMN_NAME + " " +
            "and hv.hybridization_ref_id=t." + TEMP_COLUMN_NAME + " " +
            "and hv.platform_id=? " +
            "and hv.hybridization_data_group_id = hg.hybridization_data_group_id " +
            "and p.probe_id=hv.probe_id " +
            "and p.platform_id=? " +
            "order by p.probe_name";

    /*
     END OF QUERY AND INTEGER MAPPING BLOCK 
     */
    private static final String INSERT_BARCODE_INTO_TMP_TABLE = " Insert into tmpbarcode values(?)";
    private static final String GET_LEVEL2_SAMPLE_BARCODES_QUERY = "select sb.built_barcode " +
            " from " +
            " shipped_biospecimen sb, " +
            " shipped_biospecimen_file sbf, " +
            " file_info fi , " +
            " file_to_archive fa," +
            " archive_info ai" +
            " where sb.shipped_biospecimen_id=sbf.shipped_biospecimen_id" +
            " and sbf.file_id=fi.file_id" +
            " and fi.file_id = fa.file_id " +
            " and fa.archive_id=ai.archive_id" +
            " and ai.is_latest =1" +
            " and fi.level_number = 2 ";

    public static int MAX_IN_CLAUSE = 1000;
    private static final String FILE_INFO_QUERY = "select distinct fi.file_id, " +
            " fi.file_name, " +
            " fi.file_size," +
            " f2a.file_location_url," +
            " sb.built_barcode " +
            " from " +
            " shipped_biospecimen sb," +
            " tmpbarcode tmp,"+
            " shipped_biospecimen_file sbf," +
            " file_info fi," +
            " file_to_archive f2a, " +
            " archive_info ai " +
            " where " +
            " sb.built_barcode = tmp.barcode and" +
            " sb.shipped_biospecimen_id = sbf.shipped_biospecimen_id and" +
            " sbf.file_id = fi.file_id and " +
            " fi.file_id = f2a.file_id and" +
            " f2a.archive_id = ai.archive_id and " +
            " ai.is_latest = 1 and " +
            " ai.platform_id IN ( :platformids ) and " +
            " fi.level_number = 2" ;

    private static final String HYBRIDIZATION_REF_COLUMN_HEADER = "Hybridization REF";
    private static final String COMP_ELEMENT_REF_COL_HEADER = "CompositeElement REF";

    private static final String PROBE_COUNT_QUERY = "select count(*) from probe where platform_id=(select platform_id from data_set where data_set_id=?)";
    private static final String PLATFORM_ID_QUERY = "select platform_id from data_set where data_set_id=?";
    private static final String BARCODE_FOR_HYBREF_QUERY = "select bestbarcode from hybridization_ref where hybridization_ref_id=?";
    private static final String HYB_DATA_GROUP_NAME_QUERY = "select group_column_name, hybridization_data_group_id from hybridization_data_group where data_set_id = ? order by group_column_number";
    private static final String PROBE_CONSTANT_COUNT = "select count(*) from probe where platform_id=? and chromosome is not null";
    private SourceFileTypeFinder sourceFileTypeFinder;

    /**
     * Builds the initial list of datasets by querying the DCC database.  This will include all EXPECTED data sets (that
     * is, data that we have recorded as submitted).
     *
     * @param diseaseType the disease type
     * @return list of datasets that have been submitted to us
     * @throws DAMQueriesException
     */
    protected List<DataSet> buildInitialList(final String diseaseType, final boolean forControls) throws DAMQueriesException {
        return getDccListQueries().buildInitialList(diseaseType, 2, forControls);
    }

    /**
     * Returns list of all datasets for a disease type.
     * Each dataset will become a cell in the DAM.
     *
     * @param diseaseType the disease type
     * @return a list of data sets for this disease type
     */
    public List<DataSet> getDataSetsForDiseaseType(
            final String diseaseType) throws DataAccessMatrixQueries.DAMQueriesException {
        return getDataSets(diseaseType, false);
    }

    @Override
    public List<DataSet> getDataSetsForControls(final List<String> diseaseTypes) throws DAMQueriesException {
        final List<DataSet> controlDataSets = new ArrayList<DataSet>();
        for (final String disease : diseaseTypes) {
            setDiseaseInContext(disease);
            controlDataSets.addAll(getDataSets(disease, true));
        }
        return controlDataSets;
    }

    private List<DataSet> getDataSets(final String diseaseType, final boolean forControl) throws DAMQueriesException {

        List<DataSet> allSamplesDataSetList = buildInitialList(diseaseType, forControl);

        try {
            // first get all loaded barcodes for this archive
            final List<String> level2SampleBarcodes = getLevel2SampleBarcodes();
            // now, iterate through all data sets for this group and see if the barcodes are loaded
            for (final DataSet dataSet : allSamplesDataSetList) {
                dataSet.setDiseaseType(diseaseType);
                boolean dataSetHasData = false;
                for (final String barcode : dataSet.getBarcodes()) {
                    dataSetHasData |= level2SampleBarcodes.contains(barcode);
                }
                if (!dataSetHasData) {
                    dataSet.setAvailability(DataAccessMatrixQueries.AVAILABILITY_PENDING);
                }
            }

            allSamplesDataSetList = dataSetReducer.reduceLevelTwoThree(allSamplesDataSetList, getDataLevel());
        }
        catch (DataAccessException ex) {
            // general data access exceptions mean something is wrong with the database/network/something
            logger.logToLogger(Level.ERROR, ProcessLogger.stackTracePrinter(ex));
            throw new DataAccessMatrixQueries.DAMQueriesException(ex);
        }

        return allSamplesDataSetList;
    }

    private List<String> getLevel2SampleBarcodes(){

        return getJdbcTemplate().query(GET_LEVEL2_SAMPLE_BARCODES_QUERY,
                    new ParameterizedRowMapper<String>() {
                    public String mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
                        return resultSet.getString("built_barcode");
                    }
                });
    }

    @Override
    public List<DataFile> getFileInfoForSelectedDataSets(final List<DataSet> selectedDataSets,
                                                         final boolean consolidateFiles) throws DataAccessMatrixQueries.DAMQueriesException {

        final List<DataFile> result = new ArrayList<DataFile>();
        final Map<String, List<DataSet>> diseaseToDataSetMap = getDamUtils().groupDataSetsByDisease(selectedDataSets);

        if(diseaseToDataSetMap != null) {
            for(final String diseaseType : diseaseToDataSetMap.keySet()) {

                final List<DataSet> singleDiseaseDataSets = diseaseToDataSetMap.get(diseaseType);
                if(singleDiseaseDataSets != null && singleDiseaseDataSets.size() > 0) {

                    setDiseaseInContext(diseaseType);
                    result.addAll(getFileInfoForSelectedDataSetsUniqueDisease(singleDiseaseDataSets, consolidateFiles));
                }
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
    private List<DataFile> getFileInfoForSelectedDataSetsUniqueDisease(final List<DataSet> selectedDataSets,
                                                                       final boolean consolidateFiles)
            throws DAMQueriesException {

        final List<DataFile> dataFiles = new ArrayList<DataFile>();

        final Map<String, DataSetLevelTwoThree> barcodeDataSetMap = getBarcodesForDataSets(selectedDataSets);

        final Set<String> platformIds = new HashSet<String>();
        for (DataSet dataSet : selectedDataSets) {
            if (dataSet.getPlatformId() != null && !StringUtils.isEmpty(dataSet.getPlatformId())) {
                platformIds.add(dataSet.getPlatformId());
            }
        }

        if (barcodeDataSetMap.size() > 0) {
            final List<String> barcodes = new ArrayList(barcodeDataSetMap.keySet());

            transactionTemplate.execute(new TransactionCallback() {
                public Object doInTransaction(final TransactionStatus transactionStatus) {
                    // insert barcodes into temp table
                    insertBarcodesInToTmpTable(barcodes);
                    final ParameterizedRowMapper<DataFile> mapper = new ParameterizedRowMapper<DataFile>() {
                        public DataFile mapRow(final ResultSet rs, final int rowNum) throws SQLException {
                            final DataSet dataSet = barcodeDataSetMap.get(rs.getString(5));
                            final DataFile dataFile = new DataFileLevelTwo();
                            dataFile.setFileId(rs.getString(1));
                            dataFile.setFileName(rs.getString(2));
                            dataFile.setSize(rs.getLong(3));
                            dataFile.setPath(rs.getString(4));
                            dataFile.setPlatformTypeId(dataSet.getPlatformTypeId());
                            dataFile.setCenterId(dataSet.getCenterId());
                            dataFile.setPlatformId(dataSet.getPlatformId());
                            dataFile.setDisplaySample(dataSet.getSample());
                            dataFile.setProtected(dataSet.isProtected());
                            dataFile.setBarcodes(dataSet.getBarcodes());
                            dataFile.setPermanentFile(true);
                            dataFile.setDiseaseType(dataSet.getDiseaseType());
                            return dataFile;
                        }
                    };
                    // get data files
                    final MapSqlParameterSource parameter = new MapSqlParameterSource();
                    parameter.addValue("platformids", platformIds);
                    final SimpleJdbcTemplate jdbc = new SimpleJdbcTemplate(getDataSource());
                    dataFiles.addAll(jdbc.query(FILE_INFO_QUERY, mapper, parameter));
                    return null;
                }
            });

            if (consolidateFiles) {

                return buildConsolidatedFiles(dataFiles, barcodeDataSetMap.get(barcodes.get(0)));
            }
        }
        return dataFiles;
    }

    protected List<DataFile> buildConsolidatedFiles(final List<DataFile> dataFiles,
                                                    final DataSetLevelTwoThree representativeDataSet) throws DAMQueriesException {
        /*
         * if consolidateFiles is true, then take these data files and combine them into a single data file, after
         * finding the source file type for each.
         * if any don't have source file types, leave them alone as single files
         */

        final List<DataFile> consolidatedFiles = new ArrayList<DataFile>();
        final Map<String, DataFileLevelTwoConsolidated> consolidatedFilesByType = new TreeMap<String, DataFileLevelTwoConsolidated>();
        for (final DataFile dataFile : dataFiles) {
            if (dataFile instanceof DataFileLevelTwo) {
                try {
                    final String sourceFileType = sourceFileTypeFinder.findSourceFileType(Long.valueOf(dataFile.getFileId()));
                    if (sourceFileType != null) {
                        DataFileLevelTwoConsolidated consolidatedFile = consolidatedFilesByType.get(sourceFileType);
                        if (consolidatedFile == null) {
                            consolidatedFile = new DataFileLevelTwoConsolidated();
                            consolidatedFile.setFileName(new StringBuilder().append(representativeDataSet.getCenterName()).
                                append("_").append(representativeDataSet.getPlatformName()).append("_").
                                append(sourceFileType).append(".txt").toString());
                            consolidatedFilesByType.put(sourceFileType, consolidatedFile);
                            consolidatedFiles.add(consolidatedFile);
                            consolidatedFile.setSourceFileType(sourceFileType);
                            consolidatedFile.setPlatformTypeId( dataFile.getPlatformTypeId() );
                            consolidatedFile.setCenterId( dataFile.getCenterId() );
                            consolidatedFile.setPlatformId( dataFile.getPlatformId() );
                        }
                        consolidatedFile.addConstituentDataFile((DataFileLevelTwo)dataFile);
                        consolidatedFile.setSize(consolidatedFile.getSize()+dataFile.getSize());
                    } else {
                        // if no source file type, put this file in the final list as a non-consolidated file
                        consolidatedFiles.add(dataFile);
                    }
                } catch (IOException e) {
                    throw new DataAccessMatrixQueries.DAMQueriesException(e.getMessage(), e);
                }
            }
        }

        return consolidatedFiles;
    }

    public void addPathsToSelectedFiles( final List<DataFile> selectedFiles ) throws DataAccessMatrixQueries.DAMQueriesException {

        transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction(final TransactionStatus transactionStatus) {
                // create consolidated files for DataFileLevelTwoConsolidated
                // for other DataFiles do nothing
                for (final DataFile dataFile : selectedFiles) {
                    if(dataFile instanceof DataFileLevelTwoConsolidated){
                        try{
                            createConsolidatedFiles(dataFile);
                        }catch(IOException ie){
                            throw new DataAccessException(ie.getMessage(), ie){};
                        }
                    }
                }
                return null;

            }
        });
    }

    /**
     * Combines all the level2DataFiles (getConstituentDataFiles()) into one file
     * @param dataFile
     * @throws IOException
     */

     public void createConsolidatedFiles(final DataFile dataFile) throws IOException {

         DiseaseContextHolder.setDisease(dataFile.getDiseaseType());

         if (tempfileDirectory == null) {
            throw new IOException("No tempfileDirectory specified");
        }
        if (!(new File(tempfileDirectory)).exists()) {
            throw new IOException("Directory does not exist " + tempfileDirectory);
        }
        final DataFileLevelTwoConsolidated dataFileLevelTwoConsolidated = (DataFileLevelTwoConsolidated) dataFile;
        final String uniqueName = getUniqueFilename(dataFileLevelTwoConsolidated);
        final String consolidatedFilePath = tempfileDirectory + File.separator + uniqueName;
        final String consolidatedTmpFilePath = consolidatedFilePath+"_tmp";
        dataFileLevelTwoConsolidated.setPath(consolidatedFilePath);


        TreeSet<DataFileLevelTwo> dataFilesLevelTwo = dataFileLevelTwoConsolidated.getConstituentDataFiles();
        // copy first file into consolidatedFile
        FileUtil.copyFile(dataFilesLevelTwo.first().getPath(), consolidatedFilePath);
        // copy other files into consolidated file
        final StringBuilder dataToWrite = new StringBuilder();
        for(DataFileLevelTwo dataFileLevelTwo: dataFilesLevelTwo.tailSet(dataFilesLevelTwo.first(),false)){

            BufferedReader consolidatedFile = null;
            BufferedReader constituentFile = null;
            BufferedWriter consolidatedTmpFile = null;

            try{
                consolidatedFile = new BufferedReader(new FileReader(consolidatedFilePath),SIXTY_FOUR_MEGS);
                constituentFile = new BufferedReader(new FileReader(dataFileLevelTwo.getPath()),SIXTY_FOUR_MEGS);
                consolidatedTmpFile = new BufferedWriter(new FileWriter(consolidatedTmpFilePath),SIXTY_FOUR_MEGS);
                String constituentRowData = "";
                boolean firstRow = true;
                int numColumnsToSkip = 1; // default, but will change if file has constant columns

                // assuming all the files will have same row count
                while((constituentRowData = constituentFile.readLine()) != null){
                    // clear the data
                    dataToWrite.delete(0,dataToWrite.length());
                    final String consolidatedRowData = consolidatedFile.readLine();

                    if (consolidatedRowData != null){

                        final String[] rowDataArray = constituentRowData.split("\\t");
                        if (firstRow) {
                            for (final String cellData : rowDataArray) {
                                if (cellData.length() == 0) {
                                    numColumnsToSkip++;
                                }
                            }
                        } else {
                            dataToWrite.append("\n");
                        }

                        dataToWrite.append(consolidatedRowData);
                        for (int i=numColumnsToSkip; i<rowDataArray.length; i++) {
                            dataToWrite.append("\t").append(rowDataArray[i]);
                        }
                        consolidatedTmpFile.write(dataToWrite.toString());
                    }
                    firstRow = false;

                }

            }finally{
                closeFile(consolidatedFile);
                closeFile(constituentFile);
                closeFile(consolidatedTmpFile);
                // move consolidatedTmpFile  to consolidatedFile
                FileUtil.move(consolidatedTmpFilePath,consolidatedFilePath);
                new File(consolidatedTmpFilePath).delete();
            }
        }

    }

    private void closeFile(final Closeable fileStream){
        try{
            fileStream.close();
        }catch(IOException ie){
            logger.logToLogger(Level.ERROR, "Error closing file "+ie.getMessage());
        }

    }

    private void insertBarcodesInToTmpTable(final List<String> barcodes){
        final SimpleJdbcTemplate simpleJdbcTemplate = new SimpleJdbcTemplate(getDataSource());
        List<Object[]> values = new ArrayList();
        for(String barcode:barcodes){
            if(values.size() == BATCH_SIZE){
                simpleJdbcTemplate.batchUpdate(INSERT_BARCODE_INTO_TMP_TABLE,values);
                values.clear();
            }
            values.add(new Object[]{barcode});
        }
        if(values.size() > 0){
            simpleJdbcTemplate.batchUpdate(INSERT_BARCODE_INTO_TMP_TABLE,values);
        }
    }

    private Map<String,DataFile>getFileIdsForDataFiles(final List<DataFile> selectedDataFiles){
        Map<String,DataFile> barcodeMap = new HashMap<String,DataFile>();
        for(final DataFile dataFile: selectedDataFiles){
            barcodeMap.put(dataFile.getFileId(),dataFile);
        }
        return barcodeMap;

    }

   private Map<String,DataSetLevelTwoThree>  getBarcodesForDataSets(final List<DataSet> selectedDataSets){
        Map<String,DataSetLevelTwoThree> barcodeMap = new HashMap<String,DataSetLevelTwoThree>();
        for(final DataSet dataSet: selectedDataSets){
            if(shouldHandleDataSet(dataSet)){
                for(String barcode: dataSet.getBarcodes()){
                    barcodeMap.put(barcode,(DataSetLevelTwoThree)dataSet);
                }
            }
        }
        return barcodeMap;

    }


    /**
     * Gets the integer data level.
     *
     * @return the integer 2
     */
    protected int getDataLevel() {
        return 2;
    }

    /**
     * Return the expected number of lines (including headers) for this file.
     *
     * @param df the data file object
     * @return the expected number of lines in this file, including headers
     */
    protected long getNumberOfLinesForFile(final DataFileLevelTwoThree df) {
        // number of lines is the number of probes for this platform
        // note: some files may exclude some probes, but this number is good for an estimate.  Right?  Otherwise, will need to count in hyb_value table which might be really slow.
        long numberOfLines = 2; // start with 2 because of headers: major and minor headers
        if (df.getDataSetsDP().size() > 0) {
            numberOfLines += getProbeCount(df);
        }
        return numberOfLines;
    }

    protected long getProbeCount(final DataFileLevelTwoThree df) {
        final int dataSetId = df.getDataSetsDP().iterator().next();
        try {
            return getJdbcTemplate().queryForLong(PROBE_COUNT_QUERY, new Object[]{dataSetId});
        } catch (IncorrectResultSizeDataAccessException e) {
            // this means the platform has no probes?  should not happen but if it does, file will have no data in it
            getLogger().logToLogger(Level.ERROR, "getNumberOfLinesForFile: platform for data set id " + dataSetId + " has no associated probes?");
        }
        return 0;
    }

    /**
     * Gets the average line size for this data file.  Multiplies the average value size by the number of values
     * expected in the row (which is the number of hybrefIds (aka barcodes) times the number of data groups, plus one
     * for the probe name).
     *
     * @param datafile the data file whose line size we need to estimate
     * @return the anticipated average line size for the file
     */
    protected long getAverageLineSize(final DataFileLevelTwoThree datafile) {
        long averageValueSize = getAverageValueSize();
        int numberOfBarcodes = datafile.getHybRefIds().size();
        int numberOfDataGroups = getDataGroupsCount(datafile);
        int numberOfColumns = numberOfBarcodes * numberOfDataGroups + 1;  // +1 for probe name column
        return (averageValueSize + 1) * numberOfColumns; // size +1 for tab or newline following value
    }

    protected int getDataGroupsCount(final DataFileLevelTwoThree datafile) {
        return getJdbcTemplate().queryForInt(COUNT_DATA_GROUPS_QUERY, new Object[]{datafile.getDataSetsDP().iterator().next()});
    }

    /**
     * Gets the average size of a value in the hybridization value table.
     *
     * @return average value size from avg_col_len table
     */
    protected int getAverageValueSize() {
        int averageValueSize = ESTIMATED_AVERAGE_DATA_LENGTH;
        try {
            averageValueSize = getJdbcTemplate().queryForInt(AVERAGE_VALUE_LENGTH_QUERY);
        } catch (IncorrectResultSizeDataAccessException e) {
            // do nothing, use estimated value
        }
        return averageValueSize;
    }

    /**
     * Generates the actual data file for the given data file object, writing the contents to the given Writer.
     *
     * @param dataFile the data file holding the information about what to write
     * @param writer   the writer to write to
     */
    protected void generateFile(final DataFileLevelTwoThree dataFile, final Writer writer) {
        /*
        Note: this happens within a transaction because we write to a temp table first and then
        query against it to extract the data, and we need to make sure that the temp table insertion and
        subsequent query occur in the same transaction or else the temp table data may not be visible to us.
        (It is a global temp table where each session can only see its own inserted data.)
         */
        transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction(final TransactionStatus transactionStatus) {
                try {
                    generateFileInTransaction(dataFile, writer);
                } catch (IOException e) {
                    throw new DataAccessException(e.getMessage(), e) {
                    };
                }
                return null;
            }
        });

    }

    /**
     * Inserts hybridization_ref_ids into the temp table, for use in query for data.
     *
     * @param ids the ids to insert
     */
    protected void insertTempHybrefIds(final Collection<Long> ids) {
        for (final Long id : ids) {
            getJdbcTemplate().update(TEMP_INSERT_SQL, new Object[]{id});
        }
    }

    /**
     * Inserts data_set_ids into the temp table, for use in query for data.
     *
     * @param ids the ids to insert
     */
    protected void insertTempDataSetIds(final Collection<Integer> ids) {
        for (final Integer id : ids) {
            getJdbcTemplate().update(DS_TEMP_INSERT_SQL, new Object[]{id});
        }
    }

    private void generateFileInTransaction(final DataFileLevelTwoThree dataFile, final Writer writer)
            throws IOException {
        /*
            Set the variables for column names we'll need locally.  Local var is 6 times faster than a shared constant.
            If you change the select for HYBRIDIZATION_VALUE_QUERY please change these values and maintain column number order.
         */
        final Integer PROBE_NAME = 1;
        final Integer CHROMOSOME = 2;
        final Integer START_POSITION = 3;
        final Integer END_POSITION = 4;
        final Integer HYBRIDIZATION_REF_ID = 5;
        final Integer GROUP_COLUMN_NAME = 6;
        final Integer VALUE = 7;

        final String STRING = "-";
        // 1. gather barcodes and hyb_data_groups from database
        final List<String> hybDataGroupNames = gatherHybridizationDataGroupNames(dataFile.getDataSetsDP().iterator().next());
        final Map<String, Long> hybrefIdToBarcodeMap = getBarcodesForHybrefs(dataFile);

        // sort barcodes into the order we want to write them in the file (alphabetical)
        final String[] orderedBarcodes = new String[hybrefIdToBarcodeMap.size()];
        hybrefIdToBarcodeMap.keySet().toArray(orderedBarcodes);
        Arrays.sort(orderedBarcodes);

        int platformId = getPortalPlatformId(dataFile.getDataSetsDP().iterator().next());

        final boolean willHaveProbeConstants = getWillHaveProbeConstants(platformId);
        writeHeaders(writer, hybDataGroupNames, orderedBarcodes, willHaveProbeConstants);

        List<Object> queryBindValues = new ArrayList<Object>();
        String query = prepareQueryAndBindVariables(dataFile, queryBindValues, platformId);
        insertTempHybrefIds(dataFile.getHybRefIds());
        insertTempDataSetIds(dataFile.getDataSetsDP());
        final Map<String, String> currentRowValues = new HashMap<String, String>(); // keyed by "hybref_id.data_group_name"
        final String[] lastProbe = new String[]{null, null, null}; // done this way b/c used by inner class
        getJdbcTemplate().query(query, queryBindValues.toArray(), new RowCallbackHandler() {
            public void processRow(final ResultSet resultSet) throws SQLException {
                resultSet.setFetchSize(DEFAULT_FETCHSIZE);

                String currentProbe = resultSet.getString(PROBE_NAME);
                if (lastProbe[0] != null && !lastProbe[0].equals(currentProbe)) {
                    // this result set is the start of a new row, so write the old one
                    try {
                        writeDataRow(lastProbe, currentRowValues,
                                orderedBarcodes, hybrefIdToBarcodeMap,
                                hybDataGroupNames, writer, willHaveProbeConstants);
                        currentRowValues.clear();
                    } catch (IOException e) {
                        getLogger().logError(e);
                        throw new DataAccessException(e.getMessage(), e) {
                        };
                    }
                }

                // store this value in the values map, keyed by combination of hybrefid and datagroup name
                final String key = resultSet.getLong(HYBRIDIZATION_REF_ID) + "." + resultSet.getString(GROUP_COLUMN_NAME);
                currentRowValues.put(key, resultSet.getString(VALUE));
                lastProbe[0] = currentProbe;
                lastProbe[1] = resultSet.getString(CHROMOSOME);
                lastProbe[2] = resultSet.getString(START_POSITION) + STRING + resultSet.getString(END_POSITION);
            }
        });
        // write last row!
        if (lastProbe[0] != null) {
            writeDataRow(lastProbe, currentRowValues, orderedBarcodes, hybrefIdToBarcodeMap, hybDataGroupNames, writer, willHaveProbeConstants);
        }
    }

    /**
     * Gets the expected row count for this data file. Multiplies the   number of probes * number of data group per data
     * set * number of hybridization ref id
     *
     * @param dataFile
     * @return
     */
    protected long getExpectedRowCount(final DataFileLevelTwoThree dataFile) {

        return getProbeCount(dataFile) * getDataGroupsCount(dataFile) * dataFile.getHybRefIds().size();
    }

    private boolean getWillHaveProbeConstants(final int platformId) {
        int numProbesWithConstants = getJdbcTemplate().queryForInt(PROBE_CONSTANT_COUNT, new Object[]{platformId});
        return numProbesWithConstants > 0;
    }

    protected String prepareQueryAndBindVariables(
            final DataFileLevelTwoThree dataFile,
            final List<Object> queryBindValues, final int platformId) {

        // decide whether to use hint or not in the query
        final long expectedRowCount = getExpectedRowCount(dataFile);
        String replaceData = (expectedRowCount < getMinExpectedRowsToUseHintQuery()) ? "" : HYBRIDIZATION_VALUE_QUERY_HINT;
        String query = HYBRIDIZATION_VALUE_QUERY.replace(HINT_PLACE_HOLDER, replaceData);


        // add the platform id to the bind variables twice, once for hyb value and once for probe
        queryBindValues.add(platformId);
        queryBindValues.add(platformId);

        getLogger().logToLogger(Level.INFO, query);
        getLogger().logToLogger(Level.INFO, queryBindValues.toString());
        getLogger().logToLogger(Level.INFO, " Expected row count :" + expectedRowCount + " File size " + dataFile.getSize());

        return query;
    }

    private void writeHeaders(
            final Writer writer, final List<String> hybDataGroupNames, final String[] orderedBarcodes,
            final boolean willHaveProbeConstants)
            throws IOException {

        final String TAB_CHAR = "\t";
        final String NEW_LINE_CHAR = "\n";

        StringBuilder majorHeader = new StringBuilder(HYBRIDIZATION_REF_COLUMN_HEADER);
        StringBuilder minorHeader = new StringBuilder(COMP_ELEMENT_REF_COL_HEADER);

        if (willHaveProbeConstants) {
            majorHeader.append(TAB_CHAR + TAB_CHAR); // two constants
            minorHeader.append(TAB_CHAR).append(CHROMOSOME_HEADER).append(TAB_CHAR).append(POSITION_HEADER);
        }
        // write each barcode once per hyb data group
        // and write each hyb data group once per barcode
        for (final String barcode : orderedBarcodes) {
            for (final String hybDataGroupName : hybDataGroupNames) {
                majorHeader.append(TAB_CHAR).append(barcode);
                minorHeader.append(TAB_CHAR).append(hybDataGroupName);
            }
        }
        writer.write(majorHeader.toString());
        writer.write(NEW_LINE_CHAR);
        writer.write(minorHeader.toString());
        writer.write(NEW_LINE_CHAR);
    }

    private int getPortalPlatformId(final Integer datasetId) {
        return getJdbcTemplate().queryForInt(PLATFORM_ID_QUERY, new Object[]{datasetId});
    }

    private void writeDataRow(
            final String[] probeInfo, final Map<String, String> values,
            final String[] orderedBarcodes, final Map<String, Long> hybrefIdToBarcodeMap,
            final List<String> hybDataGroupNames,
            final Writer writer, final boolean willHaveProbeConstants)
            throws IOException {

        final String TAB_CHAR = "\t";
        final String NEW_LINE_CHAR = "\n";

        // write in the order given by orderedBarcodes
        writer.write(probeInfo[0]);
        if (willHaveProbeConstants) {
            writer.write(TAB_CHAR);
            writer.write(probeInfo[1]); // chromosome
            writer.write(TAB_CHAR);
            writer.write(probeInfo[2]); // position
        }
        for (final String barcode : orderedBarcodes) {
            for (final String dataGroup : hybDataGroupNames) {
                writer.write(TAB_CHAR);
                // get the value for this barcode/datagroup combination
                String key = hybrefIdToBarcodeMap.get(barcode) + "." + dataGroup;
                String value = values.get(key);
                if (value == null) {
                    value = "";
                }
                writer.write(value);
            }
        }
        writer.write(NEW_LINE_CHAR);
    }

    private Map<String, Long> getBarcodesForHybrefs(final DataFileLevelTwoThree df) {
        final Map<String, Long> barcodeForHybRefMap = new HashMap<String, Long>();
        for (final long hybrefId : df.getHybRefIds()) {
            String barcode = (String) getJdbcTemplate().queryForObject(BARCODE_FOR_HYBREF_QUERY,
                    new Object[]{hybrefId}, String.class);
            barcodeForHybRefMap.put(barcode, hybrefId);
        }
        return barcodeForHybRefMap;
    }

    private List<String> gatherHybridizationDataGroupNames(final int firstDataSetId) {
        final List<String> hybDataGroupNames = new ArrayList<String>();
        final String sqlHDG = HYB_DATA_GROUP_NAME_QUERY;

        getJdbcTemplate().query(sqlHDG, new Object[]{firstDataSetId}, new RowCallbackHandler() {
            public void processRow(final ResultSet resultSet) throws SQLException {
                resultSet.setFetchSize(DEFAULT_FETCHSIZE);
                //get GROUP_COLUMN_NAME that is 1
                hybDataGroupNames.add(resultSet.getString(1));
            }
        });

        return hybDataGroupNames;
    }



    public int getMinExpectedRowsToUseHintQuery() {
        return minExpectedRowsToUseHintQuery;
    }

    public void setMinExpectedRowsToUseHintQuery(final int minExpectedRowsToUseHintQuery) {
        this.minExpectedRowsToUseHintQuery = minExpectedRowsToUseHintQuery;
    }

    public void setSourceFileTypeFinder(SourceFileTypeFinder sourceFileTypeFinder) {
        this.sourceFileTypeFinder = sourceFileTypeFinder;
    }
}
