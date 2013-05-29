/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server.perftest;


/**
 * Client that can be run manually to perform performance tests.
 * 
 * @author nichollsmc
 */
public class PerfClient {

//    private HttpClient           client;
//
//    // Number of connections to create
//    private static final int     CONNS       = 1;
//
//    private int                  statsCount;
//
//    private EventBus             eb;
//
//    private static final int     STR_LENGTH  = 8 * 1024;
//
//    private static final int     STATS_BATCH = 1024 * 1024;
//
//    private static final int     BUFF_SIZE   = 32 * 1024;
//
//    private String               message;
//
//    private Vertx    vertx      = VertxFactory.newVertx();
//    int              connectCount;
//    Queue<WebSocket> websockets = new LinkedList<WebSocket>();
//    Set<WebSocket>   wss        = new HashSet<WebSocket>();
//    
//    private final CountDownLatch disconnect;
//
//    public PerfClient() {
//        disconnect = new CountDownLatch(1);
//        StringBuilder sb = new StringBuilder(STR_LENGTH);
//        for (int i = 0; i < STR_LENGTH; i++) {
//            sb.append('X');
//        }
//        message = sb.toString();
//    }
//
//    private void connect(final int count) {
//        client.connectWebsocket("/io", new Handler<WebSocket>() {
//            public void handle(final WebSocket ws) {
//                connectCount++;
//
//                ws.setWriteQueueMaxSize(BUFF_SIZE);
//                ws.dataHandler(new Handler<Buffer>() {
//                    public void handle(Buffer data) {
//                        if (!wss.contains(ws)) {
//                            wss.add(ws);
//                            if (wss.size() == CONNS) {
//                                System.out.println("Received data on all conns");
//                            }
//                        }
//                        int len = data.length();
//                        statsCount += len;
//                        if (statsCount > STATS_BATCH) {
//                            eb.send("rate-counter", statsCount);
//                            statsCount = 0;
//                        }
//                    }
//                });
//
//                websockets.add(ws);
//                if (connectCount == CONNS) {
//                    startWebSocket();
//                }
//            }
//        });
//        if (count + 1 < CONNS) {
//            vertx.runOnContext(new VoidHandler() {
//                public void handle() {
//                    connect(count + 1);
//                }
//            });
//        }
//    }
//
//    private void startWebSocket() {
//        WebSocket ws = websockets.poll();
//        writeWebSocket(ws);
//        if (!websockets.isEmpty()) {
//            vertx.runOnContext(new VoidHandler() {
//                public void handle() {
//                    startWebSocket();
//                }
//            });
//        }
//
//    }
//
//    public void start() {
//        System.out.println("Starting perf client");
//        eb = vertx.eventBus();
//        client = vertx.createHttpClient().setPort(9888).setHost("localhost").setMaxPoolSize(CONNS);
//        client.setReceiveBufferSize(BUFF_SIZE);
//        client.setSendBufferSize(BUFF_SIZE);
//        client.setConnectTimeout(60000);
//        connect(0);
//    }
//
//    private void writeWebSocket(final WebSocket ws) {
//        if (!ws.writeQueueFull()) {
//            ws.writeTextFrame(message);
//            // ws.writeBinaryFrame(new Buffer(message));
//            vertx.runOnContext(new VoidHandler() {
//                public void handle() {
//                    writeWebSocket(ws);
//                }
//            });
//        }
//        else {
//            // Flow control
//            ws.drainHandler(new VoidHandler() {
//                public void handle() {
//                    writeWebSocket(ws);
//                }
//            });
//        }
//    }
//
//    public boolean awaitClose(int duration, TimeUnit unit) throws InterruptedException {
//        return disconnect.await(duration, unit);
//    }
}
