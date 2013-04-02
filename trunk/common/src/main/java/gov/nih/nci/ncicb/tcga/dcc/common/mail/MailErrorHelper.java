package gov.nih.nci.ncicb.tcga.dcc.common.mail;

/**
 * TODO: Class description
 *
 * @author Jessica Chen
 *         Last updated by: $Author$
 * @version $Rev$
 */
public interface MailErrorHelper {

    MailSender getMailSender();

    void setMailSender( MailSender mailSender );

    String getSubject();

    void setSubject( String subject );

    String getTo();

    void setTo( String to );

    void send( String explanation, String exception );
}
