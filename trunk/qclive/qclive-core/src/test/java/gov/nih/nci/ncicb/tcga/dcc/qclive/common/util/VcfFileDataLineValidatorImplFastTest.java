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
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Test for VcfFileDataLineValidatorImpl
 * 
 * @author srinivasand Last updated by: $Author$
 * @version $Rev$
 */
public class VcfFileDataLineValidatorImplFastTest {

	private static final String SAMPLES_DIR = Thread.currentThread()
			.getContextClassLoader().getResource("samples").getPath()
			+ File.separator;
	private static final String TEST_VCF_DIRECTORY = SAMPLES_DIR
			+ "qclive/vcf/";
	private static final String GOOD_TEST_FILE = "valid.vcf";
	private static final String BAD_TEST_FILE = "badDataLine.vcf";
	private static final String BAD_CHROM_NAME_TEST_FILE = "badChromName.vcf";
	private static final String BAD_REFPOS_DATA_TEST_FILE = "badReferencePosData.vcf";
	private static final String BAD_ID_DATA_TEST_FILE = "badIdData.vcf";
	private static final String BAD_ALT_DATA_TEST_FILE = "badAltData.vcf";
	private static final String BAD_FILTER_DATA_TEST_FILE = "badFilterData.vcf";
	private static final String BAD_REF_ALLELE_SEQ_TEST_FILE = "badRefAlleleSeq.vcf";
	private static final String BAD_QUAL_TEST_FILE = "badQual.vcf";
    private static final String GOOD_TEST_NUM_ALLELES_IN_SAMPLE_FILE = "validNumAllelesInSample.vcf";
    private static final String BAD_TEST_NUM_ALLELES_IN_SAMPLE_FILE = "invalidNumAllelesInSample.vcf";

	private QcContext context;
	private VcfFileDataLineValidatorImpl validator;	

	@Before
	public void setup() throws IOException {
		initQcContext();
		validator = new VcfFileDataLineValidatorImpl();
	}

	@Test
	public void testValidateDataLineGood() throws IOException {
		initQcContext();
		VcfParser vcfParser = new VcfParserImpl(new File(TEST_VCF_DIRECTORY, GOOD_TEST_FILE));
		vcfParser.parse();
		VcfFile vcfFile = vcfParser.getVcfFile();
		String[] dataLine;
        final Set<String> previousVcfIds = new HashSet<String>();
		while ((dataLine = vcfParser.getNextDataLine()) != null) {

            final boolean isValid = validator.validateDataLine(dataLine, vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds);
			assertTrue(isValid);
		}
		assertEquals(0, context.getErrorCount());
	}

    @Test
    public void testValidateNumAllelesInSampleGood() throws IOException {
        initQcContext();
        VcfParser vcfParser = new VcfParserImpl(new File(TEST_VCF_DIRECTORY, GOOD_TEST_NUM_ALLELES_IN_SAMPLE_FILE));
        vcfParser.parse();
        VcfFile vcfFile = vcfParser.getVcfFile();
        String[] dataLine;
        final Set<String> previousVcfIds = new HashSet<String>();
        while ((dataLine = vcfParser.getNextDataLine()) != null) {
            assertTrue(validator.validateDataLine(dataLine, vcfFile,
                    vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
        }
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidateNumAllelesInSampleBad() throws IOException {
        initQcContext();
        VcfParser vcfParser = new VcfParserImpl(new File(TEST_VCF_DIRECTORY, BAD_TEST_NUM_ALLELES_IN_SAMPLE_FILE));
        vcfParser.parse();
        VcfFile vcfFile = vcfParser.getVcfFile();
        String[] dataLine;
        final Set<String> previousVcfIds = new HashSet<String>();
        while ((dataLine = vcfParser.getNextDataLine()) != null) {
            assertFalse(validator.validateDataLine(dataLine, vcfFile,
                    vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
        }
        assertEquals(4, context.getErrorCount());
        assertEquals("VCF data validation error on line 23: SAMPLE #2 value '0/1/2/3/3' is not valid. Genotype field cannot contain more than 4 alleles, but found 5.", context.getErrors().get(0));
        assertEquals("VCF data validation error on line 24: SAMPLE #2 value '0/1/2/3/1/0' is not valid. Genotype field cannot contain more than 4 alleles, but found 6.", context.getErrors().get(1));
        assertEquals("VCF data validation error on line 25: SAMPLE #2 value '0/0/1/2/3/4' is not valid. Genotype field cannot contain more than 5 alleles, but found 6.", context.getErrors().get(2));
        assertEquals("VCF data validation error on line 26: SAMPLE #2 value '0/0/1/2/3/4' is not valid. Genotype field cannot contain more than 5 alleles, but found 6.", context.getErrors().get(3));
    }

	@Test
	public void testValidateDataLineShort() {
		initQcContext();
		final String[] dataLine = new String[] { "1", "100", "A" };
        final Set<String> previousVcfIds = new HashSet<String>();
		assertFalse(validator.validateDataLine(dataLine, new VcfFile(), 10,
				context,new HashSet<String>(), previousVcfIds));
	}

	@Test
	public void testValidateDataLineBad() throws IOException {
		// file has mistakes for each column type
		// All data lines in this file should have errors
		initQcContext();
		VcfParser vcfParser = new VcfParserImpl(new File(TEST_VCF_DIRECTORY, BAD_TEST_FILE));
		vcfParser.parse();
		VcfFile vcfFile = vcfParser.getVcfFile();
		String[] dataLine;
        final Set<String> previousVcfIds = new HashSet<String>();
		while ((dataLine = vcfParser.getNextDataLine()) != null) {
			assertFalse(validator.validateDataLine(dataLine, vcfFile,
					vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		}
		assertEquals(context.getErrors().toString(), 10,
				context.getErrorCount());
		assertEquals(
				"VCF data validation error on line 14: CHROM value 'm' is not valid.",
				context.getErrors().get(0));
		assertEquals(
				"VCF data validation error on line 14: INFO value 'VT' is not valid. Incorrect number of values.  Expected 1 but found 2",
				context.getErrors().get(1));
		assertEquals(
				"VCF data validation error on line 15: POS value 'foo' is not valid. The value of the reference position must be an integer",
				context.getErrors().get(2));
		assertEquals(
				"VCF data validation error on line 15: INFO value 'VAS' is not valid. Incorrect number of values.  Expected 1 but found 2",
				context.getErrors().get(3));
		assertEquals(
				"VCF data validation error on line 16: ID value 'rs 123456' is not valid.",
				context.getErrors().get(4));
		assertEquals(
				"VCF data validation error on line 16: ALT value 'badAlt' is not valid.",
				context.getErrors().get(5));
		assertEquals(
				"VCF data validation error on line 16: INFO value 'a' is not valid. Should be an integer",
				context.getErrors().get(6));
		assertEquals(
				"VCF data validation error on line 17: ID value 'rs 123456' is not valid.",
				context.getErrors().get(7));
		assertEquals(
				"VCF data validation error on line 17: FILTER value 'CT' is not valid. The value of the Filter must be PASS or correspond to an ID in the FILTER headers",
				context.getErrors().get(8));
		assertEquals(
				"VCF data validation error on line 17: INFO value 'VT' is not valid. Incorrect number of values.  Expected 1 but found 2",
				context.getErrors().get(9));
	}

	@Test
	public void testInvalidReferencePosData() throws IOException {
		// file has mistakes in the POS column values (second column)
		// first data line has a non-mandatory value for refpos (.)
		// second data line has a string
		// third data line has a decimal
		initQcContext();
		
		
		VcfParser vcfParser = new VcfParserImpl(new File(TEST_VCF_DIRECTORY, BAD_REFPOS_DATA_TEST_FILE));
		vcfParser.parse();
		VcfFile vcfFile = vcfParser.getVcfFile();
        final Set<String> previousVcfIds = new HashSet<String>();
		assertFalse(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertFalse(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertFalse(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertFalse(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertFalse(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertEquals(5, context.getErrorCount());
		assertEquals(
				"VCF data validation error on line 14: POS value '.' is not valid. The value of the reference position must be an integer",
				context.getErrors().get(0));
		assertEquals(
				"VCF data validation error on line 15: POS value 'foo' is not valid. The value of the reference position must be an integer",
				context.getErrors().get(1));
		assertEquals(
				"VCF data validation error on line 16: POS value '123456.0' is not valid. The value of the reference position must be an integer",
				context.getErrors().get(2));
		assertEquals(
				"VCF data validation error on line 17: POS value '0' is not valid. The value of the reference position must be > 0",
				context.getErrors().get(3));
		assertEquals(
				"VCF data validation error on line 18: POS value '-1' is not valid. The value of the reference position must be > 0",
				context.getErrors().get(4));
	}

	@Test
	public void testInvalidIdData() throws IOException {
		//
		initQcContext();
		VcfParser vcfParser = new VcfParserImpl(new File(TEST_VCF_DIRECTORY, BAD_ID_DATA_TEST_FILE));
		vcfParser.parse();
		VcfFile vcfFile = vcfParser.getVcfFile();
        final Set<String> previousVcfIds = new HashSet<String>();
		assertFalse(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertTrue(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertFalse(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertFalse(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertEquals(3, context.getErrorCount());
		assertEquals(
				"VCF data validation error on line 14: ID value 'rs 123456' is not valid.",
				context.getErrors().get(0));		
		assertEquals(
				"VCF data validation error on line 16: ID value 'rs123456' is not valid. ID must be unique in the file",
				context.getErrors().get(1));
		assertEquals(
				"VCF data validation error on line 17: ID value '' is not valid.",
				context.getErrors().get(2));
	}

	@Test
	public void testInvalidAltData() throws IOException {
		initQcContext();
		VcfParser vcfParser = new VcfParserImpl(new File(TEST_VCF_DIRECTORY, BAD_ALT_DATA_TEST_FILE));
		vcfParser.parse();
		VcfFile vcfFile = vcfParser.getVcfFile();
		String[] dataLine;
        final Set<String> previousVcfIds = new HashSet<String>();
		while ((dataLine = vcfParser.getNextDataLine()) != null) {
			assertFalse(validator.validateDataLine(dataLine, vcfFile,
					vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		}

		assertEquals(3, context.getErrorCount());
	}

	@Test
	public void testInvalidFilterData() throws IOException {
		initQcContext();
		VcfParser vcfParser = new VcfParserImpl(new File(TEST_VCF_DIRECTORY, BAD_FILTER_DATA_TEST_FILE));
		vcfParser.parse();
		VcfFile vcfFile = vcfParser.getVcfFile();
        final Set<String> previousVcfIds = new HashSet<String>();
		assertFalse(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertFalse(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertFalse(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertFalse(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertFalse(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertEquals(5, context.getErrorCount());
		assertEquals(
				"VCF data validation error on line 15: FILTER value 'CT' is not valid. The value of the Filter must be PASS or correspond to an ID in the FILTER headers",
				context.getErrors().get(0));
		assertEquals(
				"VCF data validation error on line 16: FILTER value '0' is not valid. The value of the Filter cannot be a reserved string",
				context.getErrors().get(1));
		assertEquals(
				"VCF data validation error on line 17: FILTER value 'GY' is not valid. The value of the Filter must be PASS or correspond to an ID in the FILTER headers",
				context.getErrors().get(2));
		assertEquals(
				"VCF data validation error on line 18: FILTER value 'CA,CH' is not valid. The value of the Filter must be PASS or correspond to an ID in the FILTER headers",
				context.getErrors().get(3));
		assertEquals(
				"VCF data validation error on line 19: FILTER value '' is not valid. The value of the Filter must be PASS or correspond to an ID in the FILTER headers",
				context.getErrors().get(4));
	}

	@Test
	public void testProcessFileInvalidChromName() throws IOException {
		initQcContext();
		VcfParser vcfParser = new VcfParserImpl(new File(TEST_VCF_DIRECTORY, BAD_CHROM_NAME_TEST_FILE));
		vcfParser.parse();
		VcfFile vcfFile = vcfParser.getVcfFile();
        final Set<String> previousVcfIds = new HashSet<String>();
		assertFalse(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertFalse(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertFalse(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertTrue(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertTrue(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertEquals(3, context.getErrorCount());
		assertEquals(
				"VCF data validation error on line 14: CHROM value 'chrY' is not valid.",
				context.getErrors().get(0));
		assertEquals(
				"VCF data validation error on line 15: CHROM value 'm' is not valid.",
				context.getErrors().get(1));
		assertEquals(
				"VCF data validation error on line 16: CHROM value '111' is not valid.",
				context.getErrors().get(2));
	}

	@Test
	public void testValidateChromNameValid() {

		assertTrue(validator.validateChromName("X", 1, context));
		assertTrue(validator.validateChromName("Y", 1, context));
		assertTrue(validator.validateChromName("MT", 1, context));
		assertTrue(validator.validateChromName("1", 1, context));
		assertTrue(validator.validateChromName("22", 1, context));
	}

	@Test
	public void testValidateChromNameInvalid() {

		assertFalse(validator.validateChromName("anX", 1, context));
		assertFalse(validator.validateChromName("chrY", 1, context));
		assertFalse(validator.validateChromName("mt", 1, context));
		assertFalse(validator.validateChromName("111", 1, context));
		assertFalse(validator.validateChromName("022", 1, context));
	}

	@Test
	public void testProcessFileInvalidQual() throws IOException {
		initQcContext();
		VcfParser vcfParser = new VcfParserImpl(new File(TEST_VCF_DIRECTORY, BAD_QUAL_TEST_FILE));
		vcfParser.parse();
		VcfFile vcfFile = vcfParser.getVcfFile();
        final Set<String> previousVcfIds = new HashSet<String>();
		assertFalse(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertFalse(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertFalse(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertTrue(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertTrue(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertEquals(3, context.getErrorCount());
		assertEquals(
				"VCF data validation error on line 14: QUAL value 'xy123' is not valid. Qual must be '.' or integer >= 0",
				context.getErrors().get(0));
		assertEquals(
				"VCF data validation error on line 15: QUAL value '1.2' is not valid. Qual must be '.' or integer >= 0",
				context.getErrors().get(1));
		assertEquals(
				"VCF data validation error on line 16: QUAL value '-2.1' is not valid. Qual must be '.' or integer >= 0",
				context.getErrors().get(2));
	}

	@Test
	public void testValidateQualValid() {

		assertTrue(validator.validateQual(".", 1, context));
		assertTrue(validator.validateQual("1", 1, context));
		assertTrue(validator.validateQual("23", 1, context));
	}

	@Test
	public void testValidateQualInvalid() {

		assertFalse(validator.validateQual("", 1, context));
		assertFalse(validator.validateQual("xy123", 1, context));
		assertFalse(validator.validateQual("1.2", 1, context));
		assertFalse(validator.validateQual("-2.1", 1, context));
	}

	@Test
	public void testValidateIdValid() {
        final Set<String> previousVcfIds = new HashSet<String>();
		assertTrue(validator.validateId("rs6054257", previousVcfIds, 1, context));
		assertTrue(validator.validateId("rs6054258;rs6040355", previousVcfIds, 1, context));
		assertTrue(validator.validateId("rs6054259", previousVcfIds, 1, context));
		assertTrue(validator.validateId(".", previousVcfIds, 1, context));
		assertTrue(validator.validateId(".", previousVcfIds, 1, context));
	}

	@Test
	public void testValidateIdInvalid() {
        final Set<String> previousVcfIds = new HashSet<String>();
		assertFalse(validator.validateId("rs 6054257", previousVcfIds, 1, context));
		assertFalse(validator.validateId("", previousVcfIds, 1, context));
		assertFalse(validator.validateId("rs6054259;", previousVcfIds, 1, context));
	}

	@Test
	public void testValidateIdDuplicate() {
		validator = null;
		validator = new VcfFileDataLineValidatorImpl();
        final Set<String> previousVcfIds = new HashSet<String>();
		assertTrue(validator.validateId("rs6054257", previousVcfIds, 1, context));
		assertFalse(validator.validateId("rs6054257", previousVcfIds, 1, context));
	}

	@Test
	public void testValidateFilterValid() {
		final VcfFileHeader header1 = new VcfFileHeader("FILTER");
		header1.setValueMap(new HashMap<String, String>() {
			{
				put("ID", "q10");
				put("Description", "Quality below 10");
			}
		});
		final VcfFileHeader header2 = new VcfFileHeader("FILTER");
		header2.setValueMap(new HashMap<String, String>() {
			{
				put("ID", "q11");
				put("Description", "Quality below 11");
			}
		});
		List<VcfFileHeader> filterHeaders = new ArrayList<VcfFileHeader>() {
			{
				add(header1);
				add(header2);
			}
		};
		assertTrue(validator.validateFilter("PASS", filterHeaders, 1, context));
		assertTrue(validator.validateFilter("q10", filterHeaders, 1, context));
		assertTrue(validator.validateFilter("q10;q11", filterHeaders, 1,
				context));
		assertTrue(validator.validateFilter(".", filterHeaders, 1, context));
	}

	@Test
	public void testValidateFilterInvalid() {
		final VcfFileHeader header1 = new VcfFileHeader("FILTER");
		header1.setValueMap(new HashMap<String, String>() {
			{
				put("ID", "q10");
				put("Description", "Quality below 10");
			}
		});
		final VcfFileHeader header2 = new VcfFileHeader("FILTER");
		header2.setValueMap(new HashMap<String, String>() {
			{
				put("ID", "0");
				put("Description",
						"Fitler code provides 0 but 0 is a reserved string");
			}
		});
		List<VcfFileHeader> filterHeaders = new ArrayList<VcfFileHeader>() {
			{
				add(header1);
				add(header2);
			}
		};
		assertFalse(validator.validateFilter("0", filterHeaders, 1, context));
		assertFalse(validator.validateFilter("PSS", filterHeaders, 1, context));
		assertFalse(validator.validateFilter("q1", filterHeaders, 1, context));
		assertFalse(validator.validateFilter("CA", filterHeaders, 1, context));
		assertFalse(validator.validateFilter("q10;", filterHeaders, 1, context));
	}

	@Test
	public void testValidateAltValueDot() {
		assertTrue(validator.validateAltValue(".", null, 20, context));
	}

	@Test
	public void testValidateAltValueOneAllele() {
		assertTrue(validator.validateAltValue("ACGTN", null, 1, context));
		assertTrue(validator.validateAltValue("A", null, 1, context));
		assertTrue(validator.validateAltValue("CC", null, 1, context));
		assertTrue(validator.validateAltValue("GA", null, 1, context));
	}

	@Test
	public void testValidateAltOneAlleleBad() {
		assertFalse(validator.validateAltValue("hello", null, 1, context));
		assertEquals(
				"VCF data validation error on line 1: ALT value 'hello' is not valid.",
				context.getErrors().get(0));
	}

	@Test
	public void testValidateAltValueOneID() {
		assertTrue(validator.validateAltValue("<mango>",
				Arrays.asList(makeAltHeader("mango")), 1, context));
	}

	@Test
	public void testValidateAltValueIDNotInHeader() {
		assertFalse(validator.validateAltValue("<strawberry>",
				Arrays.asList(makeAltHeader("mango")), 1, context));
		assertEquals(
				"VCF data validation error on line 1: ALT value 'strawberry' is not valid. ID not defined in ALT headers",
				context.getErrors().get(0));
	}

	@Test
	public void testAltWithIdComma() {
		assertFalse(validator.validateAltValue("<has,comma>",
				Arrays.asList(makeAltHeader("has,comma")), 1, context));
		assertEquals(
				"VCF data validation error on line 1: ALT value '<has' is not valid.",
				context.getErrors().get(0));
	}

	@Test
	public void testAltWithBracketId() {
		assertFalse(validator.validateAltValue("<<squirrel>>",
				Arrays.asList(makeAltHeader("<squirrel>")), 10, context));
		assertEquals(
				"VCF data validation error on line 10: ALT value '<<squirrel>>' is not valid.",
				context.getErrors().get(0));
	}

	@Test
	public void testValidateAltValueListOfAlleles() {
		assertTrue(validator.validateAltValue("A,C,G,T,N,ACGTN",
				Arrays.asList(makeAltHeader("hi")), 1, context));
	}

	@Test
	public void testValidateAltValueListofIDs() {
		List<VcfFileHeader> altHeaders = Arrays.asList(makeAltHeader("id1"),
				makeAltHeader("id2"), makeAltHeader("id3"),
				makeAltHeader("id42"));
		assertTrue(validator.validateAltValue("<id42>,<id3>,GCGC", altHeaders,
				10, context));
	}

	@Test
	public void testValidateAltValuesSemicolon() {
		assertFalse(validator.validateAltValue("A;C;N;AC", null, 10, context));
	}

	@Test
	public void testValidateAltValueListOfIDInvalid() {
		List<VcfFileHeader> altHeaders = Arrays.asList(makeAltHeader("apple"),
				makeAltHeader("orange"), makeAltHeader("strawberry"));
		assertFalse(validator.validateAltValue(
				"<apple>,<mango>,<kiwi>,<strawberry>", altHeaders, 3, context));
		assertEquals(2, context.getErrorCount());
		assertEquals(
				"VCF data validation error on line 3: ALT value 'mango' is not valid. ID not defined in ALT headers",
				context.getErrors().get(0));
		assertEquals(
				"VCF data validation error on line 3: ALT value 'kiwi' is not valid. ID not defined in ALT headers",
				context.getErrors().get(1));
	}

	@Test
	public void testValidAltValueBlank() {
		assertFalse(validator.validateAltValue("", null, 1, context));
		assertEquals(
				"VCF data validation error on line 1: ALT value '' is not valid.",
				context.getErrors().get(0));
	}

	@Test
	public void testValidateFormatAndSamplesNoFormatData() {

		final String[] dataLine = new String[] { "", "", "", "", "", "", "", "" };
		final boolean isValid = validator.validateFormatAndSamples(dataLine,
				null, null, null, 1, context);

		assertTrue(isValid);
		assertEquals(0, context.getErrorCount());
		assertEquals(0, context.getWarningCount());
	}

	@Test
	public void testValidateFormatAndSamplesFormatDataNull() {

		final String[] dataLine = getDataLineWithFormatData(null);
		final boolean isValid = validator.validateFormatAndSamples(dataLine,
				null, null, null, 1, context);

		assertFalse(isValid);
		assertEquals(1, context.getErrorCount());
		assertEquals(0, context.getWarningCount());
		assertEquals(
				"VCF data validation error on line 1: FORMAT value 'null' is not valid. Format data can not be null.",
				context.getErrors().get(0));
	}

	@Test
	public void testValidateFormatAndSamplesFormatDataEmpty() {

		final String[] dataLine = getDataLineWithFormatData("");
		final boolean isValid = validator.validateFormatAndSamples(dataLine,
				null, null, null, 1, context);

		assertFalse(isValid);
		assertEquals(1, context.getErrorCount());
		assertEquals(0, context.getWarningCount());
		assertEquals(
				"VCF data validation error on line 1: FORMAT value '' is not valid. Format data can not be empty.",
				context.getErrors().get(0));
	}

	@Test
	public void testValidateFormatAndSamplesFormatDataBlank() {

        final String formatDataField = ".";
        final boolean isValid = validateFormatAndSamples(formatDataField, null, true, null, null, null, false);

		assertFalse(isValid);
		assertEquals(1, context.getErrorCount());
		assertEquals(0, context.getWarningCount());
		assertEquals(
                "VCF data validation error on line 1: FORMAT value 'GT:DP:BQ:SS:AD:" + formatDataField + "' is not valid. The format data field is blank ('.').",
                context.getErrors().get(0));
	}

    @Test
	public void testValidateFormatAndSamplesFormatDataUndefined() {

        final String formatDataField = "squirrel";
        final boolean isValid = validateFormatAndSamples(formatDataField, null, true, null, null, null, false);

		assertFalse(isValid);
		assertEquals(1, context.getErrorCount());
		assertEquals(0, context.getWarningCount());
		assertEquals(
				new StringBuilder(
						"VCF data validation error on line 1: FORMAT value 'GT:DP:BQ:SS:AD:")
						.append(formatDataField)
						.append("' is not valid. Format data contains a field that has not been defined in the headers: '")
						.append(formatDataField).append("'.").toString(), context
						.getErrors().get(0));
	}

	@Test
	public void testValidateFormatAndSamplesFormatDataDefined() {

		final String formatDataId = "chipmunk";
        final List<VcfFileHeader> formatHeaders = getFileHeadersWithIds(new String[] { formatDataId });
        final boolean isValid = validateFormatAndSamples(formatDataId, formatHeaders, true, null, null, null, false);

		assertTrue(isValid);
		assertEquals(0, context.getErrorCount());
		assertEquals(0, context.getWarningCount());
	}

	@Test
	public void testValidateFormatAndSamplesFormatDataDefinedMoreThanOnce() {

		final String formatDataId = "chipmunk";
        final List<VcfFileHeader> formatHeaders = getFileHeadersWithIds(new String[]{formatDataId, formatDataId});
        final boolean isValid = validateFormatAndSamples(formatDataId, formatHeaders, true, null, null, null, false);

		assertTrue(isValid);
		assertEquals(0, context.getErrorCount());
		assertEquals(1, context.getWarningCount());
		assertEquals(
				"VCF data validation warning on line 1: FORMAT value '" + formatDataId + "'. "
						+ "Multiple FORMAT headers were found for the following ID: " + formatDataId + ". The first one will be chosen.",
				context.getWarnings().get(0));
	}

	@Test
	public void testValidateFormatAndSamplesFormatDataOneDefinedOneUndefinedOneBlank() {

		final String formatDataId1 = "squirrel";
		final String formatDataId2 = "chipmunk";
		final String formatData = formatDataId1 + ":" + formatDataId2 + ":.";
		final List<VcfFileHeader> formatHeaders = getFileHeadersWithIds(new String[] { formatDataId1 });
        final boolean isValid = validateFormatAndSamples(formatData, formatHeaders, true, null, null, null, false);

		assertFalse(isValid);
		assertEquals(2, context.getErrorCount());
		assertEquals(0, context.getWarningCount());

		assertEquals(
				new StringBuilder(
						"VCF data validation error on line 1: FORMAT value 'GT:DP:BQ:SS:AD:")
						.append(formatData)
						.append("' is not valid. Format data contains a field that has not been defined in the headers: '")
						.append(formatDataId2).append("'.").toString(), context
						.getErrors().get(0));

		assertEquals(
				new StringBuilder(
						"VCF data validation error on line 1: FORMAT value 'GT:DP:BQ:SS:AD:")
						.append(formatData)
						.append("' is not valid. The format data field is blank ('.').")
						.toString(), context.getErrors().get(1));
	}

	@Test
	public void testValidateFormatAndSamplesGTDefinedAndFirst() {

		final String formatDataId = "chipmunk";
		final List<VcfFileHeader> formatHeaders = getFileHeadersWithIds(new String[] {formatDataId });
        final boolean isValid = validateFormatAndSamples(formatDataId, formatHeaders, true, null, null, null, false);

		assertTrue(isValid);
		assertEquals(0, context.getErrorCount());
		assertEquals(0, context.getWarningCount());
	}

	@Test
	public void testValidateFormatAndSamplesGTDefinedAndNotFirst() {

		final String formatDataId = "chipmunk";
		final List<VcfFileHeader> formatHeaders = getFileHeadersWithIds(new String[] {formatDataId });

        final boolean isValid = validateFormatAndSamples(formatDataId, formatHeaders, false, null, null, null, false);

		assertFalse(isValid);
		assertEquals(1, context.getErrorCount());
		assertEquals(0, context.getWarningCount());

		assertEquals(
				new StringBuilder(
						"VCF data validation error on line 1: FORMAT value 'DP:BQ:SS:AD:")
						.append(formatDataId)
						.append(":GT' is not valid. If 'GT' is defined in the format headers, it must be the first field in the format data.")
						.toString(), context.getErrors().get(0));
	}

	@Test
	public void testValidateFormatAndSamplesWrongSampleSubFiledCount() {

		final String formatDataId1 = "A";
		final String formatDataId2 = "B";
		final String formatData = formatDataId2 + ":" + formatDataId1;
		final String sampleData = "1:2:3";
		final List<VcfFileHeader> formatHeaders = getFileHeadersWithIds(new String[] {
				formatDataId1, formatDataId2 });
        final boolean isValid = validateFormatAndSamples(formatData, formatHeaders, true, sampleData, null, null, false);

		assertFalse(isValid);
		assertEquals(1, context.getErrorCount());
		assertEquals(0, context.getWarningCount());

		assertEquals(
				new StringBuilder(
						"VCF data validation error on line 1: SAMPLE #1 value '.:.:.:.:.:")
						.append(sampleData)
						.append("' is not valid. The number of fields in the sample column (8) is different from the number of fields in the format column (7).")
						.toString(), context.getErrors().get(0));
	}

	@Test
	public void testValidateFormatAndSamplesBlanksInSamples() {

		final String formatDataId1 = "A";
		final String formatDataId2 = "B";
		final String formatData = formatDataId2 + ":" + formatDataId1;
		final String sampleData = ".:."; // both values are blank
		final List<VcfFileHeader> formatHeaders = getFileHeadersWithIds(new String[] {
				formatDataId1, formatDataId2 });
        final boolean isValid = validateFormatAndSamples(formatData, formatHeaders, true, sampleData, null, null, false);

		assertTrue(isValid);
		assertEquals(0, context.getErrorCount());
		assertEquals(0, context.getWarningCount());
	}

	@Test
	public void testValidateFormatAndSamplesUnexpectedNumberOfValuesForG() {

		final String formatDataId = "SQUIRREL";
		final String sampleData = "1,2,3"; // 3 values
		final String[] dataLine = getDataLineWithChromAndFormatAndSampleData(
				null, formatDataId, sampleData);
		final VcfFileHeader vcfFileHeader = makeFormatHeader(formatDataId, "G",
				"Integer");
		final List<VcfFileHeader> formatHeaders = new ArrayList<VcfFileHeader>();
		formatHeaders.add(vcfFileHeader);
		final Integer numGenotypes = 2;
        final boolean isValid = validateFormatAndSamples(formatDataId, formatHeaders, true, sampleData, numGenotypes, null, false);

		assertFalse(isValid);
		assertEquals(1, context.getErrorCount());
		assertEquals(0, context.getWarningCount());
		assertEquals(
				new StringBuilder(
						"VCF data validation error on line 1: SAMPLE #1 value '")
						.append(sampleData)
						.append("' is not valid. Incorrect number of values. Expected ")
						.append(numGenotypes).append(" but found 3.")
						.toString(), context.getErrors().get(0));
	}

	@Test
	public void testValidateFormatAndSamplesUnexpectedNumberOfValuesForNumber() {

		final String formatDataId = "SQUIRREL";
		final String sampleData = "1,2,3"; // 3 values
		final String number = "2";
		final VcfFileHeader vcfFileHeader = makeFormatHeader(formatDataId,
				number, "Integer");
		final List<VcfFileHeader> formatHeaders = new ArrayList<VcfFileHeader>();
		formatHeaders.add(vcfFileHeader);
        final boolean isValid = validateFormatAndSamples(formatDataId, formatHeaders, true, sampleData, null, null, false);

		assertFalse(isValid);
		assertEquals(1, context.getErrorCount());
		assertEquals(0, context.getWarningCount());
		assertEquals(
				new StringBuilder(
						"VCF data validation error on line 1: SAMPLE #1 value '")
						.append(sampleData)
						.append("' is not valid. Incorrect number of values. Expected ")
						.append(number).append(" but found 3.").toString(),
				context.getErrors().get(0));
	}

	@Test
	public void testValidateFormatAndSamplesUnexpectedNumberOfValuesWhenUndefined() {

		final String formatDataId = "SQUIRREL";
		final String sampleData = "1,2,3"; // 3 values
		final String number = ".";
		final String[] dataLine = getDataLineWithChromAndFormatAndSampleData(
				null, formatDataId, sampleData);
		final VcfFileHeader vcfFileHeader = makeFormatHeader(formatDataId,
				number, "String");
		final List<VcfFileHeader> formatHeaders = new ArrayList<VcfFileHeader>();
		formatHeaders.add(vcfFileHeader);
        final boolean isValid = validateFormatAndSamples(formatDataId, formatHeaders, true, sampleData, null, null, false);

		assertTrue(isValid);
		assertEquals(0, context.getErrorCount());
		assertEquals(0, context.getWarningCount());
	}

	@Test
	public void testValidateFormatAndSamplesUnexpectedNumberOfValuesForA() {

		final String formatDataId = "SQUIRREL";
		final String sampleData = "1,2,3"; // 3 values
		final String[] dataLine = getDataLineWithChromAndFormatAndSampleData(
				null, formatDataId, sampleData);
		final VcfFileHeader vcfFileHeader = makeFormatHeader(formatDataId, "A",
				"String");
		final List<VcfFileHeader> formatHeaders = new ArrayList<VcfFileHeader>();
		formatHeaders.add(vcfFileHeader);
		final Integer numAltAlleles = 2;
        final boolean isValid = validateFormatAndSamples(formatDataId, formatHeaders, true, sampleData, null, numAltAlleles, false);

		assertFalse(isValid);
		assertEquals(1, context.getErrorCount());
		assertEquals(0, context.getWarningCount());
		assertEquals(
				new StringBuilder(
						"VCF data validation error on line 1: SAMPLE #1 value '")
						.append(sampleData)
						.append("' is not valid. Incorrect number of values. Expected ")
						.append(numAltAlleles).append(" but found 3.")
						.toString(), context.getErrors().get(0));
	}

	@Test
	public void testValidateFormatAndSamplesNotAnInteger() {

		final String formatDataId = "SQUIRREL";
		final String sampleData = "nuts";
		final String dataType = "Integer";
		final String expectedErrorMessage = "VCF data validation error on line 1: SAMPLE #1 value 'nuts' is not valid. Should be an integer.";

		checkInvalidDataType(null, sampleData, formatDataId, dataType, null,
				expectedErrorMessage, false);
	}

	@Test
	public void testValidateFormatAndSamplesNotAFloat() {

		final String formatDataId = "SQUIRREL";
		final String sampleData = "nuts";
		final String dataType = "Float";
		final String expectedErrorMessage = "VCF data validation error on line 1: SAMPLE #1 value 'nuts' is not valid. Should be a floating point number.";

		checkInvalidDataType(null, sampleData, formatDataId, dataType, null,
				expectedErrorMessage, false);
	}

	@Test
	public void testValidateFormatAndSamplesNotACharacter() {

		final String formatDataId = "SQUIRREL";
		final String sampleData = "nuts";
		final String dataType = "Character";
		final String expectedErrorMessage = "VCF data validation error on line 1: SAMPLE #1 value 'nuts' is not valid. "
				+ "Defined as a Character but found value 'nuts'.";

		checkInvalidDataType(null, sampleData, formatDataId, dataType, null,
				expectedErrorMessage, false);
	}

	@Test
	public void testValidateFormatAndSamplesNotAString() {

		final String formatDataId = "SQUIRREL";
		final String sampleData = "lots of nuts;";
		final String dataType = "String";
		final String expectedErrorMessage = "VCF data validation error on line 1: SAMPLE #1 value 'lots of nuts;' is not valid. "
				+ "String cannot contain whitespace, semi-colon, or quote, but found 'lots of nuts;'.";

		checkInvalidDataType(null, sampleData, formatDataId, dataType, null,
				expectedErrorMessage, false);
	}

	@Test
	public void testValidateFormatAndSamplesGTWrongAlleleNumber() {

		final String sampleData = "0/1/2/3";
		final String dataType = "String";
		final String expectedErrorMessage = "VCF data validation error on line 1: SAMPLE #1 value '0/1/2/3' is not valid. "
				+ "Genotype field cannot contain more than 3 alleles, but found 4.";

		checkInvalidDataType("", sampleData, null, dataType, 2,
				expectedErrorMessage, true);
	}

	@Test
	public void testValidateFormatAndSamplesGTWithHaploidMTAlleleValueOutsideOfRange() {

		final String chromData = "MT"; // Haploid
		final String sampleData = "1|5";
		final Integer numberOfAltAlleles = 3;
		final String dataType = "String";
		final String expectedErrorMessage = "VCF data validation error on line 1: SAMPLE #1 value '1|5' is not valid. "
				+ "Allele value falls outside of expected range (expected 0 <= value <= 3). Found '5'.";

		checkInvalidDataType(chromData, sampleData, null, dataType,
				numberOfAltAlleles, expectedErrorMessage, true);
	}

	@Test
	public void testValidateFormatAndSamplesGTNonHaploidAlleleValueOutsideOfRange() {

		final String chromData = "1"; // Non Haploid
		final String formatDataId = "GT";
		final String sampleData = ".|8";
		final Integer numberOfAltAlleles = 7;
		final String dataType = "String";
		final String expectedErrorMessage = "VCF data validation error on line 1: SAMPLE #1 value '.|8' is not valid. "
				+ "Allele value falls outside of expected range (expected 0 <= value <= 7). Found '8'.";

		checkInvalidDataType(chromData, sampleData, null, dataType,
				numberOfAltAlleles, expectedErrorMessage, true);
	}

	/**
	 * Check that the given sample data does not have the proper type
	 *
     * @param chromData
     *            the chrom data
     * @param sampleData
     *            the sample data
     * @param formatDataId
*            the format data id
     * @param dataType
*            the data type
     * @param numberOfAltAlleles
*            number of alternate alleles
     * @param expectedErrorMessage
     * @param sampleDataIsForGTField
     */
	private void checkInvalidDataType(final String chromData,
                                      final String sampleData,
                                      final String formatDataId,
                                      final String dataType,
                                      final Integer numberOfAltAlleles,
                                      final String expectedErrorMessage,
                                      final boolean sampleDataIsForGTField) {

		final String[] dataLine = getDataLineWithChromAndFormatAndSampleData(
				chromData, formatDataId, sampleData);
		final VcfFileHeader vcfFileHeader = makeFormatHeader(formatDataId, "1",
				dataType);
		final List<VcfFileHeader> formatHeaders = new ArrayList<VcfFileHeader>();
		formatHeaders.add(vcfFileHeader);
        final boolean isValid = validateFormatAndSamples(formatDataId, formatHeaders, true, sampleData, null, numberOfAltAlleles, sampleDataIsForGTField);

		assertFalse(isValid);
		assertEquals(1, context.getErrorCount());
		assertEquals(0, context.getWarningCount());
		assertEquals(expectedErrorMessage, context.getErrors().get(0));
	}

	/**
	 * Return a list of format headers, each having one of the ID given in
	 * formatDataIds
	 * 
	 * @param formatDataIds
	 *            the IDs value to give to each individual format header to be
	 *            returned in the list
	 * @return a list of format headers, each having one of the ID given in
	 *         formatDataIds
	 */
	private List<VcfFileHeader> getFileHeadersWithIds(
			final String[] formatDataIds) {

		final List<VcfFileHeader> formatHeaders = new ArrayList<VcfFileHeader>();

		for (final String formatDataId : formatDataIds) {

			final Map<String, String> valueMap = new HashMap<String, String>();
			valueMap.put("ID", formatDataId);

			final VcfFileHeader vcfFileHeader = new VcfFileHeader(
					VcfFile.HEADER_TYPE_FORMAT);
			vcfFileHeader.setValueMap(valueMap);

			formatHeaders.add(vcfFileHeader);
		}

		return formatHeaders;
	}

	/**
	 * Return a data line with the given format data
	 * 
	 * @param formatData
	 *            the format data
	 * @return a data line with the given format data
	 */
	private String[] getDataLineWithFormatData(final String formatData) {
		return new String[] { "", "", "", "", "", "", "", "", formatData };
	}

	/**
	 * Return a data line with the given format data with one sample data
	 * 
	 * 
	 * @param chromData
	 * @param formatData
	 *            the format data
	 * @param sampleData
	 *            the first sample data
	 * @return a data line with the given format data with one sample data
	 */
	private String[] getDataLineWithChromAndFormatAndSampleData(
			final String chromData, final String formatData,
			final String sampleData) {
		return new String[] { chromData, "", "", "", "", "", "", "",
				formatData, sampleData };
	}

	@Test
	public void testProcessFileInvalidReferenceAlleleSeq() throws IOException {
		initQcContext();
		VcfParser vcfParser = new VcfParserImpl(new File(TEST_VCF_DIRECTORY, BAD_REF_ALLELE_SEQ_TEST_FILE));
		vcfParser.parse();
		VcfFile vcfFile = vcfParser.getVcfFile();
        final Set<String> previousVcfIds = new HashSet<String>();
		assertFalse(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertFalse(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertFalse(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertTrue(context.getErrors().toString(), validator.validateDataLine(
				vcfParser.getNextDataLine(), vcfFile,
				vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertTrue(validator.validateDataLine(vcfParser.getNextDataLine(),
				vcfFile, vcfParser.getCurrentLineNumber(), context,new HashSet<String>(), previousVcfIds));
		assertEquals(3, context.getErrorCount());
		assertEquals(
				"VCF data validation error on line 14: REF value 'Y' is not valid.",
				context.getErrors().get(0));
		assertEquals(
				"VCF data validation error on line 15: REF value '.' is not valid.",
				context.getErrors().get(1));
		assertEquals(
				"VCF data validation error on line 16: REF value 'mt' is not valid.",
				context.getErrors().get(2));
	}

	@Test
	public void testvalidateReferenceAlleleSeqValid() {

		assertTrue(validator.validateReferenceAlleleSeq("G", 1, context));
		assertTrue(validator.validateReferenceAlleleSeq("GT", 1, context));
		assertTrue(validator.validateReferenceAlleleSeq("ACGTN", 1, context));
	}

	@Test
	public void testvalidateReferenceAlleleSeqInvalid() {

		assertFalse(validator.validateReferenceAlleleSeq("Y", 1, context));
		assertFalse(validator.validateReferenceAlleleSeq(".", 1, context));
		assertFalse(validator.validateReferenceAlleleSeq("mt", 1, context));
		assertFalse(validator.validateReferenceAlleleSeq("", 1, context));
	}

	@Test
	public void testValidateRefPosValid() {
		assertTrue(validator.validateReferencePosition("123456", 1, context));
		assertTrue(validator.validateReferencePosition("12345678900", 1,
				context));
	}

	@Test
	public void testValidateRefPosInvalid() {
		assertFalse(validator.validateReferencePosition(".", 1, context));
		assertFalse(validator.validateReferencePosition("", 1, context));
		assertFalse(validator.validateReferencePosition("0", 1, context));
		assertFalse(validator.validateReferencePosition("-12345", 1, context));
		assertFalse(validator.validateReferencePosition("12345.01", 1, context));
	}

	@Test
	public void testCalculateNumberOfGenotypes() {
		assertEquals(new Integer(1), validator.calculateNumberOfGenotypes(0, "X"));
		assertEquals(new Integer(3), validator.calculateNumberOfGenotypes(1, "1"));
		assertEquals(new Integer(6), validator.calculateNumberOfGenotypes(2, null));
		assertEquals(new Integer(10), validator.calculateNumberOfGenotypes(3, ""));
        assertEquals(new Integer(15), validator.calculateNumberOfGenotypes(4, "hello"));

		// for Y chromosome, calculation is different
        assertEquals(new Integer(5), validator.calculateNumberOfGenotypes(4, "Y"));
        assertEquals(new Integer(2), validator.calculateNumberOfGenotypes(1, "Y"));
        assertEquals(new Integer(11), validator.calculateNumberOfGenotypes(10, "Y"));
	}

	@Test
	public void testValidateInfoDot() {
		assertTrue(validator.validateInfoValue(".", null, 0, 0, 1, context,new HashSet<String>()));
	}

	@Test
	public void testValidateInfoMissingType() {
		List<VcfFileHeader> infoHeaders = Arrays.asList(makeInfoHeader("NS",
				"1", null));

		assertFalse(validator.validateInfoValue("NS=12", infoHeaders, 1, 1, 15,
				context,new HashSet<String>()));
	}

	@Test
	public void testValidateInfoMissingNumber() {
		List<VcfFileHeader> infoHeaders = Arrays.asList(makeInfoHeader("NS",
				null, "Integer"));

		assertFalse(validator.validateInfoValue("NS=12", infoHeaders, 1, 1, 15,
				context,new HashSet<String>()));
	}

	@Test
	public void testValidateInfoInteger() {
		List<VcfFileHeader> infoHeaders = Arrays.asList(makeInfoHeader("NS",
				"1", "Integer"));
		assertTrue(validator.validateInfoValue("NS=5", infoHeaders, 0, 0, 10,
				context,new HashSet<String>()));
		assertTrue(validator.validateInfoValue("NS=-12", infoHeaders, 0, 0, 10,
				context,new HashSet<String>()));
		assertEquals(0, context.getErrorCount());
	}

	@Test
	public void testValidateInfoIntegerBad() {
		List<VcfFileHeader> infoHeaders = Arrays.asList(makeInfoHeader("NS",
				"1", "Integer"));
		assertFalse(validator.validateInfoValue("NS=1.2", infoHeaders, 0, 0, 5,
				context,new HashSet<String>()));
		assertFalse(validator.validateInfoValue("NS=a", infoHeaders, 0, 0, 6,
				context,new HashSet<String>()));
		assertEquals(2, context.getErrorCount());
		assertEquals(
				"VCF data validation error on line 5: INFO value '1.2' is not valid. Should be an integer",
				context.getErrors().get(0));
	}

	@Test
	public void testValidateInfoFloat() {
		List<VcfFileHeader> infoHeaders = Arrays.asList(makeInfoHeader("NS",
				"1", "Float"));
		assertTrue(validator.validateInfoValue("NS=5", infoHeaders, 0, 0, 10,
				context,new HashSet<String>()));
		assertTrue(validator.validateInfoValue("NS=100.4", infoHeaders, 0, 0,
				10, context,new HashSet<String>()));
		assertTrue(validator.validateInfoValue("NS=-0.8", infoHeaders, 0, 0,
				10, context,new HashSet<String>()));
	}

	@Test
	public void testValidateInfoFloatBad() {
		List<VcfFileHeader> infoHeaders = Arrays.asList(makeInfoHeader("NS",
				"1", "Float"));
		assertFalse(validator.validateInfoValue("NS=1.2.3", infoHeaders, 0, 0,
				10, context,new HashSet<String>()));
		assertFalse(validator.validateInfoValue("NS=koala123", infoHeaders, 0,
				0, 11, context,new HashSet<String>()));
		assertEquals(2, context.getErrorCount());
	}

    @Test
    public void testValidateInfoValidMissing() {
        List<VcfFileHeader> infoHeaders = Arrays.asList(
                makeInfoHeader("TEST1", ".", "Integer"),
                makeInfoHeader("TEST2", ".", "Float"),
                makeInfoHeader("NS", "1", "Integer")
        );
        assertTrue(validator.validateInfoValue("TEST1=.;NS=3", infoHeaders, 0, 0, 15, context, new HashSet<String>()));
        assertTrue(validator.validateInfoValue("TEST2=.;NS=3", infoHeaders, 0, 0, 16, context, new HashSet<String>()));
        assertEquals(0, context.getErrorCount());
    }

	@Test
	public void testValidateInfoFlag() {
		List<VcfFileHeader> infoHeaders = Arrays.asList(makeInfoHeader("AA",
				"0", "Flag"));
		assertTrue(validator.validateInfoValue("AA", infoHeaders, 0, 0, 10,
				context,new HashSet<String>()));
	}

	@Test
	public void testValidateInfoFlagWithValue() {
		List<VcfFileHeader> infoHeaders = Arrays.asList(makeInfoHeader("AA",
				"0", "Flag"));
		assertFalse(validator.validateInfoValue("AA=true", infoHeaders, 0, 0,
				10, context,new HashSet<String>()));
		assertEquals(
				"VCF data validation error on line 10: INFO value 'AA' is not valid. Incorrect number of values.  Expected 0 but found 1",
				context.getErrors().get(0));
	}

	@Test
	public void testValidateInfoFlagWithNumberAndValue() {
		List<VcfFileHeader> infoHeaders = Arrays.asList(makeInfoHeader("AA",
				"1", "Flag"));
		assertFalse(validator.validateInfoValue("AA=true", infoHeaders, 0, 0,
				10, context,new HashSet<String>()));
		assertEquals(
				"VCF data validation error on line 10: INFO value 'AA' is not valid. Defined as a Flag so should not have a value but found 'true'",
				context.getErrors().get(0));
	}

	@Test
	public void testValidateInfoWrongNumber() {
		List<VcfFileHeader> infoHeaders = Arrays.asList(makeInfoHeader("Q",
				"3", "String"));
		assertFalse(validator.validateInfoValue("Q=hello", infoHeaders, 0, 0,
				3, context,new HashSet<String>()));
		assertEquals(1, context.getErrorCount());
		assertEquals(
				"VCF data validation error on line 3: INFO value 'Q' is not valid. Incorrect number of values.  Expected 3 but found 1",
				context.getErrors().get(0));
	}

	@Test
	public void testValidateInfoCharacter() {
		List<VcfFileHeader> infoHeaders = Arrays.asList(makeInfoHeader("CH",
				"3", "Character"));
		assertTrue(validator.validateInfoValue("CH=A,b,?", infoHeaders, 0, 0,
				8, context,new HashSet<String>()));
	}

	@Test
	public void testValidateInfoCharacterBad() {
		List<VcfFileHeader> infoHeaders = Arrays.asList(makeInfoHeader("CH",
				"3", "Character"));
		assertFalse(validator.validateInfoValue("CH=A,b,abc", infoHeaders, 0,
				0, 8, context,new HashSet<String>()));
		assertEquals(
				"VCF data validation error on line 8: INFO value 'CH' is not valid. Defined as a Character but found value 'abc'",
				context.getErrors().get(0));
	}

	@Test
	public void testValidateInfoString() {
		List<VcfFileHeader> infoHeaders = Arrays.asList(makeInfoHeader("STR",
				"1", "String"));
		assertTrue(validator.validateInfoValue("STR=hello", infoHeaders, 0, 0,
				5, context,new HashSet<String>()));
	}

	@Test
	public void testValidateInfoStringBad() {
		List<VcfFileHeader> infoHeaders = Arrays.asList(makeInfoHeader("STR",
				"1", "String"));
		assertFalse(validator.validateInfoValue("STR=  ", infoHeaders, 0, 0, 5,
				context,new HashSet<String>()));
		assertFalse(validator.validateInfoValue("STR=;", infoHeaders, 0, 0, 6,
				context,new HashSet<String>()));
		assertFalse(validator.validateInfoValue("STR=\"", infoHeaders, 0, 0, 7,
				context,new HashSet<String>()));
		assertEquals(3, context.getErrorCount());
		assertEquals(
				"VCF data validation error on line 5: INFO value 'STR' is not valid. String cannot contain whitespace, semi-colon, or quote, but found '  '",
				context.getErrors().get(0));
		assertEquals(
				"VCF data validation error on line 6: INFO value 'STR' is not valid. Incorrect number of values.  Expected 1 but found 0",
				context.getErrors().get(1));
		assertEquals(
				"VCF data validation error on line 7: INFO value 'STR' is not valid. String cannot contain whitespace, semi-colon, or quote, but found '\"'",
				context.getErrors().get(2));
	}

	@Test
	public void testValidateInfoNumberA() {
		List<VcfFileHeader> infoHeaders = Arrays.asList(makeInfoHeader("ZZ",
				"A", "Integer"));
		assertTrue(validator.validateInfoValue("ZZ=1,2,3", infoHeaders, 0, 3,
				10, context,new HashSet<String>()));
		assertTrue(validator.validateInfoValue("ZZ=10,11", infoHeaders, 0, 2,
				10, context,new HashSet<String>()));
		assertTrue(validator.validateInfoValue("ZZ=-12", infoHeaders, 0, 1, 10,
				context,new HashSet<String>()));
	}

	@Test
	public void testValidateInfoNumberABad() {
		List<VcfFileHeader> infoHeaders = Arrays.asList(makeInfoHeader("ZZ",
				"A", "Integer"));
		assertFalse(validator.validateInfoValue("ZZ=1,2,3", infoHeaders, 0, 1,
				10, context,new HashSet<String>()));
		assertFalse(validator.validateInfoValue("ZZ=10,11", infoHeaders, 0, 1,
				10, context,new HashSet<String>()));
		assertEquals(2, context.getErrorCount());
	}

	@Test
	public void testValidateInfoMany() {
		List<VcfFileHeader> infoHeaders = Arrays.asList(
				makeInfoHeader("ZZ", "A", "Integer"),
				makeInfoHeader("BB", "1", "Float"),
				makeInfoHeader("XX", "0", "Flag"),
				makeInfoHeader("HI", "G", "Character"));
		assertTrue(validator.validateInfoValue("XX;ZZ=123;BB=6.7;HI=y",
				infoHeaders, 1, 1, 1, context,new HashSet<String>()));
		assertTrue(validator.validateInfoValue("XX", infoHeaders, 0, 1, 1,
				context,new HashSet<String>()));
		assertTrue(validator.validateInfoValue("XX;ZZ=1,2,3,4,5;BB=6.7",
				infoHeaders, 0, 5, 1, context,new HashSet<String>()));
	}

	@Test
	public void testValidateInfoManyBad() {
		List<VcfFileHeader> infoHeaders = Arrays.asList(
				makeInfoHeader("ZZ", "A", "Integer"),
				makeInfoHeader("BB", "1", "Float"),
				makeInfoHeader("XX", "0", "Flag"),
				makeInfoHeader("HI", "G", "Character"));
		assertFalse(validator.validateInfoValue("ZZ=45,BB=4.5;HI=yo",
				infoHeaders, 1, 1, 100, context,new HashSet<String>()));
		assertEquals(3, context.getErrorCount());
		assertEquals(
				"VCF data validation error on line 100: INFO value 'ZZ' is not valid. Incorrect number of values.  Expected 1 but found 2",
				context.getErrors().get(0));
		assertEquals(
				"VCF data validation error on line 100: INFO value 'BB' is not valid. Should be an integer",
				context.getErrors().get(1));
		assertEquals(
				"VCF data validation error on line 100: INFO value 'HI' is not valid. Defined as a Character but found value 'yo'",
				context.getErrors().get(2));
	}

	@Test
	public void testValidateInfoIDNotFound() {
		assertFalse(validator.validateInfoValue("ABC", null, 1, 1, 50, context,new HashSet<String>()));
		assertEquals(
				"VCF data validation error on line 50: INFO value 'ABC' is not valid. ID not defined in headers",
				context.getErrors().get(0));
	}

	@Test
	public void testValidateInfoG() {
		List<VcfFileHeader> infoHeaders = Arrays.asList(makeInfoHeader("TEST",
				"G", "Integer"));
		assertTrue(validator.validateInfoValue("TEST=1,2,3", infoHeaders, 3, 1,
				10, context,new HashSet<String>()));
		assertTrue(validator.validateInfoValue("TEST=10,11", infoHeaders, 2, 0,
				10, context,new HashSet<String>()));
		assertTrue(validator.validateInfoValue("TEST=-12", infoHeaders, 1, 1,
				10, context,new HashSet<String>()));
	}

	@Test
	public void testValidateInfoGBad() {
		List<VcfFileHeader> infoHeaders = Arrays.asList(makeInfoHeader("TEST",
				"G", "Integer"));
		assertFalse(validator.validateInfoValue("TEST=1", infoHeaders, 3, 1,
				10, context,new HashSet<String>()));
		assertFalse(validator.validateInfoValue("TEST=1,1,1", infoHeaders, 2,
				1, 10, context,new HashSet<String>()));
		assertFalse(validator.validateInfoValue("TEST=1", infoHeaders, 0, 1,
				10, context,new HashSet<String>()));

	}

	@Test
	public void testValidateInfoVt() {
		List<VcfFileHeader> infoHeaders = Arrays.asList(makeInfoHeader("VT",
				"1", "String"));
		assertTrue(validator.validateInfoValue("VT=abc", infoHeaders, 3, 1, 10,
				context,new HashSet<String>()));
		assertTrue(validator.validateInfoValue("VT=foo", infoHeaders, 3, 1, 10,
				context,new HashSet<String>()));
		assertEquals(0, context.getErrorCount());
	}

	@Test
	public void testValidateInfoKey() {
		assertTrue(validator.validateInfoKey("any", "foo", 1, context,new HashSet<String>()));
		assertEquals(0, context.getErrorCount());
	}

    @Test
    public void testSampleMultipleIntegerValueDot() {
        // sample column uses a header that has type integer, and one value is '.' meaning no value -- should be valid
        final List<VcfFileHeader> formatHeaders = new ArrayList<VcfFileHeader>();
        formatHeaders.add(makeFormatHeader("GT", "1", "String"));
        formatHeaders.add(makeFormatHeader("DP", "1", "Integer"));
        formatHeaders.add(makeFormatHeader("BQ", ".", "Integer"));
        formatHeaders.add(makeFormatHeader("SS", "1", "Integer"));
        formatHeaders.add(makeFormatHeader("AD", "1", "String"));
        formatHeaders.add(makeFormatHeader("GQ", "2", "Integer"));
        final String[] dataLine = new String[] { null, "", "", "", "", "", "", "",
				"GT:DP:BQ:SS:AD:GQ", ".:.:.:.:.:1,1", ".:.:.:.:.:1,0", ".:.:.:.:.:.,0" };

        boolean isValid = validator.validateFormatAndSamples(dataLine, formatHeaders, 1,  1, 10, context);
        assertEquals(0, context.getErrorCount());
        assertTrue(isValid);
    }

    @Test
    public void testSampleMultipleFloatValueDot() {
        // sample column uses a header that has type integer, and one value is '.' meaning no value -- should be valid
        final List<VcfFileHeader> formatHeaders = new ArrayList<VcfFileHeader>();
        formatHeaders.add(makeFormatHeader("GT", "1", "String"));
        formatHeaders.add(makeFormatHeader("DP", "1", "Integer"));
        formatHeaders.add(makeFormatHeader("BQ", ".", "Integer"));
        formatHeaders.add(makeFormatHeader("SS", "1", "Integer"));
        formatHeaders.add(makeFormatHeader("AD", "1", "String"));
        formatHeaders.add(makeFormatHeader("GQ", "2", "Float"));
        final String[] dataLine = new String[] { null, "", "", "", "", "", "", "",
                "GT:DP:BQ:SS:AD:GQ", ".:.:.:.:.:1.1,0.1", ".:.:.:.:.:8.1,0.9", ".:.:.:.:.:.,5.0" };

        boolean isValid = validator.validateFormatAndSamples(dataLine, formatHeaders, 1,  1, 10, context);
        assertEquals(0, context.getErrorCount());
        assertTrue(isValid);
    }

    @Test
    public void testSampleMultipleIntegerDotError() {
        final List<VcfFileHeader> formatHeaders = new ArrayList<VcfFileHeader>();
        formatHeaders.add(makeFormatHeader("GT", "1", "String"));
        formatHeaders.add(makeFormatHeader("DP", "1", "Integer"));
        formatHeaders.add(makeFormatHeader("BQ", ".", "Integer"));
        formatHeaders.add(makeFormatHeader("SS", "1", "Integer"));
        formatHeaders.add(makeFormatHeader("AD", "1", "String"));
        formatHeaders.add(makeFormatHeader("YO", "2", "Integer"));
        final String[] dataLine = new String[] { null, "", "", "", "", "", "", "",
                "GT:DP:BQ:SS:AD:YO", ".:.:.:.:.:.", ".:.:.:.:.:.,0", ".:.:.:.:.:1,0" };

        boolean isValid = validator.validateFormatAndSamples(dataLine, formatHeaders, 1,  1, 10, context);
        assertFalse(context.getErrors().toString(), isValid);
        assertEquals(1, context.getErrorCount());
        assertEquals("VCF data validation error on line 10: SAMPLE #1 value '.' is not valid. Incorrect number of values. Expected 2 but found 1.",
                context.getErrors().get(0));
    }

    @Test
    public void testEmptyFormatAndSamples() {
        final String[] dataLine = new String[] {"20", "1234567", ".", "GTC,G", "G,GTCTC", "50", "0", "TEST5=.,.;NS=3;DP=9;AA=G", ".", ".", "."};
        final boolean valid = validator.validateFormatAndSamples(dataLine, null, 1, 1, 15, context);
        assertFalse(valid);
        assertEquals(3, context.getErrorCount());
        assertEquals("VCF data validation error on line 15: FORMAT value '.' is not valid. The following mandatory format fields are missing: GT,DP,BQ,SS", context.getErrors().get(0));
        assertEquals("VCF data validation error on line 15: FORMAT value '.' is not valid. Either AD or DP4 must appear in the format fields.", context.getErrors().get(1));
        assertEquals("VCF data validation error on line 15: FORMAT value '.' is not valid. The format data field is blank ('.').", context.getErrors().get(2));
    }

    @Test
    public void testEmptyFormatNonEmptySamples() {
        final String[] dataLine = new String[] {"20", "1234567", ".", "GTC,G", "G,GTCTC", "50", "0", "TEST5=.,.;NS=3;DP=9;AA=G", ".", "0/1:35:.", "0/2:17:5"};
        assertFalse(validator.validateFormatAndSamples(dataLine, null, 1, 1, 16, context));
        assertEquals(3, context.getErrorCount());
        assertEquals("VCF data validation error on line 16: FORMAT value '.' is not valid. The following mandatory format fields are missing: GT,DP,BQ,SS", context.getErrors().get(0));
        assertEquals("VCF data validation error on line 16: FORMAT value '.' is not valid. Either AD or DP4 must appear in the format fields.", context.getErrors().get(1));
        assertEquals("VCF data validation error on line 16: FORMAT value '.' is not valid. The format data field is blank ('.').", context.getErrors().get(2));
    }

    @Test
    public void testNonEmptyFormatEmptySample() {
        final String[] dataLine = new String[] {"20", "1234567", ".", "GTC,G", "G,GTCTC", "50", "0", "TEST5=.,.;NS=3;DP=9;AA=G", "TEST1:TEST2", "a:b", "."};
        assertFalse(validator.validateFormatAndSamples(dataLine,
                Arrays.asList(makeFormatHeader("TEST1", "1", "String"), makeFormatHeader("TEST2", "1", "String")),
                1, 1, 16, context));
        assertEquals(3, context.getErrorCount());
        assertEquals("VCF data validation error on line 16: FORMAT value 'TEST1:TEST2' is not valid. The following mandatory format fields are missing: GT,DP,BQ,SS", context.getErrors().get(0));
        assertEquals("VCF data validation error on line 16: FORMAT value 'TEST1:TEST2' is not valid. Either AD or DP4 must appear in the format fields.", context.getErrors().get(1));
        assertEquals("VCF data validation error on line 16: SAMPLE #2 value '.' is not valid. The number of fields in the sample column (1) is different from the number of fields in the format column (2).",
                context.getErrors().get(2));
    }
    
    @Test
    public void testValidateIdRegEx() throws Processor.ProcessorException, IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
    	Field field = VcfFileDataLineValidatorImpl.class.getDeclaredField("ID_REGEXP");
    	field.setAccessible(true);
    	String regEx = (String)field.get(null);    	
    	assertTrue(Pattern.compile(regEx).matcher("abc").matches());
    	assertTrue(Pattern.compile(regEx).matcher("abc_123").matches());
    	assertTrue(Pattern.compile(regEx).matcher("123").matches());
    	assertTrue(Pattern.compile(regEx).matcher("abc123").matches());
    	assertTrue(Pattern.compile(regEx).matcher("+abc@#$%^&*()_{}[]|\\_").matches());
    	assertTrue(Pattern.compile(regEx).matcher("+a:bc_").matches());
    	assertFalse(Pattern.compile(regEx).matcher("123;").matches());
    	assertFalse(Pattern.compile(regEx).matcher("123;abc").matches());
    	assertFalse(Pattern.compile(regEx).matcher("123 abc").matches());
    	assertFalse(Pattern.compile(regEx).matcher(" abc").matches());    	    
    }


	private VcfFileHeader makeInfoHeader(final String id, final String number,
			final String type) {
		final VcfFileHeader header = new VcfFileHeader(VcfFile.HEADER_TYPE_INFO);
		Map<String, String> valueMap = new HashMap<String, String>();
		valueMap.put("ID", id);
		valueMap.put("Number", number);
		valueMap.put("Type", type);
		header.setValueMap(valueMap);
		header.setLineNumber(12);
		return header;
	}

	/**
	 * Return a format header
	 * 
	 * @param id
	 *            the header ID
	 * @param number
	 *            the header Number
	 * @param type
	 *            the header Type
	 * @return a format header
	 */
	private VcfFileHeader makeFormatHeader(final String id,
			final String number, final String type) {

		final VcfFileHeader header = new VcfFileHeader(
				VcfFile.HEADER_TYPE_FORMAT);
		final Map<String, String> valueMap = new HashMap<String, String>();
		valueMap.put("ID", id);
		valueMap.put("Number", number);
		valueMap.put("Type", type);
		header.setValueMap(valueMap);

		return header;
	}

	private VcfFileHeader makeAltHeader(final String idValue) {
		final VcfFileHeader header = new VcfFileHeader(VcfFile.HEADER_TYPE_ALT);
		Map<String, String> valueMap = new HashMap<String, String>();
		valueMap.put("ID", idValue);
		header.setValueMap(valueMap);
		return header;
	}

	private void initQcContext() {
		context = null;
		context = new QcContext();
	}

    /**
     * Validates format and samples for the given format field
     *
     * @param formatDataField the format field that will be added to the mandatory fields
     * @param additionalFormatHeaders list of vcf headers to add to the original ones
     * @param gtFirst whether GT field should be first or not
     * @param sampleData the sample data
     * @param numGenotypes number og genotypes
     * @param numAltAlleles number of alt alleles
     * @param sampleDataIsForGTField whether the sample data is for the GT field or not
     * @return the result of the validation
     */
    private boolean validateFormatAndSamples(final String formatDataField,
                                             final List<VcfFileHeader> additionalFormatHeaders,
                                             final boolean gtFirst,
                                             final String sampleData,
                                             final Integer numGenotypes,
                                             final Integer numAltAlleles,
                                             final boolean sampleDataIsForGTField) {

        String formatData = "DP:BQ:SS:AD";
        if(formatDataField != null) {
            formatData += ":" + formatDataField;
        }

        if(gtFirst) {
            formatData = "GT:" + formatData;
        } else {
            formatData = formatData + ":GT";
        }

        final String[] dataLine;
        final String allSampleData;
        if(sampleData != null) {

            if(sampleDataIsForGTField) {

                if(gtFirst) {
                    allSampleData = sampleData + ":.:.:.:.";
                } else {
                    allSampleData = ".:.:.:.:" + sampleData;
                }

            } else {
                allSampleData = ".:.:.:.:.:" + sampleData;
            }

            dataLine = getDataLineWithChromAndFormatAndSampleData(null, formatData, allSampleData);
        } else {
            dataLine = getDataLineWithFormatData(formatData);
        }

        final List<VcfFileHeader> vcfFileHeaders = new ArrayList<VcfFileHeader>();
        vcfFileHeaders.add(makeFormatHeader("GT", "1", "String"));
        vcfFileHeaders.add(makeFormatHeader("DP", "1", "Integer"));
        vcfFileHeaders.add(makeFormatHeader("BQ", ".", "Integer"));
        vcfFileHeaders.add(makeFormatHeader("SS", "1", "Integer"));
        vcfFileHeaders.add(makeFormatHeader("AD", "1", "String"));

        if(additionalFormatHeaders != null) {
            vcfFileHeaders.addAll(additionalFormatHeaders);
        }

        return validator.validateFormatAndSamples(dataLine, vcfFileHeaders, numAltAlleles, numGenotypes, 1, context);
    }
}
