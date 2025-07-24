package com.kustaurant.kustaurant.restaurant.query.search.service;

import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import com.kustaurant.kustaurant.restaurant.query.chart.service.ChartRankingAssembler;
import com.kustaurant.kustaurant.restaurant.query.chart.service.port.RestaurantChartRepository;
import jakarta.annotation.Nullable;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantSearchService {

    private final RestaurantChartRepository restaurantChartRepository;
    private final ChartRankingAssembler chartRankingAssembler;

    public List<RestaurantCoreInfoDto> search(String[] kwList, @Nullable Long userId) {
        // 검색 결과 가져와서 DTO로 매핑
//        List<RestaurantTierDTO> dtoList = restaurantChartRepository.search(kwList)
//                .stream().map(RestaurantQueryMapper::toDto).toList();
        List<RestaurantCoreInfoDto> dtoList = new ArrayList<>();

        // 각 식당의 즐찾여부, 평가여부, 랭킹 정보 채우기
//        chartRankingAssembler.enrichDtoList(userId, dtoList, null);

        return dtoList;
    }
}
