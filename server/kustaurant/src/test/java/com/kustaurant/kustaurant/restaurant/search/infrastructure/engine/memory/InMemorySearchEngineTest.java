package com.kustaurant.kustaurant.restaurant.search.infrastructure.engine.memory;

import com.kustaurant.kustaurant.restaurant.search.infrastructure.engine.response.SearchResult;
import com.kustaurant.kustaurant.restaurant.search.infrastructure.persistence.response.RestaurantForEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemorySearchEngineTest {

    private InMemorySearchEngine searchEngine;

    @BeforeEach
    void setUp() {
        InMemorySearchEngineManager searchEngineManager = new InMemorySearchEngineManager();
        searchEngine = new InMemorySearchEngine(searchEngineManager);
        searchEngineManager.build(sampleDocs());
    }

    @Test
    @DisplayName("검색어가 null 또는 빈 배열이면 빈 결과를 반환한다.")
    void shouldReturnEmptyResultWhenKeywordsAreNullOrEmpty() {
        SearchResult nullResult = searchEngine.searchRestaurantIds(null, PageRequest.of(0, 10));
        SearchResult emptyResult = searchEngine.searchRestaurantIds(new String[0], PageRequest.of(0, 10));

        assertTrue(nullResult.ids.getContent().isEmpty());
        assertTrue(nullResult.matchInfo.isEmpty());
        assertTrue(emptyResult.ids.getContent().isEmpty());
        assertTrue(emptyResult.matchInfo.isEmpty());
    }

    @Test
    @DisplayName("여러 검색어 중 하나만 매칭되어도 해당 식당은 결과에 포함된다.")
    void shouldIncludeRestaurantWhenAnyKeywordMatches() {
        SearchResult result = searchEngine.searchRestaurantIds(
                new String[]{"no-match-token", "bibim"},
                PageRequest.of(0, 10)
        );

        assertEquals(List.of(1L), result.ids.getContent());
    }

    @Test
    @DisplayName("여러 검색어가 동시에 매칭되면 점수가 누적되어 더 높은 순위로 정렬된다.")
    void shouldRankHigherWhenMultipleKeywordsMatchSameRestaurant() {
        SearchResult result = searchEngine.searchRestaurantIds(
                new String[]{"k", "korean", "bibim"},
                PageRequest.of(0, 10)
        );

        assertFalse(result.ids.getContent().isEmpty());
        assertEquals(1L, result.ids.getContent().get(0));
        assertTrue(result.ids.getContent().contains(3L));
        assertTrue(result.ids.getContent().contains(4L));
    }

    @Test
    @DisplayName("이름과 카테고리와 메뉴에 동시에 매칭되는 식당은 필드 정보와 하이라이트와 메뉴 매칭 결과를 함께 반환한다.")
    void shouldReturnFieldMaskHighlightsAndMatchedMenusWhenMatchedAcrossAllFields() {
        SearchResult result = searchEngine.searchRestaurantIds(
                new String[]{"korean", "bibim"},
                PageRequest.of(0, 10)
        );

        SearchResult.MatchInfo info = result.matchInfo.get(1L);
        assertNotNull(info);
        assertTrue(info.fields.contains(SearchResult.Field.NAME));
        assertTrue(info.fields.contains(SearchResult.Field.CATEGORY));
        assertTrue(info.fields.contains(SearchResult.Field.MENU));
        assertFalse(info.titleHighlights.isEmpty());
        assertFalse(info.categoryHighlights.isEmpty());
        assertEquals(List.of("bibimbap"), info.matchedMenus);
    }

    @Test
    @DisplayName("제목과 카테고리에 대한 하이라이트 범위는 매칭된 위치의 시작과 끝 인덱스를 정확히 반환한다.")
    void shouldReturnExactHighlightRangesForTitleAndCategory() {
        SearchResult result = searchEngine.searchRestaurantIds(new String[]{"korean"}, PageRequest.of(0, 10));

        SearchResult.MatchInfo info = result.matchInfo.get(1L);
        assertNotNull(info);
        assertEquals(1, info.titleHighlights.size());
        assertEquals(2, info.titleHighlights.get(0).start);
        assertEquals(8, info.titleHighlights.get(0).end);
        assertEquals(1, info.categoryHighlights.size());
        assertEquals(0, info.categoryHighlights.get(0).start);
        assertEquals(6, info.categoryHighlights.get(0).end);
    }

    @Test
    @DisplayName("하이라이트 토큰의 범위가 겹치면 하나의 범위로 병합되어 반환된다.")
    void shouldMergeOverlappingHighlightRanges() {
        SearchResult result = searchEngine.searchRestaurantIds(
                new String[]{"korean", "ore"},
                PageRequest.of(0, 10)
        );

        SearchResult.MatchInfo info = result.matchInfo.get(1L);
        assertNotNull(info);
        assertEquals(1, info.categoryHighlights.size());
        assertEquals(0, info.categoryHighlights.get(0).start);
        assertEquals(6, info.categoryHighlights.get(0).end);
    }

    @Test
    @DisplayName("동일한 점수인 경우에는 식당 ID가 큰 순서로 정렬된다.")
    void shouldSortByIdDescWhenScoresAreTied() {
        SearchResult result = searchEngine.searchRestaurantIds(new String[]{"korean"}, PageRequest.of(0, 10));

        assertEquals(List.of(4L, 1L), result.ids.getContent());
    }

    @Test
    @DisplayName("대소문자와 특수문자가 달라도 정규화되어 매칭되는 식당들이 결과에 포함된다.")
    void shouldNormalizeCaseAndPunctuationInQuery() {
        SearchResult result = searchEngine.searchRestaurantIds(new String[]{"K-BBQ!!!"}, PageRequest.of(0, 10));

        assertTrue(result.ids.getContent().contains(3L));
        assertTrue(result.ids.getContent().contains(1L));
        assertEquals(3L, result.ids.getContent().get(0));
    }

    @Test
    @DisplayName("결과 수가 페이지 크기보다 크면 요청한 페이지 크기만큼만 반환하고 전체 개수는 유지한다.")
    void shouldReturnOnlyRequestedPageSizeWhenResultsExceedPageSize() {
        SearchResult result = searchEngine.searchRestaurantIds(new String[]{"korean"}, PageRequest.of(0, 1));

        assertEquals(1, result.ids.getContent().size());
        assertEquals(2, result.ids.getTotalElements());
    }

    @Test
    @DisplayName("부분 문자열 검색으로 메뉴가 매칭되면 중복 없이 원본 메뉴명이 반환된다.")
    void shouldFindBySubstringAndReturnDistinctOriginalMenuNames() {
        SearchResult result = searchEngine.searchRestaurantIds(new String[]{"bibim"}, PageRequest.of(0, 10));

        assertEquals(List.of(1L), result.ids.getContent());
        SearchResult.MatchInfo info = result.matchInfo.get(1L);
        assertNotNull(info);
        assertEquals(List.of("bibimbap"), info.matchedMenus);
    }

    private static List<RestaurantForEngine> sampleDocs() {
        return List.of(
                new RestaurantForEngine(1L, "K Korean House", "korean", List.of("bibimbap", "kimchi stew")),
                new RestaurantForEngine(2L, "A Pizza", "western", List.of("pepperoni pizza", "pasta")),
                new RestaurantForEngine(3L, "K-BBQ House", "meat", List.of("samgyeopsal", "doenjang stew")),
                new RestaurantForEngine(4L, "Korean Table", "korean", List.of("bulgogi"))
        );
    }
}