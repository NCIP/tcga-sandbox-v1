/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Barcode;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.BaseQueriesProcessor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDDAO;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BCRID;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BiospecimenToFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.BCRIDQueries;
import org.apache.log4j.Level;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Robert S. Sfeir
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BCRIDQueriesImpl extends BaseQueriesProcessor implements BCRIDQueries {

    private static final String BIOSPECIMEN_FILE_SEQ = "biospecimen_file_seq";
    private final ProcessLogger logger = new ProcessLogger();
    private static final String BIOSPECIMEN_TO_FILE_UPSERT_QUERY =
            " MERGE INTO biospecimen_to_file " +
                    " USING DUAL ON (" +
                    " biospecimen_id = ?" +
                    " and file_id =? )" +
                    " WHEN NOT MATCHED THEN " +
                    " insert(biospecimen_file_id,biospecimen_id,file_id)" +
                    " values(?,?,?)";

    private static final String BIOSPECIMEN_BARCODE_UPSERT_QUERY =
            " MERGE INTO biospecimen_barcode " +
                    " USING DUAL ON ( barcode = ? )" +
                    " WHEN NOT MATCHED THEN " +
                    " insert(biospecimen_id,barcode,project_code,tss_code,patient,sample_type_code,sample_sequence,portion_sequence,portion_analyte_code,plate_id,bcr_center_id,uuid)" +
                    " values(?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String BIOSPECIMEN_TO_FILE_UPDATE_FILE_ID_QUERY =
            " UPDATE biospecimen_to_file " +
                    "SET file_id=? " +
                    "WHERE biospecimen_id = ? AND file_id = ?";

    private static final String SHIPPED_BIOSPECIMEN_FILE_UPDATE_FILE_ID_QUERY =
            " UPDATE shipped_biospecimen_file " +
                    "SET file_id=? " +
                    "WHERE shipped_biospecimen_id = ? AND file_id = ?";

    private static final int SLIDE_ITEM_TYPE_ID = 5;
    private static final String SLIDE_BARCODE_EXISTS_QUERY = "select barcode_history.barcode from " +
            "barcode_history, " +
            "uuid_hierarchy, " +
            "uuid_item_type where " +
            "barcode_history.uuid = uuid_hierarchy.uuid and " +
            "uuid_hierarchy.item_type_id = UUID_ITEM_TYPE.ITEM_TYPE_ID and " +
            "UUID_ITEM_TYPE.ITEM_TYPE_ID = " + SLIDE_ITEM_TYPE_ID + " and " +
            "barcode_history.barcode =  ?";

    private static final String REPLACE_ME = "REPLACE_ME";
    private static final String BIOSPECIMEN_GET_QUERY = "select biospecimen_id from biospecimen_barcode where barcode in(" + REPLACE_ME + ")";
    private static final String BIOSPECIMEN_ID_GET_QUERY = "select biospecimen_id,barcode from biospecimen_barcode where barcode in(" + REPLACE_ME + ")";
    private static final String UUID_GET_QUERY = "select UUID from biospecimen_barcode where barcode = ?";
    private static final String GET_BIOSPECIMEN_ID_FOR_UUID_QUERY = "select biospecimen_id from biospecimen_barcode where uuid=lower(?)";

    private static final String INSERT_SHIPPED_BIOSPECIMEN = "merge into shipped_biospecimen_file " +
        "using dual on (shipped_biospecimen_id=? and file_id=?) " +
        "when not matched then " +
        "insert (shipped_biospecimen_id, file_id) values(?, ?)";

    private UUIDDAO uuidDAO;
    private UUIDService uuidService;
    private CenterQueries centerQueries;

    public Collection getAllBCRIDs() {
        return getAllObjectsAsList("biospecimen_barcode", "barcode");
    }

    /**
     * Adds barcode to database
     *
     * @param theBCRId        bcr id
     * @param useIdFromCommon Set to true if the bcrId set in the BCRID should be used while adding the bar code to
     *                        database, false otherwise
     * @return barcode id
     */
    public Integer addBCRID(final BCRID theBCRId, final Boolean useIdFromCommon) {
        String insert = "insert into biospecimen_barcode(biospecimen_id,barcode,project_code,tss_code,patient,sample_type_code,sample_sequence,portion_sequence,portion_analyte_code,plate_id,bcr_center_id,UUID) values (?,?,?,?,?,?,?,?,?,?,?,?)";
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
        Integer theId;
        if (useIdFromCommon) {
            theId = theBCRId.getId();
        } else {
            theId = getNextSequenceNumberAsInteger("biospecimen_barcode_seq");
        }
        sjdbc.update(insert, theId, theBCRId.getFullID(), theBCRId.getProjectName(), theBCRId.getSiteID(),
                theBCRId.getPatientID(), theBCRId.getSampleTypeCode(), theBCRId.getSampleNumberCode(),
                theBCRId.getPortionNumber(), theBCRId.getPortionTypeCode(), theBCRId.getPlateId(), theBCRId.getBcrCenterId(), theBCRId.getUUID());
        // note, if there is an error doing the insert, it will throw a DataAccessException, which is a RuntimeException
        // that should propagate all the way to the top and result in the archive failing... because if a barcode insert
        // fails, the processing has to stop.
        return theId;
    }

    public Integer updateBCRIDStatus(final BCRID theBCRId) {
        String update = "UPDATE biospecimen_barcode " +
                "SET is_valid = ? " +
                "WHERE barcode = ? ";
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
        Integer theId = null;
        try {
            theId = sjdbc.update(update, theBCRId.getValid(), theBCRId.getFullID());
        } catch (DataAccessException e) {
            logger.logToLogger(Level.FATAL, ProcessLogger.stackTracePrinter(e));
        }
        return theId;
    }

    public Integer exists(final BCRID theID) {
        return getObjectIdByNameAsInteger(theID.getFullID(), "biospecimen_barcode", "barcode", "biospecimen_id");
    }

    public Integer exists(final String theBCRID) {
        return getObjectIdByNameAsInteger(theBCRID, "biospecimen_barcode", "barcode", "biospecimen_id");
    }

    /**
     * Get the UUID for the biospecimen -- does not check the UUID tables, just biospecimen_barcode!
     *
     * @param theID the biospecimen to look up
     * @return the UUID or null if none set
     */
    public String getBiospecimenUUID(final BCRID theID) {
        try {
            SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
            return sjdbc.queryForObject(UUID_GET_QUERY, String.class, theID.getFullID());
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    public Long getBiospecimenIdForUUID(String uuid) {
        try {
            SimpleJdbcTemplate simpleJdbcTemplate = new SimpleJdbcTemplate(getDataSource());
            return simpleJdbcTemplate.queryForLong(GET_BIOSPECIMEN_ID_FOR_UUID_QUERY, uuid);
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }

    public void updateUUIDForBarcode(final BCRID theBCRId) {
        String update = "update biospecimen_barcode set UUID = ? where barcode = ? ";
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
        sjdbc.update(update, theBCRId.getUUID(), theBCRId.getFullID());
    }

    public String getHistoryUUIDForBarcode(final BCRID bcrId) {
        return uuidDAO.getUUIDForBarcode(bcrId.getFullID());
    }

    public void updateShipDate(final BCRID theBCRID) {
        String update = "update biospecimen_barcode set ship_date = ? where biospecimen_id = ?";
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
        sjdbc.update(update, convertDateString(theBCRID.getShippingDate()), theBCRID.getId());
    }

    /**
     * Adds bcr to archive relationship in bcr_biospecimen_to_archive table
     *
     * @param theBCRID        bcr id
     * @param useIdFromCommon Set to true if the bcrArchiveId from common database should be used for saving the
     *                        association to database, false otherwise
     * @param bcrArchiveId    biospecimen to archive association id
     */
    public void addArchiveRelationship(final BCRID theBCRID, final Boolean useIdFromCommon, final int[] bcrArchiveId) {
        String insert = "insert into bcr_biospecimen_to_archive(biospecimen_archive_id,biospecimen_id,archive_id) values (?,?,?)";
        SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
        if (!useIdFromCommon) {
            bcrArchiveId[0] = getNextSequenceNumberAsInteger("BIOSPECIMEN_ARCHIVE_SEQ");
        }

        try {
            sjdbc.update(insert, bcrArchiveId[0], theBCRID.getId(), theBCRID.getArchiveId());
        } catch (DataIntegrityViolationException e) {
            //The Association already exists and can be ignored.  It is likely happening because an archive was run multiple times
            //And the barcode was already found to be defective and there is no need to report it again.
        }
    }


    /**
     * Adds BCR to File association in database
     *
     * @param fileId          file id
     * @param barcodeId       barcode id
     * @param colName         column name
     * @param useIdFromCommon Set to true if the bcrFileId from common database should be used for saving the
     *                        association to database, false otherwise
     * @param bcrFileId       bcr to file association id
     */
    public int addFileAssociation(final Long fileId,
                                  final Integer barcodeId,
                                  final String colName,
                                  final Boolean useIdFromCommon,
                                  int bcrFileId) {

        final String insert = "insert into biospecimen_to_file(biospecimen_file_id, biospecimen_id,file_id,file_col_name) values (?,?,?,?)";

        if (!useIdFromCommon) {
            bcrFileId = getNextSequenceNumberAsInteger(BIOSPECIMEN_FILE_SEQ);
        }

        final SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
        sjdbc.update(insert, bcrFileId, barcodeId, fileId, colName);
        sjdbc.update(INSERT_SHIPPED_BIOSPECIMEN, barcodeId, fileId, barcodeId, fileId);
        return bcrFileId;
    }

    public Integer findExistingAssociation(final Long fileId, final Integer bcrId, final String colName) {
        Integer associationId = 0;
        try {
            String select = "select biospecimen_file_id from biospecimen_to_file where biospecimen_id = ? and file_id = ? and file_col_name = ?";
            SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
            associationId = sjdbc.queryForInt(select, bcrId, fileId, colName);
        } catch (EmptyResultDataAccessException e) {
            //nothing found keep going
        }
        return associationId;
    }

    public void addBioSpecimenToFileAssociations(final List<BiospecimenToFile> biospecimenToFileList) {
        final SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
        final List<Object[]> valueList = new ArrayList<Object[]>();

        for (final BiospecimenToFile biospecimenToFile : biospecimenToFileList) {
            Object[] data = new Object[5];
            int i = 0;
            data[i++] = biospecimenToFile.getBiospecimenId();
            data[i++] = biospecimenToFile.getFileId();
            // if the id already exists, reuse the same
            if (biospecimenToFile.getBiospecimenFileId() == null ||
                    biospecimenToFile.getBiospecimenFileId() == ConstantValues.NOT_ASSIGNED) {
                biospecimenToFile.setBiospecimenFileId(getNextSequenceNumberAsInteger("biospecimen_file_seq"));
            }
            data[i++] = biospecimenToFile.getBiospecimenFileId();
            data[i++] = biospecimenToFile.getBiospecimenId();
            data[i++] = biospecimenToFile.getFileId();
            valueList.add(data);


        }
        sjdbc.batchUpdate(BIOSPECIMEN_TO_FILE_UPSERT_QUERY, valueList);
        valueList.clear();

    }

    public void addBarcodeHistory(final BCRID theBCRID, final Tumor disease)
            throws UUIDException {
        Barcode barcodeHist = new Barcode();
        barcodeHist.setBarcode(theBCRID.getFullID());
        barcodeHist.setUuid(theBCRID.getUUID());
        barcodeHist.setEffectiveDate(new java.util.Date());
        barcodeHist.setDisease(disease);
        uuidDAO.addBarcode(barcodeHist);
    }

    @Override
    public boolean slideBarcodeExists(final String barcode) {

        try {
            final SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
            sjdbc.queryForObject(SLIDE_BARCODE_EXISTS_QUERY, String.class, new Object[]{barcode});

        } catch (EmptyResultDataAccessException e) {
            return false;
        } catch (IncorrectResultSizeDataAccessException e) {
            // ignore if it is more than one
        }
        return true;
    }

    public void addBioSpecimenBarcodes(final List<BCRID> bcrIdList, final Tumor disease)
            throws UUIDException {
        final SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
        final List<Object[]> valueList = new ArrayList<Object[]>();

        for (final BCRID bcrId : bcrIdList) {
            Object[] data = new Object[13];
            int i = 0;
            // if the id already exists, reuse the same
            if (bcrId.getId() == null ||
                    bcrId.getId() == ConstantValues.NOT_ASSIGNED) {
                bcrId.setId(getNextSequenceNumberAsInteger("biospecimen_barcode_seq"));
            }

            // always set the UUID from the UUID system
            bcrId.setUUID(getHistoryUUIDForBarcode(bcrId));
            if (bcrId.getUUID() == null) {
                bcrId.setUUID(generateUUIDForBarcode(bcrId));
                addBarcodeHistory(bcrId, disease);
            }

            // make sure the biospecimen uuid matches the uuid system
            String biospecimenUUID = getBiospecimenUUID(bcrId);
            if (!bcrId.getUUID().equals(biospecimenUUID)) {
                updateUUIDForBarcode(bcrId);
            }
            data[i++] = bcrId.getFullID();
            data[i++] = bcrId.getId();
            data[i++] = bcrId.getFullID();
            data[i++] = bcrId.getProjectName();
            data[i++] = bcrId.getSiteID();
            data[i++] = bcrId.getPatientID();
            data[i++] = bcrId.getSampleTypeCode();
            data[i++] = bcrId.getSampleNumberCode();
            data[i++] = bcrId.getPortionNumber();
            data[i++] = bcrId.getPortionTypeCode();
            data[i++] = bcrId.getPlateId();
            data[i++] = bcrId.getBcrCenterId();
            data[i++] = bcrId.getUUID();

            valueList.add(data);
        }
        sjdbc.batchUpdate(BIOSPECIMEN_BARCODE_UPSERT_QUERY, valueList);
        valueList.clear();
    }

    private String generateUUIDForBarcode(final BCRID bcrID) throws UUIDException {
        int centerId = centerQueries.getCenterIdForBCRCenter(bcrID.getBcrCenterId());
        List<UUIDDetail> newUUID = uuidService.generateUUID(centerId, 1, UUIDConstants.GenerationMethod.API, UUIDConstants.MASTER_USER);
        return (newUUID.get(0).getUuid());
    }

    // Returns biospecimen ids for the given barcodes

    public List<Integer> getBiospecimenIds(final List<String> barcodes) {
        final JdbcTemplate jdbc = new JdbcTemplate(getDataSource());
        String SELECT_QUERY = BIOSPECIMEN_GET_QUERY.replace(REPLACE_ME, StringUtil.createPlaceHolderString(barcodes.size()));

        return jdbc.query(SELECT_QUERY,
                barcodes.toArray(),
                new ParameterizedRowMapper<Integer>() {
                    public Integer mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
                        return resultSet.getInt("biospecimen_id");
                    }
                }
        );
    }

    public Map<String, Integer> getBiospecimenIdsForBarcodes(final List<String> barcodes) {
        final Map<String, Integer> biospecimenIdsByBarcode = new HashMap<String, Integer>();
        final JdbcTemplate jdbc = new JdbcTemplate(getDataSource());
        String SELECT_QUERY = BIOSPECIMEN_ID_GET_QUERY.replace(REPLACE_ME, StringUtil.createPlaceHolderString(barcodes.size()));

        jdbc.query(SELECT_QUERY,
                barcodes.toArray(),
                new RowCallbackHandler() {
                    @Override
                    public void processRow(ResultSet resultSet) throws SQLException {
                        biospecimenIdsByBarcode.put(resultSet.getString("barcode"), resultSet.getInt("biospecimen_id"));
                    }
                });
        return biospecimenIdsByBarcode;
    }

    private java.sql.Date convertDateString(final String date) {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date parsedDate = null;
        try {
            parsedDate = sdf.parse(date);
        } catch (ParseException e) {
            logger.logToLogger(Level.FATAL, "Could not convert Date for SQL Insert.");
        }
        assert parsedDate != null;
        return new java.sql.Date(parsedDate.getTime());
    }

    public void setUuidService(final UUIDService service) {
        this.uuidService = service;
    }

    public void setUuidDAO(final UUIDDAO uuidDAO) {
        this.uuidDAO = uuidDAO;
    }

    public List<BCRID> getArchiveBarcodes(long archiveId) {
        String archiveBarcodesQuery = "Select distinct ai.archive_id, sb2f.shipped_biospecimen_id, sb.is_viewable, sb.built_barcode " +
                "from archive_info ai, file_to_archive f2a, shipped_biospecimen_file sb2f, shipped_biospecimen sb " +
                "where ai.archive_id = ? " +
                "and ai.archive_id = f2a.archive_id " +
                "and f2a.file_id = sb2f.file_id " +
                "and sb2f.shipped_biospecimen_id = sb.shipped_biospecimen_id";
        final SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());

        return sjdbc.query(archiveBarcodesQuery, new ParameterizedRowMapper<BCRID>() {
            public BCRID mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
                BCRID bcrId = new BCRID();
                bcrId.setArchiveId(resultSet.getLong("archive_id"));
                bcrId.setId(resultSet.getInt("shipped_biospecimen_id"));
                bcrId.setViewable(resultSet.getInt("is_viewable"));
                bcrId.setFullID(resultSet.getString("built_barcode"));
                return bcrId;
            }
        }, archiveId);
    }

    public void setCenterQueries(CenterQueries centerQueries) {
        this.centerQueries = centerQueries;
    }

    public void updateBiospecimenToFileAssociations(List<BiospecimenToFile> biospecimenToFiles) {
        final SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(getDataSource());
        final List<Object[]> valueList = new ArrayList<Object[]>();
        final List<Object[]> shippedBiospecimenValueList = new ArrayList<Object[]>();

        for (final BiospecimenToFile biospecimenToFile : biospecimenToFiles) {
            Object[] data = new Object[3];
            data[0] = biospecimenToFile.getFileId();
            data[1] = biospecimenToFile.getBiospecimenId();
            data[2] = biospecimenToFile.getOldFileId();
            valueList.add(data);

            //APPS-3666 - Also write to shipped_biospecimen_file
            Object[] shippedData = new Object[3];
            shippedData[0] = biospecimenToFile.getFileId();
            shippedData[1] = biospecimenToFile.getBiospecimenId();
            shippedData[2] = biospecimenToFile.getOldFileId();
            shippedBiospecimenValueList.add(shippedData);
        }

        sjdbc.batchUpdate(BIOSPECIMEN_TO_FILE_UPDATE_FILE_ID_QUERY, valueList);
        sjdbc.batchUpdate(SHIPPED_BIOSPECIMEN_FILE_UPDATE_FILE_ID_QUERY, shippedBiospecimenValueList);
        valueList.clear();

    }

    /**
     * check if uuid exists in db
     *
     * @param uuid uuid
     * @return boolean flag if uuid exists
     */
    public boolean uuidExists(String uuid) {
        return uuidDAO.uuidExists(uuid);
    }
}
