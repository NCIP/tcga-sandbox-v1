/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.api.rpc;


public class RpcContext {

//    protected String clientId;
//    protected Vertx  vertx;
//
//    public RpcContext(RpcSession session) {
//        this.vertx = session.vertx();
//        this.clientId = session.webSocket().binaryHandlerID();
//    }
//
//    public String getClientId() {
//        return clientId;
//    }
//
//    public void notification(List<String> clients, String method, JsonObject params) {
//        JsonObject message = wrapNotification(method, params);
//        Buffer buffer = new Buffer(message.encode());
//        for (String client : clients) {
//            vertx.eventBus().send(client, buffer);
//        }
//    }
//
//    public void notification(String client, String method, JsonObject params) {
//        JsonObject message = wrapNotification(method, params);
//        Buffer buffer = new Buffer(message.encode());
//        vertx.eventBus().send(client, buffer);
//    }
//
//    private JsonObject wrapNotification(String method, JsonObject params) {
//        JsonObject message = new JsonObject();
//        message.putString("method", method);
//        message.putObject("params", params);
//        
//        return message;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        
//        if (o == null || getClass() != o.getClass()) {
//            return false;
//        }
//
//        RpcContext that = (RpcContext) o;
//
//        if (!clientId.equals(that.clientId)) {
//            return false;
//        }
//
//        return true;
//    }
//
//    @Override
//    public int hashCode() {
//        return clientId.hashCode();
//    }

}
