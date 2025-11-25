package com.kustaurant.kustaurant.rating.service.port;

import com.kustaurant.kustaurant.rating.domain.model.AiEvaluation;
import com.kustaurant.kustaurant.rating.domain.vo.GlobalStats;
import java.util.Map;

public interface AiSummaryRepository {

    Map<Long, AiEvaluation> getAiEvaluations();

    GlobalStats getGlobalStats();
}
