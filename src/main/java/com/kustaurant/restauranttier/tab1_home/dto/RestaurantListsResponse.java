package com.kustaurant.restauranttier.tab1_home.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantListsResponse {
    private List<RestaurantHomeDTO> topRestaurantsByRating;
    private List<RestaurantHomeDTO> restaurantsForMe;
}
