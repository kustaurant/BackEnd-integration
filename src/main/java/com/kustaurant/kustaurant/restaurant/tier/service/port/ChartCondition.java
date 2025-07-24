package com.kustaurant.kustaurant.restaurant.tier.service.port;

import java.util.List;

public record ChartCondition(
        List<String> cuisines,
        List<Long> situations,
        List<String> positions
) {

}
