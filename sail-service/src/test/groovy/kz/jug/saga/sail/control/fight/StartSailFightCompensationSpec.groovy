package kz.jug.saga.sail.control.fight

import kz.jug.saga.sail.MessagePublisher
import kz.jug.saga.sail.control.JsonSerializer
import kz.jug.saga.sail.entity.Sail
import kz.jug.saga.sail.entity.events.outgoing.VikingsSailCanceledEvent
import spock.lang.Specification

class StartSailFightCompensationSpec extends Specification {
    def messagePublisher = Mock(MessagePublisher)
    def queueName = "sail-fight"
    def queueConfig = new SailFightQueueConfig(queueName)
    def jsonSerializer = Stub(JsonSerializer)
    def sailFightCompensation = new StartSailFightCompensation(messagePublisher, queueConfig, jsonSerializer)

    def "should send a message canceled sail"() {
        given: "a sail to be canceled"
        def transactionId = "45stuiRT"
        def vikings = [26L, 40L, 78L]
        def sail = new Sail(transactionId: transactionId, vikings: vikings)
        and: "the event is parsed to JSON"
        def json = "SAIL canceled"
        jsonSerializer.serializeToJson(new VikingsSailCanceledEvent(transactionId, vikings)) >> json

        when: "compensation is started"
        sailFightCompensation.accept(sail)

        then: "a message must be sent to vikings service"
        1 * messagePublisher.send(queueName, json)
    }
}
