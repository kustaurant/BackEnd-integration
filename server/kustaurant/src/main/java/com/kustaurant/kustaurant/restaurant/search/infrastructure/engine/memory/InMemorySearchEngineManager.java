package com.kustaurant.kustaurant.restaurant.search.infrastructure.engine.memory;

import com.kustaurant.kustaurant.restaurant.search.infrastructure.persistence.response.RestaurantForEngine;

import java.util.*;
import java.util.regex.Pattern;

public class InMemorySearchEngineManager {

    // ====== 토큰 분리 규칙 ======
    public static final Pattern MULTI_SPACE = Pattern.compile("\\s+");
    public static final Pattern PUNCT = Pattern.compile("[\\p{Punct}]+"); // 단순히 특수문자 제거

    // ====== 역인덱스: token -> restaurantIds (sorted unique) ======
    public static final Map<String, long[]> titleIndex = new HashMap<>();
    public static final Map<String, long[]> categoryIndex = new HashMap<>();
    public static final Map<String, long[]> menuIndex = new HashMap<>();

    // ====== 원문 캐시 (하이라이트 / 메뉴명 반환용) ======
    public static final Map<Long, String> titleById = new HashMap<>();
    public static final Map<Long, String> categoryById = new HashMap<>();
    public static final Map<Long, List<String>> menusById = new HashMap<>();

    private static class LongListBuilder {
        long[] a = new long[8];
        int size = 0;

        void add(long v) {
            if (size == a.length) a = Arrays.copyOf(a, a.length * 2);
            a[size++] = v;
        }

        long[] toSortedUniqueArray() {
            long[] out = Arrays.copyOf(a, size);
            Arrays.sort(out);
            // unique
            int w = 0;
            for (int i = 0; i < out.length; i++) {
                if (i == 0 || out[i] != out[i - 1]) out[w++] = out[i];
            }
            return Arrays.copyOf(out, w);
        }
    }

    // ====== 빌드(전처리) ======
    public static void build(List<RestaurantForEngine> docs) {
        // 임시 인덱스 빌더
        Map<String, LongListBuilder> tTmp = new HashMap<>();
        Map<String, LongListBuilder> cTmp = new HashMap<>();
        Map<String, LongListBuilder> mTmp = new HashMap<>();

        for (RestaurantForEngine d : docs) {
            titleById.put(d.id(), d.name());
            categoryById.put(d.id(), d.cuisine());
            menusById.put(d.id(), d.menus() == null ? List.of() : d.menus());

            // title tokens
            for (String tok : tokenizeForIndex(d.name())) {
                tTmp.computeIfAbsent(tok, k -> new LongListBuilder()).add(d.id());
            }
            // category tokens
            for (String tok : tokenizeForIndex(d.cuisine())) {
                cTmp.computeIfAbsent(tok, k -> new LongListBuilder()).add(d.id());
            }
            // menu tokens
            if (d.menus() != null) {
                for (String menu : d.menus()) {
                    for (String tok : tokenizeForIndex(menu)) {
                        mTmp.computeIfAbsent(tok, k -> new LongListBuilder()).add(d.id());
                    }
                }
            }
        }

        // finalize: sorted unique long[]
        finalizeIndex(tTmp, titleIndex);
        finalizeIndex(cTmp, categoryIndex);
        finalizeIndex(mTmp, menuIndex);
    }

    private static void finalizeIndex(Map<String, LongListBuilder> tmp, Map<String, long[]> target) {
        target.clear();
        for (Map.Entry<String, LongListBuilder> e : tmp.entrySet()) {
            target.put(e.getKey(), e.getValue().toSortedUniqueArray());
        }
    }

    /**
     * 인덱스용 토큰화:
     * - normalize 후 공백 split
     */
    private static List<String> tokenizeForIndex(String s) {
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

    public static String normalize(String s) {
        if (s == null) return "";
        String x = s.trim().toLowerCase(Locale.ROOT);
        x = PUNCT.matcher(x).replaceAll(" ");  // 특수문자는 공백
        x = MULTI_SPACE.matcher(x).replaceAll(" ");
        return x;
    }
}
