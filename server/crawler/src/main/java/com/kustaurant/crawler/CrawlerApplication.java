package com.kustaurant.crawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CrawlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrawlerApplication.class, args);
    }

}
