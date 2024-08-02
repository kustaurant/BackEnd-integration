package com.kustaurant.restauranttier.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 실행 파일이 위치한 디렉토리의 절대 경로를 기준으로 postImage 폴더를 정적 리소스로 추가
        // 예: "file:./postImage/"는 현재 작업 디렉토리에 있는 postImage 폴더를 가리킵니다.
        registry.addResourceHandler("/postImage/**")
                .addResourceLocations("file:./postImage/");
    }
}
