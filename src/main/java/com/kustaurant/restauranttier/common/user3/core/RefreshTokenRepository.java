package com.kustaurant.restauranttier.common.user3;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    //accessToken으로 RefreshToken을 찾아온다.
    Optional<RefreshToken> findByAccessToken(String accessToken);
}
