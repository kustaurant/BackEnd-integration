//package com.kustaurant.restauranttier.common.user3.core;
//
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class RefreshTokenService {
//
//    private final RefreshTokenRepository repository;
//
//    @Transactional
//    public void saveTokenInfo(String email, String refreshToken, String accessToken) {
//        repository.save(new RefreshToken3(email, accessToken, refreshToken));
//    }
//
//    @Transactional
//    public void removeRefreshToken(String accessToken) {
//        RefreshToken3 token = repository.findByAccessToken(accessToken)
//                .orElseThrow(IllegalArgumentException::new);
//
//        repository.delete(token);
//    }
//}
