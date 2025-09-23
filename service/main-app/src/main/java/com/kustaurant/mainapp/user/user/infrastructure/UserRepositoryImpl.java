package com.kustaurant.mainapp.user.user.infrastructure;

import com.kustaurant.mainapp.common.dto.UserSummary;
import com.kustaurant.mainapp.user.login.api.domain.LoginApi;
import com.kustaurant.mainapp.user.user.domain.User;
import com.kustaurant.mainapp.user.user.domain.Nickname;
import com.kustaurant.mainapp.user.user.domain.PhoneNumber;
import com.kustaurant.mainapp.user.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository jpaRepo;

    @Override
    public Optional<User> findByProviderId(String providerId) {
        return jpaRepo.findByProviderId(providerId).map(UserEntity::toModel);
    }

    @Override
    public Boolean existsByNickname(Nickname nickname) {
        return jpaRepo.existsByNickname(nickname);
    }

    @Override
    public Boolean existsByPhoneNumber(PhoneNumber phoneNumber) {
        return jpaRepo.existsByPhoneNumber(phoneNumber);
    }


    @Override
    public Optional<User> findById(Long id) {
        return jpaRepo.findById(id).map(UserEntity::toModel);
    }

    @Override
    public User save(User user) {
        return jpaRepo.save(UserEntity.from(user)).toModel();
    }


    @Override
    public int countByLoginApi(LoginApi loginApi) {
        return jpaRepo.countByLoginApi(loginApi);
    }

    @Override
    public List<User> findByIdIn(List<Long> ids) {
        return jpaRepo.findByIdIn(ids).stream().map(UserEntity::toModel).collect(Collectors.toList());
    }

}
