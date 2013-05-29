/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;

import java.net.URI;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

/**
 * Client for testing WebSocket servers.
 * 
 * @author nichollsmc
 */
public class WebSocketTestClient implements SmartLifecycle {

    private static final Logger               log = LoggerFactory.getLogger(WebSocketTestClient.class);

    @Inject
    private WebSocketTestClientHandler        webSocketTestClientHandler;
    
    private ChannelInitializer<SocketChannel> clientChannelInitializer;
    private URI                               uri;
    private Bootstrap                         bootstrap;
    private EventLoopGroup                    eventLoopGroup;
    private Channel                           channel;
    private volatile boolean                  isRunning = false;
    
    public WebSocketTestClient(final URI uri, 
                               final ChannelInitializer<SocketChannel> clientChannelInitializer) {
        this.uri = uri;
        this.clientChannelInitializer = clientChannelInitializer;
        this.bootstrap = createClientBootstrap();
        log.info("\n\n\tInitialized WebSocket client with URI " + uri + "\n");
    }

    public Bootstrap createClientBootstrap() {
        eventLoopGroup = new NioEventLoopGroup();
        return new Bootstrap()
                    .group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(clientChannelInitializer)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
    }

    @Override
    public void start() {
        log.info("Connecting WebSocket client using URI [" + uri + "]");
        
        String protocol = uri.getScheme();
        if (!"ws".equals(protocol)) {
            throw new IllegalArgumentException("Unsupported protocol: " + protocol);
        }
        
        try {
            channel = bootstrap.connect(uri.getHost(), uri.getPort()).sync().channel();
            webSocketTestClientHandler.handshakeFuture().sync();
            isRunning = true;
            
            log.info("WebSocket client connected.");
            
            channel.closeFuture().sync();
        }
        catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
    
    public void disconnect() {
        log.info("Disconnecting WebSocket client...");
        
        channel.write(new CloseWebSocketFrame());
        eventLoopGroup.shutdownGracefully();
        isRunning = false;
    
        log.info("WebSocket client disconnected.");
    }
    
    @Override
    public void stop() {
        disconnect();
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
        disconnect();
    }

    /**
     * @return the channel
     */
    public Channel getChannel() {
        return channel;
    }
    
}
