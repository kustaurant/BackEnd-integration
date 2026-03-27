package com.kustaurant.crawler.IGpartnership;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class RawPostDeduplicator {

    public List<RawPost> deduplicateByCode(List<RawPost> rawPosts) {
        Map<String, RawPost> byCode = new LinkedHashMap<>();

        for (RawPost rawPost : rawPosts) {
            if (rawPost.code() == null || rawPost.code().isBlank()) {
                continue;
            }
            byCode.putIfAbsent(rawPost.code(), rawPost);
        }

        return List.copyOf(byCode.values());
    }
}