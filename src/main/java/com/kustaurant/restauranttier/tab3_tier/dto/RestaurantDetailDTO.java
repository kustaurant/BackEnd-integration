package com.kustaurant.restauranttier.tab3_tier.dto;

import com.kustaurant.restauranttier.tab3_tier.constants.RestaurantConstants;
import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantMenu;
import com.kustaurant.restauranttier.tab3_tier.specification.RestaurantSpecification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "restaurant detail information")
public class RestaurantDetailDTO {
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

    public static RestaurantDetailDTO convertRestaurantToDetailDTO(Restaurant restaurant, Boolean isEvaluated, Boolean isFavorite, Boolean isIOS) {
        DecimalFormat df = new DecimalFormat("#.0");
        return new RestaurantDetailDTO(
                restaurant.getRestaurantId(),
                restaurant.getRestaurantImgUrl() == null || restaurant.getRestaurantImgUrl().equals("no_img") ? RestaurantConstants.REPLACE_IMG_URL : restaurant.getRestaurantImgUrl(),
                restaurant.getMainTier(),
                restaurant.getRestaurantCuisine(),
                "https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/" + restaurant.getRestaurantCuisine().replaceAll("/", "") + (isIOS ? ".svg" : ".png"),
                restaurant.getRestaurantPosition() == null ? "건대 주변" : restaurant.getRestaurantPosition(),
                restaurant.getRestaurantName(),
                restaurant.getRestaurantAddress() == null || restaurant.getRestaurantAddress().equals("no_address") ? "주소가 없습니다." : restaurant.getRestaurantAddress(),
                true,
                "영업중 오늘 10:00~20:00",
                restaurant.getRestaurantUrl(),
                restaurant.getRestaurantSituationRelationList().stream().filter(RestaurantSpecification::hasSituation).map(el -> el.getSituation().getSituationName()).collect(Collectors.toList()),
                restaurant.getPartnershipInfo(),
                restaurant.getRestaurantEvaluationCount(),
                restaurant.getRestaurantEvaluationCount() == 0 ? null : Double.parseDouble(df.format(restaurant.getRestaurantScoreSum() / restaurant.getRestaurantEvaluationCount())),
                isEvaluated,
                isFavorite,
                restaurant.getRestaurantFavorite().size(),
                restaurant.getRestaurantMenuList().stream().peek(restaurantMenu -> {
                    if (restaurantMenu.getMenuImgUrl().equals("no_img")) {
                        restaurantMenu.setMenuImgUrl(RestaurantConstants.REPLACE_IMG_URL);
                    } else if (restaurantMenu.getMenuImgUrl().contains("https://g-place.pstatic.net/assets/shared/images/menu_icon_noimg_food.png")) {
                        restaurantMenu.setMenuImgUrl(RestaurantConstants.REPLACE_IMG_URL);
                    }
                }).collect(Collectors.toList())
        );
    }
}
