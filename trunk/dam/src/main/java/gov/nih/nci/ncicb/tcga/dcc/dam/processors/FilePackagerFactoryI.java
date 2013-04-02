/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.dam.processors;

import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailErrorHelper;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.FilePackagerBean;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobHistory;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzQueueJobDetails;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequestI;
import org.quartz.SchedulerException;

import java.util.List;
import java.util.UUID;

/**
 * Interface for FilePackager, created for unit testing
 *
 * @author David Nassau
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface FilePackagerFactoryI {

    /**
     * Store the given <code>QuartzJobHistory</code> in a map with the given <code>UUID</code> as the key
     *
     * @param key              the <code>UUID</code> to use for the map key
     * @param quartzJobHistory the <code>QuartzJobHistory</code> to store
     */
    void putQuartzJobHistory(final UUID key, final QuartzJobHistory quartzJobHistory);

    /**
     * Retrieve the <code>QuartzJobHistory</code> stored in the map with the given <code>UUID</code> key
     *
     * @param key the map key
     * @return the <code>QuartzJobHistory</code> stored in the map with the given <code>UUID</code> key
     */
    public QuartzJobHistory getQuartzJobHistory(final UUID key);

    /**
     * Return the <code>QuartzQueueJobDetails</code> with the given <code>UUID</code> as the key
     *
     * @param key the Quartz job name
     * @return the <code>QuartzQueueJobDetails</code> with the given <code>UUID</code> as the key
     */
    public QuartzQueueJobDetails getQuartzQueueJobDetails(final UUID key);

    void removeFilePackagerBean(UUID key);

    FilePackagerBean createFilePackagerBean(String disease, List<DataFile> selectedFiles, String email,
                                            boolean flatten, boolean isProtected, UUID key, FilterRequestI filterRequest);

    void enqueueFilePackagerBean(FilePackagerBean fp) throws SchedulerException;

    public MailErrorHelper getErrorMailSender();
}
