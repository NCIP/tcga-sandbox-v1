/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Barcode;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DBUnitTestCase;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.UUIDDAO;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidatorImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.ArchiveLogger;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.Logger;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.LoggerImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.StdoutLoggerDestination;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRUtils;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeUuidResolverImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ClinicalLoaderQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.jdbc.ClinicalLoaderQueriesJDBCImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.clinical.ClinicalLoaderException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.clinical.ClinicalObject;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.jdbc.SimpleJdbcTestUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Slow test for ClinicalLoader functionality. Runs the ClinicalLoaderCaller on
 * a test archive
 * <p/>
 * refactored ClinicalLoaderSlowTest (standalone) to work with
 * ClincalLoaderCaller (integrated with QCLive)
 * 
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class ClinicalLoaderCallerDBUnitSlowTest extends DBUnitTestCase {
	private final Mockery context = new JUnit4Mockery();
	private UUIDService mockUuidService;
	private UUIDDAO mockUuidQueries;
	private ArchiveQueries mockArchiveQueries;
	private ClinicalLoaderCaller clinicalLoaderCaller;
	private Archive archive;
	private SimpleJdbcTemplate simpleJdbcTemplate;
	private ClinicalLoaderQueries clinicalLoaderQueries;
	private ArchiveLogger mockArchiveLogger;
	private MailSender mockMailSender;
    private BCRUtils mockBcrUtils;

	private static final String SAMPLES_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;

	/**
	 * Constructor , used to initialize DBUnit
	 */
	public ClinicalLoaderCallerDBUnitSlowTest() {
		super(SAMPLES_DIR, "autoloader/clinical/clinicalLoader_testDb.xml",
				"oracle.unittest.properties");
	}

	@Override
	public void setUp() throws Exception {
		super.setUp();
		clinicalLoaderCaller = new ClinicalLoaderCaller() {
			protected File[] getArchiveXmlFiles(final Archive archive) {
				final File[] filesToLoad = new File[1];
				filesToLoad[0] = new File(SAMPLES_DIR + "autoloader" 
						+ File.separator + "clinical"
						+ File.separator + "filesForSlowTest" + File.separator
						+ "TCGA-00-0000.biospecimen.xml");

				return filesToLoad;
			}
		};

		final Logger logger = new LoggerImpl();
		logger.addDestination(new StdoutLoggerDestination());
		clinicalLoaderCaller.setLogger(logger);
		// need to set clinicalLoaderQueries (using real dbunit data source)
		clinicalLoaderQueries = new ClinicalLoaderQueriesJDBCImpl();

		((ClinicalLoaderQueriesJDBCImpl) clinicalLoaderQueries)
				.setDataSource(getDataSource());
		clinicalLoaderCaller.setClinicalLoaderQueries(clinicalLoaderQueries);

		// set uuid queries and uuid service as MOCK objects
		mockUuidService = context.mock(UUIDService.class);
		mockUuidQueries = context.mock(UUIDDAO.class);
		mockArchiveQueries = context.mock(ArchiveQueries.class);
		clinicalLoaderCaller.setArchiveQueries(mockArchiveQueries);
		simpleJdbcTemplate = new SimpleJdbcTemplate(getDataSource());
		mockArchiveLogger = context.mock(ArchiveLogger.class);
		mockMailSender = context.mock(MailSender.class);
		clinicalLoaderCaller.setMailSender(mockMailSender);
		clinicalLoaderCaller.setArchiveLogger(mockArchiveLogger);
		clinicalLoaderCaller.setValidClinicalPlatforms("bio");
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(
				getDataSource());
		clinicalLoaderCaller.setTransactionManager(transactionManager);
		final BarcodeUuidResolverImpl barcodeUuidResolver = new BarcodeUuidResolverImpl();
		barcodeUuidResolver.setUuidService(mockUuidService);
		barcodeUuidResolver.setUuidDAO(mockUuidQueries);
		barcodeUuidResolver
				.setBarcodeAndUUIDValidator(new CommonBarcodeAndUUIDValidatorImpl());

		clinicalLoaderCaller.setBarcodeUuidResolver(barcodeUuidResolver);

        mockBcrUtils = context.mock(BCRUtils.class);
        clinicalLoaderCaller.setBcrUtils(mockBcrUtils);

		archive = new Archive(SAMPLES_DIR + "autoloader"
				+ File.separator + "clinical" + File.separator
				+ "intgen.org_GBM.bio.Level_1.38.4.0.tar.gz");

		final Center center = new Center();
		center.setCenterId(1);
		archive.setTheCenter(center);
		archive.setId(1234L);
		archive.setRealName("testArchive");
		archive.setExperimentType(Experiment.TYPE_BCR);
		archive.setPlatform("bio");
		archive.setDeployStatus(Archive.STATUS_AVAILABLE);
		final Tumor theTumor = new Tumor();
		theTumor.setTumorName("TEST");
		archive.setTheTumor(theTumor);

		context.checking(new Expectations() {
			{
				// pretend patient barcode already has a UUID
				allowing(mockUuidService).getUUIDForBarcode("TCGA-00-0000");
				will(returnValue("12345678-1234-1234-1234-1234567890ab"));

				one(mockArchiveQueries).getArchive(1234);
				will(returnValue(archive));

				one(mockArchiveQueries).getArchiveIdByName(
						"intgen.org_GBM.bio.Level_1.38.4.0");
				will(returnValue(1234L));

				allowing(mockArchiveLogger).addArchiveLog(
						with(any(Archive.class)), with(any(String.class)));
				allowing(mockMailSender).send(with(any(String.class)),
						with(any(String.class)), with(any(String.class)),
						with(any(String.class)), with(any(Boolean.class)));

                allowing(mockBcrUtils).isAuxiliaryFile(with(any(File.class)));
                will(returnValue(false));

                allowing(mockBcrUtils).isControlFile(with(any(File.class)));
                will(returnValue(false));
			}
		});

		// make all uuids in file not registered in system yet, so code will
		// register and associate them
		expectUuidAndBarcodePair("TCGA-00-0000-01A",
				"11111111-1111-1111-1111-111111111111", false);
		expectUuidAndBarcodePair("TCGA-00-0000-01A-11",
				"22222222-2222-2222-2222-222222222222", false);
		expectUuidAndBarcodePair("TCGA-00-0000-01A-11D",
				"33333333-3333-3333-3333-333333333333", false);
		expectUuidAndBarcodePair("TCGA-00-0000-01A-11D-A011-01",
				"44444444-4444-4444-4444-444444444444", false);
		expectUuidAndBarcodePair("TCGA-00-0000-01A-11R",
				"55555555-5555-5555-5555-555555555555", false);
		expectUuidAndBarcodePair("TCGA-00-0000-01A-11R-A00Z-07",
				"66666666-6666-6666-6666-666666666666", false);
		expectUuidAndBarcodePair("TCGA-00-0000-01A-11R-A010-13",
				"77777777-7777-7777-7777-777777777777", false);
		expectUuidAndBarcodePair("TCGA-00-0000-01A-01-TSA",
				"88888888-8888-8888-8888-888888888888", false);
		expectUuidAndBarcodePair("TCGA-00-0000-01A-01-BSA",
				"99999999-9999-9999-9999-999999999999", false);
		expectUuidAndBarcodePair("TCGA-00-0000-10A",
				"00000000-0000-0000-0000-000000000000", false);
		expectUuidAndBarcodePair("TCGA-00-0000-10A-01",
				"aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa", false);
		expectUuidAndBarcodePair("TCGA-00-0000-10A-01D",
				"bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb", false);
		expectUuidAndBarcodePair("TCGA-00-0000-10A-01D-A012-01",
				"cccccccc-cccc-cccc-cccc-cccccccccccc", false);
		expectUuidAndBarcodePair("TCGA-00-0000-10A-01D-A017-09",
				"dddddddd-dddd-dddd-dddd-dddddddddddd", false);

		try {
			String insertClinicalMetaDataScript = 
				SAMPLES_DIR + "autoloader" + File.separator + "clinical" + File.separator + "InsertClinicalMetaData.sql";
			SimpleJdbcTestUtils.executeSqlScript(simpleJdbcTemplate, new FileSystemResource(insertClinicalMetaDataScript), false);
		} catch (Exception e) {
			//deleteAll();
		}

	}

	private void expectUuidAndBarcodePair(final String barcode,
			final String uuid, final boolean alreadyExists)
			throws UUIDException {
		context.checking(new Expectations() {
			{
				one(mockUuidService).getUUIDForBarcode(barcode);
				will(returnValue(null));
				one(mockUuidService).getLatestBarcodeForUUID(uuid);
				will(returnValue(null));
				one(mockUuidQueries).uuidExists(uuid);
				will(returnValue(alreadyExists));

				if (!alreadyExists) {
					one(mockUuidService).registerUUID(uuid, 1);
				}
				one(mockUuidService).addBarcode(
						with(expectedBarcode(barcode, uuid)));
			}
		});
	}

	private Matcher<Barcode> expectedBarcode(final String barcode,
			final String uuid) {
		return new TypeSafeMatcher<Barcode>() {

			@Override
			public boolean matchesSafely(final Barcode barcodeDetail) {
				return barcodeDetail.getBarcode().equals(barcode)
						&& barcodeDetail.getUuid().equals(uuid);
			}

			@Override
			public void describeTo(final Description description) {
				description.appendText("expected barcode/uuid");
			}
		};
	}

	/*
	 * main running method, used to call testing methods
	 */
	public void testLoadAll() throws ClinicalLoaderException {
		try {
			final List<Archive> archiveList = new ArrayList<Archive>();
			archiveList.add(archive);

			clinicalLoaderCaller.load(archiveList, null);
			checkPatients();
			checkSamples();
			checkPortions();
			checkAnalytes();
			checkAliquots();
			checkProtocols();
			checkDna();
            checkRna();

		} finally {
			deleteAll();
		}
	}

	/*
	 * Tests loading archive by name
	 */
	public void testLoadArchive() throws ClinicalLoaderException {
		context.checking(new Expectations() {
			{
				one(mockArchiveQueries).getArchiveIdByName("testArchive");
				will(returnValue(1234L));

			}
		});
		try {
			clinicalLoaderCaller.loadArchiveByName("testArchive", null);
			checkPatients();
			checkSamples();
			checkPortions();
			checkAnalytes();
			checkAliquots();
			checkProtocols();
			checkDna();
            checkRna();

		} finally {
			deleteAll();
		}
	}

    private void checkDna() {

        final long parentId = getSimpleJdbcTemplate().queryForLong("select parent_table_id from clinical_table where element_node_name=?", new Object[]{"dna"});
        assertTrue(parentId > 0);

        checkTable("DNA", "dna", 2, null, parentId);
    }

    private void checkRna() {

        final long parentId = getSimpleJdbcTemplate().queryForLong("select parent_table_id from clinical_table where element_node_name=?", new Object[]{"rna"});
        assertTrue(parentId > 0);

		checkTable("RNA", "rna", 1, null, parentId);
    }

	private void checkProtocols() {

        final long parentId = getSimpleJdbcTemplate().queryForLong("select parent_table_id from clinical_table where element_node_name=?", new Object[]{"protocol"});
        assertTrue(parentId > 0);

		checkTable("PROTOCOL", "protocol", 3, null, parentId);
		final List<Map<String, Object>> results = simpleJdbcTemplate
				.queryForList("select element_name, element_value "
						+ "from protocol_element p, clinical_xsd_element e where p.clinical_xsd_element_id=e.clinical_xsd_element_id "
						+ "and element_value is not null order by element_name, element_value");
		assertEquals("aDNA Preparation Type",
				results.get(0).get("ELEMENT_VALUE"));
		assertEquals("mirVana (Allprep DNA) RNA",
				results.get(1).get("ELEMENT_VALUE"));
		assertEquals("tssDNA", results.get(2).get("ELEMENT_VALUE"));
	}

	private void checkPatients() {
		final Map<String, Map<String, String>> expectedPatientData = new HashMap<String, Map<String, String>>();
		expectedPatientData.put("TCGA-00-0000", null);
		checkTable("PATIENT", "patient", 1, expectedPatientData, null);
	}

	private void checkSamples() {
		final Map<String, Map<String, String>> expectedSampleData = new HashMap<String, Map<String, String>>();
		final Map<String, String> firstSampleData = new HashMap<String, String>();
		expectedSampleData.put("TCGA-00-0000-01A", firstSampleData);
		firstSampleData.put("sample_type", "Primary Tumor");
		firstSampleData.put("longest_dimension", null);
		firstSampleData.put("intermediate_dimension", null);
		firstSampleData.put("shortest_dimension", null);
		firstSampleData.put("initial_weight", "500");
		firstSampleData.put("current_weight", null);
		firstSampleData.put("freezing_method", null);
		firstSampleData.put("oct_embedded", "true");
		firstSampleData.put("time_between_clamping_and_freezing", null);
		firstSampleData.put("time_between_excision_and_freezing", null);
		firstSampleData.put("newly_added", "new");

		final Map<String, String> secondSampleData = new HashMap<String, String>();
		expectedSampleData.put("TCGA-00-0000-10A", secondSampleData);
		secondSampleData.put("sample_type", "Blood Derived Normal");
		secondSampleData.put("longest_dimension", null);
		secondSampleData.put("intermediate_dimension", null);
		secondSampleData.put("shortest_dimension", null);
		secondSampleData.put("initial_weight", null);
		secondSampleData.put("current_weight", null);
		secondSampleData.put("freezing_method", null);
		secondSampleData.put("oct_embedded", "false");
		secondSampleData.put("time_between_clamping_and_freezing", null);
		secondSampleData.put("time_between_excision_and_freezing", null);

        final long parentId = getSimpleJdbcTemplate().queryForLong("select parent_table_id from clinical_table where element_node_name=?", new Object[]{"sample"});
        assertTrue(parentId > 0);

		checkTable("SAMPLE", "sample", 2, expectedSampleData, parentId);
	}

	private void checkPortions() {
		final Map<String, Map<String, String>> expectedPortionData = new HashMap<String, Map<String, String>>();
		final Map<String, String> firstPortionData = new HashMap<String, String>();
		expectedPortionData.put("TCGA-00-0000-01A-11", firstPortionData);
		firstPortionData.put("day_of_creation", "01");
		firstPortionData.put("month_of_creation", "01");
		firstPortionData.put("year_of_creation", "2010");
		firstPortionData.put("weight", "30.00");

		final Map<String, String> secondPortionData = new HashMap<String, String>();
		expectedPortionData.put("TCGA-00-0000-10A-01", secondPortionData);
		secondPortionData.put("day_of_creation", "30");
		secondPortionData.put("month_of_creation", "04");
		secondPortionData.put("year_of_creation", "2010");
		secondPortionData.put("weight", null);

        final long parentId = getSimpleJdbcTemplate().queryForLong("select parent_table_id from clinical_table where element_node_name=?", new Object[]{"portion"});
        assertTrue(parentId > 0);

		checkTable("PORTION", "portion", 2, expectedPortionData, parentId);
	}

	private void checkAnalytes() {
		// 3 analytes
		final Map<String, Map<String, String>> expectedAnalyteData = new HashMap<String, Map<String, String>>();
		final Map<String, String> firstAnalyteData = new HashMap<String, String>();
		expectedAnalyteData.put("TCGA-00-0000-01A-11D", firstAnalyteData);
		firstAnalyteData.put("analyte_type", "DNA");
		firstAnalyteData.put("concentration", "0.16");
		firstAnalyteData.put("amount", "66.88");
		firstAnalyteData.put("a260_a280_ratio", "1.91");
		firstAnalyteData.put("gel_image_file", "fakeImageFileName");
		firstAnalyteData.put("well_number", null);

		final Map<String, String> secondAnalyteData = new HashMap<String, String>();
		expectedAnalyteData.put("TCGA-00-0000-01A-11R", secondAnalyteData);
		secondAnalyteData.put("analyte_type", "RNA");
		secondAnalyteData.put("concentration", "0.16");
		secondAnalyteData.put("amount", "86.83");
		secondAnalyteData.put("a260_a280_ratio", "1.78");
		secondAnalyteData.put("gel_image_file", "something");
		secondAnalyteData.put("well_number", null);

		final Map<String, String> thirdAnalyteData = new HashMap<String, String>();
		expectedAnalyteData.put("TCGA-00-0000-10A-01D", thirdAnalyteData);
		thirdAnalyteData.put("analyte_type", "DNA");
		thirdAnalyteData.put("concentration", "0.15");
		thirdAnalyteData.put("amount", "24.18");
		thirdAnalyteData.put("a260_a280_ratio", "1.95");
		thirdAnalyteData.put("gel_image_file", "hi");
		thirdAnalyteData.put("well_number", null);

        final long parentId = getSimpleJdbcTemplate().queryForLong("select parent_table_id from clinical_table where element_node_name=?", new Object[]{"analyte"});
        assertTrue(parentId > 0);

		checkTable("ANALYTE", "analyte", 3, expectedAnalyteData, parentId);
	}

	private void checkAliquots() {
		final Map<String, Map<String, String>> expectedAliquotData = new HashMap<String, Map<String, String>>();
		final Map<String, String> firstAliquotData = new HashMap<String, String>();
		expectedAliquotData.put("TCGA-00-0000-01A-11D-A011-01",
				firstAliquotData);
		firstAliquotData.put("amount", "6.67");
		firstAliquotData.put("day_of_shipment", "15");
		firstAliquotData.put("month_of_shipment", "06");
		firstAliquotData.put("year_of_shipment", "2010");
		firstAliquotData.put("concentration", "0.16");

		final Map<String, String> secondAliquotData = new HashMap<String, String>();
		expectedAliquotData.put("TCGA-00-0000-01A-11R-A00Z-07",
				secondAliquotData);
		secondAliquotData.put("amount", "26.70");
		secondAliquotData.put("day_of_shipment", "15");
		secondAliquotData.put("month_of_shipment", "06");
		secondAliquotData.put("year_of_shipment", "2010");
		secondAliquotData.put("concentration", "0.16");

		final Map<String, String> thirdAliquotData = new HashMap<String, String>();
		expectedAliquotData.put("TCGA-00-0000-01A-11R-A010-13",
				thirdAliquotData);
		thirdAliquotData.put("amount", "20.00");
		thirdAliquotData.put("day_of_shipment", "15");
		thirdAliquotData.put("month_of_shipment", "06");
		thirdAliquotData.put("year_of_shipment", "2010");
		thirdAliquotData.put("concentration", "0.16");

		final Map<String, String> fourthAliquotData = new HashMap<String, String>();
		expectedAliquotData.put("TCGA-00-0000-10A-01D-A012-01",
				fourthAliquotData);
		fourthAliquotData.put("amount", "6.67");
		fourthAliquotData.put("day_of_shipment", "15");
		fourthAliquotData.put("month_of_shipment", "06");
		fourthAliquotData.put("year_of_shipment", "2010");
		fourthAliquotData.put("concentration", "0.15");

		final Map<String, String> fifthAliquotData = new HashMap<String, String>();
		expectedAliquotData.put("TCGA-00-0000-10A-01D-A017-09",
				fifthAliquotData);
		fifthAliquotData.put("amount", "6.67");
		fifthAliquotData.put("day_of_shipment", "29");
		fifthAliquotData.put("month_of_shipment", "06");
		fifthAliquotData.put("year_of_shipment", "2010");
		fifthAliquotData.put("concentration", "0.15");

        final long parentId = getSimpleJdbcTemplate().queryForLong("select parent_table_id from clinical_table where element_node_name=?", new Object[]{"aliquot"});
        assertTrue(parentId > 0);

		checkTable("ALIQUOT", "aliquot", 5, expectedAliquotData, parentId);
	}

    /**
     * Check assertions for the given table
     *
     * @param tableName the table name
     * @param elementName the element name
     * @param expectedRows the expected row count
     * @param expectedElementValuesPerBarcode the expected values per barcode
     * @param parentId the element parent Id
     */
    private void checkTable(
            final String tableName,
            final String elementName,
            final int expectedRows,
            final Map<String, Map<String, String>> expectedElementValuesPerBarcode,
            final Long parentId) {

        // 1. check expected row count
        assertEquals(expectedRows, SimpleJdbcTestUtils.countRowsInTable(
                simpleJdbcTemplate, tableName));

        // 2. for each barcode, check that there is a row for it, and then that
        // there are element values for that as expected
        if (expectedElementValuesPerBarcode != null) {
            for (final String barcode : expectedElementValuesPerBarcode
                    .keySet()) {
                final ClinicalObject clinicalObject = clinicalLoaderQueries.getClinicalObjectForBarcode(elementName, barcode, parentId);
                assertNotNull(clinicalObject);

                // Test getter with a different parent Id, should not return any result
                if(parentId != null) {
                    final ClinicalObject clinicalObjectWrongParent = clinicalLoaderQueries.getClinicalObjectForBarcode(elementName, barcode, parentId+1);
                    assertNull(clinicalObjectWrongParent);
                } else {
                    final ClinicalObject clinicalObjectWrongParent = clinicalLoaderQueries.getClinicalObjectForBarcode(elementName, barcode, 1L);
                    assertNull(clinicalObjectWrongParent);
                }


                if (expectedElementValuesPerBarcode.get(barcode) != null) {
                    for (final String expectedAttribute : expectedElementValuesPerBarcode
                            .get(barcode).keySet()) {
                        assertTrue(tableName + " element table for '" + barcode
                                + "' does not have expected attribute '"
                                + expectedAttribute + "'", clinicalObject
                                .getAttributeNames()
                                .contains(expectedAttribute));
                        final String expectedValue = expectedElementValuesPerBarcode
                                .get(barcode).get(expectedAttribute);
                        assertEquals(tableName + " element table value for "
                                + barcode + " attribute '" + expectedAttribute
                                + "' does not have expected value '"
                                + expectedValue + "'", expectedValue,
                                clinicalObject.getValue(expectedAttribute));
                    }
                }
            }
        }
    }

	private void deleteAll() {
		SimpleJdbcTestUtils.deleteFromTables(simpleJdbcTemplate,
				"aliquot_element", "aliquot_archive", "aliquot", "dna_element",
				"dna_archive", "dna", "rna_element", "rna_archive", "rna",
				"protocol_element", "protocol_archive", "protocol",
				"analyte_element", "analyte_archive", "analyte",
				"slide_element", "slide_archive", "slide", "portion_element",
				"portion_archive", "portion", "sample_element",
				"sample_archive", "sample", "radiation_element",
				"radiation_archive", "radiation", "surgery_element",
				"surgery_archive", "surgery", "drug_intgen_element",
				"drug_intgen_archive", "drug_intgen", "examination_element",
				"examination_archive", "examination", "patient_element",
				"patient_archive", "patient", "clinical_xsd_enum_value",
				"clinical_file_to_table", "clinical_file_element",
				"clinical_file", "clinical_table", "clinical_xsd_element");
	}
}
