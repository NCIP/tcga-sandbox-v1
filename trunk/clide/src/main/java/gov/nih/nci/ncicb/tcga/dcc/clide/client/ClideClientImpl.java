/*
 * Software License, Version 1.0 Copyright 2011 SRA International, Inc.
 * Copyright Notice.  The software subject to this notice and license includes both human
 * readable source code form and machine readable, binary, object code form (the "caBIG
 * Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.clide.client;

import gov.nih.nci.ncicb.tcga.dcc.clide.clientmanager.ClideClientManager;
import gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideContextHolder;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Log4JLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.Executors;

import static gov.nih.nci.ncicb.tcga.dcc.clide.common.ClideConstants.UTF8;

/**
 * HTTP client to request files from centers running the CLIDE Server
 *
 * @author Jon Whitmore Last updated by: Stan Girshik
 * @version $Rev$
 */

@Component
@Scope("prototype")
public class ClideClientImpl implements ClideClient {
   
    @Autowired
    private ClideClientPipelineFactory factory;

	private final Logger logger = Logger.getLogger(ClideClientImpl.class);

    private ClientContext clientContext = null;

    private volatile boolean isStopped = false;

    private String clientThreadName;

    private ClientBootstrap bootstrap;
    
  
    @Override
    public ClientContext getClientContext() {
           return clientContext;
    }
    @Override  
	public void setClientContext(ClientContext context) {
		clientContext = context;
	}

    @Override
	public void run() {
        final Thread t = Thread.currentThread();
        t.setName(clientThreadName + " - " + t.getId());
		start();		
	}    
    
    /* (non-Javadoc)
	 * @see gov.nih.nci.ncicb.tcga.dcc.clide.client.ClideClient#start()
	 */
    @Override
	public void start() {
    	
    	 //Tell netty to use Log4J
        if (clientContext.getInternalLogging()) {
            InternalLoggerFactory.setDefaultFactory(new Log4JLoggerFactory());
        }
        
        // Configure the client.
        bootstrap = new ClientBootstrap(
                new NioClientSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        // uncomment these performance options as needed.  Use of these is inconclusive locally
//        bootstrap.setOption("tcpNoDelay", true);
//        bootstrap.setOption("keepAlive", true);
//        bootstrap.setOption("reuseAddress", true);
//        bootstrap.setOption("connectTimeoutMillis", 5000);

        
       

        // Set up the event pipeline factory.
        bootstrap.setPipelineFactory(factory);

        clientContext.setBootsrap(bootstrap);        
        clientContext.setFactory(factory);
        
        ClideContextHolder.setClientContext(clientContext);
        // Start the connection attempt.
        logger.log(Level.INFO, "Connecting to: " + clientContext.getUri());
        final ChannelFuture future = bootstrap.connect(
                new InetSocketAddress(clientContext.getHost(), clientContext.getPort()));

        future.addListener(new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture cf) throws Exception {
                if (cf.isSuccess()) {
                    sendFirstCommand(cf.getChannel(),clientContext);

                } else {
                    logger.log(Level.ERROR, future.getCause());
                    // client will exit naturally below

                }
            }
        });

        // blocking wait for the server to close the connection.
        // communication will now jump into the pipeline thread until someone closes
        future.getChannel().getCloseFuture().awaitUninterruptibly();
        shutdown(bootstrap, factory);

    }

    /**
     * Send the first command to the server on the given channel
     *
     * @param channel The channel created when we connected to the server
     * @param clientContext context used to store configuration values
     */
    private void sendFirstCommand(final Channel channel,final ClientContext clientContext) {    	    	
    	
        // Send the HTTP request.
        final HttpRequest request = new DefaultHttpRequest(
                HttpVersion.HTTP_1_1, HttpMethod.GET, clientContext.getUri().toASCIIString());
        request.setHeader(HttpHeaders.Names.HOST, clientContext.getHost());
        request.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);

        String command = ClientProtocolHandler.initiateCommunication(clientContext);
        request.setHeader(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(command.getBytes().length));
        request.setContent(ChannelBuffers.copiedBuffer(command, Charset.forName(UTF8)));
        channel.write(request);
    }

    /* (non-Javadoc)
	 * @see gov.nih.nci.ncicb.tcga.dcc.clide.client.ClideClient#shutdown(org.jboss.netty.bootstrap.ClientBootstrap, gov.nih.nci.ncicb.tcga.dcc.clide.client.ClideClientPipelineFactoryImpl)
	 */
    @Override
	public void shutdown(final ClientBootstrap bootstrap, final ClideClientPipelineFactory factory) {
        logger.log(Level.INFO, "Attempting to shutdown client");
        // stop the threads we created
        factory.getThroughputMonitor().stopMonitoring();

        logger.log(Level.INFO, "Shutting down timeout timer");
        factory.getTimeoutTimer().stop();

        // Shut down the client's executor threads to exit.
        bootstrap.releaseExternalResources();
        ClideClientManager.removeClideClient(clientContext.getCenter());
        logger.log(Level.INFO, "Client shutdown complete");             
    }
    

    /* (non-Javadoc)
	 * @see gov.nih.nci.ncicb.tcga.dcc.clide.client.ClideClient#startWithCommand(java.lang.String)
	 */
    @Override
	public void startWithCommand(final String command) {
        final ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(),Executors.newCachedThreadPool()));
        clientContext.setBootsrap(bootstrap);
        clientContext.setFactory(factory);
        ClideContextHolder.setClientContext(clientContext);
        if (clientContext.getInternalLogging()) {
            InternalLoggerFactory.setDefaultFactory(new Log4JLoggerFactory());
        }
        bootstrap.setPipelineFactory(factory);
        logger.log(Level.INFO, "Connecting to: " + clientContext.getUri());
        final ChannelFuture future = bootstrap.connect(
                new InetSocketAddress(clientContext.getHost(), clientContext.getPort()));
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(final ChannelFuture cf) throws Exception {
            	ClideContextHolder.setClientContext(clientContext);
                if (cf.isSuccess()) {
                    final HttpRequest request = new DefaultHttpRequest(
                            HttpVersion.HTTP_1_1, HttpMethod.GET, clientContext.getUri().toASCIIString());
                    request.setHeader(HttpHeaders.Names.HOST, clientContext.getHost());
                    request.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
                    request.setHeader(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(command.getBytes().length));
                    request.setContent(ChannelBuffers.copiedBuffer(command, Charset.forName(UTF8)));
                    cf.getChannel().write(request);
                } else {
                    logger.log(Level.ERROR, future.getCause());
                }
            }
        });
        future.getChannel().getCloseFuture().awaitUninterruptibly();
        shutdown(bootstrap, factory);
    }
    
    /**
     * This method returns localContext
     * @return
     */
    public ClientContext getLocalContext(){
    	return ClideContextHolder.getClientContext();
    }

    public ClideClientPipelineFactory getFactory() {
		return factory;
	}

	public void setFactory(ClideClientPipelineFactory factory) {
		this.factory = factory;
	}

    public ClientBootstrap getBootstrap() {
        return bootstrap;
    }

    public void setBootstrap(ClientBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public void setClientThreadName(String clientThreadName) {
        this.clientThreadName = clientThreadName;
    }
    
}//End of Class
