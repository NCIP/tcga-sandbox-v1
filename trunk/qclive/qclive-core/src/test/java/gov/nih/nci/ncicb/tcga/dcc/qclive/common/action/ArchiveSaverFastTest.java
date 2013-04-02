/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Platform;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.PlatformQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.TumorQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ArchiveTypeQueries;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import static junit.framework.Assert.assertEquals;

/**
 * Test class for ArchiveSaver
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class ArchiveSaverFastTest {

    private ArchiveSaver saver = new ArchiveSaver();
    private Mockery context = new JUnit4Mockery();
    private ArchiveQueries mockDiseaseArchiveQueries;
    private ArchiveQueries mockArchiveQueries;
    private CenterQueries mockCenterQueries;
    private PlatformQueries mockPlatformQueries;
    private DataTypeQueries mockDataTypeQueries;
    private TumorQueries mockTumorQueries;
    private ArchiveTypeQueries mockArchiveTypeQueries;
    private Center center;
    private Archive archive;
    private Platform platform;
    private Tumor tumor;

    @Before
    public void setup() {
        mockArchiveQueries = context.mock(ArchiveQueries.class, "ArchiveQueries_1");
        mockDiseaseArchiveQueries = context.mock(ArchiveQueries.class, "ArchiveQueries_2");
        mockCenterQueries = context.mock(CenterQueries.class);
        mockPlatformQueries = context.mock(PlatformQueries.class);
        mockDataTypeQueries = context.mock(DataTypeQueries.class);
        mockTumorQueries = context.mock(TumorQueries.class);
        mockArchiveTypeQueries = context.mock(ArchiveTypeQueries.class);
        saver.setCommonArchiveQueries(mockArchiveQueries);
        saver.setDiseaseArchiveQueries(mockDiseaseArchiveQueries);
        saver.setCenterQueries(mockCenterQueries);
        saver.setPlatformQueries(mockPlatformQueries);
        saver.setDataTypeQueries(mockDataTypeQueries);
        saver.setTumorQueries(mockTumorQueries);
        saver.setArchiveTypeQueries(mockArchiveTypeQueries);
        center = new Center();
        center.setCenterId(1);
        center.setCenterName("center");
        archive = new Archive();
        archive.setArchiveFile(new File("test" + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION));
        archive.setArchiveType("testType");
        archive.setPlatform("platform");
        archive.setDomainName("center");
        archive.setId(0L); // means unsaved
        platform = new Platform();
        platform.setPlatformId(2);
        platform.setPlatformName("platform");
        tumor = new Tumor();
        tumor.setTumorId(3);
        tumor.setTumorName("tumor");

        context.checking(new Expectations() {{
            one(mockCenterQueries).getCenterByName(with(any(String.class)), with(any(String.class)));
            will(returnValue(center));
            one(mockPlatformQueries).getPlatformForName(with(any(String.class)));
            will(returnValue(platform));
            one(mockTumorQueries).getTumorForName(with(any(String.class)));
            will(returnValue(tumor));
            one(mockDataTypeQueries).getBaseDataTypeDisplayNameForPlatform(with(any(Integer.class)));
            will(returnValue("dataType"));
            one(mockArchiveTypeQueries).getArchiveTypeId("testType");
            will(returnValue(5));
            one(mockDataTypeQueries).getCenterTypeIdForPlatformId(2);
            will(returnValue(Experiment.TYPE_CGCC));
        }});
    }

    @Test
    public void testSaveNewArchive() throws Processor.ProcessorException {
        // test for when all DAOs succeed and Archive is new
        context.checking(new Expectations() {{
            one(mockArchiveQueries).getArchiveIdByName("test");
            will(returnValue(-1L));
            one(mockArchiveQueries).addArchive(with(any(Archive.class)));
            will(returnValue(42L));
            one(mockDiseaseArchiveQueries).addArchive(with(any(Archive.class)), with(any(Boolean.class)));
            will(returnValue(42L));
        }});
        QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);
        saver.execute(archive, qcContext);
        assertEquals((Long) 42L, archive.getId());
        assertEquals(new Integer(5), archive.getArchiveTypeId());
        assertEquals(Experiment.TYPE_CGCC, archive.getExperimentType());
    }

    @Test
    public void testSaveExistingArchive() throws Processor.ProcessorException {
        archive.setId(33L);
        archive.setDeployStatus("Uploaded");
        final Date now = Calendar.getInstance().getTime();
        archive.setDateAdded(now);
        context.checking(new Expectations() {{
            one(mockArchiveQueries).getArchiveIdByName("test");
            will(returnValue(33L));
            one(mockArchiveQueries).updateArchiveStatus(archive);
            one(mockArchiveQueries).updateAddedDate(33L, now);
            one(mockArchiveQueries).updateDeployLocation(archive);
            one(mockDiseaseArchiveQueries).updateArchiveStatus(archive);
            one(mockDiseaseArchiveQueries).updateAddedDate(33L, now);
            one(mockDiseaseArchiveQueries).updateDeployLocation(archive);

        }});
        QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);
        saver.execute(archive, qcContext);
    }
}
