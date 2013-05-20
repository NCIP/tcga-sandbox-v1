/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server.websocket;

import gov.nih.nci.ncicb.tcga.dcc.io.api.rpc.RpcContext;
import gov.nih.nci.ncicb.tcga.dcc.io.api.rpc.RpcException;
import gov.nih.nci.ncicb.tcga.dcc.io.api.rpc.RpcSession;
import gov.nih.nci.ncicb.tcga.dcc.io.api.rpc.reflect.RpcMethodRegistry;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.ServerWebSocket;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import com.google.common.collect.ObjectArrays;

public class RpcWebSocketServer implements Handler<ServerWebSocket> {

    private final static Logger               log = LoggerFactory.getLogger(RpcWebSocketServer.class);

    private List<ClientStateAware>            clientStateAwareList;
    private RpcMethodRegistry                 rpcMethodRegistry;
    private Vertx                             vertx;
    private Constructor<? extends RpcContext> contextConstructor;

    public RpcWebSocketServer(Vertx vertx) {
        this.clientStateAwareList = new ArrayList<ClientStateAware>();
        this.rpcMethodRegistry = new RpcMethodRegistry();
        this.vertx = vertx;
        this.contextConstructor = getContextConstructor(RpcContext.class);
    }

    private Constructor<? extends RpcContext> getContextConstructor(Class<? extends RpcContext> contextClass) {
        try {
            return contextClass.getConstructor(RpcSession.class);
        }
        catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(contextClass.getName()
                    + " does not have a constructor that takes an RpcSession instance", e);
        }
    }

    @Override
    public void handle(final ServerWebSocket serverWebSocket) {
        RpcSession session = new RpcSession(vertx, serverWebSocket);
        try {
            final RpcContext context = contextConstructor.newInstance(session);

            log.info("Handling new connection for client [" + context.getClientId() + "]");

            serverWebSocket.dataHandler(new Handler<Buffer>() {
                @Override
                public void handle(Buffer buffer) {
                    onDataReceived(context, serverWebSocket, buffer);
                }
            });

            serverWebSocket.endHandler(new Handler<Void>() {
                @Override
                public void handle(Void aVoid) {

                    for (ClientStateAware aware : clientStateAwareList) {
                        aware.clientDisconnected(context);
                    }

                    log.info("Client [" + context.getClientId() + "] disconnected"); 
                }
            });

            serverWebSocket.exceptionHandler(new Handler<Throwable>() {
                @Override
                public void handle(Throwable t) {
                    for (ClientStateAware aware : clientStateAwareList) {
                        aware.clientError(context, t);
                    }

                    log.error("Exception with client [" + context.getClientId() + "]", t);
                }
            });

            for (ClientStateAware aware : clientStateAwareList) {
                aware.clientConnected(context);
            }
        }
        catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    private void onDataReceived(final RpcContext context, final ServerWebSocket webSocket, final Buffer buffer) {
        JsonObject message = new JsonObject(buffer.toString());

        String method = message.getString("method");
        JsonArray params = message.getArray("params");
        Number id = message.getNumber("id");

        JsonObject reply = new JsonObject();
        if (id != null) {
            reply.putNumber("id", id);
        }

        if (method == null || params == null) {
            reply.putString("error", "Missing method/params");
            log.error("Got invalid packet with missing method or params from client [" + context.getClientId() + "]");
        }
        else {
            try {
                Object[] finalParams = ObjectArrays.concat(context, params.toArray());
                JsonObject returnVal = rpcMethodRegistry.invokeTarget(method, finalParams);
                if (returnVal != null) {
                    reply.putObject("result", returnVal);
                }
            }
            catch (RpcException e) {
                reply.putString("method", method);
                reply.putString("error", "RPC error");
                
                log.error("Exception while handling data stream from client [" + context.getClientId() + "]", e);
            }
        }

        webSocket.write(new Buffer(reply.encode()));
    }

    public void registerObject(String withName, Object object) {
        log.debug("Registered object with name [" + withName + "]");

        rpcMethodRegistry.registerObject(withName, object);

        if (object instanceof ClientStateAware) {
            log.debug("Object is client state aware [" + withName + "]");
            clientStateAwareList.add((ClientStateAware) object);
        }
    }
}