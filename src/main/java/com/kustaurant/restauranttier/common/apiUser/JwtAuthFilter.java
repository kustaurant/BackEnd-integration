package com.kustaurant.restauranttier.common.apiUser;

import com.kustaurant.restauranttier.tab5_mypage.repository.UserRepository;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.kustaurant.restauranttier.tab5_mypage.entity.User;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // 특정 경로만 필터링
        if (requestURI.startsWith("/api/v") && requestURI.contains("/auth/")) {
            String jwt = getJwtFromRequest(request);
            if (StringUtils.hasText(jwt)) {
                try {
                    if (jwtUtil.validateTokenForFilter(jwt)) {
                        Integer userId = jwtUtil.getUserIdFromToken(jwt);
                        User user = userRepository.findByUserId(userId)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

                        Authentication authentication = getAuthentication(user);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        log.warn("Invalid JWT token: {}", jwt);
                    }
                } catch (ExpiredJwtException e) {
                    log.warn("Expired JWT token: {}", jwt);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 토큰이 만료되었습니다.");
                    SecurityContextHolder.clearContext();
                    return;
                } catch (JwtException e) {
                    log.warn("Invalid JWT token: {}", jwt);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다.");
                    SecurityContextHolder.clearContext();
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }


    /**
     * 요청에서 JWT 토큰을 추출하는 메서드.
     *
     * @param request 클라이언트의 HTTP 요청 객체.
     * @return 요청 헤더에 포함된 JWT 토큰 문자열. 토큰이 존재하지 않거나 유효하지 않으면 null 을 반환.
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 사용자 객체를 기반으로 Authentication 객체를 생성하는 메서드.
     *
     * @param user 인증에 사용할 User 객체.
     * @return 생성된 Authentication 객체. 이는 스프링 시큐리티 컨텍스트에 저장되어 인증된 사용자를 나타내는 데 사용됨.
     */
    private Authentication getAuthentication(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getUserRole().getValue()));

        return new UsernamePasswordAuthenticationToken(user.getUserId(), null, authorities);
    }
}