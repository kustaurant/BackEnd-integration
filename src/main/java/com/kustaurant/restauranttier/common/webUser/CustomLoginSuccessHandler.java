package com.kustaurant.restauranttier.common.webUser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final RequestCache requestCache = new HttpSessionRequestCache();
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        clearSession(request);

        SavedRequest savedRequest = requestCache.getRequest(request, response);

        /**
         * prevPage가 존재하는 경우 = 사용자가 직접 /auth/login 경로로 로그인 요청
         * 기존 Session의 prevPage attribute 제거
         */
        /*String prevPage = (String) request.getSession().getAttribute("prevPage");
        if (prevPage != null) {
            request.getSession().removeAttribute("prevPage");
        }

        // 기본 URI
        String uri = "/";

        if (prevPage != null && !prevPage.isEmpty()) {
            // 회원가입 - 로그인으로 넘어온 경우 "/"로 redirect
            if (prevPage.contains("/auth/join")) {
                uri = "/";
            } else {
                uri = prevPage;
            }
        }*/

        String prevRequestUrl = (String) request.getSession().getAttribute("currentUrl");
        if (prevRequestUrl != null) {
            request.getSession().removeAttribute("currentUrl");
        }
        String uri = "/";
        if (prevRequestUrl != null && !prevRequestUrl.isEmpty()) {
            if (prevRequestUrl.contains("/auth/join")) {
                uri = "/";
            } else {
                uri = prevRequestUrl;
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
