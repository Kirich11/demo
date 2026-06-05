package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableJpaRepositories("com.core")
@EnableTransactionManagement
@EntityScan("com.core")
@SpringBootApplication(scanBasePackages = "com")
public class Application {

	static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
