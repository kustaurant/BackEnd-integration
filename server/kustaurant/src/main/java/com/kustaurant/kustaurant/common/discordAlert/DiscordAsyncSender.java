package com.kustaurant.kustaurant.common.discordAlert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiscordAsyncSender {
    private static final String APP_NAME = "kustaurant";
    private static final String DELIMITER = "|";

    private final RestTemplate discordRestTemplate;
    private final ObjectMapper objectMapper;
    private final AlertRateLimiter limiter;

    @Value("${alert.discord.webhook:}")
    private String webhookUrl;

    @Async("alertExecutor")
    public void send5xxAsync(Exception e, RequestSnapshot snapshot, String traceId, String env, int status) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.warn("[Discord] webhook URL 미설정으로 알림을 건너뜁니다.");
            return;
        }

        String key = buildRateLimitKey(env, snapshot, e, status);
        if (!limiter.allow(key)) {
            log.debug("[Discord] rate limited. key={}", key);
            return;
        }

        try {
            ObjectNode payload = buildPayload(e, snapshot, traceId, env, status);
            String json = objectMapper.writeValueAsString(payload);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> request = new HttpEntity<>(json, headers);
            ResponseEntity<String> response =
                    discordRestTemplate.postForEntity(webhookUrl, request, String.class);

            log.info("[Discord] sent status={} body={}",
                    response.getStatusCode().value(),
                    response.getBody());

        } catch (HttpStatusCodeException ex) {
            log.error("[Discord] HTTP {} body={}",
                    ex.getStatusCode().value(),
                    ex.getResponseBodyAsString(),
                    ex);

        } catch (JsonProcessingException ex) {
            log.error("[Discord] payload serialization failed", ex);

        } catch (Exception ex) {
            log.error("[Discord] send failed", ex);
        }
    }

    private String buildRateLimitKey(String env, RequestSnapshot snapshot, Exception e, int status) {
        return String.join(
                DELIMITER,
                env,
                nullToDash(snapshot.uri()),
                e.getClass().getSimpleName(),
                String.valueOf(status)
        );
    }

    private ObjectNode buildPayload(Exception e, RequestSnapshot snapshot, String traceId, String env, int status) {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("content", "🚨 **" + APP_NAME + "** `" + env + "` 에서 **" + status + "** 오류 발생");

        ObjectNode embed = root.putArray("embeds").addObject();
        embed.put("title", e.getClass().getSimpleName());
        embed.put("description", truncate(safeMessage(e), 1000));
        embed.put("timestamp", OffsetDateTime.now().toString());
        embed.put("color", 15158332);

        ArrayNode fields = embed.putArray("fields");
        addField(fields, "profiles", env, true);
        addField(fields, "method", snapshot.method(), true);
        addField(fields, "url", snapshot.uri(), false);
        addField(fields, "query", maskQuery(snapshot.query()), true);
        addField(fields, "clientIp", snapshot.clientIp(), true);
        addField(fields, "traceId", traceId, true);
        addField(fields, "rootCause", rootCauseSummary(e), false);

        return root;
    }

    private void addField(ArrayNode fields, String name, String value, boolean inline) {
        ObjectNode field = fields.addObject();
        field.put("name", name);
        field.put("value", isBlank(value) ? "-" : "```" + truncate(value, 500) + "```");
        field.put("inline", inline);
    }

    private String safeMessage(Exception e) {
        String message = e.getMessage();
        if (isBlank(message)) {
            return "(no message)";
        }

        return maskSensitive(message);
    }

    private String maskQuery(String queryString) {
        if (queryString == null) {
            return null;
        }
        return maskSensitive(queryString);
    }

    private String maskSensitive(String value) {
        return value
                .replaceAll("(?i)(token|accessToken|refreshToken|authorization|cookie|password|secret|code)=([^&\\s]+)", "$1=****")
                .replaceAll("(?i)Bearer\\s+[A-Za-z0-9._\\-]+", "Bearer ****");
    }

    private String rootCauseSummary(Throwable throwable) {
        Throwable root = throwable;
        while (root.getCause() != null) {
            root = root.getCause();
        }

        String className = root.getClass().getSimpleName();
        String message = root.getMessage();

        if (isBlank(message)) {
            return className;
        }
        return className + ": " + truncate(maskSensitive(message), 300);
    }

    private String truncate(String value, int max) {
        if (value == null) {
            return null;
        }
        return value.length() <= max ? value : value.substring(0, max) + "...";
    }

    private String nullToDash(String value) {
        return value == null ? "-" : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
