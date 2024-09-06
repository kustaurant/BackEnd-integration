package com.kustaurant.restauranttier.common.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    private final RequestCache requestCache = new HttpSessionRequestCache();
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        System.out.println("logout 진입!!!!!!!!!!!!!!!");

        clearSession(request);

        String fullURL = request.getHeader("Referer");
        if (fullURL != null && !fullURL.contains("/login")) {
            request.getSession().setAttribute("prevPage", fullURL);
        }

        // 기본 URI
        String uri = "/";

        if (fullURL != null && !fullURL.equals("")) {
            // 회원가입 - 로그인으로 넘어온 경우 "/"로 redirect
            if (
                    fullURL.contains("/auth/join") ||
                    fullURL.contains("/myPage") ||
                    fullURL.contains("/community/write")
            ) {
                uri = "/";
            } else {
                uri = fullURL;
            }
        }
        redirectStrategy.sendRedirect(request, response, uri);
    }

    // 로그인 실패 후 성공 시 남아있는 에러 세션 제거
    protected void clearSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}
