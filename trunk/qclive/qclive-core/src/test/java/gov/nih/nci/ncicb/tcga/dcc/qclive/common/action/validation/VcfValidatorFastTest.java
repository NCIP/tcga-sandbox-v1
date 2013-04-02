/*
 * Software License, Version 1.0 Copyright 2012 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.MetaDataBean;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFile;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.VcfFileHeader;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileDataLineValidatorImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.VcfFileDataLineValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.VcfHeaderDefinitionStore;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.TcgaVcfFileHeaderValidator.HEADER_TYPE_ASSEMBLY;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Test for VcfValidator.
 *
 * @author chenjw Last updated by: $Author$
 * @version $Rev$
 */
public class VcfValidatorFastTest {
    private static final String SAMPLES_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;
    private static final String TEST_VCF_DIRECTORY = SAMPLES_DIR
            + "qclive/vcf/";
    private static final String TCGA_INVALID_INFO_VALUES_FILE = "tcga/tcgaInvalidInfoValues.vcf";
    private static final String TCGA_VALID_INFO_VALUES_FILE = "tcga/tcgaValidInfoValues.vcf";
    private static final String TCGA_INVALID_INDIVIDUAL_FILE = "tcga/tcgaWrongIndividual.vcf";
    private static final String TCGA_GOOD_CASE_INSENSITIVE_FILE = "tcga/tcgaGoodCaseInsensitive.vcf";
    private static final String TCGA_BAD_CASE_INSENSITIVE_FILE = "tcga/tcgaBadCaseInsensitive.vcf";
    private static final String TCGA_VALID_WITH_VCFPROCESSLOG_FILE_1 = "tcga/tcgaValidVcfProcessLogHeader_1.vcf";
    private static final String TCGA_VALID_WITH_VCFPROCESSLOG_FILE_2 = "tcga/tcgaValidVcfProcessLogHeader_2.vcf";
    private static final String TCGA_MISSING_VCFPROCESSLOG_FILE = "tcga/tcgaMissingVcfProcessLog.vcf";
    private static final String TCGA_VCFPROCESSLOG_NO_VALUE_FILE = "tcga/tcgaVcfProcessLogNoValue.vcf";
    private static final String TCGA_VCFPROCESSLOG_MISSING_TWO_POUND_SIGNS_FILE = "tcga/tcgaInvalidVcfProcessLogHeaderNotStartingWithTwoPoundSigns.vcf";
    private static final String TCGA_VCFPROCESSLOG_MISSING_GLOBAL_BRACKETS_FILE = "tcga/tcgaInvalidVcfProcessLogHeaderMissingGlobalBrackets.vcf";
    private static final String TCGA_VCFPROCESSLOG_MISSING_MAP_VALUES_BRACKETS_FILE = "tcga/tcgaInvalidVcfProcessLogHeaderMissingBracketsInMapValues.vcf";
    private static final String TCGA_INVALID_VCFPROCESSLOG_NUMBER_OF_VALUES_PER_TAG_FILE = "tcga/tcgaInvalidVcfProcessLogHeaderNumberOfValuesPerTag.vcf";
    private static final String TCGA_INVALID_VCFPROCESSLOG_DUPLICATES_IN_MULTIPLE_VALUES_FILE = "tcga/tcgaInvalidVcfProcessLogHeaderDuplicatesInMultipleValues.vcf";
    private static final String TCGA_INVALID_VCFPROCESSLOG_USING_WRONG_SEPARATOR_FILE = "tcga/tcgaInvalidVcfProcessLogHeaderUsingWrongSeparator.vcf";
    private static final String TCGA_INVALID_VCFPROCESSLOG_WITH_WHITESPACE_IN_VALUES_FILE = "tcga/tcgaValidVcfProcessLogHeaderWithWhitespace.vcf";
    private static final String TCGA_INVALID_VCFPROCESSLOG_MISSING_IDENTIFIER_IN_MERGE_FILE = "tcga/tcgaValidVcfProcessLogHeaderMissingIdentifierInMerge.vcf";
    private static final String TCGA_VALID_VCFPROCESSLOG_ONE_INPUTVCF_SUBFIELD_WITH_NO_MERGE_TAGS_FILE = "tcga/tcgaValidVcfProcessLogHeaderOneInputVCFSubFieldWithNoMergeTags.vcf";
    private static final String TCGA_VALID_VCFPROCESSLOG_ONE_INPUTVCF_SUBFIELD_WITH_MISSING_IDENTIFIER_IN_MERGE_TAGS_FILE = "tcga/tcgaValidVcfProcessLogHeaderOneInputVCFSubFieldWithMissingIdentifierInMergeTags.vcf";
    private static final String TCGA_VALID_VCFPROCESSLOG_ONE_INPUTVCF_SUBFIELD_WITH_MULTIPLE_SUBFIELDS_IN_MERGE_TAGS_FILE = "tcga/tcgaInvalidVcfProcessLogHeaderOneInputVCFSubFieldWithMultipleSubFieldsInMergeTags.vcf";
    private static final String TCGA_INVALID_VCFPROCESSLOG_MISSING_MERGE_TAGS_FILE = "tcga/tcgaInvalidVcfProcessLogHeaderMissingMergeTags.vcf";
    private static final String TCGA_INVALID_VCFPROCESSLOG_MISSING_MERGE_TAGS_BUT_ONE_FILE = "tcga/tcgaInvalidVcfProcessLogHeaderMissingMergeTagsButOne.vcf";
    private static final String TCGA_INVALID_VCFPROCESSLOG_MISSING_MERGEPARAM_SUBFIELDS_FILE = "tcga/tcgaInvalidVcfProcessLogHeaderMultipleInputVCFSubFieldWithMissingSubFieldsInMergeParam.vcf";
    private static final String TCGA_INVALID_VCFPROCESSLOG_MISSING_MERGEPARAM_AND_MERGEVER_SUBFIELDS_FILE = "tcga/tcgaInvalidVcfProcessLogHeaderMultipleInputVCFSubFieldWithMissingSubFieldsInMergeParamAndMergeVer.vcf";
    private static final String TCGA_INVALID_VCFPROCESSLOG_UNEXPECTED_MERGEPARAM_AND_MERGEVER_SUBFIELDS_FILE = "tcga/tcgaInvalidVcfProcessLogHeaderMultipleInputVCFSubFieldWithUnexpectedSubFieldsInMergeParamAndMergeVer.vcf";
    private static final String TCGA_MISSING_PHASING_FILE = "tcga/tcgaMissingPhasing.vcf";
    private static final String TCGA_PHASING_NO_VALUE_FILE = "tcga/tcgaPhasingNoValue.vcf";
    private static final String TCGA_PHASING_PARTIAL_VALUE_FILE = "tcga/tcgaPhasingPartialValue.vcf";
    private static final String TCGA_PHASING_QUOTED_VALUE_FILE = "tcga/tcgaPhasingQuotedValue.vcf";
    private static final String TCGA_PHASING_KEYMAP_VALUE_FILE = "tcga/tcgaPhasingKeyMapValue.vcf";
    private static final String TCGA_PHASING_BAD_VALUE_FILE = "tcga/tcgaPhasingBadValue.vcf";
    private static final String TCGA_MISSING_FILEDATE_FILE = "tcga/tcgaMissingFileDate.vcf";
    private static final String TCGA_MISSING_REFERENCE_FILE = "tcga/tcgaMissingReference.vcf";
    private static final String TCGA_REFERENCE_NO_VALUE_FILE = "tcga/tcgaReferenceNoValue.vcf";
    private static final String TCGA_REFERENCE_EMPTY_VALUE_FILE = "tcga/tcgaReferenceEmptyValue.vcf";
    private static final String TCGA_REFERENCE_INVALID_VALUE_FILE = "tcga/tcgaReferenceInvalidValue.vcf";
    private static final String TCGA_REFERENCE_IGNORE_CASE_VALUE_FILE = "tcga/tcgaReferenceIgnoreCaseValue.vcf";
    private static final String TCGA_VALID_WITH_GENE_INFO_DATA_ID_FILE = "tcga/tcgaValidWithGeneInfoDataId.vcf";
    private static final String TCGA_VALID_WITHOUT_GENE_INFO_DATA_ID_FILE = "tcga/tcgaValidWithoutGeneInfoDataId.vcf";
    private static final String TCGA_VALID_WITH_GENE_INFO_HEADER_WITHOUT_GENE_INFO_DATA_ID_FILE = "tcga/tcgaValidWithGeneInfoHeaderWithoutGeneInfoDataId.vcf";
    private static final String TCGA_INVALID_MISSING_GENE_ANNO_HEADER_WITH_GENE_INFO_DATA_ID_FILE = "tcga/tcgaInvalidMissingGeneAnnoHeaderWithGeneInfoDataId.vcf";
    private static final String TCGA_INVALID_BLANK_GENE_ANNO_HEADER_WITH_GENE_INFO_DATA_ID_FILE = "tcga/tcgaInvalidBlankGeneAnnoHeaderWithGeneInfoDataId.vcf";
    private static final String TCGA_VALID_ASSEMBLY_HEADER_WITH_CHROM_DATA_ID_FILE = "tcga/tcgaValidAssemblyHeaderWithChromDataId.vcf";
    private static final String TCGA_VALID_ASSEMBLY_HEADER_WITH_CHROM_DATA_ID_HTTP_FORMAT_FILE = "tcga/tcgaValidAssemblyHeaderWithChromDataIdHttpFormat.vcf";
    private static final String TCGA_VALID_NO_ASSEMBLY_HEADER_WITH_NO_CHROM_DATA_ID_FILE = "tcga/tcgaValidNoAssemblyHeaderWithNoChromDataId.vcf";
    private static final String TCGA_INVALID_BLANK_ASSEMBLY_HEADER_WITH_CHROM_DATA_ID_FILE = "tcga/tcgaInvalidBlankAssemblyHeaderWithChromDataId.vcf";
    private static final String TCGA_INVALID_NO_ASSEMBLY_HEADER_WITH_CHROM_DATA_ID_FILE = "tcga/tcgaInvalidNoAssemblyHeaderWithChromDataId.vcf";
    private static final String TCGA_INVALID_ASSEMBLY_HEADER_WITH_CHROM_DATA_ID_BAD_URL_FILE = "tcga/tcgaInvalidAssemblyHeaderWithChromDataIdBadUrl.vcf";
    private static final String TCGA_VALID_WITH_ASSEMBLY_HEADER_NO_CHROM_DATA_ID_FILE = "tcga/tcgaValidWithAssemblyHeaderWithNoChromDataId.vcf";
    private static final String TCGA_INVALID_CENTER_HEADER_WHITESPACE_FILE = "tcga/tcgaInvalidCenterHeaderWhiteSpace.vcf";
    private static final String TCGA_VALID_CENTER_HEADER_DOUBLEQUOTED_FILE = "tcga/tcgaValidCenterHeaderDoubleQuoted.vcf";
    private static final String TCGA_VALID_CENTER_HEADER_NOWHITESPACE_FILE = "tcga/tcgaValidCenterHeaderNoWhiteSpace.vcf";
    private static final String TCGA_INVALID_CENTER_HEADER_MULTIDOUBLEQUOTED_FILE = "tcga/tcgaInvalidCenterHeaderMultiDoubleQuoted.vcf";
    private static final String TCGA_INVALID_CENTER_HEADER_NOCENTER_FILE = "tcga/tcgaInvalidCenterHeaderNoCenter.vcf";
    private static final String TCGA_VALID_SAMPLE_HEADER_FILE = "tcga/tcgaValidSampleHeader.vcf";
    private static final String TCGA_INVALID_SAMPLE_HEADER_NOTVALEUMAP_FILE = "tcga/tcgaInValidSampleHeaderNotValueMap.vcf";
    private static final String TCGA_INVALID_SAMPLE_HEADER_NOIDKEY_FILE = "tcga/tcgaInValidSampleHeaderNoIdKey.vcf";
    private static final String TCGA_VALID_SAMPLE_HEADER_NOINDIVIDUALKEY_FILE = "tcga/tcgaValidSampleHeaderNoIndividualKey.vcf";
    private static final String TCGA_INVALID_SAMPLE_HEADER_NOFILEKEY_FILE = "tcga/tcgaInValidSampleHeaderNoFileKey.vcf";
    private static final String TCGA_INVALID_SAMPLE_HEADER_NOPLATFORMKEY_FILE = "tcga/tcgaInValidSampleHeaderNoPlatformKey.vcf";
    private static final String TCGA_INVALID_SAMPLE_HEADER_NOSOURCEKEY_FILE = "tcga/tcgaInValidSampleHeaderNoSourceKey.vcf";
    private static final String TCGA_INVALID_SAMPLE_HEADER_NOACCESSIONKEY_FILE = "tcga/tcgaInValidSampleHeaderNoAccessionKey.vcf";
    private static final String TCGA_INVALID_SAMPLE_HEADER_NOANGLEBRACKETS_FILE = "tcga/tcgaInValidSampleHeaderNoAngleBrackets.vcf";
    private static final String TCGA_INVALID_SAMPLE_HEADER_NUMVALUES_FILE = "tcga/tcgaInValidSampleHeaderNumValues.vcf";
    private static final String TCGA_INVALID_SAMPLE_HEADER_MISMATCHEDANGLEBRACKETS_FILE = "tcga/tcgaInValidSampleHeaderMisMatchedAngleBrackets.vcf";
    private static final String TCGA_INVALID_SAMPLE_HEADER_INVALID_MIXTURE_VALUE_FILE = "tcga/tcgaInValidSampleHeaderMixtureValue.vcf";
    private static final String TCGA_INVALID_SAMPLE_HEADER_INVALID_MIXTURE_VALUE_SUM_FILE = "tcga/tcgaInValidSampleHeaderMixtureValueSum.vcf";
    private static final String TCGA_INVALID_SAMPLE_HEADER_GENOME_DESCRIPTION_VALUE_FILE = "tcga/tcgaInValidSampleHeaderGenomeDescriptionValues.vcf";
    private static final String TCGA_INVALID_SAMPLE_HEADER_SAMPLE_NAME_VALUE = "tcga/tcgaInvalidSampleHeaderSampleNameValue.vcf";
    private static final String TCGA_INVALID_SAMPLE_HEADER_COLUMN_NAMES_FILE = "tcga/tcgaInValidSampleHeaderColumnNames.vcf";
    private static final String TCGA_VALID_DP_VALUES_FILE = "tcga/tcgaValidDPValues.vcf";
    private static final String TCGA_VALID_DP_VALUES_NOINFODP_FILE = "tcga/tcgaValidDPValuesNoInfoDP.vcf";
    private static final String TCGA_INVALID_DP_VALUES_FILE = "tcga/tcgaInvalidDPValues.vcf";
    private static final String TCGA_INVALID_DP_VALUES_LESS_FILE = "tcga/tcgaInvalidDPValuesLess.vcf";
    private static final String TCGA_INVALID_DP_VALUES_MORE_SAMPLES_FILE = "tcga/tcgaInvalidDPValuesMoreSamples.vcf";
    private static final String TCGA_INVALID_SVALT_VALUES_CHROMID_FORMAT1_FILE = "tcga/tcgaInValidSvaltValuesChromIdFormat1.vcf";
    private static final String TCGA_INVALID_SVALT_VALUES_CHROMID_FORMAT2_FILE = "tcga/tcgaInValidSvaltValuesChromIdFormat2.vcf";
    private static final String TCGA_INVALID_SVALT_VALUES_CHROMID_FORMAT3_FILE = "tcga/tcgaInValidSvaltValuesChromIdFormat3.vcf";
    private static final String TCGA_INVALID_SVALT_VALUES_CHROMID_FORMAT4_FILE = "tcga/tcgaInValidSvaltValuesChromIdFormat4.vcf";
    private static final String TCGA_VALID_SVALT_VALUES_CHROMID_FILE = "tcga/tcgaValidSvaltValuesChromId.vcf";
    private static final String TCGA_VALID_SVALT_VALUES_NO_CHROMID_FILE = "tcga/tcgaValidSvaltValuesNoChromId.vcf";
    private static final String TCGA_VALID_FORMATID_TE_FILE = "tcga/tcgaValidFormatIdTE.vcf";
    private static final String TCGA_INVALID_FORMATID_TE_FILE = "tcga/tcgaInValidFormatIdTE.vcf";
    private static final String TCGA_VALID_INFO_RGN_VALUES_FILE = "tcga/tcgaValidInfoRGNValues.vcf";
    private static final String TCGA_INVALID_INFO_RGN_VALUES_FILE = "tcga/tcgaInValidInfoRGNValues.vcf";
    private static final String TCGA_INVALID_REPORTING_ALL_ERRORS_WITHOUT_TRAILING_WHITESPACE_IN_COLUMN_HEADER = "tcga/tcgaInvalidReportingAllErrorsWithoutTrailingWhitespaceInColumnHeader.vcf";
    private static final String TCGA_INVALID_REPORTING_ALL_ERRORS_WITH_TRAILING_WHITESPACE_IN_COLUMN_HEADER = "tcga/tcgaInvalidReportingAllErrorsWithTrailingWhitespaceInColumnHeader.vcf";
    private static final String TCGA_INVALID_REPORTING_ALL_ERRORS = "tcga/tcgaInvalidReportingAllErrors.vcf";
    private static final String TCGA_NO_VCF_IDS = "tcga/tcgaNoVcfIds.vcf";
    private static final String TCGA_DUPS_HEADER_SCENARIO_1 = "tcga/tcgaDupsRequiredHeaderScenario1.vcf";
    private static final String TCGA_DUPS_HEADER_SCENARIO_2_AND_3 = "tcga/tcgaDupsNonRequiredHeaderScenario2And3.vcf";
    private static final String TCGA_DUPS_ID_SUBFIELD_SCENARIO_4_TO_8 = "tcga/tcgaDupsIdSubfieldScenario4To8.vcf";
    private static final String APPS_4390_TEST_FILE = "tcga/apps4390.vcf";
    private static final String TCGA_INVALID_MISSING_SVTYPE = "tcga/tcgaInvalidMissingSVTYPE.vcf";
    private static final String TCGA_VALID_FILTER_WARNING = "tcga/tcgaValidFilterWarning.vcf";
    private static final String TCGA_INVALID_REPEATING_PEDIGREE_VALUES = "tcga/tcgaInvalidRepeatingPedigreeValues.vcf";
    private static final String TCGA_INVALID_EMPTY_HEADER_LINE = "tcga/tcgaInvalidEmptyHeaderLine.vcf";
    private static final String TCGA_VALID_SAMPLE_HEADER_MISSING_SAMPLE_NAME = "tcga/tcgaValidSampleHeaderNoSampleNameKey.vcf";
    private static final String TCGA_VALID_ONE_DATA_LINE = "tcga" + File.separator + "tcgaValidOneDataLine.vcf";
    private static final String TCGA_INVALID_SAMPLE_HEADER_GOOD_INDIVIDUAL_FILE = "tcga/tcgaValidSampleHeaderGoodIndividual.vcf";
    private static final String TCGA_INVALID_SAMPLE_HEADER_WRONG_INDIVIDUAL_FILE = "tcga/tcgaValidSampleHeaderWrongIndividual.vcf";

    private final Mockery mockery = new JUnit4Mockery();
    private QcLiveBarcodeAndUUIDValidator mockBarcodeAndUUIDValidatorImpl;
    private VcfValidator vcfValidator;
    private QcContext context;

    @Before
    public void setup() {
        context = new QcContext();

        vcfValidator = new VcfValidator() {
            public VcfFileDataLineValidator getVcfFileDataLineValidator() {
                return new TcgaVcfFileDataLineValidatorImpl();
            }
        };

        TcgaVcfFileHeaderValidator headerValidator = new TcgaVcfFileHeaderValidator();
        VcfHeaderDefinitionStore headerDefinitionStore = new VcfHeaderDefinitionStore() {
            public VcfFileHeader getHeaderDefinition(final String headerType, final String headerId) {
                return null;
            }
        };
        headerValidator.setVcfHeaderDefinitionStore(headerDefinitionStore);
        vcfValidator.setVcfFileHeaderValidator(headerValidator);
        vcfValidator.setTcgaVcfVersion("1.1");
        mockBarcodeAndUUIDValidatorImpl = mockery.mock(QcLiveBarcodeAndUUIDValidator.class);
        vcfValidator.setQcLiveBarcodeAndUUIDValidator(mockBarcodeAndUUIDValidatorImpl);

        mockery.checking(new Expectations() {{
            allowing(mockBarcodeAndUUIDValidatorImpl).validateBarcodeOrUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));
        }});
    }

    @Test
    public void testGetFileExtension() {
        assertEquals(".vcf", vcfValidator.getFileExtension());
    }

    @Test
    public void testGetFileFormatTcga() throws UnsupportedFileException {
        VcfFile vcf = new VcfFile();
        VcfFileHeader header = new VcfFileHeader("tcgaversion");
        header.setValue("1.0");
        vcf.setHeaders(Arrays.asList(header));
        vcfValidator.checkFileFormat(vcf);
        assertEquals(0, context.getWarningCount());
    }

    @Test(expected = UnsupportedFileException.class)
    public void testGetFileFormatGeneral() throws UnsupportedFileException {
        VcfFile vcf = new VcfFile();
        VcfFileHeader header = new VcfFileHeader(VcfFile.HEADER_TYPE_FILEFORMAT);
        header.setValue("VCFv4.1");
        vcf.setHeaders(Arrays.asList(header));
        vcfValidator.checkFileFormat(vcf);
    }

    @Test
    public void testValidateTcgaVersion() {
        VcfFile vcf = new VcfFile();
        VcfFileHeader tcgaVersionHeader = new VcfFileHeader("tcgaversion");
        tcgaVersionHeader.setValue("1.1");
        vcf.addHeader(tcgaVersionHeader);

        assertTrue(vcfValidator.validateTcgaVersion(vcf, context));
    }

    @Test
    public void testValidateTcgaVersionInvalid() {
        VcfFile vcf = new VcfFile();
        VcfFileHeader tcgaVersionHeader = new VcfFileHeader("tcgaversion");
        tcgaVersionHeader.setValue("something");
        vcf.addHeader(tcgaVersionHeader);
        assertFalse(vcfValidator.validateTcgaVersion(vcf, context));
    }


    @Test
    public void testFirstHeaderLine() throws Processor.ProcessorException {
        // first line says "format" and not "fileformat"
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                "tcga/tcgaInvalidFirstHeader.vcf"), context));
        assertEquals(1, context.getErrorCount());
        assertEquals(
                "[tcgaInvalidFirstHeader.vcf] First line of VCF file must contain the 'fileformat' header",
                context.getErrors().get(0));
    }

    @Test(expected = Processor.ProcessorException.class)
    public void testMissingVersionHeaderLine() throws Processor.ProcessorException {
        vcfValidator.processFile(new File(TEST_VCF_DIRECTORY, "tcga/tcgaMissingVersionHeader.vcf"), context);
    }


    @Test
    public void testGetReturnValue() {
        Map<File, Boolean> validationResults = new HashMap<File, Boolean>();
        assertTrue(vcfValidator.getReturnValue(validationResults, context));

        validationResults.put(new File("a"), true);
        assertTrue(vcfValidator.getReturnValue(validationResults, context));

        validationResults.put(new File("b"), false);
        assertFalse(vcfValidator.getReturnValue(validationResults, context));
    }

    @Test
    public void testGetDefaultReturnValue() {
        assertTrue(vcfValidator.getDefaultReturnValue(null));
    }

    @Test
    public void testIsCorrectArchiveType() {
        // this just always returns true

        assertTrue(vcfValidator.isCorrectArchiveType(null));

        final Archive anArchive = new Archive();
        assertTrue(vcfValidator.isCorrectArchiveType(anArchive));
    }

    @Test
    public void testGetName() {
        assertEquals("Variant Call Format file validator",
                vcfValidator.getName());
    }

    @Test
    public void testInvalidTcgaInfoValues() throws Processor.ProcessorException {
        // file has mistake in info value for VT
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_INFO_VALUES_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals(
                "[tcgaInvalidInfoValues.vcf] VCF data validation error on line 12: INFO value 'VT' is not valid. is VT and should have one of SNP, INS or DEL but found 'abc'",
                context.getErrors().get(0));
    }

    /**
     * This test tests that a tcga vcf file containing valid info values
     *
     * @throws Processor.ProcessorException
     */
    @Test
    public void testValidTcgaInfoValues() throws Processor.ProcessorException {
        // file has no mistakes for INFO value VT
        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_INFO_VALUES_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testTcgaHeaderCaseInsensitiveGood() throws Processor.ProcessorException {
        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_GOOD_CASE_INSENSITIVE_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testTcgaHeaderCaseInsensitiveBad() throws Processor.ProcessorException {
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_BAD_CASE_INSENSITIVE_FILE), context));
        assertEquals(4, context.getErrorCount());
        assertEquals("[tcgaBadCaseInsensitive.vcf] VCF Column header validation error: column at position 1 should be 'CHROM'",
                context.getErrors().get(0));
        assertEquals("[tcgaBadCaseInsensitive.vcf] VCF Column header validation error: column at position 2 should be 'POS'",
                context.getErrors().get(1));
        assertEquals("[tcgaBadCaseInsensitive.vcf] VCF Column header validation error: column at position 3 should be 'ID'",
                context.getErrors().get(2));
        assertEquals("[tcgaBadCaseInsensitive.vcf] VCF Column header validation error: column at position 4 should be 'REF'",
                context.getErrors().get(3));
    }

    @Test
    public void testTcgaDupsRequiredHeaderScenario1() throws Processor.ProcessorException {
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_DUPS_HEADER_SCENARIO_1), context));
        assertEquals(9, context.getErrorCount());
        assertEquals("[tcgaDupsRequiredHeaderScenario1.vcf] VCF header validation error: header 'fileformat' is duplicated.",
                context.getErrors().get(0));
        assertEquals("[tcgaDupsRequiredHeaderScenario1.vcf] VCF header validation error: header 'tcgaversion' is duplicated.",
                context.getErrors().get(1));
        assertEquals("[tcgaDupsRequiredHeaderScenario1.vcf] VCF header validation error: header 'phasing' is duplicated.",
                context.getErrors().get(2));
        assertEquals("[tcgaDupsRequiredHeaderScenario1.vcf] VCF header validation error: header 'reference' is duplicated.",
                context.getErrors().get(3));
        assertEquals("[tcgaDupsRequiredHeaderScenario1.vcf] VCF header validation error: header 'fileDate' is duplicated.",
                context.getErrors().get(4));
        assertEquals("[tcgaDupsRequiredHeaderScenario1.vcf] VCF header validation error: header 'vcfProcessLog' is duplicated.",
                context.getErrors().get(5));
        assertEquals("[tcgaDupsRequiredHeaderScenario1.vcf] VCF header validation error: header 'center' is duplicated.",
                context.getErrors().get(6));
        assertEquals("[tcgaDupsRequiredHeaderScenario1.vcf] VCF header validation error: header 'geneAnno' is duplicated.",
                context.getErrors().get(7));
        assertEquals("[tcgaDupsRequiredHeaderScenario1.vcf] VCF header validation error: header 'assembly' is duplicated.",
                context.getErrors().get(8));
    }

    @Test
    public void testTcgaDupsNonRequiredHeaderScenario2and3() throws Processor.ProcessorException {
        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_DUPS_HEADER_SCENARIO_2_AND_3), context));
        assertEquals(0, context.getErrorCount());
        assertEquals(2, context.getWarningCount());
        assertEquals("[tcgaDupsNonRequiredHeaderScenario2And3.vcf] VCF header validation error: header 'individual' is duplicated.",
                context.getWarnings().get(0));
        assertEquals("[tcgaDupsNonRequiredHeaderScenario2And3.vcf] VCF header validation error: header 'twin' is duplicated.",
                context.getWarnings().get(1));
    }

    @Test
    public void testTcgaDupsIDSubFieldScenario4To8() throws Processor.ProcessorException {
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_DUPS_ID_SUBFIELD_SCENARIO_4_TO_8), context));
        assertEquals(5, context.getErrorCount());
        assertEquals("[tcgaDupsIdSubfieldScenario4To8.vcf] Header vcfProcessLog: Key 'InputVCFSource' on line 5 is a duplicate key",
                context.getErrors().get(0));
        assertEquals("[tcgaDupsIdSubfieldScenario4To8.vcf] Header INFO: Key 'ID' on line 10 is a duplicate key", context.getErrors().get(1));
        assertEquals("[tcgaDupsIdSubfieldScenario4To8.vcf] Header INFO: Key 'Type' on line 10 is a duplicate key", context.getErrors().get(2));
        assertEquals("[tcgaDupsIdSubfieldScenario4To8.vcf] Header INFO: Key 'Number' on line 11 is a duplicate key", context.getErrors().get(3));
        assertEquals("[tcgaDupsIdSubfieldScenario4To8.vcf] Header ALT: Key 'Description' on line 12 is a duplicate key", context.getErrors().get(4));
    }

    @Test
    public void testTcgaHeaderNoVCFIds() throws Processor.ProcessorException {
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_NO_VCF_IDS), context));
        assertEquals(4, context.getErrorCount());
        assertEquals("[tcgaNoVcfIds.vcf] VCF error: file [tcgaNoVcfIds.vcf] does not contain any VCF IDs", context.getErrors().get(0));
        assertEquals("[tcgaNoVcfIds.vcf] Line 11 did not contain expected number of columns", context.getErrors().get(1));
        assertEquals("[tcgaNoVcfIds.vcf] Line 12 did not contain expected number of columns", context.getErrors().get(2));
        assertEquals("[tcgaNoVcfIds.vcf] Line 13 did not contain expected number of columns", context.getErrors().get(3));
    }

    @Test
    public void testValidAssemblyHeader() throws Processor.ProcessorException {
        // file has CHROM value in ID and header contains ##assembly=something
        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_ASSEMBLY_HEADER_WITH_CHROM_DATA_ID_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testFilesWithDifferentAssemblyRequirements() throws Processor.ProcessorException {
        // running a file which needs the assembly header followed by one that does not
        // should still result in them both passing validation
        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_ASSEMBLY_HEADER_WITH_CHROM_DATA_ID_FILE), context));
        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_NO_ASSEMBLY_HEADER_WITH_NO_CHROM_DATA_ID_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidAssemblyHeaderHttpFormat() throws Processor.ProcessorException {
        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_ASSEMBLY_HEADER_WITH_CHROM_DATA_ID_HTTP_FORMAT_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidAssemblyHeaderNoAssemblyNoChromDataId() throws Processor.ProcessorException {
        // file has no CHROM value in ID and contains no ##assembly header
        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_NO_ASSEMBLY_HEADER_WITH_NO_CHROM_DATA_ID_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidAssemblyHeaderWithAssemblyNoChromDataId() throws Processor.ProcessorException {
        // file has no CHROM value in ID and contains a ##assembly header
        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_WITH_ASSEMBLY_HEADER_NO_CHROM_DATA_ID_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testInvalidAssemblyHeaderBlankAssembly() throws Processor.ProcessorException {
        // file has CHROM value in ID and header has blank ##assembly
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_BLANK_ASSEMBLY_HEADER_WITH_CHROM_DATA_ID_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInvalidBlankAssemblyHeaderWithChromDataId.vcf] VCF header validation error: header 'assembly' has no value.", context.getErrors().get(0));
    }

    @Test
    public void testInvalidAssemblyHeaderNoAssembly() throws Processor.ProcessorException {
        // file has CHROM value in ID and header has no ##assembly
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_NO_ASSEMBLY_HEADER_WITH_CHROM_DATA_ID_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInvalidNoAssemblyHeaderWithChromDataId.vcf] VCF header validation error: header 'assembly' is missing.", context.getErrors().get(0));
    }

    @Test
    public void testInvalidAssemblyHeaderBadUrl() throws Processor.ProcessorException {
        // file has CHROM value in ID and header has bad url in ##assembly
        // APPS-6529 we no longer want to enforce the URL requirement so this should pass
        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_ASSEMBLY_HEADER_WITH_CHROM_DATA_ID_BAD_URL_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidWithVcfProcessLogExample1() throws Exception {

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_WITH_VCFPROCESSLOG_FILE_1), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidWithVcfProcessLogExample2() throws Exception {

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_WITH_VCFPROCESSLOG_FILE_2), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testMissingVcfProcessLog() throws Exception {
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_MISSING_VCFPROCESSLOG_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals(
                "[tcgaMissingVcfProcessLog.vcf] VCF header validation error: header 'vcfProcessLog' is missing.",
                context.getErrors().get(0));
    }

    @Test
    public void testVcfProcessLogNoValues() throws Exception {
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VCFPROCESSLOG_NO_VALUE_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals(
                "[tcgaVcfProcessLogNoValue.vcf] VCF header validation error: header 'vcfProcessLog' has no value.",
                context.getErrors().get(0));
    }

    @Test
    public void testVcfProcessLogMissingTwoPoundSigns() throws Exception {
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VCFPROCESSLOG_MISSING_TWO_POUND_SIGNS_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals(
                "[tcgaInvalidVcfProcessLogHeaderNotStartingWithTwoPoundSigns.vcf] Header on line 5 must start with ##",
                context.getErrors().get(0));
    }

    @Test
    public void testVcfProcessLogMissingGlobalBrakets() throws Exception {
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VCFPROCESSLOG_MISSING_GLOBAL_BRACKETS_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals(
                "[tcgaInvalidVcfProcessLogHeaderMissingGlobalBrackets.vcf] VCF header validation error: " +
                        "header 'vcfProcessLog' is not surrounded by angle brackets (<>)",
                context.getErrors().get(0));
    }

    @Test
    public void testVcfProcessLogMissingMapValuesBrakets() throws Exception {
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VCFPROCESSLOG_MISSING_MAP_VALUES_BRACKETS_FILE), context));
        assertEquals(5, context.getErrorCount());
        assertEquals(
                "[tcgaInvalidVcfProcessLogHeaderMissingBracketsInMapValues.vcf] VCF header validation error: header 'vcfProcessLog': " +
                        "'InputVCFgeneAnno' tag is missing opening angle bracket (<) in '(anno1.gaf)' value.",
                context.getErrors().get(0));
        assertEquals(
                "[tcgaInvalidVcfProcessLogHeaderMissingBracketsInMapValues.vcf] VCF header validation error: header 'vcfProcessLog': " +
                        "'InputVCFgeneAnno' tag is missing closing angle bracket (>) in '(anno1.gaf)' value.",
                context.getErrors().get(1));
        assertEquals(
                "[tcgaInvalidVcfProcessLogHeaderMissingBracketsInMapValues.vcf] VCF header validation error: header 'vcfProcessLog': " +
                        "'InputVCFVer' tag has < or > inside '<1.0, InputVCFParam=<a1,c2>' value besides the requested opening and closing brackets.",
                context.getErrors().get(2));
        assertEquals(
                "[tcgaInvalidVcfProcessLogHeaderMissingBracketsInMapValues.vcf] VCF header validation error: header 'vcfProcessLog': " +
                        "'InputVCFSource' tag is missing opening angle bracket (<) in 'varCaller1' value.",
                context.getErrors().get(3));
        assertEquals(
                "[tcgaInvalidVcfProcessLogHeaderMissingBracketsInMapValues.vcf] VCF header validation error: header 'vcfProcessLog': " +
                        "'InputVCFSource' tag is missing closing angle bracket (>) in 'varCaller1' value.",
                context.getErrors().get(4));
    }

    @Test
    public void testVcfProcessLogInvalidNumberOfValuesPerTag() throws Exception {

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_VCFPROCESSLOG_NUMBER_OF_VALUES_PER_TAG_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals(
                "[tcgaInvalidVcfProcessLogHeaderNumberOfValuesPerTag.vcf] VCF header validation error: " +
                        "header 'vcfProcessLog' tags (other than 'Merge*') don't have the same number of values.",
                context.getErrors().get(0));
    }

    @Test
    public void testVcfProcessLogInvalidDuplicatesInMultipleValues() throws Exception {

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_VCFPROCESSLOG_DUPLICATES_IN_MULTIPLE_VALUES_FILE), context));
        assertEquals(2, context.getErrorCount());
        assertEquals(
                "[tcgaInvalidVcfProcessLogHeaderDuplicatesInMultipleValues.vcf] VCF header validation error: header 'vcfProcessLog' : " +
                        "tag 'InputVCFSource' has duplicate values ('varCaller2')",
                context.getErrors().get(0));
        assertEquals(
                "[tcgaInvalidVcfProcessLogHeaderDuplicatesInMultipleValues.vcf] VCF header validation error: header 'vcfProcessLog' : " +
                        "tag 'InputVCF' has duplicate values ('file1.vcf')",
                context.getErrors().get(1));
    }

    @Test
    public void testVcfProcessLogUsingWrongSeparatorForCommaDelimitedValues() throws Exception {

        // Note that since there are no list of forbidden characters in multiple values, if no comma is found,
        // it will treat the multiple values as 1 value. If only 1 value is found across all tags this is not breaking any rules
        // and no errors will be generated. The same goes for any number of values, as long as they are consistent across all tags.
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_VCFPROCESSLOG_USING_WRONG_SEPARATOR_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals(
                "[tcgaInvalidVcfProcessLogHeaderUsingWrongSeparator.vcf] VCF header validation error: " +
                        "header 'vcfProcessLog' tags (other than 'Merge*') don't have the same number of values.",
                context.getErrors().get(0));
    }

    @Test
    public void testVcfProcessLogWithWhitespaceValues() throws Exception {

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_VCFPROCESSLOG_WITH_WHITESPACE_IN_VALUES_FILE), context));
        assertEquals(3, context.getErrorCount());
        assertEquals(
                "[tcgaValidVcfProcessLogHeaderWithWhitespace.vcf] VCF header validation error: header 'vcfProcessLog' : " +
                        "tag 'InputVCFSource' has no value or contains only whitespace '')",
                context.getErrors().get(0));
        assertEquals(
                "[tcgaValidVcfProcessLogHeaderWithWhitespace.vcf] VCF header validation error: header 'vcfProcessLog' : " +
                        "tag 'InputVCF' has leading and/or trailing space in 'file1.vcf ')",
                context.getErrors().get(1));
        assertEquals(
                "[tcgaValidVcfProcessLogHeaderWithWhitespace.vcf] VCF header validation error: header 'vcfProcessLog' : " +
                        "tag 'InputVCFVer' has no value or contains only whitespace ' ')",
                context.getErrors().get(2));
    }

    @Test
    public void testVcfProcessLogMissingIdentifierInMerge() throws Exception {

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_VCFPROCESSLOG_MISSING_IDENTIFIER_IN_MERGE_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testVcfProcessLogOneInputVCFSubFieldWithNoMergeTags() throws Exception {

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_VCFPROCESSLOG_ONE_INPUTVCF_SUBFIELD_WITH_NO_MERGE_TAGS_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testVcfProcessLogOneInputVCFSubFieldWithMissingIdentifierInMergeTags() throws Exception {

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_VCFPROCESSLOG_ONE_INPUTVCF_SUBFIELD_WITH_MISSING_IDENTIFIER_IN_MERGE_TAGS_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testVcfProcessLogOneInputVCFSubFieldWithMultipleSubFieldsInMergeTags() throws Exception {

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_VCFPROCESSLOG_ONE_INPUTVCF_SUBFIELD_WITH_MULTIPLE_SUBFIELDS_IN_MERGE_TAGS_FILE), context));
        assertEquals(3, context.getErrorCount());
        assertEquals(
                "[tcgaInvalidVcfProcessLogHeaderOneInputVCFSubFieldWithMultipleSubFieldsInMergeTags.vcf] VCF header validation error: header 'vcfProcessLog' : " +
                        "tag 'InputVCF' has only 1 value, but tag 'MergeContact' has 1 value that is not the missing identifier ('.').",
                context.getErrors().get(0));
        assertEquals(
                "[tcgaInvalidVcfProcessLogHeaderOneInputVCFSubFieldWithMultipleSubFieldsInMergeTags.vcf] VCF header validation error: header 'vcfProcessLog' : " +
                        "tag 'InputVCF' has only 1 value, but tag 'MergeParam' has 1 value or more (Found 2).",
                context.getErrors().get(1));
        assertEquals(
                "[tcgaInvalidVcfProcessLogHeaderOneInputVCFSubFieldWithMultipleSubFieldsInMergeTags.vcf] VCF header validation error: header 'vcfProcessLog' : " +
                        "tag 'InputVCF' has only 1 value, but tag 'MergeSoftware' has 1 value or more (Found 2).",
                context.getErrors().get(2));
    }

    @Test
    public void testVcfProcessLogMissingMergeTags() throws Exception {

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_VCFPROCESSLOG_MISSING_MERGE_TAGS_FILE), context));
        assertEquals(4, context.getErrorCount());
        assertEquals(
                "[tcgaInvalidVcfProcessLogHeaderMissingMergeTags.vcf] VCF header validation error: header 'vcfProcessLog' : " +
                        "tag 'InputVCF' has multiple values, but tag 'MergeSoftware' is missing.",
                context.getErrors().get(0));
        assertEquals(
                "[tcgaInvalidVcfProcessLogHeaderMissingMergeTags.vcf] VCF header validation error: header 'vcfProcessLog' : " +
                        "tag 'InputVCF' has multiple values, but tag 'MergeParam' is missing.",
                context.getErrors().get(1));
        assertEquals(
                "[tcgaInvalidVcfProcessLogHeaderMissingMergeTags.vcf] VCF header validation error: header 'vcfProcessLog' : " +
                        "tag 'InputVCF' has multiple values, but tag 'MergeVer' is missing.",
                context.getErrors().get(2));
        assertEquals(
                "[tcgaInvalidVcfProcessLogHeaderMissingMergeTags.vcf] VCF header validation error: header 'vcfProcessLog' : " +
                        "tag 'InputVCF' has multiple values, but tag 'MergeContact' is missing.",
                context.getErrors().get(3));
    }

    @Test
    public void testVcfProcessLogMissingMergeTagsButOne() throws Exception {

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_VCFPROCESSLOG_MISSING_MERGE_TAGS_BUT_ONE_FILE), context));
        assertEquals(3, context.getErrorCount());
        assertEquals(
                "[tcgaInvalidVcfProcessLogHeaderMissingMergeTagsButOne.vcf] VCF header validation error: header 'vcfProcessLog' : " +
                        "tag 'InputVCF' has multiple values, but tag 'MergeParam' is missing.",
                context.getErrors().get(0));
        assertEquals(
                "[tcgaInvalidVcfProcessLogHeaderMissingMergeTagsButOne.vcf] VCF header validation error: header 'vcfProcessLog' : " +
                        "tag 'InputVCF' has multiple values, but tag 'MergeVer' is missing.",
                context.getErrors().get(1));
        assertEquals(
                "[tcgaInvalidVcfProcessLogHeaderMissingMergeTagsButOne.vcf] VCF header validation error: header 'vcfProcessLog' : " +
                        "tag 'InputVCF' has multiple values, but tag 'MergeContact' is missing.",
                context.getErrors().get(2));
    }

    @Test
    public void testVcfProcessLogMissingMergeParamSubFields() throws Exception {

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_VCFPROCESSLOG_MISSING_MERGEPARAM_SUBFIELDS_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals(
                "[tcgaInvalidVcfProcessLogHeaderMultipleInputVCFSubFieldWithMissingSubFieldsInMergeParam.vcf] VCF header validation error: header 'vcfProcessLog' : " +
                        "'Merge*' tags don't have the same number of values.",
                context.getErrors().get(0));
    }

    @Test
    public void testVcfProcessLogMissingMergeParamAndMergeVerSubFields() throws Exception {

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_VCFPROCESSLOG_MISSING_MERGEPARAM_AND_MERGEVER_SUBFIELDS_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals(
                "[tcgaInvalidVcfProcessLogHeaderMultipleInputVCFSubFieldWithMissingSubFieldsInMergeParamAndMergeVer.vcf] VCF header validation error: " +
                        "header 'vcfProcessLog' : 'Merge*' tags don't have the same number of values.",
                context.getErrors().get(0));
    }

    @Test
    public void testVcfProcessLogUnexpectedMergeParamAndMergeVerSubFields() throws Exception {

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_VCFPROCESSLOG_UNEXPECTED_MERGEPARAM_AND_MERGEVER_SUBFIELDS_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals(
                "[tcgaInvalidVcfProcessLogHeaderMultipleInputVCFSubFieldWithUnexpectedSubFieldsInMergeParamAndMergeVer.vcf] VCF header validation error: " +
                        "header 'vcfProcessLog' : 'Merge*' tags don't have the same number of values.",
                context.getErrors().get(0));
    }

    @Test
    public void testMissingPhasing() throws Exception {
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_MISSING_PHASING_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals(
                "[tcgaMissingPhasing.vcf] VCF header validation error: header 'phasing' is missing.",
                context.getErrors().get(0));
    }

    @Test
    public void testWrongIndividual() throws Exception {
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_INDIVIDUAL_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals(
                "[tcgaWrongIndividual.vcf] INDIVIDUAL header value must be a patient barcode.",
                context.getErrors().get(0));
    }

    @Test
    public void testPhasingNoValues() throws Exception {
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_PHASING_NO_VALUE_FILE), context));
        assertEquals(2, context.getErrorCount());
        assertEquals(
                "[tcgaPhasingNoValue.vcf] phasing header on line 7 value '' is invalid: may not contain spaces, "
                        + "equals signs, commas, or semi-colons unless surrounded by double quotes",
                context.getErrors().get(0));
        assertEquals(
                "[tcgaPhasingNoValue.vcf] phasing header must contain values 'partial' or 'none'",
                context.getErrors().get(1));
    }

    @Test
    public void testPhasingBadValues() throws Exception {
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_PHASING_BAD_VALUE_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals(
                "[tcgaPhasingBadValue.vcf] phasing header must contain values 'partial' or 'none'",
                context.getErrors().get(0));
    }

    @Test
    public void testPhasingKeyMapValues() throws Exception {
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_PHASING_KEYMAP_VALUE_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals(
                "[tcgaPhasingKeyMapValue.vcf] phasing header must contain values 'partial' or 'none'",
                context.getErrors().get(0));
    }

    @Test
    public void testPhasingQuotedValues() throws Exception {
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_PHASING_QUOTED_VALUE_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals(
                "[tcgaPhasingQuotedValue.vcf] phasing header must contain values 'partial' or 'none'",
                context.getErrors().get(0));
    }

    @Test
    public void testPhasingGoodPartialValue() throws Exception {
        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_PHASING_PARTIAL_VALUE_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testMissingFileDate() throws Exception {
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_MISSING_FILEDATE_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals(
                "[tcgaMissingFileDate.vcf] VCF header validation error: header 'fileDate' is missing.",
                context.getErrors().get(0));
    }

    @Test
    public void testMissingReference() throws Exception {
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_MISSING_REFERENCE_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals(
                "[tcgaMissingReference.vcf] VCF header validation error: header 'reference' is missing.",
                context.getErrors().get(0));
    }

    @Test
    public void testReferenceNoValues() throws Exception {
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_REFERENCE_NO_VALUE_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals(
                "[tcgaReferenceNoValue.vcf] reference header on line 6 value '' is invalid: may not contain spaces, "
                        + "equals signs, commas, or semi-colons unless surrounded by double quotes",
                context.getErrors().get(0));
    }

    @Test
    public void testReferenceEmptyValues() throws Exception {
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_REFERENCE_EMPTY_VALUE_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals(
                "[tcgaReferenceEmptyValue.vcf] reference header must not contain empty keys: <>",
                context.getErrors().get(0));
    }

    @Test
    public void testReferenceInvalidValues() throws Exception {
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_REFERENCE_INVALID_VALUE_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals(
                "[tcgaReferenceInvalidValue.vcf] reference header must contain keys: <ID, Source>",
                context.getErrors().get(0));
    }

    @Test
    public void testReferenceIgnoreCaseValues() throws Exception {
        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_REFERENCE_IGNORE_CASE_VALUE_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidWithGeneInfoId() throws Exception {

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_WITH_GENE_INFO_DATA_ID_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidWithoutGeneInfoId() throws Exception {

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_WITHOUT_GENE_INFO_DATA_ID_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidWithGeneInfoHeaderWithoutGeneInfoId() throws Exception {

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_WITH_GENE_INFO_HEADER_WITHOUT_GENE_INFO_DATA_ID_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testInvalidMissingGeneAnnoHeaderWithGeneInfoId() throws Exception {

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_MISSING_GENE_ANNO_HEADER_WITH_GENE_INFO_DATA_ID_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals(
                "[tcgaInvalidMissingGeneAnnoHeaderWithGeneInfoDataId.vcf] VCF header validation error: header 'geneAnno' is missing.",
                context.getErrors().get(0));
    }

    @Test
    public void testMissingGeneAnnoAPPS4217() throws Processor.ProcessorException {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY, "tcga/CEU.exon.2010_03.genotypes.vcf"), context));
        assertEquals(context.getErrors().toString(), 1, context.getErrorCount());
        assertEquals("[CEU.exon.2010_03.genotypes.vcf] VCF header validation error: header 'geneAnno' is missing.", context.getErrors().get(0));
    }

    @Test
    public void testInvalidBlankGeneAnnoHeaderWithGeneInfoId() throws Exception {

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_BLANK_GENE_ANNO_HEADER_WITH_GENE_INFO_DATA_ID_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals(
                "[tcgaInvalidBlankGeneAnnoHeaderWithGeneInfoDataId.vcf] VCF header validation error: header 'geneAnno' has no value.",
                context.getErrors().get(0));
    }

    @Test
    public void testValidCenterHeaderDoubleQuoted() throws Exception {
        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_CENTER_HEADER_DOUBLEQUOTED_FILE), context));
    }

    @Test
    public void testValidCenterHeaderNoWhiteSpace() throws Exception {
        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_CENTER_HEADER_NOWHITESPACE_FILE), context));
    }

    @Test
    public void testInvalidCenterHeaderWhiteSpace() throws Exception {
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_CENTER_HEADER_WHITESPACE_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInvalidCenterHeaderWhiteSpace.vcf] VCF header validation error: header 'center' has incorrect value. Value must be enclosed in double quotes or have no white space.", context.getErrors().get(0));
    }

    @Test
    public void testInvalidCenterHeaderMultiDoubleQuotes() throws Exception {
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_CENTER_HEADER_MULTIDOUBLEQUOTED_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInvalidCenterHeaderMultiDoubleQuoted.vcf] VCF header validation error: header 'center' has incorrect value. Value must be enclosed in double quotes or have no white space.", context.getErrors().get(0));
    }

    @Test
    public void testInvalidCenterHeaderNoCenter() throws Exception {
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_CENTER_HEADER_NOCENTER_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInvalidCenterHeaderNoCenter.vcf] VCF header validation error: header 'center' is missing.", context.getErrors().get(0));
    }

    @Test
    public void testValidSampleHeader() throws Exception {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_SAMPLE_HEADER_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidSampleHeaderStandalone() throws Exception {

        context.setStandaloneValidator(true);

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).isUUID("TCGA-06-0881-10A-01W");
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).batchValidate(with(any(List.class)), with(any(QcContext.class)), with("tcgaValidSampleHeader.vcf"), with(true));
            will(returnValue(true));
        }});

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_SAMPLE_HEADER_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidSampleHeaderCenterConverted() throws Exception {

        context.setCenterConvertedToUUID(true);

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";
        final MetaDataBean metadata = new MetaDataBean();
        metadata.setTssCode("06");
        metadata.setParticipantCode("0881");
        metadata.setProjectCode("TCGA");

        mockery.checking(new Expectations() {{

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateBarcodeFormat(with(barcode), with(any(String.class)), with("Aliquot"));
            will(returnValue(null));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).getMetadata(uuid);
            will(returnValue(metadata));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).getPatientBarcode(barcode);
            will(returnValue("TCGA-06-0881"));
        }});

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_SAMPLE_HEADER_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidSampleHeaderCenterConvertedStandalone() throws Exception {

        context.setCenterConvertedToUUID(true);
        context.setStandaloneValidator(true);

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";
        final MetaDataBean metadata = new MetaDataBean();
        metadata.setTssCode("06");
        metadata.setParticipantCode("0881");
        metadata.setProjectCode("TCGA");

        mockery.checking(new Expectations() {{

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateBarcodeFormat(with(barcode), with(any(String.class)), with("Aliquot"));
            will(returnValue(null));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).batchValidateSampleUuidAndSampleTcgaBarcode(with(any(List.class)), with(any(QcContext.class)));
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).getMetadata(uuid);
            will(returnValue(metadata));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).getPatientBarcode(barcode);
            will(returnValue("TCGA-06-0881"));
        }});

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_SAMPLE_HEADER_FILE), context));
        assertEquals(context.getErrors().toString(), 0, context.getErrorCount());
    }

    @Test
    public void testInvalidSampleHeaderNotValueMap() throws Exception {
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_SAMPLE_HEADER_NOTVALEUMAP_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInValidSampleHeaderNotValueMap.vcf] VCF header validation error: header 'SAMPLE' must be in the format <key1=value1,key2=value2,...>.", context.getErrors().get(0));
    }

    @Test
    public void testInvalidSampleHeaderNoIdKey() throws Exception {
        context.setCenterConvertedToUUID(false);
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_SAMPLE_HEADER_NOIDKEY_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInValidSampleHeaderNoIdKey.vcf] VCF header validation error: header 'SAMPLE' must have all of 'ID', 'SampleName', 'Individual', 'File', 'Platform', 'Source', 'Accession' keys.", context.getErrors().get(0));
    }

    @Test
    public void testInvalidSampleHeaderNoIdKeyCenterConverted() throws Exception {

        context.setCenterConvertedToUUID(true);
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_SAMPLE_HEADER_NOIDKEY_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInValidSampleHeaderNoIdKey.vcf] VCF header validation error: header 'SAMPLE' must have all of 'ID', 'SampleUUID', 'SampleTCGABarcode', 'File', 'Platform', 'Source', 'Accession' keys.", context.getErrors().get(0));
    }

    @Test
    public void testValidSampleHeaderNoIndividualKey() throws Exception {

        context.setCenterConvertedToUUID(false);

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_SAMPLE_HEADER_NOINDIVIDUALKEY_FILE), context));
        assertEquals(1, context.getErrorCount());

        final String error = context.getErrors().get(0);
        assertNotNull(error);
        assertEquals("[tcgaValidSampleHeaderNoIndividualKey.vcf] VCF header validation error: header 'SAMPLE' must have all of 'ID', 'SampleName', 'Individual', 'File', 'Platform', 'Source', 'Accession' keys.", error);
    }

    @Test
    public void testValidSampleHeaderNoIndividualKeyCenterConverted() throws Exception {

        context.setCenterConvertedToUUID(true);

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";
        final MetaDataBean metadata = new MetaDataBean();
        metadata.setTssCode("06");
        metadata.setParticipantCode("0881");
        metadata.setProjectCode("TCGA");

        mockery.checking(new Expectations() {{

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateBarcodeFormat(with(barcode), with(any(String.class)), with("Aliquot"));
            will(returnValue(null));

            one(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
            one(mockBarcodeAndUUIDValidatorImpl).getMetadata(uuid);
            will(returnValue(metadata));
        }});

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_SAMPLE_HEADER_NOINDIVIDUALKEY_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testInvalidSampleHeaderNoFileKey() throws Exception {

        context.setCenterConvertedToUUID(false);

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_SAMPLE_HEADER_NOFILEKEY_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInValidSampleHeaderNoFileKey.vcf] VCF header validation error: header 'SAMPLE' must have all of 'ID', 'SampleName', 'Individual', 'File', 'Platform', 'Source', 'Accession' keys.", context.getErrors().get(0));
    }

    @Test
    public void testInvalidSampleHeaderNoFileKeyCenterConverted() throws Exception {

        context.setCenterConvertedToUUID(true);

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_SAMPLE_HEADER_NOFILEKEY_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInValidSampleHeaderNoFileKey.vcf] VCF header validation error: header 'SAMPLE' must have all of 'ID', 'SampleUUID', 'SampleTCGABarcode', 'File', 'Platform', 'Source', 'Accession' keys.", context.getErrors().get(0));
    }

    @Test
    public void testInvalidSampleHeaderNoPlatformKey() throws Exception {

        context.setCenterConvertedToUUID(false);

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_SAMPLE_HEADER_NOPLATFORMKEY_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInValidSampleHeaderNoPlatformKey.vcf] VCF header validation error: header 'SAMPLE' must have all of 'ID', 'SampleName', 'Individual', 'File', 'Platform', 'Source', 'Accession' keys.", context.getErrors().get(0));
    }

    @Test
    public void testInvalidSampleHeaderNoPlatformKeyCenterConverted() throws Exception {

        context.setCenterConvertedToUUID(true);

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_SAMPLE_HEADER_NOPLATFORMKEY_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInValidSampleHeaderNoPlatformKey.vcf] VCF header validation error: header 'SAMPLE' must have all of 'ID', 'SampleUUID', 'SampleTCGABarcode', 'File', 'Platform', 'Source', 'Accession' keys.", context.getErrors().get(0));
    }

    @Test
    public void testInvalidSampleHeaderNoSourceKey() throws Exception {

        context.setCenterConvertedToUUID(false);

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_SAMPLE_HEADER_NOSOURCEKEY_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInValidSampleHeaderNoSourceKey.vcf] VCF header validation error: header 'SAMPLE' must have all of 'ID', 'SampleName', 'Individual', 'File', 'Platform', 'Source', 'Accession' keys.", context.getErrors().get(0));
    }

    @Test
    public void testInvalidSampleHeaderNoSourceKeyCenterConverted() throws Exception {

        context.setCenterConvertedToUUID(true);

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_SAMPLE_HEADER_NOSOURCEKEY_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInValidSampleHeaderNoSourceKey.vcf] VCF header validation error: header 'SAMPLE' must have all of 'ID', 'SampleUUID', 'SampleTCGABarcode', 'File', 'Platform', 'Source', 'Accession' keys.", context.getErrors().get(0));
    }

    @Test
    public void testInvalidSampleHeaderNoAccessionKey() throws Exception {

        context.setCenterConvertedToUUID(false);

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_SAMPLE_HEADER_NOACCESSIONKEY_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInValidSampleHeaderNoAccessionKey.vcf] VCF header validation error: header 'SAMPLE' must have all of 'ID', 'SampleName', 'Individual', 'File', 'Platform', 'Source', 'Accession' keys.", context.getErrors().get(0));
    }

    @Test
    public void testInvalidSampleHeaderNoAccessionKeyCenterConverted() throws Exception {

        context.setCenterConvertedToUUID(true);

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_SAMPLE_HEADER_NOACCESSIONKEY_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInValidSampleHeaderNoAccessionKey.vcf] VCF header validation error: header 'SAMPLE' must have all of 'ID', 'SampleUUID', 'SampleTCGABarcode', 'File', 'Platform', 'Source', 'Accession' keys.", context.getErrors().get(0));
    }

    @Test
    public void testInvalidSampleHeaderNoAngleBrackets() throws Exception {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_SAMPLE_HEADER_NOANGLEBRACKETS_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInValidSampleHeaderNoAngleBrackets.vcf] VCF header validation error: header 'SAMPLE' must have 'Genomes' enclosed within angular brackets and cannot contain whitespace or nested angular brackets.", context.getErrors().get(0));
    }

    @Test
    public void testInvalidSampleHeaderNumValues() throws Exception {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_SAMPLE_HEADER_NUMVALUES_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInValidSampleHeaderNumValues.vcf] VCF header validation error: header 'SAMPLE' must have same number of tokens for Genomes, Mixture and Genome_Description.", context.getErrors().get(0));
    }

    @Test
    public void testInvalidSampleHeaderMisMatchedAngleBrackets() throws Exception {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_SAMPLE_HEADER_MISMATCHEDANGLEBRACKETS_FILE), context));
        assertEquals(3, context.getErrorCount());
        assertEquals("[tcgaInValidSampleHeaderMisMatchedAngleBrackets.vcf] VCF header validation error: SAMPLE header value '<\"Germline contamination>' for key 'Genome_Description' contains whitespace and must be enclosed in double quotes", context.getErrors().get(0));
        assertEquals("[tcgaInValidSampleHeaderMisMatchedAngleBrackets.vcf] VCF header validation error: header 'SAMPLE' must have 'Genome_Description' enclosed within angular brackets and the values must be enclosed in double quotes.", context.getErrors().get(1));
        assertEquals("[tcgaInValidSampleHeaderMisMatchedAngleBrackets.vcf] Header SAMPLE: Value map on line 8 is improperly formatted near ',\"Tumor genome\"'", context.getErrors().get(2));
    }

    @Test
    public void testInvalidSampleHeaderMixtureValue() throws Exception {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_SAMPLE_HEADER_INVALID_MIXTURE_VALUE_FILE), context));
        assertEquals(2, context.getErrorCount());
        assertEquals("[tcgaInValidSampleHeaderMixtureValue.vcf] VCF header validation error: header 'SAMPLE' has an invalid floating point number value 'foo' in 'Mixture'", context.getErrors().get(0));
    }

    @Test
    public void testInvalidSampleHeaderMixtureValueSum() throws Exception {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_SAMPLE_HEADER_INVALID_MIXTURE_VALUE_SUM_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInValidSampleHeaderMixtureValueSum.vcf] VCF header validation error: header 'SAMPLE' must have all values in '0.2,0.9' sum upto 1.0 in 'Mixture'", context.getErrors().get(0));
    }

    @Test
    public void testInvalidSampleHeaderGenomeDescriptionValues() throws Exception {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_SAMPLE_HEADER_GENOME_DESCRIPTION_VALUE_FILE), context));
        assertEquals(3, context.getErrorCount());
        assertEquals("[tcgaInValidSampleHeaderGenomeDescriptionValues.vcf] VCF header validation error: SAMPLE header value 'Germline contamination' for key 'Genome_Description' contains whitespace and must be enclosed in double quotes", context.getErrors().get(0));
        assertEquals("[tcgaInValidSampleHeaderGenomeDescriptionValues.vcf] VCF header validation error: SAMPLE header value 'Tumor genome' for key 'Genome_Description' contains whitespace and must be enclosed in double quotes", context.getErrors().get(1));
        assertEquals("[tcgaInValidSampleHeaderGenomeDescriptionValues.vcf] VCF header validation error: header 'SAMPLE' must have 'Genome_Description' enclosed within angular brackets and the values must be enclosed in double quotes.", context.getErrors().get(2));
    }

    @Test
    public void testInvalidSampleHeaderColumnNames() throws Exception {

        context.setCenterConvertedToUUID(false);

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_SAMPLE_HEADER_COLUMN_NAMES_FILE), context));
        assertEquals(3, context.getErrorCount());
        assertEquals("[tcgaInValidSampleHeaderColumnNames.vcf] Column header contains sample column name 'PRIMARY' that does not have a corresponding SAMPLE header", context.getErrors().get(0));
        assertEquals("[tcgaInValidSampleHeaderColumnNames.vcf] VCF header validation error: header 'SAMPLE' must be in the format <key1=value1,key2=value2,...>.", context.getErrors().get(1));
        assertEquals("[tcgaInValidSampleHeaderColumnNames.vcf] VCF header validation error: header 'SAMPLE' must have all of 'ID', 'SampleName', 'Individual', 'File', 'Platform', 'Source', 'Accession' keys.", context.getErrors().get(2));
    }

    @Test
    public void testInvalidSampleHeaderColumnNamesCenterConverted() throws Exception {

        context.setCenterConvertedToUUID(true);

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_SAMPLE_HEADER_COLUMN_NAMES_FILE), context));
        assertEquals(3, context.getErrorCount());
        assertEquals("[tcgaInValidSampleHeaderColumnNames.vcf] Column header contains sample column name 'PRIMARY' that does not have a corresponding SAMPLE header", context.getErrors().get(0));
        assertEquals("[tcgaInValidSampleHeaderColumnNames.vcf] VCF header validation error: header 'SAMPLE' must be in the format <key1=value1,key2=value2,...>.", context.getErrors().get(1));
        assertEquals("[tcgaInValidSampleHeaderColumnNames.vcf] VCF header validation error: header 'SAMPLE' must have all of 'ID', 'SampleUUID', 'SampleTCGABarcode', 'File', 'Platform', 'Source', 'Accession' keys.", context.getErrors().get(2));
    }

    @Test
    public void testValidDpValues() throws Exception {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_DP_VALUES_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidDpValuesNoInfoDp() throws Exception {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_DP_VALUES_NOINFODP_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testInvalidDpValues() throws Exception {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_DP_VALUES_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInvalidDPValues.vcf] VCF data validation error on line 21: INFO value ' DP of 10 and sample DP total of 15' is not valid. The value for DP in INFO column and the total of the values for DP in the sample columns must be equal if DP is specified in FORMAT column", context.getErrors().get(0));
    }

    @Test
    public void testInvalidDpValuesLess() throws Exception {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_DP_VALUES_LESS_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInvalidDPValuesLess.vcf] VCF data validation error on line 21: INFO value ' DP of 10 and sample DP total of 7' is not valid. The value for DP in INFO column and the total of the values for DP in the sample columns must be equal if DP is specified in FORMAT column", context.getErrors().get(0));
    }

    @Test
    public void testInvalidDpValuesMoreSamples() throws Exception {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_DP_VALUES_MORE_SAMPLES_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInvalidDPValuesMoreSamples.vcf] Column header contains sample column name 'S5' that does not have a corresponding SAMPLE header", context.getErrors().get(0));
    }

    @Test
    public void testInvalidSvaltValuesChromIdFormat1() throws Exception {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_SVALT_VALUES_CHROMID_FORMAT1_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInValidSvaltValuesChromIdFormat1.vcf] VCF header validation error: header 'assembly' is missing.", context.getErrors().get(0));
    }

    @Test
    public void testInvalidSvaltValuesChromIdFormat2() throws Exception {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_SVALT_VALUES_CHROMID_FORMAT2_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInValidSvaltValuesChromIdFormat2.vcf] VCF header validation error: header 'assembly' is missing.", context.getErrors().get(0));
    }

    @Test
    public void testInvalidSvaltValuesChromIdFormat3() throws Exception {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_SVALT_VALUES_CHROMID_FORMAT3_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInValidSvaltValuesChromIdFormat3.vcf] VCF header validation error: header 'assembly' is missing.", context.getErrors().get(0));
    }

    @Test
    public void testInvalidSvaltValuesChromIdFormat4() throws Exception {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_SVALT_VALUES_CHROMID_FORMAT4_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInValidSvaltValuesChromIdFormat4.vcf] VCF header validation error: header 'assembly' is missing.", context.getErrors().get(0));
    }

    @Test
    public void testValidSvaltValuesChromId() throws Exception {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_SVALT_VALUES_CHROMID_FILE), context));
        assertEquals(context.getErrors().toString(), 0, context.getErrorCount());
    }

    @Test
    public void testValidSvaltValuesNoChromId() throws Exception {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_SVALT_VALUES_NO_CHROMID_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidFormatIdTE() throws Exception {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            exactly(3).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(3).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(3).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_FORMATID_TE_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testInValidFormatIdTE() throws Exception {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_FORMATID_TE_FILE), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[tcgaInValidFormatIdTE.vcf] VCF data validation error on line 19: SAMPLE #1 value 'MIL' is not valid. Should be one of [SIL,MIS,NSNS,NSTP,FSH,NA]", context.getErrors().get(0));
    }

    @Test
    public void testValidIfnoRGNValues() throws Exception {
        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_INFO_RGN_VALUES_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testInValidIfnoRGNValues() throws Exception {
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_INFO_RGN_VALUES_FILE), context));
        assertEquals(6, context.getErrorCount());
        assertEquals("[tcgaInValidInfoRGNValues.vcf] VCF data validation error on line 13: INFO value 'RGN' is not valid. is RGN and should be in 5_utr, 3_utr, exon, intron, ncds, sp but found '5_ur'", context.getErrors().get(0));
        assertEquals("[tcgaInValidInfoRGNValues.vcf] VCF data validation error on line 14: INFO value 'RGN' is not valid. is RGN and should be in 5_utr, 3_utr, exon, intron, ncds, sp but found '5_utr,3_utr,xon,intron,ncds,sp'", context.getErrors().get(1));
        assertEquals("[tcgaInValidInfoRGNValues.vcf] VCF data validation error on line 15: INFO value 'RGN' is not valid. is RGN and should be in 5_utr, 3_utr, exon, intron, ncds, sp but found '5_utr,5_ut'", context.getErrors().get(2));
        assertEquals("[tcgaInValidInfoRGNValues.vcf] VCF data validation error on line 16: INFO value 'RGN' is not valid. String cannot contain whitespace, semi-colon, or quote, but found ' 5_utr'", context.getErrors().get(3));
        assertEquals("[tcgaInValidInfoRGNValues.vcf] VCF data validation error on line 16: INFO value 'RGN' is not valid. is RGN and should be in 5_utr, 3_utr, exon, intron, ncds, sp but found '5_utr, 5_utr'", context.getErrors().get(4));
        assertEquals("[tcgaInValidInfoRGNValues.vcf] VCF data validation error on line 13: INFO value 'RGN' is not valid. is RGN and should be in 5_utr, 3_utr, exon, intron, ncds, sp but found '5_ur'", context.getErrors().get(0));
    }

    @Test
    public void testValidateSampleHeaderValuesValid() throws Exception {

        context.setStandaloneValidator(false);
        context.setFile(new File("test.txt"));

        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Genomes", "<G1,G2>");
            put("Mixture", "<0.088,0.912>");
            put("Genome_Description", "<\"This is G1\",\"This is G2\">");
            put("SampleUUID", "00000000-0000-0000-0000-000000000000");
        }};

        mockery.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID("00000000-0000-0000-0000-000000000000");
            will(returnValue(true));
        }});

        assertTrue(vcfValidator.validateSampleHeaderValues(valueMap, "SAMPLE", context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidateSampleHeaderValuesPostUuidTransitionValid() throws Exception {

        context.setStandaloneValidator(false);
        context.setCenterConvertedToUUID(true);
        context.setFile(new File("test.txt"));

        final String sampleUuid = "00000000-0000-0000-0000-000000000000";
        final String sampleTcgaBarcode = "a-valid-barcode";
        final MetaDataBean metadata = new MetaDataBean();
        metadata.setTssCode("06");
        metadata.setParticipantCode("0881");
        metadata.setProjectCode("TCGA");

        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Genomes", "<G1,G2>");
            put("Mixture", "<0.088,0.912>");
            put("Genome_Description", "<\"This is G1\",\"This is G2\">");
            put("SampleUUID", sampleUuid);
            put("SampleTCGABarcode", sampleTcgaBarcode);
            put("MetadataResource", "https://tcga-data.nci.nih.gov/uuid/uuidws/mapping/json/uuid/" + sampleUuid);
        }};

        mockery.checking(new Expectations() {{

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateBarcodeFormat(with(sampleTcgaBarcode), with(any(String.class)), with("Aliquot"));
            will(returnValue(null));

            one(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(sampleUuid);
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(sampleUuid, sampleTcgaBarcode);
            will(returnValue(true));
            one(mockBarcodeAndUUIDValidatorImpl).getMetadata(sampleUuid);
            will(returnValue(metadata));
        }});

        assertTrue(vcfValidator.validateSampleHeaderValuesPostUuidTransition(null, valueMap, "SAMPLE", context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidateSampleHeaderValuesInValidAngularBrackets() throws Exception {
        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Genomes", "G1,G2>");
            put("Mixture", "M<1>,M2");
            put("Genome_Description", "\"This is G1\",\"This is G2\"");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValues(valueMap, "SAMPLE", context));
        assertEquals(6, context.getErrorCount());
        assertEquals("VCF header validation error: SAMPLE header value 'G1,G2>' for key 'Genomes' is comma separated, and must be contained within angle '<>' brackets.", context.getErrors().get(0));
        assertEquals("VCF header validation error: SAMPLE header value 'M<1>,M2' for key 'Mixture' is comma separated, and must be contained within angle '<>' brackets.", context.getErrors().get(1));
        assertEquals("VCF header validation error: SAMPLE header value '\"This is G1\",\"This is G2\"' for key 'Genome_Description' is comma separated, and must be contained within angle '<>' brackets.", context.getErrors().get(2));
        assertEquals("VCF header validation error: header 'SAMPLE' must have 'Genomes' enclosed within angular brackets and cannot contain whitespace or nested angular brackets.", context.getErrors().get(3));
        assertEquals("VCF header validation error: header 'SAMPLE' must have 'Mixture' enclosed within angular brackets.", context.getErrors().get(4));
        assertEquals("VCF header validation error: header 'SAMPLE' must have 'Genome_Description' enclosed within angular brackets and the values must be enclosed in double quotes.", context.getErrors().get(5));
    }

    @Test
    public void testValidateSampleHeaderValuesPostUuidTransitionInValidAngularBrackets() throws Exception {

        context.setCenterConvertedToUUID(true);

        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Genomes", "G1,G2>");
            put("Mixture", "M<1>,M2");
            put("Genome_Description", "\"This is G1\",\"This is G2\"");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValuesPostUuidTransition(null, valueMap, "SAMPLE", context));
        assertEquals(6, context.getErrorCount());
        assertEquals("VCF header validation error: SAMPLE header value 'G1,G2>' for key 'Genomes' is comma separated, and must be contained within angle '<>' brackets.", context.getErrors().get(0));
        assertEquals("VCF header validation error: SAMPLE header value 'M<1>,M2' for key 'Mixture' is comma separated, and must be contained within angle '<>' brackets.", context.getErrors().get(1));
        assertEquals("VCF header validation error: SAMPLE header value '\"This is G1\",\"This is G2\"' for key 'Genome_Description' is comma separated, and must be contained within angle '<>' brackets.", context.getErrors().get(2));
        assertEquals("VCF header validation error: header 'SAMPLE' must have 'Genomes' enclosed within angular brackets and cannot contain whitespace or nested angular brackets.", context.getErrors().get(3));
        assertEquals("VCF header validation error: header 'SAMPLE' must have 'Mixture' enclosed within angular brackets.", context.getErrors().get(4));
        assertEquals("VCF header validation error: header 'SAMPLE' must have 'Genome_Description' enclosed within angular brackets and the values must be enclosed in double quotes.", context.getErrors().get(5));
    }

    @Test
    public void testValidateSampleHeaderNumValuesInAngularBrackets() throws Exception {
        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Genomes", "<G1,G2>");
            put("Mixture", "<0.1,0.9>");
            put("Genome_Description", "<\"1\",\"2\",\"3\">");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValues(valueMap, "SAMPLE", context));
        assertEquals(1, context.getErrorCount());
        assertEquals("VCF header validation error: header 'SAMPLE' must have same number of tokens for Genomes, Mixture and Genome_Description.", context.getErrors().get(0));
    }

    @Test
    public void testValidateSampleHeaderValuesPostUuidTransitionNumValuesInAngularBrackets() throws Exception {

        context.setCenterConvertedToUUID(true);

        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Genomes", "<G1,G2>");
            put("Mixture", "<0.1,0.9>");
            put("Genome_Description", "<\"1\",\"2\",\"3\">");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValuesPostUuidTransition(null, valueMap, "SAMPLE", context));
        assertEquals(1, context.getErrorCount());
        assertEquals("VCF header validation error: header 'SAMPLE' must have same number of tokens for Genomes, Mixture and Genome_Description.", context.getErrors().get(0));
    }

    @Test
    public void testValidateSampleHeaderNumValuesInAngularBracketsNoGenomes() throws Exception {
        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Mixture", "<0.12,.88>");
            put("Genome_Description", "<\"1\",\"2\",\"3\">");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValues(valueMap, "SAMPLE", context));
        assertEquals(1, context.getErrorCount());
        assertEquals("VCF header validation error: header 'SAMPLE' must have same number of tokens for Genomes, Mixture and Genome_Description.", context.getErrors().get(0));
    }

    @Test
    public void testValidateSampleHeaderValuesPostUuidTransitionNumValuesInAngularBracketsNoGenomes() throws Exception {

        context.setCenterConvertedToUUID(true);

        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Mixture", "<0.12,.88>");
            put("Genome_Description", "<\"1\",\"2\",\"3\">");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValuesPostUuidTransition(null, valueMap, "SAMPLE", context));
        assertEquals(1, context.getErrorCount());
        assertEquals("VCF header validation error: header 'SAMPLE' must have same number of tokens for Genomes, Mixture and Genome_Description.", context.getErrors().get(0));
    }


    @Test
    public void testValidateSampleHeaderNumValuesInAngularBracketsNoMixture() throws Exception {
        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Genomes", "<G1,G2>");
            put("Genome_Description", "<\"This is G1\",\"This is G2\",\"This is G3\">");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValues(valueMap, "SAMPLE", context));
        assertEquals(1, context.getErrorCount());
        assertEquals("VCF header validation error: header 'SAMPLE' must have same number of tokens for Genomes, Mixture and Genome_Description.", context.getErrors().get(0));
    }

    @Test
    public void testValidateSampleHeaderValuesPostUuidTransitionNumValuesInAngularBracketsNoMixture() throws Exception {

        context.setCenterConvertedToUUID(true);

        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Genomes", "<G1,G2>");
            put("Genome_Description", "<\"This is G1\",\"This is G2\",\"This is G3\">");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValuesPostUuidTransition(null, valueMap, "SAMPLE", context));
        assertEquals(1, context.getErrorCount());
        assertEquals("VCF header validation error: header 'SAMPLE' must have same number of tokens for Genomes, Mixture and Genome_Description.", context.getErrors().get(0));
    }

    @Test
    public void testValidateSampleHeaderNumValuesInAngularBracketsNoGenomeDescription() throws Exception {
        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Genomes", "<G1,G2>");
            put("Mixture", "<1>");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValues(valueMap, "SAMPLE", context));
        assertEquals(1, context.getErrorCount());
        assertEquals("VCF header validation error: header 'SAMPLE' must have same number of tokens for Genomes, Mixture and Genome_Description.", context.getErrors().get(0));
    }

    @Test
    public void testValidateSampleHeaderValuesPostUuidTransitionNumValuesInAngularBracketsNoGenomeDescription() throws Exception {

        context.setCenterConvertedToUUID(true);

        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Genomes", "<G1,G2>");
            put("Mixture", "<1>");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValuesPostUuidTransition(null, valueMap, "SAMPLE", context));
        assertEquals(1, context.getErrorCount());
        assertEquals("VCF header validation error: header 'SAMPLE' must have same number of tokens for Genomes, Mixture and Genome_Description.", context.getErrors().get(0));
    }

    @Test
    public void testValidateSampleHeaderNumValuesInAngularBracketsEndCommaMixture() throws Exception {
        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Genomes", "<G1>");
            put("Mixture", "<1,>");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValues(valueMap, "SAMPLE", context));
        assertEquals(2, context.getErrorCount());
        assertEquals("VCF header validation error: header 'SAMPLE' has an invalid floating point number value '' in 'Mixture'", context.getErrors().get(0));
        assertEquals("VCF header validation error: header 'SAMPLE' must have same number of tokens for Genomes, Mixture and Genome_Description.", context.getErrors().get(1));
    }

    @Test
    public void testValidateSampleHeaderValuesPostUuidTransitionNumValuesInAngularBracketsEndCommaMixture() throws Exception {

        context.setCenterConvertedToUUID(true);

        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Genomes", "<G1>");
            put("Mixture", "<1,>");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValuesPostUuidTransition(null, valueMap, "SAMPLE", context));
        assertEquals(2, context.getErrorCount());
        assertEquals("VCF header validation error: header 'SAMPLE' has an invalid floating point number value '' in 'Mixture'", context.getErrors().get(0));
        assertEquals("VCF header validation error: header 'SAMPLE' must have same number of tokens for Genomes, Mixture and Genome_Description.", context.getErrors().get(1));
    }

    @Test
    public void testValidateSampleHeaderValidMixtureValue() throws Exception {

        context.setStandaloneValidator(false);
        context.setCenterConvertedToUUID(false);
        context.setFile(new File("test.txt"));

        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Mixture", "<0.0,0.1,0.7,.2>");
            put("SampleUUID", "00000000-0000-0000-0000-000000000000");
        }};

        mockery.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID("00000000-0000-0000-0000-000000000000");
            will(returnValue(true));
        }});

        assertTrue(vcfValidator.validateSampleHeaderValues(valueMap, "SAMPLE", context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidateSampleHeaderValidMixtureValueStandalone() throws Exception {

        context.setStandaloneValidator(true);
        context.setCenterConvertedToUUID(false);
        context.setFile(new File("test.txt"));

        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Mixture", "<0.0,0.1,0.7,.2>");
            put("SampleUUID", "00000000-0000-0000-0000-000000000000");
        }};

        mockery.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID("00000000-0000-0000-0000-000000000000");
            will(returnValue(true));
        }});

        assertTrue(vcfValidator.validateSampleHeaderValues(valueMap, "SAMPLE", context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidateSampleHeaderValuesPostUuidTransitionValidMixtureValue() throws Exception {

        context.setStandaloneValidator(false);
        context.setCenterConvertedToUUID(true);
        context.setFile(new File("test.txt"));

        final String sampleUuid = "00000000-0000-0000-0000-000000000000";
        final String sampleTcgaBarcode = "a-valid-barcode";
        final MetaDataBean metadata = new MetaDataBean();
        metadata.setTssCode("06");
        metadata.setParticipantCode("0881");
        metadata.setProjectCode("TCGA");
        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Mixture", "<0.0,0.1,0.7,.2>");
            put("SampleUUID", sampleUuid);
            put("SampleTCGABarcode", sampleTcgaBarcode);
            put("MetadataResource", "https://tcga-data.nci.nih.gov/uuid/uuidws/mapping/json/uuid/" + sampleUuid);
        }};

        mockery.checking(new Expectations() {{

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateBarcodeFormat(with(sampleTcgaBarcode), with(any(String.class)), with("Aliquot"));
            will(returnValue(null));

            one(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID("00000000-0000-0000-0000-000000000000");
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(sampleUuid, sampleTcgaBarcode);
            will(returnValue(true));
            one(mockBarcodeAndUUIDValidatorImpl).getMetadata(sampleUuid);
            will(returnValue(metadata));
        }});

        assertTrue(vcfValidator.validateSampleHeaderValuesPostUuidTransition(null, valueMap, "SAMPLE", context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidateSampleHeaderValuesPostUuidTransitionValidMixtureValueStandalone() throws Exception {

        context.setStandaloneValidator(true);
        context.setCenterConvertedToUUID(true);
        context.setFile(new File("test.txt"));

        final String sampleUuid = "00000000-0000-0000-0000-000000000000";
        final String sampleTcgaBarcode = "a-valid-barcode";

        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Mixture", "<0.0,0.1,0.7,.2>");
            put("SampleUUID", sampleUuid);
            put("MetadataResource", "https://tcga-data.nci.nih.gov/uuid/uuidws/mapping/json/uuid/" + sampleUuid);
            put("SampleTCGABarcode", sampleTcgaBarcode);
        }};
        final MetaDataBean metadata = new MetaDataBean();
        metadata.setTssCode("06");
        metadata.setParticipantCode("0881");
        metadata.setProjectCode("TCGA");
        mockery.checking(new Expectations() {{

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateBarcodeFormat(with(sampleTcgaBarcode), with(any(String.class)), with("Aliquot"));
            will(returnValue(null));

            one(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));
            one(mockBarcodeAndUUIDValidatorImpl).getMetadata(sampleUuid);
            will(returnValue(metadata));
            one(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(sampleUuid);
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(sampleUuid, sampleTcgaBarcode);
            will(returnValue(true));
        }});

        assertTrue(vcfValidator.validateSampleHeaderValuesPostUuidTransition(null, valueMap, "SAMPLE", context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidateSampleHeaderInvalidMixtureValue() throws Exception {
        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Mixture", "<foo,0.1,0.9,1.0>");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValues(valueMap, "SAMPLE", context));
        assertEquals(2, context.getErrorCount());
        assertEquals("VCF header validation error: header 'SAMPLE' has an invalid floating point number value 'foo' in 'Mixture'", context.getErrors().get(0));
        assertEquals("VCF header validation error: header 'SAMPLE' must have all values in 'foo,0.1,0.9,1.0' sum upto 1.0 in 'Mixture'", context.getErrors().get(1));
    }

    @Test
    public void testValidateSampleHeaderValuesPostUuidTransitionInvalidMixtureValue() throws Exception {

        context.setCenterConvertedToUUID(true);

        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Mixture", "<foo,0.1,0.9,1.0>");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValuesPostUuidTransition(null, valueMap, "SAMPLE", context));
        assertEquals(2, context.getErrorCount());
        assertEquals("VCF header validation error: header 'SAMPLE' has an invalid floating point number value 'foo' in 'Mixture'", context.getErrors().get(0));
        assertEquals("VCF header validation error: header 'SAMPLE' must have all values in 'foo,0.1,0.9,1.0' sum upto 1.0 in 'Mixture'", context.getErrors().get(1));
    }

    @Test
    public void testValidateSampleHeaderInvalidMixtureValueOutOfBounds() throws Exception {
        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Mixture", "<1.1,0.1,0.53,.27>");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValues(valueMap, "SAMPLE", context));
        assertEquals(2, context.getErrorCount());
        assertEquals("VCF header validation error: header 'SAMPLE' has a floating point value '1.1' that is not between 0.0 and 1.0 inclusive in 'Mixture'", context.getErrors().get(0));
        assertEquals("VCF header validation error: header 'SAMPLE' must have all values in '1.1,0.1,0.53,.27' sum upto 1.0 in 'Mixture'", context.getErrors().get(1));
    }

    @Test
    public void testValidateSampleHeaderValuesPostUuidTransitionInvalidMixtureValueOutOfBounds() throws Exception {

        context.setCenterConvertedToUUID(true);

        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Mixture", "<1.1,0.1,0.53,.27>");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValuesPostUuidTransition(null, valueMap, "SAMPLE", context));
        assertEquals(2, context.getErrorCount());
        assertEquals("VCF header validation error: header 'SAMPLE' has a floating point value '1.1' that is not between 0.0 and 1.0 inclusive in 'Mixture'", context.getErrors().get(0));
        assertEquals("VCF header validation error: header 'SAMPLE' must have all values in '1.1,0.1,0.53,.27' sum upto 1.0 in 'Mixture'", context.getErrors().get(1));
    }

    @Test
    public void testValidateSampleHeaderInvalidMixtureValueNegative() throws Exception {
        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Mixture", "<-0.1,0.1,0.9,1.0>");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValues(valueMap, "SAMPLE", context));
        assertEquals(2, context.getErrorCount());
        assertEquals("VCF header validation error: header 'SAMPLE' has a floating point value '-0.1' that is not between 0.0 and 1.0 inclusive in 'Mixture'", context.getErrors().get(0));
        assertEquals("VCF header validation error: header 'SAMPLE' must have all values in '-0.1,0.1,0.9,1.0' sum upto 1.0 in 'Mixture'", context.getErrors().get(1));
    }

    @Test
    public void testValidateSampleHeaderValuesPostUuidTransitionInvalidMixtureValueNegative() throws Exception {

        context.setCenterConvertedToUUID(true);

        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Mixture", "<-0.1,0.1,0.9,1.0>");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValuesPostUuidTransition(null, valueMap, "SAMPLE", context));
        assertEquals(2, context.getErrorCount());
        assertEquals("VCF header validation error: header 'SAMPLE' has a floating point value '-0.1' that is not between 0.0 and 1.0 inclusive in 'Mixture'", context.getErrors().get(0));
        assertEquals("VCF header validation error: header 'SAMPLE' must have all values in '-0.1,0.1,0.9,1.0' sum upto 1.0 in 'Mixture'", context.getErrors().get(1));
    }

    @Test
    public void testValidateSampleHeaderInvalidMixtureValueSum() throws Exception {
        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Mixture", "<0.1,0.9,0.1>");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValues(valueMap, "SAMPLE", context));
        assertEquals(1, context.getErrorCount());
        assertEquals("VCF header validation error: header 'SAMPLE' must have all values in '0.1,0.9,0.1' sum upto 1.0 in 'Mixture'", context.getErrors().get(0));
    }

    @Test
    public void testValidateSampleHeaderValuesPostUuidTransitionInvalidMixtureValueSum() throws Exception {

        context.setCenterConvertedToUUID(true);

        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Mixture", "<0.1,0.9,0.1>");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValuesPostUuidTransition(null, valueMap, "SAMPLE", context));
        assertEquals(1, context.getErrorCount());
        assertEquals("VCF header validation error: header 'SAMPLE' must have all values in '0.1,0.9,0.1' sum upto 1.0 in 'Mixture'", context.getErrors().get(0));
    }

    @Test
    public void testValidateSampleHeaderGenomeDescriptionInvalid() throws Exception {
        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Genome_Description", "<foo,bar,\"blah\">");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValues(valueMap, "SAMPLE", context));
        assertEquals(1, context.getErrorCount());
        assertEquals("VCF header validation error: header 'SAMPLE' must have 'Genome_Description' enclosed within angular brackets and the values must be enclosed in double quotes.", context.getErrors().get(0));
    }

    @Test
    public void testValidateSampleHeaderValuesPostUuidTransitionGenomeDescriptionInvalid() throws Exception {

        context.setCenterConvertedToUUID(true);

        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Genome_Description", "<foo,bar,\"blah\">");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValuesPostUuidTransition(null, valueMap, "SAMPLE", context));
        assertEquals(1, context.getErrorCount());
        assertEquals("VCF header validation error: header 'SAMPLE' must have 'Genome_Description' enclosed within angular brackets and the values must be enclosed in double quotes.", context.getErrors().get(0));
    }

    @Test
    public void testValidateSampleHeaderGenomeDescriptionInvalidDoubleQuotes() throws Exception {
        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Genome_Description", "<\"foo,\"bar\",\"blah\">");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValues(valueMap, "SAMPLE", context));
        assertEquals(1, context.getErrorCount());
        assertEquals("VCF header validation error: header 'SAMPLE' must have 'Genome_Description' enclosed within angular brackets and the values must be enclosed in double quotes.", context.getErrors().get(0));
    }

    @Test
    public void testValidateSampleHeaderValuesPostUuidTransitionGenomeDescriptionInvalidDoubleQuotes() throws Exception {

        context.setCenterConvertedToUUID(true);

        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Genome_Description", "<\"foo,\"bar\",\"blah\">");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValuesPostUuidTransition(null, valueMap, "SAMPLE", context));
        assertEquals(1, context.getErrorCount());
        assertEquals("VCF header validation error: header 'SAMPLE' must have 'Genome_Description' enclosed within angular brackets and the values must be enclosed in double quotes.", context.getErrors().get(0));
    }

    @Test
    public void testValidateSampleHeaderGenomeDescriptionDoubleQuotesInString() throws Exception {
        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Genome_Description", "<\"fo\"o\",\"bar\",\"blah\">");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValues(valueMap, "SAMPLE", context));
        assertEquals(1, context.getErrorCount());
        assertEquals("VCF header validation error: header 'SAMPLE' must have 'Genome_Description' enclosed within angular brackets and the values must be enclosed in double quotes.", context.getErrors().get(0));
    }

    @Test
    public void testValidateSampleHeaderValuesPostUuidTransitionGenomeDescriptionDoubleQuotesInString() throws Exception {

        context.setCenterConvertedToUUID(true);

        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Genome_Description", "<\"fo\"o\",\"bar\",\"blah\">");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValuesPostUuidTransition(null, valueMap, "SAMPLE", context));
        assertEquals(1, context.getErrorCount());
        assertEquals("VCF header validation error: header 'SAMPLE' must have 'Genome_Description' enclosed within angular brackets and the values must be enclosed in double quotes.", context.getErrors().get(0));
    }

    @Test
    public void testValidateSampleHeaderGenomeDescriptionValid() throws Exception {

        context.setStandaloneValidator(false);
        context.setFile(new File("test.txt"));

        final Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Genome_Description", "<\"foo\",\"bar\",\"blah\">");
            put("SampleUUID", "00000000-0000-0000-0000-000000000000");
        }};

        mockery.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID("00000000-0000-0000-0000-000000000000");
            will(returnValue(true));
        }});

        assertTrue(vcfValidator.validateSampleHeaderValues(valueMap, "SAMPLE", context));
    }

    @Test
    public void testValidateSampleHeaderValuesPostUuidTransitionGenomeDescriptionValid() throws Exception {

        context.setStandaloneValidator(false);
        context.setCenterConvertedToUUID(true);
        context.setFile(new File("test.txt"));

        final String sampleUuid = "00000000-0000-0000-0000-000000000000";
        final String sampleTcgaBarcode = "a-valid-barcode";

        final Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Genome_Description", "<\"foo\",\"bar\",\"blah\">");
            put("SampleUUID", sampleUuid);
            put("SampleTCGABarcode", sampleTcgaBarcode);
            put("MetadataResource", "https://tcga-data.nci.nih.gov/uuid/uuidws/mapping/json/uuid/" + sampleUuid);
        }};
        final MetaDataBean metadata = new MetaDataBean();
        metadata.setTssCode("06");
        metadata.setParticipantCode("0881");
        metadata.setProjectCode("TCGA");
        mockery.checking(new Expectations() {{

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateBarcodeFormat(with(sampleTcgaBarcode), with(any(String.class)), with("Aliquot"));
            will(returnValue(null));

            one(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(sampleUuid);
            will(returnValue(true));
            one(mockBarcodeAndUUIDValidatorImpl).getMetadata(sampleUuid);
            will(returnValue(metadata));
            one(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(sampleUuid, sampleTcgaBarcode);
            will(returnValue(true));
        }});

        assertTrue(vcfValidator.validateSampleHeaderValuesPostUuidTransition(null, valueMap, "SAMPLE", context));
    }

    @Test
    public void testValidateSampleHeaderGenomeDescriptionValidSingleValue() throws Exception {

        context.setStandaloneValidator(false);
        context.setFile(new File("test.txt"));

        final Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Genome_Description", "<\"foo\">");
            put("SampleUUID", "00000000-0000-0000-0000-000000000000");
        }};

        mockery.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID("00000000-0000-0000-0000-000000000000");
            will(returnValue(true));
        }});

        assertTrue(vcfValidator.validateSampleHeaderValues(valueMap, "SAMPLE", context));
    }

    @Test
    public void testValidateSampleHeaderValuesPostUuidTransitionGenomeDescriptionValidSingleValue() throws Exception {

        context.setStandaloneValidator(false);
        context.setCenterConvertedToUUID(true);
        context.setFile(new File("test.txt"));

        final String sampleUuid = "00000000-0000-0000-0000-000000000000";
        final String sampleTcgaBarcode = "a-valid-barcode";

        final Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Genome_Description", "<\"foo\">");
            put("SampleUUID", sampleUuid);
            put("SampleTCGABarcode", sampleTcgaBarcode);
            put("MetadataResource", "https://tcga-data.nci.nih.gov/uuid/uuidws/mapping/json/uuid/" + sampleUuid);
        }};
        final MetaDataBean metadata = new MetaDataBean();
        metadata.setTssCode("06");
        metadata.setParticipantCode("0881");
        metadata.setProjectCode("TCGA");
        mockery.checking(new Expectations() {{

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateBarcodeFormat(with(sampleTcgaBarcode), with(any(String.class)), with("Aliquot"));
            will(returnValue(null));

            one(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(sampleUuid);
            will(returnValue(true));
            one(mockBarcodeAndUUIDValidatorImpl).getMetadata(sampleUuid);
            will(returnValue(metadata));
            one(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(sampleUuid, sampleTcgaBarcode);
            will(returnValue(true));
        }});

        assertTrue(vcfValidator.validateSampleHeaderValuesPostUuidTransition(null, valueMap, "SAMPLE", context));
    }

    @Test
    public void testValidateSampleHeaderGenomesInvalidWhitespace() throws Exception {
        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Genomes", "<G 1,G 2>");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValues(valueMap, "SAMPLE", context));
        assertEquals(3, context.getErrorCount());
        assertEquals("VCF header validation error: SAMPLE header value 'G 1' for key 'Genomes' contains whitespace and must be enclosed in double quotes", context.getErrors().get(0));
        assertEquals("VCF header validation error: SAMPLE header value 'G 2' for key 'Genomes' contains whitespace and must be enclosed in double quotes", context.getErrors().get(1));
        assertEquals("VCF header validation error: header 'SAMPLE' must have 'Genomes' enclosed within angular brackets and cannot contain whitespace or nested angular brackets.", context.getErrors().get(2));
    }

    @Test
    public void testValidateSampleHeaderValuesPostUuidTransitionGenomesInvalidWhitespace() throws Exception {

        context.setCenterConvertedToUUID(true);

        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Genomes", "<G 1,G 2>");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValuesPostUuidTransition(null, valueMap, "SAMPLE", context));
        assertEquals(3, context.getErrorCount());
        assertEquals("VCF header validation error: SAMPLE header value 'G 1' for key 'Genomes' contains whitespace and must be enclosed in double quotes", context.getErrors().get(0));
        assertEquals("VCF header validation error: SAMPLE header value 'G 2' for key 'Genomes' contains whitespace and must be enclosed in double quotes", context.getErrors().get(1));
        assertEquals("VCF header validation error: header 'SAMPLE' must have 'Genomes' enclosed within angular brackets and cannot contain whitespace or nested angular brackets.", context.getErrors().get(2));
    }

    @Test
    public void testValidateSampleHeaderGenomesInvalidNestedBrackets() throws Exception {
        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Genomes", "<<G1>,<G2>>");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValues(valueMap, "SAMPLE", context));
        assertEquals(1, context.getErrorCount());
        assertEquals("VCF header validation error: header 'SAMPLE' must have 'Genomes' enclosed within angular brackets and cannot contain whitespace or nested angular brackets.", context.getErrors().get(0));
    }

    @Test
    public void testValidateSampleHeaderValuesPostUuidTransitionGenomesInvalidNestedBrackets() throws Exception {

        context.setCenterConvertedToUUID(true);
        Map<String, String> valueMap = new HashMap<String, String>() {{
            put("Genomes", "<<G1>,<G2>>");
        }};
        assertFalse(vcfValidator.validateSampleHeaderValuesPostUuidTransition(null, valueMap, "SAMPLE", context));
        assertEquals(1, context.getErrorCount());
        assertEquals("VCF header validation error: header 'SAMPLE' must have 'Genomes' enclosed within angular brackets and cannot contain whitespace or nested angular brackets.", context.getErrors().get(0));
    }

    @Test
    public void testInvalidVcfReportingAllErrorsWithoutTrailingWhitespaceInColumnHeader() throws Exception {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_REPORTING_ALL_ERRORS_WITHOUT_TRAILING_WHITESPACE_IN_COLUMN_HEADER), context));
        assertEquals(5, context.getErrorCount());
    }

    @Test
    public void testInvalidVcfReportingAllErrorsWithTrailingWhitespaceInColumnHeader() throws Exception {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_REPORTING_ALL_ERRORS_WITH_TRAILING_WHITESPACE_IN_COLUMN_HEADER), context));
        assertEquals(6, context.getErrorCount());
        assertEquals("[tcgaInvalidReportingAllErrorsWithTrailingWhitespaceInColumnHeader.vcf] VCF data validation error on line 28: INFO value ' DP of 14 and sample DP total of 9' is not valid. " +
                "The value for DP in INFO column and the total of the values for DP in the sample columns must be equal if DP is specified in FORMAT column", context.getErrors().get(0));
        assertEquals("[tcgaInvalidReportingAllErrorsWithTrailingWhitespaceInColumnHeader.vcf] VCF data validation error on line 29: INFO value ' DP of 11 and sample DP total of 8' is not valid. " +
                "The value for DP in INFO column and the total of the values for DP in the sample columns must be equal if DP is specified in FORMAT column", context.getErrors().get(1));
        assertEquals("[tcgaInvalidReportingAllErrorsWithTrailingWhitespaceInColumnHeader.vcf] VCF data validation error on line 30: INFO value ' DP of 10 and sample DP total of 6' is not valid. " +
                "The value for DP in INFO column and the total of the values for DP in the sample columns must be equal if DP is specified in FORMAT column", context.getErrors().get(2));
        assertEquals("[tcgaInvalidReportingAllErrorsWithTrailingWhitespaceInColumnHeader.vcf] VCF data validation error on line 31: INFO value ' DP of 13 and sample DP total of 11' is not valid. " +
                "The value for DP in INFO column and the total of the values for DP in the sample columns must be equal if DP is specified in FORMAT column", context.getErrors().get(3));
        assertEquals("[tcgaInvalidReportingAllErrorsWithTrailingWhitespaceInColumnHeader.vcf] VCF data validation error on line 32: INFO value ' DP of 9 and sample DP total of 6' is not valid. " +
                "The value for DP in INFO column and the total of the values for DP in the sample columns must be equal if DP is specified in FORMAT column", context.getErrors().get(4));
        assertEquals("[tcgaInvalidReportingAllErrorsWithTrailingWhitespaceInColumnHeader.vcf] Header on line 27 must not start or end with whitespace", context.getErrors().get(5));
    }

    @Test
    public void testInvalidVcfReportingAllErrors() throws Exception {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_REPORTING_ALL_ERRORS), context));
        assertEquals(3, context.getErrorCount());
        assertEquals("[tcgaInvalidReportingAllErrors.vcf] VCF header validation error: header 'vcfProcessLog' is missing.", context.getErrors().get(0));
        assertEquals("[tcgaInvalidReportingAllErrors.vcf] VCF header validation error: header 'center' is missing.", context.getErrors().get(1));
        assertEquals("[tcgaInvalidReportingAllErrors.vcf] VCF Column header validation error: column at position 8 should be 'INFO'", context.getErrors().get(2));

    }

    @Test
    public void testValidateTcgaAssemblyHeaderValid() throws Exception {
        VcfFile vcf = new VcfFile();
        VcfFileHeader header1 = new VcfFileHeader("tcgaversion");
        header1.setValue("1.0");
        VcfFileHeader header2 = new VcfFileHeader("assembly");
        header2.setValue("ftp://ncbinihgov/");
        vcf.setHeaders(Arrays.asList(header1, header2));
        assertTrue(vcfValidator.validateTcgaAssemblyHeader(vcf, HEADER_TYPE_ASSEMBLY, context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidateTcgaAssemblyHeaderInValid() throws Exception {
        VcfFile vcf = new VcfFile();
        VcfFileHeader header1 = new VcfFileHeader("tcgaversion");
        header1.setValue("1.0");
        VcfFileHeader header2 = new VcfFileHeader("assembly");
        header2.setValue("ftp:/ncbi.nih.gov/");
        vcf.setHeaders(Arrays.asList(header1, header2));
        assertTrue(vcfValidator.validateTcgaAssemblyHeader(vcf, HEADER_TYPE_ASSEMBLY, context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testValidateTcgaAssemblyHeaderValidMixedCaseDomain() throws Exception {
        VcfFile vcf = new VcfFile();
        VcfFileHeader header1 = new VcfFileHeader("tcgaversion");
        header1.setValue("1.0");
        VcfFileHeader header2 = new VcfFileHeader("assembly");
        header2.setValue("ftp://nCBi.nIh.GOV/");
        vcf.setHeaders(Arrays.asList(header1, header2));
        assertTrue(vcfValidator.validateTcgaAssemblyHeader(vcf, HEADER_TYPE_ASSEMBLY, context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testSampleIdentifierMissing() throws Processor.ProcessorException {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                APPS_4390_TEST_FILE), context));
        assertEquals(context.getErrors().toString(), 3, context.getErrorCount());
        assertEquals("[apps4390.vcf] VCF data validation error on line 35: SAMPLE #1 value '.' is not valid. Incorrect number of values. Expected 2 but found 1.", context.getErrors().get(0));
        assertEquals("[apps4390.vcf] VCF data validation error on line 36: SAMPLE #1 value '.' is not valid. Incorrect number of values. Expected 2 but found 1.", context.getErrors().get(1));
        assertEquals("[apps4390.vcf] VCF data validation error on line 36: SAMPLE #2 value '1.2' is not valid. Should be an integer.", context.getErrors().get(2));
    }

    @Test
    public void testInvalidTcgaVcfMissingSvtype() throws Processor.ProcessorException {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY, TCGA_INVALID_MISSING_SVTYPE), context));
        assertEquals(context.getErrors().toString(), 5, context.getErrorCount());
        assertEquals("[tcgaInvalidMissingSVTYPE.vcf] VCF data validation error on line 32: INFO value 'NS=3;DP=14;AF=0.5;DB;H2' is not valid. " +
                "Must specify 'SVTYPE' when using SV_ALT values for column 'ALT'", context.getErrors().get(0));
        assertEquals("[tcgaInvalidMissingSVTYPE.vcf] VCF data validation error on line 33: INFO value 'NS=3;DP=11;AF=0.017,.35' is not valid. " +
                "Must specify 'SVTYPE' when using SV_ALT values for column 'ALT'", context.getErrors().get(1));
        assertEquals("[tcgaInvalidMissingSVTYPE.vcf] VCF data validation error on line 34: INFO value 'NS=2;DP=10;AF=0.333,0.667,.56;DB' is not valid. " +
                "Must specify 'SVTYPE' when using SV_ALT values for column 'ALT'", context.getErrors().get(2));
        assertEquals("[tcgaInvalidMissingSVTYPE.vcf] VCF data validation error on line 35: INFO value 'NS=3;DP=13;AA=T' is not valid. " +
                "Must specify 'SVTYPE' when using SV_ALT values for column 'ALT'", context.getErrors().get(3));
        assertEquals("[tcgaInvalidMissingSVTYPE.vcf] VCF header validation error: header 'assembly' is missing.", context.getErrors().get(4));
    }

    @Test
    public void testValidTcgaFilterWarning() throws Processor.ProcessorException {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY, TCGA_VALID_FILTER_WARNING), context));
        assertEquals(context.getErrors().toString(), 0, context.getErrorCount());
        assertEquals(context.getWarnings().toString(), 4, context.getWarningCount());
        assertEquals("[tcgaValidFilterWarning.vcf] FILTER header on line 15 has a 'Number' key that is not being used.", context.getWarnings().get(0));
        assertEquals("[tcgaValidFilterWarning.vcf] FILTER header on line 16 has a 'Type' key that is not being used.", context.getWarnings().get(1));
        assertEquals("[tcgaValidFilterWarning.vcf] FILTER header on line 17 has a 'Type' key that is not being used.", context.getWarnings().get(2));
        assertEquals("[tcgaValidFilterWarning.vcf] FILTER header on line 17 has a 'Number' key that is not being used.", context.getWarnings().get(3));
    }

    @Test
    public void testInvalidTcgaVcfRepeatingPedigreeValues() throws Processor.ProcessorException {

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY, TCGA_INVALID_REPEATING_PEDIGREE_VALUES), context));

        assertEquals(context.getErrors().toString(), 1, context.getErrorCount());
        assertEquals("[tcgaInvalidRepeatingPedigreeValues.vcf] PEDIGREE header values may not be repeated across keys, but found 'TUMOR' more than once.", context.getErrors().get(0));
    }

    @Test
    public void testInvalidTcgaEmptyHeaderLine() throws Processor.ProcessorException {

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(4).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY, TCGA_INVALID_EMPTY_HEADER_LINE), context));
        assertEquals(context.getErrors().toString(), 1, context.getErrorCount());
        assertEquals("[tcgaInvalidEmptyHeaderLine.vcf] Header line 8 is blank", context.getErrors().get(0));
    }

    @Test
    public void missingSampleNameInSampleHeader() throws Processor.ProcessorException {

        context.setCenterConvertedToUUID(false);

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY, TCGA_VALID_SAMPLE_HEADER_MISSING_SAMPLE_NAME), context));
        assertEquals(context.getErrors().toString(), 1, context.getErrorCount());

        final String error = context.getErrors().get(0);
        assertNotNull(error);
        assertEquals("[tcgaValidSampleHeaderNoSampleNameKey.vcf] VCF header validation error: header 'SAMPLE' must have all of 'ID', 'SampleName', 'Individual', 'File', 'Platform', 'Source', 'Accession' keys.", error);
    }

    @Test
    public void missingSampleNameInSampleHeaderStandalone() throws Processor.ProcessorException {

        context.setCenterConvertedToUUID(false);
        context.setStandaloneValidator(true);

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY, TCGA_VALID_SAMPLE_HEADER_MISSING_SAMPLE_NAME), context));
        assertEquals(context.getErrors().toString(), 1, context.getErrorCount());

        final String error = context.getErrors().get(0);
        assertNotNull(error);
        assertEquals("[tcgaValidSampleHeaderNoSampleNameKey.vcf] VCF header validation error: header 'SAMPLE' must have all of 'ID', 'SampleName', 'Individual', 'File', 'Platform', 'Source', 'Accession' keys.", error);
    }

    @Test
    public void missingSampleNameInSampleHeaderCenterConverted() throws Processor.ProcessorException, CommonBarcodeAndUUIDValidator.CommonBarcodeAndUUIDValidatorException {

        context.setCenterConvertedToUUID(true);

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";
        final MetaDataBean metadata = new MetaDataBean();
        metadata.setTssCode("06");
        metadata.setParticipantCode("0881");
        metadata.setProjectCode("TCGA");

        mockery.checking(new Expectations() {{

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateBarcodeFormat(with(barcode), with(any(String.class)), with("Aliquot"));
            will(returnValue(null));

            one(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).getMetadata(uuid);
            will(returnValue(metadata));

            one(mockBarcodeAndUUIDValidatorImpl).getPatientBarcode(barcode);
            will(returnValue("TCGA-06-0881"));
        }});

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY, TCGA_VALID_SAMPLE_HEADER_MISSING_SAMPLE_NAME), context));
        assertEquals(context.getErrors().toString(), 0, context.getErrorCount());
    }

    @Test
    public void missingSampleNameInSampleHeaderCenterConvertedStandalone() throws Processor.ProcessorException, CommonBarcodeAndUUIDValidator.CommonBarcodeAndUUIDValidatorException {

        context.setCenterConvertedToUUID(true);
        context.setStandaloneValidator(true);

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";
        final MetaDataBean metadata = new MetaDataBean();
        metadata.setTssCode("06");
        metadata.setParticipantCode("0881");
        metadata.setProjectCode("TCGA");
        mockery.checking(new Expectations() {{

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateBarcodeFormat(with(barcode), with(any(String.class)), with("Aliquot"));
            will(returnValue(null));

            one(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).batchValidateSampleUuidAndSampleTcgaBarcode(with(any(List.class)), with(any(QcContext.class)));
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).getMetadata(uuid);
            will(returnValue(metadata));

            one(mockBarcodeAndUUIDValidatorImpl).getPatientBarcode(barcode);
            will(returnValue("TCGA-06-0881"));
        }});

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY, TCGA_VALID_SAMPLE_HEADER_MISSING_SAMPLE_NAME), context));
        assertEquals(context.getErrors().toString(), 0, context.getErrorCount());
    }

    @Test
    public void invalidSampleNameValueInSampleHeader() throws Processor.ProcessorException {
        final QcLiveBarcodeAndUUIDValidator mockBarcodeAndUUIDValidatorImpl = mockery.mock(QcLiveBarcodeAndUUIDValidator.class, "Invalid");
        vcfValidator.setQcLiveBarcodeAndUUIDValidator(mockBarcodeAndUUIDValidatorImpl);

        mockery.checking(new Expectations() {{
            exactly(1).of(mockBarcodeAndUUIDValidatorImpl).validateBarcodeOrUuid("TCGA-0", context, "tcgaInvalidSampleHeaderSampleNameValue.vcf", true);
            will(returnValue(false));
            exactly(1).of(mockBarcodeAndUUIDValidatorImpl).validateBarcodeOrUuid("TCGA-06-0881-10A-01W-0421-09", context, "tcgaInvalidSampleHeaderSampleNameValue.vcf", true);
            will(returnValue(true));

        }});
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY, TCGA_INVALID_SAMPLE_HEADER_SAMPLE_NAME_VALUE), context));
    }

    @Test
    public void invalidSampleNameValueInSampleHeaderCenterConvertedToUUID() throws Processor.ProcessorException {

        context.setCenterConvertedToUUID(true);

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY, TCGA_INVALID_SAMPLE_HEADER_SAMPLE_NAME_VALUE), context));
    }

    @Test
    public void invalidSampleNameValueInSampleHeaderForStandalone() throws Processor.ProcessorException {
        context.setStandaloneValidator(true);
        final QcLiveBarcodeAndUUIDValidator mockBarcodeAndUUIDValidatorImpl = mockery.mock(QcLiveBarcodeAndUUIDValidator.class, "Invalid");
        vcfValidator.setQcLiveBarcodeAndUUIDValidator(mockBarcodeAndUUIDValidatorImpl);

        mockery.checking(new Expectations() {{
            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).isUUID(with(any(String.class)));
            will(returnValue(true));
            one(mockBarcodeAndUUIDValidatorImpl).batchValidate(with(any(List.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(false));
        }});
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY, TCGA_INVALID_SAMPLE_HEADER_SAMPLE_NAME_VALUE), context));
    }

    @Test
    public void invalidSampleNameValueInSampleHeaderForStandaloneCenterConvertedToUUID() throws Processor.ProcessorException, CommonBarcodeAndUUIDValidator.CommonBarcodeAndUUIDValidatorException {
        context.setStandaloneValidator(true);
        context.setCenterConvertedToUUID(true);
        final QcLiveBarcodeAndUUIDValidator mockBarcodeAndUUIDValidatorImpl = mockery.mock(QcLiveBarcodeAndUUIDValidator.class, "Invalid");
        vcfValidator.setQcLiveBarcodeAndUUIDValidator(mockBarcodeAndUUIDValidatorImpl);
        final MetaDataBean metadata = new MetaDataBean();
        metadata.setTssCode("06");
        metadata.setParticipantCode("0881");
        metadata.setProjectCode("TCGA");
        mockery.checking(new Expectations() {{
            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).isUUID(with(any(String.class)));
            will(returnValue(false));
            one(mockBarcodeAndUUIDValidatorImpl).batchValidateSampleUuidAndSampleTcgaBarcode(with(any(List.class)), with(any(QcContext.class)));
            will(returnValue(false));

        }});
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY, TCGA_INVALID_SAMPLE_HEADER_SAMPLE_NAME_VALUE), context));
    }

    @Test
    public void testValidateIdThreadSafe() throws Processor.ProcessorException {

        context.setStandaloneValidator(false);
        context.setCenterConvertedToUUID(false);

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            exactly(4 * 2).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(4 * 2).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(4 * 2).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
        }});

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY, TCGA_VALID_ONE_DATA_LINE), context));
        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY, TCGA_VALID_ONE_DATA_LINE), context));
    }

    @Test
    public void testValidateIdThreadSafeCenterConvertedToUUID() throws Processor.ProcessorException, CommonBarcodeAndUUIDValidator.CommonBarcodeAndUUIDValidatorException {

        context.setStandaloneValidator(false);
        context.setCenterConvertedToUUID(true);

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";
        final MetaDataBean metadata = new MetaDataBean();
        metadata.setTssCode("06");
        metadata.setParticipantCode("0881");
        metadata.setProjectCode("TCGA");
        mockery.checking(new Expectations() {{

            exactly(4 * 2).of(mockBarcodeAndUUIDValidatorImpl).validateBarcodeFormat(with(barcode), with(any(String.class)), with("Aliquot"));
            will(returnValue(null));

            exactly(4 * 2).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(4 * 2).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(4 * 2).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));

            exactly(4 * 2).of(mockBarcodeAndUUIDValidatorImpl).getMetadata(uuid);
            will(returnValue(metadata));

            exactly(4 * 2).of(mockBarcodeAndUUIDValidatorImpl).getPatientBarcode(barcode);
            will(returnValue("TCGA-06-0881"));
        }});

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY, TCGA_VALID_ONE_DATA_LINE), context));
        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY, TCGA_VALID_ONE_DATA_LINE), context));
    }

    @Test
    public void testValidateIdThreadSafeStandalone() throws Processor.ProcessorException {

        context.setStandaloneValidator(true);
        context.setCenterConvertedToUUID(false);

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {{
            exactly(4 * 2).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(4 * 2).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(4 * 2).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));

            exactly(4 * 2).of(mockBarcodeAndUUIDValidatorImpl).isUUID("TCGA-06-0881-10A-01W");
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).batchValidate(with(any(List.class)), with(any(QcContext.class)), with(any(String.class)), with(true));
            will(returnValue(true));
        }});

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY, TCGA_VALID_ONE_DATA_LINE), context));
        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY, TCGA_VALID_ONE_DATA_LINE), context));
    }

    @Test
    public void testValidateIdThreadSafeStandaloneCenterConvertedToUUID() throws Processor.ProcessorException, CommonBarcodeAndUUIDValidator.CommonBarcodeAndUUIDValidatorException {

        context.setStandaloneValidator(true);
        context.setCenterConvertedToUUID(true);

        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";
        final MetaDataBean metadata = new MetaDataBean();
        metadata.setTssCode("06");
        metadata.setParticipantCode("0881");
        metadata.setProjectCode("TCGA");
        mockery.checking(new Expectations() {{

            exactly(4 * 2).of(mockBarcodeAndUUIDValidatorImpl).validateBarcodeFormat(with(barcode), with(any(String.class)), with("Aliquot"));
            will(returnValue(null));

            exactly(4 * 2).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(4 * 2).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(4 * 2).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));

            exactly(4 * 2).of(mockBarcodeAndUUIDValidatorImpl).isUUID("TCGA-06-0881-10A-01W");
            will(returnValue(true));

            exactly(4 * 2).of(mockBarcodeAndUUIDValidatorImpl).getMetadata(uuid);
            will(returnValue(metadata));

            exactly(4 * 2).of(mockBarcodeAndUUIDValidatorImpl).getPatientBarcode(barcode);
            will(returnValue("TCGA-06-0881"));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).batchValidateSampleUuidAndSampleTcgaBarcode(with(any(List.class)), with(any(QcContext.class)));
            will(returnValue(true));
        }});

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY, TCGA_VALID_ONE_DATA_LINE), context));
        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY, TCGA_VALID_ONE_DATA_LINE), context));
    }

    @Test
    public void testSampleHeaderSampleUUIDWrongFormat() {

        context.setFile(new File("test.txt"));
        assertNotNull(context.getFile());
        assertFalse(context.isStandaloneValidator());

        final String expectedErrorMessage = "Wrong uuid format";

        mockery.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(new Action() {
                @Override
                public Object invoke(final Invocation invocation) throws Throwable {
                    context.addError(expectedErrorMessage);
                    return false;
                }

                @Override
                public void describeTo(final Description description) {
                    description.appendText(" wrong uuid format");
                }
            });
        }});

        final boolean result = vcfValidator.validateSampleHeaderSampleUUIDValue("not-a-valid-uuid-format", context);
        assertFalse(result);
        assertEquals(1, context.getErrorCount());

        final String actualError = context.getErrors().get(0);
        assertNotNull(actualError);
        assertEquals(expectedErrorMessage, actualError);
    }

    @Test
    public void testSampleHeaderSampleUUIDEmpty() {

        assertFalse(context.isStandaloneValidator());

        final boolean result = vcfValidator.validateSampleHeaderSampleUUIDValue("", context);
        assertFalse(result);
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testSampleHeaderSampleUUIDNotReceivedByDCC() {

        context.setFile(new File("test.txt"));
        assertNotNull(context.getFile());
        assertFalse(context.isStandaloneValidator());

        final String expectedErrorMessage = "Not Received by DCC";

        mockery.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(new Action() {
                @Override
                public Object invoke(final Invocation invocation) throws Throwable {
                    context.addError(expectedErrorMessage);
                    return false;
                }

                @Override
                public void describeTo(final Description description) {
                    description.appendText(" not received by DCC");
                }
            });
        }});

        final boolean result = vcfValidator.validateSampleHeaderSampleUUIDValue("valid-uuid-format", context);
        assertFalse(result);
        assertEquals(1, context.getErrorCount());

        final String actualError = context.getErrors().get(0);
        assertNotNull(actualError);
        assertEquals(expectedErrorMessage, actualError);
    }

    @Test
    public void testSampleHeaderSampleUUIDNotForAnAliquot() {

        final String uuid = "valid-uuid-format";

        context.setFile(new File("test.txt"));
        assertNotNull(context.getFile());
        assertFalse(context.isStandaloneValidator());

        final String expectedErrorMessage = "The uuid '" + uuid + "' is not assigned to an aliquot";

        mockery.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(false));
        }});

        final boolean result = vcfValidator.validateSampleHeaderSampleUUIDValue(uuid, context);
        assertFalse(result);
        assertEquals(1, context.getErrorCount());

        final String actualError = context.getErrors().get(0);
        assertNotNull(actualError);
        assertEquals(expectedErrorMessage, actualError);
    }

    @Test
    public void testSampleHeaderSampleUUID() {

        final String uuid = "valid-uuid-format";

        context.setFile(new File("test.txt"));
        assertNotNull(context.getFile());
        assertFalse(context.isStandaloneValidator());

        mockery.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            one(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));
        }});

        final boolean result = vcfValidator.validateSampleHeaderSampleUUIDValue(uuid, context);
        assertTrue(result);
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testSampleHeaderSampleTcgaBarcode() {

        context.setFile(new File("test.txt"));
        assertNotNull(context.getFile());

        final String sampleUuid = "valid-uuid";
        final String sampleTcgaBarcode = "tcga-barcode";

        mockery.checking(new Expectations() {{

            one(mockBarcodeAndUUIDValidatorImpl).validateBarcodeFormat(with(sampleTcgaBarcode), with(any(String.class)), with("Aliquot"));
            will(returnValue(null));

            one(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(sampleUuid, sampleTcgaBarcode);
            will(returnValue(true));
        }});

        final boolean result = vcfValidator.validateSampleHeaderSampleTcgaBarcode(sampleTcgaBarcode, sampleUuid, context);

        assertTrue(result);
    }

    @Test
    public void testSampleHeaderSampleTcgaBarcodeWhenUUIDAndBarcodeDoNotMatch() {

        context.setFile(new File("test.txt"));
        assertNotNull(context.getFile());

        final String sampleUuid = "valid-uuid";
        final String sampleTcgaBarcode = "tcga-barcode";
        final String expectedError = new StringBuilder("SampleUUID '")
                .append(sampleUuid)
                .append("' and SampleTCGABarcode '")
                .append(sampleTcgaBarcode)
                .append("' do not match.")
                .toString();

        mockery.checking(new Expectations() {{

            one(mockBarcodeAndUUIDValidatorImpl).validateBarcodeFormat(with(sampleTcgaBarcode), with(any(String.class)), with("Aliquot"));
            will(returnValue(null));

            one(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(sampleUuid, sampleTcgaBarcode);
            will(returnValue(false));
        }});

        final boolean result = vcfValidator.validateSampleHeaderSampleTcgaBarcode(sampleTcgaBarcode, sampleUuid, context);

        assertFalse(result);
        assertEquals(1, context.getErrorCount());

        final String actualError = context.getErrors().get(0);

        assertNotNull(actualError);
        assertEquals(expectedError, actualError);
    }

    @Test
    public void testSampleHeaderSampleTcgaBarcodeWhenUUIDEmpty() {

        context.setFile(new File("test.txt"));
        assertNotNull(context.getFile());

        final String sampleUuid = "";
        final String sampleTcgaBarcode = "tcga-barcode";
        final String expectedErrorMessage = "UUID empty";

        mockery.checking(new Expectations() {{

            one(mockBarcodeAndUUIDValidatorImpl).validateBarcodeFormat(with(sampleTcgaBarcode), with(any(String.class)), with("Aliquot"));
            will(returnValue(expectedErrorMessage));
        }});

        final boolean result = vcfValidator.validateSampleHeaderSampleTcgaBarcode(sampleTcgaBarcode, sampleUuid, context);
        assertFalse(result);
        assertEquals(1, context.getErrorCount());

        final String actualErrorMessage = context.getErrors().get(0);
        assertNotNull(actualErrorMessage);
        assertEquals(expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testSampleHeaderSampleTcgaBarcodeWhenEmpty() {

        final String sampleUuid = "valid-uuid";
        final String sampleTcgaBarcode = "";
        final boolean result = vcfValidator.validateSampleHeaderSampleTcgaBarcode(sampleTcgaBarcode, sampleUuid, context);
        assertFalse(result);
    }

    @Test
    public void testSampleHeaderIndividualWrongSampleUUID() throws Exception {
        context.setCenterConvertedToUUID(true);
        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";
        final MetaDataBean metadata = new MetaDataBean();
        metadata.setTssCode("06");
        metadata.setParticipantCode("1234");
        metadata.setProjectCode("TCGA");

        mockery.checking(new Expectations() {{

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateBarcodeFormat(with(barcode), with(any(String.class)), with("Aliquot"));
            will(returnValue(null));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).getMetadata(uuid);
            will(returnValue(metadata));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).getPatientBarcode(barcode);
            will(returnValue("TCGA-06-0881"));
        }});

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_SAMPLE_HEADER_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testSampleHeaderIndividualWrongTcgaBarcode() throws Exception {
        context.setCenterConvertedToUUID(true);
        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";
        final MetaDataBean metadata = new MetaDataBean();
        metadata.setTssCode("06");
        metadata.setParticipantCode("0881");
        metadata.setProjectCode("TCGA");

        mockery.checking(new Expectations() {{

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateBarcodeFormat(with(barcode), with(any(String.class)), with("Aliquot"));
            will(returnValue(null));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).getMetadata(uuid);
            will(returnValue(metadata));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).getPatientBarcode(barcode);
            will(returnValue("TCGA-06-1234"));
        }});

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_SAMPLE_HEADER_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testSampleHeaderIndividualWrongTcgaBarcodeAndSampleUUID() throws Exception {
        context.setCenterConvertedToUUID(true);
        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";
        final MetaDataBean metadata = new MetaDataBean();
        metadata.setTssCode("06");
        metadata.setParticipantCode("1234");
        metadata.setProjectCode("TCGA");

        mockery.checking(new Expectations() {{

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateBarcodeFormat(with(barcode), with(any(String.class)), with("Aliquot"));
            will(returnValue(null));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).getMetadata(uuid);
            will(returnValue(metadata));

            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).getPatientBarcode(barcode);
            will(returnValue("TCGA-06-1234"));
        }});

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_VALID_SAMPLE_HEADER_FILE), context));
        assertEquals(0, context.getErrorCount());

    }

    @Test
    public void testIndividualGoodSampleTcgaBarcodeAndSampleUUID() throws Exception {
        context.setCenterConvertedToUUID(true);
        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";
        final MetaDataBean metadata = new MetaDataBean();
        metadata.setTssCode("06");
        metadata.setParticipantCode("0881");
        metadata.setProjectCode("TCGA");

        mockery.checking(new Expectations() {{
            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));
            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));
            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).getMetadata(uuid);
            will(returnValue(metadata));
            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateBarcodeFormat(barcode, "tcgaValidSampleHeaderGoodIndividual.vcf", "Aliquot");
            will(returnValue(null));
            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).getPatientBarcode(barcode);
            will(returnValue("TCGA-06-0881"));
        }});

        assertTrue(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_SAMPLE_HEADER_GOOD_INDIVIDUAL_FILE), context));
        assertEquals(0, context.getErrorCount());
    }

    @Test
    public void testIndividualWrongSampleTcgaBarcodeAndSampleUUID() throws Exception {
        context.setCenterConvertedToUUID(true);
        final String uuid = "00000000-0000-0000-0000-000000000000";
        final String barcode = "TCGA-00-0000-00A-00A-0000-00";
        final String uuid2 = "00000000-0000-0000-0000-000000000001";
        final String barcode2 = "TCGA-00-0000-00A-00A-0000-01";
        final MetaDataBean metadata = new MetaDataBean();
        metadata.setTssCode("06");
        metadata.setParticipantCode("0881");
        metadata.setProjectCode("TCGA");
        final MetaDataBean metadata2 = new MetaDataBean();
        metadata2.setTssCode("06");
        metadata2.setParticipantCode("0882");
        metadata2.setProjectCode("TCGA");

        mockery.checking(new Expectations() {{
            exactly(2).of(mockBarcodeAndUUIDValidatorImpl).validateUuid(with(any(String.class)), with(any(QcContext.class)), with(any(String.class)), with(any(Boolean.class)));
            will(returnValue(true));
            oneOf(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid);
            will(returnValue(true));
            oneOf(mockBarcodeAndUUIDValidatorImpl).isAliquotUUID(uuid2);
            will(returnValue(true));
            oneOf(mockBarcodeAndUUIDValidatorImpl).getMetadata(uuid);
            will(returnValue(metadata));
            oneOf(mockBarcodeAndUUIDValidatorImpl).getMetadata(uuid2);
            will(returnValue(metadata2));
            oneOf(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid, barcode);
            will(returnValue(true));
            oneOf(mockBarcodeAndUUIDValidatorImpl).validateUUIDBarcodeMapping(uuid2, barcode2);
            will(returnValue(true));
            oneOf(mockBarcodeAndUUIDValidatorImpl).getPatientBarcode(barcode);
            will(returnValue("TCGA-06-0881"));
            oneOf(mockBarcodeAndUUIDValidatorImpl).getPatientBarcode(barcode2);
            will(returnValue("TCGA-06-0882"));
            oneOf(mockBarcodeAndUUIDValidatorImpl).validateBarcodeFormat(barcode, "tcgaValidSampleHeaderWrongIndividual.vcf", "Aliquot");
            will(returnValue(null));
            oneOf(mockBarcodeAndUUIDValidatorImpl).validateBarcodeFormat(barcode2, "tcgaValidSampleHeaderWrongIndividual.vcf", "Aliquot");
            will(returnValue(null));
        }});

        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY,
                TCGA_INVALID_SAMPLE_HEADER_WRONG_INDIVIDUAL_FILE), context));
        assertEquals(2, context.getErrorCount());
        assertEquals("[tcgaValidSampleHeaderWrongIndividual.vcf] VCF header validation error: " +
                "the sampleUUID '00000000-0000-0000-0000-000000000000' in header 'SAMPLE' " +
                "matching patient barcode 'TCGA-06-0881' does not match the header " +
                "'INDIVIDUAL' value 'TCGA-06-4321'.",
                context.getErrors().get(0));
        assertEquals("[tcgaValidSampleHeaderWrongIndividual.vcf] VCF header validation error: " +
                "the sampleUUID '00000000-0000-0000-0000-000000000001' in header 'SAMPLE' " +
                "matching patient barcode 'TCGA-06-0882' does not match the header " +
                "'INDIVIDUAL' value 'TCGA-06-4321'.",
                context.getErrors().get(1));
    }

    @Test
    public void testIndividualSampleMismatchNullMetaData() {
        mockery.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidatorImpl).getPatientBarcode("TCGA-11-2222-01A-01W-1234-00");
            will(returnValue("TCGA-11-2222"));
        }});

        // when noremote is used the meta-data cannot be fetched so is null
        // still should validate the barcodes against each other though
        assertFalse(vcfValidator.validateSampleHeaderIndividualValue(null, "TCGA-10-2222", "a-uuid", "TCGA-11-2222-01A-01W-1234-00", context));
        assertEquals("Individual Value 'TCGA-10-2222' does not match the participant barcode 'TCGA-11-2222' associated with sampleTcgaBarcode 'TCGA-11-2222-01A-01W-1234-00'.",
                context.getErrors().get(0));
    }

    @Test
    public void testTransitionIndividualSampleMismatchNullMetaData() {
        mockery.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidatorImpl).getPatientBarcode("TCGA-00-0101-01B-02D-7777-18");
            will(returnValue("TCGA-00-0101"));
        }});
        assertFalse(vcfValidator.validateTcgaIndividualHeaderUuidTransition(null, "a-uuid", "TCGA-00-0101-01B-02D-7777-18", "TCGA-1D-0101", context));
        assertEquals("Value of INDIVIDUAL header 'TCGA-1D-0101' does not match the participant barcode 'TCGA-00-0101' associated with sampleTcgaBarcode 'TCGA-00-0101-01B-02D-7777-18' in the SAMPLE header.",
                context.getErrors().get(0));
    }

    @Test
     public void testTransitionNullIndividualNullMetaData() {
         mockery.checking(new Expectations() {{
             one(mockBarcodeAndUUIDValidatorImpl).getPatientBarcode("TCGA-00-0101-01B-02D-7777-18");
             will(returnValue("TCGA-00-0101"));
         }});
        // if the individual value is null, is valid
         assertTrue(vcfValidator.validateTcgaIndividualHeaderUuidTransition(null, "a-uuid", "TCGA-00-0101-01B-02D-7777-18", null, context));
     }

    @Test
    public void testTransitionSampleTcgaBarcodeBad() {
        mockery.checking(new Expectations() {{
            one(mockBarcodeAndUUIDValidatorImpl).getPatientBarcode("TCGA-00-0101-01B-02D-7777-18");
            will(returnValue(null));
        }});
        assertFalse(vcfValidator.validateTcgaIndividualHeaderUuidTransition(null, "a-uuid", "TCGA-00-0101-01B-02D-7777-18", "TCGA-00-0101", context));
        assertEquals(1, context.getErrors().size());
    }

    @Test
    public void testTransitionSamplePatientBarcodeBad() {
        MockMetaDataBean metadata = new MockMetaDataBean(); metadata.setMockPatientBuiltBarcode(null);
        assertFalse(vcfValidator.validateTcgaIndividualHeaderUuidTransition(metadata, "a-uuid", "TCGA-00-0101-01B-02D-7777-18", "TCGA-00-0101", context));
        assertEquals(1, context.getErrors().size());
    }

    @Test
    public void testMissingColumnHeader() throws Processor.ProcessorException {
        assertFalse(vcfValidator.processFile(new File(TEST_VCF_DIRECTORY, "tcga/missingColumnHeader.vcf"), context));
        assertEquals(1, context.getErrorCount());
        assertEquals("[missingColumnHeader.vcf] Missing column header line", context.getErrors().get(0));
    }

    private class MockMetaDataBean extends MetaDataBean {
        private String mockPatientBuiltBarcode;
        private void setMockPatientBuiltBarcode(String mockPatientBuiltBarcode) {
            this.mockPatientBuiltBarcode = mockPatientBuiltBarcode;
        }
        public String getPatientBuiltBarcode() {
            return mockPatientBuiltBarcode;
        }
    }
}