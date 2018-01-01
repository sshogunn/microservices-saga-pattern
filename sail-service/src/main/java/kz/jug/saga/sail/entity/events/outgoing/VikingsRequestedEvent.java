package kz.jug.saga.sail.entity.events.outgoing;

import kz.jug.saga.sail.entity.events.AbstractEvent;
import lombok.*;

@Data
@NoArgsConstructor
public class VikingsRequestedEvent extends AbstractEvent {
    private int vikingsNumber;

    public VikingsRequestedEvent(String transactionId, int vikingsNumber) {
        super(transactionId);
        this.vikingsNumber = vikingsNumber;
    }
}
