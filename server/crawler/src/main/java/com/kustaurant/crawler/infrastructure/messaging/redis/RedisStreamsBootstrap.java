package com.kustaurant.crawler.infrastructure.messaging.redis;

import com.kustaurant.crawler.aianalysis.adapter.in.messaging.MessagingProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * 스프링 시작할 때 구독할 Redis Streams가 있는지 확인하고, 없으면 생성합니다.
 */
@Slf4j
@Profile("!test")
@Component
@RequiredArgsConstructor
public class RedisStreamsBootstrap implements ApplicationRunner {

    private final MessagingProps streamsProps;
    private final RedisStreamsUtils redisStreamsUtils;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        redisStreamsUtils.createStreamAndGroupIfNotExists(streamsProps.aiAnalysisStart(), streamsProps.group());
        redisStreamsUtils.createStreamAndGroupIfNotExists(streamsProps.aiAnalysisDlq(), streamsProps.group());
    }
}
