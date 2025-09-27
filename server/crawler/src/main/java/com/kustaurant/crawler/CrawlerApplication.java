package com.kustaurant.crawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@ConfigurationPropertiesScan
@SpringBootApplication(scanBasePackages = {
        "com.kustaurant.crawler",
        "com.kustaurant.jpa"
})
@EnableJpaRepositories(basePackages = {
        "com.kustaurant.crawler",
        "com.kustaurant.jpa"
})
@EntityScan(basePackages = {
        "com.kustaurant.crawler",
        "com.kustaurant.jpa"
})
public class CrawlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrawlerApplication.class, args);
    }
}
