package kz.jug.saga.sail.control.fight

import kz.jug.saga.sail.MessagePublisher
import kz.jug.saga.sail.control.JsonSerializer
import kz.jug.saga.sail.entity.EquipmentType
import kz.jug.saga.sail.entity.Sail
import kz.jug.saga.sail.entity.SailState
import kz.jug.saga.sail.entity.events.Equipment
import kz.jug.saga.sail.entity.events.outgoing.VikingsSailStartedEvent
import spock.lang.Specification

class StartSailFightTransactionSpec extends Specification {
    def messagePublisher = Mock(MessagePublisher)
    def queueName = "sail-fight"
    def queueConfig = new SailFightQueueConfig(queueName)
    def jsonSerializer = Stub(JsonSerializer)
    def sailFightTransaction = new StartSailFightTransaction(messagePublisher, queueConfig, jsonSerializer)

    def "should send a message about started sail fight"() {
        given: "a sail where we are going to request equipment for vikings"
        def transactionId = "4555esRT"

        def vikings = [78L, 80L, 99L]
        def equipment = [new Equipment(id: 45L), new Equipment(id: 42L)]
        def state = new SailState(preferredEquipment: EquipmentType.ELITE)
        def sail = new Sail(transactionId: transactionId, state: state, vikings: vikings, vikingsEquipment: equipment)

        and: "the event is parsed to JSON"
        def json = "JSON Sail Fight"
        jsonSerializer.serializeToJson(new VikingsSailStartedEvent(transactionId, vikings, equipment)) >> json

        when: "transaction is started"
        sailFightTransaction.accept(sail)

        then: "a message must be sent to sail fight service"
        1 * messagePublisher.send(queueName, json)
    }

}
