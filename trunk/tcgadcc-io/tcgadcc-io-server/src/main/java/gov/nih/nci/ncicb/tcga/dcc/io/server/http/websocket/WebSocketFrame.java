/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket;

import javolution.text.TextBuilder;
import io.netty.buffer.BufUtil;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public final class WebSocketFrame {

    private FrameType            frameType;
    private TextWebSocketFrame   textWebSocketFrame;
    private BinaryWebSocketFrame binaryWebSocketFrame;
    private CloseWebSocketFrame  closeWebSocketFrame;
    private PingWebSocketFrame   pingWebSocketFrame;
    private PongWebSocketFrame   pongWebSocketFrame;

    public WebSocketFrame(Object webSocketFrame) {
        
        BufUtil.retain(webSocketFrame);
        
        if (webSocketFrame instanceof TextWebSocketFrame) {
            this.frameType = FrameType.TEXT;
            this.textWebSocketFrame = (TextWebSocketFrame) webSocketFrame;
            return;
        }
        
        if (webSocketFrame instanceof BinaryWebSocketFrame) {
            this.frameType = FrameType.BINARY;
            this.binaryWebSocketFrame = (BinaryWebSocketFrame) webSocketFrame;
            return;
        }

        if (webSocketFrame instanceof CloseWebSocketFrame) {
            this.frameType = FrameType.CLOSE;
            this.closeWebSocketFrame = (CloseWebSocketFrame) webSocketFrame;
            return;
        }
        
        if (webSocketFrame instanceof PingWebSocketFrame) {
            this.frameType = FrameType.PING;
            this.pingWebSocketFrame = (PingWebSocketFrame) webSocketFrame;
            return;
        }

        if (webSocketFrame instanceof PongWebSocketFrame) {
            this.frameType = FrameType.PONG;
            this.pongWebSocketFrame = (PongWebSocketFrame) webSocketFrame;
            return;
        }

        throw new IllegalStateException("Unsupported WebSocket frame type: [" + webSocketFrame + "]");
    }

    /**
     * @return the type
     */
    public FrameType type() {
        return frameType;
    }

    /**
     * @return the textWebSocketFrame
     */
    public TextWebSocketFrame getTextWebSocketFrame() {
        return textWebSocketFrame;
    }
    
    /**
     * @return the binaryWebSocketFrame
     */
    public BinaryWebSocketFrame getBinaryWebSocketFrame() {
        return binaryWebSocketFrame;
    }

    /**
     * @return the closeWebSocketFrame
     */
    public CloseWebSocketFrame getCloseWebSocketFrame() {
        return closeWebSocketFrame;
    }
    
    /**
     * @return the pingWebSocketFrame
     */
    public PingWebSocketFrame getPingWebSocketFrame() {
        return pingWebSocketFrame;
    }

    /**
     * @return the pongWebSocketFrame
     */
    public PongWebSocketFrame getPongWebSocketFrame() {
        return pongWebSocketFrame;
    }

    @Override
    public String toString() {
        return new TextBuilder()
        .append("\n\nWebSocketFrame content:\n")
        .append("\tTEXT:   ").append(textWebSocketFrame).append("\n")
        .append("\tBINARY: ").append(binaryWebSocketFrame).append("\n")
        .append("\tCLOSE:  ").append(closeWebSocketFrame).append("\n")
        .append("\tPING:   ").append(pingWebSocketFrame).append("\n")
        .append("\tPONG:   ").append(pongWebSocketFrame).append("\n")
        .toString();
    }
    
    /**
     * List of all WebSocket frame types.
     */
    public enum FrameType {
        BINARY, CONTINUATION, CLOSE, PING, PONG, TEXT;
    }
}
