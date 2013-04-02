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

import java.io.File;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;

/**
 * DBUnit test for DAMQueriesStats
 *
 * @author nanans
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class DAMQueriesStatsSlowTest extends DBUnitTestCase {

	private static final String SAMPLES_DIR = 
		Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
	
    public DAMQueriesStatsSlowTest() {
        super(SAMPLES_DIR, "portal/dao/StatsQueries_TestDB.xml", "dccCommon.unittest.properties");
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
    protected void setUpDatabaseConfig(final DatabaseConfig config) {
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new OracleDataTypeFactory());
    }

    public void testGetStats() {
        DAMQueriesStats statsQueries = new DAMQueriesStats();
        statsQueries.setDataSource(getDataSource());
        String[][] stats = statsQueries.getStats();

        String[][] expectedStats = {
                {null, null, "DIS1", "DIS2"},
                {"DataType1", "L1", "10", null },
                {"DataType2", "L1", "5", null},
                {"DataType2", "L2", null, "1"},
                {"DataType3", "L2", null, "6"}
        };

        assertEquals(expectedStats.length, stats.length);
        for (int i=0; i<expectedStats.length; i++) {
            assertEquals(expectedStats[i].length, stats[i].length);
            for (int j=0; j<expectedStats[i].length; j++) {
                assertEquals(expectedStats[i][j], stats[i][j]);
            }
        }
    }
}
