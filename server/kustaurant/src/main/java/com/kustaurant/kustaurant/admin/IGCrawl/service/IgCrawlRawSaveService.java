package com.kustaurant.kustaurant.admin.IGCrawl.service;

import com.kustaurant.kustaurant.admin.IGCrawl.controller.command.IgRawSaveResult;
import com.kustaurant.kustaurant.admin.IGCrawl.infrastructure.IGCrawlerClient;
import com.kustaurant.kustaurant.admin.IGCrawl.infrastructure.IgCrawlRawEntity;
import com.kustaurant.kustaurant.admin.IGCrawl.infrastructure.IgCrawlRawRepository;
import com.kustaurant.restaurant.IGPost;
import com.kustaurant.restaurant.enums.PartnershipTarget;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class IgCrawlRawSaveService {
    private final IGCrawlerClient crawlerClient;
    private final IgCrawlRawRepository rawRepo;

    public IgRawSaveResult crawlAndSaveOnlyNew(String accountName, PartnershipTarget target) {
        List<IGPost> posts = crawlerClient.crawl(accountName, target);
        if (posts == null || posts.isEmpty()) return new IgRawSaveResult(0, 0);

        int savedCnt = saveOnlyNew(accountName, target, posts);
        return new IgRawSaveResult(posts.size(), savedCnt);
    }

    @Transactional
    protected int saveOnlyNew(String accountName, PartnershipTarget target, List<IGPost> posts) {
        // Remove duplicates inside one crawl response by shortcode.
        Map<String, IGPost> uniqueByShortCode = new LinkedHashMap<>();
        for (IGPost post : posts) {
            if (post == null) continue;

            String postUrl = post.postUrl();
            if (postUrl == null || postUrl.isBlank()) continue;

            String shortCode = extractShortCode(postUrl);
            if (shortCode == null || shortCode.isBlank()) continue;

            uniqueByShortCode.putIfAbsent(shortCode, post);
        }

        if (uniqueByShortCode.isEmpty()) return 0;

        Set<String> existingShortCodes = new HashSet<>(
                rawRepo.findExistingShortCodes(accountName, uniqueByShortCode.keySet())
        );

        List<IgCrawlRawEntity> toSave = new ArrayList<>();
        for (Map.Entry<String, IGPost> entry : uniqueByShortCode.entrySet()) {
            String shortCode = entry.getKey();
            if (existingShortCodes.contains(shortCode)) continue;

            IGPost post = entry.getValue();
            toSave.add(IgCrawlRawEntity.of(
                    accountName,
                    shortCode,
                    post.postUrl(),
                    post.restaurantName(),
                    post.benefit(),
                    post.location(),
                    post.phoneNumber(),
                    target
            ));
        }

        if (toSave.isEmpty()) return 0;

        rawRepo.saveAll(toSave);
        return toSave.size();
    }

    private String extractShortCode(String postUrl) {
        String[] keys = {"/p/", "/reel/"};
        for (String key : keys) {
            int idx = postUrl.indexOf(key);
            if (idx >= 0) {
                String tail = postUrl.substring(idx + key.length());
                int slash = tail.indexOf('/');
                return (slash >= 0) ? tail.substring(0, slash) : tail;
            }
        }
        return null;
    }
}
