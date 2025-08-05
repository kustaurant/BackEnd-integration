package com.kustaurant.kustaurant.restaurant.restaurant.constants;

import io.swagger.v3.oas.annotations.media.Schema;
import java.text.DecimalFormat;
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

    public static String getTierImgUrl(int tier) {
        return "https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/" + tier + "tier.png";
    }

    public static String restaurantImgUrlPostProcessing(String imgUrl) {
        return imgUrl == null || imgUrl.equals("no_img") ? RestaurantConstants.REPLACE_IMG_URL : imgUrl;
    }

    public static String positionPostprocessing(String restaurantPosition) {
        return restaurantPosition == null ? "건대 주변" : restaurantPosition;
    }

    public static String addressPostprocessing(String restaurantAddress) {
        if (restaurantAddress == null
                || restaurantAddress.isBlank()
                || restaurantAddress.equals("no_address")
        ) {
            restaurantAddress = "주소가 없습니다.";
        }
        return restaurantAddress;
    }
}
