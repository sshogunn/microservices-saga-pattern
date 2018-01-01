package kz.jug.saga.sail.boundary

import com.fasterxml.jackson.databind.ObjectMapper
import kz.jug.saga.sail.AbstractContextSpec
import kz.jug.saga.sail.control.JsonSerializer
import kz.jug.saga.sail.control.equipment.EquipmentQueueConfig
import kz.jug.saga.sail.control.vikings.VikingsQueueConfig
import kz.jug.saga.sail.entity.*
import kz.jug.saga.sail.entity.events.ApprovalStatus
import kz.jug.saga.sail.entity.events.Viking
import kz.jug.saga.sail.entity.events.incoming.VikingsApprovalProcessedEvent
import kz.jug.saga.sail.entity.events.outgoing.EquipmentRequestedEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.jms.core.JmsTemplate
import spock.util.concurrent.PollingConditions

class SailVikingsInboundAdapterSystemSpec extends AbstractContextSpec {
    @Autowired
    JmsTemplate jmsTemplate

    @Autowired
    MongoTemplate mongoTemplate

    @Autowired
    VikingsQueueConfig vikingsQueueConfig

    @Autowired
    EquipmentQueueConfig equipmentQueueConfig

    def jsonSerializer = new JsonSerializer()
    def conditions = new PollingConditions(timeout: 10, initialDelay: 1.5, factor: 1.25)
    def objectMapper = new ObjectMapper()

    def "should request equipment when vikings are available"() {
        given: "sail is present in database"
        def sailTransaction = "SAIL-9512"
        def sail = new Sail(sailTransaction)
        sail.id = 1L
        sail.state = new SailState(
                type: "sail-saga",
                vikingsNumber: 10,
                sailType: SailType.NEARBY,
                preferredEquipment: EquipmentType.STANDARD,
                valkyrieType: ValkyrieType.DEFAULT
        )
        mongoTemplate.save(sail)

        and: "an event about available vikings"
        def id = 26L
        def vikings = [new Viking(id: id, name: "Ivar")]
        def event = new VikingsApprovalProcessedEvent(sailTransaction, ApprovalStatus.APPROVED, vikings)

        when: "vikings response was received"
        jmsTemplate.convertAndSend(vikingsQueueConfig.replyQueueName, jsonSerializer.serializeToJson(event))

        then: "vikings info has been saved in DB"
        conditions.eventually {
            def savedSails = mongoTemplate.findAll(Sail.class)
            assert savedSails.size() == 1
            def savedSail = savedSails[0]
            assert savedSail.transactionId == sail.transactionId
            assert savedSail.vikings == [id]

            def message = jmsTemplate.receive(equipmentQueueConfig.sendQueueName)
            def json = message.text as String
            def sentEvent = objectMapper.readValue(json, EquipmentRequestedEvent.class) as EquipmentRequestedEvent
            assert sentEvent.vikingsNumber == 1
            assert sentEvent.preferredEquipment == sail.state.preferredEquipment
        }
    }
}
