/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.api.event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * Event bus that serves as the back channel for passing messages between
 * {@link Event} types.
 * <p>
 * This class implements the {@link SmartLifecycle} interface, allowing a
 * Spring container to manage startup and shutdown.
 * 
 * @param <E> the type of event
 * 
 * @author nichollsmc
 * 
 * @see SmartLifecycle
 * @see Disruptor
 */
public class EventBus<E extends Event> implements SmartLifecycle {
    
    private static final Logger log = LoggerFactory.getLogger(EventBus.class);
    
    private Disruptor<E>        disruptor;
    private ExecutorService     executorService;
    private RingBuffer<E>       ringBuffer;
    private volatile boolean    isRunning = false;
    
    /**
     * Constructor for creating an {@link EventBus} using the provided
     * parameters.
     * 
     * @param ringSize
     *            the number events that should be pre-allocated by the ring
     *            buffer
     * @param threadPoolMinProcessors
     *            minimum number of available processing cores used for
     *            calculating the number of event processing threads to create
     * @param threadPoolMinProcessorsScaleFactor
     *            number that will be multiplied by the
     *            {@code threadPoolMinProcessors} parameter for calculating the
     *            number of event processing threads to create
     * @param eventFactory
     *            the {@link EventFactory} that will be used by the event bus to
     *            create events
     */
    public EventBus(final int ringSize,
                    final int threadPoolMinProcessors,
                    final int threadPoolMinProcessorsScaleFactor,
                    final EventFactory<E> eventFactory) {
        
        // Create executor service
        this.executorService = getExecutorService(
                threadPoolMinProcessors,
                threadPoolMinProcessorsScaleFactor);
        
        // Create disruptor
        this.disruptor = new Disruptor<E>(
                eventFactory,
                ringSize,
                executorService,
                ProducerType.MULTI,
                new SleepingWaitStrategy());
    }

    private ExecutorService getExecutorService(int threadPoolMinProcessors,
                                               int threadPoolMinProcessorsScaleFactor) {
        int processors = Runtime.getRuntime().availableProcessors();
        if (processors >= threadPoolMinProcessors) {
            return Executors.newFixedThreadPool(processors * threadPoolMinProcessorsScaleFactor);
        }
        else {
            return Executors.newCachedThreadPool();
        }
    }
    
    /**
     * Exposes the {@link Disruptor#handleEventsWith(EventHandler...)} DSL
     * method of the {@link Disruptor} managed by an event bus.
     * 
     * @param handlers
     *            the event handlers that will process events.
     * @return a {@link EventHandlerGroup} that can be used to chain
     *         dependencies.
     * 
     * @see Disruptor#handleEventsWith(EventHandler...)
     */
    public EventHandlerGroup<E> handleEventsWith(final EventHandler<E>... handlers) {
        return disruptor.handleEventsWith(handlers);
    }

    /**
     * Publishes {@link EventTranslator} events to the event bus.
     * 
     * @param translator
     *            the {@link EventTranslator} to publish
     */
    public void publishEvent(final EventTranslator<E> translator) {
        ringBuffer.publishEvent(translator);
    }

    @Override
    public void start() {
        log.info("Starting event bus...");
        ringBuffer = disruptor.start();
        isRunning = true;
        log.info("Event bus started.");
    }

    @Override
    public void stop() {
        log.info("Stopping event bus...");
        disruptor.shutdown();
        executorService.shutdown();
        isRunning = false;
        log.info("Event bus stopped.");
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
        return false;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
    }

}
