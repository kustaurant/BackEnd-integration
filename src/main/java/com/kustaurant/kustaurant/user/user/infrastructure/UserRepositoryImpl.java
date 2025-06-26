package com.kustaurant.kustaurant.user.user.infrastructure;

import com.kustaurant.kustaurant.global.exception.exception.business.UserNotFoundException;
import com.kustaurant.kustaurant.post.domain.UserDTO;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.domain.vo.Nickname;
import com.kustaurant.kustaurant.user.user.domain.vo.PhoneNumber;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository jpa;

    @Override
    public User getById(Long id) {
        return findById(id).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public Optional<User> findByProviderId(String providerId) {
        return jpa.findByProviderId(providerId).map(UserEntity::toModel);
    }

    @Override
    public Boolean existsByNickname(Nickname nickname) {
        return null;
    }

    @Override
    public Boolean existsByPhoneNumber(PhoneNumber phoneNumber) {
        return null;
    }


    @Override
    public Optional<User> findById(Long id) {
        return jpa.findByUserId(id).map(UserEntity::toModel);
    }

    @Override
    public User save(User user) {
        return jpa.save(UserEntity.from(user)).toModel();
    }

    @Override
    public List<User> findUsersWithEvaluationCountDescending() {
        return jpa.findUsersWithEvaluationCountDescending()
                .stream()
                .map(UserEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findUsersByEvaluationCountForQuarter(int year, int quarter) {
        return jpa.findUsersByEvaluationCountForQuarter(year, quarter)
                .stream()
                .map(UserEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public int countByLoginApi(String loginApi) {
        return jpa.countByLoginApi(loginApi);
    }


    @Override
    public Map<Long, UserDTO> getUserDTOMapByIds(List<Long> ids) {
        return jpa.findAllById(ids)
                .stream()
                .collect(Collectors.toMap(
                        UserEntity::getId,
                        UserDTO::convertUserToUserDTO
                ));
    }

}
