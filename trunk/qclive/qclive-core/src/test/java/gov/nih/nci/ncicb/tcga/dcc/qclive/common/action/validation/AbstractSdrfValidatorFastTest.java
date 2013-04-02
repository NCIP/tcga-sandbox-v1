package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContentImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedContentNavigator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeTumorValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * AbstractSdrfValidator unit test
 *
 * @author Julien Baboud
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class AbstractSdrfValidatorFastTest {

    private final Mockery context = new JUnit4Mockery();

    private static final String SAMPLES_DIR = Thread.currentThread().getContextClassLoader().getResource("samples").getPath();

    private static final String ARCHIVE_DIR = SAMPLES_DIR + File.separator
            + "qclive" + File.separator
            + "abstractSdrfValidator";

    private MockAbstractSdrfValidator mockAbstractSdrfValidator;
    private QcLiveBarcodeAndUUIDValidator mockQcLiveBarcodeAndUUIDValidator = context.mock(QcLiveBarcodeAndUUIDValidator.class);
    private BarcodeTumorValidator mockBarcodeTumorValidator = context.mock(BarcodeTumorValidator.class);

    @Before
    public void setUp() {
        mockAbstractSdrfValidator = new MockAbstractSdrfValidator();
        mockAbstractSdrfValidator.setQcLiveBarcodeAndUUIDValidator(mockQcLiveBarcodeAndUUIDValidator);
        mockAbstractSdrfValidator.setBarcodeTumorValidator(mockBarcodeTumorValidator);
    }

    @Test
    public void testValidateWithIDFMethodOneTermSourceREF() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        final Archive archive = new Archive(ARCHIVE_DIR + ".tar.gz");

        final QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);

        final String[] headers = {"header 1", "Term Source REF"};
        final String[] row1 = {"value 1", "Source 1"};
        final TabDelimitedContentNavigator sdrfNavigator = getTabDelimitedContentNavigator(headers, row1);
        final boolean result = (Boolean) getValidateWithIDFMethod().invoke(mockAbstractSdrfValidator, archive, qcContext, sdrfNavigator);

        assertTrue(result);
        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testValidateWithIDFMethodTwoTermSourceREF() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        final Archive archive = new Archive(ARCHIVE_DIR + ".tar.gz");

        final QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);

        final String[] headers = {"hTerm Source REF", "Term Source REF"};
        final String[] row1 = {"Source 1", "Source 2"};
        final TabDelimitedContentNavigator sdrfNavigator = getTabDelimitedContentNavigator(headers, row1);
        final boolean result = (Boolean) getValidateWithIDFMethod().invoke(mockAbstractSdrfValidator, archive, qcContext, sdrfNavigator);

        assertTrue(result);
        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testValidateWithIDFMethodOneTermSourceREFMissing() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        final Archive archive = new Archive(ARCHIVE_DIR + ".tar.gz");

        final QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);

        final String[] headers = {"header 1", "Term Source REF"};
        final String[] row1 = {"value 1", "notARef"};
        final TabDelimitedContentNavigator sdrfNavigator = getTabDelimitedContentNavigator(headers, row1);
        final boolean result = (Boolean) getValidateWithIDFMethod().invoke(mockAbstractSdrfValidator, archive, qcContext, sdrfNavigator);

        assertFalse(result);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("An error occurred while validating SDRF for archive 'abstractSdrfValidator': " +
                "SDRF Term Source REF values don't match Term Source Names in the IDF: [notARef]", qcContext.getErrors().get(0));
    }

    @Test
    public void testValidateWithIDFMethodTwoTermSourceREFMissing() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        final Archive archive = new Archive(ARCHIVE_DIR + ".tar.gz");

        final QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);

        final String[] headers = {"Term Source REF", "Term Source REF"};
        final String[] row1 = {"notARef 1", "notARef 2"};
        final TabDelimitedContentNavigator sdrfNavigator = getTabDelimitedContentNavigator(headers, row1);
        final boolean result = (Boolean) getValidateWithIDFMethod().invoke(mockAbstractSdrfValidator, archive, qcContext, sdrfNavigator);

        assertFalse(result);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("An error occurred while validating SDRF for archive 'abstractSdrfValidator': " +
                "SDRF Term Source REF values don't match Term Source Names in the IDF: [notARef 1, notARef 2]", qcContext.getErrors().get(0));
    }

    @Test
    public void testValidateWithIDFMethodTermSourceREFIsControl() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        final Archive archive = new Archive(ARCHIVE_DIR + ".tar.gz");

        final QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);

        final String[] headers = {"header 1", "Term Source REF"};
        final String[] row1 = {"value 1", "->"};
        final TabDelimitedContentNavigator sdrfNavigator = getTabDelimitedContentNavigator(headers, row1);
        final boolean result = (Boolean) getValidateWithIDFMethod().invoke(mockAbstractSdrfValidator, archive, qcContext, sdrfNavigator);

        assertTrue(result);
        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testValidateWithIDFMethodNoTermSourceREF() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        final Archive archive = new Archive(ARCHIVE_DIR + ".tar.gz");

        final QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);

        final String[] headers = {"header 1", "header 2"};
        final String[] firstRow = {"value 1", "value 2"};
        final TabDelimitedContentNavigator sdrfNavigator = getTabDelimitedContentNavigator(headers, firstRow);
        final boolean result = (Boolean) getValidateWithIDFMethod().invoke(mockAbstractSdrfValidator, archive, qcContext, sdrfNavigator);

        assertTrue(result);
        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testValidateCommentColumnsIncludeForAnalysisGood() {
        final Archive archive = new Archive(ARCHIVE_DIR + ".tar.gz");

        final QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);

        final String[] headers = {"Derived Data File", "Comment [TCGA Include for Analysis]", "Comment [TCGA Archive Name]", "Comment [TCGA Data Level]", "Comment [TCGA Data Type]"};
        final String[] firstRow = {"foo", "yes", "unc.edu_LAML.IlluminaHiSeq_RNASeq.Level_2.1.13.0", "Level 2", "foo"};
        final TabDelimitedContentNavigator sdrfNavigator = getTabDelimitedContentNavigator(headers, firstRow);
        final boolean result = mockAbstractSdrfValidator.validateCommentColumns(qcContext, sdrfNavigator);

        assertTrue(result);
        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testValidateCommentColumnsIncludeForAnalysisNotPresent() {
        final Archive archive = new Archive(ARCHIVE_DIR + ".tar.gz");

        final QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);

        final String[] headers = {"Derived Data File", "Comment [TCGA Archive Name]", "Comment [TCGA Data Level]", "Comment [TCGA Data Type]"};
        final String[] firstRow = {"foo", "unc.edu_LAML.IlluminaHiSeq_RNASeq.Level_2.1.13.0", "Level 2", "foo"};
        final TabDelimitedContentNavigator sdrfNavigator = getTabDelimitedContentNavigator(headers, firstRow);
        final boolean result = mockAbstractSdrfValidator.validateCommentColumns(qcContext, sdrfNavigator);

        assertFalse(result);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("Required SDRF column 'Comment [TCGA Include for Analysis]' is missing", qcContext.getErrors().get(0));
    }

    @Test
    public void testValidateCommentColumnsIncludeForAnalysisNotChecked() {
        final Archive archive = new Archive(ARCHIVE_DIR + ".tar.gz");

        final QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);

        final String[] headers = {"Derived Data File REF", "Comment [TCGA Archive Name]", "Comment [TCGA Data Level]", "Comment [TCGA Data Type]"};
        final String[] firstRow = {"foo", "unc.edu_LAML.IlluminaHiSeq_RNASeq.Level_2.1.13.0", "Level 2", "foo"};
        final TabDelimitedContentNavigator sdrfNavigator = getTabDelimitedContentNavigator(headers, firstRow);
        final boolean result = mockAbstractSdrfValidator.validateCommentColumns(qcContext, sdrfNavigator);

        assertTrue(result);
        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testValidateCommentColumnsIncludeForAnalysisBad() {
        final Archive archive = new Archive(ARCHIVE_DIR + ".tar.gz");

        final QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);

        final String[] headers = {"Derived Data File", "Comment [TCGA Include for Analysis]", "Comment [TCGA Archive Name]", "Comment [TCGA Data Level]", "Comment [TCGA Data Type]"};
        final String[] firstRow = {"foo", "foo", "unc.edu_LAML.IlluminaHiSeq_RNASeq.Level_2.1.13.0", "Level 2", "foo"};
        final TabDelimitedContentNavigator sdrfNavigator = getTabDelimitedContentNavigator(headers, firstRow);
        final boolean result = mockAbstractSdrfValidator.validateCommentColumns(qcContext, sdrfNavigator);

        assertFalse(result);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("line 2: value for 'Comment [TCGA Include for Analysis]' must be in the format 'either 'yes' or 'no'', but found 'foo'", qcContext.getErrors().get(0));
    }

    @Test
    public void testCheckAllColumnsForBlanksWhenBlankValue() {

        final Archive archive = new Archive(ARCHIVE_DIR + ".tar.gz");

        final QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);

        final String headerName = "Header 1";
        final String[] headers = {headerName};
        final String[] firstRow = {""};
        final TabDelimitedContentNavigator sdrfNavigator = getTabDelimitedContentNavigator(headers, firstRow);
        final boolean result = mockAbstractSdrfValidator.checkAllColumnsForBlanks(sdrfNavigator, qcContext);

        assertFalse(result);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals(qcContext.getErrors().get(0), "SDRF column '" + headerName + "' (column 1) contains a blank value.");
    }

    @Test
    public void testCheckAllColumnsForBlanksWhenNoBlankValue() {

        final Archive archive = new Archive(ARCHIVE_DIR + ".tar.gz");

        final QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);

        final String headerName = "Header 1";
        final String[] headers = {headerName};
        final String[] firstRow = {"->"};
        final TabDelimitedContentNavigator sdrfNavigator = getTabDelimitedContentNavigator(headers, firstRow);
        final boolean result = mockAbstractSdrfValidator.checkAllColumnsForBlanks(sdrfNavigator, qcContext);

        assertTrue(result);
    }

    @Test
    public void testValidateColumnWhenMissingButRequired() {

        final Archive archive = new Archive(ARCHIVE_DIR + ".tar.gz");

        final QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);

        final String[] headers = {"Header 1"};
        final String[] firstRow = {"Value 1"};
        final TabDelimitedContentNavigator sdrfNavigator = getTabDelimitedContentNavigator(headers, firstRow);
        final boolean result = mockAbstractSdrfValidator.validateColumn(sdrfNavigator, "Header 2", true, true, qcContext);

        assertFalse(result);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals(qcContext.getErrors().get(0), "Required SDRF column 'Header 2' is missing");
    }

    @Test
    public void testValidateColumnWhenMissingButNotRequired() {

        final Archive archive = new Archive(ARCHIVE_DIR + ".tar.gz");

        final QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);

        final String[] headers = {"Header 1"};
        final String[] firstRow = {"Value 1"};
        final TabDelimitedContentNavigator sdrfNavigator = getTabDelimitedContentNavigator(headers, firstRow);
        final boolean result = mockAbstractSdrfValidator.validateColumn(sdrfNavigator, "Header 2", false, true, qcContext);

        assertTrue(result);
    }

    @Test
    public void testValidateColumnWhenDataRequiredButNotFound() {

        final Archive archive = new Archive(ARCHIVE_DIR + ".tar.gz");

        final QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);

        final String headerName = "Header 1";
        final String[] headers = {headerName};
        final String[] firstRow = {"->"};
        final TabDelimitedContentNavigator sdrfNavigator = getTabDelimitedContentNavigator(headers, firstRow);
        final boolean result = mockAbstractSdrfValidator.validateColumn(sdrfNavigator, headerName, true, true, qcContext);

        assertFalse(result);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals(qcContext.getErrors().get(0), "Missing required value(s) 'abstractSdrfValidator' for '" + headerName + "'");
    }

    @Test
    public void testValidateColumnWhenDataRequired() {

        final Archive archive = new Archive(ARCHIVE_DIR + ".tar.gz");

        final QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);

        final String headerName = "Header 1";
        final String[] headers = {headerName};
        final String[] firstRow = {"Value 1"};
        final TabDelimitedContentNavigator sdrfNavigator = getTabDelimitedContentNavigator(headers, firstRow);
        final boolean result = mockAbstractSdrfValidator.validateColumn(sdrfNavigator, headerName, true, true, qcContext);

        assertTrue(result);
    }

    @Test
    public void testCenterConvertedToUUIDs() throws Exception {
        final Archive archive = new Archive(ARCHIVE_DIR + ".tar.gz");

        final QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);
        archive.setSdrfFile(new File("sdrfFile"));
        qcContext.setCenterConvertedToUUID(true);
        final String headerName = AbstractSdrfValidator.EXTRACT_NAME_COLUMN_NAME;
        final String[] headers = {headerName, AbstractSdrfValidator.BARCODE_COMMENT_COLUMN_NAME};
        final String[] firstRow = {"some fake uuid", "TCGA-00-0000-00A-00D-0000-00"};
        final TabDelimitedContentNavigator sdrfNavigator = getTabDelimitedContentNavigator(headers, firstRow);

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUuid("some fake uuid", qcContext, qcContext.getArchive().getSdrfFile().getName(), true);
            will(returnValue(false));
        }});
        final boolean result = mockAbstractSdrfValidator.validateBarcodesAndUuids(qcContext, sdrfNavigator);
        assertFalse(result);
    }

    @Test
    public void testCenterConvertedButBarcodeInExtract() throws Processor.ProcessorException {
        // extract name has barcode, no Barcode comment column

        final Archive archive = new Archive(ARCHIVE_DIR + ".tar.gz");

        final QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);
        archive.setSdrfFile(new File("sdrfFile"));
        qcContext.setCenterConvertedToUUID(true);
        final String[] headers = {AbstractSdrfValidator.EXTRACT_NAME_COLUMN_NAME};
        final String[] firstRow = {"TCGA-00-0000-01A-10D-1234-00"};
        final TabDelimitedContentNavigator sdrfNavigator = getTabDelimitedContentNavigator(headers, firstRow);

        final boolean result = mockAbstractSdrfValidator.validateBarcodesAndUuids(qcContext, sdrfNavigator);
        assertFalse(result);
        assertEquals("An error occurred while validating SDRF for archive 'abstractSdrfValidator': SDRF  The number of barcodes in Comment [TCGA Barcode] is not the same as UUIDs in Extract Name", qcContext.getErrors().get(0));

    }

    @Test
    public void testCenterConvertedBarcodeInExtractWithComment() throws Processor.ProcessorException {
        final Archive archive = new Archive(ARCHIVE_DIR + ".tar.gz");

        final QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);
        archive.setSdrfFile(new File("sdrfFile"));
        qcContext.setCenterConvertedToUUID(true);
        final String[] headers = {AbstractSdrfValidator.EXTRACT_NAME_COLUMN_NAME, AbstractSdrfValidator.BARCODE_COMMENT_COLUMN_NAME};
        final String[] firstRow = {"TCGA-00-0000-01A-10D-1234-00", "TCGA-00-0000-01A-10D-1234-00"};
        final TabDelimitedContentNavigator sdrfNavigator = getTabDelimitedContentNavigator(headers, firstRow);

        final boolean result = mockAbstractSdrfValidator.validateBarcodesAndUuids(qcContext, sdrfNavigator);
        assertFalse(result);
        assertEquals("An error occurred while validating SDRF for archive 'abstractSdrfValidator': SDRF line 1: Barcode TCGA-00-0000-01A-10D-1234-00 found in Extract Name column. UUID must be used as aliquot identifiers",
                qcContext.getErrors().get(0));

    }

    @Test
    public void testCenterConvertedUuidBarcodeMismatch() throws Processor.ProcessorException {
        // uuid and barcode in comment column do not match
        final Archive archive = new Archive(ARCHIVE_DIR + ".tar.gz");

        final QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);
        archive.setSdrfFile(new File("sdrfFile"));
        qcContext.setCenterConvertedToUUID(true);
        final String headerName = AbstractSdrfValidator.EXTRACT_NAME_COLUMN_NAME;
        final String[] headers = {headerName, AbstractSdrfValidator.BARCODE_COMMENT_COLUMN_NAME};
        final String[] firstRow = {"this is a uuid", "TCGA-00-0000-00A-00D-0000-00"};
        final TabDelimitedContentNavigator sdrfNavigator = getTabDelimitedContentNavigator(headers, firstRow);

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUuid("this is a uuid", qcContext, qcContext.getArchive().getSdrfFile().getName(), true);
            will(returnValue(true));
            one(mockQcLiveBarcodeAndUUIDValidator).isAliquotUUID("this is a uuid");
            will(returnValue(true));
            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDBarcodeMapping("this is a uuid", "TCGA-00-0000-00A-00D-0000-00");
            will(returnValue(false));
            one(mockQcLiveBarcodeAndUUIDValidator).isMatchingDiseaseForUUID("this is a uuid", qcContext.getArchive().getTumorType());
            will(returnValue(true));
        }});
        final boolean result = mockAbstractSdrfValidator.validateBarcodesAndUuids(qcContext, sdrfNavigator);
        assertFalse(result);
        assertEquals("An error occurred while validating SDRF for archive 'abstractSdrfValidator': SDRF line 1: " +
                "The metadata for UUID 'this is a uuid' found in Extract Name column and barcode 'TCGA-00-0000-00A-00D-0000-00' " +
                "found in Comment [TCGA Barcode] column do not match",
                qcContext.getErrors().get(0));

    }

    @Test
    public void testCenterConvertedUuidDiseaseMismatch() throws Processor.ProcessorException {
        final Archive archive = new Archive(ARCHIVE_DIR + ".tar.gz");
        archive.setTumorType("GBM");
        final QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);
        archive.setSdrfFile(new File("sdrfFile"));
        qcContext.setCenterConvertedToUUID(true);
        final String headerName = AbstractSdrfValidator.EXTRACT_NAME_COLUMN_NAME;
        final String[] headers = {headerName, AbstractSdrfValidator.BARCODE_COMMENT_COLUMN_NAME};
        final String[] firstRow = {"this is a uuid", "TCGA-00-0000-00A-00D-0000-00"};
        final TabDelimitedContentNavigator sdrfNavigator = getTabDelimitedContentNavigator(headers, firstRow);

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUuid("this is a uuid", qcContext, qcContext.getArchive().getSdrfFile().getName(), true);
            will(returnValue(true));
            one(mockQcLiveBarcodeAndUUIDValidator).isAliquotUUID("this is a uuid");
            will(returnValue(true));
            one(mockQcLiveBarcodeAndUUIDValidator).isMatchingDiseaseForUUID("this is a uuid", qcContext.getArchive().getTumorType());
            will(returnValue(false));
        }});
        final boolean result = mockAbstractSdrfValidator.validateBarcodesAndUuids(qcContext, sdrfNavigator);
        assertFalse(result);
        assertEquals("An error occurred while validating SDRF for archive " +
                "'abstractSdrfValidator': SDRF line 1: The disease for UUID " +
                "'this is a uuid' found in Extract Name column does not match " +
                "the archive disease 'GBM'.",
                qcContext.getErrors().get(0));

    }

    @Test
    public void testCenterNotConvertedToUUIDs() throws Exception {
        final Archive archive = new Archive(ARCHIVE_DIR + ".tar.gz");

        final QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);
        archive.setSdrfFile(new File("sdrfFile"));

        final String headerName = AbstractSdrfValidator.EXTRACT_NAME_COLUMN_NAME;
        final String[] headers = {headerName};
        final String[] firstRow = {"TCGA-00-0000-00A-00D-0000-00"};
        final TabDelimitedContentNavigator sdrfNavigator = getTabDelimitedContentNavigator(headers, firstRow);

        context.checking(new Expectations() {{
            allowing(mockQcLiveBarcodeAndUUIDValidator).validate("TCGA-00-0000-00A-00D-0000-00", qcContext, qcContext.getArchive().getSdrfFile().getName(), true);
            will(returnValue(true));
            one(mockBarcodeTumorValidator).barcodeIsValidForTumor("TCGA-00-0000-00A-00D-0000-00", null);
            will(returnValue(true));
        }});
        final boolean result = mockAbstractSdrfValidator.validateBarcodesAndUuids(qcContext, sdrfNavigator);

        assertTrue(result);

    }

    @Test
    public void testCenterConvertedBadBarcodeStandaloneNoRemote() throws Processor.ProcessorException {
        // uuid and barcode in comment column do not match
        final Archive archive = new Archive(ARCHIVE_DIR + ".tar.gz");

        final QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);
        archive.setSdrfFile(new File("sdrfFile"));
        qcContext.setCenterConvertedToUUID(true);

        final String headerName = AbstractSdrfValidator.EXTRACT_NAME_COLUMN_NAME;
        final String[] headers = {headerName, AbstractSdrfValidator.BARCODE_COMMENT_COLUMN_NAME};
        final String[] firstRow = {"this is a uuid", "barcode!!!"};
        final TabDelimitedContentNavigator sdrfNavigator = getTabDelimitedContentNavigator(headers, firstRow);

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateUuid("this is a uuid", qcContext, qcContext.getArchive().getSdrfFile().getName(), true);
            will(returnValue(true));
            one(mockQcLiveBarcodeAndUUIDValidator).isAliquotUUID("this is a uuid");
            will(returnValue(true));
            one(mockQcLiveBarcodeAndUUIDValidator).validateAliquotBarcodeFormat("barcode!!!");
            will(returnValue(false));
            one(mockQcLiveBarcodeAndUUIDValidator).batchValidateUUIDsReportIndividualResults(Arrays.asList("this is a uuid"), qcContext, "sdrfFile", true);
            will(returnValue(new HashMap<String, Boolean>()));
            one(mockQcLiveBarcodeAndUUIDValidator).isMatchingDiseaseForUUID("this is a uuid", qcContext.getArchive().getTumorType());
            will(returnValue(true));
        }});

        qcContext.setStandaloneValidator(true);
        qcContext.setNoRemote(true);
        final boolean result = mockAbstractSdrfValidator.validateBarcodesAndUuids(qcContext, sdrfNavigator);
        assertFalse(result);
        assertEquals("An error occurred while validating SDRF for archive 'abstractSdrfValidator': SDRF line 1: barcode 'barcode!!!' found in Comment [TCGA Barcode] is not a valid aliquot barcode",
                qcContext.getErrors().get(0));
    }


    /**
     * Returns a {@link TabDelimitedContentNavigator} for a file that has the given headers and first row.
     *
     * @param headers  the headers
     * @param firstRow the first row
     * @return a {@link TabDelimitedContentNavigator} for a file that has the given headers and first row
     */
    private TabDelimitedContentNavigator getTabDelimitedContentNavigator(final String[] headers,
                                                                         final String[] firstRow) {

        final TabDelimitedContentNavigator result = new TabDelimitedContentNavigator();
        final Map<Integer, String[]> sdrfRows = new HashMap<Integer, String[]>();
        sdrfRows.put(0, headers);
        sdrfRows.put(1, firstRow);

        final TabDelimitedContent tabDelimitedContent = new TabDelimitedContentImpl();
        tabDelimitedContent.setTabDelimitedHeader(headers);
        tabDelimitedContent.setTabDelimitedContents(sdrfRows);

        result.setTabDelimitedContent(tabDelimitedContent);

        return result;
    }

    /**
     * Exposes <code>AbstractSdrfValidator.validateWithIDF()</code> method and return it.
     *
     * @return <code>AbstractSdrfValidator.validateWithIDF()</code> method
     * @throws NoSuchMethodException
     */
    private Method getValidateWithIDFMethod() throws NoSuchMethodException {

        final Method result = AbstractSdrfValidator.class.getDeclaredMethod("validateWithIDF",
                Archive.class, QcContext.class, TabDelimitedContentNavigator.class);
        result.setAccessible(true);

        return result;
    }

    /**
     * Mock class for AbstractSdrfValidator
     */
    private class MockAbstractSdrfValidator extends AbstractSdrfValidator {

        @Override
        protected Collection<String> getAllowedSdrfHeaders() throws ProcessorException {
            return null;
        }

        @Override
        protected Map<String, Boolean> getColumnsToCheck() {
            return null;
        }

        @Override
        protected boolean validateFileHeaderAndLevel(final QcContext context,
                                                     final String header,
                                                     final int row,
                                                     final String level) {
            return true;
        }

        @Override
        protected boolean runSpecificValidations(final QcContext context,
                                                 final TabDelimitedContentNavigator sdrfNavigator) {
            return false;
        }

        @Override
        protected boolean getDataRequired() {
            return false;
        }

        @Override
        protected boolean validateColumnValue(final String columnName,
                                              final String value,
                                              final int lineNum,
                                              final QcContext context) {
            return true;
        }
    }
}
