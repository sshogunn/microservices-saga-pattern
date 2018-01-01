package kz.jug.saga.sail.control

import kz.jug.saga.sail.AbstractSpecification
import kz.jug.saga.sail.entity.EquipmentType
import kz.jug.saga.sail.entity.Sail
import kz.jug.saga.sail.entity.SailState
import kz.jug.saga.sail.entity.SailType

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration
@DataMongoTest
class SailRepositoryIntSpec extends AbstractSpecification {
    @Autowired
    SailRepository sagaRepository
    @Autowired
    MongoTemplate mongoTemplate

    def "should save new sail-saga in the repository"() {
        given: "newly created sail with predefined transaction id"
        def sailTransaction = "SAIL-4588"
        def sail = new Sail(sailTransaction)

        when: "the sail is saved"
        sagaRepository.save(sail)

        then: "it must available in mongo repo"
        def savedSails = mongoTemplate.findAll(Sail.class)
        savedSails.size() == 1
        savedSails[0].transactionId == sail.transactionId
    }

    def "should fully filled new sail-saga in the repository"() {
        given: "newly created sail with predefined transaction id"
        def sailTransaction = "SAIL-4588"
        def sail = new Sail(sailTransaction)
        sail.state = new SailState(
                type: "sail-saga",
                vikingsNumber: 10,
                sailType: SailType.NEARBY,
                preferredEquipment: EquipmentType.STANDARD,
                valkyrieType: ValkyrieType.DEFAULT
        )

        when: "the sail is saved"
        sagaRepository.save(sail)

        then: "it must available in mongo repo"
        def savedSails = mongoTemplate.findAll(Sail.class)
        savedSails.size() == 1
        def savedSail = savedSails[0]
        savedSail.transactionId == sail.transactionId
        savedSail.state == sail.state
    }

    def "should return sail by transaction id"() {
        given: "pre saved in mongo repository sail"
        def sailTransaction = "SAIL-9512"
        def sail = new Sail(sailTransaction)
        mongoTemplate.save(sail)

        when: "searching a sail by transaction id is started"
        def foundSail = sagaRepository.findByTransactionId(sailTransaction)

        then: "found sail must be returned"
        foundSail.transactionId == sail.transactionId
    }
}
