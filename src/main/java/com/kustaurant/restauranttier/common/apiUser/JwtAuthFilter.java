package com.kustaurant.restauranttier.common.apiUser;

import com.kustaurant.restauranttier.tab5_mypage.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청 헤더에서 Authorization 값을 가져옴
        String jwt = getJwtFromRequest(request);

        if (StringUtils.hasText(jwt)) {
            // JWT 토큰의 유효성을 검증
            if (jwtUtil.validateToken(jwt)) {
                String email = jwtUtil.getUserEmailFromToken(jwt);

                // JWT 토큰에서 추출한 이메일로 사용자를 조회
                User user = userRepository.findByUserEmail(email)
                        .orElseThrow(() -> new IllegalStateException("User not found"));

                // 스프링 시큐리티 컨텍스트에 설정할 인증 객체 생성
                Authentication authentication = getAuthentication(user);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                log.warn("Invalid JWT token: {}", jwt);
            }
        }

        // 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private Authentication getAuthentication(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserEmail());
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}