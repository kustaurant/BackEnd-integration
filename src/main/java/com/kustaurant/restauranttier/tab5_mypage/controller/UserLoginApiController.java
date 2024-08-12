//package com.kustaurant.restauranttier.tab5_mypage.controller;
//
//import com.kustaurant.restauranttier.tab5_mypage.repository.UserRepository;
//import com.kustaurant.restauranttier.tab5_mypage.service.UserApiLoginService;
//import io.swagger.v3.oas.annotations.Operation;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/v1/login")
//public class UserLoginApiController {
//    private final UserApiLoginService userApiLoginService;
//    private final UserRepository userRepository;
//
//    @Operation(
//            summary = "로그인 기능",
//            description = "provider, provider Id를 보내면 accessToken을 반환해 줍니다."
//    )
//    public ResponseEntity<Map<String, String>> login(
//        @RequestBody Map<String,String> request
//    ) {
//        String provider=request.get("provider");
//        String providerId=request.get("providerId");
//
//        //db조회에서 존재하는 유저인지 확인
//    }
//
//
//}
