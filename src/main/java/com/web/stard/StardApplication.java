package com.web.stard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class StardApplication {

	public static void main(String[] args) {
		SpringApplication.run(StardApplication.class, args);
	}

}
