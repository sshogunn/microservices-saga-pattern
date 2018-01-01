package kz.jug.saga.sail.boundary;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.jug.saga.sail.control.SailEquipmentService;
import kz.jug.saga.sail.entity.events.incoming.EquipmentReturnedEvent;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SailEquipmentInboundAdapter {
    private final SailEquipmentService sailEquipmentService;
    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @JmsListener(destination = "${activemq.queue.equipment.reply}")
    public void receive(String message) {
        val event = extractEvent(message);
        this.sailEquipmentService.addEquipment(event);
    }

    private static EquipmentReturnedEvent extractEvent(String message) {
        try {
            return OBJECT_MAPPER.readValue(message, EquipmentReturnedEvent.class);
        } catch (IOException e) {
            throw new EventExtractionException("EquipmentReturnedEvent cannot be extracted", e);
        }
    }
}
