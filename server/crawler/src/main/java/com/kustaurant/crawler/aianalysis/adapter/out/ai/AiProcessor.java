package com.kustaurant.crawler.aianalysis.adapter.out.ai;

import java.util.Optional;

public interface AiProcessor {

    Optional<ReviewAnalysis> analyzeReview(String review);
}
