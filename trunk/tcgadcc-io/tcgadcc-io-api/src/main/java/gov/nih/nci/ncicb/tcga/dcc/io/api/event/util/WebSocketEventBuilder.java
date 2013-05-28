/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.api.event.util;

import gov.nih.nci.ncicb.tcga.dcc.io.api.event.WebSocketEvent;

/**
 * Implementation of the fluent {@link EventBuilderBase} for building
 * {@link WebSocketEvent}s.
 * 
 * @author nichollsmc
 */
public class WebSocketEventBuilder extends EventBuilderBase<WebSocketEventBuilder> {
    
    public static WebSocketEventBuilder webSocketEvent() {
        return new WebSocketEventBuilder();
    }

    public WebSocketEventBuilder() {
        super(new WebSocketEvent());
    }

    public WebSocketEvent build() {
        return (WebSocketEvent) getInstance();
    }
    
}
