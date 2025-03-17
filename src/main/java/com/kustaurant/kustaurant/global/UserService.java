package com.kustaurant.kustaurant.global;

import com.kustaurant.kustaurant.common.user.infrastructure.User;
import com.kustaurant.kustaurant.common.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findUserById(Integer userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findByUserIdAndStatus(userId, "ACTIVE").orElse(null);
    }
}
