package com.kustaurant.kustaurant.common.user.service;

import com.kustaurant.kustaurant.common.post.domain.UserDTO;
import com.kustaurant.kustaurant.common.user.controller.port.UserService;
import com.kustaurant.kustaurant.common.user.domain.User;
import com.kustaurant.kustaurant.common.user.service.port.UserRepository;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User getActiveUserById(Integer id) {
        return userRepository.findById(id)
                .filter(user -> "ACTIVE".equals(user.getStatus()))
//                .filter(user->user.getStatus()== UserStatus.ACTIVE)
                .orElseThrow(()->new DataNotFoundException("User" + id + " not found"));
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
