package com.kustaurant.restauranttier.common;

import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import com.kustaurant.restauranttier.tab5_mypage.repository.UserRepository;
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
