/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.TissueSourceSite;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;

import java.io.File;
import java.util.List;

/**
 * DBUnit test for SampleTypeQueries.
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class TissueSourceSiteQueriesJDBCImplSlowTest extends DBUnitTestCase {
    private static final String PROPERTIES_FILE = "unittest.properties";
    private static final String TEST_DATA_FOLDER = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "dao/TissueSourceSiteQueries_TestData.xml";
    private TissueSourceSiteQueriesJDBCImpl tissueSourceSiteQueries;

    public TissueSourceSiteQueriesJDBCImplSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
        tissueSourceSiteQueries = new TissueSourceSiteQueriesJDBCImpl();
        tissueSourceSiteQueries.setDataSource(getDataSource());
    }

    public void testGetTissueSourceSites() {
        List<TissueSourceSite> tissueSourceSites = tissueSourceSiteQueries.getAllTissueSourceSites();
        assertEquals(6, tissueSourceSites.size());
        assertEquals("07", tissueSourceSites.get(0).getTissueSourceSiteId());
        assertEquals("AV", tissueSourceSites.get(1).getTissueSourceSiteId());
        assertEquals("01", tissueSourceSites.get(2).getTissueSourceSiteId());
        assertEquals("04", tissueSourceSites.get(3).getTissueSourceSiteId());
        assertEquals("03", tissueSourceSites.get(4).getTissueSourceSiteId());
        assertEquals("02", tissueSourceSites.get(5).getTissueSourceSiteId());
    }

    public void testGetAggregateTissueSourceSites() {
        List<TissueSourceSite> tissueSourceSites = tissueSourceSiteQueries.getAggregateTissueSourceSites();
        assertEquals(5, tissueSourceSites.size());
        assertEquals("07", tissueSourceSites.get(0).getTissueSourceSiteId());
        assertEquals("AV", tissueSourceSites.get(1).getTissueSourceSiteId());
        assertEquals("01,04", tissueSourceSites.get(2).getTissueSourceSiteId());
        assertEquals("03", tissueSourceSites.get(3).getTissueSourceSiteId());
        assertEquals("02", tissueSourceSites.get(4).getTissueSourceSiteId());
    }


    public void testGetDiseasesForTissueSourceSiteCode(){
        List<String> diseases = tissueSourceSiteQueries.getDiseasesForTissueSourceSiteCode("01");
        assertEquals(2, diseases.size());
        assertEquals("GBM", diseases.get(0));
        assertEquals("OV", diseases.get(1));
    }

    public void testGetControlTssCodes() {
        final List<String> controlTssCodes = tissueSourceSiteQueries.getControlTssCodes();
        assertEquals(2, controlTssCodes.size());
        assertEquals("07", controlTssCodes.get(0));
        assertEquals("AV", controlTssCodes.get(1));
    }
}
