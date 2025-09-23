package com.kustaurant.dataprocessor.aianalysis.domain.service.port;

import java.util.List;

public interface ReviewCrawler {

    List<String> crawlReviews(String url);
}
