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
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeTumorValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.util.ChromInfoUtils;
import junit.framework.Assert;
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
 * Test for RNASeq exon file validator
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class RNASeqExonFileValidatorFastTest {
    private Mockery context;
    private RNASeqDataFileValidator validator;
    private QcContext qcContext;
    private QcLiveBarcodeAndUUIDValidator mockQcLiveBarcodeAndUUIDValidator;
    private BarcodeTumorValidator mockBarcodeTumorValidator;
    private ChromInfoUtils mockChromInfoUtils;

    @Before
    public void setUp() {
        context = new JUnit4Mockery();
        mockQcLiveBarcodeAndUUIDValidator = context.mock(QcLiveBarcodeAndUUIDValidator.class);
        mockBarcodeTumorValidator = context.mock(BarcodeTumorValidator.class);
        mockChromInfoUtils = context.mock(ChromInfoUtils.class);

        validator = new RNASeqExonFileValidator();
        validator.setQcLiveBarcodeAndUUIDValidator(mockQcLiveBarcodeAndUUIDValidator);
        validator.setBarcodeTumorValidator(mockBarcodeTumorValidator);
        validator.setSeqDataFileValidationErrorMessagePropertyType(MessagePropertyType.RNA_SEQ_DATA_FILE_VALIDATION_ERROR);
        validator.setChromInfoUtils(mockChromInfoUtils);
        qcContext = new QcContext();
        final Archive archive = new Archive();
        archive.setTumorType("TEST");
        qcContext.setArchive(archive);
        qcContext.setCenterConvertedToUUID(false);
    }

    @Test
    public void testIsIncorrectArchiveType() throws Processor.ProcessorException {

        final Archive archive = qcContext.getArchive();
        archive.setPlatform("miRNASeq");

        assertFalse(validator.isCorrectArchiveType(archive));
    }

    @Test
    public void testValidFile() throws Processor.ProcessorException {
        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateAliquotFormatAndCodes("TCGA-00-1111-22A-33R-4444-55");
            will(returnValue(true));
            one(mockBarcodeTumorValidator).barcodeIsValidForTumor("TCGA-00-1111-22A-33R-4444-55", "TEST");
            will(returnValue(true));
            exactly(20).of(mockChromInfoUtils).isValidChromValue(with(any(String.class)));
            will(returnValue(true));

        }});
        File file = new File(RNASeqGeneFileValidatorFastTest.TEST_FILE_DIR + "test.TCGA-00-1111-22A-33R-4444-55.exon.quantification.txt");
        boolean isValid = validator.processFile(file, qcContext);
        assertTrue(qcContext.getErrors().toString(), isValid);
    }

    @Test
    public void testInvalidFile() throws Processor.ProcessorException {

        context.checking(new Expectations() {{

            one(mockQcLiveBarcodeAndUUIDValidator).validateAliquotFormatAndCodes("TCGA-00-1111-22A-33R-4444-55");
            will(returnValue(true));

            one(mockBarcodeTumorValidator).barcodeIsValidForTumor("TCGA-00-1111-22A-33R-4444-55", "TEST");
            will(returnValue(true));

            //Line 4
            one(mockChromInfoUtils).isValidChromValue(with(any(String.class)));
            will(returnValue(false));

            //Line 5-10
            exactly(6).of(mockChromInfoUtils).isValidChromValue(with(any(String.class)));
            will(returnValue(true));

            //Line 11
            one(mockChromInfoUtils).isValidChromValue(with(any(String.class)));
            will(returnValue(false));

            //Line 12-20
            exactly(9).of(mockChromInfoUtils).isValidChromValue(with(any(String.class)));
            will(returnValue(true));

            //Line 21
            one(mockChromInfoUtils).isValidChromValue(with(any(String.class)));
            will(returnValue(false));

            //Line 22
            one(mockChromInfoUtils).isValidChromValue(with(any(String.class)));
            will(returnValue(true));
        }});

        final String filename = "bad.TCGA-00-1111-22A-33R-4444-55.exon.quantification.txt";
        final File file = new File(RNASeqGeneFileValidatorFastTest.TEST_FILE_DIR + filename);
        final boolean isValid = validator.processFile(file, qcContext);
        assertFalse(qcContext.getErrors().toString(), isValid);
        assertEquals(13, qcContext.getErrorCount());
        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 2: value for 'exon' must have format 'chr{chromNum}:{startCoord}-{endCoord}:{strand}", qcContext.getErrors().get(0));
        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 3: value for 'exon' must have format 'chr{chromNum}:{startCoord}-{endCoord}:{strand}", qcContext.getErrors().get(1));
        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 4: chromosome value 'chr100' is not valid", qcContext.getErrors().get(2));
        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 5: coordinate value '0' is not valid, must be a positive integer", qcContext.getErrors().get(3));
        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 6: coordinate value 'a' is not valid, must be a positive integer", qcContext.getErrors().get(4));
        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 7: strand value '?' is not valid, must be '+' or '-'", qcContext.getErrors().get(5));
        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 11: chromosome value '12' is not valid", qcContext.getErrors().get(6));
        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 13: 'median_length_normalized' cannot be negative", qcContext.getErrors().get(7));
        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 14: 'RPKM' value cannot be negative", qcContext.getErrors().get(8));
        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 15: 'median_length_normalized' value must be a number", qcContext.getErrors().get(9));
        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 16: 'RPKM' value must be a number", qcContext.getErrors().get(10));
        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 17: 'raw_counts' value must be a floating point number", qcContext.getErrors().get(11));
        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 21: chromosome value ' chr1 ' is not valid", qcContext.getErrors().get(12));
    }

    // test base methods
    @Test
    public void getBarcodeOrUUIDFromFilenameTest() {
        File file = new File(RNASeqGeneFileValidatorFastTest.TEST_FILE_DIR + "test.TCGA-00-1111-22A-33R-4444-55.exon.quantification.txt");
        String barcode = validator.getBarcodeOrUUIDFromFilename(file);
        assertEquals("TCGA-00-1111-22A-33R-4444-55", barcode);
    }

    @Test
    public void getBarcodeOrUUIDFromInvalidFilenameTest() {
        File file = new File(RNASeqGeneFileValidatorFastTest.TEST_FILE_DIR + "test.d59d48ca-16e5-46c0-80e8-6d1214acd156.exon.quantification.txt");
        String barcode = validator.getBarcodeOrUUIDFromFilename(file);
        assertEquals("d59d48ca-16e5-46c0-80e8-6d1214acd156", barcode);
    }

    @Test
    public void getBarcodeOrUUIDFromNullFilenameTest() {
        Assert.assertNull("d59d48ca-16e5-46c0-80e8-6d1214acd156", validator.getBarcodeOrUUIDFromFilename(null));
    }

    @Test
    public void getBarcodeOrUUIDFromBlankFilenameTest() {
        Assert.assertNull("d59d48ca-16e5-46c0-80e8-6d1214acd156", validator.getBarcodeOrUUIDFromFilename(new File("")));
    }

    @Test
    public void testProcessFileValid() throws ProcessorException {
        context.checking(new Expectations() {{
            //Line 4
            allowing(mockChromInfoUtils).isValidChromValue(with(any(String.class)));
            will(returnValue(true));
        }});
        File file = new File(RNASeqGeneFileValidatorFastTest.TEST_FILE_DIR + "unc.edu.d59d48ca-16e5-46c0-80e8-6d1214acd156.exon.quantification.txt");
        assertTrue(validator.processFile(file, qcContext));
    }

    @Test
    public void testProcessFileValidUUID() throws ProcessorException {
        context.checking(new Expectations() {{
            //Line 4
            allowing(mockQcLiveBarcodeAndUUIDValidator).isAliquotUUID("d59d48ca-16e5-46c0-80e8-6d1214acd156");
            will(returnValue(true));
            allowing(mockQcLiveBarcodeAndUUIDValidator).isMatchingDiseaseForUUID("d59d48ca-16e5-46c0-80e8-6d1214acd156", "TEST");
            will(returnValue(true));
            allowing(mockChromInfoUtils).isValidChromValue(with(any(String.class)));
            will(returnValue(true));
        }});
        qcContext.setCenterConvertedToUUID(true);
        File file = new File(RNASeqGeneFileValidatorFastTest.TEST_FILE_DIR + "unc.edu.d59d48ca-16e5-46c0-80e8-6d1214acd156.exon.quantification.txt");
        assertTrue(validator.processFile(file, qcContext));
    }

    @Test
    public void testProcessFileInvalid() throws ProcessorException {
        File file = new File(RNASeqGeneFileValidatorFastTest.TEST_FILE_DIR + "unc.edu.d59d48ca-16e5-46c0-80e8-6d1214acd1.exon.quantification.txt");
        assertFalse(validator.processFile(file, qcContext));
        assertEquals("An error occurred while validating RNA sequence data file 'unc.edu.d59d48ca-16e5-46c0-80e8-6d1214acd1.exon.quantification.txt': :" +
                " filename must include a valid TCGA aliquot barcode or a UUID", qcContext.getErrors().get(0));
    }
}
