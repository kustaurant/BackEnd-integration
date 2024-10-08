package com.kustaurant.restauranttier.tab3_tier.dto;

import com.kustaurant.restauranttier.tab3_tier.constants.RestaurantConstants;
import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.RoundingMode;
import java.text.DecimalFormat;

@Data
@AllArgsConstructor
@Schema(description = "restaurant tier entity")
public class RestaurantTierDTO {
    @Schema(description = "식당 id", example = "1")
    private Integer restaurantId;
    @Schema(description = "식당 ranking", example = "1")
    private Integer restaurantRanking;
    @Schema(description = "식당 이름", example = "제주곤이칼국수 건대점")
    private String restaurantName;
    @Schema(description = "식당 음식 종류", example = "한식")
    private String restaurantCuisine;
    @Schema(description = "식당 위치", example = "건입~중문")
    private String restaurantPosition;
    @Schema(description = "식당 메인 이미지 주소", example = "https://search.pstatic.net/common/?autoRotate=true&type=w560_sharpen&src=https%3A%2F%2Fldb-phinf.pstatic.net%2F20221219_73%2F1671415873694AWTMq_JPEG%2FDSC04440.jpg")
    private String restaurantImgUrl;
    @Schema(description = "식당 티어", example = "1")
    private Integer mainTier;
    @Schema(description = "식당 평가 여부", example = "false")
    private Boolean isEvaluated;
    @Schema(description = "식당 즐겨찾기 여부", example = "false")
    private Boolean isFavorite;
    @Schema(description = "식당 x좌표", example = "127.888")
    private String x;
    @Schema(description = "식당 y좌표", example = "37.333")
    private String  y;
    @Schema(description = "식당 제휴 정보", example = "건대생 공짜!!!")
    private String partnershipInfo;
    @Schema(description = "식당 점수", example = "4.5")
    private Double restaurantScore;

    public static RestaurantTierDTO convertRestaurantToTierDTO(Restaurant restaurant, Integer ranking, Boolean isEvaluated, Boolean isFavorite) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);
        Double score = restaurant.getRestaurantEvaluationCount() != 0 ? Double.parseDouble(df.format(restaurant.getRestaurantScoreSum() / restaurant.getRestaurantEvaluationCount())) : null;

        return new RestaurantTierDTO(
                restaurant.getRestaurantId(),
                ranking,
                restaurant.getRestaurantName(),
                restaurant.getRestaurantCuisine(),
                restaurant.getRestaurantPosition() == null ? "건대 주변" : restaurant.getRestaurantPosition(),
                restaurant.getRestaurantImgUrl() == null || restaurant.getRestaurantImgUrl().equals("no_img") ? RestaurantConstants.REPLACE_IMG_URL : restaurant.getRestaurantImgUrl(),
                restaurant.getMainTier(),
                isEvaluated,
                isFavorite,
                restaurant.getRestaurantLongitude(),
                restaurant.getRestaurantLatitude(),
                restaurant.getPartnershipInfo(),
                score
        );
    }
}
