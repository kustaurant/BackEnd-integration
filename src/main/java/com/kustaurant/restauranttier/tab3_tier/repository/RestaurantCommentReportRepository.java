package com.kustaurant.restauranttier.tab3_tier.repository;

import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantComment;
import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantCommentReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantCommentReportRepository extends JpaRepository<RestaurantCommentReport, Long> {
}
