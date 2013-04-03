/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.service;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.BiospecimenMetaData;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ViewAndExtensionForExport;
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

import static gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants.UUID_BROWSER_COLS;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Test class for the uuid common service implementation
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class UUIDCommonServiceImplFastTest {

    private Mockery context;
    private UUIDCommonServiceImpl<BiospecimenMetaData> commonService;

    @Before
    public void before() throws Exception {
        context = new JUnit4Mockery();
        commonService = new UUIDCommonServiceImpl<BiospecimenMetaData>();
    }

    @Test
    public void testGetPaginatedList() {
        List<BiospecimenMetaData> pageList = commonService.getPaginatedList(makeMockUUIDBrowserRows(), 0, 2);
        assertNotNull(pageList);
        assertEquals(2, pageList.size());
        assertEquals("OV", pageList.get(1).getDisease());
    }

    @Test
    public void testGetPaginatedListBigNum() {
        List<BiospecimenMetaData> pageList = commonService.getPaginatedList(makeMockUUIDBrowserRows(), 0, 12);
        assertNotNull(pageList);
        assertEquals(3, pageList.size());
        assertEquals("OV", pageList.get(1).getDisease());
    }

    @Test
    public void testGetTotalCount() {
        int count = commonService.getTotalCount(makeMockUUIDBrowserRows());
        assertEquals(3, count);
    }

    @Test
    public void testGetTotalCountNull() {
        int count = commonService.getTotalCount(null);
        assertEquals(0, count);
    }

    @Test
    public void getComparatorMap() {
        Map<String, String> strMap = new LinkedHashMap<String, String>() {{
            put("disease", "Disease");
            put("center", "Center");
        }};
        Map<String, Comparator> map = commonService.getComparatorMap(BiospecimenMetaData.class, strMap);
        assertEquals(2, map.size());
        assertNotNull(map.get("disease"));
        assertNotNull(map.get("center"));
    }

    @Test
    public void testGetSortedListASC() throws Exception {
        Map<String, String> strMap = new LinkedHashMap<String, String>() {{
            put("disease", "Disease");
        }};
        Map<String, Comparator> comp = commonService.getComparatorMap(BiospecimenMetaData.class, strMap);
        List<BiospecimenMetaData> sortedList = commonService.getSortedList(makeMockUUIDBrowserRows(), comp, "disease", "ASC");
        assertNotNull(sortedList);
        assertEquals("mockbarcode1", sortedList.get(0).getBarcode());
    }

    @Test
    public void testGetSortedListDESC() throws Exception {
        Map<String, String> strMap = new LinkedHashMap<String, String>() {{
            put("disease", "Disease");
        }};
        Map<String, Comparator> comp = commonService.getComparatorMap(BiospecimenMetaData.class, strMap);
        List<BiospecimenMetaData> sortedList = commonService.getSortedList(makeMockUUIDBrowserRows(), comp, "disease", "DESC");
        assertNotNull(sortedList);
        assertEquals("OV", sortedList.get(0).getDisease());
    }

    @Test
    public void testGetSortedListValueFirst() throws Exception {
        List<BiospecimenMetaData> sortedList = commonService.getSortedListValueFirst(makeMockUUIDBrowserRows(),
                BiospecimenMetaData.class, "uuid", "2");
        assertNotNull(sortedList);
        assertEquals("2", sortedList.get(0).getUuid());
        assertEquals("1", sortedList.get(1).getUuid());
        assertEquals("3", sortedList.get(2).getUuid());
    }

    @Test
    public void testAdaptJsonFilter() {
        String testJson = "{\"disease\":\"GBM,OV\",\"center\":\"genome.wustl.edu (GSC)\"," +
                "\"platform\":\"Undetermined\",\"analyte\":\"D\"}";
        String res = commonService.adaptJsonFilter("disease", testJson).get(0);
        assertNotNull(res);
        assertEquals("GBM", res);
    }

    @Test
    public void testAdaptJsonFilterEmptyAliquot() {
        String testJson = "{\"disease\":\"\",\"platform\":\"\"}";
        List<String> ls = commonService.adaptJsonFilter("aliquotId", testJson);
        assertNotNull(ls);
        assertEquals(0, ls.size());
    }

    @Test
    public void processJsonMultipleFilter() {
        String jsonFilter = "{\"disease\":\"GBM\",\"center\":\"genome.wustl.edu (GSC)\"," +
                "\"platform\":\"Undetermined\",\"analyte\":\"D\"}";
        String disease = commonService.processJsonMultipleFilter("disease", jsonFilter).get(0);
        assertNotNull(disease);
        assertEquals("GBM", disease);
    }

    @Test
    public void processJsonSingleFilter() {
        String jsonFilter = "{\"disease\":\"GBM\",\"center\":\"genome.wustl.edu (GSC)\"," +
                "\"platform\":\"Undetermined\",\"analyte\":\"D\"}";
        String disease = commonService.processJsonSingleFilter("disease", jsonFilter);
        assertNotNull(disease);
        assertEquals("GBM", disease);
    }

    @Test
    public void buildReportColumns() {
        String columns = "disease,uuid,receivingCenter,barcode";
        Map<String, String> colMap = commonService.buildReportColumns(UUID_BROWSER_COLS, columns);
        assertNotNull(colMap);
        assertEquals(4, colMap.size());
        assertEquals("Barcode", colMap.get("barcode"));
        assertEquals("Disease", colMap.get("disease"));
    }

    @Test
    public void genORPredicateList() {
        List<Predicate> pList = new LinkedList<Predicate>();
        List<String> strList = new LinkedList<String>() {{
            add("hyper");
            add("super");
        }};
        commonService.genORPredicateList(BiospecimenMetaData.class, pList, strList, "disease", false);
        assertEquals(1, pList.size());
    }

    @Test
    public void genListPredicates() {
        List<String> strList = new LinkedList<String>() {{
            add("GBM");
        }};
        List<Predicate> pList = commonService.genListPredicates(BiospecimenMetaData.class, strList, "disease", false);
        List<BiospecimenMetaData> fList = (List<BiospecimenMetaData>) CollectionUtils.select(makeMockUUIDBrowserRows(),
                PredicateUtils.allPredicate(pList));
        assertEquals(1, pList.size());
        assertEquals(1, fList.size());
    }

    @Test
    public void genListPredicatesMultiple() {
        List<String> strList = new LinkedList<String>() {{
            add("OV");
        }};
        List<Predicate> pList = commonService.genListPredicates(BiospecimenMetaData.class, strList, "disease", true);
        List<BiospecimenMetaData> fList = (List<BiospecimenMetaData>) CollectionUtils.select(makeMockUUIDBrowserRows(),
                PredicateUtils.allPredicate(pList));
        assertEquals(1, pList.size());
        assertEquals(2, fList.size());
    }

    @Test
    public void genListPredicatesMultipleNotReally() {
        List<String> strList = new LinkedList<String>() {{
            add("mockbarcode3");
        }};
        List<Predicate> pList = commonService.genListPredicates(BiospecimenMetaData.class, strList, "barcode", true);
        List<BiospecimenMetaData> fList = (List<BiospecimenMetaData>) CollectionUtils.select(makeMockUUIDBrowserRows(),
                PredicateUtils.allPredicate(pList));
        assertEquals(1, pList.size());
        assertEquals(1, fList.size());
        assertEquals("mockbarcode3", fList.get(0).getBarcode());
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

    public List<BiospecimenMetaData> makeMockUUIDBrowserRows() {
        List<BiospecimenMetaData> list = new LinkedList<BiospecimenMetaData>();
        list.add(new BiospecimenMetaData() {{
            setBarcode("mockbarcode1");
            setUuid("1");
            setDisease("GBM");
            setPlatform("mockplatform1");
            setReceivingCenter("mockcenter1");
        }});
        list.add(new BiospecimenMetaData() {{
            setBarcode("mockbarcode2");
            setUuid("2");
            setDisease("OV");
            setPlatform("mockplatform2");
            setReceivingCenter("mockcenter2");
        }});
        list.add(new BiospecimenMetaData() {{
            setBarcode("mockbarcode3");
            setUuid("3");
            setDisease("GBM,OV,COAD");
            setPlatform("mockplatform3");
            setReceivingCenter("mockcenter3");
        }});
        return list;
    }


} //End of Class
