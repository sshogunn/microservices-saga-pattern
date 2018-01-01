package kz.jug.saga.sail.entity.events.outgoing;

import kz.jug.saga.sail.entity.events.AbstractEvent;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class VikingsSailCanceledEvent extends AbstractEvent {
    private List<Long> vikingsIds = new ArrayList<>();

    public VikingsSailCanceledEvent(String transactionId, List<Long> vikingsIds) {
        super(transactionId);
        this.vikingsIds = vikingsIds;
    }
}
