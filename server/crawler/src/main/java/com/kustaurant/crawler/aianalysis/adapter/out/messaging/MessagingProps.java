package com.kustaurant.crawler.aianalysis.adapter.out.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

/**
 * @param aiAnalysisCrawling
 * @param aiAnalysisReview
 * @param aiAnalysisDlq
 * @param group - 현재 프로세스의 컨슈머 그룹명
 */
@ConfigurationProperties(prefix = "streams")
public record MessagingProps(
        String aiAnalysisCrawling,
        String aiAnalysisReview,
        String aiAnalysisDlq,
        String group
) {

}
