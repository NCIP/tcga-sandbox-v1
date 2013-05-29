package gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket;

import io.netty.buffer.MessageBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.channel.ChannelOperationHandlerAdapter;
import io.netty.channel.ChannelOutboundMessageHandler;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedMessageChannel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty WebSocket test utilities.
 * 
 * @author nichollsmc
 */
public class NettyWebSocketTestUtil {

    private static final Logger log = LoggerFactory.getLogger(NettyWebSocketTestUtil.class);
    
    public static EmbeddedMessageChannel createChannel() {
        return createChannel(null);
    }

    public static EmbeddedMessageChannel createChannel(ChannelHandler handler) {
        return new EmbeddedMessageChannel(
                new WebSocketServerProtocolHandler("ws://ws-client.org/test", null, false),
                //new HttpRequestDecoder(),
                new HttpResponseEncoder(),
                new MockOutboundHandler(),
                handler,
                new MockOutboundHandler());
    }

    public static void writeUpgradeRequest(EmbeddedMessageChannel ch) {
        ch.writeInbound(WebSocketRequestBuilder.sucessful());
    }

    public static String getResponseMessage(FullHttpResponse response) {
        return new String(response.content().array());
    }

    public static FullHttpResponse getHttpResponse(EmbeddedMessageChannel ch) {
        MessageBuf<Object> outbound = ch.pipeline().context(MockOutboundHandler.class).outboundMessageBuffer();
        return (FullHttpResponse) outbound.poll();
    }

    public static class MockOutboundHandler
        extends ChannelOperationHandlerAdapter implements ChannelOutboundMessageHandler<Object> {

        @Override
        public MessageBuf<Object> newOutboundBuffer(ChannelHandlerContext ctx) throws Exception {
            return Unpooled.messageBuffer();
        }

        @Override
        public void flush(ChannelHandlerContext ctx, ChannelPromise future) throws Exception {
            //NoOp
        }
    }

    public static class CustomTextFrameHandler extends ChannelInboundMessageHandlerAdapter<TextWebSocketFrame> {
        private String content;

        @Override
        public void messageReceived(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
            log.info("\n\n\t>>>>>>>>>> Processed content: " + msg.text() + "\n");
            content = "processed: " + msg.text();
        }

        String getContent() {
            return content;
        }
    }

}
