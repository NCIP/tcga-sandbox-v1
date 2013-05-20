/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server;

import gov.nih.nci.ncicb.tcga.dcc.io.api.IoApiConfig;
import gov.nih.nci.ncicb.tcga.dcc.io.server.websocket.WebSocketDispatcher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.http.HttpServer;

/**
 * Java-based Spring configuration for WebSocket server I/O components and
 * services.
 * 
 * @author nichollsmc
 */
@Configuration
@Import(IoApiConfig.class)
public class ServerIOConfig {

    @Value("${websocket.server.host}")
    private String  websocketServerHost;

    @Value("${websocket.server.port}")
    private Integer websocketServerPort;

    /**
     * Default no-arg constructor. A requirement for classes annotated with
     * {@link Configuration}.
     */
    public ServerIOConfig() {
    }

    @Bean
    public HttpServer httpServer() {
        return vertx()
                .createHttpServer()
                .websocketHandler(webSocketServerDispatcher())
                .listen(websocketServerPort);
    }

    @Bean
    public Vertx vertx() {
        return VertxFactory.newVertx();
    }

    @Bean
    public WebSocketDispatcher webSocketServerDispatcher() {
        return new WebSocketDispatcher();
    }

}
