/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.event;

import com.lmax.disruptor.EventFactory;

public class WebSocketEvent extends AbstractWebSocketEvent {
    
    public static final EventFactory<WebSocketEvent> EVENT_FACTORY = new WebSocketEventFactory();
    
    private static class WebSocketEventFactory implements EventFactory<WebSocketEvent> {
        @Override
        public WebSocketEvent newInstance() {
            return new WebSocketEvent();
        }
    }

}
