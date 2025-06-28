package com.kustaurant.kustaurant.restaurant.restaurant.service.constants;

import com.kustaurant.kustaurant.user.user.infrastructure.UserEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

public abstract class RestaurantConstants {
    public static final String REPLACE_IMG_URL ="https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/NoImgRestaurant.png";

    @Data
    public static class StarComment {
        @Schema(description = "별점", example = "4.5")
        private final double star;
        @Schema(description = "문구", example = "인생 최고의 식당입니다.")
        private final String comment;
    }
}
