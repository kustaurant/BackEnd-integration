package com.kustaurant.restauranttier.tab1_home.dto;

import com.kustaurant.restauranttier.tab3_tier.dto.RestaurantTierDTO;
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
    private List<String> photoUrls;

}
