package com.kustaurant.kustaurant.common.evaluation.constants;

import com.kustaurant.kustaurant.common.evaluation.service.port.EvaluationRepository;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.Restaurant;
import com.kustaurant.kustaurant.common.restaurant.constants.RestaurantConstants;
import com.kustaurant.kustaurant.common.restaurant.infrastructure.restaurant.RestaurantEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class EvaluationConstants {
    private final EvaluationRepository evaluationRepository;

    public static final int EVALUATION_ID_OFFSET = 10000000;

    public boolean isHasTier(RestaurantEntity restaurant) {
        return restaurant.getRestaurantEvaluationCount() >= getMinimumEvaluationCountForTier();
    }

    public int getMinimumEvaluationCountForTier() {
        return (int) (evaluationRepository.findByStatus("ACTIVE").size() * 0.004);
    }

    public static int calculateRestaurantTier(double averageScore) {
        if (averageScore >= 4.3) {
            return 1;
        } else if (averageScore > 3.9) {
            return 2;
        } else if (averageScore > 3.3) {
            return 3;
        } else if (averageScore > 2.5) {
            return 4;
        } else if (averageScore >= 1.0) {
            return 5;
        } else {
            return -1;
        }
    }

    public static final List<RestaurantConstants.StarComment> STAR_COMMENTS = List.of(
            new RestaurantConstants.StarComment(0.5, "다시 갈 것 같진 않아요."),
            new RestaurantConstants.StarComment(1.0, "많이 아쉬워요"),
            new RestaurantConstants.StarComment(1.5, "다른데 갈껄"),
            new RestaurantConstants.StarComment(2.0, "조금 아쉬워요."),
            new RestaurantConstants.StarComment(2.5, "무난했어요."),
            new RestaurantConstants.StarComment(3.0, "괜찮았어요."),
            new RestaurantConstants.StarComment(3.5, "만족스러웠어요."),
            new RestaurantConstants.StarComment(4.0, "무조건 한번 더 올 것 같아요."),
            new RestaurantConstants.StarComment(4.5, "행복했습니다."),
            new RestaurantConstants.StarComment(5.0, "인생 최고의 식당입니다.")
    );
}
