package com.kustaurant.dataprocessor.aianalysis.domain.service.port;

import com.kustaurant.dataprocessor.aianalysis.domain.model.Review;
import com.kustaurant.dataprocessor.aianalysis.domain.model.ReviewAnalysis;
import java.util.List;
import java.util.Optional;

public interface AiProcessor {

    Optional<ReviewAnalysis> analyzeReview(Review review, List<String> situations);
}
