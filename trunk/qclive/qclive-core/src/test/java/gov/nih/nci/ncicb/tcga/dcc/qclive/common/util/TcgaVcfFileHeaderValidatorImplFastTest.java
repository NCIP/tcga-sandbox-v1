package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFileHeader;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test for Tcga-specific VCF header validator.
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class TcgaVcfFileHeaderValidatorImplFastTest {
    private final Mockery context = new JUnit4Mockery();
    private TcgaVcfFileHeaderValidator tcgaVcfFileHeaderValidator;
    private final VcfHeaderDefinitionStore mockVcfHeaderDefinitionStore = context.mock(VcfHeaderDefinitionStore.class);
    private QcContext qcContext;

    @Before
    public void setUp() {
        qcContext = new QcContext();
        tcgaVcfFileHeaderValidator = new TcgaVcfFileHeaderValidator();
        tcgaVcfFileHeaderValidator.setVcfHeaderDefinitionStore(mockVcfHeaderDefinitionStore);

        context.checking(new Expectations() {{
            allowing(mockVcfHeaderDefinitionStore).getHeaderDefinition(with("PEDIGREE"), with(any(String.class)));
            will(returnValue(null));

            allowing(mockVcfHeaderDefinitionStore).getHeaderDefinition(with("fileDate"), with(any(String.class)));
            will(returnValue(null));
        }});
    }

    @Test
    public void test() {
        assertNotNull(tcgaVcfFileHeaderValidator);
    }

    @Test
    public void testHeaderWithCorrectDefinition() {
        context.checking(new Expectations() {{
            one(mockVcfHeaderDefinitionStore).getHeaderDefinition("INFO", "ABC");
            will(returnValue(makeHeader("INFO", "ABC", "1", "String", "\"The first three letters of the alphabet\"")));
        }});

        final VcfFileHeader headerToValidate = makeHeader("INFO", "ABC", "1", "String", "\"The first three letters of the alphabet\"");

        final boolean isValid = tcgaVcfFileHeaderValidator.validate(headerToValidate, qcContext);
        assertTrue(qcContext.getErrors().toString(), isValid);
    }

    @Test
    public void testHeaderWithWrongNumber() {
        context.checking(new Expectations() {{
            one(mockVcfHeaderDefinitionStore).getHeaderDefinition("INFO", "ABC");
            will(returnValue(makeHeader("INFO", "ABC", "2", "String", "\"The first three letters of the alphabet\"")));
        }});

        final VcfFileHeader headerToValidate = makeHeader("INFO", "ABC", "1", "String", "\"The first three letters of the alphabet\"");

        final boolean isValid = tcgaVcfFileHeaderValidator.validate(headerToValidate, qcContext);
        assertFalse(isValid);
        assertEquals("INFO header with ID 'ABC' expected to have Number value 2 but found 1", qcContext.getErrors().get(0));
    }

    @Test
    public void testHeaderWithWrongDefinition() {
        context.checking(new Expectations() {{
            one(mockVcfHeaderDefinitionStore).getHeaderDefinition("INFO", "ABC");
            will(returnValue(makeHeader("INFO", "ABC", "1", "String", "\"The first three letters of the alphabet\"")));
        }});

        final VcfFileHeader headerToValidate = makeHeader("INFO", "ABC", "1", "String", "\"The first four letters of the alphabet\"");

        final boolean isValid = tcgaVcfFileHeaderValidator.validate(headerToValidate, qcContext);
        assertFalse(isValid);
        assertEquals("INFO header with ID 'ABC' expected to have Description value \"The first three letters of the alphabet\" but found \"The first four letters of the alphabet\"", qcContext.getErrors().get(0));
    }

    @Test
    public void testHeaderWithWrongType() {
        context.checking(new Expectations() {{
            one(mockVcfHeaderDefinitionStore).getHeaderDefinition("INFO", "ABC");
            will(returnValue(makeHeader("INFO", "ABC", "1", "Number", "\"The first three letters of the alphabet\"")));
        }});

        final VcfFileHeader headerToValidate = makeHeader("INFO", "ABC", "1", "String", "\"The first three letters of the alphabet\"");

        final boolean isValid = tcgaVcfFileHeaderValidator.validate(headerToValidate, qcContext);
        assertFalse(isValid);
        assertEquals("INFO header with ID 'ABC' expected to have Type value Number but found String", qcContext.getErrors().get(0));
    }

    @Test
    public void testValidateValidPedigree() {
        final Map<String, String> valueMap = new HashMap<String, String>();
        valueMap.put("Name_0", "abc");
        valueMap.put("Name_1", "def");
        valueMap.put("genome_1", "tumor");
        valueMap.put("genome2", "normal");
        valueMap.put("someKey", "someValue");

        testPedigree(true, valueMap);
    }

    @Test
    public void testValidatePedigreeDuplicateValues() {
        final Map<String, String> valueMap = new HashMap<String, String>();
        valueMap.put("genome1", "tumor");
        valueMap.put("genome2", "tumor");
        testPedigree(false, valueMap, "PEDIGREE header values may not be repeated across keys, but found 'tumor' more than once.");
    }

    @Test
    public void testValidateInvalidPedigreeOneKey() {
        final Map<String, String> valueMap = new HashMap<String, String>();
        valueMap.put("Name_0", "hi");
        testPedigree(false, valueMap, "PEDIGREE header must contain at least 2 keys, but found 1");
    }

    @Test
    public void testValidateInvalidPedigreeNoKeys() {
        testPedigree(false, null, "PEDIGREE header on line 10 has no value", "PEDIGREE header must be in the format '<key1=value1,key2=value2,...>'");
    }

    @Test
    public void testValidateInvalidPedigreeBadValue() {
        final Map<String, String> valueMap = new LinkedHashMap<String, String>();
        valueMap.put("Name_1", "hi there");
        valueMap.put("Name_2", "<no>");
        testPedigree(false, valueMap, "PEDIGREE header on line 10 Name_1 value 'hi there' is invalid: may not contain spaces, equals signs, commas, or semi-colons unless surrounded by double quotes",
                "PEDIGREE header values may not contain whitespace, angle brackets, or commas, but found 'hi there'",
                "PEDIGREE header values may not contain whitespace, angle brackets, or commas, but found '<no>'");
    }

    private void testPedigree(final boolean shouldValidate, final Map<String, String> valueMap, final String... expectedErrors) {
        final VcfFileHeader pedigreeHeader = new VcfFileHeader(TcgaVcfFileHeaderValidator.HEADER_TYPE_PEDIGREE);
        pedigreeHeader.setLineNumber(10);
        pedigreeHeader.setValueMap(valueMap);

        assertEquals(shouldValidate, tcgaVcfFileHeaderValidator.validate(pedigreeHeader, qcContext));
        assertEquals(expectedErrors.length, qcContext.getErrorCount());
        for (int i=0; i<expectedErrors.length; i++) {
            assertEquals(expectedErrors[i], qcContext.getErrors().get(i));

        }
    }

    @Test
    public void testValidateFileDateHeaderValid() {
        testFileDate(true, "20110101");
    }

    @Test
    public void testValidateFileDateHeaderInvalidMonth() {
        testFileDate( false, "20115901", "fileDate header must contain value in the format 'yyyyMMdd'" );   // 59 is invalid month
    }

    @Test
    public void testValidateFileDateHeaderInvalidFormat() {
        testFileDate( false, "Jan 01, 2011", "fileDate header on line 10 value 'Jan 01, 2011' is invalid: may not contain spaces, equals signs, commas, or semi-colons unless surrounded by double quotes",
                "fileDate header must contain value in the format 'yyyyMMdd'" );
    }

    @Test
    public void testValidateSampleColumnHeaderValid() {
        final VcfFile vcf = new VcfFile();
        final VcfFileHeader header = new VcfFileHeader("SAMPLE");
        header.setValueMap(new HashMap<String, String>() {{
            put("a", "b");
            put("descr", "\"this is a description\"");
            put("ID", "TEN");
        }});
        vcf.setHeaders(Arrays.asList(header));
        vcf.setColumnHeader(Arrays.asList("ONE","TWO","THREE","FOUR","FIVE","SIX","SEVEN","EIGHT","NINE", "TEN"));
        assertTrue(tcgaVcfFileHeaderValidator.validateSampleColumnHeader(vcf, qcContext));
    }

    @Test
    public void testValidateSampleColumnHeaderExtraHeader() {
        final VcfFile vcf = new VcfFile();
        final VcfFileHeader headerOne = new VcfFileHeader("SAMPLE");
        headerOne.setValueMap(new HashMap<String, String>() {{
            put("ID", "TEN");
        }});
        final VcfFileHeader headerTwo = new VcfFileHeader("SAMPLE");
        headerTwo.setValueMap(new HashMap<String, String>() {{
            put("ID", "ELEVEN");
        }});
        vcf.setHeaders(Arrays.asList(headerOne));
        vcf.setHeaders(Arrays.asList(headerTwo));
        vcf.setColumnHeader(Arrays.asList("ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN"));
        assertTrue(tcgaVcfFileHeaderValidator.validateSampleColumnHeader(vcf, qcContext));
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(10, vcf.getColumnHeader().size());
    }

    @Test
    public void testValidateSampleColumnHeaderExtraColumn() {
        final VcfFile vcf = new VcfFile();
        final VcfFileHeader headerOne = new VcfFileHeader("SAMPLE");
        headerOne.setValueMap(new HashMap<String, String>() {{
            put("ID", "TEN");
        }});
        vcf.setHeaders(Arrays.asList(headerOne));
        vcf.setColumnHeader(Arrays.asList("ONE","TWO","THREE","FOUR","FIVE","SIX","SEVEN","EIGHT","NINE", "TEN", "ELEVEN"));
        assertFalse(tcgaVcfFileHeaderValidator.validateSampleColumnHeader(vcf, qcContext));
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("Column header contains sample column name 'ELEVEN' that does not have a corresponding SAMPLE header", qcContext.getErrors().get(0));
        assertEquals(11, vcf.getColumnHeader().size());
    }

    @Test
    public void testValidateSampleColumnHeaderValue() {
        final VcfFile vcf = new VcfFile();
        final VcfFileHeader headerOne = new VcfFileHeader("SAMPLE");
        headerOne.setValue("TEN");
        vcf.setHeaders(Arrays.asList(headerOne));
        vcf.setColumnHeader(Arrays.asList("ONE","TWO","THREE","FOUR","FIVE","SIX","SEVEN","EIGHT","NINE", "TEN"));
        assertFalse(tcgaVcfFileHeaderValidator.validateSampleColumnHeader(vcf, qcContext));
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("Column header contains sample column name 'TEN' that does not have a corresponding SAMPLE header", qcContext.getErrors().get(0));
        assertEquals(10, vcf.getColumnHeader().size());
    }

    @Test
    public void testValidateSampleColumnHeaderNoId() {
        final VcfFile vcf = new VcfFile();
        final VcfFileHeader headerOne = new VcfFileHeader("SAMPLE");
        headerOne.setValueMap(new HashMap<String, String>() {{
            put("foo", "TEN");
        }});
        vcf.setHeaders(Arrays.asList(headerOne));
        vcf.setColumnHeader(Arrays.asList("ONE","TWO","THREE","FOUR","FIVE","SIX","SEVEN","EIGHT","NINE", "TEN"));
        assertFalse(tcgaVcfFileHeaderValidator.validateSampleColumnHeader(vcf, qcContext));
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("Column header contains sample column name 'TEN' that does not have a corresponding SAMPLE header", qcContext.getErrors().get(0));
        assertEquals(10, vcf.getColumnHeader().size());
    }

    @Test
    public void testValidateSampleColumnHeaderInvalid() {
        final VcfFile vcf = new VcfFile();
        final VcfFileHeader headerOne = new VcfFileHeader("SAMPLE");
        headerOne.setValueMap(new HashMap<String, String>() {{
            put("ID", "TEN");
        }});
        final VcfFileHeader headerTwo = new VcfFileHeader("SAMPLE");
        headerTwo.setValueMap(new HashMap<String, String>() {{
            put("ID", "ELEVEN");
        }});
        vcf.setHeaders(Arrays.asList(headerOne));
        vcf.setHeaders(Arrays.asList(headerTwo));
        vcf.setColumnHeader(Arrays.asList("ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE", "TEN", "GOOSE", "TURNIP"));
        assertFalse(tcgaVcfFileHeaderValidator.validateSampleColumnHeader(vcf, qcContext));
        assertEquals(2, qcContext.getErrorCount());
        assertEquals("Column header contains sample column name 'GOOSE' that does not have a corresponding SAMPLE header", qcContext.getErrors().get(0));
        assertEquals("Column header contains sample column name 'TURNIP' that does not have a corresponding SAMPLE header", qcContext.getErrors().get(1));
        assertEquals(12, vcf.getColumnHeader().size());
    }

    @Test
    public void testWithNullColumnHeader() {
        final VcfFile vcf = new VcfFile();
        final VcfFileHeader header = new VcfFileHeader("SAMPLE");
        header.setValueMap(new HashMap<String, String>() {{
            put("a", "b");
            put("descr", "\"this is a description\"");
            put("ID", "TEN");
        }});
        vcf.setHeaders(Arrays.asList(header));
        vcf.setColumnHeader(null);
        // note: no column headers so validation can't be performed
        assertTrue(tcgaVcfFileHeaderValidator.validateSampleColumnHeader(vcf, qcContext));
    }

    private void testFileDate(final boolean shouldValidate, final String value, final String... expectedErrors) {
        final VcfFileHeader fileDateHeader = new VcfFileHeader(TcgaVcfFileHeaderValidator.HEADER_TYPE_FILEDATE);
        fileDateHeader.setLineNumber(10);
        fileDateHeader.setValue(value);

        assertEquals(shouldValidate, tcgaVcfFileHeaderValidator.validate(fileDateHeader, qcContext));
        assertEquals(expectedErrors.length, qcContext.getErrorCount());
        for (int i=0; i<expectedErrors.length; i++) {
            assertEquals(expectedErrors[i], qcContext.getErrors().get(i));
        }
    }

    private VcfFileHeader makeHeader(final String name, final String id, final String number,
                                     final String type, final String description) {

        final VcfFileHeader header = new VcfFileHeader(name);
        final Map<String, String> valueMap = new HashMap<String, String>();
        valueMap.put("ID", id);
        valueMap.put("Number", number);
        valueMap.put("Type", type);
        valueMap.put("Description", description);
        header.setValueMap(valueMap);
        return header;
    }
}
