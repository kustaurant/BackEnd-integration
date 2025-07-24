package com.kustaurant.kustaurant.restaurant.restaurant.constants;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

public abstract class RestaurantConstants {

    public static final String REPLACE_IMG_URL ="https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/NoImgRestaurant.png";

    public static final Integer SITUATION_GOE = 3;

    @Data
    public static class StarComment {
        @Schema(description = "별점", example = "4.5")
        private final double star;
        @Schema(description = "문구", example = "인생 최고의 식당입니다.")
        private final String comment;
    }

    public static String getCuisineImgUrl(String cuisine) {
        return "https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/cuisine-icon/" + cuisine.replaceAll("/", "") + ".svg";
    }
}
