package com.kustaurant.kustaurant.web.restaurant;

import com.kustaurant.kustaurant.common.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.common.restaurant.domain.dto.RestaurantCommentDTO;
import com.kustaurant.kustaurant.common.restaurant.domain.dto.RestaurantDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RestaurantDetailWebDto {
    // TODO: RestaurantDetailDTO를 손봐서 Domain은 안가져오게 해야 할듯..
    private final RestaurantDetailDTO restaurantDetail;
    private final Restaurant domain;
    private final List<RestaurantCommentDTO> comments;
}
