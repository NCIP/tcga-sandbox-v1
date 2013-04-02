/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.service;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.ViewAndExtensionForExport;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Aliquot;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.AliquotReportConstants.ALIQUOT_COLS;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Test class for the datareports service
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */

@RunWith (JMock.class)
public class DatareportsServiceFastTest {

    private Mockery context;

    private DatareportsServiceImpl<Aliquot> commonService;

    @Before
    public void before() throws Exception {
        context = new JUnit4Mockery();
        commonService = new DatareportsServiceImpl<Aliquot>();
    }

    @Test
    public void testGetPaginatedList() {
        List<Aliquot> pageList = commonService.getPaginatedList(makeMockAliquotRows(), 0, 2);
        assertNotNull(pageList);
        assertEquals(2, pageList.size());
        assertEquals("OV", pageList.get(1).getDisease());
    }

    @Test
    public void testGetPaginatedListBigNum() {
        List<Aliquot> pageList = commonService.getPaginatedList(makeMockAliquotRows(), 0, 12);
        assertNotNull(pageList);
        assertEquals(3, pageList.size());
        assertEquals("OV", pageList.get(1).getDisease());
    }

    @Test
    public void testGetTotalCount() {
        int count = commonService.getTotalCount(makeMockAliquotRows());
        assertEquals(3, count);
    }

    @Test
    public void testGetTotalCountNull() {
        int count = commonService.getTotalCount(null);
        assertEquals(0, count);
    }

     @Test
    public void getComparatorMap() {
        Map<String,String> strMap = new LinkedHashMap<String,String>() {{
            put("disease","Disease");
            put("center","Center");
        }};
        Map<String, Comparator> map = commonService.getComparatorMap(Aliquot.class, strMap);
        assertEquals(2, map.size());
        assertNotNull(map.get("disease"));
        assertNotNull(map.get("center"));
    }

    @Test
    public void testGetSortedListASC() throws Exception {
        Map<String,String> strMap = new LinkedHashMap<String,String>() {{put("disease","Disease");}};
        Map<String, Comparator> comp = commonService.getComparatorMap(Aliquot.class, strMap);
        List<Aliquot> sortedList = commonService.getSortedList(makeMockAliquotRows(),comp,"disease","ASC");
        assertNotNull(sortedList);
        assertEquals("mockaliquot1", sortedList.get(0).getAliquotId());
    }

    @Test
    public void testGetSortedListDESC() throws Exception {
        Map<String,String> strMap = new LinkedHashMap<String,String>() {{put("disease","Disease");}};
        Map<String, Comparator> comp = commonService.getComparatorMap(Aliquot.class, strMap);
        List<Aliquot> sortedList = commonService.getSortedList(makeMockAliquotRows(),comp,"disease","DESC");
        assertNotNull(sortedList);
        assertEquals("mockaliquot2", sortedList.get(0).getAliquotId());
    }

    @Test
    public void testAdaptJsonFilter() {
        String testJson = "{\"disease\":\"GBM,OV\",\"center\":\"genome.wustl.edu (GSC)\"," +
                "\"platform\":\"Undetermined\",\"analyte\":\"D\",\"levelFourSubmitted\":\"Y\"}";
        String res = commonService.adaptJsonFilter("disease", testJson).get(0);
        assertNotNull(res);
        assertEquals("GBM", res);
    }

    @Test
    public void testAdaptJsonFilterEmptyAliquot() {
        String testJson = "{\"disease\":\"\",\"levelOne\":\"\"," +
            "\"aliquotId\":\"\",\"center\":\"\",\"levelTwo\":\"\",\"bcrBatch\":\"\"," +
            "\"platform\":\"\",\"levelThree\":\"\"}";
        List<String> ls = commonService.adaptJsonFilter("aliquotId", testJson);
        assertNotNull(ls);
        assertEquals(0,ls.size());
    }

    @Test
    public void testAdaptJsonFilterEmptyBcrBatch() {
        String testJson = "{\"disease\":\"\",\"levelOne\":\"\"," +
            "\"aliquotId\":\"\",\"center\":\"\",\"levelTwo\":\"\",\"bcrBatch\":\"\"," +
            "\"platform\":\"\",\"levelThree\":\"\"}";
        List<String> ls = commonService.adaptJsonFilter("bcrBatch", testJson);
        assertNotNull(ls);
        assertEquals(0,ls.size());
    }

    @Test
    public void processJsonMultipleFilter() {
        String jsonFilter = "{\"disease\":\"GBM\",\"center\":\"genome.wustl.edu (GSC)\"," +
                "\"platform\":\"Undetermined\",\"analyte\":\"D\",\"levelFourSubmitted\":\"Y\"}";
        String disease = commonService.processJsonMultipleFilter("disease", jsonFilter).get(0);
        assertNotNull(disease);
        assertEquals("GBM", disease);
    }

    @Test
    public void processJsonSingleFilter() {
        String jsonFilter = "{\"disease\":\"GBM\",\"center\":\"genome.wustl.edu (GSC)\"," +
                "\"platform\":\"Undetermined\",\"analyte\":\"D\",\"levelFourSubmitted\":\"Y\"}";
        String disease = commonService.processJsonSingleFilter("disease", jsonFilter);
        assertNotNull(disease);
        assertEquals("GBM", disease);
    }

    @Test
    public void buildReportColumns() {
        String columns = "levelThree,levelTwo,receivingCenter,aliquotId";
        Map<String, String> colMap = commonService.buildReportColumns(ALIQUOT_COLS, columns);
        assertNotNull(colMap);
        assertEquals(4, colMap.size());
        assertEquals("Aliquot ID", colMap.get("aliquotId"));
        assertEquals("Level 2 Data", colMap.get("levelTwo"));
    }

    @Test
    public void genORPredicateList() {
        List<Predicate> pList = new LinkedList<Predicate>();
        List<String> strList = new LinkedList<String>() {{
            add("hyper");
            add("super");
        }};
        commonService.genORPredicateList(Aliquot.class, pList, strList, "disease");
        assertEquals(1, pList.size());
    }

    @Test
    public void genListPredicates() {
        List<String> strList = new LinkedList<String>() {{add("GBM");}};
        List<Predicate> pList = commonService.genListPredicates(Aliquot.class, strList, "disease");
        List<Aliquot> fList = (List<Aliquot>) CollectionUtils.select(makeMockAliquotRows(),
                PredicateUtils.allPredicate(pList));
        assertEquals(1, pList.size());
        assertEquals(2, fList.size());
    }

    @Test
    public void getViewAndExtForExport4XL() {
        ViewAndExtensionForExport vae = commonService.getViewAndExtForExport("xl");
        assertNotNull(vae);
        assertEquals("xl", vae.getView());
        assertEquals(".xlsx", vae.getExtension());
    }

    @Test
    public void getViewAndExtForExport4CSV() {
        ViewAndExtensionForExport vae = commonService.getViewAndExtForExport("csv");
        assertNotNull(vae);
        assertEquals("txt", vae.getView());
        assertEquals(".csv", vae.getExtension());
    }

    @Test
    public void getViewAndExtForExport4TAB() {
        ViewAndExtensionForExport vae = commonService.getViewAndExtForExport("tab");
        assertNotNull(vae);
        assertEquals("txt", vae.getView());
        assertEquals(".txt", vae.getExtension());
    }

    public List<Aliquot> makeMockAliquotRows() {
        List<Aliquot> list = new LinkedList<Aliquot>();
        list.add(new Aliquot() {{
            setAliquotId("mockaliquot1");
            setBcrBatch("1");
            setDisease("GBM");
            setLevelOne("mockSubmitted");
            setLevelTwo("mockSubmitted");
            setLevelThree("mockSubmitted");
            setPlatform("mockplatform1");
            setCenter("mockcenter1");
        }});
        list.add(new Aliquot() {{
            setAliquotId("mockaliquot2");
            setBcrBatch("12");
            setDisease("OV");
            setLevelOne("mockMissing");
            setLevelTwo("mockMissing");
            setLevelThree("mockMissing");
            setPlatform("mockplatform2");
            setCenter("mockcenter2");
        }});
        list.add(new Aliquot() {{
            setAliquotId("mockaliquot3");
            setBcrBatch("23");
            setDisease("GBM");
            setLevelOne("mockSubmitted");
            setLevelTwo("mockSubmitted");
            setLevelThree("mockMissing");
            setPlatform("mockplatform3");
            setCenter("mockcenter3");
        }});
        return list;
    }


} //End of Class
