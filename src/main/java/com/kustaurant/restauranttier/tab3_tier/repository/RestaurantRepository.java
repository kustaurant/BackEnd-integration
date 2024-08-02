package com.kustaurant.restauranttier.tab3_tier.repository;

import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant,Integer>, JpaSpecificationExecutor<Restaurant> {

    Restaurant findByRestaurantId(Integer id);
    Page<Restaurant> findByStatus(String status, Pageable pageable);
    Page<Restaurant> findByRestaurantCuisineAndStatus(String restaurantCuisine, String status, Pageable pageable);
    /*@Query("SELECT r " +
            "FROM Restaurant r " +
            "JOIN r.situationList s " +
            "WHERE r.status = :status AND s.situationName = :situation")
    Page<Restaurant> findActiveRestaurantsBySituation(String situation, String status, Pageable pageable);
    @Query("SELECT r " +
            "FROM Restaurant r " +
            "JOIN r.situationList s " +
            "WHERE r.status = :status AND s.situationName = :situation AND r.restaurantCuisine = :cuisine")
    Page<Restaurant> findActiveRestaurantsByCuisineAndSituation(String cuisine, String situation, String status, Pageable pageable);*/

    List<Restaurant> findByRestaurantCuisineAndStatus(String restaurantCuisine, String status);
    List<Restaurant> findByStatus(String status);

    List<Restaurant> findByRestaurantCuisineAndStatusAndRestaurantPosition(String restaurantCuisine, String status,String restaurantPosition);


    /*@Query("SELECT r " +
            "FROM Restaurant r " +
            "JOIN r.situationList s " +
            "WHERE r.status = :status AND s.situationName = :situation AND r.restaurantCuisine = :cuisine")
    List<Restaurant> findActiveRestaurantsByCuisineAndSituation(String cuisine, String situation, String status);*/

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

    // 식당의 메인 점수 순으로 cuisine에 해당하는 식당 리스트 반환
    @Query("SELECT r " +
            "FROM Restaurant r " +
            "WHERE r.restaurantCuisine = :cuisine AND r.status = 'ACTIVE' " +
            "AND (:position = '전체' OR r.restaurantPosition = :position) " +
            "ORDER BY " +
            "CASE WHEN r.restaurantEvaluationCount >= :dataNum " +
            "THEN CAST(r.restaurantScoreSum AS DOUBLE) / r.restaurantEvaluationCount " +
            "ELSE CAST(0.0 AS DOUBLE) END DESC")
    List<Restaurant> getRestaurantsByCuisineOrderedByAvgScore(
            @Param("cuisine") String cuisine,
            @Param("dataNum") Integer dataNum,
            @Param("position") String position
    );

    // TODO: 현재 주석처리함.
//    // 식당의 특정 situation 점수 순으로 situation에 해당하는 식당 리스트 반환
//    @Query("SELECT r " +
//            "FROM Restaurant r JOIN r.restaurantSituationRelationList e " +
//            "WHERE r.status = 'ACTIVE' AND e.situation = :situation AND e.dataCount >= :dataNum " +
//            "AND (:position = '전체' OR r.restaurantPosition = :position) " +
//            "ORDER BY CAST(e.scoreSum AS DOUBLE) / e.dataCount DESC")
//    List<Restaurant> getRestaurantsBySituationOrderedByAvgScore(
//            @Param("situation") Situation situation,
//            @Param("dataNum") Integer dataNum,
//            @Param("position") String position
//    );
//
//    @Query("SELECT r " +
//            "FROM Restaurant r JOIN r.restaurantSituationRelationList e " +
//            "WHERE r.status = 'ACTIVE' AND e.situation = :situation AND e.dataCount >= :dataNum AND r.restaurantCuisine = :cuisine " +
//            "AND (:position = '전체' OR r.restaurantPosition = :position) " +
//            "ORDER BY CAST(e.scoreSum AS DOUBLE) / e.dataCount + CAST(r.restaurantScoreSum AS DOUBLE) / r.restaurantEvaluationCount * 5 / 7 DESC")
//    List<Restaurant> getRestaurantsByCuisineAndSituationOrderedByAvgScore(
//            @Param("cuisine") String cuisine,
//            @Param("situation") Situation situation,
//            @Param("dataNum") Integer dataNum,
//            @Param("position") String position
//    );

    List<Restaurant> findByStatusAndRestaurantPosition(String status, String restaurantPosition);
}
