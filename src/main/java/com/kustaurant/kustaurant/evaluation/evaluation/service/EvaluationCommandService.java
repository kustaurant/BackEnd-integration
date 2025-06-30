package com.kustaurant.kustaurant.evaluation.evaluation.service;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.kustaurant.evaluation.evaluation.domain.EvaluationDTO;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationCommandRepository;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationQueryRepository;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.dto.EvaluationSaveCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EvaluationCommandService {

    private final EvaluationCommandRepository evaluationCommandRepository;
    private final EvaluationQueryRepository evaluationQueryRepository;

    private final S3Service s3Service;
    private final SituationCommandService situationCommandService;

    @Transactional
    public void evaluate(Long userId, Integer restaurantId, EvaluationDTO evaluationDTO) {
        boolean evaluatedBefore = evaluationQueryRepository.existsByUserAndRestaurant(userId, restaurantId);
        if (evaluatedBefore) {
            evaluationUpdate(userId, restaurantId, evaluationDTO);
        } else {
            evaluationCreate(userId, restaurantId, evaluationDTO);
        }
    }

    /**
     * 평가 새로 생성
     */
    private void evaluationCreate(Long userId, Integer restaurantId, EvaluationDTO evaluationDTO) {
        // 평가 생성
        if (hasNewImage(evaluationDTO)) {
            String imgUrl = s3Service.uploadFile(evaluationDTO.getNewImage());
            evaluationDTO.changeImgUrl(imgUrl);
        }
        Evaluation evaluation = evaluationCommandRepository.addEvaluation(
                EvaluationSaveCondition.from(userId, restaurantId, evaluationDTO));
        // 상황 생성
        situationCommandService.addSituations(restaurantId, evaluation.getId(), evaluationDTO.getEvaluationSituations());
    }

    /**
     * 기존 평가 수정
     */
    private void evaluationUpdate(Long userId, Integer restaurantId, EvaluationDTO evaluationDTO) {

    }

    private boolean hasNewImage(EvaluationDTO evaluationDTO) {
        return evaluationDTO.getNewImage() != null && !evaluationDTO.getNewImage().isEmpty();
    }
}
