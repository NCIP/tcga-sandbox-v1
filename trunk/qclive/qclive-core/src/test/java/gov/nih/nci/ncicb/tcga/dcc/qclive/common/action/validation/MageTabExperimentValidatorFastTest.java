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
import gov.nih.nci.ncicb.tcga.dcc.common.bean.FileInfo;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContent;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TabDelimitedContentImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.DataMatrix;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.DataMatrixParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParserImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedContentNavigator;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.TabDelimitedFileParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.Processor.ProcessorException;
import gov.nih.nci.ncicb.tcga.dcc.qclive.dao.ExperimentQueries;
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
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test class for CgccExperimentValidator
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
@RunWith(JMock.class)
public class MageTabExperimentValidatorFastTest {

    private final Mockery context = new JUnit4Mockery();
    private Experiment experiment = new Experiment();
    private MageTabExperimentValidator validator;
    private ExperimentQueries mockExperimentQueries;
    private static final String SAMPLES_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;
    private String baseArchiveLocation = SAMPLES_DIR + "qclive/cgccValidator/";
    private Archive newLevel2Const, newLevel2Archive, newLevel2ArchiveWig;
    private QcContext qcContext;
    private String matrixGroupFolder = baseArchiveLocation + File.separator
            + "matrixGroup" + File.separator;
    private DataMatrix matrix_normal;
    private DataMatrix matrix_short;
    private DataMatrix matrix_same;

    private Archive createArchive(final String archiveName,
                                  final String archiveType, final String archiveStatus,
                                  final int batch, final int revision) {
        Archive archive = new Archive();
        archive.setArchiveFile(new File(baseArchiveLocation + archiveName
                + ".tar.gz"));
        archive.setDeployLocation(baseArchiveLocation + archiveName + ".tar.gz");
        archive.setRealName(archiveName);
        archive.setArchiveType(archiveType);
        archive.setDeployStatus(archiveStatus);
        archive.setSerialIndex(String.valueOf(batch));
        archive.setRevision(String.valueOf(revision));
        return archive;
    }

    @Before
    public void setup() {
        qcContext = new QcContext();
        qcContext.setExperimentRequiresMageTab(true);
        experiment.setType(Experiment.TYPE_CGCC);
        // level 1 archive
        Archive level1Archive = createArchive(
                "domain_disease.platform.Level_1.1.0.0", Archive.TYPE_LEVEL_1,
                Archive.STATUS_UPLOADED, 1, 0);
        level1Archive.setPlatform("platform");
        // old (deployed) level 2 archive
        Archive oldLevel2Archive = createArchive(
                "domain_disease.platform.Level_2.1.1.0", Archive.TYPE_LEVEL_2,
                Archive.STATUS_AVAILABLE, 1, 1);
        oldLevel2Archive.setPlatform("platform");
        // new (uploaded) level 2 archive
        newLevel2Archive = createArchive(
                "domain_disease.platform.Level_2.1.2.0", Archive.TYPE_LEVEL_2,
                Archive.STATUS_UPLOADED, 1, 2);
        newLevel2Archive.setPlatform("platform");
        // alternate new level 2 archive
        newLevel2Const = createArchive("domain_disease.platform.Level_2.1.3.0",
                Archive.TYPE_LEVEL_2, Archive.STATUS_UPLOADED, 1, 3);
        newLevel2Const.setPlatform("platform");
        //new level 2 archive with a wig file
        newLevel2ArchiveWig = createArchive(
                "domain_disease.platform.Level_2.1.4.0", Archive.TYPE_LEVEL_2,
                Archive.STATUS_UPLOADED, 1, 4);
        newLevel2ArchiveWig.setPlatform("platform");

        experiment.addArchive(level1Archive);
        experiment.addPreviousArchive(oldLevel2Archive);
        validator = new MageTabExperimentValidator();
        validator.setMatrixParser(new DataMatrixParser());
        validator.setMatrixValidator(new DataMatrixValidator());
        validator.setManifestParser(new ManifestParserImpl());
        mockExperimentQueries = context.mock(ExperimentQueries.class);
        validator.setExperimentQueries(mockExperimentQueries);

        matrix_normal = new DataMatrix();
        matrix_normal
                .setFile(new File(matrixGroupFolder + "matrix_normal.txt"));
        matrix_normal.setFilename("matrix_normal.txt");
        matrix_normal.setNumReporters(10);
        matrix_normal.setConstantTypes(new String[]{"const1", "const2"});
        matrix_normal.setQuantitationTypes(new String[]{"value"});
        matrix_short = new DataMatrix();
        matrix_short.setFile(new File(matrixGroupFolder + "matrix_short.txt"));
        matrix_short.setFilename("matrix_short.txt");
        matrix_short.setNumReporters(9); // one shorter than normal
        matrix_short.setConstantTypes(matrix_normal.getConstantTypes());
        matrix_short.setQuantitationTypes(matrix_normal.getQuantitationTypes());
        matrix_same = new DataMatrix();
        matrix_same.setFile(new File(matrixGroupFolder + "matrix_same.txt"));
        matrix_same.setFilename("matrix_same.txt");
        matrix_same.setNumReporters(matrix_normal.getReporterCount());
        matrix_same.setConstantTypes(matrix_normal.getConstantTypes());
        matrix_same.setQuantitationTypes(matrix_normal.getQuantitationTypes());


    }

    private void setupArchive() {

        experiment = new Experiment();
        experiment.setType(Experiment.TYPE_CGCC);

        final Archive level1RevisedArchive = new Archive();
        level1RevisedArchive.setArchiveFile(new File(baseArchiveLocation
                + "domain_disease.platform.Level_1.2.0.0.tar.gz"));
        level1RevisedArchive.setDeployLocation(baseArchiveLocation
                + "domain_disease.platform.Level_1.2.0.0.tar.gz");
        level1RevisedArchive.setDeployStatus(Archive.STATUS_UPLOADED);
        level1RevisedArchive
                .setRealName("domain_disease.platform.Level_1.2.0.0");
        level1RevisedArchive.setArchiveType(Archive.TYPE_LEVEL_1);
        level1RevisedArchive.setPlatform("platform");
        Archive level2RevisedArchive = new Archive();
        level2RevisedArchive.setArchiveFile(new File(baseArchiveLocation
                + "domain_disease.platform.Level_2.2.1.0.tar.gz"));
        level2RevisedArchive.setDeployLocation(baseArchiveLocation
                + "domain_disease.platform.Level_2.2.1.0.tar.gz");
        level2RevisedArchive.setDeployStatus(Archive.STATUS_UPLOADED);
        level2RevisedArchive
                .setRealName("domain_disease.platform.Level_2.2.1.0");
        level2RevisedArchive.setArchiveType(Archive.TYPE_LEVEL_2);
        level2RevisedArchive.setPlatform("platform");
        experiment.addArchive(level1RevisedArchive);
        experiment.addArchive(level2RevisedArchive);

        validator = new MageTabExperimentValidator();
        validator.setMatrixParser(new DataMatrixParser());
        validator.setMatrixValidator(new DataMatrixValidator());
    }

    private void setupCgccProteinArrayArchive() {

        experiment = new Experiment();
        experiment.setType(Experiment.TYPE_CGCC);

        final Archive level1Archive = new Archive();
        level1Archive.setArchiveFile(new File(baseArchiveLocation
                + "mdanderson.org_OV.MDA_RPPA_Core.Level_1.1.0.0.tar.gz"));
        level1Archive.setDeployLocation(baseArchiveLocation
                + "mdanderson.org_OV.MDA_RPPA_Core.Level_1.1.0.0.tar.gz");
        level1Archive.setDeployStatus(Archive.STATUS_UPLOADED);
        level1Archive
                .setRealName("mdanderson.org_OV.MDA_RPPA_Core.Level_1.1.0.0");
        level1Archive.setArchiveType(Archive.TYPE_LEVEL_1);
        level1Archive.setPlatform("MDA_RPPA_Core");

        Archive level2Archive = new Archive();
        level2Archive.setArchiveFile(new File(baseArchiveLocation
                + "mdanderson.org_OV.MDA_RPPA_Core.Level_2.1.0.0.tar.gz"));
        level2Archive.setDeployLocation(baseArchiveLocation
                + "mdanderson.org_OV.MDA_RPPA_Core.Level_2.1.0.0.tar.gz");
        level2Archive.setDeployStatus(Archive.STATUS_UPLOADED);
        level2Archive
                .setRealName("mdanderson.org_OV.MDA_RPPA_Core.Level_2.1.0.0");
        level2Archive.setArchiveType(Archive.TYPE_LEVEL_2);
        level2Archive.setPlatform("MDA_RPPA_Core");

        Archive level3Archive = new Archive();
        level3Archive.setArchiveFile(new File(baseArchiveLocation
                + "mdanderson.org_OV.MDA_RPPA_Core.Level_3.1.0.0.tar.gz"));
        level3Archive.setDeployLocation(baseArchiveLocation
                + "mdanderson.org_OV.MDA_RPPA_Core.Level_3.1.0.0.tar.gz");
        level3Archive.setDeployStatus(Archive.STATUS_UPLOADED);
        level3Archive
                .setRealName("mdanderson.org_OV.MDA_RPPA_Core.Level_3.1.0.0");
        level3Archive.setArchiveType(Archive.TYPE_LEVEL_2);
        level3Archive.setPlatform("MDA_RPPA_Core");

        experiment.addArchive(level1Archive);
        experiment.addArchive(level2Archive);
        experiment.addArchive(level3Archive);

        validator = new MageTabExperimentValidator();
        validator.setMatrixParser(new DataMatrixParser());
        validator.setMatrixValidator(new DataMatrixValidator());
    }

    private void setupCgccArchive() {

        experiment = new Experiment();
        experiment.setType(Experiment.TYPE_CGCC);

        final Archive level1Archive = new Archive();
        level1Archive.setArchiveFile(new File(baseArchiveLocation
                + "hms.harvard.edu_OV.HG-CGH-244A.Level_1.1.0.0.tar.gz"));
        level1Archive.setDeployLocation(baseArchiveLocation
                + "hms.harvard.edu_OV.HG-CGH-244A.Level_1.1.0.0.tar.gz");
        level1Archive.setDeployStatus(Archive.STATUS_UPLOADED);
        level1Archive
                .setRealName("hms.harvard.edu_OV.HG-CGH-244A.Level_1.1.0.0");
        level1Archive.setArchiveType(Archive.TYPE_LEVEL_1);
        level1Archive.setPlatform("HG-CGH-244A");
        Archive level2Archive = new Archive();
        level2Archive.setArchiveFile(new File(baseArchiveLocation
                + "hms.harvard.edu_OV.HG-CGH-244A.Level_2.1.0.0.tar.gz"));
        level2Archive.setDeployLocation(baseArchiveLocation
                + "hms.harvard.edu_OV.HG-CGH-244A.Level_2.1.0.0.tar.gz");
        level2Archive.setDeployStatus(Archive.STATUS_UPLOADED);
        level2Archive
                .setRealName("hms.harvard.edu_OV.HG-CGH-244A.Level_2.1.0.0");
        level2Archive.setArchiveType(Archive.TYPE_LEVEL_2);
        level2Archive.setPlatform("HG-CGH-244A");
        experiment.addArchive(level1Archive);
        experiment.addArchive(level2Archive);

        validator = new MageTabExperimentValidator();
        validator.setMatrixParser(new DataMatrixParser());
        validator.setMatrixValidator(new DataMatrixValidator());
    }

    private void setupSdrf(final String name) throws IOException, ParseException {
        TabDelimitedFileParser sdrfParser = new TabDelimitedFileParser();
        TabDelimitedContent sdrf = new TabDelimitedContentImpl();
        sdrfParser.setTabDelimitedContent(sdrf);
        sdrfParser.initialize(baseArchiveLocation + "sdrfs/" + name);
        experiment.setSdrf(sdrf);
    }

    @Test
    public void testWrongExperimentType() throws Processor.ProcessorException {
        Experiment bcrExperiment = new Experiment();
        qcContext.setExperimentRequiresMageTab(false);
        bcrExperiment.setType(Experiment.TYPE_BCR);
        assertTrue(validator.execute(bcrExperiment, qcContext));
    }

    @Test
    public void testAuxOnly() throws Processor.ProcessorException {
        qcContext.setExperimentRequiresMageTab(false);
        Experiment auxExperiment = new Experiment();
        auxExperiment.setType(Experiment.TYPE_CGCC);
        Archive auxArchive1 = new Archive();
        auxArchive1.setDeployStatus(Archive.STATUS_UPLOADED);
        auxArchive1.setArchiveType(Archive.TYPE_AUX);
        Archive auArchive2 = new Archive();
        auArchive2.setDeployStatus(Archive.STATUS_UPLOADED);
        auArchive2.setArchiveType(Archive.TYPE_AUX);
        auxExperiment.setArchives(Arrays.asList(auxArchive1, auArchive2));

        assertTrue(validator.execute(auxExperiment, qcContext));
    }

    @Test
    public void test() throws Processor.ProcessorException, IOException, ParseException {
        setupSdrf("good.sdrf.txt");
        experiment.addArchive(newLevel2Archive);
        newLevel2Archive.setPlatform("platform");

        context.checking(new Expectations() {{
            allowing(mockExperimentQueries).getExperimentDataFiles(experiment.getName());
            will(returnValue(new HashMap<Archive, List<FileInfo>>()));

        }});

        boolean isValid = validator.execute(experiment, qcContext);
        assertTrue("Experiment did not validate: "
                + qcContext.getErrors().toString(), isValid);
    }

    @Test
    public void testRemoteValidation() throws IOException, ProcessorException, ParseException {

        setupSdrf("remote.sdrf.txt");
        experiment.addArchive(newLevel2Archive);
        newLevel2Archive.setPlatform("platform");
        validator.setRemote(true);
        context.checking(new Expectations() {{
            allowing(mockExperimentQueries).getExperimentDataFiles(experiment.getName());
            will(returnValue(new HashMap<Archive, List<FileInfo>>()));

        }});
        boolean isValid = validator.execute(experiment, qcContext);
        assertTrue("Experiment did not validate: "
                + qcContext.getErrors().toString(), isValid);
        assertEquals(qcContext.getWarnings().get(0), "Archive 'domain_disease.platform.Level_1.1.0.0 " +
                "The file 'file_remote.txt' is listed in SDRF and is not in the current submission group.  " +
                "The remote validator is not able to verify this case, however DCC will attempt to " +
                "locate this file and validate upon submission.	[null_null.null]");
    }

    @Test
    public void testRemoteValidationFail() throws IOException, ProcessorException, ParseException {

        setupSdrf("remote.sdrf.txt");
        experiment.addArchive(newLevel2Archive);
        newLevel2Archive.setPlatform("platform");
        validator.setRemote(false);
        boolean isValid = validator.execute(experiment, qcContext);
        assertFalse(isValid);
    }

    @Test
    public void testFail() throws IOException, Processor.ProcessorException, ParseException {
        setupSdrf("fail.sdrf.txt");
        experiment.addArchive(newLevel2Const);
        boolean isValid = validator.execute(experiment, qcContext);
        assertFalse(isValid);
        assertEquals(Archive.STATUS_INVALID, newLevel2Const.getDeployStatus());
        assertTrue(qcContext.getErrorCount() > 0);
    }

    @Test
    public void testBadMatrix() throws Processor.ProcessorException,
            IOException, ParseException {
        setupSdrf("good.sdrf.txt");
        experiment.addArchive(newLevel2Archive);
        // one of the matrices referred to does not match the others, but this
        // is no longer checked so this should not fail
        newLevel2Archive.setArchiveFile(new File(baseArchiveLocation + "bad"
                + File.separator
                + "domain_disease.platform.Level_2.1.2.0.tar.gz"));
        newLevel2Archive.setDeployLocation(baseArchiveLocation + "bad"
                + File.separator
                + "domain_disease.platform.Level_2.1.2.0.tar.gz");
        newLevel2Archive.setPlatform("platform");
        context.checking(new Expectations() {{
            allowing(mockExperimentQueries).getExperimentDataFiles(experiment.getName());
            will(returnValue(new HashMap<Archive, List<FileInfo>>()));

        }});
        assertTrue(validator.execute(experiment, qcContext));
        assertEquals(Archive.STATUS_UPLOADED,
                newLevel2Archive.getDeployStatus());
    }

    @Test
    public void testMissing() throws IOException, Processor.ProcessorException, ParseException {
        // a file in this SDRF is not in the archive specified
        setupSdrf("missing.sdrf.txt");
        experiment.addArchive(newLevel2Archive);
        assertFalse(validator.execute(experiment, qcContext));
        assertEquals(Archive.STATUS_INVALID, newLevel2Archive.getDeployStatus());
        assertTrue(qcContext.getErrorCount() > 0);
    }

    @Test
    public void testCGCCExperimentWithLevel2ArchiveContainingWigFile() throws Exception {
        //test to see that a wig file in a GCC experiment is not validated (it still has to be declared in the sdrf)
        experiment = new Experiment();
        experiment.setType(Experiment.TYPE_CGCC);
        validator = new MageTabExperimentValidator();
        validator.setMatrixParser(new DataMatrixParser());
        validator.setMatrixValidator(new DataMatrixValidator());
        validator.setExperimentQueries(mockExperimentQueries);
        setupSdrf("goodwig.sdrf.txt");
        experiment.addArchive(newLevel2ArchiveWig);
        newLevel2ArchiveWig.setPlatform("platform");
        context.checking(new Expectations() {{
            allowing(mockExperimentQueries).getExperimentDataFiles(experiment.getName());
            will(returnValue(new HashMap<Archive, List<FileInfo>>()));

        }});
        boolean isValid = validator.execute(experiment, qcContext);
        assertTrue("Experiment did not validate: "
                + qcContext.getErrors().toString(), isValid);
    }

    @Test
    public void testColumnWithDifferentProtocols() throws IOException,
            Processor.ProcessorException, ParseException {
        // this SDRF has a file column where the files are for 2 different
        // procotols, so they should not be compared
        // when validating the matrix data
        Archive testArchive = createArchive(
                "center.org_TUM.platform.Level_2.1.0.0", Archive.TYPE_LEVEL_2,
                Archive.STATUS_UPLOADED, 1, 0);
        testArchive.setPlatform("platform");
        experiment.getArchives().clear();
        experiment.getPreviousArchives().clear();
        experiment.addArchive(testArchive);
        setupSdrf("different_protocols.sdrf.txt");
        context.checking(new Expectations() {{
            allowing(mockExperimentQueries).getExperimentDataFiles(experiment.getName());
            will(returnValue(new HashMap<Archive, List<FileInfo>>()));

        }});
        boolean isValid = validator.execute(experiment, qcContext);
        assertTrue(qcContext.getErrors().toString(), isValid);
    }

    @Test
    public void testRevisedArchiveMissingFiles()
            throws Processor.ProcessorException, IOException, ParseException {
        /*
           * Test for the scenario: SDRF refers to file_a in archive 1.0. Archive
           * 1.1 is part of experiment, replacing archive 1.0, but file_a is
           * unchanged in the revised archive so is not part of the actual 1.1
           * archive as submitted to the system. Also, file_a is not listed in
           * archive 1.1's manifest. This should fail validation.
           */
        Archive archive10 = createArchive(
                "center.org_TUM.platform.Level_2.1.0.0", Archive.TYPE_LEVEL_2,
                Archive.STATUS_AVAILABLE, 1, 0);
        Archive archive11 = createArchive(
                "center.org_TUM.platform.Level_2.1.1.0", Archive.TYPE_LEVEL_2,
                Archive.STATUS_UPLOADED, 1, 1);
        experiment.getArchives().clear();
        experiment.getPreviousArchives().clear();
        experiment.addArchive(archive11);
        experiment.addPreviousArchive(archive10);
        setupSdrf("revisedArchiveMissingFiles.sdrf.txt");

        assertFalse(validator.execute(experiment, qcContext));
        assertTrue(qcContext.getErrorCount() > 0);
    }

    @Test
    public void testValidateSDRFAgainstLatestArchivesOkay() {

        final TabDelimitedContent sdrf = new TabDelimitedContentImpl();
        final Map<Integer, String[]> sdrfContents = new HashMap<Integer, String[]>();
        // SDRF header has 3 file columns with just Archive Name comment columns, since that's all that is needed for this test
        sdrf.setTabDelimitedHeader(new String[]{"Data File", "Comment [TCGA Archive Name]",
                "Array Data File", "Comment [TCGA Archive Name]",
                "Derived Array Data File", "Comment [TCGA Archive Name]"});

        sdrfContents.put(0, new String[]{"Data File", "Comment [TCGA Archive Name]",
                "Array Data File", "Comment [TCGA Archive Name]",
                "Derived Array Data File", "Comment [TCGA Archive Name]"});
        sdrfContents.put(1, new String[]{"archive1_file1.txt", "archive1", "archive2_file1.txt", "archive2", "archive3_file1.txt", "archive3"});
        sdrfContents.put(2, new String[]{"archive1_file2.txt", "archive1", "archive2_file2.txt", "archive2", "archive3_file2.txt", "archive3"});
        sdrfContents.put(3, new String[]{"archive1_file3.txt", "archive1", "archive2_file3.txt", "archive2", "archive3_file3.txt", "archive3"});

        sdrf.setTabDelimitedContents(sdrfContents);

        final TabDelimitedContentNavigator sdrfNavigator = new TabDelimitedContentNavigator();
        sdrfNavigator.setTabDelimitedContent(sdrf);

        experiment.setName("test.org_DIS.testing");
        setGetExperimentDataFilesExpectations();

        validator.warnForDroppedArchives(experiment, sdrfNavigator, qcContext);
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(qcContext.getWarnings().toString(), 0, qcContext.getWarningCount());
    }

    @Test
    public void testValidateSDRFAgainstLatestArchivesMissingArchive() {
        final TabDelimitedContent sdrf = new TabDelimitedContentImpl();
        final Map<Integer, String[]> sdrfContents = new HashMap<Integer, String[]>();
        // SDRF header has 3 file columns with just Archive Name comment columns, since that's all that is needed for this test
        sdrf.setTabDelimitedHeader(new String[]{"Data File", "Comment [TCGA Archive Name]",
                "Array Data File", "Comment [TCGA Archive Name]"});

        sdrfContents.put(0, new String[]{"Data File", "Comment [TCGA Archive Name]",
                "Array Data File", "Comment [TCGA Archive Name]"});
        sdrfContents.put(1, new String[]{"archive1_file1.txt", "archive1", "archive2_file1.txt", "archive2"});
        sdrfContents.put(2, new String[]{"archive1_file2.txt", "archive1", "archive2_file2.txt", "archive2"});
        sdrfContents.put(3, new String[]{"archive1_file3.txt", "archive1", "archive2_file3.txt", "archive2"});

        sdrf.setTabDelimitedContents(sdrfContents);

        final TabDelimitedContentNavigator sdrfNavigator = new TabDelimitedContentNavigator();
        sdrfNavigator.setTabDelimitedContent(sdrf);

        experiment.setName("test.org_DIS.testing");
        setGetExperimentDataFilesExpectations();

        validator.warnForDroppedArchives(experiment, sdrfNavigator, qcContext);
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(1, qcContext.getWarningCount());
        assertEquals("Currently available archive archive3 is not referenced in the SDRF. If this archive should no longer be available, please contact the DCC.", qcContext.getWarnings().get(0));
    }

    @Test
    public void testValidateSDRFAgainstLatestArchivesMissingFiles() {
        final TabDelimitedContent sdrf = new TabDelimitedContentImpl();
        final Map<Integer, String[]> sdrfContents = new HashMap<Integer, String[]>();
        // SDRF header has 3 file columns with just Archive Name comment columns, since that's all that is needed for this test
        sdrf.setTabDelimitedHeader(new String[]{"Data File", "Comment [TCGA Archive Name]",
                "Array Data File", "Comment [TCGA Archive Name]",
                "Derived Array Data File", "Comment [TCGA Archive Name]"});

        sdrfContents.put(0, new String[]{"Data File", "Comment [TCGA Archive Name]",
                "Array Data File", "Comment [TCGA Archive Name]",
                "Derived Array Data File", "Comment [TCGA Archive Name]"});
        sdrfContents.put(1, new String[]{"archive1_file1.txt", "archive1", "archive2_file16.txt", "archive2", "archive3_file1.txt", "archive3"});
        sdrfContents.put(2, new String[]{"archive1_file42.txt", "archive1", "archive2_file2.txt", "archive2", "archive3_file2.txt", "archive3"});
        sdrfContents.put(3, new String[]{"archive1_file3.txt", "archive1", "archive2_file3.txt", "archive2", "archive3_file78.txt", "archive3"});

        sdrf.setTabDelimitedContents(sdrfContents);

        final TabDelimitedContentNavigator sdrfNavigator = new TabDelimitedContentNavigator();
        sdrfNavigator.setTabDelimitedContent(sdrf);

        experiment.setName("test.org_DIS.testing");
        setGetExperimentDataFilesExpectations();

        validator.warnForDroppedArchives(experiment, sdrfNavigator, qcContext);
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(0, qcContext.getWarningCount());
    }

    @Test
    public void testValidateSDRFAgainstLatestArchivesMissingFilesWhenUsingSoundcheckWithNoremote() {

        validator.setExperimentQueries(null);// Using Soundcheck with -noremote does not set experiment queries

        final TabDelimitedContent sdrf = new TabDelimitedContentImpl();
        final Map<Integer, String[]> sdrfContents = new HashMap<Integer, String[]>();
        // SDRF header has 3 file columns with just Archive Name comment columns, since that's all that is needed for this test
        sdrf.setTabDelimitedHeader(new String[]{"Data File", "Comment [TCGA Archive Name]",
                "Array Data File", "Comment [TCGA Archive Name]",
                "Derived Array Data File", "Comment [TCGA Archive Name]"});

        sdrfContents.put(0, new String[]{"Data File", "Comment [TCGA Archive Name]",
                "Array Data File", "Comment [TCGA Archive Name]",
                "Derived Array Data File", "Comment [TCGA Archive Name]"});
        sdrfContents.put(1, new String[]{"archive1_file1.txt", "archive1", "archive2_file16.txt", "archive2", "archive3_file1.txt", "archive3"});
        sdrfContents.put(2, new String[]{"archive1_file42.txt", "archive1", "archive2_file2.txt", "archive2", "archive3_file2.txt", "archive3"});
        sdrfContents.put(3, new String[]{"archive1_file3.txt", "archive1", "archive2_file3.txt", "archive2", "archive3_file78.txt", "archive3"});

        sdrf.setTabDelimitedContents(sdrfContents);

        final TabDelimitedContentNavigator sdrfNavigator = new TabDelimitedContentNavigator();
        sdrfNavigator.setTabDelimitedContent(sdrf);

        experiment.setName("test.org_DIS.testing");

        validator.warnForDroppedArchives(experiment, sdrfNavigator, qcContext);
        assertEquals(0, qcContext.getErrorCount());
        assertEquals(0, qcContext.getWarningCount());
    }

    private void setGetExperimentDataFilesExpectations() {
        experiment.getArchives().clear();
        final Map<Archive, List<FileInfo>> dataFiles = new HashMap<Archive, List<FileInfo>>();
        for (int archiveNum = 1; archiveNum<=3; archiveNum++) {
            final Archive archive = new Archive("archive" + archiveNum + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
            archive.setId((long) archiveNum);
            archive.setRealName("archive" + archiveNum);
            archive.setDeployStatus(Archive.STATUS_AVAILABLE);
            experiment.addArchive(archive);
            final List<FileInfo> archiveFiles = new ArrayList<FileInfo>();
            dataFiles.put(archive, archiveFiles);
            for (int fileNum = 1; fileNum<=3; fileNum++) {
                final FileInfo file = new FileInfo();
                file.setFileName(archive.getRealName() + "_file" + fileNum + ".txt");
                archiveFiles.add(file);
            }
        }
        context.checking(new Expectations() {{
            one(mockExperimentQueries).getExperimentDataFiles(experiment.getName());
            will(returnValue(dataFiles));
        }});
    }
}
