package com.kustaurant.kustaurant.user.login.api.service;

import com.kustaurant.kustaurant.global.exception.exception.business.UserNotFoundException;
import com.kustaurant.kustaurant.user.login.api.infrastructure.RefreshTokenStore;
import com.kustaurant.kustaurant.user.user.domain.User;
import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class WithdrawService {
    private final UserRepository userRepository;
    private final RefreshTokenStore refreshStore;

    //1. 로그아웃 처리
    public void logoutUser(Long userId) {
        refreshStore.delete(userId);
    }

    //2. 회원탈퇴 처리
    @Transactional
    public void deleteUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        user.softDelete();
        refreshStore.delete(userId);

        log.info("[User 회원탈퇴] id={}", userId);
    }
}
