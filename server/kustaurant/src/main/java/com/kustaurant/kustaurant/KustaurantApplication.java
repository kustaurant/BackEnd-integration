package com.kustaurant.kustaurant;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@OpenAPIDefinition(servers = {@Server(url = "/", description = "Default Server URL")})
@EnableScheduling
@EnableCaching
@SpringBootApplication(scanBasePackages = "com.kustaurant")
@EnableJpaRepositories(basePackages = "com.kustaurant")
@EntityScan(basePackages = "com.kustaurant")
@ConfigurationPropertiesScan
public class KustaurantApplication {
    public static void main(String[] args) {
        SpringApplication.run(KustaurantApplication.class, args);
    }

}
