/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.SQLProcessingCleaner;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSetLevelTwoThree;
import org.apache.log4j.Level;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gov.nih.nci.ncicb.tcga.dcc.ConstantValues.ARCHIVE_AVAILABLE;
import static gov.nih.nci.ncicb.tcga.dcc.ConstantValues.ARCHIVE_IN_REVIEW;
import static gov.nih.nci.ncicb.tcga.dcc.ConstantValues.PIPE_SYMBOL;

/**
 * Gets the list of data sets that we expect to get (because of the BCR records) or because of archives we have processed.
 * For CGCC data only.
 *
 * @author HickeyE
 * @version $id$
 */
public class DAMQueriesCGCCLevelTwoThreeList extends DAMBaseQueriesProcessor {

    private ProcessLogger logger = new ProcessLogger();
    private static final String CGCC_CENTER_TYPE = "CGCC";
    static final String INITIAL_LIST_SQL = "select distinct bb.built_barcode as barcode, " +
            "bb.project_code || '-' || bb.tss_code || '-' || bb.participant_code || '-' || bb.sample_type_code as sample, " +
            "ai.center_id as center_id, " +
            "pi.base_data_type_id as platform_type_id, dt.sort_order, ai.platform_id, ai.deploy_status as availability, " +
            "v.identifiable, ai.date_added, ai.archive_id, pi.platform_alias, " +
            "c.domain_name, pi.platform_name, ai.serial_index, ai.revision " +
            "from shipped_biospecimen_breakdown bb, archive_info ai, file_to_archive fa, " +
            "shipped_biospecimen_file bf, file_info f, data_type dt, " +
            "data_visibility dv, visibility v, center c, platform pi, disease d " +
            "where " +
            "c.center_id = ai.center_id " +
            "and pi.platform_id = ai.platform_id " +
            "and d.disease_abbreviation = ? " +
            "and d.disease_id = ai.disease_id " +
            "and bb.shipped_biospecimen_id = bf.shipped_biospecimen_id " +
            "and bf.file_id = f.file_id " +
            "and fa.file_id=f.file_id and fa.archive_id = ai.archive_id " +
            "and pi.base_data_type_id = dt.data_type_id " +
            "and v.visibility_id = dv.visibility_id " +
            "and dv.data_type_id = dt.data_type_id " +
            "and f.level_number = ? " +
            "and ai.is_latest = 1 " +
            "and bb.is_viewable = 1" +
            "and pi.center_type_code='" + CGCC_CENTER_TYPE + "' " +
            "and dv.level_number = ? " +
            "and bb.is_control = ?";
    
    static final String INITIAL_LIST_SQL_PER_DATATYPE =
            INITIAL_LIST_SQL + " and pi.base_data_type_id = ? ";

    List<DataSet> buildInitialList(final String diseaseType,
                                   final int dataLevel, final boolean forControls) throws DataAccessMatrixQueries.DAMQueriesException {
        return buildInitialList(diseaseType, dataLevel, forControls, null);
    }

    List<DataSet> buildInitialList(final String diseaseType,
                                   final int dataLevel, final boolean forControls,
                                   final String platformType) throws DataAccessMatrixQueries.DAMQueriesException {
        logger.logToLogger(Level.DEBUG, ">>DAMQueriesLevelTwoThreeList.buildInitialList() diseaseType=" + diseaseType + ",dataLevel=" + dataLevel + ", platformType=" + platformType);
        Map<String, Integer> barcodeToBatch = getBarcodeBatches();
        final List<DataSet> dataSetList = new ArrayList<DataSet>();
        Map<String, DataSetLevelTwoThree> createdDataSets = new HashMap<String, DataSetLevelTwoThree>();
        // get BCR datasets
        getDataSets(createdDataSets, diseaseType, dataLevel, barcodeToBatch, platformType,
                forControls ? Control.TRUE.value() : Control.FALSE.value());
        dataSetList.addAll(createdDataSets.values());
        logger.logToLogger(Level.DEBUG, "<<DAMQueriesLevelTwoThreeList.buildInitialList()");
        return dataSetList;
    }

    protected void getDataSets(final Map<String, DataSetLevelTwoThree> createdDataSets, final String diseaseType,
                               final int dataLevel, final Map<String, Integer> barcodeToBatch,
                               final String platformType, final int isControl) throws DataAccessMatrixQueries.DAMQueriesException {
        String sql;
        if (platformType == null) {
            sql = INITIAL_LIST_SQL;
        } else {
            sql = INITIAL_LIST_SQL_PER_DATATYPE;
        }
        ResultSet rs = null;
        PreparedStatement statement = null;
        final Connection conn = getConnection();
        try {
            //noinspection JDBCResourceOpenedButNotSafelyClosed
            statement = conn.prepareStatement(sql);
            statement.setString(1, diseaseType);
            statement.setInt(2, dataLevel);
            statement.setInt(3, dataLevel);
            statement.setInt(4, isControl);
            if (platformType != null) {
                statement.setString(5, platformType);
            }
            logger.logToLogger(Level.DEBUG, "executing query for getAllDataSetsForDiseaseType(): " + System.currentTimeMillis());
            statement.setFetchSize(1000); //TODO: inject
            rs = statement.executeQuery();
            logger.logToLogger(Level.DEBUG, "finished executing query for getAllDataSetsForDiseaseType(): " + System.currentTimeMillis());
            while (rs.next()) {
                // did not change the Strings below because they are table names or aliases that may change in this particular class.
                final String platformTypeId = rs.getString("platform_type_id");
                final int sortOrder = rs.getInt("sort_order");
                final String center = rs.getString("center_id");
                final String platform = rs.getString("platform_id");
                final String level = Integer.toString(dataLevel);
                final String sample = rs.getString("sample");
                final int serialIndex = rs.getInt("serial_index");
                final int revision = rs.getInt("revision");
                final String barcode = rs.getString("barcode");
                String bcrBatch = ConstantValues.UNCLASSIFIED_BATCH;
                if (barcodeToBatch.get(sample) != null) {
                    bcrBatch = "Batch " + String.valueOf(barcodeToBatch.get(sample));
                }
                final boolean isProtected = rs.getInt("identifiable") == 1;
                final String platformName = rs.getString("platform_name");
                final String rsAvail = rs.getString("availability");
                final int archiveId = rs.getInt("archive_id");
                final String key = platformTypeId + PIPE_SYMBOL + center + PIPE_SYMBOL + platform + PIPE_SYMBOL + level +
                        PIPE_SYMBOL + sample + PIPE_SYMBOL + serialIndex + PIPE_SYMBOL + revision;
                DataSetLevelTwoThree dataSet23 = createdDataSets.get(key);
                if (dataSet23 != null) {
                    if (rsAvail.equals(ARCHIVE_AVAILABLE) || rsAvail.equals(ARCHIVE_IN_REVIEW)) {
                        if (!dataSet23.isProtected() == isProtected) {
                            throw new DataAccessMatrixQueries.DAMQueriesException("Inconsistant Access Level for " + barcode);
                        }
                        if (rsAvail.equals(ARCHIVE_AVAILABLE) && !dataSet23.getAvailability().equals(DataAccessMatrixQueries.AVAILABILITY_AVAILABLE)) {
                            throw new DataAccessMatrixQueries.DAMQueriesException("Inconsistent Availability for " + barcode);
                        } else if (rsAvail.equals(ARCHIVE_IN_REVIEW) && !dataSet23.getAvailability().equals(DataAccessMatrixQueries.AVAILABILITY_PENDING)) {
                            throw new DataAccessMatrixQueries.DAMQueriesException("Inconsistent Availability for " + barcode);
                        }

                        // new barcode for existing dataset
                        if (!dataSet23.getBarcodes().contains(barcode)) {
                            dataSet23.getBarcodes().add(barcode);
                        }
                        // if existing data set has "Unclassified" batch and this new set does not, change the batch
                        if (dataSet23.getBatch().equals(ConstantValues.UNCLASSIFIED_BATCH) && !bcrBatch.equals(ConstantValues.UNCLASSIFIED_BATCH)) {
                            dataSet23.setBatch(bcrBatch);
                        } else if (bcrBatch.equals(ConstantValues.UNCLASSIFIED_BATCH)) {
                            // do nothing, because the existing dataset has the correct batch number
                        } else if (!dataSet23.getBatch().equalsIgnoreCase(bcrBatch)) {
                            throw new DataAccessMatrixQueries.DAMQueriesException("Inconsistent BCR batch for " + barcode + " (" + bcrBatch + ") " + " vs " + dataSet23.getBarcodes() + "(" + dataSet23.getBatch() + ")");
                        }
                    } // if new barcode archive not Available or In Review, ignore it
                } else {
                    //new dataset
                    dataSet23 = new DataSetLevelTwoThree();
                    dataSet23.setDiseaseType( diseaseType );
                    dataSet23.setArchiveId(archiveId);
                    dataSet23.setPlatformTypeId(platformTypeId);
                    dataSet23.setPlatformTypeSortOrder(sortOrder);
                    dataSet23.setCenterId(center);
                    dataSet23.setLevel(level);
                    dataSet23.setSample(sample);
                    dataSet23.setBatch(bcrBatch);
                    dataSet23.setPlatformId(platform);
                    dataSet23.setPlatformName(platformName);
                    dataSet23.setPlatformAlias(rs.getString("platform_alias"));
                    dataSet23.setProtected(isProtected);
                    dataSet23.setCenterName(rs.getString("domain_name"));
                    dataSet23.setDataDepositBaseName(rs.getString("domain_name") + "_" + diseaseType + "." +
                            platformName);
                    dataSet23.setDataDepositBatch(serialIndex);
                    dataSet23.setDataRevision(revision);
                    dataSet23.setBarcodes(new ArrayList<String>());
                    dataSet23.getBarcodes().add(barcode);
                    final String availability;
                    if (rsAvail.equals(ARCHIVE_AVAILABLE)) {
                        availability = DataAccessMatrixQueries.AVAILABILITY_AVAILABLE;
                    } else if (rsAvail.equals(ARCHIVE_IN_REVIEW)) {
                        availability = DataAccessMatrixQueries.AVAILABILITY_PENDING;
                    } else {
                        // set to null to mean we want to ignore this dataset
                        availability = null;
                    }
                    
                    if (availability != null) {
                        dataSet23.setAvailability(availability);
                        Timestamp timestamp = rs.getTimestamp("date_added");
                        if (timestamp != null) {
                            dataSet23.setDateAdded(new Date(timestamp.getTime()));
                        }
                        createdDataSets.put(key, dataSet23);
                    }
                }
            }
        }
        catch (SQLException ex) {
            logger.logToLogger(Level.ERROR, ProcessLogger.stackTracePrinter(ex));
            throw new DataAccessMatrixQueries.DAMQueriesException(ex);
        }
        finally {
            SQLProcessingCleaner.cleanUpResultSet(rs);
            SQLProcessingCleaner.cleanUpStatement(statement);
            SQLProcessingCleaner.cleanUpConnection(conn);
        }
    }
}
