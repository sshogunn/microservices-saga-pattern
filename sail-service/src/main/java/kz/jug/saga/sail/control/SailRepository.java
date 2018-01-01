package kz.jug.saga.sail.control;

import kz.jug.saga.sail.entity.Sail;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SailRepository extends MongoRepository<Sail, Long> {
    Sail findByTransactionId(String id);
}
