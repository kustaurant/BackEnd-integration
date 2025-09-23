package com.kustaurant.mainapp.admin.adminPage.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public record PagedRestaurantResponse(
        List<RestaurantListResponse> restaurants,
        Long totalElements,
        Integer totalPages,
        Integer currentPage,
        Integer pageSize,
        Boolean hasNext,
        Boolean hasPrevious
) {

}