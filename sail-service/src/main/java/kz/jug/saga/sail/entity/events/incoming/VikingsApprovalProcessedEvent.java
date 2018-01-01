package kz.jug.saga.sail.entity.events.incoming;

import kz.jug.saga.sail.entity.events.AbstractEvent;
import kz.jug.saga.sail.entity.events.ApprovalStatus;
import kz.jug.saga.sail.entity.events.Viking;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class VikingsApprovalProcessedEvent extends AbstractEvent {
    private ApprovalStatus approvalStatus;
    private List<Viking> vikings;

    public VikingsApprovalProcessedEvent(String transactionId, ApprovalStatus approvalStatus, List<Viking> vikings) {
        super(transactionId);
        this.approvalStatus = approvalStatus;
        this.vikings = vikings;
    }
}
