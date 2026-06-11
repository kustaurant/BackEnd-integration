package com.kustaurant.kustaurant.common.view;

import com.kustaurant.kustaurant.post.post.service.port.PostRepository;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ViewCountServiceTest {

    @Test
    void countOncePerHour_increasesRestaurantVisitCount_whenRedisDedupSucceeds() {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        ValueOperations<String, String> valueOperations = mockValueOperations();
        PostRepository postRepository = mock(PostRepository.class);
        RestaurantRepository restaurantRepository = mock(RestaurantRepository.class);
        ViewCountService service = new ViewCountService(redis, postRepository, restaurantRepository);

        when(redis.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), eq("1"), eq(Duration.ofHours(1)))).thenReturn(true);

        service.countOncePerHour(ViewResourceType.RESTAURANT, 1448L, "g:test");

        verify(restaurantRepository).increaseVisitCount(1448L);
        verify(postRepository, never()).increaseVisitCount(1448L);
    }

    @Test
    void countOncePerHour_skipsViewCount_whenRedisDedupFails() {
        StringRedisTemplate redis = mock(StringRedisTemplate.class);
        ValueOperations<String, String> valueOperations = mockValueOperations();
        PostRepository postRepository = mock(PostRepository.class);
        RestaurantRepository restaurantRepository = mock(RestaurantRepository.class);
        ViewCountService service = new ViewCountService(redis, postRepository, restaurantRepository);

        when(redis.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.setIfAbsent(anyString(), eq("1"), eq(Duration.ofHours(1))))
                .thenThrow(new DataAccessResourceFailureException("Redis write failed"));

        assertDoesNotThrow(() -> service.countOncePerHour(ViewResourceType.RESTAURANT, 1448L, "g:test"));

        verify(restaurantRepository, never()).increaseVisitCount(1448L);
        verify(postRepository, never()).increaseVisitCount(1448L);
    }

    @SuppressWarnings("unchecked")
    private static ValueOperations<String, String> mockValueOperations() {
        return mock(ValueOperations.class);
    }
}
