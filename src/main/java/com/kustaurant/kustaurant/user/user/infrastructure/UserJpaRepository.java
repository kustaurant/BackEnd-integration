package com.kustaurant.kustaurant.user.user.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findById(Long userId);
    Optional<UserEntity> findByProviderId(String providerId);

    @Query("""
           SELECT  u
           FROM    UserEntity u
           WHERE   EXISTS (
                     SELECT 1
                     FROM   EvaluationEntity e
                     WHERE  e.userId = u.id
                   )
           ORDER BY (
                     SELECT COUNT(e2)
                     FROM   EvaluationEntity e2
                     WHERE  e2.userId = u.id
                   ) DESC
           """)
    List<UserEntity> findUsersWithEvaluationCountDescending();


    /* 2) 특정 연·분기 기준 평가 개수 내림차순 */
    @Query("""
           SELECT  u
           FROM    UserEntity u
           WHERE   EXISTS (
                     SELECT 1
                     FROM   EvaluationEntity e
                     WHERE  e.userId = u.id
                       AND  FUNCTION('YEAR',    e.createdAt) = :year
                       AND  FUNCTION('QUARTER', e.createdAt) = :quarter
                   )
           ORDER BY (
                     SELECT COUNT(e2)
                     FROM   EvaluationEntity e2
                     WHERE  e2.userId = u.id
                       AND  FUNCTION('YEAR',    e2.createdAt) = :year
                       AND  FUNCTION('QUARTER', e2.createdAt) = :quarter
                   ) DESC
           """)
    List<UserEntity> findUsersByEvaluationCountForQuarter(@Param("year") int year,
                                                          @Param("quarter") int quarter);


    @Query("select count(u) from UserEntity u where u.loginApi = :loginApi")
    int countByLoginApi(@Param("loginApi") String loginApi);
}
