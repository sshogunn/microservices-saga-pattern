package kz.jug.saga.sail.control.equipment;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class EquipmentQueueConfig {
    private final String sendQueueName;
    private final String replyQueueName;

    public EquipmentQueueConfig(
            @Value("${activemq.queue.equipment.send}") String sendQueueName,
            @Value("${activemq.queue.equipment.reply}") String replyQueueName) {
        this.sendQueueName = sendQueueName;
        this.replyQueueName = replyQueueName;
    }
}
