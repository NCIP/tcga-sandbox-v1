package gov.nih.nci.ncicb.tcga.dcc.clide.common;

import gov.nih.nci.ncicb.tcga.dcc.clide.client.ClientContext;

public class ClideContextHolder {

    private static final ThreadLocal<ClientContext> contextHolder = 
    	new ThreadLocal<ClientContext>();

    public static void setClientContext(ClientContext ctx) {
    	contextHolder.set(ctx);
    }

    public static ClientContext getClientContext() {
        return contextHolder.get();
    }

    public static void clearContext() {
    	contextHolder.remove();
    }
	
}
