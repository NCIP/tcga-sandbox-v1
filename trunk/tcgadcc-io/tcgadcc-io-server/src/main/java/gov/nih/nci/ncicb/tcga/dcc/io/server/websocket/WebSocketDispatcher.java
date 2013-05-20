/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server.websocket;

import gov.nih.nci.ncicb.tcga.dcc.io.server.handler.DeltaEncoder;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.ServerWebSocket;
import org.vertx.java.core.streams.Pump;

public class WebSocketDispatcher implements Handler<ServerWebSocket> {

    private final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final String CONTEXT_PATH = "/io";
    public static final String DELTA_ENCODE_PATH = CONTEXT_PATH + "/delta-encode";
    
    @Value("${websocket.buffer.size}")
    private int maxBufferSize;
    
    @Override
    public void handle(ServerWebSocket serverWebSocket) {
        switch (serverWebSocket.path()) {
        case DELTA_ENCODE_PATH:
            Pump.createPump(serverWebSocket, serverWebSocket, maxBufferSize).start();
            new DeltaEncoder().handle(serverWebSocket);
            break;
        default:
            serverWebSocket.reject();
        }
    }
    
}

