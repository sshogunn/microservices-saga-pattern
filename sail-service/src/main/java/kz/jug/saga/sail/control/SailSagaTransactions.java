package kz.jug.saga.sail.control;

import kz.jug.saga.sail.control.equipment.RequestEquipmentCompensation;
import kz.jug.saga.sail.control.equipment.RequestEquipmentTransaction;
import kz.jug.saga.sail.control.fight.StartSailFightCompensation;
import kz.jug.saga.sail.control.fight.StartSailFightTransaction;
import kz.jug.saga.sail.control.vikings.RequestVikingsCompensation;
import kz.jug.saga.sail.control.vikings.RequestVikingsTransaction;
import kz.jug.saga.sail.entity.Sail;
import kz.jug.saga.sail.entity.SailStep;
import kz.jug.saga.sail.entity.saga.StepAction;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SailSagaTransactions {
    private final RequestVikingsTransaction requestVikingsTransaction;
    private final RequestVikingsCompensation requestVikingsCompensation;

    private final RequestEquipmentTransaction requestEquipmentTransaction;
    private final RequestEquipmentCompensation requestEquipmentCompensation;

    private final StartSailFightTransaction startSailFightTransaction;
    private final StartSailFightCompensation startSailFightCompensation;

    public void attachTo(Sail sail) {
        val vikingsStep = new StepAction<Sail>(requestVikingsTransaction, requestVikingsCompensation);
        sail.addStep(SailStep.VIKING_REQUESTED, vikingsStep);

        val equipmentStep = new StepAction<Sail>(requestEquipmentTransaction, requestEquipmentCompensation);
        sail.addStep(SailStep.EQUIPMENT_REQUESTED, equipmentStep);

        val sailFightSetep = new StepAction<Sail>(startSailFightTransaction, startSailFightCompensation);
        sail.addStep(SailStep.SAIL_STARTED, sailFightSetep);
    }
}
