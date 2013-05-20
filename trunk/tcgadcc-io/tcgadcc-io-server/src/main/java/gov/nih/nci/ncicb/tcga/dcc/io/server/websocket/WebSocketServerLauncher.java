/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server.websocket;

import gov.nih.nci.ncicb.tcga.dcc.io.server.ServerIOConfig;

import java.util.concurrent.CountDownLatch;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Configures and starts a WebSocket server instance.
 * 
 * @author nichollsmc
 */
public class WebSocketServerLauncher {
    
    private AnnotationConfigApplicationContext applicationContext;
    
    public WebSocketServerLauncher() {
        applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.register(ServerIOConfig.class);
        applicationContext.registerShutdownHook();
        applicationContext.refresh();
    }
    
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            new WebSocketServerLauncher();
            countDownLatch.await();
        }
        finally {
            countDownLatch.countDown();
        }
    }
}
