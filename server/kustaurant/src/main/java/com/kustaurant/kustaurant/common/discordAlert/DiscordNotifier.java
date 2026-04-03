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
public class DiscordNotifier {

    private final DiscordAsyncSender discordAsyncSender;
    private final Environment environment;

    public void send5xx(Exception e, HttpServletRequest req, String traceId, int status) {
        RequestSnapshot snapshot = RequestSnapshot.from(req);
        String env = resolveEnvFromProfiles();
        discordAsyncSender.send5xxAsync(e, snapshot, traceId, env, status);
    }

    private String resolveEnvFromProfiles() {
        String[] actives = environment.getActiveProfiles();
        if (actives.length > 0) {
            return String.join(",", actives);
        }

        String[] defaults = environment.getDefaultProfiles();
        if (defaults.length > 0) {
            return String.join(",", defaults);
        }

        return "default";
    }
}
