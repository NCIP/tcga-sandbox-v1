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
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.IDF;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Test class for IdfValidator
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class GccIdfValidatorFastTest {

    private static final String SAMPLES_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;

    private static final String GCC_TEST_DIR = "qclive" + File.separator
            + "idf" + File.separator
            + "gcc" + File.separator;

    private GccIdfValidator val = new GccIdfValidator();
    private Archive archive = new Archive();

    @Before
    public void setup() {
        archive.setArchiveType(Archive.TYPE_MAGE_TAB);
        archive.setDomainName("jhu-usc.edu");
        archive.setPlatform("IlluminaDNAMethylation_OMA003_CPI");
    }


    /*
    Validate an IDF which has the same number of tab-delimited values for Term Source Name, Term Source File, and Term Source Version
    - Validation passes
     */
    @Test
    public void testCorrect() throws Processor.ProcessorException {
        testScenario("qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.0", Boolean.TRUE, 0);
    }

    @Test
    public void testInvalidHeader() throws Processor.ProcessorException {
        testScenario(GCC_TEST_DIR + "headersNotAllowed", false, 1);
    }

    @Test
    public void testMissingRequiredHeaders() throws Processor.ProcessorException {
        testScenario(GCC_TEST_DIR + "headersMissing", false, 2);
    }

    @Test
    public void testBadDomain() throws Processor.ProcessorException {
        archive.setArchiveFile(new File(
                SAMPLES_DIR
                        + "qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.0"
                        + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION));
        archive.setDomainName("blah");
        QcContext qcContext = new QcContext();
        qcContext.setArchive(archive);
        assertFalse(val.execute(archive, qcContext));
        assertEquals(
                "An error occurred while processing archive 'jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.0': "
                        + ConstantValues.DOMAIN_NAME_DOES_NOT_MATCH, qcContext
                .getErrors().get(0));
    }

    @Test
    /*
    missing Term Source File
    Validate an IDF which has n tab-delimited values for Term Source Name and Term Source Version but less than n values for Term Source File
    - Validation fails
    */
    public void testMissingTermSourceFile() throws Processor.ProcessorException {
        testScenario("qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.2", Boolean.FALSE, 1);
    }

    /*
    missing Term Source Version
    Validate an IDF which has n tab-delimited values for Term Source Name and Term Source File, but less than n values for Term Source Version
    - Validation fails
    */
    @Test
    public void testMissingTermSourceVersion() throws Processor.ProcessorException {
        testScenario("qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.3", Boolean.FALSE, 1);
    }

    /*
    extra Term Source File
    Validate an IDF which was n tab-delimited values for Term Source Name and Term Source Version, but more than n values for Term Source File
    - Validation fails
    */
    @Test
    public void testExtraTermSourceFile() throws Processor.ProcessorException {
        testScenario("qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.4", Boolean.FALSE, 1);
    }

    /*
    extra Term Source Version
    Validate an IDF which was n tab-delimited values for Term Source Name and Term Source File, but more than n values for Term Source Version
    - Validation fails
    */
    @Test
    public void testExtraTermSourceVersion() throws Processor.ProcessorException {
        testScenario("qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.5", Boolean.FALSE, 1);
    }

    /*
    no Term Source
    Validate an IDF which contains no Term Source Name, no Term Source File, no Term Source Version,
    AND no term source name is referenced in a Term Source REF.     */
    @Test
    public void testNoTermSourceHeaders() throws Processor.ProcessorException {
        testScenario("qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.6", Boolean.TRUE, 0);
    }

    /*
    no Term Source File
    Validate and IDF which was Term Source Name and a matching Term Source Version line, but no Term Source File line
    - Validation fails (since there are effectively 0 Term Source File entries)
    */
    @Test
    public void testNoTermSourceFile() throws Processor.ProcessorException {
        testScenario("qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.7", Boolean.FALSE, 1);
    }

    /*
    no Term Source Version
    Validate and IDF which was Term Source Name and a matching Term Source File line, but no Term Source Version line
    - Validation fails (since there are effectively 0 Term Source Version entries)
    */
    @Test
    public void testNoTermSourceVersion() throws Processor.ProcessorException {
        testScenario("qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.8", Boolean.FALSE, 1);
    }

    /*
        no Term Source Name
        Validate and IDF which was Term Source Version and a Term Source File line with 1+ tab-delimited entries each, but no Term Source Name line
        - Validation fails (since there are effectively 0 Term Source Name entries)
    */
    @Test
    public void testNoTermSourceName() throws Processor.ProcessorException {
        testScenario("qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.9", Boolean.FALSE, 1);
    }

    /*
    missing Term Source Name
    Validate an IDF which has n tab-delimited values for Term Source Version and Term Source File but less than n values for Term Source Name
    - Validation fails
    */
    @Test
    public void testMissingTermSourceName() throws Processor.ProcessorException {
        testScenario("qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.10", Boolean.FALSE, 1);
    }

    /*
    multiple tab-delimiters
    Validate an IDF that has multiple tab delimiters between each value of any of the Term Source rows
    - Validation should fail
    */
    @Test
    public void testMultiTabDelimiters() throws Processor.ProcessorException {
        testScenario("qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.12", Boolean.FALSE, 1);
    }

    /*
    Term Source File row with no value
    Validate an IDF which has both of the other two Term Source rows (Term Source Name and Term Source Version) and contains a row which only says Term Source File, but contains no values on that row
    - Validation should fail
    */
    @Test
    public void testBlankTermSourceFile() throws Processor.ProcessorException {
        testScenario("qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.13", Boolean.FALSE, 1);
    }

    /*
    Term Source Version row with no value
    Validate an IDF which has both of the other two Term Source rows (Term Source Name and Term Source File) and contains a row which only says Term Source Version, but contains no values
    - Validation fails
    */
    @Test
    public void testBlankTermSourceVersion() throws Processor.ProcessorException {
        testScenario("qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.14", Boolean.FALSE, 1);
    }

    /*
    Term Source Name row with no value
    Validate an IDF which has both of the other two Term Source rows (Term Source File and Term Source Version) and contains a row which only says Term Source Name, but contains no values
    - Validation fails
    */
    @Test
    public void testBlankTermSourceName() throws Processor.ProcessorException {
        testScenario("qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.15", Boolean.FALSE, 1);
    }

    /*
    duplicate Term Source Name header
    Validate an IDF which has more than one row of Term Source Name
    - Validation fails
    */
    @Test
    public void testDuplicateTermSourceNameHeader() throws Processor.ProcessorException {
        testScenario("qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.11", Boolean.FALSE, 1);
    }

    /*
    duplicate Term Source File header
    Validate an IDF which has more than one row of Term Source File
    - Validation fails
    */
    @Test
    public void testDuplicateTermSourceFileHeader() throws Processor.ProcessorException {
        testScenario("qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.17", Boolean.FALSE, 1);
    }

    /*
    duplicate Term Source Version header
    Validate an IDF which has more than one row of Term Source Version
    - Validation fails
    */
    @Test
    public void testDuplicateTermSourceVersionHeader() throws Processor.ProcessorException {
        testScenario("qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.18", Boolean.FALSE, 1);
    }

    /*
    duplicate Term Source Name values
    Validate an IDF which has duplicate value in Term Source Name
    - Validation only gives a warning. The archive should still pass.
    */
    @Test
    public void testDupTermSourceNameValue() throws Processor.ProcessorException {
        testScenarioWithWarning("qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.16", Boolean.TRUE, 1);
    }

    /*
    duplicate Term Source File values
    Validate an IDF which has duplicate value in Term Source File
    - Validation only gives a warning. The archive should still pass.
    */
    @Test
    public void testDupTermSourceFileValue() throws Processor.ProcessorException {
        testScenarioWithWarning("qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.19", Boolean.TRUE, 1);
    }

    /*
    duplicate Term Source Version values
    Validate an IDF which has duplicate value in Term Source File
    - Validation only gives a warning. The archive should still pass.
    */
    @Test
    public void testDupTermSourceVersionValue() throws Processor.ProcessorException {
        testScenarioWithWarning("qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.20", Boolean.TRUE, 1);
    }

    /*
    Term source Name has > n values and term source version and term source file have n values
    */
    @Test
    public void testExtraTermSourceName() throws Processor.ProcessorException {
        testScenario("qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.22", Boolean.FALSE, 1);
    }

    /*
    Term Source Name, File and Version all have different number of values (in any one combination of difference)
    */
    @Test
    public void testDifferentValueCount() throws Processor.ProcessorException {
        testScenario("qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.23", Boolean.FALSE, 1);
    }

    /*
    Validate an IDF which contains a valid Term source Name, Revision and File,
    but the Term Source Name does not match the name that is mentioned for the Term Source REF row in the IDF.
    - Validation fails
    */
    @Test
    public void testTermSourceRefNotFound() throws Processor.ProcessorException {
        testScenario("qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.21", Boolean.FALSE, 1);
    }

    @Test
    public void testcheckForDuplicateHeaderNoDupFound() throws Processor.ProcessorException, IOException {
        QcContext qcContext = new QcContext();
        archive.setArchiveFile(new File(SAMPLES_DIR + "qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.0" + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION));
        qcContext.setArchive(archive);
        final IDF idf = val.getIdf(archive);
        assertTrue(val.checkForDuplicateHeaders(archive, qcContext, idf, "Term Source Name"));
        assertEquals(0, qcContext.getErrorCount());
    }

    @Test
    public void testcheckForDuplicateHeaderDupFound() throws Processor.ProcessorException, IOException {
        QcContext qcContext = new QcContext();
        archive.setArchiveFile(new File(SAMPLES_DIR + "qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.11" + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION));
        qcContext.setArchive(archive);
        final IDF idf = val.getIdf(archive);
        assertFalse(val.checkForDuplicateHeaders(archive, qcContext, idf, "Term Source Name"));
        assertEquals(1, qcContext.getErrorCount());
    }

    @Test
    public void testcheckForDuplicateHeaderValueNoDupFound() throws Processor.ProcessorException, IOException {
        QcContext qcContext = new QcContext();
        archive.setArchiveFile(new File(SAMPLES_DIR + "qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.0" + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION));
        qcContext.setArchive(archive);
        final IDF idf = val.getIdf(archive);
        val.checkForDuplicateValues(archive, qcContext, "some header", Arrays.asList(new String[]{"x", "y"}));
        assertEquals(0, qcContext.getWarningCount());
    }

    @Test
    public void testcheckForDuplicateHeaderValueDupFound() throws Processor.ProcessorException, IOException {
        QcContext qcContext = new QcContext();
        archive.setArchiveFile(new File(SAMPLES_DIR + "qclive/jhu-usc.edu_GBM.IlluminaDNAMethylation_OMA003_CPI.2.0.0" + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION));
        qcContext.setArchive(archive);
        final IDF idf = val.getIdf(archive);
        val.checkForDuplicateValues(archive, qcContext, "some header", Arrays.asList(new String[]{"x", "x"}));
        assertEquals(1, qcContext.getWarningCount());
    }

    private void testScenario(final String file, final Boolean shouldPass, final int errorCount) throws Processor.ProcessorException {
        QcContext qcContext = new QcContext();
        archive.setArchiveFile(new File(SAMPLES_DIR + file + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION));
        qcContext.setArchive(archive);
        assertEquals(shouldPass, val.execute(archive, qcContext));
        assertEquals(errorCount, qcContext.getErrorCount());
    }

    private void testScenarioWithWarning(final String file, final Boolean shouldPass, final int warningCount) throws Processor.ProcessorException {
        QcContext qcContext = new QcContext();
        archive.setArchiveFile(new File(SAMPLES_DIR + file + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION));
        qcContext.setArchive(archive);
        assertEquals(shouldPass, val.doWork(archive, qcContext));
        assertEquals(warningCount, qcContext.getWarningCount());
    }

}
