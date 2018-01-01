package kz.jug.saga.sail.entity.events.outgoing;

import kz.jug.saga.sail.entity.EquipmentType;
import kz.jug.saga.sail.entity.events.AbstractEvent;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EquipmentRequestedEvent extends AbstractEvent {
    private EquipmentType preferredEquipment;
    private int vikingsNumber;

    public EquipmentRequestedEvent(String transactionId, EquipmentType preferredEquipment, int vikingsNumber) {
        super(transactionId);
        this.preferredEquipment = preferredEquipment;
        this.vikingsNumber = vikingsNumber;
    }
}
