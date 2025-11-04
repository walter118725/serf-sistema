package com.financorp.serf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.financorp.serf.model")
@EnableJpaRepositories("com.financorp.serf.repository")
public class SerfSistemaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SerfSistemaApplication.class, args);
	}

}
