package com.kustaurant.kustaurant.restaurant.query.chart.service;

import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantBaseInfoDto;
import com.kustaurant.kustaurant.restaurant.query.common.dto.RestaurantBaseInfoDtoV2;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChartRankingAssembler {

    // Dto 객체에 데이터 채우기
    public void enrichDtoListWithRanking(List<RestaurantBaseInfoDto> dtoList, @Nullable Integer ranking) {
        if (dtoList == null) return;

        for (RestaurantBaseInfoDto dto : dtoList) {
            if (ranking != null) {
                dto.assembleRanking(ranking);
                ranking += 1;
            }
        }
    }

    public void enrichDtoListWithRankingV2(List<RestaurantBaseInfoDtoV2> dtoList, @Nullable Integer ranking) {
        if (dtoList == null) return;

        for (RestaurantBaseInfoDtoV2 dto : dtoList) {
            if (ranking != null) {
                dto.assembleRanking(ranking);
                ranking += 1;
            }
        }
    }
}
