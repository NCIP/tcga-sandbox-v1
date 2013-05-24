/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.event;

import gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.handler.WebSocketFrameHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * Event bus that serves a the back channel for passing messages between
 * {@link WebSocketEvent}s.
 * 
 * @author nichollsmc
 */
public class WebSocketEventBus implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(WebSocketEventBus.class);
    
    private static final int RING_SIZE                  = 256 * 1024;
    private static final int MIN_THREAD_POOL_PROCESSORS = 2;
    private static final int THREAD_POOL_SCALE_FACTOR   = 2;

    private ExecutorService            executorService;
    private Disruptor<WebSocketEvent>  disruptor;
    private RingBuffer<WebSocketEvent> ringBuffer;
    private volatile boolean           isRunning = false;

    @SuppressWarnings("unchecked")
    public Disruptor<WebSocketEvent> createDisruptor() {
        ExecutorService executorService = getExecutorService();
        Disruptor<WebSocketEvent> disruptor = new Disruptor<WebSocketEvent>(
                WebSocketEvent.EVENT_FACTORY,
                RING_SIZE,
                executorService,
                ProducerType.SINGLE,
                new SleepingWaitStrategy());

        disruptor.handleEventsWith(new WebSocketFrameHandler());

        return disruptor;
    }

    private ExecutorService getExecutorService() {
        int processors = Runtime.getRuntime().availableProcessors();
        if (processors >= MIN_THREAD_POOL_PROCESSORS) {
            return Executors.newFixedThreadPool(MIN_THREAD_POOL_PROCESSORS * THREAD_POOL_SCALE_FACTOR);
        }
        else {
            return Executors.newCachedThreadPool();
        }
    }
    
    /**
     * Publishes {@link EventTranslator} events to the event bus.
     * 
     * @param translator the {@link EventTranslator} to publish
     */
    public void publishEvent(EventTranslator<WebSocketEvent> translator) {
        ringBuffer.publishEvent(translator);
    }
    
    @Override
    public void start() {
        isRunning = true;
        log.info("Starting event bus");
        ringBuffer = createDisruptor().start();
        log.info("Event bus started");
    }

    @Override
    public void stop() {
        isRunning = false;
        disruptor.shutdown();
        executorService.shutdown();
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public int getPhase() {
        return 0;
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public void stop(Runnable callback) {
        isRunning = false;
        stop();
    }

}
