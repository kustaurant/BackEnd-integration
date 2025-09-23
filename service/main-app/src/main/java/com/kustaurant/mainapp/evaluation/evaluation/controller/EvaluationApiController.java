package com.kustaurant.mainapp.evaluation.evaluation.controller;

import com.kustaurant.mainapp.evaluation.evaluation.domain.EvaluationDTO;
import com.kustaurant.mainapp.evaluation.evaluation.service.EvaluationCommandService;
import com.kustaurant.mainapp.evaluation.evaluation.service.EvaluationQueryService;
import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUser;
import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUserInfo;
import com.kustaurant.mainapp.global.exception.exception.ParamException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class EvaluationApiController implements EvaluationApiDoc {
    private final EvaluationQueryService evaluationQueryService;
    private final EvaluationCommandService evaluationCommandService;

    // 1. 이전 평가 데이터 가져오기
    @GetMapping("/v2/auth/restaurants/{restaurantId}/evaluation")
    public ResponseEntity<EvaluationDTO> getPreEvaluationInfo(
            @PathVariable Long restaurantId,
            @AuthUser AuthUserInfo user
    ) {
        EvaluationDTO dto = evaluationQueryService.getPreEvaluation(user.id(), restaurantId);

        return ResponseEntity.ok(dto);
    }

    // 2. 평가하기
    @PostMapping(value = "/v2/auth/restaurants/{restaurantId}/evaluation")
    public ResponseEntity<Void> evaluateRestaurant(
            @PathVariable Long restaurantId,
            EvaluationDTO evaluationDTO,
            @AuthUser AuthUserInfo user
    ) {
        // 필수 파라미터 체크
        if (evaluationDTO.getEvaluationScore() == null || evaluationDTO.getEvaluationScore().equals(0d)) {
            throw new ParamException("평가 점수가 필요합니다.");
        }

        // 평가 데이터 저장 또는 업데이트
        evaluationCommandService.evaluate(user.id(), restaurantId, evaluationDTO);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
