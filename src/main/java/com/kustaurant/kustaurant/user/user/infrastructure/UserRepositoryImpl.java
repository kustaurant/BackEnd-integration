package com.kustaurant.kustaurant.user.user.infrastructure;

import com.kustaurant.kustaurant.global.exception.exception.user.UserNotFoundException;
import com.kustaurant.kustaurant.common.dto.UserSummary;
import com.kustaurant.kustaurant.user.login.api.domain.LoginApi;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.domain.vo.Nickname;
import com.kustaurant.kustaurant.user.user.domain.vo.PhoneNumber;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
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
    public Map<Long, UserSummary> getUserDTOMapByIds(List<Long> ids) {
        return jpaRepo.findAllById(ids).stream()
                .collect(Collectors.toMap(
                        UserEntity::getId,
                        e-> UserSummary.from(e.toModel()),
                        (a,b)->a,
                        LinkedHashMap::new
                ));
    }

    @Override
    public List<User> findByIdIn(List<Long> ids) {
        return jpaRepo.findByIdIn(ids).stream().map(UserEntity::toModel).collect(Collectors.toList());
    }

}
