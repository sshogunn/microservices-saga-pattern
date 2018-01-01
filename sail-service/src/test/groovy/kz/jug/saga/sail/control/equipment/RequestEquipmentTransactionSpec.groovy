package kz.jug.saga.sail.control.equipment

import kz.jug.saga.sail.MessagePublisher
import kz.jug.saga.sail.control.JsonSerializer
import kz.jug.saga.sail.entity.EquipmentType
import kz.jug.saga.sail.entity.Sail
import kz.jug.saga.sail.entity.SailState
import kz.jug.saga.sail.entity.events.Viking
import kz.jug.saga.sail.entity.events.outgoing.EquipmentRequestedEvent
import spock.lang.Specification

class RequestEquipmentTransactionSpec extends Specification {
    def messagePublisher = Mock(MessagePublisher)
    def queueName = "equipment"
    def queueConfig = new EquipmentQueueConfig(queueName, replyQueueName)
    def jsonSerializer = Stub(JsonSerializer)
    def requestVikingsTransaction = new RequestEquipmentTransaction(messagePublisher, queueConfig, jsonSerializer)

    def "should send a message about requested equipment"() {
        given: "a sail where we are going to request equipment for vikings"
        def transactionId = "4555esRT"
        def equipmentType = EquipmentType.KING
        def state = new SailState(preferredEquipment: equipmentType)
        def vikings = [new Viking(), new Viking(), new Viking()]
        def sail = new Sail(transactionId: transactionId, state: state, vikings: vikings)
        and: "the event is parsed to JSON"
        def json = "JSON Equipment"
        jsonSerializer.serializeToJson(new EquipmentRequestedEvent(transactionId, equipmentType, vikings.size())) >> json

        when: "transaction is started"
        requestVikingsTransaction.accept(sail)

        then: "a message must be sent to vikings service"
        1 * messagePublisher.send(queueName, json)
    }
}
