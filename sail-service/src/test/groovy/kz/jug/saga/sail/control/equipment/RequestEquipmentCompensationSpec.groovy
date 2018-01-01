package kz.jug.saga.sail.control.equipment

import kz.jug.saga.sail.MessagePublisher
import kz.jug.saga.sail.control.JsonSerializer
import kz.jug.saga.sail.entity.EquipmentType
import kz.jug.saga.sail.entity.Sail
import kz.jug.saga.sail.entity.SailState
import kz.jug.saga.sail.entity.events.outgoing.EquipmentRequestedBackEvent
import spock.lang.Specification

class RequestEquipmentCompensationSpec extends Specification {
    def messagePublisher = Mock(MessagePublisher)
    def queueName = "equipment"
    def queueConfig = new EquipmentQueueConfig(queueName, replyQueueName)
    def jsonSerializer = Stub(JsonSerializer)

    def requestVikingsCompensation = new RequestEquipmentCompensation(messagePublisher, queueConfig, jsonSerializer)

    def "should send a message about requesting equipment back"() {
        given: "a sail where we are going to request equipment for vikings"
        def transactionId = "4555esRT"
        def equipmentType = EquipmentType.KING
        def state = new SailState(preferredEquipment: equipmentType)
        def vikings = [26L, 40L, 78L]
        def sail = new Sail(transactionId: transactionId, state: state, vikings: vikings)
        and: "the event is parsed to JSON"
        def json = "JSON Equipment"
        jsonSerializer.serializeToJson(new EquipmentRequestedBackEvent(transactionId)) >> json

        when: "transaction is started"
        requestVikingsCompensation.accept(sail)

        then: "a message must be sent to vikings service"
        1 * messagePublisher.send(queueName, json)
    }
}
