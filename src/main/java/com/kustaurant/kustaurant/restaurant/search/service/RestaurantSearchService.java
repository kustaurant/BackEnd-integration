package com.kustaurant.kustaurant.restaurant.search.service;

import com.kustaurant.kustaurant.restaurant.restaurant.service.query.RestaurantQueryAssembler;
import com.kustaurant.kustaurant.restaurant.restaurant.service.query.RestaurantQueryMapper;
import com.kustaurant.kustaurant.restaurant.tier.dto.RestaurantTierDTO;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.RestaurantQueryRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantSearchService {

    private final RestaurantQueryRepository restaurantQueryRepository;
    private final RestaurantQueryAssembler restaurantQueryAssembler;

    public List<RestaurantTierDTO> search(String[] kwList, @Nullable Long userId) {
        // 검색 결과 가져와서 DTO로 매핑
        List<RestaurantTierDTO> dtoList = restaurantQueryRepository.search(kwList)
                .stream().map(RestaurantQueryMapper::toDto).toList();

        // 각 식당의 즐찾여부, 평가여부, 랭킹 정보 채우기
        restaurantQueryAssembler.enrichDtoList(userId, dtoList, null);

        return dtoList;
    }
}
