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
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataSet;

import java.io.File;
import java.util.List;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.oracle.Oracle10DataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;

/**
 * DBUnit test for DAMQueriesLeveTwoTreeList
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class DAMQueriesLevelTwoThreeListDBUnitSlowTest extends DBUnitTestCase {
    private static final String PROPERTIES_FILE = "tcga_unittest.properties";
    private static final String TEST_DATA_FOLDER = 
    	Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_DATA_FILE = "/portal/dao/Level_23List_TestDB.xml";

    private DAMQueriesCGCCLevelTwoThreeList queries;

    public DAMQueriesLevelTwoThreeListDBUnitSlowTest() {
        super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
    }

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.CLEAN_INSERT;
    }

    @Override
    protected DatabaseOperation getTearDownOperation() throws Exception {
        return DatabaseOperation.DELETE_ALL;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        queries = new DAMQueriesCGCCLevelTwoThreeList();
        queries.setDataSource(getDataSource());
    }

    @Override
    protected void setUpDatabaseConfig(final DatabaseConfig config) {
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new Oracle10DataTypeFactory());
    }


    @Test
    public void testGetInitialList() throws DataAccessMatrixQueries.DAMQueriesException {
        List<DataSet> dataSets = queries.buildInitialList("TUM", 2, false);
        assertNotNull(dataSets);
        assertEquals(4, dataSets.size()); // should have one data set per sample, ignore any archives not Available or In Review
        for (final DataSet dataSet : dataSets) {
            assertEquals(11, dataSet.getArchiveId());
            if (dataSet.getSample().equals("TEST-00-0004-00")) {
                assertEquals(2, dataSet.getBarcodes().size()); // two barcodes for this sample
            } else {
                assertEquals(1, dataSet.getBarcodes().size()); // only 1 for other samples
            }
        }
    }

    @Test
    public void testGetInitialListNoControls() throws DataAccessMatrixQueries.DAMQueriesException {
        List<DataSet> dataSets = queries.buildInitialList("BLC", 2, false);
        assertNotNull(dataSets);
        assertEquals(1, dataSets.size());
        final DataSet dataSet = dataSets.get(0);
        assertEquals("TEST-00-0005-H", dataSets.get(0).getSample());
    }

    @Test
    public void testGetInitialListControls() throws DataAccessMatrixQueries.DAMQueriesException {
        List<DataSet> dataSets = queries.buildInitialList("BLC", 2, true);
        assertEquals(1, dataSets.size());
        assertEquals("TEST-00-0006-L", dataSets.get(0).getSample());
        assertEquals("g", dataSets.get(0).getBarcodes().get(0));
    }
}
