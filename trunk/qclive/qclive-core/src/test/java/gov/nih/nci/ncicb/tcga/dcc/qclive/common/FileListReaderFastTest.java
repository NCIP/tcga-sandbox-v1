/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common;

import gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.FileListReader;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Robert S. Sfeir
 */
public class FileListReaderFastTest extends TestCase {

    final String location = Thread.currentThread().getContextClassLoader().getResource("gov/SDRFHeaders.txt").getPath();
    final String path = location.substring( 0, location.lastIndexOf( "/" ) );

    public void testReadFile() throws IOException {
        final File theFile = new File( path, "SDRFHeaders.txt" );
        assertTrue( theFile.exists() );
        FileListReader flr = new FileListReader();
        final List theList = flr.readManifest( theFile );
        assertTrue( theList.size() == 42 );
    }
}
