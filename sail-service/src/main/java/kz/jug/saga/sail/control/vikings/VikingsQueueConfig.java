package kz.jug.saga.sail.control.vikings;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class VikingsQueueConfig {
    private final String sendQueueName;
    private final String replyQueueName;

    public VikingsQueueConfig(
            @Value("${activemq.queue.vikings.send}") String sendQueueName,
            @Value("${activemq.queue.vikings.reply}") String replyQueueName) {
        this.sendQueueName = sendQueueName;
        this.replyQueueName = replyQueueName;
    }
}
