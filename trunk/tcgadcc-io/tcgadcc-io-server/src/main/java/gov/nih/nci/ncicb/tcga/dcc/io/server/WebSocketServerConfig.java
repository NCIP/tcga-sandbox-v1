/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server;

import static gov.nih.nci.ncicb.tcga.dcc.io.api.IoApiConfigProfileType.TLS_ENABLED_PROFILE_NAME;
import static gov.nih.nci.ncicb.tcga.dcc.io.api.IoUriComponent.WEBSOCKET_CONTEXT_PATH;
import static gov.nih.nci.ncicb.tcga.dcc.io.api.IoUriComponent.WEBSOCKET_URI_SCHEME;
import static gov.nih.nci.ncicb.tcga.dcc.io.api.IoUriComponent.WEBSOCKET_URI_SCHEME_SECURE;
import static gov.nih.nci.ncicb.tcga.dcc.io.api.event.EventType.WEBSOCKET;
import static gov.nih.nci.ncicb.tcga.dcc.io.api.event.WebSocketEvent.EndpointType.SERVER;
import static gov.nih.nci.ncicb.tcga.dcc.io.api.event.util.WebSocketEventBuilder.webSocketEvent;
import gov.nih.nci.ncicb.tcga.dcc.io.api.event.EventBus;
import gov.nih.nci.ncicb.tcga.dcc.io.api.event.WebSocketEvent;
import gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.WebSocketServer;
import gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.handler.ServerWebSocketFrameHandler;
import gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.handler.WebSocketServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;

import java.net.URI;
import java.util.UUID;

import javax.inject.Inject;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.util.UriComponentsBuilder;

import com.lmax.disruptor.EventFactory;

/**
 * Java-based Spring configuration for WebSocket I/O components and services.
 * 
 * @author nichollsmc
 */
@Configuration
public class WebSocketServerConfig {
    
    @Value("${eventBus.ringSize}")
    private int ringSize;

    @Value("${eventBus.threadPool.minProcessors}")
    private int threadPoolMinProcessors;

    @Value("${eventBus.threadPool.minProcessors.scaleFactor}")
    private int threadPoolMinProcessorsScaleFactor;
    
    @Value("${http.server.host}")
    private String websocketServerHost;
    
    @Value("${http.server.port}")
    private int websocketServerPort;
    
    @Value("${http.server.port.secure}")
    private int websocketServerSecurePort;
    
    @Value("${websocket.buffer.size.max}")
    private int websocketBufferSizeMax;
    
    @Inject
    private Environment environment;
    
    @Inject
    private ApplicationContext applicationContext;
    
    /**
     * Default no-arg constructor. A requirement for classes annotated with
     * {@link Configuration}.
     */
    public WebSocketServerConfig() {
    }
    
    @Bean
    public WebSocketServer webSocketServer() {
        URI webSocketUri = webSocketUri();
        return new WebSocketServer(
                webSocketUri.getHost(),
                webSocketUri.getPort(),
                serverChannelInitializer());
    }
    
    @Bean
    public ChannelInitializer<SocketChannel> serverChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline channelPipeline = socketChannel.pipeline();
                
                if (environment.acceptsProfiles(TLS_ENABLED_PROFILE_NAME)) {
                    SSLContext sslContext = applicationContext.getBean(SSLContext.class);
                    SSLEngine sslEngine = sslContext.createSSLEngine();
                    sslEngine.setUseClientMode(false);
                    channelPipeline.addLast("ssl", new SslHandler(sslEngine));
                }
                
                channelPipeline.addLast("codec-http", new HttpServerCodec());
                channelPipeline.addLast("aggregator", new HttpObjectAggregator(websocketBufferSizeMax));
                channelPipeline.addLast("handler", new WebSocketServerHandler(webSocketEventBus(), webSocketUri()));
            }
        };
    }
    
    @Bean
    public EventBus<WebSocketEvent> webSocketEventBus() {
        // Create an factory for creating events
        final EventFactory<WebSocketEvent> eventFactory = new EventFactory<WebSocketEvent>() {
            @Override
            public WebSocketEvent newInstance() {
                return webSocketEvent()
                        .id(SERVER.label() + '-' + UUID.randomUUID())
                        .type(WEBSOCKET)
                        .timestamp(System.currentTimeMillis())
                        .build();
            }
        };
        
        // Create and initialize the event bus for handling WebSocket specific events
        final EventBus<WebSocketEvent> webSocketEventBus = new EventBus<WebSocketEvent>(
                ringSize,
                threadPoolMinProcessors,
                threadPoolMinProcessorsScaleFactor,
                eventFactory);
        
        // Define the DSL for handling WebSocket events
        webSocketEventBus.handleEventsWith(new ServerWebSocketFrameHandler());
        
        return webSocketEventBus;
    }
    
    @Bean
    public URI webSocketUri() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(WEBSOCKET_CONTEXT_PATH.componentValue());

        if (environment.acceptsProfiles(TLS_ENABLED_PROFILE_NAME)) {
            builder.scheme(WEBSOCKET_URI_SCHEME_SECURE.componentValue());
        }
        else {
            builder.scheme(WEBSOCKET_URI_SCHEME.componentValue());
        }

        return builder.host(websocketServerHost).build().toUri();
    }
    
}
