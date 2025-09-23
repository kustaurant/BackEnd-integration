package com.kustaurant.mainapp.global.auth.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public CustomLoginSuccessHandler() {
        setDefaultTargetUrl("/");
        setAlwaysUseDefaultTargetUrl(false);
        setTargetUrlParameter("redirect");
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication
    ) throws IOException, ServletException {

        var cache = new HttpSessionRequestCache();
        SavedRequest saved = cache.getRequest(request, response);

        if (saved != null && saved.getRedirectUrl() != null && !saved.getRedirectUrl().contains("/user/login")) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        // 없으면 바로 직전 페이지(인터셉터가 저장)로
        HttpSession session = request.getSession(false);
        String current = session == null ? null : (String) session.getAttribute("currentUrl");
        if (current != null && !current.contains("/user/login")) {
            session.removeAttribute("currentUrl");
            getRedirectStrategy().sendRedirect(request, response, current);
            return;
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}

