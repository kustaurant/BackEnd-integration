package com.kustaurant.mainapp.global.config.security;

import com.kustaurant.mainapp.global.auth.jwt.CustomAccessDeniedHandler;
import com.kustaurant.mainapp.global.auth.jwt.JwtAuthFilter;
import com.kustaurant.mainapp.global.auth.jwt.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityApiConfig {

    private final JwtAuthFilter jwtAuthFilter;                     // JWT 검증 필터
    private final JwtAuthenticationEntryPoint entryPoint;          // 401 JSON
    private final CustomAccessDeniedHandler accessDeniedHandler;   // 403 JSON

    @Bean(name = "filterChainApi")
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                /* 이 체인은 아래 경로만 적용 */
            .securityMatcher("/api/v*/**")

                /* 기본 설정 */
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                /* 2) 인증/권한 실패 → JSON 응답 */
                .exceptionHandling(c -> c
                        .authenticationEntryPoint(entryPoint)      // 401
                        .accessDeniedHandler(accessDeniedHandler)) // 403

                /* 3) URL별 권한 */
                .authorizeHttpRequests(c -> c
                        .requestMatchers("/api/v*/auth/**").authenticated()
                        .anyRequest().permitAll())

                /* 4) JWT 필터를 UsernamePasswordAuthenticationFilter 앞에 삽입 */
                .addFilterBefore(jwtAuthFilter, AuthorizationFilter.class)

                .build();
    }

    /** local(dev) */
    @Bean(name = "corsConfigurationSource") @Profile("local")
    public CorsConfigurationSource corsLocal() {
        CorsConfiguration c = new CorsConfiguration();
        c.addAllowedOriginPattern("*");
        c.addAllowedMethod(CorsConfiguration.ALL);
        c.addAllowedHeader(CorsConfiguration.ALL);
        c.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
        s.registerCorsConfiguration("/**", c);
        return s;
    }

    /** prod */
    @Bean(name = "corsConfigurationSource") @Profile("prod")
    public CorsConfigurationSource corsProd() {
        CorsConfiguration c = new CorsConfiguration();
        c.addAllowedOrigin("https://kustaurant.com");
        c.addAllowedMethod(CorsConfiguration.ALL);
        c.addAllowedHeader(CorsConfiguration.ALL);
        c.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
        s.registerCorsConfiguration("/**", c);
        return s;
    }
}
