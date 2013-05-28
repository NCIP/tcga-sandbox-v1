/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket;

import gov.nih.nci.ncicb.tcga.dcc.io.api.IoApiConfigProfileType;
import gov.nih.nci.ncicb.tcga.dcc.io.server.IoServerTestConfig;
import gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.handler.WebSocketServerHandler;
import io.netty.channel.embedded.EmbeddedMessageChannel;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@RunWith(JUnit4.class)
@ContextConfiguration(classes = { IoServerTestConfig.class })
@ActiveProfiles(IoApiConfigProfileType.TLS_DISABLED_PROFILE_NAME)
public class WebSocketServerHandlerTest {

    private static final Logger log = LoggerFactory.getLogger(WebSocketServerHandlerTest.class);

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

