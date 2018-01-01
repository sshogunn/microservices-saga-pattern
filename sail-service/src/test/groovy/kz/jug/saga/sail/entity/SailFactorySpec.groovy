package kz.jug.saga.sail.entity

import kz.jug.saga.sail.control.SailSagaTransactions
import spock.lang.Specification

class SailFactorySpec extends Specification {
    def sailSagaTransactions = Mock(SailSagaTransactions)
    def sailFactory = new SailFactory(sailSagaTransactions)

    def "should return new saga with built state"() {
        given: "required for initial saga state information"
        def vikingsNumber = 152
        def sailType = SailType.NEARBY
        def preferredEquipment = EquipmentType.EXTENDED
        def valkyrieType = ValkyrieType.EXCLUSIVE

        when: "creating new saga is requested"
        def sail = sailFactory.createSail(vikingsNumber, sailType, preferredEquipment, valkyrieType)

        then: "it must return it with built state"
        def sailState = sail.state
        sailState.type == "sail-saga"
        sailState.valkyrieType == valkyrieType
        sailState.preferredEquipment == preferredEquipment
        sailState.sailType == sailType
        sailState.vikingsNumber == vikingsNumber
    }

    def "should attach all required transactions and compensations to any new sail"() {
        given: "required for initial saga state information"
        def vikingsNumber = 1
        def sailType = SailType.FAR_A_WAY
        def preferredEquipment = EquipmentType.LORD
        def valkyrieType = ValkyrieType.DEFAULT

        when: "creating new saga is requested"
        sailFactory.createSail(vikingsNumber, sailType, preferredEquipment, valkyrieType)

        then: "transactions and compensations must be attached"
        1 * sailSagaTransactions.attachTo(_ as Sail) >> { arguments ->
            def sail = arguments[0]
            def sailState = sail.state
            assert sailState.valkyrieType == valkyrieType
            assert sailState.preferredEquipment == preferredEquipment
            assert sailState.sailType == sailType
            assert sailState.vikingsNumber == vikingsNumber
        }
    }
}
