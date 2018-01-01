package kz.jug.saga.sail.control.fight;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SailFightQueueConfig {
    private final String sendQueueName;

    public SailFightQueueConfig(
            @Value("${activemq.queue.sail.send}") String sendQueueName) {
        this.sendQueueName = sendQueueName;
    }
}
