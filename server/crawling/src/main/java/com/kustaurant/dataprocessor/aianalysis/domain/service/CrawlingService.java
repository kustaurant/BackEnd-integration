package com.kustaurant.dataprocessor.aianalysis.domain.service;

import com.kustaurant.dataprocessor.aianalysis.domain.model.Review;
import com.kustaurant.dataprocessor.aianalysis.domain.service.port.ReviewCrawler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrawlingService {

    private final ReviewCrawler reviewCrawler;

    public List<Review> crawl(String url) {
        return reviewCrawler.crawlReviews(url).stream()
                .filter(Review::isValid)
                .map(Review::new)
                .toList();
    }
}
