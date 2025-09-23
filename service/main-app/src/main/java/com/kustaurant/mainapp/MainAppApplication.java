package com.kustaurant.mainapp;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@OpenAPIDefinition(servers = {
        @Server(url = "/", description = "Default Server URL")
})
@EnableScheduling
@SpringBootApplication(scanBasePackages = {
        "com.kustaurant.mainapp",
        "com.kustaurant.jpa",
        "com.kustaurant.redis"
})
@ConfigurationPropertiesScan
public class MainAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainAppApplication.class, args);
    }

}
