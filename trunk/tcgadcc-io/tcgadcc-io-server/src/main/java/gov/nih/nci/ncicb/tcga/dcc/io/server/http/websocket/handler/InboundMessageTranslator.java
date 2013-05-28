/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.handler;

import gov.nih.nci.ncicb.tcga.dcc.io.api.event.WebSocketEvent;
import gov.nih.nci.ncicb.tcga.dcc.io.api.http.websocket.WebSocketFrame;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

import com.lmax.disruptor.EventTranslator;

/**
 * Implementation of an {@link EventTranslator} that translates WebSocket
 * messages into {@link WebSocketEvent}s for processing by a
 * {@code WebSocketEvent} aware {@link EventBus}.
 * 
 * @author nichollsmc
 */
public class InboundMessageTranslator implements EventTranslator<WebSocketEvent> {

    private ChannelHandlerContext     channelHandlerContext;
    private Object                    inboundMessage;
    private WebSocketServerHandshaker handshaker;

    public InboundMessageTranslator(final ChannelHandlerContext channelHandlerContext,
                                    final Object inboundMessage,
                                    final WebSocketServerHandshaker handshaker) {
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
