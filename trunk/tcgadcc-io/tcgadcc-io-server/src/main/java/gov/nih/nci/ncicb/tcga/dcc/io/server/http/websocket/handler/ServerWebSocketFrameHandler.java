package gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.handler;

import gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.WebSocketFrame;
import gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.event.DefaultEvent;
import gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.event.WebSocketEvent;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class ServerWebSocketFrameHandler extends AbstractWebSocketEventHandler<WebSocketEvent> {
    
    @Override
    protected void onEvent(WebSocketEvent webSocketEvent) throws Exception {
        final ChannelHandlerContext channelHandlerContext = webSocketEvent.getChannelHandlerContext();
        final WebSocketFrame webSocketFrame = webSocketEvent.getWebSocketFrame();
        
        switch (webSocketFrame.type()) {
            case TEXT:
                channelHandlerContext.channel()
                .write(new TextWebSocketFrame(webSocketFrame.getTextWebSocketFrame().text().toUpperCase()));
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
