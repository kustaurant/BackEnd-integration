package com.kustaurant.kustaurant.user.user.service.port;

import com.kustaurant.kustaurant.common.dto.UserSummary;
import com.kustaurant.kustaurant.user.login.api.domain.LoginApi;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.domain.Nickname;
import com.kustaurant.kustaurant.user.user.domain.PhoneNumber;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findByProviderId(String providerId);
    Boolean existsByNickname(Nickname nickname);
    Boolean existsByPhoneNumber(PhoneNumber phoneNumber);

    Optional<User> findById(Long userId);
    User save(User user);


    int countByLoginApi(LoginApi apple);
    List<User> findByIdIn(List<Long> ids);
}
