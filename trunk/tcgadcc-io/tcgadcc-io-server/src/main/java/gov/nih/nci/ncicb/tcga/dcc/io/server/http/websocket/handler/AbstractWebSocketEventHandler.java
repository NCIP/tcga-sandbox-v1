/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.handler;

import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceReportingEventHandler;

public abstract class AbstractWebSocketEventHandler<E> implements SequenceReportingEventHandler<E> {
    
    private Sequence sequence;
    
    @Override
    public void setSequenceCallback(Sequence sequence) {
        this.sequence = sequence;
    }
    
    @Override
    public void onEvent(E webSocketEvent, long sequence, boolean endOfBatch) throws Exception {
        onEvent(webSocketEvent);
        this.sequence.set(sequence);
    }
    
    /**
     * Callback method to be implemented by a sub-type for processing WebSocket
     * events.
     * 
     * @param <E> the type of WebSocket event
     * @param webSocketEvent
     *            event implementation storing the data for sharing during
     *            exchange or parallel coordination of a WebSocket event
     * @throws Exception
     *             for reporting errors that should be handled by the event
     *             chain
     */
    protected abstract void onEvent(E webSocketEvent) throws Exception;

}
