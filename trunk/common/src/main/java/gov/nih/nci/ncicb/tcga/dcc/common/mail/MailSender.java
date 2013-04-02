/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.mail;

/**
 * @author Robert S. Sfeir
 * @version $Id: MailSender.java 1253 2008-06-11 17:19:52Z sfeirr $
 */
public interface MailSender {

    void send( String to, String bcc, String subject, String body, final Boolean isHtml );

    void setDefaultFromName( String fromName );

    void setDefaultFromAddress( String fromAddress );

    void setSubjectPrefix( String subjectPrefix );

    void setMessageEncoding( String encoding );

    public boolean isMailEnabled();

    /**
     * Null implementation of MailSender used to provide sane defaults to MailService.
     */
    final static MailSender NULL_MAIL_SENDER = new MailSender() {
        public void send( final String to, final String bcc, final String subject, final String body,
                          final Boolean isHtml ) {
        }

        public void setDefaultFromName( final String fromName ) {
        }

        public void setDefaultFromAddress( final String fromAddress ) {
        }

        public void setSubjectPrefix( final String subjectPrefix ) {
        }

        public void setMessageEncoding( final String encoding ) {
        }

        public boolean isMailEnabled() {
            return false;
        }
    };
}