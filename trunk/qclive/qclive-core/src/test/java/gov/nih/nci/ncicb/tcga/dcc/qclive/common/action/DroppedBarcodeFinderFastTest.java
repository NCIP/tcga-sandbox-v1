/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BCRID;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.BCRIDQueries;

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
 * Test class for DroppedBarcodeFinder
 *
 * @author Namrata Rane
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class DroppedBarcodeFinderFastTest {

    private Mockery context = new JUnit4Mockery();
    private BCRIDQueries bcrIdQueries = context.mock(BCRIDQueries.class);
    private Archive archive = new Archive("archiveName.tar.gz");
    private Archive previousArchive = new Archive();
    private Long previousArchiveId = 1L;
    private Long currentArchiveId = 2L;
    private final QcContext qcContext = new QcContext();
    private final List<BCRID> currentBarcodeList = new ArrayList<BCRID>();
    private final List<BCRID> previousBarcodeList = new ArrayList<BCRID>();
    private DroppedBarcodeFinder droppedBarcodeFinder; 

    @Before
    public void setup() {
        archive.setId(currentArchiveId);
        archive.setArchiveType("archiveType");
        archive.setSerialIndex("1");
        previousArchive.setId(previousArchiveId);
        previousArchive.setArchiveType("archiveType");
        previousArchive.setSerialIndex("1");
        Experiment experiment = new Experiment();
        qcContext.setExperiment(experiment);
        List<Archive> previousArchives = new ArrayList<Archive>();
        previousArchives.add(previousArchive);
        experiment.setPreviousArchives(previousArchives);
        List<Archive> archiveList = new ArrayList<Archive>();
        archiveList.add(archive);
        experiment.setArchives(archiveList);
        droppedBarcodeFinder = new DroppedBarcodeFinder();
        droppedBarcodeFinder.setBcrIdQueries(bcrIdQueries);
        qcContext.setArchive(archive);
    }

    @Test
    public void testDroppedBarcodeFinder() throws Processor.ProcessorException {
        mockDroppedBarcodes();
        mockBCRQueries();
        droppedBarcodeFinder.execute(archive, qcContext);
        assertTrue(qcContext.getWarningCount() > 0);
        final List<String> warnings = qcContext.getWarnings();
        assertNotNull(warnings);
        assertEquals(warnings.get(0), "The following barcodes have been dropped in the archive 'archiveName': barcode2, barcode3 \t[null_null.null]");
    }

    @Test
    public void testNoWarnings() throws Processor.ProcessorException {
        mockNoDroppedBarcodes();
        mockBCRQueries();
        qcContext.setArchive(archive);
        droppedBarcodeFinder.execute(archive, qcContext);
        assertEquals(0, qcContext.getWarningCount());
    }

    private void mockBCRQueries() {
        context.checking(new Expectations() {{
            one(bcrIdQueries).getArchiveBarcodes(currentArchiveId);
            will(returnValue(currentBarcodeList));
            one(bcrIdQueries).getArchiveBarcodes(previousArchiveId);
            will(returnValue(previousBarcodeList));
        }});
    }

    private void mockDroppedBarcodes() {
        BCRID bcrId1 = new BCRID();
        bcrId1.setFullID("barcode1");
        bcrId1.setViewable(1);
        BCRID bcrId2 = new BCRID();
        bcrId2.setFullID("barcode2");
        bcrId2.setViewable(1);
        BCRID bcrId3 = new BCRID();
        bcrId3.setFullID("barcode3");
        bcrId3.setViewable(1);

        // add a duplicate barcode to the list
        BCRID bcrId3too = new BCRID();
        bcrId3too.setFullID("barcode3");
        bcrId3too.setViewable(1);

        previousBarcodeList.add(bcrId1);
        previousBarcodeList.add(bcrId2);
        previousBarcodeList.add(bcrId3);
        previousBarcodeList.add(bcrId3too);

        BCRID bcrId4 = new BCRID();
        bcrId4.setViewable(1);
        bcrId4.setFullID("barcode1");
        currentBarcodeList.add(bcrId4); // barcode2 and barcode3 are dropped in the current archive
    }
    
    private void mockNoDroppedBarcodes() {
        BCRID bcrId1 = new BCRID();
        bcrId1.setFullID("barcode1");
        bcrId1.setViewable(1);

        // add a duplicate barcode
        BCRID bcrId1too = new BCRID();
        bcrId1too.setFullID("barcode1");
        bcrId1too.setViewable(1);
        previousBarcodeList.add(bcrId1);
        previousBarcodeList.add(bcrId1too);
        BCRID bcrId4 = new BCRID();
        bcrId4.setFullID("barcode1");
        bcrId4.setViewable(1);
        currentBarcodeList.add(bcrId4); // no barcodes dropped in the current archive
    }


}
