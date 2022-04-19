package com.tutorials.hazelcastsessionsharing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class HazelcastSessionSharingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HazelcastSessionSharingApplication.class, args);
	}

}
