package com.kustaurant.restauranttier.tab5_mypage.repository;

import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Integer> {

    Optional<User> findByProviderId(String providerId);

    Optional<User> findByUserNickname(String userNickname);

    Optional<User> findByUserEmail(String userEmail);
    Optional<User> findByUserId(Integer userId);

    @Query("SELECT u " +
            "FROM User u " +
            "WHERE SIZE(u.evaluationList) > 0 " +
            "ORDER BY SIZE(u.evaluationList) DESC")
    List<User> findUsersWithEvaluationCountDescending();

    Optional<User> findByUserIdAndStatus(Integer userId, String status);
    @Query("SELECT u FROM User u JOIN u.evaluationList e " +
            "WHERE FUNCTION('YEAR', e.createdAt) = :year AND FUNCTION('QUARTER', e.createdAt) = :quarter " +
            "GROUP BY u " +
            "HAVING COUNT(e) > 0 " +
            "ORDER BY COUNT(e) DESC")
    List<User> findUsersByEvaluationCountForQuarter(@Param("year") int year, @Param("quarter") int quarter);

    int countByUserNicknameStartingWith(String prefix);
}
