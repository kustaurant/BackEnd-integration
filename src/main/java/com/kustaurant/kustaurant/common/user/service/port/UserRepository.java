package com.kustaurant.kustaurant.common.user.service.port;

import com.kustaurant.kustaurant.common.user.domain.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User getById(Integer id);
    Optional<User> findByProviderId(String providerId);
    Optional<User> findByNickname(String nickname);
    Optional<User> findById(Integer userId);
    User save(User user);

    List<User> findUsersWithEvaluationCountDescending();
    List<User> findUsersByEvaluationCountForQuarter(int year, int quarter);

    int countByLoginApi(String apple);
}
