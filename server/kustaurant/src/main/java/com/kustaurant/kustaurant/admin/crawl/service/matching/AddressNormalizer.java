package com.kustaurant.kustaurant.admin.crawl.service.matching;

import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AddressNormalizer {

    private static final Pattern BRACKET = Pattern.compile("\\(.*?\\)");
    private static final Pattern PUNCT_OR_SPACE = Pattern.compile("[\\p{Punct}\\s]+");
    private static final Pattern TOKEN = Pattern.compile("[가-힣0-9]{2,}");

    public String normalize(String raw) {
        if (raw == null || raw.isBlank()) {
            return "";
        }

        String s = raw.toLowerCase(Locale.ROOT).trim();
        s = s.replace("서울특별시", "서울");
        s = s.replace("경기도", "경기");
        s = BRACKET.matcher(s).replaceAll("");
        s = PUNCT_OR_SPACE.matcher(s).replaceAll("");
        s = s.replace("층", "");
        s = s.replace("호", "");

        return s;
    }

    public Set<String> tokenize(String raw) {
        String normalized = normalize(raw);
        if (normalized.isEmpty()) {
            return Set.of();
        }

        Set<String> result = new LinkedHashSet<>();
        Matcher matcher = TOKEN.matcher(normalized);
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }

    public boolean isCompatible(String rawLocation, String restaurantAddress) {
        Set<String> a = tokenize(rawLocation);
        Set<String> b = tokenize(restaurantAddress);

        if (a.isEmpty() || b.isEmpty()) return false;

        for (String token : a) {
            if (b.contains(token)) return true;
        }
        return false;
    }
}
