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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * MiRnaSeqSdrfValidator unit test
 *
 * @author Julien Baboud Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class MiRnaSeqSdrfValidatorFastTest {

    private static final String TEST_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator
            + "qclive"
            + File.separator
            + "miRnaSeqSdrfValidator" + File.separator;

    private static final String VALID_TEST_DIR = TEST_DIR + "valid"
            + File.separator;
    private static final String INVALID_TEST_DIR = TEST_DIR + "invalid"
            + File.separator;
    private static final String TUMOR_TYPE = "TEST";

    private Mockery mockery;
    private BarcodeTumorValidator mockBarcodeTumorValidator;
    private QcLiveBarcodeAndUUIDValidator mockQcLiveBarcodeAndUUIDValidator;

    private MiRnaSeqSdrfValidator miRnaSeqSdrfValidator;
    private Archive archive;
    private QcContext qcContext;

    @Before
    public void setUp() {

        mockery = new JUnit4Mockery();
        mockBarcodeTumorValidator = mockery.mock(BarcodeTumorValidator.class);
        mockQcLiveBarcodeAndUUIDValidator = mockery
                .mock(QcLiveBarcodeAndUUIDValidator.class);

        miRnaSeqSdrfValidator = new MiRnaSeqSdrfValidator();
        miRnaSeqSdrfValidator
                .setBarcodeTumorValidator(mockBarcodeTumorValidator);
        miRnaSeqSdrfValidator
                .setQcLiveBarcodeAndUUIDValidator(mockQcLiveBarcodeAndUUIDValidator);

        archive = new Archive();
        archive.setArchiveType(Archive.TYPE_MAGE_TAB);
        archive.setTumorType(TUMOR_TYPE);

        qcContext = new QcContext();
        qcContext.setArchive(archive);
    }

    @Test
    public void testValidSdrf() throws IOException,
            Processor.ProcessorException, ParseException {

        final String sdrfFilename = "good.sdrf.txt";
        final String barcode1 = "TCGA-AO-A03O-01A-11R-A010-13";
        final String barcode2 = "TCGA-A8-A06R-01A-11R-A010-13";

        final int expectedFilesPerBarcode = 3;

        mockery.checking(new Expectations() {
            {
                exactly(expectedFilesPerBarcode).of(
                        mockQcLiveBarcodeAndUUIDValidator).validate(barcode1,
                        qcContext, sdrfFilename, true);
                will(returnValue(true));
                exactly(expectedFilesPerBarcode).of(mockBarcodeTumorValidator)
                        .barcodeIsValidForTumor(barcode1, TUMOR_TYPE);
                will(returnValue(true));
                exactly(expectedFilesPerBarcode).of(
                        mockQcLiveBarcodeAndUUIDValidator).validate(barcode2,
                        qcContext, sdrfFilename, true);
                will(returnValue(true));
                exactly(expectedFilesPerBarcode).of(mockBarcodeTumorValidator)
                        .barcodeIsValidForTumor(barcode2, TUMOR_TYPE);
                will(returnValue(true));
            }
        });

        setSdrf(sdrfFilename, true);

        final boolean isValid = miRnaSeqSdrfValidator
                .doWork(archive, qcContext);
        assertTrue(qcContext.getErrors().toString(), isValid);
        assertEquals(qcContext.getErrors().toString(), 0,
                qcContext.getErrorCount());
        assertEquals(qcContext.getWarnings().toString(), 0,
                qcContext.getWarningCount());
    }

    @Test
    public void testValidSdrfMissingWig() throws IOException,
            Processor.ProcessorException, ParseException {

        final String sdrfFilename = "warningMissingWig.sdrf.txt";
        final String barcode1 = "TCGA-AO-A03O-01A-11R-A010-13";
        final String barcode2 = "TCGA-A8-A06R-01A-11R-A010-13";

        final int expectedFilesPerBarcode = 2;

        mockery.checking(new Expectations() {
            {
                exactly(expectedFilesPerBarcode).of(
                        mockQcLiveBarcodeAndUUIDValidator).validate(barcode1,
                        qcContext, sdrfFilename, true);
                will(returnValue(true));
                exactly(expectedFilesPerBarcode).of(mockBarcodeTumorValidator)
                        .barcodeIsValidForTumor(barcode1, TUMOR_TYPE);
                will(returnValue(true));
                exactly(expectedFilesPerBarcode).of(
                        mockQcLiveBarcodeAndUUIDValidator).validate(barcode2,
                        qcContext, sdrfFilename, true);
                will(returnValue(true));
                exactly(expectedFilesPerBarcode).of(mockBarcodeTumorValidator)
                        .barcodeIsValidForTumor(barcode2, TUMOR_TYPE);
                will(returnValue(true));
            }
        });

        setSdrf(sdrfFilename, true);

        final boolean isValid = miRnaSeqSdrfValidator
                .doWork(archive, qcContext);

        assertTrue(qcContext.getErrors().toString(), isValid);
        assertEquals(qcContext.getErrors().toString(), 0,
                qcContext.getErrorCount());
        assertEquals(qcContext.getWarnings().toString(), 2,
                qcContext.getWarningCount());
        assertEquals(qcContext.getWarnings().get(0), "Value 'wig file' for '"
                + barcode1 + "' was not provided");
        assertEquals(qcContext.getWarnings().get(1), "Value 'wig file' for '"
                + barcode2 + "' was not provided");
    }

    @Test
    public void testInvalidSdrf() throws IOException,
            Processor.ProcessorException, ParseException {

        final String sdrfFilename = "bad.sdrf.txt";
        final String barcode = "TCGA-A8-A06R-01A-11R-A010-13";

        setSdrf(sdrfFilename, false);

        final boolean isValid = miRnaSeqSdrfValidator
                .doWork(archive, qcContext);

        assertFalse(qcContext.getErrors().toString(), isValid);
        assertEquals(qcContext.getErrors().toString(), 5,
                qcContext.getErrorCount());

        assertEquals(
                qcContext.getErrors().get(0),
                "line 2, column 20: 'Quantification_miRNA_Isoform' file must have extension 'isoform.quantification.txt'");
        assertEquals(
                qcContext.getErrors().get(1),
                "line 3, column 20: 'Quantification_miRNA' file must have extension 'mirna.quantification.txt'");
        assertEquals(qcContext.getErrors().get(2),
                "line 4, column 20: 'coverage' file must have extension '.wig'");
        assertEquals(qcContext.getErrors().get(3),
                "Missing required value(s) 'mirna.quantification.txt' for '"
                        + barcode + "'");
        assertEquals(qcContext.getErrors().get(4),
                "Missing required value(s) 'isoform.quantification.txt' for '"
                        + barcode + "'");

        assertEquals(qcContext.getWarnings().toString(), 4,
                qcContext.getWarningCount());
        assertEquals(
                qcContext.getWarnings().get(0),
                "line 5, column 20: Derived Data File 'TCGA-A8-A06R-01A-11R-A010.isoform.quantification.xml' does not have a known Data Transformation Name and will not be validated");
        assertEquals(
                qcContext.getWarnings().get(1),
                "line 6, column 20: Derived Data File 'TCGA-A8-A06R-01A-11R-A010.mirna.quantification.xml' does not have a known Data Transformation Name and will not be validated");
        assertEquals(
                qcContext.getWarnings().get(2),
                "line 7, column 20: Derived Data File 'TCGA-A8-A06R-01A-11R-A010.wig.xml' does not have a known Data Transformation Name and will not be validated");
        assertEquals(qcContext.getWarnings().get(3), "Value 'wig file' for '"
                + barcode + "' was not provided");
    }

    /**
     * Set the SDRF for the archive
     *
     * @param sdrfFilename the SDRF filename
     * @param valid        <code>true</code> if the SDRF is valid, <code>false</code>
     *                     otherwise
     * @throws IOException if there is an error reading the file
     */
    private void setSdrf(final String sdrfFilename, final boolean valid)
            throws IOException, ParseException {

        final String archiveName = TEST_DIR + (valid ? "valid" : "invalid")
                + ".tar.gz";
        final String testDir = valid ? VALID_TEST_DIR : INVALID_TEST_DIR;

        archive.setArchiveFile(new File(archiveName));
        archive.setSdrfFile(new File(sdrfFilename));
        archive.setSdrf(getTabDelimitedContent(testDir + sdrfFilename));
    }

    /**
     * Return the <code>TabDelimitedContent</code> for the file with the given
     * name
     *
     * @param filename the name of the file
     * @return the <code>TabDelimitedContent</code> for the file with the given
     *         name
     * @throws IOException if there is an error reading the file
     */
    private TabDelimitedContent getTabDelimitedContent(final String filename)
            throws IOException, ParseException {

        final TabDelimitedFileParser sdrfParser = new TabDelimitedFileParser();
        sdrfParser.setTabDelimitedContent(new TabDelimitedContentImpl());
        sdrfParser.loadTabDelimitedContent(filename);
        sdrfParser.loadTabDelimitedContentHeader();

        return sdrfParser.getTabDelimitedContent();
    }
}
