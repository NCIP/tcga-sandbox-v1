/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Barcode;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Duration;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.SearchCriteria;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UuidBarcodeMapping;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO class for UUIDs. Used for adding /updating /getting UUID details
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */

@Repository
public class UUIDDAOImpl implements UUIDDAO {

    private SimpleJdbcTemplate jdbcTemplate;

    @Autowired
    private CenterQueries centerQueries;

    @Autowired
    private UUIDTypeQueries uuidTypeQueries;

    @Autowired
    private CommonBarcodeAndUUIDValidator commonBarcodeAndUUIDValidator;

    private static final String UUID_UPSERT_QUERY =
            " MERGE INTO UUID " +
                    " USING DUAL ON (" +
                    " UUID = ?)" +
                    " WHEN NOT MATCHED THEN " +
                    " insert(UUID, CREATE_DATE, CENTER_ID, GENERATION_METHOD_ID, CREATED_BY)" +
                    " values(?, ?, ?, ?, ?)";

    private static final String SQL_UUID_INSERT = "Insert into UUID " +
            " (UUID, CREATE_DATE, CENTER_ID, GENERATION_METHOD_ID, CREATED_BY)" +
            " values (?, ?, ?, ?, ?)";

    private static final String DISEASE_QUERY = "select DISEASE_ID AS ID, DISEASE_ABBREVIATION AS NAME," +
            " DISEASE_NAME AS DESCRIPTION FROM DISEASE WHERE ACTIVE=1 order by DISEASE_NAME";

    private static final String SQL_INSERT_BARCODE = "INSERT INTO barcode_history (barcode_id,barcode,uuid,disease_id,effective_date,item_type_id) VALUES (?,?,?,?,?,?)";

    private static final String SQL_UPDATE_UUID = "Update UUID Set latest_barcode_id = ? Where uuid = ?";

    private static final String SQL_LATEST_BARCODE_FOR_UUID = "select barcode from uuid, barcode_history " +
            "where uuid.latest_barcode_id=barcode_history.barcode_id and uuid.uuid=lower(?)";
    private List<Tumor> diseases;

    private static final String SELECT_CLAUSE = "SELECT U.UUID, U.CENTER_ID, DOMAIN_NAME, CENTER_TYPE_CODE, " +
            "U.CREATED_BY, U.CREATE_DATE, U.GENERATION_METHOD_ID, U.LATEST_BARCODE_ID, B.BARCODE, D.DISEASE_ABBREVIATION ";
    private static final String FROM_CLAUSE = " FROM UUID U INNER JOIN CENTER C ON U.CENTER_ID = C.CENTER_ID " +
            " LEFT OUTER JOIN BARCODE_HISTORY B ON B.BARCODE_ID = U.LATEST_BARCODE_ID " +
            " LEFT OUTER JOIN DISEASE D ON B.DISEASE_ID = D.DISEASE_ID ";
    private static final String WHERE_CLAUSE = " WHERE 1=1 ";
    private static final String ORDER_BY_CLAUSE = " ORDER BY U.CREATE_DATE DESC ";

    private static final String SQL_UUID_DETAIL = SELECT_CLAUSE + FROM_CLAUSE + " WHERE u.uuid = ? ";

    private static final String SQL_UUID_BARCODE = "Select BARCODE_ID, BARCODE, UUID, DISEASE_ID, EFFECTIVE_DATE, ITEM_TYPE_ID " +
            "from BARCODE_HISTORY where UUID = ?";

    private static final String SQL_BARCODE = "Select BARCODE_ID, BARCODE, UUID, DISEASE_ID, EFFECTIVE_DATE, ITEM_TYPE_ID from BARCODE_HISTORY where BARCODE_ID = ?";

    private static final String SQL_BARCODE_UUID = "Select u.uuid from uuid u, barcode_history bh where bh.barcode = upper(?) and bh.barcode_id = u.latest_barcode_id";

    private static final String SQL_BARCODE_STARTING_WITH = "select barcode_id, barcode, uuid, disease_id, effective_date, item_type_id from barcode_history " +
            "where barcode like upper(?) order by barcode";

    private static final String SQL_UUID_EXISTS_QUERY = "select count(*) from uuid where uuid=lower(?)";

    public static final String REPLACE_UUID = "REPLACE_UUID";
    public static final String QUERY_EXISTING_UUIDS = "select uuid from uuid where uuid in (" + REPLACE_UUID + ")";

    public static final String REPLACE_BARCODE = "REPLACE_BARCODE";
    public static final String QUERY_EXISTING_BARCODES = "select barcode from barcode_history where barcode in (" + REPLACE_BARCODE + ")";

    public static final String BARCODES_FROM_UUIDS_MAPPING_QUERY = "with v as (" + REPLACE_UUID + ") " +
            "select bh.barcode, v.uuid from v left outer join uuid u on v.uuid = u.uuid " +
            "left outer join barcode_history bh on bh.barcode_id = u.latest_barcode_id order by v.uuid";

    public static final String UUIDS_FROM_BARCODES_MAPPING_QUERY = "with v as (" + REPLACE_BARCODE + ") " +
            "select distinct bh.uuid, v.barcode  from v left outer join barcode_history bh on v.barcode = bh.barcode " +
            "order by v.barcode";

    public static final String MERGE_PATIENT_UUID_FILE_ID =
            " merge into participant_uuid_file using dual on (UUID=lower(?) and FILE_ID=?) " +
                    " when not matched then insert (UUID, FILE_ID) values(lower(?), ?)";

    @Resource(name = "uuidDataSource")
    public void setDataSource(final DataSource dataSource) {
        this.jdbcTemplate = new SimpleJdbcTemplate(dataSource);
    }

    /**
     * Adds an entry in the UUID table in database for the given UUID
     *
     * @param uuidDetailList list of UUIDs to be added to the database
     * @return number of rows affected
     */

    @Transactional
    @Secured({"ROLE_UUID_CREATOR"})
    @Override
    public int addUUID(final List<UUIDDetail> uuidDetailList) throws UUIDException {
        String currentUuid = "";
        try {
            for (final UUIDDetail uuidDetail : uuidDetailList) {
                currentUuid = uuidDetail.getUuid();
                jdbcTemplate.update(SQL_UUID_INSERT, currentUuid,
                        uuidDetail.getCreationDate(), uuidDetail.getCenter().getCenterId(),
                        uuidDetail.getGenerationMethod().getMethodNumber(), uuidDetail.getCreatedBy());
            }
        } catch (DataAccessException exception) {
            StringBuilder errorMsg = new StringBuilder("Error while adding following UUIDs to the database : ");
            errorMsg.append(currentUuid);
            errorMsg.append(exception.getMessage());
            throw new UUIDException(errorMsg.toString(), exception);
        }
        return uuidDetailList.size();
    }


    @Transactional
    @Secured({"ROLE_UUID_CREATOR"})
    @Override
    public void addNewUUIDs(final List<UUIDDetail> uuidDetailList) {

        final List<Object[]> valueList = new ArrayList<Object[]>();
        for (final UUIDDetail uuidDetail : uuidDetailList) {
            Object[] data = new Object[6];
            int i = 0;
            data[i++] = uuidDetail.getUuid();
            data[i++] = uuidDetail.getUuid();
            data[i++] = uuidDetail.getCreationDate();
            data[i++] = uuidDetail.getCenter().getCenterId();
            data[i++] = uuidDetail.getGenerationMethod().getMethodNumber();
            data[i++] = uuidDetail.getCreatedBy();
            valueList.add(data);
            batchUpdate(UUID_UPSERT_QUERY, valueList);
        }

        jdbcTemplate.batchUpdate(UUID_UPSERT_QUERY, valueList);

    }

    private void batchUpdate(final String query,
                             final List<Object[]> data) {
        if (data.size() >= ConstantValues.BATCH_SIZE) {
            jdbcTemplate.batchUpdate(query, data);
            data.clear();
        }
    }

    /**
     * Use to add a new barcode_history record in database and sets this barcode_id as the latest barcode for the uuid
     *
     * @param barcode details of the barcode history record to be added in database
     * @throws UUIDException exception thrown while saving data to database
     */
    @Override
    public void addBarcode(final Barcode barcode) throws UUIDException {
        final Tumor disease = barcode.getDisease();
        final String itemType = commonBarcodeAndUUIDValidator.getItemType(barcode.getBarcode());
        final Long itemTypeId = uuidTypeQueries.getUUIDTypeID(itemType);
        barcode.setItemTypeId(itemTypeId);

        String existingUUID = getUUIDForBarcode(barcode.getBarcode());
        if (existingUUID != null) {
            if (existingUUID.equals(barcode.getUuid())) {
                // is this UUID already linked to the barcode as the CURRENT barcode for the UUID?  If so, do nothing
                final String latestBarcodeForUUID = getLatestBarcodeForUUID(existingUUID);
                if (latestBarcodeForUUID != null && latestBarcodeForUUID.equals(barcode.getBarcode())) {
                    return;
                }
            } else {
                // already linked to a different UUID -- error!
                throw new UUIDException(new StringBuilder().append("Barcode '").append(barcode.getBarcode()).
                        append("' is already associated with UUID '").append(existingUUID).
                        append("'.  It cannot be associated with the UUID '").append(barcode.getUuid()).append("'.").toString());
            }
        }
        long newId;
        try {
            newId = jdbcTemplate.queryForLong("Select barcode_seq.NEXTVAL from DUAL");
            barcode.setBarcodeId(newId);
            jdbcTemplate.update(SQL_INSERT_BARCODE, newId, barcode.getBarcode(), barcode.getUuid(), disease.getTumorId(), barcode.getEffectiveDate(),
                    barcode.getItemTypeId());
        } catch (DataAccessException exception) {
            // this will happen if the UUID is not already in the uuid table 
            final StringBuilder errorMsg = new StringBuilder("Error while adding barcode_history to the database for ");
            errorMsg.append(barcode.getBarcode()).append(": [")
                    .append(barcode.getBarcode())
                    .append(",")
                    .append(barcode.getUuid())
                    .append(".")
                    .append(disease.getTumorId())
                    .append(barcode.getEffectiveDate())
                    .append(barcode.getItemTypeId())
                    .append("]")
                    .append(exception.getMessage());
            throw new UUIDException(errorMsg.toString(), exception);
        }

        try {
            jdbcTemplate.update(SQL_UPDATE_UUID, newId, barcode.getUuid());
        } catch (DataAccessException exception) {
            final StringBuilder errorMsg = new StringBuilder("Error while updating uuid latest_barcode_id for UUID: ");
            errorMsg.append(barcode.getUuid());
            throw new UUIDException(errorMsg.toString(), exception);
        }
    }

    @Override
    public String getLatestBarcodeForUUID(final String uuid) {
        try {
            return jdbcTemplate.queryForObject(SQL_LATEST_BARCODE_FOR_UUID, String.class, uuid.toLowerCase());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean uuidExists(final String uuid) {
        final int count = jdbcTemplate.queryForInt(SQL_UUID_EXISTS_QUERY, uuid.toLowerCase());
        return count > 0;
    }

    @Override
    public List<String> getUUIDsExistInDB(final List<String> UUIDsToValidate) {
        final List<String> existingUUIDs = new ArrayList<String>();
        int startIndex = 0;
        int endIndex = 0;
        final int inClauseSize = getInClauseSize();
        while (endIndex < UUIDsToValidate.size()) {
            startIndex = endIndex;
            endIndex = ((UUIDsToValidate.size() - endIndex) > inClauseSize) ? (endIndex + inClauseSize) : UUIDsToValidate.size();
            final List<String> subList = UUIDsToValidate.subList(startIndex, endIndex);
            final String query = QUERY_EXISTING_UUIDS.replaceAll(REPLACE_UUID,
                    StringUtil.createPlaceHolderString(subList.size(), StringUtil.CaseSensitivity.LOWER_CASE));
            existingUUIDs.addAll(jdbcTemplate.getJdbcOperations().query(query,
                    new ParameterizedRowMapper<String>() {
                        @Override
                        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return rs.getString("uuid");
                        }
                    }, subList.toArray()));

        }
        return existingUUIDs;
    }

    @Override
    public List<String> getExistingBarcodes(final List<String> barcodes) {
        final List<String> existingBarcodes = new ArrayList<String>();
        int startIndex;
        int endIndex = 0;
        final int inClauseSize = getInClauseSize();
        while (endIndex < barcodes.size()) {
            startIndex = endIndex;
            endIndex = ((barcodes.size() - endIndex) > inClauseSize) ? (endIndex + inClauseSize) : barcodes.size();
            final List<String> subList = barcodes.subList(startIndex, endIndex);
            final String query = QUERY_EXISTING_BARCODES.replaceAll(REPLACE_BARCODE,
                    StringUtil.createPlaceHolderString(subList.size(), StringUtil.CaseSensitivity.UPPER_CASE));
            existingBarcodes.addAll(jdbcTemplate.getJdbcOperations().query(query,
                    new ParameterizedRowMapper<String>() {
                        @Override
                        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return rs.getString("barcode");
                        }
                    }, subList.toArray()));
        }
        return existingBarcodes;
    }

    public int getInClauseSize() {
        return ConstantValues.WS_BATCH_SIZE;
    }

    private StringBuilder getUUIDList(final List<UUIDDetail> uuidDetailList) {
        StringBuilder uuidList = null;
        for (final UUIDDetail detail : uuidDetailList) {
            if (uuidList != null) {
                uuidList.append(", ").append(detail.getUuid());
            } else {
                uuidList = new StringBuilder(detail.getUuid());
            }
        }
        return uuidList;
    }

    private Center getCenter(final int centerId, final String domain_name, final String centerType) {
        Center center = new Center();
        center.setCenterId(centerId);
        center.setCenterName(domain_name);
        center.setCenterType(centerType);
        return center;
    }

    /**
     * Return the list of active diseases from database Note that the diseases are retrieved from database only once
     *
     * @return list of centers
     */

    @PostConstruct
    @Override
    public List<Tumor> getActiveDiseases() {
        if (diseases == null) {
            diseases = jdbcTemplate.getJdbcOperations().query(DISEASE_QUERY,
                    new ParameterizedRowMapper<Tumor>() {
                        public Tumor mapRow(final ResultSet resultSet, final int i) throws SQLException {
                            final Tumor tumor = new Tumor();
                            tumor.setTumorId(resultSet.getInt("ID"));
                            tumor.setTumorName(resultSet.getString("NAME"));
                            tumor.setTumorDescription(resultSet.getString("DESCRIPTION"));
                            return tumor;
                        }
                    });
        }
        return diseases;
    }

    @Override
    public Tumor getDisease(final int diseaseId) {
        Tumor retTumor = null;
        final List<Tumor> diseases = getActiveDiseases();
        for (final Tumor tumor : diseases) {
            if (tumor.getTumorId() == diseaseId) {
                retTumor = tumor;
            }
        }
        return retTumor;
    }

    /**
     * Search UUIDs for the given criteria
     *
     * @param criteria : search criteria
     * @return search results
     */
    @Override
    public List<UUIDDetail> searchUUIDs(final SearchCriteria criteria) {

        // build the query
        List<Object> bindVariables = new ArrayList<Object>();
        String searchQuery = getSearchQuery(criteria, bindVariables);
        Object[] params = bindVariables.toArray();
        return jdbcTemplate.getJdbcOperations().query(searchQuery,
                getUUIDRowMapper(), params);
    }

    /**
     * Returns UUID Details
     *
     * @param uuid uuid
     * @return uuid detail object
     * @throws UUIDException if UUID is not found
     */
    @Override
    public UUIDDetail getUUIDDetail(final String uuid) throws UUIDException {

        UUIDDetail uuidDetail = null;
        List<UUIDDetail> list = jdbcTemplate.getJdbcOperations().query(SQL_UUID_DETAIL,
                getUUIDRowMapper(), uuid);

        if (list.size() > 0) {
            uuidDetail = list.get(0);
            // get all the barcodes for that UUID
            ParameterizedRowMapper<Barcode> barcodeRowMapper = getBarcodeRowMapper();
            List<Barcode> barcodeList = jdbcTemplate.getJdbcOperations().query(SQL_UUID_BARCODE,
                    barcodeRowMapper, uuid);
            uuidDetail.setBarcodes(barcodeList);
            uuidDetail.setLatestBarcode(getLatestBarcodeForUUID(uuid));
        }

        if (uuidDetail != null) {
            return uuidDetail;
        } else {
            throw new UUIDException("UUID " + uuid + " not found");
        }
    }

    // method constructs the search query for the fields set in the search criteria

    protected String getSearchQuery(final SearchCriteria criteria, final List<Object> bindVariables) {
        // check for a value of each field and add a condition to where clause if the
        // value is non-null or the value is other than zero
        // for tables that need a join with other tables ( fields : Barcode/Disease)
        // make sure that the from clause has that table and the appropriate joins are added to the where clause

        StringBuilder fromClause = new StringBuilder(FROM_CLAUSE);
        StringBuilder whereClause = new StringBuilder(WHERE_CLAUSE);

        final String uuid = criteria.getUuid();
        if ((uuid != null) && uuid.length() > 0) {
            whereClause.append(" and U.UUID = ? ");
            bindVariables.add(uuid);
        }

        final String barcode = criteria.getBarcode();
        if ((barcode != null) && barcode.length() > 0) {
            whereClause.append(" and B.BARCODE = ? ");
            bindVariables.add(barcode);
        }

        if (criteria.getDisease() > 0) {
            whereClause.append(" and B.DISEASE_ID = ? ");
            bindVariables.add(criteria.getDisease());
        }

        if (criteria.getCenterId() > 0) {
            whereClause.append(" and U.CENTER_ID = ? ");
            bindVariables.add(criteria.getCenterId());
        }

        final String submittedBy = criteria.getSubmittedBy();
        if ((submittedBy != null) && submittedBy.length() > 0) {
            whereClause.append(" and U.CREATED_BY = ? ");
            bindVariables.add(submittedBy);
        }

        if (criteria.getCreationDate() != null) {
            whereClause.append(" and U.CREATE_DATE > ? ");
            bindVariables.add(criteria.getCreationDate());

            Calendar cal = Calendar.getInstance();
            cal.setTime(criteria.getCreationDate());
            cal.add(Calendar.DATE, 1);
            whereClause.append(" and U.CREATE_DATE < ? ");
            bindVariables.add(cal.getTime());
        }

        String searchQuery = new StringBuilder(SELECT_CLAUSE)
                .append(fromClause)
                .append(whereClause)
                .append(ORDER_BY_CLAUSE).toString();

        /* used for debugging */

        /*Object[] params = bindVariables.toArray();

        StringBuilder msg = (new StringBuilder()).append("UUID Search  - query : \n")
                .append(searchQuery).append('\n').append("params: ");
        for (final Object param : params) {
            msg.append(param.toString()).append(", ");
        }
        System.out.println(msg.toString());*/

        /* end debugging */

        return searchQuery;
    }


    /**
     * Returns Barcode for a given barcode id
     *
     * @param barcodeId barcode id
     * @return barcode
     */
    @Override
    public Barcode getBarcodeForId(final long barcodeId) {
        Barcode barcode = null;
        if (barcodeId != 0) {
            barcode = new Barcode();
            barcode.setBarcodeId(barcodeId);
            // get all the barcodes for that UUID
            ParameterizedRowMapper<Barcode> barcodeRowMapper = getBarcodeRowMapper();
            List<Barcode> barcodeList = jdbcTemplate.getJdbcOperations().query(SQL_BARCODE,
                    barcodeRowMapper, barcodeId);
            if (barcodeList.size() > 0) {
                barcode = barcodeList.get(0);
            }
        }
        return barcode;
    }

    /**
     * UUID for a given human-readable barcode
     *
     * @param barcode
     * @return UUID
     */
    @Override
    public String getUUIDForBarcode(String barcode) {
        String uuid = null;
        if (barcode.length() != 0) {
            try {
                uuid = jdbcTemplate.queryForObject(SQL_BARCODE_UUID, String.class, new Object[]{barcode});
            } catch (EmptyResultDataAccessException e) {
                //doesn't exist, return null
            }
        }
        return uuid;
    }

    @Override
    public List<Barcode> getBarcodesStartingWith(final String barcodePrefix) {

        List<Barcode> result = new ArrayList<Barcode>();

        if (!StringUtils.isBlank(barcodePrefix)) {
            result = jdbcTemplate.getJdbcOperations().query(SQL_BARCODE_STARTING_WITH,
                    getBarcodeRowMapper(), barcodePrefix + "%");
        }

        return result;
    }

    /**
     * Get the list of UUIDs generated in the specified duration
     *
     * @param duration duration : day/month/week
     * @return list of UUIDs
     */
    @Override
    public List<UUIDDetail> getNewlyGeneratedUUIDs(final Duration duration) {
        final List<Object> bindVariables = new ArrayList<Object>();
        final Date today = getCurrentDate();
        final Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        duration.getAdjustedDate(cal);
        bindVariables.add(cal.getTime());
        bindVariables.add(today);

        final String newUUIDReportQuery = new StringBuilder(SELECT_CLAUSE)
                .append(FROM_CLAUSE)
                .append(WHERE_CLAUSE)
                .append(" AND U.CREATE_DATE > ? AND U.CREATE_DATE < ? ")
                .append(ORDER_BY_CLAUSE).toString();

        final Object[] params = bindVariables.toArray();
        return jdbcTemplate.getJdbcOperations().query(newUUIDReportQuery,
                getUUIDRowMapper(), params);
    }

    /**
     * Returns the list of Submitted UUIDs. The UUIDs are submitted if there is a latest barCode associated with the
     * UUID
     *
     * @return list of submitted UUIDs
     */
    @Override
    public List<UUIDDetail> getSubmittedUUIDs() {
        final List<Object> bindVariables = new ArrayList<Object>();
        final String submittedUUIDReportQuery = new StringBuilder(SELECT_CLAUSE)
                .append(FROM_CLAUSE)
                .append(WHERE_CLAUSE)
                .append(" AND U.LATEST_BARCODE_ID is NOT NULL ")
                .append(ORDER_BY_CLAUSE).toString();

        final Object[] params = bindVariables.toArray();
        return jdbcTemplate.getJdbcOperations().query(submittedUUIDReportQuery,
                getUUIDRowMapper(), params);
    }

    /**
     * Returns the list of Missing UUIDs. The UUIDs are missing if there is no barcode associated with the UUID
     *
     * @return list of missing UUIDs
     */
    @Override
    public List<UUIDDetail> getMissingUUIDs() {
        final String missingUUIDReportQuery = new StringBuilder(SELECT_CLAUSE)
                .append(FROM_CLAUSE)
                .append(WHERE_CLAUSE)
                .append(" AND U.LATEST_BARCODE_ID is NULL ")
                .append(ORDER_BY_CLAUSE).toString();
        return jdbcTemplate.getJdbcOperations().query(missingUUIDReportQuery,
                getUUIDRowMapper());
    }


    // added this method so that we could override this in the test class and
    // specify the date ranges with respect to the data loaded for dbunit testing

    protected Date getCurrentDate() {
        return new Date();
    }

    private ParameterizedRowMapper<UUIDDetail> getUUIDRowMapper() {
        return new ParameterizedRowMapper<UUIDDetail>() {
            public UUIDDetail mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
                UUIDDetail uuidDetail = new UUIDDetail();
                uuidDetail.setUuid(resultSet.getString("UUID"));
                uuidDetail.setCreatedBy(resultSet.getString("CREATED_BY"));
                uuidDetail.setCenter(getCenter(resultSet.getInt("CENTER_ID"), resultSet.getString("DOMAIN_NAME"),
                        resultSet.getString("CENTER_TYPE_CODE")));
                uuidDetail.setCreationDate(resultSet.getDate("CREATE_DATE"));
                uuidDetail.setGenerationMethod(UUIDConstants.getGenerationMethod(resultSet.getInt("GENERATION_METHOD_ID")));
                uuidDetail.setLatestBarcode(resultSet.getString("BARCODE"));
                uuidDetail.setDiseaseAbbrev(resultSet.getString("DISEASE_ABBREVIATION"));
                return uuidDetail;
            }
        };
    }

    private ParameterizedRowMapper<Barcode> getBarcodeRowMapper() {
        return new ParameterizedRowMapper<Barcode>() {
            public Barcode mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
                final Barcode barcode = new Barcode();
                barcode.setBarcode(resultSet.getString("BARCODE"));
                barcode.setBarcodeId(resultSet.getLong("BARCODE_ID"));
                barcode.setDisease(getDisease(resultSet.getInt("DISEASE_ID")));
                barcode.setEffectiveDate(resultSet.getDate("EFFECTIVE_DATE"));
                barcode.setUuid(resultSet.getString("UUID"));
                barcode.setItemTypeId(resultSet.getLong("ITEM_TYPE_ID"));
                return barcode;
            }
        };
    }

    /**
     * process the sequential union clause needed for the multiple barcode/uuid mapping query
     *
     * @param maxParameter    max size of underlying list of arguments
     * @param name            name of element to select, uuid or barcode here
     * @param caseSensitivity lowe, upper or normal
     * @return fully constructed query arguments
     */
    protected String processUnionClause(int maxParameter, final String name,
                                        final StringUtil.CaseSensitivity caseSensitivity) {
        final StringBuilder placeHolderString = new StringBuilder();
        for (int i = 0; i < maxParameter; i++) {

            switch (caseSensitivity) {
                case CASE_SENSITIVE:
                    placeHolderString.append("select ? as " + name + " from dual union ");
                    break;
                case LOWER_CASE:
                    placeHolderString.append("select lower(?) as " + name + " from dual union ");
                    break;
                case UPPER_CASE:
                    placeHolderString.append("select upper(?) as " + name + " from dual union ");
                    break;
            }
        }
        placeHolderString.delete(placeHolderString.lastIndexOf(" union "), placeHolderString.length());
        return placeHolderString.toString();
    }

    @Override
    public List<UuidBarcodeMapping> getLatestBarcodesForUUIDs(final List<String> uuids) {
        final List<UuidBarcodeMapping> barcodeList = new LinkedList<UuidBarcodeMapping>();
        int startIndex;
        int endIndex = 0;
        final int inClauseSize = getInClauseSize();
        while (endIndex < uuids.size()) {
            startIndex = endIndex;
            endIndex = ((uuids.size() - endIndex) > inClauseSize) ? (endIndex + inClauseSize) : uuids.size();
            final List<String> subList = uuids.subList(startIndex, endIndex);
            final String query = BARCODES_FROM_UUIDS_MAPPING_QUERY.replaceAll(REPLACE_UUID,
                    processUnionClause(subList.size(), "uuid", StringUtil.CaseSensitivity.LOWER_CASE));
            barcodeList.addAll(jdbcTemplate.getJdbcOperations().query(query,
                    new ParameterizedRowMapper<UuidBarcodeMapping>() {
                        @Override
                        public UuidBarcodeMapping mapRow(ResultSet rs, int rowNum) throws SQLException {
                            final String barcode = rs.getString("barcode");
                            final String uuid = rs.getString("uuid");
                            final String error = "barcode mapping for uuid '" + uuid + "' was not found";
                            return new UuidBarcodeMapping(barcode == null ? error : barcode, uuid);
                        }
                    }, subList.toArray()));

        }
        return barcodeList;
    }

    @Override
    public List<UuidBarcodeMapping> getUUIDsForBarcodes(List<String> barcodes) {
        final List<UuidBarcodeMapping> uuidList = new LinkedList<UuidBarcodeMapping>();
        int startIndex;
        int endIndex = 0;
        final int inClauseSize = getInClauseSize();
        while (endIndex < barcodes.size()) {
            startIndex = endIndex;
            endIndex = ((barcodes.size() - endIndex) > inClauseSize) ? (endIndex + inClauseSize) : barcodes.size();
            final List<String> subList = barcodes.subList(startIndex, endIndex);
            final String query = UUIDS_FROM_BARCODES_MAPPING_QUERY.replaceAll(REPLACE_BARCODE,
                    processUnionClause(subList.size(), "barcode", StringUtil.CaseSensitivity.UPPER_CASE));
            uuidList.addAll(jdbcTemplate.getJdbcOperations().query(query,
                    new ParameterizedRowMapper<UuidBarcodeMapping>() {
                        @Override
                        public UuidBarcodeMapping mapRow(ResultSet rs, int rowNum) throws SQLException {
                            final String barcode = rs.getString("barcode");
                            final String uuid = rs.getString("uuid");
                            final String error = "uuid mapping for barcode '" + barcode + "' was not found";
                            return new UuidBarcodeMapping(barcode, uuid == null ? error : uuid);
                        }
                    }, subList.toArray()));
        }
        return uuidList;
    }

    @Override
    public void addParticipantFileUUIDAssociation(final String UUID, final Long fileId) {
        if (StringUtils.isNotEmpty(UUID) && fileId != null && fileId.longValue() > 0) {
            Object[] patientUUIDArray = {UUID.toLowerCase(), fileId, UUID.toLowerCase(), fileId};
            List patientUUIDArrayList = new ArrayList<Object[]>();
            patientUUIDArrayList.add(patientUUIDArray);
            addParticipantFileUUIDAssociation(patientUUIDArrayList);
        } else {
            if (StringUtils.isEmpty(UUID)) {
                throw new IllegalArgumentException(" Non empty UUID must be present");
            }
            if (fileId == null || fileId.longValue() <= 0) {
                throw new IllegalArgumentException(" Non empty file Id must be present");
            }
        }

    }

    @Override
    public void addParticipantFileUUIDAssociation(final List<Object[]> patientsUUIDAndFileId) {
        jdbcTemplate.batchUpdate(MERGE_PATIENT_UUID_FILE_ID, patientsUUIDAndFileId);
    }

    public void setJdbcTemplate(org.springframework.jdbc.core.simple.SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setCenterQueries(gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries centerQueries) {
        this.centerQueries = centerQueries;
    }

    public void setUuidTypeQueries(gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDTypeQueries uuidTypeQueries) {
        this.uuidTypeQueries = uuidTypeQueries;
    }

    public void setCommonBarcodeAndUUIDValidator(gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator commonBarcodeAndUUIDValidator) {
        this.commonBarcodeAndUUIDValidator = commonBarcodeAndUUIDValidator;
    }
}
