/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide.server;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideConstants;
import gov.nih.nci.ncicb.tcga.dcc.qclive.soundcheck.Soundcheck;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the steps to run Soundcheck in an easy to monitor Runnable
 *
 * @author Jon Whitmore
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class SoundcheckRun implements Runnable {

    private boolean isDone = false;
    private Soundcheck soundcheck = null;

    private final Logger logger = Logger.getLogger(
            SoundcheckRun.class.getName());

    public boolean isDone() {
        return isDone;
    }

    public boolean wasSuccessful() {
        if (soundcheck == null) {
            // this implies they forgot to run soundcheck, or soundcheck didn't run with the arguments given.
            return false;
        }
        return soundcheck.getWasSuccessful();

    }

    public List<String> getErrors() {
        return soundcheck.getQcContext().getErrors();
    }

    public void run() {
        logger.info("Beginning Soundcheck run");
        isDone = false;
        File archiveFile = ServerContext.getWorkingDir();
        logger.info("validating archives in " + archiveFile);
        ArrayList<String> args = new ArrayList<String>();
        try {
            args.add(archiveFile.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        soundcheck = Soundcheck.runSoundcheckWith(new Soundcheck(), args.toArray(new String[args.size()]));
        logger.info("Completed Soundcheck run");
        isDone = true;
    }

    /**
     * clean up everything except the archives and the md5 files unless deleteArchives is true, in which
     * case delete everything.
     */
    public void cleanUp(final File dir, final boolean deleteArchives) {
        File[] contents = dir.listFiles(new ClideConstants.NonHiddenFileFilter());
        for (int i = contents.length - 1; i >= 0; --i) {
            File file = contents[i];
            if (file.isDirectory()) {
                cleanUp(file, deleteArchives);
            }
            if (deleteArchives) {
                file.delete();
            } else {
                if (!file.getName().endsWith(ConstantValues.ARCHIVE_EXTENSION)
                        && !file.getName().endsWith(ConstantValues.ARCHIVE_EXTENSION + ".md5")) {
                    file.delete();

                }

            }

        }

    }

}
