package com.kustaurant.kustaurant.global.config;

import com.kustaurant.kustaurant.global.auth.web.CurrentUrlInterceptor;
import com.kustaurant.kustaurant.global.interceptor.V1KillSwitchInterceptor;
import com.kustaurant.kustaurant.restaurant.query.common.argument_resolver.CuisineListArgumentResolver;
import com.kustaurant.kustaurant.restaurant.query.common.argument_resolver.LocationListArgumentResolver;
import com.kustaurant.kustaurant.restaurant.query.common.argument_resolver.SituationListArgumentResolver;
import com.kustaurant.kustaurant.global.auth.argumentResolver.AuthUserArgumentResolver;
import com.kustaurant.kustaurant.v1.common.JwtTokenArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final AuthUserArgumentResolver authUserArgumentResolver;
    private final V1KillSwitchInterceptor v1KillSwitchInterceptor;

    private final JwtTokenArgumentResolver jwtTokenArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authUserArgumentResolver);
        resolvers.add(jwtTokenArgumentResolver);
        resolvers.add(new CuisineListArgumentResolver());
        resolvers.add(new SituationListArgumentResolver());
        resolvers.add(new LocationListArgumentResolver());
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 실행 파일이 위치한 디렉토리의 절대 경로를 기준으로 postImage 폴더를 정적 리소스로 추가
        // 예: "file:./postImage/"는 현재 작업 디렉토리에 있는 postImage 폴더를 가리킵니다.
        registry.addResourceHandler("/postImage/**")
                .addResourceLocations("file:./postImage/");
    }

//    @Override
//    public void addInterceptors(InterceptorRegistry reg) {
//        reg.addInterceptor(v1KillSwitchInterceptor) // TODO : v1쪽 컨트롤러 막는코드.
//                .addPathPatterns("/api/v1/**")
//                .order(Ordered.HIGHEST_PRECEDENCE);
//        reg.addInterceptor(new CurrentUrlInterceptor()).addPathPatterns("/**");
//    }
}
