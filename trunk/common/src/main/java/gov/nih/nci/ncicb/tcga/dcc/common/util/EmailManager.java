/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.common.util;

import gov.nih.nci.ncicb.tcga.dcc.common.bean.UUIDDetail;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailSender;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Description : Class for sending emails
 *
 * @author Namrata Rane Last updated by: $Author: $
 * @version $Rev: $
 */


public class EmailManager {

    @Autowired
    private MailSender mailSender;

    public EmailManager(){
    }

    public EmailManager(final MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendNewUUIDListToCenter(final String mailTo, final List<UUIDDetail> details) {
        if(mailSender != null && mailTo != null) {
            StringBuilder msg = new StringBuilder();
            msg.append( "The following UUIDs are created for the center : \n\n");
            for(final UUIDDetail detail : details) {
                msg.append(detail.getUuid()+" \n");
            }
            mailSender.send(mailTo, null, "New UUIDs Generated", msg.toString(), false);
        }
    }

}

