/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao.usage;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.usage.AbstractUsageLogger.UsageSessionAction;

import java.io.File;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.jdbc.support.incrementer.OracleSequenceMaxValueIncrementer;

/**
 * Test class for UsageLoggerJDBCImpl.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class UsageLoggerJDBCImplSlowTest extends DBUnitTestCase {

    private String testKey;
    private UsageLoggerJDBCImpl logger;
    private static final String PROPERTIES_FILE = "tcga_unittest.properties";
    private static final String TEST_DATA_FOLDER = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "/usageLogger/usageLoggerData.xml";

    public UsageLoggerJDBCImplSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    @Override
    protected void setUpDatabaseConfig(DatabaseConfig config) {
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new OracleDataTypeFactory());
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        logger = new UsageLoggerJDBCImpl();
        dataSource = getDataSource();
        logger.setDataSource(getDataSource());
        logger.setSessionIdIncrementer( new OracleSequenceMaxValueIncrementer(dataSource, "portal_session_id_seq" ) );
        logger.setSessionActionIdIncrementer( new OracleSequenceMaxValueIncrementer(dataSource, "portal_session_action_id_seq" ) );
        testKey = "TEST_" + new Random().nextInt( 1000 );        
    }    

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.CLEAN_INSERT;
    }

    @Override
    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.DELETE_ALL;
    }    

    /**
     * Tests that the getSessionId method works as expected.
     *
     * @throws UsageLoggerException if an error occurs
     * @throws SQLException         if an error occurs
     */
    public void testGetSessionId() throws UsageLoggerException, SQLException {
            // get the ID, which will insert the session
            Integer id = logger.getSessionId( testKey );
            assertNotNull( id );
            // now get it again, which should just find the same one that was just added
            Integer id2 = logger.getSessionId( testKey );
            assertNotNull( id2 );
            assertEquals( id, id2 );
    }

    /**
     * Tests that IDs of action_types in database are what is expected.  This is manually synched with
     * the things in usage_logging.sql for now.
     *
     * @throws UsageLoggerException if there is an error
     */
    public void testGetActionTypeId() throws UsageLoggerException {
        // go through enum of action types and make sure each one is findable in the db by name
        for(AbstractUsageLogger.ActionType type : AbstractUsageLogger.ActionType.values()) {
            assertNotNull( type.toString(), logger.getActionTypeId( AbstractUsageLogger.getActionName(type) ) );
        }
        // make sure one that's not supposed to be there throws an exception
        boolean exceptionThrown = false;
        try {
            logger.getActionTypeId( "TEST FAKE" );
        }
        catch(IllegalArgumentException e) {
            exceptionThrown = true;
        }
        assertTrue( "Exception was not thrown when invalid action type was given", exceptionThrown );
    }

    /**
     * Test the logging of a single action
     *
     * @throws SQLException         if an error occurs
     * @throws UsageLoggerException if an error occurs
     */
    public void testLogAction() throws UsageLoggerException, SQLException {
        // log the action
        String testValue = "test value";
        logger.logAction( testKey, AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.DAM_REQUESTED), testValue );
        // there was no exception, so logging is successful
        Integer id = logger.getSessionId( testKey );
        assertNotNull( id );
    }

    /**
     * Test the logging of an action group
     *
     * @throws SQLException         if an error occurs
     * @throws UsageLoggerException if an error occurs
     */
    public void testLogActionGroup() throws SQLException, UsageLoggerException {
        String level1Val = "a";
        String clinicalVal = "b";
        // make a group of actions
        Map<String, Object> actions = new HashMap<String, Object>();
        actions.put( AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.LEVEL_1_FILES_SELECTED), level1Val );
        actions.put( AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.CLINICAL_FILES_SELECTED), clinicalVal );
        actions.put( AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.FILES_SELECTED), clinicalVal );
        logger.logActionGroup( testKey, actions );
        int sessionId = logger.getSessionId(testKey);
        assertNotNull(sessionId);
        List<UsageSessionAction> sessionActionsRet = logger.getSessionActions(sessionId);
        assertNotNull(sessionActionsRet);
    }

    public void testGetSessions() throws UsageLoggerException, SQLException {
        String testValue = "test value";
        logger.logAction( testKey, AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.DAM_REQUESTED), testValue );
        Calendar oneHourAgo = Calendar.getInstance();
        oneHourAgo.add(Calendar.HOUR_OF_DAY, -1);
        List<AbstractUsageLogger.UsageSession> sessions = logger.getAllSessionsForDates(oneHourAgo.getTime(), Calendar.getInstance().getTime());
        assertNotNull( sessions );
        AbstractUsageLogger.UsageSession testSession = null;
        for(AbstractUsageLogger.UsageSession session : sessions) {
            if(session.sessionKey.equals( testKey )) {
                testSession = session;
            }
        }
        assertNotNull( testSession );
        assertTrue( testSession.actions.size() == 1 );
        assertEquals( AbstractUsageLogger.getActionName(AbstractUsageLogger.ActionType.DAM_REQUESTED), testSession.actions.get( 0 ).name );
        assertEquals( testValue, testSession.actions.get( 0 ).value );
    }
    
}
