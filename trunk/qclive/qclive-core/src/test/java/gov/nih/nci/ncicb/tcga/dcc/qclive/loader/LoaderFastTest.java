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
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDDAO;
import gov.nih.nci.ncicb.tcga.dcc.common.service.FileTypeLookupFromMap;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.LoaderQueriesException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.util.RowCounter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.transaction.support.TransactionOperations;

/**
 * Loader test using mock objects for db operations.
 * 
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class LoaderFastTest {
	private static final String SAMPLE_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	private Mockery context = new JUnit4Mockery();
	private LoaderQueries mockLoaderQueries = context.mock(LoaderQueries.class);
	private ArchiveQueries mockArchiveQueries = context
			.mock(ArchiveQueries.class);
	private UUIDDAO mockUUIDDAO = context.mock(UUIDDAO.class);
	private TransactionOperations mockTransactionOperations;
	private List<Object[]> expectedBatchArgs;
	private RowCounter rowCounter;
	private Loader loader;

	private static final int PLATFORM_ID = 1;
	private static final int CENTER_ID = 10;
	private static final long ARCHIVE_ID = 1001;
	private static final long EXPERIMENT_ID = 20;
	private static final long BARCODE1_ID = 101;
	private static final long BARCODE2_ID = 102;
	private static final long BARCODE3_ID = 103;
	private static final long DATASET_ID_1 = 40;
	private static final long HYB_GROUP_RATIO_ID = 50L;
	private static final long HYB_GROUP_A_ID = 200L;
	private static final long HYB_GROUP_B_ID = 201L;
	private static final long HYB_GROUP_VALUE_ID = 202L;
	private static final long DATASET_ID_2 = 41;
	private static final long FILE_INFO_ID_1 = 1001;
	private static final long FILE_INFO_ID_2 = 1002;
	private static final long FILE_INFO_ID_3 = 1003;
	private static final long FILE_INFO_ID_4 = 1004;
	private static final String UUID1 = "067e6162-3b6f-4ae2-a171-2470b63dff01";
	private static final String UUID2 = "067e6162-3b6f-4ae2-a171-2470b63dff02";
	private static final String UUID3 = "067e6162-3b6f-4ae2-a171-2470b63dff03";

	@Before
	public void setup() throws LoaderQueriesException {
		expectedBatchArgs = new ArrayList<Object[]>();
		loader = new Loader() {
			protected ValuesLoader makeValuesLoader() {
				return new ValuesLoader() {
					protected List<Object[]> makeBatchArgumentsList() {
						expectedBatchArgs.clear();
						return expectedBatchArgs;
					}
				};
			}

			protected RowCounter makeRowCounter() {
				rowCounter = new RowCounter();
				return rowCounter;
			}
		};

		Loader.probeLookup = new ProbeLookup() {
			public void load(final int platformId) {
				// do nothing
			}

			// assumes probe names will be like "probeX" where X is the ID we
			// want
			public Integer lookupProbeId(final int platformId,
					final String probeName) {
				return Integer.valueOf(probeName.substring(5));
			}
		};
		loader.setLoaderQueries(mockLoaderQueries);
		loader.setDiseaseArchiveQueries(mockArchiveQueries);
		loader.setCommonArchiveQueries(mockArchiveQueries);
		loader.setUuidDAO(mockUUIDDAO);
		loader.setLogger(new BareBonesLogger());

		mockTransactionOperations = context.mock(TransactionOperations.class);

		context.checking(new Expectations() {
			{
				allowing(mockLoaderQueries).lookupPlatformId("platform");
				will(returnValue(PLATFORM_ID));
				allowing(mockLoaderQueries).lookupCenterId("center",
						PLATFORM_ID);
				will(returnValue(CENTER_ID));
				allowing(mockLoaderQueries).getTransactionOperations();
				will(returnValue(mockTransactionOperations));
			}
		});
	}

	@Test
	public void simpleTest() throws LoaderException, LoaderQueriesException {
		// test an archive with simple data files of a single type, and an SDRF
		// in the same directory
		loader.setLoadDirectory(SAMPLE_DIR
				+ "autoloader/loaderTest/center_DIS.platform.1.0.0");
		FileTypeLookupFromMap lookup = new FileTypeLookupFromMap("archive",
				"center", "platform");
		lookup.addFileType("datafile1.txt", "TEST");
		lookup.addFileType("datafile2.txt", "TEST");
		lookup.addFileType("datafile3.txt", "TEST");
		loader.setFileTypeLookup(lookup);

		final Map<String, Long> filesMap = new HashMap<String, Long>();
		filesMap.put("datafile1.txt", FILE_INFO_ID_1);
		filesMap.put("datafile2.txt", FILE_INFO_ID_2);
		filesMap.put("datafile3.txt", FILE_INFO_ID_3);

		final Map<String, Long> hybGroupMap = new HashMap<String, Long>();
		hybGroupMap.put("ratio", HYB_GROUP_RATIO_ID);
		context.checking(new Expectations() {
			{
				// lookup experiment, find it (so don't insert it)
				one(mockLoaderQueries).lookupExperimentId(
						"center_DIS.platform", 1, 0);
				will(returnValue(EXPERIMENT_ID));

				// lookup archiveId giving archive_name
				one(mockArchiveQueries).getArchiveIdByName(
						"center_DIS.platform.1.0.0");
				will(returnValue(ARCHIVE_ID));

				one(mockUUIDDAO).getUUIDForBarcode("TCGA-10-0000-00A-1");
				will(returnValue(UUID1));
				one(mockUUIDDAO).getUUIDForBarcode("TCGA-20-0000-00A-2");
				will(returnValue(UUID2));
				one(mockUUIDDAO).getUUIDForBarcode("TCGA-30-0000-00A-3");
				will(returnValue(UUID3));

				// insert the 3 hybrefs from the SDRF
				one(mockLoaderQueries).insertHybRef("TCGA-10-0000-00A-1",
						"TCGA-10-0000-00", 0, UUID1);
				will(returnValue(BARCODE1_ID));
				one(mockLoaderQueries).insertHybRef("TCGA-20-0000-00A-2",
						"TCGA-20-0000-00", 0, UUID2);
				will(returnValue(BARCODE2_ID));
				one(mockLoaderQueries).insertHybRef("TCGA-30-0000-00A-3",
						"TCGA-30-0000-00", 0, UUID3);
				will(returnValue(BARCODE3_ID));

				// insert new dataset
				one(mockLoaderQueries).insertDataset(EXPERIMENT_ID,
						"center_DIS.platform.1.0.0/*TEST", "TEST", "PUBLIC", 2,
						CENTER_ID, PLATFORM_ID, ARCHIVE_ID);
				will(returnValue(DATASET_ID_1));
				one(mockLoaderQueries).lookupFileInfoData(ARCHIVE_ID);
				will(returnValue(filesMap));
				// insert hyb data group for this file type (only 1)
				one(mockLoaderQueries).insertHybDataGroups(DATASET_ID_1,
						Arrays.asList("ratio"));
				will(returnValue(hybGroupMap));

				// insert data set - file rows
				one(mockLoaderQueries).insertDataSetFile(DATASET_ID_1,
						"datafile1.txt", FILE_INFO_ID_1);
				one(mockLoaderQueries).insertDataSetFile(DATASET_ID_1,
						"datafile2.txt", FILE_INFO_ID_2);
				one(mockLoaderQueries).insertDataSetFile(DATASET_ID_1,
						"datafile3.txt", FILE_INFO_ID_3);

				// insert hybref - dataset rows if needed
				one(mockLoaderQueries).hybRefDatasetExists(BARCODE1_ID,
						DATASET_ID_1);
				will(returnValue(false));
				one(mockLoaderQueries).insertHybRefDataset(BARCODE1_ID,
						DATASET_ID_1, "barcode1");
				one(mockLoaderQueries).hybRefDatasetExists(BARCODE2_ID,
						DATASET_ID_1);
				will(returnValue(true));
				one(mockLoaderQueries).hybRefDatasetExists(BARCODE3_ID,
						DATASET_ID_1);
				will(returnValue(true));

				// insert values for each hybridization ref
				exactly(3).of(mockLoaderQueries).insertHybridizationValues(
						expectedBatchArgs);

				one(mockLoaderQueries).setDataSetFileLoaded(DATASET_ID_1,
						"datafile1.txt");
				one(mockLoaderQueries).setDataSetFileLoaded(DATASET_ID_1,
						"datafile2.txt");
				one(mockLoaderQueries).setDataSetFileLoaded(DATASET_ID_1,
						"datafile3.txt");
				exactly(2).of(mockArchiveQueries).updateArchiveInfo(ARCHIVE_ID);
				one(mockLoaderQueries).setDataSetLoaded(DATASET_ID_1);
			}
		});

		loader.go();
		context.assertIsSatisfied();
	}

	@Test
	public void multipleTypeTest() throws LoaderQueriesException,
			LoaderException {
		// archive has multiple file types

		loader.setLoadDirectory(SAMPLE_DIR
				+ "autoloader/loaderTest/center_DIS.platform.3.1.0");
		FileTypeLookupFromMap lookup = new FileTypeLookupFromMap("archive",
				"center", "platform");
		lookup.addFileType("file1_apple.txt", "apple");
		lookup.addFileType("file2_apple.txt", "apple");
		lookup.addFileType("file1_orange.txt", "orange");
		lookup.addFileType("file2_orange.txt", "orange");
		loader.setFileTypeLookup(lookup);

		final Map<String, Long> hybGroupMap1 = new HashMap<String, Long>();
		hybGroupMap1.put("A", HYB_GROUP_A_ID);
		hybGroupMap1.put("B", HYB_GROUP_B_ID);

		final Map<String, Long> hybGroupMap2 = new HashMap<String, Long>();
		hybGroupMap2.put("value", HYB_GROUP_VALUE_ID);

		final Map<String, Long> filesMap = new HashMap<String, Long>();
		filesMap.put("file1_apple.txt", FILE_INFO_ID_1);
		filesMap.put("file2_apple.txt", FILE_INFO_ID_2);
		filesMap.put("file1_orange.txt", FILE_INFO_ID_3);
		filesMap.put("file2_orange.txt", FILE_INFO_ID_4);

		context.checking(new Expectations() {
			{
				// lookup experiment, find it (so don't insert it)
				one(mockLoaderQueries).lookupExperimentId(
						"center_DIS.platform", 3, 1);
				will(returnValue(EXPERIMENT_ID));

				// lookup archiveId giving archive_name
				one(mockArchiveQueries).getArchiveIdByName(
						"center_DIS.platform.3.1.0");
				will(returnValue(ARCHIVE_ID));

				one(mockUUIDDAO).getUUIDForBarcode("TCGA-10-0000-00A-1");
				will(returnValue(UUID1));
				one(mockUUIDDAO).getUUIDForBarcode("TCGA-20-0000-00A-2");
				will(returnValue(UUID2));

				// insert the 2 hybrefs from the SDRF
				one(mockLoaderQueries).insertHybRef("TCGA-10-0000-00A-1",
						"TCGA-10-0000-00", 0, UUID1);
				will(returnValue(BARCODE1_ID));
				one(mockLoaderQueries).insertHybRef("TCGA-20-0000-00A-2",
						"TCGA-20-0000-00", 0, UUID2);
				will(returnValue(BARCODE2_ID));

				// insert new dataset for type "apple"
				one(mockLoaderQueries).insertDataset(EXPERIMENT_ID,
						"center_DIS.platform.3.1.0/*apple", "apple", "PUBLIC",
						2, CENTER_ID, PLATFORM_ID, ARCHIVE_ID);
				will(returnValue(DATASET_ID_1));
				one(mockLoaderQueries).lookupFileInfoData(ARCHIVE_ID);
				will(returnValue(filesMap));

				one(mockLoaderQueries).insertDataset(EXPERIMENT_ID,
						"center_DIS.platform.3.1.0/*orange", "orange",
						"PUBLIC", 2, CENTER_ID, PLATFORM_ID, ARCHIVE_ID);
				will(returnValue(DATASET_ID_2));
				one(mockLoaderQueries).lookupFileInfoData(ARCHIVE_ID);
				will(returnValue(filesMap));

				// insert hyb data group for file types -- apple has 2, orange
				// has 1
				one(mockLoaderQueries).insertHybDataGroups(DATASET_ID_1,
						Arrays.asList("A", "B"));
				will(returnValue(hybGroupMap1));
				one(mockLoaderQueries).insertHybDataGroups(DATASET_ID_2,
						Arrays.asList("value"));
				will(returnValue(hybGroupMap2));

				// insert dataset-file rows
				one(mockLoaderQueries).insertDataSetFile(DATASET_ID_1,
						"file1_apple.txt", FILE_INFO_ID_1);
				one(mockLoaderQueries).insertDataSetFile(DATASET_ID_1,
						"file2_apple.txt", FILE_INFO_ID_2);
				one(mockLoaderQueries).insertDataSetFile(DATASET_ID_2,
						"file1_orange.txt", FILE_INFO_ID_3);
				one(mockLoaderQueries).insertDataSetFile(DATASET_ID_2,
						"file2_orange.txt", FILE_INFO_ID_4);

				// insert hybref-dataset rows if needed
				exactly(2).of(mockLoaderQueries).hybRefDatasetExists(
						BARCODE1_ID, DATASET_ID_1);
				will(returnValue(true));
				exactly(2).of(mockLoaderQueries).hybRefDatasetExists(
						BARCODE2_ID, DATASET_ID_1);
				will(returnValue(true));
				one(mockLoaderQueries).hybRefDatasetExists(BARCODE1_ID,
						DATASET_ID_2);
				will(returnValue(false));
				one(mockLoaderQueries).insertHybRefDataset(BARCODE1_ID,
						DATASET_ID_2, "barcode1");
				one(mockLoaderQueries).hybRefDatasetExists(BARCODE2_ID,
						DATASET_ID_2);
				will(returnValue(false));
				one(mockLoaderQueries).insertHybRefDataset(BARCODE2_ID,
						DATASET_ID_2, "barcode2");

				exactly(4).of(mockLoaderQueries).insertHybridizationValues(
						expectedBatchArgs);

				one(mockLoaderQueries).setDataSetFileLoaded(DATASET_ID_1,
						"file1_apple.txt");
				one(mockLoaderQueries).setDataSetFileLoaded(DATASET_ID_1,
						"file2_apple.txt");
				one(mockLoaderQueries).setDataSetFileLoaded(DATASET_ID_2,
						"file1_orange.txt");
				one(mockLoaderQueries).setDataSetFileLoaded(DATASET_ID_2,
						"file2_orange.txt");

				one(mockLoaderQueries).setDataSetLoaded(DATASET_ID_1);
				one(mockLoaderQueries).setDataSetLoaded(DATASET_ID_2);
				exactly(2).of(mockArchiveQueries).updateArchiveInfo(ARCHIVE_ID);
			}
		});

		loader.go();
		context.assertIsSatisfied();
		assertEquals(18, rowCounter.getRowCount("hybridization_value"));
	}

	@Test
	public void testArchiveWithRefSamples() throws LoaderQueriesException,
			LoaderException {
		// this archive's SDRF has non-TCGA samples (RefSeq, etc) and also in
		// the data file
		loader.setLoadDirectory(SAMPLE_DIR
				+ "autoloader/loaderTest/center_DIS.platform.10.3.0");
		FileTypeLookupFromMap lookup = new FileTypeLookupFromMap("archive",
				"center", "platform");
		lookup.addFileType("data.txt", "fun");
		loader.setFileTypeLookup(lookup);

		final Map<String, Long> hybGroupMap = new HashMap<String, Long>();
		hybGroupMap.put("value", HYB_GROUP_VALUE_ID);
		final Map<String, Long> filesMap = new HashMap<String, Long>();
		filesMap.put("data.txt", FILE_INFO_ID_1);

		context.checking(new Expectations() {
			{
				// lookup experiment, find it (so don't insert it)
				one(mockLoaderQueries).lookupExperimentId(
						"center_DIS.platform", 10, 3);
				will(returnValue(EXPERIMENT_ID));
				// lookup archiveId giving archive_name
				one(mockArchiveQueries).getArchiveIdByName(
						"center_DIS.platform.10.3.0");
				will(returnValue(ARCHIVE_ID));

				one(mockUUIDDAO).getUUIDForBarcode("TCGA-10-0000-00A-1");
				will(returnValue(UUID1));
				one(mockUUIDDAO).getUUIDForBarcode("TCGA-20-0000-00A-2");
				will(returnValue(UUID2));

				// insert the 2 hybrefs from the SDRF -- does not add the two
				// reference hybrefs
				one(mockLoaderQueries).insertHybRef("TCGA-10-0000-00A-1",
						"TCGA-10-0000-00", 0, UUID1);
				will(returnValue(BARCODE1_ID));
				one(mockLoaderQueries).insertHybRef("TCGA-20-0000-00A-2",
						"TCGA-20-0000-00", 0, UUID2);
				will(returnValue(BARCODE2_ID));

				// insert new dataset
				one(mockLoaderQueries).insertDataset(EXPERIMENT_ID,
						"center_DIS.platform.10.3.0/*fun", "fun", "PUBLIC", 2,
						CENTER_ID, PLATFORM_ID, ARCHIVE_ID);
				will(returnValue(DATASET_ID_1));
				one(mockLoaderQueries).lookupFileInfoData(ARCHIVE_ID);
				will(returnValue(filesMap));

				// insert hyb data group for this file type (only 1)
				one(mockLoaderQueries).insertHybDataGroups(DATASET_ID_1,
						Arrays.asList("value"));
				will(returnValue(hybGroupMap));

				one(mockLoaderQueries).insertDataSetFile(DATASET_ID_1,
						"data.txt", FILE_INFO_ID_1);

				one(mockLoaderQueries).hybRefDatasetExists(BARCODE1_ID,
						DATASET_ID_1);
				will(returnValue(true));
				one(mockLoaderQueries).hybRefDatasetExists(BARCODE2_ID,
						DATASET_ID_1);
				will(returnValue(true));

				one(mockLoaderQueries).insertHybridizationValues(
						expectedBatchArgs);

				one(mockLoaderQueries).setDataSetFileLoaded(DATASET_ID_1,
						"data.txt");
				one(mockLoaderQueries).setDataSetLoaded(DATASET_ID_1);
				exactly(2).of(mockArchiveQueries).updateArchiveInfo(ARCHIVE_ID);
			}
		});

		loader.go();
		context.assertIsSatisfied();
	}
}
