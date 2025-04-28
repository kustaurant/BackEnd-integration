package com.kustaurant.kustaurant.common.restaurant.application.service.query;

import com.kustaurant.kustaurant.common.restaurant.application.service.query.dto.RestaurantTierDTO;
import com.kustaurant.kustaurant.common.evaluation.service.EvaluationService;
import com.kustaurant.kustaurant.common.restaurant.application.service.command.RestaurantFavoriteService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RestaurantQueryAssembler {

    private final RestaurantFavoriteService restaurantFavoriteService;
    private final EvaluationService evaluationService;

    // Dto 객체에 데이터 채우기
    public void enrichDtoList(Integer userId, List<RestaurantTierDTO> dtoList, @Nullable Integer ranking) {
        for (RestaurantTierDTO dto : dtoList) {
            if (ranking != null) {
                setRanking(dto, ranking);
                ranking += 1;
            }
            setFavorite(dto, userId);
            setEvaluated(dto, userId);
        }
    }
    // 랭킹 설정하기
    private void setRanking(RestaurantTierDTO dto, Integer ranking) {
        dto.setRestaurantRanking(dto.existTier() ? ranking : null);
    }
    // 즐찾여부 설정하기 (userId가 null이면 false로 설정됨)
    private void setFavorite(RestaurantTierDTO dto, Integer userId) {
        dto.setIsFavorite(restaurantFavoriteService.isUserFavorite(userId, dto.getRestaurantId()));
    }
    // 평가여부 설정하기 (userId가 null이면 false로 설정됨)
    private void setEvaluated(RestaurantTierDTO dto, Integer userId) {
        dto.setIsEvaluated(evaluationService.isUserEvaluated(userId, dto.getRestaurantId()));
    }
}
