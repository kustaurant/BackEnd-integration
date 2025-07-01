package com.kustaurant.kustaurant.evaluation.evaluation.constants;

import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationQueryRepository;
import com.kustaurant.kustaurant.restaurant.restaurant.service.constants.RestaurantConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class EvaluationConstants {
    private final EvaluationQueryRepository evaluationQueryRepository;

    public static final int EVALUATION_ID_OFFSET = 10000000;


    public int calculateRestaurantTier(int evalCount, double avgScore) {
        if (!isEligibleForTier(evalCount)) {
            return -1;
        }
        if (avgScore >= 4.3) {
            return 1;
        } else if (avgScore > 3.9) {
            return 2;
        } else if (avgScore > 3.3) {
            return 3;
        } else if (avgScore > 2.5) {
            return 4;
        } else{
            return 5;
        }
    }

    private boolean isEligibleForTier(int evalCount) {
        int aa = getMinimumEvaluationCountForTier();
        return evalCount >= aa;
    }

    private int getMinimumEvaluationCountForTier() {
        return (int) (evaluationQueryRepository.countByStatus("ACTIVE") * 0.004);
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
