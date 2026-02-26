package com.kustaurant.kustaurant.admin.crawl.service;

import com.kustaurant.jpa.restaurant.IGPost;
import com.kustaurant.jpa.restaurant.enums.PartnershipTarget;
import com.kustaurant.kustaurant.admin.crawl.infrastructure.IGCrawlerClient;
import com.kustaurant.kustaurant.admin.crawl.infrastructure.IgCrawlRawEntity;
import com.kustaurant.kustaurant.admin.crawl.infrastructure.IgCrawlRawRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class IgCrawlRawSaveService {
    private final IGCrawlerClient crawlerClient;
    private final IgCrawlRawRepository rawRepo;

    public int crawlAndReplaceAll(String accountName, PartnershipTarget target) {
        List<IGPost> posts = crawlerClient.crawl(accountName);
        if (posts == null || posts.isEmpty()) return 0;
        return replaceAll(accountName, target, posts);
    }

    @Transactional
    protected int replaceAll(String accountName, PartnershipTarget target, List<IGPost> posts) {

        // 1) 기존 raw 전부 삭제
        rawRepo.deleteBySourceAccount(accountName);

        // 2) URL 기준 중복 제거
        Map<String, IGPost> unique = new LinkedHashMap<>();
        for (IGPost p : posts) {
            if (p == null) continue;

            String postUrl = p.postUrl();
            if (postUrl == null || postUrl.isBlank()) continue;

            unique.putIfAbsent(postUrl, p);
        }

        if (unique.isEmpty()) return 0;

        // 3) 삽입
        List<IgCrawlRawEntity> toSave = new ArrayList<>();
        for (IGPost p : unique.values()) {
            String postUrl = p.postUrl();

            String code = extractShortCode(postUrl);
            if (code == null || code.isBlank()) continue;

            toSave.add(IgCrawlRawEntity.of(
                    accountName,
                    code,
                    postUrl,
                    p.restaurantName(),
                    p.benefit(),
                    p.location(),
                    p.phoneNumber(),
                    target
            ));
        }

        rawRepo.saveAll(toSave);
        return toSave.size();
    }

    private String extractShortCode(String postUrl) {
        String[] keys = {"/p/", "/reel/"};
        for (String k : keys) {
            int idx = postUrl.indexOf(k);
            if (idx >= 0) {
                String tail = postUrl.substring(idx + k.length());
                int slash = tail.indexOf('/');
                return (slash >= 0) ? tail.substring(0, slash) : tail;
            }
        }
        return null;
    }
}
