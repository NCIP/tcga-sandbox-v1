/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * Event bus that serves a the back channel for passing messages between
 * {@link DefaultEvent}s.
 * 
 * @author nichollsmc
 */
public class EventBus<E> implements SmartLifecycle {

    private static final Logger log = LoggerFactory.getLogger(EventBus.class);
    
    @Value("${eventBus.ringSize}")
    private int ringSize;
    
    @Value("${eventBus.threadPool.minProcessors}")
    private int threadPoolMinProcessors;
    
    @Value("${eventBus.threadPool.minProcessors.scaleFactor}")
    private int threadPoolMinProcessorsScaleFactor;

    private ExecutorService     executorService;
    private EventFactory<E>     eventFactory;
    private Disruptor<E>        disruptor;
    private RingBuffer<E>       ringBuffer;
    private volatile boolean    isRunning = false;

    public Disruptor<E> createEventBus() {
        ExecutorService executorService = getExecutorService();
        Disruptor<E> disruptor = new Disruptor<E>(
                eventFactory,
                ringSize,
                executorService,
                ProducerType.SINGLE,
                new SleepingWaitStrategy());

        return disruptor;
    }

    private ExecutorService getExecutorService() {
        int processors = Runtime.getRuntime().availableProcessors();
        if (processors >= threadPoolMinProcessors) {
            return Executors.newFixedThreadPool(processors * threadPoolMinProcessorsScaleFactor);
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
    public void publishEvent(EventTranslator<E> translator) {
        ringBuffer.publishEvent(translator);
    }
    
    @Override
    public void start() {
        isRunning = true;
        log.info("Starting event bus");
        ringBuffer = createEventBus().start();
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
