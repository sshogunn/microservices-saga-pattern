package kz.jug.saga.sail.control;

import kz.jug.saga.sail.entity.EquipmentType;
import kz.jug.saga.sail.entity.SailFactory;
import kz.jug.saga.sail.entity.SailType;
import kz.jug.saga.sail.entity.events.incoming.VikingsApprovalProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SailVikingsService {
    private static final Logger LOG = LoggerFactory.getLogger(SailVikingsService.class);
    private final SailRepository sailRepository;
    private final SailFactory sailFactory = new SailFactory();
    private final SailSagaTransactions sailSagaTransactions;

    public void startSail(
            int vikingsNumber,
            SailType sailType,
            EquipmentType preferredEquipment) {
        LOG.trace("Requested new sail for the following number of vikings {}", vikingsNumber);
        val sail = this.sailFactory.createSail(vikingsNumber, sailType, preferredEquipment);
        this.sailSagaTransactions.attachTo(sail);
        sail.startSail();
        sail.cleanSteps();
        this.sailRepository.save(sail);
    }

    public void addVikings(VikingsApprovalProcessedEvent event) {
        val sail = this.sailRepository.findByTransactionId(event.getTransactionId());
        this.sailSagaTransactions.attachTo(sail);
        sail.addVikings(event);
        sail.cleanSteps();
        this.sailRepository.save(sail);
    }
}
