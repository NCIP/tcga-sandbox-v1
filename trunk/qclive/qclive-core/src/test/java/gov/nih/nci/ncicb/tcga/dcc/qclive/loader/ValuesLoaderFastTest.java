/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.loader;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.LoaderQueriesException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.util.RowCounter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Fast tests for ValuesLoader. Uses mock object for DAO.
 * 
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class ValuesLoaderFastTest {
	private static final String SAMPLE_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	private Mockery context = new JUnit4Mockery();
	private LoaderQueries mockLoaderQueries = context.mock(LoaderQueries.class);
	private ValuesLoader valuesLoader;
	private RowCounter rowCounter;

	private List<Object[]> expectedBatchArgs;

	private static final int PLATFORM_ID = 1;
	private static final int DATASET_ID = 1;
	private static final long BARCODE1_ID = 5L;
	private static final long BARCODE2_ID = 6L;
	private static final long BARCODE3_ID = 7L;
	private static final long GROUP_COL1_ID = 10L;
	private static final long GROUP_COL2_ID = 11L;

	@Before
	public void setup() {
		expectedBatchArgs = new ArrayList<Object[]>();
		valuesLoader = new ValuesLoader() {
			// assumes probe names are "probeX" where X is the number that will
			// return as ID
			protected Integer lookupProbeId(final int platformId,
					final String probeName) {
				return Integer.valueOf(probeName.substring(5));
			}

			protected List<Object[]> makeBatchArgumentsList() {
				return expectedBatchArgs;
			}
		};
		valuesLoader.loaderQueries = mockLoaderQueries;
		valuesLoader.datasetId = DATASET_ID;
		valuesLoader.experimentId = 1;
		valuesLoader.platformId = PLATFORM_ID;
		rowCounter = new RowCounter();
		valuesLoader.rowCounter = rowCounter;

		context.checking(new Expectations() {
			{
				allowing(mockLoaderQueries).hybRefDatasetExists(
						with(any(Long.class)), with(any(Long.class)));
				will(returnValue(false));
			}
		});
	}

	private void initTest(final String filename) throws LoaderException {
		File testFile = new File(SAMPLE_DIR + "autoloader/valuesLoader/"
				+ filename);
		valuesLoader.datafile = new DataFile(testFile, "test", 2);
		Map<String, Long> groupMap = new HashMap<String, Long>();
		groupMap.put("col1", GROUP_COL1_ID);
		valuesLoader.hybGroupIds = groupMap;
		Map<String, Long> hybrefs = new HashMap<String, Long>();
		hybrefs.put("barcode1", BARCODE1_ID);
		valuesLoader.hybrefIdsByName = hybrefs;
	}

	@Test
	public void testOneColumnNoConstants() throws LoaderException,
			LoaderQueriesException {
		initTest("one_column_no_constants.txt");

		context.checking(new Expectations() {
			{
				one(mockLoaderQueries).insertHybRefDataset(BARCODE1_ID,
						DATASET_ID, "barcode1");
				one(mockLoaderQueries).insertHybridizationValues(
						expectedBatchArgs);
			}
		});

		valuesLoader.insertValues();
		assertEquals(1, rowCounter.getRowCount("hybrid_ref_data_set"));
		assertEquals(4, rowCounter.getRowCount("hybridization_value"));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE1_ID, GROUP_COL1_ID, 1,
				"1.0" }, expectedBatchArgs.get(0));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE1_ID, GROUP_COL1_ID, 2,
				"2.0" }, expectedBatchArgs.get(1));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE1_ID, GROUP_COL1_ID, 3,
				"3.0" }, expectedBatchArgs.get(2));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE1_ID, GROUP_COL1_ID, 4,
				"4.0" }, expectedBatchArgs.get(3));

		context.assertIsSatisfied();
	}

	@Test
	public void testOneColumnWithConstants() throws LoaderException,
			LoaderQueriesException {
		initTest("one_column_with_constants.txt");

		context.checking(new Expectations() {
			{
				one(mockLoaderQueries).insertHybRefDataset(BARCODE1_ID,
						DATASET_ID, "barcode1");
				one(mockLoaderQueries).insertHybridizationValues(
						expectedBatchArgs);
			}
		});

		valuesLoader.insertValues();
		assertEquals(1, rowCounter.getRowCount("hybrid_ref_data_set"));
		assertEquals(3, rowCounter.getRowCount("hybridization_value"));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE1_ID, GROUP_COL1_ID, 1,
				"1.0" }, expectedBatchArgs.get(0));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE1_ID, GROUP_COL1_ID, 2,
				"2.0" }, expectedBatchArgs.get(1));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE1_ID, GROUP_COL1_ID, 3,
				"3.0" }, expectedBatchArgs.get(2));
		context.assertIsSatisfied();
	}

	@Test
	public void testMultipleColumnsNoConstants() throws LoaderException,
			LoaderQueriesException {
		initTest("multiple_columns_no_constants.txt");
		valuesLoader.hybrefIdsByName.put("barcode2", BARCODE2_ID);
		valuesLoader.hybrefIdsByName.put("barcode3", BARCODE3_ID);

		context.checking(new Expectations() {
			{
				one(mockLoaderQueries).insertHybRefDataset(BARCODE1_ID,
						DATASET_ID, "barcode1");
				one(mockLoaderQueries).insertHybRefDataset(BARCODE2_ID,
						DATASET_ID, "barcode2");
				one(mockLoaderQueries).insertHybRefDataset(BARCODE3_ID,
						DATASET_ID, "barcode3");
				one(mockLoaderQueries).insertHybridizationValues(
						expectedBatchArgs);
			}
		});
		valuesLoader.insertValues();
		context.assertIsSatisfied();
		assertEquals(new Object[] { PLATFORM_ID, BARCODE1_ID, GROUP_COL1_ID, 1,
				"1.1" }, expectedBatchArgs.get(0));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE2_ID, GROUP_COL1_ID, 1,
				"1.2" }, expectedBatchArgs.get(1));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE3_ID, GROUP_COL1_ID, 1,
				"1.3" }, expectedBatchArgs.get(2));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE1_ID, GROUP_COL1_ID, 2,
				"2.1" }, expectedBatchArgs.get(3));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE2_ID, GROUP_COL1_ID, 2,
				"2.2" }, expectedBatchArgs.get(4));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE3_ID, GROUP_COL1_ID, 2,
				"2.3" }, expectedBatchArgs.get(5));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE1_ID, GROUP_COL1_ID, 3,
				"3.1" }, expectedBatchArgs.get(6));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE2_ID, GROUP_COL1_ID, 3,
				"3.2" }, expectedBatchArgs.get(7));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE3_ID, GROUP_COL1_ID, 3,
				"3.3" }, expectedBatchArgs.get(8));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE1_ID, GROUP_COL1_ID, 4,
				"4.1" }, expectedBatchArgs.get(9));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE2_ID, GROUP_COL1_ID, 4,
				"4.2" }, expectedBatchArgs.get(10));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE3_ID, GROUP_COL1_ID, 4,
				"4.3" }, expectedBatchArgs.get(11));
	}

	@Test
	public void testMultipleColumnsWithConstants() throws LoaderException,
			LoaderQueriesException {
		initTest("multiple_columns_with_constants.txt");
		valuesLoader.hybrefIdsByName.put("barcode2", BARCODE2_ID);
		valuesLoader.hybrefIdsByName.put("barcode3", BARCODE3_ID);

		context.checking(new Expectations() {
			{
				one(mockLoaderQueries).insertHybRefDataset(BARCODE1_ID,
						DATASET_ID, "barcode1");
				one(mockLoaderQueries).insertHybRefDataset(BARCODE2_ID,
						DATASET_ID, "barcode2");
				one(mockLoaderQueries).insertHybRefDataset(BARCODE3_ID,
						DATASET_ID, "barcode3");
				one(mockLoaderQueries).insertHybridizationValues(
						expectedBatchArgs);
			}
		});
		valuesLoader.insertValues();
		context.assertIsSatisfied();
		assertEquals(new Object[] { PLATFORM_ID, BARCODE1_ID, GROUP_COL1_ID, 1,
				"1.1" }, expectedBatchArgs.get(0));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE2_ID, GROUP_COL1_ID, 1,
				"1.2" }, expectedBatchArgs.get(1));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE3_ID, GROUP_COL1_ID, 1,
				"1.3" }, expectedBatchArgs.get(2));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE1_ID, GROUP_COL1_ID, 2,
				"2.1" }, expectedBatchArgs.get(3));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE2_ID, GROUP_COL1_ID, 2,
				"2.2" }, expectedBatchArgs.get(4));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE3_ID, GROUP_COL1_ID, 2,
				"2.3" }, expectedBatchArgs.get(5));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE1_ID, GROUP_COL1_ID, 3,
				"3.1" }, expectedBatchArgs.get(6));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE2_ID, GROUP_COL1_ID, 3,
				"3.2" }, expectedBatchArgs.get(7));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE3_ID, GROUP_COL1_ID, 3,
				"3.3" }, expectedBatchArgs.get(8));
	}

	@Test
	public void testMultipleColumnsPerBarcode() throws LoaderException,
			LoaderQueriesException {
		initTest("multiple_columns_per_barcode.txt");
		valuesLoader.hybrefIdsByName.put("barcode2", BARCODE2_ID);
		valuesLoader.hybGroupIds.put("col2", GROUP_COL2_ID);

		context.checking(new Expectations() {
			{
				exactly(2).of(mockLoaderQueries).insertHybRefDataset(
						BARCODE1_ID, DATASET_ID, "barcode1"); // happens twice
																// because we
																// pretend it
																// isn't
																// inserted both
																// times
				exactly(2).of(mockLoaderQueries).insertHybRefDataset(
						BARCODE2_ID, DATASET_ID, "barcode2"); // but if using
																// real DAO will
																// only be
																// called once
																// per barcode
				one(mockLoaderQueries).insertHybridizationValues(
						expectedBatchArgs);
			}
		});
		valuesLoader.insertValues();
		context.assertIsSatisfied();
		assertEquals(new Object[] { PLATFORM_ID, BARCODE1_ID, GROUP_COL1_ID, 1,
				"1.11" }, expectedBatchArgs.get(0));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE1_ID, GROUP_COL2_ID, 1,
				"1.12" }, expectedBatchArgs.get(1));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE2_ID, GROUP_COL1_ID, 1,
				"1.21" }, expectedBatchArgs.get(2));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE2_ID, GROUP_COL2_ID, 1,
				"1.22" }, expectedBatchArgs.get(3));

		assertEquals(new Object[] { PLATFORM_ID, BARCODE1_ID, GROUP_COL1_ID, 2,
				"2.11" }, expectedBatchArgs.get(4));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE1_ID, GROUP_COL2_ID, 2,
				"2.12" }, expectedBatchArgs.get(5));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE2_ID, GROUP_COL1_ID, 2,
				"2.21" }, expectedBatchArgs.get(6));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE2_ID, GROUP_COL2_ID, 2,
				"2.22" }, expectedBatchArgs.get(7));

		assertEquals(new Object[] { PLATFORM_ID, BARCODE1_ID, GROUP_COL1_ID, 3,
				"3.11" }, expectedBatchArgs.get(8));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE1_ID, GROUP_COL2_ID, 3,
				"3.12" }, expectedBatchArgs.get(9));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE2_ID, GROUP_COL1_ID, 3,
				"3.21" }, expectedBatchArgs.get(10));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE2_ID, GROUP_COL2_ID, 3,
				"3.22" }, expectedBatchArgs.get(11));
	}

	@Test
	public void testRefSamplesInData() throws LoaderException,
			LoaderQueriesException {
		initTest("ref_samples_in_data.txt");
		valuesLoader.hybrefIdsByName.put("barcode2", BARCODE2_ID);
		valuesLoader.hybrefIdsByName.put("ref1", null);
		valuesLoader.hybrefIdsByName.put("ref2", null);
		context.checking(new Expectations() {
			{
				one(mockLoaderQueries).insertHybRefDataset(BARCODE1_ID,
						DATASET_ID, "barcode1");
				one(mockLoaderQueries).insertHybRefDataset(BARCODE2_ID,
						DATASET_ID, "barcode2");
				one(mockLoaderQueries).insertHybridizationValues(
						expectedBatchArgs);
			}
		});

		valuesLoader.insertValues();
		context.assertIsSatisfied();
		assertEquals(new Object[] { PLATFORM_ID, BARCODE1_ID, GROUP_COL1_ID, 1,
				"1" }, expectedBatchArgs.get(0));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE2_ID, GROUP_COL1_ID, 1,
				"2" }, expectedBatchArgs.get(1));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE1_ID, GROUP_COL1_ID, 2,
				"5" }, expectedBatchArgs.get(2));
		assertEquals(new Object[] { PLATFORM_ID, BARCODE2_ID, GROUP_COL1_ID, 2,
				"6" }, expectedBatchArgs.get(3));
	}
}
