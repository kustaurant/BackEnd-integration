package com.kustaurant.kustaurant.restaurant.tier.service;

import com.kustaurant.kustaurant.restaurant.tier.dto.RestaurantTierDTO;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChartRankingAssembler {

    // Dto 객체에 데이터 채우기
    public void enrichDtoList(List<RestaurantTierDTO> dtoList, @Nullable Integer ranking) {
        for (RestaurantTierDTO dto : dtoList) {
            if (ranking != null) {
                dto.assembleRanking(ranking);
                ranking += 1;
            }
        }
    }
}
