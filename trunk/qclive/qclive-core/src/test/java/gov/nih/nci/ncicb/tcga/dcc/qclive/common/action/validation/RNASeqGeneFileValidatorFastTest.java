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
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for RNASeqGeneFileValidator
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class RNASeqGeneFileValidatorFastTest {
    private Mockery context;
    private Archive archive;
    private RNASeqDataFileValidator validator;
    private QcContext qcContext;
    private QcLiveBarcodeAndUUIDValidator mockQcLiveBarcodeAndUUIDValidator;
    private BarcodeTumorValidator mockBarcodeTumorValidator;
    private static final String SAMPLES_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;
    public static final String TEST_FILE_DIR = SAMPLES_DIR + File.separator
            + "qclive" + File.separator + "rnaSeqDataFileValidator"
            + File.separator;

    @Before
    public void setUp() {
        context = new JUnit4Mockery();
        mockQcLiveBarcodeAndUUIDValidator = context
                .mock(QcLiveBarcodeAndUUIDValidator.class);
        mockBarcodeTumorValidator = context.mock(BarcodeTumorValidator.class);
        validator = new RNASeqGeneFileValidator();
        validator
                .setQcLiveBarcodeAndUUIDValidator(mockQcLiveBarcodeAndUUIDValidator);
        validator.setBarcodeTumorValidator(mockBarcodeTumorValidator);
        validator
                .setSeqDataFileValidationErrorMessagePropertyType(MessagePropertyType.RNA_SEQ_DATA_FILE_VALIDATION_ERROR);
        archive = new Archive();
        archive.setExperimentType(Experiment.TYPE_CGCC);
        archive.setArchiveType(Archive.TYPE_LEVEL_3);
        archive.setTumorType("TEST");
        archive.setPlatform("testRNASeq");
        qcContext = new QcContext();
        qcContext.setArchive(archive);
        qcContext.setCenterConvertedToUUID(false);
    }

    @Test
    public void testIsCorrectArchiveType() throws Processor.ProcessorException {
        assertTrue(validator.isCorrectArchiveType(archive));
        archive.setPlatform("blahRNASeqBlah");
        assertTrue(validator.isCorrectArchiveType(archive));
    }

    @Test
    public void testIsIncorrectArchiveType()
            throws Processor.ProcessorException {

        archive.setPlatform("ABI");
        assertFalse(validator.isCorrectArchiveType(archive));

        archive.setPlatform("RNASeq");
        archive.setArchiveType(Archive.TYPE_MAGE_TAB);
        assertFalse(validator.isCorrectArchiveType(archive));

        archive.setPlatform("miRNASeq");
        assertFalse(validator.isCorrectArchiveType(archive));
    }

    @Test
    public void testValidFile() throws Processor.ProcessorException {
        context.checking(new Expectations() {
            {
                one(mockQcLiveBarcodeAndUUIDValidator)
                        .validateAliquotFormatAndCodes(
                                "TCGA-00-1111-22A-33R-4444-55");
                will(returnValue(true));
                one(mockBarcodeTumorValidator).barcodeIsValidForTumor(
                        "TCGA-00-1111-22A-33R-4444-55", "TEST");
                will(returnValue(true));
            }
        });
        final File file = new File(TEST_FILE_DIR
                + "test.TCGA-00-1111-22A-33R-4444-55.gene.quantification.txt");
        final boolean isValid = validator.processFile(file, qcContext);
        assertTrue(qcContext.getErrors().toString(), isValid);
    }

    @Test
    public void testValidFileUUID() throws Exception {
        context.checking(new Expectations() {{
            allowing(mockQcLiveBarcodeAndUUIDValidator).isAliquotUUID("d59d48ca-16e5-46c0-80e8-6d1214acd156");
            will(returnValue(true));
            allowing(mockQcLiveBarcodeAndUUIDValidator).isMatchingDiseaseForUUID("d59d48ca-16e5-46c0-80e8-6d1214acd156", "TEST");
            will(returnValue(true));
        }});
        qcContext.setCenterConvertedToUUID(true);
        final File file = new File(TEST_FILE_DIR
                + "test.d59d48ca-16e5-46c0-80e8-6d1214acd156.gene.quantification.txt");
        final boolean isValid = validator.processFile(file, qcContext);
        assertTrue(qcContext.getErrors().toString(), isValid);
    }

    @Test
    public void testInvalidBarcodeInFilename()
            throws Processor.ProcessorException {
        // file is valid except for incomplete barcode in name
        final File file = new File(TEST_FILE_DIR
                + "test.TCGA-00-1111-22A.gene.quantification.txt");
        final boolean isValid = validator.processFile(file, qcContext);
        assertFalse(isValid);
        assertEquals(
                "An error occurred while validating RNA sequence data file 'test.TCGA-00-1111-22A.gene.quantification.txt': : filename must include a valid TCGA aliquot barcode or a UUID",
                qcContext.getErrors().get(0));
    }

    @Test
    public void testBarcodeNotValid() throws Processor.ProcessorException {
        context.checking(new Expectations() {
            {
                one(mockQcLiveBarcodeAndUUIDValidator)
                        .validateAliquotFormatAndCodes(
                                "TCGA-00-1111-22A-33R-4444-55");
                will(returnValue(false));
            }
        });
        // this file is valid, but we have the barcode validator failing
        final File file = new File(TEST_FILE_DIR
                + "test.TCGA-00-1111-22A-33R-4444-55.gene.quantification.txt");
        final boolean isValid = validator.processFile(file, qcContext);
        assertFalse(isValid);
        assertEquals(
                "An error occurred while validating RNA sequence data file 'test.TCGA-00-1111-22A-33R-4444-55.gene.quantification.txt': : TCGA aliquot barcode in filename is invalid",
                qcContext.getErrors().get(0));
    }

    @Test
    public void testBarcodeNotForDiseaseInFilename()
            throws Processor.ProcessorException {
        context.checking(new Expectations() {
            {
                one(mockQcLiveBarcodeAndUUIDValidator)
                        .validateAliquotFormatAndCodes(
                                "TCGA-00-1111-22A-33R-4444-55");
                will(returnValue(true));
                one(mockBarcodeTumorValidator).barcodeIsValidForTumor(
                        "TCGA-00-1111-22A-33R-4444-55", "TEST");
                will(returnValue(false));
            }
        });
        // this file is valid, but we have the barcode-tumor validator failing
        final File file = new File(TEST_FILE_DIR
                + "test.TCGA-00-1111-22A-33R-4444-55.gene.quantification.txt");
        final boolean isValid = validator.processFile(file, qcContext);
        assertFalse(isValid);
        assertEquals(
                "An error occurred while validating RNA sequence data file 'test.TCGA-00-1111-22A-33R-4444-55.gene.quantification.txt': : TCGA aliquot barcode in filename does not belong to the disease set for TEST",
                qcContext.getErrors().get(0));
    }

    @Test
    public void testInvalidHeaders() throws Processor.ProcessorException {
        context.checking(new Expectations() {
            {
                one(mockQcLiveBarcodeAndUUIDValidator)
                        .validateAliquotFormatAndCodes(
                                "TCGA-00-1111-22A-33R-4444-55");
                will(returnValue(true));
                one(mockBarcodeTumorValidator).barcodeIsValidForTumor(
                        "TCGA-00-1111-22A-33R-4444-55", "TEST");
                will(returnValue(true));
            }
        });
        final String filename = "bad.headers.TCGA-00-1111-22A-33R-4444-55.gene.quantification.txt";
        final File file = new File(TEST_FILE_DIR + filename);
        final boolean isValid = validator.processFile(file, qcContext);
        assertFalse(isValid);
        assertEquals(2, qcContext.getErrorCount());
        assertEquals(
                "An error occurred while validating RNA sequence data file '"
                        + filename
                        + "': Expected header 'raw_counts' at column '2' but found 'raw_count'",
                qcContext.getErrors().get(0));
        assertEquals(
                "An error occurred while validating RNA sequence data file '"
                        + filename
                        + "': Expected header 'median_length_normalized' at column '3' but found 'median_lengthnormalized'",
                qcContext.getErrors().get(1));
    }

    @Test
    public void testInvalidFile() throws Processor.ProcessorException {
        context.checking(new Expectations() {
            {
                one(mockQcLiveBarcodeAndUUIDValidator)
                        .validateAliquotFormatAndCodes(
                                "TCGA-00-1111-22A-33R-4444-55");
                will(returnValue(true));
                one(mockBarcodeTumorValidator).barcodeIsValidForTumor(
                        "TCGA-00-1111-22A-33R-4444-55", "TEST");
                will(returnValue(true));
            }
        });
        final String filename = "bad.TCGA-00-1111-22A-33R-4444-55.gene.quantification.txt";
        final File file = new File(TEST_FILE_DIR + filename);
        final boolean isValid = validator.processFile(file, qcContext);
        assertFalse(qcContext.getErrors().toString(), isValid);
        assertEquals(5, qcContext.getErrorCount());

        assertEquals(
                "An error occurred while validating RNA sequence data file '"
                        + filename
                        + "':  line 3: 'raw_counts' value must be a floating point number",
                qcContext.getErrors().get(0));
        assertEquals(
                "An error occurred while validating RNA sequence data file '"
                        + filename
                        + "':  line 4: 'median_length_normalized' cannot be negative",
                qcContext.getErrors().get(1));
        assertEquals(
                "An error occurred while validating RNA sequence data file '"
                        + filename
                        + "':  line 5: 'RPKM' value must be a number",
                qcContext.getErrors().get(2));
        assertEquals(
                "An error occurred while validating RNA sequence data file '"
                        + filename
                        + "':  line 6: 'raw_counts' cannot be negative",
                qcContext.getErrors().get(3));
        assertEquals(
                "An error occurred while validating RNA sequence data file '"
                        + filename
                        + "':  line 7: 'RPKM' value cannot be negative",
                qcContext.getErrors().get(4));
    }
}
