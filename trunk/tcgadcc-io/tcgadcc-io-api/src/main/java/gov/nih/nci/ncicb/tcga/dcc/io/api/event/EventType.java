/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.api.event;

/**
 * Enumerates the supported event types.
 * 
 * @author nichollsmc
 */
public enum EventType {
    
    DEFAULT("default"),
    
    WEBSOCKET("websocket");
    
    private String label;

    private EventType(String label) {
        this.label = label;
    }
    
    public String label() {
        return label;
    }
}
