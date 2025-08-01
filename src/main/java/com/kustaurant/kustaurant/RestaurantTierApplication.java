package com.kustaurant.kustaurant;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@OpenAPIDefinition(servers = {
		@Server(url = "/", description = "Default Server URL")
})
@SpringBootApplication
@EnableScheduling
public class RestaurantTierApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestaurantTierApplication.class, args);
	}

}
