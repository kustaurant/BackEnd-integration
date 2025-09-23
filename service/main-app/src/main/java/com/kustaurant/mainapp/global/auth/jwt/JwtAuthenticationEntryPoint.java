package com.kustaurant.mainapp.global.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustaurant.mainapp.global.exception.ApiErrorResponse;
import com.kustaurant.mainapp.global.exception.ErrorCode;
import com.kustaurant.mainapp.global.exception.exception.auth.JwtAuthException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest req,
                         HttpServletResponse res,
                         AuthenticationException authEx) throws IOException {

        ErrorCode ec = (authEx instanceof JwtAuthException ae)
                ? ae.getErrorCode()
                : ErrorCode.UNAUTHORIZED;

        res.setStatus(ec.getStatus().value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        om.writeValue(res.getWriter(), ApiErrorResponse.of(ec));
    }
}
