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
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcContext;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.action.validation.ManifestValidator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description : Class used to create the change log file for each archive. Change log is summary of changes from the
 * previous version of that archive.
 *
 * @author Namrata Rane Last updated by: $Author$
 * @version $Rev$
 */
public class ChangeLogCreator extends AbstractProcessor<Archive, Archive> {

    private static final String ADDED_FILE_SYMBOL = "+";
    private static final String DELETED_FILE_SYMBOL = "-";
    private static final String CHANGED_FILE_SYMBOL = "R";
    private static final String LINE_DELIMITER = "\n";
    private static final String TAB_DELIMITER = "\t";
    public static final String CHANGE_FILE_NAME = "CHANGES_DCC.txt";

    private ManifestParser manifestParser;

    protected Archive doWork(Archive archive, final QcContext context) throws ProcessorException {

        Map<String, String> manifestEntries, previousManifestEntries;
        Archive previousArchive = context.getExperiment().getPreviousArchiveFor(archive);
        List<String> addedFiles = new ArrayList<String>();
        List<String> changedFiles = new ArrayList<String>();
        List<String> deletedFiles = new ArrayList<String>();

        if (previousArchive != null) {
            try {
                previousManifestEntries = getManifestEntries(previousArchive);
                manifestEntries = getManifestEntries(archive);
                compareManifestFiles(manifestEntries, previousManifestEntries, addedFiles, changedFiles, deletedFiles, previousArchive);
                writeChangeLogFile(archive, previousArchive, addedFiles, changedFiles, deletedFiles);
                //update the manifest with an entry for change log file
                if (!manifestEntries.containsKey(CHANGE_FILE_NAME)) {
                    File manifestFile = new File(archive.getDeployDirectory(), ManifestValidator.MANIFEST_FILE);
                    File changeFile = new File(archive.getDeployDirectory(), CHANGE_FILE_NAME);
                    manifestParser.addFileToManifest(changeFile, manifestFile);
                }
            } catch (IOException e) {
                archive.setDeployStatus(Archive.STATUS_IN_REVIEW);
                throw new ProcessorException(new StringBuilder().append("Error while creating the change log: ").append(e.getMessage()).toString());
            } catch (ParseException e) {
                archive.setDeployStatus(Archive.STATUS_IN_REVIEW);
                throw new ProcessorException(new StringBuilder().append("Error while creating the change log: ").append(e.getMessage()).toString());
            } catch (NoSuchAlgorithmException e) {
                archive.setDeployStatus(Archive.STATUS_IN_REVIEW);
                throw new ProcessorException(new StringBuilder().append("Error while creating the change log: ").append(e.getMessage()).toString());
            }
        }
        return archive;
    }

    private void writeChangeLogFile(final Archive archive, final Archive previousArchive,
                                    List<String> addedFiles, List<String> changedFiles,
                                    List<String> deletedFiles) throws IOException {

        final File changeLogFile = new File(archive.getDeployDirectory(), CHANGE_FILE_NAME);
        BufferedWriter writer = null;
        FileWriter fWriter = null;
        try {
            fWriter = new FileWriter(changeLogFile);
            writer = new BufferedWriter(fWriter);
            writer.write("CHANGES for revised archive " +
                    archive.getRealName() + " compared to archive " +
                    previousArchive.getRealName());

            writer.write(LINE_DELIMITER);
            writer.write(LINE_DELIMITER);

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
            writer.write("This file was automatically generated on " + dateFormat.format(new Date()) +
                    " using the TCGA DCC Archive Processing System.");

            writer.write(LINE_DELIMITER);
            writer.write(LINE_DELIMITER);

            writer.write("# key" + LINE_DELIMITER);
            writer.write("# " + ADDED_FILE_SYMBOL + TAB_DELIMITER + "added" + LINE_DELIMITER);
            writer.write("# " + DELETED_FILE_SYMBOL + TAB_DELIMITER + "removed" + LINE_DELIMITER);
            writer.write("# " + CHANGED_FILE_SYMBOL + TAB_DELIMITER + "revised" + LINE_DELIMITER);

            writer.write(LINE_DELIMITER);
            writer.write(LINE_DELIMITER);

            writer.write("Files Revised" + LINE_DELIMITER);

            String fileInfo = TAB_DELIMITER + ADDED_FILE_SYMBOL + LINE_DELIMITER;
            for (String fileName : addedFiles) {
                writer.write(fileName + fileInfo);
            }

            fileInfo = TAB_DELIMITER + CHANGED_FILE_SYMBOL + LINE_DELIMITER;
            for (String fileName : changedFiles) {
                writer.write(fileName + fileInfo);
            }

            fileInfo = TAB_DELIMITER + DELETED_FILE_SYMBOL + LINE_DELIMITER;
            for (String fileName : deletedFiles) {
                writer.write(fileName + fileInfo);
            }

            writer.write(LINE_DELIMITER);
            writer.write(LINE_DELIMITER);

            writer.write("#This is a tab-delimited document");
            writer.close();
            fWriter.close();
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                    fWriter.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    private Map<String, String> getManifestEntries(final Archive archive)
            throws IOException, ParseException {
        Map<String, String> manifestEntries;
        final File manifestFile = new File(archive.getDeployDirectory(), ManifestValidator.MANIFEST_FILE);
        manifestEntries = manifestParser.parseManifest(manifestFile);
        return manifestEntries;
    }

    private void compareManifestFiles(
            final Map<String, String> manifestFileList, final Map<String,
                    String> previousManifestFileList,
            final List<String> addedFiles, final List<String> changedFiles,
            final List<String> deletedFiles, final Archive previousArchive) throws IOException {
        // files that are newly added
        difference(manifestFileList.keySet(), previousManifestFileList.keySet(), addedFiles);
        // files that are removed
        difference(previousManifestFileList.keySet(), manifestFileList.keySet(), deletedFiles);
        // files that are common
        intersection(manifestFileList.keySet(), previousManifestFileList.keySet(), changedFiles);

        Map<String, String> alteredFiles = AlteredFileListCreator.getAlteredFileListMap(previousArchive);

        // Check the MD5 entries for each of them to check if they really changed
        // Also check the MD5 in the manifest vs in the altered files list, and remove any whose MD5s match,
        // because that means they didn't change either
        String fileName;
        for (int fileIndex = changedFiles.size() - 1; fileIndex >= 0; fileIndex--) {
            fileName = changedFiles.get(fileIndex);
            if (manifestFileList.get(fileName).equals(previousManifestFileList.get(fileName))) {
                changedFiles.remove(fileName);
            } else if (manifestFileList.get(fileName).equals(alteredFiles.get(fileName))) {
                changedFiles.remove(fileName);
            }
        }
    }


    private void difference(Collection<String> a, Collection<String> b, Collection<String> result) {
        result.addAll(a);
        result.removeAll(b);
    }

    private void intersection(Collection<String> a, Collection<String> b, Collection<String> result) {
        result.addAll(a);
        result.retainAll(b);
    }

    public String getName() {
        return "change log creator";
    }

    public void setManifestParser(final ManifestParser manifestParser) {
        this.manifestParser = manifestParser;
    }
}
