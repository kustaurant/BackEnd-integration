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
    void RatingPolicy_빈_정상_등록_확인() {
        assertThat(ratingPolicy).isNotNull();
        assertThat(ratingPolicy.evaluation()).isNotNull();
        assertThat(ratingPolicy.restaurant()).isNotNull();
        assertThat(ratingPolicy.tier()).isNotNull();
        System.out.println(ratingPolicy);
    }
}