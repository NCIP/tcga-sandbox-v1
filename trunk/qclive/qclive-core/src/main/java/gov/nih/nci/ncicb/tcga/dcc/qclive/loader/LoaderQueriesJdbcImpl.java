/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.loader;


import gov.nih.nci.ncicb.tcga.dcc.common.dao.BaseQueriesProcessor;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLoggerI;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.LoaderQueriesException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to load level 2 data to db
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class LoaderQueriesJdbcImpl extends BaseQueriesProcessor implements LoaderQueries {
    private static final String HYBRIDIZATION_VALUE_INSERT = "insert into hybridization_value (hybridization_value_id, platform_id, hybridization_ref_id, hybridization_data_group_id, probe_id, value) values (HYB_VALUE_ID_SEQ.NEXTVAL,?,?,?,?,?)";

    private ProcessLoggerI logger;

    public void setLogger(final ProcessLoggerI logger) {
        this.logger = logger;
    }

    public long insertExperiment(
            final String baseName, final int dataDepositBatch, final int dataRevision, final long centerId,
            final long platformId)
            throws LoaderQueriesException {
        try {
            final SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(getDataSource());
            long experimentId = getNextSequenceNumber("EXPERIMENT_experiment_id_SEQ");
            jdbcTemplate.update("insert into experiment (base_name, experiment_id, data_deposit_batch, data_revision, center_id, platform_id) values (?,?,?,?,?,?)",
                    baseName, experimentId, dataDepositBatch, dataRevision, centerId, platformId);
            return experimentId;
        } catch (DataAccessException e) {
            logger.logError(e);
            throw new LoaderQueriesException(e);
        }
    }

    public void insertDataSetFile(final long datasetId, final String filename, long fileInfoId)
            throws LoaderQueriesException {
        try {
            final SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(getDataSource());
            jdbcTemplate.update("insert into data_set_file(data_set_file_id, data_set_id, file_name, load_start_date, is_loaded, file_id) values(data_set_file_seq.nextval, ?, ?, ?, 0,?)",
                    datasetId, filename, new java.sql.Timestamp(System.currentTimeMillis()), fileInfoId);
        } catch (DataAccessException e) {
            logger.logError(e);
            throw new LoaderQueriesException(e);
        }
    }

    public void setDataSetFileLoaded(final long datasetid, final String filename) throws LoaderQueriesException {
        final SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(getDataSource());
        int rowsUpdated = jdbcTemplate.update("update data_set_file set is_loaded=1, load_end_date=? where data_set_id=? and file_name=?",
                new java.sql.Timestamp(System.currentTimeMillis()), datasetid, filename);
        if (rowsUpdated != 1) {
            throw new LoaderQueriesException("Update of data_set_file for data set " + datasetid + " and file " + filename + " failed (" + rowsUpdated + " rows updated)");
        }
    }

    public long lookupExperimentId(final String baseName, final int dataDepositBatch, final int dataRevision) {
        long experimentId;
        try {
            final SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(getDataSource());
            experimentId = jdbcTemplate.queryForLong("select experiment_id from experiment where base_name = ? and data_deposit_batch = ? and data_revision = ?", baseName, dataDepositBatch, dataRevision);
        } catch (EmptyResultDataAccessException e) {
            //means experiment doesn't exist, return -1
            experimentId = -1;
        }
        return experimentId;
    }

    public long insertDataset(
            final long experimentId, final String sourceFileName, final String sourceFileType, final String accessLevel,
            final int dataLevel, final long centerId, final long platformId, final long archiveId)
            throws LoaderQueriesException {
        try {
            final SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(getDataSource());
            long datasetId = getNextSequenceNumber("DATA_SET_data_set_id_SEQ");
            jdbcTemplate.update("insert into data_set (data_set_id, experiment_id, source_file_name, source_file_type, access_level, data_level, center_id, platform_id, archive_id) values (?,?,?,?,?,?,?,?,?)",
                    datasetId, experimentId, sourceFileName, sourceFileType, accessLevel, dataLevel, centerId, platformId, archiveId);
            return datasetId;
        } catch (DataAccessException e) {
            logger.logError(e);
            throw new LoaderQueriesException(e);
        }
    }

    public Map<String, Long> insertHybDataGroups(final long datasetId, final List<String> groupColumnNames)
            throws LoaderQueriesException {
        Map<String, Long> dataGroupMap = new HashMap<String, Long>();
        for (int colidx = 0; colidx < groupColumnNames.size(); colidx++) {
            String columnName = groupColumnNames.get(colidx);
            long dataGroupId = insertHybDataGroup(datasetId, colidx, columnName);
            dataGroupMap.put(columnName, dataGroupId);
        }
        return dataGroupMap;
    }

    //inserts one per column

    long insertHybDataGroup(final long datasetId, final int colidx, final String columnName)
            throws LoaderQueriesException {
        try {
            final SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(getDataSource());
            long hgId = getNextSequenceNumber("HDGroup_hybridization_SEQ");
            jdbcTemplate.update("insert into hybridization_data_group (hybridization_data_group_id, data_set_id, group_column_number, group_column_name) values (?, ?, ?, ?)",
                    hgId, datasetId, colidx, columnName);
            return hgId;
        }
        catch (DataAccessException e) {
            logger.logError(e);
            throw new LoaderQueriesException(e);
        }
    }

    public long insertHybRef(final String bestBarcode, final String sampleName, final long aliquotId, String uuid)
            throws LoaderQueriesException {
        try {
            final SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(getDataSource());
            long hybrefId = getNextSequenceNumber("HYBREF_HYBRIDIZATION_SEQ");
            jdbcTemplate.update("insert into hybridization_ref (hybridization_ref_id, bestbarcode, sample_name, aliquot_id,uuid) values (?,?,?,?,?)",
                    hybrefId, bestBarcode, sampleName, aliquotId, uuid);
            return hybrefId;
        } catch (DataAccessException e) {
//            logger.logError( e );  //don't log because in a multithreaded situation we expect this to happen
            throw new LoaderQueriesException(e);
        }
    }

    public long lookupHybRefId(final String bestbarcode) {
        long hybrefId = -1;
        try {
            final SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(getDataSource());
            hybrefId = jdbcTemplate.queryForLong("select hybridization_ref_id from hybridization_ref where bestBarcode = ?", bestbarcode);
        } catch (EmptyResultDataAccessException e) {
            //doesn't exist, return -1
        }
        return hybrefId;
    }

    public long insertHybRefDataset(
            final long hybridizationRefId, final long dataSetId, final String hybridizationRefName)
            throws LoaderQueriesException {
        try {
            final SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(getDataSource());
            long hybrefDatasetId = getNextSequenceNumber("HYBREF_DATASET_SEQ");
            jdbcTemplate.update("insert into hybrid_ref_data_set (hybref_dataset_id, hybridization_ref_id, data_set_id, hybridization_ref_name) values (?,?,?,?)",
                    hybrefDatasetId, hybridizationRefId, dataSetId, hybridizationRefName);
            return hybrefDatasetId;
        } catch (DataAccessException e) {
            logger.logError(e);
            throw new LoaderQueriesException(e);
        }
    }


    public void insertHybridizationValue(
            final long platformId, final long hybRefId, final long hybDataGroupId, final long probeId,
            final String value)
            throws LoaderQueriesException {
        try {
            final SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(getDataSource());
            jdbcTemplate.update(HYBRIDIZATION_VALUE_INSERT,
                    platformId, hybRefId, hybDataGroupId, probeId, value);
        } catch (DataAccessException e) {
            logger.logError(e);
            throw new LoaderQueriesException(e);
        }
    }


    public void insertHybridizationValues(final List<Object[]> batchedArguments) throws LoaderQueriesException {
        SimpleJdbcTemplate template = new SimpleJdbcTemplate(getDataSource());
        try {
            template.batchUpdate(HYBRIDIZATION_VALUE_INSERT, batchedArguments);
        } catch (DataAccessException e) {
            logger.logError(e);
            throw new LoaderQueriesException(e);
        }
    }

    public int lookupCenterId(final String centerName, int platformId)
            throws LoaderQueriesException {
        try {
            final SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(getDataSource());
            return jdbcTemplate.queryForInt("select center_id from center, platform " +
                    "where platform_id = ? and domain_name = ? and platform.center_type_code = center.center_type_code",
                    platformId, centerName);
        } catch (DataAccessException e) {
            logger.logError(e);
            throw new LoaderQueriesException(e);
        }
    }

    public int lookupPlatformId(final String platformName) throws LoaderQueriesException {
        try {
            final SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(getDataSource());
            return jdbcTemplate.queryForInt("select platform_id from platform where platform_name = ?", platformName);
        } catch (DataAccessException e) {
            throw new LoaderQueriesException(e);
        }
    }

    public Map<String, Integer> downloadProbesForPlatform(final int platformId)
            throws LoaderQueriesException {
        try {
            final JdbcTemplate jdbcTemplate = getJdbcTemplate();
            final Map<String, Integer> probes = new HashMap<String, Integer>();
            final Object[] args = new Object[1];
            args[0] = platformId;
            jdbcTemplate.query("select probe_name, probe_id from probe where platform_id = ?", args, new RowCallbackHandler() {
                public void processRow(final ResultSet resultSet) throws SQLException {
                    probes.put(resultSet.getString(1), resultSet.getInt(2));
                }
            });
            return probes;
        } catch (DataAccessException e) {
            logger.logError(e);
            throw new LoaderQueriesException(e);
        }
    }

    public boolean hybRefDatasetExists(final long hybrefId, final long datasetId) {
        final SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(getDataSource());
        int count = jdbcTemplate.queryForInt("select count(*) from hybrid_ref_data_set where hybridization_ref_id=? and data_set_id=?", hybrefId, datasetId);
        return count > 0;
    }

    public TransactionTemplate getTransactionOperations() {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(getDataSource());
        return new TransactionTemplate(transactionManager);
    }

    public void setDataSetLoaded(final long datasetId) throws LoaderQueriesException {
        final SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(getDataSource());
        int rowsUpdated = jdbcTemplate.update("update data_set set load_complete=1 where data_set_id=?", datasetId);
        if (rowsUpdated != 1) {
            throw new LoaderQueriesException("Update of data_set " + datasetId + " failed (" + rowsUpdated + " rows updated)");
        }
    }

    public Map<String, Long> lookupFileInfoData(final long archiveId)
            throws LoaderQueriesException {
        final JdbcTemplate jdbcTemplate = getJdbcTemplate();
        final Map<String, Long> files = new HashMap<String, Long>();
        final Object[] args = new Object[1];
        args[0] = archiveId;
        try {
            jdbcTemplate.query("select f.file_name, f.file_id from file_info f, file_to_archive fa where f.file_id = fa.file_id and fa.archive_id = ?", args, new RowCallbackHandler() {
                public void processRow(final ResultSet resultSet) throws SQLException {
                    files.put(resultSet.getString(1), resultSet.getLong(2));
                }
            });
            return files;
        } catch (DataAccessException e) {
            logger.logError(e);
            throw new LoaderQueriesException(e);
        }
    }
}
