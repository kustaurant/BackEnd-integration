package com.kustaurant.kustaurant.common.view;

import com.kustaurant.kustaurant.post.post.service.port.PostRepository;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantRepository;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ViewCountService {
    private final StringRedisTemplate redis;
    private final PostRepository postRepo;
    private final RestaurantRepository restaurantRepo;

    private static final String FORMAT_KEY = "%s::view::v1:%s:%s";

    public static String buildDedupKey(ViewResourceType type, long id, String viewerKey) {
        return FORMAT_KEY.formatted(type.key(), type.slot(id), viewerKey);
    }

    @Observed
    public void countOncePerHour(ViewResourceType type, long id, String viewerKey) {
        String dedupKey = buildDedupKey(type, id, viewerKey);
        Boolean firstTime = redis.opsForValue().setIfAbsent(dedupKey, "1", Duration.ofHours(1));
        if(!Boolean.TRUE.equals(firstTime)) return;

        switch (type) {
            case POST -> postRepo.increaseVisitCount(id);
            case RESTAURANT -> restaurantRepo.increaseVisitCount(id);
        }
    }
}

