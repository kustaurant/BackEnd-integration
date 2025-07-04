package com.kustaurant.kustaurant.restaurant.restaurant.controller.web;

import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.evaluation.comment.controller.response.EvalCommentResponse;
import com.kustaurant.kustaurant.restaurant.restaurant.controller.response.RestaurantDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RestaurantDetailWebDto {
    // TODO: RestaurantDetailDTO를 손봐서 Domain은 안가져오게 해야 할듯..
    private final RestaurantDetailDTO restaurantDetail;
    private final Restaurant domain;
    private final List<EvalCommentResponse> comments;
}
