package com.veersa.eventApp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.userdetails.UserDetailsService;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class EventAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventAppApplication.class, args);
	}

}
