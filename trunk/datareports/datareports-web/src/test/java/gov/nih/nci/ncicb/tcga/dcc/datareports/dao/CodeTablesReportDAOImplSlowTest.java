/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.SampleType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.BcrBatchCode;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.CenterCode;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.CodeReport;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.DataType;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.PlatformCode;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Tissue;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.TissueSourceSite;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.jdbc.CodeTablesReportDAOImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Test class for the code table report dao layer
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class CodeTablesReportDAOImplSlowTest extends DatareportDBUnitConfig {

    public void testGetTissueSourceSite() throws Exception {
        CodeTablesReportDAOImpl impl = new CodeTablesReportDAOImpl();
        impl.setDataSource(getDataSource());
        List<TissueSourceSite> allRows = impl.getTissueSourceSite();
        assertNotNull(allRows);
        assertEquals(3, allRows.size());
        assertEquals("Glioblastoma multiforme (GBM)", allRows.get(0).getStudyName());
        assertEquals("Cell Line Control", allRows.get(1).getStudyName());
        assertEquals("07", allRows.get(1).getCode());
        assertEquals("AA", allRows.get(2).getCode());
    }

    public void testGetCenterCode() throws Exception {
        CodeTablesReportDAOImpl impl = new CodeTablesReportDAOImpl();
        impl.setDataSource(getDataSource());
        List<CenterCode> allRows = impl.getCenterCode();
        assertNotNull(allRows);
        assertEquals(10, allRows.size());
        assertEquals("BI", allRows.get(0).getShortName());
        assertEquals("lbl.gov", allRows.get(2).getCenterName());
        assertEquals("05", allRows.get(4).getCode());
    }

    public void testGetDataLevel() throws Exception {
        CodeTablesReportDAOImpl impl = new CodeTablesReportDAOImpl();
        impl.setDataSource(getDataSource());
        List<CodeReport> allRows = impl.getDataLevel();
        assertNotNull(allRows);
        assertEquals(4, allRows.size());
        String[] testDataDefinitions = {"Raw data", "Normalized data", "Aggregated data", "Regions of Interest data"};
        List<String> dataDefinitions = new ArrayList();
        for (final CodeReport codeReport : allRows) {
            dataDefinitions.add(codeReport.getDefinition());
        }
        assertTrue(dataDefinitions.containsAll(Arrays.asList(testDataDefinitions)));
    }

    public void testGetDataType() throws Exception {
        CodeTablesReportDAOImpl impl = new CodeTablesReportDAOImpl();
        impl.setDataSource(getDataSource());
        List<DataType> allRows = impl.getDataType();
        assertNotNull(allRows);
        assertEquals(14, allRows.size());
        assertEquals("Expression-Genes", allRows.get(0).getDisplayName());
    }

    public void testGetDisease() throws Exception {
        CodeTablesReportDAOImpl impl = new CodeTablesReportDAOImpl();
        impl.setDataSource(getDataSource());
        List<Tumor> allRows = impl.getTumor();
        assertNotNull(allRows);
        assertEquals(8, allRows.size());
        String[] testDiseaseNames = {"GBM", "LUAD", "BRDC", "BRLC", "OV", "KIRC", "LUSC", "KIRP"};
        List<String> diseaseNames = new ArrayList();
        for (final Tumor disease : allRows) {
            diseaseNames.add(disease.getTumorName());
        }
        assertTrue(diseaseNames.containsAll(Arrays.asList(testDiseaseNames)));
    }

    public void testGetPlatformCode() throws Exception {
        CodeTablesReportDAOImpl impl = new CodeTablesReportDAOImpl();
        impl.setDataSource(getDataSource());
        List<PlatformCode> allRows = impl.getPlatformCode();
        assertNotNull(allRows);
        assertEquals(35, allRows.size());
        assertEquals("HG-U133_Plus_2", allRows.get(1).getPlatformAlias());
        assertEquals("IlluminaGG", allRows.get(21).getPlatformName());
    }

    public void testGetPortionAnalyte() throws Exception {
        CodeTablesReportDAOImpl impl = new CodeTablesReportDAOImpl();
        impl.setDataSource(getDataSource());
        List<CodeReport> allRows = impl.getPortionAnalyte();
        assertNotNull(allRows);
        assertEquals(5, allRows.size());
        assertEquals("DNA", allRows.get(0).getDefinition());
    }

    public void testGetSampleType() throws Exception {
        CodeTablesReportDAOImpl impl = new CodeTablesReportDAOImpl();
        impl.setDataSource(getDataSource());
        List<SampleType> allRows = impl.getSampleType();
        assertNotNull(allRows);
        assertEquals(1, allRows.size());
        assertEquals("solid tumor", allRows.get(0).getDefinition());
        assertEquals("01", allRows.get(0).getSampleTypeCode());
        assertEquals("TP", allRows.get(0).getShortLetterCode());
    }

    public void testGetTissue() throws Exception {
        CodeTablesReportDAOImpl impl = new CodeTablesReportDAOImpl();
        impl.setDataSource(getDataSource());
        List<Tissue> allRows = impl.getTissue();
        assertNotNull(allRows);
        assertEquals(1, allRows.size());
        assertEquals("brain", allRows.get(0).getTissue());
    }

    public void testGetBcrBatchCode() throws Exception {
        CodeTablesReportDAOImpl impl = new CodeTablesReportDAOImpl();
        impl.setDataSource(getDataSource());
        List<BcrBatchCode> allRows = impl.getBcrBatchCode();
        assertNotNull(allRows);
        assertEquals(8, allRows.size());
        assertEquals("1", allRows.get(0).getBcrBatch());
        assertEquals("BCR", allRows.get(5).getBcr());
    }
}//End of class
