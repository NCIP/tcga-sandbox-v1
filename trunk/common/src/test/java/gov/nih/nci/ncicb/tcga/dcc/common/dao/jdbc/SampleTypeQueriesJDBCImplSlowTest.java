/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.SampleType;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;

import java.io.File;
import java.util.List;

/**
 * DBUnit test for SampleTypeQueries.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class SampleTypeQueriesJDBCImplSlowTest extends DBUnitTestCase {
    private static final String PROPERTIES_FILE = "unittest.properties";
    private static final String TEST_DATA_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "dao/SampleTypeQueries_TestData.xml";

    public SampleTypeQueriesJDBCImplSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    public void testGetSampleTypes() {
        SampleTypeQueriesJDBCImpl sampleTypeQueries = new SampleTypeQueriesJDBCImpl();
        sampleTypeQueries.setDataSource(getDataSource());
        List<SampleType> sampleTypes = sampleTypeQueries.getAllSampleTypes();
        assertEquals(4, sampleTypes.size());
        assertEquals("01", sampleTypes.get(0).getSampleTypeCode());
        assertEquals("tumor", sampleTypes.get(0).getDefinition());
        assertEquals("TP", sampleTypes.get(0).getShortLetterCode());
        assertTrue(sampleTypes.get(0).getIsTumor());

        assertEquals("02", sampleTypes.get(1).getSampleTypeCode());
        assertEquals("recurring tumor", sampleTypes.get(1).getDefinition());
        assertEquals("TR", sampleTypes.get(1).getShortLetterCode());
        assertTrue(sampleTypes.get(1).getIsTumor());

        assertEquals("10", sampleTypes.get(2).getSampleTypeCode());
        assertEquals("normal blood", sampleTypes.get(2).getDefinition());
        assertEquals("NB", sampleTypes.get(2).getShortLetterCode());
        assertFalse(sampleTypes.get(2).getIsTumor());

        assertEquals("11", sampleTypes.get(3).getSampleTypeCode());
        assertEquals("normal tissue", sampleTypes.get(3).getDefinition());
        assertEquals("NT", sampleTypes.get(3).getShortLetterCode());
        assertFalse(sampleTypes.get(3).getIsTumor());
    }
}
