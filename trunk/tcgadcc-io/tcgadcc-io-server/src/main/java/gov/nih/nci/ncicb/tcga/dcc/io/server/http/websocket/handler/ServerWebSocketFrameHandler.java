/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.handler;

import java.net.SocketAddress;

import gov.nih.nci.ncicb.tcga.dcc.io.api.event.WebSocketEvent;
import gov.nih.nci.ncicb.tcga.dcc.io.api.event.handler.AbstractEventHandler;
import gov.nih.nci.ncicb.tcga.dcc.io.api.http.websocket.WebSocketFrame;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * Implementation of an {@link AbstractEventHandler} for processing server-side
 * {@link WebSocketFrame}s.
 * 
 * @author nichollsmc
 */
public class ServerWebSocketFrameHandler extends AbstractEventHandler<WebSocketEvent> {
    
    @Override
    protected void onEvent(WebSocketEvent webSocketEvent) throws Exception {
        final ChannelHandlerContext channelHandlerContext = webSocketEvent.getChannelHandlerContext();
        final WebSocketFrame webSocketFrame = webSocketEvent.getWebSocketFrame();
        
        switch (webSocketFrame.type()) {
            case TEXT:
                String text = webSocketFrame.getTextWebSocketFrame().text();
                System.out.println("\n\n\ttext payload > " + text + "\n");
                channelHandlerContext.channel()
                .write(new TextWebSocketFrame(text.toUpperCase()));
                break;
            case CLOSE:
                webSocketEvent.getHandshaker()
                .close(channelHandlerContext.channel(), webSocketFrame.getCloseWebSocketFrame());
                return;
            case PING:
                channelHandlerContext.channel()
                .write(new PongWebSocketFrame(webSocketFrame.getPingWebSocketFrame().content()));
                return;
            default:
                throw new IllegalStateException("Unsupported WebSocket frame type: [" + webSocketFrame + ']');
        }
    }

}
