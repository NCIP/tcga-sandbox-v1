/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server.websocket;

import gov.nih.nci.ncicb.tcga.dcc.io.server.support.EchoSocket;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.http.client.utils.URIBuilder;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.vertx.java.core.http.HttpServer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { WebSocketServerTestConfig.class })
public class WebSocketServerTest {

    private final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    @Value("${websocket.server.scheme}")
    private String websocketServerScheme;
    
    @Value("${websocket.server.host}")
    private String websocketServerHost;
    
    @Value("${websocket.server.port}")
    private Integer websocketServerPort;
    
    @Value("${websocket.server.context.root}")
    private String websocketServerContextRoot;
    
    @Inject
    private HttpServer webSocketServer;
    
    private WebSocketClient webSocketClient;

    @Before
    public void startClient() throws Exception {
        webSocketClient = new WebSocketClient();
        webSocketClient.start();
    }
    
    @After
    public void stopClient() throws Exception {
        webSocketClient.stop();
    }

    @Test(timeout = 30000)
    public void echoText() {
        EchoSocket echoSocket = new EchoSocket();
        ClientUpgradeRequest request = new ClientUpgradeRequest();
        
        try {
            URI echoUri = new URIBuilder()
            .setScheme(websocketServerScheme)
            .setHost(websocketServerHost)
            .setPort(websocketServerPort)
            .setPath(websocketServerContextRoot + "/delta-encode")
            .build();
            
            webSocketClient.connect(echoSocket, echoUri, request);
            log.info("Connecting to : %s%n", echoUri);

            echoSocket.awaitClose(10, TimeUnit.SECONDS);
        }
        catch (URISyntaxException|IOException|InterruptedException e) {
            e.printStackTrace();
        }
    }
    
}

