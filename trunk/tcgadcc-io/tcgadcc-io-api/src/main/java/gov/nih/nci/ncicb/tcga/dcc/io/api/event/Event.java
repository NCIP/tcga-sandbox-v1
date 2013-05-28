/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.api.event;

/**
 * Defines the primary behavior of an I/O event. 
 * 
 * @author nichollsmc
 */
public interface Event {

    /**
     * Returns the event Id.
     */
    String getId();

    /**
     * Returns the {@link EventType}.
     */
    EventType getType();
    
    /**
     * Returns the source that generated the event, can be null.
     */
    Object getSource();

    /**
     * Returns the {@link EventContext}.
     */
    EventContext getContext();
    
    /**
     * Returns the time at which an event was created.
     */
    Long getTimestamp();

}
