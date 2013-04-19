package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.MetaDataBean;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.SampleType;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DataTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.SampleTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ShippedBiospecimenQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.util.CommonBarcodeAndUUIDValidatorImpl;
import gov.nih.nci.ncicb.tcga.dcc.common.util.DataTypeName;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeTumorValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.util.ChromInfoUtilsImpl;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test class for the MafFileValidatorV2_4
 *
 * @author bertondl
 *         Last updated by: $Author$
 * @version $Rev$
 */

@RunWith(JMock.class)
public class MafFileValidatorV2_4FastTest {

    private static final String SAMPLES_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;
    private static final String MAFV2_4_DIR = SAMPLES_DIR + "qclive" + File.separator
            + "mafFileValidator" + File.separator + "mafV2_4" + File.separator;
    final List<SampleType> sampleList = new ArrayList<SampleType>();
    private final Mockery context = new JUnit4Mockery();
    private MafFileValidatorV2_4 mafFileValidator;
    private QcContext qcContext;
    private QcLiveBarcodeAndUUIDValidator mockBarcodeValidator;
    private CenterQueries mockCenterQueries;
    private SampleTypeQueries mockSampleTypeQueries;
    private ShippedBiospecimenQueries mockShippedBiospecimenQueries;
    private DataTypeQueries mockDataTypeQueries;

    @Before
    public void setup() {
        final Archive archive = new Archive();
        archive.setExperimentType(Experiment.TYPE_GSC);
        archive.setArchiveType(Archive.TYPE_LEVEL_2);
        qcContext = new QcContext();
        qcContext.setCenterConvertedToUUID(true);
        qcContext.setArchive(archive);
        qcContext.setNoRemote(false);
        mafFileValidator = new MafFileValidatorV2_4();
        mafFileValidator.setChromInfoUtils(new ChromInfoUtilsImpl());
        mafFileValidator.setBarcodeTumorValidator(new BarcodeTumorValidator() {
            // just returns true
            public boolean barcodeIsValidForTumor(final String barcode,
                                                  final String tumorAbbreviation) {
                return true;
            }
        });

        mafFileValidator.sampleCodes = new HashMap<String, Boolean>();
        mafFileValidator.sampleCodes.put("00", true);
        mafFileValidator.sampleCodes.put("01", false);
        mafFileValidator.sampleCodes.put("10", false);

        SampleType sampleElement = new SampleType();
        sampleElement.setSampleTypeCode("00");
        sampleElement.setIsTumor(true);
        sampleList.add(sampleElement);

        sampleElement = new SampleType();
        sampleElement.setSampleTypeCode("01");
        sampleElement.setIsTumor(false);
        sampleList.add(sampleElement);

        mockCenterQueries = context.mock(CenterQueries.class);
        mafFileValidator.setCenterQueries(mockCenterQueries);

        mockBarcodeValidator = context.mock(QcLiveBarcodeAndUUIDValidator.class);
        mafFileValidator.setBarcodeValidator(mockBarcodeValidator);
        mockSampleTypeQueries = context.mock(SampleTypeQueries.class);
        mafFileValidator.setSampleTypeQueries(mockSampleTypeQueries);
        mockShippedBiospecimenQueries = context.mock(ShippedBiospecimenQueries.class);
        mafFileValidator.setShippedBiospecimenQueries(mockShippedBiospecimenQueries);
        qcContext.setPlatformName("IlluminaGA_DNASeq");

        mockDataTypeQueries = context.mock(DataTypeQueries.class);
        mafFileValidator.setDataTypeQueries(mockDataTypeQueries);
        context.checking(new Expectations() {
            {
                allowing(mockSampleTypeQueries).getAllSampleTypes();
                will(returnValue(sampleList));

                allowing(mockCenterQueries).findCenterId("genome.wustl.edu", "GSC");
                will(returnValue(123));

                allowing(mockCenterQueries).findCenterId("hgsc.bcm.edu", "GSC");
                will(returnValue(5));

            }
        });

    }

    /*
     * Pass in a list of tumor uuids, normal uuids, tumor barcodes, and normal barcodes. They should be ordered
     * so the nth item of tumorUuids corresponds to the nth item of tumorBarcodes and normalUuids etc. (In other words,
     * the index in the list should be the same for all items from the same row of the file.)
     */
    private void setupForValidUUIDs(final List<String> tumorUuids, final List<String> normalUuids,
                                    final List<String> tumorBarcodes, final List<String> normalBarcodes) {
        setupForUUIDs(tumorUuids, normalUuids, tumorBarcodes, normalBarcodes, true, true, true, true);
    }

    private void setupForUUIDs(final List<String> tumorUuids, final List<String> normalUuids,
                               final List<String> tumorBarcodes, final List<String> normalBarcodes,
                               final boolean validateUuidResults, final boolean isMatchingDiseaseResults,
                               final boolean validateMappingResults, final boolean isAliquotResults) {

        for (int i = 0; i < tumorUuids.size(); i++) {
            final String tumorUuid = tumorUuids.get(i);
            final String tumorBarcode = tumorBarcodes.get(i);
            final String normalUuid = normalUuids.get(i);
            final String normalBarcode = normalBarcodes.get(i);

            final Matcher tumorBarcodeMatcher = CommonBarcodeAndUUIDValidatorImpl.ALIQUOT_BARCODE_PATTERN.matcher(tumorBarcode);
            final Matcher normalBarcodeMatcher = CommonBarcodeAndUUIDValidatorImpl.ALIQUOT_BARCODE_PATTERN.matcher(normalBarcode);
            if (tumorBarcodeMatcher.matches() && normalBarcodeMatcher.matches()) {
                final MetaDataBean tumorMetaData = new MetaDataBean();

                tumorMetaData.setSampleCode(tumorBarcodeMatcher.group(CommonBarcodeAndUUIDValidatorImpl.SAMPLE_TYPE_CODE_GROUP));
                tumorMetaData.setParticipantCode(tumorBarcodeMatcher.group(CommonBarcodeAndUUIDValidatorImpl.PATIENT_GROUP));
                tumorMetaData.setTssCode(tumorBarcodeMatcher.group(CommonBarcodeAndUUIDValidatorImpl.TSS_GROUP));

                final MetaDataBean normalMetaData = new MetaDataBean();
                normalMetaData.setSampleCode(normalBarcodeMatcher.group(CommonBarcodeAndUUIDValidatorImpl.SAMPLE_TYPE_CODE_GROUP));
                normalMetaData.setParticipantCode(normalBarcodeMatcher.group(CommonBarcodeAndUUIDValidatorImpl.PATIENT_GROUP));
                normalMetaData.setTssCode(normalBarcodeMatcher.group(CommonBarcodeAndUUIDValidatorImpl.TSS_GROUP));

                context.checking(new Expectations() {{

                    one(mockBarcodeValidator).validateUuid(
                            with(tumorUuid),
                            with(qcContext),
                            with(any(String.class)),
                            with(true));
                    will(returnValue(validateUuidResults));

                    one(mockBarcodeValidator).validateUuid(
                            with(normalUuid),
                            with(qcContext),
                            with(any(String.class)),
                            with(true));
                    will(returnValue(validateUuidResults));

                    one(mockBarcodeValidator).isMatchingDiseaseForUUID(
                            with(tumorUuid),
                            with(any(String.class)));
                    will(returnValue(isMatchingDiseaseResults));

                    one(mockBarcodeValidator).isMatchingDiseaseForUUID(
                            with(normalUuid),
                            with(any(String.class)));
                    will(returnValue(isMatchingDiseaseResults));

                    one(mockBarcodeValidator).validateUUIDBarcodeMapping(tumorUuid, tumorBarcode);
                    will(returnValue(validateMappingResults));

                    one(mockBarcodeValidator).validateUUIDBarcodeMapping(normalUuid, normalBarcode);
                    will(returnValue(validateMappingResults));

                    one(mockShippedBiospecimenQueries).retrieveUUIDMetadata(tumorUuid);
                    will(returnValue(tumorMetaData));

                    one(mockShippedBiospecimenQueries).retrieveUUIDMetadata(normalUuid);
                    will(returnValue(normalMetaData));

                    one(mockBarcodeValidator).isAliquotUUID(tumorUuid);
                    will(returnValue(isAliquotResults));

                    one(mockBarcodeValidator).isAliquotUUID(normalUuid);
                    will(returnValue(isAliquotResults));
                }});
            }
        }
    }

    @After
    public void tearDown() {
        mafFileValidator.cleanup();
    }

    @Test
    public void testValidSomaticMAFFilename() throws Exception {

        qcContext.setPlatformName("IlluminaGA_DNASeq");
        File mafFile = new File(MAFV2_4_DIR + "/validArchive/genome.wustl.edu_OV.IlluminaGA_DNASeq.Level_2.7.preliminary.somatic.maf");
        qcContext.getArchive().setArchiveFile(new File(MAFV2_4_DIR
                + "/validArchive/genome.wustl.edu_OV.IlluminaGA_DNASeq.Level_2.7.somatic.tar.gz"));

        context.checking(new Expectations() {{
            one(mockDataTypeQueries).getBaseDataTypeNameForPlatform("IlluminaGA_DNASeq");
            will(returnValue(DataTypeName.SOMATIC_MUTATIONS.getValue()));
        }
        });



        mafFileValidator.validateFilename(mafFile.getName(), qcContext);
        assertTrue("Errors: " + qcContext.getErrors(), (qcContext.getErrorCount() == 0));

    }

    @Test
    public void testInvalidSomaticMAFFilename() {

        qcContext.setPlatformName("IlluminaGA_DNASeq");
        File mafFile = new File(MAFV2_4_DIR + "/validArchive/genome.wustl.edu_OV.IlluminaGA_DNASeq.Level_2.7.germ.somatic.maf");
        qcContext.getArchive().setArchiveFile(new File(MAFV2_4_DIR
                + "/validArchive/genome.wustl.edu_OV.IlluminaGA_DNASeq.Level_2.7.somatic.tar.gz"));

        context.checking(new Expectations() {{
            exactly(2).of(mockDataTypeQueries).getBaseDataTypeNameForPlatform("IlluminaGA_DNASeq");
            will(returnValue(DataTypeName.SOMATIC_MUTATIONS.getValue()));
        }
        });

        try {
            mafFileValidator.validateFilename(mafFile.getName(), qcContext);
            fail("Not a valid filename. Must throw an exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Failed processing maf file genome.wustl.edu_OV.IlluminaGA_DNASeq.Level_2.7.germ.somatic.maf. Somatic maf files must not have 'germ' or 'protected'  text in the filename"));
        }
        mafFile = new File(MAFV2_4_DIR + "/validArchive/genome.wustl.edu_OV.IlluminaGA_DNASeq.Level_2.7.protected.somatic.maf");
        qcContext.getArchive().setArchiveFile(new File(MAFV2_4_DIR
                + "/validArchive/genome.wustl.edu_OV.IlluminaGA_DNASeq.Level_2.7.somatic.tar.gz"));
        try {
            mafFileValidator.validateFilename(mafFile.getName(), qcContext);
            fail("Not a valid filename. Must throw an exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Failed processing maf file genome.wustl.edu_OV.IlluminaGA_DNASeq.Level_2.7.protected.somatic.maf. Somatic maf files must not have 'germ' or 'protected'  text in the filename"));
        }

    }

    @Test
    public void testValidProtectedMAFFilename() throws Exception {

        qcContext.setPlatformName("IlluminaGA_DNASeq_Cont");
        File mafFile = new File(MAFV2_4_DIR + "/validArchive/genome.wustl.edu_OV.IlluminaGA_DNASeq.Level_2.7.protected.maf");
        qcContext.getArchive().setArchiveFile(new File(MAFV2_4_DIR
                + "/validArchive/genome.wustl.edu_OV.IlluminaGA_DNASeq.Level_2.7.protected.tar.gz"));

        context.checking(new Expectations() {{
            one(mockDataTypeQueries).getBaseDataTypeNameForPlatform("IlluminaGA_DNASeq_Cont");
            will(returnValue(DataTypeName.PROTECTED_MUTATIONS.getValue()));
        }
        });

        mafFileValidator.validateFilename(mafFile.getName(), qcContext);
        assertTrue("Errors: " + qcContext.getErrors(), (qcContext.getErrorCount() == 0));

    }

    @Test
    public void testInvalidProtectedMAFFilename() {

        qcContext.setPlatformName("IlluminaGA_DNASeq");
        File mafFile = new File(MAFV2_4_DIR + "/validArchive/genome.wustl.edu_OV.IlluminaGA_DNASeq.Level_2.7.somatic.protected.maf");
        qcContext.getArchive().setArchiveFile(new File(MAFV2_4_DIR
                + "/validArchive/genome.wustl.edu_OV.IlluminaGA_DNASeq.Level_2.7.somatic.tar.gz"));

        context.checking(new Expectations() {{
            one(mockDataTypeQueries).getBaseDataTypeNameForPlatform("IlluminaGA_DNASeq");
            will(returnValue(DataTypeName.SOMATIC_MUTATIONS.getValue()));
        }
        });

        try {
            mafFileValidator.validateFilename(mafFile.getName(), qcContext);
            fail("Not a valid filename. Must throw an exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Failed processing maf file genome.wustl.edu_OV.IlluminaGA_DNASeq.Level_2.7.somatic.protected.maf. File extension must be SOMATIC_MAF_EXTENSION"));
        }
    }


    @Test
    public void testMAFHeaderOK() throws Exception {
        setupForValidUUIDs(Arrays.asList("5760a312-43d7-42fb-b03d-b2c6728ab74a"),
                Arrays.asList("5760a312-43d7-42fb-b03d-b2c6728ab74b"),
                Arrays.asList("TCGA-25-1317-00A-01W-0490-10"),
                Arrays.asList("TCGA-25-1317-01A-01W-0490-10"));

        File mafFile = new File(MAFV2_4_DIR + "/validArchive/center_disease_platform_valid.somatic.maf");
        qcContext.getArchive().setArchiveFile(new File(MAFV2_4_DIR
                + "/validArchive/center_disease_platform_valid.somatic.tar.gz"));

        context.checking(new Expectations() {{
            one(mockDataTypeQueries).getBaseDataTypeNameForPlatform("IlluminaGA_DNASeq");
            will(returnValue(DataTypeName.SOMATIC_MUTATIONS.getValue()));
        }
        });


        boolean valid = mafFileValidator.execute(mafFile, qcContext);
        assertTrue("Errors: " + qcContext.getErrors(), valid);
    }

    @Test
    public void testUuidBarcodeNoMatch() throws Processor.ProcessorException {
        setupForUUIDs(Arrays.asList("5760a312-43d7-42fb-b03d-b2c6728ab74a"),
                Arrays.asList("5760a312-43d7-42fb-b03d-b2c6728ab74b"),
                Arrays.asList("TCGA-25-1317-00A-01W-0490-10"),
                Arrays.asList("TCGA-25-1317-01A-01W-0490-10"),
                true, true, false, true); // the false tells the mock to return false for validateMapping
        final File mafFile = new File(MAFV2_4_DIR + "/validArchive/center_disease_platform_valid.somatic.maf");


        qcContext.getArchive().setArchiveFile(new File(MAFV2_4_DIR
                + "/validArchive/center_disease_platform_valid.somatic.tar.gz"));

        context.checking(new Expectations() {{
            one(mockDataTypeQueries).getBaseDataTypeNameForPlatform("IlluminaGA_DNASeq");
            will(returnValue(DataTypeName.SOMATIC_MUTATIONS.getValue()));
        }
        });

        assertFalse(mafFileValidator.execute(mafFile, qcContext));
        assertEquals(2, qcContext.getErrorCount());
        assertEquals("An error occurred while validating MAF file 'center_disease_platform_valid.somatic.maf', line 3: " +
                "tumor barcode (TCGA-25-1317-00A-01W-0490-10) doesn't map to given tumor UUID " +
                "(5760a312-43d7-42fb-b03d-b2c6728ab74a)", qcContext.getErrors().get(0));
        assertEquals("An error occurred while validating MAF file 'center_disease_platform_valid.somatic.maf', " +
                "line 3: normal barcode (TCGA-25-1317-01A-01W-0490-10) doesn't map to given normal UUID " +
                "(5760a312-43d7-42fb-b03d-b2c6728ab74b)", qcContext.getErrors().get(1));
    }

    @Test
    public void testUuidsNotAliquots() throws Processor.ProcessorException {
        setupForUUIDs(Arrays.asList("5760a312-43d7-42fb-b03d-b2c6728ab74a"),
                Arrays.asList("5760a312-43d7-42fb-b03d-b2c6728ab74b"),
                Arrays.asList("TCGA-25-1317-00A-01W-0490-10"),
                Arrays.asList("TCGA-25-1317-01A-01W-0490-10"),
                true, true, true, false); // the false tells the mock validator to return false for isAliquot
        final File mafFile = new File(MAFV2_4_DIR + "/validArchive/center_disease_platform_valid.somatic.maf");

        context.checking(new Expectations() {{
            one(mockDataTypeQueries).getBaseDataTypeNameForPlatform("IlluminaGA_DNASeq");
            will(returnValue(DataTypeName.SOMATIC_MUTATIONS.getValue()));
        }
        });

        qcContext.getArchive().setArchiveFile(new File(MAFV2_4_DIR
                + "/validArchive/center_disease_platform_valid.somatic.tar.gz"));
        assertFalse(mafFileValidator.execute(mafFile, qcContext));
        assertEquals(2, qcContext.getErrorCount());

        assertEquals("An error occurred while validating MAF file 'center_disease_platform_valid.somatic.maf', " +
                "line 3: tumor sample UUID does not represent an aliquot", qcContext.getErrors().get(0));
        assertEquals("An error occurred while validating MAF file 'center_disease_platform_valid.somatic.maf', " +
                "line 3: normal sample UUID does not represent an aliquot", qcContext.getErrors().get(1));
    }

    @Test
    public void testMAFHeaderNCBIBuildCaseInsensitive() throws Exception {
        setupForValidUUIDs(Arrays.asList("5760a312-43d7-42fb-b03d-b2c6728ab74a"),
                Arrays.asList("5760a312-43d7-42fb-b03d-b2c6728ab74b"),
                Arrays.asList("TCGA-25-1317-00A-01W-0490-10"),
                Arrays.asList("TCGA-25-1317-01A-01W-0490-10"));
        File mafFile = new File(MAFV2_4_DIR + "/validArchive/ncbi_case_insensitive_valid.somatic.maf");

        context.checking(new Expectations() {{
            one(mockDataTypeQueries).getBaseDataTypeNameForPlatform("IlluminaGA_DNASeq");
            will(returnValue(DataTypeName.SOMATIC_MUTATIONS.getValue()));
        }
        });

        qcContext.getArchive().setArchiveFile(new File(MAFV2_4_DIR
                + "/validArchive/ncbi_case_insensitive_valid.somatic.tar.gz"));
        boolean valid = mafFileValidator.execute(mafFile, qcContext);
        assertTrue("Errors: " + qcContext.getErrors(), valid);
    }

    @Test
    public void testMAFHeaderBadPublic() throws Exception {
        File mafFile = new File(MAFV2_4_DIR + "/invalidArchive/header_invalid.somatic.maf");
        qcContext.getArchive().setArchiveFile(new File(MAFV2_4_DIR
                + "/validArchive/header_invalid.somatic.tar.gz"));
        context.checking(new Expectations() {{
            one(mockDataTypeQueries).getBaseDataTypeNameForPlatform("IlluminaGA_DNASeq");
            will(returnValue(DataTypeName.SOMATIC_MUTATIONS.getValue()));
        }
        });

        boolean valid = mafFileValidator.execute(mafFile, qcContext);
        assertFalse(valid);
        assertEquals(16, qcContext.getErrorCount());
        assertEquals("An error occurred while validating MAF file 'header_invalid.somatic.maf', line 3: " +
                "'Tumor_Sample_UUID' value '5760a312-43d7-42fb-b03d-b2c6728ab74aD' is invalid - must be a " +
                "valid aliquot UUID",
                qcContext.getErrors().get(0));
        assertEquals("An error occurred while validating MAF file 'header_invalid.somatic.maf', line 3: " +
                "'Match_Norm_Validation_Allele1' value 'cardinal' is invalid - must either be '-' for deleted, or be " +
                "composed of A, C, G, T, and '-'",
                qcContext.getErrors().get(1));
        assertEquals("An error occurred while validating MAF file 'header_invalid.somatic.maf', line 3: " +
                "'Validation_Status' value 'Wildtype' is invalid - must be Untested, Inconclusive, Valid, or Invalid",
                qcContext.getErrors().get(2));
        assertEquals("An error occurred while validating MAF file 'header_invalid.somatic.maf', line 3: " +
                "'Match_Norm_Validation_Allele2' value 'eagle' is invalid - must either be '-' for deleted, or " +
                "be composed of A, C, G, T, and '-'",
                qcContext.getErrors().get(3));
        assertEquals("An error occurred while validating MAF file 'header_invalid.somatic.maf', line 3: " +
                "'Unknown' is an invalid value for 'dbSNP_Val_Status - must be by1000genomes, by2Hit2Allele, byCluster, " +
                "byFrequency, byHapMap, byOtherPop, bySubmitter, alternate_allele or blank'",
                qcContext.getErrors().get(4));
        assertEquals("An error occurred while validating MAF file 'header_invalid.somatic.maf', line 3: " +
                "'NCBI_Build' value '38' is invalid - must be hg18, hg19, GRCh37, GRCh37-lite, 36, 36.1 or 37",
                qcContext.getErrors().get(5));
        assertEquals("An error occurred while validating MAF file 'header_invalid.somatic.maf', line 3: " +
                "'Validation_Method' value '' is invalid - must be something like: Sanger_PCR_WGA, Sanger_PCR_gDNA, " +
                "454_PCR_WGA, 454_PCR_gDNA, Illumina GAIIx, SOLiD; separate multiple entries using semicolons. " +
                "Blank is not allowed",
                qcContext.getErrors().get(6));
        assertEquals("An error occurred while validating MAF file 'header_invalid.somatic.maf', line 3: " +
                "'Reference_Allele' value 'DEL' is invalid - must be A,C,G,T and/or -",
                qcContext.getErrors().get(7));
        assertEquals("An error occurred while validating MAF file 'header_invalid.somatic.maf', line 3: " +
                "'Matched_Norm_Sample_UUID' value '5760a312-43d7-42fb-b03d-b2c6728ab74bD' is invalid - must be " +
                "a valid aliquot UUID",
                qcContext.getErrors().get(8));
        assertEquals("An error occurred while validating MAF file 'header_invalid.somatic.maf', line 3: " +
                "'Tumor_Validation_Allele1' value 'squirel' is invalid - must either be '-' for deleted, or be composed " +
                "of A, C, G, T, and '-'",
                qcContext.getErrors().get(9));
        assertEquals("An error occurred while validating MAF file 'header_invalid.somatic.maf', line 3: " +
                "'Tumor_Seq_Allele1' value 'INS' is invalid - must be A,C,G,T and/or -",
                qcContext.getErrors().get(10));
        assertEquals("An error occurred while validating MAF file 'header_invalid.somatic.maf', line 3: " +
                "'Tumor_Validation_Allele2' value 'robin' is invalid - must either be '-' for deleted, or be composed " +
                "of A, C, G, T, and '-'",
                qcContext.getErrors().get(11));
        assertEquals("An error occurred while validating MAF file 'header_invalid.somatic.maf', line 3: " +
                "'Tumor_Seq_Allele2' value 'DEL' is invalid - must be A,C,G,T and/or -",
                qcContext.getErrors().get(12));
        assertEquals("An error occurred while validating MAF file 'header_invalid.somatic.maf', line 3: " +
                "'Capture' is an invalid value for 'Sequence_Source - must be one or more of WGS, WGA, WXS, RNA-Seq, " +
                "miRNA-Seq, ncRNA-Seq, WCS, CLONE, POOLCLONE, AMPLICON, CLONEEND, FINISHING, ChIP-Seq, MNase-Seq, " +
                "DNase-Hypersensitivity, Bisulfite-Seq, EST, FL-cDNA, CTS, MRE-Seq, MeDIP-Seq, MBD-Seq, Tn-Seq, " +
                "VALIDATION, FAIRE-seq, SELEX, RIP-Seq, ChIA-PET, Other; separate multiple values using semicolons'",
                qcContext.getErrors().get(13));
        assertEquals("An error occurred while validating MAF file 'header_invalid.somatic.maf', line 3: " +
                "'Variant_Classification' value 'De_novo_Start_OutOfFrame' is invalid - must be one of " +
                "Frame_Shift_Del, Frame_Shift_Ins, In_Frame_Del, In_Frame_Ins, Missense_Mutation, Nonsense_Mutation, " +
                "Silent, Splice_Site, Nonstop_Mutation, 3'UTR, 3'Flank, 5'UTR, 5'Flank, IGR, Intron, RNA, " +
                "Targeted_Region or Translation_Start_Site",
                qcContext.getErrors().get(14));
        assertEquals("An error occurred while validating MAF file 'header_invalid.somatic.maf', line 3: " +
                "'Mutation_Status' value 'None' is invalid - must be Somatic",
                qcContext.getErrors().get(15));
    }

    @Test
    public void testMAFHeaderBadProtected() throws Exception {
        qcContext.setPlatformName("IlluminaGA_DNASeq_Cont");
        File mafFile = new File(MAFV2_4_DIR + "/invalidArchive/header_invalid.protected.maf");
        qcContext.getArchive().setArchiveFile(new File(MAFV2_4_DIR
                + "/validArchive/header_invalid.protected.tar.gz"));
        context.checking(new Expectations() {{
            one(mockDataTypeQueries).getBaseDataTypeNameForPlatform("IlluminaGA_DNASeq_Cont");
            will(returnValue(DataTypeName.PROTECTED_MUTATIONS.getValue()));
        }
        });
        boolean valid = mafFileValidator.execute(mafFile, qcContext);
        assertFalse(valid);
        assertEquals(16, qcContext.getErrorCount());
        assertEquals("An error occurred while validating MAF file 'header_invalid.protected.maf', line 3: " +
                "'Tumor_Sample_UUID' value '5760a312-43d7-42fb-b03d-b2c6728ab74aD' is invalid - must be a " +
                "valid aliquot UUID",
                qcContext.getErrors().get(0));
        assertEquals("An error occurred while validating MAF file 'header_invalid.protected.maf', line 3: " +
                "'Match_Norm_Validation_Allele1' value 'cardinal' is invalid - must either be '-' for deleted, or be " +
                "composed of A, C, G, T, and '-'",
                qcContext.getErrors().get(1));
        assertEquals("An error occurred while validating MAF file 'header_invalid.protected.maf', line 3: " +
                "'Validation_Status' value 'Wildtype' is invalid - must be Untested, Inconclusive, Valid, or Invalid",
                qcContext.getErrors().get(2));
        assertEquals("An error occurred while validating MAF file 'header_invalid.protected.maf', line 3: " +
                "'Match_Norm_Validation_Allele2' value 'eagle' is invalid - must either be '-' for deleted, or " +
                "be composed of A, C, G, T, and '-'",
                qcContext.getErrors().get(3));
        assertEquals("An error occurred while validating MAF file 'header_invalid.protected.maf', line 3: " +
                "'Unknown' is an invalid value for 'dbSNP_Val_Status - must be by1000genomes, by2Hit2Allele, byCluster, " +
                "byFrequency, byHapMap, byOtherPop, bySubmitter, alternate_allele or blank'",
                qcContext.getErrors().get(4));
        assertEquals("An error occurred while validating MAF file 'header_invalid.protected.maf', line 3: " +
                "'NCBI_Build' value '38' is invalid - must be hg18, hg19, GRCh37, GRCh37-lite, 36, 36.1 or 37",
                qcContext.getErrors().get(5));
        assertEquals("An error occurred while validating MAF file 'header_invalid.protected.maf', line 3: " +
                "'Validation_Method' value '' is invalid - must be something like: Sanger_PCR_WGA, Sanger_PCR_gDNA, " +
                "454_PCR_WGA, 454_PCR_gDNA, Illumina GAIIx, SOLiD; separate multiple entries using semicolons. " +
                "Blank is not allowed",
                qcContext.getErrors().get(6));
        assertEquals("An error occurred while validating MAF file 'header_invalid.protected.maf', line 3: " +
                "'Reference_Allele' value 'DEL' is invalid - must be A,C,G,T and/or -",
                qcContext.getErrors().get(7));
        assertEquals("An error occurred while validating MAF file 'header_invalid.protected.maf', line 3: " +
                "'Matched_Norm_Sample_UUID' value '5760a312-43d7-42fb-b03d-b2c6728ab74bD' is invalid - must be " +
                "a valid aliquot UUID",
                qcContext.getErrors().get(8));
        assertEquals("An error occurred while validating MAF file 'header_invalid.protected.maf', line 3: " +
                "'Tumor_Validation_Allele1' value 'squirel' is invalid - must either be '-' for deleted, or be composed " +
                "of A, C, G, T, and '-'",
                qcContext.getErrors().get(9));
        assertEquals("An error occurred while validating MAF file 'header_invalid.protected.maf', line 3: " +
                "'Tumor_Seq_Allele1' value 'INS' is invalid - must be A,C,G,T and/or -",
                qcContext.getErrors().get(10));
        assertEquals("An error occurred while validating MAF file 'header_invalid.protected.maf', line 3: " +
                "'Tumor_Validation_Allele2' value 'robin' is invalid - must either be '-' for deleted, or be composed " +
                "of A, C, G, T, and '-'",
                qcContext.getErrors().get(11));
        assertEquals("An error occurred while validating MAF file 'header_invalid.protected.maf', line 3: " +
                "'Tumor_Seq_Allele2' value 'DEL' is invalid - must be A,C,G,T and/or -",
                qcContext.getErrors().get(12));
        assertEquals("An error occurred while validating MAF file 'header_invalid.protected.maf', line 3: " +
                "'Capture' is an invalid value for 'Sequence_Source - must be one or more of WGS, WGA, WXS, RNA-Seq, " +
                "miRNA-Seq, ncRNA-Seq, WCS, CLONE, POOLCLONE, AMPLICON, CLONEEND, FINISHING, ChIP-Seq, MNase-Seq, " +
                "DNase-Hypersensitivity, Bisulfite-Seq, EST, FL-cDNA, CTS, MRE-Seq, MeDIP-Seq, MBD-Seq, Tn-Seq, " +
                "VALIDATION, FAIRE-seq, SELEX, RIP-Seq, ChIA-PET, Other; separate multiple values using semicolons'",
                qcContext.getErrors().get(13));
        assertEquals("An error occurred while validating MAF file 'header_invalid.protected.maf', line 3: " +
                "'Variant_Classification' value 'De_novo_Start_OutOfFrame' is invalid - must be one of " +
                "Frame_Shift_Del, Frame_Shift_Ins, In_Frame_Del, In_Frame_Ins, Missense_Mutation, Nonsense_Mutation, " +
                "Silent, Splice_Site, Nonstop_Mutation, 3'UTR, 3'Flank, 5'UTR, 5'Flank, IGR, Intron, RNA, " +
                "Targeted_Region or Translation_Start_Site",
                qcContext.getErrors().get(14));
        assertEquals("An error occurred while validating MAF file 'header_invalid.protected.maf', line 3: " +
                "'Mutation_Status' value 'vulture' is invalid - must be Germline, Somatic, LOH, None, " +
                "Post-transcriptional modification or Unknown",
                qcContext.getErrors().get(15));
    }

    @Test
    public void testPublicMafRules() throws Exception {
        context.checking(new Expectations() {{
            allowing(mockBarcodeValidator).validateUuid(with(any(String.class)), with(qcContext), with(any(String.class)), with(true));
            will(returnValue(true));
            allowing(mockBarcodeValidator).isMatchingDiseaseForUUID(with(any(String.class)), with(any(String.class)));
            will(returnValue(true));
        }});
        File mafFile = new File(MAFV2_4_DIR + "/invalidArchive/invalid_data.somatic.maf");
        qcContext.getArchive().setArchiveFile(new File(MAFV2_4_DIR
                + "/validArchive/invalid_data.somatic.tar.gz"));
        context.checking(new Expectations() {{
            one(mockDataTypeQueries).getBaseDataTypeNameForPlatform("IlluminaGA_DNASeq");
            will(returnValue(DataTypeName.SOMATIC_MUTATIONS.getValue()));
        }
        });

        boolean valid = mafFileValidator.execute(mafFile, qcContext);
        assertFalse(valid);
        assertEquals(4, qcContext.getErrorCount());
        assertEquals("An error occurred while validating MAF file 'invalid_data.somatic.maf', " +
                "line 3: Validation_Method (454_PCR_WGA) must be none when Validation_Status is 'Untested'",
                qcContext.getErrors().get(0));
        assertEquals("An error occurred while validating MAF file 'invalid_data.somatic.maf', line 4: Must be either Validation_Status(Untested)== 'Valid' or  Verification_Status(Unknown)== 'Verified' or Variant_Classification( 3'Flank)== {Frame_Shift_Del, Frame_Shift_Ins, In_Frame_Del, In_Frame_Ins, Missense_Mutation, Nonsense_Mutation, Silent, Splice_Site, Translation_Start_Site, Nonstop_Mutation, RNA, Targeted_Region}",
                qcContext.getErrors().get(1));
        assertEquals("An error occurred while validating MAF file 'invalid_data.somatic.maf', " +
                "line 5: Tumor_Validation_Allele1 (G) must be equal to Tumor_Validation_Allele2 AND Tumor_Validation_Allele2 (T) must be equal to Reference_Allele when Validation_Status is 'Invalid'",
                qcContext.getErrors().get(2));
        assertEquals("An error occurred while validating MAF file 'invalid_data.somatic.maf', " +
                "line 5: Mutation_Status (Somatic) must be 'None' when Validation_Status is 'Invalid'",
                qcContext.getErrors().get(3));
    }

    @Test
    public void testProtectedMafRules() throws Exception {
        context.checking(new Expectations() {{
            allowing(mockBarcodeValidator).validateUuid(with(any(String.class)), with(qcContext), with(any(String.class)), with(true));
            will(returnValue(true));
            allowing(mockBarcodeValidator).isMatchingDiseaseForUUID(with(any(String.class)), with(any(String.class)));
            will(returnValue(true));
            one(mockDataTypeQueries).getBaseDataTypeNameForPlatform("SOLiD_DNASeq_Cont");
            will(returnValue(DataTypeName.PROTECTED_MUTATIONS.getValue()));

        }});
        qcContext.setPlatformName("SOLiD_DNASeq_Cont");
        File mafFile = new File(MAFV2_4_DIR + "/invalidArchive/invalid_data.protected.maf");
        qcContext.getArchive().setArchiveFile(new File(MAFV2_4_DIR
                + "/validArchive/invalid_data.protected.tar.gz"));
        boolean valid = mafFileValidator.execute(mafFile, qcContext);
        assertFalse(valid);
        assertEquals(8, qcContext.getErrorCount());
        assertEquals("An error occurred while validating MAF file 'invalid_data.protected.maf', " +
                "line 3: Mutation_Status (None) must be Germline, Somatic, LOH, Post-transcriptional modification " +
                "or Unknown when Validation_Status is 'Valid'",
                qcContext.getErrors().get(0));
        assertEquals("An error occurred while validating MAF file 'invalid_data.protected.maf', " +
                "line 3: Tumor_Validation_Allele1 () must not be blank when Validation_Status is 'Valid'",
                qcContext.getErrors().get(1));
        assertEquals("An error occurred while validating MAF file 'invalid_data.protected.maf', " +
                "line 3: Tumor_Validation_Allele2 () must not be blank when Validation_Status is 'Valid'",
                qcContext.getErrors().get(2));
        assertEquals("An error occurred while validating MAF file 'invalid_data.protected.maf', " +
                "line 3: Match_Norm_Validation_Allele1 () must not be blank when Validation_Status is 'Valid'",
                qcContext.getErrors().get(3));
        assertEquals("An error occurred while validating MAF file 'invalid_data.protected.maf', " +
                "line 3: Match_Norm_Validation_Allele2 () must not be blank when Validation_Status is 'Valid'",
                qcContext.getErrors().get(4));
        assertEquals("An error occurred while validating MAF file 'invalid_data.protected.maf', " +
                "line 4: Tumor_Validation_Allele1 (G) must be equal to Tumor_Validation_Allele2 AND Tumor_Validation_Allele2 (T) must be equal to Reference_Allele when Validation_Status is 'Invalid'",
                qcContext.getErrors().get(5));
        assertEquals("An error occurred while validating MAF file 'invalid_data.protected.maf', " +
                "line 4: Mutation_Status (Somatic) must be 'None' when Validation_Status is 'Invalid'",
                qcContext.getErrors().get(6));
        assertEquals("An error occurred while validating MAF file 'invalid_data.protected.maf', " +
                "line 5: Tumor_Validation_Allele1 (G) must not be blank AND Tumor_Validation_Allele2 (G) must not be blank AND Match_Norm_Validation_Allele1 (G) must not be blank AND Match_Norm_Validation_Allele2 ( ) must not be blank when Validation_Status is 'Invalid'",
                qcContext.getErrors().get(7));

    }

}//End of Class