/*
 * Software License, Version 1.0 Copyright 2009 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.datareports.email;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.Center;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.DatareportsService;
import gov.nih.nci.ncicb.tcga.dcc.datareports.service.SampleSummaryReportService;
import org.apache.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static gov.nih.nci.ncicb.tcga.dcc.datareports.constants.DatareportsProperties.pocEmail;

/**
 * Generate and email a filtered Sample Summary report to each center
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */

public class SendEmail {

    @Autowired
    private ProcessLogger logger;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private CenterSampleReportGenerator emailService;

    @Autowired
    private DatareportsService commonService;

    @Autowired
    private SampleSummaryReportService ssService;

    private boolean active = true;
    private static SendEmail instance;

    public void sendEmails() {
        logger.logToLogger(Level.INFO, "SendEmail start runJob ");
        logger.logToLogger(Level.DEBUG, "SendEmail runJob active = " + active);
        if (active) {
            List<Center> centerList = ssService.getCenters();
            logger.logToLogger(Level.INFO, "SendEmail prepare to set email to " + centerList.size() + " centers ");
            for (Center c : centerList) {
                String centerContact = c.getCenterEmail();
                String center = c.getCenterName();
                String display = c.getCenterDisplayName();
                String type = c.getCenterType();
                String subject = "Weekly Biospecimen List report for " + display + " (" + type + ")";
                String content = emailService.generateHTMLFor(center);
                mailSender.send(centerContact, pocEmail, subject, content, true);
            }
            logger.logToLogger(Level.INFO, "SendEmail end runJob ");

        } else {
            logger.logToLogger(Level.DEBUG, "SendEmailQuartzJob is inactive " + active);
        }
    }


    public synchronized boolean getActive() {
        return active;
    }

    public synchronized void setActive(final boolean active) {
        logger.logToLogger(Level.INFO, "SendEmail setActive active = " + active);
        this.active = active;
    }

    public static synchronized SendEmail getInstance() {
        if (instance == null) {
            instance = new SendEmail();
        }
        return instance;
    }
}