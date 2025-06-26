package com.kustaurant.kustaurant.global.auth.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class JwtUtilTest {
    private static final String SECRET = "0123456789ABCDEF0123456789ABCDEF";
    private static final Duration ACCESS_TTL  = Duration.ofMinutes(30);
    private static final Duration REFRESH_TTL = Duration.ofDays(15);

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        //dummy JwtProperties
        JwtProperties props = Mockito.mock(JwtProperties.class);
        Mockito.when(props.getSecret()).thenReturn(SECRET);
        Mockito.when(props.getAccessTtl()).thenReturn(ACCESS_TTL);
        Mockito.when(props.getRefreshTtl()).thenReturn(REFRESH_TTL);

        jwtUtil = new JwtUtil(props);
        jwtUtil.init();
    }

    @Test
    @DisplayName("AccessToken을 잘 발행한다")
    void generateAccessToken() {
        // given
        Long userId = 1L;
        String role = "ROLE_USER";

        // when
        String token = jwtUtil.generateAccessToken(userId, role);

        // then
        JwtUtil.ParsedToken tk = jwtUtil.parse(token);
        assertThat(tk.userId()).isEqualTo(userId);
        assertThat(tk.role()).isEqualTo(role);
        assertThat(tk.tokenType()).isEqualTo("AT");
        assertThat(jwtUtil.isValid(token)).isTrue();
    }

    @Test
    @DisplayName("refreshToken발행시 tokenType은 RT")
    void generateRefreshToken_claims() {
        // given
        Long userId = 5L;

        // when
        String token = jwtUtil.generateRefreshToken(userId, "ROLE_ADMIN");

        // then
        assertThat(jwtUtil.parse(token).tokenType()).isEqualTo("RT");
    }

    @Test
    @DisplayName("만료된 토큰은 isValid()가 false를 반환한다")
    void isValid_expiredToken() {
        // g
        SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        String expired = Jwts.builder()
                .claims(Jwts.claims()
                        .subject("9")
                        .add("role", "ROLE_USER")
                        .add("tokenType", "AT")
                        .build())
                .expiration(java.sql.Timestamp.from(java.time.Instant.now().minusSeconds(5)))
                .signWith(key)
                .compact();

        // w
        boolean valid = jwtUtil.isValid(expired);

        // t
        assertThat(valid).isFalse();
    }

    @Test
    @DisplayName("parse()는 ParsedToken 필드를 정확히 반환한다")
    void parse_returnsCorrectParsedToken() {
        // g
        Long userId = 42L;

        // w
        String token = jwtUtil.generateAccessToken(userId, "ROLE_USER");
        JwtUtil.ParsedToken tk = jwtUtil.parse(token);

        // t
        assertThat(tk.userId()).isEqualTo(userId);
        assertThat(tk.role()).isEqualTo("ROLE_USER");
        assertThat(tk.tokenType()).isEqualTo("AT");
    }

}