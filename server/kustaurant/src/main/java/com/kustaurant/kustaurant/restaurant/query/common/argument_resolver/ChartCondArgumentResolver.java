package com.kustaurant.kustaurant.restaurant.query.common.argument_resolver;

import static com.kustaurant.kustaurant.restaurant.query.chart.controller.RestaurantChartController.TIER_PAGE_SIZE;

import com.kustaurant.kustaurant.global.exception.exception.ParamException;
import com.kustaurant.kustaurant.restaurant.query.common.dto.ChartCondition;
import com.kustaurant.kustaurant.restaurant.query.common.dto.ChartCondition.TierFilter;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Cuisine;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Position;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class ChartCondArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(ChartCond.class) != null
                && parameter.getParameterType().equals(ChartCondition.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {

        Pageable pageable = parsePageable(webRequest);
        List<Long> situations = parseSituations(webRequest);
        List<String> cuisines = parseCuisines(webRequest);
        List<String> locations = parseLocations(webRequest);

        return new ChartCondition(cuisines, situations, locations, TierFilter.ALL, pageable);
    }

    private Pageable parsePageable(NativeWebRequest webRequest) {
        String pageParam = webRequest.getParameter("page");
        String limitParam = webRequest.getParameter("limit");

        // 기본값 설정 및 변환
        int page = pageParam != null ? Integer.parseInt(pageParam) : 0;
        int limit = limitParam != null ? Integer.parseInt(limitParam) : TIER_PAGE_SIZE;
        page--;
        if (page < 0) {
            page = 0;
        }
        return PageRequest.of(page, limit);
    }

    private List<Long> parseSituations(NativeWebRequest webRequest) throws IOException {
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
                    .map(c -> Long.parseLong(c.trim()))
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

    private List<String> parseLocations(NativeWebRequest webRequest) throws IOException {
        boolean isApiRequest = isApiRequest(webRequest);

        String locations = webRequest.getParameter("locations");
        if (locations == null || locations.isEmpty()) {
            return null;
        }

        try {
            // 파라미터가 "ALL"이면 null 반환
            if (locations.contains("ALL")) {
                return null;
            }

            // 문자열을 List<String>으로 변환
            return Arrays.stream(locations.split(","))
                    .map(c -> Position.valueOf(c.trim()).getValue())
                    .collect(Collectors.toList());

        } catch (IllegalArgumentException e) {
            if (isApiRequest) {
                // API 요청의 경우 예외를 던집니다.
                throw new ParamException("locations 파라미터 입력이 올바르지 않습니다.");
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

    private List<String> parseCuisines(NativeWebRequest webRequest) throws IOException {
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
                    .map(c -> Cuisine.valueOf(c.trim()).getValue())
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
