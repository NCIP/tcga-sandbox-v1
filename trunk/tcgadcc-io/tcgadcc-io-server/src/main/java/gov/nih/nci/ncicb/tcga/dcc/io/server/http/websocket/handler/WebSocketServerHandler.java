/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.handler;

import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import gov.nih.nci.ncicb.tcga.dcc.io.api.event.EventBus;
import gov.nih.nci.ncicb.tcga.dcc.io.api.event.WebSocketEvent;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;

import java.net.URI;

/**
 * Non-blocking message handler that moderates all server-side WebSocket traffic.
 * <p>
 * Upon a successful handshake, all subsequent messages received from a client
 * are published to a {@link WebSocketEvent} aware {@link EventBus} for
 * downstream processing.
 * 
 * @author nichollsmc
 */
public class WebSocketServerHandler extends ChannelInboundMessageHandlerAdapter<Object> {

    private EventBus<WebSocketEvent>  webSocketEventBus;
    private WebSocketServerHandshaker handshaker;
    private URI webSocketUri;
    
    public WebSocketServerHandler(final EventBus<WebSocketEvent> webSocketEventBus,
                                  final URI webSocketUri) {
        this.webSocketEventBus = webSocketEventBus;
        this.webSocketUri = webSocketUri;
    }

    @Override
    public void messageReceived(ChannelHandlerContext channelHandlerContext, Object inboundMessage) throws Exception {
        if (inboundMessage instanceof FullHttpRequest) {
            System.out.println("\n\n\thandling http request: " + inboundMessage + "\n");
            handleHttpRequest(channelHandlerContext, (FullHttpRequest) inboundMessage);
        }
        else if (inboundMessage instanceof io.netty.handler.codec.http.websocketx.WebSocketFrame) {
            System.out.println("\n\n\thandling WebSocket frame: " + inboundMessage + "\n");
            webSocketEventBus.publishEvent(
                    new InboundMessageTranslator(channelHandlerContext, inboundMessage, handshaker));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) throws Exception {
        cause.printStackTrace();
        channelHandlerContext.close();
    }

    /**
     * Handles inbound HTTP requests and performs the initial handshake with the
     * client.
     * 
     * @param channelHandlerContext
     *            context of the inbound message
     * @param fullHttpRequest
     *            the HTTP request from the client
     */
    protected void handleHttpRequest(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) {
        if (!fullHttpRequest.getDecoderResult().isSuccess()) {
            sendHttpResponse(channelHandlerContext, fullHttpRequest, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            return;
        }

        if (fullHttpRequest.getMethod() != GET) {
            sendHttpResponse(channelHandlerContext, fullHttpRequest, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
            return;
        }

        WebSocketServerHandshakerFactory webSocketServerHandshakerFactory = 
                new WebSocketServerHandshakerFactory(webSocketUri.toString(), null, false);
        
        handshaker = webSocketServerHandshakerFactory.newHandshaker(fullHttpRequest);

        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(channelHandlerContext.channel());
        }
        else {
            handshaker.handshake(channelHandlerContext.channel(), fullHttpRequest);
        }
    }

    /**
     * Used to send an HTTP response to a client.
     * 
     * @param channelHandlerContext
     *            the context handler for the channel pipeline
     * @param fullHttpRequest
     *            the inbound HTTP request
     * @param fullHttpResponse
     *            the outbound HTTP response
     */
    protected void sendHttpResponse(ChannelHandlerContext channelHandlerContext,
                                    FullHttpRequest fullHttpRequest,
                                    FullHttpResponse fullHttpResponse) {

        if (fullHttpResponse.getStatus() != OK) {
            fullHttpResponse.content().writeBytes(
                    Unpooled.copiedBuffer(fullHttpResponse.getStatus().toString(), CharsetUtil.UTF_8));

            setContentLength(fullHttpResponse, fullHttpResponse.content().readableBytes());
        }

        ChannelFuture channelFuture = channelHandlerContext.channel().write(fullHttpResponse);
        if (!isKeepAlive(fullHttpRequest) || fullHttpResponse.getStatus() != OK) {
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

}
