package com.kustaurant.kustaurant.admin.crawl.service;

import com.kustaurant.jpa.restaurant.IGPost;
import com.kustaurant.jpa.restaurant.entity.RestaurantPartnershipEntity;
import com.kustaurant.jpa.restaurant.enums.MatchStatus;
import com.kustaurant.jpa.restaurant.enums.PartnershipTarget;
import com.kustaurant.jpa.restaurant.repository.RestaurantPartnershipJpaRepository;
import com.kustaurant.kustaurant.admin.crawl.infrastructure.IGCrawlerClient;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class IGPartnershipImportService {
    private final IGCrawlerClient crawlerClient;
    private final RestaurantRepository restaurantRepo;
    private final RestaurantPartnershipJpaRepository partnershipRepo;
    private final PhoneNumberNormalizer phoneNumberNormalizer;

    @Transactional
    public int importFromIG(String username, PartnershipTarget target) {
        List<IGPost> posts = crawlerClient.crawl(username);
        List<String> postUrls = posts.stream()
                .map(IGPost::postUrl)
                .filter(u->u!=null && !u.isBlank())
                .distinct()
                .toList();

        Set<String> existing = new HashSet<>(partnershipRepo.findExistingPostUrls(postUrls));
        List<RestaurantPartnershipEntity> toSave = new ArrayList<>();

        for (IGPost post : posts) {
            String postUrl = post.postUrl();
            if (postUrl == null || postUrl.isBlank() || existing.contains(postUrl)) continue;

            String normalizedPhone = phoneNumberNormalizer.normalize(post.phoneNumber());

            Long restaurantId = null;
            if (normalizedPhone != null && !normalizedPhone.isBlank()) {
                restaurantId = restaurantRepo.findIdByPhoneNumber(normalizedPhone).orElse(null);
            }

            MatchStatus status = (restaurantId != null) ? MatchStatus.MATCHED : MatchStatus.UNMATCHED;

            toSave.add(RestaurantPartnershipEntity.builder()
                    .restaurantId(restaurantId)
                    .partnerName(post.restaurantName())
                    .benefit(post.benefit())
                    .locationText(post.location())
                    .contactPhone(normalizedPhone)
                    .sourceAccount(username)
                    .postUrl(postUrl)
                    .matchStatus(status)
                    .target(target)
                    .build());
        }

        partnershipRepo.saveAll(toSave);
        return toSave.size();
    }


}
