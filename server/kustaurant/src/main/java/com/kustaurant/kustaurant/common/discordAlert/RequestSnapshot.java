package com.kustaurant.kustaurant.common.discordAlert;

public record RequestSnapshot(
        String method,
        String uri,
        String query,
        String clientIp
) {
    public static RequestSnapshot from(jakarta.servlet.http.HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        String ip = (xff != null && !xff.isBlank()) ? xff.split(",")[0].trim() : req.getRemoteAddr();
        return new RequestSnapshot(req.getMethod(), req.getRequestURI(), req.getQueryString(), ip);
    }
}
