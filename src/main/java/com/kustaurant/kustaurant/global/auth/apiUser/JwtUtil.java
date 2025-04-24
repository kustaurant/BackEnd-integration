package com.kustaurant.kustaurant.global.auth.apiUser;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
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
    private SecretKey key;

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

    // 테스트용 10초짜리 토큰 생성
    public String generateYOLOToken(Integer userId) {
        return generateToken(userId, 10000); // 10초 = 1000밀리초
    }


    // JWT 토큰 생성 로직
    private String generateToken(Integer userId, long expirationTime) {
        Instant now    = Instant.now();
        Instant expiry = now.plusMillis(expirationTime);

        return Jwts.builder()
                .claims( Jwts.claims()
                        .subject(String.valueOf(userId))
                        .build() )
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(key)
                .compact();
    }

    // JWT 토큰에서 userId 추출
    public Integer getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Integer.parseInt(claims.getSubject());
    }

    // JWT 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            // 토큰을 파싱하고 유효성을 검증합니다.
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
//            log.debug("토큰이 유효합니다: {}", token);
            return true;
        } catch (ExpiredJwtException e) {
            // 만료된 토큰의 경우 이 예외가 발생해야 합니다.
//            log.error("JWT 토큰이 만료되었습니다.", e);
            return false;
        } catch (JwtException e) {
            // 그 외의 경우, 토큰이 유효하지 않음을 알립니다.
//            log.error("JWT 토큰이 유효하지 않습니다.", e);
            return false;
        }
    }

    public Claims parseAndValidate(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token.trim())
                .getPayload();      // 유효하면 Claims 리턴, 아니면 예외
    }
}