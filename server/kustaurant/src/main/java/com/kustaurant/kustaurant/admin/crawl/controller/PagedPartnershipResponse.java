package com.kustaurant.kustaurant.admin.crawl.controller;

import java.util.List;

public record PagedPartnershipResponse(
        List<PartnershipListResponse> partnerships,
        Long totalElements,
        Integer totalPages,
        Integer currentPage,
        Integer pageSize,
        Boolean hasNext,
        Boolean hasPrevious
) {}