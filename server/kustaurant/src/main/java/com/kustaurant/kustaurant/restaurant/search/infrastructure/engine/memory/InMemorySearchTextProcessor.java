package com.kustaurant.kustaurant.restaurant.search.infrastructure.engine.memory;

import java.util.*;
import java.util.regex.Pattern;

public final class InMemorySearchTextProcessor {

    private static final Pattern MULTI_SPACE = Pattern.compile("\\s+");
    private static final Pattern PUNCT = Pattern.compile("[\\p{Punct}]+");

    private InMemorySearchTextProcessor() {
    }

    // 인덱싱용 토큰화: normalize 후 공백 분리 + 부분 문자열 토큰 생성
    public static List<String> tokenizeForIndex(String s) {
        String norm = normalize(s);
        if (norm.isEmpty()) return List.of();

        String[] base = MULTI_SPACE.split(norm);
        Set<String> out = new LinkedHashSet<>();

        for (String b : base) {
            if (b.isEmpty()) continue;

            out.add(b);
            int len = b.length();

            for (int gram = 1; gram <= len; gram++) {
                for (int i = 0; i <= len - gram; i++) {
                    out.add(b.substring(i, i + gram));
                }
            }
        }

        return new ArrayList<>(out);
    }

    // 검색어 토큰화: normalize 후 공백 분리만 수행
    public static List<String> tokenizeQuery(String s) {
        String norm = normalize(s);
        if (norm.isEmpty()) return List.of();

        String[] parts = MULTI_SPACE.split(norm);
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            if (!p.isEmpty()) out.add(p);
        }
        return out;
    }

    // 하이라이트용 토큰: 중복 제거 + 길이 내림차순 정렬
    public static List<String> normalizeQueryTokensForHighlight(String[] queryTokens) {
        List<String> out = new ArrayList<>();

        for (String qt : queryTokens) {
            String norm = normalize(qt);
            if (norm.isEmpty()) continue;

            String[] parts = MULTI_SPACE.split(norm);
            for (String p : parts) {
                if (!p.isEmpty()) out.add(p);
            }
        }

        LinkedHashSet<String> set = new LinkedHashSet<>(out);
        ArrayList<String> uniq = new ArrayList<>(set);
        uniq.sort((a, b) -> Integer.compare(b.length(), a.length()));
        return uniq;
    }

    public static String normalize(String s) {
        if (s == null) return "";
        String x = s.trim().toLowerCase(Locale.ROOT);
        x = PUNCT.matcher(x).replaceAll(" ");
        x = MULTI_SPACE.matcher(x).replaceAll(" ");
        return x;
    }
}
