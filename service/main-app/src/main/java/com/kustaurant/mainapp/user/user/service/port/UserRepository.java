package com.kustaurant.mainapp.user.user.service.port;

import com.kustaurant.mainapp.user.login.api.domain.LoginApi;
import com.kustaurant.mainapp.user.user.domain.User;
import com.kustaurant.mainapp.user.user.domain.Nickname;
import com.kustaurant.mainapp.user.user.domain.PhoneNumber;

import java.util.List;
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
