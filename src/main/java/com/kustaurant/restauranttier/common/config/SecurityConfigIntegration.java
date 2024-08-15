//package com.kustaurant.restauranttier.common.config;
//
//import com.kustaurant.restauranttier.common.user.CustomLoginSuccessHandler;
//import com.kustaurant.restauranttier.common.user.CustomLogoutSuccessHandler;
//import com.kustaurant.restauranttier.common.user.CustomOAuth2UserService;
//import com.kustaurant.restauranttier.common.user3.settings.JwtAuthFilter;
//import com.kustaurant.restauranttier.common.user3.settings.JwtExceptionFilter;
//import com.kustaurant.restauranttier.common.user3.settings.MyApiAuthenticationFailureHandler;
//import com.kustaurant.restauranttier.common.user3.settings.MyApiAuthenticationSuccessHandler;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//@Configuration
//@RequiredArgsConstructor
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class SecurityConfigIntegration {
//
//    // 웹 관련 핸들러 및 서비스
//    private final CustomOAuth2UserService customOAuth2UserService;
//    private final CustomLoginSuccessHandler customLoginSuccessHandler;
//    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;
//
//    // 모바일 API 관련 핸들러 및 서비스
//    private final MyApiAuthenticationSuccessHandler oAuth2LoginSuccessHandler;
//    private final MyApiAuthenticationFailureHandler oAuth2LoginFailureHandler;
//    private final JwtAuthFilter jwtAuthFilter;
//    private final JwtExceptionFilter jwtExceptionFilter;
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                // 웹과 API에 대해 각각 다른 CSRF 설정 적용
//                .csrf(csrf -> csrf
//                        .ignoringRequestMatchers("/api/**") // API 요청에 대해 CSRF 보호 비활성화
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())) // 웹 요청에 대해 CSRF 보호 활성화
//                .headers((headerConfig) -> headerConfig
//                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
//                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 모든 요청에 대해 Stateless로 설정
//
//                // 인증 및 권한 설정
//                .authorizeHttpRequests((authorizeRequests) -> authorizeRequests
//                        .requestMatchers("/token/**", "/user/login", "/user/logout").permitAll() // 토큰 발급, 로그인/로그아웃 경로는 허용
//                        .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/favicon.ico", "/h2-console/**").permitAll() // 정적 리소스와 기본 경로 허용
//                        .requestMatchers("/api/**").authenticated() // API 경로는 인증 필요
//                        .anyRequest().permitAll()) // 기타 요청은 허용
//
//                // OAuth2 로그인 설정 (웹과 API 모두에서 동작 가능)
//                .oauth2Login(oauth2 -> oauth2
//                        .loginPage("/user/login")
//                        .defaultSuccessUrl("/")
//                        .failureUrl("/user/login")
//                        .userInfoEndpoint(userInfoEndPoint -> userInfoEndPoint
//                                .userService(customOAuth2UserService))
//                        .successHandler(oAuth2LoginSuccessHandler)
//                        .failureHandler(oAuth2LoginFailureHandler))
//
//                // 로그아웃 설정
//                .logout((logout) -> logout
//                        .logoutUrl("/user/logout")
//                        .invalidateHttpSession(true)
//                        .clearAuthentication(true)
//                        .deleteCookies("JSESSIONID")
//                        .permitAll()
//                        .logoutSuccessHandler(customLogoutSuccessHandler))
//
//                // JWT 인증 필터 추가 (모바일 API 요청에 사용)
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(jwtExceptionFilter, JwtAuthFilter.class);
//
//        return http.build();
//    }
//
//    // BCryptPasswordEncoder 암호화 객체를 빈으로 등록
//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    // 스프링 시큐리티의 인증을 처리
//    @Bean
//    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
//
//    // CORS 설정
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
