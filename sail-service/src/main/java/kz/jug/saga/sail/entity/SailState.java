package kz.jug.saga.sail.entity;

import kz.jug.saga.sail.entity.saga.SagaState;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SailState implements SagaState {
    private String type;
    private int vikingsNumber;
    private SailType sailType;
    private EquipmentType preferredEquipment;
}
