package gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.event;

public class WebSocketEventBuilder extends EventBuilderBase<WebSocketEventBuilder> {
    
    public static WebSocketEventBuilder webSocketEvent() {
        return new WebSocketEventBuilder();
    }

    public WebSocketEventBuilder() {
        super(new WebSocketEvent());
    }

    public WebSocketEvent build() {
        return (WebSocketEvent) getInstance();
    }
    
}
