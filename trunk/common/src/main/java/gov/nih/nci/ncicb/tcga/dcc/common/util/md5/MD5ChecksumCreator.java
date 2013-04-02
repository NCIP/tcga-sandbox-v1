/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util.md5;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class to create a checksum out of a submitted file.
 */
public class MD5ChecksumCreator {

    private static final int BUF_SIZE = 8192;

    private int bufferSize;

    public MD5ChecksumCreator() {
        this(BUF_SIZE);

    }

    public MD5ChecksumCreator(final int bufSize) {
        bufferSize = bufSize;

    }

    public byte[] generate(final File fileName) throws NoSuchAlgorithmException, IOException {

        InputStream theFile = null;
        byte[] result = null;

        try {
            final MessageDigest complete;
            //noinspection IOResourceOpenedButNotSafelyClosed
            theFile = new FileInputStream(fileName);
            final byte[] byteArrayBuffer = new byte[bufferSize];
            complete = MessageDigest.getInstance("MD5");
            int numRead;
            do {
                numRead = theFile.read(byteArrayBuffer);
                if (numRead > 0) {
                    complete.update(byteArrayBuffer, 0, numRead);
                }
            } while (numRead != -1);
            assert complete != null;
            result = complete.digest();

        } finally {
            IOUtils.closeQuietly(theFile);
            theFile = null;
        }

        return result;
    }

    public static String convertStringToHex(final byte[] theArray) {
        final StringBuilder result = new StringBuilder();
        for (final byte aB : theArray) {
            result.append(Integer.toString((aB & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }
}