/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Visibility;

import java.io.File;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.ext.oracle.OracleDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;

/**
 * Test class for AccessQueriesJDBCImpl
 * 
 * @author Namrata Rane
 * @version $Rev$
 */

public class VisibilityQueriesJDBCImplSlowTest extends DBUnitTestCase {

	private static final String PROPERTIES_FILE = "common.unittest.properties";
	private static final String TEST_DATA_FOLDER = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	private static final String TEST_DATA_FILE = "qclive/dao/VisibilityQueries_TestData.xml";

	private VisibilityQueriesJDBCImpl visibilityQueries;

	public VisibilityQueriesJDBCImplSlowTest() {
		super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		visibilityQueries = new VisibilityQueriesJDBCImpl();
		visibilityQueries.setDataSource(getDataSource());
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

	public void testGetAccessLevelForArchive() {
		Archive archive = new Archive("test.tar.gz");
		archive.setDataType("Complete Clinical Set");
		archive.setDataLevel(1);
		Visibility visibility = visibilityQueries
				.getVisibilityForArchive(archive);
		assertNotNull(visibility);
		assertTrue(visibility.isIdentifiable());
	}

	public void testNullAccessLevelForArchive() {
		Archive archive = new Archive("test.tar.gz");
		archive.setDataType("bad");
		Visibility access = visibilityQueries.getVisibilityForArchive(archive);
		assertNull(access);
	}

	public void testGetVisibilityForPlatformRNASeq() {
		String platformName = "RNASeq";
		String dataTypeName = "Quantification-Exon";
		String centerTypeCode = "CGCC";
		Integer levelNumber = 3;
		final Visibility access = visibilityQueries.getVisibilityForPlatform(
				platformName, dataTypeName, centerTypeCode, levelNumber);
		assertFalse(access.isIdentifiable());
	}

	public void testGetVisibilityForPlatformHumanHap550() {
		String platformName = "HumanHap550";
		String dataTypeName = "SNP Frequencies";
		String centerTypeCode = "CGCC";
		Integer levelNumber = 0;
		final Visibility access = visibilityQueries.getVisibilityForPlatform(
				platformName, dataTypeName, centerTypeCode, levelNumber);
		assertTrue(access.isIdentifiable());
	}

	public void testGetVisibilityForPlatformAgilentG4502A_07_1() {
		String platformName = "AgilentG4502A_07_1";
		String dataTypeName = "Expression-Gene";
		String centerTypeCode = "CGCC";
		Integer levelNumber = 2;
		final Visibility access = visibilityQueries.getVisibilityForPlatform(
				platformName, dataTypeName, centerTypeCode, levelNumber);
		assertFalse(access.isIdentifiable());
	}

	public void testGetVisibilityForPlatformBogus() {
		String platformName = "Bogus";
		String dataTypeName = "Bogus";
		String centerTypeCode = "Bogus";
		Integer levelNumber = 0;
		final Visibility access = visibilityQueries.getVisibilityForPlatform(
				platformName, dataTypeName, centerTypeCode, levelNumber);
		assertNull(access);
	}

	public void testGetLeastVisibilityForDataTypeSNP() {
		// SNP has level 1+2 protected, 3 public, so least visibility is
		// protected
		final Visibility visibility = visibilityQueries
				.getLeastVisibilityForDataType("SNP");
		assertTrue(visibility.isIdentifiable());
	}

	public void testGetLeastVisibilityForDataTypeGeneExp() {
		// all are public
		final Visibility visibility = visibilityQueries
				.getLeastVisibilityForDataType("Expression-Gene");
		assertFalse(visibility.isIdentifiable());
	}

	public void testGetLeastVisibilityForDataTypeUnknown() {
		// not a known data type
		final Visibility visibility = visibilityQueries
				.getLeastVisibilityForDataType("pumpkin");
		assertNull(visibility);
	}

}
