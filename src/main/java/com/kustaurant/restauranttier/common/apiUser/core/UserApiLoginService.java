package com.kustaurant.restauranttier.common.apiUser.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.kustaurant.restauranttier.common.apiUser.JwtUtil;
import com.kustaurant.restauranttier.common.apiUser.apple.AppleApiService;
import com.kustaurant.restauranttier.common.apiUser.naver.NaverApiService;
import com.kustaurant.restauranttier.common.webUser.UserRole;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import com.kustaurant.restauranttier.tab5_mypage.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserApiLoginService {
    private final UserRepository userRepository;
    private final NaverApiService naverApiService;
    private final AppleApiService appleApiService;
    private final JwtUtil jwtUtil;

    //1
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
            if(user.getStatus().equals("DELETED")){
                //탈퇴한 회원이 재가입 하는 경우
                user.setUserEmail(userEmail);
                user.setUserNickname(StringUtils.substringBefore(userEmail, "@"));
                user.setStatus("ACTIVE");
                user.setCreatedAt(LocalDateTime.now());
            }
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

            // 회원가입 시에 DB에 저장해서 userId를 초기화함.
            userRepository.save(user);
        }

        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUserId());
        String newAccessToken = jwtUtil.generateAccessToken(user.getUserId());
        user.setAccessToken(newAccessToken);
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return user;
    }

    //2
    // 애플 로그인 처리
    public User processAppleLogin(String provider, String identityToken, String authorizationCode) {
        // identityToken 검증 및 사용자 정보 가져오기
        Claims claims = appleApiService.verifyAppleIdentityToken(identityToken);
        String userIdentifier = claims.getSubject(); // `sub` claim이 사용자 ID

        // 사용자 정보로 기존 회원 조회 또는 새로 가입 처리
        Optional<User> optionalUser = userRepository.findByProviderId(userIdentifier);
        int appleUserCount = userRepository.countByUserNicknameStartingWith("애플사용자");

        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            if(user.getStatus().equals("DELETED")){
                //탈퇴한 회원이 재가입 하는 경우
                user.setUserNickname("애플사용자" + (appleUserCount + 1));
                user.setStatus("ACTIVE");
                user.setCreatedAt(LocalDateTime.now());
            }
        } else {
            // 새로운 사용자 생성 및 저장
            user = User.builder()
                    .providerId(userIdentifier)
                    .loginApi(provider)
                    .userNickname("애플사용자" + (appleUserCount + 1))
                    .status("ACTIVE")
                    .createdAt(LocalDateTime.now())
                    .userRole(UserRole.USER)
                    .build();
            userRepository.save(user);
        }

        String newRefreshToken = jwtUtil.generateRefreshToken(user.getUserId());
        String newAccessToken = jwtUtil.generateAccessToken(user.getUserId());
        user.setAccessToken(newAccessToken);
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        // 새로운 JWT 액세스 토큰 생성
        return user;
    }

    //3
    //새로운 액세스 토큰 발급
    public String refreshAccessToken(String accessToken) {
        Integer userId;
        try {
            // 액세스 토큰이 만료되었는지 확인
            userId = jwtUtil.getUserIdFromToken(accessToken);
            if (jwtUtil.validateToken(accessToken)) {
                throw new IllegalArgumentException("액세스 토큰이 아직 유효합니다.");
            }
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우 새로운 액세스 토큰 발급 로직으로 이동
            userId = e.getClaims().getSubject() != null ? Integer.parseInt(e.getClaims().getSubject()) : null;
        }

        // 만료된 토큰이므로 새 토큰을 발급
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        String storedRefreshToken = user.getRefreshToken();

        if (storedRefreshToken == null || !jwtUtil.validateToken(storedRefreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 새로운 액세스 토큰 발급
        String newAccessToken = jwtUtil.generateAccessToken(user.getUserId());
        user.setAccessToken(newAccessToken);
        userRepository.save(user);

        return newAccessToken;
    }

    //4
    //로그아웃 처리
    public void logoutUser(Integer userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        // 리프레시 토큰을 삭제
        user.setRefreshToken(null);
        user.setAccessToken(null);
        userRepository.save(user);
    }

    //5
    //회원탈퇴 처리
    @Transactional
    public boolean deleteUserById(Integer userId) {
        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                User user=userOptional.get();
                user.setStatus("DELETED");
                user.setUserNickname("(탈퇴한 회원)");
                user.setAccessToken(null);
                user.setRefreshToken(null);
                user.setPhoneNumber(null);
                user.setUserEmail(null);
                userRepository.save(user);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    //6
    //유효기간이 10초인 테스트용 액세스토큰을 발급해줌
    public String yoloAccessToken(String accessToken) {
            Integer userId = jwtUtil.getUserIdFromToken(accessToken);

            User user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            // 새로운 액세스 토큰 발급
            String newAccessToken = jwtUtil.generateYOLOToken(user.getUserId());
            user.setAccessToken(newAccessToken);
            userRepository.save(user);

            return newAccessToken;
    }

    //7
    //유효기간이 10초인 테스트용 리프레시토큰을 발급해줌
    public String yoloRefreshToken(String accessToken) {
        Integer userId = jwtUtil.getUserIdFromToken(accessToken);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 새로운 리프레시 토큰 발급
        String newRefreshToken = jwtUtil.generateYOLOToken(user.getUserId());
        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return newRefreshToken;
    }


}



