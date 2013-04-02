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
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Log;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.LogQueries;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for ArchiveLoggerImpl
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class ArchiveLoggerImplFastTest {

    private Mockery context = new JUnit4Mockery();
    private ArchiveQueries mockArchiveQueries = context.mock( ArchiveQueries.class );
    private LogQueries mockLogQueries = context.mock( LogQueries.class );

    @Test
    public void testAddLog() {
        Archive a = new Archive();
        a.setId( 1L );
        final Log log = new Log();
        log.setDescription( "test" );
        context.checking( new Expectations() {{
            one( mockLogQueries ).addLogEntry( log );
            will( returnValue( 1 ) );
            one( mockArchiveQueries ).addLogToArchiveEntry( 1L, 1 );
        }} );
        TestableArchiveLoggerImpl archiveLogger = new TestableArchiveLoggerImpl();
        archiveLogger.setLog( log );
        archiveLogger.setArchiveQueries( mockArchiveQueries );
        archiveLogger.setLogQueries( mockLogQueries );
        archiveLogger.addArchiveLog( a, "test" );
        context.assertIsSatisfied();
    }

    class TestableArchiveLoggerImpl extends ArchiveLoggerImpl {

        private Log log;

        public void setLog( Log log ) {
            this.log = log;
        }

        protected Log makeLog( String message ) {
            return log;
        }
    }
}
