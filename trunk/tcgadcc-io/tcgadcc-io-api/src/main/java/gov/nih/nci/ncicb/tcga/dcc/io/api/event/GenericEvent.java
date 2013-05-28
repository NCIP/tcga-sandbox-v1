/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.api.event;

/**
 * Generic implementation of an {@link Event} type.
 * 
 * @author nichollsmc
 */
public class GenericEvent implements Event {
    
    private String       id;
    private EventType    type;
    private Object       source;
    private EventContext context;
    private Long         timestamp;

    @Override
    public String getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public EventType getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(EventType type) {
        this.type = type;
    }

    @Override
    public Object getSource() {
        return source;
    }

    /**
     * @param source
     *            the source to set
     */
    public void setSource(Object source) {
        this.source = source;
    }

    @Override
    public EventContext getContext() {
        return context;
    }

    /**
     * @param context
     *            the context to set
     */
    public void setContext(EventContext context) {
        this.context = context;
    }

    @Override
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp
     *            the timestamp to set
     */
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    
}
