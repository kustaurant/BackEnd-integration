package com.kustaurant.kustaurant.restaurant.search.infrastructure.engine.memory;

import com.kustaurant.kustaurant.restaurant.search.infrastructure.engine.RestaurantSearchEngine;
import com.kustaurant.kustaurant.restaurant.search.infrastructure.engine.response.SearchResult;
import com.kustaurant.kustaurant.restaurant.search.infrastructure.persistence.response.RestaurantForEngine;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;

@Component
public class InMemorySearchEngine implements RestaurantSearchEngine {

    // ====== 가중치 (원하는대로 조절) ======
    private static final int W_TITLE = 100;
    private static final int W_CATEGORY = 30;
    private static final int W_MENU = 10;

    // ====== 토큰 분리 규칙 ======
    private static final Pattern MULTI_SPACE = Pattern.compile("\\s+");
    private static final Pattern PUNCT = Pattern.compile("[\\p{Punct}]+"); // 단순히 특수문자 제거

    // ====== 역인덱스: token -> restaurantIds (sorted unique) ======
    private final Map<String, long[]> titleIndex = new HashMap<>();
    private final Map<String, long[]> categoryIndex = new HashMap<>();
    private final Map<String, long[]> menuIndex = new HashMap<>();

    // ====== 원문 캐시 (하이라이트 / 메뉴명 반환용) ======
    private final Map<Long, String> titleById = new HashMap<>();
    private final Map<Long, String> categoryById = new HashMap<>();
    private final Map<Long, List<String>> menusById = new HashMap<>();

    // ====== 전처리용 임시: token -> LongListBuilder ======
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
    public void build(List<RestaurantForEngine> docs) {
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

    private static String normalize(String s) {
        if (s == null) return "";
        String x = s.trim().toLowerCase(Locale.ROOT);
        x = PUNCT.matcher(x).replaceAll(" ");  // 특수문자는 공백
        x = MULTI_SPACE.matcher(x).replaceAll(" ");
        return x;
    }

    // ====== 검색 ======
    @Override
    public SearchResult searchRestaurantIds(String[] kwArr, Pageable pageable) {
        if (kwArr == null || kwArr.length == 0) {
            return new SearchResult(new PageImpl<>(List.of(), pageable, 0), Map.of());
        }

        // 1) 쿼리 토큰 정규화 + 토큰 확장(인덱스와 동일 규칙을 쓰면 매칭 잘 됨)
        LinkedHashSet<String> qTokSet = new LinkedHashSet<>();
        for (String qt : kwArr) {
            for (String tok : tokenizeQuery(qt)) {
                if (!tok.isEmpty()) qTokSet.add(tok);
            }
        }
        if (qTokSet.isEmpty()) return new SearchResult(new PageImpl<>(List.of(), pageable, 0), Map.of());

        // 2) 점수 누적
        HashMap<Long, Integer> score = new HashMap<>(2048);
        HashMap<Long, Byte> mask = new HashMap<>(2048); // 1=TITLE,2=CATEGORY,4=MENU

        for (String qt : qTokSet) {
            addMatches(titleIndex.get(qt), score, mask, W_TITLE, (byte) 1);
            addMatches(categoryIndex.get(qt), score, mask, W_CATEGORY, (byte) 2);
            addMatches(menuIndex.get(qt), score, mask, W_MENU, (byte) 4);
        }

        if (score.isEmpty()) return new SearchResult(new PageImpl<>(List.of(), pageable, 0), Map.of());

        // 3) 정렬 (score desc, tie id desc)
        ArrayList<Long> ids = new ArrayList<>(score.keySet());
        int total = ids.size();
        ids.sort((a, b) -> {
            int sa = score.get(a);
            int sb = score.get(b);
            if (sa != sb) return Integer.compare(sb, sa);
            return Long.compare(b, a);
        });

        int page = pageable.getPageNumber(); // 1-base
        int size = pageable.getPageSize();

        int start = (page - 1) * size;
        int end = Math.min(start + size, ids.size());

        if (start >= ids.size()) {
            return new SearchResult(new PageImpl<>(List.of(), pageable, 0), Map.of());
        } else {
            ids = new ArrayList<>(ids.subList(start, end));
        }

        // 4) TopK에 대해서만 매칭정보 구성 (하이라이트/메뉴명 추출)
        HashMap<Long, SearchResult.MatchInfo> infoMap = new HashMap<>(ids.size() * 2);

        // 하이라이트는 "공백 토큰"만으로도 충분히 보기 좋음.
        // (2-gram까지 하이라이트에 쓰면 너무 과하게 칠해질 수 있음)
        List<String> highlightTokens = normalizeQueryTokensForHighlight(kwArr);

        for (long id : ids) {
            byte m = mask.getOrDefault(id, (byte) 0);
            EnumSet<SearchResult.Field> fields = EnumSet.noneOf(SearchResult.Field.class);
            if ((m & 1) != 0) fields.add(SearchResult.Field.NAME);
            if ((m & 2) != 0) fields.add(SearchResult.Field.CATEGORY);
            if ((m & 4) != 0) fields.add(SearchResult.Field.MENU);

            List<SearchResult.Range> titleRanges = List.of();
            List<SearchResult.Range> categoryRanges = List.of();
            if ((m & 1) != 0) {
                titleRanges = buildHighlightRanges(titleById.get(id), highlightTokens);
            }
            if ((m & 2) != 0) {
                categoryRanges = buildHighlightRanges(categoryById.get(id), highlightTokens);
            }

            List<String> matchedMenus = List.of();
            if ((m & 4) != 0) {
                matchedMenus = findMatchedMenus(menusById.get(id), highlightTokens);
            }

            infoMap.put(id, new SearchResult.MatchInfo(fields, titleRanges, categoryRanges, matchedMenus));
        }

        return new SearchResult(new PageImpl<>(ids, pageable, total), infoMap);
    }

    private static List<String> tokenizeQuery(String s) {
        String norm = normalize(s);
        if (norm.isEmpty()) return List.of();

        String[] parts = MULTI_SPACE.split(norm);
        List<String> out = new ArrayList<>();

        for (String p : parts) {
            if (!p.isEmpty()) {
                out.add(p);
            }
        }
        return out;
    }

    private static void addMatches(long[] postings,
                                   HashMap<Long, Integer> score,
                                   HashMap<Long, Byte> mask,
                                   int weight,
                                   byte bit) {
        if (postings == null) return;
        for (long id : postings) {
            score.merge(id, weight, Integer::sum);
            mask.put(id, (byte) (mask.getOrDefault(id, (byte) 0) | bit));
        }
    }

    /**
     * 하이라이트용 쿼리 토큰: normalize 후 공백 split만 사용(2-gram 제외)
     */
    private static List<String> normalizeQueryTokensForHighlight(String[] queryTokens) {
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

    /**
     * 원문 문자열에서 query token들이 등장하는 모든 위치를 range로 반환 (겹치면 merge)
     */
    private static List<SearchResult.Range> buildHighlightRanges(String original, List<String> tokens) {
        if (original == null || original.isEmpty() || tokens.isEmpty()) return List.of();

        String norm = normalize(original);

        ArrayList<SearchResult.Range> ranges = new ArrayList<>();
        for (String t : tokens) {
            int from = 0;
            while (true) {
                int idx = norm.indexOf(t, from);
                if (idx < 0) break;
                ranges.add(new SearchResult.Range(idx, idx + t.length()));
                from = idx + 1;
            }
        }
        if (ranges.isEmpty()) return List.of();

        // merge overlaps
        ranges.sort(Comparator.comparingInt(r -> r.start));
        ArrayList<SearchResult.Range> merged = new ArrayList<>();
        int cs = ranges.get(0).start;
        int ce = ranges.get(0).end;

        for (int i = 1; i < ranges.size(); i++) {
            SearchResult.Range r = ranges.get(i);
            if (r.start <= ce) {
                ce = Math.max(ce, r.end);
            } else {
                merged.add(new SearchResult.Range(cs, ce));
                cs = r.start;
                ce = r.end;
            }
        }
        merged.add(new SearchResult.Range(cs, ce));
        return merged;
    }

    /**
     * 메뉴 리스트에서 쿼리 토큰이 포함된 "메뉴명"을 반환
     */
    private static List<String> findMatchedMenus(List<String> menus, List<String> tokens) {
        if (menus == null || menus.isEmpty() || tokens.isEmpty()) return List.of();

        LinkedHashSet<String> matched = new LinkedHashSet<>();
        for (String menu : menus) {
            if (menu == null) continue;
            String normMenu = normalize(menu);
            boolean ok = false;
            for (String t : tokens) {
                if (t.isEmpty()) continue;
                if (normMenu.contains(t)) { ok = true; break; }
            }
            if (ok) matched.add(menu); // 원문 메뉴명 그대로
        }
        return new ArrayList<>(matched);
    }
}
