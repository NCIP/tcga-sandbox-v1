/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideConstants;
import gov.nih.nci.ncicb.tcga.dcc.clide.server.ClideServer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.net.URISyntaxException;

/**
 * A Class to simplify the command line arguments
 *
 * @author Jon Whitmore
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class Server {
    public static void main(String[] args) throws URISyntaxException {
        ConfigurableApplicationContext ctx = new FileSystemXmlApplicationContext(new String[]{
                ClideConstants.APP_CONTEXT, ClideConstants.INTEGRATION_CONTEXT});
        ctx.registerShutdownHook();
        ClideServer clideServer = (ClideServer)ctx.getBean("clideServer");
        clideServer.ClideServerSetUp();
        clideServer.start();
    }
}
