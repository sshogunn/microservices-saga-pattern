package kz.jug.saga.sail.entity;

import kz.jug.saga.sail.entity.events.ApprovalStatus;
import kz.jug.saga.sail.entity.events.Equipment;
import kz.jug.saga.sail.entity.events.Viking;
import kz.jug.saga.sail.entity.events.incoming.EquipmentReturnedEvent;
import kz.jug.saga.sail.entity.events.incoming.VikingsApprovalProcessedEvent;
import kz.jug.saga.sail.entity.events.outgoing.*;
import kz.jug.saga.sail.entity.saga.AbstractSaga;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
@EqualsAndHashCode(of = {"transactionId"}, callSuper = true)
@NoArgsConstructor
@Document(collection = "sail-saga")
public class Sail extends AbstractSaga<SailState, SailStep> {
    @Id
    @Setter
    private long id;
    @Indexed(unique = true)
    private String transactionId;
    @Indexed
    private List<Long> vikings = new ArrayList<>();
    private List<Equipment> vikingsEquipment = new ArrayList<>();

    public Sail(SailState state) {
        super(state);
    }

    public Sail(String transactionId) {
        this.transactionId = transactionId;
    }

    public Sail(String transactionId, SailState state) {
        super(state);
        this.transactionId = transactionId;
        this.setStep(SailStep.INITIATED);
    }

    public void startSail() {
        log.debug("Vikings request will be executed to reserve required number of vikings for the sail. Vikings number is {}", getState().getVikingsNumber());
        this.moveTo(SailStep.VIKING_REQUESTED);
    }

    public VikingsRequestedEvent requestVikings() {
        return new VikingsRequestedEvent(transactionId, getState().getVikingsNumber());
    }

    public void addVikings(VikingsApprovalProcessedEvent approvalEvent) {
        if (approvalEvent.getApprovalStatus() == ApprovalStatus.APPROVED) {
            List<Long> vikingIds = approvalEvent.getVikings()
                    .stream()
                    .map(Viking::getId)
                    .collect(Collectors.toList());
            this.vikings.addAll(vikingIds);
        }
        this.moveTo(SailStep.EQUIPMENT_REQUESTED);
    }

    public VikingsDismissedEvent dismissVikings() {
        VikingsDismissedEvent event = new VikingsDismissedEvent(transactionId, vikings);
        this.vikings.clear();
        return event;
    }

    public EquipmentRequestedEvent requestEquipment() {
        EquipmentType preferredEquipment = getState().getPreferredEquipment();
        int requiredEquipment = this.vikings.size();
        return new EquipmentRequestedEvent(transactionId, preferredEquipment, requiredEquipment);
    }

    public EquipmentRequestedBackEvent requestEquipmentBack() {
        return new EquipmentRequestedBackEvent(transactionId);
    }

    public void addEquipment(EquipmentReturnedEvent equipmentReturnedEvent) {
        if (equipmentReturnedEvent.getApprovalStatus() == ApprovalStatus.APPROVED) {
            this.vikingsEquipment = equipmentReturnedEvent.getEquipment();
            this.moveTo(SailStep.SAIL_STARTED);
        } else {
            this.compensateTill(SailStep.INITIATED);
        }
    }


    public VikingsSailStartedEvent prepareVikingsForSail() {
        return new VikingsSailStartedEvent(transactionId, vikings, vikingsEquipment);
    }

    public VikingsSailCanceledEvent requestVikingsBackFromSail() {
        return new VikingsSailCanceledEvent(transactionId, vikings);
    }
}
