package com.kustaurant.kustaurant.user.user.infrastructure;

import com.kustaurant.kustaurant.user.login.api.domain.LoginApi;
import com.kustaurant.kustaurant.user.user.domain.Nickname;
import com.kustaurant.kustaurant.user.user.domain.PhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    Optional<UserEntity> findById(Long userId);
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    Optional<UserEntity> findByProviderId(String providerId);


    int countByLoginApi(LoginApi loginApi);

    List<UserEntity> findByIdIn(List<Long> ids);

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    boolean existsByNickname(Nickname nickname);
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    boolean existsByPhoneNumber(PhoneNumber phoneNumber);
}
