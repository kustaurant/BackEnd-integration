package com.kustaurant.mainapp.evaluation.evaluation.service;

import com.kustaurant.mainapp.evaluation.evaluation.domain.Evaluation;
import com.kustaurant.mainapp.evaluation.evaluation.domain.EvaluationDTO;
import com.kustaurant.mainapp.evaluation.evaluation.service.port.EvaluationCommandRepository;
import com.kustaurant.mainapp.evaluation.evaluation.service.port.EvaluationQueryRepository;
import java.util.ArrayList;
import java.util.List;

import com.kustaurant.mainapp.user.mypage.service.UserStatsService;
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

    private final UserStatsService userStatsService;

    @Transactional
    public void evaluate(Long userId, Long restaurantId, EvaluationDTO dto) {
        EvaluationDTO updated = applyImgUrlIfNewImageAdded(dto);

        if (hasUserEvaluatedRestaurant(userId, restaurantId)) {
            reEvaluate(userId, restaurantId, updated);
        } else {
            createEvaluation(userId, restaurantId, updated);
            userStatsService.incEvaluatedRestaurant(userId);
        }
    }

    private void createEvaluation(Long userId, Long restaurantId, EvaluationDTO dto) {
        Evaluation created = Evaluation.create(userId, restaurantId, dto);
        evaluationCommandRepository.create(created);

        restaurantEvaluationService.afterEvaluationCreated(
                restaurantId,
                dto.getEvaluationSituations()
        );
    }

    private void reEvaluate(Long userId, Long restaurantId, EvaluationDTO dto) {
        evaluationQueryRepository
                .findActiveByUserAndRestaurant(userId, restaurantId)
                .ifPresent(evaluation -> { // 항상 존재함.
                    List<Long> oldSituations = new ArrayList<>(evaluation.getSituationIds());
                    evaluation.reEvaluate(dto);
                    evaluationCommandRepository.reEvaluate(evaluation);
                    // 식당 평가 관련 데이터 갱신
                    restaurantEvaluationService.afterReEvaluated(
                            restaurantId,
                            oldSituations,
                            dto.getEvaluationSituations()
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

    private boolean hasUserEvaluatedRestaurant(Long userId, Long restaurantId) {
        return evaluationQueryRepository.existsByUserAndRestaurant(userId, restaurantId);
    }
}
