package com.kustaurant.restauranttier.tab5_mypage.service;

import com.kustaurant.restauranttier.common.apiUser.JwtUtil;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import com.kustaurant.restauranttier.tab5_mypage.repository.UserRepository;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserApiService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    //사용자의 Id를 Email로 검색한다
    public Integer getUserIdByEmail(String email) {
        User user=userRepository.findByUserEmail(email)
                .orElseThrow(()->new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        return user.getUserId();
    }

    //사용자의 Id를 토큰에서 가져온다
    public Integer getUserIdFromToken(String token) {
        String userEmail = jwtUtil.getUserEmailFromToken(token);
        return getUserIdByEmail(userEmail);
    }
}
