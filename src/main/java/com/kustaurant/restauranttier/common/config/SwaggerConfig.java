package com.kustaurant.restauranttier.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    private static final String API_NAME = "Kustaurant Mobile Application API";
    private static final String API_VERSION = "v1.0.6";
    private static final String API_DESCRIPTION = """
    쿠스토랑 모바일 앱 API 문서입니다.

    **Version 1.0.1 (2024-07-20)**
    - 티어표 지도 api 추가

    **Version 1.0.2 (2024-07-21)**
    - 커뮤니티 api 추가
    - 홈 화면 식당 반환에 식당 평점 추가
    
    **Version 1.0.3 (2024-07-25)**
    - 티어표 지도 API에 즐겨찾기 식당 리스트 추가
    
    **Version 1.0.4 (2024-07-29)**
    - 커뮤니티 api 전반적인 구현 수정
    
    **Version 1.0.5 (2024-07-31)**
    - 티어표 api 수정
        - 티어표 리스트를 페이징으로 불러오게 함. (ranking 파라미터를 page로 바꿈)
        - 리스트 끝에서 500 오류 내던 것을 빈 리스트를 반환하도록 수정함.
        - 데이터 형식 통일을 위해 티어표 식당 데이터에 restaurantScore 필드가 추가됨.
    
    **Version 1.0.6 (2024-08-01)**
    - 식당 상세 화면 api 추가
        - 기능은 아직 정상 동작하지 않습니다.
    
    **Version 1.0.7 (2024-08-05)**
    - 홈, 뽑기 화면에서 반환하는 식당 데이터를 티어 화면의 반환 양식로 통일.
        - 기존 반환 데이터는 유지하면서 몇개의 필드가 추가되었습니다.
    """;


    @Bean
    public OpenAPI OpenAPIConfig() {
        return new OpenAPI()
                .info(new Info()
                        .title(API_NAME)
                        .description(API_DESCRIPTION)
                        .version(API_VERSION));
    }
}
