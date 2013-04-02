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
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFileLevelTwoThree;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetLevelTwoThree;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import java.io.IOException;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jessica Chen
 * @version $id$
 */
public abstract class DAMQueriesLevel3 extends DAMQueriesCGCCLevelTwoAndThree implements DataAccessMatrixQueries {
    private static final String TMP_HYBREF_ID_TABLE = "tmphybref";
    private static final String TEMP_INSERT_HYBREF_ID_SQL = "insert into tmphybref (hybridization_ref_id ) values(?)";

    private static final int ESTIMATED_AVERAGE_LINE_SIZE = 29;

    /**
     * Gets the name of the table in the database where the data for this type of level 3 is stored.
     * @return table name
     */
    protected abstract String getValueTable();

    /**
     * Gets the list of allowed datatypes for this type of level 3.
     * @return list of datatype names
     */
    protected abstract List<String> getAllowedDatatypes();

    /**
     * Calls parent method and then sets all data sets to not protected since this is level 3.
     * Note this now also calls the superclass getFileInfoForSelectedDataSets for each data set,
     * and then saved those data file objects in the data set.
     *
     * @param diseaseType the disease type
     *
     * @return a list of data sets for this disease type
     */
    @Override
    public List<DataSet> getDataSetsForDiseaseType(final String diseaseType) throws DAMQueriesException {
        return getDataSets(diseaseType, false);
    }

    @Override
    public List<DataSet> getDataSetsForControls(final List<String> diseaseTypes) throws DAMQueriesException {

        final List<DataSet> result = new ArrayList<DataSet>();

        for(final String diseaseType : diseaseTypes) {
            DiseaseContextHolder.setDisease(diseaseType);
            result.addAll(getDataSets(diseaseType, true));
        }

        return result;
    }

    /**
     * Return a {@link List} of {@link DataSet} for the given disease type.
     *
     * @param diseaseType the disease type
     * @param forControls <code>true</code> to only include {@link DataSet} for control samples, <code>false</code> otherwise
     * @return a {@link List} of {@link DataSet} for the given disease type
     * @throws DAMQueriesException
     */
    private List<DataSet> getDataSets(final String diseaseType,
                                      final boolean forControls) throws DAMQueriesException {

        List<DataSet> datasets = super.getDataSetsForDiseaseType(diseaseType, forControls);
        final List<DataSet> level3DataSets = new ArrayList<DataSet>();
        final Map<String, DataSet> experimentIdAndBarcodeToDataSet = new HashMap<String, DataSet>();
        for (final DataSet dataset : datasets) {
            if (shouldHandleDataSet(dataset)) {
                dataset.setProtected(false);

                level3DataSets.add(dataset);
                for (final String barcode : dataset.getBarcodes()) {
                    experimentIdAndBarcodeToDataSet.put(((DataSetLevelTwoThree)dataset).getExperimentID() + "." + barcode,
                            dataset);
                }
            }
        }

        // call superclass method, which does the db queries to get the info
        List<DataFile> dataFiles = super.getFileInfoForSelectedDataSets(level3DataSets, false);
        for (final DataFile dataFile : dataFiles) {
            DataFileLevelTwoThree dataFileLevelTwoThree = (DataFileLevelTwoThree) dataFile;
            if (dataFileLevelTwoThree.getBarcodes().size() > 0) {
                final DataSet dataSet = experimentIdAndBarcodeToDataSet.get(dataFileLevelTwoThree.getExperimentId() + "." + dataFile.getBarcodes().iterator().next());
                if (dataSet != null) {
                    if (dataSet.getDataFiles() == null) {
                        dataSet.setDataFiles(new ArrayList<DataFile>());
                    }
                    dataSet.getDataFiles().add(dataFile);
                }
            }
        }

        return datasets;
    }

    /**
     * This is overridden here to pull the file beans out of the data sets!
     *
     * @param selectedDataSets the list of selected data sets
     * @param consolidateFiles whether the files should be one per barcode or one per type (with all barcodes in one)
     * @return data files from the data sets
     * @throws DAMQueriesException
     */
    @Override
    public List<DataFile> getFileInfoForSelectedDataSets(final List<DataSet> selectedDataSets,
                                                         final boolean consolidateFiles) throws DAMQueriesException {

        if (consolidateFiles) {
            // if we want to consolidate files, need to make new data file objects (that option is currently disabled in the UI...)
            // so call the superclass method to go to the db
            return super.getFileInfoForSelectedDataSets(selectedDataSets, consolidateFiles);

        } else {
            // otherwise, pull all data files from the selected data sets that are things we should handle
            final List<DataFile> result = new ArrayList<DataFile>();
            final Map<String, List<DataSet>> diseaseToDataSetMap = getDamUtils().groupDataSetsByDisease(selectedDataSets);

            if(diseaseToDataSetMap != null) {
                for(final String diseaseType : diseaseToDataSetMap.keySet()) {

                    final List<DataSet> singleDiseaseDataSets = diseaseToDataSetMap.get(diseaseType);
                    if(singleDiseaseDataSets != null && singleDiseaseDataSets.size() > 0) {

                        setDiseaseInContext(diseaseType);
                        result.addAll(getFileInfoForSelectedDataSetsSingleDisease(singleDiseaseDataSets));
                    }
                }
            }

            return result;
        }
    }

    /**
     * Return a {@link List} of {@link DataFile} for the given {@link DataSet}s
     *
     * Note 1: It is assumed that the given {@link DataSet}s are all for the same disease type
     * Note 2: Files will not be consolidated
     *
     * @param selectedDataSets the {@link DataSet}s to get the info from
     * @return a {@link List} of {@link DataFile} for the given {@link DataSet}s
     */
    private List<DataFile> getFileInfoForSelectedDataSetsSingleDisease(final List<DataSet> selectedDataSets) {

        final List<DataFile> result = new ArrayList<DataFile>();

        if(selectedDataSets != null && selectedDataSets.size() > 0) {

            for (final DataSet dataset : selectedDataSets) {
                if (shouldHandleDataSet(dataset) && dataset.getDataFiles() != null) {
                    result.addAll(dataset.getDataFiles());
                }
            }

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

    protected void estimateFileSizes(final List<DataFile> dataFiles){
        return;
    }

    /**
     * Returns the expected number of lines (including headers) for this file.
     * @param df the data file
     * @return number of lines
     */
    protected long getNumberOfLinesForFile(final DataFileLevelTwoThree df) {
        final Long[] numberOfLines = new Long[1];
        transactionTemplate.execute(new TransactionCallback() {
        public Object doInTransaction(final TransactionStatus transactionStatus) {
            try {
                insertTempHybrefIds(df.getHybRefIds());
                numberOfLines[0] = getJdbcTemplate().queryForLong(getRowCountQuery(),
                            new Object[]{ df.getDataSetsDP().iterator().next()});
            }catch (Throwable e) {
                    throw new DataAccessException(e.getMessage(), e) {};
            }
            return null;
            }
        });
        return numberOfLines[0] + 1; // +1 for the header
    }

    private void insertTempHybrefIds(final Collection<Long> ids) {
        final List<Object[]> valueList = new ArrayList<Object[]>();
        for (final Long id : ids) {
            Object[] data = new Object[1];
            data[0] = id;
            valueList.add(data);
        }
        new SimpleJdbcTemplate(getDataSource()).batchUpdate(TEMP_INSERT_HYBREF_ID_SQL, valueList);
    }

    /**
     * Gets the estimate for the size of one line of this data file.
     * @param dataFile the file
     * @return the estimated size
     */
    protected long getAverageLineSize(final DataFileLevelTwoThree dataFile) {
        // The file size is already calculated. So we don't need average line size
        // to calculate the file size
        return 0l;
    }

    /**
     * Gets initial list of data sets based on DCC database.  Combines list from all of the different level 3 types.
     * @param diseaseType the disease
     * @return list of data sets which may be in the database
     * @throws DataAccessMatrixQueries.DAMQueriesException if an error occurs when querying the dcc database   
     */
    protected List<DataSet> buildInitialList(final String diseaseType, final boolean forControls) throws DataAccessMatrixQueries.DAMQueriesException {
        List<DataSet> ret = new ArrayList<DataSet>();
        for (final String datatype : getAllowedDatatypes()) {
            if (datatype != null) {
                ret.addAll(getDccListQueries().buildInitialList(diseaseType, 3, forControls, datatype));
            }
        }
        return ret;
    }

    protected int getDataLevel() {
        return 3;
    }

    protected String getRowCountQuery() {
        final String valueTable = getValueTable();
        final StringBuilder rowCountQuery = new StringBuilder( "select count(*) from " );
                rowCountQuery.append(valueTable)
                        .append(",")
                        .append(TMP_HYBREF_ID_TABLE)
                        .append(" where ")
                        .append(valueTable)
                        .append(".hybridization_ref_id =")
                        .append(TMP_HYBREF_ID_TABLE)
                        .append(".hybridization_ref_id and ")
                        .append(valueTable)
                        .append(".data_set_id=?");
        return rowCountQuery.toString();
    }

    protected abstract String getRowSizeQuery(DataFileLevelTwoThree dataFile);
      
    protected void generateFile(final DataFileLevelTwoThree dataFile, final Writer writer) throws IOException {
        writer.write(getFileHeader(dataFile));
        final Collection<Long> hybRefIds = dataFile.getHybRefIds();
        final String fileType = dataFile.getFileId();

        final String valueQuery = getValueSql(dataFile.getDataSetsDP().size(), dataFile);
        for (final Long id : hybRefIds) {
            List<Object> params = new ArrayList<Object>();
            params.add(id);
            params.add(fileType);
            params.addAll(dataFile.getDataSetsDP());
            getJdbcTemplate().query(valueQuery, params.toArray(), new RowCallbackHandler() {
                public void processRow(final ResultSet rs) throws SQLException {
                    final String[] valueColumnNames = getValueColumnNames(dataFile);
                    for (int i = 0; i < valueColumnNames.length; i++) {
                        //TODO change this to integers.  Is that even possible here?
                        String value = rs.getString(valueColumnNames[i]);
                        if (value == null || rs.wasNull()) {
                            value = "";
                        }
                        try {
                            writer.write(value);

                            if (i == valueColumnNames.length - 1) {
                                // last one, so newline
                                writer.write("\n");
                            } else {
                                writer.write("\t");
                            }
                        } catch (IOException e) {
                            getLogger().logError(e);
                            throw new DataAccessException(e.getMessage(), e) {};
                        }
                    }
                }
            });
        }
    }

    @Override
    protected boolean shouldGenerateFile(final DataFile df) {
        return super.shouldGenerateFile(df) && getAllowedDatatypes().contains(df.getPlatformTypeId());
    }

    @Override
    protected boolean shouldHandleDataSet(final DataSet dataSet) {
        return super.shouldHandleDataSet(dataSet) && getAllowedDatatypes().contains(dataSet.getPlatformTypeId());
    }


    /**
     * Gets an array of the value column names for this DAO.
     * @return array of names
     * @param dataFile the data file bean the columns are for
     */
    protected abstract String[] getValueColumnNames(DataFileLevelTwoThree dataFile);

    /**
     * Gets the header for the file, given that the file is for the indicated platform type.
     *
     * @param platformTypeId the platform type of the file we are generating
     * @return string header for the file
     */
    protected abstract String getFileHeader(final DataFileLevelTwoThree platformTypeId);

    /**
     * Gets the query to use to get the actual data values for the file.  The number of datasets is given
     * because the query must allow for that many bind variables.
     *
     * @param numDatasets the number of datasets to query on
     * @param dataFile the data file bean the values will be used for
     * @return SQL query for values
     */
    private String getValueSql(final int numDatasets, DataFileLevelTwoThree dataFile) {
        final StringBuilder query = new StringBuilder("SELECT h.bestbarcode");
        for (final String column : getValueColumnNames(dataFile)) {
            if (! column.equalsIgnoreCase("bestbarcode")) {
                query.append(", c.").append(column);
            }
        }
        query.append(" FROM hybridization_ref h, data_set d, ").append(getValueTable()).append(" c ");
        query.append(" WHERE h.hybridization_ref_id = c.hybridization_ref_id ").
                append("AND c.data_set_id = d.data_set_id ").
                append("AND h.hybridization_ref_id = ? ").
                append("AND d.source_file_type = ? ");

        query.append(" AND d.data_set_id in (?");
        for (int i=0; i<numDatasets-1; i++) {
            query.append(", ?");
        }
        query.append(") order by h.bestbarcode ");
        String[] orderByColumns = getValueQueryOrderByColumns();
        for (final String column : orderByColumns) {
            query.append(", c.").append(column);
        }
        return query.toString();
    }

    protected abstract String[] getValueQueryOrderByColumns();

}
