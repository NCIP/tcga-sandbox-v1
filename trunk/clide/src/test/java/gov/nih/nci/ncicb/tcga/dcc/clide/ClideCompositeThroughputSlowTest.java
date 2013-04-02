/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide;

import gov.nih.nci.ncicb.tcga.dcc.clide.client.ClideClient;
import gov.nih.nci.ncicb.tcga.dcc.clide.client.ClideClientImpl;
import gov.nih.nci.ncicb.tcga.dcc.clide.client.ClideClientPipelineFactory;
import gov.nih.nci.ncicb.tcga.dcc.clide.client.ClientContext;
import gov.nih.nci.ncicb.tcga.dcc.clide.clientmanager.ClideClientManager;
import gov.nih.nci.ncicb.tcga.dcc.clide.clientmanager.DccCenter;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideUtils;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideUtilsImpl;
import gov.nih.nci.ncicb.tcga.dcc.clide.server.ClideServer;
import gov.nih.nci.ncicb.tcga.dcc.clide.server.ClideServerPipelineFactory;
import gov.nih.nci.ncicb.tcga.dcc.clide.server.ServerContext;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.concurrent.Executors;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test class for the clide composite throughput monitor
 *
 * @author Dominique Berton
 *         Last updated by: $Author$
 * @version $Rev$
 */

public class ClideCompositeThroughputSlowTest extends ClideAbstractBaseTest {

	protected static final String CLIDE_SAMPLES_PATH = 
		Thread.currentThread().getContextClassLoader().getResource("samples").getPath() + File.separator;
    private ClideClientManager clientManager = new ClideClientManager();
    private ClideUtils clideUtils = (ClideUtils) ctx.getBean("clideUtilsImpl");
    private ClideServerPipelineFactory serverFactory = (ClideServerPipelineFactory) ctx.getBean("clideServerPipelineFactory");
    private ClideClientPipelineFactory clientFactory = (ClideClientPipelineFactory) ctx.getBean("clideClientPipelineFactoryImpl");
    ClideServer server1 = new ClideServer();
    ClideServer server2 = new ClideServer();
    ClideClientImpl client1 = new ClideClientImpl();
    ClideClientImpl client2 = new ClideClientImpl();


    @Before
    public void setUp() throws NoSuchAlgorithmException, IOException {
        clientManager.setProps(
        		ClideUtilsImpl.getClideProperties(
        				Thread.currentThread().getContextClassLoader().getResource("clide.properties").getPath()));
    }

    @After
    public void tearDown() {
        server1.stop();
        server1 = null;
        server2.stop();
        server2 = null;
        clientManager.stop();
        clientManager = null;
    }

    @Test
    public void testClideCompositeThroughput() throws Exception {
        ClideServerSetUp(server1,"http://localhost:8081");
        server1.start();
        waitASec();
        ClideServerSetUp(server2, "http://localhost:8083");
        server2.start();
        waitASec();
        clientManager.start();
        waitASec();
        client1.setClientContext(initClientContext(client1,"broad"));
        clientManager.startNewClientThread(client1, DccCenter.BROAD);
        waitASec();
        client2.setClientContext(initClientContext(client2,"jhu"));
        clientManager.startNewClientThread(client2, DccCenter.JHU);
        waitASec();
        ClideClient broadClient = clientManager.getClideClient(DccCenter.BROAD);
        ClideClient jhuClient = clientManager.getClideClient(DccCenter.JHU);
        assertNotNull(broadClient.getClientContext().getFactory().getThroughputMonitor());
        assertNotNull(jhuClient.getClientContext().getFactory().getThroughputMonitor());
        assertTrue(clientManager.getTransferredBytes()>=0L);
        assertEquals(clientManager.getTransferredBytes(),
                broadClient.getClientContext().getFactory().getThroughputMonitor().getTransferredBytes()+
                jhuClient.getClientContext().getFactory().getThroughputMonitor().getTransferredBytes());

    }

    private void waitASec() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }

    private ClientContext initClientContext(ClideClientImpl client,String center) throws URISyntaxException {
        final ClientContext clientContext = new ClientContext();
        Properties properties = 
        	ClideUtilsImpl.getClideProperties(Thread.currentThread().getContextClassLoader().getResource(center + ".properties").getPath());
        final String uriString = properties.getProperty("serverURI");
        final String destinationPath = properties.getProperty("clientDownloadDirectory");
        final String processedPath = properties.getProperty("clientProcessedDirectory");
        final String privateKey = properties.getProperty("privateKey");
        final String encrypt = properties.getProperty("clideEnableEncryption");
        final String internalLogging = properties.getProperty("");
        final String timeout = properties.getProperty("timeoutInSeconds");
        final String validate = properties.getProperty("forceValidate");
        final String diskSpaceThreshold = properties.getProperty("diskSpaceThreshold");
        final String noSpaceEmailTo = properties.getProperty("noSpaceEmailTo");
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
        final ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),Executors.newCachedThreadPool()));
        bootstrap.setPipelineFactory(serverFactory);
        client.setBootstrap(bootstrap);
        client.setFactory(clientFactory);
        return clientContext;
    }

    public void ClideServerSetUp(ClideServer server, String uriString) throws URISyntaxException {
        final String publicKey = CLIDE_SAMPLES_PATH + "outcomes.public.key";
        final String archiveDir = CLIDE_SAMPLES_PATH + "to_send";
        final String workingDir = CLIDE_SAMPLES_PATH + "server_working";
        final String sentDir = CLIDE_SAMPLES_PATH + "sent";
        
        final URI uri = new URI(uriString);
        final String host = uri.getHost() == null ? "localhost" : uri.getHost();
        final int port = uri.getPort() == -1 ? 80 : uri.getPort();
        ServerContext.setAddress(host);
        ServerContext.setPort(port);
        clideUtils.cleanUpDirectory(workingDir);
        ServerContext.setDownloadDir(clideUtils.validateServerArchiveDirectory(archiveDir));
        ServerContext.setWorkingDir(clideUtils.validateServerWorkingDirectory(workingDir, archiveDir));
        ServerContext.setSentDir(clideUtils.validateServerSentDirectory(sentDir, archiveDir));
        ServerContext.setInternalLogging(clideUtils.validateServerInternalLogging("false"));
        ServerContext.setPublicKey(clideUtils.validatePublicKey(publicKey));
        ServerContext.setTimeout(clideUtils.validateTimeout("60"));
        ServerContext.setServerOnWindows(clideUtils.validateServerOnWindows("true"));
        ServerContext.setFactory(serverFactory);
        ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));
        bootstrap.setPipelineFactory(serverFactory);
        server.setBootstrap(bootstrap);
        server.setFactory(serverFactory);
    }
}
