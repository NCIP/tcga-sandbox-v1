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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * MiRNASeqIsoformFileValidator unit test
 *
 * @author Julien Baboud Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class MiRNASeqIsoformFileValidatorFastTest {

    private static final String TEST_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator
            + "qclive"
            + File.separator
            + "miRnaSeqDataFileValidator" + File.separator;

    private static final String ERROR_MSG = "An error occurred while validating micro RNA sequence data file ";

    private final String TUMOR_TYPE = "TEST";

    private Mockery mockery;
    private QcLiveBarcodeAndUUIDValidator mockQcLiveBarcodeAndUUIDValidator;
    private BarcodeTumorValidator mockBarcodeTumorValidator;

    private Archive archive;
    private MiRNASeqDataFileValidator miRNASeqDataFileValidator;
    private QcContext qcContext;

    @Before
    public void setUp() {

        mockery = new JUnit4Mockery();
        mockQcLiveBarcodeAndUUIDValidator = mockery
                .mock(QcLiveBarcodeAndUUIDValidator.class);
        mockBarcodeTumorValidator = mockery.mock(BarcodeTumorValidator.class);

        miRNASeqDataFileValidator = new MiRNASeqIsoformFileValidator();
        miRNASeqDataFileValidator
                .setQcLiveBarcodeAndUUIDValidator(mockQcLiveBarcodeAndUUIDValidator);
        miRNASeqDataFileValidator
                .setBarcodeTumorValidator(mockBarcodeTumorValidator);
        miRNASeqDataFileValidator
                .setSeqDataFileValidationErrorMessagePropertyType(MessagePropertyType.MIRNA_SEQ_DATA_FILE_VALIDATION_ERROR);

        archive = new Archive();
        archive.setExperimentType(Experiment.TYPE_CGCC);
        archive.setArchiveType(Archive.TYPE_LEVEL_3);
        archive.setTumorType(TUMOR_TYPE);
        archive.setPlatform("miRNASeq");
        qcContext = new QcContext();
        qcContext.setArchive(archive);
        qcContext.setCenterConvertedToUUID(false);
    }

    @Test
    public void testIsCorrectArchiveType() throws Processor.ProcessorException {

        assertTrue(miRNASeqDataFileValidator.isCorrectArchiveType(archive));

        archive.setPlatform("blahmiRNASeqBlah");
        assertTrue(miRNASeqDataFileValidator.isCorrectArchiveType(archive));
    }

    @Test
    public void testIsIncorrectArchiveType()
            throws Processor.ProcessorException {

        archive.setPlatform("ABI");
        assertFalse(miRNASeqDataFileValidator.isCorrectArchiveType(archive));

        archive.setPlatform("miRNASeq");
        archive.setArchiveType(Archive.TYPE_MAGE_TAB);
        assertFalse(miRNASeqDataFileValidator.isCorrectArchiveType(archive));

        archive.setPlatform("RNASeq");
        assertFalse(miRNASeqDataFileValidator.isCorrectArchiveType(archive));
    }

    @Test
    public void testValidFile() throws Processor.ProcessorException {

        final String barcode = "TCGA-00-0000-00A-00A-0000-00";

        mockery.checking(new Expectations() {
            {
                one(mockQcLiveBarcodeAndUUIDValidator)
                        .validateAliquotFormatAndCodes(barcode);
                will(returnValue(true));
                one(mockBarcodeTumorValidator).barcodeIsValidForTumor(barcode,
                        TUMOR_TYPE);
                will(returnValue(true));
            }
        });

        final File file = new File(
                TEST_DIR
                        + "good-TCGA-00-0000-00A-00A-0000-00.isoform.quantification.txt");
        final boolean isValid = miRNASeqDataFileValidator.processFile(file,
                qcContext);

        assertTrue(qcContext.getErrors().toString(), isValid);
    }

    @Test
    public void testInvalidBarcodeInFilename()
            throws Processor.ProcessorException {

        // The file is valid except for an incomplete aliquot barcode in name
        final String filename = "bad-filename-TCGA-00-0000.isoform.quantification.txt";
        final File file = new File(TEST_DIR + filename);
        final boolean isValid = miRNASeqDataFileValidator.processFile(file,
                qcContext);

        assertFalse(isValid);

        final List<String> errors = qcContext.getErrors();
        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals(ERROR_MSG + "'" + filename + "': : "
                + "filename must include a valid TCGA aliquot barcode or a UUID",
                qcContext.getErrors().get(0));
    }

    @Test
    public void testBarcodeNotForDiseaseInFilename()
            throws Processor.ProcessorException {

        final String barcode = "TCGA-00-0000-00A-00A-0000-00";
        final String filename = "good-TCGA-00-0000-00A-00A-0000-00.isoform.quantification.txt";

        mockery.checking(new Expectations() {
            {
                one(mockQcLiveBarcodeAndUUIDValidator)
                        .validateAliquotFormatAndCodes(barcode);
                will(returnValue(true));
                one(mockBarcodeTumorValidator).barcodeIsValidForTumor(barcode,
                        TUMOR_TYPE);
                will(returnValue(false));
            }
        });

        final File file = new File(TEST_DIR + filename);
        final boolean isValid = miRNASeqDataFileValidator.processFile(file,
                qcContext);

        assertFalse(isValid);

        final List<String> errors = qcContext.getErrors();
        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertEquals(
                ERROR_MSG
                        + "'"
                        + filename
                        + "': : TCGA aliquot barcode in filename does not belong to the disease set for TEST",
                qcContext.getErrors().get(0));
    }

    @Test
    public void testInvalidHeaders() throws Processor.ProcessorException {

        final String barcode = "TCGA-00-0000-00A-00A-0000-00";
        final String filename = "bad-headers-TCGA-00-0000-00A-00A-0000-00.isoform.quantification.txt";

        mockery.checking(new Expectations() {
            {
                one(mockQcLiveBarcodeAndUUIDValidator)
                        .validateAliquotFormatAndCodes(barcode);
                will(returnValue(true));
                one(mockBarcodeTumorValidator).barcodeIsValidForTumor(barcode,
                        TUMOR_TYPE);
                will(returnValue(true));
            }
        });

        final File file = new File(TEST_DIR + filename);
        final boolean isValid = miRNASeqDataFileValidator.processFile(file,
                qcContext);

        assertFalse(isValid);

        final List<String> errors = qcContext.getErrors();
        assertNotNull(errors);
        assertEquals(6, errors.size());
        assertEquals(
                ERROR_MSG
                        + "'"
                        + filename
                        + "': "
                        + "Expected header 'miRNA_ID' at column '1' but found 'miRNA_ID_wrong'",
                qcContext.getErrors().get(0));
        assertEquals(
                ERROR_MSG
                        + "'"
                        + filename
                        + "': "
                        + "Expected header 'isoform_coords' at column '2' but found 'isoform_coords_wrong'",
                qcContext.getErrors().get(1));
        assertEquals(
                ERROR_MSG
                        + "'"
                        + filename
                        + "': "
                        + "Expected header 'read_count' at column '3' but found 'read_count_wrong'",
                qcContext.getErrors().get(2));
        assertEquals(
                ERROR_MSG
                        + "'"
                        + filename
                        + "': "
                        + "Expected header 'reads_per_million_miRNA_mapped' at column '4' but found 'reads_per_million_miRNA_mapped_wrong'",
                qcContext.getErrors().get(3));
        assertEquals(
                ERROR_MSG
                        + "'"
                        + filename
                        + "': "
                        + "Expected header 'cross-mapped' at column '5' but found 'cross-mapped_wrong'",
                qcContext.getErrors().get(4));
        assertEquals(
                ERROR_MSG
                        + "'"
                        + filename
                        + "': "
                        + "Expected header 'miRNA_region' at column '6' but found 'miRNA_region_wrong'",
                qcContext.getErrors().get(5));
    }

    @Test
    public void testInvalidContent() throws Processor.ProcessorException {

        final String barcode = "TCGA-00-0000-00A-00A-0000-00";
        final String filename = "bad-content-TCGA-00-0000-00A-00A-0000-00.isoform.quantification.txt";

        mockery.checking(new Expectations() {
            {
                one(mockQcLiveBarcodeAndUUIDValidator)
                        .validateAliquotFormatAndCodes(barcode);
                will(returnValue(true));
                one(mockBarcodeTumorValidator).barcodeIsValidForTumor(barcode,
                        TUMOR_TYPE);
                will(returnValue(true));
            }
        });

        final File file = new File(TEST_DIR + filename);
        final boolean isValid = miRNASeqDataFileValidator.processFile(file,
                qcContext);

        assertFalse(isValid);

        final List<String> errors = qcContext.getErrors();
        assertNotNull(errors);
        assertEquals(14, errors.size());
        assertEquals(ERROR_MSG + "'" + filename + "': "
                + " line 2: Invalid 'miRNA_ID' value: hsa-let-7a+1", qcContext
                .getErrors().get(0));
        assertEquals(ERROR_MSG + "'" + filename + "': "
                + " line 2: Invalid Strand value: *", qcContext.getErrors()
                .get(1));
        assertEquals(ERROR_MSG + "'" + filename + "': "
                + " line 2: 'read_count' value '-4' cannot be negative",
                qcContext.getErrors().get(2));
        assertEquals(ERROR_MSG + "'" + filename + "': "
                + " line 2: 'read_count' value '-1.211903' cannot be negative",
                qcContext.getErrors().get(3));
        assertEquals(ERROR_MSG + "'" + filename + "': "
                + " line 2: 'cross-mapped' value 'OUI' must be 'Y' or 'N'",
                qcContext.getErrors().get(4));
        assertEquals(ERROR_MSG + "'" + filename + "': "
                + " line 2: Invalid Annotation Word: squirrel", qcContext
                .getErrors().get(5));
        assertEquals(ERROR_MSG + "'" + filename + "': "
                + " line 3: Invalid Genome build value: hg19+", qcContext
                .getErrors().get(6));
        assertEquals(ERROR_MSG + "'" + filename + "': "
                + " line 3: Invalid Chromosome Id value: 9+", qcContext
                .getErrors().get(7));
        assertEquals(
                ERROR_MSG
                        + "'"
                        + filename
                        + "': "
                        + " line 3: Invalid start-end Coord value: -96938242-9.6938265",
                qcContext.getErrors().get(8));
        assertEquals(ERROR_MSG + "'" + filename + "': "
                + " line 4: Invalid start-end Coord value: 0-96938261",
                qcContext.getErrors().get(9));
        assertEquals(
                ERROR_MSG
                        + "'"
                        + filename
                        + "': "
                        + " line 5: Invalid Start-End Coord value (Start coord > End coord: 96938243>86938263): 96938243-86938263",
                qcContext.getErrors().get(10));
        assertEquals(ERROR_MSG + "'" + filename + "': "
                + " line 6: Invalid 'MIMAT ACC Number': SQUIRREL0000062",
                qcContext.getErrors().get(11));
        assertEquals(
                ERROR_MSG
                        + "'"
                        + filename
                        + "': "
                        + " line 7: 'miRNA_region' is invalid (It is made of 3 fields, but a maximum of 2 is expected)",
                qcContext.getErrors().get(12));
        assertEquals(
                ERROR_MSG
                        + "'"
                        + filename
                        + "': "
                        + " line 8: Invalid Isoform coords  (It is made of 5 fields, but expected 4)",
                qcContext.getErrors().get(13));
    }
}
