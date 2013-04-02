package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import com.googlecode.ehcache.annotations.Cacheable;
import com.googlecode.ehcache.annotations.KeyGenerator;
import com.googlecode.ehcache.annotations.Property;
import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.BiospecimenMetaData;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDHierarchyQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class to return data from the uuid_hierarchy that can be added to and can be used from apps
 * other than the uuid browser
 *
 * @author Shelley Alonso
 *         Last updated by: Stan Girshik
 * @version $Rev$
 */

public class UUIDHierarchyQueriesJDBCImpl extends SimpleJdbcDaoSupport implements UUIDHierarchyQueries {
    private CommonBarcodeAndUUIDValidator commonBarcodeAndUUIDValidator;
    private Validator validator;
    private final Log logger = LogFactory.getLog(UUIDHierarchyQueriesJDBCImpl.class);
    private final int MAX_IN_CLAUSE = 20;
    private final String GET_CHILDREN_BY_UUID = "select uuid from uuid_hierarchy start with uuid = ? connect by prior uuid=parent_uuid";
    private final String GET_CHILDREN_BY_BARCODE = "select uuid from uuid_hierarchy start with barcode = ? connect by prior uuid=parent_uuid";

    private static final String GET_UUID_FROM_HIERARCHY = "select count(uuid) from uuid_hierarchy where uuid = lower(?)";
    private static final String ADD_UUID_HIERARCHY = "insert into uuid_hierarchy (" +
            "DISEASE_ABBREVIATION," +
            "UUID," +
            "PARENT_UUID," +
            "ITEM_TYPE_ID," +
            "TSS_CODE," +
            "CENTER_ID_BCR," +
            "BATCH_NUMBER," +
            "BARCODE," +
            "PARTICIPANT_NUMBER," +
            "SAMPLE_TYPE_CODE," +
            "SAMPLE_SEQUENCE," +
            "PORTION_SEQUENCE," +
            "PORTION_ANALYTE_CODE," +
            "PLATE_ID," +
            "CENTER_CODE, " +
            "RECEIVING_CENTER_ID," +
            "SLIDE," +
            "SLIDE_LAYER," +
            "IS_SHIPPED," +
            "SHIPPED_DATE," +
            "CREATE_DATE," +
            "UPDATE_DATE," +
            "PLATFORMS) values(?,lower(?),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE,SYSDATE,null)";

    private static final String UPDATE_UUID_HIERARCHY = "update uuid_hierarchy set " +
            "DISEASE_ABBREVIATION = ?," +
            "PARENT_UUID=?," +
            "ITEM_TYPE_ID=?," +
            "TSS_CODE=?," +
            "CENTER_ID_BCR=?," +
            "BATCH_NUMBER=?," +
            "BARCODE=?," +
            "PARTICIPANT_NUMBER=?," +
            "SAMPLE_TYPE_CODE=?," +
            "SAMPLE_SEQUENCE=?," +
            "PORTION_SEQUENCE=?," +
            "PORTION_ANALYTE_CODE=?," +
            "PLATE_ID=?," +
            "CENTER_CODE=?, " +
            "RECEIVING_CENTER_ID=?," +
            "SLIDE=?," +
            "SLIDE_LAYER=?," +
            "IS_SHIPPED=?," +
            "SHIPPED_DATE=?," +
            "UPDATE_DATE=SYSDATE" +
            " where uuid=lower(?)";

    private static final String GET_UUID_ITEM_TYPE = "select ITEM_TYPE_ID from uuid_item_type where XML_NAME = ?";

    private static final String GET_RECEIVING_CENTER_ID = "select center_id from center_to_bcr_center where bcr_center_id=?";

    private static final String GET_BCR_RECEIVING_CENTER_ID = "select bcr_center_id from center_to_bcr_center where center_id=?";

    private static final String GET_UUID_HIERARCHY_PLATFORMS = "select UUID,PLATFORMS from uuid_hierarchy where platforms is not null and platforms like '%,%'";

    private static final String UPDATE_PLATFORMS = " update uuid_hierarchy set platforms = ? where uuid=lower(?)";

    private static final int batchsize = 1000;

    private static final String GET_UUID_PLATFORMS =
            " SELECT UUID,RTRIM(XMLAGG(XMLELEMENT(e,platform_id || ',')).EXTRACT('//text()'),',') PLATFORMS from " +
                    " (SELECT  distinct uuid, platform_id " +
                    " FROM   shipped_biospecimen b, shipped_biospecimen_file bf, file_to_archive fa, archive_info a, center c " +
                    " WHERE  b.is_viewable=1 " +
                    " AND    b.shipped_biospecimen_id = bf.shipped_biospecimen_id " +
                    " AND    bf.file_id=fa.file_id" +
                    " AND    fa.archive_id = a.archive_id" +
                    " AND    a.is_latest = 1" +
                    " AND    a.center_id=c.center_id" +
                    " AND    c.center_type_code != 'BCR')" +
                    " group by uuid";

    private static final String UPDATE_UUID_HIERARCHY_PLATFORM =
            " update uuid_hierarchy set platforms = " +
                    " case " +
                    "  when (platforms is null) then ? " +
                    "  else platforms || ? " +
                    " END " +
                    " where  UUID in (" +
                    " SELECT uuid FROM uuid_hierarchy " +
                    " START WITH uuid= ? " +
                    " CONNECT BY uuid = prior parent_uuid)";

    private static final String PLACEMENT_HOLDER = "PLACEMENT_HOLDER";
    private static final String REPLACE_FILTER = "REPLACE_FILTER";
    private static final String GET_META_DATA = "select " +
            "disease_abbreviation," +
            "uuid," +
            "parent_uuid," +
            "item_type_id," +
            "tss_code," +
            "center_id_bcr," +
            "batch_number," +
            "barcode," +
            "participant_number," +
            "sample_type_code," +
            "sample_sequence," +
            "portion_sequence," +
            "portion_analyte_code," +
            "plate_id," +
            "receiving_center_id," +
            "center_code," +
            "slide," +
            "slide_layer," +
            "is_shipped," +
            "shipped_date from uuid_hierarchy " +
            " where " + REPLACE_FILTER + " in ( " + PLACEMENT_HOLDER + ") ";

    private static final String GET_SHIPPED_DATE = "select shipped_date from shipped_biospecimen where uuid = lower(?)";

    protected Date getShippedDateFromDB(final String uuid) {
        try {
            return getSimpleJdbcTemplate().queryForObject(GET_SHIPPED_DATE, Date.class,
                    new Object[]{uuid == null ? "" : uuid});
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /*
   * get all uuids for a family starting with either the parent uuid or parent barcode
   * depending on the value of parentFormat. This will return them in a format that can be used
   * as an argument list to bulk insert
   *
   * @param parent
   * @param parentFormat
   * @return children
    */
    public List<UUIDDetail> getChildUUIDs(final String parent, final String parentFormat) {
        final String select = ConstantValues.BARCODE.equals(parentFormat) ? GET_CHILDREN_BY_BARCODE : GET_CHILDREN_BY_UUID;
        return getSimpleJdbcTemplate().getJdbcOperations().query(select,
                new ParameterizedRowMapper<UUIDDetail>() {
                    public UUIDDetail mapRow(final ResultSet resultSet, final int i) throws SQLException {
                        UUIDDetail uuid = new UUIDDetail();
                        uuid.setUuid(resultSet.getString("uuid"));
                        return uuid;
                    }
                }, new Object[]{parent});
    }

    /**
     * Get a list grouping platforms and uuid
     *
     * @return a list of uuid - platforms List maps
     */
    @Override
    public Map<String, String> getPlatformsPerUUID() {
        final Map<String, String> uuidPlatforms = new HashMap<String, String>();
        List<Map<String, String>> platformsListMap = getSimpleJdbcTemplate().getJdbcOperations().
                query(GET_UUID_PLATFORMS, new ParameterizedRowMapper<Map<String, String>>() {
                    public Map<String, String> mapRow(final ResultSet rs, final int i) throws SQLException {
                        uuidPlatforms.put(rs.getString("UUID"), rs.getString("PLATFORMS"));
                        return uuidPlatforms;
                    }
                });
        if (platformsListMap != null && platformsListMap.size() > 0) {
            return platformsListMap.get(0);
        } else {
            return (new HashMap<String, String>());
        }
    }

    @Override
    public void persistUUIDHierarchy(final List<BiospecimenMetaData> uuidHierarchyList) {
        // initialize bean validator
        Validator validator = getValidator();
        for (BiospecimenMetaData uuidHierarchy : uuidHierarchyList) {
            if (uuidHierarchy != null) {
                // validate the bean
                Set<ConstraintViolation<BiospecimenMetaData>> violationList = validator.validate(uuidHierarchy);
                if (!violationList.isEmpty()) {
                    StringBuilder validationError = new StringBuilder();
                    for (ConstraintViolation<BiospecimenMetaData> violation : violationList) {
                        validationError.append(violation.getPropertyPath() + " " + violation.getMessage() + ",");
                    }
                    throw new IllegalArgumentException(" Unable to persist BiospecimenMetaData object , the object has the following violations " + validationError.toString());
                }
                BiospecimenMetaData transformedMetaData = transformHierarchyValues(uuidHierarchy);
                // check if the uuid is already there , if it is then insert

                int result = getSimpleJdbcTemplate().queryForInt(GET_UUID_FROM_HIERARCHY, uuidHierarchy.getUuid());
                // doesn't exist so insert
                if (result == 0) {
                    Object[] valuesToInsert = biospecimenMetaDataToArray(transformedMetaData).toArray();
                    logger.info("About to insert UUID " + valuesToInsert[1] + " with parent UUID " + valuesToInsert[2]);
                    getSimpleJdbcTemplate().update(ADD_UUID_HIERARCHY, valuesToInsert);
                    logger.debug("Insertion of metadata in db done");
                } else {
                    //uuid exists therefore update
                    List<Object> updateElements = biospecimenMetaDataToArray(transformedMetaData);
                    //move uuid element to the bottom to match query placeholders
                    Object uuidElement = updateElements.remove(1);
                    updateElements.add(uuidElement);
                    getSimpleJdbcTemplate().update(UPDATE_UUID_HIERARCHY, updateElements.toArray());
                    logger.debug("Update of metadata in db done");
                }
            }
        }
    }

    /**
     * Updates uuid_hierarchy with platforms
     *
     * @param uuid      uuid for which to update platforms . can't be empty
     * @param platforms comma delimited platforms string to update
     * @return number of records updated
     */
    protected int updateUUIDHierarchyPlatforms(String uuid, String platforms) {
        if (StringUtils.isEmpty(uuid)) {
            throw new IllegalArgumentException(" Unable update uuid_hierarchy for an empty uuid");
        }
        // run recursive udpates
        return getSimpleJdbcTemplate().update(UPDATE_UUID_HIERARCHY_PLATFORM, platforms,
                "," + platforms, uuid);
    }

    @Override
    public void updateAllUUIDHierarchyPlatforms() {
        // get a list of platforms / uuid combos . The uuids returned are only for aliquot/shipped portion level
        Map<String, String> uuidPlatforms = getPlatformsPerUUID();
        if (uuidPlatforms.size() > 0) {
            for (String uuid : uuidPlatforms.keySet()) {
                int platformsUpdated = updateUUIDHierarchyPlatforms(uuid, uuidPlatforms.get(uuid));
                if (platformsUpdated <= 0) {
                    // if updated nothing , print a warning in the logs, but do not add it to context.
                    // should help with debugging.
                    logger.debug("Warning: Platforms in uuid_hierarchy did not get updated for " + uuid);
                }
            }
        }
    }


    @Override
    public void deletePlatforms() {
        getSimpleJdbcTemplate().update("update uuid_hierarchy set platforms=null");
    }

    @Override
    public void deduplicatePlatforms() {
        final Map<String, String> uuidComboList = getUUIDComboList();
        if (uuidComboList != null && uuidComboList.size() > 0) {
            Map<String, String> deduplicatedPlatforms = getDeduplicatePlatforms(uuidComboList);
            updateDeduplicatedPlatforms(deduplicatedPlatforms);
        }
    }

    @Override
    public Map<String, BiospecimenMetaData> getMetaData(final List<String> uuidsORBarcodes) {

        final Map<String, BiospecimenMetaData> metaDataByUUIDORBarcode = new HashMap<String, BiospecimenMetaData>();
        final List<String> uuids = new ArrayList<String>();
        final List<String> barcodes = new ArrayList<String>();
        for (final String uuidORBarcode : uuidsORBarcodes) {
            if (commonBarcodeAndUUIDValidator.validateUUIDFormat(uuidORBarcode)) {
                uuids.add(uuidORBarcode);
            } else {
                barcodes.add(uuidORBarcode);
            }
        }
        if (uuids.size() > 0) {
            metaDataByUUIDORBarcode.putAll(getMetaData(uuids, "uuid"));
        }
        if (barcodes.size() > 0) {
            metaDataByUUIDORBarcode.putAll(getMetaData(barcodes, "barcode"));
        }

        return metaDataByUUIDORBarcode;
    }

    private Map<String, BiospecimenMetaData> getMetaData(final List<String> uuidsORBarcodes, final String filter) {

        final Map<String, BiospecimenMetaData> metaDataByUUIDORBarcode = new HashMap<String, BiospecimenMetaData>();
        int startIndex = 0;
        int endIndex = 0;
        while (endIndex < uuidsORBarcodes.size()) {
            startIndex = endIndex;
            endIndex = ((uuidsORBarcodes.size() - endIndex) > MAX_IN_CLAUSE) ? (endIndex + MAX_IN_CLAUSE) : uuidsORBarcodes.size();
            List<String> subList = uuidsORBarcodes.subList(startIndex, endIndex);

            String query = GET_META_DATA.replaceAll(PLACEMENT_HOLDER, StringUtil.createPlaceHolderString(subList.size()));
            query = query.replaceAll(REPLACE_FILTER, filter);

            getSimpleJdbcTemplate().getJdbcOperations().query(query, subList.toArray(),
                    new RowCallbackHandler() {
                        @Override
                        public void processRow(ResultSet rs) throws SQLException {
                            final BiospecimenMetaData biospecimenMetaData = new BiospecimenMetaData();
                            biospecimenMetaData.setUuid(rs.getString("uuid"));
                            biospecimenMetaData.setUuidType(rs.getString("item_type_id"));
                            biospecimenMetaData.setDisease(rs.getString("disease_abbreviation"));
                            biospecimenMetaData.setParticipantId(rs.getString("participant_number"));
                            biospecimenMetaData.setBarcode(rs.getString("barcode"));
                            biospecimenMetaData.setTissueSourceSite(rs.getString("tss_code"));
                            biospecimenMetaData.setSampleType(rs.getString("sample_type_code"));
                            biospecimenMetaData.setVialId(rs.getString("sample_sequence"));
                            // get the bcr assigned receiving center id
                            biospecimenMetaData.setReceivingCenter(rs.getString("receiving_center_id"));
                            biospecimenMetaData.setAnalyteType(rs.getString("portion_analyte_code"));
                            biospecimenMetaData.setPortionId(rs.getString("portion_sequence"));
                            biospecimenMetaData.setPlateId(rs.getString("plate_id"));
                            biospecimenMetaData.setSlide(rs.getString("slide"));
                            biospecimenMetaData.setSlideLayer(rs.getString("slide_layer"));
                            biospecimenMetaData.setShipped(rs.getInt("is_shipped") == 1);
                            biospecimenMetaData.setShippedDate(rs.getTimestamp("shipped_date"));
                            biospecimenMetaData.setCenterCode(rs.getString("center_code"));
                            metaDataByUUIDORBarcode.put(rs.getString(filter), biospecimenMetaData);
                        }
                    });

        }

        return metaDataByUUIDORBarcode;
    }


    private Map<String, String> getUUIDComboList() {
        final Map<String, String> uuidPlatforms = new HashMap<String, String>();
        List<Map<String, String>> platformsListMap = getSimpleJdbcTemplate().getJdbcOperations().
                query(GET_UUID_HIERARCHY_PLATFORMS, new ParameterizedRowMapper<Map<String, String>>() {
                    public Map<String, String> mapRow(final ResultSet rs, final int i) throws SQLException {
                        uuidPlatforms.put(rs.getString("UUID"), rs.getString("PLATFORMS"));
                        return uuidPlatforms;
                    }
                });
        return uuidPlatforms;
    }

    private Map<String, String> getDeduplicatePlatforms(final Map<String, String> uuidComboMap) {
        final Map<String, String> deduplicatedPlatforms = new HashMap<String, String>();

        for (String uuid : uuidComboMap.keySet()) {
            String platforms = uuidComboMap.get(uuid);
            String[] platrformsArray = platforms.split(",");
            Set<String> uniquePlatfroms = new HashSet<String>();
            for (String platform : platrformsArray) {
                uniquePlatfroms.add(platform);
            }
            deduplicatedPlatforms.put(uuid, StringUtils.join(uniquePlatfroms, ","));
        }
        return deduplicatedPlatforms;
    }

    private void updateDeduplicatedPlatforms(Map<String, String> deduplicatedPlatforms) {
        List<Object[]> updateBatch = new ArrayList<Object[]>();

        for (String uuid : deduplicatedPlatforms.keySet()) {
            Object[] uuidPltaformCombo = {deduplicatedPlatforms.get(uuid), uuid};
            updateBatch.add(uuidPltaformCombo);

            if (updateBatch.size() == batchsize) {
                updatePlatforms(updateBatch);
                updateBatch.clear();
            }
        }
        // write off the remaining records
        if (updateBatch.size() > 0) {
            updatePlatforms(updateBatch);
            updateBatch.clear();
        }
    }

    private void updatePlatforms(List<Object[]> uuidPltaformCombo) {
        getSimpleJdbcTemplate().batchUpdate(UPDATE_PLATFORMS, uuidPltaformCombo);
    }

    /**
     * Method to retrieve a uuid item type corresponding to a given XML type
     *
     * @param uuidHierarchy use to get uuidType for the query
     * @return uuidHierarchy with uuidTypeId populated
     */
    private BiospecimenMetaData transformHierarchyValues(final BiospecimenMetaData uuidHierarchy) {
        // transform uuid type lookup sample type
        uuidHierarchy.setUuidType(getUUIDItemTypeId(uuidHierarchy.getUuidType()).toString());

        // if receiving center exists for the type, transform from XSD defined center to BCR defined center
        if (StringUtils.isNotEmpty(uuidHierarchy.getReceivingCenter())) {
            uuidHierarchy.setCenterCode(uuidHierarchy.getReceivingCenter());
            uuidHierarchy.setReceivingCenter(getTcgaCenterIdFromBcrId(uuidHierarchy.getReceivingCenter()).toString());
        }

        // set shippedDate and shipped flag
        final Date shippedDate = getShippedDateFromDB(uuidHierarchy.getUuid());
        uuidHierarchy.setShippedDate(shippedDate);
        uuidHierarchy.setShipped(shippedDate == null ? false : true);

        return uuidHierarchy;
    }

    @Cacheable(cacheName = "uuidItemTypeCache",
            keyGenerator = @KeyGenerator(
                    name = "HashCodeCacheKeyGenerator", properties = @Property(name = "includeMethod", value = "false")
            )
    )
    public Long getUUIDItemTypeId(final String uuidType) {
        return getSimpleJdbcTemplate().queryForObject(GET_UUID_ITEM_TYPE, Long.class, uuidType);
    }

    @Cacheable(cacheName = "bcrCenterIdCache",
            keyGenerator = @KeyGenerator(
                    name = "HashCodeCacheKeyGenerator", properties = @Property(name = "includeMethod", value = "false")
            )
    )
    public Long getTcgaCenterIdFromBcrId(final String receivingCenterId) {
        return getSimpleJdbcTemplate().queryForObject(GET_RECEIVING_CENTER_ID, Long.class, receivingCenterId);
    }

    public Long getBcrCenterIdFromTcgaId(final String tcgaCenterId) {
        return getSimpleJdbcTemplate().queryForObject(GET_BCR_RECEIVING_CENTER_ID, Long.class, tcgaCenterId);
    }

    public CommonBarcodeAndUUIDValidator getCommonBarcodeAndUUIDValidator() {
        return commonBarcodeAndUUIDValidator;
    }

    public void setCommonBarcodeAndUUIDValidator(CommonBarcodeAndUUIDValidator commonBarcodeAndUUIDValidator) {
        this.commonBarcodeAndUUIDValidator = commonBarcodeAndUUIDValidator;
    }

    protected List<Object> biospecimenMetaDataToArray(final BiospecimenMetaData transformedUuidHierarchy) {

        if (transformedUuidHierarchy == null) {
            throw new IllegalArgumentException("Unable to convert an empty BiospecimenMetaData object to an Object []");
        }
        ArrayList<Object> uuidElements = new ArrayList<Object>();
        uuidElements.add(transformedUuidHierarchy.getDisease());
        uuidElements.add(transformedUuidHierarchy.getUuid());
        if (StringUtils.isNotEmpty(transformedUuidHierarchy.getParentUUID())) {
            uuidElements.add(transformedUuidHierarchy.getParentUUID());
        } else {
            uuidElements.add(null);
        }
        uuidElements.add(new BigDecimal(transformedUuidHierarchy.getUuidType()));
        uuidElements.add(transformedUuidHierarchy.getTissueSourceSite());
        uuidElements.add(new BigDecimal(transformedUuidHierarchy.getBcr()));
        uuidElements.add(new BigDecimal(transformedUuidHierarchy.getBatch()));
        if (StringUtils.isNotEmpty(transformedUuidHierarchy.getBarcode())) {
            uuidElements.add(transformedUuidHierarchy.getBarcode());
        } else {
            uuidElements.add(null);
        }
        uuidElements.add(transformedUuidHierarchy.getParticipantId());
        if (StringUtils.isNotEmpty(transformedUuidHierarchy.getSampleType())) {
            uuidElements.add(transformedUuidHierarchy.getSampleType());
        } else {
            uuidElements.add(null);
        }
        if (StringUtils.isNotEmpty(transformedUuidHierarchy.getVialId())) {
            uuidElements.add(transformedUuidHierarchy.getVialId());
        } else {
            uuidElements.add(null);
        }
        if (StringUtils.isNotEmpty(transformedUuidHierarchy.getPortionId())) {
            uuidElements.add(transformedUuidHierarchy.getPortionId());
        } else {
            uuidElements.add(null);
        }
        if (StringUtils.isNotEmpty(transformedUuidHierarchy.getAnalyteType())) {
            uuidElements.add(transformedUuidHierarchy.getAnalyteType());
        } else {
            uuidElements.add(null);
        }
        if (StringUtils.isNotEmpty(transformedUuidHierarchy.getPlateId())) {
            uuidElements.add(transformedUuidHierarchy.getPlateId());
        } else {
            uuidElements.add(null);
        }
        // adding original center_code before it got transformed by a look up
        if (StringUtils.isNotEmpty(transformedUuidHierarchy.getCenterCode())) {
            uuidElements.add(transformedUuidHierarchy.getCenterCode());
        } else {
            uuidElements.add(null);
        }
        if (StringUtils.isNotEmpty(transformedUuidHierarchy.getReceivingCenter())) {
            uuidElements.add(new BigDecimal(transformedUuidHierarchy.getReceivingCenter()));
        } else {
            uuidElements.add(null);
        }
        if (StringUtils.isNotEmpty(transformedUuidHierarchy.getSlide())) {
            uuidElements.add(transformedUuidHierarchy.getSlide());
        } else {
            uuidElements.add(null);
        }
        if (StringUtils.isNotEmpty(transformedUuidHierarchy.getSlideLayer())) {
            uuidElements.add(transformedUuidHierarchy.getSlideLayer());
        } else {
            uuidElements.add(null);
        }
        final Boolean shipped = transformedUuidHierarchy.getShipped();
        if (shipped != null) {
            uuidElements.add(shipped ? 1 : 0);
        } else {
            uuidElements.add(0);
        }
        uuidElements.add(transformedUuidHierarchy.getShippedDate());
        return uuidElements;
    }

    public void setValidator(final Validator validator) {
        this.validator = validator;
    }

    public Validator getValidator() {

        return this.validator;
    }
}