//package com.kustaurant.restauranttier.common.config;
//
//import com.kustaurant.restauranttier.common.user3.settings.JwtAuthFilter;
//import com.kustaurant.restauranttier.common.user3.settings.JwtExceptionFilter;
//import com.kustaurant.restauranttier.common.user3.settings.MyAuthenticationFailureHandler;
//import com.kustaurant.restauranttier.common.user3.settings.MyAuthenticationSuccessHandler;
//import com.kustaurant.restauranttier.common.user.CustomOAuth2UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig3 {
//    private final MyAuthenticationSuccessHandler oAuth2LoginSuccessHandler;
//    private final CustomOAuth2UserService customOAuth2UserService;
//    private final JwtAuthFilter jwtAuthFilter;
//    private final JwtExceptionFilter jwtExceptionFilter;
//    private final MyAuthenticationFailureHandler oAuth2LoginFailureHandler;
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .httpBasic(AbstractHttpConfigurer::disable) // HTTP 기본 인증을 비활성화
//                .cors(cors -> corsConfigurationSource()) // CORS 활성화
//                .csrf(csrf -> csrf
//                        .ignoringRequestMatchers("/api/**")) // /api/** 경로에 대해 CSRF 보호 비활성화
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션관리 정책을 STATELESS로 설정 // 세션관리 정책을 STATELESS(세션이 있으면 쓰지도 않고, 없으면 만들지도 않는다)
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/token/**").permitAll() // 토큰 발급을 위한 경로는 모두 허용
//                        .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/favicon.ico", "/h2-console/**").permitAll()
//                        .anyRequest().authenticated()) // 그 외의 모든 요청은 인증이 필요하다
//                .oauth2Login(oauth2 -> oauth2
//                        // OAuth2 로그인 시 사용자 정보를 가져오는 엔드포인트와 사용자 서비스를 설정
//                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
//                        .failureHandler(oAuth2LoginFailureHandler) // OAuth2 로그인 실패 시 처리할 핸들러를 지정
//                        .successHandler(oAuth2LoginSuccessHandler)); // OAuth2 로그인 성공 시 처리할 핸들러를 지정
//
//
//        // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 추가한다.
//        return http
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(jwtExceptionFilter, JwtAuthFilter.class)
//                .build();
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.addAllowedOrigin("*"); // 모든 도메인 허용. 필요시 특정 도메인만 허용 가능
//        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
//        configuration.addAllowedHeader("*"); // 모든 헤더 허용
//        configuration.setAllowCredentials(true); // 자격 증명 허용
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//}
