package com.kustaurant.restauranttier.tab3_tier.argument_resolver;

import com.kustaurant.restauranttier.common.exception.exception.ParamException;
import com.kustaurant.restauranttier.tab3_tier.etc.CuisineEnum;
import com.kustaurant.restauranttier.tab3_tier.etc.SituationEnum;
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
            throw new ParamException("situations 파라미터 입력이 올바르지 않습니다.");
        }
    }
}
