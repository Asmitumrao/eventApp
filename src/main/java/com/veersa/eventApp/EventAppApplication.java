package com.veersa.eventApp;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;

@SpringBootApplication
public class EventAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventAppApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(UserDetailsService userDetailsService) {
		return args -> {
			System.out.println("UserDetailsService loaded: " + userDetailsService.getClass().getName());
		};
	}

}
