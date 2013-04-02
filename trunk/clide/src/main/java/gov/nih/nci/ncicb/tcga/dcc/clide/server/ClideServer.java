/*
 * Software License, Version 1.0 Copyright 2010 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide.server;

import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideUtils;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.HttpEntityLifecycle;
import gov.nih.nci.ncicb.tcga.dcc.common.annotations.TCGAValue;
import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Log4JLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;

/**
 * Clide's HTTP server allows the DCC to download archives
 *
 * @author Jon Whitmore Last updated by: $
 * @version $
 */

@Component
public class ClideServer implements HttpEntityLifecycle {

    @Autowired
    private ClideUtils clideUtils;

    @Autowired
    private ClideServerPipelineFactory factory;

    protected final Logger logger = Logger.getLogger(ClideServer.class);

    /**
     * If an instance was created off the command line we'll keep track of it with this member so that our command line
     * test can shut it down
     */
    private static ClideServer commandLineServer;
    private ServerBootstrap bootstrap;
    private boolean hasStarted = false;

    private String uriString;
    private String archiveDir;
    private String workingDir;
    private String sentDir;
    private String publicKey;
    private String internalLogging;
    private String timeout;
    private String serverOnWindows;

    public static ClideServer getCommandLineInstance() {
        return commandLineServer;
    }

    public void ClideServerSetUp() throws URISyntaxException {

        final URI uri = new URI(uriString);
        final String host = uri.getHost() == null ? "localhost" : uri.getHost();
        final int port = uri.getPort() == -1 ? 80 : uri.getPort();
        ServerContext.setAddress(host);
        ServerContext.setPort(port);
        // check if there are file in a working directory and if they are clean them
        clideUtils.cleanUpDirectory(workingDir);
        ServerContext.setDownloadDir(clideUtils.validateServerArchiveDirectory(archiveDir));
        ServerContext.setWorkingDir(clideUtils.validateServerWorkingDirectory(workingDir, archiveDir));
        ServerContext.setSentDir(clideUtils.validateServerSentDirectory(sentDir, archiveDir));
        ServerContext.setInternalLogging(clideUtils.validateServerInternalLogging(internalLogging));
        ServerContext.setPublicKey(clideUtils.validatePublicKey(publicKey));
        ServerContext.setTimeout(clideUtils.validateTimeout(timeout));
        ServerContext.setServerOnWindows(clideUtils.validateServerOnWindows(serverOnWindows));
        ServerContext.setFactory(factory);
        // Configure the server.
        bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));
//        uncomment these performance options as needed.  Use of these is inconclusive locally
//        bootstrap.setOption("child.tcpNoDelay", true);
//        bootstrap.setOption("child.keepAlive", true);
//        bootstrap.setOption("child.reuseAddress", true);
//        bootstrap.setOption("child.connectTimeoutMillis", 5000);

        //Tell netty to use Log4J
        if (ServerContext.getInternalLogging()) {
            InternalLoggerFactory.setDefaultFactory(new Log4JLoggerFactory());
        }

        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(factory);
    }

    /**
     * Fire up the server by binding to our socket.
     */
    synchronized public void start() {
        if (!hasStarted) {
            String address = ServerContext.getAddress();
            int port = ServerContext.getPort();
            // Bind and start to accept incoming connections.
            Channel chan = bootstrap.bind(new InetSocketAddress(address, port));
            ServerContext.addChannel(chan);
            hasStarted = true;
            logger.info("Server started at " + address + " on port " + port);
        }

    }

    /**
     * Shutdown executor threads and exit.  If the server is busy this might be messy. Don't call this in the middle of
     * a download!
     */
    synchronized public void stop() {
        if (hasStarted) {
            logger.info("Begin server shutdown");
            ServerContext.closeChannels().awaitUninterruptibly();
            factory.stopTimeoutTimer();
            bootstrap.releaseExternalResources();
            logger.info("Server shutdown complete");
        }

    }

    public void setClideUtils(ClideUtils clideUtils) {
        this.clideUtils = clideUtils;
    }

    @TCGAValue(key = "timeoutInSeconds")
    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    @TCGAValue(key = "serverURI")
    public void setUriString(String uriString) {
        this.uriString = uriString;
    }

    @TCGAValue(key = "serverArchiveDirectory")
    public void setArchiveDir(String archiveDir) {
        this.archiveDir = archiveDir;
    }

    @TCGAValue(key = "serverWorkingDirectory")
    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    @TCGAValue(key = "serverSentDirectory")
    public void setSentDir(String sentDir) {
        this.sentDir = sentDir;
    }

    @TCGAValue(key = "publicKey")
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @TCGAValue(key = "serverInternalLogging")
    public void setInternalLogging(String internalLogging) {
        this.internalLogging = internalLogging;
    }

    @TCGAValue(key = "serverOnWindows")
    public void setServerOnWindows(String serverOnWindows) {
        this.serverOnWindows = serverOnWindows;
    }

    public void setBootstrap(ServerBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public void setFactory(ClideServerPipelineFactory factory) {
        this.factory = factory;
    }
    
}//End of Class
