package com.kustaurant.kustaurant.post.post.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ImageExtractorTest {

    @Test
    @DisplayName("기본적인 <img> src를 순서대로 추출한다")
    void extractsBasicSrcsInOrder() {
        // g
        String html = "<html><body>"
                + "<img src=\"/a.png\">"
                + "<img src=\"https://ex.com/b.jpg\">"
                + "</body></html>";
        // w
        List<String> urls = ImageExtractor.extract(html);
        // t
        assertThat(urls).containsExactly("/a.png", "https://ex.com/b.jpg");
    }

    @Test
    @DisplayName("src가 없거나 비어 있는 <img>는 무시한다")
    void ignoresEmptyOrMissingSrc() {
        // g
        String html = "<img><img src=\"\"> <img src=\" \"> <img src=\"/ok.png\">";
        // w
        List<String> urls = ImageExtractor.extract(html);
        // t
        assertThat(urls).containsExactly("/ok.png");
    }

    @Test
    @DisplayName("중복 src도 그대로 유지한다(정책) · 입력 순서 유지")
    void keepsDuplicatesAndOrder() {
        // g
        String html = "<img src=\"/same.png\"><img src=\"/same.png\">";
        // w
        List<String> urls = ImageExtractor.extract(html);
        // t
        assertThat(urls).containsExactly("/same.png", "/same.png");
    }

    @Test
    @DisplayName("상대경로 · data URL · 쿼리스트링도 추출 대상이다")
    void supportsRelativeDataUrlAndQueryParams() {
        // g
        String html = "<img src=\"./rel/a.png\">"
                + "<img src=\"data:image/png;base64,AAA\">"
                + "<img src=\"/c.png?ver=1\">";
        // w
        List<String> urls = ImageExtractor.extract(html);
        // t
        assertThat(urls).containsExactly("./rel/a.png", "data:image/png;base64,AAA", "/c.png?ver=1");
    }

    @Test
    @DisplayName("빈 문자열이나 공백만 주면 빈 리스트를 반환한다")
    void blankStringReturnsEmptyList() {
        // g
        String html1 = "";
        String html2 = "   ";
        // w
        List<String> urls1 = ImageExtractor.extract(html1);
        List<String> urls2 = ImageExtractor.extract(html2);
        // t
        assertThat(urls1).isEmpty();
        assertThat(urls2).isEmpty();
    }


}