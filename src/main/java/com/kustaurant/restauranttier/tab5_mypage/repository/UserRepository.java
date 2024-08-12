package com.kustaurant.restauranttier.tab5_mypage.repository;

import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {

    Optional<User> findByNaverProviderId(String naverProviderId);

    Optional<User> findByUserNickname(String userNickname);

    @Query("SELECT u " +
            "FROM User u " +
            "WHERE SIZE(u.evaluationList) > 0 " +
            "ORDER BY SIZE(u.evaluationList) DESC")
    List<User> findUsersWithEvaluationCountDescending();
}
