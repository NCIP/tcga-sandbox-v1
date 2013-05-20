/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server.websocket;

import gov.nih.nci.ncicb.tcga.dcc.io.api.rpc.RpcContext;


public interface ClientStateAware {
    
    public void clientConnected(RpcContext rpcContext);

    public void clientError(RpcContext rpcContext, Throwable t);

    public void clientDisconnected(RpcContext rpcContext);
    
}
