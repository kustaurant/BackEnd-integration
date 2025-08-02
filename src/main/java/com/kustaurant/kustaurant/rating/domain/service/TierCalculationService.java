package com.kustaurant.kustaurant.rating.domain.service;

import com.kustaurant.kustaurant.rating.domain.model.Rating;
import com.kustaurant.kustaurant.rating.domain.model.RatingPolicy;
import com.kustaurant.kustaurant.rating.domain.model.RatingPolicy.TierPolicy;
import com.kustaurant.kustaurant.rating.domain.model.RatingPolicy.TierPolicy.TierLevel;
import com.kustaurant.kustaurant.rating.domain.model.RatingScore;
import com.kustaurant.kustaurant.rating.domain.model.Tier;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TierCalculationService {

    private final RatingPolicy policy;

    public List<Rating> calculate(List<RatingScore> scores, LocalDateTime now) {
        // 점수 기준으로 내림차순 정렬
        scores.sort(Comparator.comparingDouble(RatingScore::score).reversed());
        // 티어가 있는 평가 개수
        int tierCount = countExistTiers(scores);
        // 티어 할당
        return assignTier(policy.tier(), scores, tierCount, now);
    }

    private List<Rating> assignTier(
            TierPolicy tierP,
            List<RatingScore> sorted,
            int tierCount,
            LocalDateTime now
    ) {
        List<Rating> result = new ArrayList<>(sorted.size());
        int tier = 1;
        int count = 0;
        TierLevel level = tierP.levels().get(tier);
        int maxCount = (int) Math.ceil(tierCount * level.maxRatio());
        double minScore = level.minScore();
        for (RatingScore score : sorted) {
            if (score.score() == 0) {
                result.add(new Rating(score.restaurantId(), score.score(), Tier.NONE, false, now));
                continue;
            }
            if (tier == 5) {
                result.add(new Rating(score.restaurantId(), score.score(), Tier.FIVE, false, now));
                continue;
            }
            while (tier < 5 && (score.score() < minScore || count >= maxCount)) {
                tier++;
                if (tier == 5) {
                    break;
                }
                count = 0;
                level = tierP.levels().get(tier);
                maxCount = (int) Math.ceil(tierCount * level.maxRatio());
                minScore = level.minScore();
            }

            if (tier == 5) {
                result.add(new Rating(score.restaurantId(), score.score(), Tier.FIVE, false, now));
            } else if (score.score() >= minScore && count < maxCount) {
                result.add(new Rating(score.restaurantId(), score.score(), Tier.find(tier), false, now));
                count++;
            } else {
                result.add(new Rating(score.restaurantId(), score.score(), Tier.FIVE, false, now));
            }
        }
        return result;
    }

    private int countExistTiers(List<RatingScore> scores) {
        int existTierCount = 0;
        for (RatingScore score : scores) {
            if (score.score() == 0) {
                return existTierCount;
            }
            existTierCount++;
        }
        return existTierCount;
    }
}
