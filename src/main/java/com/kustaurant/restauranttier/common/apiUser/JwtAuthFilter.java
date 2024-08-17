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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
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

    /**
     * 요청에서 JWT 토큰을 추출하는 메서드.
     *
     * @param request 클라이언트의 HTTP 요청 객체.
     * @return 요청 헤더에 포함된 JWT 토큰 문자열. 토큰이 존재하지 않거나 유효하지 않으면 null을 반환.
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
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserEmail());
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}