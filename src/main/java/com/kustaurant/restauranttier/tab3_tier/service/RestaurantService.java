package com.kustaurant.restauranttier.tab3_tier.service;

import com.kustaurant.restauranttier.common.exception.exception.DataNotFoundException;
import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantHashtag;
import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantMenu;
import com.kustaurant.restauranttier.tab3_tier.repository.RestaurantMenuRepository;
import com.kustaurant.restauranttier.tab3_tier.repository.RestaurantRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RestaurantService {
    // 슬라이더에 나오는 식당들의 평가 수 기준 (현재 2이상)
    public static final Integer evaluationCount = 2;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMenuRepository restaurantmenuRepository;

    @Value("${tier.min.evaluation}")
    private int minNumberOfEvaluations;

    public List<Restaurant> searchRestaurants(String[] keyword) {
        Specification<Restaurant> spec = createSearchSpecification(keyword);
        return restaurantRepository.findAll(spec);
    }

    private Specification<Restaurant> createSearchSpecification(String[] kws) {
        return new Specification<Restaurant>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Restaurant> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거

                // 조인
                Join<Restaurant, RestaurantHashtag> joinHashtag = root.join("restaurantHashtagList", JoinType.LEFT);
                Join<Restaurant, RestaurantMenu> joinMenu = root.join("restaurantMenuList", JoinType.LEFT);
                //Join<Restaurant, Situation> joinSituation = root.join("situationList", JoinType.LEFT);

                List<Predicate> predicates = new ArrayList<>();
                // 여기서 for문을 사용하여 각 항목에 대한 Predicate를 생성하고 predicates 리스트에 추가합니다.
                Predicate statusPredicate = cb.equal(root.get("status"), "ACTIVE");
                for (String kw : kws) {
                    Predicate namePredicate = cb.like(root.get("restaurantName"), "%" + kw + "%");
                    Predicate typePredicate = cb.like(root.get("restaurantType"), "%" + kw + "%");
                    Predicate cuisinePredicate = cb.like(root.get("restaurantCuisine"), "%" + kw + "%");
                    Predicate hashtagPredicate = cb.like(joinHashtag.get("hashtagName"), "%" + kw + "%");
                    Predicate menuPredicate = cb.like(joinMenu.get("menuName"), "%" + kw + "%");

                    // 각 Predicate를 predicates 리스트에 추가합니다.
                    predicates.add(cb.and(
                            cb.equal(root.get("status"), "ACTIVE"),
                            cb.or(namePredicate, typePredicate, cuisinePredicate, hashtagPredicate, menuPredicate)));
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

    public Restaurant getRestaurant(Integer id) {
        Optional<Restaurant> restaurant = restaurantRepository.findById(id);
        if (restaurant.isPresent()) {
            return restaurant.get();
        } else {
            throw new DataNotFoundException("restaurant not found");
        }

    }

    // 식당의 메뉴 리스트 반환
    public List<RestaurantMenu> getRestaurantMenuList(int restaurantId) {
        Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId);
        if (restaurant.getStatus().equals("ACTIVE")) {
            return restaurantmenuRepository.findByRestaurantOrderByMenuId(restaurant);
        } else {
            return null;
        }
    }
    // status가 ACTIVE인 cuisine에 속한 식당 리스트 반환
    public List<Restaurant> getRestaurantList(String cuisine) {
        if (cuisine.equals("전체")) {
            return restaurantRepository.findByStatus("ACTIVE");
        } else {
            return restaurantRepository.findByRestaurantCuisineAndStatus(cuisine, "ACTIVE");
        }
    }
    // 뽑기 리스트 반환
    public List<Restaurant> getRestaurantListByRandomPick(String cuisine, String location) {
        // cuisine 이 전체일 떄
        if(cuisine.equals("전체")){
            if(location.equals("전체")) {
                return restaurantRepository.findByStatus("ACTIVE");
            }
            return restaurantRepository.findByStatusAndRestaurantPosition("ACTIVE", location);
        }
        // cuisine이 전체가 아닐때

        else{
            if(location.equals("전체")) {
                return restaurantRepository.findByRestaurantCuisineAndStatus(cuisine, "ACTIVE");
            }
            return restaurantRepository.findByRestaurantCuisineAndStatusAndRestaurantPosition(cuisine, "ACTIVE", location);
        }

    }
    // 해당 식당이 방문 상위 몇 퍼센트인지 반환
    public Float getPercentOrderByVisitCount(Restaurant restaurant) {
        return restaurantRepository.getPercentOrderByVisitCount(restaurant);
    }
    // 식당 방문 카운트 1 증가
    // 식당 방문 카운트 1 증가(5분에 최대한번)
//    private Map<Integer, LocalDateTime> lastUpdateMap = new HashMap<>(); // 식당별로 마지막 업데이트 시간을 저장하기 위한 맵
    public void plusVisitCount(Restaurant restaurant) {
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

    public List<Restaurant> getTopRestaurants() {
        // 모든 'ACTIVE' 상태의 식당을 불러온다.
        List<Restaurant> restaurants = restaurantRepository.findByStatus("ACTIVE");

        // 평가 데이터가 evaluationCount개 이상 있는 식당을 필터링하고,
        // calculateAverageScore 메소드를 사용하여 평균 평가 점수를 기준으로 내림차순 정렬하여 상위 15개를 추출
        return restaurants.stream()
                .filter(r -> r.getEvaluationList().size() >= evaluationCount)
                .sorted(Comparator.comparingDouble(Restaurant::calculateAverageScore).reversed()) // 평균 점수에 따라 내림차순 정렬
                .limit(15) // 상위 15개만 추출
                .collect(Collectors.toList()); // 리스트로 수집
    }

}
