/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.api.event.handler;

import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceReportingEventHandler;

/**
 * Template that defines basic event handler behavior.
 * 
 * @param <E> the event type
 * 
 * @author nichollsmc
 */
public abstract class AbstractEventHandler<E> implements SequenceReportingEventHandler<E> {
    
    private Sequence sequence;
    
    @Override
    public void setSequenceCallback(Sequence sequence) {
        this.sequence = sequence;
    }
    
    @Override
    public void onEvent(E event, long sequence, boolean endOfBatch) throws Exception {
        onEvent(event);
        this.sequence.set(sequence);
    }
    
    /**
     * Callback method to be implemented by a sub-type for processing events.
     * 
     * @param <E> the type of event
     * @param event
     *            event implementation storing the data for sharing during
     *            exchange or parallel coordination of an event
     * @throws Exception
     *             for reporting errors that should be handled by the event
     *             chain
     */
    protected abstract void onEvent(E event) throws Exception;

}
