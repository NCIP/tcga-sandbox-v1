package gov.nih.nci.ncicb.tcga.dcc.io.api;

public enum IoUriComponent {
    
    HTTP_URI_SCHEME("http"),
    
    HTTP_URI_SCHEME_SECURE("https"),
    
    WEBSOCKET_URI_SCHEME("ws"),
    
    WEBSOCKET_URI_SCHEME_SECURE("wss"),
    
    WEBSOCKET_CONTEXT_PATH("io");
    
    private String componentValue;
    
    private IoUriComponent(String componentValue) {
        this.componentValue = componentValue;
    }

    public String componentValue() {
        return componentValue;
    }
}
