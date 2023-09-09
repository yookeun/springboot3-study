package com.example.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Springboot3StudyApplication {

	public static void main(String[] args) {
		SpringApplication.run(Springboot3StudyApplication.class, args);
	}

}
