package kz.jug.saga.sail.entity.saga;

public interface SagaState {
    String getType();

    void setType(String type);
}
