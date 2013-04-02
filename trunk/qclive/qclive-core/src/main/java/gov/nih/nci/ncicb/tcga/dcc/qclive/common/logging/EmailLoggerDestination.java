/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.common.logging;

import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;

/**
 * Logger destination that sends log messages via email.
 *
 * @author Jessica Chen
 *         Last updated by: $Author: sfeirr $
 * @version $Rev: 3419 $
 */
public class EmailLoggerDestination extends AbstractLoggerDestination {

    private String emailAddress;
    private MailSender mailSender;

    protected void log( final String message ) throws LoggerException {
        String subject = message;
        if(subject.length() > 20) {
            subject = subject.substring( 0, 20 ) + "...";
        }
        if(mailSender != null && emailAddress != null) {
            mailSender.send( emailAddress, null, subject, message, false );
        } else {
            throw new LoggerException( "EmailLoggerDestination is not configured properly.  MailSender instance and email address must both be set" );
        }
    }

    public void setEmailAddress( final String emailAddress ) {
        this.emailAddress = emailAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setMailSender( final MailSender mailSender ) {
        this.mailSender = mailSender;
    }
}
