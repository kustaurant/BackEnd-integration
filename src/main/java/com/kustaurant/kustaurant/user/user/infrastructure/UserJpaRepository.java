package com.kustaurant.kustaurant.user.user.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUserId(Long userId);
    Optional<UserEntity> findByProviderId(String providerId);
    boolean existsByNickname_Value(String nickname);
    boolean existsByPhoneNumber_Value(String phoneNumber);

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

    @Query("select count(u) from UserEntity u where u.loginApi = :loginApi")
    int countByLoginApi(@Param("loginApi") String loginApi);
}
