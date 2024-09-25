package com.kustaurant.restauranttier.tab3_tier.repository;

import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant,Integer>, JpaSpecificationExecutor<Restaurant> {

    Restaurant findByRestaurantId(Integer id);

    Optional<Restaurant> findByRestaurantIdAndStatus(Integer restaurantId, String status);
    List<Restaurant> findByRestaurantCuisineAndStatus(String restaurantCuisine, String status);
    List<Restaurant> findByStatus(String status);

    List<Restaurant> findByRestaurantCuisineAndStatusAndRestaurantPosition(String restaurantCuisine, String status,String restaurantPosition);

    // 페이징
    Page<Restaurant> findAll(Pageable pageable);
    // 검색결과 페이징
    Page<Restaurant> findAll(Specification<Restaurant> spec, Pageable pageable);

    // 방문 상위 몇 퍼센트인지
    @Query("SELECT 100.0 * COUNT(r) / (SELECT COUNT(e) FROM Restaurant e) " +
            "FROM Restaurant r " +
            "WHERE r.visitCount >= :#{#restaurant.visitCount}")
    Float getPercentOrderByVisitCount(Restaurant restaurant);

    // 식당의 메인 점수 순으로 식당 리스트 반환
    @Query("SELECT r " +
            "FROM Restaurant r " +
            "WHERE r.status = 'ACTIVE' " +
            "AND (:position = '전체' OR r.restaurantPosition = :position) " +
            "ORDER BY " +
            "CASE WHEN r.restaurantEvaluationCount >= :dataNum " +
            "THEN CAST(r.restaurantScoreSum AS DOUBLE) / r.restaurantEvaluationCount " +
            "ELSE CAST(0.0 AS DOUBLE) END DESC")
    List<Restaurant> getAllRestaurantsOrderedByAvgScore(
            @Param("dataNum") Integer dataNum,
            @Param("position") String position
    );

    List<Restaurant> findByStatusAndRestaurantPosition(String status, String restaurantPosition);

    List<Restaurant> findByStatusAndMainTierNot(String status, Integer mainTier);

    List<Restaurant> findByStatusAndRestaurantPositionAndMainTierNot(String status, String location, Integer mainTier);

    List<Restaurant> findByRestaurantCuisineAndStatusAndMainTierNot(String cuisine, String status, Integer mainTier);

    List<Restaurant> findByRestaurantCuisineAndStatusAndRestaurantPositionAndMainTierNot(String cuisine, String status, String location, Integer mainTier);
}
