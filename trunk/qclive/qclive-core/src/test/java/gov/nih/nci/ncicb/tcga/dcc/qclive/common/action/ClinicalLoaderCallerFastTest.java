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
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcLiveStateBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.ArchiveLogger;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging.Logger;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRUtils;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeUuidResolver;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ClinicalLoaderQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.clinical.ClinicalLoaderException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.clinical.ClinicalObject;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.clinical.ClinicalTable;
import org.apache.log4j.Level;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test Class for ClinicalLoaderCaller class
 * 
 * @author Stanley Girshik Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class ClinicalLoaderCallerFastTest {

	private static final String SAMPLE_DIRECTORY_LOCATION = 
		Thread.currentThread().getContextClassLoader().getResource("samples/autoloader/clinical").getPath() + File.separator;
	private final Mockery context = new JUnit4Mockery();

	private ClinicalLoaderQueries mockClinicalLoaderQueries;
	private ArchiveQueries mockArchiveQueries;
	private ArchiveLogger mockArchiveLogger;
	private Logger mockLogger;
	private BarcodeUuidResolver mockBarcodeUuidResolver;
	private PlatformTransactionManager mockPlatformTxManager;
    private BCRUtils mockBcrUtils;
	private ClinicalLoaderCaller loader;
	private ClinicalObject patient, sample, portion, analyte, aliquot,
			justPatient;
	private Archive archive;
	private static final int CENTER_ID = 42;
	private static final List<String> ALLOWED_ELEMENT_TYPES = Arrays.asList(
			"patient", "sample", "portion", "analyte", "aliquot", "dna", "rna",
			"slide", "protocol", "drug", "surgery", "radiation",
			"tumor_pathology", "biospecimen_cqcf", "normal_control", "tumor_sample", "clinical_cqcf", "follow_up", "control");
	private static final String PATIENT_BARCODE = "patient-1";
	private final File biospecimenXmlFile = new File(SAMPLE_DIRECTORY_LOCATION
			+ "fake_TEST.bio.Level_1.1.2.0/testFile_Biospecimen.xml");
    private final File controlXmlFile = new File(SAMPLE_DIRECTORY_LOCATION + "test_CNTL.bio.Level_1.0.10.0/test.control.xml");

	private QcLiveStateBean stateContex;

	@Before
	public void setup() {
		loader = new ClinicalLoaderCaller() {
			protected String getPatientBarcodeFromFilename(final String fileName) {
				return PATIENT_BARCODE;
			}
		};

		loader.setValidClinicalPlatforms("bio");
		mockClinicalLoaderQueries = context.mock(ClinicalLoaderQueries.class);
		mockArchiveQueries = context.mock(ArchiveQueries.class);
		mockArchiveLogger = context.mock(ArchiveLogger.class);
		mockLogger = context.mock(Logger.class);
		mockBarcodeUuidResolver = context.mock(BarcodeUuidResolver.class);
		mockPlatformTxManager = context.mock(PlatformTransactionManager.class);
        mockBcrUtils = context.mock(BCRUtils.class);

		loader.setClinicalLoaderQueries(mockClinicalLoaderQueries);
		loader.setArchiveQueries(mockArchiveQueries);
		loader.setArchiveLogger(mockArchiveLogger);
		loader.setLogger(mockLogger);
		loader.setBarcodeUuidResolver(mockBarcodeUuidResolver);
		loader.setTransactionManager(mockPlatformTxManager);
        loader.setBcrUtils(mockBcrUtils);
		stateContex = new QcLiveStateBean();
		stateContex.setTransactionId(1l);

		archive = new Archive();
		archive.setRealName("thisIsAnArchiveName");
		archive.setId(123L);
		archive.setExperimentType(Experiment.TYPE_BCR);
		archive.setPlatform("bio");
		archive.setDeployStatus(Archive.STATUS_AVAILABLE);
		final Center archiveCenter = new Center();
		archiveCenter.setCenterId(CENTER_ID);
		archive.setTheCenter(archiveCenter);
		final Tumor theTumor = new Tumor();
		theTumor.setTumorName("TEST");
		archive.setTheTumor(theTumor);

		patient = new ClinicalObject();
		patient.setObjectType("patient");
		patient.setBarcode("patient-barcode");
		sample = new ClinicalObject();
		sample.setBarcode("sample-barcode");
		portion = new ClinicalObject();
		portion.setBarcode("portion-barcode");
		analyte = new ClinicalObject();
		analyte.setBarcode("analyte-barcode");
		aliquot = new ClinicalObject();
		aliquot.setBarcode("aliquot-barcode");

		patient.addChild(sample);
		sample.addChild(portion);
		portion.addChild(analyte);
		analyte.addChild(aliquot);

		justPatient = new ClinicalObject();
		justPatient.setObjectType("patient");
		justPatient.setBarcode("justPatientBarcode");

        final ClinicalTable followUpTable = new ClinicalTable();
        followUpTable.setElementNodeName("follow_up");
        followUpTable.setDynamic(true);
        followUpTable.setTableName("FOLLOW_UP");
        followUpTable.setArchiveLinkTableName("FOLLOW_UP_ARCHIVE");
        followUpTable.setDynamicIdentifierColumnName("follow_up_version");
        followUpTable.setElementTableName("FOLLOW_UP_ELEMENT");

		context.checking(new Expectations() {
			{
				allowing(mockClinicalLoaderQueries)
						.getClinicalTableForElementName(with(any(String.class)), with(any(Long.class)));
				will(returnClinicalTable());

                allowing(mockClinicalLoaderQueries).getDynamicClinicalTables();
                will(returnValue(Arrays.asList(followUpTable)));
			}
		});
	}

	@Test
	public void testGetPatientBarcodeFromFilename()
			throws ClinicalLoaderException {
		final ClinicalLoaderCaller loaderCaller = new ClinicalLoaderCaller();
		loaderCaller
				.getPatientBarcodeFromFilename("blahblahblah-TCGA-11-1234-hello.xml");
		final String barcode = loaderCaller
				.getPatientBarcodeFromFilename("TCGA-AA-BCDE-123.txt");
		assertEquals("TCGA-AA-BCDE", barcode);
	}

	@Test(expected = ClinicalLoaderException.class)
	public void testGetPatientBarcodeFromBadFilename()
			throws ClinicalLoaderException {
		final ClinicalLoaderCaller loaderCaller = new ClinicalLoaderCaller();
		loaderCaller.getPatientBarcodeFromFilename("biospecimen.xml");
	}

	@Test
	public void testSavePatientNew() throws ClinicalLoaderException,
			UUIDException {
		final Barcode barcode = new Barcode();
		barcode.setUuid("fake-uuid-1");

		context.checking(new Expectations() {
			{
				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						"justPatientBarcode", null, archive.getTheTumor(),
						archive.getTheCenter(), true);
				will(returnValue(barcode));

				// none of the objects are in the db yet
				one(mockClinicalLoaderQueries).getId(justPatient);
				will(returnValue(-1L));
				one(mockClinicalLoaderQueries).insert(justPatient, -1L, 123,
						null);
			}
		});
		loader.save(justPatient, ClinicalLoaderCaller.FileType.Biospecimen,
				archive);
		assertEquals("fake-uuid-1", justPatient.getUuid());
	}

    @Test (expected = ClinicalLoaderException.class)
    public void testSavePatientDbException() throws Exception {
        final Barcode barcode = new Barcode();
		barcode.setUuid("fake-uuid-1");

		context.checking(new Expectations() {
			{				
                // get ID results in DAO exception
				one(mockClinicalLoaderQueries).getId(justPatient);
				will(throwException(new IncorrectResultSizeDataAccessException(2)));
			}
		});
		loader.save(justPatient, ClinicalLoaderCaller.FileType.Biospecimen,
				archive);
    }

	@Test
	public void testSaveBiospecimenPatientExisting() throws UUIDException, ClinicalLoaderException {
		final Barcode barcode = new Barcode();
		barcode.setUuid("fake-uuid");

		context.checking(new Expectations() {
			{
				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						"justPatientBarcode", null, archive.getTheTumor(),
						archive.getTheCenter(), true);
				will(returnValue(barcode));

				one(mockClinicalLoaderQueries).getId(justPatient);
				will(returnValue(100L));
				one(mockClinicalLoaderQueries).addArchiveLink(justPatient, 123);

				// since this is a biospecimen update, no need to update
				// anything
			}
		});
		loader.save(justPatient, ClinicalLoaderCaller.FileType.Biospecimen,
				archive);
		assertEquals("fake-uuid", justPatient.getUuid());
	}

	@Test
	public void testSaveClinicalPatientExisting() throws UUIDException, ClinicalLoaderException {
		final Barcode barcode = new Barcode();
		barcode.setUuid("hi");

		context.checking(new Expectations() {
			{
				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						"justPatientBarcode", null, archive.getTheTumor(),
						archive.getTheCenter(), true);
				will(returnValue(barcode));

				// none of the objects are in the db yet
				one(mockClinicalLoaderQueries).getId(justPatient);
				will(returnValue(100L));
				one(mockClinicalLoaderQueries).update(justPatient, 123, null);
			}
		});

		loader.save(justPatient, ClinicalLoaderCaller.FileType.Clinical,
				archive);
		assertEquals("hi", justPatient.getUuid());
	}

	@Test
	public void testSaveClinicalNew() throws UUIDException, ClinicalLoaderException {
		context.checking(new Expectations() {
			{
				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						"patient-barcode", null, archive.getTheTumor(),
						archive.getTheCenter(), true);
				will(returnValue(makeBarcode("patient-barcode", "patient-uuid")));

				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						"sample-barcode", null, archive.getTheTumor(),
						archive.getTheCenter(), true);
				will(returnValue(makeBarcode("sample-barcode", "sample-uuid")));

				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						"portion-barcode", null, archive.getTheTumor(),
						archive.getTheCenter(), true);
				will(returnValue(makeBarcode("portion-barcode", "portion-uuid")));

				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						"analyte-barcode", null, archive.getTheTumor(),
						archive.getTheCenter(), true);
				will(returnValue(makeBarcode("analyte-barcode", "analyte-uuid")));

				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						"aliquot-barcode", null, archive.getTheTumor(),
						archive.getTheCenter(), true);
				will(returnValue(makeBarcode("aliquot-barcode", "aliquot-uuid")));

				one(mockClinicalLoaderQueries).getId(patient);
				will(returnValue(-1L));
				one(mockClinicalLoaderQueries).insert(patient, -1L, 123, null);
				will(returnValue(1L));

				one(mockClinicalLoaderQueries).getId(sample);
				will(returnValue(-1L));
				one(mockClinicalLoaderQueries).insert(sample, 1L, 123, null);
				will(returnValue(2L));

				one(mockClinicalLoaderQueries).getId(portion);
				will(returnValue(-1L));
				one(mockClinicalLoaderQueries).insert(portion, 2L, 123, null);
				will(returnValue(3L));

				one(mockClinicalLoaderQueries).getId(analyte);
				will(returnValue(-1L));
				one(mockClinicalLoaderQueries).insert(analyte, 3L, 123, null);
				will(returnValue(4L));

				one(mockClinicalLoaderQueries).getId(aliquot);
				will(returnValue(-1L));
				one(mockClinicalLoaderQueries).insert(aliquot, 4L, 123, null);
				will(returnValue(5L));
			}
		});

		loader.save(patient, ClinicalLoaderCaller.FileType.Clinical, archive);
		assertEquals("patient-uuid", patient.getUuid());
		assertEquals("sample-uuid", sample.getUuid());
		assertEquals("portion-uuid", portion.getUuid());
		assertEquals("analyte-uuid", analyte.getUuid());
		assertEquals("aliquot-uuid", aliquot.getUuid());
	}

	private static Barcode makeBarcode(final String barcode, final String uuid) {
		final Barcode barcodeDetail = new Barcode();
		barcodeDetail.setUuid(uuid);
		barcodeDetail.setBarcode(barcode);
		return barcodeDetail;
	}

	@Test
	public void testSaveClinicalExisting() throws UUIDException, ClinicalLoaderException {
		context.checking(new Expectations() {
			{
				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						"patient-barcode", null, archive.getTheTumor(),
						archive.getTheCenter(), true);
				will(returnValue(makeBarcode("patient-barcode", "patient-uuid")));

				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						"sample-barcode", null, archive.getTheTumor(),
						archive.getTheCenter(), true);
				will(returnValue(makeBarcode("sample-barcode", "sample-uuid")));

				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						"portion-barcode", null, archive.getTheTumor(),
						archive.getTheCenter(), true);
				will(returnValue(makeBarcode("portion-barcode", "portion-uuid")));

				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						"analyte-barcode", null, archive.getTheTumor(),
						archive.getTheCenter(), true);
				will(returnValue(makeBarcode("analyte-barcode", "analyte-uuid")));

				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						"aliquot-barcode", null, archive.getTheTumor(),
						archive.getTheCenter(), true);
				will(returnValue(makeBarcode("aliquot-barcode", "aliquot-uuid")));

				one(mockClinicalLoaderQueries).getId(patient);
				will(returnValue(1L));
				one(mockClinicalLoaderQueries).update(patient, 123, null);

				one(mockClinicalLoaderQueries).getId(sample);
				will(returnValue(2L));
				one(mockClinicalLoaderQueries).update(sample, 123, null);

				one(mockClinicalLoaderQueries).getId(portion);
				will(returnValue(3L));
				one(mockClinicalLoaderQueries).update(portion, 123, null);

				one(mockClinicalLoaderQueries).getId(analyte);
				will(returnValue(4L));
				one(mockClinicalLoaderQueries).update(analyte, 123, null);

				one(mockClinicalLoaderQueries).getId(aliquot);
				will(returnValue(5L));
				one(mockClinicalLoaderQueries).update(aliquot, 123, null);
			}
		});

		loader.save(patient, ClinicalLoaderCaller.FileType.Clinical, archive);
		assertEquals("patient-uuid", patient.getUuid());
		assertEquals("sample-uuid", sample.getUuid());
		assertEquals("portion-uuid", portion.getUuid());
		assertEquals("analyte-uuid", analyte.getUuid());
		assertEquals("aliquot-uuid", aliquot.getUuid());
	}

	@Test
	public void testSaveBiospecimenNew() throws UUIDException, ClinicalLoaderException {
        biospecimenOrControlNewTest(ClinicalLoaderCaller.FileType.Biospecimen);
    }

     @Test
    public void testSaveControlNew() throws UUIDException, ClinicalLoaderException {
         biospecimenOrControlNewTest(ClinicalLoaderCaller.FileType.Control);
    }

    private void biospecimenOrControlNewTest(final ClinicalLoaderCaller.FileType fileType) throws UUIDException, ClinicalLoaderException {
         // both biospecimen and control xml files should be treated the same way
        context.checking(new Expectations() {
			{
				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						"patient-barcode", null, archive.getTheTumor(),
						archive.getTheCenter(), true);
				will(returnValue(makeBarcode("patient-barcode", "patient-uuid")));

				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						"sample-barcode", null, archive.getTheTumor(),
						archive.getTheCenter(), true);
				will(returnValue(makeBarcode("sample-barcode", "sample-uuid")));

				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						"portion-barcode", null, archive.getTheTumor(),
						archive.getTheCenter(), true);
				will(returnValue(makeBarcode("portion-barcode", "portion-uuid")));

				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						"analyte-barcode", null, archive.getTheTumor(),
						archive.getTheCenter(), true);
				will(returnValue(makeBarcode("analyte-barcode", "analyte-uuid")));

				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						"aliquot-barcode", null, archive.getTheTumor(),
						archive.getTheCenter(), true);
				will(returnValue(makeBarcode("aliquot-barcode", "aliquot-uuid")));

				one(mockClinicalLoaderQueries).getId(patient);
				will(returnValue(-1L));
				one(mockClinicalLoaderQueries).insert(patient, -1L, 123, null);
				will(returnValue(1L));

				one(mockClinicalLoaderQueries).getId(sample);
				will(returnValue(-1L));
				one(mockClinicalLoaderQueries).insert(sample, 1L, 123, null);
				will(returnValue(2L));

				one(mockClinicalLoaderQueries).getId(portion);
				will(returnValue(-1L));
				one(mockClinicalLoaderQueries).insert(portion, 2L, 123, null);
				will(returnValue(3L));

				one(mockClinicalLoaderQueries).getId(analyte);
				will(returnValue(-1L));
				one(mockClinicalLoaderQueries).insert(analyte, 3L, 123, null);
				will(returnValue(4L));

				one(mockClinicalLoaderQueries).getId(aliquot);
				will(returnValue(-1L));
				one(mockClinicalLoaderQueries).insert(aliquot, 4L, 123, null);
				will(returnValue(5L));
			}
		});

		loader.save(patient, fileType, archive);
		assertEquals("patient-uuid", patient.getUuid());
		assertEquals("sample-uuid", sample.getUuid());
		assertEquals("portion-uuid", portion.getUuid());
		assertEquals("analyte-uuid", analyte.getUuid());
		assertEquals("aliquot-uuid", aliquot.getUuid());
	}

	@Test
	public void testSaveBiospecimenExisting() throws UUIDException, ClinicalLoaderException {
        biospecimenOrControlSaveExisting(ClinicalLoaderCaller.FileType.Biospecimen);
    }

    @Test
    public void testSaveControlExisting() throws UUIDException, ClinicalLoaderException {
        biospecimenOrControlSaveExisting(ClinicalLoaderCaller.FileType.Control);
    }

    private void biospecimenOrControlSaveExisting(final ClinicalLoaderCaller.FileType fileType) throws UUIDException, ClinicalLoaderException {
        // both biospecimen and control xml files should be treated the same way
		context.checking(new Expectations() {
			{
				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						"patient-barcode", null, archive.getTheTumor(),
						archive.getTheCenter(), true);
				will(returnValue(makeBarcode("patient-barcode", "patient-uuid")));

				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						"sample-barcode", null, archive.getTheTumor(),
						archive.getTheCenter(), true);
				will(returnValue(makeBarcode("sample-barcode", "sample-uuid")));

				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						"portion-barcode", null, archive.getTheTumor(),
						archive.getTheCenter(), true);
				will(returnValue(makeBarcode("portion-barcode", "portion-uuid")));

				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						"analyte-barcode", null, archive.getTheTumor(),
						archive.getTheCenter(), true);
				will(returnValue(makeBarcode("analyte-barcode", "analyte-uuid")));

				one(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
						"aliquot-barcode", null, archive.getTheTumor(),
						archive.getTheCenter(), true);
				will(returnValue(makeBarcode("aliquot-barcode", "aliquot-uuid")));

				one(mockClinicalLoaderQueries).getId(patient);
				will(returnValue(1L));

				one(mockClinicalLoaderQueries).addArchiveLink(patient, 123);

				one(mockClinicalLoaderQueries).getId(sample);
				will(returnValue(2L));
				one(mockClinicalLoaderQueries).update(sample, 123, null);

				one(mockClinicalLoaderQueries).getId(portion);
				will(returnValue(3L));
				one(mockClinicalLoaderQueries).update(portion, 123, null);

				one(mockClinicalLoaderQueries).getId(analyte);
				will(returnValue(4L));
				one(mockClinicalLoaderQueries).update(analyte, 123, null);

				one(mockClinicalLoaderQueries).getId(aliquot);
				will(returnValue(5L));
				one(mockClinicalLoaderQueries).update(aliquot, 123, null);
			}
		});

		loader.save(patient, fileType, archive);
	}

    @Test
    public void testParseControlXml() throws ClinicalLoaderException {

        context.checking(new Expectations() {{
            allowing(mockClinicalLoaderQueries).elementRepresentsClinicalTable(with(any(String.class)));
            will(returnTrueIfClinicalTable());
        }});

        final ClinicalObject patient = loader.parseControlXmlFile(controlXmlFile, archive);
        assertNotNull(patient);
        assertEquals(3, patient.getChildren().size());
        final ClinicalObject control = patient.getChildren().get(2);
        assertEquals("control", control.getObjectType());
        assertEquals(4, control.getAttributeNames().size());
        assertEquals("cell_line_control", control.getValue("control_element"));
        assertEquals("GBM BLCA LUSC SARC", control.getValue("disease_code_list"));
        assertEquals("string", control.getValue("bcr_aliquot_barcode"));
        assertEquals("string", control.getValue("bcr_aliquot_uuid"));
    }

	@Test
	public void testParseBiospecimenXml() throws ClinicalLoaderException {
		// The code below checks to make sure the structure of the objects
		// matches what is expected based on the XML

		final ClinicalObject patient = loader.parseBiospecimenXmlFile(
				biospecimenXmlFile, archive);

		assertNotNull(patient);
		assertEquals(PATIENT_BARCODE, patient.getBarcode());
		assertEquals("patient", patient.getObjectType());
		assertEquals(2, patient.getChildren().size());
		assertEquals(archive, patient.getArchive());
        assertEquals(null, patient.getDynamicIdentifier());

		final ClinicalObject sample = patient.getChildren().get(0);
		assertEquals("sample", sample.getObjectType());
		assertEquals("sample-1", sample.getBarcode());
		assertEquals("tumor", sample.getValue("sample_type"));
		assertEquals("10 mg", sample.getValue("weight"));
		assertEquals(2, sample.getChildren().size());
		assertEquals(archive, sample.getArchive());
        assertEquals(null, sample.getDynamicIdentifier());

		final ClinicalObject portion = sample.getChildren().get(0);
		assertEquals("portion", portion.getObjectType());
		assertEquals("portion-1-1", portion.getBarcode());
		assertEquals("A", portion.getValue("portion_number"));
		assertEquals(3, portion.getChildren().size());
		assertEquals(archive, portion.getArchive());
        assertEquals(null, portion.getDynamicIdentifier());

		final ClinicalObject analyte1 = portion.getChildren().get(0);
		final ClinicalObject analyte2 = portion.getChildren().get(1);
		final ClinicalObject slide = portion.getChildren().get(2);

		assertEquals("analyte", analyte1.getObjectType());
		assertEquals("analyte-1-1-1", analyte1.getBarcode());
		assertEquals("DNA", analyte1.getValue("analyte_type"));
		assertEquals(4, analyte1.getChildren().size());
		// analyte1's first child
		assertEquals("aliquot", analyte1.getChildren().get(0).getObjectType());
		assertEquals("aliquot-1-1-1-1", analyte1.getChildren().get(0)
				.getBarcode());
		assertEquals("1 uL", analyte1.getChildren().get(0).getValue("volume"));
		assertEquals(0, analyte1.getChildren().get(0).getChildren().size());
		// analyte1's second child
		assertEquals("aliquot", analyte1.getChildren().get(1).getObjectType());
		assertEquals("aliquot-1-1-1-2", analyte1.getChildren().get(1)
				.getBarcode());
		assertEquals("1.5 uL", analyte1.getChildren().get(1).getValue("volume"));
		assertEquals(0, analyte1.getChildren().get(1).getChildren().size());
		// analyte1's third child
		assertEquals("protocol", analyte1.getChildren().get(2).getObjectType());
		assertNull(analyte1.getChildren().get(2).getBarcode());
		assertEquals("Fred",
				analyte1.getChildren().get(2).getValue("protocol_name"));
		assertEquals(0, analyte1.getChildren().get(2).getChildren().size());
		// analyte1's fourth child
		assertEquals("dna", analyte1.getChildren().get(3).getObjectType());
		assertNull(analyte1.getChildren().get(3).getBarcode());
		assertEquals("YES",
				analyte1.getChildren().get(3).getValue("pcr_successful"));
		assertEquals(0, analyte1.getChildren().get(3).getChildren().size());
        assertEquals(null, analyte1.getDynamicIdentifier());

		assertEquals("analyte", analyte2.getObjectType());
		assertEquals("analyte-1-1-2", analyte2.getBarcode());
		assertEquals("RNA", analyte2.getValue("analyte_type"));
		assertEquals(2, analyte2.getChildren().size());
		// analyte2's first child
		assertEquals("aliquot", analyte2.getChildren().get(0).getObjectType());
		assertEquals("aliquot-1-1-2-1", analyte2.getChildren().get(0)
				.getBarcode());
		assertEquals("2 uL", analyte2.getChildren().get(0).getValue("volume"));
		assertEquals(0, analyte2.getChildren().get(0).getChildren().size());
		// analyte2's second child
		assertEquals("rna", analyte2.getChildren().get(1).getObjectType());
		assertEquals("8.2", analyte2.getChildren().get(1).getValue("rinvalue"));
		assertNull(analyte2.getChildren().get(1).getBarcode());
		assertEquals(0, analyte2.getChildren().get(1).getChildren().size());
        assertEquals(null, analyte2.getDynamicIdentifier());

		assertEquals("slide", slide.getObjectType());
		assertEquals("slide-1-1-1", slide.getBarcode());
		assertEquals("top", slide.getValue("section_location"));
        assertEquals(null, slide.getDynamicIdentifier());

		final ClinicalObject tumorPathology = sample.getChildren().get(1);
		assertEquals("tumor_pathology", tumorPathology.getObjectType());
		assertNull(tumorPathology.getBarcode());
		assertEquals("NO", tumorPathology.getValue("lymphatic_invasion"));
		assertEquals(0, tumorPathology.getChildren().size());
        assertEquals(null, tumorPathology.getDynamicIdentifier());

        final ClinicalObject followUp = patient.getChildren().get(1);
        assertEquals("follow_up", followUp.getObjectType());
        assertEquals("31", followUp.getValue("age"));
        assertEquals("alive", followUp.getValue("status"));
        assertEquals("follow_up_v2.0", followUp.getDynamicIdentifier());
	}

	@Test
	public void testParseClinicalXmlFile() throws ClinicalLoaderException {
		final File testXmlFile = new File(SAMPLE_DIRECTORY_LOCATION
				+ "fake_TEST.bio.Level_1.1.2.0/testFile_Clinical.xml");

		final ClinicalObject patient = loader.parseClinicalXmlFile(testXmlFile,
				archive);
		assertNotNull(patient);
		assertEquals("patient-A", patient.getBarcode());
		assertEquals("patient", patient.getObjectType());
		assertEquals("FEMALE", patient.getValue("gender"));
		assertEquals("123", patient.getValue("age_at_diagnosis"));
		assertEquals("ALIVE", patient.getValue("vital_status"));
		assertEquals(4, patient.getChildren().size());
		assertEquals(archive, patient.getArchive());
        assertEquals(null, patient.getDynamicIdentifier());

		// child 1 is drug
		final ClinicalObject drug = patient.getChildren().get(0);
		assertEquals("drug", drug.getObjectType());
		assertEquals("drug-1", drug.getBarcode());
		assertEquals("chemotherapy", drug.getValue("drug_type"));
		assertEquals(0, drug.getChildren().size());
		assertEquals(archive, drug.getArchive());
        assertEquals(null, drug.getDynamicIdentifier());

		// child 2 is surgery1
		final ClinicalObject surgery1 = patient.getChildren().get(1);
		assertEquals("surgery", surgery1.getObjectType());
		assertEquals("surgery-1", surgery1.getBarcode());
		assertEquals("0", surgery1.getValue("days_to_surgery"));
		assertEquals(0, surgery1.getChildren().size());
        assertEquals(null, surgery1.getDynamicIdentifier());

		// child 3 is surgery2
		final ClinicalObject surgery2 = patient.getChildren().get(2);
		assertEquals("surgery", surgery2.getObjectType());
		assertEquals("surgery-2", surgery2.getBarcode());
		assertEquals("456", surgery2.getValue("days_to_surgery"));
		assertEquals(0, surgery2.getChildren().size());
        assertEquals(null, surgery2.getDynamicIdentifier());

		// child 4 is radiation
		final ClinicalObject radiation = patient.getChildren().get(3);
		assertEquals("radiation", radiation.getObjectType());
		assertEquals("radiation-1", radiation.getBarcode());
		assertEquals("4", radiation.getValue("days_to_radiation_start"));
		assertEquals("60", radiation.getValue("days_to_radiation_end"));
		assertEquals(0, radiation.getChildren().size());
        assertEquals(null, radiation.getDynamicIdentifier());

		// test multiple values
		final Collection<String> attributes = patient.getAttributeNames();
		assertTrue(attributes != null && attributes.size() == 6);
		final String attributeValue = patient
				.getValue("loss_expression_of_mismatch_repair_proteins_by_ihc_result");
		assertEquals(attributeValue, "value1,value2,value3");

		// test single value
		final String attributeValue2 = patient
				.getValue("number_of_first_degree_relatives_with_cancer_diagnosis");
		assertEquals(attributeValue2, "0");
	}

	@Test(expected = ClinicalLoaderException.class)
	public void testParseXmlWithNoPatient() throws ClinicalLoaderException {
		loader.parseClinicalXmlFile(new File(SAMPLE_DIRECTORY_LOCATION + "bad.xml"), archive);
	}

	@Test(expected = ClinicalLoaderException.class)
	public void testParseXmlWithTwoPatients() throws ClinicalLoaderException {
		loader.parseBiospecimenXmlFile(new File(SAMPLE_DIRECTORY_LOCATION + "twoPatients.xml"), archive);
	}

    @Test
    public void testLoadArchive() throws ClinicalLoaderException, UUIDException {

        archive.setId(567L);
        archive.setDeployLocation(SAMPLE_DIRECTORY_LOCATION
                + "good_TEST.bio.Level_1.1.2.0.tar.gz");

        context.checking(new Expectations() {{
            allowing(mockClinicalLoaderQueries).elementRepresentsClinicalTable(with(any(String.class)));
            will(returnTrueIfClinicalTable());

            // 14 objects need UUIDs
            exactly(14).of(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
                    with(any(String.class)), with(any(String.class)),
                    with(archive.getTheTumor()),
                    with(archive.getTheCenter()), with(true));
            will(returnValue(makeBarcode("", "some-fake-uuid")));

            allowing(mockClinicalLoaderQueries).clinicalXsdElementExists(
                    with(any(String.class)));
            will(returnValue(true));
            allowing(mockClinicalLoaderQueries).getId(
                    with(any(ClinicalObject.class)));
            will(returnValue(-1L));

            // there are 18 objects to be inserted in the 2 files in this
            // pretend archive
            exactly(18).of(mockClinicalLoaderQueries).insert(
                    with(any(ClinicalObject.class)), with(any(Long.class)),
                    with(567L), with(any(List.class)));
            will(returnValue(1L));
        }});

        addMockBcrUtilsExpectation(false, false);
        loader.loadArchive(archive);
    }

    private Action returnTrueIfClinicalTable() {
        return new Action() {
            @Override
            public Object invoke(final Invocation invocation) throws Throwable {
                final String elementName = (String) invocation.getParameter(0);
                return ALLOWED_ELEMENT_TYPES.contains(elementName);
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("checks parameter against ALLOWED_ELEMENT_TYPES list");
            }
        };
    }

    @Test
    public void testLoadArchiveWithControl()
            throws ClinicalLoaderException, UUIDException {

        archive.setId(567L);
        archive.setDeployLocation(SAMPLE_DIRECTORY_LOCATION
                + "test_CNTL.bio.Level_1.0.10.0.tar.gz");

        context.checking(new Expectations() {{
            allowing(mockClinicalLoaderQueries).elementRepresentsClinicalTable(with(any(String.class)));
            will(returnTrueIfClinicalTable());

            exactly(21).of(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
                    with(any(String.class)), with(any(String.class)),
                    with(archive.getTheTumor()),
                    with(archive.getTheCenter()), with(true));
            will(returnValue(makeBarcode("", "some-fake-uuid")));

            allowing(mockClinicalLoaderQueries).clinicalXsdElementExists(
                    with(any(String.class)));
            will(returnValue(true));
            allowing(mockClinicalLoaderQueries).getId(
                    with(any(ClinicalObject.class)));
            will(returnValue(-1L));

            exactly(35).of(mockClinicalLoaderQueries).insert(
                    with(any(ClinicalObject.class)), with(any(Long.class)),
                    with(567L), with(any(List.class)));
            will(returnValue(1L));
        }});

        addMockBcrUtilsExpectation(false, true);
        loader.loadArchive(archive);

    }

    @Test
    public void testLoadArchiveWithAuxiliary() throws ClinicalLoaderException, UUIDException {

        archive.setId(567L);
        archive.setDeployLocation(SAMPLE_DIRECTORY_LOCATION + "good_TEST.bio.Level_1.1.2.0.tar.gz");

        addMockBcrUtilsExpectation(true, false);

        loader.loadArchive(archive);
    }

	@Test
	public void testCheckDiseaseContext() throws ClinicalLoaderException,
			UUIDException {
		archive.setArchiveFile(new File(SAMPLE_DIRECTORY_LOCATION
				+ "good_TEST.bio.Level_1.1.2.0.tar.gz"));
		archive.setId(567L);
		archive.setDeployLocation(SAMPLE_DIRECTORY_LOCATION
				+ "good_TEST.bio.Level_1.1.2.0.tar.gz");

        context.checking(new Expectations() {{
            allowing(mockClinicalLoaderQueries).getId(
                    with(any(ClinicalObject.class)));
            will(returnValue(-1L));
            allowing(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
                    with(any(String.class)), with(any(String.class)),
                    with(any(Tumor.class)), with(any(Center.class)),
                    with(true));
            will(returnValue(makeBarcode("barcode", "I am not a real UUID")));

            allowing(mockClinicalLoaderQueries).insert(
                    with(any(ClinicalObject.class)), with(any(Long.class)),
                    with(567L), with(any(List.class)));
            will(returnValue(1L));
            allowing(mockArchiveQueries).getArchiveIdByName(
                    "good_TEST.bio.Level_1.1.2.0");
            will(returnValue(567L));
            allowing(mockArchiveQueries).getArchive(567);
            will(returnValue(archive));

            allowing(mockArchiveLogger).addArchiveLog(archive,
                    " archive loading completed");
            allowing(mockPlatformTxManager).getTransaction(
                    with(any(TransactionDefinition.class)));
            allowing(mockPlatformTxManager).commit(
                    with(any(TransactionStatus.class)));
            allowing(mockArchiveLogger).addTransactionLog("", 1l);
            allowing(mockArchiveLogger).updateTransactionLogRecordResult(
                    1l, "", true);
            allowing(mockArchiveLogger).endTransaction(1l, true);
            allowing(mockArchiveLogger).addTransactionLog("", 1l);
        }});

        addMockBcrUtilsExpectation(false, false);
        
		final List<Archive> archivesToLoad = new ArrayList<Archive>();
		archivesToLoad.add(archive);
		loader.load(archivesToLoad, stateContex);
		assertTrue("TEST".equals(DiseaseContextHolder.getDisease()));
	}

	@Test(expected = ClinicalLoaderException.class)
	public void testLoadArchiveBadFilename() throws ClinicalLoaderException {
		final Archive archive = new Archive();
		archive.setDeployLocation(SAMPLE_DIRECTORY_LOCATION
				+ "fake_TEST.bio.Level_1.1.3.0.tar.gz");
		loader.loadArchive(archive);
	}

	@Test
	public void testLoadArchiveNoFiles() throws ClinicalLoaderException {

		final ClinicalLoaderCaller caller = new ClinicalLoaderCaller() {
			protected File[] getArchiveXmlFiles(final Archive archive) {
				return null;
			}
		};

		caller.setLogger(mockLogger);
		caller.setValidClinicalPlatforms("bio");
		archive.setRealName("filelessArchive");

		context.checking(new Expectations() {
			{
				one(mockLogger)
						.log(Level.ERROR,
								"Archive filelessArchive has no XML files (path = null)");
			}
		});
		// expect nothing to be called because no files
		archive.setDeployStatus(Archive.STATUS_AVAILABLE);
		caller.loadArchive(archive);
	}

	@Test(expected = ClinicalLoaderException.class)
	public void testOldStyleXml() throws ClinicalLoaderException {
		// create test files that don't have 'patient' node
		final Archive archive = new Archive();
		archive.setDeployLocation(SAMPLE_DIRECTORY_LOCATION
				+ "old_xml.bio.Level_1.1.0.0.tar.gz");
		loader.loadArchive(archive);
		fail("Should have thrown an exception before this point");
	}

	@Test(expected = ClinicalLoaderException.class)
	public void testFailLoad() throws ClinicalLoaderException, UUIDException {
		final Archive archiveFailLoad = new Archive(SAMPLE_DIRECTORY_LOCATION
				+ "squirrel.tar.gz");
		archiveFailLoad.setRealName("squirrel");
		archiveFailLoad.setId(123L);
		archiveFailLoad.setExperimentType(Experiment.TYPE_BCR);
		archiveFailLoad.setPlatform("bio");
		archiveFailLoad.setDeployStatus(Archive.STATUS_AVAILABLE);
		final Center archiveCenter = new Center();
		archiveCenter.setCenterId(CENTER_ID);
		archiveFailLoad.setTheCenter(archiveCenter);
		final Tumor theTumor = new Tumor();
		theTumor.setTumorName("TEST");
		archiveFailLoad.setTheTumor(theTumor);
		archiveFailLoad.setDeployLocation(SAMPLE_DIRECTORY_LOCATION
				+ "fake_TEST.bio.Level_1.1.2.0.tar.gz");

        context.checking(new Expectations() {{
            one(mockArchiveQueries).getArchiveIdByName("squirrel");
            will(returnValue(123L));
            one(mockArchiveQueries).getArchive(123L);
            will(returnValue(archiveFailLoad));

            allowing(mockClinicalLoaderQueries).getId(
                    with(any(ClinicalObject.class)));
            will(returnValue(-1L));
            allowing(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
                    with(any(String.class)), with(any(String.class)),
                    with(any(Tumor.class)), with(any(Center.class)),
                    with(true));
            will(throwException(new UUIDException("oops!")));
            //noinspection ThrowableResultOfMethodCallIgnored
            allowing(mockLogger).log(with(any(Exception.class)));
            allowing(mockPlatformTxManager).getTransaction(
                    with(any(TransactionDefinition.class)));
            allowing(mockPlatformTxManager).rollback(
                    with(any(TransactionStatus.class)));
            allowing(mockArchiveLogger).addTransactionLog("", 1L);
            allowing(mockArchiveLogger).endTransaction(1l, false);
            allowing(mockArchiveLogger).addErrorMessage(1l, "squirrel",
                    "oops!");
        }});

        addMockBcrUtilsExpectation(false, false);

		final List<Archive> archiveList = new ArrayList<Archive>();
		archiveList.add(archiveFailLoad);
		loader.load(archiveList, stateContex);
	}

	@Test
	public void testGetLoaderType() {
		assertEquals(loader.getLoaderType(),
				ArchiveLoader.ArchiveLoaderType.CLINICAL_LOADER);
	}

	@Test
	public void testLoadArchiveByName() throws ClinicalLoaderException,
			UUIDException {
		archive.setDeployLocation(SAMPLE_DIRECTORY_LOCATION
				+ "fake_TEST.bio.Level_1.1.2.0.tar.gz");

        context.checking(new Expectations() {{
            // 14 objects need UUIDs
            exactly(14).of(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
                    with(any(String.class)), with(any(String.class)),
                    with(archive.getTheTumor()),
                    with(archive.getTheCenter()), with(true));
            will(returnValue(makeBarcode("", "some-fake-uuid")));

            allowing(mockClinicalLoaderQueries).clinicalXsdElementExists(
                    with(any(String.class)));
            will(returnValue(true));
            one(mockArchiveQueries).getArchiveIdByName("squirrel");
            will(returnValue(123L));
            one(mockArchiveQueries).getArchive(123L);
            will(returnValue(archive));

            allowing(mockLogger).log(Level.INFO,
                    "About to load thisIsAnArchiveName");
            allowing(mockLogger).log(Level.INFO,
                    "Finished loading thisIsAnArchiveName");

            allowing(mockClinicalLoaderQueries).getId(
                    with(any(ClinicalObject.class)));
            will(returnValue(-1L));

            // there are 19 objects to be inserted in the 2 files in this
            // pretend archive
            exactly(19).of(mockClinicalLoaderQueries).insert(
                    with(any(ClinicalObject.class)), with(any(Long.class)),
                    with(123L), with(any(List.class)));
            will(returnValue(1L));
            allowing(mockPlatformTxManager).getTransaction(
                    with(any(TransactionDefinition.class)));
            allowing(mockPlatformTxManager).commit(
                    with(any(TransactionStatus.class)));
        }});

        addMockBcrUtilsExpectation(false, false);
        
		loader.loadArchiveByName("squirrel", stateContex);
	}

	@Test(expected = ClinicalLoaderException.class)
	public void testLoadArchiveWrongType() throws ClinicalLoaderException {
		archive.setExperimentType(Experiment.TYPE_CGCC);
		loader.loadArchive(archive);
	}

	@Test(expected = ClinicalLoaderException.class)
	public void testLoadArchiveNotBio() throws ClinicalLoaderException {
		archive.setExperimentType(Experiment.TYPE_BCR);
		archive.setPlatform("tissue_images");
		loader.loadArchive(archive);
	}

	@Test(expected = ClinicalLoaderException.class)
	public void testLoadArchiveNotAvailable() throws ClinicalLoaderException {
		archive.setExperimentType(Experiment.TYPE_BCR);
		archive.setPlatform("bio");
		archive.setDeployStatus(Archive.STATUS_UPLOADED);
		loader.loadArchive(archive);
	}

	@Test
	public void testLoadWithNullTransactionId() throws ClinicalLoaderException,
			UUIDException {
		final List<Archive> archivesToLoad = new ArrayList<Archive>();
		archivesToLoad.add(archive);
		archive.setArchiveFile(new File(SAMPLE_DIRECTORY_LOCATION
				+ "good_TEST.bio.Level_1.1.2.0.tar.gz"));
		archive.setId(567L);
		archive.setDeployLocation(SAMPLE_DIRECTORY_LOCATION
				+ "good_TEST.bio.Level_1.1.2.0.tar.gz");

		final QcLiveStateBean state = new QcLiveStateBean();
		state.setTransactionId(null);

        context.checking(new Expectations() {{
            allowing(mockClinicalLoaderQueries).getId(
                    with(any(ClinicalObject.class)));
            will(returnValue(-1L));
            allowing(mockBarcodeUuidResolver).resolveBarcodeAndUuid(
                    with(any(String.class)), with(any(String.class)),
                    with(any(Tumor.class)), with(any(Center.class)),
                    with(true));
            will(returnValue(makeBarcode("barcode", "I am not a real UUID")));

            allowing(mockClinicalLoaderQueries).insert(
                    with(any(ClinicalObject.class)), with(any(Long.class)),
                    with(567L), with(any(List.class)));
            will(returnValue(1L));
            allowing(mockArchiveQueries).getArchiveIdByName(
                    "good_TEST.bio.Level_1.1.2.0");
            will(returnValue(567L));
            allowing(mockArchiveQueries).getArchive(567);
            will(returnValue(archive));

            allowing(mockPlatformTxManager).getTransaction(
                    with(any(TransactionDefinition.class)));
            allowing(mockPlatformTxManager).commit(
                    with(any(TransactionStatus.class)));
        }});

        addMockBcrUtilsExpectation(false, false);

		loader.load(archivesToLoad, state);
	}

	public static Matcher<Barcode> barcode(final String theBarcode) {
		return new TypeSafeMatcher<Barcode>() {

			@Override
			public boolean matchesSafely(final Barcode barcode) {
				return barcode.getBarcode().equals(theBarcode);
			}

			public void describeTo(final Description description) {
				description.appendText("barcode matches barcode");
			}
		};
	}

	private Action returnClinicalTable() {
		return new Action() {

			public Object invoke(final Invocation invocation) throws Throwable {
				final String elementName = (String) invocation.getParameter(0);
				if (ALLOWED_ELEMENT_TYPES.contains(elementName)) {
					final ClinicalTable clinicalTable = new ClinicalTable();
					clinicalTable.setBarcodeElementName("bcr_" + elementName
							+ "_barcode");
					return clinicalTable;
				} else {
					return null;
				}
			}

			public void describeTo(final Description description) {
				description
						.appendText("makes a ClinicalObject of the type given as the first parameter");
			}
		};
	}

    /**
     * Add an expectation for the call of methods on mockBcrUtils
     *
     * @param isAuxiliaryFileReturnValue the return value for isAuxiliaryFile() call
     * @param isControlFileReturnValue   the return value for isControlFile() call
     */
    private void addMockBcrUtilsExpectation(final Boolean isAuxiliaryFileReturnValue,
                                            final Boolean isControlFileReturnValue) {

        context.checking(new Expectations() {{

            if (isAuxiliaryFileReturnValue != null) {
                allowing(mockBcrUtils).isAuxiliaryFile(with(any(File.class)));
                will(returnValue(isAuxiliaryFileReturnValue));
            }

            if (isControlFileReturnValue != null) {
                allowing(mockBcrUtils).isControlFile(with(any(File.class)));
                will(returnValue(isControlFileReturnValue));
            }
        }});
    }
}
