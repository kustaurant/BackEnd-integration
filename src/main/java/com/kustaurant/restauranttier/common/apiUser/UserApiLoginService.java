package com.kustaurant.restauranttier.common.apiUser;

import com.fasterxml.jackson.databind.JsonNode;
import com.kustaurant.restauranttier.common.user.UserRole;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import com.kustaurant.restauranttier.tab5_mypage.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.Date;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserApiLoginService {
    private final UserRepository userRepository;
    private final NaverApiService naverApiService;
    private final JwtUtil jwtUtil;

    public User processNaverLogin(String provider, String providerId, String accessToken) {
        // 네이버 API를 통해 사용자 정보 가져오기
        JsonNode userInfo = naverApiService.getUserInfo(accessToken);

        String userEmail = userInfo.path("email").asText();

        // 사용자 정보로 기존 회원 조회
        Optional<User> optionalUser = userRepository.findByNaverProviderId(providerId);

        User user;
        if (optionalUser.isPresent()) {
            // 기존 회원이 있으면 그 정보를 반환
            user = optionalUser.get();

        } else {
            // 없으면 새로운 회원을 생성하고 저장
            user = User.builder()
                    .naverProviderId(providerId)
                    .loginApi(provider)
                    .userEmail(userEmail)
                    .userNickname(StringUtils.substringBefore(userEmail, "@"))
                    .status("ACTIVE")
                    .createdAt(LocalDateTime.now())
                    .userRole(UserRole.USER)
                    .build();
        }

        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUserEmail());
        String newAccessToken = jwtUtil.generateAccessToken(user.getUserEmail());
        user.setAccessToken(newAccessToken);
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return user;
    }


    // 리프레시 토큰을 사용해 새로운 액세스 토큰 발급
    public String refreshAccessToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        String userEmail = jwtUtil.getUserEmailFromToken(refreshToken);
        Optional<User> userOptional = userRepository.findByUserEmail(userEmail);
        User user = userOptional.get();

        // 리프레시 토큰이 일치하는지 확인
        if (!user.getRefreshToken().equals(refreshToken)) {
            throw new IllegalArgumentException("리프레시 토큰이 일치하지 않습니다.");
        }

        // 새로운 액세스 토큰 발급
        String newAccessToken = jwtUtil.generateAccessToken(user.getUserEmail());
        user.setAccessToken(newAccessToken);
        userRepository.save(user);

        return newAccessToken;
    }

    //로그아웃 처리
    public void logoutUser(String refreshToken) {
        // 리프레시 토큰 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        String userEmail = jwtUtil.getUserEmailFromToken(refreshToken);
        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 리프레시 토큰을 삭제 (또는 무효화)
        user.setRefreshToken(null);
        user.setAccessToken(null);
        userRepository.save(user);
    }
}



