/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.handler;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import gov.nih.nci.ncicb.tcga.dcc.io.api.IoApiConfig;
import gov.nih.nci.ncicb.tcga.dcc.io.api.IoApiConfigProfileType;
import gov.nih.nci.ncicb.tcga.dcc.io.api.IoApiSecurityConfig;
import gov.nih.nci.ncicb.tcga.dcc.io.api.event.EventBus;
import gov.nih.nci.ncicb.tcga.dcc.io.api.event.WebSocketEvent;
import gov.nih.nci.ncicb.tcga.dcc.io.server.WebSocketServerTestConfig;
import gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.WebSocketServer;
import gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.WebSocketTestClient;

import javax.inject.Inject;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = { 
                IoApiConfig.class,
                IoApiSecurityConfig.class,
                WebSocketServerTestConfig.class })
@ActiveProfiles(IoApiConfigProfileType.TLS_DISABLED_PROFILE_NAME)
public class WebSocketServerHandlerTest {

    private static final Logger log = LoggerFactory.getLogger(WebSocketServerHandlerTest.class);

    @Inject
    private EventBus<WebSocketEvent> webSocketEventBus;
    
    @Inject
    private WebSocketServer webSocketServer;
    
    @Inject
    private WebSocketTestClient webSocketTestClient;
    
    @Test
    public void shouldReturnPongForPing() throws InterruptedException {
        
        webSocketTestClient.getChannel().write(
                new PingWebSocketFrame(Unpooled.copiedBuffer(new byte[]{1, 2, 3, 4, 5, 6})));
    }
    
//    @Test
//    public void testPerformOpeningHandshake() {
//        EmbeddedByteChannel ch = new EmbeddedByteChannel(
//                new HttpObjectAggregator(42), new HttpRequestDecoder(), new HttpResponseEncoder());
//
//        FullHttpRequest req = new DefaultFullHttpRequest(HTTP_1_1, HttpMethod.GET, "/chat");
//        req.headers().set(Names.HOST, "server.example.com");
//        req.headers().set(Names.UPGRADE, WEBSOCKET.toLowerCase());
//        req.headers().set(Names.CONNECTION, "Upgrade");
//        req.headers().set(Names.SEC_WEBSOCKET_KEY, "dGhlIHNhbXBsZSBub25jZQ==");
//        req.headers().set(Names.SEC_WEBSOCKET_ORIGIN, "http://example.com");
//        req.headers().set(Names.SEC_WEBSOCKET_PROTOCOL, "chat, superchat");
//        req.headers().set(Names.SEC_WEBSOCKET_VERSION, "13");
//
//        new WebSocketServerHandshaker13(
//                "ws://example.com/chat", "chat", false, Integer.MAX_VALUE).handshake(ch, req);
//
//        ByteBuf resBuf = ch.readOutbound();
//
//        EmbeddedByteChannel ch2 = new EmbeddedByteChannel(new HttpResponseDecoder());
//        ch2.writeInbound(resBuf);
//        HttpResponse res = (HttpResponse) ch2.readInbound();
//
//        Assert.assertEquals(
//                "s3pPLMBiTxaQ9kYGzzhZRbK+xOo=", res.headers().get(Names.SEC_WEBSOCKET_ACCEPT));
//        Assert.assertEquals("chat", res.headers().get(Names.SEC_WEBSOCKET_PROTOCOL));
//    }
    
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

