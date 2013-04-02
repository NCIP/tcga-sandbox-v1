/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 *  Copyright Notice.  The software subject to this notice and license includes both human
 *  readable source code form and machine readable, binary, object code form (the "caBIG
 *  Software").
 *
 *  Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContentImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedFileParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeTumorValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidator;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * DNASeqSdrfValidator unit test.
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class DNASeqSdrfValidatorFastTest {

    private static final String SAMPLES_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;

    private static final String TEST_DIR = SAMPLES_DIR + File.separator
            + "qclive" + File.separator + "dnaSeqSdrfValidator"
            + File.separator;

    private Mockery mockery;
    private QcLiveBarcodeAndUUIDValidator mockQcLiveBarcodeAndUUIDValidator;
    private BarcodeTumorValidator mockBarcodeTumorValidator;

    private DNASeqSdrfValidator dnaSeqSdrfValidator;
    private QcContext context;

    @Before
    public void setUp() {

        mockery = new JUnit4Mockery();
        mockQcLiveBarcodeAndUUIDValidator = mockery.mock(QcLiveBarcodeAndUUIDValidator.class);
        mockBarcodeTumorValidator = mockery.mock(BarcodeTumorValidator.class);

        dnaSeqSdrfValidator = new DNASeqSdrfValidator();
        dnaSeqSdrfValidator.setQcLiveBarcodeAndUUIDValidator(mockQcLiveBarcodeAndUUIDValidator);
        dnaSeqSdrfValidator.setBarcodeTumorValidator(mockBarcodeTumorValidator);

        context = new QcContext();
    }

    @Test
    public void testGetName() {
        assertEquals("SDRF (DNASeq) validation", dnaSeqSdrfValidator.getName());
    }

    @Test
    public void testValidNotAMageTabArchive() throws Processor.ProcessorException, IOException, ParseException {

        final Archive archive = makeTestArchive(false, null);
        final boolean result = dnaSeqSdrfValidator.doWork(archive, context);

        assertTrue(result);
        assertEquals(0, context.getErrorCount());
        assertEquals(0, context.getWarningCount());
    }

    @Test
    public void testNoSDRFInArchiveThrowsProcessorException() throws IOException, ParseException {

        final Archive archive = makeTestArchive(true, null);
        try {
            dnaSeqSdrfValidator.doWork(archive, context);
            fail("ProcessorException was not thrown");
        } catch (final Processor.ProcessorException e) {
            assertEquals("Archive does not have an SDRF", e.getMessage());
        }
    }

    @Test
    public void testNoDataThrowsProcessorException() throws IOException, ParseException, Processor.ProcessorException {

        final String sdrfFileName = TEST_DIR + "noData.sdrf.txt";
        final Archive archive = makeTestArchive(true, sdrfFileName);
        try {
            dnaSeqSdrfValidator.doWork(archive, context);
            fail("IllegalArgumentException was not thrown");
        } catch (final IllegalArgumentException e) {
            assertEquals("TabDelimitedContentNavigator must contain at least one content row and a header", e.getMessage());
        }
    }

    @Test
    public void testMissingDataColumn() throws IOException, ParseException {

        final String sdrfFileName = TEST_DIR + "missingDataColumn.sdrf.txt";
        final Archive archive = makeTestArchive(true, sdrfFileName);
        try {
            dnaSeqSdrfValidator.doWork(archive, context);
            fail("ProcessorException was not thrown");
        } catch (final Processor.ProcessorException e) {
            assertEquals("Malformed SDRF file: row 2 has 1 elements but there are 2 headers", e.getMessage());
        }
    }

    @Test
    public void testMissingRequiredHeader() throws IOException, ParseException, Processor.ProcessorException {

        final String sdrfFileName = TEST_DIR + "missingRequiredHeader.sdrf.txt";
        final Archive archive = makeTestArchive(true, sdrfFileName);

        final boolean  isValid = dnaSeqSdrfValidator.doWork(archive, context);

        assertFalse(isValid);
        assertEquals(1, context.getErrorCount());
        checkErrorExists("Required SDRF column 'Extract Name' is missing\t[archive testArchive]");
        assertEquals(0, context.getWarningCount());
    }

    @Test
    public void testMissingCommentColumn() throws IOException, ParseException, Processor.ProcessorException {

        final String sdrfFileName = TEST_DIR + "missingCommentColumn.sdrf.txt";
        final Archive archive = makeTestArchive(true, sdrfFileName);

        final boolean  isValid = dnaSeqSdrfValidator.doWork(archive, context);

        assertFalse(isValid);
        assertEquals(1, context.getErrorCount());
        checkErrorExists("Required SDRF column 'Comment [TCGA Include for Analysis]' is missing\t[archive testArchive]");
        assertEquals(0, context.getWarningCount());
    }

    @Test
    public void testInvalidDataLevelValue() throws IOException, ParseException, Processor.ProcessorException {

        final String sdrfFileName = TEST_DIR + "invalidDataLevelValue.sdrf.txt";
        final Archive archive = makeTestArchive(true, sdrfFileName);

        final boolean  isValid = dnaSeqSdrfValidator.doWork(archive, context);

        assertFalse(isValid);
        assertEquals(1, context.getErrorCount());
        checkErrorExists("SDRF line 2: value for 'Comment [TCGA Data Level]' must be in the format ''Level N' where N is a valid level number', " +
                "but found 'Level three'\t[archive testArchive]");
        assertEquals(0, context.getWarningCount());
    }

    @Test
    public void testInvalidTcgaIncludeForAnalysisValue() throws IOException, ParseException, Processor.ProcessorException {

        final String sdrfFileName = TEST_DIR + "invalidTcgaIncludeForAnalysisValue.sdrf.txt";
        final Archive archive = makeTestArchive(true, sdrfFileName);

        final boolean  isValid = dnaSeqSdrfValidator.doWork(archive, context);

        assertFalse(isValid);
        assertEquals(1, context.getErrorCount());
        checkErrorExists("line 2: value for 'Comment [TCGA Include for Analysis]' must be in the format 'either 'yes' or 'no'', but found 'oui'\t[archive testArchive]");
        assertEquals(0, context.getWarningCount());
    }

    @Test
    public void testInvalidTcgaArchiveNameValue() throws IOException, ParseException, Processor.ProcessorException {

        final String sdrfFileName = TEST_DIR + "invalidTcgaArchiveNameValue.sdrf.txt";
        final Archive archive = makeTestArchive(true, sdrfFileName);

        final boolean  isValid = dnaSeqSdrfValidator.doWork(archive, context);

        assertFalse(isValid);
        assertEquals(1, context.getErrorCount());
        checkErrorExists("line 2: value for 'Comment [TCGA Archive Name]' must be 'must be a valid archive name', " +
                "but found 'center_disease.platform.Level_8.zero.one.two'\t[archive testArchive]");
        assertEquals(0, context.getWarningCount());
    }

    @Test
    public void testInvalidTcgaArchiveNameValueNotMatchingTcgaDataLevelValue() throws IOException, ParseException, Processor.ProcessorException {

        final String sdrfFileName = TEST_DIR + "invalidTcgaArchiveNameValueNotMatchingTcgaDataLevelValue.sdrf.txt";
        final Archive archive = makeTestArchive(true, sdrfFileName);

        final boolean  isValid = dnaSeqSdrfValidator.doWork(archive, context);

        assertFalse(isValid);
        assertEquals(1, context.getErrorCount());
        checkErrorExists("An error occurred while validating SDRF for archive 'testArchive': SDRF line 2: val_13 was marked as 'Level 3' " +
                "but it is within archive with level 'Level 1'\t[archive testArchive]");
        assertEquals(0, context.getWarningCount());
    }

    @Test
    public void testNonBlankAfterTcgaIncludeForAnalysisNoValue() throws IOException, ParseException, Processor.ProcessorException {

        final String sdrfFileName = TEST_DIR + "nonBlankAfterTcgaIncludeForAnalysisNoValue.sdrf.txt";
        final Archive archive = makeTestArchive(true, sdrfFileName);

        final boolean  isValid = dnaSeqSdrfValidator.doWork(archive, context);

        assertFalse(isValid);
        assertEquals(1, context.getErrorCount());
        checkErrorExists("An error occurred while validating SDRF for archive 'testArchive': " +
                "SDRF line '2' was marked for exclusion from analysis in column '12' but column '17' contains a file name rather than '->'" +
                "\t[archive testArchive]");
        assertEquals(0, context.getWarningCount());
    }

    @Test
    public void testBlankAfterTcgaIncludeForAnalysisNoValue() throws IOException, ParseException, Processor.ProcessorException {

        final String sdrfFileName = "blankAfterTcgaIncludeForAnalysisNoValue.sdrf.txt";
        final Archive archive = makeTestArchive(true, TEST_DIR + sdrfFileName);

        mockery.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUuid("val_1", context, sdrfFileName, true);
            will(returnValue(true));
        }});

        final boolean  isValid = dnaSeqSdrfValidator.doWork(archive, context);

        assertTrue(isValid);
        assertEquals(0, context.getErrorCount());
        assertEquals(0, context.getWarningCount());
    }

    @Test
    public void testValidMinimalSdrf() throws IOException, ParseException, Processor.ProcessorException {

        final String sdrfFileName = "valid_minimal.sdrf.txt";
        final Archive archive = makeTestArchive(true, TEST_DIR + sdrfFileName);

        mockery.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUuid("val_1", context, sdrfFileName, true);
            will(returnValue(true));
        }});

        final boolean  isValid = dnaSeqSdrfValidator.doWork(archive, context);

        assertTrue(isValid);
        assertEquals(0, context.getErrorCount());
        assertEquals(0, context.getWarningCount());
    }

    @Test
    public void testHeadersNotAllowed() throws IOException, ParseException, Processor.ProcessorException {

        final String sdrfFileName = TEST_DIR + "headersNotAllowed.sdrf.txt";
        final Archive archive = makeTestArchive(true, sdrfFileName);
        final boolean  isValid = dnaSeqSdrfValidator.doWork(archive, context);

        assertFalse(isValid);
        assertEquals(3, context.getErrorCount());
        checkErrorExists("An error occurred while validating SDRF for archive 'testArchive': SDRF contains an invalid header: 'Unexpected Col 1'\t[archive testArchive]");
        checkErrorExists("An error occurred while validating SDRF for archive 'testArchive': SDRF contains an invalid header: 'Unexpected Col 2'\t[archive testArchive]");
        checkErrorExists("An error occurred while validating SDRF for archive 'testArchive': SDRF contains an invalid header: 'Unexpected Col 3'\t[archive testArchive]");
        assertEquals(0, context.getWarningCount());
    }

    @Test
    public void testExtractNameValueNotValidAliquotBarcodeCenterNotConvertedToUuid()
            throws IOException, ParseException, Processor.ProcessorException {

        final String sdrfFileName = "extractNameValueNotValidAliquotBarcode.sdrf.txt";
        final Archive archive = makeTestArchive(true, TEST_DIR + sdrfFileName);

        context.setCenterConvertedToUUID(false);

        mockery.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validate(with("TCGA-1"), with(context), with(sdrfFileName), with(true));
            will(new Action() {
                @Override
                public Object invoke(Invocation invocation) throws Throwable {
                    context.addError("Invalid barcode");
                    return false;
                }

                @Override
                public void describeTo(final Description description) {
                    description.appendText("Adding 1 error to the context and returning false");
                }
            });
        }});

        final boolean  isValid = dnaSeqSdrfValidator.doWork(archive, context);

        assertFalse(isValid);
        assertEquals(1, context.getErrorCount());
        checkErrorExists("Invalid barcode\t[archive testArchive]");
        assertEquals(0, context.getWarningCount());
    }

    @Test
    public void testExtractNameValueNotValidAliquotBarcodeCenterConvertedToUuid()
            throws IOException, ParseException, Processor.ProcessorException {

        final String sdrfFileName = "extractNameValueNotValidAliquotBarcode.sdrf.txt";
        final Archive archive = makeTestArchive(true, TEST_DIR + sdrfFileName);

        context.setCenterConvertedToUUID(true);

        final boolean  isValid = dnaSeqSdrfValidator.doWork(archive, context);

        assertFalse(isValid);
        assertEquals(1, context.getErrorCount());
        checkErrorExists("An error occurred while validating SDRF for archive 'testArchive': " +
                "SDRF line 1: Barcode TCGA-1 found in Extract Name column. UUID must be used as aliquot identifiers\t[archive testArchive]");
        assertEquals(0, context.getWarningCount());
    }

    @Test
    public void testExtractNameValueNotValidUuidCenterNotConvertedToUuid()
            throws IOException, ParseException, Processor.ProcessorException {

        final String sdrfFileName = "extractNameValueNotValidUuid.sdrf.txt";
        final Archive archive = makeTestArchive(true, TEST_DIR + sdrfFileName);

        context.setCenterConvertedToUUID(false);

        mockery.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUuid(with("uuid-1"), with(context), with(sdrfFileName), with(true));
            will(new Action() {
                @Override
                public Object invoke(Invocation invocation) throws Throwable {
                    context.addError("Invalid uuid");
                    return false;
                }

                @Override
                public void describeTo(final Description description) {
                    description.appendText("Adding 1 error to the context and returning false");
                }
            });
        }});

        final boolean  isValid = dnaSeqSdrfValidator.doWork(archive, context);

        assertFalse(isValid);
        assertEquals(1, context.getErrorCount());
        checkErrorExists("Invalid uuid\t[archive testArchive]");
        assertEquals(0, context.getWarningCount());
    }

    @Test
    public void testExtractNameValueNotValidUuidCenterConvertedToUuid()
            throws IOException, ParseException, Processor.ProcessorException {

        final String sdrfFileName = "extractNameValueNotValidUuid.sdrf.txt";
        final Archive archive = makeTestArchive(true, TEST_DIR + sdrfFileName);

        context.setCenterConvertedToUUID(true);

        mockery.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUuid(with("uuid-1"), with(context), with(sdrfFileName), with(true));
            will(new Action() {
                @Override
                public Object invoke(Invocation invocation) throws Throwable {
                    context.addError("Invalid uuid");
                    return false;
                }

                @Override
                public void describeTo(final Description description) {
                    description.appendText("Adding 1 error to the context and returning false");
                }
            });
        }});

        final boolean  isValid = dnaSeqSdrfValidator.doWork(archive, context);

        assertFalse(isValid);
        assertEquals(1, context.getErrorCount());
        checkErrorExists("Invalid uuid\t[archive testArchive]");
        assertEquals(0, context.getWarningCount());
    }

    @Test
    public void testBarcodeForWrongDisease() throws IOException, ParseException, Processor.ProcessorException {

        final String sdrfFileName = "barcodeForWrongDisease.sdrf.txt";
        final Archive archive = makeTestArchive(true, TEST_DIR + sdrfFileName);
        final String tumorAbbreviation = "XYZ";
        archive.setTumorType(tumorAbbreviation);
        context.setArchive(archive);
        
        final String barcode= "TCGA-1";

        mockery.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validate(with(barcode), with(context), with(sdrfFileName), with(true));
            will(returnValue(true));

            one(mockBarcodeTumorValidator).barcodeIsValidForTumor(barcode, tumorAbbreviation);
            will(returnValue(false));
        }});

        final boolean  isValid = dnaSeqSdrfValidator.doWork(archive, context);

        assertFalse(isValid);
        assertEquals(1, context.getErrorCount());
        checkErrorExists("An error occurred while validating SDRF for archive 'testArchive': " +
                "SDRF line 1: Barcode 'TCGA-1' does not belong to the disease set for tumor type 'XYZ'\t[archive testArchive]");
        assertEquals(0, context.getWarningCount());

    }

    @Test
    public void testWarningForExtractNameBarcodeInMultipleArchives() throws IOException, ParseException, Processor.ProcessorException {

        final String sdrfFileName = "extractNameBarcodeInMultipleArchives.sdrf.txt";
        final Archive archive = makeTestArchive(true, TEST_DIR + sdrfFileName);
        final String tumorAbbreviation = "XYZ";
        archive.setTumorType(tumorAbbreviation);
        context.setArchive(archive);

        final String barcode= "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validate(with(barcode), with(context), with(sdrfFileName), with(true));
            will(returnValue(true));

            one(mockBarcodeTumorValidator).barcodeIsValidForTumor(barcode, tumorAbbreviation);
            will(returnValue(true));
        }});

        final boolean  isValid = dnaSeqSdrfValidator.doWork(archive, context);

        assertTrue(isValid);
        assertEquals(0, context.getErrorCount());
        assertEquals(1, context.getWarningCount());

        final String actualWarningMessage = context.getWarnings().get(0);
        final String expectedWarningMessage = new StringBuilder("Extract name ")
                .append(barcode)
                .append(" is included in both center_disease.platform.Level_8.0.1.2 and center_disease.platform.Level_8.9.1.2\t[testArchive]")
                .toString();

        assertEquals(expectedWarningMessage, actualWarningMessage);
    }

    @Test
    public void testValidSdrf() throws IOException, ParseException, Processor.ProcessorException {

        final String sdrfFileName = "valid.sdrf.txt";
        final Archive archive = makeTestArchive(true, TEST_DIR + sdrfFileName);

        mockery.checking(new Expectations() {{
            exactly(18).of(mockQcLiveBarcodeAndUUIDValidator).validateUuid(with(any(String.class)), with(context), with(sdrfFileName), with(true));
            will(returnValue(true));
        }});

        final boolean  isValid = dnaSeqSdrfValidator.doWork(archive, context);

        assertTrue(isValid);
        assertEquals(0, context.getErrorCount());
        assertEquals(0, context.getWarningCount());
    }

    @Test
    public void testInvalidProtocolRef() throws IOException, ParseException, Processor.ProcessorException {

        final String sdrfFileName = "invalidProtocolRef.sdrf.txt";
        final Archive archive = makeTestArchive(true, TEST_DIR + sdrfFileName);
        final boolean  isValid = dnaSeqSdrfValidator.doWork(archive, context);

        assertFalse(isValid);
        assertEquals(1, context.getErrorCount());
        checkErrorExists("line 2: value for 'Protocol REF' must be in the format 'domain:protocol:platform:version', " +
                "but found 'crazy:squirrel'\t[archive testArchive]");
        assertEquals(0, context.getWarningCount());
    }

    /**
     * Asserts that errors in the context contain the expected error message.
     *
     * @param expectedErrorMessage the expected error message
     */
    private void checkErrorExists(final String expectedErrorMessage) {

        assertTrue(context.getErrorCount() > 0);
        boolean foundExpectedErrorMessage = false;

        final Iterator<String> actualErrorsIterator = context.getErrors().iterator();
        while (!foundExpectedErrorMessage && actualErrorsIterator.hasNext()) {

            final String actualErrorMessage = actualErrorsIterator.next();
            foundExpectedErrorMessage = expectedErrorMessage.equals(actualErrorMessage);
        }

        assertTrue(foundExpectedErrorMessage);
    }

    /**
     * Return a test {@link Archive}.
     *
     *
     * @param isMageTab {@code true} if the archive is a mage-tab, {@code false} otherwise
     * @param sdrfFileName SDRF file name
     * @return a test {@link Archive}.
     * @throws IOException if the SDRF can not be read
     * @throws ParseException if the SDRF can not be parsed
     */
    private Archive makeTestArchive(final boolean isMageTab,
                                    final String sdrfFileName) throws IOException, ParseException {

        final Archive archive = new Archive(TEST_DIR.substring(0, TEST_DIR.lastIndexOf(File.separator)) + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        archive.setRealName("testArchive");

        if (isMageTab) {
            archive.setArchiveType(Archive.TYPE_MAGE_TAB);
        }

        if (StringUtils.isNotEmpty(sdrfFileName)) {

            archive.setSdrfFile(new File(sdrfFileName));

            final TabDelimitedContent tabDelimitedContent = new TabDelimitedContentImpl();
            archive.setSdrf(tabDelimitedContent);

            final TabDelimitedFileParser tabDelimitedFileParser = new TabDelimitedFileParser();
            tabDelimitedFileParser.setTabDelimitedContent(tabDelimitedContent);
            tabDelimitedFileParser.loadTabDelimitedContent(sdrfFileName);
            tabDelimitedFileParser.loadTabDelimitedContentHeader();
        }

        return archive;
    }
}
