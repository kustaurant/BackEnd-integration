package com.kustaurant.kustaurant.restaurant.presentation.argument_resolver;

import com.kustaurant.kustaurant.global.exception.exception.ParamException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SituationListArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(SituationList.class) != null && parameter.getParameterType().equals(List.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        boolean isApiRequest = isApiRequest(webRequest);

        String situations = webRequest.getParameter("situations");
        if (situations == null || situations.isEmpty()) {
            return null;
        }

        try {
            // 파라미터가 "ALL"이면 null 반환
            if (situations.contains("ALL")) {
                return null;
            }

            // 문자열을 List<String>으로 변환
            return Arrays.stream(situations.split(","))
                    .map(c -> Integer.parseInt(c.trim()))
                    .collect(Collectors.toList());

        } catch (IllegalArgumentException e) {
            if (isApiRequest) {
                // API 요청의 경우 예외를 던집니다.
                throw new ParamException("situations 파라미터 입력이 올바르지 않습니다.");
            } else {
                // 웹 요청의 경우 클라이언트에 리다이렉트를 지시합니다.
                HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
                if (response != null) {
                    response.sendRedirect("/tier"); // 리다이렉트할 URL
                }
                return null;
            }
        }
    }

    private boolean isApiRequest(NativeWebRequest webRequest) {
        String acceptHeader = webRequest.getHeader("Accept");
        String contentTypeHeader = webRequest.getHeader("Content-Type");

        // JSON 요청일 경우 API 요청으로 간주합니다.
        return (acceptHeader != null && acceptHeader.contains("application/json")) ||
                (contentTypeHeader != null && contentTypeHeader.contains("application/json"));
    }
}
