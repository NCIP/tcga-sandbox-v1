/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;

/**
 * Created by IntelliJ IDEA.
 * User: nassaud
 * Date: Oct 6, 2008
 * Time: 11:38:57 AM
 * To change this template use File | Settings | File Templates.
 * 
 * @deprecated  Effective 10/10/2011, use {@link TextFileComparer} to compare text files instead.
 */
@Deprecated
public class FileComparer extends TestCase {

    public static void compareFiles( String fname1, String fname2 ) throws IOException {

        Reader r1 = null;
        Reader r2 = null;

        try {
            File f1 = new File( fname1 );
            File f2 = new File( fname2 );
            //noinspection IOResourceOpenedButNotSafelyClosed
            r1 = new BufferedReader( new FileReader( f1 ) );
            //noinspection IOResourceOpenedButNotSafelyClosed
            r2 = new BufferedReader( new FileReader( f2 ) );
            int c1, c2;
            c1 = r1.read();
            c2 = r2.read();
            assertFalse( "file1 has no content", c1 < 0 );
            int i = 0;
            while(c1 != -1) {
                assertEquals( "files are not equal at index "+i, c1, c2 );
                c1 = r1.read();
                c2 = r2.read();
                ++i;
            }
        } finally {
            IOUtils.closeQuietly(r1);
            IOUtils.closeQuietly(r2);
        }
    }
}
