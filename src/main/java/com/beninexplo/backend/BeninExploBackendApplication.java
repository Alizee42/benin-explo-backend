package com.beninexplo.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BeninExploBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BeninExploBackendApplication.class, args);
	}

}
