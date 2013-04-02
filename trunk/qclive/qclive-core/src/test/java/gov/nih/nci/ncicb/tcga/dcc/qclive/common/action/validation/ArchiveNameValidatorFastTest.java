/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Test class for ArchiveNameValidator
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class ArchiveNameValidatorFastTest {
    private final Mockery context = new JUnit4Mockery();

    private ArchiveNameValidator val;
    private Archive archive;
    private QcContext qcContext;

    private final static String center = "broad.mit.edu";
    private final static String disease = "GBM";
    private final static String platform = "Genome_Wide_SNP_6";
    private final static String type = "Level_2";
    private final static String batch = "3";
    private final static String revision = "2";
    private final static String series = "0";
    private final static String badSeries = "1";


    @Before
    public void setup() throws Exception {
        qcContext = new QcContext();
        val = new ArchiveNameValidator();
        context.assertIsSatisfied();
        archive = new Archive();
        archive.setArchiveFile(new File(center + "_" + disease + "." + platform + "." + type + "." +
                batch + "." + revision + "." + series + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION));
        archive.setDeployLocation(archive.getArchiveFile().getCanonicalPath());
        archive.setPlatform(platform);
        archive.setTumorType(disease);
        archive.setDomainName(center);
        archive.setSerialIndex(batch);
        qcContext.setArchive(archive);

    }

    @Test
    public void testExecute() throws Processor.ProcessorException {
        assertTrue("Errors: " + qcContext.getErrors(), val.execute(archive, qcContext));
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(0, qcContext.getWarningCount());
        assertEquals(center, archive.getDomainName());
        assertEquals(disease, archive.getTumorType());
        assertEquals(platform, archive.getPlatform());
        assertEquals(type, archive.getArchiveType());
        assertEquals(batch, archive.getSerialIndex());
        assertEquals(revision, archive.getRevision());
        assertEquals(series, archive.getSeries());
    }

    @Test
    public void testInvalidExtension() throws Processor.ProcessorException {
        final File invalidFile = new File("grover_cleveland.zip");
        archive.setArchiveFile(invalidFile);
        assertEquals("archive name validation on grover_cleveland.zip", val.getDescription(archive));
        try {
            assertFalse(val.execute(archive, qcContext));
            fail("Exception was not thrown");
        } catch (Processor.ProcessorException e) {
            assertEquals(1, qcContext.getErrorCount());
            assertEquals("Archives must be .tar.gz or .tar files, but 'grover_cleveland.zip' does not end with '.tar.gz or .tar'.", qcContext.getErrors().get(0));
        }
    }

    @Test
    public void testInvalidName() throws Exception {
        final File invalidName = new File("not_good.tar.gz");
        archive.setArchiveFile(invalidName);
        archive.setDeployLocation(invalidName.getCanonicalPath());
        try {
            assertFalse(val.execute(archive, qcContext));
            fail("Exception was not thrown");
        } catch (Processor.ProcessorException e) {
            assertEquals(1, qcContext.getErrorCount());
            assertEquals("Archive filename (not_good.tar.gz) does not have valid format.  Should be: '[center]_[disease].[platform].[archive_type].[batch].[revision].[series].tar.gz'.", qcContext.getErrors().get(0));
        }
    }

    @Test
    public void testInvalidSeries() throws Processor.ProcessorException {
        final File invalidSeriesFile = new File(center + "_" + disease + "." + platform + "." + type + "." +
                batch + "." + revision + "." + badSeries + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        archive.setArchiveFile(invalidSeriesFile);
        assertFalse(val.execute(archive, qcContext));
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("An error occurred while processing archive 'broad.mit.edu_GBM.Genome_Wide_SNP_6.Level_2.3.2.1.tar.gz': Archive series must be '0'", qcContext.getErrors().get(0));
    }

    @Test
    public void testMissingSeries() throws Processor.ProcessorException {
        final File missingSeries = new File(center + "_" + disease + "." + platform + "." + type + "." +
                batch + "." + revision + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        archive.setArchiveFile(missingSeries);
        assertFalse(val.execute(archive, qcContext));
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("An error occurred while processing archive 'broad.mit.edu_GBM.Genome_Wide_SNP_6.Level_2.3.2.tar.gz': Archive is missing its series.  Name should be: 'broad.mit.edu_GBM.Genome_Wide_SNP_6.Level_2.3.2.0'", qcContext.getErrors().get(0));
    }

    @Test
    public void testGetExpectedArchiveNameFormat() {
        assertEquals("[center]_[disease].[platform].[archive_type].[batch].[revision].[series]", ArchiveNameValidator.getExpectedArchiveNameFormat());
    }
}
