package com.kustaurant.kustaurant.common.restaurant.infrastructure.repository;

import com.kustaurant.kustaurant.common.restaurant.infrastructure.entity.RestaurantCommentReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantCommentReportRepository extends JpaRepository<RestaurantCommentReport, Long> {
}
