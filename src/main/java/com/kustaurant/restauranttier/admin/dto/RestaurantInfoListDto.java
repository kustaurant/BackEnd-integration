package com.kustaurant.restauranttier.admin.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RestaurantInfoListDto {

    private List<RestaurantInfoDto> restaurants;
}
