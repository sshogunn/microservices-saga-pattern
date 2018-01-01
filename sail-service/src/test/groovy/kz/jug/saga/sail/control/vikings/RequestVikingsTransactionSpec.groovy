package kz.jug.saga.sail.control.vikings

import kz.jug.saga.sail.MessagePublisher
import kz.jug.saga.sail.control.JsonSerializer
import kz.jug.saga.sail.entity.Sail
import kz.jug.saga.sail.entity.SailState
import kz.jug.saga.sail.entity.events.outgoing.VikingsRequestedEvent
import spock.lang.Specification

class RequestVikingsTransactionSpec extends Specification {
    def messagePublisher = Mock(MessagePublisher)
    def queueName = "vikings"
    def queueConfig = new VikingsQueueConfig(queueName, receiveQueueName)
    def jsonSerializer = Stub(JsonSerializer)
    def requestVikingsTransaction = new RequestVikingsTransaction(messagePublisher, queueConfig, jsonSerializer)

    def "should send a message about requested vikings" () {
        given: "a sail where we are going to request vikings"
        def transactionId = "4555ert89"
        def vikingsNumber = 45
        def state = new SailState(vikingsNumber: vikingsNumber)
        def sail = new Sail(transactionId: transactionId, state: state)
        and: "the event is parsed to JSON"
        def json = "JSON"
        jsonSerializer.serializeToJson(new VikingsRequestedEvent(transactionId, vikingsNumber)) >> json

        when: "transaction is started"
        requestVikingsTransaction.accept(sail)

        then: "a message must be sent to vikings service"
        1 * messagePublisher.send(queueName, json)
    }
}
