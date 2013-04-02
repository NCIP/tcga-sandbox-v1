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
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParserImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.assertTrue;

/**
 * Test class for AddFileToArchive
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveFileAdderFastTest {

    private static final String SAMPLES_DIR = Thread.currentThread()
            .getContextClassLoader().getResource("samples").getPath()
            + File.separator;

    @Test
    public void test() throws Processor.ProcessorException, IOException {

        BufferedReader reader = null;

        try {
            // run and verify that file has been copied and manifest includes new
            // file
            String manifest = SAMPLES_DIR + "qclive" + File.separator
                    + "addFileToArchive" + File.separator + "MANIFEST.txt";
            File manifestFile = new File(manifest);
            System.out.println("Manifest file: " + manifestFile);
            if (manifestFile.exists()) {
                // delete in case it is there so don't skew results
                // noinspection ResultOfMethodCallIgnored
                manifestFile.delete();
            }
            // noinspection ResultOfMethodCallIgnored
            manifestFile.createNewFile();
            String fileToAdd = SAMPLES_DIR + "qclive" + File.separator
                    + "dataMatrix" + File.separator + "Test.sdrf";
            ArchiveFileAdder fileAdder = new ArchiveFileAdder(Experiment.TYPE_GSC,
                    null, fileToAdd);
            // make an archive
            Archive gscArchive = new Archive();
            gscArchive.setArchiveType(Archive.TYPE_LEVEL_1);
            gscArchive.setExperimentType(Experiment.TYPE_GSC);
            gscArchive.setArchiveFile(new File(SAMPLES_DIR + "qclive"
                    + File.separator + "addFileToArchive"
                    + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION));
            gscArchive.setDeployLocation(SAMPLES_DIR + "qclive" + File.separator
                    + "addFileToArchive" + ConstantValues.COMPRESSED_ARCHIVE_EXTENSION);
            Archive cgccArchive = new Archive();
            cgccArchive.setArchiveType(Archive.TYPE_AUX);
            cgccArchive.setExperimentType(Experiment.TYPE_CGCC);
            fileAdder.setManifestParser(new ManifestParserImpl());
            QcContext context = new QcContext();
            fileAdder.execute(gscArchive, context);
            File createdFile = new File(SAMPLES_DIR + "qclive" + File.separator
                    + "addFileToArchive" + File.separator + "Test.sdrf");
            assertTrue(createdFile.exists());
            // verify the copied file is listed in the manifest
            //noinspection IOResourceOpenedButNotSafelyClosed
            reader = new BufferedReader(new FileReader(manifestFile));
            StringBuffer manifestText = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                manifestText.append(line);
            }
            assertTrue(context.getErrors().toString(), manifestText.toString()
                    .contains("Test.sdrf"));
            createdFile.deleteOnExit();
            manifestFile.deleteOnExit();
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }
}
