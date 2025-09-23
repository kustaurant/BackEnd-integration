package com.kustaurant.kustaurant.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableCaching
public class LocalCacheConfig {
    @Bean
    public CacheManager cacheManager() {
        var cm = new SimpleCacheManager();
        cm.setCaches(List.of(
                new CaffeineCache("restaurantChartPage",
                        Caffeine.newBuilder()
                                .maximumSize(100)
                                .expireAfterWrite(Duration.ofHours(24))
                                .recordStats()
                                .build())
        ));
        return cm;
    }
}
