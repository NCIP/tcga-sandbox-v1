/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.client.http.websocket;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.VertxFactory;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.WebSocket;
import org.vertx.java.platform.Verticle;

public class ConnectClient extends Verticle {

    private HttpClient           client;

    private static final int     CONNS = 1000;

    int                          connectCount;

    private final CountDownLatch disconnect;

    public ConnectClient() {
        disconnect = new CountDownLatch(1);
    }

    @Override
    public void start() {
        System.out.println("Starting perf client");
        Vertx vertx = VertxFactory.newVertx();

        client = vertx.createHttpClient().setPort(9888).setHost("localhost").setMaxPoolSize(CONNS);
        for (int i = 0; i < CONNS; i++) {
            System.out.println("Creating connection [" + i + "]");
            client.connectWebsocket("/async/echo", new Handler<WebSocket>() {
                public void handle(WebSocket ws) {
                    System.out.println("ws connected: " + ++connectCount);
                }
            });
        }
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
        return disconnect.await(duration, unit);
    }

}
