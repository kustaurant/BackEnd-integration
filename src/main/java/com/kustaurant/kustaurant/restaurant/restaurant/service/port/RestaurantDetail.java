package com.kustaurant.kustaurant.restaurant.restaurant.service.port;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.RestaurantMenu;
import com.kustaurant.kustaurant.restaurant.restaurant.constants.RestaurantConstants;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Schema(description = "restaurant detail information")
@AllArgsConstructor
public class RestaurantDetail {

    @Schema(description = "식당 id", example = "1")
    private Integer restaurantId;
    @Schema(description = "식당 메인 이미지 주소", example = "https://search.pstatic.net/common/?autoRotate=true&type=w560_sharpen&src=https%3A%2F%2Fldb-phinf.pstatic.net%2F20221219_73%2F1671415873694AWTMq_JPEG%2FDSC04440.jpg")
    private String restaurantImgUrl;
    @Schema(description = "식당 티어", example = "1")
    private Integer mainTier;
    @Schema(description = "식당 음식 종류", example = "한식")
    private String restaurantCuisine;
    @Schema(description = "식당 음식 종류 이미지 URL", example = "https://kustaurant.com/img/home/%EA%B3%A0%EA%B8%B0.png")
    private String restaurantCuisineImgUrl;
    @Schema(description = "식당 위치", example = "건입~중문")
    private String restaurantPosition;
    @Schema(description = "식당 이름", example = "제주곤이칼국수 건대점")
    private String restaurantName;
    @Schema(description = "식당 주소", example = "서울시 광진구 어딘가 222-22, 304호")
    private String restaurantAddress;
    @Schema(description = "영업중 여부", example = "true")
    private Boolean isOpen;
    @Schema(description = "영업 시간", example = "오늘 10:00~20:00")
    private String businessHours;
    @Schema(description = "네이버 지도 url", example = "map.naver.com/2222")
    private String naverMapUrl;
    @Schema(description = "상황 리스트", example = "[\"혼밥\", \"배달\"]")
    private List<String> situationList;
    @Schema(description = "제휴 정보", example = "학생증 제시 시에 전메뉴 10% 할인 대박!!!!")
    private String partnershipInfo;
    @Schema(description = "평가 수", example = "100")
    private Integer evaluationCount;
    @Schema(description = "식당 평점", example = "4.4")
    private Double restaurantScore;
    @Schema(description = "식당 평가 여부", example = "false")
    private Boolean isEvaluated;
    @Schema(description = "식당 즐겨찾기 여부", example = "false")
    private Boolean isFavorite;
    @Schema(description = "즐겨찾기 횟수")
    private Integer favoriteCount;
    @Schema(description = "메뉴 리스트")
    private List<RestaurantMenu> restaurantMenuList;

    // 웹 사이트 렌더링 용
    @JsonIgnore
    private String restaurantType;
    @JsonIgnore
    private String restaurantTel;
    @JsonIgnore
    private Integer visitCount;
    @JsonIgnore
    private Double latitude;
    @JsonIgnore
    private Double longitude;

    @QueryProjection
    public RestaurantDetail(
            Integer restaurantId,
            String restaurantImgUrl,
            Integer mainTier,
            String restaurantCuisine,
            String restaurantPosition,
            String restaurantName,
            String restaurantAddress,
            String naverMapUrl,
            Set<String> situationSet,
            String partnershipInfo,
            Integer evaluationCount,
            Double restaurantScoreSum,
            Boolean isEvaluated,
            Boolean isFavorite,
            Long favoriteCount,
            Set<RestaurantMenu> restaurantMenuSet,
            String restaurantType,
            String restaurantTel,
            Integer visitCount,
            Double latitude,
            Double longitude
    ) {
            this (
                    restaurantId,
                    restaurantImgUrl,
                    mainTier,
                    restaurantCuisine,
                    RestaurantConstants.getCuisineImgUrl(restaurantCuisine),
                    positionPostprocessing(restaurantPosition),
                    restaurantName,
                    addressPostprocessing(restaurantAddress),
                    true,
                    "-",
                    naverMapUrl,
                    situationSet.stream().toList(),
                    partnershipInfo,
                    evaluationCount,
                    avgScorePostprocessing(restaurantScoreSum, evaluationCount),
                    isEvaluated,
                    isFavorite,
                    (int) (long) favoriteCount,
                    restaurantMenuSet.stream().toList(),
                    restaurantType,
                    restaurantTel,
                    visitCount,
                    latitude,
                    longitude
            );
    }

    private static double avgScorePostprocessing(double scoreSum, long evaluationCount) {
            DecimalFormat df = new DecimalFormat("#.0");
            return evaluationCount == 0 ? null : Double.parseDouble(df.format(scoreSum / evaluationCount));
    }

    private static String positionPostprocessing(String restaurantPosition) {
            return restaurantPosition == null ? "건대 주변" : restaurantPosition;
    }

    private static String addressPostprocessing(String restaurantAddress) {
            if (restaurantAddress == null
                    || restaurantAddress.isBlank()
                    || restaurantAddress.equals("no_address")
            ) {
                    restaurantAddress = "주소가 없습니다.";
            }
            return restaurantAddress;
    }
}
