/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.LatestArchive;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Maf;
import gov.nih.nci.ncicb.tcga.dcc.datareports.bean.Sdrf;
import gov.nih.nci.ncicb.tcga.dcc.datareports.dao.jdbc.LatestGenericReportDAOImpl;

import java.util.List;

/**
 * Test class for the latest generic report dao layer
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class LatestGenericReportDAOImplSlowTest extends DatareportDBUnitConfig {

    //the server address that comes from the properties file and Spring takes care of loading the
    // properties file and making those values available to this DAO class.
    // the intention of the test here is to check if the url minus the sever address part comes in properly.
    private String serverAddress = null;
    
    public void testGetLatestSdrfWS() throws Exception {
        LatestGenericReportDAOImpl impl = new LatestGenericReportDAOImpl();
        impl.setDataSource(getDataSource());
        List<Sdrf> allRows = impl.getLatestSdrfWS();
        assertNotNull(allRows);
        assertEquals(2, allRows.size());
        assertNotNull(allRows.get(0).getSdrfUrl());        
        assertEquals(serverAddress + "DOMI-GSC1.sdrf.idf.txt", allRows.get(0).getSdrfUrl());
        assertNotNull(allRows.get(0).getSdrfUrl());
        assertEquals("N/A", allRows.get(1).getSdrfUrl());        
    }

    public void testGetLatestArchiveWS() throws Exception {
        LatestGenericReportDAOImpl impl = new LatestGenericReportDAOImpl();
        impl.setDataSource(getDataSource());
        List<Archive> allRows = impl.getLatestArchiveWS();
        assertNotNull(allRows);
        assertEquals(16, allRows.size());
        assertEquals("broad.mit.edu_GBM.Genome_Wide_SNP_6.1.0.0",allRows.get(0).getRealName());
    }

    public void testGetLatestArchiveWSByType() throws Exception {
        LatestGenericReportDAOImpl impl = new LatestGenericReportDAOImpl();
        impl.setDataSource(getDataSource());
        List<Archive> allRows = impl.getLatestArchiveWSByType("Level_1");
        assertNotNull(allRows);
        assertEquals(5, allRows.size());
        assertEquals("intgen.org_GBM.bio.Level_1.4.24.0",allRows.get(0).getRealName());
    }

    public void testGetLatestMafWS() throws Exception {
        LatestGenericReportDAOImpl impl = new LatestGenericReportDAOImpl();
        impl.setDataSource(getDataSource());
        List<Maf> allRows = impl.getLatestMafWS();
        assertNotNull(allRows);
        assertEquals(3, allRows.size());
        assertEquals("genome.wustl.edu_GBM.ABI.53.3.0",allRows.get(0).getRealName());
        assertNotNull(allRows.get(1).getMafUrl());
        assertEquals("N/A", allRows.get(1).getMafUrl());
        assertNotNull(allRows.get(2).getMafUrl());
        assertEquals(serverAddress + "DOMI-GSC2.idf.maf.txt", allRows.get(2).getMafUrl());
    }

    public void testGetLatestArchive() throws Exception {
        LatestGenericReportDAOImpl impl = new LatestGenericReportDAOImpl();
        impl.setDataSource(getDataSource());
        List<LatestArchive> allRows = impl.getLatestArchive();
        assertNotNull(allRows);
        assertEquals(19, allRows.size());
        assertEquals("broad.mit.edu_GBM.Genome_Wide_SNP_6.1.0.0",allRows.get(0).getArchiveName());
        assertEquals("N/A",allRows.get(1).getMafUrl());
        assertEquals(serverAddress + "DOMI-GSC3.idf.maf.sdrf.txt",allRows.get(1).getSdrfUrl());
    }
    
}//End of class
