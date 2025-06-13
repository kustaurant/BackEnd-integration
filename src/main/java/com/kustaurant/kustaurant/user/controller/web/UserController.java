package com.kustaurant.kustaurant.user.controller.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final List<String> loginImgUrlList = List.of(
            "/img/login/food1.jpg",
            "/img/login/food1.jpg",
            "/img/login/food2.jpg",
            "/img/login/food3.jpg",
            "/img/login/food4.jpg",
            "/img/login/food5.jpg",
            "/img/login/food6.jpg",
            "/img/login/food7.jpg",
            "/img/login/food8.jpg",
            "/img/login/food9.jpg",
            "/img/login/food10.jpg",
            "/img/login/food11.jpg"
    );

    // 실제 로그인을 진행하는 post는 스프링 시큐리티가 해줌
    @GetMapping("/login")
    public String login(
            Model model,
            HttpServletRequest request
    ) {
        String uri = request.getHeader("Referer");
        if (uri != null && !uri.contains("/login")) {
            request.getSession().setAttribute("prevPage", uri);
        }
        String imgUrl = selectRandomString(loginImgUrlList);
        model.addAttribute("imgUrl", imgUrl);
        return "login_form";
    }

    public static String selectRandomString(List<String> stringList) {
        // 빈 리스트인 경우 null을 반환하거나 예외 처리를 수행할 수 있습니다.
        if (stringList == null || stringList.isEmpty()) {
            return null;
        }

        // 리스트의 크기를 얻어옵니다.
        int size = stringList.size();

        // 무작위 인덱스를 생성합니다.
        Random random = new Random();
        int randomIndex = random.nextInt(size);

        // 무작위로 선택된 문자열을 반환합니다.
        return stringList.get(randomIndex);
    }

    // 로그인되어있는지 여부 확인
    @PreAuthorize("isAuthenticated() and hasRole('USER')")
    @GetMapping("/api/is-login")
    public ResponseEntity<String> isLogin () {
        return ResponseEntity.ok("true");
    }

}
