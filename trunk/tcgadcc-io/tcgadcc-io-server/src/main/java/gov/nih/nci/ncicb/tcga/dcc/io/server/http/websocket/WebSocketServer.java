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

public class WebSocketServer extends AbstractNettyServer {

    public static final String                SCHEME       = "ws://";
    public static final String                CONTEXT_PATH = "/io";

    private static final Logger               log          = LoggerFactory.getLogger(WebSocketServer.class);
    private volatile boolean                  isRunning    = false;

    private EventLoopGroup                    bossGroup;
    private EventLoopGroup                    workerGroup;
    private Channel                           channel;
    
    @Inject
    private ChannelInitializer<SocketChannel> channelInitializer;
    
    public WebSocketServer(int port) {
        setPort(port);
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
        isRunning = true;
        start(getPort());
    }
    
    @Override
    public void stop() {
        isRunning = false;
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
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
    public void start(int port) {
        start(new InetSocketAddress(port));
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        isRunning = false;
        stop();
    }
    
    @Override
    public void start(InetSocketAddress inetSocketAddress) {
        try {
            log.info("Starting WebSocket server on port [" + port + ']');
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

}
