package kz.jug.saga.sail.control

import kz.jug.saga.sail.entity.Sail
import kz.jug.saga.sail.entity.SailStep
import kz.jug.saga.sail.entity.events.ApprovalStatus
import kz.jug.saga.sail.entity.events.Equipment
import kz.jug.saga.sail.entity.events.incoming.EquipmentReturnedEvent
import kz.jug.saga.sail.entity.saga.StepAction
import spock.lang.Specification

import java.util.function.Consumer

class SailEquipmentServiceSpec extends Specification {
    def repo = Mock(SailRepository)
    def sailSagaTransactions = Mock(SailSagaTransactions)
    def service = new SailEquipmentService(repo, sailSagaTransactions)
    def transaction = Mock(Consumer)

    def "should add received equipment in the found by transaction id sail"() {
        given: "response from equipment service"
        long firstVikingId = 10
        long secondVikingId = 15
        def transactionId = "ER-8955472"
        def firstEquipment = new Equipment(vikingId: firstVikingId)
        def secondEquipment = new Equipment(vikingId: secondVikingId)
        def equipment = [firstEquipment, secondEquipment]
        def response = new EquipmentReturnedEvent(transactionId, ApprovalStatus.APPROVED, equipment)
        and: "a sail that is saved in the repository"
        def sail = new Sail(transactionId: transactionId)
        repo.findByTransactionId(transactionId) >> sail

        and: "we have some predefined attached transactions"
        sailSagaTransactions.attachTo(_ as Sail) >> { Sail s -> attachSailTransaction(s) }

        when: "response from equipment service is requested to be processed"
        service.addEquipment(response)

        then: "the saga must be saved in the repository"
        1 * repo.save(_ as Sail) >> { Sail s ->
            assert s.vikingsEquipment == [firstEquipment, secondEquipment]
        }
    }

    def attachSailTransaction(Sail s) {
        def action = new StepAction<Sail>(transaction, new FakeAction())
        s.addStep(SailStep.SAIL_STARTED, action)
    }
}
