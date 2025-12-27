package com.kustaurant.crawler.aianalysis.adapter.out.persistence;

import com.kustaurant.crawler.aianalysis.domain.model.AiSummary;
import java.util.Optional;

public interface AiSummaryRepo {

    void save(AiSummary aiSummary);

    Optional<AiSummary> findByRestaurantId(long id);
}
