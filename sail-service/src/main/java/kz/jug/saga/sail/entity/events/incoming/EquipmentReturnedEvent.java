package kz.jug.saga.sail.entity.events.incoming;

import kz.jug.saga.sail.entity.events.AbstractEvent;
import kz.jug.saga.sail.entity.events.ApprovalStatus;
import kz.jug.saga.sail.entity.events.Equipment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class EquipmentReturnedEvent extends AbstractEvent {
    private ApprovalStatus approvalStatus;
    private List<Equipment> equipment = new ArrayList<>();

    public EquipmentReturnedEvent(String transactionId, ApprovalStatus approvalStatus, List<Equipment> equipment) {
        super(transactionId);
        this.approvalStatus = approvalStatus;
        this.equipment = equipment;
    }

    public EquipmentReturnedEvent(String transactionId, ApprovalStatus approvalStatus) {
        super(transactionId);
        this.approvalStatus = approvalStatus;
    }
}
