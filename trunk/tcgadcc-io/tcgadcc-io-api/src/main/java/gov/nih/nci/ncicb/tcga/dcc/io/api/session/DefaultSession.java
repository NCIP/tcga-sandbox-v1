/*
* Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.api.session;

import java.util.Map;

public class DefaultSession implements Session {

    protected final String              id;

    protected final Map<String, Object> sessionAttributes;

    protected final long                creationTime;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setAttribute(String key, Object value) {
        sessionAttributes.put(key, value);
    }

    @Override
    public Object getAttribute(String key) {
        return sessionAttributes.get(key);
    }

    @Override
    public void removeAttribute(String key) {
        sessionAttributes.remove(key);
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    protected DefaultSession(Builder builder) {
        this.id = builder.id;
        this.sessionAttributes = builder.sessionAttributes;
        this.creationTime = builder.creationTime;
    }

    public static class Builder {
        protected String              id                = null;
        protected Map<String, Object> sessionAttributes = null;
        protected long                creationTime      = 0L;

        public Object getId() {
            return id;
        }

        public Builder id(final String id) {
            this.id = id;
            return this;
        }

        public Builder sessionAttributes(final Map<String, Object> sessionAttributes) {
            this.sessionAttributes = sessionAttributes;
            return this;
        }

        public Builder creationTime(long creationTime) {
            this.creationTime = creationTime;
            return this;
        }

        public DefaultSession build() {
            return new DefaultSession(this);
        }
    }

}
