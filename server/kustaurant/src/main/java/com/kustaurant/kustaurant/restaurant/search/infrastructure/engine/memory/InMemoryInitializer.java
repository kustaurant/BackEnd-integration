package com.kustaurant.kustaurant.restaurant.search.infrastructure.engine.memory;

import com.kustaurant.kustaurant.restaurant.search.infrastructure.persistence.RestaurantSearchRepository;
import com.kustaurant.kustaurant.restaurant.search.infrastructure.persistence.response.RestaurantForEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryInitializer {

    private final RestaurantSearchRepository restaurantSearchRepository;
    private final InMemorySearchEngineManager  inMemorySearchEngineManager;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("식당 검색 인덱스 초기화 실행");
        List<RestaurantForEngine> docs = restaurantSearchRepository.getRestaurantForEngine();
        inMemorySearchEngineManager.build(docs);
        log.info("식당 검색 인덱스 초기화 완료");
    }
}
