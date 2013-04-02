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
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test class for BcrExperimentValidator
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class BcrExperimentValidatorFastTest {

    private static final String SAMPLES_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;
    private String archiveWithLocalXsd = SAMPLES_DIR
            + "qclive/bcrExperimentValidator/good/a1"
            + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;
    private String archiveWithoutLocalXsd = SAMPLES_DIR
            + "qclive/bcrExperimentValidator/bad/a2"
            + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;
    private BcrExperimentValidator validator = new BcrExperimentValidator();
    private Experiment experiment;
    private QcContext context;

    @Before
    public void setup() {
        validator = new BcrExperimentValidator();
        validator.setClinicalPlatform("bio");
        context = new QcContext();
    }

    @Test
    public void testValidateNonBcrArchive() throws Processor.ProcessorException {
        experiment = new Experiment();
        experiment.setType(Experiment.TYPE_BCR);
        experiment.setPlatformName("nonbio");
        context.setExperiment(experiment);
        assertTrue(validator.execute(experiment, context));
    }

    @Test
    public void testValidateBcrArchiveWithLocalXSD()
            throws Processor.ProcessorException {
        testValidateBcrArchive(archiveWithLocalXsd, false);
    }

    @Test
    public void testValidateBcrArchiveWithoutLocalXSD()
            throws Processor.ProcessorException {
        testValidateBcrArchive(archiveWithoutLocalXsd, true);

    }

    @Test
    public void testValidateMultipleBcrArchives()
            throws Processor.ProcessorException {
        experiment = new Experiment();
        experiment.setType(Experiment.TYPE_BCR);
        experiment.setPlatformName("bio");
        context.setExperiment(experiment);
        Archive invalidArchive = new Archive();
        invalidArchive.setArchiveFile(new File(archiveWithLocalXsd));
        invalidArchive.setDeployLocation(archiveWithLocalXsd);
        invalidArchive.setDeployStatus(Archive.STATUS_UPLOADED);
        experiment.addArchive(invalidArchive);

        Archive validArchive = new Archive();
        validArchive.setArchiveFile(new File(archiveWithoutLocalXsd));
        validArchive.setDeployLocation(archiveWithoutLocalXsd);
        validArchive.setDeployStatus(Archive.STATUS_UPLOADED);
        experiment.addArchive(validArchive);

        assertFalse(validator.execute(experiment, context));
        assertEquals(Archive.STATUS_INVALID, experiment.getArchives().get(0)
                .getDeployStatus());
        assertEquals(Archive.STATUS_UPLOADED, experiment.getArchives().get(1)
                .getDeployStatus());
    }

    private void testValidateBcrArchive(final String archiveName,
                                        final boolean shouldPass) throws Processor.ProcessorException {
        experiment = new Experiment();
        experiment.setType(Experiment.TYPE_BCR);
        experiment.setPlatformName("bio");
        context.setExperiment(experiment);
        Archive archive = new Archive();
        archive.setArchiveFile(new File(archiveName));
        archive.setDeployLocation(archiveName);
        archive.setDeployStatus(Archive.STATUS_UPLOADED);
        experiment.addArchive(archive);

        assertEquals(shouldPass, validator.execute(experiment, context));
        if (shouldPass) {
            assertEquals(Archive.STATUS_UPLOADED,
                    experiment.getArchives().get(0).getDeployStatus());
            assertEquals(0, context.getErrorCount());
        } else {
            assertEquals(Archive.STATUS_INVALID, experiment.getArchives()
                    .get(0).getDeployStatus());
            assertEquals(1, context.getErrorCount());
        }
    }

    @Test
    public void testGetName() {
        assertEquals("BCR experiment validation", validator.getName());
    }

}
