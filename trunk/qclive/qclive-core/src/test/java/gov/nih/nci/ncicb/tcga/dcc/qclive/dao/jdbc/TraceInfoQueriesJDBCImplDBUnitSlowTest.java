package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.NcbiTrace;

import java.io.File;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA. User: ramanr Date: May 31, 2010 Time: 10:45:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class TraceInfoQueriesJDBCImplDBUnitSlowTest extends DBUnitTestCase {

	private static final String PROPERTIES_FILE = "common.unittest.properties";
	private static final String TEST_DATA_FOLDER = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	private static final String TEST_DATA_FILE = "/qclive/dao/TraceInfo_TestData.xml";
	private TraceInfoQueriesJDBCImpl queries;

	public TraceInfoQueriesJDBCImplDBUnitSlowTest() throws SQLException {
		super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		queries = new TraceInfoQueriesJDBCImpl();
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
	public void testAddTraceInfo() throws Exception {
		final NcbiTrace ncbiTrace = new NcbiTrace();
		ncbiTrace.setTi(2049722313);
		ncbiTrace.setTrace_name("16475054060184054584");
		ncbiTrace.setCenter_name("BCM");
		ncbiTrace.setSubmission_type("TEST");
		ncbiTrace.setGene_name("WISP1");
		ncbiTrace.setReference_accession("NC_000008.9");
		ncbiTrace.setReference_acc_max(134272779);
		ncbiTrace.setReference_acc_min(134272398);
		ncbiTrace.setBasecall_length(410);
		ncbiTrace.setState("active");
		ncbiTrace.setLoad_date(Timestamp.valueOf("2008-03-17 01:17:00.0"));

		assertEquals(1, queries.addTraceInfo(ncbiTrace));

		// compare the primary key
		List<NcbiTrace> testData = queries.getMatchingTraces(
				"16475054060184054584", "BCM");
		assertEquals(ncbiTrace.getTrace_name(), testData.get(0).getTrace_name());
		assertEquals(ncbiTrace.getCenter_name(), testData.get(0)
				.getCenter_name());
		assertEquals(ncbiTrace.getLoad_date(), testData.get(0).getLoad_date());
	}

	@Test
	public void testGetMatchingTrace() throws Exception {
		List<NcbiTrace> traceList = queries.getMatchingTraces(
				"16474049325735115559", "BCM");
		assertEquals(1, traceList.size());
		assertEquals("16474049325735115559", traceList.get(0).getTrace_name());
	}

	@Test
	public void testEmptyDataAccessForGetMatchingTrace() throws Exception {

		List<NcbiTrace> traceList = queries.getMatchingTraces(
				"0000000000000000", "BCM");
		assertEquals(0, traceList.size());

	}

	@Test
	public void testUpdateTraceinfo() throws Exception {

		List<NcbiTrace> traceList = queries.getMatchingTraces(
				"16474049325735115559", "BCM");
		assertEquals(1, traceList.size());
		final long oldTraceId = traceList.get(0).getTi();
		final long newTraceId = oldTraceId + 1000;

		assertEquals(1, queries.updateTraceinfo(oldTraceId, newTraceId));

		traceList = queries.getMatchingTraces("16474049325735115559", "BCM");
		assertEquals(1, traceList.size());
		assertEquals(newTraceId, traceList.get(0).getReplaced_by());

	}

	@Test
	public void testTraceInfoExists() throws Exception {
		assertEquals(1, queries.exists(2049722311));
	}

	@Test
	public void testTraceInfoNotExists() throws Exception {
		assertEquals(0, queries.exists(2049722399));
	}

	@Test
	public void testGetLastLoadDate() throws Exception {
		assertEquals("2008-03-17", queries.getLastLoadDate().toString());
	}

}
