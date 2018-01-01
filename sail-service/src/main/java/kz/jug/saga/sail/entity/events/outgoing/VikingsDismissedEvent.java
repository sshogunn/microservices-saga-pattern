package kz.jug.saga.sail.entity.events.outgoing;

import kz.jug.saga.sail.entity.events.AbstractEvent;
import kz.jug.saga.sail.entity.events.Viking;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class VikingsDismissedEvent extends AbstractEvent {
    private List<Long> vikings = new ArrayList<>();

    public VikingsDismissedEvent(String transactionId, List<Long> vikings) {
        super(transactionId);
        this.vikings.addAll(vikings);
    }
}
