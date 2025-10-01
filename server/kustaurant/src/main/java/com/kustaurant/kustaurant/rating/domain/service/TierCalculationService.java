package com.kustaurant.kustaurant.rating.domain.service;

import com.kustaurant.kustaurant.common.clockHolder.ClockHolder;
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
    private final ClockHolder clockHolder;

    public List<Rating> calculate(List<RatingScore> scores) {
        // 점수 기준으로 내림차순 정렬
        scores.sort(Comparator.comparingDouble(RatingScore::score).reversed());
        // 티어가 있는 평가 개수
        int tierCount = countExistTiers(scores);
        // 티어 할당
        return assignTier(policy.tier(), scores, tierCount);
    }

    private List<Rating> assignTier(
            TierPolicy tierP,
            List<RatingScore> sorted,
            int tierCount
    ) {
        List<Rating> result = new ArrayList<>(sorted.size());

        int tier = 1;
        int count = 0;
        double lastScoreInTier = -1;

        TierLevel level = tierP.levels().get(tier);
        int maxCount = (int) Math.ceil(tierCount * level.maxRatio());
        double minScore = level.minScore();

        for (RatingScore score : sorted) {
            if (score.score() == 0) {
                result.add(new Rating(score.restaurantId(), score.score(), Tier.NONE, false, clockHolder.now()));
                continue;
            }
            if (tier == 5) {
                result.add(new Rating(score.restaurantId(), score.score(), Tier.FIVE, false, clockHolder.now()));
                continue;
            }
            while (tier < 5 && (score.score() < minScore || (count >= maxCount && score.score() != lastScoreInTier))) {
                tier++;
                if (tier == 5) {
                    break;
                }
                count = 0;
                lastScoreInTier = -1;
                level = tierP.levels().get(tier);
                maxCount = (int) Math.ceil(tierCount * level.maxRatio());
                minScore = level.minScore();
            }

            Tier assignedTier = tier == 5 ? Tier.FIVE : Tier.find(tier);

            result.add(new Rating(score.restaurantId(), score.score(), assignedTier, false, clockHolder.now()));

            if (assignedTier != Tier.FIVE) {
                count++;
                lastScoreInTier = score.score();
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
