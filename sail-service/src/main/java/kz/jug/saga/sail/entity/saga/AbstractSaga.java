package kz.jug.saga.sail.entity.saga;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@NoArgsConstructor
public abstract class AbstractSaga<S extends SagaState, Step extends Enum> implements Saga<S, Step> {
    @Setter
    private S state;
    @Indexed
    @Setter
    private Step step;
    private transient Map<Step, StepAction> stepActions = new HashMap<>();

    protected AbstractSaga(S state) {
        this.state = state;
    }

    @Override
    public Saga<S, Step> addStep(Step step, StepAction action) {
        this.stepActions.put(step, action);
        return this;
    }

    @Override
    public void moveTo(Step step) {
        this.step = step;
        Optional.ofNullable(this.stepActions.get(step))
                .ifPresent(stepAction -> stepAction.runAction(this));
    }

    @Override
    public void compensateTill(Step step) {
        int compensateTill = step.ordinal();
        int compensateStart = this.step.ordinal();
        this.stepActions.entrySet()
                .stream()
                .filter(e -> {
                    Step compensationStep = e.getKey();
                    int compensationStepOrder = compensationStep.ordinal();
                    return compensationStepOrder > compensateTill && compensationStepOrder < compensateStart;
                })
                .forEach(es -> es.getValue().runCompensation(this));
        this.step = step;
    }

    public void cleanSteps() {
        this.stepActions.clear();
    }
}
