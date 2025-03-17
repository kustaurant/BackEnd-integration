//package com.kustaurant.restauranttier.tab3_tier.service;
//
//import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
//import com.kustaurant.restauranttier.tab3_tier.repository.RestaurantApiRepository;
//import com.kustaurant.restauranttier.tab5_mypage.entity.User;
//import com.kustaurant.restauranttier.tab5_mypage.repository.UserRepository;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//class RestaurantApiServiceTest {
//
//    @Autowired
//    private RestaurantApiRepository restaurantApiRepository;
//    @Autowired
//    private UserRepository userRepository;
//
//    @Test
//    void isEvaluated() {
//        Optional<User> userOptional = userRepository.findByUserId(23);
//        Restaurant byRestaurantId = restaurantApiRepository.findByRestaurantId(599);
//
//        Assertions.assertThat(userOptional.get().getEvaluationList().stream().anyMatch(evaluation -> evaluation.getRestaurant().equals(byRestaurantId))).isEqualTo(true);
//    }
//}