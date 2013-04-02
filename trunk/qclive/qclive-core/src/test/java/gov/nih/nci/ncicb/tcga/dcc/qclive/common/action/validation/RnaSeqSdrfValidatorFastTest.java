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
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContentImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedFileParser;
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
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test class for RNASeq SDRF validator.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class RnaSeqSdrfValidatorFastTest {
    private Mockery context;
    private BarcodeTumorValidator mockBarcodeTumorValidator;
    private QcLiveBarcodeAndUUIDValidator mockQcLiveBarcodeAndUUIDValidator;
    private RnaSeqSdrfValidator validator;
    private Archive archive;
    private TabDelimitedContent sdrf;
    private QcContext qcContext;

    private static final String SAMPLES_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;
    private static final String TEST_DIR = SAMPLES_DIR + File.separator
            + "qclive" + File.separator + "rnaSeqSdrfValidator"
            + File.separator;

    @Before
    public void setUp() {
        context = new JUnit4Mockery();
        mockBarcodeTumorValidator = context.mock(BarcodeTumorValidator.class);
        mockQcLiveBarcodeAndUUIDValidator = context
                .mock(QcLiveBarcodeAndUUIDValidator.class);

        validator = new RnaSeqSdrfValidator();
        validator.setBarcodeTumorValidator(mockBarcodeTumorValidator);
        validator.setQcLiveBarcodeAndUUIDValidator(mockQcLiveBarcodeAndUUIDValidator);

        archive = new Archive();
        archive.setArchiveType(Archive.TYPE_MAGE_TAB);
        sdrf = new TabDelimitedContentImpl();
        archive.setSdrf(sdrf);
        archive.setTumorType("TEST");
        archive.setSdrfFile(new File("test.sdrf.txt"));

        qcContext = new QcContext();
        qcContext.setArchive(archive);
    }

    private void setSdrf(final String filename) throws IOException, ParseException {
        TabDelimitedFileParser sdrfParser = new TabDelimitedFileParser();
        sdrfParser.setTabDelimitedContent(sdrf);
        sdrfParser.loadTabDelimitedContent(filename);
        sdrfParser.loadTabDelimitedContentHeader();
    }

    @Test
    public void testValidWithUUIDsNotConverted() throws IOException, ParseException, Processor.ProcessorException {

        context.checking(new Expectations() {{
            exactly(6).of(mockQcLiveBarcodeAndUUIDValidator).validateUuid("11111111-2222-3333-4444-abcdefabcdef", qcContext, "good.uuids.sdrf.txt", true);
            will(returnValue(true));
        }});

        archive.setPlatform("IlluminaHiSeq_RNASeqV2");
        qcContext.setCenterConvertedToUUID(false);
        archive.setArchiveFile(new File(SAMPLES_DIR + "qclive" + File.separator
                + "rnaSeqSdrfValidator" + File.separator + "valid.tar.gz"));
        archive.setSdrfFile(new File("good.uuids.sdrf.txt"));
        setSdrf(SAMPLES_DIR + "qclive" + File.separator + "rnaSeqSdrfValidator"
                + File.separator + "valid" + File.separator + "good.uuids.sdrf.txt");
        final boolean isValid = validator.doWork(archive, qcContext);
        assertEquals(0, qcContext.getErrorCount());
        assertTrue(qcContext.getErrors().toString(), isValid);
    }

    @Test
    public void testValidWithUUIDsNotConvertedStandalone() throws IOException, ParseException, Processor.ProcessorException {
        final Map<String, Boolean> batchResults = new HashMap<String, Boolean>();
        batchResults.put("11111111-2222-3333-4444-abcdefabcdef", true);
        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).batchValidateUUIDsReportIndividualResults(Arrays.asList("11111111-2222-3333-4444-abcdefabcdef"), qcContext, "good.uuids.sdrf.txt", true);
            will(returnValue(batchResults));
        }});

        qcContext.setStandaloneValidator(true);
        qcContext.setCenterConvertedToUUID(false);
        archive.setPlatform("IlluminaHiSeq_RNASeqV2");
        archive.setArchiveFile(new File(SAMPLES_DIR + "qclive" + File.separator
                + "rnaSeqSdrfValidator" + File.separator + "valid.tar.gz"));
        archive.setSdrfFile(new File("good.uuids.sdrf.txt"));
        setSdrf(SAMPLES_DIR + "qclive" + File.separator + "rnaSeqSdrfValidator"
                + File.separator + "valid" + File.separator + "good.uuids.sdrf.txt");
        final boolean isValid = validator.doWork(archive, qcContext);
        assertEquals(0, qcContext.getErrorCount());
        assertTrue(qcContext.getErrors().toString(), isValid);
    }

    @Test
    public void testValidWithUUIDsConverted() throws IOException, ParseException, Processor.ProcessorException {
        context.checking(new Expectations() {{
            exactly(6).of(mockQcLiveBarcodeAndUUIDValidator).isMatchingDiseaseForUUID("11111111-2222-3333-4444-abcdefabcdef", "TEST");
            will(returnValue(true));

            exactly(6).of(mockQcLiveBarcodeAndUUIDValidator).validateUuid("11111111-2222-3333-4444-abcdefabcdef", qcContext, "good.uuids.sdrf.txt", true);
            will(returnValue(true));

            exactly(6).of(mockQcLiveBarcodeAndUUIDValidator).isAliquotUUID("11111111-2222-3333-4444-abcdefabcdef");
            will(returnValue(true));

            exactly(6).of(mockQcLiveBarcodeAndUUIDValidator).validateUUIDBarcodeMapping("11111111-2222-3333-4444-abcdefabcdef", "TCGA-AO-A0J9-01A-11R-A034-07");
            will(returnValue(true));
        }});

        archive.setPlatform("IlluminaHiSeq_RNASeqV2");
        qcContext.setCenterConvertedToUUID(true);
        archive.setArchiveFile(new File(SAMPLES_DIR + "qclive" + File.separator
                + "rnaSeqSdrfValidator" + File.separator + "valid.tar.gz"));
        archive.setSdrfFile(new File("good.uuids.sdrf.txt"));
        setSdrf(SAMPLES_DIR + "qclive" + File.separator + "rnaSeqSdrfValidator"
                + File.separator + "valid" + File.separator + "good.uuids.sdrf.txt");
        final boolean isValid = validator.doWork(archive, qcContext);
        assertEquals(0, qcContext.getErrorCount());
        assertTrue(qcContext.getErrors().toString(), isValid);
    }

    @Test
    public void testValidWithUUIDsConvertedStandaloneNoRemote() throws IOException, ParseException, Processor.ProcessorException {
        final Map<String, Boolean> batchResults = new HashMap<String, Boolean>();
        batchResults.put("11111111-2222-3333-4444-abcdefabcdef", true);
        context.checking(new Expectations() {{
            exactly(6).of(mockQcLiveBarcodeAndUUIDValidator).isMatchingDiseaseForUUID("11111111-2222-3333-4444-abcdefabcdef", "TEST");
            will(returnValue(true));
            exactly(6).of(mockQcLiveBarcodeAndUUIDValidator).validateUuid("11111111-2222-3333-4444-abcdefabcdef", qcContext, "good.uuids.sdrf.txt", true);
            will(returnValue(true));
            exactly(6).of(mockQcLiveBarcodeAndUUIDValidator).isAliquotUUID("11111111-2222-3333-4444-abcdefabcdef");
            will(returnValue(true));
            exactly(6).of(mockQcLiveBarcodeAndUUIDValidator).validateAliquotBarcodeFormat("TCGA-AO-A0J9-01A-11R-A034-07");
            will(returnValue(true));
            one(mockQcLiveBarcodeAndUUIDValidator).batchValidateUUIDsReportIndividualResults(Arrays.asList("11111111-2222-3333-4444-abcdefabcdef"), qcContext, "good.uuids.sdrf.txt", true);
            will(returnValue(batchResults));
        }});

        qcContext.setStandaloneValidator(true);
        qcContext.setNoRemote(true);
        qcContext.setCenterConvertedToUUID(true);
        archive.setPlatform("IlluminaHiSeq_RNASeqV2");
        archive.setArchiveFile(new File(SAMPLES_DIR + "qclive" + File.separator
                + "rnaSeqSdrfValidator" + File.separator + "valid.tar.gz"));
        archive.setSdrfFile(new File("good.uuids.sdrf.txt"));
        setSdrf(SAMPLES_DIR + "qclive" + File.separator + "rnaSeqSdrfValidator"
                + File.separator + "valid" + File.separator + "good.uuids.sdrf.txt");
        final boolean isValid = validator.doWork(archive, qcContext);
        assertEquals(0, qcContext.getErrorCount());
        assertTrue(qcContext.getErrors().toString(), isValid);
    }

    @Test
    public void testInvalidBarcodeCenterConverted() throws IOException, ParseException, Processor.ProcessorException {
        context.checking(new Expectations() {{
            exactly(6).of(mockQcLiveBarcodeAndUUIDValidator).isMatchingDiseaseForUUID("11111111-2222-3333-4444-abcdefabcdef", "TEST");
            will(returnValue(true));

            exactly(6).of(mockQcLiveBarcodeAndUUIDValidator).validateUuid("11111111-2222-3333-4444-abcdefabcdef", qcContext, "bad.barcode.uuids.sdrf.txt", true);
            will(returnValue(true));

            exactly(6).of(mockQcLiveBarcodeAndUUIDValidator).isAliquotUUID("11111111-2222-3333-4444-abcdefabcdef");
            will(returnValue(true));

            one(mockQcLiveBarcodeAndUUIDValidator).validateUUIDBarcodeMapping("11111111-2222-3333-4444-abcdefabcdef", "squirrel");
            will(returnValue(false));
            exactly(5).of(mockQcLiveBarcodeAndUUIDValidator).validateUUIDBarcodeMapping("11111111-2222-3333-4444-abcdefabcdef", "TCGA-AO-A0J9-01A-11R-A034-07");
            will(returnValue(true));
        }});

        archive.setPlatform("IlluminaHiSeq_RNASeqV2");
        qcContext.setCenterConvertedToUUID(true);
        archive.setArchiveFile(new File(SAMPLES_DIR + "qclive" + File.separator
                + "rnaSeqSdrfValidator" + File.separator + "invalid.tar.gz"));
        archive.setSdrfFile(new File("bad.barcode.uuids.sdrf.txt"));
        setSdrf(SAMPLES_DIR + "qclive" + File.separator + "rnaSeqSdrfValidator"
                + File.separator + "invalid" + File.separator + "bad.barcode.uuids.sdrf.txt");
        final boolean isValid = validator.doWork(archive, qcContext);
        assertFalse(isValid);
    }

    @Test
    public void testInvalidBarcodeCenterConvertedStandaloneNoRemote() throws IOException, ParseException, Processor.ProcessorException {
        final Map<String, Boolean> batchResults = new HashMap<String, Boolean>();
        batchResults.put("11111111-2222-3333-4444-abcdefabcdef", true);
        context.checking(new Expectations() {{
            exactly(6).of(mockQcLiveBarcodeAndUUIDValidator).isMatchingDiseaseForUUID("11111111-2222-3333-4444-abcdefabcdef", "TEST");
            will(returnValue(true));

            exactly(6).of(mockQcLiveBarcodeAndUUIDValidator).validateUuid("11111111-2222-3333-4444-abcdefabcdef", qcContext, "bad.barcode.uuids.sdrf.txt", true);
            will(returnValue(true));
            exactly(6).of(mockQcLiveBarcodeAndUUIDValidator).isAliquotUUID("11111111-2222-3333-4444-abcdefabcdef");
            will(returnValue(true));

            one(mockQcLiveBarcodeAndUUIDValidator).validateAliquotBarcodeFormat("squirrel");
            will(returnValue(false));
            exactly(5).of(mockQcLiveBarcodeAndUUIDValidator).validateAliquotBarcodeFormat("TCGA-AO-A0J9-01A-11R-A034-07");
            will(returnValue(true));
            one(mockQcLiveBarcodeAndUUIDValidator).batchValidateUUIDsReportIndividualResults(Arrays.asList("11111111-2222-3333-4444-abcdefabcdef"), qcContext, "bad.barcode.uuids.sdrf.txt", true);
            will(returnValue(batchResults));
        }});

        qcContext.setStandaloneValidator(true);
        qcContext.setNoRemote(true);
        qcContext.setCenterConvertedToUUID(true);
        archive.setPlatform("IlluminaHiSeq_RNASeqV2");
        archive.setArchiveFile(new File(SAMPLES_DIR + "qclive" + File.separator
                + "rnaSeqSdrfValidator" + File.separator + "invalid.tar.gz"));
        archive.setSdrfFile(new File("bad.barcode.uuids.sdrf.txt"));
        setSdrf(SAMPLES_DIR + "qclive" + File.separator + "rnaSeqSdrfValidator"
                + File.separator + "invalid" + File.separator + "bad.barcode.uuids.sdrf.txt");
        final boolean isValid = validator.doWork(archive, qcContext);
        assertFalse(isValid);
        assertEquals("An error occurred while validating SDRF for archive 'invalid': SDRF line 1: barcode 'squirrel' found in Comment [TCGA Barcode] is not a valid aliquot barcode", qcContext.getErrors().get(0));
    }

    @Test
    public void testInvalidUuidNotConvertedStandalone() throws IOException, ParseException, Processor.ProcessorException {
        final Map<String, Boolean> batchResults = new HashMap<String, Boolean>();
        batchResults.put("0a0a4e25-c733-4882-acc1-a617a052853", false);
        context.checking(new Expectations() {{

            one(mockQcLiveBarcodeAndUUIDValidator).batchValidateUUIDsReportIndividualResults(Arrays.asList("0a0a4e25-c733-4882-acc1-a617a052853", "0a78bfd7-a00c-447f-b5f4-f3111a0f67af"), qcContext, "unc.edu_KIRC.IlluminaHiSeq_RNASeqV2.1.2.0.sdrf.txt", true);
            will(returnValue(batchResults));

        }});

        qcContext.setStandaloneValidator(true);
        qcContext.setCenterConvertedToUUID(false);
        archive.setPlatform("IlluminaHiSeq_RNASeqV2");
        archive.setArchiveFile(new File(SAMPLES_DIR + "qclive" + File.separator
                + "rnaSeqSdrfValidator" + File.separator + "invalid.tar.gz"));
        archive.setSdrfFile(new File("unc.edu_KIRC.IlluminaHiSeq_RNASeqV2.1.2.0.sdrf.txt"));
        setSdrf(SAMPLES_DIR + "qclive" + File.separator + "rnaSeqSdrfValidator"
                + File.separator + "invalid" + File.separator + "unc.edu_KIRC.IlluminaHiSeq_RNASeqV2.1.2.0.sdrf.txt");
        final boolean isValid = validator.doWork(archive, qcContext);
        assertFalse(isValid);
    }

    @Test
    public void testValidSdrf() throws IOException, ParseException,
            Processor.ProcessorException {
        context.checking(new Expectations() {
            {
                exactly(4).of(mockQcLiveBarcodeAndUUIDValidator).validate(
                        "TCGA-A6-2670-01A-02R-0821-07", qcContext,
                        "test.sdrf.txt", true);
                will(returnValue(true));
                exactly(4).of(mockQcLiveBarcodeAndUUIDValidator).validate(
                        "TCGA-A6-2672-01A-01R-0826-07", qcContext,
                        "test.sdrf.txt", true);
                will(returnValue(true));
                exactly(4).of(mockBarcodeTumorValidator)
                        .barcodeIsValidForTumor("TCGA-A6-2670-01A-02R-0821-07",
                                "TEST");
                will(returnValue(true));
                exactly(4).of(mockBarcodeTumorValidator)
                        .barcodeIsValidForTumor("TCGA-A6-2672-01A-01R-0826-07",
                                "TEST");
                will(returnValue(true));
            }
        });
        archive.setArchiveFile(new File(SAMPLES_DIR + "qclive" + File.separator
                + "rnaSeqSdrfValidator" + File.separator + "valid.tar.gz"));
        setSdrf(SAMPLES_DIR + "qclive" + File.separator + "rnaSeqSdrfValidator"
                + File.separator + "valid" + File.separator + "good.sdrf.txt");
        boolean isValid = validator.doWork(archive, qcContext);
        assertTrue(qcContext.getErrors().toString(), isValid);
    }

    @Test
    public void testValidSdrfV2() throws IOException, ParseException,
            Processor.ProcessorException {
        context.checking(new Expectations() {
            {
                allowing(mockQcLiveBarcodeAndUUIDValidator).validate(
                        "TCGA-AO-A0J9-01A-11R-A034-07", qcContext,
                        "test.sdrf.txt", true);
                will(returnValue(true));
                allowing(mockBarcodeTumorValidator).barcodeIsValidForTumor("TCGA-AO-A0J9-01A-11R-A034-07", "TEST");
                will(returnValue(true));
            }
        });
        archive.setArchiveFile(new File(SAMPLES_DIR + "qclive" + File.separator
                + "rnaSeqSdrfValidator" + File.separator + "valid.tar.gz"));

        setSdrf(SAMPLES_DIR + "qclive" + File.separator + "rnaSeqSdrfValidator"
                + File.separator + "valid" + File.separator + "goodV2.sdrf.txt");

        archive.setPlatform("IlluminaHiSeq_RNASeqV2");
        boolean isValid = validator.doWork(archive, qcContext);
        assertTrue(qcContext.getErrors().toString(), isValid);
    }

    @Test
    public void testMixedV1AndV2Version() throws IOException, ParseException,
            Processor.ProcessorException {
        context.checking(new Expectations() {
            {
                allowing(mockQcLiveBarcodeAndUUIDValidator).validate(
                        "TCGA-AO-A0J9-01A-11R-A034-07", qcContext,
                        "test.sdrf.txt", true);
                will(returnValue(true));
                allowing(mockBarcodeTumorValidator).barcodeIsValidForTumor("TCGA-AO-A0J9-01A-11R-A034-07", "TEST");
                will(returnValue(true));
            }
        });
        archive.setArchiveFile(new File(SAMPLES_DIR + "qclive" + File.separator
                + "rnaSeqSdrfValidator" + File.separator + "valid.tar.gz"));

        setSdrf(SAMPLES_DIR + "qclive" + File.separator + "rnaSeqSdrfValidator"
                + File.separator + "invalid" + File.separator + "mixedV1V2.sdrf.txt");

        archive.setPlatform("IlluminaHiSeq_RNASeqV2");
        assertFalse(validator.doWork(archive, qcContext));
        assertEquals(qcContext.getErrors().toString(), 4, qcContext.getErrors().size());
    }

    @Test
    public void testValidSdrfLevel2Only()
            throws IOException, ParseException, Processor.ProcessorException {

        final String barcode1 = "TCGA-A6-2670-01A-02R-0821-07";
        final String barcode2 = "TCGA-A6-2672-01A-01R-0826-07";

        context.checking(new Expectations() {{
            exactly(4).of(mockQcLiveBarcodeAndUUIDValidator).validate(barcode1, qcContext, "test.sdrf.txt", true);
            will(returnValue(true));
            exactly(4).of(mockQcLiveBarcodeAndUUIDValidator).validate(barcode2, qcContext, "test.sdrf.txt", true);
            will(returnValue(true));
            exactly(4).of(mockBarcodeTumorValidator).barcodeIsValidForTumor(barcode1, "TEST");
            will(returnValue(true));
            exactly(4).of(mockBarcodeTumorValidator).barcodeIsValidForTumor(barcode2, "TEST");
            will(returnValue(true));
        }});

        archive.setArchiveFile(new File(TEST_DIR + "valid.tar.gz"));
        setSdrf(TEST_DIR + "valid" + File.separator + "good.level2.sdrf.txt");
        boolean isValid = validator.doWork(archive, qcContext);
        assertTrue(qcContext.getErrors().toString(), isValid);
    }

    @Test
    public void testInvalidSdrf() throws Processor.ProcessorException,
            IOException, ParseException {
        archive.setArchiveFile(new File(TEST_DIR + "invalid.tar.gz"));
        setSdrf(TEST_DIR + "invalid" + File.separator + "bad.sdrf.txt");
        boolean isValid = validator.doWork(archive, qcContext);
        assertFalse(isValid);
        assertEquals(qcContext.getErrors().toString(), 2,
                qcContext.getErrorCount());
        assertEquals(1, qcContext.getWarningCount());
        assertEquals(
                "Required SDRF column 'Comment [TCGA Include for Analysis]' is missing",
                qcContext.getErrors().get(0));
        assertEquals(
                "Value 'wig file' for 'TCGA-A6-2672-01A-01R-0826-07' was not provided",
                qcContext.getWarnings().get(0));
    }

    @Test
    public void testInvalidSdrfCenterReadyForUUIDTransition() throws Processor.ProcessorException,
            IOException, ParseException {
        archive.setArchiveFile(new File(TEST_DIR + "invalid.tar.gz"));
        setSdrf(TEST_DIR + "invalid" + File.separator + "bad.sdrf.txt");
        qcContext.setCenterConvertedToUUID(true);
        boolean isValid = validator.doWork(archive, qcContext);
        assertFalse(isValid);
        assertEquals(qcContext.getErrors().toString(), 3,
                qcContext.getErrorCount());
        assertEquals(1, qcContext.getWarningCount());
        assertEquals(
                "Required SDRF column 'Comment [TCGA Include for Analysis]' is missing",
                qcContext.getErrors().get(1));
        assertEquals(
                "Required SDRF column 'Comment [TCGA Barcode]' is missing",
                qcContext.getErrors().get(0));
        assertEquals(
                "Value 'wig file' for 'TCGA-A6-2672-01A-01R-0826-07' was not provided",
                qcContext.getWarnings().get(0));
    }

    @Test
    public void testInvalidFilenameKnownTransformationName()
            throws IOException, Processor.ProcessorException, ParseException {
        // file has unknown extension, but transformation name is known -- fail
        archive.setArchiveFile(new File(TEST_DIR + "invalid.tar.gz"));
        setSdrf(TEST_DIR + "invalid" + File.separator + "bad.filename.sdrf.txt");
        boolean isValid = validator.execute(archive, qcContext);
        assertFalse(isValid);
        assertEquals(4, qcContext.getErrorCount());
        assertEquals(0, qcContext.getWarningCount());
        final Iterator<String> errorIterator = qcContext.getErrors().iterator();
        assertEquals(
                "line 2, column 20: 'gene' file must have extension 'gene.quantification.txt'",
                errorIterator.next());
        assertEquals(
                "line 3, column 20: 'exon' file must have extension '*.exon.quantification.txt,*.exon_quantification.txt'",
                errorIterator.next());
        assertEquals(
                "line 4, column 20: 'splice' file must have extension '*.spljxn.quantification.txt,*.junction_quantification.txt'",
                errorIterator.next());
        assertEquals(
                "line 5, column 20: 'coverage' file must have extension '.wig'",
                errorIterator.next());
    }

    @Test
    public void testUnknownFilename() throws IOException,
            Processor.ProcessorException, ParseException {
        context.checking(new Expectations() {
            {
                exactly(5).of(mockQcLiveBarcodeAndUUIDValidator).validate(
                        "TCGA-A6-2670-01A-02R-0821-07", qcContext,
                        "test.sdrf.txt", true);
                will(returnValue(true));
                exactly(4).of(mockQcLiveBarcodeAndUUIDValidator).validate(
                        "TCGA-A6-2672-01A-01R-0826-07", qcContext,
                        "test.sdrf.txt", true);
                will(returnValue(true));
                exactly(5).of(mockBarcodeTumorValidator)
                        .barcodeIsValidForTumor("TCGA-A6-2670-01A-02R-0821-07",
                                "TEST");
                will(returnValue(true));
                exactly(4).of(mockBarcodeTumorValidator)
                        .barcodeIsValidForTumor("TCGA-A6-2672-01A-01R-0826-07",
                                "TEST");
                will(returnValue(true));
            }
        });
        // filename is unknown extension, transformation name is unknown -- warn
        archive.setArchiveFile(new File(TEST_DIR + "valid.tar.gz"));
        setSdrf(TEST_DIR + "valid" + File.separator + "warning.sdrf.txt");
        assertTrue(validator.execute(archive, qcContext));
        assertEquals(1, qcContext.getWarningCount());
        assertEquals(
                "line 6, column 21: Derived Data File 'UNCID_27874.TCGA-A6-2670-01A-02R-0821-07.100730_UNC2-RDR300275_00017_FC_629JTAAXX.1.acorn' does not have a known Data Transformation Name and will not be validated",
                qcContext.getWarnings().get(0));
    }

    @Test
    public void testBadSdrf() throws IOException, Processor.ProcessorException, ParseException {
        // test an SDRF that has bad column values
        archive.setArchiveFile(new File(TEST_DIR + "invalid.tar.gz"));
        setSdrf(TEST_DIR + "invalid" + File.separator + "invalid.sdrf.txt");
        boolean isValid = validator.doWork(archive, qcContext);
        assertFalse(isValid);
        assertEquals(6, qcContext.getErrorCount());
        assertEquals(0, qcContext.getWarningCount());
        int errorNum = 0;
        assertEquals(
                "line 2: value for 'Material Type' must be 'Total RNA', but found 'Total DNA'",
                qcContext.getErrors().get(errorNum++));
        assertEquals(
                "line 3: value for 'Protocol REF' must be in the format 'domain:protocol:platform:version', but found 'unc.edu:reverse_transcription:IlluminaGA_RNASeq'",
                qcContext.getErrors().get(errorNum++));
        assertEquals(
                "line 7: value for 'Comment [NCBI dbGAP Experiment Accession]' must be 'a complete and valid URL', but found 'hi://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/other/GAF/GAF_bundle/outputs/TCGA.Sept2010.09202010.gaf'",
                qcContext.getErrors().get(errorNum++));
        assertEquals(
                "line 5: value for 'Comment [NCBI SRA Experiment Accession]' must be 'a null (->) or a valid SRA accession', but found 'NotValid'",
                qcContext.getErrors().get(errorNum++));
        assertEquals(
                "line 6: value for 'Comment [NCBI dbGAP Experiment Accession]' must be 'a null (->) or a valid dbGaP experiment accession', but found 'NotAValidValue'",
                qcContext.getErrors().get(errorNum++));
        assertEquals(
                "line 4, column 3: value for 'Assay Name' must be 'Extract Name (TCGA-A6-2670-01A-02R-0821-07)', but found 'TCGA-A6-2670-01A-02R-0821-08 reverse_transcription'",
                qcContext.getErrors().get(errorNum));
    }

    @Test
    public void testUrlValidationPattern() {
        assertTrue(validator
                .urlIsValid("https://tcga-data.nci.nih.gov/tcgafiles/ftp_auth/distro_ftpusers/anonymous/other/GAF/GAF_bundle/outputs/TCGA.Sept2010.09202010.gaf"));
        assertTrue(validator.urlIsValid("http://www.google.com"));
        assertTrue(validator.urlIsValid("https://www.google.com/search"));
        assertTrue(validator.urlIsValid("http://localhost:8080"));
        assertTrue(validator.urlIsValid("http://something?hi"));
        assertTrue(validator.urlIsValid("http://test#bookmark"));
        assertTrue(validator.urlIsValid("http://123.org"));
        assertTrue(validator.urlIsValid("http://hyphenated-name.gov"));
        assertTrue(validator.urlIsValid("https://1.2.3.4.101"));

        assertFalse(validator.urlIsValid("hi"));
        assertFalse(validator.urlIsValid("http://"));
        assertFalse(validator.urlIsValid("http:/google.com"));
        assertFalse(validator.urlIsValid("ftp:/server"));
        assertFalse(validator.urlIsValid("www.google.com/somePage"));

    }

    @Test
    public void testCheckRnaSeqV1Files() {
        List<String> aliquotsWithLevel3Data = new ArrayList<String>();
        aliquotsWithLevel3Data.add("TCGA-A6-2670-01A-02R-0821-11");
        aliquotsWithLevel3Data.add("TCGA-A6-2670-01A-02R-0821-12");
        aliquotsWithLevel3Data.add("TCGA-A6-2670-01A-02R-0821-13");
        aliquotsWithLevel3Data.add("TCGA-A6-2670-01A-02R-0821-14");

        Map<String, String> geneFileForBarcodes = new HashMap<String, String>();
        Map<String, String> exonFileForBarcodes = new HashMap<String, String>();
        Map<String, String> junctionFileForBarcodes = new HashMap<String, String>();
        Map<String, String> wigFileForBarcodes = new HashMap<String, String>();

        for (int i = 11; i < 15; i++) {
            geneFileForBarcodes.put("TCGA-A6-2670-01A-02R-0821-" + i,
                    "TCGA-A6-2670-01A-02R-0821-" + i + ".gene.quantification.txt");
            exonFileForBarcodes.put("TCGA-A6-2670-01A-02R-0821-" + i,
                    "TCGA-A6-2670-01A-02R-0821-" + i + ".exon.quantification.txt");
            junctionFileForBarcodes.put("TCGA-A6-2670-01A-02R-0821-" + i,
                    "TCGA-A6-2670-01A-02R-0821-" + i + "spljxn.quantification.txt");
        }
        assertTrue(validator.checkRnaSeqV1Files(
                aliquotsWithLevel3Data,
                qcContext,
                geneFileForBarcodes,
                exonFileForBarcodes,
                junctionFileForBarcodes,
                wigFileForBarcodes));
    }

    @Test
    public void testCheckRnaSeqV1FilesMissingFiles() {
        List<String> aliquotsWithLevel3Data = new ArrayList<String>();
        aliquotsWithLevel3Data.add("TCGA-A6-2670-01A-02R-0821-11");
        aliquotsWithLevel3Data.add("TCGA-A6-2670-01A-02R-0821-12");
        aliquotsWithLevel3Data.add("TCGA-A6-2670-01A-02R-0821-13");
        aliquotsWithLevel3Data.add("TCGA-A6-2670-01A-02R-0821-14");

        Map<String, String> geneFileForBarcodes = new HashMap<String, String>();
        Map<String, String> exonFileForBarcodes = new HashMap<String, String>();
        Map<String, String> junctionFileForBarcodes = new HashMap<String, String>();
        Map<String, String> wigFileForBarcodes = new HashMap<String, String>();


        assertFalse(validator.checkRnaSeqV1Files(
                aliquotsWithLevel3Data,
                qcContext,
                geneFileForBarcodes,
                exonFileForBarcodes,
                junctionFileForBarcodes,
                wigFileForBarcodes));

        assertEquals(12, qcContext.getErrorCount());
    }

    @Test
    public void testCheckRnaSeqV2Files() {
        List<String> aliquotsWithLevel3Data = new ArrayList<String>();
        aliquotsWithLevel3Data.add("TCGA-A6-2670-01A-02R-0821-11");

        Map<String, String> rsemIsoformNormalized = new HashMap<String, String>();
        Map<String, String> rsemIsoformResults = new HashMap<String, String>();
        Map<String, String> rsemGeneNormalized = new HashMap<String, String>();
        Map<String, String> rsemGeneResults = new HashMap<String, String>();
        Map<String, String> exonFileForBarcodes = new HashMap<String, String>();
        Map<String, String> junctionFileForBarcodes = new HashMap<String, String>();

        exonFileForBarcodes.put("TCGA-A6-2670-01A-02R-0821-11",
                "TCGA-A6-2670-01A-02R-0821-11.exon.quantification.txt");
        junctionFileForBarcodes.put("TCGA-A6-2670-01A-02R-0821-11",
                "TCGA-A6-2670-01A-02R-0821-11.spljxn.quantification.txt");
        rsemIsoformNormalized.put("TCGA-A6-2670-01A-02R-0821-11",
                "TCGA-A6-2670-01A-02R-0821-11.rsem.isoforms.normalized_results");
        rsemIsoformResults.put("TCGA-A6-2670-01A-02R-0821-11",
                "TCGA-A6-2670-01A-02R-0821-11.rsem.isoforms.results");
        rsemGeneNormalized.put("TCGA-A6-2670-01A-02R-0821-11",
                "TCGA-A6-2670-01A-02R-0821-11.rsem.genes.normalized_results");
        rsemGeneResults.put("TCGA-A6-2670-01A-02R-0821-11",
                "TCGA-A6-2670-01A-02R-0821-11.rsem.genes.results");

        assertTrue(validator.checkRnaSeqV2Files(
                aliquotsWithLevel3Data,
                qcContext,
                exonFileForBarcodes,
                junctionFileForBarcodes,
                rsemGeneNormalized,
                rsemGeneResults,
                rsemIsoformNormalized,
                rsemIsoformResults
        ));
    }

    @Test
    public void testCheckRnaSeqV2FilesMissingFiles() {
        List<String> aliquotsWithLevel3Data = new ArrayList<String>();
        aliquotsWithLevel3Data.add("TCGA-A6-2670-01A-02R-0821-11");

        Map<String, String> rsemIsoformNormalized = new HashMap<String, String>();
        Map<String, String> rsemIsoformResults = new HashMap<String, String>();
        Map<String, String> rsemGeneNormalized = new HashMap<String, String>();
        Map<String, String> rsemGeneResults = new HashMap<String, String>();
        Map<String, String> exonFileForBarcodes = new HashMap<String, String>();
        Map<String, String> junctionFileForBarcodes = new HashMap<String, String>();


        assertFalse(validator.checkRnaSeqV2Files(
                aliquotsWithLevel3Data,
                qcContext,
                exonFileForBarcodes,
                junctionFileForBarcodes,
                rsemGeneNormalized,
                rsemGeneResults,
                rsemIsoformNormalized,
                rsemIsoformResults
        ));

        assertEquals(6, qcContext.getErrorCount());
    }

    @Test
    public void testIsRNASeqV2Archive() {
        assertTrue(validator.isRNASeqV2Archive("IlluminaHiSeq_RNASeqV2"));
        assertTrue(validator.isRNASeqV2Archive("IlluminaGA_RNASeqV2"));
        assertFalse(validator.isRNASeqV2Archive("IlluminaHiSeq_RNASeq"));
        assertFalse(validator.isRNASeqV2Archive("badPlatform"));
    }

}
