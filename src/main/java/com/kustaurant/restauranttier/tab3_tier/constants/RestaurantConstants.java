package com.kustaurant.restauranttier.tab3_tier.constants;

import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantComment;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

public abstract class RestaurantConstants {
    public static final String REPLACE_IMG_URL ="https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/NoImgRestaurant.svg";

    public static String getIconImgUrl(User user, String userAgent) {
        if (user == null) {
            return null;
        }

        boolean isIOS = isIOS(userAgent);

        if (user.getEvaluationList().size() > 60) {
            return "https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/level3icon" + (isIOS ? ".svg" : ".png");
        } else if (user.getEvaluationList().size() > 30) {
            return "https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/level2icon" + (isIOS ? ".svg" : ".png");
        } else {
            return "https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/level1icon" + (isIOS ? ".svg" : ".png");
        }
    }

    public static boolean isIOS(String userAgent) {
        return userAgent.toLowerCase().contains("iphone") || userAgent.toLowerCase().contains("ipad") || userAgent.toLowerCase().contains("ios");
    }

    @Data
    public static class StarComment {
        @Schema(description = "별점", example = "4.5")
        private final double star;
        @Schema(description = "문구", example = "인생 최고의 식당입니다.")
        private final String comment;
    }
}
