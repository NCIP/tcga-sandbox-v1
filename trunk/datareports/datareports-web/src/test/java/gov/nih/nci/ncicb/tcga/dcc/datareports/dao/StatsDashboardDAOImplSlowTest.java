/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.dao;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.fusioncharts.ChartDataRow;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.jdbc.StatsDashboardDAOImpl;

import java.util.List;

/**
 * Test class for the dao layer of the stat dashboard
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class StatsDashboardDAOImplSlowTest extends DatareportDBUnitConfig {

    StatsDashboardDAOImpl impl = new StatsDashboardDAOImpl();

    public void testGetNumberArchivesDownloadedTotal() throws Exception {
        impl.setDataSource(getDataSource());
        List<ChartDataRow> res = impl.getNumberArchivesDownloadedTotal();
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals("135",res.get(0).getValue());
        assertEquals("GBM",res.get(0).getLabel());
    }

    public void testGetSizeArchivesDownloadedTotal() throws Exception {
        impl.setDataSource(getDataSource());
        List<ChartDataRow> res = impl.getSizeArchivesDownloadedTotal();
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals("1111111110",res.get(0).getValue());
        assertEquals("GBM",res.get(0).getLabel());
    }

    public void testGetNumberArchivesSubmittedTotal() throws Exception {
        impl.setDataSource(getDataSource());
        List<ChartDataRow> res = impl.getNumberArchivesReceivedTotal();
        assertNotNull(res);
        assertEquals(2, res.size());
    }

    public void testGetSizeArchivesSubmittedTotal() throws Exception {
        impl.setDataSource(getDataSource());
        List<ChartDataRow> res = impl.getSizeArchivesReceivedTotal();
        assertNotNull(res);
        assertEquals(2, res.size());
        assertEquals("GBM",res.get(0).getLabel());
    }

    public void testGetNumberArchivesDownloadedDrillDown() throws Exception {
        impl.setDataSource(getDataSource());
        List<ChartDataRow> res = impl.getNumberArchivesDownloadedDrillDown("GBM");
        assertNotNull(res);
        assertEquals(2, res.size());
        assertEquals("12",res.get(0).getValue());
        assertEquals("MAR-2010",res.get(0).getLabel());
    }

    public void testGetSizeArchivesDownloadedDrillDown() throws Exception {
        impl.setDataSource(getDataSource());
        List<ChartDataRow> res = impl.getSizeArchivesDownloadedDrillDown("GBM");
        assertNotNull(res);
        assertEquals(2, res.size());
        assertEquals("123456789",res.get(0).getValue());
        assertEquals("MAR-2010",res.get(0).getLabel());
    }

    public void testGetNumberArchivesSubmittedDrillDown() throws Exception {
        impl.setDataSource(getDataSource());
        List<ChartDataRow> res = impl.getNumberArchivesReceivedDrillDown("GBM");
        assertNotNull(res);
        assertEquals(6, res.size());
        assertEquals("1",res.get(0).getValue());
        assertEquals("AUG/2007",res.get(0).getLabel());
    }

    public void testGetSizeArchivesSubmittedDrillDown() throws Exception {
        impl.setDataSource(getDataSource());
        List<ChartDataRow> res = impl.getSizeArchivesReceivedDrillDown("GBM");
        assertNotNull(res);
        assertEquals(6, res.size());
        assertEquals("AUG/2007",res.get(0).getLabel());
    }

    public void testGetCumulativeNumberArchivesDownloadedDrillDown() throws Exception {
        impl.setDataSource(getDataSource());
        List<ChartDataRow> res = impl.getCumulativeNumberArchivesDownloadedDrillDown("GBM");
        assertNotNull(res);
        assertEquals(2, res.size());
        assertEquals("MAR-2010", res.get(0).getLabel());
    }

    public void testGetCumulativeSizeArchivesDownloadedDrillDown() throws Exception {
        impl.setDataSource(getDataSource());
        List<ChartDataRow> res = impl.getCumulativeSizeArchivesDownloadedDrillDown("GBM");
        assertNotNull(res);
        assertEquals(2, res.size());
        assertEquals("MAR-2010", res.get(0).getLabel());
    }

    public void testGetCumulativeNumberArchivesReceivedDrillDown() throws Exception {
        impl.setDataSource(getDataSource());
        List<ChartDataRow> res = impl.getCumulativeNumberArchivesReceivedDrillDown("GBM");
        assertNotNull(res);
        assertEquals(6, res.size());
        assertEquals("AUG/2007", res.get(0).getLabel());
    }

    public void testGetCumulativeSizeArchivesReceivedDrillDown() throws Exception {
        impl.setDataSource(getDataSource());
        List<ChartDataRow> res = impl.getCumulativeSizeArchivesReceivedDrillDown("GBM");
        assertNotNull(res);
        assertEquals(6, res.size());
        assertEquals("AUG/2007", res.get(0).getLabel());
    }

    public void testGetFilterPieChart() throws Exception {
        impl.setDataSource(getDataSource());
        List<ChartDataRow> res = impl.getFilterPieChart("16");
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals("Level 2", res.get(0).getLabel());
    }

    public void testGetFilterPieChartDrillDown() throws Exception {
        impl.setDataSource(getDataSource());
        List<ChartDataRow> res = impl.getFilterPieChartDrillDown("16","Level 2");
        assertNotNull(res);
        assertEquals(1, res.size());
        assertEquals("GBM", res.get(0).getLabel());
    }

    public void testGetAbsoluteTotalNumberArchiveDownloaded() throws Exception {
        impl.setDataSource(getDataSource());
        long res = impl.getAbsoluteTotalNumberArchiveDownloaded();
        assertNotNull(res);
        assertEquals(135, res);
    }

    public void testGetAbsoluteTotalSizeArchiveDownloaded() throws Exception {
        impl.setDataSource(getDataSource());
        long res = impl.getAbsoluteTotalSizeArchiveDownloaded();
        assertNotNull(res);
        assertEquals(1111111110, res);
    }

    public void testGetAbsoluteTotalNumberArchiveReceived() throws Exception {
        impl.setDataSource(getDataSource());
        long res = impl.getAbsoluteTotalNumberArchiveReceived();
        assertNotNull(res);
        assertEquals(16, res);
    }

    public void testGetAbsoluteTotalSizeArchiveReceived() throws Exception {
        impl.setDataSource(getDataSource());
        long res = impl.getAbsoluteTotalSizeArchiveReceived();
        assertNotNull(res);
        assertEquals(0, res);
    }

}//End of Class
