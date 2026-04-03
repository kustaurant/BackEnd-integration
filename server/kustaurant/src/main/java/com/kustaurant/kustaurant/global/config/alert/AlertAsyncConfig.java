package com.kustaurant.kustaurant.global.config.alert;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AlertAsyncConfig {

    @Bean(name="alertExecutor")
    public Executor alertExecutor() {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(1);
        ex.setMaxPoolSize(4);
        ex.setQueueCapacity(100);
        ex.setThreadNamePrefix("discord-alert-");
        ex.setTaskDecorator(r -> {
            var contextMap = MDC.getCopyOfContextMap();
            return () -> {
                Map<String, String> old = MDC.getCopyOfContextMap();
                try { if (contextMap != null) MDC.setContextMap(contextMap); r.run(); }
                finally { if (old != null) MDC.setContextMap(old); else MDC.clear(); }
            };
        });
        ex.initialize();
        return ex;
    }
}
