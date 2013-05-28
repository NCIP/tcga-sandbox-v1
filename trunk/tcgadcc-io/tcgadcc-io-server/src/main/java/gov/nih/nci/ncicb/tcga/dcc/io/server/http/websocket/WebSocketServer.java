/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket;

import gov.nih.nci.ncicb.tcga.dcc.io.server.AbstractNettyServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of an {@link AbstractNettyServer} for handling WebSocket
 * (http://tools.ietf.org/html/rfc6455) requests.
 * 
 * @author nichollsmc
 */
public class WebSocketServer extends AbstractNettyServer {

    private static final Logger               log       = LoggerFactory.getLogger(WebSocketServer.class);

    private EventLoopGroup                    bossGroup;
    private EventLoopGroup                    workerGroup;
    private Channel                           channel;
    private volatile boolean                  isRunning = false;
    
    @Inject
    private ChannelInitializer<SocketChannel> channelInitializer;
    
    public WebSocketServer(final String host, final int port) {
        super.setInetSocketAddress(new InetSocketAddress(host, port));
    }
    
    @Override
    public ServerBootstrap createServerBootstrap() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                       .channel(NioServerSocketChannel.class)
                       .childHandler(channelInitializer);
        
        return serverBootstrap;
    }

    @Override
    public void start() {
        start(getInetSocketAddress());
        isRunning = true;
    }
    
    @Override
    public void start(InetSocketAddress inetSocketAddress) {
        try {
            log.info("Starting WebSocket server on port [" + inetSocketAddress.getPort() + "]...");
            channel = createServerBootstrap().bind(inetSocketAddress).sync().channel();
            
            log.info("WebSocket server started.");
            channel.closeFuture().sync();
        }
        catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        finally {
            stop();
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
        return 0;
    }
    
    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
        isRunning = false;
    }
    
}
