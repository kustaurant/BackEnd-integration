package com.kustaurant.restauranttier.tab1_home.dto;

import com.kustaurant.restauranttier.tab3_tier.dto.RestaurantTierDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantListsResponse {
    private List<RestaurantTierDTO> topRestaurantsByRating;
    private List<RestaurantTierDTO> restaurantsForMe;
    @Schema(description = "배너 이미지 리스트", example = "https://search.pstatic.net/common/?autoRotate=true&type=w560_sharpen&src=https%3A%2F%2Fldb-phinf.pstatic.net%2F20221219_73%2F1671415873694AWTMq_JPEG%2FDSC04440.jpg")
    private List<String> photoUrls;

}
