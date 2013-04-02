package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;

import java.io.File;

import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;

/**
 * Created by IntelliJ IDEA. User: ramanr Date: May 31, 2010 Time: 1:27:10 AM To
 * change this template use File | Settings | File Templates.
 */
public class ArchiveTypeQueriesJDBCImplDBUnitSlowTest extends DBUnitTestCase {
	private static final String PROPERTIES_FILE = "common.unittest.properties";
	private static final String TEST_DATA_FOLDER = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	private static final String TEST_DATA_FILE = "/qclive/dao/ArchiveType_TestData.xml";
	private ArchiveTypeQueriesJDBCImpl queries;

	public ArchiveTypeQueriesJDBCImplDBUnitSlowTest() {
		super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		queries = new ArchiveTypeQueriesJDBCImpl();
		queries.setDataSource(getDataSource());

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
	public void testGetArchiveTypeId() throws Exception {
		assertEquals(new Integer(1), queries.getArchiveTypeId("Level_1"));
	}

	@Test(expected = EmptyResultDataAccessException.class)
	public void testEmptyDataAccessForArchiveTypeId() throws Exception {
		try {
			// Archive type doesn't exsist in the data set
			queries.getArchiveTypeId("Level_100");
		} catch (Exception e) {
			// As this class is extended from DBUnit TestCase @Test annotation
			// is not being used.
			// So the exception is captured to validate
			assertTrue(e instanceof EmptyResultDataAccessException);
		}

	}

	@Test
	public void testGetArchiveType() throws Exception {
		assertEquals("Level_1", queries.getArchiveType(1));
	}

	@Test(expected = EmptyResultDataAccessException.class)
	public void testEmptyDataAccessForArchiveType() throws Exception {
		// Archive type id doesn't exsist in the data set
		try {
			queries.getArchiveType(100);
		} catch (Exception e) {
			// As this class is extended from DBUnit TestCase @Test annotation
			// is not being used.
			// So the exception is captured to validate
			assertTrue(e instanceof EmptyResultDataAccessException);
		}

	}

	@Test
	public void testValidArchiveType() throws Exception {
		assertTrue(queries.isValidArchiveType("Level_1"));
	}

	@Test
	public void testInValidArchiveType() throws Exception {
		// Invalid archive type id
		assertFalse(queries.isValidArchiveType("Level_100"));
	}

	@Test
	public void testGetArchiveTypeDataLevel() throws Exception {
		assertEquals(new Integer(1), queries.getArchiveTypeDataLevel("Level_1"));
		// Invalid archive type id
		assertNull(queries.getArchiveTypeDataLevel("Level_100"));

	}

	@Test
	public void testGetAllArchiveTypes() throws Exception {
		// data set has 7 entries
		assertEquals(7, queries.getAllArchiveTypes().size());
	}

}
