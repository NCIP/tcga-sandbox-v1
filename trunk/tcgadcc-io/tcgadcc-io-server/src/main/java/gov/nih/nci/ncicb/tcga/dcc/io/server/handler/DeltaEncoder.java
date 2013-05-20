/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server.handler;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.vertx.java.core.http.ServerWebSocket;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

public class DeltaEncoder {

    public void handle(final ServerWebSocket serverWebSocket) {
        ExecutorService exec = Executors.newCachedThreadPool();

        Disruptor<DeltaEncodeEvent> disruptor = new Disruptor<DeltaEncodeEvent>(
                DeltaEncodeEvent.EVENT_FACTORY, 1024,exec);
        
        final EventHandler<DeltaEncodeEvent> eventHandler = new EventHandler<DeltaEncodeEvent>() {

            public void onEvent(final DeltaEncodeEvent event, final long sequence, final boolean endOfBatch)
                    throws Exception {

                serverWebSocket.writeTextFrame("Sequence: " + sequence);
                serverWebSocket.writeTextFrame("ValueEvent: " + event.getValue());
            }
            
        };

        disruptor.handleEventsWith(eventHandler);
        RingBuffer<DeltaEncodeEvent> ringBuffer = disruptor.start();

        for (long i = 10; i < 2000; i++) {
            String uuid = UUID.randomUUID().toString();

            // Two phase commit. Grab one of the 1024 slots
            long sequence = ringBuffer.next();
            DeltaEncodeEvent valueEvent = ringBuffer.get(sequence);
            valueEvent.setValue(uuid);
            ringBuffer.publish(sequence);
        }

        disruptor.shutdown();

        exec.shutdown();
    }
}
