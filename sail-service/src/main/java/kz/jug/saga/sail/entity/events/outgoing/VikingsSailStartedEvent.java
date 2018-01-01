package kz.jug.saga.sail.entity.events.outgoing;

import kz.jug.saga.sail.entity.events.AbstractEvent;
import kz.jug.saga.sail.entity.events.Equipment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class VikingsSailStartedEvent extends AbstractEvent {
    private List<Long> vikingsIds = new ArrayList<>();
    private List<Equipment> equipment = new ArrayList<>();

    public VikingsSailStartedEvent(List<Long> vikingsIds, List<Equipment> equipment) {
        this.vikingsIds = vikingsIds;
        this.equipment = equipment;
    }

    public VikingsSailStartedEvent(String transactionId, List<Long> vikingsIds, List<Equipment> equipment) {
        super(transactionId);
        this.vikingsIds = vikingsIds;
        this.equipment = equipment;
    }
}
