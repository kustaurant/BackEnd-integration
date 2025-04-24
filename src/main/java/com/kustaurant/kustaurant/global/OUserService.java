package com.kustaurant.kustaurant.global;

import com.kustaurant.kustaurant.common.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.common.user.infrastructure.OUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OUserService {
    private final OUserRepository OUserRepository;

    public UserEntity findUserById(Integer userId) {
        if (userId == null) {
            return null;
        }
        return OUserRepository.findByUserIdAndStatus(userId, "ACTIVE").orElse(null);
    }
}
