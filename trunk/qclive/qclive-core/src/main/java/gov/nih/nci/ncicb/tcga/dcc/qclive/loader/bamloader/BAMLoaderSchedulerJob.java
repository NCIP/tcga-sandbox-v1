/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */
package gov.nih.nci.ncicb.tcga.dcc.qclive.loader.bamloader;


import gov.nih.nci.ncicb.tcga.dcc.common.bean.bam.BamContext;
import gov.nih.nci.ncicb.tcga.dcc.common.framework.SpringApplicationContext;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailErrorHelper;
import gov.nih.nci.ncicb.tcga.dcc.common.util.SpringBeanName;
import gov.nih.nci.ncicb.tcga.dcc.common.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

/**
 * Schedules BAM loader to load BAM data.
 *
 * @author ramanr
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class BAMLoaderSchedulerJob implements Job {
    private final Log logger = LogFactory.getLog(getClass());

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        // since Quartz and Spring don't share a lifecycle, lookup the beans manually
        final BamContext bamContext = new BamContext();
        try {
            logger.info("Started BAM loader job");
            BAMLoader bamLoader = getBAMLoader();
            bamLoader.loadBAMData(bamContext);
            if(bamContext.getErrorList().size() > 0 ||
                    bamContext.getWarningList().size() > 0) {
                sendErrorEmail(null, bamContext);
            }
            logger.info("Completed BAM loader job");
        } catch (Throwable e) {
            logger.error("Error loading BAM data " + e.getMessage());
            // email full stack trace
            sendErrorEmail((Exception) e, bamContext);
        }
    }

    protected BAMLoader getBAMLoader(){
        return (BAMLoader) SpringApplicationContext.getObject(SpringBeanName.BAM_LOADER.getValue());
    }

    protected MailErrorHelper getErrorMailSender() {
        return (MailErrorHelper) SpringApplicationContext.getObject(SpringBeanName.MAIL_ERROR_HELPER.getValue());
    }

    private void sendErrorEmail(final Exception exception,
                                final BamContext bamContext) {
        final String subject = "BAM Loader:";
        final StringBuilder message = new StringBuilder();
        if(exception != null ){
            message.append("Exception occurred while loading BAM data:")
                    .append(StringUtil.stackTraceAsString(exception));
        }
        if(bamContext.getErrorList().size() > 0 ) {
            message.append("\nFollowing errors are recorded while loading BAM data: ");
            message.append(getMessages(bamContext.getErrorList(),true));
        }
        if(bamContext.getWarningList().size() > 0 ) {
            message.append("\nFollowing warnings are recorded while loading BAM data: ");
            message.append(getMessages(bamContext.getWarningList(),false));
        }
        getErrorMailSender()
                .getMailSender()
                .send( getErrorMailSender().getTo(),
                        null,
                        subject,
                        message.toString(),
                        false );

    }

    private String getMessages(final List<String> msgs, final Boolean isError) {
        final StringBuffer stringBuffer = new StringBuffer();
        for(String msg: msgs){
            if(isError){
                logger.error(msg);
            }else{
                logger.warn(msg);
            }
            stringBuffer.append("\n")
                    .append(msg);
        }

        return stringBuffer.toString();
    }

}