package com.kustaurant.kustaurant.user.user.infrastructure;

import com.kustaurant.kustaurant.global.exception.exception.business.UserNotFoundException;
import com.kustaurant.kustaurant.post.post.domain.dto.UserDTO;
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
    private final UserJpaRepository jpaRepository;

    @Override
    public User getById(Long id) {
        return findById(id).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public Optional<User> findByProviderId(String providerId) {
        return jpaRepository.findByProviderId(providerId).map(UserEntity::toModel);
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
        return jpaRepository.findById(id).map(UserEntity::toModel);
    }

    @Override
    public User save(User user) {
        return jpaRepository.save(UserEntity.from(user)).toModel();
    }

    @Override
    public List<User> findUsersWithEvaluationCountDescending() {
        return jpaRepository.findUsersWithEvaluationCountDescending()
                .stream()
                .map(UserEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findUsersByEvaluationCountForQuarter(int year, int quarter) {
        return jpaRepository.findUsersByEvaluationCountForQuarter(year, quarter)
                .stream()
                .map(UserEntity::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public int countByLoginApi(String loginApi) {
        return jpaRepository.countByLoginApi(loginApi);
    }


    @Override
    public Map<Long, UserDTO> getUserDTOMapByIds(List<Long> ids) {
        return jpaRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(
                        UserEntity::getId,
                        e->UserDTO.from(e.toModel()),
                        (a,b)->a,
                        LinkedHashMap::new
                ));
    }

    @Override
    public List<User> findByIdIn(List<Long> ids) {
        return jpaRepository.findByIdIn(ids).stream().map(UserEntity::toModel).collect(Collectors.toList());
    }

}
