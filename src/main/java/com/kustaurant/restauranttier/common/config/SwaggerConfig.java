package com.kustaurant.restauranttier.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    private static final String API_NAME = "Kustaurant Mobile Application API";
    private static final String API_VERSION = "v1.1.1";
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
        
    **Version 1.0.8 (2024-08-05)**
    - 커뮤니티 api 구현 완료.
        - 로그인된 유저 불러오는 로직은 특정 유저(임재)로 하드코딩한 상황입니다.
        
    **Version 1.0.9 (2024-08-05)**
    - 티어표와 티어표 지도 api 파라미터가 변경되었습니다!!!!!
        - situation을 입력할 때 ONE,TWO 이런식으로 입력하던 것을 1,2로 숫자로 입력하게 수정했습니다.
    - 지도 api기능이 구현되었습니다.
        - 티어표는 현재 난관을 만나서 곧 구현하겠습니다.
    - 식당 댓글 관련 api 엔드 포인트를 "comment"에서 "comments"로 수정했습니다.

    - 식당 평가하기, 댓글 관련 api의 반환 형식을 Swagger에서 간단하게나마 보이게 했습니다.
    - 식당 상세 정보 api에서 식당 티어의 음식 종류 아이콘 이미지 url을 같이 반환합니다. (restaurantCuisineImgUrl)
        - 현재는 제대로 안 보이는 이미지도 있을 수 있습니다.
        
    **Version 1.0.10 (2024-08-09)**
    - 뽑기화면의 api의 파라미터 타입을 수정하였고 swagger 설명을 추가하였습니다.

        -cuisine, location을 티어 화면의 api 파라미터와 같이 KO, L1 이런식으로 영어 키워드로 설정하였습니다.
    
    **Version 1.1.1 (2024-08-10)**
    - 로그인이 필요한 기능은 "/api/v1/auth"로 시작합니다.
        - 로그인이 필요한 식당 API에 대해 "/api/v1" 뒤에 "/auth"를 추가하였습니다.
    - 티어표 API (/api/v1/tier) 구현 완료
        - 반환 형식에서 티어가 없는 식당의 경우 restaurantRanking을 null을 반환하게 했습니다.
    - 지도 API (/api/v1/tier/map) 구현 완료
    
    **Version 1.2.1 (2024-08-13)**
    - 식당 상세 화면 API
        - /evaluation GET, POST 두 개의 api 모두 evaluationSituations를 문자열 리스트에서 정수형 리스트로 변환했습니다.
        - 즐겨찾기 토글 기능 구현
        - 이전 평가 데이터 불러오기 구현
        
    **Version 1.2.2 (2024-08-13)**
    - 뽑기 화면 API
        - 조건에 맞는 식당이 없을 때 404 에러 메시지 반환
        - no_img일때 대체이미지로 변환해서 반환
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
