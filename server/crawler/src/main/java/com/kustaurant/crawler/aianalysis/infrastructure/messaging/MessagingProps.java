package com.kustaurant.crawler.aianalysis.infrastructure.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

/**
 * @param aiAnalysisStart
 * @param aiAnalysisDlq
 * @param group - 현재 프로세스의 컨슈머 그룹명
 */
@Profile("!test")
@ConfigurationProperties(prefix = "streams")
public record MessagingProps(
        String aiAnalysisStart,
        String aiAnalysisDlq,
        String group
) {

}
