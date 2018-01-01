package kz.jug.saga.sail.control.equipment;

import kz.jug.saga.sail.MessagePublisher;
import kz.jug.saga.sail.control.JsonSerializer;
import kz.jug.saga.sail.entity.Sail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestEquipmentCompensation implements Consumer<Sail> {
    private final MessagePublisher messagePublisher;
    private final EquipmentQueueConfig equipmentQueueConfig;
    private final JsonSerializer jsonSerializer;

    @Override
    public void accept(Sail sail) {
        log.info("Unrequested {} units of equipment for sailing. The event will be sent into equipment service.", sail.getVikings().size());
        val event = sail.requestEquipmentBack();
        this.messagePublisher.send(this.equipmentQueueConfig.getSendQueueName(), this.jsonSerializer.serializeToJson(event));
    }
}
