package kz.jug.saga.sail.boundary;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.jug.saga.sail.control.SailVikingsService;
import kz.jug.saga.sail.entity.events.incoming.VikingsApprovalProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SailVikingsInboundAdapter {
    private final SailVikingsService sailVikingsService;
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @JmsListener(destination = "${activemq.queue.vikings.reply}")
    public void receive(String message) {
        val event = extractEvent(message);
        this.sailVikingsService.addVikings(event);
    }

    private VikingsApprovalProcessedEvent extractEvent(String message) {
        try {
            return OBJECT_MAPPER.readValue(message, VikingsApprovalProcessedEvent.class);
        } catch (IOException e) {
            throw new EventExtractionException("VikingsApprovalProcessedEvent cannot be extracted", e);
        }
    }
}
