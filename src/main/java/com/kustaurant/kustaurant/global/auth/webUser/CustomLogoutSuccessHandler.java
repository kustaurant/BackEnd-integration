package com.kustaurant.kustaurant.global.auth.webUser;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        // 단순히 이전 페이지(Referer)로만 이동,
        // 로그인·회원가입·MY 페이지 같은 민감 URL이면 "/" 로 대체
        String referer = request.getHeader("Referer");
        String uri = shouldRedirectHome(referer) ? "/" : referer;
        redirectStrategy.sendRedirect(request, response, uri);
    }

    private boolean shouldRedirectHome(String url) {
        if (url == null) return true;
        return url.contains("/auth/login") ||
                url.contains("/auth/join")  ||
                url.contains("/myPage")     ||
                url.contains("/community/write");
    }
}
