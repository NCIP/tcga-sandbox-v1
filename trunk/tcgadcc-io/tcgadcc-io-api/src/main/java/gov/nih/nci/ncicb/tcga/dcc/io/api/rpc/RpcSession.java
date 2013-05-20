/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.api.rpc;

import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.ServerWebSocket;

public class RpcSession {

    private Vertx vertx;
    private ServerWebSocket webSocket;

    public RpcSession(Vertx vertx, ServerWebSocket webSocket) {
        this.vertx = vertx;
        this.webSocket = webSocket;
    }

    public Vertx vertx() {
        return vertx;
    }

    public ServerWebSocket webSocket() {
        return webSocket;
    }
}
