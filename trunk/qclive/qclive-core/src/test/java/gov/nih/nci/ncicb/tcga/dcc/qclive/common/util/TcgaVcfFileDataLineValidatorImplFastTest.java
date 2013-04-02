/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFileHeader;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.VcfParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.VcfParserImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.UnsupportedFileException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.VcfValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.VcfFileDataLineValidatorImpl.VcfColumns;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Test for TcgaVcfFileDataLineValidatorImpl
 * 
 * @author srinivasand Last updated by: $Author$
 * @version $Rev$
 */
public class TcgaVcfFileDataLineValidatorImplFastTest {

	private static final String SAMPLES_DIR = 
		Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
	private static final String TEST_VCF_DIRECTORY = SAMPLES_DIR + "qclive/vcf/";
	private static final String GOOD_TEST_FILE = "tcga/tcgaValidInfoValues.vcf";
	private static final String BAD_TEST_FILE = "tcga/tcgaInvalidInfoValues.vcf";	
	private static final String BAD_TEST_SVTYPE_FILE = "tcga/tcgaInvalidInfoSVTYPEValues.vcf";	
    private static final String TCGA_VALID_SVALT_FORM1 = "tcga/tcgavalidSVAltForm1.vcf";
    private static final String TCGA_VALID_SVALT_FORM2 = "tcga/tcgaValidSVAltValueForm2.vcf";
    private static final String TCGA_VALID_SVALT_FORM3 = "tcga/tcgavalidSVAltForm3.vcf";
    private static final String TCGA_VALID_SVALT_FORM4 = "tcga/tcgavalidSVAltForm4.vcf";
    private static final String TCGA_VALID_SVType_FILE = "tcga/tcgaValidSVTypeFile.vcf";
    private static final String TCGA_VALID_SVALT_LIST = "tcga/tcgavalidSVAltList.vcf";
    private static final String TCGA_INVALID_INFO = "tcga/tcgaInvalidInfo.vcf";
    private static final String TCGA_INVALID_SEQ = "tcga/tcgaInvalidSeq.vcf";
    private static final String TCGA_INVALID_CHROM = "tcga/tcgaInvalidChrom.vcf";
    private static final String TCGA_INVALID_POS = "tcga/tcgaInvalidPos.vcf";
    private static final String TCGA_INVALID_SVALT_LIST = "tcga/tcgaInvalidSVAltList.vcf";
    private static final String TCGA_INVALID_SVALT_VALUE = "tcga/tcgaInvalidSVAltValue.vcf";
    private static final String TCGA_VALID_GENE_DP_INFO_VALUE = "tcga/TCGA-18-3417_targeted.vcf";
    private static final String TCGA_INVALID_CHROM_ALLELE_VALUE = "tcga/tcgaInvalidChromAllele.vcf";
	private QcContext context;
	private TcgaVcfFileDataLineValidatorImpl validator;	
	private Set<String> ids = null;
	
	@Before
	public void setup() throws IOException,UnsupportedFileException {
		initQcContext();
		validator = new TcgaVcfFileDataLineValidatorImpl();
		ids = new HashSet<String>();

		VcfParser parser = new VcfParserImpl(new File(TEST_VCF_DIRECTORY, GOOD_TEST_FILE));
		parser.parse();
		ids = parser.getVcfIds();
		
	}

	private void initQcContext() {
		context = new QcContext();	
	}
	
	@Test
	public void testValidateDataLineGood() throws IOException {
		VcfParser vcfParser = new VcfParserImpl(new File(TEST_VCF_DIRECTORY, GOOD_TEST_FILE));
		vcfParser.parse();		
		VcfFile vcfFile = vcfParser.getVcfFile();		
		String[] dataLine;
		boolean isValid = true;
        final Set<String> previousVcfIds = new HashSet<String>();
		while ((dataLine = vcfParser.getNextDataLine()) != null) {
			isValid &= validator.validateDataLine(dataLine, vcfFile,
					vcfParser.getCurrentLineNumber(), context,ids, previousVcfIds);
		}
		assertTrue(isValid);
		assertEquals(0, context.getErrorCount());
	}

    @Test
    public void testValidateDataLineBad() throws IOException {
        // file has mistakes for each column type
        // All data lines in this file should have errors
    	VcfParser vcfParser = new VcfParserImpl(new File(TEST_VCF_DIRECTORY, BAD_TEST_FILE));
        vcfParser.parse();
        VcfFile vcfFile = vcfParser.getVcfFile();
        String[] dataLine;
        final Set<String> previousVcfIds = new HashSet<String>();
        while((dataLine = vcfParser.getNextDataLine()) != null) {
            assertFalse(validator.validateDataLine(dataLine, vcfFile, vcfParser.getCurrentLineNumber(), context, ids, previousVcfIds));
        }
        assertEquals(context.getErrors().toString(), 1, context.getErrorCount());
        assertEquals("VCF data validation error on line 12: INFO value 'VT' is not valid. is VT and should have one of SNP, INS or DEL but found 'abc'", context.getErrors().get(0));
    }

    @Test
    public void testValidateDPLineBad() throws IOException {
        // file has mistakes for each column type
        // All data lines in this file should have errors
    	VcfParser vcfParser = new VcfParserImpl(new File(TEST_VCF_DIRECTORY, "tcga/tcgaMissingDPValue.vcf"));
        vcfParser.parse();
        VcfFile vcfFile = vcfParser.getVcfFile();
        String[] dataLine;
        final Set<String> previousVcfIds = new HashSet<String>();
        while((dataLine = vcfParser.getNextDataLine()) != null) {
            validator.validateDataLine(dataLine, vcfFile, vcfParser.getCurrentLineNumber(), context, ids, previousVcfIds);
        }
        assertEquals(context.getErrors().toString(), 2, context.getErrorCount());
        assertEquals("VCF data validation error on line 39: SAMPLE #1 value '1/2:0:7:8' is not valid. The number of fields in the sample column (4) is different from the number of fields in the format column (7).", context.getErrors().get(0));
        assertEquals("VCF data validation error on line 39: SAMPLE #2 value 'X' is not valid. Should be an integer.", context.getErrors().get(1));
    }

	@Test
	public void testValidateInfoVtGood() {
		assertTrue(validator.validateInfoKey("VA", null, 1, context,ids));
		assertTrue(validator.validateInfoKey("VA", "abc", 1, context,ids));
		assertTrue(validator.validateInfoKey("VT", "SNP", 1, context,ids));
		assertTrue(validator.validateInfoKey("VT", "INS", 1, context,ids));
		assertTrue(validator.validateInfoKey("VT", "DEL", 1, context,ids));
		assertEquals(0, context.getErrorCount());
	}

	@Test
	public void testValidateInfoVtBad() {
		assertFalse(validator.validateInfoKey("VT", null, 1, context,ids));
		assertFalse(validator.validateInfoKey("VT", "foo", 1, context,ids));
		assertFalse(validator.validateInfoKey("VT", "abc", 1, context,ids));
		assertFalse(validator.validateInfoKey("VT", "SNP;INS", 1, context,ids));
	}

    @Test
    public void testInfoDataForSpecialValuesGeneInfoId() {
        checkInfoDataForSpecialValues("GENE", "ABC", true);
    }

    @Test
    public void testInfoDataForSpecialValuesSidInfoId() {
        checkInfoDataForSpecialValues("SID", "ABC", true);
    }

    @Test
    public void testInfoDataForSpecialValuesRgnInfoId() {
        checkInfoDataForSpecialValues("RGN", "exon", true);
    }

    @Test
    public void testInfoDataForSpecialValuesOtherInfoId() {
        checkInfoDataForSpecialValues("GENETICS", "ABC", false);
    }

    @Test
    public void testChromValueForAssemblyHeaderTrueFirst() throws Exception {
        assertTrue(validator.validateChromName("<abc>", 0, context));
        assertTrue(validator.isFoundChromDataRequiringAssemblyHeader());
        assertTrue(validator.validateChromName("12", 0, context));
        assertTrue(validator.isFoundChromDataRequiringAssemblyHeader());
        assertFalse(validator.validateChromName("<>", 0, context));
        assertTrue(validator.isFoundChromDataRequiringAssemblyHeader());
    }

    @Test
    public void testChromValueForAssemblyHeaderTrueMiddle() throws Exception {
        assertTrue(validator.validateChromName("12", 0, context));
        assertFalse(validator.isFoundChromDataRequiringAssemblyHeader());
        assertTrue(validator.validateChromName("<abc>", 0, context));
        assertTrue(validator.isFoundChromDataRequiringAssemblyHeader());
        assertFalse(validator.validateChromName("<>", 0, context));
        assertTrue(validator.isFoundChromDataRequiringAssemblyHeader());
    }

    @Test
    public void testChromValueForAssemblyHeaderFalse() throws Exception {
        assertTrue(validator.validateChromName("12", 0, context));
        assertFalse(validator.isFoundChromDataRequiringAssemblyHeader());
        assertFalse(validator.validateChromName("<>", 0, context));
        assertFalse(validator.isFoundChromDataRequiringAssemblyHeader());
    }
    
    
    

    /**
     * Create a valid data line with the given data info Id and run validateInfoValue().
     * Check that the value for isFoundInfoDataRequiringGeneAnnoInfoHeader is set as expected.
     *
     * @param infoId the info Id
     * @param expectedIsFoundInfoDataRequiringGeneAnnoInfoHeader the expected value for isFoundInfoDataRequiringGeneAnnoInfoHeader
     */
    private void checkInfoDataForSpecialValues(final String infoId, String value,
                                               boolean expectedIsFoundInfoDataRequiringGeneAnnoInfoHeader) {
        final List<VcfFileHeader> headers = makeVcfFileHeaderListWithInfoId(infoId);

        assertTrue(validator.validateInfoValue(infoId + "=" + value, headers, 0, 0, 1, context,ids));
        assertEquals(expectedIsFoundInfoDataRequiringGeneAnnoInfoHeader,
                validator.isFoundInfoDataRequiringGeneAnnoInfoHeader());
    }

    /**
     * Return a list of 1 INFO <code>VcfFileHeader</code> with the given Id
     *
     * @param infoId the INFO header Id
     * @return a list of 1 INFO <code>VcfFileHeader</code> with the given Id
     */
    private List<VcfFileHeader> makeVcfFileHeaderListWithInfoId(final String infoId) {
        final List<VcfFileHeader> result = new LinkedList<VcfFileHeader>();
        final VcfFileHeader infoHeader = new VcfFileHeader("INFO");
        final Map<String,String> valueMap = new HashMap<String, String>();
        valueMap.put("ID", infoId);
        valueMap.put("Number", "1");
        valueMap.put("Type", "String");
        infoHeader.setValueMap(valueMap);
        result.add(infoHeader);

        return result;
    }

    @Test
	public void testValidateFormatSSValuesInRangeGood() {

        final boolean isValid = validateSSSampleData("1");

		assertTrue(isValid);
		assertEquals(0, context.getErrorCount());
		assertEquals(0, context.getWarningCount());
	}

    @Test
	public void testValidateFormatSSValuesBlankGood() {

        final boolean isValid = validateSSSampleData(".");

		assertTrue(isValid);
		assertEquals(0, context.getErrorCount());
		assertEquals(0, context.getWarningCount());
	}

    @Test
	public void testValidateFormatSSValuesBadRange() {

		final String sampleData = "6";
        final boolean isValid = validateSSSampleData(sampleData);

		assertFalse(isValid);
		assertEquals(1, context.getErrorCount());
        assertEquals("VCF data validation error on line 1: SAMPLE #1 value '" + sampleData + "' is not valid. Should be one of [.,0,1,2,3,4,5]",
                context.getErrors().get(0));
	}

    @Test
	public void testValidateFormatSSValuesBadType() {

        final String sampleData = "germline";
        final boolean isValid = validateSSSampleData(sampleData);

		assertFalse(isValid);
		assertEquals(2, context.getErrorCount());
        assertEquals("VCF data validation error on line 1: SAMPLE #1 value '" + sampleData + "' is not valid. Should be one of [.,0,1,2,3,4,5]",
                context.getErrors().get(0));
        assertEquals("VCF data validation error on line 1: SAMPLE #1 value '" + sampleData + "' is not valid. Should be an integer.",
                context.getErrors().get(1));
	}
    
    @Test
	public void testValidateDataLineBadMATEID() throws IOException {
    	ids.remove("id2");
    	VcfParser vcfParser = new VcfParserImpl(new File(TEST_VCF_DIRECTORY, GOOD_TEST_FILE));
		vcfParser.parse();
		VcfFile vcfFile = vcfParser.getVcfFile();		
		String[] dataLine;
		boolean isValid = true;
        final Set<String> previousVcfIds = new HashSet<String>();
		while ((dataLine = vcfParser.getNextDataLine()) != null) {
			isValid &= validator.validateDataLine(dataLine, vcfFile,vcfParser.getCurrentLineNumber(), context,ids, previousVcfIds);
		}
		assertFalse(isValid);
		assertEquals(1, context.getErrorCount());
		assertEquals("VCF data validation error on line 18: INFO value 'MATEID' " +
				"is not valid. MATEID and PAIRID should refer to valid IDs",context.getErrors().get(0));
	}

    @Test
	public void testValidateDataLineBadPARID() throws IOException {
    	ids.remove("id3");
    	VcfParser vcfParser = new VcfParserImpl(new File(TEST_VCF_DIRECTORY, GOOD_TEST_FILE));
		vcfParser.parse();
		VcfFile vcfFile = vcfParser.getVcfFile();		
		String[] dataLine;
		boolean isValid = true;
        final Set<String> previousVcfIds = new HashSet<String>();
		while ((dataLine = vcfParser.getNextDataLine()) != null) {
			isValid &= validator.validateDataLine(dataLine, vcfFile,vcfParser.getCurrentLineNumber(), context,ids, previousVcfIds);
		}
		assertFalse(isValid);
		assertEquals(1, context.getErrorCount());
		assertEquals("VCF data validation error on line 19: INFO value 'PARID' is not valid. MATEID and PAIRID should refer to valid IDs",context.getErrors().get(0));
	}
    
    @Test
	public void testValidateDataLineInvalidPARID() throws IOException {
    	ids.remove("id7");
    	VcfParser vcfParser = new VcfParserImpl(new File(TEST_VCF_DIRECTORY, GOOD_TEST_FILE));
		vcfParser.parse();
		VcfFile vcfFile = vcfParser.getVcfFile();		
		String[] dataLine;
		boolean isValid = true;
        final Set<String> previousVcfIds = new HashSet<String>();
		while ((dataLine = vcfParser.getNextDataLine()) != null) {
			isValid &= validator.validateDataLine(dataLine, vcfFile,vcfParser.getCurrentLineNumber(), context,ids, previousVcfIds);
		}
		assertFalse(isValid);
		assertEquals(1, context.getErrorCount());
		assertEquals("VCF data validation error on line 22: INFO value 'PARID' is not valid. MATEID and PAIRID should refer to valid IDs",context.getErrors().get(0));
	}
    
    @Test
	public void testValidateDataLineInvalidSVTYPE() throws IOException {
    	VcfParser vcfParser = new VcfParserImpl(new File(TEST_VCF_DIRECTORY, BAD_TEST_SVTYPE_FILE));
		vcfParser.parse();
		VcfFile vcfFile = vcfParser.getVcfFile();		
		String[] dataLine;
		boolean isValid = true;
        final Set<String> previousVcfIds = new HashSet<String>();
		while ((dataLine = vcfParser.getNextDataLine()) != null) {
			isValid &= validator.validateDataLine(dataLine, vcfFile,vcfParser.getCurrentLineNumber(), context,ids, previousVcfIds);
		}
		assertFalse(isValid);
		assertEquals(7, context.getErrorCount());		
		assertEquals("VCF data validation error on line 18: INFO value 'SVTYPE' is not valid. SVTYPE should have one of BND,FND but found 'SOME'",context.getErrors().get(0));
		assertEquals("VCF data validation error on line 20: INFO value 'PARID' is not valid. SVTYPE must be present whenever PARID is found",context.getErrors().get(1));
		assertEquals("VCF data validation error on line 21: INFO value 'MATEID' is not valid. SVTYPE must be present whenever MATEID is found",context.getErrors().get(2));
		assertEquals("VCF data validation error on line 22: INFO value 'MATEID' is not valid. MATEID and PAIRID should refer to valid IDs",context.getErrors().get(3));
		assertEquals("VCF data validation error on line 22: INFO value 'MATEID' is not valid. SVTYPE must be present whenever MATEID is found",context.getErrors().get(4));
		assertEquals("VCF data validation error on line 22: INFO value 'PARID' is not valid. SVTYPE must be present whenever PARID is found",context.getErrors().get(5));
		assertEquals("VCF data validation error on line 23: INFO value 'MATEID' is not valid. MATEID and PAIRID should refer to valid IDs",context.getErrors().get(6));
		
		
	}       
    
    @Test
    public void testValidateTEMultipleValuesValid(){
    	final String[] dataLine = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "NS=3;RGN=exon,intron,intron;SID=val1,val2,val3;GENE=g1,g2,g3;AF=0.5;DB;H2;", "NS:TE:AF", "0/1:X,NA,NA:.", "foo:X,NA,NA:boo"};
    	assertTrue(validator.validateTeCount(dataLine, 1, context));
    	assertEquals(0, context.getErrorCount());
    }
    @Test
    public void testValidateTEsingleValueValid(){
    	final String[] dataLine = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "NS=3;RGN=exon;SID=val1;GENE=g1;AF=0.5;DB;H2;", "NS:TE:AF", "0/1:X:.", "foo:X:boo"};
    	assertTrue(validator.validateTeCount(dataLine, 1, context));
    	assertEquals(0, context.getErrorCount());
    }
    
    @Test
    public void testValidateTESingleValue(){    	
    	final String[] dataLine = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "NS=3;AF=0.5;DB;H2;", "NS:TE:AF", "0/1:X:.", "foo:X:boo"};
    	assertTrue(validator.validateTeCount(dataLine, 1, context));
    	assertEquals(0, context.getErrorCount());
    }
    @Test
    public void testValidatenoTE(){    	
    	final String[] dataLine = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "NS=3;RGN=exon;SID=val1;GENE=g1;AF=0.5;DB;H2;", "NS:AF", "0/1:.", "foo:boo"};
    	assertTrue(validator.validateTeCount(dataLine, 1, context));
    	assertEquals(0, context.getErrorCount());    
    }
    @Test
    public void testValidateTEnoelEments(){    	
    	final String[] dataLine = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "NS=3;AF=0.5;DB;H2;", "NS:AF", "0/1:.", "foo:foo"};
    	assertTrue(validator.validateTeCount(dataLine, 1, context));
    	assertEquals(0, context.getErrorCount());
    }
    @Test
    public void testValidateTERGNValueInValid(){
    	context.setFile(new File("filename"));
    	final String[] dataLine = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "NS=3;RGN=exon,intron;SID=val1;GENE=g1;AF=0.5;DB;H2;", "NS:TE:AF", "0/1:X:.", "foo:X:boo"};
    	assertFalse(validator.validateTeCount(dataLine, 1, context));
    	assertEquals(context.getErrors().get(0),"VCF data validation error on line 1. if any one of RGN,SID,GENE(in INFO) or TE(in FORMAT) are present, they should all have the same number of values. Instead found: RGN=exon,intron SID=val1 GENE=g1 TE=  X  X");
    	assertEquals(1, context.getErrorCount());
    }
    @Test
    public void testValidateTEtwoValuesInValid(){
    	context.setFile(new File("filename"));
    	final String[] dataLine = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "NS=3;RGN=exon,intron;SID=val1,val2,val3;GENE=g1;AF=0.5;DB;H2;", "NS:TE:AF", "0/1:X:.", "foo:X:boo"};
    	assertFalse(validator.validateTeCount(dataLine, 1, context));
    	assertEquals(context.getErrors().get(0),"VCF data validation error on line 1. if any one of RGN,SID,GENE(in INFO) or TE(in FORMAT) are present, they should all have the same number of values. Instead found: RGN=exon,intron SID=val1,val2,val3 GENE=g1 TE=  X  X");
    	assertEquals(1, context.getErrorCount());
    }   
    @Test
    public void testValidateTEInvalid(){
    	context.setFile(new File("filename"));
    	final String[] dataLine = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "NS=3;RGN=exon,exon,exon;SID=val1;GENE=g1,g2;AF=0.5;DB;H2;", "NS:AF", "0/1:.", "foo:boo"};
    	assertFalse(validator.validateTeCount(dataLine, 1, context));
    	assertEquals(context.getErrors().get(0),"VCF data validation error on line 1. if any one of RGN,SID,GENE(in INFO) or TE(in FORMAT) are present, they should all have the same number of values. Instead found: RGN=exon,exon,exon SID=val1 GENE=g1,g2");
    	assertEquals(1, context.getErrorCount());
    }
    @Test
    public void testValidateRGNkeySingleValueValid(){
    	final String[] dataLine = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "NS=3;RGN=exon;AF=0.5;DB;H2", "NS:TE:AF", "0/1:X:.", "foo:X:boo"};
    	assertTrue(validator.validateRGNkey(dataLine, 1, context));
    	assertEquals(0, context.getErrorCount());
    }
    @Test
    public void testValidateRGNkeyMultipleValueValid(){
    	final String[] dataLine = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "NS=3;RGN=exon,intron,intron;AF=0.5;DB;H2", "NS:TE:AF", "0/1:X,NA,NA:.", "foo:X,NA,NA:boo"};
    	assertTrue(validator.validateRGNkey(dataLine, 1, context));
    	assertEquals(0, context.getErrorCount());
    }
    @Test
    public void testValidateRGNkeySingleValueInValid(){
    	final String[] dataLine = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "NS=3;RGN=intron;AF=0.5;DB;H2", "NS:TE:AF", "0/1:X:.", "foo:X:boo"};
    	assertFalse(validator.validateRGNkey(dataLine, 1, context));
    	assertEquals(1, context.getErrorCount());
    	assertEquals(context.getErrors().get(0), "VCF data validation error on line 1: TE value 'X' is not valid.  if INFO value for RGN is not 'exon' then SAMPLE value for TE must be NA ");
    }
    @Test
    public void testValidateRGNkeyInValid(){
    	final String[] dataLine = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "NS=3;RGN=intron;AF=0.5;DB;H2", "NS:TE:AF", "0/1:.:.", "foo:.:boo"};
    	assertFalse(validator.validateRGNkey(dataLine, 1, context));
    	assertEquals(1, context.getErrorCount());
    	assertEquals(context.getErrors().get(0), "VCF data validation error on line 1: TE value '.' is not valid.  if INFO value for RGN is not 'exon' then SAMPLE value for TE must be NA ");
    }         
    
    @Test
    public void testValidateRGNkeyBadValue() {
    	final String[] dataLine = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "NS=3;RGN=exon,intron,intron;AF=0.5;DB;H2", "NS:TE:AF", "0/1:X,NA,NA:.", "foo:X,VAL,NA:boo"};
    	assertFalse(validator.validateRGNkey(dataLine, 1, context));
       	assertEquals(1, context.getErrorCount());
       	assertEquals(context.getErrors().get(0),"VCF data validation error on line 1: TE value 'X,VAL,NA' is not valid.  if INFO value for RGN is not 'exon' then SAMPLE value for TE must be NA ");
    }
    
    @Test
    public void testValidateRGNkeyAcceptanceTest() {
    	final String[] dataLine = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "NS=3;RGN=exon,intron,intron;AF=0.5;DB;H2", "NS:TE:AF", "0/1:MIS,SIL,NA:.", "foo:MIS,SIL,NA:boo"};
    	assertFalse(validator.validateRGNkey(dataLine, 1, context));
       	assertEquals(1, context.getErrorCount());
       	assertEquals(context.getErrors().get(0),"VCF data validation error on line 1: TE value 'MIS,SIL,NA' is not valid.  if INFO value for RGN is not 'exon' then SAMPLE value for TE must be NA ");
    }

    @Test
    public void testValidateDPKeyValid() {
        final String[] dataLine = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "NS=3;DP=25;AF=0.5;DB;H2", "NS:DP:AF", "0/1:12:.", "foo:13:boo"};
        assertTrue(validator.validateDPKey(dataLine, 1, context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidateDPValueValid() {

        final String[] dataLine = "X\t151619708\trs7391474\tT\tG\t5\tmf1\tDB;DP=1;MQ0=0;SS=Germline;VT=SNP GT:AD:DP:FA:MQ0\t0/1\t0/1:0,1:1:1.000:0".split("\t");
        assertTrue(validator.validateDPKey(dataLine, 1, context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidateDPKeyInsufficientColumns() {
        final String[] dataLine = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "EIGHT", "NINE"};
        assertTrue(validator.validateDPKey(dataLine, 1, context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidateDPKeyNoInfoDP() {
        final String[] dataLine = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "NS=3;AF=0.5;DB;H2", "NINE"};
        assertTrue(validator.validateDPKey(dataLine, 1, context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidateDPKeyNoFormatDP() {
        final String[] dataLine = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "NS=3;DP=4;AF=0.5;DB;H2", "GT:GQ:HQ"};
        assertTrue(validator.validateDPKey(dataLine, 1, context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidateDPKeyNoFormatValue() {
        final String[] dataLine = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "NS=3;DP=4;AF=0.5;DB;H2", ""};
        assertTrue(validator.validateDPKey(dataLine, 1, context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidateDPKeyInvalidDPValue() {
        final String[] dataLine = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "NS=3;DP=foo;AF=0.5;DB;H2", ""};
        assertFalse(validator.validateDPKey(dataLine, 1, context));
        assertEquals(1, context.getErrorCount());
        assertEquals("VCF data validation error on line 1: INFO value 'foo' is not valid. The value of DP must be an Integer", context.getErrors().get(0));
    }

    @Test
    public void testValidateDPKeyInvalidSampleValue() {
        final String[] dataLine = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "NS=3;DP=2;AF=0.5;DB;H2", "NS:DP:AF", "0/1:foo:."};
        assertFalse(validator.validateDPKey(dataLine, 1, context));
    }

    @Test
    public void testValidateDPKeyInvalidTotal() {
        final String[] dataLine = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "NS=3;DP=2;AF=0.5;DB;H2", "NS:DP:AF", "0/1:1:."};
        assertFalse(validator.validateDPKey(dataLine, 1, context));
        assertEquals(1, context.getErrorCount());
        assertEquals("VCF data validation error on line 1: INFO value ' DP of 2 and sample DP total of 1' is not valid. The value for DP in INFO column and the total of the values for DP in the sample columns must be equal if DP is specified in FORMAT column", context.getErrors().get(0));
    }

    @Test
    public void testValidateDPKeyBlankInInfo() {
        final String[] dataLine = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "NS=3;DP=.;AF=0.5;DB;H2", "NS:DP:AF", "0/1:.:.", "foo:.:boo"};
        assertTrue(validator.validateDPKey(dataLine, 1, context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidateDPKeyBlankSample() {
        final String[] dataLine = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "NS=3;DP=5;AF=0.5;DB;H2", "NS:DP:AF", "0/1:5:.", "foo:.:boo"};
        assertTrue(validator.validateDPKey(dataLine, 1, context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidateDPKeyBlankSampleBadSum() {
        // one sample has dot (missing value) so still passes
        final String[] dataLine = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "NS=3;DP=5;AF=0.5;DB;H2", "NS:DP:AF", "0/1:.:.", "foo:4:boo"};
        assertTrue(validator.validateDPKey(dataLine, 1, context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidateDPBlankInfoNotSample() {
        // DP in info is '.' but samples have values -- still passes
        final String[] dataLine = {"ONE", "TWO", "THREE", "FOUR", "FIVE", "SIX", "SEVEN", "NS=3;DP=.;AF=0.5;DB;H2", "NS:DP:AF", "0/1:1:.", "foo:2:boo"};
        assertTrue(validator.validateDPKey(dataLine, 1, context));
        assertEquals(0, context.getErrorCount());
    }
    
    @Test
    public void testValidateSvAltInfoValues() {
        String[] dataLine = {"chrom-value", "pos-value", "id", "ref-value", "G[17:198982[", "qual-value", "filter-value", "SVTYPE=BND;NS=3;DP=2;AF=0.5;DB;H2"};
        assertTrue(validator.validateSvAltInfoValues(dataLine, 1, context));
        // Reset the data line value for the INFO column to test for missing SVTYPE
        dataLine[VcfColumns.INFO_POS.colPos()] = "NS=3;DP=2;AF=0.5;DB;H2";
        assertFalse(validator.validateSvAltInfoValues(dataLine, 1, context));
        assertEquals(1, context.getErrorCount());
        assertEquals("VCF data validation error on line 1: INFO value 'NS=3;DP=2;AF=0.5;DB;H2' is not valid. Must specify 'SVTYPE' when using SV_ALT values for column 'ALT'", context.getErrors().get(0));
    }

    private String[] getDataLineWithChromAndFormatAndSampleData(final String chromData, final String formatData, final String sampleData) {
		return new String[] { chromData, "", "", "", "", "", "", "",
				formatData, sampleData };
	}

    private VcfFileHeader makeFormatHeader(final String id, final String number, final String type) {
		final VcfFileHeader header = new VcfFileHeader(
				VcfFile.HEADER_TYPE_FORMAT);
		final Map<String, String> valueMap = new HashMap<String, String>();
		valueMap.put("ID", id);
		valueMap.put("Number", number);
		valueMap.put("Type", type);
		header.setValueMap(valueMap);

		return header;
	}


    @Test
    public void testValidateAltValue() {
        assertTrue(validator.validateAltValue("ACGTN", null, 20, context));
    }

    @Test
    public void testValidateSVAltValueForm1() {
        assertTrue(validator.validateAltValue("G[17:198982[", null, 20, context));
    }

    @Test
    public void testValidateSVAltValueForm2() {
        assertTrue(validator.validateAltValue("GC]1:238909]", null, 20, context));
    }

    @Test
    public void testValidateSVAltValueForm3() {
        assertTrue(validator.validateAltValue("]<ctg1>:235788]GCNA", null, 20, context));
    }

    @Test
    public void testValidateSVAltValueForm4() {
        assertTrue(validator.validateAltValue("[1:2812734[ACT", null, 20, context));
    }

    @Test
    public void testValidateSVAltValueForm4ValidChromID() {
        assertTrue(validator.validateAltValue("[<ID>:2812734[ACT", null, 20, context));
    }

    @Test
    public void testValidateSVAltValueInvalidChrom() {
        assertFalse(validator.validateAltValue("[100:2812734[ACT", null, 20, context));
    }

    @Test
    public void testValidateSVAltValueInvalidPos() {
        assertFalse(validator.validateAltValue("G[17:-1[", null, 20, context));
    }

    @Test
    public void testValidateSVAltValueInvalidSeq() {
        assertFalse(validator.validateAltValue("1[17:1[", null, 20, context));
    }

    @Test
    public void testTrackAltValueSvAltChromIdPresent() {
        validator.processAltValueSvAltChromId("G[<foo>:198982[");
        assertTrue(validator.isFoundAltDataRequiringAssemblyHeader());
        validator.processAltValueSvAltChromId("GC]<chr>:238909]");
        assertTrue(validator.isFoundAltDataRequiringAssemblyHeader());
        validator.processAltValueSvAltChromId("]<ctg1>:235788]GCNA");
        assertTrue(validator.isFoundAltDataRequiringAssemblyHeader());
        validator.processAltValueSvAltChromId("[<cccc>:2812734[ACT");
        assertTrue(validator.isFoundAltDataRequiringAssemblyHeader());
    }


    @Test
    public void testTrackAltValueSvAltChromIdNotPresent() {
        validator.processAltValueSvAltChromId("G[1:198982[");
        assertFalse(validator.isFoundAltDataRequiringAssemblyHeader());
        validator.processAltValueSvAltChromId("GC]A:238909]");
        assertFalse(validator.isFoundAltDataRequiringAssemblyHeader());
        validator.processAltValueSvAltChromId("]MT:235788]GCNA");
        assertFalse(validator.isFoundAltDataRequiringAssemblyHeader());
        validator.processAltValueSvAltChromId("[X:2812734[ACT");
        assertFalse(validator.isFoundAltDataRequiringAssemblyHeader());
    }

    @Test
    public void testFormatIDGood() {
        assertTrue(validator.validateTEFormatID("SIL,MIS,NSNS,NSTP,FSH,NA", "TE", "S1", 1, context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testFormatIdBad() {
        assertFalse(validator.validateTEFormatID("foo,3,SILE,MI,SIL,C/A,SILMIS", "TE", "S1", 1, context));
        assertEquals(6, context.getErrorCount());
        assertEquals("VCF data validation error on line 1: S1 value 'foo' is not valid. Should be one of [SIL,MIS,NSNS,NSTP,FSH,NA]", context.getErrors().get(0));
        assertEquals("VCF data validation error on line 1: S1 value '3' is not valid. Should be one of [SIL,MIS,NSNS,NSTP,FSH,NA]", context.getErrors().get(1));
        assertEquals("VCF data validation error on line 1: S1 value 'SILE' is not valid. Should be one of [SIL,MIS,NSNS,NSTP,FSH,NA]", context.getErrors().get(2));
        assertEquals("VCF data validation error on line 1: S1 value 'MI' is not valid. Should be one of [SIL,MIS,NSNS,NSTP,FSH,NA]", context.getErrors().get(3));
        assertEquals("VCF data validation error on line 1: S1 value 'C/A' is not valid. Should be one of [SIL,MIS,NSNS,NSTP,FSH,NA]", context.getErrors().get(4));
        assertEquals("VCF data validation error on line 1: S1 value 'SILMIS' is not valid. Should be one of [SIL,MIS,NSNS,NSTP,FSH,NA]", context.getErrors().get(5));
    }

    @Test
    public void testInfoKeyRGNGood() {
        assertTrue(validator.validateInfoKey("RGN", "5_utr", 1, context, null));
        assertTrue(validator.validateInfoKey("RGN", "exon,exon,exon", 1, context, null));
        assertTrue(validator.validateInfoKey("RGN", "5_utr,3_utr,exon,intron,ncds,sp", 1, context, null));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testInfoKeyRGNBad() {
        assertFalse(validator.validateInfoKey("RGN", "_utr,3_utr", 1, context, null));
        assertFalse(validator.validateInfoKey("RGN", "exn,intr,foo", 1, context, null));
        assertFalse(validator.validateInfoKey("RGN", "xon,exo,exon", 1, context, null));
        assertFalse(validator.validateInfoKey("RGN", "5_utr,3_utr,exon,intn,ncds,sp", 1, context, null));
        assertEquals(4, context.getErrorCount());
        assertEquals("VCF data validation error on line 1: INFO value 'RGN' is not valid. is RGN and should be in 5_utr, 3_utr, exon, intron, ncds, sp but found '_utr,3_utr'", context.getErrors().get(0));
        assertEquals("VCF data validation error on line 1: INFO value 'RGN' is not valid. is RGN and should be in 5_utr, 3_utr, exon, intron, ncds, sp but found 'exn,intr,foo'", context.getErrors().get(1));
        assertEquals("VCF data validation error on line 1: INFO value 'RGN' is not valid. is RGN and should be in 5_utr, 3_utr, exon, intron, ncds, sp but found 'xon,exo,exon'", context.getErrors().get(2));
        assertEquals("VCF data validation error on line 1: INFO value 'RGN' is not valid. is RGN and should be in 5_utr, 3_utr, exon, intron, ncds, sp but found '5_utr,3_utr,exon,intn,ncds,sp'", context.getErrors().get(3));
    }

    @Test
    public void testInfoVLS() {
        assertTrue(validator.validateInfoKey("VLS", "0", 1, context, null));
        assertTrue(validator.validateInfoKey("VLS", "1", 1, context, null));
        assertTrue(validator.validateInfoKey("VLS", "2", 1, context, null));
        assertTrue(validator.validateInfoKey("VLS", "3", 1, context, null));
        assertTrue(validator.validateInfoKey("VLS", "4", 1, context, null));
        assertTrue(validator.validateInfoKey("VLS", "5", 1, context, null));

        assertFalse(validator.validateInfoKey("VLS", "6", 1, context, null));
        assertFalse(validator.validateInfoKey("VLS", "A", 2, context, null));
        assertFalse(validator.validateInfoKey("VLS", "", 3, context, null));
        assertEquals(3, context.getErrorCount());
        assertEquals("VCF data validation error on line 1: INFO value 'VLS' is not valid. VLS should be one of 0, 1, 2, 3, 4, 5 but found '6'", context.getErrors().get(0));
        assertEquals("VCF data validation error on line 2: INFO value 'VLS' is not valid. VLS should be one of 0, 1, 2, 3, 4, 5 but found 'A'", context.getErrors().get(1));
        assertEquals("VCF data validation error on line 3: INFO value 'VLS' is not valid. VLS should be one of 0, 1, 2, 3, 4, 5 but found ''", context.getErrors().get(2));
    }

    @Test
    public void testTcgaValidSVAltForm1() throws IOException {
        testValidTcgaVcfFile(TCGA_VALID_SVALT_FORM1);
    }

    @Test
    public void testTcgaValidSVAltForm2() throws IOException {
        testValidTcgaVcfFile(TCGA_VALID_SVALT_FORM2);
    }

    @Test
    public void testTcgaValidSVAltForm3() throws IOException {
        testValidTcgaVcfFile(TCGA_VALID_SVALT_FORM3);
    }

    @Test
    public void testTcgaValidSVAltForm4() throws IOException {
        testValidTcgaVcfFile(TCGA_VALID_SVALT_FORM4);
    }

    @Test
    public void testTcgaValidSVAltListForm4() throws IOException {
        testValidTcgaVcfFile(TCGA_VALID_SVALT_LIST);
    }


    @Test
    public void testTcgaValidSVType() throws IOException {
        testValidTcgaVcfFile(TCGA_VALID_SVType_FILE);
    }

    private void testValidTcgaVcfFile(final String fileName) throws IOException {        
        VcfParser vcfParser = new VcfParserImpl(new File(TEST_VCF_DIRECTORY, fileName));
        vcfParser.parse();
        VcfFile vcfFile = vcfParser.getVcfFile();
        final Set<String> previousVcfIds = new HashSet<String>();
        assertTrue( validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context, ids, previousVcfIds));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testTcgaInvalidInfo() throws IOException {
        testInvalidTcgaVcfFile(TCGA_INVALID_INFO, 5);
    }

    @Test
    public void testTcgaInvalidSeq() throws IOException {
        testInvalidTcgaVcfFile(TCGA_INVALID_SEQ, 1);
    }

    @Test
    public void testTcgaInvalidChrom() throws IOException {
        testInvalidTcgaVcfFile(TCGA_INVALID_CHROM, 1);
    }

    @Test
    public void testTcgaInvalidPos() throws IOException {
        testInvalidTcgaVcfFile(TCGA_INVALID_POS, 1);
    }

    @Test
    public void testTcgaInvalidSVAltList() throws IOException {
        testInvalidTcgaVcfFile(TCGA_INVALID_SVALT_LIST, 1);
    }

    @Test
    public void testTcgaInvalidSVAltValue() throws IOException {
        testInvalidTcgaVcfFile(TCGA_INVALID_SVALT_VALUE, 2);
    }

     @Test
    public void testEventInfoWithoutSvType() {
        assertFalse(validator.validateInfoLineRelationships("EVENT=R01", 10, context));
        assertTrue(validator.validateInfoLineRelationships("Event=R02", 11, context));
        assertTrue(validator.validateInfoLineRelationships("event=R03", 12, context));
        assertEquals(1, context.getErrorCount());
        assertEquals("VCF data validation error on line 10: INFO value 'EVENT' is not valid. SVTYPE must be present whenever EVENT is found", context.getErrors().get(0));
    }

    @Test
    public void testEventInfoWithoutSvTypeLowercase() {
        assertTrue(validator.validateInfoLineRelationships("Event=R01", 10, context));
        assertEquals(0, context.getErrorCount());
    }


    @Test 	// APPS-4404
    public void testGeneContainsDPcase() throws IOException{
    	String [] dataLine = {"1","879676","rs6605067","G","A","45","PASS","DB;Gene=PDPN/uc001avd.2;VT=SNP;VC=3'UTR;SS=Germline;DP=20","GT:DP:AD:BQ:MQ:SB:FA:SS","1/1:13:0,13:0,34.8:0,60.0:0,1.0:1.0:0","1/1:7:0,7:0,35.7:0,60.0:0,1.0:1.0:0"};    	
    	VcfParser vcfParser = new VcfParserImpl(new File(TEST_VCF_DIRECTORY, TCGA_VALID_GENE_DP_INFO_VALUE));
    	vcfParser.parse();
    	final VcfFile vcf = vcfParser.getVcfFile();    	    	
        final Set<String> previousVcfIds = new HashSet<String>();
    	assertTrue(validator.validateDataLine(dataLine, vcf, 7, context, new HashSet(), previousVcfIds));
    	assertTrue(context.getErrorCount() == 0);
    }

    private void testInvalidTcgaVcfFile(final String fileName, int errorCount) throws IOException {        
    	VcfParser vcfParser = new VcfParserImpl(new File(TEST_VCF_DIRECTORY, fileName));
    	vcfParser.parse();        
        VcfFile vcfFile = vcfParser.getVcfFile();
        // process all lines in the file...
        String[] dataLine;
        List<Boolean> results = new ArrayList<Boolean>();
        final Set<String> previousVcfIds = new HashSet<String>();
        while((dataLine = vcfParser.getNextDataLine()) != null) {
            results.add(validator.validateDataLine(dataLine, vcfFile, vcfParser.getCurrentLineNumber(), context, ids, previousVcfIds));
        }
        // make sure none of the lines passed (assume all lines of file are bad)
        assertFalse(results.contains(true));
        assertEquals(errorCount, context.getErrorCount());
    }

    /**
     * Validates the given SS sample data.
     *
     * @param ssSampleData
     * @return the result of the validation of the SS sample data
     */
    private boolean validateSSSampleData(final String ssSampleData) {

        final String gtFormatDataId = "GT";
        final String gtSampleData = "0/0";
        final String gtNumber = "1";

        final String ssFormatDataId = "SS";
        final String ssNumber = "1";

        final String dpFormatDataId = "DP";
        final String dpSampleData = "2";
        final String dpNumber = "1";

        final String bqFormatDataId = "BQ";
        final String bqSampleData = "3";
        final String bqNumber = ".";

        final String adFormatDataId = "AD";
        final String adSampleData = "4";
        final String adNumber = ".";

        final String formatData = new StringBuilder(gtFormatDataId).append(":").append(ssFormatDataId).append(":").append(dpFormatDataId)
                .append(":").append(bqFormatDataId).append(":").append(adFormatDataId).toString();
        final String sampleData = new StringBuilder(gtSampleData).append(":").append(ssSampleData).append(":").append(dpSampleData)
                .append(":").append(bqSampleData).append(":").append(adSampleData).toString();

        final String[] dataLine = getDataLineWithChromAndFormatAndSampleData(null, formatData, sampleData);

        final VcfFileHeader gtVcfFileHeader = makeFormatHeader(gtFormatDataId, gtNumber, "String");
        final VcfFileHeader ssVcfFileHeader = makeFormatHeader(ssFormatDataId, ssNumber, "Integer");
        final VcfFileHeader dpVcfFileHeader = makeFormatHeader(dpFormatDataId, dpNumber, "Integer");
        final VcfFileHeader bqVcfFileHeader = makeFormatHeader(bqFormatDataId, bqNumber, "Float");
        final VcfFileHeader adVcfFileHeader = makeFormatHeader(adFormatDataId, adNumber, "Float");

        final List<VcfFileHeader> formatHeaders = new ArrayList<VcfFileHeader>();
        formatHeaders.add(gtVcfFileHeader);
        formatHeaders.add(ssVcfFileHeader);
        formatHeaders.add(dpVcfFileHeader);
        formatHeaders.add(bqVcfFileHeader);
        formatHeaders.add(adVcfFileHeader);

        return validator.validateFormatAndSamples(dataLine, formatHeaders, 1, null, 1, context);
    }
}
