package kz.jug.saga.sail.entity.saga

import lombok.Getter
import lombok.Setter
import spock.lang.Specification

import java.util.function.Consumer

class AbstractSagaSpec extends Specification {
    def fakeTransaction = Stub(Consumer)
    def firstStepCompensation = Mock(Consumer)
    def secondStepCompensation = Mock(Consumer)

    def "compensate till should execute compensations for all steps before passed step"() {
        given: "saga object with some predefined steps and step actions"
        def saga = new TestSaga()
        saga.addStep(TestStep.FIRST, new StepAction<TestStep>(TestStep.FIRST, fakeTransaction, firstStepCompensation))
        saga.addStep(TestStep.SECOND, new StepAction<TestStep>(TestStep.SECOND, fakeTransaction, secondStepCompensation))
        and: "current state of the saga is at the third step"
        saga.setStep(TestStep.THIRD)
        when: "all local transactions till third step must be rolled back"
        saga.compensateTill(TestStep.THIRD)

        then: "first and second step compensations must be executed"
        1 * firstStepCompensation.accept(saga)
        1 * secondStepCompensation.accept(saga)
    }

    def "compensate till should not execute compensation for failed transaction"() {
        given: "saga object with some predefined steps and step actions"
        def saga = new TestSaga()
        saga.addStep(TestStep.FIRST, new StepAction<TestStep>(TestStep.FIRST, fakeTransaction, firstStepCompensation))
        saga.addStep(TestStep.SECOND, new StepAction<TestStep>(TestStep.SECOND, fakeTransaction, secondStepCompensation))
        and: "current state of the saga is at the second step"
        saga.setStep(TestStep.SECOND)
        when: "all local transactions till second step must be rolled back"
        saga.compensateTill(TestStep.SECOND)

        then: "only previous transactions must be rolled back"
        1 * firstStepCompensation.accept(saga)
        0 * secondStepCompensation.accept(saga)
    }

    class TestSaga extends AbstractSaga<TestSagaState, TestStep> {
    }

    @Getter
    @Setter
    class TestSagaState implements SagaState {
        String type
    }

    enum TestStep {
        FIRST,
        SECOND,
        THIRD
    }
}
