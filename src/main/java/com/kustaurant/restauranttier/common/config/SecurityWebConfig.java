//package com.kustaurant.restauranttier.common.config;
//
//import com.kustaurant.restauranttier.common.user.CustomLoginSuccessHandler;
//import com.kustaurant.restauranttier.common.user.CustomLogoutSuccessHandler;
//import com.kustaurant.restauranttier.common.user.CustomOAuth2UserService;
//import com.kustaurant.restauranttier.common.user.CustomRedirectionFilter;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
//
//@Configuration
//@RequiredArgsConstructor
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class SecurityConfig {
//    private final CustomOAuth2UserService customOAuth2UserService;
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
////        http
////                .csrf(AbstractHttpConfigurer::disable)
////                .headers((headerConfig) -> headerConfig
////                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
////                .authorizeHttpRequests((auth) -> auth
////                        .anyRequest().permitAll())
////                .addFilterAfter(new CustomRedirectionFilter(), BasicAuthenticationFilter.class);
//        http
//                .csrf(AbstractHttpConfigurer::disable)
//                .headers((headerConfig) -> headerConfig
//                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
//                .authorizeHttpRequests((authorizeRequests) -> authorizeRequests
//                        .anyRequest().permitAll())
//                .oauth2Login((oauth2) -> oauth2
//                        .loginPage("/user/login")
//                        .defaultSuccessUrl("/")
//                        .failureUrl("/user/login")
//                        .userInfoEndpoint(userInfoEndPoint -> userInfoEndPoint
//                                .userService(customOAuth2UserService))
//                        .successHandler(new CustomLoginSuccessHandler()))
//                //로그아웃
//                .logout((logout) -> logout
//                        .logoutUrl("/user/logout")
//                        .invalidateHttpSession(true)
//                        .clearAuthentication(true)
//                        .deleteCookies("JSESSIONID")
//                        .permitAll()
//                        .logoutSuccessHandler(new CustomLogoutSuccessHandler()));
//        return http.build();
//    }
//
//    //    BCryptPasswordEncoder 암호화 객체를 빈으로 등록
//    @Bean
//    PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    //  스프링 시큐리티의 인증을 처리함
//    //  AuthenticationManager는 사용자 인증 시 앞에서 작성한 UserSecurityService와 PasswordEncoder를 내부적으로 사용하여 인증과 권한 부여 프로세스를 처리함
//    @Bean
//    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
//}

package com.kustaurant.restauranttier.common.config;

import com.kustaurant.restauranttier.common.user.CustomLoginSuccessHandler;
import com.kustaurant.restauranttier.common.user.CustomLogoutSuccessHandler;
import com.kustaurant.restauranttier.common.user.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityWebConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomLoginSuccessHandler customLoginSuccessHandler;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())) // CSRF 보호 활성화
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)) // Frame Options 비활성화
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll()) // 모든 요청 허용
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/user/login")
                        .defaultSuccessUrl("/")
                        .failureUrl("/user/login?error")
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(customLoginSuccessHandler)) // 로그인 성공 핸들러
                .logout(logout -> logout
                        .logoutUrl("/user/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler(customLogoutSuccessHandler)); // 로그아웃 성공 핸들러

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
