package com.kustaurant.kustaurant.restaurant.query.chart.service;

import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantCoreInfoDto;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChartRankingAssembler {

    // Dto 객체에 데이터 채우기
    public void enrichDtoList(List<RestaurantCoreInfoDto> dtoList, @Nullable Integer ranking) {
        for (RestaurantCoreInfoDto dto : dtoList) {
            if (ranking != null) {
                dto.assembleRanking(ranking);
                ranking += 1;
            }
        }
    }
}
