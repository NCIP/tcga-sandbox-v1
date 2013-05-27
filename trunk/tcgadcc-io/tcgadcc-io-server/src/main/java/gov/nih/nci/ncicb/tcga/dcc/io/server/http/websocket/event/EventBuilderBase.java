package gov.nih.nci.ncicb.tcga.dcc.io.server.http.websocket.event;

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
