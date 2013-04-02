package gov.nih.nci.ncicb.tcga.dcc.qclive.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;

import java.io.File;

import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * Slow test for shipped biospecimen queries
 * 
 * @author chenjw Last updated by: $Author$
 * @version $Rev$
 */
public class QcLiveShippedBiospecimenQueriesJDBCImplSlowTest extends
		DBUnitTestCase {
	private QcLiveShippedBiospecimenQueriesJDBCImpl shippedBiospecimenQueries;
	private static final String SAMPLES_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;

	public QcLiveShippedBiospecimenQueriesJDBCImplSlowTest() {
		super(SAMPLES_DIR, "qclive/dao/ShippedBiospecimenQueries_testData.xml",
				"common.unittest.properties");
	}

	public void setUp() throws Exception {
		super.setUp();
		shippedBiospecimenQueries = new QcLiveShippedBiospecimenQueriesJDBCImpl();
		shippedBiospecimenQueries.setDataSource(getDataSource());
	}

	public void testGetBiospecimenForUUID() {
		Long biospecimenId = shippedBiospecimenQueries
				.getShippedBiospecimenIdForUUID("uuid1");
		assertNotNull(biospecimenId);
		assertEquals(new Long(1), biospecimenId);
	}

	public void testGetBiospecimenForUnknownUuid() {
		Long biospecimenId = shippedBiospecimenQueries
				.getShippedBiospecimenIdForUUID("monkey");
		assertNull(biospecimenId);
	}

	public void testAddFileRelationship() {
		shippedBiospecimenQueries.addFileRelationship(1L, 100L);
		SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(
				getDataSource());
		int count = jdbcTemplate
				.queryForInt("select count(*) from shipped_biospecimen_file where shipped_biospecimen_id=1 and file_id=100");
		assertEquals(1, count);
	}

	public void testAddDuplicateFileRelationship() {
		shippedBiospecimenQueries.addFileRelationship(1L, 100L);
		shippedBiospecimenQueries.addFileRelationship(1L, 100L);
		SimpleJdbcTemplate jdbcTemplate = new SimpleJdbcTemplate(
				getDataSource());
		int count = jdbcTemplate
				.queryForInt("select count(*) from shipped_biospecimen_file where shipped_biospecimen_id=1 and file_id=100");
		assertEquals(1, count);
	}

	public void testIsShippedBiospecimenShippedPortionUUIDValid() {
		Boolean isValid = shippedBiospecimenQueries
				.isShippedBiospecimenShippedPortionUUIDValid("uuid2");
		assertTrue(isValid);
	}

	public void testIsShippedBiospecimenShippedPortionUUIDAliquot() {
		Boolean isValid = shippedBiospecimenQueries
				.isShippedBiospecimenShippedPortionUUIDValid("uuid3");
		assertFalse(isValid);
	}

	public void testIsShippedBiospecimenShippedPortionUUIDInvalid() {
		Boolean isValid = shippedBiospecimenQueries
				.isShippedBiospecimenShippedPortionUUIDValid("garbage");
		assertFalse(isValid);
	}

}
