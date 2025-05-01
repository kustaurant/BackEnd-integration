package com.kustaurant.kustaurant.common.restaurant.application.service.query;

import com.kustaurant.kustaurant.common.restaurant.application.service.query.dto.RestaurantTierDTO;
import com.kustaurant.kustaurant.common.restaurant.application.service.query.port.RestaurantQueryRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantSearchService {

    private final RestaurantQueryRepository restaurantQueryRepository;
    private final RestaurantQueryAssembler restaurantQueryAssembler;

    public List<RestaurantTierDTO> search(String[] kwList, @Nullable Integer userId) {
        // 검색 결과 가져와서 DTO로 매핑
        List<RestaurantTierDTO> dtoList = restaurantQueryRepository.search(kwList)
                .stream().map(RestaurantQueryMapper::toDto).toList();

        // 각 식당의 즐찾여부, 평가여부, 랭킹 정보 채우기
        restaurantQueryAssembler.enrichDtoList(userId, dtoList, null);

        return dtoList;
    }
}
