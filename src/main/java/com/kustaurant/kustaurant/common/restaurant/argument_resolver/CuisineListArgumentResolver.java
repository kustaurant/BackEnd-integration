package com.kustaurant.kustaurant.common.restaurant.argument_resolver;

import com.kustaurant.kustaurant.common.restaurant.domain.enums.CuisineEnum;
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

public class CuisineListArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CuisineList.class) != null && parameter.getParameterType().equals(List.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        boolean isApiRequest = isApiRequest(webRequest);

        String cuisines = webRequest.getParameter("cuisines");
        if (cuisines == null || cuisines.isEmpty()) {
            return null;
        }

        if (cuisines.contains("ALL") && cuisines.contains(("JH"))) {
            if (isApiRequest) {
                // API 요청의 경우 예외를 던집니다.
                throw new ParamException("cuisines 파라미터 값에 ALL와 JH가 둘 다 있습니다.");
            } else {
                // 웹 요청의 경우 클라이언트에 리다이렉트를 지시합니다.
                HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
                if (response != null) {
                    response.sendRedirect("/tier"); // 리다이렉트할 URL
                }
                return null;
            }
        }

        try {
            // 파라미터가 "ALL"이면 null 반환
            if (cuisines.contains("ALL")) {
                return null;
            }

            // 문자열을 List<String>으로 변환
            List<String> cuisineList = Arrays.stream(cuisines.split(","))
                    .map(c -> CuisineEnum.valueOf(c.trim()).getValue())
                    .collect(Collectors.toList());

            if (cuisineList.contains("제휴업체")) {
                cuisineList = List.of("JH");
            }

            return cuisineList;

        } catch (IllegalArgumentException e) {
            if (isApiRequest) {
                // API 요청의 경우 예외를 던집니다.
                throw new ParamException("cuisines 파라미터 값이 올바르지 않습니다.");
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
