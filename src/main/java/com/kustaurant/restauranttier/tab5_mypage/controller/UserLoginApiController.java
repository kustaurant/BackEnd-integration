package com.kustaurant.restauranttier.tab5_mypage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/login")
public class UserLoginController {
    public ResponseEntity<Map<String, String>> login(
        @RequestBody Map<String,String> request
    ) {
        String provider=request.get("provider");
        String providerId=request.get("providerId");

    }

}
