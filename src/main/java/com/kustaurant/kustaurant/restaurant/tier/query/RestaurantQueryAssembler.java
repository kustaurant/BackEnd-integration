package com.kustaurant.kustaurant.restaurant.tier.query;

import com.kustaurant.kustaurant.evaluation.evaluation.service.EvaluationQueryService;
import com.kustaurant.kustaurant.restaurant.tier.dto.RestaurantTierDTO;
import com.kustaurant.kustaurant.restaurant.favorite.service.RestaurantFavoriteService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RestaurantQueryAssembler {

    private final RestaurantFavoriteService restaurantFavoriteService;
    private final EvaluationQueryService evaluationQueryService;

    // Dto 객체에 데이터 채우기
    public void enrichDtoList(Long userId, List<RestaurantTierDTO> dtoList, @Nullable Integer ranking) {
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
    private void setFavorite(RestaurantTierDTO dto, Long userId) {
//        dto.setIsFavorite(restaurantFavoriteService.isUserFavorite(userId, dto.getRestaurantId()));
    }
    // 평가여부 설정하기 (userId가 null이면 false로 설정됨)
    private void setEvaluated(RestaurantTierDTO dto, Long userId) {
        dto.setIsEvaluated(evaluationQueryService.isUserEvaluated(userId, dto.getRestaurantId()));
    }
}
