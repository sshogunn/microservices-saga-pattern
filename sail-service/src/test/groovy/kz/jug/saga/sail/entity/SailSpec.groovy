package kz.jug.saga.sail.entity

import kz.jug.saga.sail.entity.events.ApprovalStatus
import kz.jug.saga.sail.entity.events.Equipment
import kz.jug.saga.sail.entity.events.Viking
import kz.jug.saga.sail.entity.events.incoming.EquipmentReturnedEvent
import kz.jug.saga.sail.entity.events.incoming.VikingsApprovalProcessedEvent
import spock.lang.Specification

class SailSpec extends Specification {

    def "should remember all vikings returned from the vikings service"() {
        given: "a sail without any vikings"
        def sail = new Sail()
        and: "reply from the vikings service"
        def reply = new VikingsApprovalProcessedEvent("455", ApprovalStatus.APPROVED, [new Viking(id: 78L, name: "Ragnar")])

        when: "reply is requested to be saved"
        sail.addVikings(reply)

        then: "the saved vikings must be registered in the sail"
        sail.vikings == reply.vikings*.id
    }

    def "should not remember vikings from vikings service if they are not available"() {
        given: "a sail without any vikings"
        def sail = new Sail()
        and: "reply from the vikings service"
        def reply = new VikingsApprovalProcessedEvent("455", ApprovalStatus.NOT_APPROVED, [new Viking(id: 78L, name: "Ragnar")])

        when: "reply is requested to be saved"
        sail.addVikings(reply)

        then: "the sail should not have any remembered vikings"
        sail.vikings.empty
    }

    def "should return vikings dismissal event with all vikings from the sail"() {
        given: "a sail with registered vikings"
        def vikings = [78L, 80L]
        def sail = new Sail(vikings: vikings)

        when: "it is requested to dismiss vikings"
        def dismissalEvent = sail.dismissVikings()

        then: "all dismissed vikings must be added in the event"
        dismissalEvent.vikings.size() == 2
    }

    def "should remove vikings from sail when all vikings dismissed"() {
        def vikings = [78L, 80L]
        def sail = new Sail(vikings: vikings)

        when: "it is requested to dismiss vikings"
        sail.dismissVikings()

        then: "vikings must be removed from the sail"
        sail.vikings == []
    }

    def "should remember equipment returned from equipment service" () {
        given: "a sail that requested equipment"
        def sail = new Sail()
        and: "reply from the vikings service"
        def reply = new EquipmentReturnedEvent("455", ApprovalStatus.APPROVED, [new Equipment(id: 78L, vikingId: 10L, equipmentType: EquipmentType.ELITE)])

        when: "new equipment is processed"
        sail.addEquipment(reply)

        then: "equipment is remembered in the sail"
        sail.vikingsEquipment == reply.equipment
    }

    def "should cancel all previous requests when equipment is not available for vikings" () {
        given: "a sail that requested equipment"
        def sail = new Sail()
        and: "reply from the vikings service"
        def reply = new EquipmentReturnedEvent("455", ApprovalStatus.NOT_APPROVED)

        when: "new equipment is processed"
        sail.addEquipment(reply)

        then: "equipment is not added"
        sail.vikingsEquipment == []

        and: "sail state is changed to initial"
        sail.step == SailStep.INITIATED
    }

    def "should prepare information about vikings for sail"() {
        given: "a sail with predefined vikings and equipment"
        def vikings = [27L, 45L]
        def equipment = [new Equipment(id: 75L), new Equipment(id: 45L)]
        def sail = new Sail(vikings: vikings, vikingsEquipment: equipment)

        when: "requested info for sail fight"
        def event = sail.prepareVikingsForSail()

        then: "requested for sail fight event has vikings and equipment"
        event.vikingsIds == vikings
        event.equipment == equipment
    }
}
