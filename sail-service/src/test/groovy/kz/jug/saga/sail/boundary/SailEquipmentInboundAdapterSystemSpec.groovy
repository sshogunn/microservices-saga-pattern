package kz.jug.saga.sail.boundary

import com.fasterxml.jackson.databind.ObjectMapper
import kz.jug.saga.sail.AbstractContextSpec
import kz.jug.saga.sail.control.JsonSerializer
import kz.jug.saga.sail.control.equipment.EquipmentQueueConfig
import kz.jug.saga.sail.control.fight.SailFightQueueConfig
import kz.jug.saga.sail.control.vikings.VikingsQueueConfig
import kz.jug.saga.sail.entity.*
import kz.jug.saga.sail.entity.events.ApprovalStatus
import kz.jug.saga.sail.entity.events.Equipment
import kz.jug.saga.sail.entity.events.incoming.EquipmentReturnedEvent
import kz.jug.saga.sail.entity.events.outgoing.VikingsDismissedEvent
import kz.jug.saga.sail.entity.events.outgoing.VikingsSailStartedEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.jms.core.JmsTemplate
import spock.util.concurrent.PollingConditions

class SailEquipmentInboundAdapterSystemSpec extends AbstractContextSpec {

    @Autowired
    JmsTemplate jmsTemplate

    @Autowired
    MongoTemplate mongoTemplate

    @Autowired
    EquipmentQueueConfig equipmentQueueConfig

    @Autowired
    VikingsQueueConfig vikingsQueueConfig

    @Autowired
    SailFightQueueConfig sailFightQueueConfig

    def jsonSerializer = new JsonSerializer()
    def conditions = new PollingConditions(timeout: 10, initialDelay: 1.5, factor: 1.25)
    def objectMapper = new ObjectMapper()

    def "should send a request about vikings with equipment available for sail fight"() {
        given: "sail is present in database"
        def sailTransaction = "SAIL-478s8e"
        def sail = prepareSail(sailTransaction, SailStep.EQUIPMENT_REQUESTED)

        and: "an event about returned equipment"
        def equipment = [new Equipment(vikingId: 786L, equipmentType: EquipmentType.ELITE, id: 45L)]
        def event = new EquipmentReturnedEvent(sailTransaction, ApprovalStatus.APPROVED, equipment)

        when: "equipment response was received"
        jmsTemplate.convertAndSend(equipmentQueueConfig.replyQueueName, jsonSerializer.serializeToJson(event))

        then: "equipment info has been saved in DB"
        conditions.eventually {
            and: "sail has been updated in the repository"
            checkSailSaved(SailStep.SAIL_STARTED, sail.transactionId)

            and: "a message about started sail fight has been sent"
            def message = jmsTemplate.receive(sailFightQueueConfig.sendQueueName)
            def json = message.text as String
            def sentEvent = objectMapper.readValue(json, VikingsSailStartedEvent.class) as VikingsSailStartedEvent
            assert sentEvent.transactionId == sailTransaction
            assert sentEvent.vikingsIds == sail.vikings
            assert sentEvent.equipment == equipment
        }
    }

    def "should send roll back vikings request due lack of equipment"() {
        given: "sail is present in database"
        def sailTransaction = "SAIL-478s8e"
        def sail = prepareSail(sailTransaction, SailStep.EQUIPMENT_REQUESTED)

        and: "an event about unavailable equipment"
        def event = new EquipmentReturnedEvent(sailTransaction, ApprovalStatus.NOT_APPROVED)

        when: "equipment response was received"
        jmsTemplate.convertAndSend(equipmentQueueConfig.replyQueueName, jsonSerializer.serializeToJson(event))

        then: "sail state was rolled back"
        conditions.eventually {
            and: "sail has been updated in the repository"
            checkSailSaved(SailStep.INITIATED, sail.transactionId)

            and: "a message about started sail fight has been sent"
            def message = jmsTemplate.receive(vikingsQueueConfig.sendQueueName)
            def json = message.text as String
            def sentEvent = objectMapper.readValue(json, VikingsDismissedEvent.class) as VikingsDismissedEvent
            assert sentEvent.transactionId == sailTransaction
            assert sentEvent.vikings == sail.vikings
        }
    }

    def prepareSail(String sailTransaction, SailStep step) {
        def sail = new Sail(transactionId: sailTransaction, step: step)
        sail.id = 1L
        sail.state = new SailState(
                type: "sail-saga",
                vikingsNumber: 10,
                sailType: SailType.NEARBY,
                preferredEquipment: EquipmentType.STANDARD,
                valkyrieType: ValkyrieType.DEFAULT
        )
        mongoTemplate.save(sail)
        sail
    }

    def checkSailSaved(SailStep expectedStep, String transactionId) {
        def savedSails = mongoTemplate.findAll(Sail.class)
        assert savedSails.size() == 1
        def savedSail = savedSails[0]
        assert savedSail.transactionId == transactionId
        assert savedSail.step == expectedStep
    }
}