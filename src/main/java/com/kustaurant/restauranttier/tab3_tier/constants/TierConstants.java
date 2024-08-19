package com.kustaurant.restauranttier.tab3_tier.constants;

public class TierConstants {
    public static final int minNumberOfEvaluations = 2;

    // TODO: 티어 산출 기준 로직 수정
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
}
