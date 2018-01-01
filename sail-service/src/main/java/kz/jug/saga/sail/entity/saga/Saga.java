package kz.jug.saga.sail.entity.saga;

public interface Saga<S extends SagaState, Step extends Enum> {
    S getState();

    Saga<S, Step> addStep(Step step, StepAction action);

    void moveTo(Step step);

    void compensateTill(Step step);
}
