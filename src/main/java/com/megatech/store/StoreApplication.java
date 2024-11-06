package com.megatech.store;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StoreApplication {

	public static void main(String[] args) {
		System.out.printf("=== INITIALIZING === PROFILE %s %n", System.getenv("PROFILE"));
		SpringApplication.run(StoreApplication.class, args);
	}

}
