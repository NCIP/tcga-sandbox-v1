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
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.util.MessagePropertyType;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.BarcodeTumorValidator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcLiveBarcodeAndUUIDValidator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.assertTrue;


/**
 * Test class for RNASeqRSEMGenesFileValidator
 *
 * @author Stan Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class RNASeqRSEMGenesFileValidatorFastTest {

    private Mockery context;
    RNASeqRSEMGeneResultsFileValidator validator;
    private BarcodeTumorValidator mockBarcodeTumorValidator;
    private QcLiveBarcodeAndUUIDValidator mockQcLiveBarcodeAndUUIDValidator;

    private Archive archive;
    private QcContext qcContext;

    private static final String SAMPLES_DIR =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private static final String TEST_FILE_DIR = SAMPLES_DIR + "qclive/rnaseqv2files";


    @Before
    public void setUp() {
        context = new JUnit4Mockery();
        mockBarcodeTumorValidator = context.mock(BarcodeTumorValidator.class);
        mockQcLiveBarcodeAndUUIDValidator = context.mock(QcLiveBarcodeAndUUIDValidator.class);

        validator = new RNASeqRSEMGeneResultsFileValidator();
        validator.setBarcodeTumorValidator(mockBarcodeTumorValidator);
        validator.setQcLiveBarcodeAndUUIDValidator(mockQcLiveBarcodeAndUUIDValidator);
        validator.setSeqDataFileValidationErrorMessagePropertyType
                (MessagePropertyType.valueOf("RNA_SEQ_DATA_FILE_VALIDATION_ERROR"));

        archive = new Archive();
        archive.setExperimentType(Experiment.TYPE_CGCC);
        archive.setArchiveType(Archive.TYPE_LEVEL_3);
        archive.setPlatform("IlluminGA_RNASeqV2");
        qcContext = new QcContext();
        qcContext.setArchive(archive);
        qcContext.setCenterConvertedToUUID(false);
    }

    @Test
    public void testProcessValidFile() throws ProcessorException {
        context.checking(new Expectations() {
            {
                allowing(mockQcLiveBarcodeAndUUIDValidator).validateAliquotFormatAndCodes("TCGA-44-3918-01A-01R-1107-07");
                will(returnValue(true));
                allowing(mockBarcodeTumorValidator).barcodeIsValidForTumor("TCGA-44-3918-01A-01R-1107-07", null);
                will(returnValue(true));

            } });
        File validFile = new File(TEST_FILE_DIR + File.separator + "unc.edu.TCGA-44-3918-01A-01R-1107-07.951996.rsem.genes.results");
        assertTrue(validator.processFile(validFile, qcContext));
    }

    @Test
    public void testProcessInValidFile() throws ProcessorException {
        context.checking(new Expectations() {
            {
                allowing(mockQcLiveBarcodeAndUUIDValidator).validateAliquotFormatAndCodes("TCGA-44-3918-01A-01R-1107-07");
                will(returnValue(true));
                allowing(mockBarcodeTumorValidator).barcodeIsValidForTumor("TCGA-44-3918-01A-01R-1107-07", null);
                will(returnValue(true));

            } });
        File validFile = new File(TEST_FILE_DIR + File.separator + "invalid.TCGA-44-3918-01A-01R-1107-07.951996.rsem.genes.results");
        Assert.assertFalse(validator.processFile(validFile, qcContext));
        Assert.assertEquals("An error occurred while validating RNA " +
                "sequence data file 'invalid.TCGA-44-3918-01A-01R-1107-07.951996.rsem.genes.results': " +
                "Expected header 'scaled_estimate' at column '3' but found ''", qcContext.getErrors().get(0));
    }

}
