package com.kustaurant.kustaurant.restaurant.search.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantSearchV3ServiceTest {

    @Test
    @DisplayName("null이나 빈 배열이 들어오면 빈 결과를 반환한다.")
    void searchV3WhenEmptyInput() {
        // given
        String[] kwArr = null;
        RestaurantSearchV3Service service = new RestaurantSearchV3Service(null, null);
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 10);

        // when
        var nullResponse = service.search(kwArr, 1L, pageable);
        var emptyArrayResponse = service.search(new String[0], 1L, pageable);

        // then
        assertNotNull(nullResponse);
        assertTrue(nullResponse.items().isEmpty());
        assertNotNull(emptyArrayResponse);
        assertTrue(emptyArrayResponse.items().isEmpty());
    }
}