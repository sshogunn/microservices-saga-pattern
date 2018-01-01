package kz.jug.saga.sail.entity.events;

import lombok.*;

import java.io.Serializable;

@Data
public abstract class AbstractEvent implements Serializable {
    private String transactionId;

    protected AbstractEvent() {
    }

    public AbstractEvent(String transactionId) {
        this.transactionId = transactionId;
    }
}
