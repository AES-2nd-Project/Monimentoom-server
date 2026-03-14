package com.example.monimentoom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class MonimentoomApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonimentoomApplication.class, args);
	}

}
