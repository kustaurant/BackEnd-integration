package com.kustaurant.kustaurant.restaurant.query.common.dto;

import java.util.List;

public record ChartCondition(
        List<String> cuisines,
        List<Long> situations,
        List<String> positions
) {

}
