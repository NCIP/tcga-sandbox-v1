/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.service;

import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.fusioncharts.BubbleXYZ;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.fusioncharts.Category;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.fusioncharts.Chart;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.fusioncharts.ChartDataRow;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.fusioncharts.Dataset;
import gov.nih.nci.ncicb.tcga.dcc.datareports.constants.StatsDashboardConstants;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.StatsDashboardDAO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test class for the stats dashboard service layer
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class StatsDashboardServiceFastTest {

    private final Mockery context = new JUnit4Mockery();
    private StatsDashboardDAO daoImpl;
    private StatsDashboardService service;

    @Before
    public void before() throws Exception {

        daoImpl = context.mock(StatsDashboardDAO.class);
        service = new StatsDashboardServiceImpl();

        Field daoImplServiceField = service.getClass().getDeclaredField("daoImpl");
        daoImplServiceField.setAccessible(true);
        daoImplServiceField.set(service, daoImpl);
    }

    @Test
    public void testGetChartForDrillDownArchiveCreated() throws Exception {
        Chart c = service.getChartForDrillDownArchiveDownloaded("GBM", "Number");
        assertNotNull(c);
        assertEquals("GBM Number Archives Downloaded", c.getCaption());
    }

    @Test
    public void testGetChartForTotalArchiveCreated() throws Exception {
        context.checking(new Expectations() {{
            allowing(daoImpl).getAbsoluteTotalNumberArchiveDownloaded();
            will(returnValue(123l));
            allowing(daoImpl).getAbsoluteTotalSizeArchiveDownloaded();
            will(returnValue(123456l));
        }});
        Chart c = service.getChartForTotalArchiveDownloaded();
        assertNotNull(c);
        assertEquals("Total Archives Downloaded", c.getCaption());
    }

    @Test
    public void testGetDatasetListForArchivesTrue() throws Exception {
        List<Dataset> l = service.getDatasetListForArchives(true, false, "fripouille", null, mockNumberTotalArchives(), mockSizeTotalArchives());
        assertNotNull(l);
        assertEquals("valuouille", l.get(0).getData().get(0).getValue());
        assertEquals("j-ddArchiveChart-labelouille,fripouille", l.get(0).getData().get(0).getLink());
    }

    @Test
    public void testGetDatasetListForArchivesFalse() throws Exception {
        List<Dataset> l = service.getDatasetListForArchives(false, true, null, "Number", mockNumberTotalArchives(), mockSizeTotalArchives());
        assertNotNull(l);
        assertEquals("valuouille", l.get(0).getData().get(0).getValue());
        assertEquals(null, l.get(0).getData().get(0).getLink());
    }

    @Test
    public void testGetCategoryListForArchives() throws Exception {
        List<Category> l = service.getCategoryListForArchives(mockNumberTotalArchives());
        assertNotNull(l);
        assertEquals("labelouille", l.get(0).getCategory().get(0).getLabel());
    }

    @Test
    public void testGetNumberDrillDownArchives() throws Exception {
        context.checking(new Expectations() {{
            allowing(daoImpl).getNumberArchivesDownloadedDrillDown("GBM");
            will(returnValue(mockNumberTotalArchives()));
        }});
        List<ChartDataRow> l = service.getNumberArchivesDownloadedDrillDown("GBM");
        assertNotNull(l);
        assertEquals("labelouille", l.get(0).getLabel());
        assertEquals("valuouille", l.get(0).getValue());
    }

    @Test
    public void testGetSizeDrillDownArchives() throws Exception {
        context.checking(new Expectations() {{
            allowing(daoImpl).getSizeArchivesDownloadedDrillDown("GBM");
            will(returnValue(mockSizeTotalArchives()));
        }});
        List<ChartDataRow> l = service.getSizeArchivesDownloadedDrillDown("GBM");
        assertNotNull(l);
        assertEquals("labelasse", l.get(0).getLabel());
        assertEquals("valuasse", l.get(0).getValue());
    }

    @Test
    public void testGetNumberTotalArchives() throws Exception {
        context.checking(new Expectations() {{
            allowing(daoImpl).getNumberArchivesDownloadedTotal();
            will(returnValue(mockNumberTotalArchives()));
        }});
        List<ChartDataRow> l = service.getNumberArchivesDownloadedTotal();
        assertNotNull(l);
        assertEquals("labelouille", l.get(0).getLabel());
        assertEquals("valuouille", l.get(0).getValue());
    }

    @Test
    public void testGetSizeTotalArchives() throws Exception {
        context.checking(new Expectations() {{
            allowing(daoImpl).getSizeArchivesDownloadedTotal();
            will(returnValue(mockSizeTotalArchives()));
        }});
        List<ChartDataRow> l = service.getSizeArchivesDownloadedTotal();
        assertNotNull(l);
        assertEquals("labelasse", l.get(0).getLabel());
        assertEquals("valuasse", l.get(0).getValue());
    }

    @Test
    public void testGetCategoryForBubblePlatform() throws Exception {
        context.checking(new Expectations() {{
            allowing(daoImpl).getFilterPieChart(StatsDashboardConstants.FILTER_PIE_CHART.get("platformTypeFilter"));
            will(returnValue(mockPlatforms()));
        }});
        List<Category> list = service.getCategoryForBubblePlatformType();
        assertNotNull(list);
        assertEquals("Platform1", list.get(0).getCategory().get(0).getLabel());
        assertEquals("2", list.get(0).getCategory().get(1).getX());
        assertEquals("Platform3", list.get(0).getCategory().get(2).getLabel());
    }

    @Test
    public void testGetBubbleChartBatch() throws Exception {
        context.checking(new Expectations() {{
            allowing(daoImpl).getFilterBatch();
            will(returnValue(mockBatches()));
        }});
        List<BubbleXYZ> list = service.getBubbleChartBatch();
        assertNotNull(list);
        assertEquals("Batch 1", list.get(1).getName());
        assertEquals("2", list.get(1).getX());
        assertEquals("j-ddPieChart-batchFilter,Batch 1", list.get(1).getLink());
        assertEquals("Batch 1, 250", list.get(1).getTooltext());
    }

    @Test
    public void testGetBubbleChartPlatform() throws Exception {
        context.checking(new Expectations() {{
            allowing(daoImpl).getFilterPieChart(StatsDashboardConstants.FILTER_PIE_CHART.get("platformTypeFilter"));
            will(returnValue(mockPlatforms()));
        }});
        List<BubbleXYZ> list = service.getBubbleChartPlatformType();
        assertNotNull(list);
        assertEquals("Platform3", list.get(2).getName());
        assertEquals("3", list.get(2).getX());
        assertEquals("j-ddPieChart-platformTypeFilter,Platform1", list.get(0).getLink());
        assertEquals("Platform1, 250", list.get(0).getTooltext());
    }

    private List<ChartDataRow> mockNumberTotalArchives() {
        List<ChartDataRow> dataRow = new LinkedList<ChartDataRow>();
        dataRow.add(new ChartDataRow("labelouille", "valuouille", "linkouille"));
        return dataRow;
    }

    private List<ChartDataRow> mockSizeTotalArchives() {
        List<ChartDataRow> dataRow = new LinkedList<ChartDataRow>();
        dataRow.add(new ChartDataRow("labelasse", "valuasse", "linkasse"));
        return dataRow;
    }

    private List<ChartDataRow> mockBatches() {
        List<ChartDataRow> dataRow = new LinkedList<ChartDataRow>();
        dataRow.add(new ChartDataRow("Unclassified", "3", "unclassifiedLink"));
        dataRow.add(new ChartDataRow("Batch 1", "250", "batch1Link"));
        dataRow.add(new ChartDataRow("Batch 2", "50", "batch2Link"));
        return dataRow;
    }

    private List<ChartDataRow> mockPlatforms() {
        List<ChartDataRow> dataRow = new LinkedList<ChartDataRow>();
        dataRow.add(new ChartDataRow("Platform1", "250", "platform1Link"));
        dataRow.add(new ChartDataRow("Platform2", "50", "platform2Link"));
        dataRow.add(new ChartDataRow("Platform3", "3", "platform3Link"));
        return dataRow;
    }

}//End of Class
