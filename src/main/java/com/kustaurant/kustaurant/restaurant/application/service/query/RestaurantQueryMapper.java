package com.kustaurant.kustaurant.restaurant.application.service.query;

import com.kustaurant.kustaurant.restaurant.application.service.query.dto.RestaurantTierDTO;
import com.kustaurant.kustaurant.restaurant.application.constants.RestaurantConstants;
import com.kustaurant.kustaurant.restaurant.domain.Restaurant;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class RestaurantQueryMapper {

    public static RestaurantTierDTO toDto(Restaurant restaurant) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);
        Double score = restaurant.getRestaurantEvaluationCount() != 0 ? Double.parseDouble(df.format(restaurant.getRestaurantScoreSum() / restaurant.getRestaurantEvaluationCount())) : null;

        return new RestaurantTierDTO(
                restaurant.getRestaurantId(),
                null,
                restaurant.getRestaurantName(),
                restaurant.getRestaurantCuisine(),
                restaurant.getRestaurantPosition() == null ? "건대 주변" : restaurant.getRestaurantPosition(),
                restaurant.getRestaurantImgUrl() == null || restaurant.getRestaurantImgUrl().equals("no_img") ? RestaurantConstants.REPLACE_IMG_URL : restaurant.getRestaurantImgUrl(),
                restaurant.getMainTier(),
                false,
                false,
                restaurant.getRestaurantLongitude(),
                restaurant.getRestaurantLatitude(),
                restaurant.getPartnershipInfo(),
                score,
                restaurant.getSituations(),
                restaurant.getRestaurantType()
        );
    }
}
