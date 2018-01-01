package kz.jug.saga.sail.control.fight;

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
public class StartSailFightTransaction implements Consumer<Sail> {
    private final MessagePublisher messagePublisher;
    private final SailFightQueueConfig sailFightQueueConfig;
    private final JsonSerializer jsonSerializer;

    @Override
    public void accept(Sail sail) {
        log.info("Sail {} will be started with {} number of vikings", sail.getTransactionId(), sail.getVikings().size());
        val event = sail.prepareVikingsForSail();
        this.messagePublisher.send(this.sailFightQueueConfig.getSendQueueName(), this.jsonSerializer.serializeToJson(event));
    }
}
