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

import java.io.File;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for QcStatsLogger implementation.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class QcStatsLoggerImplFastTest {

	private static final String SAMPLES_DIR = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private Mockery context = new JUnit4Mockery();
    private ArchiveQueries mockArchiveQueries = context.mock(ArchiveQueries.class);

    @Test
    public void testLogIncomingArchive() {
        final File testFile = new File(SAMPLES_DIR + "qclive/SDRFHeaders.txt");
        context.checking(new Expectations() {{
            one(mockArchiveQueries).setArchiveInitialSize(1L, testFile.length() / 1000);
        }});
        QcStatsLoggerImpl logger = new QcStatsLoggerImpl();
        logger.setArchiveQueries(mockArchiveQueries);
        Archive archive = new Archive();
        archive.setId(1L);
        archive.setArchiveFile(testFile);
        logger.logIncomingArchive(archive);
    }

    public void testLogDeployedArchive() {
        final File testFile = new File(SAMPLES_DIR + "samples/qclive/SDRFHeaders.txt");
        context.checking(new Expectations() {{
            one(mockArchiveQueries).setArchiveFinalSize(1L, testFile.length() / 1000);
        }});
        QcStatsLoggerImpl logger = new QcStatsLoggerImpl();
        logger.setArchiveQueries(mockArchiveQueries);
        Archive archive = new Archive();
        archive.setId(1L);
        archive.setArchiveFile(testFile);
        logger.logDeployedArchive(archive);
    }
}
