package com.kustaurant.mainapp.v1.restaurant.response;

import com.kustaurant.mainapp.restaurant.query.common.dto.RestaurantCoreInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

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

    public static RestaurantTierDTO fromV2(RestaurantCoreInfoDto v2) {
        return new RestaurantTierDTO(
                (int) (long) v2.getRestaurantId(),
                v2.getRestaurantRanking(),
                v2.getRestaurantName(),
                v2.getRestaurantCuisine(),
                v2.getRestaurantPosition(),
                v2.getRestaurantImgUrl(),
                v2.getMainTier(),
                v2.getIsEvaluated(),
                v2.getIsFavorite(),
                v2.getLongitude().toString(),
                v2.getLatitude().toString(),
                v2.getPartnershipInfo(),
                v2.getRestaurantScore()
        );
    }
}
