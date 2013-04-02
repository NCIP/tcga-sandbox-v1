/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.mail.impl;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;
import gov.nih.nci.ncicb.tcga.dcc.common.util.ProcessLogger;
import org.apache.log4j.Level;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * @author Robert S. Sfeir
 * @version $Id: MailSenderImpl.java 1253 2008-06-11 17:19:52Z sfeirr $
 */
public class MailSenderImpl implements MailSender {

    private JavaMailSender javaMailSender = null;
    private String defaultFromName = null;
    private String defaultFromAddress = null;
    private String subjectPrefix = null;
    private String messageEncoding = null;
    private boolean mailEnabled = false;
    private final ProcessLogger processLogger = new ProcessLogger();
    private String defaultCcAddress = null;
    private String defaultReplyTo;

    public void setJavaMailSender( final JavaMailSender javaMailSender ) {
        this.javaMailSender = javaMailSender;
    }

    public void setDefaultCcAddress( final String defaultCcAddress ) {
        this.defaultCcAddress = defaultCcAddress;
    }

    public void setDefaultFromName( final String defaultFromName ) {
        this.defaultFromName = defaultFromName;
    }

    public void setDefaultFromAddress( final String defaultFromAddress ) {
        this.defaultFromAddress = defaultFromAddress;
    }

    public void setSubjectPrefix( final String subjectPrefix ) {
        this.subjectPrefix = subjectPrefix;
    }

    public void setMessageEncoding( final String messageEncoding ) {
        this.messageEncoding = messageEncoding;
    }

    public void setDefaultReplyTo( final String defaultReplyTo ) {
        this.defaultReplyTo = defaultReplyTo;
    }

    public boolean isMailEnabled() {
        return mailEnabled;
    }

    public void setMailEnabled( final boolean mailEnabled ) {
        this.mailEnabled = mailEnabled;
    }

    public void send( final String to, final String bcc, final String subject, final String body, final Boolean sendHtmlEmailFormat ) {
        if(isMailEnabled()) {
            final MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = null;
            try {
                messageHelper = new MimeMessageHelper( message, "UTF-8" );
                messageHelper.setFrom( defaultFromAddress, defaultFromName );
                //We can handle more than one email address, just submit a String with , between each email.
                if(to != null && to.length() > 0 && !to.contains( ConstantValues.EMAIL_SEPARATOR )) {
                    //We only have one email, no comma separating anything.
                    messageHelper.setTo( to );
                } else if(to != null && to.length() > 0 && to.contains( ConstantValues.EMAIL_SEPARATOR )) {
                    //We have more than one email separated by commas.
                    messageHelper.setTo( to.split( ConstantValues.EMAIL_SEPARATOR ) );
                }
                if(defaultCcAddress != null && defaultCcAddress.length() > 0) {
                    messageHelper.setCc( defaultCcAddress );
                }
                if(defaultReplyTo != null && defaultReplyTo.length() > 0) {
                    messageHelper.setReplyTo( defaultReplyTo );
                }
                if(bcc != null && bcc.length() > 0 && !bcc.contains( ConstantValues.EMAIL_SEPARATOR )) {
                    //we just have one single address
                    messageHelper.addBcc( bcc );
                } else if(bcc != null && bcc.contains( ConstantValues.EMAIL_SEPARATOR )) {
                    //we have more than one address
                    String[] bccList = bcc.split( ConstantValues.EMAIL_SEPARATOR );
                    for(final String aBccList : bccList) {
                        messageHelper.addBcc( aBccList );
                    }
                }
            }
            catch(MessagingException e) {
                processLogger.logToLogger( Level.FATAL, "An exception occured while setting up a MailMessageHelper" );
            }
            catch(UnsupportedEncodingException e) {
                processLogger.logToLogger( Level.FATAL, "An exception occured, the encoding passed in is invalid " + messageEncoding +
                        " is not supported!" );
            }
            try {
                messageHelper.setText(addDateStampToBodyAsFooter(body), sendHtmlEmailFormat);
            }
            catch(MessagingException e) {
                processLogger.logToLogger( Level.FATAL, "An exception occured while setting the message body!" );
            }
            try {
                messageHelper.setSubject( subjectPrefix + ' ' + subject );
            }
            catch(MessagingException e) {
                processLogger.logToLogger( Level.FATAL, "An exception occured while setting the message subject" );
            }
            javaMailSender.send( message );
        }
    }

    private String addDateStampToBodyAsFooter(final String body) {
        StringBuilder footer = new StringBuilder(2);
        footer.append(body).append("\n\n").append("--\n").append("Sent by the DCC on ").append(new Date().toString());
        return footer.toString();
    }
}

