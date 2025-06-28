package com.kustaurant.kustaurant.restaurant.restaurant.service;

import com.kustaurant.kustaurant.global.exception.ErrorCode;
import com.kustaurant.kustaurant.evaluation.comment.service.EvaluationCommentService;
import com.kustaurant.kustaurant.restaurant.restaurant.controller.web.RestaurantDetailWebDto;
import com.kustaurant.kustaurant.restaurant.restaurant.domain.Restaurant;
import com.kustaurant.kustaurant.evaluation.comment.controller.response.EvalCommResponse;
import com.kustaurant.kustaurant.restaurant.restaurant.controller.response.RestaurantDetailDTO;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantMenuEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.entity.RestaurantEntity;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantMenuRepository;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantRepository;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RestaurantWebService {
    
    private final RestaurantService restaurantService;
    private final EvaluationCommentService restaurantCommentService;

//    public RestaurantDetailWebDto getRestaurantWebDetails(Long userId, Integer restaurantId) {
//        RestaurantDetailDTO restaurantDetailDto = restaurantService.getActiveRestaurantDetailDto(restaurantId, userId);
//        Restaurant restaurant = restaurantService.getActiveDomain(restaurantId);
//        List<EvalCommResponse> comments = restaurantCommentService.getRestaurantCommentList(restaurantId, userId, true);
//
//        return new RestaurantDetailWebDto(
//                restaurantDetailDto,
//                restaurant,
//                comments
//        );
//    }

    // TODO: need to delete everything below this
    
    // 슬라이더에 나오는 식당들의 평가 수 기준 (현재 2이상)
    public static final Integer evaluationCount = 2;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMenuRepository restaurantMenuRepository;

    public List<RestaurantEntity> searchRestaurants(String[] keyword) {
        Specification<RestaurantEntity> spec = createSearchSpecification(keyword);
//        return restaurantRepository.findAll(spec);
        return new ArrayList<>();
    }

    private Specification<RestaurantEntity> createSearchSpecification(String[] kws) {
        return new Specification<RestaurantEntity>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<RestaurantEntity> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거

                // 조인
                Join<RestaurantEntity, RestaurantMenuEntity> joinMenu = root.join("restaurantMenuList", JoinType.LEFT);
                //Join<Restaurant, Situation> joinSituation = root.join("situationList", JoinType.LEFT);

                List<Predicate> predicates = new ArrayList<>();
                // 여기서 for문을 사용하여 각 항목에 대한 Predicate를 생성하고 predicates 리스트에 추가합니다.
                Predicate statusPredicate = cb.equal(root.get("status"), "ACTIVE");
                for (String kw : kws) {
                    Predicate namePredicate = cb.like(root.get("restaurantName"), "%" + kw + "%");
                    Predicate typePredicate = cb.like(root.get("restaurantType"), "%" + kw + "%");
                    Predicate cuisinePredicate = cb.like(root.get("restaurantCuisine"), "%" + kw + "%");
                    Predicate menuPredicate = cb.like(joinMenu.get("menuName"), "%" + kw + "%");

                    // 각 Predicate를 predicates 리스트에 추가합니다.
                    predicates.add(cb.and(
                            cb.equal(root.get("status"), "ACTIVE"),
                            cb.or(namePredicate, typePredicate, cuisinePredicate, menuPredicate)));
                }

                // predicates 리스트에 있는 모든 Predicate를 and() 메소드에 전달하여 모든 조건을 결합합니다.
                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };
    }

    // 페이지 번호를 입력받아 해당 페이지의 데이터 조회
    /*public Page<Restaurant> getList(int page, String kw) {
        Pageable pageable = PageRequest.of(page, 30);
        Specification<Restaurant> spec = search(kw);

        return restaurantRepository.findAll(spec, pageable);
    }*/

    public RestaurantEntity getRestaurant(Integer id) {
        Optional<RestaurantEntity> restaurant = restaurantRepository.findById(id);
        if (restaurant.isPresent()) {
            return restaurant.get();
        } else {
            throw new DataNotFoundException(ErrorCode.RESTAURANT_NOT_FOUND, id, "식당");
        }
    }

    // status가 ACTIVE인 cuisine에 속한 식당 리스트 반환
    public List<RestaurantEntity> getRestaurantList(String cuisine) {
        if (cuisine.equals("전체")) {
            return restaurantRepository.findByStatus("ACTIVE");
        } else {
            return restaurantRepository.findByRestaurantCuisineAndStatus(cuisine, "ACTIVE");
        }
    }
    // 뽑기 리스트 반환
    public List<RestaurantEntity> getRestaurantListByRandomPick(String cuisine, String location) {
        // cuisine이 전체일 때
        if (cuisine.equals("전체")) {
            if (location.equals("전체")) {
                return restaurantRepository.findByStatusAndMainTierNot("ACTIVE", -1);
            }
            return restaurantRepository.findByStatusAndRestaurantPositionAndMainTierNot("ACTIVE", location, -1);
        } else {
            if (location.equals("전체")) {
                return restaurantRepository.findByRestaurantCuisineAndStatusAndMainTierNot(cuisine, "ACTIVE", -1);
            }
            return restaurantRepository.findByRestaurantCuisineAndStatusAndRestaurantPositionAndMainTierNot(cuisine, "ACTIVE", location, -1);
        }
    }

    // 식당 방문 카운트 1 증가
    // 식당 방문 카운트 1 증가(5분에 최대한번)
//    private Map<Integer, LocalDateTime> lastUpdateMap = new HashMap<>(); // 식당별로 마지막 업데이트 시간을 저장하기 위한 맵
    public void plusVisitCount(RestaurantEntity restaurant) {
        restaurant.setVisitCount(restaurant.getVisitCount() + 1);
        restaurantRepository.save(restaurant);

//        Integer restaurantId = restaurant.getRestaurantId();
//        LocalDateTime lastUpdate = lastUpdateMap.getOrDefault(restaurantId, LocalDateTime.MIN); // 해당 식당의 마지막 업데이트 시간 가져오기
//        LocalDateTime now = LocalDateTime.now();
//
//        // 마지막 업데이트 시간부터 5분이 지났는지 확인
//        if (Duration.between(lastUpdate, now).toMinutes() >= 5) {
//            restaurant.setVisitCount(restaurant.getVisitCount() + 1);
//            restaurantRepository.save(restaurant);
//            lastUpdateMap.put(restaurantId, now); // 업데이트된 시간을 맵에 저장
//        }
    }

    // 인기 식당 반환

    public List<RestaurantEntity> getTopRestaurants() {
        // 모든 'ACTIVE' 상태의 식당을 불러온다.
        List<RestaurantEntity> restaurants = restaurantRepository.findByStatus("ACTIVE");

        // 평가 데이터가 evaluationCount개 이상 있는 식당을 필터링하고,
        // calculateAverageScore 메소드를 사용하여 평균 평가 점수를 기준으로 내림차순 정렬하여 상위 15개를 추출
        return restaurants.stream()
                .filter(r -> r.getEvaluationList().size() >= evaluationCount)
                .sorted(Comparator.comparingDouble(RestaurantEntity::calculateAverageScore).reversed()) // 평균 점수에 따라 내림차순 정렬
                .limit(15) // 상위 15개만 추출
                .collect(Collectors.toList()); // 리스트로 수집
    }

}
