package com.kustaurant.kustaurant.rating.service.port;

import com.kustaurant.kustaurant.rating.domain.model.AiEvaluation;
import com.kustaurant.kustaurant.rating.domain.model.Rating;
import com.kustaurant.kustaurant.rating.domain.vo.GlobalStats;
import java.util.List;
import java.util.Map;

public interface RatingRepository {

    void saveAll(List<Rating> rating);
}
