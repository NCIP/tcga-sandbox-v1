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
import gov.nih.nci.ncicb.tcga.dcc.common.bean.ArchiveBase;
import gov.nih.nci.ncicb.tcga.dcc.common.framework.SpringApplicationContext;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import gov.nih.nci.ncicb.tcga.dcc.qclive.live.Live;

/**
 * Job for cleaning up failed archives.
 *
 * @author Jessica Chen Last updated by: $Author$
 * @version $Rev$
 */
public class ArchiveCleanupJob extends QuartzJobBean {
    public static final String ARCHIVE = "archive";
    public static final String ARCHIVE_FAILED = "archiveFailed";

    protected void executeInternal(final JobExecutionContext jobExecutionContext) throws JobExecutionException {
        ArchiveBase archiveBase = (ArchiveBase) jobExecutionContext.getJobDetail().getJobDataMap().get(ARCHIVE);
        boolean archiveFailed = (Boolean) jobExecutionContext.getJobDetail().getJobDataMap().get(ARCHIVE_FAILED);
        Live live = (Live) SpringApplicationContext.getObject(ConstantValues.LIVE_OBJECT_SPRING_BEAN_NAME);
        live.cleanupArchive(archiveBase, archiveFailed);
    }
}
