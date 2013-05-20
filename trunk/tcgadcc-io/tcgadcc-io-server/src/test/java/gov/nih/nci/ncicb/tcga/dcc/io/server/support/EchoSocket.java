/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server.support;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.StatusCode;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test WebSocket stub for echoing messages.
 * 
 * @author nichollsmc
 */
@WebSocket(maxMessageSize = 64 * 1024)
public class EchoSocket {

    private final Logger         log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final CountDownLatch disconnect;

    @SuppressWarnings("unused")
    private Session              session;

    public EchoSocket() {
        disconnect = new CountDownLatch(1);
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.printf("Got connect: %s%n", session);
        this.session = session;
        try {
            Future<Void> futureOfVoid;
            futureOfVoid = session.getRemote().sendStringByFuture("Hello");
            futureOfVoid.get(2, TimeUnit.SECONDS);

            futureOfVoid = session.getRemote().sendStringByFuture("Thanks for the conversation.");
            futureOfVoid.get(2, TimeUnit.SECONDS);

            session.close(StatusCode.NORMAL, "I'm done");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        System.out.printf("Got msg: %s%n", message);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.printf("Connection closed: %d - %s%n", statusCode, reason);
        session = null;
        disconnect.countDown();
    }

    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
        return disconnect.await(duration, unit);
    }

}
