/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * This class is a test to identify which version of Java a class has been compiled into.
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */


public class ClassVersionChecker {

    private final Logger LOGGER = Logger.getLogger( "TCGALogger" );

    public static void main(String[] args) throws IOException {
        for (int i = 0; i < args.length; i++) {
            new ClassVersionChecker().checkClassVersion(args[i]);
        }
    }

    private void checkClassVersion(String filename)
            throws IOException {

        DataInputStream in = null;

        try {
            //noinspection IOResourceOpenedButNotSafelyClosed
            in = new DataInputStream
                    (new FileInputStream(filename));

            int magic = in.readInt();

//        The first 4 bytes are a magic number, 0xCAFEBABe, to identify a valid class file
//        then the next 2 bytes identify the class format version (major and minor).
//        Possible major/minor value :
//
//        major  minor Java platform version
//        45       3           1.0
//        45       3           1.1
//        46       0           1.2
//        47       0           1.3
//        48       0           1.4
//        49       0           1.5
//        50       0           1.6

            if (magic != 0xcafebabe) LOGGER.info(filename + " is not a valid class!");
            int minor = in.readUnsignedShort();
            int major = in.readUnsignedShort();
            LOGGER.info(filename + ": " + major + " . " + minor);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

} //End of Class
