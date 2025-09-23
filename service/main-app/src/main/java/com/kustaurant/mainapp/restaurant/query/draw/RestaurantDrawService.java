package com.kustaurant.mainapp.restaurant.query.draw;

import static com.kustaurant.mainapp.global.exception.ErrorCode.*;

import com.kustaurant.mainapp.global.exception.exception.DataNotFoundException;
import com.kustaurant.mainapp.restaurant.query.common.dto.ChartCondition;
import com.kustaurant.mainapp.restaurant.query.common.dto.RestaurantCoreInfoDto;
import com.kustaurant.mainapp.restaurant.query.common.infrastructure.repository.RestaurantCoreInfoRepository;
import com.kustaurant.mainapp.restaurant.restaurant.domain.Restaurant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RestaurantDrawService {

    private final int TARGET_SIZE = 30;

    private final RestaurantDrawRepository restaurantDrawRepository;
    private final RestaurantCoreInfoRepository restaurantCoreInfoRepository;

    Random rand = new Random();

    public Restaurant getById(Long restaurantId) {
        return restaurantDrawRepository.getById(restaurantId);
    }

    public List<RestaurantCoreInfoDto> draw(ChartCondition condition) {
        // 정렬해서 페이지에 맞는 식당 id만 읽어오기
        List<Long> ids = restaurantDrawRepository.getRestaurantIds(condition);
        // 식당 데이터(+ 평가 여부, 즐찾 여부, 상황 리스트) 가져오기
        List<RestaurantCoreInfoDto> restaurants = restaurantCoreInfoRepository.getRestaurantTiers(
                ids, null);

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
