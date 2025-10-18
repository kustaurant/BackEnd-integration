package com.kustaurant.kustaurant.rating.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.kustaurant.kustaurant.common.clockHolder.ClockHolder;
import com.kustaurant.kustaurant.common.clockHolder.SystemClockHolder;
import com.kustaurant.kustaurant.rating.domain.model.Rating;
import com.kustaurant.kustaurant.rating.domain.vo.Tier;
import com.kustaurant.kustaurant.rating.domain.vo.TierPolicyProp;
import com.kustaurant.kustaurant.rating.domain.vo.TierPolicyProp.TierLevel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TierCalculationServiceTest {

    private TierCalculationService service;
    private ClockHolder clockHolder;

    @BeforeEach
    void setUp() {
        // 1~4 티어: maxRatio 0.2 / minScore 4.0, 3.5, 3.0, 2.5
        Map<Integer, TierLevel> levels = Map.of(
                1, new TierLevel(4.0, 0.2),
                2, new TierLevel(3.5, 0.2),
                3, new TierLevel(3.0, 0.2),
                4, new TierLevel(2.5, 0.2)
        );
        TierPolicyProp tierPolicyProp = new TierPolicyProp(levels);

        service = new TierCalculationService(tierPolicyProp, new SystemClockHolder());
        clockHolder = new SystemClockHolder();
    }

    // 헬퍼 메서드
    private List<Rating> scores(Object... tuples) {
        // 가변인자 형태: (id, score, id, score, …)
        List<Rating> list = new ArrayList<>(tuples.length / 2);
        for (int i = 0; i < tuples.length; i += 2) {
            list.add(new Rating((Integer) tuples[i], false, (Double) tuples[i + 1], 0.0, (Double) tuples[i + 1], clockHolder.now()));
        }
        return list;
    }

    // 헬퍼 메서드
    private void assertTiers(List<Rating> actual, Map<Integer, Tier> expected) {
        assertThat(actual).hasSize(expected.size());
        expected.forEach((id, tier) ->
                assertThat(
                        actual.stream()
                                .filter(r -> r.getRestaurantId() == id)
                                .findFirst()
                                .orElseThrow()
                                .getTier()
                )
                        .as("restaurantId=%s", id)
                        .isEqualTo(tier)
        );
    }

    @Test
    void 일반적인_티어_분포_상황() {
        List<Rating> input = scores(
                1, 4.6,
                2, 4.4,   // Tier 1 (2개)
                3, 4.1,
                4, 3.7,   // Tier 2 (2개)
                5, 3.4,
                6, 3.2,   // Tier 3 (2개)
                7, 2.9,
                8, 2.6,   // Tier 4 (2개)
                9, 2.4,
                10, 2.1   //  Tier 5 (2개)
        );

        Map<Integer, Tier> expect = Map.of(
                1, Tier.ONE, 2, Tier.ONE,
                3, Tier.TWO, 4, Tier.TWO,
                5, Tier.THREE, 6, Tier.THREE,
                7, Tier.FOUR, 8, Tier.FOUR,
                9, Tier.FIVE, 10, Tier.FIVE
        );

        assertTiers(service.calculate(input), expect);
    }

    @Test
    void 점수_0점인_경우는_Tier_가_없음() {
        List<Rating> input = scores(
                11, 4.2,
                12, 0.0,   // NONE
                13, 3.8,
                14, 0.0,   // NONE
                15, 2.9
        );

        Map<Integer, Tier> expect = Map.of(
                11, Tier.ONE,
                12, Tier.NONE,
                13, Tier.TWO,
                14, Tier.NONE,
                15, Tier.FOUR
        );

        assertTiers(service.calculate(input), expect);
    }

    @Test
    void 점수_조건에_충족하지_않아서_티어1이_없는_경우() {
        List<Rating> input = scores(
                21, 3.9,
                22, 3.8,
                23, 3.6,
                24, 3.5,
                25, 3.2,
                26, 3.0,
                27, 2.9
        ); // 7개 → 각 티어 최대 2개

        Map<Integer, Tier> expect = Map.of(
                21, Tier.TWO, 22, Tier.TWO,
                23, Tier.THREE, 24, Tier.THREE,
                25, Tier.FOUR, 26, Tier.FOUR,
                27, Tier.FIVE
        );

        assertTiers(service.calculate(input), expect);
    }

    @Test
    void 점수_조건에_충족하지_않아서_중간_티어가_없고_티어_비율을_넘었는데_동점인_경우() {
        List<Rating> input = scores(
                21, 4.0,
                22, 3.1,
                23, 3.1,
                24, 3.1,
                25, 3.1,
                26, 3.0,
                27, 2.8,
                28, 2.8,
                29, 2.8,
                30, 2.6
        );

        Map<Integer, Tier> expect = Map.of(
                21, Tier.ONE, 22, Tier.THREE,
                23, Tier.THREE, 24, Tier.THREE,
                25, Tier.THREE, 26, Tier.FOUR,
                27, Tier.FOUR, 28, Tier.FOUR,
                29, Tier.FOUR, 30, Tier.FIVE
        );

        List<Rating> ratings = service.calculate(input);
        System.out.println(ratings);
        assertTiers(ratings, expect);
    }

    @Test
    void 중간에_티어를_많이_건너뛴_경우() {
        List<Rating> input = scores(
                31, 4.7,
                32, 2.3,
                33, 2.2,
                34, 0.0,
                35, 2.1
        );

        Map<Integer, Tier> expect = Map.of(
                31, Tier.ONE,
                32, Tier.FIVE,
                33, Tier.FIVE,
                34, Tier.NONE,
                35, Tier.FIVE
        );

        assertTiers(service.calculate(input), expect);
    }
}