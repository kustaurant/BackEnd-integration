package com.kustaurant.restauranttier.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppApiConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
