/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.jdbc.ArchiveQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;

import java.io.File;
import java.io.IOException;

import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Robert S. Sfeir
 * @version $Rev$
 *          <p/>
 *          Class to test the existence of an archive in the db.
 */
public class ArchiveExistenceValidatorSlowTest extends DBUnitTestCase {
	private static final String PROPERTIES_FILE = "common.unittest.properties";
	private static final String TEST_DATA_FOLDER = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	private static final String TEST_DATA_FILE = "/qclive/dao/archiveExistance_testData.xml";
	private ArchiveExistenceValidator validator;
	private QcContext context;

	public ArchiveExistenceValidatorSlowTest() throws IOException {
		super(TEST_DATA_FOLDER, TEST_DATA_FILE, PROPERTIES_FILE);
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		final Archive fakeArchiveWithNameField = new Archive();
		fakeArchiveWithNameField
				.setRealName("test.org_TUM.aPlatform.aux.1.0.0");
		validator = new ArchiveExistenceValidator();
		ArchiveQueriesJDBCImpl archiveQueries = new ArchiveQueriesJDBCImpl();
		archiveQueries.setDataSource(getDataSource());
		validator.setArchiveQueries(archiveQueries);
		context = new QcContext();
	}

	@Test
	public void testGetName() throws Exception {
		assertEquals(validator.getName(),
				"checking for existence of an Available archive with same version in db");
	}

	@Test
	public void testAvailableArchive() throws Processor.ProcessorException {
		assertFalse(validator.execute(new Archive("available_latest.tar.gz"),
				context));
		assertTrue(context.getErrorCount() > 0);
	}

	// even if an archive is not the latest, still should fail validation if it
	// is available

	@Test
	public void testAvailableNotLatestArchive()
			throws Processor.ProcessorException {
		assertFalse(validator.execute(
				new Archive("available_not_latest.tar.gz"), context));
		assertTrue(context.getErrorCount() > 0);
	}

	@Test
	public void testNotAvailableArchive() throws Processor.ProcessorException {
		assertTrue(validator.execute(new Archive("uploaded.tar.gz"), context));
		assertTrue(validator.execute(new Archive("in_review.tar.gz"), context));
		assertTrue(validator.execute(new Archive("invalid.tar.gz"), context));
		assertEquals(0, context.getErrorCount());
	}

	@Test
	public void testNewArchive() throws Processor.ProcessorException {
		assertTrue(validator.execute(new Archive("does_not_exist.tar.gz"),
				context));
		assertEquals(0, context.getErrorCount());
	}

	/**
	 * Tests the case where you try to validate an archive object without a file
	 * (just make sure it doesn't throw a runtime exception)
	 */
	@Test
	public void testEmptyArchive() {
		try {
			validator.execute(new Archive(), context);
			fail("exception wasn't thrown");
		} catch (Processor.ProcessorException e) {
			// good
		}
	}

	@Override
	protected DatabaseOperation getSetUpOperation() throws Exception {
		return DatabaseOperation.CLEAN_INSERT;
	}

	@Override
	protected DatabaseOperation getTearDownOperation() throws Exception {
		return DatabaseOperation.DELETE_ALL;
	}
}