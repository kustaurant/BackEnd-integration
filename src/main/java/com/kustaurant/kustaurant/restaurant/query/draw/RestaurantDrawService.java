package com.kustaurant.kustaurant.restaurant.query.draw;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RestaurantDrawService {

    private final int TARGET_SIZE = 30;

    private final RestaurantDrawRepository restaurantDrawRepository;

    Random rand = new Random();

    public Restaurant getById(Long restaurantId) {
        return restaurantDrawRepository.getById(restaurantId);
    }

    public List<RestaurantCoreInfoDto> draw(List<String> cuisines, List<String> positions) {
        if (cuisines.contains("전체")) {
            cuisines = null;
        }
        if (positions.contains("전체")) {
            positions = null;
        }
        List<RestaurantCoreInfoDto> restaurants = restaurantDrawRepository.draw(cuisines, positions);

        // 조건에 맞는 식당이 없을 경우 404 에러 반환
        if (restaurants.isEmpty()) {
            throw new DataNotFoundException(RESTAURANT_NOT_FOUND, "해당 조건에 맞는 맛집이 존재하지 않습니다.");
        }

        return pickRandomUnique(new ArrayList<>(restaurants));
    }

    private List<RestaurantCoreInfoDto> pickRandomUnique(List<RestaurantCoreInfoDto> candidates) {
        Collections.shuffle(candidates, rand);

        if (candidates.size() >= TARGET_SIZE) {
            return new ArrayList<>(candidates.subList(0, TARGET_SIZE));
        }
        List<RestaurantCoreInfoDto> result = new ArrayList<>(candidates);
        while (result.size() < TARGET_SIZE) {
            result.add(candidates.get(rand.nextInt(candidates.size())));
        }
        return result;
    }
}
