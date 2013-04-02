/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.DirectoryLister;

import java.io.File;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for GscExperimentValidator
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith (JMock.class)
public class GscExperimentValidatorFastTest {
    private Mockery context = new JUnit4Mockery();
    private DirectoryLister mockDirectoryLister = context.mock(DirectoryLister.class);
    private GscExperimentValidator validator;
    private Experiment experiment;
    private Archive levelOneArchive, levelTwoArchive, levelThreeArchive;

    @Before
    public void setup() {
        levelOneArchive = new Archive();
        levelOneArchive.setDeployStatus(Archive.STATUS_UPLOADED);
        levelOneArchive.setArchiveType(Archive.TYPE_LEVEL_1);
        levelOneArchive.setDeployLocation("ARCHIVE1.tar.gz");
        levelTwoArchive = new Archive();
        levelTwoArchive.setDeployStatus(Archive.STATUS_UPLOADED);
        levelTwoArchive.setArchiveType(Archive.TYPE_LEVEL_2);
        levelTwoArchive.setDeployLocation("ARCHIVE2.tar.gz");
        levelThreeArchive = new Archive();
        levelThreeArchive.setDeployStatus(Archive.STATUS_UPLOADED);
        levelThreeArchive.setArchiveType(Archive.TYPE_LEVEL_3);
        levelThreeArchive.setDeployLocation("ARCHIVE3.tar.gz");

        validator = new GscExperimentValidator() {
            protected DirectoryLister getDirectoryLister() {
                return mockDirectoryLister;
            }
        };
        experiment = new Experiment();
        experiment.setType(Experiment.TYPE_GSC);
    }

    @Test
    public void testValidateNonGsc() throws Processor.ProcessorException {
        experiment.setType(Experiment.TYPE_CGCC);
        assertTrue(validator.execute(experiment, new QcContext()));
    }

    @Test
    public void testLevel1Gsc() throws Processor.ProcessorException {
        experiment.addArchive(levelOneArchive);
        assertTrue(validator.execute(experiment, new QcContext()));
    }

    @Test
    public void testValidateGsc() throws Processor.ProcessorException {
        final File[] vcfFiles = { new File("1.vcf.gz"), new File("2.vcf.gz") };
        final File[] mafFiles = { new File("1.maf"), new File("2.maf") };
        experiment.addArchive(levelOneArchive);
        experiment.addArchive(levelTwoArchive);
        experiment.addArchive(levelThreeArchive);
        context.checking( new Expectations() {{
            one(mockDirectoryLister).getFilesInDirectoryByExtension("ARCHIVE2", ".vcf.gz");
            will(returnValue(vcfFiles));
            one(mockDirectoryLister).getFilesInDirectoryByExtension("ARCHIVE3", ".vcf.gz");
            will(returnValue(vcfFiles));
            one(mockDirectoryLister).getFilesInDirectoryByExtension("ARCHIVE2", ".maf");
            will(returnValue(mafFiles));
            one(mockDirectoryLister).getFilesInDirectoryByExtension("ARCHIVE3", ".maf");
            will(returnValue(mafFiles));
        }});
        assertTrue(validator.execute(experiment, new QcContext()));
    }

    @Test
    public void testMafInOlderArchive() throws Processor.ProcessorException {
        /*
        tests the following situation:
        archive1 is available (previously processed) and contains 1.maf and 1.vcf.gz
        archive2 is newly uploaded and is replacing archive1 and contains just 1.vcf.gz
        1.maf will be copied from the older archive, so should still pass validation
         */
        levelTwoArchive.setSerialIndex("1");
        levelTwoArchive.setRevision("1");
        Archive existingLevel2Archive = new Archive();
        existingLevel2Archive.setArchiveType(Archive.TYPE_LEVEL_2);
        existingLevel2Archive.setSerialIndex("1");
        existingLevel2Archive.setRevision("0");
        existingLevel2Archive.setDeployLocation("oldArchive.tar.gz");
        experiment.addArchive(levelTwoArchive);
        experiment.addPreviousArchive(existingLevel2Archive);
        context.checking( new Expectations() {{
            one(mockDirectoryLister).getFilesInDirectoryByExtension("ARCHIVE2", ".vcf.gz");
            will(returnValue(new File[] { new File("1.vcf.gz")}));
            one(mockDirectoryLister).getFilesInDirectoryByExtension("ARCHIVE2", ".maf");
            will(returnValue(new File[] {}));
            one(mockDirectoryLister).getFilesInDirectoryByExtension("oldArchive", ".maf");
            will(returnValue(new File[] { new File("1.maf")}));
        }});
        assertTrue(validator.execute(experiment, new QcContext()));
    }

    @Test
    public void testMafWithNoVCF() throws Processor.ProcessorException {
        final File[] vcfFiles = null;
        final File[] mafFiles = { new File("1.maf") };
        experiment.addArchive(levelThreeArchive);
        context.checking( new Expectations() {{
            one(mockDirectoryLister).getFilesInDirectoryByExtension("ARCHIVE3", ".vcf.gz");
            will(returnValue(vcfFiles));
            one(mockDirectoryLister).getFilesInDirectoryByExtension("ARCHIVE3", ".maf");
            will(returnValue(mafFiles));
        }});
        assertTrue(validator.execute(experiment, new QcContext()));
    }

    @Test
    public void testMafMissing() throws Processor.ProcessorException {
        final File[] vcfFiles = { new File("1.vcf.gz"), new File("2.vcf.gz") };
        final File[] mafFiles = { new File("1.maf") };
        experiment.addArchive(levelThreeArchive);
        context.checking( new Expectations() {{
            one(mockDirectoryLister).getFilesInDirectoryByExtension("ARCHIVE3", ".vcf.gz");
            will(returnValue(vcfFiles));
            one(mockDirectoryLister).getFilesInDirectoryByExtension("ARCHIVE3", ".maf");
            will(returnValue(mafFiles));
        }});
        assertFalse(validator.execute(experiment, new QcContext()));
    }
}
