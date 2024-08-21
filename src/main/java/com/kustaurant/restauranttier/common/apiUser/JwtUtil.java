package com.kustaurant.restauranttier.common.apiUser;

import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import com.kustaurant.restauranttier.tab5_mypage.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secretKey;
    // 액세스 토큰 유효 시간
    @Value("${jwt.accessTokenExpiration}")
    private long accessTokenExpiration;
    // 리프레시 토큰 유효 시간
    @Value("${jwt.refreshTokenExpiration}")
    private long refreshTokenExpiration;

    private final UserRepository userRepository;

    @PostConstruct
    protected void init() {
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // JWT 액세스 토큰 생성
    public String generateAccessToken(String userEmail) {
        return generateToken(userEmail, accessTokenExpiration);
    }

    // JWT 리프레시 토큰 생성
    public String generateRefreshToken(String userEmail) {
        return generateToken(userEmail, refreshTokenExpiration);
    }

    // JWT 토큰 생성 로직
    private String generateToken(String userEmail, long expirationTime) {
        Claims claims = Jwts.claims().setSubject(userEmail);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // JWT 토큰에서 userEmail 추출
    public String getUserEmailFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 이메일로 유저 아이디 반환
    public Integer getUserIdFromToken(String token) {
        String userEmail = getUserEmailFromToken(token);
        User user = userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return user.getUserId();
    }

    // JWT 토큰의 유효성 검증
    public boolean validateToken(String token) {
        try {
            //토큰이 기간이 만료됬는지 확인됨
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("JWT 토큰이 유효하지 않습니다.", e);
            return false;
        }
    }

}