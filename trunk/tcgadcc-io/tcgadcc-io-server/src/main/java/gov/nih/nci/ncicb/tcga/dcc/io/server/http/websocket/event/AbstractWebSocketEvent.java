package gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.event;

import gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.WebSocketFrame;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

public class AbstractWebSocketEvent {

    private ChannelHandlerContext     channelHandlerContext;
    private WebSocketFrame            webSocketFrame;
    private WebSocketServerHandshaker handshaker;

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

}
