package com.kustaurant.restauranttier.tab3_tier.service;

import com.kustaurant.restauranttier.tab3_tier.constants.TierConstants;
import com.kustaurant.restauranttier.tab3_tier.controller.TierWebController;
import com.kustaurant.restauranttier.tab3_tier.dto.EvaluationDTO;
import com.kustaurant.restauranttier.tab3_tier.dto.RestaurantCommentDTO;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import com.kustaurant.restauranttier.common.etc.JsonData;
import com.kustaurant.restauranttier.tab3_tier.etc.RestaurantTierDataClass;
import com.kustaurant.restauranttier.common.user.CustomOAuth2UserService;
import com.kustaurant.restauranttier.tab3_tier.entity.*;
import com.kustaurant.restauranttier.tab3_tier.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationService {
    private final EvaluationRepository evaluationRepository;
    private final SituationRepository situationRepository;
    private final EvaluationItemScoreRepository evaluationItemScoreRepository;
    private final RestaurantService restaurantService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final SituationService situationService;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantFavoriteRepository restaurantFavoriteRepository;
    private final RestaurantSituationRelationRepository restaurantSituationRelationRepository;
    private final RestaurantCommentService restaurantCommentService;
    private final RestaurantCommentRepository restaurantCommentRepository;
    private final RestaurantSituationRelationService restaurantSituationRelationService;
    private final EvaluationItemScoresService evaluationItemScoresService;

    @Value("${tier.min.evaluation}")
    private int minNumberOfEvaluations;

    public Evaluation getByUserAndRestaurant(User user, Restaurant restaurant) {
        Optional<Evaluation> evaluation = evaluationRepository.findByUserAndRestaurant(user, restaurant);
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

    public void createOrUpdate(User user, Restaurant restaurant, EvaluationDTO evaluationDTO) {
        Evaluation evaluation = evaluationRepository.findByUserAndRestaurantAndStatus(user, restaurant, "ACTIVE").orElse(null);

        if (evaluation == null) { // 이전 평가가 없을 경우
            evaluationCreate(user, restaurant, evaluationDTO);
        } else { // 이전 평가가 있을 경우
            evaluationUpdate(user, restaurant, evaluation, evaluationDTO);
        }
    }

    private void evaluationUpdate(User user, Restaurant restaurant, Evaluation evaluation, EvaluationDTO evaluationDTO) {
        // 평가 업데이트
        evaluation.setUpdatedAt(LocalDateTime.now());
        evaluation.setEvaluationScore(evaluationDTO.getEvaluationScore());
        evaluationRepository.save(evaluation);
        // 평가 코멘트 업데이트
        RestaurantComment comment = restaurantCommentService.findCommentByEvaluationId(evaluation.getEvaluationId());
        if (evaluationDTO.getEvaluationComment() == null || evaluationDTO.getEvaluationComment().isEmpty()) { // 코멘트 내용이 없는 경우
            restaurantCommentService.deleteComment(comment);
        } else { // 코멘트 내용이 있는 경우
            if (comment == null) { // 기존 코멘트가 없으면 생성
                restaurantCommentService.createRestaurantComment(user, restaurant, evaluation, evaluationDTO);
            } else { // 기존 코멘트가 있으면 수정
                restaurantCommentService.updateRestaurantComment(evaluationDTO, comment);
            }
        }
        // Evaluation Situation Item Table & Restaurant Situation Relation Table 반영
        evaluationItemScoresService.deleteSituationsByEvaluation(evaluation);
        for (Integer evaluationSituation : evaluationDTO.getEvaluationSituations()) {
            // Evaluation Situation Item Table
            situationRepository.findBySituationId(evaluationSituation).ifPresent(newSituation -> {
                evaluationItemScoreRepository.save(new EvaluationItemScore(evaluation, newSituation));
            });
            // Restaurant Situation Relation Table
            situationRepository.findBySituationId(evaluationSituation).ifPresent(newSituation ->
                    restaurantSituationRelationService.updateOrCreate(restaurant, newSituation, 1));
        }
    }

    private void evaluationCreate(User user, Restaurant restaurant, EvaluationDTO evaluationDTO) {
        // 평가 저장
        Evaluation evaluation = new Evaluation(
                evaluationDTO.getEvaluationScore(), "ACTIVE", LocalDateTime.now(), user, restaurant
        );
        evaluationRepository.save(evaluation);
        // 평가 코멘트
        if (evaluationDTO.getEvaluationComment() != null && !evaluationDTO.getEvaluationComment().isEmpty()) {
            restaurantCommentService.createRestaurantComment(user, restaurant, evaluation, evaluationDTO);
        }
        // Evaluation Situation Item Table & Restaurant Situation Relation Table 반영
        for (Integer evaluationSituation : evaluationDTO.getEvaluationSituations()) {
            // Evaluation Situation Item Table
            situationRepository.findBySituationId(evaluationSituation).ifPresent(newSituation ->
                    evaluationItemScoreRepository.save(new EvaluationItemScore(evaluation, newSituation)));
            // Restaurant Situation Relation Table
            situationRepository.findBySituationId(evaluationSituation).ifPresent(newSituation ->
                    restaurantSituationRelationService.updateOrCreate(restaurant, newSituation, 1));
        }
    }

    // 모든 평가 기록에 대해서 티어를 다시 계산함.
    public void calculateAllTier() {
        // 가게 메인 티어 계산
        List<Restaurant> restaurantList = restaurantRepository.getAllRestaurantsOrderedByAvgScore(minNumberOfEvaluations, "전체");
        for (Restaurant restaurant: restaurantList) {
            if (restaurant.getRestaurantEvaluationCount() >= minNumberOfEvaluations) {
                restaurant.setMainTier(
                        TierConstants.calculateRestaurantTier(restaurant.getRestaurantScoreSum() / restaurant.getRestaurantEvaluationCount())
                );
            } else {
                restaurant.setMainTier(-1);
            }
            restaurantRepository.save(restaurant);
        }
    }


    public List<RestaurantTierDataClass> getAllRestaurantTierDataClassList(String position, Principal principal, int page, Boolean isSearching) {
        List<Restaurant> restaurantList = restaurantRepository.getAllRestaurantsOrderedByAvgScore(minNumberOfEvaluations, position);

        return convertToTierDataClassList(restaurantList, principal, page, isSearching);
    }

    public List<RestaurantTierDataClass> getRestaurantTierDataClassListByCuisine(String cuisine, String position, Principal principal, int page, Boolean isSearching) {
        List<Restaurant> restaurantList = restaurantRepository.getRestaurantsByCuisineOrderedByAvgScore(cuisine, minNumberOfEvaluations, position);

        return convertToTierDataClassList(restaurantList, principal, page, isSearching);
    }

    public List<RestaurantTierDataClass> getRestaurantTierDataClassListBySituation(Situation situation, String position, Principal principal, int page, Boolean isSearching) {
        // TODO: 현재 주석처리 했습니다.
        return null;
//        List<Restaurant> restaurantList = restaurantRepository.getRestaurantsBySituationOrderedByAvgScore(situation, minNumberOfEvaluations, position);
//
//        return convertToTierDataClassList(restaurantList, situation, principal, page, isSearching);
    }

    public List<RestaurantTierDataClass> getRestaurantTierDataClassListByCuisineAndSituation(String cuisine, Situation situation, String position, Principal principal, int page, Boolean isSearching) {
        // TODO: 현재 주석처리 했습니다.
        return null;
//        List<Restaurant> restaurantList = restaurantRepository.getRestaurantsByCuisineAndSituationOrderedByAvgScore(cuisine, situation, minNumberOfEvaluations, position);
//
//        return convertToTierDataClassList(restaurantList, situation, principal, page, isSearching);
    }

    // 그냥 Restaurant 리스트를 RestaurantTierDataClass 리스트로 변경 ver1
    public List<RestaurantTierDataClass> convertToTierDataClassList(List<Restaurant> restaurantList, Principal principal, int page, Boolean isSearching) {
        List<RestaurantTierDataClass> resultList = new ArrayList<>();
        for (int i = 0; i < restaurantList.size(); i++) {
            Restaurant restaurant = restaurantList.get(i);
            RestaurantTierDataClass newDataClass = new RestaurantTierDataClass(restaurant);
            injectIsFavoriteIsEvaluation(principal, newDataClass, restaurant, i, page, isSearching);
            //
            if (restaurant.getRestaurantEvaluationCount() < minNumberOfEvaluations) { // 평가 데이터 부족
                newDataClass.setRanking("-");
                resultList.add(newDataClass);
            } else { // 평가 데이터가 충분히 있는 경우
                newDataClass.setRanking((i + 1) + "");
                insertSituation(newDataClass, restaurant);
                resultList.add(newDataClass);
            }
        }
        return resultList;
    }


    private void injectIsFavoriteIsEvaluation(Principal principal, RestaurantTierDataClass newDataClass, Restaurant restaurant, int index, int page, Boolean isSearching) {
        if (principal == null) {
            newDataClass.setIsEvaluation(false);
            newDataClass.setIsFavorite(false);
            return;
        }
        if ( // 현재 페이지만 연산
                index >= TierWebController.tierPageSize * page
                        && index < TierWebController.tierPageSize * (page + 1)
        ) {
            User user = customOAuth2UserService.getUser(principal.getName());
            // 로그인 된 경우에 즐찾, 평가 여부 저장
            // 평가 여부
            Optional<Evaluation> evaluationOptional = evaluationRepository.findByUserAndRestaurant(user, restaurant);
            newDataClass.setIsEvaluation(evaluationOptional.isPresent());
            // 즐겨찾기 여부
            Optional<RestaurantFavorite> favoriteOptional = restaurantFavoriteRepository.findByUserAndRestaurant(user, restaurant);
            newDataClass.setIsFavorite(favoriteOptional.isPresent());
            return;
        }
        if (isSearching) {
            User user = customOAuth2UserService.getUser(principal.getName());
            // 로그인 된 경우에 즐찾, 평가 여부 저장
            // 평가 여부
            Optional<Evaluation> evaluationOptional = evaluationRepository.findByUserAndRestaurant(user, restaurant);
            newDataClass.setIsEvaluation(evaluationOptional.isPresent());
            // 즐겨찾기 여부
            Optional<RestaurantFavorite> favoriteOptional = restaurantFavoriteRepository.findByUserAndRestaurant(user, restaurant);
            newDataClass.setIsFavorite(favoriteOptional.isPresent());
        }
    }

    public void insertSituation(RestaurantTierDataClass newDataClass, Restaurant restaurant) {
        for (RestaurantSituationRelation restaurantSituationRelation : restaurant.getRestaurantSituationRelationList()) {
            if (restaurantSituationRelation.getDataCount() >= minNumberOfEvaluations) {
                newDataClass.addSituation(restaurantSituationRelation);
            }
        }
        // situation 순서대로 정렬
        newDataClass.setRestaurantSituationRelationList(newDataClass.getRestaurantSituationRelationList().stream()
                .sorted(Comparator.comparing(restaurantSituationRelation -> restaurantSituationRelation.getSituation().getSituationId()))
                .collect(Collectors.toList()));
    }
}

