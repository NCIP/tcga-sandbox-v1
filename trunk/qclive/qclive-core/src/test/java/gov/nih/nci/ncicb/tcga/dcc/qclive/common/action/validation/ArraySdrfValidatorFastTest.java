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
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContentImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedContentNavigator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeTumorValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
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
 * Test class for ArraySdrfValidator
 * 
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class ArraySdrfValidatorFastTest {

	private final Mockery context = new JUnit4Mockery();
    private QcLiveBarcodeAndUUIDValidator mockQcLiveBarcodeAndUUIDValidator = context.mock(QcLiveBarcodeAndUUIDValidator.class);
    
	private ArraySdrfValidator validator = new ArraySdrfValidator();

	private static final String SAMPLES_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	private String testDir = SAMPLES_DIR + "qclive/sdrfValidator";
	private TabDelimitedContentNavigator sdrfNavigator = new TabDelimitedContentNavigator();
	private TabDelimitedContentNavigator deprecatedSdrfNavigator = new TabDelimitedContentNavigator();
	private Map<Integer, String[]> sdrfValues = new HashMap<Integer, String[]>();
	private Map<Integer, String[]> deprecatedSdrvValues = new HashMap<Integer, String[]>();
	private String[] sdrfHeaders = new String[13];
	private String[] sdrfWithDeprecatedHeaders = new String[15];
	private TabDelimitedContent sdrf = new TabDelimitedContentImpl();
	private TabDelimitedContent deprecatedSdrf = new TabDelimitedContentImpl();
	private Archive archive = new Archive();	
	
	private Archive deprecatedArchive = new Archive();
	private QcContext qcContext = new QcContext();

	@Before
	public void setUp() throws IOException {
		validator.setBarcodeTumorValidator(new BarcodeTumorValidator() {
			public boolean barcodeIsValidForTumor(final String barcode,
					final String tumorAbbreviation) {
				return tumorAbbreviation.equals("fakeTumor")
						&& barcode.startsWith("TCGA-00");
			}
		});
		validator.setQcLiveBarcodeAndUUIDValidator(mockQcLiveBarcodeAndUUIDValidator);
		archive.setSdrfFile(new File("test.sdrf.txt"));
		qcContext.setArchive(archive);
		archive.setTumorType("fakeTumor");
		qcContext.setCenterConvertedToUUID(false);
		// SDRF With Valid Headers
		sdrfValues.put(0, sdrfHeaders);

		sdrfValues.put(1, new String[] {/* "->", */"->", "->",
				"TCGA-00-0000-00A-00D-0000-00", "afile.txt",
				"center_disease.platform.Level_1.1.0.0", "Level 1", "Fake",
				"yes", "matrix.txt", "center_disease.platform.Level_2.1.0.0",
				"Level 2", "Type", "yes" });

		sdrfValues.put(2, new String[] {/* "MGED Ontology", */"a", "b",
				"TCGA-00-1111-00A-00D-0000-00", "afile.txt",
				"center_disease.platform.Level_1.1.0.0", "Level 1", "Fake",
				"yes", "matrix.txt", "center_disease.platform.Level_2.1.0.0",
				"Level 2", "Type", "yes" });

		sdrfHeaders[0] = "Source Name";
		sdrfHeaders[1] = "Sample Name";
		sdrfHeaders[2] = "Extract Name";
		sdrfHeaders[3] = "Array Data File";
		sdrfHeaders[4] = "Comment [TCGA Archive Name]";
		sdrfHeaders[5] = "Comment [TCGA Data Level]";
		sdrfHeaders[6] = "Comment [TCGA Data Type]";
		sdrfHeaders[7] = "Comment [TCGA Include for Analysis]";
		sdrfHeaders[8] = "Array Data Matrix File";
		sdrfHeaders[9] = "Comment [TCGA Archive Name]";
		sdrfHeaders[10] = "Comment [TCGA Data Level]";
		sdrfHeaders[11] = "Comment [TCGA Data Type]";
		sdrfHeaders[12] = "Comment [TCGA Include for Analysis]";
		sdrf.setTabDelimitedContents(sdrfValues);
		sdrf.setTabDelimitedHeader(sdrfHeaders);
		sdrfNavigator.setTabDelimitedContent(sdrf);
		archive.setRealName("a");
		archive.setArchiveFile(new File(testDir + ".tar.gz"));
		archive.setArchiveType(Archive.TYPE_MAGE_TAB);
		archive.setSdrf(sdrf);

		// SDRF with Deprecated Headers
		deprecatedSdrvValues.put(0, sdrfWithDeprecatedHeaders);
		deprecatedSdrvValues.put(1, new String[] { "->", "->", "->", "->",
				"TCGA-00-0000-00A-00D-0000-00", "afile.txt",
				"center_disease.platform.Level_1.1.0.0", "Level 1", "Fake",
				"yes", "matrix.txt", "center_disease.platform.Level_2.1.0.0",
				"Level 2", "Type", "yes" });
		deprecatedSdrvValues.put(2, new String[] { "MGED Ontology", "->", "a",
				"b", "TCGA-00-1111-00A-00D-0000-00", "afile.txt",
				"center_disease.platform.Level_1.1.0.0", "Level 1", "Fake",
				"yes", "matrix.txt", "center_disease.platform.Level_2.1.0.0",
				"Level 2", "Type", "yes" });
		sdrfWithDeprecatedHeaders[0] = "Term Source REF";
		sdrfWithDeprecatedHeaders[1] = "Extract Term Source REF";
		sdrfWithDeprecatedHeaders[2] = "Source Name";
		sdrfWithDeprecatedHeaders[3] = "Sample Name";
		sdrfWithDeprecatedHeaders[4] = "Extract Name";
		sdrfWithDeprecatedHeaders[5] = "Array Data File";
		sdrfWithDeprecatedHeaders[6] = "Comment [TCGA Archive Name]";
		sdrfWithDeprecatedHeaders[7] = "Comment [TCGA Data Level]";
		sdrfWithDeprecatedHeaders[8] = "Comment [TCGA Data Type]";
		sdrfWithDeprecatedHeaders[9] = "Comment [TCGA Include for Analysis]";
		sdrfWithDeprecatedHeaders[10] = "Array Data Matrix File";
		sdrfWithDeprecatedHeaders[11] = "Comment [TCGA Archive Name]";
		sdrfWithDeprecatedHeaders[12] = "Comment [TCGA Data Level]";
		sdrfWithDeprecatedHeaders[13] = "Comment [TCGA Data Type]";
		sdrfWithDeprecatedHeaders[14] = "Comment [TCGA Include for Analysis]";
		deprecatedSdrf.setTabDelimitedContents(sdrfValues);
		deprecatedSdrf.setTabDelimitedHeader(sdrfWithDeprecatedHeaders);
		deprecatedSdrfNavigator.setTabDelimitedContent(deprecatedSdrf);
		deprecatedArchive.setRealName("deprecated_a");
		deprecatedArchive.setArchiveFile(new File(testDir
				+ "with_deprecated_headers.tar.gz"));
		deprecatedArchive.setArchiveType(Archive.TYPE_MAGE_TAB);
		deprecatedArchive.setSdrf(deprecatedSdrf);
	}

	@Test
	public void testHeadersAreValid() throws Processor.ProcessorException {
		assertTrue(validator.headersAreValid(sdrfNavigator, qcContext));
		assertEquals(0, qcContext.getErrorCount());
	}

	/*
	 * Tests that error message is generated
	 */
	@Test
	public void testFailDeprcatedSDRFColumns()
			throws Processor.ProcessorException {

		assertFalse(validator.headersAreValid(deprecatedSdrfNavigator,
                qcContext));
		assertTrue(qcContext.getErrorCount() > 0);
	}

	@Test
	public void test() throws Processor.ProcessorException {

        context.checking(new Expectations() {{
            allowing(mockQcLiveBarcodeAndUUIDValidator).validate("TCGA-00-0000-00A-00D-0000-00", qcContext, "test.sdrf.txt", true);
            will(returnValue(true));
        }});

		boolean valid = validator.execute(archive, qcContext);
		assertTrue(qcContext.getErrors().toString(), valid);
	}

	@Test
	public void testNoExtractName() throws Processor.ProcessorException {
		// sdrfHeaders[3] = "Blah";
		sdrfHeaders[2] = "Blah";
		boolean valid = validator.execute(archive, qcContext);
		assertFalse(qcContext.getErrors().toString(), valid);
		assertEquals(2, qcContext.getErrorCount());
		assertEquals(
				"An error occurred while validating SDRF for archive 'a': SDRF contains an invalid header: 'Blah'	[archive a]",
				qcContext.getErrors().get(0));
		assertEquals(
                "Required SDRF column 'Extract Name' is missing\t[archive a]",
                qcContext.getErrors().get(1));
	}

	@Test
	public void testMissingComment() throws Processor.ProcessorException {
		// sdrfHeaders[5] = "Comment [Not Right]";
		sdrfHeaders[4] = "Comment [Not Right]";

		boolean valid = validator.execute(archive, qcContext);
		assertFalse(valid);
		assertEquals(1, qcContext.getErrorCount());
		assertEquals(
				"Required SDRF column 'Comment [TCGA Archive Name]' is missing	[archive a]",
				qcContext.getErrors().get(0));
	}

	@Test
	public void testWrongFileType() throws Processor.ProcessorException {
		// sdrfValues.get(1)[6] = "Level 2";
		sdrfValues.get(1)[5] = "Level 2";
		boolean valid = validator.execute(archive, qcContext);
		assertFalse(valid);
		assertEquals(1, qcContext.getErrorCount());

		assertEquals(
				"An error occurred while validating SDRF for archive 'a': SDRF line 2: afile.txt was marked as 'Level 2' but it is within archive with level 'Level 1'	[archive a]",
				qcContext.getErrors().get(0));
	}

	@Test
	public void testMismatchedLevel() throws Processor.ProcessorException {
		// sdrfValues.get(1)[11] = "Level 3";
		sdrfValues.get(1)[10] = "Level 3";
		boolean valid = validator.execute(archive, qcContext);
		assertFalse(valid);
		assertEquals(1, qcContext.getErrorCount());
		assertEquals(
				"An error occurred while validating SDRF for archive 'a': SDRF line 2: matrix.txt was marked as 'Level 3' but it is within archive with level 'Level 2'	[archive a]",
				qcContext.getErrors().get(0));
	}

	@Test
	public void testBadLevel() throws Processor.ProcessorException {
		// sdrfValues.get(1)[6] = "Level Squirrel";
		sdrfValues.get(1)[5] = "Level Squirrel";
		boolean valid = validator.execute(archive, qcContext);
		assertFalse(valid);
		assertEquals(2, qcContext.getErrorCount());
		assertEquals(
				"SDRF line 2: value for 'Comment [TCGA Data Level]' must be in the format ''Level N' where N is a valid level number', but found 'Level Squirrel'	[archive a]",
				qcContext.getErrors().get(0));
		assertEquals(
				"An error occurred while validating SDRF for archive 'a': SDRF line 2: afile.txt was marked as 'Level Squirrel' but it is within archive with level 'Level 1'	[archive a]",
				qcContext.getErrors().get(1));
	}

	@Test
	public void testIncludeAfterExclude() throws Processor.ProcessorException {
		// change "include for analysis" to no for level 1 file but still list
		// level 2 file
		sdrfValues.put(1, new String[] {/* "->", */"->", "->",
				"TCGA-00-0000-00A-00D-0000-00", "afile.txt",
				"center_disease.platform.Level_1.1.0.0", "Level 1", "Fake",
				"no", "matrix.txt", "center_disease.platform.Level_2.1.0.0",
				"Level 2", "Type", "yes" });
		boolean valid = validator.execute(archive, qcContext);
		assertFalse(valid);
		assertEquals(1, qcContext.getErrorCount());
		// assertEquals("An error occurred while validating SDRF for archive 'a': SDRF line '2' was marked for exclusion from analysis in column '4' but column '9' contains a file name rather than '->'	[archive a]",
		// qcContext.getErrors().get(0));
		assertEquals(
				"An error occurred while validating SDRF for archive 'a': SDRF line '2' was marked for exclusion from analysis in column '3' but column '8' contains a file name rather than '->'	[archive a]",
				qcContext.getErrors().get(0));
	}

	@Test
	public void testWrongBarcodeForTumor() throws Processor.ProcessorException {

        context.checking(new Expectations() {{
            allowing(mockQcLiveBarcodeAndUUIDValidator).validate("TCGA-99-0000-00A-00D-0000-00", qcContext, "test.sdrf.txt", true);
            will(returnValue(true));
        }});

		// sdrfValues.get(1)[3] = "TCGA-99-0000-00A-00D-0000-00";
		sdrfValues.get(1)[2] = "TCGA-99-0000-00A-00D-0000-00";
		assertFalse(validator.execute(archive, qcContext));
		assertEquals(1, qcContext.getErrorCount());
		assertEquals(
				"An error occurred while validating SDRF for archive 'a': SDRF line 1: Barcode 'TCGA-99-0000-00A-00D-0000-00' does not belong to the disease set for tumor type 'fakeTumor'	[archive a]",
				qcContext.getErrors().get(0));
	}

	@Test(expected = Processor.ProcessorException.class)
	public void testMalformedSDRF() throws Processor.ProcessorException {
		// this row is short one value
		sdrfValues.put(2, new String[]{/* "MGED Ontology", */"->", "->",
                "TCGA-00-1111-00A-00D-0000-00", "afile.txt",
                "center_disease.platform.Level_1.1.0.0", "Level 1", "Fake",
                "yes", "matrix.txt", "center_disease.platform.Level_2.1.0.0",
                "Level 2", "Type"});
		validator.execute(archive, qcContext);
	}

	@Test
	public void testValidateBarcodeAndArchiveNamesDiffSerialNumbers()
			throws Processor.ProcessorException {

		// notice that the same barcode for same level is files with different
		// serial number
		sdrfValues.put(3, new String[] {/* "MGED Ontology", */"->", "->",
				"TCGA-00-0000-00A-00D-0000-77", "afile1.txt",
				"center_disease.platform.Level_1.1.0.0", "Level 1", "Fake",
				"yes", "matrix.txt", "center_disease.platform.Level_2.1.0.0",
				"Level 2", "Type" });

		sdrfValues.put(4, new String[] {/* "MGED Ontology", */"->", "->",
				"TCGA-00-0000-00A-00D-0000-77", "afile1.txt",
				"center_disease.platform.Level_1.2.0.0", "Level 1", "Fake",
				"yes", "matrix.txt", "center_disease.platform.Level_2.1.0.0",
				"Level 2", "Type" });

		final TabDelimitedContent sdrf = archive.getSdrf();
		final TabDelimitedContentNavigator sdrfNavigator = new TabDelimitedContentNavigator();
		sdrfNavigator.setTabDelimitedContent(sdrf);

		validator.checkExtractNamePerArchive(qcContext, sdrfNavigator);
		assertEquals(1, qcContext.getWarningCount());
		assertEquals(
                "Extract name TCGA-00-0000-00A-00D-0000-77 is included " +
                "in both center_disease.platform.Level_1.1.0.0 and " +
                "center_disease.platform.Level_1.2.0.0	[a]",
                qcContext.getWarnings().get(0));
	}

	@Test
	public void testValidateBarcodeAndArchiveForSameSerialIndex()
			throws Processor.ProcessorException {

		// notice that the revision number for both the level 1 archives are
		// same , since they belong to same
		// serial numbers, there shouldn't be any warning

		sdrfValues.put(3, new String[] { "MGED Ontology", "->", "->",
				"TCGA-00-0000-00A-00D-0000-77", "afile1.txt",
				"center_disease.platform.Level_1.5.1.0", "Level 1", "Fake",
				"yes", "matrix.txt", "center_disease.platform.Level_1.1.0.0",
				"Level 2", "Type" });

		sdrfValues.put(4, new String[] { "MGED Ontology", "->", "->",
				"TCGA-00-0000-00A-00D-0000-77", "afile1.txt",
				"center_disease.platform.Level_1.5.2.0", "Level 1", "Fake",
				"yes", "matrix.txt", "center_disease.platform.Level_1.1.0.0",
				"Level 2", "Type" });

		final TabDelimitedContent sdrf = archive.getSdrf();
		final TabDelimitedContentNavigator sdrfNavigator = new TabDelimitedContentNavigator();
		sdrfNavigator.setTabDelimitedContent(sdrf);

		validator.checkExtractNamePerArchive(qcContext, sdrfNavigator);
		assertEquals(0, qcContext.getWarningCount());
	}

	@Test
	public void testValidateWithControlSamples() {
		sdrfValues.put(3, new String[] { "MGED Ontology", "->", "->",
				"Promega Control", "afile1.txt",
				"center_disease.platform.Level_1.1.0.0", "Level 1", "Fake",
				"yes", "matrix.txt", "center_disease.platform.Level_2.1.0.0",
				"Level 2", "Type" });

		sdrfValues.put(4, new String[] { "MGED Ontology", "->", "->",
				"Promega Control", "afile1.txt",
				"center_disease.platform.Level_1.2.0.0", "Level 1", "Fake",
				"yes", "matrix.txt", "center_disease.platform.Level_2.1.0.0",
				"Level 2", "Type" });

		final TabDelimitedContent sdrf = archive.getSdrf();
		final TabDelimitedContentNavigator sdrfNavigator = new TabDelimitedContentNavigator();
		sdrfNavigator.setTabDelimitedContent(sdrf);

		validator.checkExtractNamePerArchive(qcContext, sdrfNavigator);
		assertEquals(0, qcContext.getWarningCount());
	}

	@Test
	public void testValidateBarcodes() throws Processor.ProcessorException {

        context.checking(new Expectations() {{
            allowing(mockQcLiveBarcodeAndUUIDValidator).validate("TCGA-00-0000-00A-00D-0000-00", qcContext, "test.sdrf.txt", true);
            will(returnValue(true));
        }});

		// this should be fine: 1st has full barcode, 2nd has control plus
		// Source Name and Sample Name
		sdrfValues.put(1, new String[] {/* "->", */"->", "->",
				"TCGA-00-0000-00A-00D-0000-00", "afile.txt",
				"center_disease.platform.Level_1.1.0.0", "Level 1", "Fake",
				"yes", "matrix.txt", "center_disease.platform.Level_2.1.0.0",
				"Level 2", "Type", "yes" });
		sdrfValues.put(2, new String[]{/* "MGED Ontology", */"a", "b",
                "CONTROL", "afile.txt",
                "center_disease.platform.Level_1.1.0.0", "Level 1", "Fake",
                "yes", "matrix.txt", "center_disease.platform.Level_2.1.0.0",
                "Level 2", "Type", "yes"});

		assertTrue(validator.validateBarcodesAndUuids(qcContext, sdrfNavigator));
	}

	@Test
	public void testValidateUuids() throws Processor.ProcessorException {
		// this should be fine: 1st has valid uuid, 2nd has control plus Source
		// Name and Sample Name
		sdrfValues.put(1, new String[] {/* "->", */"->", "->",
				"170aa168-575a-4ea2-a910-a7523501d659", "afile.txt",
				"center_disease.platform.Level_1.1.0.0", "Level 1", "Fake",
				"yes", "matrix.txt", "center_disease.platform.Level_2.1.0.0",
				"Level 2", "Type", "yes" });
		sdrfValues.put(2, new String[] {/* "MGED Ontology", */"a", "b",
				"CONTROL", "afile.txt",
				"center_disease.platform.Level_1.1.0.0", "Level 1", "Fake",
				"yes", "matrix.txt", "center_disease.platform.Level_2.1.0.0",
				"Level 2", "Type", "yes" });

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUuid("170aa168-575a-4ea2-a910-a7523501d659", qcContext, "test.sdrf.txt", true);
            will(returnValue(true));
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat("170aa168-575a-4ea2-a910-a7523501d659");
            will(returnValue(true));
        }});

		assertTrue(validator.validateBarcodesAndUuids(qcContext, sdrfNavigator));
	}

	@Test
	public void testValidateInvalidUuid() throws Processor.ProcessorException {

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat("wrong uuid");
            will(returnValue(false));
            one(mockQcLiveBarcodeAndUUIDValidator).validateUuid("wrong uuid", qcContext, "test.sdrf.txt", true);
            will(returnValue(false));
        }});

		// this should fail: first uuid is wrong, 2nd has control plus Source
		// Name and Sample Name
		// Then first uuid is assumed to be a control row with missing data
		sdrfValues.put(1, new String[]{/* "->", */"->", "->", "wrong uuid",
                "afile.txt", "center_disease.platform.Level_1.1.0.0",
                "Level 1", "Fake", "yes", "matrix.txt",
                "center_disease.platform.Level_2.1.0.0", "Level 2", "Type",
                "yes"});
		sdrfValues.put(2, new String[] {/* "MGED Ontology", */"a", "b",
				"CONTROL", "afile.txt",
				"center_disease.platform.Level_1.1.0.0", "Level 1", "Fake",
				"yes", "matrix.txt", "center_disease.platform.Level_2.1.0.0",
				"Level 2", "Type", "yes" });
		assertFalse(validator
				.validateBarcodesAndUuids(qcContext, sdrfNavigator));
		assertEquals(qcContext.getErrors().toString(), 1,
				qcContext.getErrorCount());
		assertEquals(
                "An error occurred while validating SDRF for archive 'a': SDRF line 2: Source Name and Sample "
                        + "Name columns must be included for internal controls and non-BCR analytes	[archive a]",
                qcContext.getErrors().get(0));
	}

	@Test
	public void testValidateInvalidBarcodes()
			throws Processor.ProcessorException {

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validate("TCGA-00-0000-00A-00D", qcContext, "test.sdrf.txt", true);
            will(returnValue(false));
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat("CONTROL");
            will(returnValue(false));
            one(mockQcLiveBarcodeAndUUIDValidator).validateUuid("CONTROL", qcContext, "test.sdrf.txt", true);
            will(returnValue(false));
        }});

		// this should fail: first barcode is partial, 2nd is control without
		// required columns
		sdrfValues.put(1, new String[] {/* "->", */"->", "->",
				"TCGA-00-0000-00A-00D", "afile.txt",
				"center_disease.platform.Level_1.1.0.0", "Level 1", "Fake",
				"yes", "matrix.txt", "center_disease.platform.Level_2.1.0.0",
				"Level 2", "Type", "yes" });
		sdrfValues.put(2, new String[] {/* "MGED Ontology", */"->", "->",
				"CONTROL", "afile.txt",
				"center_disease.platform.Level_1.1.0.0", "Level 1", "Fake",
				"yes", "matrix.txt", "center_disease.platform.Level_2.1.0.0",
				"Level 2", "Type", "yes" });

		assertFalse(validator
                .validateBarcodesAndUuids(qcContext, sdrfNavigator));
		assertEquals(qcContext.getErrors().toString(), 1,
				qcContext.getErrorCount());
		assertEquals(
				"An error occurred while validating SDRF for archive 'a': SDRF line 3: Source Name and Sample Name columns must be included for internal controls and non-BCR analytes	[archive a]",
				qcContext.getErrors().get(0));
	}

    @Test
    public void testBarcodesWithLeadingOrTrailingWhiteSpace()
            throws Processor.ProcessorException {

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validate("TCGA-00-0000-00A-00D-0000-00 ", qcContext, "test.sdrf.txt", true);
            will(returnValue(false));
        }});

        sdrfValues.put(1, new String[] {/* "->", */"->", "->",
                "TCGA-00-0000-00A-00D-0000-00 ", "afile.txt",
                "center_disease.platform.Level_1.1.0.0", "Level 1", "Fake",
                "yes", "matrix.txt", "center_disease.platform.Level_2.1.0.0",
                "Level 2", "Type", "yes" });
        sdrfValues.put(2, new String[] {/* "MGED Ontology", */"a", "b",
                " TCGA-00-1111-00A-00D-0000-00", "afile.txt",
                "center_disease.platform.Level_1.1.0.0", "Level 1", "Fake",
                "yes", "matrix.txt", "center_disease.platform.Level_2.1.0.0",
                "Level 2", "Type", "yes" });

        assertFalse(validator.validateBarcodesAndUuids(qcContext, sdrfNavigator));
    }

    @Test
    public void testWithControlRowsSoundcheck() throws Processor.ProcessorException {
        qcContext.setStandaloneValidator(true);
        qcContext.setCenterConvertedToUUID(false);
        
        sdrfValues.put(1, new String[] {"->", "->",
                "TCGA-00-0000-00A-00D-0000-00", "afile.txt",
                "center_disease.platform.Level_1.1.0.0", "Level 1", "Fake",
                "yes", "matrix.txt", "center_disease.platform.Level_2.1.0.0",
                "Level 2", "Type", "yes" });
        // second row has control row
        sdrfValues.put(2, new String[] {"a", "b",
                "This Is A Control", "afile.txt",
                "center_disease.platform.Level_1.1.0.0", "Level 1", "Fake",
                "yes", "matrix.txt", "center_disease.platform.Level_2.1.0.0",
                "Level 2", "Type", "yes" });

        sdrfValues.put(3, new String[] {"a", "b",
                "This Is A Control", "afile.txt",
                "center_disease.platform.Level_1.2.0.0", "Level 1", "Fake",
                "yes", "matrix.txt", "center_disease.platform.Level_2.2.0.0",
                "Level 2", "Type", "yes" });

        // should not pass control extract name to validate
        final Map<String, Boolean> barcodeValidityMap = new HashMap<String, Boolean>();
        barcodeValidityMap.put("TCGA-00-0000-00A-00D-0000-00", true);

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).batchValidateReportIndividualResults(Arrays.asList("TCGA-00-0000-00A-00D-0000-00"),
                    qcContext, "test.sdrf.txt", true);
            will(returnValue(barcodeValidityMap));
        }});

        boolean isValid = validator.doWork(archive, qcContext);
        assertTrue(qcContext.getErrors().toString(), isValid);
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(0, qcContext.getWarningCount());
    }

    @Test
    public void testWithControlRowsSoundcheckUUID() throws Processor.ProcessorException {
        sdrfHeaders = new String[14];
        sdrf.setTabDelimitedHeader(sdrfHeaders);
       sdrfValues.put(0, sdrfHeaders);

        sdrfHeaders[0] = "Source Name";
		sdrfHeaders[1] = "Sample Name";
		sdrfHeaders[2] = "Extract Name";
        sdrfHeaders[3] = "Comment [TCGA Barcode]";
		sdrfHeaders[4] = "Array Data File";
		sdrfHeaders[5] = "Comment [TCGA Archive Name]";
		sdrfHeaders[6] = "Comment [TCGA Data Level]";
		sdrfHeaders[7] = "Comment [TCGA Data Type]";
		sdrfHeaders[8] = "Comment [TCGA Include for Analysis]";
		sdrfHeaders[9] = "Array Data Matrix File";
		sdrfHeaders[10] = "Comment [TCGA Archive Name]";
		sdrfHeaders[11] = "Comment [TCGA Data Level]";
		sdrfHeaders[12] = "Comment [TCGA Data Type]";
		sdrfHeaders[13] = "Comment [TCGA Include for Analysis]";

        qcContext.setStandaloneValidator(true);
        qcContext.setCenterConvertedToUUID(true);

        sdrfValues.put(1, new String[] {"->", "->",
                "12345678-1234-1234-1234-abcdef-abcdef", "TCGA-00-0000-00A-00D-0000-00",
                "afile.txt",
                "center_disease.platform.Level_1.1.0.0", "Level 1", "Fake",
                "yes", "matrix.txt", "center_disease.platform.Level_2.1.0.0",
                "Level 2", "Type", "yes" });
        // second row has control row
        sdrfValues.put(2, new String[] {"a", "b",
                "This Is A Control", "->", "afile.txt",
                "center_disease.platform.Level_1.1.0.0", "Level 1", "Fake",
                "yes", "matrix.txt", "center_disease.platform.Level_2.1.0.0",
                "Level 2", "Type", "yes" });

        sdrfValues.put(3, new String[] {"a", "b",
                "This Is A Control", "->", "afile.txt",
                "center_disease.platform.Level_1.2.0.0", "Level 1", "Fake",
                "yes", "matrix.txt", "center_disease.platform.Level_2.2.0.0",
                "Level 2", "Type", "yes" });

        final Map<String, Boolean> uuidValidityMap = new HashMap<String, Boolean>();
        uuidValidityMap.put("12345678-1234-1234-1234-abcdef-abcdef", true);

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat("12345678-1234-1234-1234-abcdef-abcdef");
            will(returnValue(true));

            one(mockQcLiveBarcodeAndUUIDValidator).validateUuid("12345678-1234-1234-1234-abcdef-abcdef", qcContext, "test.sdrf.txt", true);
            will(returnValue(true));

            one(mockQcLiveBarcodeAndUUIDValidator).isAliquotUUID("12345678-1234-1234-1234-abcdef-abcdef");
            will(returnValue(true));

            one(mockQcLiveBarcodeAndUUIDValidator).isMatchingDiseaseForUUID("12345678-1234-1234-1234-abcdef-abcdef", "fakeTumor");
            will(returnValue(true));

            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDBarcodeMapping("12345678-1234-1234-1234-abcdef-abcdef", "TCGA-00-0000-00A-00D-0000-00");
            will(returnValue(true));

            one(mockQcLiveBarcodeAndUUIDValidator).batchValidateUUIDsReportIndividualResults(Arrays.asList("12345678-1234-1234-1234-abcdef-abcdef"), qcContext, "test.sdrf.txt", true);
            will(returnValue(uuidValidityMap));

            exactly(2).of(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat("This Is A Control");
            will(returnValue(false));

        }});

        //boolean isValid = validator.validateBarcodesAndUuids(qcContext, sdrfNavigator);
        boolean isValid = validator.doWork(archive, qcContext);
        assertTrue(qcContext.getErrors().toString(), isValid);
        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testBarcodesWithLeadingOrTrailingWhiteSpaceSoundcheck()
            throws Processor.ProcessorException {

        // Soundcheck setup
        qcContext.setStandaloneValidator(true);

        final String barcode1 = "TCGA-00-0000-00A-00D-0000-00 ";
        final String barcode2 = " TCGA-00-1111-00A-00D-0000-00";

        final List<String> barcodes = new LinkedList<String>();
        barcodes.add(barcode1);
        barcodes.add(barcode2);

        final Map<String, Boolean> barcodeValidityMap = new HashMap<String, Boolean>();
        barcodeValidityMap.put(barcode1, false);
        barcodeValidityMap.put(barcode2, false);

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).batchValidateReportIndividualResults(barcodes, qcContext, "test.sdrf.txt", true);
            will(returnValue(barcodeValidityMap));
        }});

        sdrfValues.put(1, new String[] {"->", "->",
                "TCGA-00-0000-00A-00D-0000-00 ", "afile.txt",
                "center_disease.platform.Level_1.1.0.0", "Level 1", "Fake",
                "yes", "matrix.txt", "center_disease.platform.Level_2.1.0.0",
                "Level 2", "Type", "yes" });
        sdrfValues.put(2, new String[] {"->", "->",
                " TCGA-00-1111-00A-00D-0000-00", "afile.txt",
                "center_disease.platform.Level_1.1.0.0", "Level 1", "Fake",
                "yes", "matrix.txt", "center_disease.platform.Level_2.1.0.0",
                "Level 2", "Type", "yes" });

        assertFalse(validator.validateBarcodesAndUuids(qcContext, sdrfNavigator));
    }

	@Test
	public void testNonBarcodeRowDataWithLeadingOrTrailingWhiteSpace()
			throws Processor.ProcessorException {
		sdrfValues.put(1, new String[] {/* "-> ", */"-> ", "-> ",
				"TCGA-00-0000-00A-00D-0000-00", "afile.txt ",
				"center_disease.platform.Level_1.1.0.0 ", "Level 1 ", "Fake ",
				"yes ", "matrix.txt ",
				"center_disease.platform.Level_2.1.0.0 ", "Level 2 ", "Type ",
				"yes " });
		sdrfValues.put(2, new String[] {/* " MGED Ontology", */" a", " b",
				"TCGA-00-1111-00A-00D-0000-00", " afile.txt",
				" center_disease.platform.Level_1.1.0.0", " Level 1", " Fake",
				" yes", " matrix.txt",
				" center_disease.platform.Level_2.1.0.0", " Level 2", " Type",
				" yes" });

		assertFalse(validator.validateNonBarcodeRowData(qcContext,
				sdrfNavigator));
		// assertEquals(26, qcContext.getErrorCount()); // errors in every cell
		// except extract names
		assertEquals(24, qcContext.getErrorCount()); // errors in every cell
														// except extract names
		// assertEquals("An error occurred while validating SDRF for archive 'a': SDRF line '2 column '1': Term Source REF '-> ' has leading or trailing whitespace	[archive a]",
		// qcContext.getErrors().get(0));
		assertEquals(
				"An error occurred while validating SDRF for archive 'a': SDRF line '2 column '1': Source Name '-> ' has leading or trailing whitespace	[archive a]",
				qcContext.getErrors().get(0));
	}

    /**
     * Test SDRF with the following column:
     *  "Derived Array Data Matrix REF"
     *
     * @throws Processor.ProcessorException
     */
    @Test
    public void testSDRFWithDerivedArrayDataMatrixREFColumns() throws Processor.ProcessorException {

        final String[] sdrfHeaders = {
                "Source Name",
                "Provider",
                "Material Type",
                "Term Source REF",
                "Characteristics [Genotype]",
                "Term Source REF",
                "Characteristics [Organism]",
                "Term Source REF",
                "Protocol REF",
                "Sample Name",
                "Protocol REF",
                "Parameter Value [Amplification]",
                "Extract Name",
                "Protocol REF",
                "Labeled Extract Name",
                "Comment [SRA Sample Accession]",
                "Derived Array Data Matrix REF",
                "Comment [TCGA Include for Analysis]",
                "Comment [dBGaP Reference]",
                "Comment [TCGA Data Type]",
                "Comment [TCGA Data Level]",
                "Protocol REF",
                "Sample Name",
                "Derived Array Data File",
                "Comment [TCGA Include for Analysis]",
                "Comment [TCGA Archive Name]",
                "Comment [TCGA Data Type]",
                "Comment [TCGA Data Level]"
        };

        final String[] sdrfValues1 = new String[] {
                "sourceNameRequiredValue",
                "providerRequiredValue",
                "materialTypeRequiredValue",
                "->",
                "characteristicsGenotypeRequiredValue",
                "->",
                "characteristicsOrganismRequiredValue",
                "->",
                "->",
                "sampleNameRequiredValue",
                "->",
                "none",
                "TCGA-00-0000-00A-00A-0000-00",
                "hms.harvard.edu:labeling:Illumina_HiSeq:01",
                "TCGA-00-0000-00A-00A-0000-00",
                "SRS156719",
                "TCGA-00-0000-00A-00A-0000-00_Illumina_HiSeq.bam",
                "yes",
                "SRZ011111",
                "Copy Number - NextGen",
                "Level 2",
                "hms.harvard.edu:segmentation:Illumina_HiSeq:01",
                "TCGA-00-0000-00A-00A-0000-00",
                "TCGA-00-0000-00A-00A-0000-00_TCGA-00-0000-00A-00A-0000-00_Segment.tsv",
                "yes",
                "hms.harvard.edu_COAD.Illumina_HiSeq.Level_3.1.0.0",
                "Copy Number - NextGen",
                "Level 3"
        };

        final String[] sdrfValues2 = new String[] {
                "sourceNameRequiredValue",
                "providerRequiredValue",
                "materialTypeRequiredValue",
                "->",
                "characteristicsGenotypeRequiredValue",
                "->",
                "characteristicsOrganismRequiredValue",
                "->",
                "->",
                "sampleNameRequiredValue",
                "->",
                "none",
                "TCGA-00-0000-00A-00A-0000-00",
                "hms.harvard.edu:labeling:Illumina_HiSeq:01",
                "TCGA-00-0000-00A-00A-0000-00",
                "SRS156726",
                "TCGA-00-0000-00A-00A-0000-00_Illumina_HiSeq.bam",
                "yes",
                "SRZ010919",
                "Copy Number - NextGen",
                "Level 2",
                "hms.harvard.edu:segmentation:Illumina_HiSeq:01",
                "TCGA-00-0000-00A-00A-0000-00",
                "TCGA-00-0000-00A-00A-0000-00_TCGA-00-0000-00A-00A-0000-00_Segment.tsv",
                "yes",
                "hms.harvard.edu_COAD.Illumina_HiSeq.Level_3.1.0.0",
                "Copy Number - NextGen",
                "Level 3"
        };

        final List<String[]> sdrfValuesList = new ArrayList<String[]>();
        sdrfValuesList.add(sdrfValues1);
        sdrfValuesList.add(sdrfValues2);

        final Archive archive = getMageTabArchive(sdrfHeaders, sdrfValuesList);

        final boolean isValid = validator.execute(archive, qcContext);

        assertTrue(isValid);
        Assert.assertEquals(0, qcContext.getErrorCount());
        Assert.assertEquals(0, qcContext.getWarningCount());
    }

    /**
     * Test SDRF with missing 'Comment [TCGA Archive Name]' column for 'Derived Array Data Matrix File' column
     *
     * @throws Processor.ProcessorException
     */
    @Test
    public void testSDRFWithMissingCommentTCGAArchiveNameColForFileCol() throws Processor.ProcessorException {

        final String[] sdrfHeaders = {
                "Source Name",
                "Provider",
                "Material Type",
                "Term Source REF",
                "Characteristics [Genotype]",
                "Term Source REF",
                "Characteristics [Organism]",
                "Term Source REF",
                "Protocol REF",
                "Sample Name",
                "Protocol REF",
                "Parameter Value [Amplification]",
                "Extract Name",
                "Protocol REF",
                "Labeled Extract Name",
                "Comment [SRA Sample Accession]",
                "Derived Array Data Matrix File",
                "Comment [TCGA Include for Analysis]",
                "Comment [dBGaP Reference]",
                "Comment [TCGA Data Type]",
                "Comment [TCGA Data Level]",
                "Protocol REF",
                "Sample Name",
                "Derived Array Data File",
                "Comment [TCGA Include for Analysis]",
                "Comment [TCGA Archive Name]",
                "Comment [TCGA Data Type]",
                "Comment [TCGA Data Level]"
        };

        final String[] sdrfValues1 = new String[] {
                "sourceNameRequiredValue",
                "providerRequiredValue",
                "materialTypeRequiredValue",
                "->",
                "characteristicsGenotypeRequiredValue",
                "->",
                "characteristicsOrganismRequiredValue",
                "->",
                "->",
                "sampleNameRequiredValue",
                "->",
                "none",
                "TCGA-00-0000-00A-00A-0000-00",
                "hms.harvard.edu:labeling:Illumina_HiSeq:01",
                "TCGA-00-0000-00A-00A-0000-00",
                "SRS156719",
                "TCGA-00-0000-00A-00A-0000-00_Illumina_HiSeq.bam",
                "yes",
                "SRZ011111",
                "Copy Number - NextGen",
                "Level 2",
                "hms.harvard.edu:segmentation:Illumina_HiSeq:01",
                "TCGA-00-0000-00A-00A-0000-00",
                "TCGA-00-0000-00A-00A-0000-00_TCGA-00-0000-00A-00A-0000-00_Segment.tsv",
                "yes",
                "hms.harvard.edu_COAD.Illumina_HiSeq.Level_3.1.0.0",
                "Copy Number - NextGen",
                "Level 3"
        };

        final String[] sdrfValues2 = new String[] {
                "sourceNameRequiredValue",
                "providerRequiredValue",
                "materialTypeRequiredValue",
                "->",
                "characteristicsGenotypeRequiredValue",
                "->",
                "characteristicsOrganismRequiredValue",
                "->",
                "->",
                "sampleNameRequiredValue",
                "->",
                "none",
                "TCGA-00-0000-00A-00A-0000-00",
                "hms.harvard.edu:labeling:Illumina_HiSeq:01",
                "TCGA-00-0000-00A-00A-0000-00",
                "SRS156726",
                "TCGA-00-0000-00A-00A-0000-00_Illumina_HiSeq.bam",
                "yes",
                "SRZ010919",
                "Copy Number - NextGen",
                "Level 2",
                "hms.harvard.edu:segmentation:Illumina_HiSeq:01",
                "TCGA-00-0000-00A-00A-0000-00",
                "TCGA-00-0000-00A-00A-0000-00_TCGA-00-0000-00A-00A-0000-00_Segment.tsv",
                "yes",
                "hms.harvard.edu_COAD.Illumina_HiSeq.Level_3.1.0.0",
                "Copy Number - NextGen",
                "Level 3"
        };

        final List<String[]> sdrfValuesList = new ArrayList<String[]>();
        sdrfValuesList.add(sdrfValues1);
        sdrfValuesList.add(sdrfValues2);

        final Archive archive = getMageTabArchive(sdrfHeaders, sdrfValuesList);

        final boolean isValid = validator.execute(archive, qcContext);

        assertFalse(isValid);
        Assert.assertEquals(1, qcContext.getErrorCount());
        assertEquals("Required SDRF column 'Comment [TCGA Archive Name]' is missing\t[archive archiveRealName]", qcContext.getErrors().get(0));
        Assert.assertEquals(0, qcContext.getWarningCount());
    }

    /**
     * Test SDRF with all of the following columns:
     *  "Array Data REF",
     *  "Derived Array Data REF",
     *  "Array Data Matrix REF",
     *  "Derived Array Data Matrix REF",
     *  "Image REF"
     *
     * @throws Processor.ProcessorException
     */
    @Test
    public void testSDRFWithAllREFColumns() throws Processor.ProcessorException {

        final String[] sdrfHeaders = {
                "Source Name",
                "Provider",
                "Material Type",
                "Term Source REF",
                "Characteristics [Genotype]",
                "Term Source REF",
                "Characteristics [Organism]",
                "Term Source REF",
                "Protocol REF",
                "Sample Name",
                "Protocol REF",
                "Parameter Value [Amplification]",
                "Extract Name",
                "Protocol REF",
                "Labeled Extract Name",
                "Comment [SRA Sample Accession]",
                "Comment [TCGA Include for Analysis]",
                "Comment [dBGaP Reference]",
                "Comment [TCGA Data Type]",
                "Comment [TCGA Data Level]",
                "Protocol REF",
                "Sample Name",
                "Derived Array Data File",
                "Comment [TCGA Include for Analysis]",
                "Comment [TCGA Archive Name]",
                "Comment [TCGA Data Type]",
                "Comment [TCGA Data Level]",
                "Array Data REF",
                "Derived Array Data REF",
                "Array Data Matrix REF",
                "Derived Array Data Matrix REF",
                "Image REF"
        };

        final String[] sdrfValues = new String[] {
                "sourceNameValueRequired",
                "providerValueRequired",
                "materialTypeValueRequired",
                "->",
                "characteristicsGenotypeValueRequired",
                "->",
                "characteristicsOrganismValueRequired",
                "->",
                "->",
                "sampleNameValueRequired",
                "->",
                "->",
                "extractNameValueRequired",
                "->",
                "->",
                "->",
                "->",
                "->",
                "->",
                "->",
                "->",
                "sampleNameValueRequired",
                "->",
                "->",
                "->",
                "->",
                "->",
                "->",
                "->",
                "->",
                "->",
                "->"
        };

        final List<String[]> sdrfValuesList = new ArrayList<String[]>();
        sdrfValuesList.add(sdrfValues);

        final Archive archive = getMageTabArchive(sdrfHeaders, sdrfValuesList);

        final boolean isValid = validator.execute(archive, qcContext);

        assertTrue(isValid);
        Assert.assertEquals(0, qcContext.getErrorCount());
        Assert.assertEquals(0, qcContext.getWarningCount());
    }

    /**
     * Test SDRF with none of the following REF columns:
     *  "Array Data REF",
     *  "Derived Array Data REF",
     *  "Array Data Matrix REF",
     *  "Derived Array Data Matrix REF",
     *  "Image REF"
     *
     * @throws Processor.ProcessorException
     */
    @Test
    public void testSDRFWithoutREFColumns() throws Processor.ProcessorException {

        final String[] sdrfHeaders = {
                "Source Name",
                "Provider",
                "Material Type",
                "Term Source REF",
                "Characteristics [Genotype]",
                "Term Source REF",
                "Characteristics [Organism]",
                "Term Source REF",
                "Protocol REF",
                "Sample Name",
                "Protocol REF",
                "Parameter Value [Amplification]",
                "Extract Name",
                "Protocol REF",
                "Labeled Extract Name",
                "Comment [SRA Sample Accession]",
                "Comment [TCGA Include for Analysis]",
                "Comment [dBGaP Reference]",
                "Comment [TCGA Data Type]",
                "Comment [TCGA Data Level]",
                "Protocol REF",
                "Sample Name",
                "Derived Array Data File",
                "Comment [TCGA Include for Analysis]",
                "Comment [TCGA Archive Name]",
                "Comment [TCGA Data Type]",
                "Comment [TCGA Data Level]"
        };

        final String[] sdrfValues = new String[] {
                "sourceNameValueRequired",
                "providerValueRequired",
                "materialTypeValueRequired",
                "->",
                "characteristicsGenotypeValueRequired",
                "->",
                "characteristicsOrganismValueRequired",
                "->",
                "->",
                "sampleNameValueRequired",
                "->",
                "->",
                "extractNameValueRequired",
                "->",
                "->",
                "->",
                "->",
                "->",
                "->",
                "->",
                "->",
                "sampleNameValueRequired",
                "->",
                "->",
                "->",
                "->",
                "->"
        };

        final List<String[]> sdrfValuesList = new ArrayList<String[]>();
        sdrfValuesList.add(sdrfValues);

        final Archive archive = getMageTabArchive(sdrfHeaders, sdrfValuesList);

        final boolean isValid = validator.execute(archive, qcContext);

        assertTrue(isValid);
        Assert.assertEquals(0, qcContext.getErrorCount());
        Assert.assertEquals(0, qcContext.getWarningCount());
    }

    @Test
    public void testWithHiSeqHeaders() throws Processor.ProcessorException {
        final String[] sdrfHeaders = new String[]{
                "Extract Name",
                "Comment [SRA Sample Accession]",
                "Protocol REF","Labeled Extract Name",
                "Protocol REF",
                "Assay Name",
                "Protocol REF",
                "Hybridization Name",
                "Protocol REF",
                "Assay Name",
                "Derived Data REF",
                "Comment [NCBI dBGaP BAM Accession]",
                "Comment [Genome reference]",
                "Comment [TCGA Include for Analysis]",
                "Comment [TCGA Data Type]",
                "Comment [TCGA Data Level]",
                "Protocol REF",
                "Data Transformation Name",
                "Derived Data File",
                "Comment [TCGA Include for Analysis]",
                "Comment [TCGA Archive Name]",
                "Comment [TCGA Data Type]",
                "Comment [TCGA Data Level]",
                "Comment [TCGA MD5]",
                "Comment [TCGA File Type]"
        };

        final String[] sdrfValuesLine1 =
                ("TCGA-00-2671-01A-01D-1405-02\tSRS156719\thms.harvard.edu:labeling:IlluminaHiSeq_DNASeqC:01\t" +
                        "TCGA-A6-2671-01A-01D-1405-02 labeling\thms.harvard.edu:library_preparation:IlluminaHiSeq_DNASeq:01\t" +
                        "TCGA-A6-2671-01A-01D-1405-02_library_prep\thms.harvard.edu:cluster_generation:IlluminaHiSeq_DNASeq:01\t" +
                        "TCGA-A6-2671-01A-01D-1405-02_cluster generation\thms.harvard.edu:DNA_Sequencing:IlluminaHiSeq_DNASeq:01\t" +
                        "TCGA-A6-2671-01A-01D-1405-02_DNA_Sequencing\tTCGA-A6-2671-01A-01D-1405-02_Illumina_HiSeq.bam\tSRZ011111\t" +
                        "hg19 (GRCh37)\tyes\tDNA Sequence-Alignment\tLevel 1\thms.harvard.edu:segmentation:IlluminaHiSeqDNASeqC:01\t" +
                        "TCGA-A6-2671-01A-01D-1405-02 Segmentation\tTCGA-A6-2671-01A-01D-1405-02_TCGA-A6-2671-10A-01D-1405-02_Segment.tsv\t" +
                        "yes\thms.harvard.edu_COAD.IlluminaHiSeq_DNASeqC.Level_3.1.0.0\tCopy Number-DNASeq\tLevel 3\tsdfaSDFadsfdsaDFS\t" +
                        "tsv").split("\\t");


        final String[] sdrfValuesLine2 =
                ("TCGA-00-2671-10A-01D-1405-02\tSRS156726\thms.harvard.edu:labeling:IlluminaHiSeq_DNASeqC:01\t" +
                        "TCGA-A6-2671-10A-01D-1405-02 labeling\thms.harvard.edu:library_preparation:IlluminaHiSeq_DNASeq:01\t" +
                        "TCGA-A6-2671-10A-01D-1405-02_library_prep\thms.harvard.edu:cluster_generation:IlluminaHiSeq_DNASeq:01\t" +
                        "TCGA-A6-2671-10A-01D-1405-02_cluster generation\thms.harvard.edu:DNA_Sequencing:IlluminaHiSeq_DNASeq:01\t" +
                        "TCGA-A6-2671-10A-01D-1405-02_DNA_Sequencing\tTCGA-A6-2671-10A-01D-1405-02_Illumina_HiSeq.bam\tSRZ010919\t" +
                        "hg19 (GRCh37)\tyes\tDNA Sequence-Alignment\tLevel 1\thms.harvard.edu:segmentation:IlluminaHiSeq_DNASeqC:01\t" +
                        "TCGA-A6-2671-10A-01D-1405-02 Segmentation\tTCGA-A6-2671-01A-01D-1405-02_TCGA-A6-2671-10A-01D-1405-02_Segment.tsv\t" +
                        "yes\thms.harvard.edu_COAD.IlluminaHiSeq_DNASeqC.Level_3.1.0.0\tCopy Number-DNASeq\tLevel 3\tEDGHWDEDGHADFG\t" +
                        "tsv").split("\\t");

        final Archive mageTabArchive = getMageTabArchive(sdrfHeaders, Arrays.asList(sdrfValuesLine1, sdrfValuesLine2));
        mageTabArchive.setTumorType("fakeTumor");

        context.checking(new Expectations() {{
            allowing(mockQcLiveBarcodeAndUUIDValidator).validate(with(any(String.class)), with(qcContext), with("test.sdrf.txt"), with(true));
            will(returnValue(true));

        }});

        final boolean isValid = validator.execute(mageTabArchive, qcContext);

        assertTrue(isValid);
        Assert.assertEquals(0, qcContext.getErrorCount());
        Assert.assertEquals(0, qcContext.getWarningCount());
    }   
    
    @Test
    public void testWithUUIDConversion() throws Processor.ProcessorException {
    	
    	String[] headers = getUUIDConversionHeaders();
        String[] values = getUUIDConversionValues();                              
        
        final List<String[]> sdrfValuesList = new ArrayList<String[]>();
        sdrfValuesList.add(values);    

        final Archive uuidArchive = getMageTabArchive(headers, sdrfValuesList);
    	qcContext.setCenterConvertedToUUID(true);
    	context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).isMatchingDiseaseForUUID("69de087d-e31d-4ff5-a760-6be8da96b6e2", null);
            will(returnValue(true));
    		 one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat("69de087d-e31d-4ff5-a760-6be8da96b6e2");
             will(returnValue(true));
             one(mockQcLiveBarcodeAndUUIDValidator).validateUuid("69de087d-e31d-4ff5-a760-6be8da96b6e2", qcContext, "test.sdrf.txt", true);
             will(returnValue(true));
             one(mockQcLiveBarcodeAndUUIDValidator).isAliquotUUID("69de087d-e31d-4ff5-a760-6be8da96b6e2");
             will(returnValue(true));
             one (mockQcLiveBarcodeAndUUIDValidator).validateUUIDBarcodeMapping("69de087d-e31d-4ff5-a760-6be8da96b6e2", "TCGA-00-0000-00A-00A-0000-00");
             will(returnValue(true));

        }});

 		boolean valid = validator.execute(uuidArchive, qcContext);
 		assertTrue(qcContext.getErrors().toString(), valid);
    }
    
    @Test
    public void testWithUuidConversionStandaloneControl() throws ProcessorException {
        final String[] sdrfValues = new String[]{
                "aSource",
                "aSample",
                "Control LTxEJC",
                "->",
                "spots.txt",
                "test.org_DIS.Platform.Level_1.1.0.0",
                "Level 1",
                "a data type",
                "Yes",
                "level2.txt",
                "test.org_DIS.Platform.Level_2.1.0.0",
                "Level 2",
                "another data type",
                "Yes"
        };

        final List<String[]> sdrfValuesList = new ArrayList<String[]>();
        sdrfValuesList.add(sdrfValues);

        List<String> headers = new ArrayList<String>();
        for (final String header : sdrfHeaders) {
            headers.add(header);
        }
        headers.add(3, "Comment [TCGA Barcode]");


        final Archive uuidArchive = getMageTabArchive(headers.toArray(new String[14]), sdrfValuesList);
    	qcContext.setCenterConvertedToUUID(true);
    	qcContext.setStandaloneValidator(true);


 		boolean valid = validator.execute(uuidArchive, qcContext);
        assertTrue(qcContext.getErrors().toString(), valid);
    }

    @Test
    public void testWithUUIDConversionStandaloneNoRemote() throws ProcessorException{
    	String[] headers = getUUIDConversionHeaders();
        String[] values = getUUIDConversionValues();                              
        
        final List<String[]> sdrfValuesList = new ArrayList<String[]>();
        sdrfValuesList.add(values);    

        final Archive uuidArchive = getMageTabArchive(headers, sdrfValuesList);
    	qcContext.setCenterConvertedToUUID(true);
    	qcContext.setStandaloneValidator(true);
        qcContext.setNoRemote(true);
    	
    	context.checking(new Expectations() {{
    		 one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat("69de087d-e31d-4ff5-a760-6be8da96b6e2");
    		 will(returnValue(true));

            one(mockQcLiveBarcodeAndUUIDValidator).isMatchingDiseaseForUUID("69de087d-e31d-4ff5-a760-6be8da96b6e2", null);
            will(returnValue(true));

            one(mockQcLiveBarcodeAndUUIDValidator).validateUuid("69de087d-e31d-4ff5-a760-6be8da96b6e2", qcContext, "test.sdrf.txt", true);
            will(returnValue(true));
            one(mockQcLiveBarcodeAndUUIDValidator).isAliquotUUID("69de087d-e31d-4ff5-a760-6be8da96b6e2");
            will(returnValue(true));
            one(mockQcLiveBarcodeAndUUIDValidator).validateAliquotBarcodeFormat("TCGA-00-0000-00A-00A-0000-00");
            will(returnValue(true));


    		 one(mockQcLiveBarcodeAndUUIDValidator).batchValidateUUIDsReportIndividualResults(Arrays.asList("69de087d-e31d-4ff5-a760-6be8da96b6e2"), qcContext, "test.sdrf.txt", true);
    		 will(returnValue(new HashMap()));
        }});

 		boolean valid = validator.execute(uuidArchive, qcContext); 		
    }
    
    @Test
    public void testWithUUIDConverstionBadBarcode() throws Processor.ProcessorException {
    	
    	String[] headers = getUUIDConversionHeaders();
        String[] values = getUUIDConversionValues();                              
        
        final List<String[]> sdrfValuesList = new ArrayList<String[]>();
        sdrfValuesList.add(values);    

        final Archive uuidArchive = getMageTabArchive(headers, sdrfValuesList);
    	qcContext.setCenterConvertedToUUID(true);
    	context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).isMatchingDiseaseForUUID("69de087d-e31d-4ff5-a760-6be8da96b6e2", null);
            will(returnValue(true));
    		 one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat("69de087d-e31d-4ff5-a760-6be8da96b6e2");
             will(returnValue(true));
             one(mockQcLiveBarcodeAndUUIDValidator).validateUuid("69de087d-e31d-4ff5-a760-6be8da96b6e2", qcContext, "test.sdrf.txt", true);
             will(returnValue(true));
             one(mockQcLiveBarcodeAndUUIDValidator).isAliquotUUID("69de087d-e31d-4ff5-a760-6be8da96b6e2");
             will(returnValue(true));
             one (mockQcLiveBarcodeAndUUIDValidator).validateUUIDBarcodeMapping("69de087d-e31d-4ff5-a760-6be8da96b6e2", "TCGA-00-0000-00A-00A-0000-00");
             will(returnValue(false));
        }});

 		boolean valid = validator.execute(uuidArchive, qcContext);
 		assertFalse(valid);
 		assertEquals ("An error occurred while validating SDRF for archive 'archiveRealName': " +
 				"SDRF line 1: The metadata for UUID '69de087d-e31d-4ff5-a760-6be8da96b6e2' found in " +
 				"Extract Name column and barcode 'TCGA-00-0000-00A-00A-0000-00' found in " +
 				"Comment [TCGA Barcode] column " +
 				"do not match	[archive archiveRealName]",qcContext.getErrors().get(0));
    }
    
    @Test
    public void testWithUUIDConverstionNotAliquot() throws Processor.ProcessorException {
    	
    	String[] headers = getUUIDConversionHeaders();
        String[] values = getUUIDConversionValues();                              
        
        final List<String[]> sdrfValuesList = new ArrayList<String[]>();
        sdrfValuesList.add(values);    

        final Archive uuidArchive = getMageTabArchive(headers, sdrfValuesList);
    	qcContext.setCenterConvertedToUUID(true);
    	context.checking(new Expectations() {{
    		 one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat("69de087d-e31d-4ff5-a760-6be8da96b6e2");
             will(returnValue(true));
             one(mockQcLiveBarcodeAndUUIDValidator).validateUuid("69de087d-e31d-4ff5-a760-6be8da96b6e2", qcContext, "test.sdrf.txt", true);
             will(returnValue(true));
             one(mockQcLiveBarcodeAndUUIDValidator).isAliquotUUID("69de087d-e31d-4ff5-a760-6be8da96b6e2");
             will(returnValue(false));

        }});

 		boolean valid = validator.execute(uuidArchive, qcContext);
 		assertFalse(valid);
 		assertEquals("An error occurred while validating SDRF for archive 'archiveRealName': " +
 				"SDRF line 1: Extract 69de087d-e31d-4ff5-a760-6be8da96b6e2 found in Extract Name " +
 				"column is not an aliquot UUID. Only aliquot UUIDs are allowed for this data type" +
 				"	[archive archiveRealName]",qcContext.getErrors().get(0));
    }
    
    @Test
    public void testWithUUIDNonConverted() throws Processor.ProcessorException {
    	
    	String[] headers = getUUIDConversionHeaders();
        String[] values = getUUIDConversionValues();                              
        
        final List<String[]> sdrfValuesList = new ArrayList<String[]>();
        sdrfValuesList.add(values);    

        final Archive uuidArchive = getMageTabArchive(headers, sdrfValuesList);
    	qcContext.setCenterConvertedToUUID(false);
    	context.checking(new Expectations() {{
    		 one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDFormat("69de087d-e31d-4ff5-a760-6be8da96b6e2");
             will(returnValue(true));
             one(mockQcLiveBarcodeAndUUIDValidator).validateUuid("69de087d-e31d-4ff5-a760-6be8da96b6e2", qcContext, "test.sdrf.txt", true);
             will(returnValue(true));             
        }});

 		boolean valid = validator.execute(uuidArchive, qcContext);
 		assertTrue(qcContext.getErrors().toString(), valid);
    }
    @Test
    public void testWithBarcodeUUIDConverted() throws Processor.ProcessorException {
    	
    	String[] headers = getUUIDConversionHeaders();
        String[] values = getUUIDConversionValues();                              
        
        values[11] = "TCGA-00-0000-00A-00A-0000-00";
        
        final List<String[]> sdrfValuesList = new ArrayList<String[]>();
        sdrfValuesList.add(values);    

        final Archive uuidArchive = getMageTabArchive(headers, sdrfValuesList);
    	qcContext.setCenterConvertedToUUID(true);    	
 		boolean valid = validator.execute(uuidArchive, qcContext); 		
 		assertFalse(valid);
 		assertEquals("An error occurred while validating SDRF for " +
 				"archive 'archiveRealName': SDRF line 1: Barcode TCGA-00-0000-00A-00A-0000-00 " +
 				"found in Extract Name column. UUID must be used as aliquot " +
 				"identifiers	[archive archiveRealName]",qcContext.getErrors().get(0));
    }
    
    
    
    public String [] getUUIDConversionHeaders(){
    	return new String[] {             
                "Provider",
                "Material Type",
                "Term Source REF",
                "Characteristics [Genotype]",
                "Term Source REF",
                "Characteristics [Organism]",
                "Term Source REF",
                "Protocol REF",
                "Sample Name",
                "Protocol REF",
                "Parameter Value [Amplification]",
                "Extract Name",
                "Comment [TCGA Barcode]",
                "Protocol REF",
                "Labeled Extract Name",
                "Comment [SRA Sample Accession]",
                "Derived Array Data Matrix REF",
                "Comment [TCGA Include for Analysis]",
                "Comment [dBGaP Reference]",
                "Comment [TCGA Data Type]",
                "Comment [TCGA Data Level]",
                "Protocol REF",
                "Sample Name",
                "Derived Array Data File",
                "Comment [TCGA Include for Analysis]",
                "Comment [TCGA Archive Name]",
                "Comment [TCGA Data Type]",
                "Comment [TCGA Data Level]"
        };
    }
    
    public String [] getUUIDConversionValues (){
    	    	    	    	    
    	return new String[] {
                "providerRequiredValue",
                "materialTypeRequiredValue",
                "->",
                "characteristicsGenotypeRequiredValue",
                "->",
                "characteristicsOrganismRequiredValue",
                "->",
                "->",
                "arName",
                "->",
                "none",
                "69de087d-e31d-4ff5-a760-6be8da96b6e2",
                "TCGA-00-0000-00A-00A-0000-00",
                "hms.harvard.edu:labeling:Illumina_HiSeq:01",
                "TCGA-00-0000-00A-00A-0000-00",
                "SRS156719",
                "TCGA-00-0000-00A-00A-0000-00_Illumina_HiSeq.bam",
                "yes",
                "SRZ011111",
                "Copy Number - NextGen",
                "Level 2",
                "hms.harvard.edu:segmentation:Illumina_HiSeq:01",
                "TCGA-00-0000-00A-00A-0000-00",
                "TCGA-00-0000-00A-00A-0000-00_TCGA-00-0000-00A-00A-0000-00_Segment.tsv",
                "yes",
                "hms.harvard.edu_COAD.Illumina_HiSeq.Level_3.1.0.0",
                "Copy Number - NextGen",
                "Level 3"
        };       

    }

    /**
     * Return a mage-tab {@link Archive} with an SDRF file that has the given headers and values.
     *
     * Note: sdrfHeaders and sdrf values in sdrfValuesList must have the same size.
     *
     * @param sdrfHeaders the SDRF headers
     * @param sdrfValuesList the {@link List} of rows of SDRF values
     * @return a mage-tab {@link Archive} with an SDRF file that has the given headers and values
     */
    private Archive getMageTabArchive(final String[] sdrfHeaders, final List<String[]> sdrfValuesList) {

        final Map<Integer, String[]> sdrfValuesMap = new HashMap<Integer, String[]>();
        sdrfValuesMap.put(0, sdrfHeaders);

        for(final String[] sdrfValues : sdrfValuesList) {
            sdrfValuesMap.put(1, sdrfValues);
        }

        final TabDelimitedContent sdrf = new TabDelimitedContentImpl();
        sdrf.setTabDelimitedContents(sdrfValuesMap);
        sdrf.setTabDelimitedHeader(sdrfHeaders);

        final TabDelimitedContentNavigator deprecatedSdrfNavigator = new TabDelimitedContentNavigator();
        deprecatedSdrfNavigator.setTabDelimitedContent(sdrf);

        final Archive archive = new Archive();
        archive.setRealName("archiveRealName");
        archive.setArchiveFile(new File(testDir + ".tar.gz"));
        archive.setArchiveType(Archive.TYPE_MAGE_TAB);
        archive.setSdrf(sdrf);
        archive.setSdrfFile(new File(testDir + "/test.sdrf.txt"));

        return archive;
    }
}
