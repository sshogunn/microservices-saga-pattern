package kz.jug.saga.sail.control;

import kz.jug.saga.sail.entity.events.incoming.EquipmentReturnedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SailEquipmentService {
    private final SailRepository sailRepository;
    private final SailSagaTransactions sailSagaTransactions;

    public void addEquipment(EquipmentReturnedEvent equipmentReturnedEvent) {
        val sail = this.sailRepository.findByTransactionId(equipmentReturnedEvent.getTransactionId());
        this.sailSagaTransactions.attachTo(sail);
        sail.addEquipment(equipmentReturnedEvent);
        sail.cleanSteps();
        this.sailRepository.save(sail);
    }
}
