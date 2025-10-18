package com.kustaurant.kustaurant.rating.domain.service;

import com.kustaurant.kustaurant.common.clockHolder.ClockHolder;
import com.kustaurant.kustaurant.rating.domain.model.Rating;
import com.kustaurant.kustaurant.rating.domain.vo.Tier;
import com.kustaurant.kustaurant.rating.domain.vo.TierPolicyProp;
import com.kustaurant.kustaurant.rating.domain.vo.TierPolicyProp.TierLevel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TierCalculationService {

    private final TierPolicyProp policy;
    private final ClockHolder clockHolder;

    public List<Rating> calculate(List<Rating> scores) {
        // 점수 기준으로 내림차순 정렬
        scores.sort(Comparator.comparingDouble(Rating::getFinalScore).reversed());
        // 티어가 있는 평가 개수
        int tierCount = countExistTiers(scores);
        // 티어 할당
        return assignTier(policy, scores, tierCount);
    }

    private List<Rating> assignTier(
            TierPolicyProp tierP,
            List<Rating> sorted,
            int tierCount
    ) {
        List<Rating> result = new ArrayList<>(sorted.size());

        int tier = 1;
        int count = 0;
        double lastScoreInTier = -1;

        TierLevel level = tierP.levels().get(tier);
        int maxCount = (int) Math.ceil(tierCount * level.maxRatio());
        double minScore = level.minScore();

        for (Rating score : sorted) {
            if (score.getFinalScore() == 0) {
                score.changeTier(Tier.NONE);
                result.add(score);
                continue;
            }
            if (tier == 5) {
                score.changeTier(Tier.FIVE);
                result.add(score);
                continue;
            }
            while (tier < 5 && (score.getFinalScore() < minScore || (count >= maxCount && score.getFinalScore() != lastScoreInTier))) {
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

            score.changeTier(assignedTier);
            result.add(score);

            if (assignedTier != Tier.FIVE) {
                count++;
                lastScoreInTier = score.getFinalScore();
            }
        }
        return result;
    }

    private int countExistTiers(List<Rating> scores) {
        int existTierCount = 0;
        for (Rating score : scores) {
            if (score.getFinalScore() == 0) {
                return existTierCount;
            }
            existTierCount++;
        }
        return existTierCount;
    }
}
