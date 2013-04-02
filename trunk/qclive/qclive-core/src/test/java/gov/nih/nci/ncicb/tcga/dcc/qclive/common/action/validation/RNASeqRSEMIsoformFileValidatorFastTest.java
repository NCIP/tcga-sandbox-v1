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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Test class for RNASeqRSEMIsoformFileValidator
 *
 * @author Stan Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class RNASeqRSEMIsoformFileValidatorFastTest {

    private Mockery context;
    RNASeqRSEMIsoformFileValidator validator;
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

        validator = new RNASeqRSEMIsoformFileValidator();
        validator.setBarcodeTumorValidator(mockBarcodeTumorValidator);
        validator.setQcLiveBarcodeAndUUIDValidator(mockQcLiveBarcodeAndUUIDValidator);
        validator.setSeqDataFileValidationErrorMessagePropertyType
                (MessagePropertyType.valueOf("RNA_SEQ_DATA_FILE_VALIDATION_ERROR"));

        //validator.setMe
        archive = new Archive();
        archive.setExperimentType(Experiment.TYPE_CGCC);
        archive.setArchiveType(Archive.TYPE_LEVEL_3);
        archive.setPlatform("IlluminGA_RNASeqV2");
        archive.setTumorType("TEST");
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
                allowing(mockBarcodeTumorValidator).barcodeIsValidForTumor("TCGA-44-3918-01A-01R-1107-07", "TEST");
                will(returnValue(true));

            }
        });
        File validFile = new File(TEST_FILE_DIR + File.separator + "unc.edu.TCGA-44-3918-01A-01R-1107-07.951997.rsem.isoforms.results");
        assertTrue(validator.processFile(validFile, qcContext));
    }

    @Test
    public void testProcessValidUUIDFile() throws ProcessorException {
        context.checking(new Expectations() {{
            allowing(mockQcLiveBarcodeAndUUIDValidator).isAliquotUUID("d59d48ca-16e5-46c0-80e8-6d1214acd156");
            will(returnValue(true));
            allowing(mockQcLiveBarcodeAndUUIDValidator).isMatchingDiseaseForUUID("d59d48ca-16e5-46c0-80e8-6d1214acd156", "TEST");
            will(returnValue(true));
        }});
        qcContext.setCenterConvertedToUUID(true);
        File validFile = new File(TEST_FILE_DIR + File.separator + "unc.edu.d59d48ca-16e5-46c0-80e8-6d1214acd156.rsem.isoforms.results");
        assertTrue(validator.processFile(validFile, qcContext));
    }

    @Test
    public void testProcessInValidFile() throws ProcessorException {
        context.checking(new Expectations() {
            {
                allowing(mockQcLiveBarcodeAndUUIDValidator).validateAliquotFormatAndCodes("TCGA-44-3918-01A-01R-1107-07");
                will(returnValue(true));
                allowing(mockBarcodeTumorValidator).barcodeIsValidForTumor("TCGA-44-3918-01A-01R-1107-07", "TEST");
                will(returnValue(true));

            }
        });
        File validFile = new File(TEST_FILE_DIR + File.separator + "invalid.TCGA-44-3918-01A-01R-1107-07.951997.rsem.isoforms.results");
        assertFalse(validator.processFile(validFile, qcContext));
        assertEquals("An error occurred while validating RNA sequence data file " +
                "'invalid.TCGA-44-3918-01A-01R-1107-07.951997.rsem.isoforms.results': " +
                "Expected header 'raw_count' at column '2' but found ''", qcContext.getErrors().get(0));
    }

}
