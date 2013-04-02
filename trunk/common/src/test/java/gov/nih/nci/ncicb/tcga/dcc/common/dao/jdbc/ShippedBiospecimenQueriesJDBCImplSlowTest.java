package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.InvalidMetadataException;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.MetaDataBean;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ShippedBiospecimen;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ShippedBiospecimenElement;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ShippedBiospecimenQueries;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Class to test ShippedBiospecimenQueries JDBC implementation
 *
 * @author Rohini Raman
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ShippedBiospecimenQueriesJDBCImplSlowTest extends DBUnitTestCase {

    private static final String PROPERTIES_FILE = "unittest.properties";
    private static final String TEST_DATA_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "dao/ShippedBiospecimenTestData.xml";
    private static final String appContextFile = "samples/applicationContext-dbunit.xml";
    private final ApplicationContext appContext;
    private final ShippedBiospecimenQueries queries;

    public ShippedBiospecimenQueriesJDBCImplSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
        appContext = new ClassPathXmlApplicationContext(appContextFile);
        queries = (ShippedBiospecimenQueries) appContext.getBean("shippedBiospecimenQueries");

    }

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.CLEAN_INSERT;
    }

    @Override
    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.DELETE_ALL;
    }


    public void testAddNewShippedPortions() throws Exception {

        final List<ShippedBiospecimen> shippedBiospecimens = new ArrayList<ShippedBiospecimen>();
        ShippedBiospecimen shippedBiospecimen = ShippedBiospecimen.parseShippedPortionBarcode("TCGA-A3-1111-01A-02-2000-20");
        shippedBiospecimen.setUuid(UUID.randomUUID().toString());
        shippedBiospecimen.setBatchNumber(42);
        shippedBiospecimens.add(shippedBiospecimen);

        shippedBiospecimen = ShippedBiospecimen.parseShippedPortionBarcode("TCGA-A3-1111-01A-03-2001-20");
        shippedBiospecimen.setUuid(UUID.randomUUID().toString());
        shippedBiospecimen.setBatchNumber(18);
        shippedBiospecimens.add(shippedBiospecimen);

        queries.addShippedBiospecimens(shippedBiospecimens, 2);

        assertEquals(2, getSimpleJdbcTemplate().queryForInt("select count(*) from shipped_biospecimen where built_barcode in('TCGA-A3-1111-01A-02-2000-20','TCGA-A3-1111-01A-03-2001-20')"));
        // make sure the ids are set in the objects
        for (final ShippedBiospecimen updatedShippedBiospecimen : shippedBiospecimens) {
            assertNotNull(updatedShippedBiospecimen.getShippedBiospecimenId());
            assertNotNull(updatedShippedBiospecimen.getShippedBiospecimenTypeId());
        }

        assertEquals(42,
                getSimpleJdbcTemplate().queryForInt("select batch_id from shipped_biospecimen where built_barcode='TCGA-A3-1111-01A-02-2000-20'"));

    }

    public void testAddExistingShippedPortions() throws Exception {

        final List<ShippedBiospecimen> shippedBiospecimens = new ArrayList<ShippedBiospecimen>();
        final String barcode = "TCGA-A3-3308-01A-03-1111-20";
        final String uuid = "12345-D5F8-4C55-A3A4-BE3355122480";
        ShippedBiospecimen shippedBiospecimen = ShippedBiospecimen.parseShippedPortionBarcode(barcode);
        // uuid already exists in the database, but in upper-case; UUIDs should be case-insensitive so it should find it in lower-case
        shippedBiospecimen.setUuid(uuid.toLowerCase());
        shippedBiospecimen.setBatchNumber(19);
        shippedBiospecimen.setRedacted(false);

        shippedBiospecimen.setViewable(true);

        shippedBiospecimens.add(shippedBiospecimen);

        assertNotSame(barcode, getSimpleJdbcTemplate().queryForObject("select built_barcode from shipped_biospecimen where uuid = '" + uuid.toLowerCase() + "' ", String.class));
        getSimpleJdbcTemplate().update("update shipped_biospecimen set is_viewable = 0  ,is_redacted = 1 where uuid = '" + uuid.toLowerCase() + "' ");
        queries.addShippedBiospecimens(shippedBiospecimens, 2);
        assertEquals(barcode, getSimpleJdbcTemplate().queryForObject("select built_barcode from shipped_biospecimen where uuid = '" + uuid.toLowerCase() + "' ", String.class));
        assertEquals(19, getSimpleJdbcTemplate().queryForInt("select batch_id from shipped_biospecimen where uuid='" + uuid.toLowerCase() + "'"));
        assertEquals(1, getSimpleJdbcTemplate().queryForInt("select is_redacted from shipped_biospecimen where uuid='" + uuid.toLowerCase() + "'"));
        assertEquals(0, getSimpleJdbcTemplate().queryForInt("select is_viewable from shipped_biospecimen where uuid='" + uuid.toLowerCase() + "'"));
    }

    public void testAddNewShippedBiospecimenElements() throws Exception {

        final List<ShippedBiospecimenElement> shippedBiospecimenElements = new ArrayList<ShippedBiospecimenElement>();
        ShippedBiospecimenElement shippedBiospecimenElement = new ShippedBiospecimenElement();
        shippedBiospecimenElement.setShippedBiospecimenId(1l);
        shippedBiospecimenElement.setElementName("sample_type_code");
        shippedBiospecimenElement.setElementValue("111");
        shippedBiospecimenElements.add(shippedBiospecimenElement);

        shippedBiospecimenElement = new ShippedBiospecimenElement();
        shippedBiospecimenElement.setShippedBiospecimenId(1l);
        shippedBiospecimenElement.setElementName("sample_sequence");
        shippedBiospecimenElement.setElementValue("222");
        shippedBiospecimenElements.add(shippedBiospecimenElement);
        getSimpleJdbcTemplate().update("delete from shipped_biospecimen_element");

        queries.addShippedBiospecimenElements(shippedBiospecimenElements);

        assertEquals(2, getSimpleJdbcTemplate().queryForInt("select count(*) from shipped_biospecimen_element"));
        // make sure the ids are set in the objects
        for (final ShippedBiospecimenElement updatedShippedBiospecimenElements : shippedBiospecimenElements) {
            assertNotNull(updatedShippedBiospecimenElements.getShippedBiospecimenElementId());
            assertNotNull(updatedShippedBiospecimenElements.getElementTypeId());
        }

    }


    public void testAddExistingShippedPortionElement() throws Exception {
        final String sampleTypeCode = "2222";
        final List<ShippedBiospecimenElement> shippedBiospecimenElements = new ArrayList<ShippedBiospecimenElement>();
        ShippedBiospecimenElement shippedBiospecimenElement = new ShippedBiospecimenElement();
        // element type already exists in the database
        shippedBiospecimenElement.setElementTypeId(1);
        // biospecimen id already exists in the database
        shippedBiospecimenElement.setShippedBiospecimenId(1l);
        shippedBiospecimenElement.setElementValue(sampleTypeCode);
        shippedBiospecimenElements.add(shippedBiospecimenElement);

        assertNotSame(sampleTypeCode, getSimpleJdbcTemplate().queryForObject("select element_value from shipped_biospecimen_element where shipped_biospecimen_id = 1 AND  element_type_id = 1", String.class));
        queries.addShippedBiospecimenElements(shippedBiospecimenElements);
        assertEquals(sampleTypeCode, getSimpleJdbcTemplate().queryForObject("select element_value from shipped_biospecimen_element where shipped_biospecimen_id = 1 AND  element_type_id = 1", String.class));

    }


    public void testGetShippedElementsType() throws Exception {
        final Map<String, Integer> shippedElementTypeIdByName = queries.getShippedElementsType();
        assertEquals(new Integer(1), shippedElementTypeIdByName.get("sample_type_code"));
        assertEquals(new Integer(2), shippedElementTypeIdByName.get("sample_sequence"));
        assertEquals(new Integer(3), shippedElementTypeIdByName.get("portion_sequence"));
        assertEquals(new Integer(4), shippedElementTypeIdByName.get("analyte_code"));
        assertEquals(new Integer(5), shippedElementTypeIdByName.get("plate_id"));

    }

    public void testGetBiospecimenForUUID() {
        Long biospecimenId = queries.getShippedBiospecimenIdForUUID("uuid1");
        assertNotNull(biospecimenId);
        assertEquals(new Long(10), biospecimenId);
    }

    public void testGetBiospecimenIdsMultiple() {
        List<Long> ids = queries.getShippedBiospecimenIds(Arrays.asList("12345-D5F8-4C55-A3A4-BE3355122480", "uuid1", "uuid2"));
        assertEquals(new Long(1), ids.get(0));
        assertEquals(new Long(10), ids.get(1));
        assertEquals(new Long(20), ids.get(2));
    }

    public void testGetBiospecimenForUUIDUpperCase() {

        final Long biospecimenId = queries.getShippedBiospecimenIdForUUID("UUID1");

        assertNotNull(biospecimenId);
        assertEquals(new Long(10), biospecimenId);
    }

    public void testGetBiospecimenForUnknownUuid() {
        Long biospecimenId = queries.getShippedBiospecimenIdForUUID("monkey");
        assertNull(biospecimenId);
    }

    public void testAddFileRelationship() {
        queries.addFileRelationship(1L, 100L);
        SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(getDataSource());
        int count = jdbcTemplate.queryForInt("select count(*) from shipped_biospecimen_file where shipped_biospecimen_id=1 and file_id=100");
        assertEquals(1, count);
    }

    public void testAddDuplicateFileRelationship() {
        queries.addFileRelationship(1L, 100L);
        queries.addFileRelationship(1L, 100L);
        SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(getDataSource());
        int count = jdbcTemplate.queryForInt("select count(*) from shipped_biospecimen_file where shipped_biospecimen_id=1 and file_id=100");
        assertEquals(1, count);
    }

    public void testIsShippedBiospecimenShippedPortionUUIDValid() {
        Boolean isValid = queries.isShippedBiospecimenShippedPortionUUIDValid("uUiD2");
        assertTrue(isValid);
    }

    public void testIsShippedBiospecimenShippedPortionUUIDAliquot() {
        Boolean isValid = queries.isShippedBiospecimenShippedPortionUUIDValid("uuid3");
        assertFalse(isValid);
    }

    public void testIsShippedBiospecimenShippedPortionUUIDInvalid() {
        Boolean isValid = queries.isShippedBiospecimenShippedPortionUUIDValid("garbage");
        assertFalse(isValid);
    }

    public void testAddFileRelationships() {
        queries.addFileRelationships(Arrays.asList(10L, 30L), 101L);
        SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(getDataSource());
        List<Map<String, Object>> results = jdbcTemplate.queryForList("select shipped_biospecimen_id from shipped_biospecimen_file where file_id=101 order by shipped_biospecimen_id");
        assertEquals("10", results.get(0).get("shipped_biospecimen_id").toString());
        assertEquals("30", results.get(1).get("shipped_biospecimen_id").toString());
    }

    public void testAddFileRelationshipsSomeExisting() {
        // one of these (id 20) already is in the db
        queries.addFileRelationships(Arrays.asList(10L, 20L, 30L), 100L);
        SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(getDataSource());
        List<Map<String, Object>> results = jdbcTemplate.queryForList("select shipped_biospecimen_id from shipped_biospecimen_file where file_id=100 order by shipped_biospecimen_id");
        assertEquals("10", results.get(0).get("shipped_biospecimen_id").toString());
        assertEquals("20", results.get(1).get("shipped_biospecimen_id").toString());
        assertEquals("30", results.get(2).get("shipped_biospecimen_id").toString());
    }

    public void testAddArchiveRelationships() {
        queries.addArchiveRelationships(Arrays.asList(40L, 50L), 1L);
        SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(getDataSource());
        List<Map<String, Object>> results = jdbcTemplate.queryForList("select shipped_biospecimen_id from shipped_biospec_bcr_archive where archive_id=1 and shipped_biospecimen_id in(40,50) order by shipped_biospecimen_id");
        assertEquals("40", results.get(0).get("shipped_biospecimen_id").toString());
        assertEquals("50", results.get(1).get("shipped_biospecimen_id").toString());
    }

    public void testAddArchiveRelationshipsSomeExisting() {
        // one of these (id 20) already is in the db
        queries.addArchiveRelationships(Arrays.asList(10L, 20L, 30L), 1L);
        SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(getDataSource());
        List<Map<String, Object>> results = jdbcTemplate.queryForList("select shipped_biospecimen_id from shipped_biospec_bcr_archive where archive_id=1 and shipped_biospecimen_id in(10,20,30) order by shipped_biospecimen_id");
        assertEquals("10", results.get(0).get("shipped_biospecimen_id").toString());
        assertEquals("20", results.get(1).get("shipped_biospecimen_id").toString());
        assertEquals("30", results.get(2).get("shipped_biospecimen_id").toString());
    }

    public void testRedactedParticipants() {
        Collection<String> redactedIdsList = new ArrayList<String>();
        redactedIdsList.add("0003");
        redactedIdsList.add("0001");
        redactedIdsList.add("0002");
        redactedIdsList.add("0004");
        List<String> redactedParticipants = queries.getRedactedParticipants(redactedIdsList);

        assertEquals(redactedParticipants.get(0), "0001");
        assertEquals(redactedParticipants.get(1), "0002");
        assertTrue(redactedParticipants.size() == 2);

    }

    public void testAddShippedBiospecimens() {
        final ShippedBiospecimen aliquot = new ShippedBiospecimen();
        aliquot.setUuid("this-is-a-uuid");
        aliquot.setBarcode("TCGA-A3-1234-01A-02D-6789-20");
        // type set, but not type id, so DAO has to look it up
        aliquot.setShippedBiospecimenType(ShippedBiospecimen.SHIPPED_ITEM_NAME_ALIQUOT);
        aliquot.setProjectCode("TCGA");
        aliquot.setTssCode("A3");
        aliquot.setParticipantCode("1234");
        aliquot.setSampleTypeCode("01");
        aliquot.setSampleSequence("A");
        aliquot.setPortionSequence("02");
        aliquot.setAnalyteTypeCode("D");
        aliquot.setPlateId("6789");
        aliquot.setBcrCenterId("20");

        queries.addShippedBiospecimens(Arrays.asList(aliquot));

        // this will throw exception if not found
        int biospecimenCount = getSimpleJdbcTemplate().queryForInt("select count(*) from shipped_biospecimen where uuid='this-is-a-uuid' " +
                "and project_code='TCGA' and tss_code='A3' and participant_code='1234' and bcr_center_id='20' and built_barcode='TCGA-A3-1234-01A-02D-6789-20'");
        assertEquals(1, biospecimenCount);
    }
    
    public void testRetrieveUUIDMetadata() throws InvalidMetadataException{
    	MetaDataBean metadata = queries.retrieveUUIDMetadata("12345-D5F8-4C55-A3A4-BE3355122480");
    	assertEquals ("TCGA-A3-3308-01C-03A-1234-20",metadata.getAliquotBuiltBarcode());    	
    }  
    
    public void testRetrieveUUIDMetadataMissingMetadata() throws InvalidMetadataException{
    	// delete vial
    	SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(getDataSource());
    	jdbcTemplate.update(" delete from shipped_biospecimen_element where shipped_biospecimen_element_id = 2");    	    	
    	assertNull (queries.retrieveUUIDMetadata("12345-D5F8-4C55-A3A4-BE3355122480"));    	  	
    }
    
    public void testRetrieveUUIDMetadataEmptyUUID() throws InvalidMetadataException{
    	assertNull(queries.retrieveUUIDMetadata(""));    	    	
    }

    public void testRetrieveUUIDMetadataUnknownElement() {
      assertNull(queries.retrieveUUIDMetadata("nonsense"));
    }
    
    public void testAddShippedBiospecimenElements() {
        final ShippedBiospecimen aliquot = new ShippedBiospecimen();
        aliquot.setUuid("uuid-60");
        aliquot.setBarcode("TCGA-A3-1234-01A-02D-6789-20");
        // type set, but not type id, so DAO has to look it up
        aliquot.setShippedBiospecimenType(ShippedBiospecimen.SHIPPED_ITEM_NAME_ALIQUOT);
        aliquot.setProjectCode("TCGA");
        aliquot.setTssCode("A3");
        aliquot.setParticipantCode("1234");
        aliquot.setSampleTypeCode("01");
        aliquot.setSampleSequence("A");
        aliquot.setPortionSequence("02");
        aliquot.setAnalyteTypeCode("D");
        aliquot.setPlateId("6789");
        aliquot.setBcrCenterId("20");
        aliquot.setShippedBiospecimenId(60l);

        final List<ShippedBiospecimenElement> shippedBiospecimenElements = aliquot.getShippedBiospecimenElements();
        queries.addShippedBiospecimenElements(shippedBiospecimenElements);

        int elementCount = getSimpleJdbcTemplate().queryForInt("select count(*) from shipped_biospecimen_element where shipped_biospecimen_id=?", 60);
        // sample type code, sample sequence, portion sequence, analyte type code, plate id
        assertEquals(5, elementCount);

        assertEquals("01", getSimpleJdbcTemplate().queryForObject("select element_value from shipped_biospecimen_element where shipped_biospecimen_id=? and element_type_id=1", String.class, 60));
        assertEquals("A", getSimpleJdbcTemplate().queryForObject("select element_value from shipped_biospecimen_element where shipped_biospecimen_id=? and element_type_id=2", String.class, 60));
        assertEquals("02", getSimpleJdbcTemplate().queryForObject("select element_value from shipped_biospecimen_element where shipped_biospecimen_id=? and element_type_id=3", String.class, 60));
        assertEquals("D", getSimpleJdbcTemplate().queryForObject("select element_value from shipped_biospecimen_element where shipped_biospecimen_id=? and element_type_id=4", String.class, 60));
        assertEquals("6789", getSimpleJdbcTemplate().queryForObject("select element_value from shipped_biospecimen_element where shipped_biospecimen_id=? and element_type_id=5", String.class, 60));

        // element record for sample type, sequence, portion sequence, analyte type, plate id
    }

    public void testAddShippedBiospecimensUnknownType() {
        final ShippedBiospecimen biospecimen = new ShippedBiospecimen();
        biospecimen.setShippedBiospecimenType("dragon");

        try {
            queries.addShippedBiospecimens(Arrays.asList(biospecimen));
            fail("exception was not thrown");
        } catch (IllegalArgumentException e) {
            // good, expected because "dragon" is not a known type of shipped biospecimen
        }
    }

    public void testGetItemTypeIdBad() {
        assertNull(queries.getShippedItemId("squirrel"));
    }

    public void testInsertShippedBiospecBcrArchiveNew() {
        assertEquals(0, getSimpleJdbcTemplate().queryForInt("select count(*) from shipped_biospec_bcr_archive where shipped_biospecimen_id=1 and archive_id=1"));
        queries.addArchiveRelationship(1L, 1L);
        assertEquals(1, getSimpleJdbcTemplate().queryForInt("select count(*) from shipped_biospec_bcr_archive where shipped_biospecimen_id=1 and archive_id=1"));
    }

    public void testInsertShippedBiospecBcrArchiveExists() {
        // is already in test data
        assertEquals(1, getSimpleJdbcTemplate().queryForInt("select count(*) from shipped_biospec_bcr_archive where shipped_biospecimen_id=20 and archive_id=1"));
        queries.addArchiveRelationship(20L, 1L);
        assertEquals(1, getSimpleJdbcTemplate().queryForInt("select count(*) from shipped_biospec_bcr_archive where shipped_biospecimen_id=20 and archive_id=1"));
    }

    public void testGetShippedBiospecimenIdLowerCase() {

        final String uuid = "uuid1";
        final Long id = queries.getShippedBiospecimenId(uuid);
        final Long expectedId = 10L;

        assertEquals(expectedId, id);
    }

    public void testGetShippedBiospecimenIdUpperCase() {

        final String uuid = "UUID1";
        final Long id = queries.getShippedBiospecimenId(uuid);
        final Long expectedId = 10L;

        assertEquals(expectedId, id);
    }
        
    public  void testGetUUIDLevel(){
    	assertEquals ("Aliquot",queries.getUUIDLevel("12345-D5F8-4C55-A3A4-BE3355122480"));
    }
    
    public  void testGetUUIDLevelBadUUID(){
    	assertNull (queries.getUUIDLevel("badUUID"));
    }
    
    public  void testGetUUIDLevelEmptyInput(){
    	try{
    		queries.getUUIDLevel("");
    		fail();
    	}catch (IllegalArgumentException e){
    		// swallow
    	}
    }

    public void testGetDiseaseForUUID() {
        assertEquals("TEST", queries.getDiseaseForUUID("12345-d5f8-4c55-a3a4-be3355122480"));
        assertEquals("TEST", queries.getDiseaseForUUID("12345-D5F8-4C55-A3A4-BE3355122480"));
    }

    public void testGetDiseaseForUUIDNotFound() {
        assertNull(queries.getDiseaseForUUID("something-wrong"));
    }
}
