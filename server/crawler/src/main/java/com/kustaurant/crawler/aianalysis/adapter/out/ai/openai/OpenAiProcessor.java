package com.kustaurant.crawler.aianalysis.adapter.out.ai.openai;

import com.kustaurant.crawler.aianalysis.adapter.out.ai.AiProcessor;
import com.kustaurant.crawler.aianalysis.adapter.out.ai.openai.dto.ScoreResponse;
import com.kustaurant.crawler.aianalysis.adapter.out.ai.ReviewAnalysis;
import com.kustaurant.crawler.global.util.JsonUtils;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.metadata.RateLimit;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiProcessor implements AiProcessor {

    private static final int MIN_REQUESTS_REMAINING = 1000;
    private static final int MIN_TOKEN_REMAINING = 10000;

    private final OpenAiChatModel openAiChatModel;
    private final OpenAiRateLimitGate rateLimitGate;

    @Override
    public Optional<ReviewAnalysis> analyzeReview(String review) {
        ChatResponse res = openAiChatModel.call(
                OpenAiPrompts.getMainScorePrompt(List.of(1, 2, 3, 4, 5), review)
        );
        manageRateLimit(res.getMetadata().getRateLimit());
        String scoreJson = res.getResult().getOutput().getText();

        ScoreResponse scoreRes = JsonUtils.deserialize(scoreJson, ScoreResponse.class);

        return Optional.of(new ReviewAnalysis(
                review, scoreRes.score(), scoreRes.sentiment(), null
        ));
    }

    private void manageRateLimit(RateLimit rateLimit) {
        if (rateLimit.getRequestsRemaining() < MIN_REQUESTS_REMAINING) {
            rateLimitGate.blockForMs(rateLimit.getRequestsReset().toMillis());
        }
        if (rateLimit.getTokensRemaining() < MIN_TOKEN_REMAINING) {
            rateLimitGate.blockForMs(rateLimit.getTokensReset().toMillis());
        }
    }
}
