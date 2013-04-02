/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.aspect.cache.Cached;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.PendingUUID;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.PendingUUIDDAO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO layer implementation for pending UUIDs.
 *
 * @author Stan Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 */

@Repository
public class PendingUUIDDAOImpl implements PendingUUIDDAO {

    private static final String PENDIND_UUID_INSERT_QUERY = "insert into PENDING_UUID (PENDING_UUID_ID,UUID, BCR, CENTER, SHIP_DATE, PLATE_ID, BATCH_NUMBER," +
            " PLATE_COORDINATE, BARCODE, SAMPLE_TYPE, ANALYTE_TYPE," +
            " PORTION_NUMBER, VIAL_NUMBER, ITEM_TYPE, DCC_RECEIVED_DATE) " +
            " values(PENDING_UID_SEQ.nextval,LOWER(?), ?, ?, ? , ? , ? , ? , ? , ? ,? ,?, ?, ?, ?) ";


    private static final String PENDING_UUID_SELECT_QUERY =
            "select UUID, BCR, CENTER, SHIP_DATE, PLATE_ID, BATCH_NUMBER," +
                    " PLATE_COORDINATE, BARCODE,SAMPLE_TYPE, ANALYTE_TYPE," +
                    " PORTION_NUMBER, VIAL_NUMBER,ITEM_TYPE, DCC_RECEIVED_DATE from PENDING_UUID ";

    private static final String PENDING_UUID_REPORT_QUERY =
            "select UUID, BCR, CENTER, SHIP_DATE, PLATE_ID, BATCH_NUMBER," +
                    " PLATE_COORDINATE, BARCODE,SAMPLE_TYPE, ANALYTE_TYPE," +
                    " PORTION_NUMBER, VIAL_NUMBER,ITEM_TYPE, DCC_RECEIVED_DATE " +
                    "from PENDING_UUID where DCC_RECEIVED_DATE IS NULL " +
                    "order by ship_date desc, bcr, center, plate_id, plate_coordinate";

    private static final String VALID_CENTER_QUERY = "select count(*) from center_to_bcr_center where bcr_center_id = ?";
    private static final String VALID_BATCH_NUMBER_QUERY = "select count(batch_id) from batch_number_assignment where batch_id = ?";
    private static final String VALID_SAMPLE_TYPE_QUERY = "select count(*) from sample_type where sample_type_code = ?";
    private static final String VALID_ANALYTE_TYPE_QUERY = "select count(*) from portion_analyte where portion_analyte_code = ?";
    private static final String SINGLE_PENDING_UUID_SELECT_QUERY = PENDING_UUID_SELECT_QUERY + " where uuid = LOWER(?)";
    private static final String ALREADY_PENDING_UUID_QUERY = "select count(*) from pending_uuid where UUID = LOWER(?)";
    private static final String ALREADY_RECEIVED_UUID_QUERY = "select count(*) from shipped_biospecimen where UUID = LOWER(?)";
    private static final String ALREADY_PENDING_BARCODE_QUERY = "select count(*) from pending_uuid where barcode = ?";
    private static final String ALREADY_RECEIVED_BARCODE_QUERY = "select count(*) from shipped_biospecimen where built_barcode = ?";
    private static final String PENDING_UUID_DELETE_QUERY = "delete from PENDING_UUID where uuid=LOWER(?)";

    private static final int batchsize = 1000;
    private static List<Center> centers;

    private JdbcTemplate jdbcTemplate;

    @Resource(name = "dataReportsDataSource")
    public void setDataSource(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Autowired
    private CenterQueries centerQueries;

    @PostConstruct
    private void initQueriesList() {
        setCenters(centerQueries.getRealCenterList());
    }

    @Override
    public void insertPendingUUID(final PendingUUID pendingUUID) {
        // since we're using merge, insert will just upsert
        if (pendingUUID != null) {
            upsertPendingUUID(pendingUUID);
        }
    }

    @Override
    public int deletePendingUUID(final String uuid) {
        return jdbcTemplate.update(PENDING_UUID_DELETE_QUERY, uuid);
    }

    @Override
    @Cached
    public List<PendingUUID> getAllPendingUUIDs() {
        return jdbcTemplate.query(PENDING_UUID_REPORT_QUERY, pendingUUIDRowMapper);
    }

    public static String getCenterDisplayText(final String centerId) {
        for (final Center center : centers) {
            if (centerId != null && centerId.equals(center.getBcrCenterId())) {
                return center.getCenterDisplayText();
            }
        }
        return null;
    }

    @Override
    public PendingUUID getPendingUuid(final String uuid) {
        try {
            return jdbcTemplate.queryForObject(SINGLE_PENDING_UUID_SELECT_QUERY, pendingUUIDRowMapper, uuid);
        } catch (final EmptyResultDataAccessException e) {
            return null; // No result
        }
    }

    @Override
    public List<PendingUUID> getPendingUUIDs(final List<String> uuids) {
        List<PendingUUID> result = new ArrayList<PendingUUID>();
        if (uuids != null && uuids.size() > 0) {
            final String sql = new StringBuffer(PENDING_UUID_SELECT_QUERY)
                    .append(" where uuid in (")
                    .append(StringUtil.createPlaceHolderString(uuids.size(), StringUtil.CaseSensitivity.LOWER_CASE))
                    .append(")")
                    .toString();
            result = jdbcTemplate.query(sql, pendingUUIDRowMapper, uuids.toArray());
        }
        return result;
    }

    // upserts pending UUID
    private void upsertPendingUUID(final PendingUUID pendingUUID) {
        if (pendingUUID != null) {
            final List<Object> mergeElements = new ArrayList<Object>();
            mergeElements.addAll(pendingUUIDToArray(pendingUUID));
            jdbcTemplate.update(PENDIND_UUID_INSERT_QUERY, mergeElements.toArray());
        }
    }

    // converts a pending UUID to a collection
    private List<Object> pendingUUIDToArray(final PendingUUID pendingUUID) {
        final ArrayList<Object> pendingUUIDList = new ArrayList<Object>();
        if (pendingUUID != null) {
            if (StringUtils.isNotEmpty(pendingUUID.getUuid())) {
                pendingUUIDList.add(pendingUUID.getUuid());
            } else {
                pendingUUIDList.add(null);
            }
            pendingUUIDList.add(pendingUUID.getBcr());

            if (StringUtils.isNotEmpty(pendingUUID.getCenter())) {
                pendingUUIDList.add(pendingUUID.getCenter());
            } else {
                pendingUUIDList.add(null);
            }

            pendingUUIDList.add(pendingUUID.getShippedDate());

            if (StringUtils.isNotEmpty(pendingUUID.getPlateId())) {
                pendingUUIDList.add(pendingUUID.getPlateId());
            } else {
                pendingUUIDList.add(null);
            }
            pendingUUIDList.add(pendingUUID.getBatchNumber());

            if (StringUtils.isNotEmpty(pendingUUID.getPlateCoordinate())) {
                pendingUUIDList.add(pendingUUID.getPlateCoordinate());
            } else {
                pendingUUIDList.add(null);
            }
            if (StringUtils.isNotEmpty(pendingUUID.getBcrAliquotBarcode())) {
                pendingUUIDList.add(pendingUUID.getBcrAliquotBarcode());
            } else {
                pendingUUIDList.add(null);
            }
            if (StringUtils.isNotEmpty(pendingUUID.getSampleType())) {
                pendingUUIDList.add(pendingUUID.getSampleType());
            } else {
                pendingUUIDList.add(null);
            }
            if (StringUtils.isNotEmpty(pendingUUID.getAnalyteType())) {
                pendingUUIDList.add(pendingUUID.getAnalyteType());
            } else {
                pendingUUIDList.add(null);
            }
            if (StringUtils.isNotEmpty(pendingUUID.getPortionNumber())) {
                pendingUUIDList.add(pendingUUID.getPortionNumber());
            } else {
                pendingUUIDList.add(null);
            }
            if (StringUtils.isNotEmpty(pendingUUID.getVialNumber())) {
                pendingUUIDList.add(pendingUUID.getVialNumber());
            } else {
                pendingUUIDList.add(null);
            }
            if (StringUtils.isNotEmpty(pendingUUID.getItemType())) {
                pendingUUIDList.add(pendingUUID.getItemType());
            } else {
                pendingUUIDList.add(null);
            }
            pendingUUIDList.add(pendingUUID.getDccReceivedDate());
        }
        return pendingUUIDList;
    }

    public static final ParameterizedRowMapper<PendingUUID> pendingUUIDRowMapper =
            new ParameterizedRowMapper<PendingUUID>() {
                public PendingUUID mapRow(ResultSet resultSet, int i) throws SQLException {
                    final PendingUUID pendingUUID = new PendingUUID();
                    pendingUUID.setBcr(resultSet.getString("BCR"));
                    pendingUUID.setCenter(getCenterDisplayText(resultSet.getString("CENTER")));
                    pendingUUID.setShippedDate(resultSet.getTimestamp("SHIP_DATE"));
                    pendingUUID.setPlateId(resultSet.getString("PLATE_ID"));
                    pendingUUID.setBatchNumber(resultSet.getString("BATCH_NUMBER"));
                    pendingUUID.setPlateCoordinate(resultSet.getString("PLATE_COORDINATE"));
                    pendingUUID.setUuid(resultSet.getString("UUID"));
                    pendingUUID.setBcrAliquotBarcode(resultSet.getString("BARCODE"));
                    pendingUUID.setSampleType(resultSet.getString("SAMPLE_TYPE"));
                    pendingUUID.setAnalyteType(resultSet.getString("ANALYTE_TYPE"));
                    pendingUUID.setPortionNumber(resultSet.getString("PORTION_NUMBER"));
                    pendingUUID.setVialNumber(resultSet.getString("VIAL_NUMBER"));
                    pendingUUID.setItemType(resultSet.getString("ITEM_TYPE"));
                    pendingUUID.setDccReceivedDate(resultSet.getTimestamp("DCC_RECEIVED_DATE"));
                    return pendingUUID;
                }
            };

    @Override
    public void insertPendingUUIDList(final List<PendingUUID> pendingUUIDList) {
        final List<Object[]> updateBatch = new ArrayList<Object[]>();
        if (pendingUUIDList != null) {
            for (final PendingUUID pendingUUID : pendingUUIDList) {
                final List<Object> mergeElements = new ArrayList<Object>();
                mergeElements.addAll(pendingUUIDToArray(pendingUUID));
                updateBatch.add(mergeElements.toArray());
                if (updateBatch.size() == batchsize) {
                    insertPendingUUIDs(updateBatch);
                    updateBatch.clear();
                }
            }
            // write off the remaining records
            if (updateBatch.size() > 0) {
                insertPendingUUIDs(updateBatch);
                updateBatch.clear();
            }
        }
    }

    private void insertPendingUUIDs(final List<Object[]> pendingUUIDs) {
        (new SimpleJdbcTemplate(jdbcTemplate.getDataSource()))
                .batchUpdate(PENDIND_UUID_INSERT_QUERY, pendingUUIDs);
    }

    @Override
    public boolean alreadyReceivedUUID(final String uuid) {
        if (StringUtils.isNotEmpty(uuid)) {
            int numberOfRecords = jdbcTemplate.queryForInt(ALREADY_RECEIVED_UUID_QUERY, uuid);
            return numberOfRecords > 0;
        } else {
            return false;
        }
    }

    @Override
    public boolean alreadyPendingUUID(final String uuid) {
        if (StringUtils.isNotEmpty(uuid)) {
            int numberOfRecords = jdbcTemplate.queryForInt(ALREADY_PENDING_UUID_QUERY, uuid);
            return numberOfRecords > 0;
        } else {
            return false;
        }
    }

    @Override
    public boolean alreadyReceivedBarcode(final String uuid) {
        if (StringUtils.isNotEmpty(uuid)) {
            int numberOfRecords = jdbcTemplate.queryForInt(ALREADY_RECEIVED_BARCODE_QUERY, uuid);
            return numberOfRecords > 0;
        } else {
            return false;
        }
    }

    @Override
    public boolean alreadyPendingBarcode(final String uuid) {
        if (StringUtils.isNotEmpty(uuid)) {
            int numberOfRecords = jdbcTemplate.queryForInt(ALREADY_PENDING_BARCODE_QUERY, uuid);
            return numberOfRecords > 0;
        } else {
            return false;
        }
    }

    @Override
    public boolean isValidCenter(final String bcrCenterId) {
        int numberCenters = jdbcTemplate.queryForInt(VALID_CENTER_QUERY, bcrCenterId);
        return numberCenters > 0;
    }

    @Override
    public boolean isValidBatchNumber(final String batchNumber) {
        int numberBatch = jdbcTemplate.queryForInt(VALID_BATCH_NUMBER_QUERY, batchNumber);
        return numberBatch > 0;
    }

    @Override
    public boolean isValidSampleType(final String sampleType) {
        int numberSampleTypes = jdbcTemplate.queryForInt(VALID_SAMPLE_TYPE_QUERY, sampleType);
        return numberSampleTypes > 0;
    }

    @Override
    public boolean isValidAnalyteType(String analyteType) {
        int numberAnalyteTypes = jdbcTemplate.queryForInt(VALID_ANALYTE_TYPE_QUERY, analyteType);
        return numberAnalyteTypes > 0;
    }

    public void setCenterQueries(CenterQueries centerQueries) {
        this.centerQueries = centerQueries;
    }

    public void setCenters(final List<Center> centers) {
        this.centers = centers;
    }
}
