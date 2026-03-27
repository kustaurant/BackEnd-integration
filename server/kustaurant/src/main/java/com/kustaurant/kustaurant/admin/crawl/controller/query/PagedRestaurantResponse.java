package com.kustaurant.kustaurant.admin.crawl.controller.query;

import com.kustaurant.kustaurant.admin.adminPage.controller.response.RestaurantListResponse;

import java.util.List;

public record PagedRestaurantResponse(
        List<RestaurantListResponse> restaurants,
        Long totalElements,
        Integer totalPages,
        Integer currentPage,
        Integer pageSize,
        Boolean hasNext,
        Boolean hasPrevious
) {}