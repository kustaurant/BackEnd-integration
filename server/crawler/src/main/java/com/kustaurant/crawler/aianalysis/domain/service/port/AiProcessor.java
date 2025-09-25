package com.kustaurant.crawler.aianalysis.domain.service.port;

import com.kustaurant.crawler.aianalysis.domain.model.Review;
import com.kustaurant.crawler.aianalysis.domain.model.ReviewAnalysis;
import java.util.List;
import java.util.Optional;

public interface AiProcessor {

    Optional<ReviewAnalysis> analyzeReview(Review review, List<String> situations);
}
