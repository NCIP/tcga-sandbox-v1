/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.stats.TumorMainCount;
import gov.nih.nci.ncicb.tcga.dcc.dam.dao.TumorMainCountQueries;

import java.io.File;
import java.util.List;

import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

/**
 * TumorMainCountQueriesImpl unit tests
 *
 * @author julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class TumorMainCountQueriesImplSlowTest extends DBUnitTestCase {

    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    private static final String PATH_TO_DB_PROPERTIES = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

    private static final String DB_PROPERTIES_FILE = "dccCommon.unittest.properties";

    private static final String DATA_FILE = "portal" + FILE_SEPARATOR + "dao" + FILE_SEPARATOR + "TumorMainData.xml";

    private TumorMainCountQueriesImpl tumorMainCountQueries;

    public TumorMainCountQueriesImplSlowTest() {
        super(PATH_TO_DB_PROPERTIES, DATA_FILE, DB_PROPERTIES_FILE);
    }

    @Before
    public void setUp() throws Exception {

        super.setUp();

        tumorMainCountQueries = new TumorMainCountQueriesImpl();
        tumorMainCountQueries.setDataSource(getDataSource());
    }

    @Override
    public DatabaseOperation getTearDownOperation() {
        return DatabaseOperation.DELETE_ALL;
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testGetAllTumorMainCount() {

        try {
            final List<TumorMainCount> allTumorMainCount = tumorMainCountQueries.getAllTumorMainCount();

            assertNotNull(allTumorMainCount);
            assertEquals("Unexpected result size: ", 9, allTumorMainCount.size());

            //Is it sorted?
            testTumorMainCount(allTumorMainCount.get(0), "LAML", "Acute Myeloid Leukemia", 202, 149, "08/04/10");
            testTumorMainCount(allTumorMainCount.get(1), "BRCA", "Breast invasive carcinoma", 94, 202, "10/11/10");
            testTumorMainCount(allTumorMainCount.get(2), "COAD", "Colon adenocarcinoma", 154, 149, "09/15/10");
            testTumorMainCount(allTumorMainCount.get(3), "GBM", "Glioblastoma multiforme", 458, 202, "10/19/10");
            testTumorMainCount(allTumorMainCount.get(4), "KIRC", "Kidney renal clear cell carcinoma", 31, 202, "09/21/10");
            testTumorMainCount(allTumorMainCount.get(5), "KIRP", "Kidney renal papillary cell carcinoma", 47, 2, "09/15/10");
            testTumorMainCount(allTumorMainCount.get(6), "LUAD", "Lung adenocarcinoma", 68, 79, "09/15/10");
            testTumorMainCount(allTumorMainCount.get(7), "LUSC", "Lung squamous cell carcinoma", 66, 202, "09/06/10");
            testTumorMainCount(allTumorMainCount.get(8), "READ", "Rectum adenocarcinoma", 50, 202, "09/13/10");

        } catch (TumorMainCountQueries.TumorMainCountQueriesException e) {
            fail("Unexpected TumorMainCountQueriesException: " + e.getMessage());
        }
    }

    /**
     * Check if the actual TumorMainCount has the expected values
     *
     * @param actualTumorMainCount the actual TumorMainCount
     * @param expectedDiseaseAbbreviation the expected DiseaseAbbreviation
     * @param expectedDiseaseName the expected DiseaseName
     * @param expectedPatientSamples the expected PatientSamples
     * @param expectedDownloadableTumorSamples the expected DownloadableTumorSamples
     * @param expectedLastUpdate the expected LastUpdate
     */
    private void testTumorMainCount(final TumorMainCount actualTumorMainCount,
                                    final String expectedDiseaseAbbreviation,
                                    final String expectedDiseaseName,
                                    final int expectedPatientSamples,
                                    final int expectedDownloadableTumorSamples,
                                    final String expectedLastUpdate) {

        assertEquals("Unexpected Tumor Abbreviation: ", actualTumorMainCount.getTumorAbbreviation(), expectedDiseaseAbbreviation);
        assertEquals("Unexpected Tumor Name: ", actualTumorMainCount.getTumorName(), expectedDiseaseName);
        assertEquals("Unexpected Patient Samples: ", actualTumorMainCount.getCasesShipped(), expectedPatientSamples);
        assertEquals("Unexpected Downloadable TumorSamples: ", actualTumorMainCount.getCasesWithData(), expectedDownloadableTumorSamples);
        assertEquals("Unexpected Last Update: ", actualTumorMainCount.getLastUpdate(), expectedLastUpdate);
    }
}
