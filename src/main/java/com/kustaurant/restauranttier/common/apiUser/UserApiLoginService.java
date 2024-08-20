package com.kustaurant.restauranttier.common.apiUser;

import com.fasterxml.jackson.databind.JsonNode;
import com.kustaurant.restauranttier.common.apiUser.naver.NaverApiService;
import com.kustaurant.restauranttier.common.user.UserRole;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import com.kustaurant.restauranttier.tab5_mypage.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserApiLoginService {
    private final UserRepository userRepository;
    private final NaverApiService naverApiService;
    private final JwtUtil jwtUtil;


    //네이버 로그인 처리
    public User processNaverLogin(String provider, String providerId, String accessToken) {
        // 네이버 API를 통해 사용자 정보 가져오기
        JsonNode userInfo = naverApiService.getUserInfo(accessToken);

        String userEmail = userInfo.path("email").asText();

        // 사용자 정보로 기존 회원 조회
        Optional<User> optionalUser = userRepository.findByProviderId(providerId);

        User user;
        if (optionalUser.isPresent()) {
            // 기존 회원이 있으면 그 정보를 반환
            user = optionalUser.get();

        } else {
            // 없으면 새로운 회원을 생성하고 저장
            user = User.builder()
                    .providerId(providerId)
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


    // 애플 로그인 처리
    public String processAppleLogin(String provider, String identityToken, String authorizationCode) {
        // identityToken 검증 및 사용자 정보 가져오기
        Claims claims = jwtUtil.verifyAppleIdentityToken(identityToken);
        String userEmail = claims.get("email", String.class);
        String userIdentifier = claims.getSubject(); // `sub` claim이 사용자 ID

        // 사용자 정보로 기존 회원 조회 또는 새로 가입 처리
        Optional<User> optionalUser = userRepository.findByProviderId(userIdentifier);

        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            // 새로운 사용자 생성 및 저장
            user = User.builder()
                    .providerId(userIdentifier)
                    .loginApi(provider)
                    .userEmail(userEmail)
                    .userNickname(StringUtils.substringBefore(userEmail, "@"))
                    .status("ACTIVE")
                    .createdAt(LocalDateTime.now())
                    .userRole(UserRole.USER)
                    .build();
            userRepository.save(user);
        }

        // 새로운 JWT 액세스 토큰 생성
        return jwtUtil.generateAccessToken(user.getUserEmail());
    }


    //새로운 액세스 토큰 발급
    public String refreshAccessToken(String accessToken) {
        // 액세스 토큰이 만료되었는지 확인
        if (jwtUtil.isTokenExpired(accessToken)) {
            // 액세스 토큰에서 이메일 추출
            String userEmail = jwtUtil.getUserEmailFromToken(accessToken);

            // 해당 이메일로 사용자를 조회
            User user = userRepository.findByUserEmail(userEmail)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            // 서버에 저장된 리프레시 토큰을 가져옴
            String storedRefreshToken = user.getRefreshToken();

            // 리프레시 토큰이 유효한지 확인
            if (storedRefreshToken == null || !jwtUtil.validateToken(storedRefreshToken)) {
                throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
            }

            // 새로운 액세스 토큰 발급
            String newAccessToken = jwtUtil.generateAccessToken(user.getUserEmail());
            user.setAccessToken(newAccessToken);
            userRepository.save(user);

            return newAccessToken;
        } else {
            throw new IllegalArgumentException("액세스 토큰이 아직 유효합니다.");
        }
    }

    //로그아웃 처리
    public void logoutUser(Integer userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        // 리프레시 토큰을 삭제
        user.setRefreshToken(null);
        user.setAccessToken(null);
        userRepository.save(user);
    }


}



