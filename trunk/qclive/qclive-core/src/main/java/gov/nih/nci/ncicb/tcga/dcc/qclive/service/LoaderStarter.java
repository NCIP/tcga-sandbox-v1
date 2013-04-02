/*
 * The caBIG Software License, Version 1.0 Copyright 2009 TCGA DCC/Portal Project (Cancer Center)
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.service;

import gov.nih.nci.ncicb.tcga.dcc.common.service.FileTypeLookup;
import gov.nih.nci.ncicb.tcga.dcc.common.service.StatusCallback;
import org.quartz.SchedulerException;
/**
 * Interface to implement for objects that add data loading jobs to the queue.
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */

/**
 * Interface for class that will schedule a job to be loaded.
 * Usage example:
 * LoaderStarter starter = LoaderEnqueuer.getLoaderStarter();
 * try {
 * starter.queueLoaderJob(loadDir, magetabDir, new FileTypeLookupImpl());
 * } catch (SchedulerException e) { ... }
 */
public interface LoaderStarter {

    /**
     * Adds a Loader job to the queue, with callback for getting file type information.
     *
     * @param loadDirectory    Location of the exploded archive containing data files
     * @param magetabDirectory (Optional) Location of the magetab archive containing SDRF. If archive is old style, leave null
     * @param ftLookup         callback for the Loader to look up information about file type, level, and platform
     * @param statusCallback   callback for the Loader to report the current job's status to the caller
     * @throws SchedulerException Quartz exception if scheduling fails
     */
    void queueLoaderJob(String loadDirectory, String magetabDirectory, FileTypeLookup ftLookup,
                        StatusCallback statusCallback, String experimentName) throws SchedulerException;
}
