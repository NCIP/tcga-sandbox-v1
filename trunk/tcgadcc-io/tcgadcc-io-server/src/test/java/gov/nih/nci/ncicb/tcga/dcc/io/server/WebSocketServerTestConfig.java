/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server;

import static gov.nih.nci.ncicb.tcga.dcc.io.api.IoUriComponent.WEBSOCKET_CONTEXT_PATH;
import static gov.nih.nci.ncicb.tcga.dcc.io.api.IoUriComponent.WEBSOCKET_URI_SCHEME;
import gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.WebSocketTestClient;
import gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.WebSocketTestClientHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.NetUtil;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Java-based Spring configuration for testing server I/O components and services.
 * 
 * @author nichollsmc
 */
@Configuration
@Import(WebSocketServerConfig.class)
public class WebSocketServerTestConfig {
    
    @Value("${websocket.buffer.size.max}")
    private int websocketBufferSizeMax;
    
    /**
     * Default no-arg constructor. A requirement for classes annotated with
     * {@link Configuration}.
     */
    public WebSocketServerTestConfig() {
    }
    
    @Bean
    @DependsOn("webSocketServer")
    public WebSocketTestClient webSocketTestClient() {
        return new WebSocketTestClient(webSocketUri(), clientChannelInitializer());
    }
    
    @Bean
    public ChannelInitializer<SocketChannel> clientChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("http-codec", new HttpClientCodec());
                pipeline.addLast("aggregator", new HttpObjectAggregator(websocketBufferSizeMax));
                pipeline.addLast("handler", webSocketTestClientHandler());
            }
        };
    }

    @Bean
    public WebSocketTestClientHandler webSocketTestClientHandler() {
        return new WebSocketTestClientHandler(WebSocketClientHandshakerFactory
                .newHandshaker(webSocketUri(), WebSocketVersion.V13, null, false, new DefaultHttpHeaders()));
    }
    
    @Bean
    @Primary
    public URI webSocketUri() {
        return UriComponentsBuilder
                .fromPath(WEBSOCKET_CONTEXT_PATH.componentValue())
                .scheme(WEBSOCKET_URI_SCHEME.componentValue())
                .host(NetUtil.LOCALHOST.getHostName())
                .port(TestUtils.getFreePort())
                .build()
                .toUri();
    }
    
}
