package com.kustaurant.restauranttier.tab3_tier.argument_resolver;

import com.kustaurant.restauranttier.common.exception.exception.ParamException;
import com.kustaurant.restauranttier.tab3_tier.etc.CuisineEnum;
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

        String cuisines = webRequest.getParameter("cuisines");
        if (cuisines == null || cuisines.isEmpty()) {
            return null;
        }

        if (cuisines.contains("ALL") && cuisines.contains(("JH"))) {
            throw new ParamException("cuisines 파라미터 값에 ALL와 JH가 둘 다 있습니다.");
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
            throw new ParamException("cuisines 파라미터 입력이 올바르지 않습니다.");
        }
    }
}
