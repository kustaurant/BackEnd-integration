package com.kustaurant.kustaurant.user.user.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OUserRepository extends JpaRepository<UserEntity,Integer> {

    Optional<UserEntity> findByProviderId(String providerId);

    Optional<UserEntity> findByNickname_Value(String userNickname);



    @Query("SELECT u " +
            "FROM UserEntity u " +
            "WHERE SIZE(u.evaluationList) > 0 " +
            "ORDER BY SIZE(u.evaluationList) DESC")
    List<UserEntity> findUsersWithEvaluationCountDescending();

    Optional<UserEntity> findByUserIdAndStatus(Long userId, String status);

}
