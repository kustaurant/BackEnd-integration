package com.kustaurant.kustaurant.common.util;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

public final class ClientInfoUtil {
    private ClientInfoUtil() {}

    // 프록시 / 로드밸런서를 고려한 클라이언트 IP 추출
    public static String extractIp(HttpServletRequest req) {
        String xf = req.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return req.getRemoteAddr();
    }

    // 123.45.67.* 형태로 앞 3옥텟 반환
    public static String subnetV4(String ip) {
        String[] p = ip.split("\\.");
        return (p.length >= 3) ? p[0] + '.' + p[1] + '.' + p[2] : ip;
    }

    public static String majorUA(String ua) {
        if (ua == null) return "";
        if (ua.contains("Android"))   return "Android";
        if (ua.contains("iPhone"))    return "iPhone";
        if (ua.contains("iPad"))      return "iPad";
        if (ua.contains("Macintosh")) return "Mac";
        if (ua.contains("Windows"))   return "Windows";
        return ua;
    }
}
