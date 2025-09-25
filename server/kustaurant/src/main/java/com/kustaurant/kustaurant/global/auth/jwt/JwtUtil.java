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
    private JwtParser jwtParser;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
        this.jwtParser = Jwts.parser().verifyWith(key).build();
    }

    // 1. JWT 토큰 발급
    private String generate(Long userId, String role, TokenType tokenType, Duration ttl) {
        Instant now    = Instant.now();
        Instant expiry = now.plus(ttl);

        return Jwts.builder()
                .claims( Jwts.claims()
                        .subject(String.valueOf(userId))
                        .add("role",role)
                        .add("tokenType", tokenType.name())
                        .build() )
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(key)
                .compact();
    }

    public String generateAccess(Long userId, String role) {
        return generate(userId, role, TokenType.ACCESS, jwtProperties.getAccessTtl());
    }
    public String generateRefresh(Long userId, String role) {
        return generate(userId, role, TokenType.REFRESH, jwtProperties.getRefreshTtl());
    }
    public String generateYOLO(Long userId, String role) { // 테스트용 10초짜리 토큰 생성
        return generate(userId, role, TokenType.YOLO, Duration.ofSeconds(10));
    }

    //2. 파싱 & 검증
    public Claims parseAndValidate(String token) throws JwtException {
        try {
            return jwtParser.parseSignedClaims(token.trim()).getPayload();
        } catch (io.jsonwebtoken.JwtException e) { //서로다른 jwt라이브러리로 별도의 예외처리 해줘서 응답상태 통일해야함
            throw new AccessTokenInvalidException(e);
        }
    }

    //3.
    public ParsedToken parse(String token) {
        Claims c = parseAndValidate(token);
        return new ParsedToken(
                Long.valueOf(c.getSubject()),
                (String) c.get("role"),
                TokenType.valueOf((String) c.get("tokenType")));
    }

    public boolean isValid(String token) {
        try {// 내부에서 서명 + 만료일 + 헤더/클레임 형식 전부 체크
            parseAndValidate(token.trim());
            return true;
        } catch (JwtException | AccessTokenInvalidException e) {
            return false;
        }
    }

    public record ParsedToken(Long userId, String role, TokenType tokenType) {}
}