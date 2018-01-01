package kz.jug.saga.sail.control

import kz.jug.saga.sail.entity.*
import kz.jug.saga.sail.entity.events.ApprovalStatus
import kz.jug.saga.sail.entity.events.Viking
import kz.jug.saga.sail.entity.events.incoming.VikingsApprovalProcessedEvent
import kz.jug.saga.sail.entity.saga.StepAction
import spock.lang.Specification

import java.util.function.Consumer

class SailVikingsServiceSpec extends Specification {
    def repo = Mock(SailRepository)
    def sailSagaTransactions = Mock(SailSagaTransactions)
    def service = new SailVikingsService(repo, sailSagaTransactions)
    def transaction = Mock(Consumer)

    def "should save all passed state in saga repository"() {
        given: "all information needed for vikings sail"
        def vikingsNumber = 152
        def sailType = SailType.FAR_A_WAY
        def preferredEquipment = EquipmentType.EXTENDED
        def valkyrieType = ValkyrieType.DEFAULT

        and: "transactions must be prepared for the saga"
        sailSagaTransactions.attachTo(_ as Sail) >> { arguments ->
            def saga = arguments[0] as Sail
            def action = new StepAction<Sail>(new FakeAction(), new FakeAction())
            saga.addStep(SailStep.VIKING_REQUESTED, action)
            saga
        }

        when: "when the sail is started"
        service.startSail(vikingsNumber, sailType, preferredEquipment, valkyrieType)

        then: "it must be saved in the repository"
        1 * repo.save(*_) >> { arguments ->
            def sailSaga = arguments[0] as Sail
            assert sailSaga.state.vikingsNumber == vikingsNumber
            assert sailSaga.state.sailType == sailType
            assert sailSaga.state.preferredEquipment == preferredEquipment
            assert sailSaga.state.valkyrieType == valkyrieType
        }
    }

    def "should call vikings request transaction"() {
        given: "all information needed for vikings sail"
        def vikingsNumber = 152
        def sailType = SailType.FAR_A_WAY
        def preferredEquipment = EquipmentType.LORD
        def valkyrieType = ValkyrieType.EXCLUSIVE
        and: "we have some predefined attached transactions"
        sailSagaTransactions.attachTo(_ as Sail) >> { Sail s ->
            def action = new StepAction<Sail>(transaction, new FakeAction())
            s.addStep(SailStep.VIKING_REQUESTED, action)
        }

        when: "when the sail is started"
        service.startSail(vikingsNumber, sailType, preferredEquipment, valkyrieType)

        then: "vikings transaction must be started"
        1 * transaction.accept(_ as Sail) >> { Sail s ->
            def state = s.state
            assert state.preferredEquipment == preferredEquipment
            assert state.vikingsNumber == vikingsNumber
            assert state.sailType == sailType
            assert state.valkyrieType == valkyrieType
        }
    }

    def "add vikings should load saga by transaction id and add incoming vikings in the sail"() {
        given: "response from vikings service"
        def transactionId = "ER-45211em"
        def vikings = [new Viking(id: 10), new Viking(id: 89)]
        def response = new VikingsApprovalProcessedEvent(transactionId, ApprovalStatus.APPROVED, vikings)
        and: "a sail that is saved in the repository"
        def sail = new Sail(transactionId: transactionId)
        repo.findByTransactionId(transactionId) >> sail

        and: "we have some predefined attached transactions"
        sailSagaTransactions.attachTo(_ as Sail) >> { Sail s -> attachEquipmentTransaction(s) }

        when: "response from vikings service is requested to be processed"
        service.addVikings(response)

        then: "all vikings must be added in the sail"
        sail.vikings == vikings*.id
    }

    def "add vikings should save loaded saga after all changes"() {
        given: "response from vikings service"
        def transactionId = "ER-45211em"
        def vikings = [new Viking(), new Viking()]
        def response = new VikingsApprovalProcessedEvent(transactionId, ApprovalStatus.APPROVED, vikings)
        and: "a sail that is saved in the repository"
        def sail = new Sail(transactionId: transactionId)
        repo.findByTransactionId(transactionId) >> sail

        and: "we have some predefined attached transactions"
        sailSagaTransactions.attachTo(_ as Sail) >> { Sail s -> attachEquipmentTransaction(s) }

        when: "response from vikings service is requested to be processed"
        service.addVikings(response)

        then: "the saga must be saved in the repository"
        1 * repo.save(sail)
    }

    def attachEquipmentTransaction(Sail s) {
        def action = new StepAction<Sail>(transaction, new FakeAction())
        s.addStep(SailStep.EQUIPMENT_REQUESTED, action)
    }
}
