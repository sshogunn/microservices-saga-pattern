package kz.jug.saga.sail.entity.events;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Data
@Document
public class Viking implements Serializable {
    @Id
    private Long id;
    @Indexed
    private String name;
}
