package com.kustaurant.kustaurant.rating.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.kustaurant.kustaurant.rating.domain.model.Rating;
import com.kustaurant.kustaurant.rating.domain.model.RatingPolicy;
import com.kustaurant.kustaurant.rating.domain.model.RatingPolicy.TierPolicy;
import com.kustaurant.kustaurant.rating.domain.model.RatingPolicy.TierPolicy.TierLevel;
import com.kustaurant.kustaurant.rating.domain.model.RatingScore;
import com.kustaurant.kustaurant.rating.domain.model.Tier;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
class TierCalculationServiceTest {
    private TierCalculationService service;
    private RatingPolicy policy;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        // tier 정책: 1~4 티어 각각 maxRatio 0.2, minScore 4.0,3.5,3.0,2.5
        Map<Integer, TierLevel> levels = Map.of(
                1, new TierLevel(4.0, 0.2),
                2, new TierLevel(3.5, 0.2),
                3, new TierLevel(3.0, 0.2),
                4, new TierLevel(2.5, 0.2)
        );
        TierPolicy tierPolicy = new TierPolicy(levels);
        policy = new RatingPolicy(0, null, null, tierPolicy);
        service = new TierCalculationService(policy);
        now = LocalDateTime.now();
    }

    // helper factory
    private static RatingScore score(int id, double score) {
        return new RatingScore(id, score);
    }

    private static List<RatingScore> descendingScores(double... scores) {
        List<RatingScore> list = new ArrayList<>();
        for (int i = 0; i < scores.length; i++) {
            list.add(new RatingScore(i + 1, scores[i]));
        }
        return list;
    }

    // convert to map restaurantId -> tier value (int)
    private static Map<Integer, Integer> toTierMap(List<Rating> ratings) {
        Map<Integer, Integer> m = new HashMap<>();
        for (Rating r : ratings) {
            m.put(r.restaurantId(), r.tier().getValue());
        }
        return m;
    }

    @Test
    void basicDistribution_assignsTiersAccordingToQuotaAndThresholds() {
        // 10 positive scores descending, no zeros
        List<RatingScore> scores = List.of(
                score(1, 5.0),
                score(2, 4.8),
                score(3, 4.6),
                score(4, 4.4),
                score(5, 4.2),
                score(6, 4.0),
                score(7, 3.8),
                score(8, 3.6),
                score(9, 3.4),
                score(10, 3.2)
        );

        List<Rating> ratings = service.calculate(new ArrayList<>(scores), now);
        Map<Integer, Integer> tierMap = toTierMap(ratings);

        // Quota: tierCount =10 -> each tier1..4 has ceil(10*0.2)=2
        // Expected:
        // tier1: ids 1,2
        assertThat(tierMap.get(1)).isEqualTo(Tier.ONE.getValue());
        assertThat(tierMap.get(2)).isEqualTo(Tier.ONE.getValue());
        // tier2: ids 3,4
        assertThat(tierMap.get(3)).isEqualTo(Tier.TWO.getValue());
        assertThat(tierMap.get(4)).isEqualTo(Tier.TWO.getValue());
        // tier3: ids 5,6
        assertThat(tierMap.get(5)).isEqualTo(Tier.THREE.getValue());
        assertThat(tierMap.get(6)).isEqualTo(Tier.THREE.getValue());
        // tier4: ids 7,8
        assertThat(tierMap.get(7)).isEqualTo(Tier.FOUR.getValue());
        assertThat(tierMap.get(8)).isEqualTo(Tier.FOUR.getValue());
        // rest -> tier5
        assertThat(tierMap.get(9)).isEqualTo(Tier.FIVE.getValue());
        assertThat(tierMap.get(10)).isEqualTo(Tier.FIVE.getValue());
    }

    @Test
    void zeroScoreProducesNoneTier_andStopsTierCountEarly() {
        // scores: some positives, then a 0, then more positives
        List<RatingScore> scores = List.of(
                score(1, 5.0),  // positive
                score(2, 4.5),  // positive
                score(3, 4.0),  // positive
                score(4, 3.5),  // positive
                score(5, 3.0),  // positive -> tierCount =5
                score(6, 0.0),  // stops tierCount here
                score(7, 4.9),  // this will be treated after 0
                score(8, 3.0)
        );

        List<Rating> ratings = service.calculate(new ArrayList<>(scores), now);
        Map<Integer, Integer> tierMap = toTierMap(ratings);

        // tierCount = 5 => quotas each tier1..4 = ceil(5*0.2)=1
        // positive before zero: ids 1..5 get:
        // tier1: id1 (5.0)
        assertThat(tierMap.get(1)).isEqualTo(Tier.ONE.getValue());
        // tier2: next highest (4.5) id2
        assertThat(tierMap.get(2)).isEqualTo(Tier.TWO.getValue());
        // tier3: id3 (4.0)
        assertThat(tierMap.get(3)).isEqualTo(Tier.THREE.getValue());
        // tier4: id4 (3.5)
        assertThat(tierMap.get(4)).isEqualTo(Tier.FOUR.getValue());
        // remaining positive before zero id5 -> since tier1..4 quotas filled, goes to tier5
        assertThat(tierMap.get(5)).isEqualTo(Tier.FIVE.getValue());

        // zero score -> NONE
        assertThat(tierMap.get(6)).isEqualTo(Tier.NONE.getValue());

        // after zero appears, tierCount stopped; remaining ones (7,8) should both end up in tier5
        assertThat(tierMap.get(7)).isEqualTo(Tier.FIVE.getValue());
        assertThat(tierMap.get(8)).isEqualTo(Tier.FIVE.getValue());
    }

    @Test
    void tieAtBoundary_movesThirdSameScoreToNextTier() {
        // Three same high scores; quota for tier1 is 2 (assume total positives=5 including some others)
        List<RatingScore> scores = List.of(
                score(1, 5.0),
                score(2, 5.0),
                score(3, 5.0),
                score(4, 3.9),
                score(5, 3.7)
        );
        // total positive =5 => each tier1..4 quota=1
        // After applying algorithm:
        // tier1: id1 (5.0)
        // tier2: id2 (5.0) because quota for tier1=1 reached
        // tier3: id3 (5.0)
        // tier4: id4 (3.9) because it meets threshold 2.5 etc.
        // remaining (id5) -> tier5

        List<Rating> ratings = service.calculate(new ArrayList<>(scores), now);
        Map<Integer, Integer> tierMap = toTierMap(ratings);

        assertThat(tierMap.get(1)).isEqualTo(Tier.ONE.getValue());
        assertThat(tierMap.get(2)).isEqualTo(Tier.TWO.getValue());
        assertThat(tierMap.get(3)).isEqualTo(Tier.THREE.getValue());
        assertThat(tierMap.get(4)).isEqualTo(Tier.FOUR.getValue());
        assertThat(tierMap.get(5)).isEqualTo(Tier.FIVE.getValue());
    }

    @Test
    void allZeroFirst_thenAllPositive_goToTierFiveExceptZero() {
        List<RatingScore> scores = new ArrayList<>();
        scores.add(score(1, 0.0)); // zero at top
        // positives follow
        for (int i = 2; i <= 6; i++) {
            scores.add(score(i, 4.5 - 0.1 * (i - 2))); // descending positives
        }

        List<Rating> ratings = service.calculate(new ArrayList<>(scores), now);
        System.out.println(ratings);
        Map<Integer, Integer> tierMap = toTierMap(ratings);

        // first is NONE
        assertThat(tierMap.get(1)).isEqualTo(Tier.NONE.getValue());
        // Because tierCount=0 (first is zero), all positives must fall into tier5
        for (int i = 2; i <= 6; i++) {
            assertThat(tierMap.get(i)).isEqualTo(Tier.FIVE.getValue());
        }
    }

    @Test
    void belowAnyThresholdButPositive_goesToTierFive() {
        // Single score below all thresholds (e.g., 1.0)
        List<RatingScore> scores = List.of(score(1, 1.0));

        List<Rating> ratings = service.calculate(new ArrayList<>(scores), now);
        Map<Integer, Integer> tierMap = toTierMap(ratings);

        assertThat(tierMap.get(1)).isEqualTo(Tier.FIVE.getValue());
    }
}