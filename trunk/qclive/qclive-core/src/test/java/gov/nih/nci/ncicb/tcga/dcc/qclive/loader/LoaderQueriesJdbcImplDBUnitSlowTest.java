/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.loader;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.LoaderQueriesException;

import java.io.File;
import java.util.Map;

import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

/**
 * DBUnit test for loader queries.
 * 
 * @author Jessica Chen Last updated by: Jeyanthi Thangiah
 * @version $Rev$
 */
public class LoaderQueriesJdbcImplDBUnitSlowTest extends DBUnitTestCase {
	private static final String SAMPLE_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	private static final int CENTER_ID = 1;
	private static final int PLATFORM_ID = 2;
	private static final int ARCHIVE_ID = 10;
	private static final long FILE_INFO_ID_1 = 1;
	private static final long FILE_INFO_ID_2 = 2;
	private static final long FILE_INFO_ID_3 = 3;
	private LoaderQueriesJdbcImpl queries;

	public LoaderQueriesJdbcImplDBUnitSlowTest() {
		super(SAMPLE_DIR, "autoloader/dbunit/ForLoaderQueries.xml",
				"oracle.unittest.properties");
	}

	@Override
	public DatabaseOperation getTearDownOperation() {
		return DatabaseOperation.DELETE_ALL;
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		queries = new LoaderQueriesJdbcImpl();
		queries.setDataSource(this.getDataSource());
		queries.setLogger(new BareBonesLogger());
	}

	@After
	public void tearDown() throws Exception {
		SimpleJdbcTemplate simpleJdbcTemplate = new SimpleJdbcTemplate(
				getDataSource());
		simpleJdbcTemplate.update("delete from data_set_file");
		simpleJdbcTemplate.update("delete from data_set");
		simpleJdbcTemplate.update("delete from experiment");
		simpleJdbcTemplate.update("delete from file_to_archive");
		simpleJdbcTemplate.update("delete from file_info");
		simpleJdbcTemplate.update("delete from archive_info");
		super.tearDown();
	}

	@Test
	public void testGetFileInfoData() throws Exception {
		Map<String, Long> fileInfoIdsByName = queries.lookupFileInfoData(10);
		assertEquals(true, fileInfoIdsByName.containsKey("a_file"));
	}

	@Test
	public void testInsertDataSetFile() throws LoaderQueriesException {
		SimpleJdbcTemplate simpleJdbcTemplate = new SimpleJdbcTemplate(
				getDataSource());

		long experimentId = queries.insertExperiment("base name", 2, 1,
				CENTER_ID, PLATFORM_ID);
		long datasetId = queries.insertDataset(experimentId,
				"source file name", "source file type", "PUBLIC", 2, CENTER_ID,
				PLATFORM_ID, ARCHIVE_ID);
		queries.insertDataSetFile(datasetId, "a_file", FILE_INFO_ID_1);
		long fileId = simpleJdbcTemplate
				.queryForInt(
						"select data_set_file_id from data_set_file where data_set_id=? and file_name=?",
						datasetId, "a_file");
		assertEquals(0, simpleJdbcTemplate.queryForInt(
				"select is_loaded from data_set_file where data_set_file_id=?",
				fileId));
		java.sql.Timestamp loadStartDate = simpleJdbcTemplate
				.queryForObject(
						"select load_start_date from data_set_file where data_set_file_id=?",
						java.sql.Timestamp.class, fileId);
		assertNotNull(loadStartDate);

		queries.setDataSetFileLoaded(datasetId, "a_file");
		java.sql.Timestamp loadEnd = simpleJdbcTemplate
				.queryForObject(
						"select load_end_date from data_set_file where data_set_file_id=?",
						java.sql.Timestamp.class, fileId);
		assertNotNull(loadEnd);
	}

	@Test
	public void testLookupExperimentId() throws Exception {
		long theInvalidID = queries.lookupExperimentId("foo", 1, 1);
		assertEquals(theInvalidID, -1);
		long theValidID = queries.lookupExperimentId("robert", 1, 42);
		assertEquals(theValidID, 1);

	}

	@Test
	public void testLookupCenterId() throws LoaderQueriesException {
		long centerId = queries.lookupCenterId("center", 2);
		assertEquals(centerId, 1);
	}

	@Test
	public void testLookupPlatformId() throws LoaderQueriesException {
		long platformId = queries.lookupPlatformId("platform");
		assertEquals(platformId, 2);
	}

}
