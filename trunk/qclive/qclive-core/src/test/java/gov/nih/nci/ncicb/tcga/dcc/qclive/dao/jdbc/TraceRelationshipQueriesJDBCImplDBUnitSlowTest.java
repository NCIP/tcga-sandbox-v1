package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.TraceRelationship;

import java.io.File;
import java.sql.Date;
import java.sql.SQLException;

import org.dbunit.dataset.ITable;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * Slow test for TraceRelationship JDBC implementation. It uses disease specific
 * data source
 * 
 * @author Rohini Raman Last updated by: $Author$
 * @version $Rev$
 */

public class TraceRelationshipQueriesJDBCImplDBUnitSlowTest extends
		DBUnitTestCase {

	private static final String PROPERTIES_FILE = "common.unittest.properties";
	private static final String TEST_DATA_FOLDER = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	private static final String TEST_DATA_FILE = "qclive/dao/TraceRelationship_TestData.xml";
	private TraceRelationshipQueriesJDBCImpl queries;
	private SimpleJdbcTemplate template = null;

	public TraceRelationshipQueriesJDBCImplDBUnitSlowTest() throws SQLException {
		super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		queries = new TraceRelationshipQueriesJDBCImpl();
		queries.setDataSource(getDataSource());
		template = new SimpleJdbcTemplate(getDataSource());
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
	public void testAddTraceRelationship() throws Exception {
		// create new trace relationship object to add
		TraceRelationship traceRelationShip = new TraceRelationship();
		traceRelationShip.setBiospecimenID(3000);
		traceRelationShip.setTraceID(2041828391);
		traceRelationShip.setFileID(7725);
		traceRelationShip.setDccReceived(Date.valueOf("2008-03-07"));
		queries.addTraceRelationship(traceRelationShip);
		assertEquals(getCountTraceRelationship(traceRelationShip),
				getCountShippedTraceRelationship(traceRelationShip));
	}

	@Test
	public void testUpdateDccDate() throws Exception {
		// get the first entry from database;
		TraceRelationship traceRelationship = getTraceRelationshipDBObject();
		Date newDccDate = Date.valueOf("2009-03-07");
		traceRelationship.setDccReceived(newDccDate);
		assertEquals(1, queries.updateDccDate(traceRelationship));
		assertEquals(newDccDate, queries.getDccDate(
				traceRelationship.getBiospecimenID(),
				traceRelationship.getTraceID()));

	}

	@Test
	public void testUpdateFileID() throws Exception {
		TraceRelationship traceRelationship = getTraceRelationshipDBObject();
		Long newFileId = 8000L;
		traceRelationship.setFileID(newFileId);
		assertEquals(1, queries.updateFileID(traceRelationship));
		assertEquals(newFileId, queries.getFileId(
				traceRelationship.getBiospecimenID(),
				traceRelationship.getTraceID()));
	}

	@Test
	public void testGetDccDate() throws Exception {
		TraceRelationship traceRelationship = getTraceRelationshipDBObject();
		assertEquals(traceRelationship.getDccReceived(), queries.getDccDate(
				traceRelationship.getBiospecimenID(),
				traceRelationship.getTraceID()));
	}

	@Test
	public void testGetFileID() throws Exception {
		TraceRelationship traceRelationship = getTraceRelationshipDBObject();
		assertEquals(
				traceRelationship.getFileID(),
				queries.getFileId(traceRelationship.getBiospecimenID(),
						traceRelationship.getTraceID()).longValue());
	}

	private TraceRelationship getTraceRelationshipDBObject() throws Exception {
		// Read the first entry from the database and create TraceRelationship
		// object
		TraceRelationship traceRelationship = new TraceRelationship();
		ITable traceRelationshipTable = getDataSet().getTable(
				"biospecimen_ncbi_trace");
		Object o = traceRelationshipTable.getValue(0, "biospecimen_id");
		traceRelationship.setBiospecimenID(Integer
				.parseInt((String) traceRelationshipTable.getValue(0,
						"biospecimen_id")));
		traceRelationship.setTraceID(Integer
				.parseInt((String) traceRelationshipTable.getValue(0,
						"ncbi_trace_id")));
		traceRelationship.setFileID(Integer
				.parseInt((String) traceRelationshipTable
						.getValue(0, "file_id")));
		traceRelationship.setDccReceived(Date
				.valueOf((String) traceRelationshipTable.getValue(0,
						"dcc_date_received")));
		return traceRelationship;
	}

	private int getCountTraceRelationship(TraceRelationship traceRelationship) {
		int testValue = template
				.queryForInt(
						"select count(*) from biospecimen_ncbi_trace where biospecimen_id = ? and ncbi_trace_id = ? and file_id=?",
						traceRelationship.getBiospecimenID(),
						traceRelationship.getTraceID(),
						traceRelationship.getFileID());
		return testValue;

	}

	private int getCountShippedTraceRelationship(
			TraceRelationship traceRelationship) {
		int testValue = template
				.queryForInt(
						"select count(*) from shipped_biospec_ncbi_trace where shipped_biospecimen_id = ? and ncbi_trace_id = ? and file_id=?",
						traceRelationship.getBiospecimenID(),
						traceRelationship.getTraceID(),
						traceRelationship.getFileID());
		return testValue;

	}
}
