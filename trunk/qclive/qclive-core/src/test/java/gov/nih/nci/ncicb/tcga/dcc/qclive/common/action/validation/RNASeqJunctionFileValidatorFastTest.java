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
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeTumorValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.util.ChromInfoUtils;
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
 * Test for junction file validator.
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class RNASeqJunctionFileValidatorFastTest {

    private Mockery context;
    private RNASeqJunctionFileValidator rnaSeqJunctionFileValidator;
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

        rnaSeqJunctionFileValidator = new RNASeqJunctionFileValidator();
        rnaSeqJunctionFileValidator.setQcLiveBarcodeAndUUIDValidator(mockQcLiveBarcodeAndUUIDValidator);
        rnaSeqJunctionFileValidator.setBarcodeTumorValidator(mockBarcodeTumorValidator);
        rnaSeqJunctionFileValidator.setSeqDataFileValidationErrorMessagePropertyType(MessagePropertyType.RNA_SEQ_DATA_FILE_VALIDATION_ERROR);
        rnaSeqJunctionFileValidator.setChromInfoUtils(mockChromInfoUtils);

        final Archive archive = new Archive();
        archive.setTumorType("TEST");

        qcContext = new QcContext();
        qcContext.setArchive(archive);
        qcContext.setCenterConvertedToUUID(false);
    }

    @Test
    public void testIsIncorrectArchiveType() throws Processor.ProcessorException {

        final Archive archive = qcContext.getArchive();
        archive.setPlatform("miRNASeq");

        assertFalse(rnaSeqJunctionFileValidator.isCorrectArchiveType(archive));
    }

    @Test
    public void testValidFile() throws Processor.ProcessorException {

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateAliquotFormatAndCodes("TCGA-00-1111-22A-33R-4444-55");
            will(returnValue(true));
            one(mockBarcodeTumorValidator).barcodeIsValidForTumor("TCGA-00-1111-22A-33R-4444-55", "TEST");
            will(returnValue(true));
            allowing(mockChromInfoUtils).isValidChromValue("chr1");
            will(returnValue(true));
            allowing(mockChromInfoUtils).isValidChromValue("chr8");
            will(returnValue(true));
            allowing(mockChromInfoUtils).isValidChromValue("HSCHR1_RANDOM_CTG5");
            will(returnValue(true));
            allowing(mockChromInfoUtils).isValidChromValue("hschr1_random_ctg5");
            will(returnValue(true));
        }});

        final File file = new File(RNASeqGeneFileValidatorFastTest.TEST_FILE_DIR + "test.TCGA-00-1111-22A-33R-4444-55.spljxn.quantification.txt");
        final boolean isValid = rnaSeqJunctionFileValidator.processFile(file, qcContext);

        assertTrue(qcContext.getErrors().toString(), isValid);
        assertEquals(1, qcContext.getWarningCount());
        assertEquals("test.TCGA-00-1111-22A-33R-4444-55.spljxn.quantification.txt line 20: both points are not on the same chromosome for 'junction' value", qcContext.getWarnings().get(0));
    }

    @Test
    public void testValidFileUUID() throws Processor.ProcessorException {
        context.checking(new Expectations() {{
            allowing(mockQcLiveBarcodeAndUUIDValidator).isAliquotUUID("d59d48ca-16e5-46c0-80e8-6d1214acd156");
            will(returnValue(true));
            allowing(mockQcLiveBarcodeAndUUIDValidator).isMatchingDiseaseForUUID("d59d48ca-16e5-46c0-80e8-6d1214acd156", "TEST");
            will(returnValue(true));
            allowing(mockChromInfoUtils).isValidChromValue("chr1");
            will(returnValue(true));
            allowing(mockChromInfoUtils).isValidChromValue("chr8");
            will(returnValue(true));
            allowing(mockChromInfoUtils).isValidChromValue("HSCHR1_RANDOM_CTG5");
            will(returnValue(true));
            allowing(mockChromInfoUtils).isValidChromValue("hschr1_random_ctg5");
            will(returnValue(true));
        }});
        qcContext.setCenterConvertedToUUID(true);
        final File file = new File(RNASeqGeneFileValidatorFastTest.TEST_FILE_DIR + "test.d59d48ca-16e5-46c0-80e8-6d1214acd156.spljxn.quantification.txt");
        final boolean isValid = rnaSeqJunctionFileValidator.processFile(file, qcContext);
        assertTrue(qcContext.getErrors().toString(), isValid);
    }

    @Test
    public void testInvalidFile() throws Processor.ProcessorException {

        context.checking(new Expectations() {{
            one(mockQcLiveBarcodeAndUUIDValidator).validateAliquotFormatAndCodes("TCGA-00-1111-22A-33R-4444-55");
            will(returnValue(true));
            one(mockBarcodeTumorValidator).barcodeIsValidForTumor("TCGA-00-1111-22A-33R-4444-55", "TEST");
            will(returnValue(true));
            allowing(mockChromInfoUtils).isValidChromValue("1");
            will(returnValue(false));
            allowing(mockChromInfoUtils).isValidChromValue("chr1");
            will(returnValue(true));
            allowing(mockChromInfoUtils).isValidChromValue("chr123");
            will(returnValue(false));
            allowing(mockChromInfoUtils).isValidChromValue(" chr1 ");
            will(returnValue(false));
            allowing(mockChromInfoUtils).isValidChromValue("chr12");
            will(returnValue(true));
        }});

        final String filename = "bad.TCGA-00-1111-22A-33R-4444-55.spljxn.quantification.txt";
        final File file = new File(RNASeqGeneFileValidatorFastTest.TEST_FILE_DIR + filename);
        final boolean isValid = rnaSeqJunctionFileValidator.processFile(file, qcContext);

        assertFalse(isValid);
        assertEquals(14, qcContext.getErrorCount());
        assertEquals(5, qcContext.getWarningCount());

        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 2: value for 'junction' must have format 'chr{chrom}:{coord}:{strand},chr{chrom}:{coord}:{strand}", qcContext.getErrors().get(0));
        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 3: value for 'junction' must have format 'chr{chrom}:{coord}:{strand},chr{chrom}:{coord}:{strand}", qcContext.getErrors().get(1));
        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 4: value for 'junction' must have format 'chr{chrom}:{coord}:{strand},chr{chrom}:{coord}:{strand}", qcContext.getErrors().get(2));
        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 5: chromosome value '1' is not valid", qcContext.getErrors().get(3));
        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 6: chromosome value '1' is not valid", qcContext.getErrors().get(4));
        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 7: coordinate value 'abc' is not valid, must be a positive integer", qcContext.getErrors().get(5));
        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 8: coordinate value 'def' is not valid, must be a positive integer", qcContext.getErrors().get(6));
        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 9: strand value '?' is not valid, must be '+' or '-'", qcContext.getErrors().get(7));
        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 10: strand value '?' is not valid, must be '+' or '-'", qcContext.getErrors().get(8));
        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 11: chromosome value 'chr123' is not valid", qcContext.getErrors().get(9));
        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 12: chromosome value ' chr1 ' is not valid", qcContext.getErrors().get(10));
        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 13: coordinate value '168.54' is not valid, must be a positive integer", qcContext.getErrors().get(11));
        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 14: coordinate value '16.765' is not valid, must be a positive integer", qcContext.getErrors().get(12));
        assertEquals("An error occurred while validating RNA sequence data file '" + filename + "':  line 15: 'raw_counts' cannot be negative", qcContext.getErrors().get(13));
        assertEquals(filename + " line 5: both points are not on the same chromosome for 'junction' value", qcContext.getWarnings().get(0));
        assertEquals(filename + " line 6: both points are not on the same chromosome for 'junction' value", qcContext.getWarnings().get(1));
        assertEquals(filename + " line 11: both points are not on the same chromosome for 'junction' value", qcContext.getWarnings().get(2));
        assertEquals(filename + " line 12: both points are not on the same chromosome for 'junction' value", qcContext.getWarnings().get(3));
        assertEquals(filename + " line 16: both points are not on the same chromosome for 'junction' value", qcContext.getWarnings().get(4));
    }
}
