/*
 * Software License, Version 1.0 Copyright 2013 SRA International, Inc. Copyright Notice.  
 * The software subject to this notice and license includes both human readable source 
 * code form and machine readable, binary, object code form (the "caBIG Software").
 *
 * Please refer to the complete License text for full details at the root of the project.
 */

package gov.nih.nci.ncicb.tcga.dcc.io.api.event;

import java.util.Map;

import javolution.util.FastMap;

/**
 * Used for maintaining state for {@link Event} types.
 * <p>
 * Attributes are managed internally within a thread-safe map.
 * 
 * @author nichollsmc
 */
public class EventContext {

    private final Map<String, Object> attributeMap = new FastMap<String, Object>().setShared(true);
    
    /**
     * Set an event attribute using the provided key and object.
     * 
     * @param key string representing the attribute key to set
     * @param value attribute object to set
     */
    public void setAttribute(String key, Object value) {
        attributeMap.put(key, value);
    }

    /**
     * Retrieves an attribute using the provided key.
     * 
     * @param key string representing the attribute key 
     * @return the object mapped by the provided key
     */
    public Object getAttribute(String key) {
        return attributeMap.get(key);
    }

    /**
     * Removes and attribute using the provided key.
     * 
     * @param key string representing the key of the attribute to remove
     */
    public void removeAttribute(String key) {
        attributeMap.remove(key);
    }

}
