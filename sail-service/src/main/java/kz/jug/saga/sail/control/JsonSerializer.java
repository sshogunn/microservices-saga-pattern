package kz.jug.saga.sail.control;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JsonSerializer {
    private static final Logger LOG = LoggerFactory.getLogger(JsonSerializer.class);

    public <T> String serializeToJson(T object) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonInString = mapper.writeValueAsString(object);
            LOG.debug("Serialized message payload: {}", jsonInString);
            return jsonInString;
        } catch (JsonProcessingException e) {
            throw new JsonSerializeException("Serialization to JSON failed", e);
        }
    }

    static class JsonSerializeException extends RuntimeException {
        JsonSerializeException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
