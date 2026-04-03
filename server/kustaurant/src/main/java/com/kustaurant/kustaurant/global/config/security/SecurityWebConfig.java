package com.kustaurant.kustaurant.global.config.security;

import com.kustaurant.kustaurant.global.auth.web.CustomLoginSuccessHandler;
import com.kustaurant.kustaurant.global.auth.web.CustomLogoutSuccessHandler;
import com.kustaurant.kustaurant.user.login.web.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityWebConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomLoginSuccessHandler customLoginSuccessHandler;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

    @Bean(name = "filterChainWeb")
    public SecurityFilterChain filterChain(HttpSecurity http, SessionRegistry sessionRegistry) throws Exception {
        http
                .securityMatcher(request -> !request.getServletPath().matches("^/api/v\\d+/.*$"))
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))

                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/restaurant/**", "/evaluation/**", "/user/myPage", "/community/write","/api/posts/**","/api/comments/**", "/api/images/**").authenticated()
                        .anyRequest().permitAll())

                .requestCache(rc -> {
                    var cache = new HttpSessionRequestCache();
                    cache.setRequestMatcher(request -> {
                        String method = request.getMethod();
                        String uri    = request.getRequestURI();
                        String accept = request.getHeader("Accept");

                        boolean isHtmlNav = accept != null && accept.contains("text/html");
                        boolean isApi     = uri.startsWith("/api/") || uri.startsWith("/web/api/");
                        boolean isAction  = uri.startsWith("/evaluation/");
                        boolean isLoginFlow = uri.startsWith("/user/") || uri.startsWith("/oauth2/") || uri.startsWith("/login");

                        return "GET".equalsIgnoreCase(method)
                                && isHtmlNav
                                && !isApi
                                && !isAction
                                && !isLoginFlow;
                    });
                    rc.requestCache(cache);
                })

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, authEx) -> {
                            String uri = req.getRequestURI();
                            String accept = req.getHeader("Accept");
                            String rw = req.getHeader("X-Requested-With");

                            boolean isApi = uri.startsWith("/api/") || uri.startsWith("/web/api/");
                            boolean isAjax = "XMLHttpRequest".equalsIgnoreCase(rw) || (accept != null && accept.contains("application/json"));

                            if (isApi || isAjax) {
                                res.setStatus(401);
                                res.setContentType("application/json;charset=UTF-8");
                                res.getWriter().write("{\"status\":\"UNAUTHORIZED\",\"message\":\"로그인이 필요한 기능입니다.\"}");
                                return;
                            }

                            new LoginUrlAuthenticationEntryPoint("/user/login").commence(req, res, authEx);
                        })
                        .accessDeniedHandler((req, res, ex2) -> {
                            String uri = req.getRequestURI();
                            String accept = req.getHeader("Accept");
                            boolean isApi = uri.startsWith("/api/") || uri.startsWith("/web/api/");
                            boolean isAjax = accept != null && accept.contains("application/json");

                            if (isApi || isAjax) {
                                res.setStatus(403);
                                res.setContentType("application/json;charset=UTF-8");
                                res.getWriter().write("{\"status\":\"FORBIDDEN\",\"message\":\"권한이 없습니다.\"}");
                                return;
                            }
                            res.sendRedirect("/user/login");
                        })
                )

                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/user/login")
                        .failureUrl("/user/login?error")
                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                        .successHandler(customLoginSuccessHandler))

                .logout(logout -> logout
                        .logoutUrl("/user/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler(customLogoutSuccessHandler))

                .sessionManagement(sess->sess
                        .maximumSessions(1)
                        .sessionRegistry(sessionRegistry)
                );

        return http.build();
    }

    @Bean
    public SpringSessionBackedSessionRegistry<? extends Session> sessionRegistry(
            FindByIndexNameSessionRepository<? extends Session> sessionRepository) {

        return new SpringSessionBackedSessionRegistry<>(sessionRepository);
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
