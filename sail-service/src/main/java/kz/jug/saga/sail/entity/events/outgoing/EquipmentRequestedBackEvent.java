package kz.jug.saga.sail.entity.events.outgoing;

import kz.jug.saga.sail.entity.events.AbstractEvent;
import lombok.Data;

@Data
public class EquipmentRequestedBackEvent extends AbstractEvent {

    public EquipmentRequestedBackEvent(String transactionId) {
        super(transactionId);
    }
}
