package com.kustaurant.kustaurant.common.discordAlert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiscordNotifier {
    private final RestTemplate restTemplate = new RestTemplate(new SimpleClientHttpRequestFactory() {{
        setConnectTimeout(3000);
        setReadTimeout(3000);
    }});
    private final ObjectMapper om = new ObjectMapper();
    private final AlertRateLimiter limiter;
    private final Environment environment;

    @Value("${alert.discord.webhook}")
    private String webhookUrl;

    private static final String APP_NAME = "kustaurant";
    private static final String DELIMITER = "|";


    // 호출부(동기): 여기서 req를 스냅샷으로 변환만 하고 즉시 반환
    public void send5xx(Exception e, HttpServletRequest req, String traceId, int status) {
        RequestSnapshot snap = RequestSnapshot.from(req);
        String env = resolveEnvFromProfiles();
        send5xxAsync(e, snap, traceId, env, status);
    }

    // 실제 비동기 전송
    @Async("alertExecutor")
    public void send5xxAsync(Exception e, RequestSnapshot snap, String traceId, String env, int status) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.warn("[Discord] webhook URL 미설정으로 알림을 건너뜁니다. (alert.discord.webhook 비어있음)");
            return;
        }

        String key = env + "|" + snap.uri() + "|" + e.getClass().getSimpleName() + "|" + status;
        if (!limiter.allow(key)) return;

        try {
            ObjectNode payload = buildPayload(e, snap, traceId, env, status);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String json = om.writeValueAsString(payload);

            ResponseEntity<String> resp = restTemplate.postForEntity(webhookUrl, new HttpEntity<>(json, headers), String.class);
            log.info("[Discord] status={} body={}", resp.getStatusCodeValue(), resp.getBody());
        } catch (HttpStatusCodeException ex1) {
            log.error("[Discord] HTTP {} body={}", ex1.getStatusCode().value(), ex1.getResponseBodyAsString(), ex1);
        } catch (Exception ex2) {
            log.error("[Discord] send failed", ex2);
        }
    }



    private String resolveEnvFromProfiles() {
        String[] actives = environment.getActiveProfiles();
        if (actives.length > 0) return String.join(",", actives);   // 다수면 콤마로 표기
        String[] defaults = environment.getDefaultProfiles();
        return defaults.length > 0 ? String.join(",", defaults) : "default";
    }

    private ObjectNode buildPayload(Exception e, RequestSnapshot s, String traceId, String env, int status) {
        ObjectNode root = om.createObjectNode();
        root.put("content", "🚨 **" + APP_NAME + "** `" + env + "` 에서 **" + status + "** 오류 발생");

        ObjectNode emb = root.putArray("embeds").addObject();
        emb.put("title", e.getClass().getSimpleName());
        emb.put("description", truncate(safeMessage(e), 1000));
        emb.put("timestamp", OffsetDateTime.now().toString());
        emb.put("color", 15158332);

        ArrayNode fields = emb.putArray("fields");
        addField(fields, "method", s.method(), true);
        addField(fields, "url", s.uri(), true);
        addField(fields, "query", maskQuery(s.query()), true);
        addField(fields, "clientIp", s.clientIp(), true);
        addField(fields, "traceId", traceId, true);
        addField(fields, "profiles", env, true);
        addField(fields, "stack", firstStackLines(e, 4), false);
        return root;
    }

    private void addField(ArrayNode fields, String name, String value, boolean inline) {
        ObjectNode f = fields.addObject();
        f.put("name", name);
        f.put("value", value == null || value.isBlank() ? "-" : "```" + truncate(value, 500) + "```");
        f.put("inline", inline);
    }

    private String safeMessage(Exception e) {
        String m = e.getMessage();
        if (m == null || m.isBlank()) return "(no message)";
        return m.replaceAll("(?i)token=[^&\\s]+", "token=****");
    }

    private String maskQuery(String qs) {
        if (qs == null) return null;
        return qs.replaceAll("(?i)(token|authorization|cookie)=[^&\\s]+", "$1=****");
    }

    private String firstStackLines(Exception e, int lines) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] arr = e.getStackTrace();
        for (int i = 0; i < Math.min(lines, arr.length); i++) sb.append(arr[i]).append("\n");
        return sb.toString();
    }

    private String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
