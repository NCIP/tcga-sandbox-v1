/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for Experiment object.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class ExperimentFastTest {

    private Experiment experiment;
    private String experimentName = "center_disease.platform";
    private Archive archive10, archive11, archive20, archive30, archive31, archive40;

    @Before
    public void setUp() {
        experiment = new Experiment();
        archive10 = makeArchive(experimentName + ".Level_1.1.0.0", Archive.STATUS_AVAILABLE, Archive.TYPE_LEVEL_1, 1, 0);
        archive11 = makeArchive(experimentName + ".Level_1.1.1.0", Archive.STATUS_UPLOADED, Archive.TYPE_LEVEL_1, 1, 1);
        archive20 = makeArchive(experimentName + ".Level_1.2.0.0", Archive.STATUS_AVAILABLE, Archive.TYPE_LEVEL_1, 2, 0);
        archive30 = makeArchive(experimentName + ".Level_1.3.0.0", Archive.STATUS_AVAILABLE, Archive.TYPE_LEVEL_1, 3, 0);
        archive31 = makeArchive(experimentName + ".Level_1.3.1.0", Archive.STATUS_UPLOADED, Archive.TYPE_LEVEL_1, 3, 1);
        archive40 = makeArchive(experimentName + ".Level_1.4.0.0", Archive.STATUS_UPLOADED, Archive.TYPE_LEVEL_1, 4, 0);

        experiment.addArchive(archive11);
        experiment.addArchive(archive20);
        experiment.addArchive(archive31);
        experiment.addArchive(archive40);
        experiment.addPreviousArchive(archive10);
        experiment.addPreviousArchive(archive30);                        
    }

    /**
     *  Convenience method for making Archive objects.
     *
     * @param name the name of the archive
     * @param status the deploy status of the archive
     * @param type the archive type
     * @param batch the batch (serial index) of the archive
     * @param revision the revision number of the archive
     * @return and archive object
     */
    public static Archive makeArchive(final String name, final String status, final String type, final int batch, final int revision) {
        Archive a = new Archive();
        a.setDeployLocation(name + ".tar.gz");
        a.setRealName(name);
        a.setSerialIndex(String.valueOf(batch));
        a.setRevision(String.valueOf(revision));
        a.setDeployStatus(status);
        a.setArchiveType(type);
        return a;
    }

    @Test
    public void testSetNameWithNoArchiveFiltering() {
        checkSetName(experimentName);
    }

    @Test
    public void testSetNameWithArchiveFiltering() {
        checkSetName(experimentName + ".thisIsTheArchiveFilter");
    }

    /**
     * Sets the experiment name with the given input and verify some assertions
     * 
     * @param name the experiment name
     */
	private void checkSetName(final String name) {
		
		experiment.setName(name);
        assertEquals("center", experiment.getCenterName());
        assertEquals("disease", experiment.getTumorName());
        assertEquals("platform", experiment.getPlatformName());
        assertEquals(experimentName, experiment.getName());
	}

    @Test(expected = IllegalArgumentException.class)
    public void testSetNameFail() {
        experiment.setName("this is not a valid name");
    }

    @Test
    public void testEquals() {
        String name = "a_b.c";
        experiment.setName(name);
        Experiment experiment2 = new Experiment();
        experiment2.setName(name); // same name
        assertTrue(experiment.equals(experiment2));
        Experiment experiment3 = new Experiment();
        experiment3.setName("x_y.z");
        assertFalse(experiment.equals(experiment3));
    }

    @Test
    public void testGetArchivesForStatus() {
        List<Archive> archives = experiment.getArchivesForStatus(Archive.STATUS_UPLOADED);
        assertEquals(3, archives.size());
        assertTrue(archives.contains(archive11));
        assertTrue(archives.contains(archive31));
        assertTrue(archives.contains(archive40));
    }

    @Test
    public void testAddPreviousArchive() {
        Archive someArchive = new Archive();
        someArchive.setRealName("some archive");
        experiment.addPreviousArchive(someArchive);
        assertTrue(experiment.getPreviousArchives().contains(someArchive));
    }

    @Test
    public void testGetPreviousArchiveFor() {
        assertEquals(archive10, experiment.getPreviousArchiveFor(archive11));
        assertEquals(archive30, experiment.getPreviousArchiveFor(archive31));
        assertNull(experiment.getPreviousArchiveFor(archive40));
    }

    @Test
    public void testGetDescriptionForType() {
        assertEquals(Experiment.BIOSPECIMEN_CORE_RESOURCE, Experiment.getDescriptionForType(Experiment.TYPE_BCR));
        assertEquals(Experiment.CANCER_GENOMIC_CHARACTERIZATION_CENTER, Experiment.getDescriptionForType(Experiment.TYPE_CGCC));
        assertEquals(Experiment.GENOME_SEQUENCING_CENTER, Experiment.getDescriptionForType(Experiment.TYPE_GSC));
        assertEquals("foo", Experiment.getDescriptionForType("foo"));
    }

    @Test
    public void testGetRevisedArchiveFor() {
        assertEquals(archive31, experiment.getRevisedArchiveFor(archive30));
        assertEquals(archive11, experiment.getRevisedArchiveFor(archive10));
        assertNull(experiment.getRevisedArchiveFor(archive20));
    }
}
