/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.live.service;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.bean.Archive;
import gov.nih.nci.ncicb.tcga.dcc.common.dao.DiseaseContextHolder;
import gov.nih.nci.ncicb.tcga.dcc.common.framework.SpringApplicationContext;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailErrorHelper;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;
import gov.nih.nci.ncicb.tcga.dcc.qclive.common.QcLiveStateBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.live.Live;
import gov.nih.nci.ncicb.tcga.dcc.qclive.live.LiveI;
import org.apache.log4j.Level;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

import java.util.ArrayList;
import java.util.List;

/**
 * Job for loading clinical archives .
 * The job calls loadClinicalData method on Live object.
 *
 * @author Stanley Girshik
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class ClinicalLoaderJob implements StatefulJob {
    public static final String CLINICAL_ARCHIVES = "clinicalArchives";
    public static final String CLINICAL_PROCESSED_ARCHIVES = "clinicalProcessedArchives";
    ProcessLogger logger = new ProcessLogger();


    public void execute(final JobExecutionContext context) throws JobExecutionException {
        // As this is a stateful job archives to be loaded are stored in the trigger job datamap,
        // so get the archives from merged job data map ( merged data map  merges the job datamap from job and trigger
        List<Archive> archives = (List<Archive>) context.getMergedJobDataMap().get(CLINICAL_ARCHIVES);
        final String diseaseAbbreviation = archives.get(0).getTheTumor().getTumorName();
        DiseaseContextHolder.setDisease(diseaseAbbreviation);

        // load clinical data
        loadArchives(context);
     }

    private void loadArchives(final JobExecutionContext context) {
        // Each clinical archive is treated as one experiment, so for clinical archives there will be only one one archive in the list.
        List<Archive> archives = (List<Archive>) context.getMergedJobDataMap().get(CLINICAL_ARCHIVES);

        for (final Archive archive : archives) {
             try {
                logger.logToLogger(Level.INFO, " Started job for " + archive);
                getLive().loadClinicalData(archives, (QcLiveStateBean) context.getMergedJobDataMap().get(LiveScheduler.STATE_CONTEXT));
                logger.logToLogger(Level.INFO, " Completed job for " + archive);
            } catch (Exception e) {
                logger.logError(e);
                sendErrorEmail(e, "Failed loading clinical archives " + archive.getArchiveName());
            } finally {
                saveArchivesInJobDataMap(context, archive);
            }
        }
    }

    private void cleanupJobDataMap(final JobExecutionContext context) {
         context.getJobDetail().getJobDataMap().remove(CLINICAL_PROCESSED_ARCHIVES);
     }

     private void saveArchivesInJobDataMap(final JobExecutionContext context,
                                           final Archive archive) {
         // store processed archives in job data map
         List<Archive> processedArchives = (List<Archive>) context.getJobDetail().getJobDataMap().get(CLINICAL_PROCESSED_ARCHIVES);
         // save the archive status
         if (processedArchives == null) {
             processedArchives = new ArrayList<Archive>();
         }
         processedArchives.add(archive);
         context.getJobDetail().getJobDataMap().put(CLINICAL_PROCESSED_ARCHIVES, processedArchives);
         // debug message
         logger.logToLogger(Level.INFO, " Processed Archives " + getArchiveListAsString(processedArchives));
     }

    private String getArchiveListAsString(final List<Archive> archiveList) {
        final StringBuilder archives = new StringBuilder();
        for (Archive archive : archiveList) {
            archives.append(archive.getArchiveName())
                    .append("\n");

        }

        return archives.toString();
    }

    protected LiveI getLive() {
        return Live.getInstance();
    }

    protected MailErrorHelper getErrorMailSender() {
        return (MailErrorHelper) SpringApplicationContext.getObject(ConstantValues.MAIL_ERROR_HELPER_SPRING_BEAN_NAME);
    }

    private void sendErrorEmail(final Exception exception,
                                final String message) {
        getErrorMailSender().send(message, StringUtil.stackTraceAsString(exception));
    }
}
