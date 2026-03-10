package com.kustaurant.crawler.aianalysis.service.port;

import com.kustaurant.crawler.aianalysis.adapter.in.scheduler.dto.CrawlingReviewReq;

public interface CrawlReviewService {

    void crawlReviews(CrawlingReviewReq req);
}
