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
import gov.nih.nci.ncicb.tcga.dcc.common.bean.MetaDataBean;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.SampleType;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.CenterQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.SampleTypeQueries;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.ShippedBiospecimenQueries;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeTumorValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.util.ChromInfoUtilsImpl;
import junit.framework.Assert;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test class for MafFileValidator
 *
 * @author Robert S. Sfeir Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class Maf2FileValidatorFastTest {

    private static final String SAMPLES_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;
    private final Mockery context = new JUnit4Mockery();
    private Maf2FileValidator mafFileValidator;
    private QcContext qcContext;
    private QcLiveBarcodeAndUUIDValidator mockBarcodeValidator;
    private CenterQueries mockCenterQueries;
    private SampleTypeQueries mockSampleTypeQueries;
    private ShippedBiospecimenQueries mockShippedBiospecimenQueries;
    final List<SampleType> sampleList = new ArrayList<SampleType>();

    @Before
    public void setup() {
        final Archive archive = new Archive();
        archive.setExperimentType(Experiment.TYPE_GSC);
        archive.setArchiveType(Archive.TYPE_LEVEL_2);
        qcContext = new QcContext();
        qcContext.setArchive(archive);
        mafFileValidator = new Maf2FileValidator();
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

        mockBarcodeValidator = context
                .mock(QcLiveBarcodeAndUUIDValidator.class);
        mafFileValidator.setBarcodeValidator(mockBarcodeValidator);
        mockSampleTypeQueries = context.mock(SampleTypeQueries.class);
        mafFileValidator.setSampleTypeQueries(mockSampleTypeQueries);
        mockShippedBiospecimenQueries = context.mock(ShippedBiospecimenQueries.class);
        mafFileValidator.setShippedBiospecimenQueries(mockShippedBiospecimenQueries);
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

    private void setupForValidUUIDs() {
        context.checking(new Expectations() {{
            allowing(mockBarcodeValidator).validate(
                    with(any(String.class)), with(qcContext),
                    with(any(String.class)), with(true));
            will(returnValue(true));

            allowing(mockBarcodeValidator).validateUuid(
                    with(any(String.class)),
                    with(qcContext),
                    with(any(String.class)),
                    with(true));
            will(returnValue(true));

            allowing(mockBarcodeValidator).isMatchingDiseaseForUUID(
                    with(any(String.class)),
                    with(any(String.class)));
            will(returnValue(true));
        }});
    }

    @After
    public void tearDown() {
        mafFileValidator.cleanup();
    }

    @Test
    public void testMAFHeaderOK() throws Exception {
        setupForValidUUIDs();
        File mafFile = new File(SAMPLES_DIR
                + "qclive/mafFileValidator/goodMaf2/testMaf2.maf");
        qcContext.getArchive().setArchiveFile(
                new File(SAMPLES_DIR
                        + "qclive/mafFileValidator/goodMaf2/testMaf2.tar.gz"));
        boolean valid = mafFileValidator.execute(mafFile, qcContext);
        assertTrue("Errors: " + qcContext.getErrors(), valid);
    }

    @Test
    public void testMAFWithMissingUUIDHeader() throws Exception {
        setupForValidUUIDs();
        File mafFile = new File(SAMPLES_DIR
                + "qclive/mafFileValidator/goodMaf2/testMaf2.maf");
        qcContext.getArchive().setArchiveFile(
                new File(SAMPLES_DIR
                        + "qclive/mafFileValidator/goodMaf2/testMaf2.tar.gz"));

        qcContext.setCenterConvertedToUUID(true);
        boolean valid = mafFileValidator.execute(mafFile, qcContext);
        assertFalse(valid);
        assertEquals(1, qcContext.getErrors().size());
        assertTrue(qcContext.getErrors().get(0).contains("Required column 'Tumor_Sample_UUID' is missing"));

    }

    @Test
    public void testMAFWithUUIDHeader() throws Exception {
        setupForValidUUIDs();
        context.checking(new Expectations() {
            {
                allowing(mockShippedBiospecimenQueries).retrieveUUIDMetadata(
                        with("5760a312-43d7-42fb-b03d-b2c6728ab74a"));
                will(returnValue(
                        new MetaDataBean() {{
                            setTssCode("02");
                            setParticipantCode("0001");
                            setSampleCode("00");
                        }}
                ));

                allowing(mockShippedBiospecimenQueries).retrieveUUIDMetadata(
                        with("5760a312-43d7-42fb-b03d-b2c6728ab74b"));
                will(returnValue(
                        new MetaDataBean() {{
                            setTssCode("02");
                            setParticipantCode("0001");
                            setSampleCode("01");
                        }}
                ));

                one(mockBarcodeValidator).isAliquotUUID("5760a312-43d7-42fb-b03d-b2c6728ab74a");
                will(returnValue(true));
                one(mockBarcodeValidator).isAliquotUUID("5760a312-43d7-42fb-b03d-b2c6728ab74b");
                will(returnValue(true));

                one(mockBarcodeValidator).validateUUIDBarcodeMapping("5760a312-43d7-42fb-b03d-b2c6728ab74a", "TCGA-25-1317-00A-01W-0490-10");
                will(returnValue(true));

                one(mockBarcodeValidator).validateUUIDBarcodeMapping("5760a312-43d7-42fb-b03d-b2c6728ab74b", "TCGA-25-1317-01A-01W-0490-10");
                will(returnValue(true));
            }
        });

        File mafFile = new File(SAMPLES_DIR + "qclive/mafFileValidator/goodMaf2/mafWithUUIDHeader.maf");
        qcContext.getArchive().setArchiveFile(
                new File(SAMPLES_DIR + "qclive/mafFileValidator/goodMaf2/mafWithUUIDHeader.tar.gz"));
        qcContext.setCenterConvertedToUUID(true);
        boolean valid = mafFileValidator.execute(mafFile, qcContext);
        assertTrue(valid);
    }

    @Test
    public void testBadValues() throws Processor.ProcessorException,
            IOException, ParseException {
        File badMaf = new File(SAMPLES_DIR
                + "qclive/mafFileValidator/badMaf2/bad_values.maf");
        qcContext.getArchive().setArchiveFile(
                new File(SAMPLES_DIR
                        + "qclive/mafFileValidator/badMaf2/bad_values.tar.gz"));
        boolean valid = mafFileValidator.execute(badMaf, qcContext);
        assertFalse("Errors: " + qcContext.getErrors(), valid);
    }

    @Test
    public void testBadHeader() throws Processor.ProcessorException,
            IOException, ParseException {
        File badMaf = new File(SAMPLES_DIR
                + "qclive/mafFileValidator/badMaf2/testBadMaf2Header.maf");
        qcContext
                .getArchive()
                .setArchiveFile(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/badMaf2/testBadMaf2Header.tar.gz"));
        boolean valid = mafFileValidator.execute(badMaf, qcContext);
        assertFalse("Errors: " + qcContext.getErrors(), valid);
    }

    @Test
    public void testBadHeaderConditions() throws IOException,
            Processor.ProcessorException {
        File badCondition = new File(SAMPLES_DIR
                + "qclive/mafFileValidator/bad/bad_conditions.maf");
        qcContext.getArchive().setArchiveFile(
                new File(SAMPLES_DIR
                        + "qclive/mafFileValidator/bad/bad_conditions.tar.gz"));
        assertFalse(mafFileValidator.validate(badCondition, qcContext));
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("Required column 'Center' is missing", qcContext
                .getErrors().get(0));
    }

    @Test
    public void testValidateStandalone() throws Processor.ProcessorException {
        setupForValidUUIDs();
        mafFileValidator.setBarcodeTumorValidator(null);
        File mafFile = new File(SAMPLES_DIR
                + "qclive/mafFileValidator/goodMaf2/testMaf2.maf");
        qcContext.getArchive().setArchiveFile(
                new File(SAMPLES_DIR
                        + "qclive/mafFileValidator/goodMaf2/testMaf2.tar.gz"));
        boolean valid = mafFileValidator.execute(mafFile, qcContext);
        assertTrue("Errors: " + qcContext.getErrors(), valid);
    }

    @Test
    public void testMultiValueDbSnpStatus() throws Processor.ProcessorException {
        setupForValidUUIDs();
        qcContext
                .getArchive()
                .setArchiveFile(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/other/multiValue.v2.tar.gz"));
        boolean valid = mafFileValidator
                .execute(new File(SAMPLES_DIR
                        + "qclive/mafFileValidator/other/multiValue.v2.maf"),
                        qcContext);
        assertTrue(qcContext.getErrors().toString(), valid);
    }

    @Test
    public void testNullableConditions() throws IOException,
            Processor.ProcessorException {
        setupForValidUUIDs();
        File nullCondition = new File(SAMPLES_DIR
                + "qclive/mafFileValidator/validMaf2/NullMaf2.maf");
        qcContext.getArchive().setArchiveFile(
                new File(SAMPLES_DIR
                        + "qclive/mafFileValidator/validMaf2/NullMaf2.tar.gz"));
        boolean isValid = mafFileValidator.validate(nullCondition, qcContext);
        assertTrue(qcContext.getErrors().toString(), isValid);
        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testSeqPhase() throws IOException, Processor.ProcessorException {
        setupForValidUUIDs();
        File intCondition = new File(SAMPLES_DIR
                + "qclive/mafFileValidator/validMaf2/AriMaf2.maf");
        qcContext.getArchive().setArchiveFile(
                new File(SAMPLES_DIR
                        + "qclive/mafFileValidator/validMaf2/AriMaf2.tar.gz"));
        assertTrue(mafFileValidator.validate(intCondition, qcContext));
        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testBadVocab() throws IOException, Processor.ProcessorException {
        setupForValidUUIDs();
        File badVocab = new File(SAMPLES_DIR
                + "qclive/mafFileValidator/badMaf2/invalid_vocab.maf");
        qcContext
                .getArchive()
                .setArchiveFile(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/badMaf2/invalid_vocab.tar.gz"));
        assertFalse(mafFileValidator.validate(badVocab, qcContext));
        assertEquals(6, qcContext.getErrorCount());
        assertEquals(
                "An error occurred while validating MAF file 'invalid_vocab.maf', line 4: 'dbSNP_RS' value '()' is invalid - must be 'novel', dbSNP_ID, or blank",
                qcContext.getErrors().get(0));
        assertTrue(qcContext
                .getErrors()
                .get(1)
                .contains(
                        "An error occurred while validating MAF file 'invalid_vocab.maf', line 4: 'Variant_Classification' value 'MisSense_Mutation' is invalid"));
        assertTrue(qcContext
                .getErrors()
                .get(2)
                .contains(
                        "An error occurred while validating MAF file 'invalid_vocab.maf', line 5: 'Variant_Classification' value '' is invalid"));
        assertTrue(qcContext
                .getErrors()
                .get(3)
                .contains(
                        "An error occurred while validating MAF file 'invalid_vocab.maf', line 6: 'dbSNP_RS' value 'Squirrel' is invalid"));
        assertTrue(qcContext
                .getErrors()
                .get(4)
                .contains(
                        "An error occurred while validating MAF file 'invalid_vocab.maf', line 8: 'dbSNP_RS' value 'Tiger' is invalid"));
        assertTrue(qcContext
                .getErrors()
                .get(5)
                .contains(
                        "An error occurred while validating MAF file 'invalid_vocab.maf', line 10: '()' is an invalid value for 'dbSNP_Val_Status - "
                                + "must be by2Hit2Allele, byCluster, byFrequency, byHapMap, byOtherPop, bySubmitter, alternate_allele, by1000genomes, unknown, none, or blank'"));
    }

    @Test
    public void testNewVocab() throws IOException, Processor.ProcessorException {
        setupForValidUUIDs();
        File badVocab = new File(SAMPLES_DIR
                + "qclive/mafFileValidator/validMaf2/newVocab.maf");
        qcContext.getArchive().setArchiveFile(
                new File(SAMPLES_DIR
                        + "qclive/mafFileValidator/validMaf2/newVocab.tar.gz"));
        boolean isValid = mafFileValidator.validate(badVocab, qcContext);
        assertTrue(qcContext.getErrors().toString(), isValid);
        assertEquals(qcContext.getErrors().toString(), 0,
                qcContext.getErrorCount());
    }

    @Test
    public void testBadVocabForVariantType() throws IOException,
            Processor.ProcessorException {
        setupForValidUUIDs();
        File badVocab = new File(SAMPLES_DIR
                + "qclive/mafFileValidator/badMaf2/badVocab.maf");
        qcContext.getArchive().setArchiveFile(
                new File(SAMPLES_DIR
                        + "qclive/mafFileValidator/badMaf2/badVocab.tar.gz"));
        assertFalse(mafFileValidator.validate(badVocab, qcContext));
        assertEquals(qcContext.getErrors().toString(), 2,
                qcContext.getErrorCount());
        assertEquals(
                "An error occurred while validating MAF file 'badVocab.maf', line 4: 'Variant_Type' value 'snp' is invalid - must be SNP, INS, DNP, TNP, ONP, Consolidated or DEL",
                qcContext.getErrors().get(0));
        assertEquals(
                "An error occurred while validating MAF file 'badVocab.maf', line 8: 'Variant_Type' value 'missense' is invalid - must be SNP, INS, DNP, TNP, ONP, Consolidated or DEL",
                qcContext.getErrors().get(1));
    }

    @Test
    public void testNewMafRulesAPPS1779() throws Processor.ProcessorException,
            IOException {
        setupForValidUUIDs();
        // for APPS-1779
        File newRulesFile = new File(SAMPLES_DIR
                + "qclive/mafFileValidator/validMaf2/newRules.maf");
        qcContext.getArchive().setArchiveFile(
                new File(SAMPLES_DIR
                        + "qclive/mafFileValidator/validMaf2/newRules.tar.gz"));
        boolean isValid = mafFileValidator.validate(newRulesFile, qcContext);
        assertTrue(qcContext.getErrors().toString(), isValid);
        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testVariantTypeChecksAPPS1910()
            throws Processor.ProcessorException, IOException {
        setupForValidUUIDs();
        qcContext
                .getArchive()
                .setArchiveFile(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/goodMaf2/variantTypeChecks.tar.gz"));
        boolean isValid = mafFileValidator.validate(new File(SAMPLES_DIR
                + "qclive/mafFileValidator/goodMaf2/variantTypeChecks.maf"),
                qcContext);
        assertTrue(qcContext.getErrors().toString(), isValid);
        assertEquals(qcContext.getErrors().toString(), 0,
                qcContext.getErrorCount());
    }

    @Test
    public void testVariantTypeChecksINS() throws Processor.ProcessorException,
            IOException {
        setupForValidUUIDs();
        qcContext
                .getArchive()
                .setArchiveFile(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/badMaf2/variantTypeChecksBadIns.tar.gz"));
        boolean isValid = mafFileValidator
                .validate(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/badMaf2/variantTypeChecksBadIns.maf"),
                        qcContext);
        assertFalse(isValid);
        assertEquals(qcContext.getErrors().toString(), 4, qcContext.getErrors()
                .size());
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadIns.maf', line 3: if Variant_Type is INS then either End_Position - Start_Position = 1 or End_Position - Start_Position + 1 = length of Reference_Allele",
                qcContext.getErrors().get(0));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadIns.maf', line 4: if Variant_Type is INS then the length of Reference_Allele must be less than or equal to the length of both Tumor_Seq_Allele1 and Tumor_Seq_Allele2",
                qcContext.getErrors().get(1));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadIns.maf', line 5: if Variant_Type is INS then the length of Reference_Allele must be less than or equal to the length of both Tumor_Seq_Allele1 and Tumor_Seq_Allele2",
                qcContext.getErrors().get(2));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadIns.maf', line 6: if Variant_Type is INS and End_Position - Start_Position = 1 then ( End_Position - Start_Position + 1 = length of Reference_Allele or Reference_Allele should be '-' )",
                qcContext.getErrors().get(3));
    }

    @Test
    public void testVariantTypeChecksBadDEL()
            throws Processor.ProcessorException, IOException {
        setupForValidUUIDs();
        qcContext
                .getArchive()
                .setArchiveFile(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/badMaf2/variantTypeChecksBadDel.tar.gz"));
        boolean isValid = mafFileValidator
                .validate(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/badMaf2/variantTypeChecksBadDel.maf"),
                        qcContext);
        assertFalse(isValid);
        assertEquals(qcContext.getErrors().toString(), 5, qcContext.getErrors()
                .size());
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadDel.maf', line 3: if Variant_Type is DEL then End_Position - Start_Position + 1 should be equal to the length of the Reference_Allele",
                qcContext.getErrors().get(0));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadDel.maf', line 4: if Variant_Type is DEL then the length of Reference_Allele must be greater than or equal to the length of both Tumor_Seq_Allele1 and Tumor_Seq_Allele2",
                qcContext.getErrors().get(1));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadDel.maf', line 4: if Variant_Type is DEL then the length of Reference_Allele must be greater than the length of Tumor_Seq_Allele1 or Tumor_Seq_Allele2",
                qcContext.getErrors().get(2));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadDel.maf', line 5: if Variant_Type is DEL then the length of Reference_Allele must be greater than or equal to the length of both Tumor_Seq_Allele1 and Tumor_Seq_Allele2",
                qcContext.getErrors().get(3));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadDel.maf', line 5: if Variant_Type is DEL then the length of Reference_Allele must be greater than the length of Tumor_Seq_Allele1 or Tumor_Seq_Allele2",
                qcContext.getErrors().get(4));
    }

    @Test
    public void testVariantTypeChecksBadSNP()
            throws Processor.ProcessorException, IOException {
        setupForValidUUIDs();
        qcContext
                .getArchive()
                .setArchiveFile(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/badMaf2/variantTypeChecksBadSNP.tar.gz"));
        boolean isValid = mafFileValidator
                .validate(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/badMaf2/variantTypeChecksBadSNP.maf"),
                        qcContext);
        assertFalse(isValid);
        assertEquals(9, qcContext.getErrors().size());
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadSNP.maf', line 3: if Variant_Type is SNP then Reference_Allele length must be 1",
                qcContext.getErrors().get(0));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadSNP.maf', line 4: if Variant_Type is SNP then Tumor_Seq_Allele1 length must be 1",
                qcContext.getErrors().get(1));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadSNP.maf', line 5: if Variant_Type is SNP then Tumor_Seq_Allele2 length must be 1",
                qcContext.getErrors().get(2));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadSNP.maf', line 6: if Variant_Type is SNP then Reference_Allele length must be 1",
                qcContext.getErrors().get(3));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadSNP.maf', line 6: if Variant_Type is SNP then Reference_Allele value must not be '-'",
                qcContext.getErrors().get(4));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadSNP.maf', line 7: if Variant_Type is SNP then Tumor_Seq_Allele1 length must be 1",
                qcContext.getErrors().get(5));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadSNP.maf', line 7: if Variant_Type is SNP then Tumor_Seq_Allele1 value must not be '-'",
                qcContext.getErrors().get(6));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadSNP.maf', line 8: if Variant_Type is SNP then Tumor_Seq_Allele2 length must be 1",
                qcContext.getErrors().get(7));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadSNP.maf', line 8: if Variant_Type is SNP then Tumor_Seq_Allele2 value must not be '-'",
                qcContext.getErrors().get(8));
    }

    @Test
    public void testVariantTypeChecksBadDNP()
            throws Processor.ProcessorException, IOException {
        setupForValidUUIDs();
        qcContext
                .getArchive()
                .setArchiveFile(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/badMaf2/variantTypeChecksBadDNP.tar.gz"));
        boolean isValid = mafFileValidator
                .validate(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/badMaf2/variantTypeChecksBadDNP.maf"),
                        qcContext);
        assertFalse(isValid);
        assertEquals(6, qcContext.getErrors().size());
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadDNP.maf', line 3: if Variant_Type is DNP then Reference_Allele length must be 2",
                qcContext.getErrors().get(0));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadDNP.maf', line 4: if Variant_Type is DNP then Tumor_Seq_Allele1 length must be 2",
                qcContext.getErrors().get(1));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadDNP.maf', line 5: if Variant_Type is DNP then Tumor_Seq_Allele2 length must be 2",
                qcContext.getErrors().get(2));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadDNP.maf', line 6: if Variant_Type is DNP then Reference_Allele value must not contain '-'",
                qcContext.getErrors().get(3));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadDNP.maf', line 7: if Variant_Type is DNP then Tumor_Seq_Allele1 value must not contain '-'",
                qcContext.getErrors().get(4));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadDNP.maf', line 8: if Variant_Type is DNP then Tumor_Seq_Allele2 value must not contain '-'",
                qcContext.getErrors().get(5));
    }

    @Test
    public void testVariantTypeChecksBadTNP()
            throws Processor.ProcessorException, IOException {
        setupForValidUUIDs();
        qcContext
                .getArchive()
                .setArchiveFile(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/badMaf2/variantTypeChecksBadTNP.tar.gz"));
        boolean isValid = mafFileValidator
                .validate(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/badMaf2/variantTypeChecksBadTNP.maf"),
                        qcContext);
        assertFalse(isValid);
        assertEquals(6, qcContext.getErrors().size());
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadTNP.maf', line 3: if Variant_Type is TNP then Reference_Allele length must be 3",
                qcContext.getErrors().get(0));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadTNP.maf', line 4: if Variant_Type is TNP then Tumor_Seq_Allele1 length must be 3",
                qcContext.getErrors().get(1));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadTNP.maf', line 5: if Variant_Type is TNP then Tumor_Seq_Allele2 length must be 3",
                qcContext.getErrors().get(2));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadTNP.maf', line 6: if Variant_Type is TNP then Reference_Allele value must not contain '-'",
                qcContext.getErrors().get(3));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadTNP.maf', line 7: if Variant_Type is TNP then Tumor_Seq_Allele1 value must not contain '-'",
                qcContext.getErrors().get(4));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadTNP.maf', line 8: if Variant_Type is TNP then Tumor_Seq_Allele2 value must not contain '-'",
                qcContext.getErrors().get(5));
    }

    @Test
    public void testVariantTypeChecksBadONP()
            throws Processor.ProcessorException, IOException {
        setupForValidUUIDs();
        qcContext
                .getArchive()
                .setArchiveFile(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/badMaf2/variantTypeChecksBadONP.tar.gz"));
        boolean isValid = mafFileValidator
                .validate(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/badMaf2/variantTypeChecksBadONP.maf"),
                        qcContext);
        assertFalse(isValid);
        assertEquals(7, qcContext.getErrors().size());
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadONP.maf', line 3: if Variant_Type is ONP then Reference_Allele length must be >3 (use SNP, DNP, or TNP)",
                qcContext.getErrors().get(0));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadONP.maf', line 3: if Variant_Type is ONP then Tumor_Seq_Allele1 length must be >3 (use SNP, DNP, or TNP)",
                qcContext.getErrors().get(1));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadONP.maf', line 3: if Variant_Type is ONP then Tumor_Seq_Allele2 length must be >3 (use SNP, DNP, or TNP)",
                qcContext.getErrors().get(2));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadONP.maf', line 4: if Variant_Type is ONP then Reference_Allele value must not contain '-'",
                qcContext.getErrors().get(3));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadONP.maf', line 5: if Variant_Type is ONP then Tumor_Seq_Allele1 value must not contain '-'",
                qcContext.getErrors().get(4));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadONP.maf', line 6: if Variant_Type is ONP then Tumor_Seq_Allele2 value must not contain '-'",
                qcContext.getErrors().get(5));
        assertEquals(
                "An error occurred while validating MAF file 'variantTypeChecksBadONP.maf', line 7: if Variant_Type is ONP then Reference_Allele, Tumor_Seq_Allele1, and Tumor_Seq_Allele2 values must be the same length",
                qcContext.getErrors().get(6));
    }

    @Test
    public void testWildtypeValidationStatusChecks()
            throws Processor.ProcessorException, IOException {
        setupForValidUUIDs();
        qcContext
                .getArchive()
                .setArchiveFile(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/other/wildtypeValidationStatusChecks.tar.gz"));
        boolean isValid = mafFileValidator
                .validate(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/other/wildtypeValidationStatusChecks.maf"),
                        qcContext);
        assertTrue(qcContext.getErrors().toString(), isValid);
    }

    @Test
    public void testWildtypeValidationStatusBad()
            throws Processor.ProcessorException, IOException {
        setupForValidUUIDs();
        qcContext
                .getArchive()
                .setArchiveFile(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/badMaf2/wildtypeValidationStatusBad.tar.gz"));
        boolean isValid = mafFileValidator
                .validate(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/badMaf2/wildtypeValidationStatusBad.maf"),
                        qcContext);
        assertFalse(qcContext.getErrors().toString(), isValid);
        assertEquals(qcContext.getErrors().toString(), 8, qcContext.getErrors()
                .size());
        assertEquals(
                "An error occurred while validating MAF file 'wildtypeValidationStatusBad.maf', line 3: Match_Norm_Validation_Allele2 (A) must be equal to Reference_Allele when Validation_Status is 'Wildtype'",
                qcContext.getErrors().get(0));
        assertEquals(
                "An error occurred while validating MAF file 'wildtypeValidationStatusBad.maf', line 4: Tumor_Validation_Allele1 (T) must be equal to Tumor_Validation_Allele2 when Validation_Status is 'Wildtype'",
                qcContext.getErrors().get(1));
        assertEquals(
                "An error occurred while validating MAF file 'wildtypeValidationStatusBad.maf', line 5: Tumor_Validation_Allele1 (A) must be equal to Tumor_Validation_Allele2 when Validation_Status is 'Wildtype'",
                qcContext.getErrors().get(2));
        assertEquals(
                "An error occurred while validating MAF file 'wildtypeValidationStatusBad.maf', line 5: Tumor_Validation_Allele2 (T) must be equal to Match_Norm_Validation_Allele1 when Validation_Status is 'Wildtype'",
                qcContext.getErrors().get(3));
        assertEquals(
                "An error occurred while validating MAF file 'wildtypeValidationStatusBad.maf', line 6: Tumor_Validation_Allele2 (A) must be equal to Match_Norm_Validation_Allele1 when Validation_Status is 'Wildtype'",
                qcContext.getErrors().get(4));
        assertEquals(
                "An error occurred while validating MAF file 'wildtypeValidationStatusBad.maf', line 6: Match_Norm_Validation_Allele1 (T) must be equal to Match_Norm_Validation_Allele2 when Validation_Status is 'Wildtype'",
                qcContext.getErrors().get(5));
        assertEquals(
                "An error occurred while validating MAF file 'wildtypeValidationStatusBad.maf', line 7: Match_Norm_Validation_Allele1 (A) must be equal to Match_Norm_Validation_Allele2 when Validation_Status is 'Wildtype'",
                qcContext.getErrors().get(6));
        assertEquals(
                "An error occurred while validating MAF file 'wildtypeValidationStatusBad.maf', line 7: Match_Norm_Validation_Allele2 (T) must be equal to Reference_Allele when Validation_Status is 'Wildtype'",
                qcContext.getErrors().get(7));
    }

    @Test
    public void testMutationStatusChecks() throws Processor.ProcessorException,
            IOException {
        setupForValidUUIDs();
        qcContext
                .getArchive()
                .setArchiveFile(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/other/mutationStatusChecks.tar.gz"));
        boolean isValid = mafFileValidator.validate(new File(SAMPLES_DIR
                + "qclive/mafFileValidator/other/mutationStatusChecks.maf"),
                qcContext);
        assertTrue(qcContext.getErrors().toString(), isValid);
        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testMutationFileGerm()
            throws Processor.ProcessorException, IOException {
        setupForValidUUIDs();
        qcContext
                .getArchive()
                .setArchiveFile(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/badMaf2/mutationStatusChecksBadGermline.tar.gz"));
        boolean isValid = mafFileValidator
                .validate(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/badMaf2/mutationStatusChecksBadGermline.maf"),
                        qcContext);
        assertFalse(isValid);
        assertEquals(
				"An error occurred while validating MAF file 'mutationStatusChecksBadGermline.maf', line 3: Tumor_Validation_Allele1 (A) must be equal to Match_Norm_Validation_Allele1 when Mutation_Status is 'Germline' and Validation_Status is 'Valid'",
				qcContext.getErrors().get(0));
		assertEquals(
				"An error occurred while validating MAF file 'mutationStatusChecksBadGermline.maf', line 4: Tumor_Validation_Allele2 (G) must be equal to Match_Norm_Validation_Allele2 when Mutation_Status is 'Germline' and Validation_Status is 'Valid'",
				qcContext.getErrors().get(1));
		assertEquals(
				"An error occurred while validating MAF file 'mutationStatusChecksBadGermline.maf', line 5: Tumor_Seq_Allele1 (AAA) must be equal to Match_Norm_Seq_Allele1 when Mutation_Status is 'Germline' and Validation_Status is 'Unknown'",
				qcContext.getErrors().get(2));
		assertEquals(
				"An error occurred while validating MAF file 'mutationStatusChecksBadGermline.maf', line 6: Tumor_Seq_Allele2 (G) must be equal to Match_Norm_Seq_Allele2 when Mutation_Status is 'Germline' and Validation_Status is 'Unknown'",
				qcContext.getErrors().get(3));
    }

    @Test
    public void testMutationStatusSomaticBad()
            throws Processor.ProcessorException, IOException {
        setupForValidUUIDs();
        qcContext
                .getArchive()
                .setArchiveFile(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/badMaf2/mutationStatusChecksBadSomatic.tar.gz"));
        boolean isValid = mafFileValidator
                .validate(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/badMaf2/mutationStatusChecksBadSomatic.maf"),
                        qcContext);
        assertFalse(isValid);
        assertEquals(qcContext.getErrors().toString(), 6,
                qcContext.getErrorCount());
        assertEquals(
                "An error occurred while validating MAF file 'mutationStatusChecksBadSomatic.maf', line 3: Match_Norm_Validation_Allele1 (G) must be equal to Match_Norm_Validation_Allele2 when Mutation_Status is 'Somatic' and Validation_Status is 'Valid'",
                qcContext.getErrors().get(0));
        assertEquals(
                "An error occurred while validating MAF file 'mutationStatusChecksBadSomatic.maf', line 4: Tumor_Validation_Allele1 (G) must not be equal to Reference_Allele OR Tumor_Validation_Allele2 (G) must not be equal to Reference_Allele when Mutation_Status is 'Somatic' and Validation_Status is 'Valid'",
                qcContext.getErrors().get(1));
        assertEquals(
                "An error occurred while validating MAF file 'mutationStatusChecksBadSomatic.maf', line 5: Match_Norm_Seq_Allele1 (A) must be equal to Reference_Allele when Mutation_Status is 'Somatic' and Validation_Status is 'Unknown'",
                qcContext.getErrors().get(2));
        assertEquals(
                "An error occurred while validating MAF file 'mutationStatusChecksBadSomatic.maf', line 6: Match_Norm_Seq_Allele1 (G) must be equal to Match_Norm_Seq_Allele2 when Mutation_Status is 'Somatic' and Validation_Status is 'Unknown'",
                qcContext.getErrors().get(3));
        assertEquals(
                "An error occurred while validating MAF file 'mutationStatusChecksBadSomatic.maf', line 7: Match_Norm_Seq_Allele1 (AA) must be equal to Match_Norm_Seq_Allele2 when Mutation_Status is 'Somatic' and Validation_Status is 'Unknown'",
                qcContext.getErrors().get(4));
        assertEquals(
                "An error occurred while validating MAF file 'mutationStatusChecksBadSomatic.maf', line 7: Match_Norm_Seq_Allele1 (AA) must be equal to Reference_Allele when Mutation_Status is 'Somatic' and Validation_Status is 'Unknown'",
                qcContext.getErrors().get(5));
    }

    @Test
    public void testMutationStatusLOHBad() throws Processor.ProcessorException,
            IOException {
        setupForValidUUIDs();
        qcContext
                .getArchive()
                .setArchiveFile(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/badMaf2/mutationStatusChecksBadLOH.tar.gz"));
        boolean isValid = mafFileValidator
                .validate(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/badMaf2/mutationStatusChecksBadLOH.maf"),
                        qcContext);
        assertFalse(isValid);
        assertEquals(qcContext.getErrors().toString(), 6,
                qcContext.getErrorCount());
        assertEquals(
				"An error occurred while validating MAF file 'mutationStatusChecksBadLOH.maf', line 3: Tumor_Validation_Allele1 (A) must be equal to Tumor_Validation_Allele2 when Mutation_Status is 'LOH' and Validation_Status is 'Valid'",
				qcContext.getErrors().get(0));
		assertEquals(
				"An error occurred while validating MAF file 'mutationStatusChecksBadLOH.maf', line 4: Match_Norm_Validation_Allele1 (G) must not be equal to Match_Norm_Validation_Allele2 when Mutation_Status is 'LOH' and Validation_Status is 'Valid'",
				qcContext.getErrors().get(1));
		assertEquals(
				"An error occurred while validating MAF file 'mutationStatusChecksBadLOH.maf', line 5: Tumor_Validation_Allele1 (T) must be equal to Match_Norm_Validation_Allele1 OR Tumor_Validation_Allele1 (T) must be equal to Match_Norm_Validation_Allele2 when Mutation_Status is 'LOH' and Validation_Status is 'Valid'",
				qcContext.getErrors().get(2));
		assertEquals(
				"An error occurred while validating MAF file 'mutationStatusChecksBadLOH.maf', line 6: Tumor_Seq_Allele1 (A) must be equal to Tumor_Seq_Allele2 when Mutation_Status is 'LOH' and Validation_Status is 'Unknown'",
				qcContext.getErrors().get(3));
		assertEquals(
				"An error occurred while validating MAF file 'mutationStatusChecksBadLOH.maf', line 7: Match_Norm_Seq_Allele1 (A) must not be equal to Match_Norm_Seq_Allele2 when Mutation_Status is 'LOH' and Validation_Status is 'Unknown'",
				qcContext.getErrors().get(4));
		assertEquals(
				"An error occurred while validating MAF file 'mutationStatusChecksBadLOH.maf', line 8: Tumor_Seq_Allele1 (G) must be equal to Match_Norm_Seq_Allele1 OR Tumor_Seq_Allele1 (G) must be equal to Match_Norm_Seq_Allele2 when Mutation_Status is 'LOH' and Validation_Status is 'Unknown'",
				qcContext.getErrors().get(5));
    }

    @Test
    public void testIlluminaHiSeq() throws Processor.ProcessorException,
            IOException {
        setupForValidUUIDs();
        qcContext.getArchive().setArchiveFile(new File(SAMPLES_DIR
                + "qclive/mafFileValidator/validMaf2/illuminaHiSeq.tar.gz"));
        boolean isValid = mafFileValidator.validate(new File(SAMPLES_DIR
                + "qclive/mafFileValidator/validMaf2/illuminaHiSeq.maf"),
                qcContext);
        assertTrue(isValid);
    }

    @Test
    public void testMaf2SequencerValuesGood() throws Exception {
        setupForValidUUIDs();
        qcContext.getArchive().setArchiveFile(new File(SAMPLES_DIR
                + "qclive/mafFileValidator/validMaf2/GoodMaf2Sequencer.tar.gz"));
        boolean isValid = mafFileValidator.validate(new File(SAMPLES_DIR
                + "qclive/mafFileValidator/validMaf2/GoodMaf2Sequencer.maf"),
                qcContext);
        assertTrue(isValid);
    }

    @Test
    public void testMaf2SequencerValuesBad() throws Exception {
        setupForValidUUIDs();
        qcContext.getArchive().setArchiveFile(new File(SAMPLES_DIR
                + "qclive/mafFileValidator/validMaf2/BadMaf2Sequencer.tar.gz"));
        boolean isValid = mafFileValidator.validate(new File(SAMPLES_DIR
                + "qclive/mafFileValidator/validMaf2/BadMaf2Sequencer.maf"),
                qcContext);
        assertFalse(isValid);
        assertEquals(qcContext.getErrors().toString(), 1,
                qcContext.getErrorCount());
        assertEquals("An error occurred while validating MAF file 'BadMaf2Sequencer.maf', line 3: " +
                "'Sequencer' value 'Yikes!' is invalid - must be one or more of Illumina GAIIx, " +
                "Illumina HiSeq, SOLID, 454, ABI 3730xl, Ion Torrent PGM, Ion Torrent Proton, " +
                "PacBio RS, Illumina MiSeq, Illumina HiSeq 2500, 454 GS FLX Titanium, " +
                "AB SOLiD 4 System; separate multiple entries with semicolon",
                qcContext.getErrors().get(0));
    }

    @Test
    public void testNegativeStrandNotAllowed()
            throws Processor.ProcessorException, IOException {
        setupForValidUUIDs();
        qcContext
                .getArchive()
                .setArchiveFile(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/badMaf2/negativeStrand.tar.gz"));
        boolean isValid = mafFileValidator.validate(new File(SAMPLES_DIR
                + "qclive/mafFileValidator/badMaf2/negativeStrand.maf"),
                qcContext);
        assertFalse(isValid);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals(
                "An error occurred while validating MAF file 'negativeStrand.maf', line 3: 'Strand' value '-' is invalid - must be +",
                qcContext.getErrors().get(0));
    }

    @Test
    public void testDashAlleleLength() throws Processor.ProcessorException,
            IOException {
        setupForValidUUIDs();
        qcContext
                .getArchive()
                .setArchiveFile(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/badMaf2/emptyAllele.tar.gz"));
        boolean isValid = mafFileValidator
                .validate(new File(SAMPLES_DIR
                        + "qclive/mafFileValidator/badMaf2/emptyAllele.maf"),
                        qcContext);
        assertFalse(isValid);
        assertEquals(3, qcContext.getErrorCount());
        assertEquals(
                "An error occurred while validating MAF file 'emptyAllele.maf', line 3: if Variant_Type is DEL then End_Position - Start_Position + 1 should be equal to the length of the Reference_Allele",
                qcContext.getErrors().get(0));
        assertEquals(
                "An error occurred while validating MAF file 'emptyAllele.maf', line 3: if Variant_Type is DEL then the length of Reference_Allele must be greater than or equal to the length of both Tumor_Seq_Allele1 and Tumor_Seq_Allele2",
                qcContext.getErrors().get(1));
        assertEquals(
                "An error occurred while validating MAF file 'emptyAllele.maf', line 3: if Variant_Type is DEL then the length of Reference_Allele must be greater than the length of Tumor_Seq_Allele1 or Tumor_Seq_Allele2",
                qcContext.getErrors().get(2));
    }

    @Test
    public void testNcbiBuild() throws Processor.ProcessorException,
            IOException {
        setupForValidUUIDs();
        qcContext
                .getArchive()
                .setArchiveFile(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/validMaf2/ncbiBuild.tar.gz"));
        boolean isValid = mafFileValidator
                .validate(new File(SAMPLES_DIR
                        + "qclive/mafFileValidator/validMaf2/ncbiBuild.maf"),
                        qcContext);
        assertTrue(isValid);
    }

    @Test
    public void testRowNumWithError() throws Processor.ProcessorException {
        setupForValidUUIDs();

        context.checking(new Expectations() {{
            allowing(mockCenterQueries).findCenterId("hgsc.bcm.3du", "GSC");
            will(returnValue(null));

            allowing(mockCenterQueries).findCenterId("hgsc.bcm.1du", "GSC");
            will(returnValue(null));
        }});

        qcContext
                .getArchive()
                .setArchiveFile(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/badMaf2/rownumTestError.tar.gz"));
        boolean valid = mafFileValidator.execute(new File(SAMPLES_DIR
                + "qclive/mafFileValidator/badMaf2/rownumTestError.maf"),
                qcContext);
        assertFalse(valid);
        assertEquals(6, qcContext.getErrorCount());
        assertEquals("An error occurred while validating MAF file 'rownumTestError.maf', line 5: if Variant_Type is INS then the length of Reference_Allele must be less than or equal to the length of both Tumor_Seq_Allele1 and Tumor_Seq_Allele2",
                qcContext.getErrors().get(0));
        assertEquals(
                "An error occurred while validating MAF file 'rownumTestError.maf', line 5: Center value 'hgsc.bcm.3du' is not a valid GSC domain",
                qcContext.getErrors().get(1));
        assertEquals(
                "An error occurred while validating MAF file 'rownumTestError.maf', line 7: Center value 'hgsc.bcm.1du' is not a valid GSC domain",
                qcContext.getErrors().get(2));
        assertEquals(
                "An error occurred while validating MAF file 'rownumTestError.maf', line 12: 'NCBI_Build' value '36df' is invalid - must be a decimal number e.g. 36.1",
                qcContext.getErrors().get(3));
        assertEquals(
                "An error occurred while validating MAF file 'rownumTestError.maf', line 20: 'NCBI_Build' value '36e' is invalid - must be a decimal number e.g. 36.1",
                qcContext.getErrors().get(4));
        assertEquals(
                "An error occurred while validating MAF file 'rownumTestError.maf', line 22: 'NCBI_Build' value '36f' is invalid - must be a decimal number e.g. 36.1",
                qcContext.getErrors().get(5));

    }

    @Test
    public void testRowNumWithNoError() throws Processor.ProcessorException {
        setupForValidUUIDs();
        qcContext
                .getArchive()
                .setArchiveFile(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/goodMaf2/rownumTestNoError.tar.gz"));
        boolean valid = mafFileValidator.execute(new File(SAMPLES_DIR
                + "qclive/mafFileValidator/goodMaf2/rownumTestNoError.maf"),
                qcContext);
        assertTrue(valid);
        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testGoodFileNameMatchWithArchiveFolder()
            throws Processor.ProcessorException {
        setupForValidUUIDs();
        qcContext
                .getArchive()
                .setArchiveFile(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/test_archive_name.1.1.0.tar.gz"));

        boolean valid = mafFileValidator
                .execute(
                        new File(
                                SAMPLES_DIR
                                        + "qclive/mafFileValidator/goodMaf2/test_archive_name.1.1.0.maf"),
                        qcContext);
        assertTrue(valid);
        assertEquals(0, qcContext.getWarningCount());
    }

    @Test
    public void testBadCenterValues() throws Processor.ProcessorException {
        setupForValidUUIDs();
        context.checking(new Expectations() {{
            one(mockCenterQueries).findCenterId("HELLO", "GSC");
            will(returnValue(null));
            one(mockCenterQueries).findCenterId("WHAT", "GSC");
            will(returnValue(null));
        }});

        final File fileToTest = new File(SAMPLES_DIR + "qclive/mafFileValidator/badMaf2/badCenter.maf");
        qcContext.getArchive().setArchiveFile(
                new File(SAMPLES_DIR + "qclive/mafFileValidator/test_archive_name.1.1.0.tar.gz"));

        boolean valid = mafFileValidator.execute(fileToTest, qcContext);
        assertFalse(valid);
        assertEquals(3, qcContext.getErrorCount());
        assertEquals("An error occurred while validating MAF file 'badCenter.maf', line 4: Center value 'HELLO' is not a valid GSC domain", qcContext.getErrors().get(0));
        assertEquals("An error occurred while validating MAF file 'badCenter.maf', line 6: Center value 'WHAT' is not a valid GSC domain", qcContext.getErrors().get(1));
        assertEquals("An error occurred while validating MAF file 'badCenter.maf', line 8: multiple Center values must be delimited by semicolons", qcContext.getErrors().get(2));
    }

    @Test
    public void testIncorrectDiseaseUUID() throws Processor.ProcessorException {
        qcContext.getArchive().setTumorType("TEST");
        context.checking(new Expectations() {
            {
                allowing(mockBarcodeValidator).validateUuid("5760a312-43d7-42fb-b03d-b2c6728ab74a", qcContext, "mafWithUUIDHeader.maf", true);
                will(returnValue(true));

                allowing(mockBarcodeValidator).isMatchingDiseaseForUUID("5760a312-43d7-42fb-b03d-b2c6728ab74a", "TEST");
                will(returnValue(false));

                allowing(mockBarcodeValidator).validateUuid("5760a312-43d7-42fb-b03d-b2c6728ab74b", qcContext, "mafWithUUIDHeader.maf", true);
                will(returnValue(true));

                allowing(mockBarcodeValidator).isMatchingDiseaseForUUID("5760a312-43d7-42fb-b03d-b2c6728ab74b", "TEST");
                will(returnValue(true));


                allowing(mockShippedBiospecimenQueries).retrieveUUIDMetadata(
                        with("5760a312-43d7-42fb-b03d-b2c6728ab74a"));
                will(returnValue(
                        new MetaDataBean() {{
                            setTssCode("02");
                            setParticipantCode("0001");
                            setSampleCode("00");
                        }}
                ));

                allowing(mockShippedBiospecimenQueries).retrieveUUIDMetadata(
                        with("5760a312-43d7-42fb-b03d-b2c6728ab74b"));
                will(returnValue(
                        new MetaDataBean() {{
                            setTssCode("02");
                            setParticipantCode("0001");
                            setSampleCode("01");
                        }}
                ));

            }
        });

        File mafFile = new File(SAMPLES_DIR + "qclive/mafFileValidator/goodMaf2/mafWithUUIDHeader.maf");
        qcContext.getArchive().setArchiveFile(
                new File(SAMPLES_DIR + "qclive/mafFileValidator/goodMaf2/mafWithUUIDHeader.tar.gz"));
        qcContext.setCenterConvertedToUUID(true);
        boolean valid = mafFileValidator.execute(mafFile, qcContext);
        assertFalse(valid);
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("An error occurred while validating MAF file 'mafWithUUIDHeader.maf', line 3: UUID 5760a312-43d7-42fb-b03d-b2c6728ab74a is not part of disease set for TEST",
                qcContext.getErrors().get(0));
    }
}
