package kz.jug.saga.sail.control.vikings

import kz.jug.saga.sail.MessagePublisher
import kz.jug.saga.sail.control.JsonSerializer
import kz.jug.saga.sail.entity.Sail
import kz.jug.saga.sail.entity.SailState
import kz.jug.saga.sail.entity.events.Viking
import kz.jug.saga.sail.entity.events.outgoing.VikingsDismissedEvent
import spock.lang.Specification

class RequestVikingsCompensationSpec extends Specification {
    def messagePublisher = Mock(MessagePublisher)
    def queueName = "vikings"
    def queueConfig = new VikingsQueueConfig(queueName, receiveQueueName)
    def jsonSerializer = Stub(JsonSerializer)

    def requestVikingsCompensation = new RequestVikingsCompensation(messagePublisher, queueConfig, jsonSerializer)


    def "should send a message about dismissed vikings" () {
        given: "a sail where we are going to dismiss vikings"
        def transactionId = "4555ert89"
        def state = new SailState(vikingsNumber: 45)
        def vikings = [new Viking(id: 17, name: "Rollo"), new Viking(id: 1, name: "Bjorn")]
        def sail = new Sail(transactionId: transactionId, state: state, vikings: vikings)
        and: "the event is parsed to JSON"
        def json = "dismissal JSON"
        jsonSerializer.serializeToJson(new VikingsDismissedEvent(transactionId, vikings)) >> json

        when: "transaction is started"
        requestVikingsCompensation.accept(sail)

        then: "a message must be sent to vikings service"
        1 * messagePublisher.send(queueName, json)
    }
}
