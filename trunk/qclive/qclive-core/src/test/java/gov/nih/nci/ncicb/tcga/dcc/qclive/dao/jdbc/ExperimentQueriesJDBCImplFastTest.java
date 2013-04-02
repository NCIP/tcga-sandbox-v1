/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TumorQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ArchiveTypeQueries;

import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class for ExperimentQueriesJDBCImpl
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith (JMock.class)
public class ExperimentQueriesJDBCImplFastTest {

    private Mockery context = new JUnit4Mockery();
    private ArchiveQueries mockArchiveQueries = context.mock(ArchiveQueries.class);
    private ArchiveTypeQueries mockArchiveTypeQueries = context.mock(ArchiveTypeQueries.class);
    private CenterQueries mockCenterQueries = context.mock(CenterQueries.class);
    private PlatformQueries mockPlatformQueries = context.mock(PlatformQueries.class);
    private TumorQueries mockTumorQueries = context.mock(TumorQueries.class);
    private DataTypeQueries mockDataTypeQueries = context.mock(DataTypeQueries.class);
    private ExperimentQueriesJDBCImpl experimentQueries = new ExperimentQueriesJDBCImpl();

    @Before
    public void setup() {
        experimentQueries.setArchiveQueries(mockArchiveQueries);
        experimentQueries.setArchiveTypeQueries(mockArchiveTypeQueries);
        experimentQueries.setCenterQueries(mockCenterQueries);
        experimentQueries.setPlatformQueries(mockPlatformQueries);
        experimentQueries.setTumorQueries(mockTumorQueries);
        experimentQueries.setDataTypeQueries(mockDataTypeQueries);
    }

    @Test
    public void testSetExperimentArchives() {
        // batch one has older available, and newer uploaded archive
        final Archive archive1 = makeArchive(1, Archive.STATUS_AVAILABLE, "1", "1");
        final Archive archive2 = makeArchive(2, Archive.STATUS_UPLOADED, "1", "2");
        // batch 2 has an older validated and newer available archive
        final Archive archive3 = makeArchive(3, Archive.STATUS_VALIDATED, "2", "6");
        final Archive archive4 = makeArchive(4, Archive.STATUS_AVAILABLE, "2", "7");
        // batch 3 has 2 uploaded archives
        final Archive archive5 = makeArchive(5, Archive.STATUS_UPLOADED, "3", "0");
        final Archive archive6 = makeArchive(6, Archive.STATUS_UPLOADED, "3", "2");
        final Platform thePlatform = new Platform();
        thePlatform.setPlatformId(1);
        context.checking(new Expectations() {{
            one(mockArchiveQueries).getArchive(1);
            will(returnValue(archive1));
            one(mockArchiveQueries).getArchive(2);
            will(returnValue(archive2));
            one(mockArchiveQueries).getArchive(3);
            will(returnValue(archive3));
            one(mockArchiveQueries).getArchive(4);
            will(returnValue(archive4));
            one(mockArchiveQueries).getArchive(5);
            will(returnValue(archive5));
            one(mockArchiveQueries).getArchive(6);
            will(returnValue(archive6));
            allowing(mockArchiveTypeQueries).getArchiveType(1);
            will(returnValue(Archive.TYPE_LEVEL_1));
            allowing(mockCenterQueries).getCenterByName(with("domain"), with(any(String.class)));
            will(returnValue(null));
            allowing(mockPlatformQueries).getPlatformForName("platform");
            will(returnValue(thePlatform));
            allowing(mockTumorQueries).getTumorForName("tumor");
            will(returnValue(null));
            allowing(mockDataTypeQueries).getBaseDataTypeDisplayNameForPlatform(1);
            will(returnValue(null));
        }});
        Experiment experiment = new Experiment();
        List<Integer> ids = new ArrayList<Integer>();
        ids.add(1);
        ids.add(2);
        ids.add(3);
        ids.add(4);
        ids.add(5);
        ids.add(6);
        experimentQueries.setExperimentArchives(experiment, ids);
        assertFalse(experiment.getArchives().contains(archive1));
        assertTrue(experiment.getArchives().contains(archive2));
        assertFalse(experiment.getArchives().contains(archive3));
        assertTrue(experiment.getArchives().contains(archive4));
        assertFalse(experiment.getArchives().contains(archive5));
        assertTrue(experiment.getArchives().contains(archive6));
        assertTrue(experiment.getPreviousArchives().contains(archive1));
    }

    @Test
    public void testSetExperimentArchivesClassic() {
        final Archive classicArchive = makeArchive(1, Archive.STATUS_AVAILABLE, "1", "1");
        classicArchive.setArchiveTypeId(7);

        context.checking(new Expectations() {{
            one(mockArchiveQueries).getArchive(1);
            will(returnValue(classicArchive));
            one(mockArchiveTypeQueries).getArchiveType(7);
            will(returnValue(Archive.TYPE_CLASSIC));
            allowing(mockCenterQueries).getCenterByName(with("domain"), with(any(String.class)));
            will(returnValue(null));
            one(mockPlatformQueries).getPlatformForName("platform");
            will(returnValue(new Platform()));
            one(mockTumorQueries).getTumorForName("tumor");
            will(returnValue(null));
            one(mockDataTypeQueries).getBaseDataTypeDisplayNameForPlatform(null);
            will(returnValue(null));
        }});

        Experiment experiment = new Experiment();
        List<Integer> ids = new ArrayList<Integer>();
        ids.add(1);
        experimentQueries.setExperimentArchives(experiment, ids);
        // archive should not have been added
        assertEquals(0, experiment.getArchives().size());
        assertEquals(0, experiment.getPreviousArchives().size());
    }

    @Test
    public void testSetExperimentArchivesNoType() {
        final Archive archive = makeArchive(1, Archive.STATUS_AVAILABLE, "1", "1");
        archive.setArchiveTypeId(null);

        context.checking(new Expectations() {{
            one(mockArchiveQueries).getArchive(1);
            will(returnValue(archive));
        }});

        Experiment experiment = new Experiment();
        List<Integer> ids = new ArrayList<Integer>();
        ids.add(1);
        experimentQueries.setExperimentArchives(experiment, ids);
        // archive should not have been added
        assertEquals(0, experiment.getArchives().size());
        assertEquals(0, experiment.getPreviousArchives().size());
    }

    @Test
    public void testSetExperimentArchivesInvalidType() {
        final Archive classicArchive = makeArchive(1, Archive.STATUS_AVAILABLE, "1", "1");
        classicArchive.setArchiveTypeId(100);

        context.checking(new Expectations() {{
            one(mockArchiveQueries).getArchive(1);
            will(returnValue(classicArchive));
            one(mockArchiveTypeQueries).getArchiveType(100);
            will(returnValue(null));
            allowing(mockCenterQueries).getCenterByName(with("domain"), with(any(String.class)));
            will(returnValue(null));
            one(mockPlatformQueries).getPlatformForName("platform");
            will(returnValue(new Platform()));
            one(mockTumorQueries).getTumorForName("tumor");
            will(returnValue(null));
            one(mockDataTypeQueries).getBaseDataTypeDisplayNameForPlatform(null);
            will(returnValue(null));
        }});

        Experiment experiment = new Experiment();
        List<Integer> ids = new ArrayList<Integer>();
        ids.add(1);
        experimentQueries.setExperimentArchives(experiment, ids);
        // archive should not have been added
        assertEquals(0, experiment.getArchives().size());
        assertEquals(0, experiment.getPreviousArchives().size());
    }

    private Archive makeArchive(final long id, final String status, final String batch, final String revision) {
        final Archive archive = new Archive();
        archive.setId(id);
        archive.setDeployStatus(status);
        archive.setSerialIndex(batch);
        archive.setRevision(revision);
        archive.setArchiveTypeId(1);
        archive.setDomainName("domain");
        archive.setPlatform("platform");
        archive.setTumorType("tumor");
        archive.setDataType("datatype");
        archive.setDeployLocation("testlocation");
        return archive;
    }
}
