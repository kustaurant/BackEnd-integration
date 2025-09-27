//package com.kustaurant.kustaurant.user.login.api.controller.test;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Profile;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//@Profile("local")
//@RestController
//@RequiredArgsConstructor
//public class NaverCallbackController {
//
//    /** 로컬에서 테스트하는용 컨트롤러 입니다
//     *
//     * 1. https://nid.naver.com/oauth2.0/authorize
//     *   ?response_type=code
//     *   &client_id = YOUR_CLIENT_ID
//     *   &redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Ftest%2Fcallback
//     *   &state=abc123   // 임의 난수. 세션에 저장해 두고 콜백 때 동일성 검증
//     *
//     *   위와 같은 url을 서버에 키고 입력하면 아래 콜백 엔드포인트로 access,refresh token을 네이버에서 보내줌.
//     *
//     *   2. db에 저장된 providerId, 위의 accessToken을 포함 서버 login api로 요청을 보내면 서버에서 발급해준 토큰을 확인할 수 있음
//     * */
//
//    @Value("${NAVER_CLIENT_ID}")
//    private String clientId;
//
//    @Value("${NAVER_CLIENT_SECRET}")
//    private String clientSecret;
//
//    private String redirectUri="http://localhost:8080/test/callback";
//
//    @GetMapping("/test/callback")
//    public String handleNaverCallback(
//            @RequestParam String code,
//            @RequestParam String state
//    ) throws IOException {
//
//        // 1. access_token 요청 URL 구성
//        String tokenRequestUrl = "https://nid.naver.com/oauth2.0/token"
//                + "?grant_type=authorization_code"
//                + "&client_id=" + clientId
//                + "&client_secret=" + clientSecret
//                + "&redirect_uri=" + redirectUri
//                + "&code=" + code
//                + "&state=" + state;
//
//        // 2. 요청 보내기
//        URL url = new URL(tokenRequestUrl);
//        HttpURLConnection con = (HttpURLConnection) url.openConnection();
//        con.setRequestMethod("GET");
//
//        // 3. 응답 읽기
//        int responseCode = con.getResponseCode();
//        InputStream inputStream = (responseCode == 200)
//                ? con.getInputStream()
//                : con.getErrorStream();
//
//        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//        StringBuilder responseBody = new StringBuilder();
//        String line;
//        while ((line = reader.readLine()) != null) {
//            responseBody.append(line);
//        }
//        reader.close();
//
//        return responseBody.toString();
//    }
//}
