/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common;

import gov.nih.nci.ncicb.tcga.dcc.common.util.md5.MD5ChecksumCreator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;

/**
 * @author Robert S. Sfeir
 */
public class MD5ChecksumCreatorFastTest extends TestCase {

    private static final String TEST_CHECKSUM_TEXT = "For the Horde";
    private static final String EXPECTED_CONENTS_HASH = "e14426c47f9fb8b12d7701f870acd829";
    private static final String TEST_FILE_NAME = "md5ChecksumTestFile.txt";
    private File testFile;

    public void setUp() throws FileNotFoundException {

        PrintWriter writer = null;

        try {
            testFile = new File( TEST_FILE_NAME );
            //noinspection IOResourceOpenedButNotSafelyClosed
            writer = new PrintWriter( testFile );
            writer.print(TEST_CHECKSUM_TEXT);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    public void testChecksumContentsOnly() throws Exception {
        final MD5ChecksumCreator checksumCreator = new MD5ChecksumCreator();
        assertEquals( EXPECTED_CONENTS_HASH, MD5ChecksumCreator.convertStringToHex( checksumCreator.generate( testFile ) ) );
    }

    public void tearDown() {
        //noinspection ResultOfMethodCallIgnored
        testFile.delete();
    }
}
