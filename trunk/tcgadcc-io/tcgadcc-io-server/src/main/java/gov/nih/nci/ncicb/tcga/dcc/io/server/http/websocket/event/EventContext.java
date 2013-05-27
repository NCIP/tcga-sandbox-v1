package gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.event;

import java.util.Map;

import javolution.util.FastMap;

public class EventContext {

    private final Map<String, Object> attributeMap = new FastMap<String, Object>().setShared(true);
    
    public void setAttribute(String key, Object value) {
        attributeMap.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributeMap.get(key);
    }

    public void removeAttribute(String key) {
        attributeMap.remove(key);
    }

}
