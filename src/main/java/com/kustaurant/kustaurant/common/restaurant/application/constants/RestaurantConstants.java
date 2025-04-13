package com.kustaurant.kustaurant.common.restaurant.application.constants;

import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

public abstract class RestaurantConstants {
    public static final String REPLACE_IMG_URL ="https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/NoImgRestaurant.png";

    public static String getIconImgUrl(UserEntity UserEntity, String userAgent) {
        if (UserEntity == null) {
            return null;
        }

        boolean isIOS = isIOS(userAgent);

        if (UserEntity.getEvaluationList().size() > 60) {
            return "https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/level3icon" + (isIOS ? ".svg" : ".png");
        } else if (UserEntity.getEvaluationList().size() > 30) {
            return "https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/level2icon" + (isIOS ? ".svg" : ".png");
        } else {
            return "https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/level1icon" + (isIOS ? ".svg" : ".png");
        }
    }

    public static boolean isIOS(String userAgent) {
        return userAgent != null && (userAgent.contains("web") || userAgent.toLowerCase().contains("iphone") || userAgent.toLowerCase().contains("ipad") || userAgent.toLowerCase().contains("ios"));
    }

    @Data
    public static class StarComment {
        @Schema(description = "별점", example = "4.5")
        private final double star;
        @Schema(description = "문구", example = "인생 최고의 식당입니다.")
        private final String comment;
    }
}
