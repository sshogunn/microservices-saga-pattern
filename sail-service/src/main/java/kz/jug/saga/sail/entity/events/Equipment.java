package kz.jug.saga.sail.entity.events;

import kz.jug.saga.sail.entity.EquipmentType;
import lombok.Data;

import java.io.Serializable;

@Data
public class Equipment implements Serializable {
    private Long id;
    private Long vikingId;
    private EquipmentType equipmentType;
}
