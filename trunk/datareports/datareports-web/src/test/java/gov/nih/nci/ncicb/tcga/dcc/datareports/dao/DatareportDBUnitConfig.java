/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.dao;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.QUERY_REFRESH_SAMPLE_SUMMARY_TABLE;
import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.SampleSummaryReportConstants.SAMPLE_SUMMARY_DB_FILE;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import java.io.File;

/**
 * Test class to extends for all dao tests in datareports to avoid code duplication in every drep dao tests
 *
 * @author Dominique Berton Last updated by: $Author$
 * @version $Rev$
 */
public class DatareportDBUnitConfig extends DBUnitTestCase {

    /**
     * output directory *
     */
    public static final String DATA_SUMMARY_DB_DUMP_FOLDER =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;


    private final static String tcgaTestPropertiesFile = "tcgadata.properties";

    public DatareportDBUnitConfig() {
        super(DATA_SUMMARY_DB_DUMP_FOLDER, SAMPLE_SUMMARY_DB_FILE, tcgaTestPropertiesFile);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        SimpleJdbcDaoSupport dao = new SimpleJdbcDaoSupport();
        dao.setDataSource(getDataSource());
        dao.getJdbcTemplate().execute(QUERY_REFRESH_SAMPLE_SUMMARY_TABLE);
    }

    @Override
    protected DatabaseOperation getSetUpOperation() throws Exception {
        return DatabaseOperation.CLEAN_INSERT;
    }

    @Override
    protected DatabaseOperation getTearDownOperation() {
        return DatabaseOperation.DELETE_ALL;
    }

    @Override
    protected void setUpDatabaseConfig(DatabaseConfig config) {
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new OracleDataTypeFactory());
    }

    public void testDatabaseConfig() throws Exception {
        Object o = this.getConnection().getConfig().getProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY);
        assertTrue(o instanceof OracleDataTypeFactory);
    }
}//End of Class
