package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableJpaRepositories("com.infrastructure.*.Persistance")
@EntityScan("com.domain")
@SpringBootApplication(scanBasePackages = "com")
public class Application {

	static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
