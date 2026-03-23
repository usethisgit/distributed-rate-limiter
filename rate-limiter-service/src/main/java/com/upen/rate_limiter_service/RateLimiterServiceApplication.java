package com.upen.rate_limiter_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class RateLimiterServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RateLimiterServiceApplication.class, args);
	}

}
