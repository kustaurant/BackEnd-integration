package com.kustaurant.kustaurant.post.post.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

public final class ImageExtractor {
    private ImageExtractor() {}
    public static List<String> extract(String html) {
        List<String> result = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        for (Element img : doc.select("img")) {
            String url = img.attr("src");
            if (url != null && !url.isBlank()) {
                result.add(url);
            }
        }
        return result;
    }
}
