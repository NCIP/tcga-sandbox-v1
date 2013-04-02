/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.usage;

import gov.nih.nci.ncicb.tcga.dcc.dam.dao.usage.UsageLoggerException;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.usage.UsageLoggerFileImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;

/**
 * Test class for UsageLoggerFileImpl.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class UsageLoggerFileImplFastTest extends TestCase {

    // can be used by this or other test classes to parse a usage log file
    // returns a Map keyed by session, value is a map of actions and values
    // note, for non-test purposes, the UsageLogger.getAllSessions method should be used.
    public static Map<String, Map<String, String>> parseLog( UsageLoggerFileImpl logger ) throws IOException {

        BufferedReader logFile = null;

        try {
            Map<String, Map<String, String>> log = new HashMap<String, Map<String, String>>();
            //noinspection IOResourceOpenedButNotSafelyClosed
            logFile = new BufferedReader( new FileReader( logger.getFile() ) );
            String line = logFile.readLine();
            Pattern actionPattern = Pattern.compile( "^\\[(.+)\\](.+)=(.*)@(.+)$" );
            while(line != null) {
                Matcher m = actionPattern.matcher( line );
                if(m.matches()) {
                    String sessionId = m.group( 1 );
                    String actionName = m.group( 2 );
                    String value = m.group( 3 );
                    String date = m.group( 4 );
                    if(value.length() == 0) {
                        value = date;
                    }
                    Map<String, String> sessionLog = log.get( sessionId );
                    if(sessionLog == null) {
                        sessionLog = new HashMap<String, String>();
                        log.put( sessionId, sessionLog );
                    }
                    sessionLog.put( actionName, value );
                }
                line = logFile.readLine();
            }
            return log;
        } finally {
            IOUtils.closeQuietly(logFile);
        }
    }

    private static String thisFolder = 
    	Thread.currentThread().getContextClassLoader().getResource("gov/nih/nci/ncicb/tcga/dcc/dam/dao/usage").getPath();
    public static UsageLoggerFileImpl logger;

    public void testGetFilename() {
        assertEquals( "usageLoggerFileImplTest.log", logger.getFilename() );
    }

    public void testGetLogdir() {
        assertEquals( thisFolder, logger.getLogdir() );
    }

    public void testGetFile() throws IOException {
        assertEquals( new File( logger.getLogdir() + "/" + logger.getFilename() ).getPath(), logger.getFile().getPath() );
    }

    public void testLogAction() throws UsageLoggerException, IOException {
        // log plain value
        logger.logAction( "1", "TEST ACTION", "TEST VALUE" );
        Map<String, Map<String, String>> log = parseLog( logger );
        assertNotNull( log.get( "1" ) );
        assertNotNull( log.get( "1" ).get( "TEST ACTION" ) );
        assertEquals( "TEST VALUE", log.get( "1" ).get( "TEST ACTION" ) );
    }

    public void setUp() {
        logger = new UsageLoggerFileImpl( thisFolder, "usageLoggerFileImplTest.log" );
    }
}
