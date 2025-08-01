package com.kustaurant.kustaurant.rating.domain.model;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RatingPolicyTest {

    @Autowired
    RatingPolicy ratingPolicy;

    @Test
    void 평가_완성도_가중치의_합은_1이어야_한다() {
        double weightSum = ratingPolicy.evaluation().completenessWeight().getWeightSum();
        assertThat(weightSum).isCloseTo(1.0, within(1e-6));
    }

    @Test
    void 식당_스탯_가중치의_합은_1이어야_한다() {
        double weightSum = ratingPolicy.restaurant().popularityWeight().getWeightSum();
        assertThat(weightSum).isCloseTo(1.0, within(1e-6));
    }
}