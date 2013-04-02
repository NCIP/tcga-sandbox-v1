/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.qclive.live.service;

import gov.nih.nci.ncicb.tcga.dcc.ConstantValues;
import gov.nih.nci.ncicb.tcga.dcc.common.mail.MailErrorHelper;
import org.sadun.util.polling.DirectoryPoller;

/**
 * @author Robert S. Sfeir
 */
public class TCGADirectoryPoller extends DirectoryPoller {

    private TCGAPollManagerService tpoll = null;
    private MailErrorHelper mailHelper;

    public TCGADirectoryPoller() {
    }

    public TCGAPollManagerService getTpoll() {
        return tpoll;
    }

    public void shutdown() {
        super.shutdown();
        getMailHelper().send( ConstantValues.QCLIVE_SHUTDOWN_MESSAGE, "" );
    }

    public void setTpoll( final TCGAPollManagerService tpoll ) {
        this.tpoll = tpoll;
        this.setDirectories( tpoll.getTheDirs() );
        this.addPollManager( tpoll );
    }

    public void setMailHelper( MailErrorHelper mailHelper ) {
        this.mailHelper = mailHelper;
    }

    public MailErrorHelper getMailHelper() {
        return mailHelper;
    }
}
