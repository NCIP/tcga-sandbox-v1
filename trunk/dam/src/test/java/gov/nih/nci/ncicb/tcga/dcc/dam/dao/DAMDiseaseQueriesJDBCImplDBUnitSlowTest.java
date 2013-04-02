/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.common.util.DiseaseNameLister;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.Disease;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.junit.Test;

/**
 * DBUnit test for DAMDiseaseQueries JDBC impl
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class DAMDiseaseQueriesJDBCImplDBUnitSlowTest extends DBUnitTestCase {
    private static final String SAMPLES_FOLDER = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "portal/dao/DamDiseaseQueries_data.xml";
    private static final String PROPERTIES_FILE = "tcga_unittest.properties";

    private DAMDiseaseQueriesJDBCImpl damDiseaseQueries;

    public DAMDiseaseQueriesJDBCImplDBUnitSlowTest() {
        super(SAMPLES_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    public void setUp() throws Exception {
        super.setUp();

        damDiseaseQueries = new DAMDiseaseQueriesJDBCImpl();
        damDiseaseQueries.setControlDiseaseAbbreviation("CNTL");

        damDiseaseQueries.setDiseaseNameLister(new DiseaseNameLister() {
            public Set<Object> getDiseaseNames() {
                // not real schemas...
            	final Set<Object> diseaseNames = new HashSet<Object>();
            	diseaseNames.add("DIS2");
            	diseaseNames.add("DIS1");
            	diseaseNames.add("DIS3");
                diseaseNames.add("CNTL");
                return diseaseNames;
            }
        });
        damDiseaseQueries.setDataSource(getDataSource());
    }

    @Override
    protected void setUpDatabaseConfig(DatabaseConfig config) {
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new OracleDataTypeFactory());
    }

    @Test
    public void testGetDiseases() {
        List<Disease> diseases = damDiseaseQueries.getDiseases();

        assertEquals(4, diseases.size());
        assertEquals("DIS1", diseases.get(0).getAbbreviation());
        assertEquals("DIS2", diseases.get(1).getAbbreviation());
        assertEquals("DIS3", diseases.get(2).getAbbreviation());
        assertEquals("CNTL", diseases.get(3).getAbbreviation());
    }

    @Test
    public void testGetActiveDiseasesNoControlAbbrevSet() {
        damDiseaseQueries.setControlDiseaseAbbreviation(null);

        List<Disease> diseases = damDiseaseQueries.getActiveDiseases();
        // only gets diseases with archives now
        assertEquals(2, diseases.size());

        // here, CNTL shows up as a regular disease because we do not have controlDiseaseAbbrev set
        assertEquals("CNTL", diseases.get(0).getAbbreviation());
        assertEquals("DIS1", diseases.get(1).getAbbreviation());
    }

    @Test
    public void testGetActiveDiseasesWithControlAbbrev() {
        List<Disease> diseases = damDiseaseQueries.getActiveDiseases();

        assertEquals(2, diseases.size());
        assertEquals("DIS1", diseases.get(0).getAbbreviation());
        assertEquals("CNTL", diseases.get(1).getAbbreviation());
    }

    @Test
    public void testGetDisease() {
        Disease dis = damDiseaseQueries.getDisease("DIS1");
        assertEquals("Disease 1", dis.getName());
    }

    @Test
    public void testGetDiseaseNotFound() {
        Disease dis = damDiseaseQueries.getDisease("hello");
        assertNull(dis);
    }

}
