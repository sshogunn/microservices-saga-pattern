package kz.jug.saga.sail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessagePublisher {
    private final JmsTemplate jmsTemplate;

    public void send(String destination, String message) {
        log.info("sending message='{}' to destination='{}'", message, destination);
        this.jmsTemplate.convertAndSend(destination, message);
    }
}
