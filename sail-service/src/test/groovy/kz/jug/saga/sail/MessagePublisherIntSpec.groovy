package kz.jug.saga.sail

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.core.JmsTemplate
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration
class MessagePublisherIntSpec extends AbstractContextSpec {
    @Autowired
    MessagePublisher messagePublisher
    @Autowired
    JmsTemplate jmsTemplate

    def "should publish passed message"() {
        given: "a message to be published"
        def message = "some value should be here"
        and: "it must be published in the channel"
        def queueName = "vikings"

        when: "the message is published"
        messagePublisher.send(queueName, message)

        then: "the message is successfully sent"
        jmsTemplate.receive(queueName)
    }
}
