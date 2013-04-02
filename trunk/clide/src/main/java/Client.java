/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

import gov.nih.nci.ncicb.tcga.dcc.clide.client.ClideClientImpl;
import gov.nih.nci.ncicb.tcga.dcc.clide.client.ClientContext;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideConstants;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideUtils;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideUtilsImpl;
import org.apache.log4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * A class outside of all packages to simplify the command line of clide
 *
 * @author Jon Whitmore
 *         Last updated by: $Author$
 * @version $Rev$
 */
public class Client {
    
	private static final Logger logger = Logger.getLogger(Client.class);	
    public static void main(String[] args) throws URISyntaxException {
        final ConfigurableApplicationContext ctx = new FileSystemXmlApplicationContext(ClideConstants.APP_CONTEXT);
        ctx.registerShutdownHook();        
       
        final ClideUtils clideUtils = (ClideUtils)ctx.getBean("clideUtilsImpl");
       
        final ClientContext clientContext = new ClientContext();
        Properties properties = ClideUtilsImpl.getClideProperties(System.getProperty("clide.configuration"));
        final String uriString = properties.getProperty("serverURI");
        final String destinationPath = properties.getProperty("clientDownloadDirectory");
        final String processedPath = properties.getProperty("clientProcessedDirectory");
        final String privateKey = properties.getProperty("privateKey");
        final String encrypt = properties.getProperty("clideEnableEncryption");
        final String internalLogging = properties.getProperty("clientInternalLogging");
        final String timeout = properties.getProperty("timeoutInSeconds");
        final String validate = properties.getProperty("forceValidate");
        final String diskSpaceThreshold= properties.getProperty("diskSpaceThreshold");
        final String noSpaceEmailTo= properties.getProperty("noSpaceEmailTo");
        final String noSpaceEmailBcc = properties.getProperty("noSpaceEmailBcc");
        final String noSpaceEmailSubject = properties.getProperty("noSpaceEmailSubject");
        final String noSpaceEmailContent = properties.getProperty("noSpaceEmailContent");

        final URI uri = new URI(uriString);
        final String scheme = uri.getScheme() == null ? "http" : uri.getScheme();
        final String host = uri.getHost() == null ? "localhost" : uri.getHost();
        final int port = uri.getPort() == -1 ? 80 : uri.getPort();

        if (!scheme.equals("http")) {
            throw new IllegalArgumentException("Only http is supported.");
        }

        clientContext.setDestinationPath(destinationPath);
        clientContext.setProcessedPath(processedPath);
        clientContext.setProcessedDir(clideUtils.validateClientProcessedDirectory(processedPath, destinationPath));
        clientContext.setDownloadDir(clideUtils.validateClientDownloadedDirectory(destinationPath));
        clientContext.setEncryptionEnabled(clideUtils.validateEncryption(encrypt));
        clientContext.setForceValidate(clideUtils.validateForceValidate(validate));
        clientContext.setUri(uri);
        clientContext.setHost(host);
        clientContext.setPort(port);
        clientContext.setPrivateKey(clideUtils.validatePrivateKey(privateKey));
        clientContext.setInternalLogging(clideUtils.validateClientInternalLogging(internalLogging));
        clientContext.setTimeout(clideUtils.validateTimeout(timeout));
        clientContext.setDiskSpaceThreshold(diskSpaceThreshold);
        clientContext.setNoSpaceEmailBcc(noSpaceEmailBcc);
        clientContext.setNoSpaceEmailContent(noSpaceEmailContent);
        clientContext.setNoSpaceEmailSubject(noSpaceEmailSubject);
        clientContext.setNoSpaceEmailTo(noSpaceEmailTo);
      
        clideUtils.cleanUpDirectory(destinationPath);
        clideUtils.setUpDirectories(clientContext);
        clideUtils.checkDiskSpace(clientContext);
        
        final ClideClientImpl clideClientImpl = (ClideClientImpl) ctx.getBean("clideClientImpl");
        clideClientImpl.setClientContext(clientContext);
        
        (new Thread(clideClientImpl)).start();
    }
}
