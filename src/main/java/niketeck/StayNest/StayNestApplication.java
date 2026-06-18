package niketeck.StayNest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.ai.vectorstore.pgvector.autoconfigure.PgVectorStoreAutoConfiguration;

@SpringBootApplication(exclude = {PgVectorStoreAutoConfiguration.class})
public class StayNestApplication {

	public static void main(String[] args) {
		SpringApplication.run(StayNestApplication.class, args);

	}


}
