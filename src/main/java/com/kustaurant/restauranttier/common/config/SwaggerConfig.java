package com.kustaurant.restauranttier.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    private static final String API_NAME = "Kustaurant Mobile Application API";
    private static final String API_VERSION = "v1.3.1";
    private static final String SECURITY_SCHEME_NAME = "Authorization";
    private static final String API_DESCRIPTION = """
    
    쿠스토랑 모바일 앱 API 문서입니다.

    [Swagger 패치노트](/swagger-update)
    """;


    @Bean
    public OpenAPI OpenAPIConfig() {
        return new OpenAPI()
                .info(new Info()
                        .title(API_NAME)
                        .description(API_DESCRIPTION)
                        .version(API_VERSION))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .bearerFormat("JWT")
                                .scheme("bearer")));
    }
}
