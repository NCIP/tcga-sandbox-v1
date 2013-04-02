/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.common.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for ManifestParserImpl
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ManifestParserImplFastTest {
    private ManifestParser parser;
    private static final String SAMPLE_DIR = 
		Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

    @Before
    public void setup() {
        parser = new ManifestParserImpl();
    }

    @Test
    public void testParseManifest() throws IOException, ParseException {

        final File manifest = new File(SAMPLE_DIR + "qclive/manifestParser/center_disease.platform.Level_1.1.0.0/MANIFEST.txt");
        final Map<String, String> manifestEntries = parser.parseManifest(manifest);
        assertNotNull(manifestEntries);
        assertEquals("12345", manifestEntries.get("file1.txt"));
        assertEquals("56789", manifestEntries.get("file2.txt"));

    }

    @Test
    public void testNoManifest() throws IOException, ParseException {
        // now test the one without a manifest
        final File manifest = new File(SAMPLE_DIR + "qclive/manifestParser/archive_without.manifest.Level_1.1.0.0/MANIFEST.txt");
        boolean exceptionThrown = false;
        try {
            parser.parseManifest(manifest);
        } catch (FileNotFoundException e) {
            exceptionThrown = true;
        }
        assertTrue("an io exception should have been thrown for the nonexistent manifest file", exceptionThrown);
    }

    @Test
    public void testBadManifest() throws IOException {
        // now test one with a badly-formatted manifest
        final File manifest = new File(SAMPLE_DIR + "qclive/manifestParser/center_disease.platform.Level_1.1.0.0/BAD_MANIFEST.txt");
        boolean exceptionThrown = false;
        try {
            parser.parseManifest(manifest);
        } catch (ParseException e) {
            exceptionThrown = true;
        }
        assertTrue("A parse exception should have been thrown for the badly-formatted manifest", exceptionThrown);
    }

    @Test
    public void testAddFileToManifest() throws IOException, NoSuchAlgorithmException, ParseException {
        // write a new manifest file (to make sure we know what is in it)
        final File manifest = new File(SAMPLE_DIR + "qclive/manifestParser/center_disease.platform.Level_1.1.0.0/TEMP_MANIFEST.txt");
        FileWriter writer = null;

        try {
            //noinspection IOResourceOpenedButNotSafelyClosed
            writer = new FileWriter(manifest);
            writer.write("12345  hello\n");
        } finally {
            IOUtils.closeQuietly(writer);
        }

        // this file should be added to the manifest
        final File toAdd = new File(SAMPLE_DIR + "qclive/manifestParser/center_disease.platform.Level_1.1.0.0/README.txt");
        try {
            parser.addFileToManifest(toAdd, manifest);
            final Map<String, String> manifestContent = parser.parseManifest(manifest);
            // only care that file has been added, not what the MD5 is
            assertTrue(manifestContent.containsKey("README.txt"));
        } finally {
            manifest.deleteOnExit();
        }
    }

    @Test
    public void testUpdateFileToManifest() throws IOException, NoSuchAlgorithmException, ParseException {
        // write a new manifest file (to make sure we know what is in it)
        final File manifest = new File(SAMPLE_DIR + "qclive/manifestParser/center_disease.platform.Level_1.1.0.0/TEMP_MANIFEST.txt");
        FileWriter writer = null;
        try {
            //noinspection IOResourceOpenedButNotSafelyClosed
            writer = new FileWriter(manifest);
            writer.write("12345  file1\n");
        } finally {
            IOUtils.closeQuietly(writer);
        }

        List<File> filesToBeAdded = new ArrayList<File>();
        // this file should be added to the manifest
        File toAdd = createNewFile("file2");
        filesToBeAdded.add(toAdd);
        toAdd = createNewFile("file3");
        filesToBeAdded.add(toAdd);

        List<File> filesToBeRemoved = Arrays.asList(new File[]{new File("file1")});

        try {
            parser.updateManifest(filesToBeAdded, filesToBeRemoved, manifest);
            final Map<String, String> manifestContent = parser.parseManifest(manifest);
            // only care that file has been added, not what the MD5 is
            assertTrue("Error updating manifest file", manifestContent.containsKey("file2"));
            assertTrue("Error updating manifest file", manifestContent.containsKey("file3"));
            assertFalse("Error updating manifest file", manifestContent.containsKey("file1"));
        } finally {
            manifest.deleteOnExit();
            for(File file : filesToBeAdded) {
            	if(file.exists()) {
            		file.delete();
            	}
            }
        }
    }

    private File createNewFile(final String fileName) throws IOException {
        final File file = new File(SAMPLE_DIR + fileName);
        final String data = "new file";
        FileUtil.writeContentToFile(data, file);
        return file;
    }
}
