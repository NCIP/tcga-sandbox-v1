/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.api.endpoint;

import gov.nih.nci.ncicb.tcga.dcc.io.api.rpc.RpcContext;

import org.vertx.java.core.json.JsonObject;

public class RpcTestEnpoint implements Endpoint {
    
    public JsonObject sum(RpcContext context, int a, int b) {
        return new JsonObject().putNumber("result", a + b);
    }

    public JsonObject diff(RpcContext context, int a, int b) {
        return new JsonObject().putNumber("result", a - b);
    }

    public JsonObject multiply(RpcContext context, int a, int b) {
        return new JsonObject().putNumber("result", a * b);
    }
    
}
