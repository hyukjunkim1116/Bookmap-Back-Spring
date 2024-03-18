package foodmap.V2;

import foodmap.V2.config.event.EventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class V2Application {
	public static void main(String[] args) {
		SpringApplication.run(V2Application.class, args);
	}

}
