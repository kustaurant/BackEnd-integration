package com.kustaurant.restauranttier.common.apiUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    // JWT 액세스 토큰 생성
    public String generateAccessToken(Integer userId) {
        return generateToken(userId, accessTokenExpiration);
    }

    // JWT 리프레시 토큰 생성
    public String generateRefreshToken(Integer userId) {
        return generateToken(userId, refreshTokenExpiration);
    }

    // 테스트용 1초짜리 액세스 토큰 생성
    public String generateYOLOAccessToken(Integer userId) {
        return generateToken(userId, 1000); // 1초 = 1000밀리초
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
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    //jwt 토큰에서 userId 추출
    public Integer getUserIdFromToken(String token) {
        return Integer.parseInt(Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token.trim())
                .getBody()
                .getSubject());
    }

    //jwt 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            //토큰의 서명, 유효기간 등등 검증
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token.trim());
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT 토큰이 만료되었습니다.", e);
            return false;
        } catch (Exception e) {
            log.error("JWT 토큰이 유효하지 않습니다.", e);
            return false;
        }
    }


}