package com.kustaurant.kustaurant.restaurant.tier.query;

import com.kustaurant.kustaurant.restaurant.tier.dto.RestaurantTierDTO;
import com.kustaurant.kustaurant.restaurant.restaurant.constants.RestaurantConstants;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

public class RestaurantQueryMapper {

    public static RestaurantTierDTO toDto(Restaurant restaurant) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);
        Double score = restaurant.getRestaurantEvaluationCount() != 0 ? Double.parseDouble(df.format(restaurant.getRestaurantScoreSum() / restaurant.getRestaurantEvaluationCount())) : null;

        return new RestaurantTierDTO(
                restaurant.getRestaurantId(),
                null,
                restaurant.getRestaurantName(),
                restaurant.getRestaurantCuisine().getValue(),
                restaurant.getGeoPosition() == null ? "건대 주변" : restaurant.getGeoPosition().position().getValue(),
                restaurant.getRestaurantImgUrl() == null || restaurant.getRestaurantImgUrl().equals("no_img") ? RestaurantConstants.REPLACE_IMG_URL : restaurant.getRestaurantImgUrl(),
                restaurant.getMainTier().getValue(),
                false,
                false,
                restaurant.getGeoPosition().coordinates().longitude() + "",
                restaurant.getGeoPosition().coordinates().latitude() + "",
                restaurant.getPartnershipInfo(),
                score,
                List.of(),
                restaurant.getRestaurantType()
        );
    }
}
