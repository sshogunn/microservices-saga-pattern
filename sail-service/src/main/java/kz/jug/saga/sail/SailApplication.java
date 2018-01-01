package kz.jug.saga.sail;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class SailApplication {

	public static void main(String[] args) {
		SpringApplication.run(SailApplication.class, args);
	}
}
