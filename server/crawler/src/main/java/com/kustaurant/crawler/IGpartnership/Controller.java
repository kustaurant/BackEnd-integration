package com.kustaurant.crawler.IGpartnership;

import com.kustaurant.jpa.restaurant.IGPost;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ig")
public class Controller {
    private final Crawler crawler;

    @PostMapping("/crawl")
    public List<IGPost> crawl(@RequestBody CrawlRequest req) {
        return crawler.crawl(req.userName());
    }
}
