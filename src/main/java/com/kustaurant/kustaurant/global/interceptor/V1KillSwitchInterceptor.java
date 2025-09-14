package com.kustaurant.kustaurant.global.interceptor;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class V1KillSwitchInterceptor implements HandlerInterceptor {
    private static final String MESSAGE = "현재 앱 업데이트 중입니다. 웹 서비스는 정상적으로 이용 가능합니다. 빠른 시일 내에 찾아뵙겠습니다.";

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws IOException {
        String path = req.getRequestURI();
        if(!path.startsWith("/api/v1")) return true;

        res.setStatus(503);
        res.setContentType("application/json;charset=UTF-8");

        String body = """
                {"code":"MAINTENANCE","message":"%s"}
                """.formatted(MESSAGE);

        res.getWriter().write(body);
        res.getWriter().flush();
        return false;
    }

}
