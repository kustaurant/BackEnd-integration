package com.kustaurant.kustaurant.user.infrastructure;

import com.kustaurant.kustaurant.post.domain.UserDTO;
import com.kustaurant.kustaurant.user.domain.User;
import com.kustaurant.kustaurant.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;

    @Override
    public User getById(Integer id) {
        return findById(id).orElseThrow(()->new RuntimeException("유저 없음"));
    }

    @Override
    public Optional<User> findByProviderId(String providerId) {
        return userJpaRepository.findByProviderId(providerId).map(UserEntity::toModel);
    }

    @Override
    public Optional<User> findByNickname(String nickname) {
        return userJpaRepository.findByNickname_Value(nickname).map(UserEntity::toModel);
    }

    @Override
    public Optional<User> findById(Integer id) {
        return userJpaRepository.findByUserId(id).map(UserEntity::toModel);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(UserEntity.from(user)).toModel();
    }

    @Override
    public List<User> findUsersWithEvaluationCountDescending() {
        return userJpaRepository.findUsersWithEvaluationCountDescending()
                .stream()
                .map(UserEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findUsersByEvaluationCountForQuarter(int year, int quarter) {
        return userJpaRepository.findUsersByEvaluationCountForQuarter(year, quarter)
                .stream()
                .map(UserEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public int countByLoginApi(String loginApi) {
        return userJpaRepository.countByLoginApi(loginApi);
    }

    @Override
    public Map<Integer, UserDTO> getUserDTOMapByIds(List<Integer> ids) {
        return userJpaRepository.findAllById(
                        ids.stream().map(Integer::longValue).toList()
                ).stream()
                .collect(Collectors.toMap(
                        UserEntity::getUserId,
                        UserDTO::convertUserToUserDTO
                ));
    }


    public Optional<UserEntity> findEntityById(Integer id) {
        return userJpaRepository.findByUserId(id);
    }
}
