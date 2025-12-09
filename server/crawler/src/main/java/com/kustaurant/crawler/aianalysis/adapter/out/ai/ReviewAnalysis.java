package com.kustaurant.crawler.aianalysis.adapter.out.ai;

import com.kustaurant.crawler.aianalysis.domain.model.Sentiment;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record ReviewAnalysis(
        String review,
        double score,
        Sentiment sentiment,
        List<String> situations
) {
}
