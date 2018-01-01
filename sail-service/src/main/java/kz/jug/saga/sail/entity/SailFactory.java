package kz.jug.saga.sail.entity;

import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.UUID;

@RequiredArgsConstructor
public class SailFactory {

    public Sail createSail(
            int vikingsNumber,
            SailType sailType,
            EquipmentType preferredEquipment) {
        val sagaState = new SailState("sail-saga", vikingsNumber, sailType, preferredEquipment);
        val transactionId = UUID.randomUUID().toString();
        return new Sail(transactionId, sagaState);
    }
}
