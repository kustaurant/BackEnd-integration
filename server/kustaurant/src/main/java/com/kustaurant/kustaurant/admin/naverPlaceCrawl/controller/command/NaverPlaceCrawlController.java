package com.kustaurant.kustaurant.admin.naverPlaceCrawl.controller.command;

import com.kustaurant.kustaurant.admin.naverPlaceCrawl.service.NaverPlaceAnalyzeService;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.service.NaverPlaceRawSaveService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/crawl")
public class NaverPlaceCrawlController {

    private final NaverPlaceRawSaveService naverPlaceRawSaveService;
    private final NaverPlaceAnalyzeService naverPlaceAnalyzeService;

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @PostMapping("/naver-place/raw")
    public NaverPlaceRawCrawlResponse crawlNaverPlaceRaw(
            @Valid @RequestBody NaverPlaceRawCrawlRequest request
    ) {
        return naverPlaceRawSaveService.crawlAndSave(request.placeUrl());
    }

    @PreAuthorize("isAuthenticated() and hasRole('ROLE_ADMIN')")
    @PostMapping("/naver-place/analyze")
    public NaverPlaceRawCrawlResponse analyzeNaverPlaceRaw(
            @Valid @RequestBody NaverPlaceRawCrawlRequest request
    ) {
        return naverPlaceAnalyzeService.analyze(request.placeUrl());
    }
}
