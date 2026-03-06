package com.kustaurant.kustaurant.restaurant.search.infrastructure.engine.memory;

import com.kustaurant.kustaurant.restaurant.search.infrastructure.engine.RestaurantSearchEngine;
import com.kustaurant.kustaurant.restaurant.search.infrastructure.engine.response.SearchResult;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.kustaurant.kustaurant.restaurant.search.infrastructure.engine.memory.InMemorySearchEngineManager.*;

@Component
public class InMemorySearchEngine implements RestaurantSearchEngine {

    // ====== 가중치 ======
    private static final int W_TITLE = 100;
    private static final int W_CATEGORY = 30;
    private static final int W_MENU = 10;

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

        // 4) 매칭정보 구성 (하이라이트/메뉴명 추출)
        HashMap<Long, SearchResult.MatchInfo> infoMap = new HashMap<>(ids.size() * 2);

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
