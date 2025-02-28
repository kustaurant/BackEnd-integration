package com.kustaurant.restauranttier.common.webUser;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CustomRedirectionFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (!path.startsWith("/api/v1") && !path.startsWith("/swagger")) {
            // 아래 if문은 요청이 들어올 때 요청이 여러개가 와서 redirect가 많이되는 오류가 발생해서 추가함.
            if (path.equals("/") || path.startsWith("/login") || path.startsWith("/tier") || path.startsWith("/restaurants") || path.startsWith("community") || path.startsWith("ranking") || path.contains("admin")) {
                response.sendRedirect("/temp");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
