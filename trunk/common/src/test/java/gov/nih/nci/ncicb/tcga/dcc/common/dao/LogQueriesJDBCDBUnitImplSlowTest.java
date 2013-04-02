/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Log;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.LogQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;
import gov.nih.nci.ncicb.tcga.dcc.common.web.LogQueryRequest;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.lob.OracleLobHandler;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * DBUnit tests for LogQueriesJDBCImpl.
 *
 * @author Jeyanthi Thangiah Last updated by: $Author$
 * @version $Rev$
 */
public class LogQueriesJDBCDBUnitImplSlowTest extends DBUnitTestCase {

    private static final String SAMPLES_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "ProcessLog_testDB.xml";
    private static final String PROPERTIES_FILE = "unittest.properties";

    private LogQueriesJDBCImpl queries;
    private SimpleJdbcTemplate sjdbc;


    public LogQueriesJDBCDBUnitImplSlowTest() throws IOException {
        super(SAMPLES_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.CLEAN_INSERT;
    }

    protected DatabaseOperation getTearDownOperation() {
        return DatabaseOperation.DELETE_ALL;

    }

    @Override
    protected void setUpDatabaseConfig(DatabaseConfig config) {
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new OracleDataTypeFactory());
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        queries = new LogQueriesJDBCImpl();
        queries.setDataSource(getDataSource());
        queries.setLobHandler(new OracleLobHandler());
        sjdbc = new SimpleJdbcTemplate(queries.getDataSource());
        insertBaseTransactionData();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        cleanUptransctionTables();
    }


    public void testGetLogInDateRange() {
        LogQueryRequest queryParams = new LogQueryRequest();
        try {
            Date myDate = new SimpleDateFormat("dd.MM.yyyy").parse("21.04.2010");
            queryParams.setStartDate(myDate);
        } catch (ParseException e) {
            System.out.println("Invalid Date Parser Exception ");
            e.printStackTrace();
        }
        List<Log> logEntries = queries.getLogInDateRange(queryParams);
        assertEquals(316, logEntries.size());
        Log testLog = createLogObject(5831, "Execution of archive saver on jhu-usc.edu_OV.HumanMethylation27.mage-tab.1.1.0 succeeded", "2010-05-19 13:34:16.896");
        assertTrue(logEntries.contains(testLog));
    }

    public Log createLogObject(Integer log_id, String description, String start_date) {
        Log testLog = new Log();
        testLog.setId(log_id);
        testLog.setDescription(description);
        testLog.setStartTime(Timestamp.valueOf(start_date));
        return testLog;
    }

    public void testGetAllLogEntries() {
        Collection<Log> logEntries = queries.getAllLogEntries();
        assertEquals(837, logEntries.size());
    }

    public void testUpdateLogEntry() {
        Log testLog = createLogObject(5009, "Execution of archive saver on intgen.org_GBM.bio.Level_1.6.23.0 succeeded", "2010-03-31 18:09:28.888");
        testLog.setResultId(7);
        queries.updateLogEntry(testLog);
        int result_id = sjdbc.queryForInt("select result_id from process_log where log_id = 5009");
        assertEquals(7, result_id);
    }

    public void testUpdateLogEntryForCLOB() {
        String largeDesc = null;
        try {
            largeDesc = FileUtil.readFile(new File(SAMPLES_FOLDER + "largeFile.txt"), false);
        } catch (IOException io) {
        }
        Log largeTestLog = createLogObject(5840, largeDesc, "2010-06-04 18:09:28.777");
        queries.updateLogEntry(largeTestLog);
        int clob_length = sjdbc.queryForInt("select dbms_lob.getLength(description) from process_log where log_id = 5840");
        assertEquals(24142, clob_length);
    }

    public void testAddLogEntry() {
        Log testLog = createLogObject(0, "Execution of archive saver on intgen.org_GBM.bio.Level_1.6.23.0 failed", "2010-03-31 18:09:28.888");
        testLog.setResultId(7);
        queries.addLogEntry(testLog);
        int add_count = sjdbc.queryForInt("select count(*) from process_log where result_id = 7");
        assertEquals(1, add_count);
        int log_id = sjdbc.queryForInt("select log_id from process_log where result_id = 7");
        int curr_seq = sjdbc.queryForInt("SELECT process_log_seq.CURRVAL FROM DUAL");
        assertEquals(curr_seq, log_id);
        String add_desc = sjdbc.queryForObject("select description from process_log where result_id = 7", String.class);
        assertEquals("Execution of archive saver on intgen.org_GBM.bio.Level_1.6.23.0 failed", add_desc);
    }

    public void testAddLogEntryForTime() {
        Log testLog = createLogObject(0, "Execution of archive saver on intgen.org_GBM.bio.Level_1.6.23.0 failed", "2010-05-31 18:09:28.888");
        testLog.setResultId(10);
        queries.addLogEntry(testLog);
        int curr_seq = sjdbc.queryForInt("SELECT process_log_seq.CURRVAL FROM DUAL");
        Date startTime = sjdbc.queryForObject("select start_time from process_log where log_id =" + curr_seq, Date.class);
        assertEquals("2010-05-31 18:09:28.0", startTime.toString());
    }

    public void testAddLogEntryForCLOB() {
        String largeDesc = null;
        try {
            largeDesc = FileUtil.readFile(new File(SAMPLES_FOLDER + "largeFile.txt"), false);
        } catch (IOException io) {
        }
        Log largeTestLog = createLogObject(5843, largeDesc, "2010-06-03 18:09:28.777");
        queries.addLogEntry(largeTestLog);
        int clob_length = sjdbc.queryForInt("select dbms_lob.getLength(description) from process_log where log_id = 5843");
        assertEquals(24142, clob_length);

    }

    public void testAddTransactionLog() {
        SimpleJdbcTemplate template = new SimpleJdbcTemplate(queries.getDataSource());
        List valueList = template.queryForList("select * from transaction_log");
        assertTrue(valueList != null && valueList.size() == 1);
        assertEquals(((Map) (valueList.get(0))).get("ARCHIVE_NAME"), "available_latest");
        assertEquals(((Map) (valueList.get(0))).get("ENVIRONMENT"), "DEV");
        assertEquals(((Map) (valueList.get(0))).get("ISSUCCESSFUL"), "Y");
    }

    public void testAddTransactionLogRecord() {
        SimpleJdbcTemplate template = new SimpleJdbcTemplate(queries.getDataSource());
        List valueList = template.queryForList("select * from transaction_log_record");
        assertTrue(valueList != null && valueList.size() == 2);
        assertEquals(((Map) (valueList.get(0))).get("LOGGING_STATE"), "MD5VALIDATOR");
        assertEquals(((Map) (valueList.get(0))).get("ISSUCCESSFUL"), "N");
        assertEquals(((Map) (valueList.get(1))).get("LOGGING_STATE"), "COOLVALIDATOR");
        assertEquals(((Map) (valueList.get(1))).get("ISSUCCESSFUL"), "Y");

    }


    private void cleanUptransctionTables() {
        SimpleJdbcTemplate template = new SimpleJdbcTemplate(queries.getDataSource());
        template.update("delete from transaction_error ");
        template.update("delete from transaction_log_record ");
        template.update("delete from transaction_log ");
    }

    private void insertBaseTransactionData() {
        SimpleJdbcTemplate template = new SimpleJdbcTemplate(queries.getDataSource());
        Long txLogId = queries.addTransactionLog("available_latest", "DEV");
        queries.addTransactionLogRecord(txLogId, "MD5VALIDATOR");
        queries.addTransactionLogRecord(txLogId, "COOLVALIDATOR");
        queries.updateTransactionLogRecordResult(txLogId, "COOLVALIDATOR", true);
        queries.addErrorMessage(txLogId, "available_latest", " long and boring error message");
        queries.updateTransactionLogStatus(txLogId, true);
    }
}
