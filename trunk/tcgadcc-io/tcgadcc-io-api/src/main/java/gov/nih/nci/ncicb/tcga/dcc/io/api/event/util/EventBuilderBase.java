/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.api.event.util;

import gov.nih.nci.ncicb.tcga.dcc.io.api.event.Event;
import gov.nih.nci.ncicb.tcga.dcc.io.api.event.EventContext;
import gov.nih.nci.ncicb.tcga.dcc.io.api.event.EventType;
import gov.nih.nci.ncicb.tcga.dcc.io.api.event.GenericEvent;

/**
 * Defines the fluent builder API for building {@link Event} types.
 * 
 * @param <B> fluent builder sub-type
 * 
 * @author nichollsmc
 *
 */
public class EventBuilderBase<B extends EventBuilderBase<B>> {
    
    private final GenericEvent event;

    protected EventBuilderBase(GenericEvent event) {
        this.event = event;
    }

    protected Event getInstance() {
        return event;
    }

    @SuppressWarnings("unchecked")
    public B id(String id) {
        event.setId(id);

        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B type(EventType type) {
        event.setType(type);

        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B source(Object source) {
        event.setSource(source);

        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B context(EventContext context) {
        event.setContext(context);

        return (B) this;
    }

    @SuppressWarnings("unchecked")
    public B timestamp(Long timestamp) {
        event.setTimestamp(timestamp);

        return (B) this;
    }
    
}
