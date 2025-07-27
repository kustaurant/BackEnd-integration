//package com.kustaurant.kustaurant.user.login.api.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.kustaurant.kustaurant.user.login.api.infrastructure.RefreshTokenStore;
//import com.kustaurant.kustaurant.user.user.domain.User;
//import com.kustaurant.kustaurant.user.user.domain.enums.UserStatus;
//import com.kustaurant.kustaurant.user.user.domain.vo.Nickname;
//import com.kustaurant.kustaurant.user.user.service.port.UserRepository;
//import com.kustaurant.kustaurant.global.auth.jwt.JwtProperties;
//import com.kustaurant.kustaurant.global.auth.jwt.JwtUtil;
//import com.kustaurant.kustaurant.user.login.api.controller.response.TokenResponse;
//import com.kustaurant.kustaurant.user.login.api.infrastructure.AppleOAuthClient;
//import com.kustaurant.kustaurant.user.login.api.controller.request.AppleLoginRequest;
//import com.kustaurant.kustaurant.user.login.api.infrastructure.NaverOAuthClient;
//import com.kustaurant.kustaurant.user.user.domain.enums.UserRole;
//import com.kustaurant.kustaurant.user.login.api.controller.request.NaverLoginRequest;
//import io.jsonwebtoken.Claims;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class ApiLoginService {
//
//    private final UserRepository userRepository;
//    private final NaverOAuthClient naverApiService;
//    private final AppleOAuthClient appleApiService;
//
//    private final RefreshTokenStore refreshStore;
//    private final JwtUtil jwtUtil;
//
//    private final JwtProperties jwtProperties;
//
//    //1
//    //네이버 로그인 처리
//    public TokenResponse processNaverLogin(NaverLoginRequest req) {
//        String provider = req.provider();
//        String providerId = req.providerId();
//        String naverAccessToken = req.naverAccessToken();
//
//        /* 1) 네이버 API를 통해 사용자 정보 가져오기 */
//        JsonNode userInfo = naverApiService.getUserInfo(naverAccessToken);
//        String userEmail = userInfo.path("email").asText();
//
//        /* 2) 회원 조회 or 생성 */
//        User user = userRepository.findByProviderId(providerId)
//                .map(u -> rejoinIfDeleted(u, userEmail))
//                .orElseGet(() -> signUp(provider, providerId, userEmail));
//
//        /* 3) 토큰 발급 + Redis 저장  */
//        String access  = jwtUtil.generateAccessToken(user.getId(), user.getRole().getValue());
//        String refresh = jwtUtil.generateRefreshToken(user.getId(), user.getRole().getValue());
//        refreshStore.save(user.getId(), refresh, jwtProperties.getRefreshTtl());
//
//        return new TokenResponse(access, refresh);
//    }
//
//    //2
//    // 애플 로그인 처리
//    public TokenResponse processAppleLogin(AppleLoginRequest req) {
//        String provider = req.provider();
//        String identityToken = req.identityToken();
//        String authorizationCode = req.authorizationCode();
//
//        // identityToken 검증 및 사용자 정보 가져오기
//        Claims claims = appleApiService.verifyAppleIdentityToken(identityToken);
//        String appleId = claims.getSubject(); // `sub` claim이 사용자 ID
//
//        // 사용자 정보로 기존 회원 조회 또는 새로 가입 처리
//        User user = userRepository.findByProviderId(appleId)
//                .map(this::rejoinIfDeleted)
//                .orElseGet(() ->signUp(provider, appleId));
//
//        String access  = jwtUtil.generateAccessToken(user.getId(), user.getRole().getValue());
//        String refresh = jwtUtil.generateRefreshToken(user.getId(), user.getRole().getValue());
//        refreshStore.save(user.getId(), refresh, jwtProperties.getRefreshTtl());
//
//        return new TokenResponse(access, refresh);
//    }
//
//
//    /** 탈퇴 회원을 재활성화 for Naver */
//    private User rejoinIfDeleted(User user, String email) {
//        if (user.isDeleted()) {
//            user.revive(email, Nickname.fromEmail(email));
//        }
//        return user;
//    }
//
//    /** 탈퇴 회원을 재활성화 for Apple */
//    private User rejoinIfDeleted(User user) {
//        if (user.isDeleted()) {
//            user.revive(null, new Nickname(nextAppleNickname()));
//        }
//        return user;
//    }
//
//    /** 신규 회원 생성 for NAVER*/
//    private User signUp(String provider, String pid, String email) {
//        User newUser = User.builder()
//                .providerId(pid)
//                .loginApi(provider)
//                .email(email)
//                .nickname(Nickname.fromEmail(email))
//                .role(UserRole.USER)
//                .status(UserStatus.ACTIVE)
//                .createdAt(LocalDateTime.now())
//                .build();
//        return userRepository.save(newUser);
//    }
//
//    /** 신규 회원 생성 for APPLE*/
//    private User signUp(String provider, String appleId) {
//        User newUser = User.builder()
//                .providerId(appleId)
//                .loginApi(provider)
//                .nickname(new Nickname(nextAppleNickname()))
//                .role(UserRole.USER)
//                .status(UserStatus.ACTIVE)
//                .createdAt(LocalDateTime.now())
//                .build();
//        return userRepository.save(newUser);
//    }
//
//    private String nextAppleNickname() {
//        int idx = userRepository.countByLoginApi("APPLE") + 1;
//        return "애플사용자" + idx;
//    }
//
//}
//
//
//
