/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket;

import gov.nih.nci.ncicb.tcga.dcc.io.server.NettyServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of an {@link NettyServer} for handling WebSocket
 * (http://tools.ietf.org/html/rfc6455) requests.
 * 
 * @author nichollsmc
 */
public class WebSocketServer implements NettyServer {

    private static final Logger               log       = LoggerFactory.getLogger(WebSocketServer.class);

    private ChannelInitializer<SocketChannel> channelInitializer;
    private InetSocketAddress                 inetSocketAddress;
    private ServerBootstrap                   serverBootstrap;
    private EventLoopGroup                    bossGroup;
    private EventLoopGroup                    workerGroup;
    private Channel                           channel;
    private volatile boolean                  isRunning = false;
    
    /**
     * Constructor used for creating a {@link WebSocketServer} with the provided
     * hostname and port.
     * 
     * @param hostname
     *            the hostname to bind to
     * @param port
     *            the port to bind to
     * @param channelInitializer
     *            the {@link ChannelInitializer} for initializing the server
     *            pipeline
     */
    public WebSocketServer(final String hostname,
                           final int port,
                           final ChannelInitializer<SocketChannel> channelInitializer) {
        this.inetSocketAddress = new InetSocketAddress(hostname, port);
        this.channelInitializer = channelInitializer;
        this.serverBootstrap = createServerBootstrap();
    }
    
    @Override
    public ServerBootstrap createServerBootstrap() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        
        return new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(channelInitializer)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
    }

    @Override
    public void start() {
        start(getInetSocketAddress());
    }
    
    @Override
    public void start(InetSocketAddress inetSocketAddress) {
        try {
            log.info("Starting WebSocket server on host [" + inetSocketAddress.getHostName() + "] and port ["
                    + inetSocketAddress.getPort() + "]...");
            
            channel = serverBootstrap.bind(inetSocketAddress).sync().channel();
            isRunning = true;
            
            log.info("WebSocket server started.");
            channel.closeFuture().sync();
        }
        catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
    
    @Override
    public void stop() {
        log.info("Stopping WebSocket server...");
        
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        isRunning = false;
        
        log.info("WebSocket server stopped.");
    }
    
    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public int getPhase() {
        return 1;
    }
    
    @Override
    public boolean isAutoStartup() {
        return false;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
    }

    @Override
    public void setInetSocketAddress(InetSocketAddress inetSocketAddress) {
        this.inetSocketAddress = inetSocketAddress;
    }

    @Override
    public InetSocketAddress getInetSocketAddress() {
        return inetSocketAddress;
    }
    
}
