package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.BiospecimenMetaData;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDHierarchyQueries;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Test class for UUIDHierarchyQueriesJDBCImpl
 *
 * @author Shelley Alonso
 *         Last updated by: $Shelley Alonso$
 * @version $Rev$
 */

public class UUIDHierarchyQueriesJDBCImplTestSlow extends DBUnitTestCase {
    // common schema
    private static final String PROPERTIES_FILE = "unittest.properties";
    private static final String TEST_DATA_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "dao/UUIDHierarchyQueriesTestData.xml";

    private static final String appContextFile = "samples/applicationContext-dbunit.xml";
    private final ApplicationContext appContext;
    private final UUIDHierarchyQueries queries;
    private SimpleJdbcTemplate sjdbc;

    public UUIDHierarchyQueriesJDBCImplTestSlow() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
        appContext = new ClassPathXmlApplicationContext(appContextFile);
        queries = (UUIDHierarchyQueries) appContext.getBean("uuidHierarchyQueries");
        sjdbc = new SimpleJdbcTemplate(getDataSource());
    }

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.CLEAN_INSERT;
    }

    @Override
    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.DELETE_ALL;
    }

    @Test
    public void testGetChildUuidsByBarcode() {
        List<UUIDDetail> children = queries.getChildUUIDs("TCGA-28-2499", ConstantValues.BARCODE);
        assertEquals(children.size(), 36);
        assertEquals("d88b35a3-a291-457a-b15b-a314859b25c5", children.get(0).getUuid());
        assertEquals("ca209848-8c54-4696-b61f-3627dfd64d1b", children.get(35).getUuid());
    }

    @Test
    public void testGetChildUuidsByUUID() {
        List<UUIDDetail> children = queries.getChildUUIDs("d88b35a3-a291-457a-b15b-a314859b25c5", ConstantValues.UUID);
        assertEquals(children.size(), 36);
        assertEquals("d88b35a3-a291-457a-b15b-a314859b25c5", children.get(0).getUuid());
        assertEquals("ca209848-8c54-4696-b61f-3627dfd64d1b", children.get(35).getUuid());
    }

    @Test
    public void testBiospecimenMetaDataToArray() {
        UUIDHierarchyQueriesJDBCImpl queriesImpl = new UUIDHierarchyQueriesJDBCImpl();
        BiospecimenMetaData metaData = new BiospecimenMetaData();
        metaData.setBarcode("TCGA-");
        metaData.setUuid("123");
        metaData.setDisease("gbm");
        metaData.setUuidType("3");
        metaData.setTissueSourceSite("tss");
        metaData.setBcr("4");
        metaData.setBatch("5");
        metaData.setParticipantId("5");
        List<Object> metadataArray = queriesImpl.biospecimenMetaDataToArray(metaData);
        assertEquals(metadataArray.get(7), "TCGA-");
    }

    @Test
    public void testGetShippedDateFromDBFound() throws Exception {
        UUIDHierarchyQueriesJDBCImpl queriesImpl = new UUIDHierarchyQueriesJDBCImpl();
        queriesImpl.setJdbcTemplate(new JdbcTemplate(getDataSource()));
        final Date result = queriesImpl.getShippedDateFromDB("uuid2");
        assertEquals("2010-01-11 16:44:50.0", result.toString());
    }

    @Test
    public void testGetShippedDateFromDBNotFound() throws Exception {
        UUIDHierarchyQueriesJDBCImpl queriesImpl = new UUIDHierarchyQueriesJDBCImpl();
        queriesImpl.setJdbcTemplate(new JdbcTemplate(getDataSource()));
        final Date result = queriesImpl.getShippedDateFromDB("uuid16");
        assertNull(result);
    }

    @Test
    public void testGetShippedDateFromDBNullDate() throws Exception {
        final UUIDHierarchyQueriesJDBCImpl queriesImpl = new UUIDHierarchyQueriesJDBCImpl();
        queriesImpl.setJdbcTemplate(new JdbcTemplate(getDataSource()));
        final Date result = queriesImpl.getShippedDateFromDB("uuid1");
        assertNull(result);
    }

    @Test
    public void testGetShippedDateFromDBNullUuid() throws Exception {
        final UUIDHierarchyQueriesJDBCImpl queriesImpl = new UUIDHierarchyQueriesJDBCImpl();
        queriesImpl.setJdbcTemplate(new JdbcTemplate(getDataSource()));
        final Date result = queriesImpl.getShippedDateFromDB(null);
        assertNull(result);
    }

    @Test
    public void testGetShippedDateUppercaseUuid() {
        UUIDHierarchyQueriesJDBCImpl queriesImpl = new UUIDHierarchyQueriesJDBCImpl();
        queriesImpl.setJdbcTemplate(new JdbcTemplate(getDataSource()));
        final Date result = queriesImpl.getShippedDateFromDB("UuId2");
        assertEquals("2010-01-11 16:44:50.0", result.toString());
    }

    @Test
    public void testBiospecimenMetaDataToArrayNullParam() {
        UUIDHierarchyQueriesJDBCImpl queriesImpl = new UUIDHierarchyQueriesJDBCImpl();
        try {
            List<Object> metadataArray = queriesImpl.biospecimenMetaDataToArray(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Unable to convert an empty BiospecimenMetaData object to an Object []", e.getMessage());
        }
    }

    @Test
    public void testBiospecimenMetaDataToArrayBeanValidationError() {
        BiospecimenMetaData metaData = new BiospecimenMetaData();
        List<BiospecimenMetaData> metadataList = new ArrayList<BiospecimenMetaData>();
        metadataList.add(metaData);
        try {
            queries.persistUUIDHierarchy(metadataList);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Unable to persist BiospecimenMetaData object"));
        }
    }

    @Test
    public void testPersistUUIDHierarchyInsert() {
        BiospecimenMetaData metaData = new BiospecimenMetaData();
        metaData.setUuid("uuID2"); // should be lowercased in the db
        metaData.setDisease("gbm");
        metaData.setUuidType("surgery");
        metaData.setTissueSourceSite("tss");
        metaData.setBcr("4");
        metaData.setBatch("5");
        metaData.setParticipantId("5");
        List<BiospecimenMetaData> metadataList = new ArrayList<BiospecimenMetaData>();
        metadataList.add(metaData);
        queries.persistUUIDHierarchy(metadataList);

        JdbcTemplate template = new JdbcTemplate(getDataSource());
        BiospecimenMetaData result = (BiospecimenMetaData) template.queryForObject("select * from uuid_hierarchy where uuid = 'uuid2'", uuidBrowserRowMapper);
        assertEquals("uuid2", result.getUuid());
        assertEquals("gbm", result.getDisease());
        assertEquals("10", result.getUuidType());
        assertEquals("tss", result.getTissueSourceSite());
        assertEquals("4", result.getBcr());
        assertEquals("5", result.getBatch());
        assertEquals(result.getCreateDate(),result.getUpdateDate());
        assertEquals("5", result.getParticipantId());
        assertEquals(null, result.getPlatform());
        assertNull(result.getPlateId());
        assertNull(result.getAnalyteType());
        assertNull(result.getBarcode());
        assertNull(result.getParentUUID());
        assertNull(result.getPortionId());
        assertNull(result.getReceivingCenter());
        assertNull(result.getSampleType());
        assertNull(result.getSlide());
        assertNull(result.getSlideLayer());
        assertNull(result.getVialId());
        assertNotNull(result.getCreateDate());
        assertTrue(result.getShipped());
        assertEquals("2010-01-11 16:44:50.0", result.getShippedDate().toString());
    }

    @Test
    public void testPersistUUIDHierarchyInsertAll() {
        BiospecimenMetaData metaData = new BiospecimenMetaData();
        metaData.setAnalyteType("6");
        metaData.setBarcode("dcff");
        metaData.setBatch("5");
        metaData.setBcr("4");
        metaData.setDisease("gbm");
        metaData.setParticipantId("22");
        metaData.setPlateId("333");
        metaData.setPortionId("1");
        metaData.setReceivingCenter("03");
        metaData.setSampleType("2");
        metaData.setSlide("s");
        metaData.setSlideLayer("sl");
        metaData.setTissueSourceSite("tss");
        metaData.setUuid("uuid2");
        metaData.setUuidType("patient");
        metaData.setVialId("6");

        List<BiospecimenMetaData> metadataList = new ArrayList<BiospecimenMetaData>();
        metadataList.add(metaData);
        queries.persistUUIDHierarchy(metadataList);

        JdbcTemplate template = new JdbcTemplate(getDataSource());
        BiospecimenMetaData result = (BiospecimenMetaData) template.queryForObject("select * from uuid_hierarchy where uuid = 'uuid2'", uuidBrowserRowMapper);
        assertEquals("uuid2", result.getUuid());
        assertEquals("gbm", result.getDisease());
        assertEquals("1", result.getUuidType());
        assertEquals("tss", result.getTissueSourceSite());
        assertEquals("4", result.getBcr());
        assertEquals("5", result.getBatch());
        assertEquals("22", result.getParticipantId());
        assertEquals("333", result.getPlateId());
        assertEquals("6", result.getAnalyteType());
        assertEquals("dcff", result.getBarcode());
        assertNull(result.getParentUUID());
        assertEquals("1", result.getPortionId());
        assertEquals("3", result.getReceivingCenter());
        assertEquals("03", result.getCenterCode());
        assertEquals("2", result.getSampleType());
        assertEquals("s", result.getSlide());
        assertEquals("sl", result.getSlideLayer());
        assertEquals("6", result.getVialId());
        assertNotNull(result.getCreateDate());
        assertTrue(result.getShipped());
        assertEquals("2010-01-11 16:44:50.0", result.getShippedDate().toString());
        assertEquals(result.getCreateDate(),result.getUpdateDate());
    }

    @Test
    public void testPersistUUIDHierarchyUpdate() throws InterruptedException {
        BiospecimenMetaData metaData = new BiospecimenMetaData();
        metaData.setUuid("UUID2"); // start with uppercase and make sure it still works
        metaData.setDisease("gbm");
        metaData.setUuidType("patient");
        metaData.setTissueSourceSite("tss");
        metaData.setBcr("4");
        metaData.setBatch("5");
        metaData.setParticipantId("5");
        metaData.setReceivingCenter("03");
        List<BiospecimenMetaData> metadataList = new ArrayList<BiospecimenMetaData>();
        metadataList.add(metaData);
        queries.persistUUIDHierarchy(metadataList);
        Thread.sleep(5000);
        metaData.setDisease("gbmUpdate");
        metaData.setUuidType("patient");
        metaData.setReceivingCenter("06");
        queries.persistUUIDHierarchy(metadataList);
        JdbcTemplate template = new JdbcTemplate(getDataSource());
        BiospecimenMetaData result = template.queryForObject("select * from uuid_hierarchy where uuid = 'uuid2'", uuidBrowserRowMapper);
        assertEquals("gbmUpdate", result.getDisease());
        assertEquals("06", result.getCenterCode());
    }

    @Test
    public void testGetUUIDItemType() {
        assertEquals(new Long(1), queries.getUUIDItemTypeId("patient"));

    }

    @Test
    public void testGetUUIDItemTypeFromCache() {
        // read it from database
        assertEquals(new Long(1), queries.getUUIDItemTypeId("patient"));
        getSimpleJdbcTemplate().update("delete from uuid_item_type where xml_name like 'patient'");
        //make sure the entry is not in the database
        assertEquals(0, (getSimpleJdbcTemplate().queryForList(" select * from uuid_item_type where xml_name like 'patient'")).size());
        // Read it from cache
        assertEquals(new Long(1), queries.getUUIDItemTypeId("patient"));
    }

    @Test
    public void testGetPlatformsPerUUID() {
        Map<String, String> platformUUIDList = queries.getPlatformsPerUUID();
        assertNotNull(platformUUIDList);
        assertEquals("1,3,2", platformUUIDList.get("uuid2"));
        assertEquals("1,2", platformUUIDList.get("uuid3"));
        assertEquals("1", platformUUIDList.get("uuid4"));
    }

    @Test
    public void testGetTcgaCenterIdFromBcrId() {
        // read it from database
        assertEquals(new Long(6), queries.getTcgaCenterIdFromBcrId("02"));

        getSimpleJdbcTemplate().update("delete from center_to_bcr_center where bcr_center_id = '02'");
        //make sure the entry is not in the database
        assertEquals(0, (getSimpleJdbcTemplate().queryForList(" select * from center_to_bcr_center where bcr_center_id = '02'")).size());
        // Read it from cache
        assertEquals(new Long(6), queries.getTcgaCenterIdFromBcrId("02"));
    }

    @Test
    public void testUUIDHierarchyPlatforms() {
        UUIDHierarchyQueriesJDBCImpl hierarchyQueries = new UUIDHierarchyQueriesJDBCImpl();
        JdbcTemplate template = new JdbcTemplate(getDataSource());
        hierarchyQueries.setJdbcTemplate(template);
        int updateCount = hierarchyQueries.updateUUIDHierarchyPlatforms("1dd740dc-2752-4511-ac12-45766c8e2737", "12,2,3");
        assertEquals(5, updateCount);
        // check the recursion tree to make sure all parents got updated
        String platform = (String) template.queryForObject(" SELECT distinct platforms FROM uuid_hierarchy START WITH uuid= ? CONNECT BY uuid = prior parent_uuid ",
                new Object[]{"1dd740dc-2752-4511-ac12-45766c8e2737"}, String.class);
        assertEquals("12,2,3", platform);

        // append 3 more platforms
        updateCount = hierarchyQueries.updateUUIDHierarchyPlatforms("1dd740dc-2752-4511-ac12-45766c8e2737", "4,36,22");
        assertEquals(5, updateCount);

        // check the recursion tree to make sure all parents got updated
        platform = (String) template.queryForObject(" SELECT distinct platforms FROM uuid_hierarchy START WITH uuid= ? CONNECT BY uuid = prior parent_uuid ",
                new Object[]{"1dd740dc-2752-4511-ac12-45766c8e2737"}, String.class);
        assertEquals("12,2,3,4,36,22", platform);
    }

    @Test
    public void testUUIDHierarchyBadInput() {
        UUIDHierarchyQueriesJDBCImpl hierarchyQueries = new UUIDHierarchyQueriesJDBCImpl();
        JdbcTemplate template = new JdbcTemplate(getDataSource());
        hierarchyQueries.setJdbcTemplate(template);

        try {
            int updateCount = hierarchyQueries.updateUUIDHierarchyPlatforms("", "12,2,3");
            fail();
        } catch (IllegalArgumentException e) {
            // swallow
        }
    }

    @Test
    public void testUUIDHierarchynonExistingUUID() {
        UUIDHierarchyQueriesJDBCImpl hierarchyQueries = new UUIDHierarchyQueriesJDBCImpl();
        JdbcTemplate template = new JdbcTemplate(getDataSource());
        hierarchyQueries.setJdbcTemplate(template);
        int updateCount = hierarchyQueries.updateUUIDHierarchyPlatforms("11111", "12,2,3");
        assertEquals(0, updateCount);
    }

    @Test
    public void testUpdateAllUUIDHierarchyPlatforms() {
        List<BiospecimenMetaData> metadataList = new ArrayList<BiospecimenMetaData>();
        BiospecimenMetaData metaData = new BiospecimenMetaData();
        metaData.setAnalyteType("6");
        metaData.setBarcode("dcff");
        metaData.setBatch("5");
        metaData.setBcr("4");
        metaData.setDisease("gbm");
        metaData.setParticipantId("22");
        metaData.setPlateId("333");
        metaData.setPortionId("1");
        metaData.setReceivingCenter("03");
        metaData.setSampleType("2");
        metaData.setSlide("s");
        metaData.setSlideLayer("sl");
        metaData.setTissueSourceSite("tss");
        metaData.setUuid("uuid5");
        metaData.setUuidType("patient");
        metaData.setVialId("6");
        metadataList.add(metaData);

        metaData = new BiospecimenMetaData();
        metaData.setAnalyteType("6");
        metaData.setBarcode("dcff1");
        metaData.setBatch("5");
        metaData.setBcr("4");
        metaData.setDisease("gbm");
        metaData.setParticipantId("22");
        metaData.setPlateId("333");
        metaData.setPortionId("1");
        metaData.setReceivingCenter("03");
        metaData.setSampleType("2");
        metaData.setSlide("s");
        metaData.setSlideLayer("sl");
        metaData.setTissueSourceSite("tss");
        metaData.setUuid("uuid2");
        metaData.setParentUUID("uuid5");
        metaData.setUuidType("patient");
        metaData.setVialId("6");
        metadataList.add(metaData);

        metaData = new BiospecimenMetaData();
        metaData.setAnalyteType("6");
        metaData.setBarcode("dcff2");
        metaData.setBatch("5");
        metaData.setBcr("4");
        metaData.setDisease("gbm");
        metaData.setParticipantId("22");
        metaData.setPlateId("333");
        metaData.setPortionId("1");
        metaData.setReceivingCenter("03");
        metaData.setSampleType("2");
        metaData.setSlide("s");
        metaData.setSlideLayer("sl");
        metaData.setTissueSourceSite("tss");
        metaData.setUuid("uuid3");
        metaData.setUuidType("patient");
        metaData.setVialId("6");
        metadataList.add(metaData);

        queries.persistUUIDHierarchy(metadataList);
        queries.updateAllUUIDHierarchyPlatforms();

        JdbcTemplate template = new JdbcTemplate(getDataSource());
        BiospecimenMetaData resultForUUID2 = (BiospecimenMetaData) template.queryForObject("select * from uuid_hierarchy where uuid = 'uuid2'", uuidBrowserRowMapper);
        BiospecimenMetaData resultForUUID5 = (BiospecimenMetaData) template.queryForObject("select * from uuid_hierarchy where uuid = 'uuid5'", uuidBrowserRowMapper);
        BiospecimenMetaData resultForUUID3 = (BiospecimenMetaData) template.queryForObject("select * from uuid_hierarchy where uuid = 'uuid3'", uuidBrowserRowMapper);

        assertTrue(resultForUUID2.getPlatform().equals("1,3,2"));
        assertTrue(resultForUUID5.getPlatform().equals("1,3,2"));
        assertTrue(resultForUUID3.getPlatform().equals("1,2"));
    }


    @Test
    public void testDeletePlatforms() {
        setUpDeduplicationCase();
        queries.deletePlatforms();
        JdbcTemplate template = new JdbcTemplate(getDataSource());
        assertNull(template.queryForObject("select platforms from uuid_hierarchy where uuid = 'uuid2'", String.class));
    }

    @Test
    public void testDeduplicatePlatforms() {
        // set up test records
        setUpDeduplicationCase();
        queries.deduplicatePlatforms();
        // verify the results
        JdbcTemplate template = new JdbcTemplate(getDataSource());
        assertEquals("3,2,1,5,4", (String) template.queryForObject("select platforms from uuid_hierarchy where uuid = 'uuid2'", String.class));
        assertEquals("1", (String) template.queryForObject("select platforms from uuid_hierarchy where uuid = 'uuid3'", String.class));
        assertEquals("3,2,1", (String) template.queryForObject("select platforms from uuid_hierarchy where uuid = 'uuid4'", String.class));
        assertEquals("1", (String) template.queryForObject("select platforms from uuid_hierarchy where uuid = 'uuid6'", String.class));
        assertNull(template.queryForObject("select platforms from uuid_hierarchy where uuid = 'uuid5'", String.class));
    }

    @Test
    public void testGetMetaData() {
        final List<String> uuids = Arrays.asList("1dd740dc-2752-4511-ac12-45766c8e2737",
                "17023eab-a4b8-4bec-94e7-4dbe8f3dded2");
        final Map<String, BiospecimenMetaData> existingMetaData = queries.getMetaData(uuids);
        assertEquals(2, existingMetaData.size());
        String str = existingMetaData.get("1dd740dc-2752-4511-ac12-45766c8e2737").getMetaDataString();
        str = existingMetaData.get("17023eab-a4b8-4bec-94e7-4dbe8f3dded2").getMetaDataString();
        assertEquals("[Barcode: 'TCGA-28-2499-01A-01R-1850-01', TSS: '28', Participant Id: '2449', Sample Type: '01', Vial Number: 'A', Portion Id: '01', Analyte Type Id: 'R', Plate Id: '1850', Center Code: '06']", existingMetaData.get("1dd740dc-2752-4511-ac12-45766c8e2737").getMetaDataString());
        assertEquals("[Barcode: 'TCGA-28-2499-01A-01D-0788-05', TSS: '28', Participant Id: '2449', Sample Type: '01', Vial Number: 'A', Portion Id: '01', Analyte Type Id: 'D', Plate Id: '0788', Center Code: '06']", existingMetaData.get("17023eab-a4b8-4bec-94e7-4dbe8f3dded2").getMetaDataString());
    }

    private void setUpDeduplicationCase() {
        List<BiospecimenMetaData> metadataList = new ArrayList<BiospecimenMetaData>();
        BiospecimenMetaData metaData = new BiospecimenMetaData();
        metaData.setAnalyteType("6");
        metaData.setBarcode("dcff");
        metaData.setBatch("5");
        metaData.setBcr("4");
        metaData.setDisease("gbm");
        metaData.setParticipantId("22");
        metaData.setPlateId("333");
        metaData.setPortionId("1");
        metaData.setReceivingCenter("03");
        metaData.setSampleType("2");
        metaData.setSlide("s");
        metaData.setSlideLayer("sl");
        metaData.setTissueSourceSite("tss");
        metaData.setUuid("uuid2");
        metaData.setUuidType("patient");
        metaData.setVialId("6");
        metadataList.add(metaData);

        metaData = new BiospecimenMetaData();
        metaData.setAnalyteType("6");
        metaData.setBarcode("dcff1");
        metaData.setBatch("5");
        metaData.setBcr("4");
        metaData.setDisease("gbm");
        metaData.setParticipantId("22");
        metaData.setPlateId("333");
        metaData.setPortionId("1");
        metaData.setReceivingCenter("03");
        metaData.setSampleType("2");
        metaData.setSlide("s");
        metaData.setSlideLayer("sl");
        metaData.setTissueSourceSite("tss");
        metaData.setUuid("uuid3");
        metaData.setUuidType("patient");
        metaData.setVialId("6");
        metadataList.add(metaData);

        metaData = new BiospecimenMetaData();
        metaData.setAnalyteType("6");
        metaData.setBarcode("dcff2");
        metaData.setBatch("5");
        metaData.setBcr("4");
        metaData.setDisease("gbm");
        metaData.setParticipantId("22");
        metaData.setPlateId("333");
        metaData.setPortionId("1");
        metaData.setReceivingCenter("03");
        metaData.setSampleType("2");
        metaData.setSlide("s");
        metaData.setSlideLayer("sl");
        metaData.setTissueSourceSite("tss");
        metaData.setUuid("uuid4");
        metaData.setUuidType("patient");
        metaData.setVialId("6");
        metadataList.add(metaData);

        metaData = new BiospecimenMetaData();
        metaData.setAnalyteType("6");
        metaData.setBarcode("dcff3");
        metaData.setBatch("5");
        metaData.setBcr("4");
        metaData.setDisease("gbm");
        metaData.setParticipantId("22");
        metaData.setPlateId("333");
        metaData.setPortionId("1");
        metaData.setReceivingCenter("03");
        metaData.setSampleType("2");
        metaData.setSlide("s");
        metaData.setSlideLayer("sl");
        metaData.setTissueSourceSite("tss");
        metaData.setUuid("uuid5");
        metaData.setUuidType("patient");
        metaData.setVialId("6");
        metadataList.add(metaData);

        metaData = new BiospecimenMetaData();
        metaData.setAnalyteType("6");
        metaData.setBarcode("dcff4");
        metaData.setBatch("5");
        metaData.setBcr("4");
        metaData.setDisease("gbm");
        metaData.setParticipantId("22");
        metaData.setPlateId("333");
        metaData.setPortionId("1");
        metaData.setReceivingCenter("03");
        metaData.setSampleType("2");
        metaData.setSlide("s");
        metaData.setSlideLayer("sl");
        metaData.setTissueSourceSite("tss");
        metaData.setUuid("uuid6");
        metaData.setUuidType("patient");
        metaData.setVialId("6");
        metadataList.add(metaData);

        JdbcTemplate template = new JdbcTemplate(getDataSource());
        queries.persistUUIDHierarchy(metadataList);
        template.update("update uuid_hierarchy set platforms = '1,2,3,3,3,3,1,4,5,5' where uuid = 'uuid2'");
        template.update("update uuid_hierarchy set platforms = '1,1,1,1,1,1' where uuid = 'uuid3'");
        template.update("update uuid_hierarchy set platforms = '1,2,3' where uuid = 'uuid4'");
        template.update("update uuid_hierarchy set platforms = null where uuid = 'uuid5'");
        template.update("update uuid_hierarchy set platforms = '1' where uuid = 'uuid6'");
    }

    private final ParameterizedRowMapper<BiospecimenMetaData> uuidBrowserRowMapper =
            new ParameterizedRowMapper<BiospecimenMetaData>() {
                public BiospecimenMetaData mapRow(ResultSet resultSet, int i) throws SQLException {
                    final BiospecimenMetaData uuidBrowser = new BiospecimenMetaData();
                    uuidBrowser.setDisease(resultSet.getString("disease_abbreviation"));
                    uuidBrowser.setUuid(resultSet.getString("uuid"));
                    uuidBrowser.setParentUUID(resultSet.getString("parent_uuid"));
                    uuidBrowser.setUuidType(resultSet.getString("item_type_id"));
                    uuidBrowser.setTissueSourceSite(resultSet.getString("tss_code"));
                    uuidBrowser.setBcr(resultSet.getString("center_id_bcr"));
                    uuidBrowser.setBatch(resultSet.getString("batch_number"));
                    uuidBrowser.setBarcode(resultSet.getString("barcode"));
                    uuidBrowser.setParticipantId(resultSet.getString("participant_number"));
                    uuidBrowser.setSampleType(resultSet.getString("sample_type_code"));
                    uuidBrowser.setVialId(resultSet.getString("sample_sequence"));
                    uuidBrowser.setPortionId(resultSet.getString("portion_sequence"));
                    uuidBrowser.setAnalyteType(resultSet.getString("portion_analyte_code"));
                    uuidBrowser.setPlateId(resultSet.getString("plate_id"));
                    uuidBrowser.setReceivingCenter(resultSet.getString("receiving_center_id"));
                    uuidBrowser.setCenterCode(resultSet.getString("center_code"));
                    uuidBrowser.setSlide(resultSet.getString("slide"));
                    uuidBrowser.setSlideLayer(resultSet.getString("slide_layer"));
                    uuidBrowser.setCreateDate(resultSet.getTimestamp("create_date"));
                    uuidBrowser.setUpdateDate(resultSet.getTimestamp("update_date"));
                    uuidBrowser.setPlatform(resultSet.getString("platforms"));
                    uuidBrowser.setShipped(resultSet.getInt("is_shipped") == 1);
                    uuidBrowser.setShippedDate(resultSet.getTimestamp("shipped_date"));
                    return uuidBrowser;
                }
            };
}
