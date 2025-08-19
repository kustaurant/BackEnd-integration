package com.kustaurant.kustaurant.evaluation.evaluation.service;

import com.kustaurant.kustaurant.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.kustaurant.evaluation.evaluation.domain.EvaluationDTO;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationCommandRepository;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationQueryRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EvaluationCommandService {

    private final EvaluationCommandRepository evaluationCommandRepository;
    private final EvaluationQueryRepository evaluationQueryRepository;

    private final EvaluationRestaurantService restaurantEvaluationService;
    private final EvalS3Service s3Service;

    @Transactional
    public void evaluate(Long userId, Integer restaurantId, EvaluationDTO dto) {
        EvaluationDTO updated = applyImgUrlIfNewImageAdded(dto);

        if (hasUserEvaluatedRestaurant(userId, restaurantId)) {
            reEvaluate(userId, restaurantId, updated);
        } else {
            createEvaluation(userId, restaurantId, updated);
        }
    }

    /**
     * 평가 새로 생성
     */
    private void createEvaluation(Long userId, Integer restaurantId, EvaluationDTO dto) {
        // 평가 생성
        Evaluation created = Evaluation.create(userId, restaurantId, dto);

        // 저장
        evaluationCommandRepository.create(created);

        // 식당 평가 관련 데이터 갱신
        restaurantEvaluationService.afterEvaluationCreated(
                restaurantId,
                dto.getEvaluationSituations(),
                dto.getEvaluationScore()
        );
    }

    /**
     * 재평가
     */
    private void reEvaluate(Long userId, Integer restaurantId, EvaluationDTO dto) {
        evaluationQueryRepository
                .findActiveByUserAndRestaurant(userId, restaurantId)
                .ifPresent(evaluation -> { // 항상 존재함.
                    double oldScore = evaluation.getEvaluationScore();
                    List<Long> oldSituations = new ArrayList<>(evaluation.getSituationIds());

                    // 재평가
                    evaluation.reEvaluate(dto);

                    // 업데이트
                    evaluationCommandRepository.reEvaluate(evaluation);

                    // 식당 평가 관련 데이터 갱신
                    restaurantEvaluationService.afterReEvaluated(
                            restaurantId,
                            oldSituations,
                            dto.getEvaluationSituations(),
                            oldScore,
                            dto.getEvaluationScore()
                    );
                });
    }

    private EvaluationDTO applyImgUrlIfNewImageAdded(EvaluationDTO evaluationDTO) {
        if (hasNewImage(evaluationDTO)) {
            String imgUrl = s3Service.uploadFile(evaluationDTO.getNewImage());
            return evaluationDTO.withEvaluationImgUrl(imgUrl);
        }
        return evaluationDTO;
    }

    private boolean hasNewImage(EvaluationDTO evaluationDTO) {
        return evaluationDTO.getNewImage() != null && !evaluationDTO.getNewImage().isEmpty();
    }

    private boolean hasUserEvaluatedRestaurant(Long userId, Integer restaurantId) {
        return evaluationQueryRepository.existsByUserAndRestaurant(userId, restaurantId);
    }
}
