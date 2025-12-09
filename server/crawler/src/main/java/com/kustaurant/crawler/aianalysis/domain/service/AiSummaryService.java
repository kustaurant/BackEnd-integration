package com.kustaurant.crawler.aianalysis.domain.service;

import com.kustaurant.crawler.aianalysis.domain.model.AiAnalysisReview;
import com.kustaurant.crawler.aianalysis.domain.model.AiSummary;
import com.kustaurant.crawler.aianalysis.domain.model.Sentiment;
import com.kustaurant.crawler.global.exception.AiAnalysisException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiSummaryService {

    private final Clock clock;

    public void summarize(long jobId, AiSummary summary, List<AiAnalysisReview> reviews) {
        int total = reviews.size();
        if (total == 0) throw new AiAnalysisException("분석된 AI 리뷰가 없습니다.");

        int pos = 0, neg = 0;
        double sum = 0;
        for (AiAnalysisReview r : reviews) {
            if (r.getSentiment() == Sentiment.POSITIVE) {
                pos++;
            } else if (r.getSentiment() == Sentiment.NEGATIVE) {
                neg++;
            }
            sum += r.getScore();
        }
        double avg = sum / total;

        summary.update(jobId, total, pos, neg, sum, avg, LocalDateTime.now(clock));
    }
}
