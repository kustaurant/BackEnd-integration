package com.kustaurant.kustaurant.global.auth.jwt;

import com.kustaurant.kustaurant.global.exception.exception.auth.AccessTokenInvalidException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtProperties jwtProperties;
    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    // JWT 액세스 토큰 생성
    public String generateAccessToken(Integer userId, String role) {
        return generate(userId, role, jwtProperties.getAccessTtl());
    }

    // JWT 리프레시 토큰 생성
    public String generateRefreshToken(Integer userId, String role) {
        return generate(userId, role, jwtProperties.getRefreshTtl());
    }

    // 테스트용 10초짜리 토큰 생성
    public String generateYOLOToken(Integer userId, String role) {
        return generate(userId, role,Duration.ofSeconds(10));
    }


    // JWT 토큰 생성 로직
    private String generate(Integer userId, String role, Duration ttl) {
        Instant now    = Instant.now();
        Instant expiry = now.plus(ttl);

        return Jwts.builder()
                .claims( Jwts.claims()
                        .subject(String.valueOf(userId))
                        .add("role",role)
                        .add("tokenType", ttl == jwtProperties.getAccessTtl() ? "AT" : "RT")
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

    public boolean isValid(String token) {
        try {
            // 내부에서 서명 + 만료일 + 헤더/클레임 형식 전부 체크
            parseAndValidate(token.trim());
            return true;
        } catch (JwtException | AccessTokenInvalidException e) {
            return false;
        }
    }

    public Claims parseAndValidate(String token) throws JwtException {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token.trim())
                    .getPayload();
            //서로다른 jwt라이브러리로 별도의 예외처리 해줘서 응답상태 통일해야함
        } catch (io.jsonwebtoken.JwtException e) {
            throw new AccessTokenInvalidException(e);
        }
    }

    public record ParsedToken(Integer userId, String role, String tokenType) {}
    public ParsedToken parse(String token) {
        Claims c = parseAndValidate(token);
        return new ParsedToken(Integer.valueOf(c.getSubject()),
                (String) c.get("role"),
                (String) c.get("tokenType"));
    }
}