/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.bean.Experiment;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParserImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.FileCopier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Map;

import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.ChangeLogCreator.CHANGE_FILE_NAME;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Description : Class to test the Change log creator
 *
 * @author Namrata Rane Last updated by: $Author$
 * @version $Rev$
 */

public class ChangelogCreatorFastTest {

    private static final String MANIFEST_FILE_NAME = "MANIFEST.txt";
    private static final String ORIGINAL_MANIFEST_FILE_NAME = "MANIFEST.txt.orig";
    private static final String SAMPLES_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;
    private static final String SAMPLE_DIR_LOCATION = SAMPLES_DIR
            + "qclive/changelogCreator";

    private String archiveFileLocation;
    private String prevArchiveFileLocation;

    private final Archive archive = new Archive();
    private final Archive previousArchive = new Archive();

    private final Experiment experiment = new Experiment();
    private ManifestParser manifestParser = new ManifestParserImpl();

    private ChangeLogCreator changelogCreator;
    private QcContext qcContext;

    @Before
    public void setup() {
        archive.setArchiveFile(new File(
                "hms.harvard.edu_OV.HG-CGH-244A.aux.8.2.0"
                        + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION));
        archive.setArchiveType(Archive.TYPE_AUX);
        archive.setSerialIndex("8");
        archive.setRealName("hms.harvard.edu_OV.HG-CGH-244A.aux.8.2.0");

        previousArchive.setArchiveFile(new File(
                "hms.harvard.edu_OV.HG-CGH-244A.aux.8.1.0"
                        + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION));
        previousArchive.setArchiveType(Archive.TYPE_AUX);
        previousArchive.setSerialIndex("8");
        previousArchive.setRealName("hms.harvard.edu_OV.HG-CGH-244A.aux.8.1.0");

        changelogCreator = new ChangeLogCreator();
        changelogCreator.setManifestParser(manifestParser);

        qcContext = new QcContext();
        qcContext.setArchive(archive);

        experiment.setArchives(Arrays.asList(archive));
        experiment.setPreviousArchives(Arrays.asList(previousArchive));
        qcContext.setExperiment(experiment);

    }

    @Test
    public void testChangedFiles() throws Processor.ProcessorException,
            IOException, ParseException {

        archiveFileLocation = "/changed/hms.harvard.edu_OV.HG-CGH-244A.aux.8.2.0";
        prevArchiveFileLocation = "/changed/hms.harvard.edu_OV.HG-CGH-244A.aux.8.1.0";

        archive.setDeployLocation(SAMPLE_DIR_LOCATION + archiveFileLocation
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        previousArchive.setDeployLocation(SAMPLE_DIR_LOCATION
                + prevArchiveFileLocation + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);

        changelogCreator.execute(archive, qcContext);

        // make sure the change log file is created
        File sampleDir = new File(SAMPLE_DIR_LOCATION + archiveFileLocation);
        File changeLogFile = new File(sampleDir, CHANGE_FILE_NAME);
        checkLogFileExistence(sampleDir, changeLogFile);

        // check the log file contents
        assertTrue(checkFileContents(changeLogFile,
                "TCGA-01-0628-11A-01D-0360-02_BioSizing.tsv\tR"));

    }

    @Test
    public void testDeletedFiles() throws Processor.ProcessorException,
            IOException, ParseException {

        archiveFileLocation = "/deleted/hms.harvard.edu_OV.HG-CGH-244A.aux.8.2.0";
        prevArchiveFileLocation = "/deleted/hms.harvard.edu_OV.HG-CGH-244A.aux.8.1.0";

        archive.setDeployLocation(SAMPLE_DIR_LOCATION + archiveFileLocation
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        previousArchive.setDeployLocation(SAMPLE_DIR_LOCATION
                + prevArchiveFileLocation + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);

        changelogCreator.execute(archive, qcContext);

        // make sure the change log file is created
        File sampleDir = new File(SAMPLE_DIR_LOCATION + archiveFileLocation);
        File changeLogFile = new File(sampleDir, CHANGE_FILE_NAME);
        checkLogFileExistence(sampleDir, changeLogFile);

        // check the log file contents
        assertTrue(checkFileContents(changeLogFile,
                "TCGA-01-0628-11A-01D-0360-02_QA.tsv\t-"));

    }

    @Test
    public void testAddedFiles() throws Processor.ProcessorException,
            IOException, ParseException {

        archiveFileLocation = "/added/hms.harvard.edu_OV.HG-CGH-244A.aux.8.2.0";
        prevArchiveFileLocation = "/added/hms.harvard.edu_OV.HG-CGH-244A.aux.8.1.0";

        archive.setDeployLocation(SAMPLE_DIR_LOCATION + archiveFileLocation
                + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
        previousArchive.setDeployLocation(SAMPLE_DIR_LOCATION
                + prevArchiveFileLocation + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);

        changelogCreator.execute(archive, qcContext);

        // make sure the change log file is created
        File sampleDir = new File(SAMPLE_DIR_LOCATION + archiveFileLocation);
        File changeLogFile = new File(sampleDir, CHANGE_FILE_NAME);
        checkLogFileExistence(sampleDir, changeLogFile);

        // check the log file contents
        assertTrue(checkFileContents(changeLogFile,
                "TCGA-01-0628-11A-01D-0360-02_QA.tsv\t+"));
    }

    private boolean checkFileContents(final File changeLogFile,
                                      final String checkForEntry) throws IOException {

        boolean foundText = false;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(changeLogFile));
            String record = reader.readLine();
            while ((record != null) && (!foundText)) {
                if (record.equals(checkForEntry)) {
                    foundText = true;
                }
                record = reader.readLine();
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return foundText;
    }

    private void checkLogFileExistence(final File sampleDir,
                                       final File changeLogFile) throws IOException, ParseException {

        assertTrue("Change log file was not created", changeLogFile.exists());
        File manifest = new File(sampleDir, MANIFEST_FILE_NAME);
        ManifestParser realManifestParser = new ManifestParserImpl();
        Map<String, String> manifestEntries = realManifestParser
                .parseManifest(manifest);
        assertNotNull("Change log file entry not found in manifest.",
                manifestEntries.get(CHANGE_FILE_NAME));
    }

    @After
    public void cleanup() throws IOException {

        File sampleDir = new File(SAMPLE_DIR_LOCATION + archiveFileLocation);
        File changeFile = new File(sampleDir, CHANGE_FILE_NAME);
        File manifest = new File(sampleDir, MANIFEST_FILE_NAME);
        File manifestOriginal = new File(sampleDir, ORIGINAL_MANIFEST_FILE_NAME);
        changeFile.delete();
        FileCopier.copy(manifestOriginal, manifest);
    }

}
