/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Test class for TraceFileValidator
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class TraceFileValidatorFastTest {

    private static final String SAMPLES_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;
    private String goodFile = SAMPLES_DIR + "qclive/traceFileValidator/good"
            + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;
    private String badBarcodeFile = SAMPLES_DIR
            + "qclive/traceFileValidator/bad_barcode"
            + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;
    private String badTraceidFile = SAMPLES_DIR
            + "qclive/traceFileValidator/bad_traceid"
            + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION;
    private TraceFileValidator traceFileValidator;
    private Archive archive;
    private QcContext qcContext;

    private final Mockery mockery = new JUnit4Mockery();
    private QcLiveBarcodeAndUUIDValidator mockQcLiveBarcodeAndUUIDValidator;
    private BarcodeTumorValidator mockBarcodeTumorValidator;

    @Before
    public void setup() {

        mockQcLiveBarcodeAndUUIDValidator = mockery.mock(QcLiveBarcodeAndUUIDValidator.class);
        mockBarcodeTumorValidator = mockery.mock(BarcodeTumorValidator.class);

        traceFileValidator = new TraceFileValidator(mockQcLiveBarcodeAndUUIDValidator);
        traceFileValidator.setBarcodeTumorValidator(mockBarcodeTumorValidator);

        archive = new Archive();
        archive.setExperimentType(Experiment.TYPE_GSC);
        archive.setArchiveType(Archive.TYPE_LEVEL_1);

        qcContext = new QcContext();
        qcContext.setArchive(archive);
    }

    @Test
    public void testGood() throws Processor.ProcessorException {

        archive.setArchiveFile(new File(goodFile));
        archive.setDeployLocation(goodFile);

        mockery.checking(new Expectations() {{
            exactly(1).of(mockQcLiveBarcodeAndUUIDValidator).validate("TCGA-00-0000-00A-00B-0000-00", qcContext, "file.tr", true);
            will(returnValue(true));
            exactly(1).of(mockQcLiveBarcodeAndUUIDValidator).validate("TCGA-01-0000-00A-00B-0000-00", qcContext, "file.tr", true);
            will(returnValue(true));
            exactly(1).of(mockQcLiveBarcodeAndUUIDValidator).validate("TCGA-02-0000-00A-00B-0000-00", qcContext, "file.tr", true);
            will(returnValue(true));

            allowing(mockBarcodeTumorValidator).barcodeIsValidForTumor(with(any(String.class)), with(any(String.class)));
            will(returnValue(true));
        }});

        assertTrue("Validation did not pass", traceFileValidator.execute(archive, qcContext));
    }

    @Test
    public void testGoodStandalone() throws Processor.ProcessorException {

        archive.setArchiveFile(new File(goodFile));
        archive.setDeployLocation(goodFile);

        // Standalone setup
        qcContext.setStandaloneValidator(true);

        final String barcode1 = "barcode";
        final String barcode2 = "TCGA-00-0000-00A-00B-0000-00";
        final String barcode3 = "TCGA-01-0000-00A-00B-0000-00";
        final String barcode4 = "TCGA-02-0000-00A-00B-0000-00";

        final List<String> barcodes = new LinkedList<String>();
        barcodes.add(barcode1);
        barcodes.add(barcode2);
        barcodes.add(barcode3);
        barcodes.add(barcode4);

        final Map<String, Boolean> barcodeValidityMap = new HashMap<String, Boolean>();
        barcodeValidityMap.put(barcode1, false);
        barcodeValidityMap.put(barcode2, true);
        barcodeValidityMap.put(barcode3, true);
        barcodeValidityMap.put(barcode4, true);

        mockery.checking(new Expectations() {{
            exactly(1).of(mockQcLiveBarcodeAndUUIDValidator).batchValidateReportIndividualResults(barcodes, qcContext, "file.tr", true);
            will(returnValue(barcodeValidityMap));

            allowing(mockBarcodeTumorValidator).barcodeIsValidForTumor(with(any(String.class)), with(any(String.class)));
            will(returnValue(true));
        }});

        assertTrue("Validation did not pass", traceFileValidator.execute(archive, qcContext));
    }

    @Test
    public void testBadBarcode() throws Processor.ProcessorException {

        archive.setArchiveFile(new File(badBarcodeFile));
        archive.setDeployLocation(badBarcodeFile);

        mockery.checking(new Expectations() {{
            exactly(1).of(mockQcLiveBarcodeAndUUIDValidator).validate("00-0000-00A-00B-0000-00", qcContext, "file.tr", true);
            will(returnValue(false));
            exactly(1).of(mockQcLiveBarcodeAndUUIDValidator).validate("TCGA-01-0000-00A-00B-0000-00", qcContext, "file.tr", true);
            will(returnValue(true));
            exactly(1).of(mockQcLiveBarcodeAndUUIDValidator).validate("TCGA-02-0000-00A-00B-0000-00", qcContext, "file.tr", true);
            will(returnValue(true));

            allowing(mockBarcodeTumorValidator).barcodeIsValidForTumor(with(any(String.class)), with(any(String.class)));
            will(returnValue(true));
        }});

        assertFalse("Validation passed", traceFileValidator.execute(archive, qcContext));
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("An error occurred while validating Trace file 'file.tr':  " +
                "line 2 contains an invalid barcode: 00-0000-00A-00B-0000-00", qcContext.getErrors().get(0));
    }

    @Test
    public void testBadBarcodeStandalone() throws Processor.ProcessorException {

        archive.setArchiveFile(new File(badBarcodeFile));
        archive.setDeployLocation(badBarcodeFile);

        // Standalone setup
        qcContext.setStandaloneValidator(true);

        final String barcode1 = "barcode";
        final String barcode2 = "00-0000-00A-00B-0000-00";
        final String barcode3 = "TCGA-01-0000-00A-00B-0000-00";
        final String barcode4 = "TCGA-02-0000-00A-00B-0000-00";

        final List<String> barcodes = new LinkedList<String>();
        barcodes.add(barcode1);
        barcodes.add(barcode2);
        barcodes.add(barcode3);
        barcodes.add(barcode4);

        final Map<String, Boolean> barcodeValidityMap = new HashMap<String, Boolean>();
        barcodeValidityMap.put(barcode1, false);
        barcodeValidityMap.put(barcode2, false);
        barcodeValidityMap.put(barcode3, true);
        barcodeValidityMap.put(barcode4, true);

        mockery.checking(new Expectations() {{
            exactly(1).of(mockQcLiveBarcodeAndUUIDValidator).batchValidateReportIndividualResults(barcodes, qcContext, "file.tr", true);
            will(returnValue(barcodeValidityMap));

            allowing(mockBarcodeTumorValidator).barcodeIsValidForTumor(with(any(String.class)), with(any(String.class)));
            will(returnValue(true));
        }});

        assertFalse("Validation passed", traceFileValidator.execute(archive, qcContext));
        assertEquals(1, qcContext.getErrorCount());
        assertEquals("An error occurred while validating Trace file 'file.tr':  " +
                "line 2 contains an invalid barcode: 00-0000-00A-00B-0000-00", qcContext.getErrors().get(0));
    }

    @Test
    public void testBadTraceId() throws Processor.ProcessorException {
        archive.setArchiveFile(new File(badTraceidFile));
        archive.setDeployLocation(badTraceidFile);

        mockery.checking(new Expectations() {{
            exactly(1).of(mockQcLiveBarcodeAndUUIDValidator).validate("TCGA-00-0000-00A-00B-0000-00", qcContext, "file.tr", true);
            will(returnValue(true));
            exactly(1).of(mockQcLiveBarcodeAndUUIDValidator).validate("TCGA-01-0000-00A-00B-0000-00", qcContext, "file.tr", true);
            will(returnValue(true));
            exactly(1).of(mockQcLiveBarcodeAndUUIDValidator).validate("TCGA-02-0000-00A-00B-0000-00", qcContext, "file.tr", true);
            will(returnValue(true));

            allowing(mockBarcodeTumorValidator).barcodeIsValidForTumor(with(any(String.class)), with(any(String.class)));
            will(returnValue(true));
        }});

        assertFalse("Validation passed",
                traceFileValidator.execute(archive, qcContext));
    }

    @Test(expected = Processor.ProcessorException.class)
    public void testWrongLevelGoodFile() throws Processor.ProcessorException {
        archive.setArchiveType(Archive.TYPE_LEVEL_2); // make it a level 2 archive
        archive.setArchiveFile(new File(goodFile));
        archive.setDeployLocation(goodFile);

        traceFileValidator.execute(archive, qcContext);
    }

    @Test(expected = Processor.ProcessorException.class)
    public void testWrongLevelGoodFileStandalone() throws Processor.ProcessorException {
        archive.setArchiveType(Archive.TYPE_LEVEL_2); // make it a level 2 archive
        archive.setArchiveFile(new File(goodFile));
        archive.setDeployLocation(goodFile);

        // Standalone setup
        qcContext.setStandaloneValidator(true);

        traceFileValidator.execute(archive, qcContext);
    }
}
