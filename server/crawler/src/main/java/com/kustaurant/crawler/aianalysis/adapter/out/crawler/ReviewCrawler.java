package com.kustaurant.crawler.aianalysis.adapter.out.crawler;

import java.util.List;

public interface ReviewCrawler {

    List<String> crawlReviews(String url);
}
