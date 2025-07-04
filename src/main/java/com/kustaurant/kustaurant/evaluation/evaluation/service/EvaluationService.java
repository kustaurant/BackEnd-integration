package com.kustaurant.kustaurant.evaluation.evaluation.service;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.RestaurantCommentDislikeEntity;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.EvalCommentEntity;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.entity.EvalCommentLikeEntity;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.repo.RestaurantCommentDislikeRepository;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.repo.EvalCommentLikeRepository;
import com.kustaurant.kustaurant.evaluation.comment.infrastructure.repo.EvalCommentRepository;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.*;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.query.EvaluationQueryRepository;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import com.kustaurant.kustaurant.evaluation.evaluation.service.port.EvaluationRepository;
import com.kustaurant.kustaurant.restaurant.tier.RestaurantTierDataClass;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.situation.RestaurantSituationRelationEntity;
import com.kustaurant.kustaurant.evaluation.evaluation.infrastructure.situation.SituationRepository;
import com.kustaurant.kustaurant.restaurant.restaurant.service.S3Service;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantRepository;
import com.kustaurant.kustaurant.global.exception.exception.ParamException;
import com.kustaurant.kustaurant.evaluation.evaluation.constants.EvaluationConstants;
import com.kustaurant.kustaurant.evaluation.evaluation.domain.EvaluationDTO;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.spec.RestaurantChartSpec;
import com.kustaurant.kustaurant.global.etc.JsonData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class EvaluationService {
    private final EvaluationRepository evaluationRepository;
    private final SituationRepository situationRepository;
    private final EvaluationSituationRepository evaluationSituationRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantSituationRelationService restaurantSituationRelationService;
    private final EvaluationItemScoresService evaluationItemScoresService;
    private final S3Service s3Service;
    private final EvalCommentLikeRepository restaurantCommentLikeRepository;
    private final RestaurantCommentDislikeRepository restaurantCommentDislikeRepository;
    private final EvalCommentRepository restaurantCommentRepository;
    private final EvaluationConstants evaluationConstants;

    private final EvaluationQueryRepository evaluationQueryRepository;

    // new code start
    public Boolean isUserEvaluated(Long userId, Integer restaurantId) {
        return evaluationRepository.existsByUserAndRestaurant(userId, restaurantId);
    }

    public List<EvaluationEntity> findByRestaurantId(Integer restaurantId) {
        return evaluationRepository.findByRestaurantIdAndStatus(restaurantId, "ACTIVE");
    }

    @Transactional(readOnly = true)
    public EvaluationDTO getPreEvaluation(Long userId, Integer restaurantId) {
        return evaluationQueryRepository.findActiveByUserAndRestaurant(userId, restaurantId)
                .map(EvaluationDTO::convertEvaluation)
                .orElse(EvaluationDTO.convertEvaluationWhenNoEvaluation());
    }
    // new code end

    public EvaluationEntity getByEvaluationId(Integer evaluationId) {
        Optional<EvaluationEntity> evaluationOptional = evaluationRepository.findByEvaluationIdAndStatus(evaluationId, "ACTIVE");
        if (evaluationOptional.isEmpty()) {
            throw new DataNotFoundException(EVALUATION_NOT_FOUND, evaluationId, "평가");
        }
        return evaluationOptional.get();
    }

    public EvaluationEntity getByUserAndRestaurant(Long userId, RestaurantEntity restaurant) {
        Optional<EvaluationEntity> evaluation = evaluationRepository.findByUserAndRestaurant(userId, restaurant);
        return evaluation.orElse(null);
    }

    public void createOrUpdate(JsonData jsonData, Principal principal) {
        /*Restaurant restaurant = restaurantService.getRestaurant(jsonData.getRestaurantId());
        User user = customOAuth2UserService.getUser(principal.getName());

        // user와 restaurant 정보로 db에 평가한 데이터가 있는지 확인
        Optional<Evaluation> evaluationOptional = evaluationRepository.findByUserAndRestaurant(user, restaurant);
        Evaluation evaluation;
        // 있으면 업데이트 및 삭제
        if (evaluationOptional.isPresent()) {
            evaluation = evaluationOptional.get();
            // updated at
            evaluation.setUpdatedAt(LocalDateTime.now());
            // restaurant tbl update
            double preveMainScore = evaluation.getEvaluationScore();
            restaurant.setRestaurantScoreSum(restaurant.getRestaurantScoreSum() - preveMainScore + jsonData.getStarRating());
            // 메인 점수 업데이트
            evaluation.setEvaluationScore(jsonData.getStarRating());
            // 상황 점수 삭제
            List<EvaluationItemScore> evaluationItemScoreList = evaluation.getEvaluationItemScoreList();
            for (EvaluationItemScore itemScore : evaluationItemScoreList) {
                double prevScore = itemScore.getScore();

                Situation situation = itemScore.getSituation();
                situation.getEvaluationItemScoreList().remove(itemScore);
                situationRepository.save(situation);
                evaluationItemScoreRepository.delete(itemScore);

                // restaurant situation relation tbl update
                List<RestaurantSituationRelation> restaurantSituationRelationList = restaurant.getRestaurantSituationRelationList();
                for (RestaurantSituationRelation restaurantSituationRelation: restaurantSituationRelationList) {
                    Situation relationSituation = restaurantSituationRelation.getSituation();
                    if (relationSituation == situation) {
                        restaurantSituationRelation.setDataCount(restaurantSituationRelation.getDataCount() - 1);
                        restaurantSituationRelation.setScoreSum(restaurantSituationRelation.getScoreSum() - prevScore);
                        break;
                    }
                }
            }
            evaluation.getEvaluationItemScoreList().clear();
        }
        // 없으면 새로 평가 데이터 생성
        else {
            evaluation = new Evaluation(restaurant, user, jsonData.getStarRating());
            // restaurant tbl first update
            restaurant.setRestaurantEvaluationCount(restaurant.getRestaurantEvaluationCount() + 1);
            restaurant.setRestaurantScoreSum(restaurant.getRestaurantScoreSum() + jsonData.getStarRating());
        }
        restaurantRepository.save(restaurant);
        evaluationRepository.save(evaluation);

        // 새로운 EvaluationItemScore 생성 및 저장
        List<EvaluationItemScore> evaluationItemScoreList = new ArrayList<>();
        List situationScoreList = jsonData.getBarRatings();
        for (int i = 0; i < situationScoreList.size(); i++) {
            if (situationScoreList.get(i) == null) {
                continue;
            }

            Situation situation = situationService.getSituation(i + 1); // 1인덱싱이라 +1

            EvaluationItemScore evaluationItemScore = new EvaluationItemScore(evaluation, situation, (Double) situationScoreList.get(i));
            evaluationItemScoreRepository.save(evaluationItemScore);

            // EvaluationItemScore -> evaluationItemScore, situation 과 일대다 매핑
            evaluationItemScoreList.add(evaluationItemScore);
            situation.getEvaluationItemScoreList().add(evaluationItemScore);

            // restaurant situation relation tbl insert
            List<RestaurantSituationRelation> restaurantSituationRelationList = restaurant.getRestaurantSituationRelationList();
            boolean isExist = false;
            double newScore = (Double) situationScoreList.get(i);
            for (RestaurantSituationRelation restaurantSituationRelation: restaurantSituationRelationList) {
                Situation relationSituation = restaurantSituationRelation.getSituation();
                if (relationSituation == situation) {
                    restaurantSituationRelation.setDataCount(restaurantSituationRelation.getDataCount() + 1);
                    restaurantSituationRelation.setScoreSum(restaurantSituationRelation.getScoreSum() + newScore);
                    restaurantSituationRelationRepository.save(restaurantSituationRelation);
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                RestaurantSituationRelation restaurantSituationRelation = new RestaurantSituationRelation();
                restaurantSituationRelation.setRestaurant(restaurant);
                restaurantSituationRelation.setSituation(situation);
                restaurantSituationRelation.setDataCount(1);
                restaurantSituationRelation.setScoreSum((Double) situationScoreList.get(i));
                restaurantSituationRelationRepository.save(restaurantSituationRelation);
            }
        }

        evaluation.setEvaluationItemScoreList(evaluationItemScoreList);
        evaluationRepository.save(evaluation);

        // 메인(cuisine) 티어 저장
        if (restaurant.getRestaurantEvaluationCount() >= minNumberOfEvaluations) {
            EnumTier tier = EnumTier.calculateTierOfRestaurant(restaurant.getRestaurantScoreSum() / restaurant.getRestaurantEvaluationCount());
            restaurant.setMainTier(tier.getValue());
        } else {
            restaurant.setMainTier(-1);
        }
        restaurantRepository.save(restaurant);

        // 상황(situation) 티어 저장
        for (RestaurantSituationRelation restaurantSituationRelation : restaurant.getRestaurantSituationRelationList()) {
            if (restaurantSituationRelation.getDataCount() >= minNumberOfEvaluations) {
                Double AvgScore = restaurantSituationRelation.getScoreSum() / restaurantSituationRelation.getDataCount();
                restaurantSituationRelation.setSituationTier(EnumTier.calculateSituationTierOfRestaurant(AvgScore).getValue());
            } else {
                restaurantSituationRelation.setSituationTier(-1);
            }
            restaurantSituationRelationRepository.save(restaurantSituationRelation);
        }*/
    }

    public Double getEvaluationScoreByRestaurantComment(EvalCommentEntity restaurantComment) {
        if (restaurantComment == null) {
            return null;
        }
        EvaluationEntity evaluation = restaurantComment.getEvaluation();
        return evaluation.getEvaluationScore();
    }

    public void createOrUpdate(Long userId, RestaurantEntity restaurant, EvaluationDTO evaluationDTO) {
        // 이전 평가 가져오기
        EvaluationEntity evaluation = evaluationRepository.findByUserAndRestaurantAndStatus(userId, restaurant, "ACTIVE").orElse(null);
        if (evaluation == null) { // 이전 평가가 없을 경우
            evaluationCreate(userId, restaurant, evaluationDTO);
        } else { // 이전 평가가 있을 경우
            evaluationUpdate(userId, restaurant, evaluation, evaluationDTO);
        }
    }

    // 이전 평가가 있는 경우 - 평가 업데이트하기
    @Transactional
    public void evaluationUpdate(Long userId, RestaurantEntity restaurant, EvaluationEntity evaluation, EvaluationDTO evaluationDTO) {
        // restaurants_tbl 테이블 점수 업데이트
        restaurant.setRestaurantScoreSum(restaurant.getRestaurantScoreSum() - evaluation.getEvaluationScore() + evaluationDTO.getEvaluationScore());
        restaurantRepository.save(restaurant);
        calculateTierOfOneRestaurant(restaurant);
        // 평가 업데이트
        evaluation.setUpdatedAt(LocalDateTime.now());
        evaluation.setEvaluationScore(evaluationDTO.getEvaluationScore());
        String commentBody = evaluationDTO.getEvaluationComment();
        if (commentBody == null || commentBody.isEmpty()) {
            commentBody = null;
        }
        evaluation.setCommentBody(commentBody);
        if (evaluationDTO.getNewImage() != null && !evaluationDTO.getNewImage().isEmpty()) {
            evaluation.setCommentImgUrl(s3Service.uploadFile(evaluationDTO.getNewImage()));
        }
        evaluationRepository.save(evaluation);
        // Evaluation Situation Item Table & Restaurant Situation Relation Table 반영
        // 이전 상황 데이터 삭제 & 이전에 선택한 상황에 대해 restaurant_situation_relations_tbl 테이블의 count 1씩 감소
        for (EvaluationSituationEntity evaluationSituationEntity : evaluation.getEvaluationSituationEntityList()) {
            restaurantSituationRelationService.updateOrCreate(restaurant, evaluationSituationEntity.getSituation(), -1);
        }
        evaluationItemScoresService.deleteSituationsByEvaluation(evaluation);
        // 새로 추가
        if (evaluationDTO.getEvaluationSituations() != null && !evaluationDTO.getEvaluationSituations().isEmpty()) {
            for (Integer evaluationSituation : evaluationDTO.getEvaluationSituations()) {
                // Evaluation Situation Item Table
                situationRepository.findBySituationId(evaluationSituation).ifPresent(newSituation -> {
                    evaluationSituationRepository.save(new EvaluationSituationEntity(evaluation, newSituation));
                });
                // Restaurant Situation Relation Table
                situationRepository.findBySituationId(evaluationSituation).ifPresent(newSituation ->
                        restaurantSituationRelationService.updateOrCreate(restaurant, newSituation, 1));
            }
        }
    }

    // 이전 평가가 없는 경우 - 평가 생성하기
    @Transactional
    public void evaluationCreate(Long userId, RestaurantEntity restaurant, EvaluationDTO evaluationDTO) {
        // 평가 저장
        String commentImgUrl = null;
        if (evaluationDTO.getNewImage() != null && !evaluationDTO.getNewImage().isEmpty()) {
            commentImgUrl = s3Service.uploadFile(evaluationDTO.getNewImage());
        }
        String commentBody = evaluationDTO.getEvaluationComment();
        if (commentBody == null || commentBody.isEmpty()) {
            commentBody = null;
        }
        EvaluationEntity evaluation = new EvaluationEntity(
                evaluationDTO.getEvaluationScore(),
                "ACTIVE",
                LocalDateTime.now(),
                commentBody,
                commentImgUrl,
                userId,
                restaurant
        );
        evaluationRepository.save(evaluation);
        // restaurants_tbl 테이블 점수 업데이트
        restaurant.setRestaurantScoreSum(restaurant.getRestaurantScoreSum() + evaluationDTO.getEvaluationScore());
        restaurant.setRestaurantEvaluationCount(restaurant.getRestaurantEvaluationCount() + 1);
        restaurantRepository.save(restaurant);
        calculateTierOfOneRestaurant(restaurant);
        // Evaluation Situation Item Table & Restaurant Situation Relation Table 반영
        if (evaluationDTO.getEvaluationSituations() != null && !evaluationDTO.getEvaluationSituations().isEmpty()) {
            for (Integer evaluationSituation : evaluationDTO.getEvaluationSituations()) {
                // Evaluation Situation Item Table
                situationRepository.findBySituationId(evaluationSituation).ifPresent(newSituation ->
                        evaluationSituationRepository.save(new EvaluationSituationEntity(evaluation, newSituation)));
                // Restaurant Situation Relation Table
                situationRepository.findBySituationId(evaluationSituation).ifPresent(newSituation ->
                        restaurantSituationRelationService.updateOrCreate(restaurant, newSituation, 1));
            }
        }
    }

    // 식당의 티어를 다시 계산함
    @Transactional
    public void calculateTierOfOneRestaurant(RestaurantEntity restaurant) {
        if (evaluationConstants.isHasTier(restaurant)) {
            restaurant.setMainTier(
                    EvaluationConstants.calculateRestaurantTier(restaurant.getRestaurantScoreSum() / restaurant.getRestaurantEvaluationCount())
            );
            restaurantRepository.save(restaurant);
        }
    }

    // 모든 평가 기록에 대해서 티어를 다시 계산함.
    @Transactional
    public void calculateAllTier() {
        log.info("전체 식당에 대해 메인 티어 계산 시작");
        // 가게 메인 티어 계산
        List<RestaurantEntity> restaurantList = restaurantRepository.findAll();
        for (RestaurantEntity restaurant: restaurantList) {
            if (evaluationConstants.isHasTier(restaurant)) {
                restaurant.setMainTier(
                        EvaluationConstants.calculateRestaurantTier(restaurant.getRestaurantScoreSum() / restaurant.getRestaurantEvaluationCount())
                );
            } else {
                restaurant.setMainTier(-1);
            }
            restaurantRepository.save(restaurant);
        }
        log.info("전체 식당에 대해 메인 티어 계산 완료");
    }

    // 식당 별 점수, 평가 수, 상황 개수를 다시 카운트합니다.
    @Transactional
    public void calculateEvaluationDatas() {
        List<RestaurantEntity> restaurantList = restaurantRepository.findByStatus("ACTIVE");

        restaurantList.forEach(restaurant -> {
            Map<Integer, Integer> situationCountMap = new HashMap<>(Map.of(
                    1, 0, 2, 0, 3, 0, 4, 0, 5, 0, 6, 0, 7, 0, 8, 0, 9, 0));
            int evaluationCount = 0;
            double scoreSum = 0;

            for (EvaluationEntity evaluation : restaurant.getEvaluationList()) {
                if (evaluation.getStatus().equals("ACTIVE")) {
                    evaluationCount++;
                    scoreSum += evaluation.getEvaluationScore();

                    for (EvaluationSituationEntity item : evaluation.getEvaluationSituationEntityList()) {
                        Integer situationId = item.getSituation().getSituationId();
                        situationCountMap.put(situationId, situationCountMap.getOrDefault(situationId, 0) + 1);
                    }
                }
            }

            restaurant.setRestaurantEvaluationCount(evaluationCount);
            restaurant.setRestaurantScoreSum(scoreSum);
            restaurantRepository.save(restaurant);
            log.info("식당id:{} 식당:{}, 평가개수:{}, 총합:{}", restaurant.getRestaurantId(), restaurant.getRestaurantName(), evaluationCount, scoreSum);

            situationCountMap.forEach((key, value) -> restaurantSituationRelationService.createOrDelete(restaurant, key, value));
        });
    }


    // 그냥 Restaurant 리스트를 RestaurantTierDataClass 리스트로 변경 ver1
    public List<RestaurantTierDataClass> convertToTierDataClassList(
            List<RestaurantEntity> restaurantList,
            Long userId,
            boolean isRanking
    ) {
        List<RestaurantTierDataClass> resultList = new ArrayList<>();

        for (int i = 0; i < restaurantList.size(); i++) {
            RestaurantEntity restaurant = restaurantList.get(i);
            RestaurantTierDataClass newDataClass = new RestaurantTierDataClass(restaurant);
            // 즐겨찾기, 평가 여부 추가
            injectIsFavoriteIsEvaluation(newDataClass, restaurant, userId);
            // 순위, 상황 추가
            if (restaurant.getMainTier() > 0) { // 티어가 있는 경우
                if (isRanking) {
                    int ranking = i + 1;
                    newDataClass.setRanking(ranking + "");
                } else {
                    newDataClass.setRanking("-");
                }
                insertSituation(newDataClass, restaurant);
                resultList.add(newDataClass);
            } else { // 티어가 없는 경우 = 평가 데이터 부족 = 순위가 없음
                newDataClass.setRanking("-");
                resultList.add(newDataClass);
            }
        }

        return resultList;
    }

    public void injectIsFavoriteIsEvaluation(
            RestaurantTierDataClass data,
            RestaurantEntity restaurant,
            Long userId
    ) {
        // TODO: 임시로 하드코딩함. 이후 수정해야됨.
        data.setIsEvaluation(false);
        data.setIsFavorite(false);
    }

    public void insertSituation(RestaurantTierDataClass data, RestaurantEntity restaurant) {
        for (RestaurantSituationRelationEntity restaurantSituationRelationEntity : restaurant.getRestaurantSituationRelationEntityList()) {
            if (RestaurantChartSpec.hasSituation(restaurantSituationRelationEntity)) {
                data.addSituation(restaurantSituationRelationEntity);
            }
        }
    }

    // Evaluation 좋아요
    @Transactional
    public void likeEvaluation(Long userId, EvaluationEntity evaluation) {
        Optional<EvalCommentLikeEntity> likeOptional = restaurantCommentLikeRepository.findByUserIdAndEvaluationId(userId, evaluation.getId());
        Optional<RestaurantCommentDislikeEntity> dislikeOptional = restaurantCommentDislikeRepository.findByUserIdAndEvaluationId(userId, evaluation.getId());

        if (likeOptional.isPresent() && dislikeOptional.isPresent()) {
            throw new IllegalStateException(evaluation.getId() + "id 평가 좋아요 상태 문제. 서버측에 알려주세요.. 감사합니다!");
        } else if (likeOptional.isPresent()) {
            restaurantCommentLikeRepository.delete(likeOptional.get());
            evaluationLikeCountAdd(evaluation, -1);
        } else if (dislikeOptional.isPresent()) {
            restaurantCommentDislikeRepository.delete(dislikeOptional.get());
            EvalCommentLikeEntity restaurantCommentLike = new EvalCommentLikeEntity(userId, evaluation);
            restaurantCommentLikeRepository.save(restaurantCommentLike);
            evaluationLikeCountAdd(evaluation, 2);
        } else {
            EvalCommentLikeEntity restaurantCommentLike = new EvalCommentLikeEntity(userId, evaluation);
            restaurantCommentLikeRepository.save(restaurantCommentLike);
            evaluationLikeCountAdd(evaluation, 1);
        }

    }

    // Evaluation 싫어요
    @Transactional
    public void dislikeEvaluation(Long userId, EvaluationEntity evaluation) {
        Optional<EvalCommentLikeEntity> likeOptional = restaurantCommentLikeRepository.findByUserIdAndEvaluationId(userId, evaluation.getId());
        Optional<RestaurantCommentDislikeEntity> dislikeOptional = restaurantCommentDislikeRepository.findByUserIdAndEvaluationId(userId, evaluation.getId());

        if (likeOptional.isPresent() && dislikeOptional.isPresent()) {
            throw new IllegalStateException(evaluation.getId() + "id 평가 좋아요 상태 문제. 서버측에 알려주세요.. 감사합니다!");
        } else if (dislikeOptional.isPresent()) {
            restaurantCommentDislikeRepository.delete(dislikeOptional.get());
            evaluationLikeCountAdd(evaluation, 1);
        } else if (likeOptional.isPresent()) {
            restaurantCommentLikeRepository.delete(likeOptional.get());
            RestaurantCommentDislikeEntity restaurantCommentDislike = new RestaurantCommentDislikeEntity(userId, evaluation);
            restaurantCommentDislikeRepository.save(restaurantCommentDislike);
            evaluationLikeCountAdd(evaluation, -2);
        } else {
            RestaurantCommentDislikeEntity restaurantCommentDislike = new RestaurantCommentDislikeEntity(userId, evaluation);
            restaurantCommentDislikeRepository.save(restaurantCommentDislike);
            evaluationLikeCountAdd(evaluation, -1);
        }

    }

    private void evaluationLikeCountAdd(EvaluationEntity evaluation, int addNum) {
        evaluation.setCommentLikeCount(evaluation.getCommentLikeCount() + addNum);
        evaluationRepository.save(evaluation);
    }

    // 평가의 댓글 내용 삭제
    @Transactional
    public void deleteComment(EvaluationEntity evaluation, Long userId) {
        if (evaluation == null || userId == null) {
            return;
        }

        if (!evaluation.getUserId().equals(userId)) {
            throw new ParamException("해당 유저가 단 댓글이 아닙니다.");
        }

        // 평가 코멘트 및 사진 삭제
        evaluation.setCommentBody(null);
        evaluation.setCommentImgUrl(null);
        evaluation.setCommentLikeCount(0);
        evaluationRepository.save(evaluation);

        // 좋아요, 싫어요 삭제
        restaurantCommentLikeRepository.deleteAll(evaluation.getRestaurantCommentLikeList());
        restaurantCommentDislikeRepository.deleteAll(evaluation.getRestaurantCommentDislikeList());

        // 대댓글 삭제
        for (EvalCommentEntity restaurantComment : evaluation.getRestaurantCommentList()) {
            restaurantComment.setStatus("DELETED");
            restaurantComment.setCommentLikeCount(0);
            restaurantCommentRepository.save(restaurantComment);

            restaurantCommentLikeRepository.deleteAll(restaurantComment.getRestaurantCommentLikeList());
            restaurantCommentDislikeRepository.deleteAll(restaurantComment.getRestaurantCommentDislikeList());
        }
    }
}

