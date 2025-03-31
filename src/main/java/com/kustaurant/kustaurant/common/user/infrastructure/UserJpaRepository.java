package com.kustaurant.kustaurant.common.user.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

//    Optional<OUserEntity> findByUsername(String username);
    Optional<UserEntity> findByUserId(Integer userId);
    Optional<UserEntity> findByUserIdAndStatus(Integer userId, String status);
    Optional<UserEntity> findByProviderId(String providerId);
    Optional<UserEntity> findByUserEmail(String email);
    Optional<UserEntity> findByUserNickname_Value(String nickname);

    @Query("SELECT u " +
            "FROM UserEntity u " +
            "WHERE SIZE(u.evaluationList) > 0 " +
            "ORDER BY SIZE(u.evaluationList) DESC")
    List<UserEntity> findUsersWithEvaluationCountDescending();

    @Query("SELECT u FROM UserEntity u JOIN u.evaluationList e " +
            "WHERE FUNCTION('YEAR', e.createdAt) = :year AND FUNCTION('QUARTER', e.createdAt) = :quarter " +
            "GROUP BY u " +
            "HAVING COUNT(e) > 0 " +
            "ORDER BY COUNT(e) DESC")
    List<UserEntity> findUsersByEvaluationCountForQuarter(@Param("year") int year, @Param("quarter") int quarter);

}
