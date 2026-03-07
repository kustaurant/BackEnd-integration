package com.kustaurant.kustaurant.restaurant.search.infrastructure.engine.memory;

import com.kustaurant.kustaurant.restaurant.search.infrastructure.persistence.response.RestaurantForEngine;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class InMemorySearchEngineManager {

    // 검색용 스냅샷을 원자적으로 교체해 읽기 경로 일관성을 보장
    private final AtomicReference<Snapshot> snapshotRef = new AtomicReference<>(Snapshot.empty());

    public Snapshot snapshot() {
        return snapshotRef.get();
    }

    // 인덱스를 새로 빌드한 뒤 스냅샷으로 교체
    public void build(List<RestaurantForEngine> docs) {
        Map<String, LongListBuilder> tTmp = new HashMap<>();
        Map<String, LongListBuilder> cTmp = new HashMap<>();
        Map<String, LongListBuilder> mTmp = new HashMap<>();

        Map<Long, String> titleTmp = new HashMap<>();
        Map<Long, String> categoryTmp = new HashMap<>();
        Map<Long, List<String>> menusTmp = new HashMap<>();

        for (RestaurantForEngine d : docs) {
            titleTmp.put(d.id(), d.name());
            categoryTmp.put(d.id(), d.cuisine());
            menusTmp.put(d.id(), d.menus() == null ? List.of() : List.copyOf(d.menus()));

            for (String tok : InMemorySearchTextProcessor.tokenizeForIndex(d.name())) {
                tTmp.computeIfAbsent(tok, k -> new LongListBuilder()).add(d.id());
            }
            for (String tok : InMemorySearchTextProcessor.tokenizeForIndex(d.cuisine())) {
                cTmp.computeIfAbsent(tok, k -> new LongListBuilder()).add(d.id());
            }
            if (d.menus() != null) {
                for (String menu : d.menus()) {
                    for (String tok : InMemorySearchTextProcessor.tokenizeForIndex(menu)) {
                        mTmp.computeIfAbsent(tok, k -> new LongListBuilder()).add(d.id());
                    }
                }
            }
        }

        snapshotRef.set(new Snapshot(
                finalizeIndex(tTmp),
                finalizeIndex(cTmp),
                finalizeIndex(mTmp),
                Map.copyOf(titleTmp),
                Map.copyOf(categoryTmp),
                Map.copyOf(menusTmp)
        ));
    }

    private static Map<String, long[]> finalizeIndex(Map<String, LongListBuilder> tmp) {
        Map<String, long[]> target = new HashMap<>(tmp.size() * 2);
        for (Map.Entry<String, LongListBuilder> e : tmp.entrySet()) {
            target.put(e.getKey(), e.getValue().toSortedUniqueArray());
        }
        return Map.copyOf(target);
    }

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
            int w = 0;
            for (int i = 0; i < out.length; i++) {
                if (i == 0 || out[i] != out[i - 1]) out[w++] = out[i];
            }
            return Arrays.copyOf(out, w);
        }
    }

    public static final class Snapshot {
        private final Map<String, long[]> titleIndex;
        private final Map<String, long[]> categoryIndex;
        private final Map<String, long[]> menuIndex;

        private final Map<Long, String> titleById;
        private final Map<Long, String> categoryById;
        private final Map<Long, List<String>> menusById;

        private Snapshot(Map<String, long[]> titleIndex,
                         Map<String, long[]> categoryIndex,
                         Map<String, long[]> menuIndex,
                         Map<Long, String> titleById,
                         Map<Long, String> categoryById,
                         Map<Long, List<String>> menusById) {
            this.titleIndex = titleIndex;
            this.categoryIndex = categoryIndex;
            this.menuIndex = menuIndex;
            this.titleById = titleById;
            this.categoryById = categoryById;
            this.menusById = menusById;
        }

        static Snapshot empty() {
            return new Snapshot(Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of());
        }

        public long[] titlePostings(String token) {
            return titleIndex.get(token);
        }

        public long[] categoryPostings(String token) {
            return categoryIndex.get(token);
        }

        public long[] menuPostings(String token) {
            return menuIndex.get(token);
        }

        public String title(long id) {
            return titleById.get(id);
        }

        public String category(long id) {
            return categoryById.get(id);
        }

        public List<String> menus(long id) {
            return menusById.get(id);
        }
    }
}
