package com.kustaurant.kustaurant.admin.IGCrawl.service.matching;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.regex.Pattern;

@Component
public class RestaurantNameNormalizer {
    private static final Pattern BRACKET = Pattern.compile("\\(.*?\\)");
    private static final Pattern PUNCT = Pattern.compile("[\\p{Punct}]+");
    private static final Pattern MULTI_SPACE = Pattern.compile("\\s+");

    public String normalize(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }

        String s = raw.toLowerCase(Locale.ROOT).trim();
        s = BRACKET.matcher(s).replaceAll(" ");
        s = PUNCT.matcher(s).replaceAll(" ");
        s = MULTI_SPACE.matcher(s).replaceAll(" ").trim();
        s = s.replace(" ", "");

        return s;
    }
}
