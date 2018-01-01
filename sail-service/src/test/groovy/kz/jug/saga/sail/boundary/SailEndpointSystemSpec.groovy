package kz.jug.saga.sail.boundary

import com.fasterxml.jackson.databind.ObjectMapper
import kz.jug.saga.sail.AbstractContextSpec
import kz.jug.saga.sail.control.vikings.VikingsQueueConfig
import kz.jug.saga.sail.entity.EquipmentType
import kz.jug.saga.sail.entity.Sail
import kz.jug.saga.sail.entity.SailType

import kz.jug.saga.sail.entity.events.outgoing.VikingsRequestedEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.HttpStatus
import org.springframework.jms.core.JmsTemplate

class SailEndpointSystemSpec extends AbstractContextSpec {
    @Autowired
    TestRestTemplate restTemplate

    @Autowired
    JmsTemplate jmsTemplate

    @Autowired
    MongoTemplate mongoTemplate

    @Autowired
    VikingsQueueConfig vikingsQueueConfig

    def objectMapper = new ObjectMapper()

    def "should start sail process and start request process"() {
        given: "request params"
        def sailRequest = new SailRequest(
                vikingsNumber: 10,
                equipment: EquipmentType.STANDARD,
                sailType: SailType.NEARBY,
                valkyrieType: ValkyrieType.DEFAULT
        )

        when: "sail process has been started"
        def result = restTemplate.postForEntity("/sails", sailRequest, String.class)

        then: "the request has been accepted"
        result.statusCode == HttpStatus.ACCEPTED

        and: "it must available in mongo repo"
        def savedSails = mongoTemplate.findAll(Sail.class)
        savedSails.size() == 1
        def savedSail = savedSails[0]
        savedSail.transactionId

        and: "an event about requested vikings has been sent"
        def message = jmsTemplate.receive(vikingsQueueConfig.sendQueueName)
        def sentEvent = objectMapper.readValue(message.text, VikingsRequestedEvent.class) as VikingsRequestedEvent
        sentEvent.vikingsNumber == 10
    }
}
