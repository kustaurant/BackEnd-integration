package com.kustaurant.kustaurant.user.login.api.controller.test;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Profile("local")
@RestController
@RequiredArgsConstructor
public class NaverCallbackController {

    /** 로컬에서 테스트하는용 컨트롤러 입니다 */

    @Value("${NAVER_CLIENT_ID}")
    private String clientId;

    @Value("${NAVER_CLIENT_SECRET}")
    private String clientSecret;

    private String redirectUri="http://localhost:8080/test/callback";

    @GetMapping("/test/callback")
    public String handleNaverCallback(
            @RequestParam String code,
            @RequestParam String state
    ) throws IOException {

        // 1. access_token 요청 URL 구성
        String tokenRequestUrl = "https://nid.naver.com/oauth2.0/token"
                + "?grant_type=authorization_code"
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&redirect_uri=" + redirectUri
                + "&code=" + code
                + "&state=" + state;

        // 2. 요청 보내기
        URL url = new URL(tokenRequestUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        // 3. 응답 읽기
        int responseCode = con.getResponseCode();
        InputStream inputStream = (responseCode == 200)
                ? con.getInputStream()
                : con.getErrorStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder responseBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            responseBody.append(line);
        }
        reader.close();

        return responseBody.toString();
    }
}
