package com.kustaurant.crawler.infrastructure.messaging.redis;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;

@Configuration
public class RedisStreamsConfig {

    @Bean
    public StreamMessageListenerContainer<String, MapRecord<String,String,String>> streamMessageListenerContainer(
            RedisConnectionFactory cf,
            StreamMessageListenerContainerOptions<String, MapRecord<String,String,String>> options
    ) {
        return StreamMessageListenerContainer.create(cf, options);
    }

    @Bean
    public StreamMessageListenerContainerOptions<String, MapRecord<String,String,String>> streamOptions() {
        return StreamMessageListenerContainerOptions.builder()
                .pollTimeout(Duration.ofSeconds(2))
                .build();
    }
}
