/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;

import java.io.File;
import java.util.List;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;

/**
 * Slow test for TissueSourceSiteQueries JDBC implementation.
 * 
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class TissueSourceSiteQueriesImplSlowTest extends DBUnitTestCase {

	private static final String PROPERTIES_FILE = "common.unittest.properties";
	private static final String TEST_DATA_FOLDER = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	private static final String TEST_DATA_FILE = "qclive/dao/TissueSourceSite_TestData.xml";

	private TissueSourceSiteQueriesImpl tissueSourceSiteQueries;

	public TissueSourceSiteQueriesImplSlowTest() {
		super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		tissueSourceSiteQueries = new TissueSourceSiteQueriesImpl();
		tissueSourceSiteQueries.setDataSource(getDataSource());
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
	protected void setUpDatabaseConfig(DatabaseConfig config) {
		config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
				new OracleDataTypeFactory());
	}

	@Test
	public void testGetDiseasesForTissueSourceSiteCode() {
		List<String> diseaseList = tissueSourceSiteQueries
				.getDiseasesForTissueSourceSiteCode("02");
		assertNotNull(diseaseList);
		assertEquals("GBM", diseaseList.get(0));
	}

	@Test
	public void testBadSiteCode() {
		assertEquals(0, tissueSourceSiteQueries
				.getDiseasesForTissueSourceSiteCode("bad").size());
	}

}
