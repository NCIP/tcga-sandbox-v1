package gov.nih.nci.ncicb.tcga.dcc.io.server.handler;

import com.lmax.disruptor.EventFactory;

/**
 * A mutable object that serves as a unit of work.
 * 
 * @author nichollsmc
 */
public final class DeltaEncodeEvent {
    
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public final static EventFactory<DeltaEncodeEvent> EVENT_FACTORY = new EventFactory<DeltaEncodeEvent>() {
        public DeltaEncodeEvent newInstance() {
            return new DeltaEncodeEvent();
        }
    };
    
}