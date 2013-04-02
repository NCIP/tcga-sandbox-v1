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
 *         Class which is used to send out exceptions as they happen within the application.
 *         It will email a default user set in the spring file preferences.
 *         There is only one method which needs to be called and that is the send() method.
 *         The send method needs a explanation and the exception so we can include them in the email
 */
public class MailErrorHelperImpl implements MailErrorHelper {

    private String to;
    private MailSender mailSender;
    private String subject;

    /**
     * get the default mail sender implementation.  Needed to inject via spring
     *
     * @return mail sender impl
     */
    public MailSender getMailSender() {
        return mailSender;
    }

    /**
     * sets the impl by injection through spring framework
     *
     * @param mailSender the mail sender impl which matches this interface
     */
    public void setMailSender( final MailSender mailSender ) {
        this.mailSender = mailSender;
    }

    /**
     * get the default subject for the meail
     *
     * @return the subject string
     */
    public String getSubject() {
        return subject;
    }

    /**
     * sets the default subject via injection in spring.
     *
     * @param subject the string used for subject.
     */
    public void setSubject( final String subject ) {
        this.subject = subject;
    }

    /**
     * Who are we sending this to, returns email address
     *
     * @return returns the value of the TO field in the email.
     */
    public String getTo() {
        return to;
    }

    /**
     * Set who we're sending this to
     *
     * @param to email of address we're sending exception to
     */
    public void setTo( final String to ) {
        this.to = to;
    }

    public MailErrorHelperImpl() {
    }

    /**
     * Method used to send out the email when an exception happens.
     *
     * @param explanation what we think happened, non cryptic possible explanation of what's going on.
     * @param exception   the raw error thrown at the point of failure
     */
    public void send( String explanation, String exception ) {
        getMailSender().send( getTo(), null, getSubject(), explanation + "\n\n\n Raw Exception:\n\n" + exception, false );
    }
}
