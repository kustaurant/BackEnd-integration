package com.kustaurant.kustaurant.common;

import com.kustaurant.kustaurant.user.user.infrastructure.UserEntity;
import com.kustaurant.kustaurant.user.user.infrastructure.OUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OUserService {
    private final OUserRepository OUserRepository;

    public UserEntity findUserById(Long userId) {
        if (userId == null) {
            return null;
        }
        return OUserRepository.findByUserIdAndStatus(userId, "ACTIVE").orElse(null);
    }
}
