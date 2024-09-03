package com.kustaurant.restauranttier.common.apiUser;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

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
    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // JWT 액세스 토큰 생성
    public String generateAccessToken(Integer userId) {
        return generateToken(userId, accessTokenExpiration);
    }

    // JWT 리프레시 토큰 생성
    public String generateRefreshToken(Integer userId) {
        return generateToken(userId, refreshTokenExpiration);
    }

    // 테스트용 10초짜리 액세스 토큰 생성
    public String generateYOLOAccessToken(Integer userId) {
        return generateToken(userId, 10000); // 10초 = 1000밀리초
    }


    // JWT 토큰 생성 로직
    private String generateToken(Integer userId, long expirationTime) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT 토큰에서 userId 추출
    public Integer getUserIdFromToken(String token) {
        return Integer.parseInt(Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token.trim())
                .getBody()
                .getSubject());
    }

    // JWT 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            // 토큰을 파싱하고 유효성을 검증합니다.
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            log.debug("토큰이 유효합니다: {}", token);
            return true;
        } catch (ExpiredJwtException e) {
            // 만료된 토큰의 경우 이 예외가 발생해야 합니다.
            log.error("JWT 토큰이 만료되었습니다.", e);
            return false;
        } catch (JwtException e) {
            // 그 외의 경우, 토큰이 유효하지 않음을 알립니다.
            log.error("JWT 토큰이 유효하지 않습니다.", e);
            return false;
        }
    }

    // JWT 토큰 유효성 검증 for Filter
    public boolean validateTokenForFilter(String token) throws ExpiredJwtException, JwtException {
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        return true;
    }

}