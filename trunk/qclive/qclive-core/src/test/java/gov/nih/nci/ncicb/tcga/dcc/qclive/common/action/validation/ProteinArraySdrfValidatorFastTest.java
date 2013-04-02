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
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ShippedBiospecimenQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContentImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedContentNavigator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedFileParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.AbstractSdrfHandler;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.BCRIDQueries;
import junit.framework.Assert;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test class for ProteinArraySdrfValidator
 * 
 * @author Stanley Girshik Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class ProteinArraySdrfValidatorFastTest {

	private ProteinArraySdrfValidator validator;
	private TabDelimitedContentNavigator sdrfNavigator;
	private static final String SAMPLES_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	private String testDir = SAMPLES_DIR + "qclive/proteinArrayValidator";
	private String proteinSDRFFileName = "/mdanderson.org_OV.MDA_RPPA_Core.mage-tab.1.0.0/mdanderson.org_OV.MDA_RPPA_Core.1.sdrf.txt";
	private String[] sdrfHeaders = new String[13];
	private QcContext qcContext;
	private TabDelimitedContent sdrf;
	private Map<Integer, String[]> sdrfValues;
	private final Mockery context = new JUnit4Mockery();
	private BCRIDQueries mockBcrIdQueries = context.mock(BCRIDQueries.class);
	private ShippedBiospecimenQueries mockedShippedBioQueries = context.mock(ShippedBiospecimenQueries.class);
    private QcLiveBarcodeAndUUIDValidator mockQcLiveBarcodeAndUUIDValidator = context.mock(QcLiveBarcodeAndUUIDValidator.class);
	private Archive archive;

	@Before
	public void setUp() throws IOException {
		validator = new ProteinArraySdrfValidator();
		archive = new Archive();
		validator = new ProteinArraySdrfValidator();
		sdrfNavigator = new TabDelimitedContentNavigator();
		sdrf = new TabDelimitedContentImpl();
		sdrfValues = new HashMap<Integer, String[]>();
		archive.setTumorType("TUM");
		archive.setRealName("proteinArchive");
		archive.setArchiveFile(new File(testDir
				+ "/mdanderson.org_OV.MDA_RPPA_Core.mage-tab.1.0.0.tar.gz"));
		archive.setArchiveType(Archive.TYPE_MAGE_TAB);
		archive.setSdrf(sdrf);
		archive.setSdrfFile(new File(testDir + proteinSDRFFileName));
		archive.setDeployLocation("testDir/mdanderson.org_OV.MDA_RPPA_Core.mage-tab.1.0.0");
		validator.setShippedBioQueries(mockedShippedBioQueries);
        validator.setQcLiveBarcodeAndUUIDValidator(mockQcLiveBarcodeAndUUIDValidator);
		qcContext = new QcContext();
		qcContext.setArchive(archive);
	}

    /**
     * Scenario 1 Valid SDRF
     *
     * @throws Exception
     */
    @Test
    public void testProteinArraySdrfValidatorGoodFile() throws Exception {

        final String uuid1 = "16211caa-d5f8-4c55-a3a4-be3355122480";
        final String uuid2 = "462b80e9-9015-43b7-b847-2fe3fdd384ea";

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat(uuid1);
            will(returnValue(true));
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat(uuid2);
            will(returnValue(true));
            one(mockedShippedBioQueries).isShippedBiospecimenShippedPortionUUIDValid(uuid1);
            will(returnValue(true));
            one(mockedShippedBioQueries).isShippedBiospecimenShippedPortionUUIDValid(uuid2);
            will(returnValue(true));
        }});

        setTabDelimitedContent(proteinSDRFFileName);
        sdrfNavigator.setTabDelimitedContent(sdrf);
        assertTrue(validator.headersAreValid(sdrfNavigator, qcContext));
        assertTrue(validator.checkRequiredColumns(sdrfNavigator, qcContext));
        assertTrue(validator.runSpecificValidations(qcContext, sdrfNavigator));
        assertTrue(validator.validateCommentColumns(qcContext, sdrfNavigator));
        assertEquals(qcContext.getErrors().toString(), 0, qcContext.getErrorCount());
    }

    @Test
    public void testProteinArraySdrfValidatorGoodFileSoundcheck() throws Exception {

        qcContext.setStandaloneValidator(true);

        final String uuid1 = "16211caa-d5f8-4c55-a3a4-be3355122480";
        final String uuid2 = "462b80e9-9015-43b7-b847-2fe3fdd384ea";
        final List<String> uuids = Arrays.asList(uuid1, uuid2);
        final Map<String, Boolean> expectedMap = new HashMap<String, Boolean>();
        expectedMap.put(uuid1, true);
        expectedMap.put(uuid2, true);

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat(uuid1);
            will(returnValue(true));
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat(uuid2);
            will(returnValue(true));
            one(mockQcLiveBarcodeAndUUIDValidator).batchValidateUUIDsReportIndividualResults(uuids, qcContext, null, true);
            will(returnValue(expectedMap));
        }});

        setTabDelimitedContent(proteinSDRFFileName);
        sdrfNavigator.setTabDelimitedContent(sdrf);
        assertTrue(validator.headersAreValid(sdrfNavigator, qcContext));
        assertTrue(validator.checkRequiredColumns(sdrfNavigator, qcContext));
        assertTrue(validator.runSpecificValidations(qcContext, sdrfNavigator));
        assertTrue(validator.validateCommentColumns(qcContext, sdrfNavigator));
        assertEquals(qcContext.getErrors().toString(), 0, qcContext.getErrorCount());
    }

    @Test
    public void testProteinArraySdrfValidatorNonBCRSubmittedUUIDSoundcheck() throws Exception {

        qcContext.setStandaloneValidator(true);

        final String uuid1 = "16211caa-d5f8-4c55-a3a4-be3355122480";
        final String uuid2 = "462b80e9-9015-43b7-b847-2fe3fdd384ea";
        final List<String> uuids = Arrays.asList(uuid1, uuid2);
        final Map<String, Boolean> expectedMap = new HashMap<String, Boolean>();
        expectedMap.put(uuid1, true);
        expectedMap.put(uuid2, false);

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat(uuid1);
            will(returnValue(true));
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat(uuid2);
            will(returnValue(true));
            one(mockQcLiveBarcodeAndUUIDValidator).batchValidateUUIDsReportIndividualResults(uuids, qcContext, null, true);
            will(returnValue(expectedMap));
        }});

        setTabDelimitedContent(proteinSDRFFileName);
        sdrfNavigator.setTabDelimitedContent(sdrf);
        assertTrue(validator.headersAreValid(sdrfNavigator, qcContext));
        assertTrue(validator.checkRequiredColumns(sdrfNavigator, qcContext));
        assertFalse(validator.runSpecificValidations(qcContext, sdrfNavigator));
        assertTrue(validator.validateCommentColumns(qcContext, sdrfNavigator));
        assertEquals(qcContext.getErrors().toString(), 1, qcContext.getErrorCount());
        assertEquals("An error occurred while validating SDRF for archive 'proteinArchive':"
                + " SDRF  'Sample Name' column has a UUID which has not been submitted by the BCR yet, so data for it cannot be accepted."
                + " The UUID value in the column is " + uuid2 +"\t[archive proteinArchive]",
                qcContext.getErrors().get(0));
    }

	/**
	 * Scenario 2 Invalid SDRF: Missing 'Sample Name' column
	 * 
	 * @throws Exception
	 */
	@Test
	public void testProteinArraySdrfMissingSampleName() throws Exception {
		final String filename = "scenario_2_missing_sample_name_col_sdrf.txt";
		setTabDelimitedContent(filename);
		sdrfNavigator.setTabDelimitedContent(sdrf);
		assertTrue(validator.headersAreValid(sdrfNavigator, qcContext));
		assertFalse(validator.runSpecificValidations(qcContext, sdrfNavigator));
		assertEquals(qcContext.getErrors().toString(), 2,
				qcContext.getErrorCount());
		assertEquals(
				"An error occurred while validating SDRF for archive 'proteinArchive': "
						+ "SDRF header 'Sample Name' was not found\t[archive proteinArchive]",
				qcContext.getErrors().get(0));
	}

	/**
	 * Scenario 2.5 Invalid SDRF: 'Sample Name' column occurring multiple times
	 * 
	 * @throws Exception
	 */
	@Test
	public void testProteinArraySdrfValidatorScenarioMultipleSampleName()
			throws Exception {

		final String sampleNameColumn = "Sample Name";
		final String filename = "scenario_2.5_multiple_sample_name_col_sdrf.txt";
		setTabDelimitedContent(filename);
		sdrfNavigator.setTabDelimitedContent(sdrf);
		assertTrue(validator.headersAreValid(sdrfNavigator, qcContext));
		assertFalse(validator.runSpecificValidations(qcContext, sdrfNavigator));
		assertEquals(2, qcContext.getErrorCount());
		assertEquals(
				"An error occurred while validating SDRF for archive 'proteinArchive': SDRF header '"
						+ sampleNameColumn
						+ "' should only occur once\t[archive proteinArchive]",
				qcContext.getErrors().get(0));
	}

	/**
	 * Scenario 3 Invalid SDRF: Missing column Biospecimen Type
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMissingBiospecimenType() throws Exception {
		final String fileName = "scenario_3_missing_biospecimen_type_sdrf.txt";
		setTabDelimitedContent(fileName);
		sdrfNavigator.setTabDelimitedContent(sdrf);
		assertTrue(validator.headersAreValid(sdrfNavigator, qcContext));
		assertFalse(validator.checkRequiredColumns(sdrfNavigator, qcContext));
		assertEquals(1, qcContext.getErrorCount());
		assertEquals(
				"Required SDRF column 'Comment [TCGA Biospecimen Type]' is missing\t[archive proteinArchive]",
				qcContext.getErrors().get(0));
	}

	/**
	 * Scenario 4 Invalid SDRF: missing 'Array Design File' column
	 * 
	 * @throws Exception
	 */
	@Test
	public void testProteinArraySdrfValidatorMissingArrayDesignFileColumn()
			throws Exception {
		final String filename = "scenario_4_missing_array_design_file_col_sdrf.txt";
		setTabDelimitedContent(filename);
		sdrfNavigator.setTabDelimitedContent(sdrf);
		assertTrue(validator.headersAreValid(sdrfNavigator, qcContext));
		assertFalse(validator.checkRequiredColumns(sdrfNavigator, qcContext));
		assertEquals(1, qcContext.getErrorCount());
		assertEquals(
				"Required SDRF column 'Array Design File' is missing\t[archive proteinArchive]",
				qcContext.getErrors().get(0));
	}

	/**
	 * Scenario 5 Invalid SDRF: missing 'Annotations File' column
	 * 
	 * @throws Exception
	 */
	@Test
	public void testProteinArraySdrfValidatorMissingAnnotationsFileColumn()
			throws Exception {

		final String filename = "scenario_5_missing_annotations_file_col_sdrf.txt";
		setTabDelimitedContent(filename);
		sdrfNavigator.setTabDelimitedContent(sdrf);
		assertTrue(validator.headersAreValid(sdrfNavigator, qcContext));
		assertFalse(validator.checkRequiredColumns(sdrfNavigator, qcContext));
		assertEquals(1, qcContext.getErrorCount());
		assertEquals(
				"Required SDRF column 'Annotations File' is missing\t[archive proteinArchive]",
				qcContext.getErrors().get(0));
	}

	/**
	 * Scenario 6 Invalid SDRF: missing 'Image File' column
	 * 
	 * @throws Exception
	 */
	@Test
	public void testProteinArraySdrfValidatorMissingImageFileColumn()
			throws Exception {
		final String filename = "scenario_6_missing_image_file_col_sdrf.txt";
		setTabDelimitedContent(filename);
		sdrfNavigator.setTabDelimitedContent(sdrf);
		assertTrue(validator.headersAreValid(sdrfNavigator, qcContext));
		assertFalse(validator.checkRequiredColumns(sdrfNavigator, qcContext));
		assertEquals(1, qcContext.getErrorCount());
		assertEquals(
				"Required SDRF column 'Image File' is missing\t[archive proteinArchive]",
				qcContext.getErrors().get(0));
	}

	/**
	 * Scenario 9 Invalid SDRF: wrong *File column for Level 1
	 * 
	 * @throws Exception
	 */
	@Test
	public void testProteinArraySdrfValidatorWrongLevel1FileColumn()
			throws Exception {

		final String filename = "scenario_9_wrong_level1_file_column_sdrf.txt";
		setTabDelimitedContent(filename);
		sdrfNavigator.setTabDelimitedContent(sdrf);

		Assert.assertFalse(validator.validateCommentColumns(qcContext,
				sdrfNavigator));
		assertEquals(qcContext.getErrors().toString(), 1,
				qcContext.getErrorCount());
		assertEquals(
				"An error occurred while validating SDRF for archive 'proteinArchive': "
						+ "SDRF line 2: Level 1 files must one of [Image File, Array Data File, Derived Array Data Matrix File], "
						+ "but the type found was 'Annotations File'\t[archive proteinArchive]",
				qcContext.getErrors().get(0));
	}

	/**
	 * Scenario 10 Invalid SDRF: wrong *File column for Level 2
	 * 
	 * @throws Exception
	 */
	@Test
	public void testProteinArraySdrfValidatorWrongLevel2FileColumn()
			throws Exception {

		final String filename = "scenario_10_wrong_level2_file_column_sdrf.txt";
		setTabDelimitedContent(filename);
		sdrfNavigator.setTabDelimitedContent(sdrf);

		Assert.assertFalse(validator.validateCommentColumns(qcContext,
				sdrfNavigator));
		assertEquals(qcContext.getErrors().toString(), 1,
				qcContext.getErrorCount());
		assertEquals(
				"An error occurred while validating SDRF for archive 'proteinArchive': "
						+ "SDRF line 2: Level 2 files must one of [Derived Array Data File, Derived Array Data Matrix File], "
						+ "but the type found was 'Array Data File'\t[archive proteinArchive]",
				qcContext.getErrors().get(0));
	}

	/**
	 * Scenario 11 Invalid SDRF: wrong *File column for Level 3
	 * 
	 * @throws Exception
	 */
	@Test
	public void testProteinArraySdrfValidatorWrongLevel3FileColumn()
			throws Exception {

		final String filename = "scenario_11_wrong_level3_file_column_sdrf.txt";
		setTabDelimitedContent(filename);
		sdrfNavigator.setTabDelimitedContent(sdrf);

		Assert.assertFalse(validator.validateCommentColumns(qcContext,
				sdrfNavigator));
		assertEquals(qcContext.getErrors().toString(), 1,
				qcContext.getErrorCount());
		assertEquals(
				"An error occurred while validating SDRF for archive 'proteinArchive': "
						+ "SDRF line 2: Level 3 files must one of [Derived Array Data Matrix File], "
						+ "but the type found was 'Derived Array Data File'\t[archive proteinArchive]",
				qcContext.getErrors().get(0));
	}

	/**
	 * Scenario 12 Invalid SDRF: blank space found (in array design file)
	 * 
	 * @throws Exception
	 */
	@Test
	public void testProteinArraySdrfValidatorColumnContainsBlankSpace()
			throws Exception {
		final String filename = "scenario_12_blank_space_col_sdrf.txt";
		setTabDelimitedContent(filename);
		sdrfNavigator.setTabDelimitedContent(sdrf);
		assertTrue(validator.headersAreValid(sdrfNavigator, qcContext));
		assertFalse(validator.checkAllColumnsForBlanks(sdrfNavigator, qcContext));
		assertEquals(1, qcContext.getErrorCount());
		assertEquals(
				"SDRF column 'Array Design File' (column 2) contains a blank value.\t[archive proteinArchive]",
				qcContext.getErrors().get(0));
	}

	/**
	 * Scenario 17 Invalid SDRF: invalid value for Comment [TCGA File Type]
	 * 
	 * @throws Exception
	 */
	@Test
	public void testProteinArraySdrfValidatorInvalidValueForCommentTCGAFileType()
			throws Exception {
		final String filename = "scenario_17_invalid_value_for_comment_tcga_file_type_sdrf.txt";
		setTabDelimitedContent(filename);
		sdrfNavigator.setTabDelimitedContent(sdrf);
		assertFalse(validator.checkRequiredColumns(sdrfNavigator, qcContext));
		assertEquals(1, qcContext.getErrorCount());
		assertEquals(
				"An error occurred while validating SDRF for archive 'proteinArchive': SDRF Line 2:"
						+ "Comment [TCGA File Type] value is 'oscar mon oncle'. It should be one of "
						+ "[[Antibody Annotations (txt), Array Slide Image (TIFF), RPPA Slide Image Measurements (txt), "
						+ "SuperCurve Results (txt), MDA_RPPA Slide Design (txt), "
						+ "Normalized Protein Expression (MAGE-TAB data matrix)]] \t[archive proteinArchive]",
				qcContext.getErrors().get(0));
	}

	@Test
	public void testMissingCommentsAfterFileColumn() throws Exception {
		final String filename = "missing_comments_after_file_column_sdrf.txt";
		setTabDelimitedContent(filename);
		sdrfNavigator.setTabDelimitedContent(sdrf);
		assertFalse(validator.validateCommentColumns(qcContext, sdrfNavigator));
		assertEquals(3, qcContext.getErrorCount());
		assertEquals(
				"Required SDRF column 'Comment [TCGA Data Level]' is missing\t[archive proteinArchive]",
				qcContext.getErrors().get(0));
		assertEquals(
				"Required SDRF column 'Comment [TCGA Data Type]' is missing\t[archive proteinArchive]",
				qcContext.getErrors().get(1));
		assertEquals(
				"Required SDRF column 'Comment [TCGA File Type]' is missing\t[archive proteinArchive]",
				qcContext.getErrors().get(2));
	}

	@Test
	public void testValidCommentsAfterFileColumn() throws Exception {
		final String filename = "valid_comments_after_file_column_sdrf.txt";
		setTabDelimitedContent(filename);
		sdrfNavigator.setTabDelimitedContent(sdrf);
		assertTrue(validator.validateCommentColumns(qcContext, sdrfNavigator));
	}

	@Test
	public void validateDataFileColumnHeaders() throws Exception {
		assertTrue(validator.validateDataFileColumnHeaders(
				getValidSdrfHeader(), qcContext));
	}

	@Test
	public void validateDataFileColumnHeadersForMissingCommentColumns()
			throws Exception {
		assertFalse(validator.validateDataFileColumnHeaders(
				getInValidSdrfHeader(), qcContext));
		assertEquals(qcContext.getErrors().toString(), 2,
				qcContext.getErrorCount());
		assertTrue(qcContext
				.getErrors()
				.get(0)
				.startsWith(
						"An error occurred while validating SDRF for archive 'proteinArchive': SDRF header 'Image File'[23] does not have 'Comment [TCGA Include for Analysis]'"));
		assertTrue(qcContext
				.getErrors()
				.get(1)
				.startsWith(
						"An error occurred while validating SDRF for archive 'proteinArchive': SDRF header 'Derived Array Data Matrix File'[47] does not have 'Comment [TCGA Include for Analysis]'"));
	}

	/**
	 * Scenario 19: 'Image File', 'Array Data File', 'Derived Array Data File'
	 * and 'Derived Array Data Matrix File' columns are missing
	 * "Comment [TCGA Archive Name]" column
	 * 
	 * @throws Exception
	 */
	@Test
	public void validateDataFileColumnHeadersForMissingArchiveNameCommentColumns()
			throws Exception {

		final List invalidHeaders = new LinkedList<String>();
		invalidHeaders.addAll(getValidSdrfHeader());
		invalidHeaders.removeAll(Arrays
				.asList(AbstractSdrfValidator.COMMENT_ARCHIVE_NAME));

		assertFalse(validator.validateDataFileColumnHeaders(invalidHeaders,
				qcContext));
		assertEquals(qcContext.getErrors().toString(), 4,
				qcContext.getErrorCount());
		assertEquals(
				"An error occurred while validating SDRF for archive 'proteinArchive': SDRF header 'Image File'[31] does not have 'Comment [TCGA Archive Name]' column.\t[archive proteinArchive]",
				qcContext.getErrors().get(0));
		assertEquals(
				"An error occurred while validating SDRF for archive 'proteinArchive': SDRF header 'Array Data File'[37] does not have 'Comment [TCGA Archive Name]' column.\t[archive proteinArchive]",
				qcContext.getErrors().get(1));
		assertEquals(
				"An error occurred while validating SDRF for archive 'proteinArchive': SDRF header 'Derived Array Data File'[45] does not have 'Comment [TCGA Archive Name]' column.\t[archive proteinArchive]",
				qcContext.getErrors().get(2));
		assertEquals(
				"An error occurred while validating SDRF for archive 'proteinArchive': SDRF header 'Derived Array Data Matrix File'[53] does not have 'Comment [TCGA Archive Name]' column.\t[archive proteinArchive]",
				qcContext.getErrors().get(3));
	}

	/**
	 * Sets the SDRF Tab delimited content and header from the given filename
	 * 
	 * @param filename
	 *            the name of the file to use to populate the SDRF content and
	 *            header
	 * @throws IOException
	 */
	private void setTabDelimitedContent(final String filename)
			throws IOException,ParseException {
		final TabDelimitedFileParser tabDelimitedFileParser = new TabDelimitedFileParser();
		tabDelimitedFileParser.setTabDelimitedContent(sdrf);
		tabDelimitedFileParser.loadTabDelimitedContent(testDir + File.separator
				+ filename);
		tabDelimitedFileParser.loadTabDelimitedContentHeader();
	}

	private List<String> getValidSdrfHeader() {
		final String[] sdrfHeader = { "Source Name", "Material Type",
				"Term Source REF", "Provider", "Sample Name", "Material Type",
				"Term Source REF", "Comment [TCGA Biospecimen Type]",
				"Protocol REF", "Extract Name", "Material Type",
				"Term Source REF", "Protocol REF", "Extract Name",
				"Protocol REF", "Extract Name", "Protocol REF", "Array Name",
				"Array Design File", "Comment [TCGA Data Type]",
				"Comment [TCGA Data Level]", "Comment [TCGA File Type]",
				"Protocol REF", "Comment [TCGA Antibody Name]",
				"Annotations File", "Comment [TCGA Data Level]",
				"Comment [TCGA Data Type]", "Comment [TCGA File Type]",
				"Hybridization Name", "Protocol REF", "Scan Name",
				"Image File", "Comment [TCGA Include for Analysis]",
				"Comment [TCGA Data Type]", "Comment [TCGA Data Level]",
				"Comment [TCGA Archive Name]", "Comment [TCGA File Type]",
				"Comment [TCGA MD5]", "Array Data File",
				"Comment [TCGA Include for Analysis]",
				"Comment [TCGA Data Type]", "Comment [TCGA Data Level]",
				"Comment [TCGA Archive Name]", "Comment [TCGA File Type]",
				"Comment [TCGA MD5]", "Protocol REF",
				"Data Transformation Name", "Derived Array Data File",
				"Comment [TCGA Include for Analysis]",
				"Comment [TCGA Data Type]", "Comment [TCGA Data Level]",
				"Comment [TCGA Archive Name]", "Comment [TCGA File Type]",
				"Comment [TCGA MD5]", "Protocol REF", "Normalization Name",
				"Derived Array Data Matrix File",
				"Comment [TCGA Include for Analysis]",
				"Comment [TCGA Data Type]", "Comment [TCGA Data Level]",
				"Comment [TCGA Archive Name]", "Comment [TCGA File Type]",
				"Comment [TCGA MD5]" };
		return Arrays.asList(sdrfHeader);
	}

	private List<String> getInValidSdrfHeader() {
		final String[] sdrfHeader = { "Source Name", "Material Type",
				"Term Source REF", "Provider", "Sample Name", "Material Type",
				"Term Source REF", "Comment [TCGA Biospecimen Type]",
				"Protocol REF", "Extract Name", "Material Type",
				"Term Source REF", "Protocol REF", "Extract Name",
				"Protocol REF", "Extract Name", "Protocol REF", "Array Name",
				"Protocol REF", "Comment [TCGA Antibody Name]",
				"Hybridization Name", "Protocol REF", "Scan Name",
				"Image File", "Comment [TCGA Data Type]",
				"Comment [TCGA Data Level]", "Comment [TCGA Archive Name]",
				"Comment [TCGA File Type]", "Comment [TCGA MD5]",
				"Array Data File", "Comment [TCGA Data Type]",
				"Comment [TCGA Data Level]", "Comment [TCGA Archive Name]",
				"Comment [TCGA File Type]", "Comment [TCGA MD5]",
				"Comment [TCGA Include for Analysis]", "Protocol REF",
				"Data Transformation Name", "Derived Array Data File",
				"Comment [TCGA Data Type]", "Comment [TCGA Data Level]",
				"Comment [TCGA Archive Name]",
				"Comment [TCGA Include for Analysis]",
				"Comment [TCGA File Type]", "Comment [TCGA MD5]",
				"Protocol REF", "Normalization Name",
				"Derived Array Data Matrix File", "Comment [TCGA Data Type]",
				"Comment [TCGA Data Level]", "Comment [TCGA Archive Name]",
				"Comment [TCGA File Type]", "Comment [TCGA MD5]" };

		return Arrays.asList(sdrfHeader);
	}

	@Test
	public void testSampleNameColumn() throws IOException,ParseException {

		setTabDelimitedContent(proteinSDRFFileName);
		sdrfNavigator.setTabDelimitedContent(sdrf);

        final String uuid1 = "16211caa-d5f8-4c55-a3a4-be3355122480";
        final String uuid2 = "462b80e9-9015-43b7-b847-2fe3fdd384ea";

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat(uuid1);
            will(returnValue(true));
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat(uuid2);
            will(returnValue(true));
            one(mockedShippedBioQueries).isShippedBiospecimenShippedPortionUUIDValid(uuid1);
            will(returnValue(true));
            one(mockedShippedBioQueries).isShippedBiospecimenShippedPortionUUIDValid(uuid2);
            will(returnValue(true));
        }});

		assertTrue(validator.validateSampleNameColumn(sdrfNavigator, qcContext));
		assertTrue(qcContext.getErrorCount() == 0);
	}

	@Test
	public void testSampleNameColumnBadUUID() throws IOException,ParseException {
		setTabDelimitedContent(proteinSDRFFileName);
		sdrfNavigator.setTabDelimitedContent(sdrf);

        final String uuid1 = "16211caa-d5f8-4c55-a3a4-be3355122480";
        final String uuid2 = "462b80e9-9015-43b7-b847-2fe3fdd384ea";

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat(uuid1);
            will(returnValue(true));
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat(uuid2);
            will(returnValue(true));
            one(mockedShippedBioQueries).isShippedBiospecimenShippedPortionUUIDValid(uuid1);
            will(returnValue(true));
            one(mockedShippedBioQueries).isShippedBiospecimenShippedPortionUUIDValid(uuid2);
            will(returnValue(false));
        }});

		assertFalse(validator
				.validateSampleNameColumn(sdrfNavigator, qcContext));
		assertTrue(qcContext.getErrorCount() == 1);
		assertEquals(
				"An error occurred while validating SDRF for archive 'proteinArchive': SDRF "
						+ " 'Sample Name' column has a UUID which has not been submitted by the BCR yet, so data for it cannot be accepted."
                        + " The UUID value in the column is 462b80e9-9015-43b7-b847-2fe3fdd384ea\t[archive proteinArchive]",
				qcContext.getErrors().get(0));
		qcContext = new QcContext();
		qcContext.setArchive(archive);

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat(uuid1);
            will(returnValue(true));
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat(uuid2);
            will(returnValue(true));
            one(mockedShippedBioQueries).isShippedBiospecimenShippedPortionUUIDValid(uuid1);
            will(returnValue(false));
            one(mockedShippedBioQueries).isShippedBiospecimenShippedPortionUUIDValid(uuid2);
            will(returnValue(true));
        }});

		assertFalse(validator
				.validateSampleNameColumn(sdrfNavigator, qcContext));
		assertTrue(qcContext.getErrorCount() == 1);
		assertEquals(
				"An error occurred while validating SDRF for archive 'proteinArchive': SDRF "
						+ " 'Sample Name' column has a UUID which has not been submitted by the BCR yet, so data for it cannot be accepted."
                        + " The UUID value in the column is 16211caa-d5f8-4c55-a3a4-be3355122480\t[archive proteinArchive]",
				qcContext.getErrors().get(0));
	}

	@Test
	public void testSampleNameColumnBadMultipleUUID() throws IOException,ParseException {
		setTabDelimitedContent(proteinSDRFFileName);
		sdrfNavigator.setTabDelimitedContent(sdrf);

        final String uuid1 = "16211caa-d5f8-4c55-a3a4-be3355122480";
        final String uuid2 = "462b80e9-9015-43b7-b847-2fe3fdd384ea";

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat(uuid1);
            will(returnValue(true));
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat(uuid2);
            will(returnValue(true));
            one(mockedShippedBioQueries).isShippedBiospecimenShippedPortionUUIDValid(uuid1);
            will(returnValue(false));
            one(mockedShippedBioQueries).isShippedBiospecimenShippedPortionUUIDValid(uuid2);
            will(returnValue(false));
        }});

		assertFalse(validator
				.validateSampleNameColumn(sdrfNavigator, qcContext));
		assertTrue(qcContext.getErrorCount() == 2);
		assertEquals(
				"An error occurred while validating SDRF for archive 'proteinArchive': SDRF "
						+ " 'Sample Name' column has a UUID which has not been submitted by the BCR yet, so data for it cannot be accepted."
                        + " The UUID value in the column is 16211caa-d5f8-4c55-a3a4-be3355122480\t[archive proteinArchive]",
				qcContext.getErrors().get(0));
		assertEquals(
				"An error occurred while validating SDRF for archive 'proteinArchive': SDRF "
						+ " 'Sample Name' column has a UUID which has not been submitted by the BCR yet, so data for it cannot be accepted."
                        + " The UUID value in the column is 462b80e9-9015-43b7-b847-2fe3fdd384ea\t[archive proteinArchive]",
				qcContext.getErrors().get(1));
	}

	@Test
	public void testSampleNameColumnBadComment() throws IOException,ParseException {
		setTabDelimitedContent(proteinSDRFFileName);

		String[] sdrfRow = sdrf.getTabDelimitedContents().get(3);
		sdrfRow[7] = "garbage";
		sdrf.getTabDelimitedContents().put(3, sdrfRow);
		sdrfNavigator.setTabDelimitedContent(sdrf);

        final String uuid1 = "16211caa-d5f8-4c55-a3a4-be3355122480";
        final String uuid2 = "462b80e9-9015-43b7-b847-2fe3fdd384ea";

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat(uuid1);
            will(returnValue(true));
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat(uuid2);
            will(returnValue(true));
            one(mockedShippedBioQueries).isShippedBiospecimenShippedPortionUUIDValid(uuid1);
            will(returnValue(true));
            one(mockedShippedBioQueries).isShippedBiospecimenShippedPortionUUIDValid(uuid2);
            will(returnValue(true));
        }});

		assertFalse(validator
				.validateSampleNameColumn(sdrfNavigator, qcContext));
		assertTrue(qcContext.getErrorCount() == 1);
		assertEquals(
				"An error occurred while validating SDRF for archive 'proteinArchive':"
						+ " SDRF 'Comment [TCGA Biospecimen Type]'  must either be a blank line '->' or an"
						+ " UUID , but in this case it is :garbage	[archive proteinArchive]",
				qcContext.getErrors().get(0));
	}

	@Test
	public void testProteinArraySdrfValidatorNumberTokensValid()
			throws Exception {
		setTabDelimitedContent(proteinSDRFFileName);
		sdrfNavigator.setTabDelimitedContent(sdrf);

		assertTrue(validator.validateHeaderTokenCount(sdrf, qcContext));
	}

	@Test
	public void testProteinArraySdrfValidatorNumberTokensLess()
			throws Exception {
		setTabDelimitedContent(proteinSDRFFileName);
		sdrfNavigator.setTabDelimitedContent(sdrf);
		final String errorMessage = "An error occurred while validating SDRF for archive 'proteinArchive':"
				+ " SDRF  A row  2 in the SDRF file contains a number of tokens different than the number"
				+ " of headers in the file	[archive proteinArchive]";
		String[] values = sdrfNavigator.getTabDelimitedContent()
				.getTabDelimitedContents().get(2);
		String[] shortenedValues = Arrays.copyOf(values, values.length - 2);
		ArrayList<String> valueList = new ArrayList<String>(
				Arrays.asList(shortenedValues));
		sdrfNavigator.getTabDelimitedContent().getTabDelimitedContents()
				.put(2, valueList.toArray(new String[] {}));
		assertFalse(validator.validateHeaderTokenCount(
				sdrfNavigator.getTabDelimitedContent(), qcContext));
		assertTrue(qcContext.getErrorCount() == 1);
		assertTrue(qcContext.getErrors().get(0).equals(errorMessage));
	}

	@Test
	public void testProteinArraySdrfValidatorNumberTokensMore()
			throws Exception {
		setTabDelimitedContent(proteinSDRFFileName);
		sdrfNavigator.setTabDelimitedContent(sdrf);
		final String errorMessage1 = "An error occurred while validating SDRF for archive 'proteinArchive': "
				+ "SDRF  A row  2 in the SDRF file contains a number of tokens different than the number of headers"
				+ " in the file	[archive proteinArchive]";

		final String errorMessage2 = "An error occurred while validating SDRF for archive 'proteinArchive': "
				+ "SDRF  A row  3 in the SDRF file contains a number of tokens different than the number of headers"
				+ " in the file	[archive proteinArchive]";

		// mod 1
		String[] values = sdrfNavigator.getTabDelimitedContent()
				.getTabDelimitedContents().get(2);
		ArrayList<String> valueList = new ArrayList<String>(
				Arrays.asList(values));
		valueList.add("garbage");
		sdrfNavigator.getTabDelimitedContent().getTabDelimitedContents()
				.put(2, valueList.toArray(new String[] {}));
		// mod2
		values = sdrfNavigator.getTabDelimitedContent()
				.getTabDelimitedContents().get(3);
		valueList = new ArrayList<String>(Arrays.asList(values));
		valueList.add("garbage2");
		sdrfNavigator.getTabDelimitedContent().getTabDelimitedContents()
				.put(3, valueList.toArray(new String[] {}));

		assertFalse(validator.validateHeaderTokenCount(
				sdrfNavigator.getTabDelimitedContent(), qcContext));
		assertTrue(qcContext.getErrorCount() == 2);
		assertTrue(qcContext.getErrors().get(0).equals(errorMessage1));
		assertTrue(qcContext.getErrors().get(1).equals(errorMessage2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testProteinArraySdrfValidatorBadInput() throws Exception {
		sdrf.setTabDelimitedHeader(null);
		assertTrue(validator.validateHeaderTokenCount(sdrf, qcContext));
	}

	@Test
	public void testTCGAIncludeForAnalysisCommentValidValue()
			throws IOException {
		assertTrue(validator.validateColumnValue(
				AbstractSdrfHandler.COMMENT_INCLUDE_FOR_ANALYSIS, "YES", 1,
				qcContext));
		assertTrue(validator.validateColumnValue(
				AbstractSdrfHandler.COMMENT_INCLUDE_FOR_ANALYSIS, "No", 1,
				qcContext));
		assertTrue(validator.validateColumnValue(
				AbstractSdrfHandler.COMMENT_INCLUDE_FOR_ANALYSIS, "->", 1,
				qcContext));
	}

	@Test
	public void testTCGAIncludeForAnalysisCommentInValidValue()
			throws IOException {
		assertFalse(validator.validateColumnValue(
				AbstractSdrfHandler.COMMENT_INCLUDE_FOR_ANALYSIS, "Invalid", 1,
				qcContext));
		assertEquals(1, qcContext.getErrors().size());
		assertTrue(qcContext
				.getErrors()
				.get(0)
				.startsWith(
						"An error occurred while validating SDRF for archive 'proteinArchive': SDRF Line 1:Comment [TCGA Include for Analysis] value is 'Invalid'. It should be one of [[yes, no, ->]] "));

	}

	@Test
	public void testTCGADataTypesComment() throws IOException {
		assertTrue(validator.validateColumnValue(
				AbstractSdrfHandler.COMMENT_DATA_TYPE, "Expression-Protein", 1,
				qcContext));
		assertTrue(validator.validateColumnValue(
				AbstractSdrfHandler.COMMENT_DATA_TYPE,
				"Annotations-Platform Design", 1, qcContext));
		assertTrue(validator.validateColumnValue(
				AbstractSdrfHandler.COMMENT_DATA_TYPE,
				"Annotations-Antibodies", 1, qcContext));
		assertTrue(validator.validateColumnValue(
				AbstractSdrfHandler.COMMENT_DATA_TYPE, "->", 1, qcContext));
	}

	@Test
	public void TCGADataTypesCommenInValidValue() throws IOException {
		assertFalse(validator.validateColumnValue(
				AbstractSdrfHandler.COMMENT_DATA_TYPE, "Invalid", 1, qcContext));
		assertEquals(1, qcContext.getErrors().size());
		assertTrue(qcContext
				.getErrors()
				.get(0)
				.startsWith(
						"An error occurred while validating SDRF for archive 'proteinArchive': SDRF Line 1:Comment [TCGA Data Type] value is 'Invalid'. It should be one of [[Annotations-Platform Design, Expression-Protein, Annotations-Antibodies, ->]] 	[archive proteinArchive]"));
	}

	@Test
	public void testValidateBarcodesAndUuids() throws IOException,
			ProcessorException {
		// should always be true regardless of the arguments
		assertTrue(validator.validateBarcodesAndUuids(qcContext, sdrfNavigator));
		assertTrue(validator.validateBarcodesAndUuids(null, null));
		assertTrue(validator.validateBarcodesAndUuids(qcContext, null));
		assertTrue(validator.validateBarcodesAndUuids(null, sdrfNavigator));
	}

    /**
     * Scenario 13 Invalid SDRF:
     *
     * One row in the SDRF for "Comment [TCGA Biospecimen Type]" is "Shipped Portion" but the value for "Sample Name" column is "Control"
     *
     * @throws Exception
     */
    @Test
    public void testProteinArraySdrfWrongCommentTcgaBiospecimenTypeValueForControlSampleName() throws Exception {

        final String filename = "scenario_13_wrong_sample_name_value_for_shipped_portion_sdrf.txt";
        setTabDelimitedContent(filename);
        sdrfNavigator.setTabDelimitedContent(sdrf);

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat("Control");
            will(returnValue(false));
        }});

        assertTrue(validator.headersAreValid(sdrfNavigator, qcContext));
        assertFalse(validator.runSpecificValidations(qcContext, sdrfNavigator));
        assertEquals(qcContext.getErrors().toString(), 1, qcContext.getErrorCount());
        assertEquals("An error occurred while validating SDRF for archive 'proteinArchive': " +
                "SDRF  'Sample Name' column has a UUID with an invalid format. The UUID value in the column is Control\t[archive proteinArchive]",
                qcContext.getErrors().get(0));
    }

}
