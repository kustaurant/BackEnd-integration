package com.kustaurant.kustaurant.common.restaurant.infrastructure;

import com.kustaurant.kustaurant.common.restaurant.domain.RestaurantDomain;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantEntityTest {

    @Test
    void 엔티티_to_도메인_함수_테스트() {
        // given
        RestaurantEntity entity = new RestaurantEntity();
        entity.setRestaurantId(123);
        entity.setRestaurantName("테스트 식당");
        entity.setRestaurantType("한식");
        entity.setRestaurantPosition("서울");
        entity.setRestaurantAddress("서울특별시 강남구");
        entity.setRestaurantTel("010-1234-5678");
        entity.setRestaurantUrl("https://example.com");
        entity.setRestaurantImgUrl("https://example.com/image.jpg");
        entity.setRestaurantCuisine("고기구이");
        entity.setRestaurantLatitude("37.5665");
        entity.setRestaurantLongitude("126.9780");
        entity.setPartnershipInfo("파트너십 정보");
        entity.setStatus("TEST");
        entity.setCreatedAt(LocalDateTime.parse("2024-03-19T15:30:00"));
        entity.setUpdatedAt(LocalDateTime.parse("2024-03-19T15:30:00"));
        entity.setRestaurantVisitCount(111);
        entity.setVisitCount(55);
        entity.setRestaurantEvaluationCount(22);
        entity.setRestaurantScoreSum(99D);
        entity.setMainTier(3);

        // when
        RestaurantDomain domain = entity.toModel();

        // then
        assertEquals(entity.getRestaurantId(), domain.getRestaurantId());
        assertEquals(entity.getRestaurantName(), domain.getRestaurantName());
        assertEquals(entity.getRestaurantType(), domain.getRestaurantType());
        assertEquals(entity.getRestaurantPosition(), domain.getRestaurantPosition());
        assertEquals(entity.getRestaurantAddress(), domain.getRestaurantAddress());
        assertEquals(entity.getRestaurantTel(), domain.getRestaurantTel());
        assertEquals(entity.getRestaurantUrl(), domain.getRestaurantUrl());
        assertEquals(entity.getRestaurantImgUrl(), domain.getRestaurantImgUrl());
        assertEquals(entity.getRestaurantCuisine(), domain.getRestaurantCuisine());
        assertEquals(entity.getRestaurantLatitude(), domain.getRestaurantLatitude());
        assertEquals(entity.getRestaurantLongitude(), domain.getRestaurantLongitude());
        assertEquals(entity.getPartnershipInfo(), domain.getPartnershipInfo());
        assertEquals(entity.getStatus(), domain.getStatus());
        assertEquals(entity.getCreatedAt(), domain.getCreatedAt());
        assertEquals(entity.getUpdatedAt(), domain.getUpdatedAt());
        assertEquals(entity.getRestaurantVisitCount(), domain.getRestaurantVisitCount());
        assertEquals(entity.getVisitCount(), domain.getVisitCount());
        assertEquals(entity.getRestaurantEvaluationCount(), domain.getRestaurantEvaluationCount());
        assertEquals(entity.getRestaurantScoreSum(), domain.getRestaurantScoreSum());
        assertEquals(entity.getMainTier(), domain.getMainTier());
    }

    @Test
    void 도메인_to_엔티티_함수_테스트() {
        // given
        RestaurantDomain domain = RestaurantDomain.builder()
                .restaurantId(123)
                .restaurantName("테스트 식당")
                .restaurantType("한식")
                .restaurantPosition("서울")
                .restaurantAddress("서울특별시 강남구")
                .restaurantTel("010-1234-5678")
                .restaurantUrl("https://example.com")
                .restaurantImgUrl("https://example.com/image.jpg")
                .restaurantCuisine("고기구이")
                .restaurantLatitude("37.5665")
                .restaurantLongitude("126.9780")
                .partnershipInfo("파트너십 정보")
                .status("TEST")
                .createdAt(LocalDateTime.parse("2024-03-19T15:30:00"))
                .updatedAt(LocalDateTime.parse("2024-03-19T15:30:00"))
                .restaurantVisitCount(111)
                .visitCount(55)
                .restaurantEvaluationCount(22)
                .restaurantScoreSum(99D)
                .mainTier(3)
                .build();

        // when
        RestaurantEntity entity = RestaurantEntity.from(domain);

        // then
        assertEquals(domain.getRestaurantId(), entity.getRestaurantId());
        assertEquals(domain.getRestaurantName(), entity.getRestaurantName());
        assertEquals(domain.getRestaurantType(), entity.getRestaurantType());
        assertEquals(domain.getRestaurantPosition(), entity.getRestaurantPosition());
        assertEquals(domain.getRestaurantAddress(), entity.getRestaurantAddress());
        assertEquals(domain.getRestaurantTel(), entity.getRestaurantTel());
        assertEquals(domain.getRestaurantUrl(), entity.getRestaurantUrl());
        assertEquals(domain.getRestaurantImgUrl(), entity.getRestaurantImgUrl());
        assertEquals(domain.getRestaurantCuisine(), entity.getRestaurantCuisine());
        assertEquals(domain.getRestaurantLatitude(), entity.getRestaurantLatitude());
        assertEquals(domain.getRestaurantLongitude(), entity.getRestaurantLongitude());
        assertEquals(domain.getPartnershipInfo(), entity.getPartnershipInfo());
        assertEquals(domain.getStatus(), entity.getStatus());
        assertEquals(domain.getCreatedAt(), entity.getCreatedAt());
        assertEquals(domain.getUpdatedAt(), entity.getUpdatedAt());
        assertEquals(domain.getRestaurantVisitCount(), entity.getRestaurantVisitCount());
        assertEquals(domain.getVisitCount(), entity.getVisitCount());
        assertEquals(domain.getRestaurantEvaluationCount(), entity.getRestaurantEvaluationCount());
        assertEquals(domain.getRestaurantScoreSum(), entity.getRestaurantScoreSum());
        assertEquals(domain.getMainTier(), entity.getMainTier());
    }

}