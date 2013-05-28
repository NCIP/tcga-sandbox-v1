/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.api.event;

import static gov.nih.nci.ncicb.tcga.dcc.io.api.event.EventType.WEBSOCKET;
import gov.nih.nci.ncicb.tcga.dcc.io.api.http.websocket.WebSocketFrame;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

/**
 * Specialized implementation of an {@link Event} type for handling WebSocket
 * events.
 * 
 * @author nichollsmc
 */
public class WebSocketEvent extends GenericEvent {
    
    private ChannelHandlerContext     channelHandlerContext;
    private WebSocketServerHandshaker handshaker;
    private WebSocketFrame            webSocketFrame;
    
    /**
     * @return the channelHandlerContext
     */
    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    /**
     * @param channelHandlerContext
     *            the channelHandlerContext to set
     */
    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }
    
    /**
     * @return the handshaker
     */
    public WebSocketServerHandshaker getHandshaker() {
        return handshaker;
    }

    /**
     * @param handshaker the handshaker to set
     */
    public void setHandshaker(WebSocketServerHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    /**
     * @return the webSocketFrame
     */
    public WebSocketFrame getWebSocketFrame() {
        return webSocketFrame;
    }

    /**
     * @param webSocketFrame the webSocketFrame to set
     */
    public void setWebSocketFrame(WebSocketFrame webSocketFrame) {
        this.webSocketFrame = webSocketFrame;
    }
    
    /**
     * WebSocket endpoint types. 
     */
    public static enum EndpointType {
        CLIENT("client-"),
        SERVER("server-");
        
        private String labelPrefix;
        
        private EndpointType(String labelPrefix) {
            this.labelPrefix = labelPrefix;
        }
        
        /**
         * @return string representing the endpoint type name
         */
        public String label() {
            return labelPrefix + WEBSOCKET.label();
        }
    }
    
}
