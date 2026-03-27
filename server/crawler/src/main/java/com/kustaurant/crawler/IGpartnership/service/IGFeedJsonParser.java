package com.kustaurant.crawler.IGpartnership;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IGFeedJsonParser {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public List<RawPost> parse(String body) {
        try {
            JsonNode root = MAPPER.readTree(body);
            JsonNode edges = root.path("data")
                    .path("xdt_api__v1__feed__user_timeline_graphql_connection")
                    .path("edges");

            if (!edges.isArray()) {
                return List.of();
            }

            List<RawPost> result = new ArrayList<>();

            for (JsonNode edge : edges) {
                JsonNode node = edge.path("node");
                String code = node.path("code").asText("");
                String captionText = "";

                JsonNode captionNode = node.path("caption");
                if (!captionNode.isMissingNode()) {
                    captionText = captionNode.path("text").asText("");
                }

                if (code == null || code.isBlank()) {
                    continue;
                }
                if (captionText == null || captionText.isBlank()) {
                    continue;
                }

                result.add(new RawPost(code, captionText));
            }

            return result;
        } catch (Exception e) {
            throw new IllegalStateException("feed JSON 파싱 실패", e);
        }
    }
}