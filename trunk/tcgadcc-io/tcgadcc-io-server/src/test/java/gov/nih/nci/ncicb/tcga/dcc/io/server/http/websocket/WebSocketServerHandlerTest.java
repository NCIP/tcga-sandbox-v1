/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket;

import static io.netty.handler.codec.http.HttpHeaders.Values.WEBSOCKET;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import gov.nih.nci.ncicb.tcga.dcc.io.client.http.websocket.WebSocketClientHandler;
import gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.NettyWebSocketTestUtil.MockOutboundHandler;
import gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.handler.WebSocketServerHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.MessageBuf;
import io.netty.channel.embedded.EmbeddedByteChannel;
import io.netty.channel.embedded.EmbeddedMessageChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker13;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(JUnit4.class)
//@ContextConfiguration(classes = { WebSocketServerTestConfig.class })
//@ActiveProfiles(IoApiConfigProfileType.TLS_DISABLED_PROFILE_NAME)
public class WebSocketServerHandlerTest {

    private static final Logger log = LoggerFactory.getLogger(WebSocketServerHandlerTest.class);
    
    //@Value("${websocket.server.scheme}")
    private String websocketServerScheme;
    
    //@Value("${websocket.server.host}")
    private String websocketServerHost;
    
    //@Value("${websocket.server.port}")
    private Integer websocketServerPort;
    
    //@Value("${websocket.server.context.path}")
    private String websocketServerContextPath;

    @Test
    public void testHandleTextFrame() {
//        WebSocketServerHandler webSocketServerHandler = new WebSocketServerHandler();
//        EmbeddedMessageChannel ch = NettyWebSocketTestUtil.createChannel(webSocketServerHandler);
//        NettyWebSocketTestUtil.writeUpgradeRequest(ch);
//        // Removing the HttpRequestDecoder as we are writing a TextWebSocketFrame so decoding is not neccessary.
//        ch.pipeline().remove(HttpRequestDecoder.class);
//
//        ch.writeInbound(new TextWebSocketFrame("payload"));
//
//        FullHttpResponse response = NettyWebSocketTestUtil.getHttpResponse(ch);
//        log.info("\n\ngot response msg: " + response.content() + "\n");
//        //assertEquals("processed: payload", customTextFrameHandler.getContent());
    }
    
//    @Test(timeout = 30000)
//    public void echoText() {
//        EchoSocket echoSocket = new EchoSocket();
//        ClientUpgradeRequest request = new ClientUpgradeRequest();
//        
//        try {
//            URI echoUri = new URIBuilder()
//            .setScheme(websocketServerScheme)
//            .setHost(websocketServerHost)
//            .setPort(websocketServerPort)
//            .setPath(websocketServerContextPath + "/delta-encode")
//            .build();
//            
//            webSocketClient.connect(echoSocket, echoUri, request);
//            log.info("Connecting to : %s%n", echoUri);
//
//            echoSocket.awaitClose(10, TimeUnit.SECONDS);
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    
}

