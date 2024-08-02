package com.kustaurant.restauranttier.common.etc;

import com.kustaurant.restauranttier.tab3_tier.service.EvaluationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MyStartupRunner implements CommandLineRunner { // 서버 시작하고 모든 주입이 완료되고 작동실행되는 함수

    private final EvaluationService evaluationService;

    public MyStartupRunner(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Spring Boot 애플리케이션이 시작될 때 실행될 코드를 여기에 작성합니다.
        evaluationService.calculateAllTier();
    }
}