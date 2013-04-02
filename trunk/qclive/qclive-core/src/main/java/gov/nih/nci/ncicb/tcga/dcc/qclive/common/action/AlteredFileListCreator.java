/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.action;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParser;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.ManifestParserImpl;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ManifestValidator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * Creates a file in the archive that lists the files that were changed by the DCC, according to information recorded
 * in the QcContext.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class AlteredFileListCreator extends AbstractProcessor<Archive, Archive> {
    public static final String DCC_ALTERED_FILES_NAME = "DCC_ALTERED_FILES.txt";
    private ManifestParser manifestParser;

    @Override
    protected Archive doWork(final Archive archive, final QcContext context) throws ProcessorException {
        if (context.getAlteredFiles() != null && context.getAlteredFiles().size() > 0) {
            try {
                writeAlteredFilesList(archive, context.getAlteredFiles());
            } catch (IOException e) {
                throw new ProcessorException("Failed to write altered file list: " + e.getMessage());
            } catch (NoSuchAlgorithmException e) {
                throw new ProcessorException("Failed to write altered file list: " + e.getMessage());
            } catch (ParseException e) {
                throw new ProcessorException("Failed to write altered file list: " + e.getMessage());
            }
        }
        return archive;
    }

    private void writeAlteredFilesList(final Archive archive, final Map<String, String[]> changedFiles)
            throws IOException, NoSuchAlgorithmException, ParseException {
        final File alteredFileList = new File(archive.getDeployDirectory(), DCC_ALTERED_FILES_NAME);
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(alteredFileList));
            writer.write("#This file lists the files in this archive that were altered by the DCC after receipt from the submitting center");
            writer.newLine();
            writer.write("#Original MD5 Checksum\tFilename\tReason for change");
            writer.newLine();

            // sort file names alphabetically
            TreeSet<String> changedFileNames = new TreeSet<String>();
            changedFileNames.addAll(changedFiles.keySet());
            for (final String changedFile : changedFileNames) {
                String[] md5AndReason = changedFiles.get(changedFile);
                writer.write(md5AndReason[0] + "\t" + changedFile + "\t" + md5AndReason[1]);
                writer.newLine();
            }
            manifestParser.addFileToManifest(alteredFileList, new File(archive.getDeployDirectory(), ManifestValidator.MANIFEST_FILE));
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }

    public String getName() {
        return "changed file list creation";
    }

    public void setManifestParser(final ManifestParserImpl manifestParser) {
        this.manifestParser = manifestParser;
    }

    /**
     * Parses the altered file list file in the archive, if any, and gets out the filenames and original MD5 hashes.
     * The returned map will be empty if there is no altered file list.
     *
     * @param archive the archive to look in
     * @return a map, with filenames as keys and MD5 hashes as values
     * @throws IOException if there is an error reading the file
     */
    public static Map<String, String> getAlteredFileListMap(final Archive archive) throws IOException {
        Map<String, String> alteredFiles = new HashMap<String, String>();
        if (archive != null) {
            File alteredFileList = new File(archive.getDeployDirectory() + File.separator + AlteredFileListCreator.DCC_ALTERED_FILES_NAME);
            if (alteredFileList.exists()) {
                FileReader fReader = new FileReader(alteredFileList);
                BufferedReader bufferedReader = new BufferedReader(fReader);
                try {
                    String line = bufferedReader.readLine();
                    while (line != null) {

                        if (!line.startsWith("#") && !line.trim().isEmpty()) {
                            String[] lineParts = line.split("\t");
                            if(lineParts.length >= 2){
                                alteredFiles.put(lineParts[1], lineParts[0]);
                            }
                        }
                        line = bufferedReader.readLine();
                    }
                } finally {
                    bufferedReader.close();
                    bufferedReader = null;
                    fReader.close();
                    fReader = null;
                }
            }
        }
        return alteredFiles;
    }
}
