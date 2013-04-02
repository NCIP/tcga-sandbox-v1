/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.ArchiveQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.LogQueriesJDBCImpl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.lob.OracleLobHandler;

/**
 * DBUnit tests for ArchiveLoggerImpl.
 * 
 * @author Jeyanthi Thangiah Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveLoggerImplDBUnitSlowTest extends DBUnitTestCase {

	private static final String TEST_DATA_FOLDER = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	private static final String TEST_DATA_FILE = "/logger/ArchiveLogger_TestData.xml";
	private static final String PROPERTIES_FILE = "common.unittest.properties";
	private final String archiveFile = TEST_DATA_FOLDER
			+ "qclive/tarGz/good.tar.gz";

	private ArchiveLoggerImpl archiveLogger;
	private LogQueriesJDBCImpl queries;

	public ArchiveLoggerImplDBUnitSlowTest() throws IOException {
		super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
	}

	protected DatabaseOperation getSetUpOperation() throws Exception {
		return DatabaseOperation.CLEAN_INSERT;
	}

	protected DatabaseOperation getTearDownOperation() {
		return DatabaseOperation.DELETE_ALL;

	}

	@Override
	protected void setUpDatabaseConfig(DatabaseConfig config) {
		config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
				new OracleDataTypeFactory());
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		archiveLogger = new ArchiveLoggerImpl();
		queries = new LogQueriesJDBCImpl();
		queries.setDataSource(getDataSource());
		queries.setLobHandler(new OracleLobHandler());
		archiveLogger.setLogQueries(queries);
		ArchiveQueries archiveQueries = new ArchiveQueriesJDBCImpl();
		((ArchiveQueriesJDBCImpl) archiveQueries)
				.setDataSource(getDataSource());
		archiveLogger.setArchiveQueries(archiveQueries);

	}

	public void testAddArchiveLog() {
		Archive testArchive = new Archive(archiveFile);
		testArchive.setId(100L);
		String testMessage = null;
		try {
			testMessage = readFileAsString(TEST_DATA_FOLDER
					+ "/logger/LargeArchiveLogFile.txt");
		} catch (IOException io) {
			System.out
					.println("Please create LargeArchiveLogFile in the test folder");
		}
		archiveLogger.addArchiveLog(testArchive, testMessage);
		SimpleJdbcTemplate sjdbc = new SimpleJdbcTemplate(
				queries.getDataSource());
		int clob_length = sjdbc
				.queryForInt("select dbms_lob.getLength(description) from process_log where start_time >= cast(sysdate-1 as timestamp)");
		assertEquals(4609, clob_length);
		int log_id = sjdbc
				.queryForInt("select log_id from process_log where start_time >= cast(sysdate-1 as timestamp)");
		int curr_seq = sjdbc
				.queryForInt("SELECT process_log_seq.CURRVAL FROM DUAL");
		assertEquals(curr_seq, log_id);
		int archive_id = sjdbc
				.queryForInt("select archive_id from log_to_archives where log_id="
						+ log_id);
		assertEquals(100, archive_id);
	}

	private static String readFileAsString(String filePath)
			throws java.io.IOException {

        String result = null;
        BufferedInputStream f = null;

        try {
            byte[] buffer = new byte[(int) new File(filePath).length()];
            //noinspection IOResourceOpenedButNotSafelyClosed
            f = new BufferedInputStream(new FileInputStream(filePath));
            f.read(buffer);
            result = new String(buffer);
        } finally {
            IOUtils.closeQuietly(f);
        }

		return result;
	}
}
