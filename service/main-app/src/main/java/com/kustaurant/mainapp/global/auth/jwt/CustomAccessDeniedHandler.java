package com.kustaurant.mainapp.global.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kustaurant.mainapp.global.exception.ApiErrorResponse;
import com.kustaurant.mainapp.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res,
                       AccessDeniedException ex) throws IOException {

        ErrorCode ec = ErrorCode.ACCESS_DENIED;

        res.setStatus(ec.getStatus().value());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        om.writeValue(res.getWriter(), ApiErrorResponse.of(ec));
    }
}
