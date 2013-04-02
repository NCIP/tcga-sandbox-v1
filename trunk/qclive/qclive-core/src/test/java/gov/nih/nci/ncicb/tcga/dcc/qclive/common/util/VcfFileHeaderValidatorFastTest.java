package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.VcfFileHeaderValidator.DESCRIPTION_PATTERN;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.VcfFileHeaderValidator.FORMAT_TYPE_PATTERN;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.VcfFileHeaderValidator.ID_PATTERN;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.VcfFileHeaderValidator.NUMBER_PATTERN;
import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.VcfFileHeaderValidator.TYPE_PATTERN;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFileHeader;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * Test for vcfFileHeaderValidator
 *
 * @author chenjw
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class VcfFileHeaderValidatorFastTest {
    private VcfFileHeaderValidator vcfFileHeaderValidator;
    private VcfFileHeader vcfFileHeader;
    private Map<String, String> valueMap;
    private QcContext context;


    @Before
    public void setUp() {
        vcfFileHeaderValidator = new VcfFileHeaderValidator();
        valueMap = new HashMap<String, String>();
        context = new QcContext();
    }

    @Test
    public void testValidId() {
        assertTrue(vcfFileHeaderValidator.isValid("hello", ID_PATTERN));
        assertTrue(vcfFileHeaderValidator.isValid(".", ID_PATTERN));
        assertTrue(vcfFileHeaderValidator.isValid("123:abc", ID_PATTERN));
    }

    @Test
    public void testInvalidId() {
        assertFalse(vcfFileHeaderValidator.isValid("abc,def", ID_PATTERN));
        assertFalse(vcfFileHeaderValidator.isValid("hi there", ID_PATTERN));
        assertFalse(vcfFileHeaderValidator.isValid("", ID_PATTERN));
    }

    @Test
    public void testValidNumber() {
        assertTrue(vcfFileHeaderValidator.isValid("0", NUMBER_PATTERN));
        assertTrue(vcfFileHeaderValidator.isValid("999", NUMBER_PATTERN));
        assertTrue(vcfFileHeaderValidator.isValid("A", NUMBER_PATTERN));
        assertTrue(vcfFileHeaderValidator.isValid("G", NUMBER_PATTERN));
        assertTrue(vcfFileHeaderValidator.isValid(".", NUMBER_PATTERN));
    }

    @Test
    public void testInvalidNumber() {
        assertFalse(vcfFileHeaderValidator.isValid("-12", NUMBER_PATTERN));
        assertFalse(vcfFileHeaderValidator.isValid("5.4", NUMBER_PATTERN));
        assertFalse(vcfFileHeaderValidator.isValid("AG", NUMBER_PATTERN));
        assertFalse(vcfFileHeaderValidator.isValid("AA", NUMBER_PATTERN));
        assertFalse(vcfFileHeaderValidator.isValid(" 5 ", NUMBER_PATTERN));
        assertFalse(vcfFileHeaderValidator.isValid("5 ", NUMBER_PATTERN));
        assertFalse(vcfFileHeaderValidator.isValid(" 5", NUMBER_PATTERN));
    }

    @Test
    public void testValidType() {
        assertTrue(vcfFileHeaderValidator.isValid("Integer", TYPE_PATTERN));
        assertTrue(vcfFileHeaderValidator.isValid("String", TYPE_PATTERN));
        assertTrue(vcfFileHeaderValidator.isValid("Float", TYPE_PATTERN));
        assertTrue(vcfFileHeaderValidator.isValid("Flag", TYPE_PATTERN));
        assertTrue(vcfFileHeaderValidator.isValid("Character", TYPE_PATTERN));
    }

    @Test
    public void testInvalidType() {
        assertFalse(vcfFileHeaderValidator.isValid("llama", TYPE_PATTERN));
        assertFalse(vcfFileHeaderValidator.isValid("float", TYPE_PATTERN));
        assertFalse(vcfFileHeaderValidator.isValid("", TYPE_PATTERN));
        assertFalse(vcfFileHeaderValidator.isValid(".", TYPE_PATTERN));
        assertFalse(vcfFileHeaderValidator.isValid("3", TYPE_PATTERN));

        assertFalse(vcfFileHeaderValidator.isValid("Flag", FORMAT_TYPE_PATTERN));
    }

    @Test
    public void testValidDescription() {
        assertTrue(vcfFileHeaderValidator.isValid("\"a\"", DESCRIPTION_PATTERN));
        assertTrue(vcfFileHeaderValidator.isValid("\"hi\"", DESCRIPTION_PATTERN));
        assertTrue(vcfFileHeaderValidator.isValid("\"This is a description\"", DESCRIPTION_PATTERN));
        assertTrue(vcfFileHeaderValidator.isValid("\"123 is the description! 1+2=3\"", DESCRIPTION_PATTERN));
        assertTrue(vcfFileHeaderValidator.isValid("\"1, 2, 3, climb a tree.\"", DESCRIPTION_PATTERN));
    }

    @Test
    public void testInvalidDescription() {
        assertFalse(vcfFileHeaderValidator.isValid("no quotes", DESCRIPTION_PATTERN));
        assertFalse(vcfFileHeaderValidator.isValid("\" leading space\"", DESCRIPTION_PATTERN));
        assertFalse(vcfFileHeaderValidator.isValid("\"trailing space \"", DESCRIPTION_PATTERN));
        assertFalse(vcfFileHeaderValidator.isValid("\"extra quote \"in\" description\"", DESCRIPTION_PATTERN));
        assertFalse(vcfFileHeaderValidator.isValid("\"\"", DESCRIPTION_PATTERN));
    }

    @Test
    public void testValidHeaderTypeINFO() {
        setUpHeader(VcfFile.HEADER_TYPE_INFO, "test", "2", "String", "\"This is a test\"");
        assertTrue(vcfFileHeaderValidator.validate(vcfFileHeader, context));
    }

    @Test
    public void testInvalidHeaderTypeINFO() {
        setUpHeader(VcfFile.HEADER_TYPE_INFO, "bad id", "cow", "moose", "squirrel");

        assertFalse(vcfFileHeaderValidator.validate(vcfFileHeader, context));
        assertEquals(4, context.getErrorCount());
        assertEquals("INFO header on line 3 has invalid ID value 'bad id'", context.getErrors().get(0));
        assertEquals("INFO header on line 3 has invalid Number value 'cow'", context.getErrors().get(1));
        assertEquals("INFO header on line 3 has invalid Type value 'moose'", context.getErrors().get(2));
        assertEquals("INFO header on line 3 has invalid Description value 'squirrel'", context.getErrors().get(3));
    }

    @Test
    public void testMissingKeyForINFO() {
        // all required keys are missing
        setUpHeader(VcfFile.HEADER_TYPE_INFO, null, null, null, null);

        assertFalse(vcfFileHeaderValidator.validate(vcfFileHeader, context));
        assertEquals(4, context.getErrorCount());
        assertEquals("INFO header on line 3 is missing required key ID", context.getErrors().get(0));
        assertEquals("INFO header on line 3 is missing required key Number", context.getErrors().get(1));
        assertEquals("INFO header on line 3 is missing required key Type", context.getErrors().get(2));
        assertEquals("INFO header on line 3 is missing required key Description", context.getErrors().get(3));
    }

    @Test
    public void testValidHeaderTypeFORMAT() {
        setUpHeader(VcfFile.HEADER_TYPE_FORMAT, "anId", "A", "Integer", "\"this is a test FORMAT header\"");

        assertTrue(vcfFileHeaderValidator.validate(vcfFileHeader, context));
        assertEquals(0, context.getErrorCount());

    }

    @Test
    public void testInvalidHeaderTypeFORMAT() {
        setUpHeader(VcfFile.HEADER_TYPE_FORMAT, "i d", "9.6", "Flag", "\"hi \" bye\"");
        assertFalse(vcfFileHeaderValidator.validate(vcfFileHeader, context));
        assertEquals(4, context.getErrorCount());
        assertEquals("FORMAT header on line 3 has invalid ID value 'i d'", context.getErrors().get(0));
        assertEquals("FORMAT header on line 3 has invalid Number value '9.6'", context.getErrors().get(1));
        assertEquals("FORMAT header on line 3 has invalid Type value 'Flag'", context.getErrors().get(2));
        assertEquals("FORMAT header on line 3 has invalid Description value '\"hi \" bye\"'", context.getErrors().get(3));
    }

    @Test
    public void testMissingKeysForFORMAT() {
        setUpHeader(VcfFile.HEADER_TYPE_FORMAT, null, null, null, null);
        assertFalse(vcfFileHeaderValidator.validate(vcfFileHeader, context));
        assertEquals(4, context.getErrorCount());
        assertEquals("FORMAT header on line 3 is missing required key ID", context.getErrors().get(0));
        assertEquals("FORMAT header on line 3 is missing required key Number", context.getErrors().get(1));
        assertEquals("FORMAT header on line 3 is missing required key Type", context.getErrors().get(2));
        assertEquals("FORMAT header on line 3 is missing required key Description", context.getErrors().get(3));
    }

    @Test
    public void testValidHeaderTypeFILTER() {
        setUpHeader(VcfFile.HEADER_TYPE_FILTER, "filter42", null, null, "\"this is a test FILTER header\"");
        assertTrue(vcfFileHeaderValidator.validate(vcfFileHeader, context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testInvalidHeaderTypeFILTER() {
        setUpHeader(VcfFile.HEADER_TYPE_FILTER, " no ", "9.8.7", "something weird", "no quotes!");
        assertFalse(vcfFileHeaderValidator.validate(vcfFileHeader, context));
        assertEquals(2, context.getErrorCount());
        assertEquals("FILTER header on line 3 has invalid ID value ' no '", context.getErrors().get(0));
        assertEquals("FILTER header on line 3 has invalid Description value 'no quotes!'", context.getErrors().get(1));
    }

    @Test
    public void testMissingKeysForFILTER() {
        setUpHeader(VcfFile.HEADER_TYPE_FILTER, null, null, null, null);
        assertFalse(vcfFileHeaderValidator.validate(vcfFileHeader, context));
        assertEquals(2, context.getErrorCount());
        assertEquals("FILTER header on line 3 is missing required key ID", context.getErrors().get(0));
        assertEquals("FILTER header on line 3 is missing required key Description", context.getErrors().get(1));
    }

    @Test
    public void testGoodSimpleCustomHeader() {
        final VcfFileHeader header = new VcfFileHeader("custom");
        header.setValue("hello");
        assertTrue(vcfFileHeaderValidator.validate(header, context));
    }

    @Test
    public void testGoodComplexCustomHeader() {
        final VcfFileHeader header = new VcfFileHeader("custom");
        header.setValueMap(new HashMap<String, String>() {{
            put("a", "b");
            put("descr", "\"this is a description\"");
            put("ID", ".");
        }});
        assertTrue(vcfFileHeaderValidator.validate(header, context));
    }

    @Test
    public void testSimpleBadHeaderValue() {
        final VcfFileHeader header = new VcfFileHeader("something");
        header.setLineNumber(10);
        header.setValue("this has spaces so is bad");
        assertFalse(vcfFileHeaderValidator.validate(header, context));
        assertEquals(1, context.getErrorCount());
        assertEquals("something header on line 10 value 'this has spaces so is bad' is invalid: may not contain spaces, equals signs, commas, or semi-colons unless surrounded by double quotes",
                context.getErrors().get(0));
    }

    @Test
    public void testSimpleBadHeaderKey() {
        final VcfFileHeader header = new VcfFileHeader("bad key");
        header.setLineNumber(5);
        header.setValue("hi");
        assertFalse(vcfFileHeaderValidator.validate(header, context));
        assertEquals(1, context.getErrorCount());
        assertEquals("bad key header on line 5 is invalid: key may not contain spaces, commas, semi-colons, or quotes", context.getErrors().get(0));
    }

    @Test
    public void testComplexBadHeaderKey() {
        setUpHeader("custom", "123", null, null, null);
        vcfFileHeader.getValueMap().put("oh no", "bad");
        vcfFileHeader.getValueMap().put("oop's", "notgood");
        vcfFileHeader.getValueMap().put("this\"is\"wrong", "yup");
        assertFalse(vcfFileHeaderValidator.validate(vcfFileHeader, context));
        assertEquals(3, context.getErrorCount());
    }

    @Test
    public void testComplexBadHeaderValue() {
        setUpHeader("SOMETHING", null, null, null, " noooo ");
        vcfFileHeader.getValueMap().put("abc", "hi,hi;hi");
        assertFalse(vcfFileHeaderValidator.validate(vcfFileHeader, context));
        assertEquals(2, context.getErrorCount());
    }



    private void setUpHeader(final String headerType, final String id, final String number, final String type, final String description) {

        vcfFileHeader = new VcfFileHeader(headerType);
        vcfFileHeader.setValueMap(valueMap);
        vcfFileHeader.setLineNumber(3);

        if (id != null) {
            valueMap.put("ID", id);
        }
        if (number != null) {
            valueMap.put("Number", number);
        }
        if (type != null) {
            valueMap.put("Type", type);
        }
        if (description != null) {
            valueMap.put("Description", description);
        }
    }

}
