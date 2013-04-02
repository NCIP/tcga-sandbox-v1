/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.util;

import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;
import org.apache.commons.io.IOUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;

import static gov.nih.nci.ncicb.tcga.dcc.qclive.common.util.QcliveCloseableUtil.close;

/**
 * @author David Nassau
 * @version $Rev$
 */
public class MailGenerator {

    private MailSender mailSender;
    private String mailTo;
    private String loadDirectory;
    private RowCounter rowCounter;

    public MailGenerator( MailSender mailSender, String mailTo, String loadDirectory, RowCounter rowCounter ) {
        this.mailSender = mailSender;
        this.mailTo = mailTo;
        this.loadDirectory = loadDirectory;
        this.rowCounter = rowCounter;
    }

    public void sendSuccessEmail( String message ) {
        if(mailSender != null && mailTo != null) {
            mailSender.send( mailTo, null, "Loader Succeeded", message, false );
        }
    }

    public void sendFailureEmail( final Exception e ) {

        PrintWriter pw = null;

        try {
            if(mailSender != null && mailTo != null) {
                StringWriter sw = new StringWriter();
                //noinspection IOResourceOpenedButNotSafelyClosed
                pw = new PrintWriter( sw );
                e.printStackTrace( pw );
                String stackTrace = sw.toString();
                pw.close();
                StringBuffer msg = new StringBuffer();
                msg.append( "Loader Failed. Archive directory: " ).append( loadDirectory ).append( "\n\n" );
                msg.append( stackTrace );
                mailSender.send( mailTo, null, "Loader Failed", msg.toString(), false );
            }
        } finally {
            IOUtils.closeQuietly(pw);
        }
    }

    public void sendStartEmail() {
        if(mailSender != null && mailTo != null) {
            mailSender.send( mailTo, null, "Loader Started", "Loader started. Archive directory: " + loadDirectory, false );
        }
    }
}
