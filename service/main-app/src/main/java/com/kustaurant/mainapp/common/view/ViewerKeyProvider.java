package com.kustaurant.mainapp.common.view;

import com.kustaurant.mainapp.global.auth.argumentResolver.AuthUserInfo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ViewerKeyProvider {
    private static final String COOKIE_NAME = "kust_anno";

    private static final String DEVICE_ID_HEADER = "X-Device-Id";
    private static final String RETURN_DEVICE_ID_HEADER = "X-Anonymous-Id";

    public String resolveViewerKey(@Nullable AuthUserInfo user, HttpServletRequest req, HttpServletResponse res) {

        // 로그인 한 사용자
        if (user != null && user.id() != null) return "m:" + user.id();

        // 앱/클라이언트가 보내온 디바이스 ID 헤더 우선
        String deviceId = trimOrNull(req.getHeader(DEVICE_ID_HEADER));
        if (deviceId != null) return "g:" + deviceId;

        // 웹 비회원: 쿠키 사용
        String annoFromCookie = findCookie(req, COOKIE_NAME);
        if (annoFromCookie != null) return "g:" + annoFromCookie;

        // 아무것도 없으면 서버가 새 ID 발급
        String newAnon = java.util.UUID.randomUUID().toString();
        Cookie cookie = new jakarta.servlet.http.Cookie(COOKIE_NAME, newAnon);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 30); // 한 달
        res.addCookie(cookie);

        // 앱일 수도 있으니 응답 헤더도 같이 내려줌 -> 클라가 저장 후 다음부터 X-Device-Id로 전송
        res.setHeader(RETURN_DEVICE_ID_HEADER, newAnon);

        return "g:" + newAnon;
    }

    private static String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static String findCookie(HttpServletRequest req, String name) {
        if (req.getCookies() == null) return null;
        for (jakarta.servlet.http.Cookie c : req.getCookies()) {
            if (name.equals(c.getName())) {
                String v = c.getValue();
                if (v != null && !v.isBlank()) return v;
            }
        }
        return null;
    }
}
