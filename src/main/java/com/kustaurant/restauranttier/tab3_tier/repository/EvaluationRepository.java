package com.kustaurant.restauranttier.tab3_tier.repository;

import com.kustaurant.restauranttier.tab3_tier.entity.Evaluation;
import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation,Integer>{
    Optional<Evaluation> findByUserAndRestaurant(User user, Restaurant restaurant);
    Optional<Evaluation> findByUserAndRestaurantAndStatus(User user, Restaurant restaurant, String status);

    Integer countByRestaurant(Restaurant restaurant);

    Integer countAllByStatus(String status);

    Optional<Evaluation> findByEvaluationIdAndStatus(Integer evaluationId, String status);

    List<Evaluation> findByStatus(String status);
}
