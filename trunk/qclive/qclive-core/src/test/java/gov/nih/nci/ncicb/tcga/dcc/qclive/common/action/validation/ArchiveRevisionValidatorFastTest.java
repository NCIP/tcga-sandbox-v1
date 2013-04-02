/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test for ArchiveRevisionValidator.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class ArchiveRevisionValidatorFastTest {
    private Mockery context;
    private ArchiveRevisionValidator archiveRevisionValidator;
    private Archive archive;
    private QcContext qcContext;
    private ArchiveQueries mockArchiveQueries;
    private static final long archiveRevision = 3;

    @Before
    public void setUp() throws Exception {
        context = new JUnit4Mockery();
        mockArchiveQueries = context.mock(ArchiveQueries.class);
        archiveRevisionValidator = new ArchiveRevisionValidator();
        archive = new Archive();
        qcContext = new QcContext();
        archiveRevisionValidator.setArchiveQueries(mockArchiveQueries);
        archive.setArchiveFile(new File("domain_DIS.platform.Level_1.5." + archiveRevision + ".0" + ".tar.gz"));
        archive.setDeployLocation(archive.getArchiveFile().getCanonicalPath());
        final ArchiveNameValidator archiveNameValidator = new ArchiveNameValidator();
        assertTrue("archive setup failed due to archive name validator",
                archiveNameValidator.execute(archive, qcContext));
    }

    @Test
    public void testNoOtherRevisions() throws Processor.ProcessorException {
        setMaxRevision(-1);
        assertTrue(archiveRevisionValidator.execute(archive, qcContext));
        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testOtherRevisionIsLower() throws Processor.ProcessorException {
        setMaxRevision(archiveRevision - 1);
        assertTrue(archiveRevisionValidator.execute(archive, qcContext));
        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testOtherRevisionIsHigher() throws Processor.ProcessorException {
        setMaxRevision(archiveRevision + 2);
        assertFalse(archiveRevisionValidator.execute(archive, qcContext));
        assertEquals("An error occurred while processing archive 'domain_DIS.platform.Level_1.5.3.0.tar.gz': The next revision for this serial index should be 6 or greater (revision 5 already exists)", qcContext.getErrors().get(0));
    }

    @Test
    public void testArchiveRevisionNotANumber() {
        archive.setRevision("a");
        try {
            assertFalse(archiveRevisionValidator.execute(archive, qcContext));
            fail("Exception wasn't thrown");
        } catch (Processor.ProcessorException e) {
            assertEquals("Archive revision must be a number", qcContext.getErrors().get(0));
        }
    }

    private void setMaxRevision(final long maxRevision) throws Processor.ProcessorException {
        context.checking(new Expectations() {{
            one(mockArchiveQueries).getMaxRevisionForArchive(archive, true);
            will(returnValue(maxRevision));
        }});
    }
}
