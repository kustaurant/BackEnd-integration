package com.kustaurant.kustaurant.restaurant.search.infrastructure.engine.response;

import org.springframework.data.domain.Page;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class SearchResult {

    public final Page<Long> ids; // score desc, tie id desc
    public final Map<Long, MatchInfo> matchInfo;

    public SearchResult(Page<Long> ids, Map<Long, MatchInfo> matchInfo) {
        this.ids = ids;
        this.matchInfo = matchInfo;
    }

    public static class MatchInfo {
        public final EnumSet<Field> fields;
        public final List<Range> titleHighlights;
        public final List<Range> categoryHighlights;
        public final List<String> matchedMenus;

        public MatchInfo(EnumSet<Field> fields,
                         List<Range> titleHighlights,
                         List<Range> categoryHighlights,
                         List<String> matchedMenus) {
            this.fields = fields;
            this.titleHighlights = titleHighlights;
            this.categoryHighlights = categoryHighlights;
            this.matchedMenus = matchedMenus;
        }
    }

    public enum Field { NAME, CATEGORY, MENU }

    public static class Range {
        public final int start; // inclusive
        public final int end;   // exclusive
        public Range(int start, int end) { this.start = start; this.end = end; }
    }
}
