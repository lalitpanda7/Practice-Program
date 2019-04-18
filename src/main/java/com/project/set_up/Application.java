package com.project.set_up;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaRepositories
@EnableScheduling
@SpringBootApplication
public class Application {
	/**
	 * main Method
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
