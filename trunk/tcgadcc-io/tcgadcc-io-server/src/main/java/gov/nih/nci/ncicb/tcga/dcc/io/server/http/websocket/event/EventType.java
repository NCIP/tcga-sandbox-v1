package gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.event;

public enum EventType {
    
    DEFAULT("default"),
    
    WEBSOCKET("websocket");
    
    private String id;

    private EventType(String id) {
        this.id = id;
    }
    
    public String id() {
        return id;
    }
}
