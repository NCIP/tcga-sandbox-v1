/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.loader.levelthree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nih.nci.ncicb.tcga.dcc.qclive.loader.LoaderException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import sun.security.acl.WorldGroupImpl;

/**
 * Class used to test LThreeLoaderCaller
 *
 * @author Stanley Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class LevelThreeLoaderCallerFastTest {

    private static final String SAMPLE_DIR =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;

    private static final String GOOD_APPLICATION_CONTEXT = "application-context-level-three-test.xml";
    private static final String BAD_APPLICATION_CONTEXT = "application-context-level-three-bad.xml";
    private static final String PATTERN_FILE_PATH = "levelThreePatterns.txt";
    private static final String GOOD_EXCLUSION_FILE = SAMPLE_DIR + "GoodExclusionFile";

    @Test
    public void testInit() throws LoaderException {
        LevelThreeLoaderCaller caller = new LevelThreeLoaderCaller();
        caller.init(GOOD_APPLICATION_CONTEXT);
    }

    @Test(expected = org.springframework.beans.factory.BeanDefinitionStoreException.class)
    public void testInitBadFile() throws LoaderException {
        LevelThreeLoaderCaller caller = new LevelThreeLoaderCaller();
        caller.init(GOOD_APPLICATION_CONTEXT + "BadFile");
    }

    @Test(expected = LoaderException.class)
    public void testInitBadContext() throws LoaderException {
        LevelThreeLoaderCaller caller = new LevelThreeLoaderCaller();
        // should not find level three and throw an exception
        caller.init(BAD_APPLICATION_CONTEXT);
    }

    @Test
    public void testLoadPatterns() throws LoaderException {
        LevelThreeLoaderCaller caller = new LevelThreeLoaderCaller();
        List<CenterPlatformPattern> patternList = caller.loadPatterns(PATTERN_FILE_PATH);
        assertTrue(patternList.size() > 0);

        for (final CenterPlatformPattern centerPlatformPattern : patternList) {
            if (centerPlatformPattern.getCenter().equals("broad.mit.edu") && centerPlatformPattern.getPlatform().equals("Genome_Wide_SNP_6")) {
                assertEquals(4, centerPlatformPattern.getPattern().size());
                assertTrue(centerPlatformPattern.getPattern().contains("*.hg18.seg.txt"));
                assertTrue(centerPlatformPattern.getPattern().contains("*.hg19.seg.txt"));
                assertTrue(centerPlatformPattern.getPattern().contains("*.nocnv_hg18.seg.txt"));
                assertTrue(centerPlatformPattern.getPattern().contains("*.nocnv_hg19.seg.txt"));
            }
        }
    }

    @Test
    public void testListExclusionFileNamesValid() {
        List<String> files = LevelThreeLoaderCaller.listExclusionFileNames(GOOD_EXCLUSION_FILE);
        assertEquals(1, files.size());
        assertEquals("Hello World", files.get(0));
    }

    @Test
    public void testListExclusionFileNamesNull() {
        List<String> files = LevelThreeLoaderCaller.listExclusionFileNames(null);
        assertEquals(0, files.size());
    }

    @Test(expected = RuntimeException.class)
    public void testListExclusionFileNamesInvalid() {
        List<String> files = LevelThreeLoaderCaller.listExclusionFileNames("foo");
    }

    @Test
    public void testProcessFileListExclusion() {
        String args[] = {"-Efoo", "hello", "world"};
        List<String> archivesToLoad = new ArrayList<String> ();
        String exclusionFile = LevelThreeLoaderCaller.processFileList(args, archivesToLoad);
        assertEquals("foo", exclusionFile);
        assertEquals(2, archivesToLoad.size());
        assertEquals("hello", archivesToLoad.get(0));
        assertEquals("world", archivesToLoad.get(1));
    }

    @Test
    public void testProcessFileListNoExclusion() {
        String args[] = {"hello", "world", "smithereens"};
        List<String> archivesToLoad = new ArrayList<String> ();
        String exclusionFile = LevelThreeLoaderCaller.processFileList(args, archivesToLoad);
        assertEquals(null, exclusionFile);
        assertEquals(3, archivesToLoad.size());
        assertEquals("hello", archivesToLoad.get(0));
        assertEquals("world", archivesToLoad.get(1));
        assertEquals("smithereens", archivesToLoad.get(2));
    }

    @Test
    public void testProcessFileListExclusionRandom() {
        String args[] = {"hello", "world", "-Efoo", "smithereens"};
        List<String> archivesToLoad = new ArrayList<String> ();
        String exclusionFile = LevelThreeLoaderCaller.processFileList(args, archivesToLoad);
        assertEquals("foo", exclusionFile);
        assertEquals(3, archivesToLoad.size());
        assertEquals("hello", archivesToLoad.get(0));
        assertEquals("world", archivesToLoad.get(1));
        assertEquals("smithereens", archivesToLoad.get(2));
    }
}
