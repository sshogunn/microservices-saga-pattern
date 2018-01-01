package kz.jug.saga.sail.control.vikings;

import kz.jug.saga.sail.MessagePublisher;
import kz.jug.saga.sail.control.JsonSerializer;
import kz.jug.saga.sail.entity.Sail;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class RequestVikingsCompensation implements Consumer<Sail> {
    private static final Logger LOG = LoggerFactory.getLogger(RequestVikingsCompensation.class);
    private final MessagePublisher messagePublisher;
    private final VikingsQueueConfig vikingsQueueConfig;
    private final JsonSerializer jsonSerializer;

    @Override
    public void accept(Sail sail) {
        LOG.info("Dismissed {} vikings from  sailing {}. The event will be sent into vikings service.", sail.getVikings(), sail.getTransactionId());
        val dismissedEvent = sail.dismissVikings();
        this.messagePublisher.send(this.vikingsQueueConfig.getSendQueueName(), this.jsonSerializer.serializeToJson(dismissedEvent));
    }
}
