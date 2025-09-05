package com.kustaurant.kustaurant.user.user.service;

import com.kustaurant.kustaurant.global.exception.exception.user.UserNotFoundException;
import com.kustaurant.kustaurant.user.user.controller.port.UserService;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.domain.UserStatus;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    /** 유저 서비스를 별도로 만들지 않고 login 로직 쪽에서 user 도메인의 메서드를 직접 사용하도록 구현함
     *  적절한 방법 일지는 잘 모르겠음
     * */

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .filter(user->user.getStatus()== UserStatus.ACTIVE)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public User create(User user) {
        return null;
    }

    @Override
    public User update(User user) {
        return null;
    }
}
