/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.handler;

import gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.WebSocketFrame;
import gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.event.WebSocketEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

import com.lmax.disruptor.EventTranslator;

public class InboundMessageTranslator implements EventTranslator<WebSocketEvent> {

    private ChannelHandlerContext     channelHandlerContext;
    private Object                    inboundMessage;
    private WebSocketServerHandshaker handshaker;

    public InboundMessageTranslator(ChannelHandlerContext channelHandlerContext,
                                    Object inboundMessage,
                                    WebSocketServerHandshaker handshaker) {
        this.channelHandlerContext = channelHandlerContext;
        this.inboundMessage = inboundMessage;
        this.handshaker = handshaker;
    }

    @Override
    public void translateTo(WebSocketEvent webSocketEvent, long sequence) {
        webSocketEvent.setChannelHandlerContext(channelHandlerContext);
        webSocketEvent.setWebSocketFrame(new WebSocketFrame(inboundMessage));
        webSocketEvent.setHandshaker(handshaker);
    }
     
}
