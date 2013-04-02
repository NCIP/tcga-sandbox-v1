package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.Level2DataQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.DataException;
import gov.nih.nci.ncicb.tcga.dcc.common.service.JDBCCallback;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC implementation for Level2 data queries
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class Level2DataQueriesJDBCImpl implements Level2DataQueries {
    private final Log logger = LogFactory.getLog(getClass());
    public static final int DEFAULT_FETCHSIZE = 1000;
    public static final String REPLACE_IN_CLAUSE = "REPLACE_IN_CLAUSE";
    private static final String HINT_PLACE_HOLDER = "HINT_PLACE_HOLDER";
    private static final String DS_TEMP_TABLE_NAME = "TMPDATASET";
    private static final String DS_TEMP_COLUMN_NAME = "DATA_SET_ID";
    private static final String HYB_REF_ID_TEMP_TABLE_NAME = "TMPHYBREF";
    private static final String HYB_REF_ID_TEMP_COLUMN_NAME = "HYBRIDIZATION_REF_ID";
    private static final String HYBRIDIZATION_VALUE_QUERY_HINT = "/*+ FULL(hv) PARALLEL(hv, 4) */";

    private static final String HYB_DATA_GROUP_NAME_QUERY = " select group_column_name, hybridization_data_group_id " +
            " from hybridization_data_group " +
            " where data_set_id = ? " +
            " order by group_column_number";
    private static final String HYB_REF_ID_QUERY = " select hybridization_ref_id " +
            " from hybrid_ref_data_set " +
            " where data_set_id in (" + REPLACE_IN_CLAUSE + ")";

    private static final String BARCODE_FOR_HYBREF_QUERY = " select bestbarcode,hybridization_ref_id " +
            " from hybridization_ref " +
            " where hybridization_ref_id in (" + REPLACE_IN_CLAUSE + ")" +
            " order by bestbarcode";

    private static final String PROBE_CONSTANT_COUNT = " select count(*) " +
            " from probe " +
            " where platform_id=? and chromosome is not null";

    private static final String PROBE_COUNT_QUERY = " select count(*) " +
            " from probe " +
            " where platform_id= ?";

    private static final String DATA_GROUPS_COUNT_QUERY = " select count(hybridization_data_group_id) " +
            " from hybridization_data_group " +
            " where data_set_id = ?";
    private static final String TEMP_INSERT_HYBREF_ID_SQL = "insert into tmphybref (hybridization_ref_id ) values(?)";

    private static final String TEMP_INSERT_DATA_SET_ID_SQL = "insert into tmpdataset (data_set_id ) values(?)";


    /* query to get data for writing to files.  note in clauses are placeholders that must be replaced by appropriate number of question marks!

    THE FOLLOWING BLOCK MUST BE CHANGED CAREFULLY!  WE ARE USING INTEGERS FOR THE COLUMN NAMES SO WE CAN RETRIEVE THE DATA FROM THE DB
    EFFICIENTLY WITHOUT MAKING A CALL TO getColumnIndex() IN THE ORACLE JDBC DRIVER.

    IF YOU ARE GOING TO CHANGE THE SELECT, PLEASE LOOK TO MAKE SURE THE ORDER OF THE COLUMN HAS NOT CHANGED!
    */

    private static final String HYBRIDIZATION_VALUE_QUERY = "select " + HINT_PLACE_HOLDER +
            " distinct p.probe_name, p.chromosome, p.start_position, p.end_position, " +
            " hv.hybridization_ref_id, hg.group_column_name, hv.value " +
            " from hybridization_data_group hg, " +
            " hybridization_value hv, probe p, " +
            HYB_REF_ID_TEMP_TABLE_NAME + " t, " +
            DS_TEMP_TABLE_NAME + " td " +
            " where hg.data_set_id = td." + DS_TEMP_COLUMN_NAME + " " +
            " and hv.hybridization_ref_id=t." + HYB_REF_ID_TEMP_COLUMN_NAME + " " +
            " and hv.platform_id=? " +
            " and hv.hybridization_data_group_id = hg.hybridization_data_group_id " +
            " and p.probe_id=hv.probe_id " +
            " and p.platform_id=? " +
            " order by p.probe_name";

    /*
     END OF QUERY AND INTEGER MAPPING BLOCK
     */
    private static final String GET_EXPERIMENT_SOURCE_FILE_TYPE_QUERY = " select source_file_type " +
            " from data_set " +
            " where experiment_id in (" + REPLACE_IN_CLAUSE + ")";

    private static final String GET_LEVEL2_DATA_SET_IDS_QUERY = " select data_set_id " +
            " from data_set ds ,archive_info ai" +
            " where " +
            " ds.archive_id = ai.archive_id and " +
            " ds.platform_id=? and " +
            " ds.center_id=? and " +
            " ds.source_file_type = ? and " +
            " ds.data_level = '" + ConstantValues.DATA_LEVEL_2 + "' and " +
            " ai.is_latest = '" + ConstantValues.LATEST_ARCHIVE + "'";


    private static final String UPDATE_DATA_SET_DAM_FIELD_QUERY = "update data_set set use_in_dam=1 " +
            "where experiment_id in (" + REPLACE_IN_CLAUSE + ")";

    private SimpleJdbcTemplate simpleJdbcTemplate;
    private TransactionTemplate transactionTemplate;

    public void setDataSource(DataSource dataSource) {
        simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);
        ((JdbcTemplate) simpleJdbcTemplate.getJdbcOperations()).setFetchSize(DEFAULT_FETCHSIZE);
    }

    public TransactionTemplate getTransactionTemplate() {
        return transactionTemplate;
    }

    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    public List<String> getHybridizationDataGroupNames(final long dataSetId) {
        return getJdbcTemplate().query(HYB_DATA_GROUP_NAME_QUERY,
                new ParameterizedRowMapper<String>() {
                    public String mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
                        return resultSet.getString(1);
                    }
                },
                new Object[]{dataSetId});

    }


    public List<Long> getHybridizationRefIds(final List<Long> dataSetIds) {
        return getJdbcTemplate().query(getReplacedInClauseQuery(HYB_REF_ID_QUERY, dataSetIds.size()),
                new ParameterizedRowMapper<Long>() {
                    public Long mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
                        return resultSet.getLong(1);
                    }
                },
                new Object[]{dataSetIds.toArray()});

    }

    public Map<String, Long> getBarcodesForHybrefIds(final List<Long> hybRefIds) {
        // LinkedHashMap is used to maintain the barcodes in the sorted order
        final Map<String, Long> barcodeHybRefIdMap = new LinkedHashMap<String, Long>();
        getJdbcTemplate().query(getReplacedInClauseQuery(BARCODE_FOR_HYBREF_QUERY, hybRefIds.size()),
                new ParameterizedRowMapper<Long>() {
                    public Long mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
                        barcodeHybRefIdMap.put(resultSet.getString(1), resultSet.getLong(2));
                        return resultSet.getLong(2);
                    }
                },
                hybRefIds.toArray());
        return barcodeHybRefIdMap;
    }

    public Integer getProbeCountForValidChromosome(final int platformId) {
        return getJdbcTemplate().queryForInt(PROBE_CONSTANT_COUNT, new Object[]{platformId});
    }

    public Integer getProbeCount(final int platformId) {
        return getJdbcTemplate().queryForInt(PROBE_COUNT_QUERY, new Object[]{platformId});
    }

    public Integer getDataGroupsCount(final long dataSetId) {
        return getJdbcTemplate().queryForInt(DATA_GROUPS_COUNT_QUERY, new Object[]{dataSetId});
    }


    public void getHybridizationValue(final int platformId,
                                      final boolean useHint,
                                      final List<Long> hybRefIds,
                                      final List<Long> dataSetIds,
                                      final List<String> hybDataGroupNames,
                                      final Map<String, Long> barcodesHybRefIdMap,
                                      final boolean willHaveProbeConstants,
                                      final JDBCCallback callback) throws DataException {
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
        final String replaceData = (useHint) ? HYBRIDIZATION_VALUE_QUERY_HINT : "";
        final String query = HYBRIDIZATION_VALUE_QUERY.replace(HINT_PLACE_HOLDER, replaceData);
        final Map<String, String> currentRowValues = new HashMap<String, String>(); // keyed by "hybref_id.data_group_name"
        final String[] lastProbe = new String[]{null, null, null}; // done this way b/c used by inner class

        /*
       Note: this happens within a transaction because we write to a temp table first and then
       query against it to extract the data, and we need to make sure that the temp table insertion and
       subsequent query occur in the same transaction or else the temp table data may not be visible to us.
       (It is a global temp table where each session can only see its own inserted data.)
        */

        transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction(final TransactionStatus transactionStatus) {
                try {
                    insertTempHybrefIds(hybRefIds);
                    insertTempDataSetIds(dataSetIds);

                    ((JdbcTemplate) simpleJdbcTemplate.getJdbcOperations()).query(query, new Integer[]{platformId, platformId}, new RowCallbackHandler() {
                        public void processRow(final ResultSet resultSet) throws SQLException {
                            String currentProbe = resultSet.getString(PROBE_NAME);
                            if (lastProbe[0] != null && !lastProbe[0].equals(currentProbe)) {
                                try {
                                    // this result set is the start of a new row, so write the old one
                                    callback.processData(lastProbe, currentRowValues, hybDataGroupNames, barcodesHybRefIdMap, willHaveProbeConstants);
                                    currentRowValues.clear();
                                } catch (Throwable e) {
                                    logger.error(e);
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

                } catch (Throwable e) {
                    throw new DataAccessException(e.getMessage(), e) {
                    };
                }
                return null;
            }
        });
        // send last row!
        callback.processData(lastProbe, currentRowValues, hybDataGroupNames, barcodesHybRefIdMap, willHaveProbeConstants);
    }

    public List<String> getExperimentSourceFileTypes(final Collection<Long> experimentIds) {
        return getJdbcTemplate().query(getReplacedInClauseQuery(GET_EXPERIMENT_SOURCE_FILE_TYPE_QUERY, experimentIds.size()),
                new ParameterizedRowMapper<String>() {
                    public String mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
                        return resultSet.getString("source_file_type");
                    }
                }, new Object[]{experimentIds.toArray()}
        );

    }

    public List<Long> getLevel2DataSetIds(final int platformId,
                                          final int centerId,
                                          final String sourceFileType
    ) {
        return getJdbcTemplate().query(GET_LEVEL2_DATA_SET_IDS_QUERY,
                new ParameterizedRowMapper<Long>() {
                    public Long mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
                        return resultSet.getLong("data_set_id");
                    }
                },
                new Object[]{platformId,
                        centerId,
                        sourceFileType}
        );
    }

    public int updateDataSetUseInDAMStatus(final Collection<Long> experimentIdList) {
        return getJdbcTemplate().update(getReplacedInClauseQuery(UPDATE_DATA_SET_DAM_FIELD_QUERY, experimentIdList.size()), experimentIdList.toArray());
    }

    private void insertTempHybrefIds(final Collection<Long> ids) {
        final List<Object[]> valueList = new ArrayList<Object[]>();
        for (final Long id : ids) {
            Object[] data = new Object[1];
            data[0] = id;
            valueList.add(data);
        }
        getJdbcTemplate().batchUpdate(TEMP_INSERT_HYBREF_ID_SQL, valueList);
    }

    private void insertTempDataSetIds(final Collection<Long> dataSetIds) {
        final List<Object[]> valueList = new ArrayList<Object[]>();
        for (final Long dataSetId : dataSetIds) {
            Object[] data = new Object[1];
            data[0] = dataSetId;
            valueList.add(data);
        }
        getJdbcTemplate().batchUpdate(TEMP_INSERT_DATA_SET_ID_SQL, valueList);
    }

    private String getReplacedInClauseQuery(final String query, final int maxInClauseParameter) {
        return query.replace(REPLACE_IN_CLAUSE, StringUtil.createPlaceHolderString(maxInClauseParameter));
    }

    private SimpleJdbcTemplate getJdbcTemplate() {
        return simpleJdbcTemplate;
    }
}
