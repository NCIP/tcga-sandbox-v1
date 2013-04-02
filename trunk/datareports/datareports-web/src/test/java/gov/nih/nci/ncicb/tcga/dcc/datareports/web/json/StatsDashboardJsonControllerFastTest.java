/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.web.json;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.fusioncharts.Category;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.fusioncharts.Chart;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.fusioncharts.ChartDataRow;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.fusioncharts.Dataset;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.fusioncharts.Label;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.fusioncharts.Value;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.StatsDashboardService;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ui.ModelMap;

/**
 * Test class for the stats dashboard json controller
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class StatsDashboardJsonControllerFastTest {

    private final Mockery context = new JUnit4Mockery();
    private StatsDashboardService service;
    private StatsDashboardJsonController controller;

    @Before
    public void before() throws Exception {

        service = context.mock(StatsDashboardService.class);
        controller = new StatsDashboardJsonController();

        Field serviceControllerField = controller.getClass().getDeclaredField("service");
        serviceControllerField.setAccessible(true);
        serviceControllerField.set(controller, service);
    }

    @Test
    public void testTotalArchiveCreatedData() throws Exception {
        context.checking(new Expectations() {{
            allowing(service).getChartForTotalArchiveDownloaded();
            will(returnValue(new Chart("blah blah blah","a","b","c","d","e")));
            allowing(service).getNumberArchivesDownloadedTotal();
            will(returnValue(mockNumberTotalArchives()));
            allowing(service).getSizeArchivesDownloadedTotal();
            will(returnValue(mockSizeTotalArchives()));
            allowing(service).getCategoryListForArchives(with(any(List.class)));
            will(returnValue(mockCategoryListForArchives()));
            allowing(service).getDatasetListForArchives(with(any(Boolean.class)),with(any(Boolean.class)),
                    with(any(String.class)),with(any(String.class)),with(any(List.class)),with(any(List.class)));
            will(returnValue(mockDatasetListForArchives()));
        }});
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.totalArchiveDownloadedData(model);
        assertTrue(resMap != null);
        Chart ch = (Chart)resMap.get("chart");
        List<Category> cList = (List<Category>)resMap.get("categories");
        List<Dataset> dList = (List<Dataset>)resMap.get("dataset");
        assertTrue(ch != null);
        assertTrue(cList != null);
        assertTrue(dList != null);
        assertEquals("blah blah blah",ch.getCaption());
        assertEquals("a",ch.getSubcaption());
        assertEquals("labelouille",cList.get(0).getCategory().get(0).getLabel());
        assertEquals("valuouille",dList.get(0).getData().get(0).getValue());
        assertEquals("linkouille",dList.get(0).getData().get(0).getLink());
    }

    @Test
    public void testDDArchiveCreatedData() throws Exception {
        context.checking(new Expectations() {{
            allowing(service).getChartForDrillDownArchiveDownloaded("GBM","Number");
            will(returnValue(new Chart("qwerty","z","x","y","t","w")));
            allowing(service).getNumberArchivesDownloadedDrillDown("GBM");
            will(returnValue(mockNumberTotalArchives()));
            allowing(service).getCumulativeNumberArchivesDownloadedDrillDown("GBM");
            will(returnValue(mockNumberTotalArchives()));
            allowing(service).getSizeArchivesDownloadedDrillDown("GBM");
            will(returnValue(mockSizeTotalArchives()));
            allowing(service).getCategoryListForArchives(with(any(List.class)));
            will(returnValue(mockCategoryListForArchives()));
            allowing(service).getDatasetListForArchives(with(any(Boolean.class)),with(any(Boolean.class)),
                    with(any(String.class)),with(any(String.class)),with(any(List.class)),with(any(List.class)));
            will(returnValue(mockDatasetListForArchives()));
        }});
        ModelMap model = new ModelMap();
        final ModelMap resMap = controller.ddNumberArchiveDownloadedData(model,"GBM");
        assertTrue(resMap != null);
        Chart ch = (Chart)resMap.get("chart");
        List<Category> cList = (List<Category>)resMap.get("categories");
        List<Dataset> dList = (List<Dataset>)resMap.get("dataset");
        assertTrue(ch != null);
        assertTrue(cList != null);
        assertTrue(dList != null);
        assertEquals("qwerty",ch.getCaption());
        assertEquals("z",ch.getSubcaption());
        assertEquals("labelouille",cList.get(0).getCategory().get(0).getLabel());
        assertEquals("valuouille",dList.get(0).getData().get(0).getValue());
        assertEquals("linkouille",dList.get(0).getData().get(0).getLink());
    }


    private List<ChartDataRow>mockNumberTotalArchives(){
        List<ChartDataRow> dataRow = new LinkedList<ChartDataRow>();
        dataRow.add(new ChartDataRow("labelouille","valuouille","linkouille"));
        return dataRow;
    }

    private List<ChartDataRow>mockSizeTotalArchives(){
        List<ChartDataRow> dataRow = new LinkedList<ChartDataRow>();
         dataRow.add(new ChartDataRow("labelasse","valuasse","linkasse"));
        return dataRow;
    }

    private List<Dataset> mockDatasetListForArchives(){
        List<Dataset> datasetList = new LinkedList<Dataset>();
        datasetList.add(new Dataset("test"){{
            setData(new LinkedList<Value>(){{
                add(new Value("valuouille","linkouille"));
            }});
        }});
        return datasetList;
    }

    private List<Category> mockCategoryListForArchives(){
        List<Category> categories = new LinkedList<Category>();
        categories.add(new Category(){{
            setCategory(new LinkedList<Label>(){{
                add(new Label("labelouille"));
            }});
        }});
        return categories;
    }

}//End of Class
