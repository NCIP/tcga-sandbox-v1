/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javolution.util.FastTable;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;

public class WebSocketTestClientHandler extends ChannelInboundMessageHandlerAdapter<Object> {

    private static final Logger log = LoggerFactory.getLogger(WebSocketTestClientHandler.class);

    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise                  handshakeFuture;
    private FastTable<String>               receivedMessages = new FastTable<String>();

    public WebSocketTestClientHandler(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }
    
    @Override
    public void messageReceived(ChannelHandlerContext channelHandlerContext, Object inboundMessage) throws Exception {
        Channel channel = channelHandlerContext.channel();
        if (!handshaker.isHandshakeComplete()) {
            handshaker.finishHandshake(channel, (FullHttpResponse) inboundMessage);
            log.info("WebSocket Client connected!");
            handshakeFuture.setSuccess();
            return;
        }

        if (inboundMessage instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) inboundMessage;
            throw new Exception("Unexpected FullHttpResponse (getStatus=" + response.getStatus() + ", content="
                    + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        WebSocketFrame frame = (WebSocketFrame) inboundMessage;
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            String frameText = textFrame.text();
            log.info("WebSocket Client received message: " + frameText);
            receivedMessages.add(frameText);
        }
        else if (frame instanceof PongWebSocketFrame) {
            log.info("WebSocket Client received pong");
        }
        else if (frame instanceof CloseWebSocketFrame) {
            log.info("WebSocket Client received closing");
            channel.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
        cause.printStackTrace();

        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }

        channelHandlerContext.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext channelHandlerContext) throws Exception {
        handshakeFuture = channelHandlerContext.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
        handshaker.handshake(channelHandlerContext.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {
        log.info("WebSocket Client disconnected!");
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }
    
    /**
     * @return the receivedMessages
     */
    public FastTable<String> getReceivedMessages() {
        return receivedMessages;
    }

}
