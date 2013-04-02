/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import gov.nih.nci.ncicb.tcga.dcc.common.util.TextFileComparer.TextComparisonEvent;
import gov.nih.nci.ncicb.tcga.dcc.common.util.TextFileComparer.TextComparisonResult;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Test class that tests the functionality of the {@link TextFileComparer}.
 *
 * @author Matt Nicholls
 *         Last updated by: nichollsmc
 */
public class TextFileComparerFastTest {

    private static final String TEST_FILE_DIR =
            Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator +
                    "util" + File.separator +
                    "compare" + File.separator;

    private static final String SOURCE_FILE_DIR = TEST_FILE_DIR + "sourceFiles" + File.separator;
    private static final String TARGET_FILE_DIR = TEST_FILE_DIR + "targetFiles" + File.separator;

    @Test
    public void compareFilesWithNullPaths() {
        TextComparisonResult textComparisonResult = TextFileComparer.compareFiles(null, TARGET_FILE_DIR + "target-01.txt");
        assertFalse(textComparisonResult.filesMatch());
        assertEquals("Cannot perform line comparison because one of the file paths is null", textComparisonResult.getMessage());
        assertNull(textComparisonResult.getTextComparisonEvents());
    }

    @Test
    public void compareFilesWithInvalidPath() {
        TextComparisonResult textComparisonResult = TextFileComparer.compareFiles("source-01.txt", TARGET_FILE_DIR + "target-01.txt");
        assertFalse(textComparisonResult.filesMatch());
        assertNull(textComparisonResult.getTextComparisonEvents());
        assertEquals("Cannot perform line comparison because one of the files does not exist", textComparisonResult.getMessage());
    }

    @Test
    public void compareFilesThatMatch() {
        TextComparisonResult textComparisonResult = TextFileComparer.compareFiles(SOURCE_FILE_DIR + "source-01.txt", TARGET_FILE_DIR + "target-01.txt");
        assertTrue(textComparisonResult.filesMatch());
        assertEquals("", textComparisonResult.getMessage());
        assertNull(textComparisonResult.getTextComparisonEvents());
    }

    @Test
    public void compareFilesWithDifferentLineCount() {
        TextComparisonResult textComparisonResult = TextFileComparer.compareFiles(SOURCE_FILE_DIR + "source-01.txt", TARGET_FILE_DIR + "target-03.txt");
        assertFalse(textComparisonResult.filesMatch());
        assertEquals("Files do not contain the same number of lines (source file contains 1 line(s), target file contains 2 line(s))",
                textComparisonResult.getMessage());
        assertNull(textComparisonResult.getTextComparisonEvents());
    }

    @Test
    public void compareFilesWithVariableLineLengths() {
        TextComparisonResult textComparisonResult = TextFileComparer.compareFiles(SOURCE_FILE_DIR + "source-02.txt", TARGET_FILE_DIR + "target-04.txt");
        assertFalse(textComparisonResult.filesMatch());
        assertEquals("Found [2] non-matching lines", textComparisonResult.getMessage());
        assertEquals(2, textComparisonResult.getTextComparisonEvents().size());
        assertEquals(0, textComparisonResult.getTextComparisonEvents().get(0).getColumn());
        assertEquals(0, textComparisonResult.getTextComparisonEvents().get(1).getColumn());
    }

    @Test
    public void compareFilesThatDoNotMatch() {
        TextComparisonResult textComparisonResult = TextFileComparer.compareFiles(SOURCE_FILE_DIR + "source-01.txt", TARGET_FILE_DIR + "target-02.txt");
        assertFalse(textComparisonResult.filesMatch());
        assertEquals("Found [1] non-matching line", textComparisonResult.getMessage());
        assertNotNull(textComparisonResult.getTextComparisonEvents());

        // Assert location of non-matching text
        TextComparisonEvent textComparisonEvent = textComparisonResult.getTextComparisonEvents().get(0);
        assertEquals(1, textComparisonEvent.getLine());
        assertEquals(42, textComparisonEvent.getColumn());

        StringBuilder expectedString = new StringBuilder();
        expectedString.append("\nSource line: The quick brown fox jumped over the lazy dog\n");
        expectedString.append("Target line: The quick brown fox jumped over the lazy cat\n");
        expectedString.append("Non-matching location: line 1, column 42");
        expectedString.append("\n\n");
        assertEquals(expectedString.toString(), textComparisonEvent.toString());
    }
}
