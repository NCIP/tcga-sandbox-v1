/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common;

import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.MD5Validator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Implementation of ManifestParser.  Expects manifest format to be one entry per line, with md5 and filename in that
 * order separated by whitespace.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class ManifestParserImpl implements ManifestParser {

    /**
     * Parses the given file.  It expects each line to have an MD5 sum, whitespace, and then a filename.
     *
     * @param manifest the file to parse
     * @return a map, where keys are filenames and values are MD5 hashes
     * @throws java.io.IOException      if the file is not found or cannot be read
     * @throws java.text.ParseException if the file isn't formatted correctly
     */
    public Map<String, String> parseManifest(final File manifest) throws IOException, ParseException {
        final Map<String, String> manifestEntries = new TreeMap<String, String>();
        // 1. open file
        FileReader fReader = new FileReader(manifest);
        BufferedReader in = null;
        try {
            in = new BufferedReader(fReader);
            // 2. read each line
            String lineStr;
            int lineNum = 1;
            while ((lineStr = in.readLine()) != null) {
                // ignore blank lines
                if (lineStr.trim().length() > 0) {
                    // 3. split line on whitespace
                    final String[] line = lineStr.split("\\s+");
                    // 4. if more than (or fewer than) 2 items, format error
                    if (line.length != 2) {
                        throw new ParseException(new StringBuilder().append("Manifest line ").append(lineNum).append(" did not have expected format ([MD5_hash] [filename]): ").append(lineStr).toString(),
                                lineNum);
                    }
                    // 5. add to map: second value (filename) is key, and MD5 is value
                    manifestEntries.put(line[1], line[0]);
                }
                lineNum++;
            }
        } finally {
            if (in != null) {
                in.close();
                in = null;
            }
            fReader.close();
            fReader = null;
        }

        return manifestEntries;
    }

    /**
     * Adds the given file to the manifest.
     *
     * @param fileToAdd file to add to the manifest
     * @param manifest  the manifest to add to
     * @throws IOException              of there is an error reading or writing the manifest
     * @throws NoSuchAlgorithmException if there is an error getting the MD5 checksum of the added file
     * @throws ParseException           if the given manifest isn't formatted correctly
     */
    public void addFileToManifest(final File fileToAdd, final File manifest)
            throws IOException, NoSuchAlgorithmException, ParseException {
        addFilesToManifest(Arrays.asList(new File[]{fileToAdd}), manifest);
    }

    public void addFilesToManifest(final List<File> filesToAdd,
                                   final File manifest) throws IOException, NoSuchAlgorithmException, ParseException {

        updateManifest(filesToAdd, null, manifest);
    }

    public void updateManifest(final List<File> filesToAdd,
                               final List<File> filesToRemove,
                               final File manifest) throws IOException, NoSuchAlgorithmException, ParseException {
        final Map<String, String> manifestEntries = parseManifest(manifest);

        if (filesToRemove != null) {
            for (final File fileToRemove : filesToRemove) {
                manifestEntries.remove(fileToRemove.getName());
            }
        }

        if (filesToAdd != null) {
            for (final File fileToAdd : filesToAdd) {
                final String md5 = MD5Validator.getFileMD5(fileToAdd);
                manifestEntries.put(fileToAdd.getName(), md5);
            }
        }

        FileWriter fWriter = new FileWriter(manifest);
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(fWriter);
            for (final String filename : manifestEntries.keySet()) {
                bufferedWriter.write(manifestEntries.get(filename));
                bufferedWriter.write("  ");
                bufferedWriter.write(filename);
                bufferedWriter.newLine();
            }
        } finally {
            if (bufferedWriter != null) {
                bufferedWriter.flush();
                bufferedWriter.close();
                bufferedWriter = null;
            }
            fWriter.close();
            fWriter = null;
        }
    }
}
