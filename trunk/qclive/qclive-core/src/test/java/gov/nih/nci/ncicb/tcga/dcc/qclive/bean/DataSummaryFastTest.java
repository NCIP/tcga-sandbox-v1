/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG(TM)
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.bean;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: kaned
 * Date: Jul 30, 2009
 * Time: 2:25:39 PM
 * Test class for DataSummary.
 */
public class DataSummaryFastTest {

    @Before
    public void setup() {
    }

    @Test
    public void testPlatformAccumulation() throws Exception {
        DataSummary summary = new DataSummary();
        assertEquals("",summary.getPlatform());
        summary.addPlatform("platform1");
        assertEquals("platform1",summary.getPlatform());
        summary.addPlatform("platform2");
        assertEquals("platform1, platform2",summary.getPlatform());
        assertEquals(2,summary.getPlatforms().size());
    }

    public void testPlatformReset() throws Exception {
        DataSummary summary = new DataSummary();
        summary.addPlatform("platform1");
        summary.addPlatform("platform2");
        summary.setPlatform("platform3");
        assertEquals("platform3",summary.getPlatform());
        assertEquals(1,summary.getPlatforms().size());
    }

    public void testLevel1Calculations() throws Exception {
        DataSummary summary = new DataSummary();
        assertEquals(null,summary.getLevel1ResultsReportedByCenter());
        assertEquals(null,summary.getPercentageOflevel1ResultsReportedByCenter());
        summary.setSamplesFromBcrToCenter(50);
        summary.addLevel1ResultsReportedByCenter(25);
        assertEquals(25,(long)summary.getLevel1ResultsReportedByCenter());
        assertEquals(50,(long)summary.getPercentageOflevel1ResultsReportedByCenter());
        summary.addLevel1ResultsReportedByCenter(25);
        assertEquals(50,(long)summary.getLevel1ResultsReportedByCenter());
        assertEquals(100,(long)summary.getPercentageOflevel1ResultsReportedByCenter());
    }

    public void testLevel2Calculations() throws Exception {
        DataSummary summary = new DataSummary();
        assertEquals(null,summary.getLevel2ResultsReportedByCenter());
        assertEquals(null,summary.getPercentageOflevel2ResultsReportedByCenter());
        summary.setSamplesFromBcrToCenter(50);
        summary.addLevel2ResultsReportedByCenter(25);
        assertEquals(25,(long)summary.getLevel2ResultsReportedByCenter());
        assertEquals(50,(long)summary.getPercentageOflevel2ResultsReportedByCenter());
        summary.addLevel1ResultsReportedByCenter(25);
        assertEquals(50,(long)summary.getLevel2ResultsReportedByCenter());
        assertEquals(100,(long)summary.getPercentageOflevel2ResultsReportedByCenter());
    }

    public void testLevel3Calculations() throws Exception {
        DataSummary summary = new DataSummary();
        assertEquals(null,summary.getLevel3ResultsReportedByCenter());
        assertEquals(null,summary.getPercentageOflevel3ResultsReportedByCenter());
        summary.setSamplesFromBcrToCenter(50);
        summary.addLevel3ResultsReportedByCenter(25);
        assertEquals(25,(long)summary.getLevel3ResultsReportedByCenter());
        assertEquals(50,(long)summary.getPercentageOflevel3ResultsReportedByCenter());
        summary.addLevel1ResultsReportedByCenter(25);
        assertEquals(50,(long)summary.getLevel3ResultsReportedByCenter());
        assertEquals(100,(long)summary.getPercentageOflevel3ResultsReportedByCenter());
    }

    public void testSampleReportingCalculations() throws Exception {
        DataSummary summary = new DataSummary();
        assertEquals(null,summary.getLevel3ResultsReportedByCenter());
        assertEquals(null,summary.getSamplesReportedOnByCenter());
        assertEquals(null,summary.getUnaccountedForSampleCount());
        summary.setSamplesFromBcrToCenter(50);
        assertEquals(50,(long)summary.getUnaccountedForSampleCount());
        summary.addSamplesReportedOnByCenter(15);
        assertEquals(35,(long)summary.getUnaccountedForSampleCount());
        summary.addSamplesReportedOnByCenter(15);
        assertEquals(20,(long)summary.getUnaccountedForSampleCount());
        summary.addSamplesReportedOnByCenter(20);
        assertEquals(0,(long)summary.getUnaccountedForSampleCount());
    }


}

