/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.util;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcliveCloseableUtil.close;

/**
 * Class file which reads the manifest file in a TCGA archive and puts the list of files listed in its contents
 * in a List object, backed by an ArrayList, and returns the List for further processing by other parts of the application.
 *
 * @author Robert S. Sfeir
 */
public class FileListReader {

    private final List<String> fileToReadList = new ArrayList<String>();
    private static final String SEPARATOR = File.separator;

    public List<String> readManifest(final File theFile) throws IOException {
        final String theCanPath = theFile.getCanonicalPath();
        final String fullPath = theCanPath.substring(0, theCanPath.lastIndexOf(SEPARATOR));
        final String name = theCanPath.substring(theCanPath.lastIndexOf(SEPARATOR) + 1, theCanPath.length());
        return readFileWithList(fullPath, name);
    }

    public List<String> readFileWithList(final String fileLocation, final String fileName) throws IOException {

        List<String> result = null;
        InputStream fis = null;

        try {
            final File theFileWithList = new File(fileLocation, fileName);
            //noinspection IOResourceOpenedButNotSafelyClosed
            fis = new FileInputStream(theFileWithList);
            result = readFileWithList(fis);
        } finally {
            IOUtils.closeQuietly(fis);
        }

        return result;
    }

    public List<String> readFileWithList(InputStream is) throws IOException {
        Reader isr = new InputStreamReader(is);
        BufferedReader in = new BufferedReader(isr);
        try {
            while (in.ready()) {
                try {
                    final String text = in.readLine().trim();
                    if (text.trim().length() > 0 && !text.trim().equals("->")) {
                        fileToReadList.add(text.trim());
                    }
                }
                catch (IOException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
        finally {
            try {
                in.close();
                isr.close();
                is.close();
                in = null;
                isr = null;
                is = null;
            }
            catch (IOException ignored) {
                //Do nothing here just keep going
            }
        }
        return fileToReadList;
    }
}
