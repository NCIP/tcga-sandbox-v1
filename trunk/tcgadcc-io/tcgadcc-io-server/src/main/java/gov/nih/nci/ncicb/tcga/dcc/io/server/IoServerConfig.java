/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server;

import gov.nih.nci.ncicb.tcga.dcc.io.api.IoApiConfigProfileType;
import gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.WebSocketServer;
import gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.event.WebSocketEventBus;
import gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.handler.WebSocketServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;

import javax.inject.Inject;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Java-based Spring configuration for common server I/O components and
 * services.
 * 
 * @author nichollsmc
 */
@Configuration
public class IoServerConfig {

    /**
     * The default server port to use if not explicitly by the
     * configuration property 'websocket.server.port'.
     */
    public static final int DEFAULT_WS_SERVER_PORT = 8080;
    
    @Value("${websocket.server.host}")
    private String websocketServerHost;

    @Value("${websocket.server.port}")
    private Integer websocketServerPort;
    
    /**
     * Default no-arg constructor. A requirement for classes annotated with
     * {@link Configuration}.
     */
    public IoServerConfig() {
    }

    @Configuration
    @Profile(IoApiConfigProfileType.TLS_ENABLED_PROFILE_NAME)
    static class TlsEnabled {

        @Value("${websocket.buffer.size.max}")
        private Integer websocketBufferSizeMax;

        @Inject
        private SSLContext sslContext;
        
        @Inject
        private WebSocketEventBus webSocketEventBus;

        @Bean
        public ChannelInitializer<SocketChannel> channelInitializer() {
            return new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline channelPipeline = socketChannel.pipeline();
                    SSLEngine sslEngine = sslContext.createSSLEngine();
                    sslEngine.setUseClientMode(false);
                    
                    channelPipeline.addLast("ssl", new SslHandler(sslEngine));
                    channelPipeline.addLast("codec-http", new HttpServerCodec());
                    channelPipeline.addLast("aggregator", new HttpObjectAggregator(websocketBufferSizeMax));
                    channelPipeline.addLast("handler", new WebSocketServerHandler(webSocketEventBus));
                }
            };
        }
    }

    @Configuration
    @Profile(IoApiConfigProfileType.TLS_DISABLED_PROFILE_NAME)
    static class TlsDisabled {
        
        @Value("${websocket.buffer.size.max}")
        private Integer websocketBufferSizeMax;

        @Inject
        private WebSocketEventBus webSocketEventBus;
        
        @Bean
        public ChannelInitializer<SocketChannel> channelInitializer() {
            return new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline channelPipeline = socketChannel.pipeline();
                    channelPipeline.addLast("codec-http", new HttpServerCodec());
                    channelPipeline.addLast("aggregator", new HttpObjectAggregator(websocketBufferSizeMax));
                    channelPipeline.addLast("handler", new WebSocketServerHandler(webSocketEventBus));
                }
            };
        }
    }
    
    @Bean
    public WebSocketEventBus webSocketEventBus() {
        return new WebSocketEventBus();
    }
    
    @Bean
    public WebSocketServer webSocketServer() {
        if (websocketServerPort != null) {
            return new WebSocketServer(websocketServerPort);
        }
        else {
            return new WebSocketServer(DEFAULT_WS_SERVER_PORT);
        }
    }

}
