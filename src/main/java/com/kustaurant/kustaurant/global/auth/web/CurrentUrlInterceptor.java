package com.kustaurant.kustaurant.global.auth.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class CurrentUrlInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        if (!"GET".equalsIgnoreCase(req.getMethod())) return true;

        String uri = req.getRequestURI();
        if (uri.startsWith("/user/") || uri.startsWith("/api/")
                || uri.startsWith("/evaluation/") || uri.startsWith("/web/api/")
                || uri.startsWith("/css/") || uri.startsWith("/js/")
                || uri.startsWith("/img/") || uri.startsWith("/webjars/")
                || uri.equals("/favicon.ico") || uri.startsWith("/error")) {
            return true;
        }

        String qs = req.getQueryString();
        String full = req.getRequestURL().toString() + (qs != null ? "?" + qs : "");
        req.getSession(true).setAttribute("currentUrl", full);
        return true;
    }
}
