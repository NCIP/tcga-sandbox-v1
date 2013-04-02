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
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.DataFile;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.FilePackagerBean;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobHistory;
import gov.nih.nci.ncicb.tcga.dcc.dam.bean.QuartzJobStatus;
import gov.nih.nci.ncicb.tcga.dcc.dam.service.FilePackagerEnqueuerI;
import gov.nih.nci.ncicb.tcga.dcc.dam.view.request.FilterRequestI;
import org.quartz.SchedulerException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Author: David Nassau
 * <p/>
 * Creates new FilePackager instances. Is created by the Spring framework as a bean and configured with several
 * properties.  But within the application, it is referenced as a singleton.
 */
public abstract class FilePackagerFactory implements FilePackagerFactoryI {

    /**
     * Map containing all the QuartzJobHistory (lightweight FilePackagerBean) that are currently "alive", either running or queued.
     * It will also contain all QuartzJobHistory that finished (succeeded or failed) and not past their expiration date
     * after the application restarts so that the web service user can check on the job status
     */
    private final Map<UUID, QuartzJobHistory> livePackagers;

    private FilePackagerEnqueuerI filePackagerEnqueuer;
    private String protectedArchivePhysicalPath, protectedArchiveLogicalPath,
            notProtectedArchivePhysicalPath, notProtectedArchiveLogicalPath;
    private MailErrorHelper errorMailSender;
    private MailSender mailSender;

    public FilePackagerFactory() {
        livePackagers = Collections.synchronizedMap(new HashMap<UUID, QuartzJobHistory>());
    }

    public MailErrorHelper getErrorMailSender() {
        return errorMailSender;
    }

    public void setErrorMailSender(MailErrorHelper errorMailSender) {
        this.errorMailSender = errorMailSender;
    }

    /**
     * Store the given <code>QuartzJobHistory</code> in a map with the given <code>UUID</code> as the key
     *
     * @param key              the <code>UUID</code> to use for the map key
     * @param quartzJobHistory the <code>QuartzJobHistory</code> to store
     */
    public void putQuartzJobHistory(final UUID key, final QuartzJobHistory quartzJobHistory) {
        livePackagers.put(key, quartzJobHistory);
    }

    /**
     * Retrieve the <code>QuartzJobHistory</code> stored in the map with the given <code>UUID</code> key
     *
     * @param key the map key
     * @return the <code>QuartzJobHistory</code> stored in the map with the given <code>UUID</code> key
     */
    public QuartzJobHistory getQuartzJobHistory(UUID key) {
        return livePackagers.get(key);
    }

    public void removeFilePackagerBean(UUID key) {
        livePackagers.remove(key);
    }

    public FilePackagerEnqueuerI getFilePackagerEnqueuer() {
        return filePackagerEnqueuer;
    }

    public void setFilePackagerEnqueuer(FilePackagerEnqueuerI filePackagerEnqueuer) {
        this.filePackagerEnqueuer = filePackagerEnqueuer;
    }


    private String ensureTrailingSlash(String s) {
        if (s != null && !s.endsWith("/")) {
            s = s + "/";
        }
        return s;
    }

    private String ensureLeadingSlash(String s) {
        if (s != null && !s.startsWith("/")) {
            s = "/" + s;
        }
        return s;
    }

    public void setProtectedArchivePhysicalPath(final String protectedArchivePhysicalPath) {
        this.protectedArchivePhysicalPath = ensureTrailingSlash(protectedArchivePhysicalPath);
    }

    public void setProtectedArchiveLogicalPath(final String protectedArchiveLogicalPath) {
        this.protectedArchiveLogicalPath = ensureLeadingSlash(ensureTrailingSlash(protectedArchiveLogicalPath));
    }

    public void setNotProtectedArchivePhysicalPath(final String notProtectedArchivePhysicalPath) {
        this.notProtectedArchivePhysicalPath = ensureTrailingSlash(notProtectedArchivePhysicalPath);
    }

    public void setNotProtectedArchiveLogicalPath(final String notProtectedArchiveLogicalPath) {
        this.notProtectedArchiveLogicalPath = ensureLeadingSlash(ensureTrailingSlash(notProtectedArchiveLogicalPath));
    }

    public FilePackagerBean createFilePackagerBean(
            final String disease, final List<DataFile> selectedFiles, final String email,
            final boolean flatten, final boolean isProtected, final UUID key, final FilterRequestI filterRequest) {
        // getFilePackager should be method injected in spring to return a proxy to FP that can be advised
        final FilePackagerBean filePackagerBean = getFilePackagerBean();
        final String name = UUID.randomUUID().toString();
        final String physicalName;
        final String logicalName;
        if (isProtected) {
            physicalName = protectedArchivePhysicalPath + name;
            logicalName = protectedArchiveLogicalPath + name;
        } else {
            physicalName = notProtectedArchivePhysicalPath + name;
            logicalName = notProtectedArchiveLogicalPath + name;
        }

        filePackagerBean.setArchivePhysicalName(physicalName);
        filePackagerBean.setArchiveLogicalName(logicalName);
        filePackagerBean.setFlatten(flatten);
        filePackagerBean.setEmail(email);
        filePackagerBean.setDisease(disease);
        filePackagerBean.setKey(key);
        filePackagerBean.setSelectedFiles(selectedFiles);
        filePackagerBean.setFilterRequest(filterRequest);
        return filePackagerBean;
    }

    // method injected by spring

    public abstract FilePackagerBean getFilePackagerBean();

    public void enqueueFilePackagerBean(final FilePackagerBean fp) throws SchedulerException {
        try {
            filePackagerEnqueuer.queueFilePackagerJob(fp);
            putQuartzJobHistory(fp.getKey(), fp.getUpdatedQuartzJobHistory());//this statement must go after the preceding to get QuartzJobHistory

            if (fp.getStatusCheckUrl() != null && fp.getEmail() != null && mailSender != null) {
                StringBuilder emailBody = new StringBuilder();
                emailBody.append("Your archive request has been submitted to the DCC. You will receive another email when the job is complete, ").
                        append("along with a link to download the archive. To check on the status of your request, please use this link: ").append(fp.getStatusCheckUrl());
                if (fp.getFilterRequest() != null) {
                    emailBody.append("\n\nThe following filter settings were used for the search criteria:\n\n");
                    emailBody.append(fp.getFilterRequest().toString());
                }
                mailSender.send(fp.getEmail(), null, "Download Requested", emailBody.toString(), false);
            }
        } catch (SchedulerException se) {
            fp.setStatus(QuartzJobStatus.Failed);
            fp.setException(se);
            throw se;
        }
    }

    public void setMailSender(final MailSender mailSender) {
        this.mailSender = mailSender;
    }
}
