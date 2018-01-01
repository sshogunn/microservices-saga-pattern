package kz.jug.saga.sail.entity.saga;

import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class StepAction<T extends Saga> {
    private final Consumer<T> action;
    private final Consumer<T> compensation;

    public void runAction(T param) {
        this.action.accept(param);
    }

    public void runCompensation(T param) {
        this.compensation.accept(param);
    }
}
