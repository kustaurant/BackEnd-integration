package com.kustaurant.mainapp.restaurant.query.chart.service;

import com.kustaurant.mainapp.restaurant.query.common.dto.RestaurantBaseInfoDto;
import com.kustaurant.mainapp.restaurant.query.common.dto.RestaurantCoreInfoDto;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChartRankingAssembler {

    // Dto 객체에 데이터 채우기
    public void enrichDtoListWithRanking(List<RestaurantBaseInfoDto> dtoList, @Nullable Integer ranking) {
        if (dtoList == null) {
            return;
        }
        for (RestaurantBaseInfoDto dto : dtoList) {
            if (ranking != null) {
                dto.assembleRanking(ranking);
                ranking += 1;
            }
        }
    }
}
