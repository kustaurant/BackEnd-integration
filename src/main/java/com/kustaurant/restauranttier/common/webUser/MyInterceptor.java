package com.kustaurant.restauranttier.common.webUser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class MyInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String requestURL = request.getRequestURL().toString();


        // API 요청 여부를 판단합니다.
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        // 상대적인 URL 생성
        String relativeUrl = requestURI.substring(contextPath.length());

        if (relativeUrl.startsWith("/home")
                || relativeUrl.equals("/")
                || relativeUrl.startsWith("/recommend")
                || relativeUrl.startsWith("/tier")
                || relativeUrl.startsWith("/community")
                || relativeUrl.startsWith("/user/myPage")
                || relativeUrl.startsWith("/ranking")
                || relativeUrl.startsWith("/restaurant")
                || relativeUrl.startsWith("/search")){
            request.getSession().setAttribute("currentUrl", requestURL);
        }

        return true; // true를 반환하면 요청 처리를 계속하고, false를 반환하면 요청 처리가 중단됩니다.
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // 이 곳에서 API 호출 후 실행할 코드를 작성할 수 있습니다.
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {
        // 이 곳에서 API 호출 완료 후 실행할 코드를 작성할 수 있습니다.
    }
}
