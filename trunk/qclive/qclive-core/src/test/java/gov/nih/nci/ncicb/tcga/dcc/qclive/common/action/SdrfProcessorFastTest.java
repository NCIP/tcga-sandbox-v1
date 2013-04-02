/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotation;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationCategory;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.DccAnnotationItemType;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Tumor;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ArchiveQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.FileInfoQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ShippedBiospecimenQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.annotations.AnnotationQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.BeanException;
import gov.nih.nci.ncicb.tcga.dcc.common.exception.UUIDException;
import gov.nih.nci.ncicb.tcga.dcc.common.service.UUIDService;
import gov.nih.nci.ncicb.tcga.dcc.common.service.annotations.AnnotationService;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidatorImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContentImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.BCRID;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedFileParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BCRIDProcessor;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * Test class for SdrfProcessor
 * 
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class SdrfProcessorFastTest {

	private static final String PROTEIN_ARCHIVE_NAME_LEVEL_1 = "mdanderson.org_OV.MDA_RPPA_Core.Level_1.1.0.0";
	private static final String PROTEIN_ARCHIVE_NAME_MAGE_TAB = "mdanderson.org_OV.MDA_RPPA_Core.mage-tab.1.0.0";
	private static final String PROTEIN_ARCHIVE_NAME_LEVEL_2 = "mdanderson.org_OV.MDA_RPPA_Core.Level_2.1.0.0";
	private static final String PROTEIN_ARCHIVE_NAME_LEVEL_3 = "mdanderson.org_OV.MDA_RPPA_Core.Level_3.1.0.0";

	private final static long proteinMageTabArchiveId = 111L;
	private final static long proteinLevel1ArchiveId = 123L;
	private final static long proteinLevel2ArchiveId = 456L;
	private final static long proteinLevel3ArchiveId = 789L;

	private Mockery context = new JUnit4Mockery();
	private BCRIDProcessor bcrIdProcessor = context.mock(BCRIDProcessor.class,
			"bcrIdProcessor_1");
	private BCRIDProcessor diseaseBcrIdProcessor = context.mock(
			BCRIDProcessor.class, "bcrIdProcessor_2");
	private ArchiveQueries archiveQueries = context.mock(ArchiveQueries.class);
	private FileInfoQueries fileInfoQueries = context
			.mock(FileInfoQueries.class);
	private ShippedBiospecimenQueries commonShippedBiospecimenQueries = context
			.mock(ShippedBiospecimenQueries.class,
					"commonShippedBiospecimenQueries");
	private ShippedBiospecimenQueries diseaseShippedBiospecimenQueries = context
			.mock(ShippedBiospecimenQueries.class,
					"diseaseShippedBiospecimenQueries");
	private Processor<Archive, Archive> mockDroppedBarcodeFinder = (Processor<Archive, Archive>) context
			.mock(Processor.class);
    private UUIDService mockUuidService;
	private SdrfProcessor processor = new SdrfProcessor();
	private Tumor disease;
	private Archive mageTabArchive, archive1, archive2, archiveProteinSdrf,
			archiveNullPlatform;
	private QcContext qcContext;
	private static final String SAMPLES_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	private String SDRF_DIR = SAMPLES_DIR + "qclive" + File.separator
			+ "sdrfProcessor" + File.separator;

	private String testSDRF = SDRF_DIR + "test-sdrf.txt";
	private String uuidSDRF = SDRF_DIR + "uuid-sdrf.txt";
	private String scenario1Sdrf = SDRF_DIR + "scenario_1.sdrf.txt";
	private String scenario2Sdrf = SDRF_DIR + "scenario_2.sdrf.txt";
	private String scenario5Sdrf = SDRF_DIR + "scenario_5.sdrf.txt";

	private AnnotationService mockAnnotationsService = context
			.mock(AnnotationService.class);

	@Before
	public void setup() throws IOException,ParseException {
		processor.setBcrIdProcessor(bcrIdProcessor);
		processor.setDiseaseBcrIdProcessor(diseaseBcrIdProcessor);
		processor.setArchiveQueries(archiveQueries);
		processor.setFileInfoQueries(fileInfoQueries);
		processor.setDroppedBarcodeFinder(mockDroppedBarcodeFinder);
		processor.setAnnotationService(mockAnnotationsService);
		processor
				.setCommonShippedBiospecimenQueries(commonShippedBiospecimenQueries);
		processor
				.setDiseaseShippedBiospecimenQueries(diseaseShippedBiospecimenQueries);

		TabDelimitedContent sdrf = new TabDelimitedContentImpl();
		TabDelimitedFileParser parser = new TabDelimitedFileParser();
		parser.setTabDelimitedContent(sdrf);
		parser.initialize(testSDRF);
		disease = new Tumor();
		disease.setTumorId(1);
		mageTabArchive = new Archive();
		mageTabArchive.setArchiveType(Archive.TYPE_MAGE_TAB);
		mageTabArchive.setSdrf(sdrf);
		mageTabArchive.setTheTumor(disease);
		mageTabArchive.setRealName("test.mage-tab.1.0.0");
		mageTabArchive.setPlatform("some_platform");
		qcContext = new QcContext();
		qcContext.setArchive(mageTabArchive);

		Experiment experiment = new Experiment();
		experiment.addArchive(mageTabArchive);
		archive1 = new Archive();
		archive1.setArchiveType(Archive.TYPE_LEVEL_1);
		archive1.setSerialIndex("1");
		Archive oldArchive1 = new Archive();
		oldArchive1.setArchiveType(Archive.TYPE_LEVEL_1);
		oldArchive1.setSerialIndex("1");
		archive2 = new Archive();
		archive2.setArchiveType(Archive.TYPE_LEVEL_2);
		archive2.setSerialIndex("2");
		archiveProteinSdrf = new Archive();
		archiveProteinSdrf
				.setPlatform(SdrfProcessor.PROTEIN_SDRF_PLATFORM_CODE);
		archiveNullPlatform = new Archive();
		experiment.addArchive(archive1);
		experiment.addArchive(archive2);
		experiment.addPreviousArchive(oldArchive1);

		qcContext.setExperiment(experiment);

		processor
				.setBarcodeAndUUIDValidator(new CommonBarcodeAndUUIDValidatorImpl());
        mockUuidService = context.mock(UUIDService.class);
        processor.setUuidService(mockUuidService);
	}

	@Test
	public void testExecute() throws ParseException,
			Processor.ProcessorException, UUIDException,
			AnnotationQueries.AnnotationQueriesException, BeanException {
		final int[] bcrAndBcrFileId = { -1, -1 };
		final BCRID BcridA = new BCRID();
		BcridA.setFullID("TCGA-extractA");
		final BCRID BcridB = new BCRID();
		BcridA.setFullID("TCGA-extractB");
		final BCRID BcridC = new BCRID();
		BcridA.setFullID("TCGA-extractC");
		final DccAnnotation pretendDNUAnnotation = new DccAnnotation();
		sdrfCommonExpectations();
		context.checking(new Expectations() {
			{
				allowing(bcrIdProcessor).parseAliquotBarcode("TCGA-extractA");
				will(returnValue(BcridA));
				allowing(bcrIdProcessor).parseAliquotBarcode("TCGA-extractB");
				will(returnValue(BcridB));
				allowing(bcrIdProcessor).parseAliquotBarcode("TCGA-extractC");
				will(returnValue(BcridC));
				// add each association once
				one(bcrIdProcessor).addFileAssociation(10L, BcridA,
						"Array Data File", 1L, false, bcrAndBcrFileId, disease);
				one(bcrIdProcessor).addFileAssociation(11L, BcridB,
						"Array Data File", 1L, false, bcrAndBcrFileId, disease);
				one(bcrIdProcessor).addFileAssociation(20L, BcridA,
						"Derived Array Data File", 2L, false, bcrAndBcrFileId,
						disease);
				one(bcrIdProcessor).addFileAssociation(21L, BcridB,
						"Derived Array Data File", 2L, false, bcrAndBcrFileId,
						disease);
				one(bcrIdProcessor).addFileAssociation(12L, BcridC,
						"Array Data File", 1L, false, bcrAndBcrFileId, disease);
				one(diseaseBcrIdProcessor).addFileAssociation(10L, BcridA,
						"Array Data File", 1L, true, bcrAndBcrFileId, disease);
				one(diseaseBcrIdProcessor).addFileAssociation(11L, BcridB,
						"Array Data File", 1L, true, bcrAndBcrFileId, disease);
				one(diseaseBcrIdProcessor).addFileAssociation(20L, BcridA,
						"Derived Array Data File", 2l, true, bcrAndBcrFileId,
						disease);
				one(diseaseBcrIdProcessor).addFileAssociation(21L, BcridB,
						"Derived Array Data File", 2L, true, bcrAndBcrFileId,
						disease);
				one(diseaseBcrIdProcessor).addFileAssociation(12L, BcridC,
						"Array Data File", 1L, true, bcrAndBcrFileId, disease);

				// note extractC / file fileC.2 not added because include for
				// analysis is "no"
				// but an annotation will be added!
				one(mockAnnotationsService)
						.addAnnotation(
								1,
								DccAnnotationItemType.ALIQUOT_TYPE_ID,
								"TCGA-extractC",
								DccAnnotationCategory.DCC_ANNOTATION_DNU_ID,
								"SDRF in test.mage-tab.1.0.0 flagged aliquot to be excluded for analysis based on file 'fileC.2'.",
								"DCC");
				will(returnValue(pretendDNUAnnotation));
				one(mockAnnotationsService).curate(pretendDNUAnnotation);
			}
		});

		processor.execute(mageTabArchive, qcContext);
		context.assertIsSatisfied();
	}

	@Test(expected = Processor.ProcessorException.class)
	public void testDatabaseException() throws Processor.ProcessorException {
		context.checking(new Expectations() {
			{
				one(archiveQueries).getArchiveIdByName("archive1");
				// noinspection ThrowableInstanceNeverThrown
				will(throwException(new DataIntegrityViolationException(
						"oopsie daisies")));
			}
		});
		QcContext qcContext = new QcContext();
		qcContext.setArchive(mageTabArchive);
		processor.execute(mageTabArchive, qcContext);
	}

	@Test
	public void testAnnotationsError() throws Exception {
        testWithAnnotationsError("blah");
        assertEquals(1, qcContext.getWarningCount());
        assertEquals("Failed to add 'Do Not Use' annotation for 'TCGA-extractC'\t[test.mage-tab.1.0.0]", qcContext.getWarnings().get(0));
    }

    @Test
    public void testAnnotationsErrorDuplicate() throws Exception {
        testWithAnnotationsError("The following annotation is not unique: something");
        // no warnings for this kind of exception
        assertEquals(0, qcContext.getWarningCount());
        assertEquals(0, qcContext.getErrorCount());
    }

    private void testWithAnnotationsError(final String annotationExceptionMessage) throws Exception {
		final int[] bcrAndBcrFileId = { -1, -1 };
		final BCRID BcridA = new BCRID();
		BcridA.setFullID("TCGA-extractA");
		final BCRID BcridB = new BCRID();
		BcridA.setFullID("TCGA-extractB");
		final BCRID BcridC = new BCRID();
		BcridA.setFullID("TCGA-extractC");

		context.checking(new Expectations() {
			{

				// get archive1 id once for each association
				one(archiveQueries).getArchiveIdByName("archive1");
				will(returnValue(1L));
				// get archive2 id once for each association
				one(archiveQueries).getArchiveIdByName("archive2");
				will(returnValue(2L));
				// get each file id once
				one(fileInfoQueries).getFileId("fileA.1", 1L);
				will(returnValue(10L));
				one(fileInfoQueries).getFileId("fileB.1", 1L);
				will(returnValue(11L));
				one(fileInfoQueries).getFileId("fileA.2", 2L);
				will(returnValue(20L));
				one(fileInfoQueries).getFileId("fileB.2", 2L);
				will(returnValue(21L));
				one(fileInfoQueries).getFileId("fileC.1", 1L);
				will(returnValue(12L));
				allowing(bcrIdProcessor).parseAliquotBarcode("TCGA-extractA");
				will(returnValue(BcridA));
				allowing(bcrIdProcessor).parseAliquotBarcode("TCGA-extractB");
				will(returnValue(BcridB));
				allowing(bcrIdProcessor).parseAliquotBarcode("TCGA-extractC");
				will(returnValue(BcridC));
				// add each association once
				one(bcrIdProcessor).addFileAssociation(10L, BcridA,
						"Array Data File", 1L, false, bcrAndBcrFileId, disease);
				one(bcrIdProcessor).addFileAssociation(11L, BcridB,
						"Array Data File", 1L, false, bcrAndBcrFileId, disease);
				one(bcrIdProcessor).addFileAssociation(20L, BcridA,
						"Derived Array Data File", 2L, false, bcrAndBcrFileId,
						disease);
				one(bcrIdProcessor).addFileAssociation(21L, BcridB,
						"Derived Array Data File", 2L, false, bcrAndBcrFileId,
						disease);
				one(bcrIdProcessor).addFileAssociation(12L, BcridC,
						"Array Data File", 1L, false, bcrAndBcrFileId, disease);
				one(diseaseBcrIdProcessor).addFileAssociation(10L, BcridA,
						"Array Data File", 1L, true, bcrAndBcrFileId, disease);
				one(diseaseBcrIdProcessor).addFileAssociation(11L, BcridB,
						"Array Data File", 1L, true, bcrAndBcrFileId, disease);
				one(diseaseBcrIdProcessor).addFileAssociation(20L, BcridA,
						"Derived Array Data File", 2L, true, bcrAndBcrFileId,
						disease);
				one(diseaseBcrIdProcessor).addFileAssociation(21L, BcridB,
						"Derived Array Data File", 2L, true, bcrAndBcrFileId,
						disease);
				one(diseaseBcrIdProcessor).addFileAssociation(12L, BcridC,
						"Array Data File", 1L, true, bcrAndBcrFileId, disease);

				// note extractC / file fileC.2 not added because include for
				// analysis is "no"
				// but an annotation will be added!
				one(mockAnnotationsService)
						.addAnnotation(
								1,
								DccAnnotationItemType.ALIQUOT_TYPE_ID,
								"TCGA-extractC",
								DccAnnotationCategory.DCC_ANNOTATION_DNU_ID,
								"SDRF in test.mage-tab.1.0.0 flagged aliquot to be excluded for analysis based on file 'fileC.2'.",
								"DCC");
				// noinspection ThrowableInstanceNeverThrown
				will(throwException(new AnnotationQueries.AnnotationQueriesException(
						annotationExceptionMessage)));

				// will be called for Archive1 and not Archive2 because Archive2
				// isn't replacing anything
				one(mockDroppedBarcodeFinder).execute(archive1, qcContext);
				one(mockDroppedBarcodeFinder).execute(archive2, qcContext);
			}
		});

		processor.execute(mageTabArchive, qcContext);
	}

	@Test
	public void testGetSdrfTypeNonProtein() throws Processor.ProcessorException {

		assertEquals(SdrfProcessor.SDRFType.NON_PROTEIN_SDRF,
				processor.getSdrfType(mageTabArchive));
	}

	@Test
	public void testGetSdrfTypeProtein() throws Processor.ProcessorException {

		assertEquals(SdrfProcessor.SDRFType.PROTEIN_SDRF,
				processor.getSdrfType(archiveProteinSdrf));
	}

	@Test(expected = Processor.ProcessorException.class)
	public void testGetSdrfTypeNullPlatform()
			throws Processor.ProcessorException {

		processor.getSdrfType(archiveNullPlatform);
	}

	@Test
	public void testGetSampleIdColumnNameNonProteinSdrf()
			throws Processor.ProcessorException {

		assertEquals("Extract Name", processor.getSampleIdColumnName(processor
				.getSdrfType(mageTabArchive)));
	}

	@Test
	public void testGetSampleIdColumnNameProteinSdrf()
			throws Processor.ProcessorException {

		assertEquals("Sample Name", processor.getSampleIdColumnName(processor
				.getSdrfType(archiveProteinSdrf)));
	}

	@Test(expected = Processor.ProcessorException.class)
	public void testGetSampleIdColumnNameNullPlatform()
			throws Processor.ProcessorException {

		processor.getSampleIdColumnName(processor
				.getSdrfType(archiveNullPlatform));
	}

	private Archive setupForProteinTests(final String sdrfLocation)
			throws IOException, Processor.ProcessorException,ParseException {
		final Archive proteinMageTab = new Archive();
		proteinMageTab.setArchiveType(Archive.TYPE_MAGE_TAB);
		proteinMageTab.setPlatform("MDA_RPPA_Core");
		proteinMageTab.setRealName(PROTEIN_ARCHIVE_NAME_MAGE_TAB);
		final Archive proteinLevel1 = new Archive();
		proteinLevel1.setPlatform("MDA_RPPA_Core");
		proteinLevel1.setArchiveType(Archive.TYPE_LEVEL_1);
		proteinLevel1.setRealName(PROTEIN_ARCHIVE_NAME_LEVEL_1);

		final Archive proteinLevel2 = new Archive();
		proteinLevel2.setArchiveType(Archive.TYPE_LEVEL_2);
		proteinLevel2.setPlatform("MDA_RPPA_Core");
		proteinLevel2.setRealName(PROTEIN_ARCHIVE_NAME_LEVEL_2);

		final Archive proteinLevel3 = new Archive();
		proteinLevel3.setArchiveType(Archive.TYPE_LEVEL_3);
		proteinLevel3.setPlatform("MDA_RPPA_Core");
		proteinLevel3.setRealName(PROTEIN_ARCHIVE_NAME_LEVEL_3);

		qcContext.getExperiment().getArchives().clear();
		qcContext.getExperiment().addArchive(proteinMageTab);
		qcContext.getExperiment().addArchive(proteinLevel1);
		qcContext.getExperiment().addArchive(proteinLevel2);
		qcContext.getExperiment().addArchive(proteinLevel3);

		TabDelimitedContent sdrf = new TabDelimitedContentImpl();
		TabDelimitedFileParser parser = new TabDelimitedFileParser();
		parser.setTabDelimitedContent(sdrf);
		parser.initialize(sdrfLocation);
		proteinMageTab.setSdrf(sdrf);
		qcContext.getExperiment().setSdrf(sdrf);

		context.checking(new Expectations() {
			{
				allowing(mockDroppedBarcodeFinder).execute(proteinLevel1,
						qcContext);
				allowing(mockDroppedBarcodeFinder).execute(proteinLevel2,
						qcContext);
				allowing(mockDroppedBarcodeFinder).execute(proteinLevel3,
						qcContext);

				allowing(archiveQueries).getArchiveIdByName(
						PROTEIN_ARCHIVE_NAME_MAGE_TAB);
				will(returnValue(proteinMageTabArchiveId));

				allowing(archiveQueries).getArchiveIdByName(
						PROTEIN_ARCHIVE_NAME_LEVEL_1);
				will(returnValue(proteinLevel1ArchiveId));

				allowing(archiveQueries).getArchiveIdByName(
						PROTEIN_ARCHIVE_NAME_LEVEL_2);
				will(returnValue(proteinLevel2ArchiveId));

				allowing(archiveQueries).getArchiveIdByName(
						PROTEIN_ARCHIVE_NAME_LEVEL_3);
				will(returnValue(proteinLevel3ArchiveId));

			}
		});

		return proteinMageTab;
	}

	/*
	 * Tests basic functional scenario, where the SDRF contains two rows, each
	 * representing a valid Portion whose UUID is in the database. Checks that
	 * all required file associations are added between the files and
	 * biospecimens.
	 */
	@Test
	public void testProcessProteinSDRFScenario1() throws IOException,
			Processor.ProcessorException,ParseException {

		testProcessProteinSDRFScenario(scenario1Sdrf);
	}

	/* Test valid SDRF with control rows and shipped portion rows */
	@Test
	public void testProcessProteinSDRFScenario2() throws IOException,
			Processor.ProcessorException,ParseException {

		testProcessProteinSDRFScenario(scenario2Sdrf);
	}

	/* Test a given SDRF file */
	private void testProcessProteinSDRFScenario(final String sdrfFile)
			throws IOException, Processor.ProcessorException,ParseException {
		final Archive mageTabArchive = setupForProteinTests(sdrfFile);
		final String rowOneUuid = "16211caa-d5f8-4c55-a3a4-be3355122480";
		final String rowTwoUuid = "462b80e9-9015-43b7-b847-2fe3fdd384ea";
		final String rowOneSlideDesignFile = "AKT_pS473(V)_GBL9010352.slide_design.txt";
		final String rowTwoSlideDesignFile = "4EBP1(V)_GBL9010358.slide_design.txt";
		final String annotationsFile = "mdanderson.org_OV.antibody_annotations.txt";
		final String rowOneTif = "AKT_pS473(V)_GBL9010352.tif";
		final String rowTwoTif = "4EBP1(V)_GBL9010358.tif";
		final String rowOneLevel1File = "AKT_pS473(V)_GBL9010352.txt";
		final String rowTwoLevel1File = "4EBP1(V)_GBL9010358.txt";
		final String levelTwoFile = "mdanderson.org_OV.SuperCurve.Level_2.Set29.txt";
		final String levelThreeFile = "mdanderson_OV.protein_expression.Level_3.Set29.txt";

		final Long rowOneBiospecimenId = 42L;
		final Long rowTwoBiospecimenId = 53L;

		context.checking(new Expectations() {
			{
				// expect to find biospecimens for UUIDs in SDRF
				atLeast(1).of(commonShippedBiospecimenQueries)
						.getShippedBiospecimenIdForUUID(rowOneUuid);
				will(returnValue(rowOneBiospecimenId));
				atLeast(1).of(commonShippedBiospecimenQueries)
						.getShippedBiospecimenIdForUUID(rowTwoUuid);
				will(returnValue(rowTwoBiospecimenId));

				// expect to find first row slide design file in mage-tab
				// archive, and then to associate it with biospecimen 1 in
				// common and disease
				one(fileInfoQueries).getFileId(rowOneSlideDesignFile,
						proteinMageTabArchiveId);
				will(returnValue(1L));
				one(commonShippedBiospecimenQueries).addFileRelationship(
						rowOneBiospecimenId, 1L);
				one(diseaseShippedBiospecimenQueries).addFileRelationship(
						rowOneBiospecimenId, 1L);

				// expect to find second row slide design file in mage-tab
				// archive, and then to associate it with biospecimen 2 in
				// common and disease
				one(fileInfoQueries).getFileId(rowTwoSlideDesignFile,
						proteinMageTabArchiveId);
				will(returnValue(2L));
				one(commonShippedBiospecimenQueries).addFileRelationship(
						rowTwoBiospecimenId, 2L);
				one(diseaseShippedBiospecimenQueries).addFileRelationship(
						rowTwoBiospecimenId, 2L);

				// expect to find annotations file, and then to associate it
				// with biospecimens 1 and 2 in common and disease
				exactly(2).of(fileInfoQueries).getFileId(annotationsFile,
						proteinMageTabArchiveId);
				will(returnValue(3L));
				one(commonShippedBiospecimenQueries).addFileRelationship(
						rowOneBiospecimenId, 3L);
				one(commonShippedBiospecimenQueries).addFileRelationship(
						rowTwoBiospecimenId, 3L);
				one(diseaseShippedBiospecimenQueries).addFileRelationship(
						rowOneBiospecimenId, 3L);
				one(diseaseShippedBiospecimenQueries).addFileRelationship(
						rowTwoBiospecimenId, 3L);

				// expect to find second row tif file in level 1 archive, and
				// then to associate it with biospecimen 2 in common and disease
				one(fileInfoQueries).getFileId(rowTwoTif,
						proteinLevel1ArchiveId);
				will(returnValue(4L));
				one(commonShippedBiospecimenQueries).addFileRelationship(
						rowTwoBiospecimenId, 4L);
				one(diseaseShippedBiospecimenQueries).addFileRelationship(
						rowTwoBiospecimenId, 4L);

				one(fileInfoQueries).getFileId(rowTwoLevel1File,
						proteinLevel1ArchiveId);
				will(returnValue(5L));
				one(commonShippedBiospecimenQueries).addFileRelationship(
						rowTwoBiospecimenId, 5L);
				one(diseaseShippedBiospecimenQueries).addFileRelationship(
						rowTwoBiospecimenId, 5L);

				one(fileInfoQueries).getFileId(rowOneTif,
						proteinLevel1ArchiveId);
				will(returnValue(6L));
				one(commonShippedBiospecimenQueries).addFileRelationship(
						rowOneBiospecimenId, 6L);
				one(diseaseShippedBiospecimenQueries).addFileRelationship(
						rowOneBiospecimenId, 6L);

				one(fileInfoQueries).getFileId(rowOneLevel1File,
						proteinLevel1ArchiveId);
				will(returnValue(7L));
				one(commonShippedBiospecimenQueries).addFileRelationship(
						rowOneBiospecimenId, 7L);
				one(diseaseShippedBiospecimenQueries).addFileRelationship(
						rowOneBiospecimenId, 7L);

				exactly(2).of(fileInfoQueries).getFileId(levelTwoFile,
						proteinLevel2ArchiveId);
				will(returnValue(8L));
				one(commonShippedBiospecimenQueries).addFileRelationship(
						rowOneBiospecimenId, 8L);
				one(commonShippedBiospecimenQueries).addFileRelationship(
						rowTwoBiospecimenId, 8L);
				one(diseaseShippedBiospecimenQueries).addFileRelationship(
						rowOneBiospecimenId, 8L);
				one(diseaseShippedBiospecimenQueries).addFileRelationship(
						rowTwoBiospecimenId, 8L);

				exactly(2).of(fileInfoQueries).getFileId(levelThreeFile,
						proteinLevel3ArchiveId);
				will(returnValue(9L));
				one(commonShippedBiospecimenQueries).addFileRelationship(
						rowOneBiospecimenId, 9L);
				one(commonShippedBiospecimenQueries).addFileRelationship(
						rowTwoBiospecimenId, 9L);
				one(diseaseShippedBiospecimenQueries).addFileRelationship(
						rowOneBiospecimenId, 9L);
				one(diseaseShippedBiospecimenQueries).addFileRelationship(
						rowTwoBiospecimenId, 9L);
			}
		});
		processor.doWork(mageTabArchive, qcContext);

	}

	@Test
	public void testExecuteProteinSdrfScenario5() throws Exception {
		final DccAnnotation pretendDNUAnnotation = new DccAnnotation();
		final Archive mageTabArchive = setupForProteinTests(scenario5Sdrf);
		mageTabArchive.setTheTumor(disease);
		final String rowOneUuid = "16211caa-d5f8-4c55-a3a4-be3355122480";
		final String rowTwoUuid = "462b80e9-9015-43b7-b847-2fe3fdd384ea";
		final String rowOneSlideDesignFile = "AKT_pS473(V)_GBL9010352.slide_design.txt";
		final String rowTwoSlideDesignFile = "4EBP1(V)_GBL9010358.slide_design.txt";
		final String annotationsFile = "mdanderson.org_OV.antibody_annotations.txt";
		final String rowOneTif = "AKT_pS473(V)_GBL9010352.tif";
		final String rowOneLevel1File = "AKT_pS473(V)_GBL9010352.txt";
		final String levelTwoFile = "mdanderson.org_OV.SuperCurve.Level_2.Set29.txt";
		final String levelThreeFile = "mdanderson_OV.protein_expression.Level_3.Set29.txt";

		final Long rowOneBiospecimenId = 42L;
		final Long rowTwoBiospecimenId = 53L;

		context.checking(new Expectations() {
			{
				// expect to find biospecimens for UUIDs in SDRF
				atLeast(1).of(commonShippedBiospecimenQueries)
						.getShippedBiospecimenIdForUUID(rowOneUuid);
				will(returnValue(rowOneBiospecimenId));
				atLeast(1).of(commonShippedBiospecimenQueries)
						.getShippedBiospecimenIdForUUID(rowTwoUuid);
				will(returnValue(rowTwoBiospecimenId));

				// expect to find first row slide design file in mage-tab
				// archive, and then to associate it with biospecimen 1 in
				// common and disease
				one(fileInfoQueries).getFileId(rowOneSlideDesignFile,
						proteinMageTabArchiveId);
				will(returnValue(1L));
				one(commonShippedBiospecimenQueries).addFileRelationship(
						rowOneBiospecimenId, 1L);
				one(diseaseShippedBiospecimenQueries).addFileRelationship(
						rowOneBiospecimenId, 1L);

				// expect to find second row slide design file in mage-tab
				// archive, and then to associate it with biospecimen 2 in
				// common and disease
				one(fileInfoQueries).getFileId(rowTwoSlideDesignFile,
						proteinMageTabArchiveId);
				will(returnValue(2L));
				one(commonShippedBiospecimenQueries).addFileRelationship(
						rowTwoBiospecimenId, 2L);
				one(diseaseShippedBiospecimenQueries).addFileRelationship(
						rowTwoBiospecimenId, 2L);

				// expect to find annotations file, and then to associate it
				// with biospecimens 1 and 2 in common and disease
				exactly(2).of(fileInfoQueries).getFileId(annotationsFile,
						proteinMageTabArchiveId);
				will(returnValue(3L));
				one(commonShippedBiospecimenQueries).addFileRelationship(
						rowOneBiospecimenId, 3L);
				one(commonShippedBiospecimenQueries).addFileRelationship(
						rowTwoBiospecimenId, 3L);
				one(diseaseShippedBiospecimenQueries).addFileRelationship(
						rowOneBiospecimenId, 3L);
				one(diseaseShippedBiospecimenQueries).addFileRelationship(
						rowTwoBiospecimenId, 3L);

				one(fileInfoQueries).getFileId(rowOneTif,
						proteinLevel1ArchiveId);
				will(returnValue(6L));
				one(commonShippedBiospecimenQueries).addFileRelationship(
						rowOneBiospecimenId, 6L);
				one(diseaseShippedBiospecimenQueries).addFileRelationship(
						rowOneBiospecimenId, 6L);

				one(fileInfoQueries).getFileId(rowOneLevel1File,
						proteinLevel1ArchiveId);
				will(returnValue(7L));
				one(commonShippedBiospecimenQueries).addFileRelationship(
						rowOneBiospecimenId, 7L);
				one(diseaseShippedBiospecimenQueries).addFileRelationship(
						rowOneBiospecimenId, 7L);

				exactly(1).of(fileInfoQueries).getFileId(levelTwoFile,
						proteinLevel2ArchiveId);
				will(returnValue(8L));
				one(commonShippedBiospecimenQueries).addFileRelationship(
						rowOneBiospecimenId, 8L);
				one(diseaseShippedBiospecimenQueries).addFileRelationship(
						rowOneBiospecimenId, 8L);

				exactly(1).of(fileInfoQueries).getFileId(levelThreeFile,
						proteinLevel3ArchiveId);
				will(returnValue(9L));
				one(commonShippedBiospecimenQueries).addFileRelationship(
						rowOneBiospecimenId, 9L);
				one(diseaseShippedBiospecimenQueries).addFileRelationship(
						rowOneBiospecimenId, 9L);

                one(mockUuidService).getLatestBarcodeForUUID("462b80e9-9015-43b7-b847-2fe3fdd384ea");
                will(returnValue("TCGA-HI-BARCODE"));

				// note 4EBP1(V)_GBL9010358.tif not added because include for
				// analysis is "no"
				// but an annotation will be added!
				one(mockAnnotationsService)
						.addAnnotation(
								1,
								DccAnnotationItemType.ALIQUOT_TYPE_ID,
								"TCGA-HI-BARCODE",
								DccAnnotationCategory.DCC_ANNOTATION_DNU_ID,
								"SDRF in mdanderson.org_OV.MDA_RPPA_Core.mage-tab.1.0.0 flagged aliquot to "
										+ "be excluded for analysis based on file '4EBP1(V)_GBL9010358.tif'.",
								"DCC");
				will(returnValue(pretendDNUAnnotation));
				one(mockAnnotationsService).curate(pretendDNUAnnotation);
			}
		});

		processor.doWork(mageTabArchive, qcContext);
	}

	/*
	 * The UUIDs in the SDRF are not found in the database
	 */
	@Test(expected = Processor.ProcessorException.class)
	public void testProteinSdrfScenario3() throws Processor.ProcessorException,
			IOException,ParseException {

		final String rowOneUuid = "16211caa-d5f8-4c55-a3a4-be3355122480";

		final Archive mageTabArchive = setupForProteinTests(scenario1Sdrf);

		context.checking(new Expectations() {
			{
				// will throw exception when processing first row, since UUID
				// not found
				atLeast(1).of(commonShippedBiospecimenQueries)
						.getShippedBiospecimenIdForUUID(rowOneUuid);
				will(returnValue(null));

				allowing(fileInfoQueries).getFileId(with(any(String.class)),
						with(any(Long.class)));
				will(returnValue(123L));

			}
		});
		processor.doWork(mageTabArchive, qcContext);
	}

	@Test
	public void testIsTcgaSample() {
		final String invalidUuid = "invalidUuid";
		final String validUuid = "16211caa-d5f8-4c55-a3a4-be3355122480";
		// PROTEIN_SDRF
		assertFalse(processor.isTcgaSample(invalidUuid, "biospecimenType",
				SdrfProcessor.SDRFType.PROTEIN_SDRF));
		assertFalse(processor.isTcgaSample(invalidUuid, null,
				SdrfProcessor.SDRFType.PROTEIN_SDRF));
		assertFalse(processor.isTcgaSample(validUuid, null,
				SdrfProcessor.SDRFType.PROTEIN_SDRF));
		assertFalse(processor.isTcgaSample(validUuid, "->",
				SdrfProcessor.SDRFType.PROTEIN_SDRF));
		assertTrue(processor.isTcgaSample(validUuid, "biospecimenType",
				SdrfProcessor.SDRFType.PROTEIN_SDRF));
		// NON_PROTEIN_SDRF
		assertFalse(processor.isTcgaSample(invalidUuid, "biospecimenType",
				SdrfProcessor.SDRFType.NON_PROTEIN_SDRF));
		assertFalse(processor.isTcgaSample(invalidUuid, null,
				SdrfProcessor.SDRFType.NON_PROTEIN_SDRF));
		assertTrue(processor.isTcgaSample(validUuid, null,
				SdrfProcessor.SDRFType.NON_PROTEIN_SDRF));
		assertTrue(processor.isTcgaSample(validUuid, "biospecimenType",
				SdrfProcessor.SDRFType.NON_PROTEIN_SDRF));
	}

	@Test
	public void testBarcodeTcgaSample() {
		final String validBarcode = "TCGA-07-0227-20A-01D-1129-05";
		assertTrue(processor.isTcgaSample(validBarcode, "biospecimenType",
				SdrfProcessor.SDRFType.NON_PROTEIN_SDRF));
	}

	@Test
	public void testInvalidTcgaSample() {
		final String inValidBarcode = "TEST-07-0227-20A-01D-1129-05";
		assertFalse(processor.isTcgaSample(inValidBarcode, "biospecimenType",
				SdrfProcessor.SDRFType.NON_PROTEIN_SDRF));
		assertFalse(processor.isTcgaSample(inValidBarcode, null,
				SdrfProcessor.SDRFType.NON_PROTEIN_SDRF));
	}

	@Test
	public void UUIDSdrf() throws Processor.ProcessorException, IOException,
			AnnotationQueries.AnnotationQueriesException, BeanException,ParseException {
		final DccAnnotation pretendDNUAnnotation = new DccAnnotation();

		TabDelimitedContent sdrf = new TabDelimitedContentImpl();
		TabDelimitedFileParser parser = new TabDelimitedFileParser();
		parser.setTabDelimitedContent(sdrf);
		parser.initialize(uuidSDRF);
		mageTabArchive.setSdrf(sdrf);
		sdrfCommonExpectations();
		context.checking(new Expectations() {
			{
				one(bcrIdProcessor).getBiospecimenIdForUUID(
						"16211caa-d5f8-4c55-a3a4-be3355122480");
				will(returnValue(1000l));
				one(bcrIdProcessor).addFileAssociation(10L, 1000,
						"Array Data File", false, -1);
				will(returnValue(100));
				one(diseaseBcrIdProcessor).addFileAssociation(10L, 1000,
						"Array Data File", true, 100);

				one(bcrIdProcessor).getBiospecimenIdForUUID(
						"462b80e9-9015-43b7-b847-2fe3fdd384ea");
				will(returnValue(2000l));
				one(bcrIdProcessor).addFileAssociation(11L, 2000,
						"Array Data File", false, -1);
				will(returnValue(200));
				one(diseaseBcrIdProcessor).addFileAssociation(11L, 2000,
						"Array Data File", true, 200);

				one(bcrIdProcessor).getBiospecimenIdForUUID(
						"591b80e9-9015-43b7-b847-2fe3fdd384ea");
				will(returnValue(3000l));
				one(bcrIdProcessor).addFileAssociation(12L, 3000,
						"Array Data File", false, -1);
				will(returnValue(300));
				one(diseaseBcrIdProcessor).addFileAssociation(12L, 3000,
						"Array Data File", true, 300);

				one(bcrIdProcessor).getBiospecimenIdForUUID(
						"16211caa-d5f8-4c55-a3a4-be3355122480");
				will(returnValue(1000l));
				one(bcrIdProcessor).addFileAssociation(20L, 1000,
						"Derived Array Data File", false, -1);
				will(returnValue(400));
				one(diseaseBcrIdProcessor).addFileAssociation(20L, 1000,
						"Derived Array Data File", true, 400);

				one(bcrIdProcessor).getBiospecimenIdForUUID(
						"462b80e9-9015-43b7-b847-2fe3fdd384ea");
				will(returnValue(2000l));
				one(bcrIdProcessor).addFileAssociation(21L, 2000,
						"Derived Array Data File", false, -1);
				will(returnValue(500));
				one(diseaseBcrIdProcessor).addFileAssociation(21L, 2000,
						"Derived Array Data File", true, 500);

                one(mockUuidService).getLatestBarcodeForUUID("591b80e9-9015-43b7-b847-2fe3fdd384ea");
                will(returnValue("fakeBarcode"));

				one(mockAnnotationsService)
						.addAnnotation(
								1,
								DccAnnotationItemType.ALIQUOT_TYPE_ID,
								"fakeBarcode",
								DccAnnotationCategory.DCC_ANNOTATION_DNU_ID,
								"SDRF in test.mage-tab.1.0.0 flagged aliquot to be excluded for analysis based on file 'fileC.2'.",
								"DCC");
				will(returnValue(pretendDNUAnnotation));
				one(mockAnnotationsService).curate(pretendDNUAnnotation);
			}
		});
		processor.doWork(mageTabArchive, qcContext);
	}

	private void sdrfCommonExpectations() throws Processor.ProcessorException,
			AnnotationQueries.AnnotationQueriesException {

		context.checking(new Expectations() {
			{
				one(archiveQueries).getArchiveIdByName("archive1");
				will(returnValue(1L));
				// get archive2 id once for each association
				one(archiveQueries).getArchiveIdByName("archive2");
				will(returnValue(2L));
				// get each file id once
				one(fileInfoQueries).getFileId("fileA.1", 1L);
				will(returnValue(10L));
				one(fileInfoQueries).getFileId("fileB.1", 1L);
				will(returnValue(11L));
				one(fileInfoQueries).getFileId("fileA.2", 2L);
				will(returnValue(20L));
				one(fileInfoQueries).getFileId("fileB.2", 2L);
				will(returnValue(21L));
				one(fileInfoQueries).getFileId("fileC.1", 1L);
				will(returnValue(12L));
				one(mockDroppedBarcodeFinder).execute(archive1, qcContext);
				one(mockDroppedBarcodeFinder).execute(archive2, qcContext);

			}
		});
	}

}
