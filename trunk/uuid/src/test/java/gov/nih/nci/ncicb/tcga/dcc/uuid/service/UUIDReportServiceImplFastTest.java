/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.uuid.service;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.util.UUIDConstants;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * Test for UUIDReportServiceImpl
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */
public class UUIDReportServiceImplFastTest {

    private UUIDReportServiceImpl reportService;
    private List<UUIDDetail> uuidList;

    @Before
    public void before() throws Exception {
        reportService = new UUIDReportServiceImpl();
        Center center = new Center();
        center.setCenterId(1);
        center.setCenterName("");

        uuidList = new ArrayList<UUIDDetail>();
        uuidList.add(new UUIDDetail("mammoth", new Date(), UUIDConstants.GenerationMethod.Web, center, "master_user"));
        uuidList.add(new UUIDDetail("tiger", new Date(), UUIDConstants.GenerationMethod.Web, center, "master_user"));
        uuidList.add(new UUIDDetail("sloth", new Date(), UUIDConstants.GenerationMethod.Web, center, "master_user"));
    }


    @Test
    public void testGetPaginatedList() {
        List<UUIDDetail> pageList = reportService.getPaginatedList(uuidList, 0, 2);
        assertNotNull(pageList);
        assertEquals(2, pageList.size());
        assertEquals("mammoth", pageList.get(0).getUuid());
    }
    
    @Test
    public void testGetTotalCount() {
        int count = reportService.getTotalCount(uuidList);
        assertEquals(3, count);
    }

    @Test
    public void testGetTotalCountNull() {
        int count = reportService.getTotalCount(null);
        assertEquals(0, count);
    }

    @Test
    public void testGetUUIDDetailComparatorMap(){
        Map<String, Comparator> comparatorMap = reportService.getUUIDDetailComparatorMap();
        assertNotNull(comparatorMap);
    }

    @Test
    public void testSortListOnUUID(){
        reportService.sortList(uuidList, UUIDConstants.COLUMN_UUID, UUIDConstants.ASC);
        assertEquals("mammoth", uuidList.get(0).getUuid());
        reportService.sortList(uuidList, UUIDConstants.COLUMN_UUID, UUIDConstants.DESC);
        assertEquals("tiger", uuidList.get(0).getUuid());        
    }

    @Test
    public void testSortListOnCenter(){
        mockListForDifferentCenters();
        reportService.sortList(uuidList, UUIDConstants.COLUMN_CENTER_NAME, UUIDConstants.ASC);
        assertEquals("mammoth", uuidList.get(0).getUuid());
        reportService.sortList(uuidList, UUIDConstants.COLUMN_CENTER_NAME, UUIDConstants.DESC);
        assertEquals("tiger", uuidList.get(0).getUuid());
    }

    @Test
    public void testSortListOnCreationDates(){
        mockListForDifferentCreationDates();
        reportService.sortList(uuidList, UUIDConstants.COLUMN_CREATION_DATE, UUIDConstants.ASC);
        assertEquals("mammoth", uuidList.get(0).getUuid());
        reportService.sortList(uuidList, UUIDConstants.COLUMN_CREATION_DATE, UUIDConstants.DESC);
        assertEquals("tiger", uuidList.get(0).getUuid());
    }

    @Test
    public void testSortListOnGenerationMethod(){
        mockListForDifferentGenerationMethods();
        reportService.sortList(uuidList, UUIDConstants.COLUMN_GENERATION_METHOD, UUIDConstants.ASC);
        assertEquals("tiger", uuidList.get(0).getUuid());
        reportService.sortList(uuidList, UUIDConstants.COLUMN_GENERATION_METHOD, UUIDConstants.DESC);
        assertEquals("mammoth", uuidList.get(0).getUuid());
    }

    @Test
    public void testSortNullList(){
        mockListForDifferentGenerationMethods();
        reportService.sortList(null, UUIDConstants.COLUMN_UUID, UUIDConstants.ASC);
        // should not fail
    }    

    @Test
    public void testSortListOnBarcode(){
        mockListForSeveralFields();
        reportService.sortList(uuidList, UUIDConstants.COLUMN_LATEST_BARCODE, UUIDConstants.ASC);
        assertEquals("TCGA-41-4097-01A-01T-1232-07", uuidList.get(0).getLatestBarcode());
        reportService.sortList(uuidList, UUIDConstants.COLUMN_LATEST_BARCODE, UUIDConstants.DESC);
        assertEquals("TCGA-BH-A1EW-01A-01-TSA", uuidList.get(0).getLatestBarcode());
    }

    @Test
    public void testSortListOnDisease(){
        mockListForSeveralFields();
        reportService.sortList(uuidList, UUIDConstants.COLUMN_DISEASE, UUIDConstants.ASC);
        assertEquals("BRCA", uuidList.get(0).getDiseaseAbbrev());
        reportService.sortList(uuidList, UUIDConstants.COLUMN_DISEASE, UUIDConstants.DESC);
        assertEquals("GBM", uuidList.get(0).getDiseaseAbbrev());
    }

    @Test
    public void testSortListOnCreatedBy() {
        mockListForSeveralFields();
        reportService.sortList(uuidList, UUIDConstants.COLUMN_CREATED_BY, UUIDConstants.ASC);
        assertEquals("master_user", uuidList.get(0).getCreatedBy());
        reportService.sortList(uuidList, UUIDConstants.COLUMN_CREATED_BY, UUIDConstants.DESC);
        assertEquals("simple_user", uuidList.get(0).getCreatedBy());

    }

    private void mockListForDifferentGenerationMethods() {
        Center center = new Center();
        center.setCenterId(1);
        center.setCenterName("center_abc");
        uuidList = new ArrayList<UUIDDetail>();
        uuidList.add(new UUIDDetail("mammoth", new Date(), UUIDConstants.GenerationMethod.Web, center, "master_user"));
        uuidList.add(new UUIDDetail("tiger", new Date(), UUIDConstants.GenerationMethod.Upload, center, "master_user"));
    }

    private void mockListForDifferentCenters() {
        Center center1 = new Center();
        center1.setCenterId(1);
        center1.setCenterName("center_abc");
        center1.setCenterDisplayName("center_abc");
        Center center2 = new Center();
        center2.setCenterId(1);
        center2.setCenterName("center_def");
        center2.setCenterDisplayName("center_def");
        uuidList = new ArrayList<UUIDDetail>();
        uuidList.add(new UUIDDetail("mammoth", new Date(), UUIDConstants.GenerationMethod.Web, center1, "master_user"));
        uuidList.add(new UUIDDetail("tiger", new Date(), UUIDConstants.GenerationMethod.Web, center2, "master_user"));
    }

    private void mockListForDifferentCreationDates() {
        Center center = new Center();
        center.setCenterId(1);
        center.setCenterName("center_abc");
        //next day
        uuidList = new ArrayList<UUIDDetail>();
        uuidList.add(new UUIDDetail("mammoth", new Date(987654321), UUIDConstants.GenerationMethod.Web, center, "master_user"));
        uuidList.add(new UUIDDetail("tiger", new Date(12345), UUIDConstants.GenerationMethod.Web, center, "master_user"));
    }    

    private void mockListForSeveralFields() {
        uuidList = new ArrayList<UUIDDetail>();
        String barcode1 = "TCGA-BH-A1EW-01A-01-TSA" ;
        String barcode2 = "TCGA-41-4097-01A-01T-1232-07";
        Center center = new Center();
        center.setCenterId(1);
        center.setCenterName("center_abc");
        UUIDDetail uuidDetail1 = new UUIDDetail("6503e9d5-9d7b-4b4f", new Date(), UUIDConstants.GenerationMethod.Web, center, "master_user");
        UUIDDetail uuidDetail2 = new UUIDDetail("cd0a4591-9e50-412e", new Date(), UUIDConstants.GenerationMethod.Web, center, "simple_user");
        uuidDetail1.setLatestBarcode(barcode1);
        uuidDetail2.setLatestBarcode(barcode2);
        uuidDetail1.setDiseaseAbbrev("BRCA");
        uuidDetail2.setDiseaseAbbrev("GBM");
        uuidList.add(uuidDetail1);
        uuidList.add(uuidDetail2);

    }
}
