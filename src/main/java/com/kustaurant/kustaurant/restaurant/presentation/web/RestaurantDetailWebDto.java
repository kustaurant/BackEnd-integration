package com.kustaurant.kustaurant.restaurant.presentation.web;

import com.kustaurant.kustaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.restaurant.application.service.command.dto.RestaurantCommentDTO;
import com.kustaurant.kustaurant.restaurant.application.service.command.dto.RestaurantDetailDTO;
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
