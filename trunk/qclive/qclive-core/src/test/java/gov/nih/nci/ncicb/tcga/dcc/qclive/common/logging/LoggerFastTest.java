/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging;

import static junit.framework.Assert.assertEquals;

import org.apache.log4j.Level;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test for Logger
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class LoggerFastTest {

    private Logger logger = new LoggerImpl();
    private LoggerDestination mockDestination;
    Mockery context = new JUnit4Mockery();

    @Before
    public void setUp() {
        logger = new LoggerImpl();
        mockDestination = context.mock( LoggerDestination.class );
        logger.addDestination( mockDestination );
    }

    @Test
    public void testLog() throws LoggerDestination.LoggerException {
        final String message = "message";
        // mock destination should get three calls to logToDestination
        context.checking( new Expectations() {{
            one( mockDestination ).logToDestination( Level.ERROR, message );
            one( mockDestination ).logToDestination( Level.WARN, message );
            one( mockDestination ).logToDestination( Level.INFO, message );
        }} );
        logger.log( Level.ERROR, message );
        logger.log( Level.WARN, message );
        logger.log( Level.INFO, message );
    }

    @Test
    public void testAddDestination() {
        assertEquals( mockDestination, logger.getDestinations().get( 0 ) );
    }
}
