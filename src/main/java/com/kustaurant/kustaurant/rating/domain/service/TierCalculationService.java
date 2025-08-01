package com.kustaurant.kustaurant.rating.domain.service;

import com.kustaurant.kustaurant.rating.domain.model.Rating;
import com.kustaurant.kustaurant.rating.domain.model.RatingScore;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TierCalculationService {

    public List<Rating> calculate(List<RatingScore> scores) {
        return List.of();
    }
}
