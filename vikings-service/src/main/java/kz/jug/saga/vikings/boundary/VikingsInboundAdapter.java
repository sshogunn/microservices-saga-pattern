package kz.jug.saga.vikings.boundary;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VikingsInboundAdapter {

    @RabbitListener(queues = "vikingsQueue")
    public void receive(String message) {
    }
}
