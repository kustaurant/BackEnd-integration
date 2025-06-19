package com.kustaurant.kustaurant.user.service;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.post.domain.UserDTO;
import com.kustaurant.kustaurant.user.controller.port.UserService;
import com.kustaurant.kustaurant.user.domain.User;
import com.kustaurant.kustaurant.user.domain.enums.UserStatus;
import com.kustaurant.kustaurant.user.service.port.UserRepository;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User getActiveUserById(Integer id) {
        return userRepository.findById(id)
//                .filter(user -> "ACTIVE".equals(user.getStatus()))
                .filter(user->user.getStatus()== UserStatus.ACTIVE)
                .orElseThrow(()->new DataNotFoundException(USER_NOT_FOUND, id, "유저"));
    }

    @Override
    public User create(User user) {
        return null;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public Map<Integer, UserDTO> getUserDTOsByIds(List<Integer> userIds) {
        return userRepository.getUserDTOMapByIds(userIds);
    }
}
