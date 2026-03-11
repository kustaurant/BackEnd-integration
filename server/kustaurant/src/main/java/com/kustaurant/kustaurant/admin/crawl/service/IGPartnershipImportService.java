package com.kustaurant.kustaurant.admin.crawl.service;

import com.kustaurant.jpa.restaurant.entity.RestaurantPartnershipEntity;
import com.kustaurant.jpa.restaurant.enums.MatchStatus;
import com.kustaurant.jpa.restaurant.enums.PartnershipTarget;
import com.kustaurant.kustaurant.admin.crawl.infrastructure.IgCrawlRawEntity;
import com.kustaurant.kustaurant.admin.crawl.infrastructure.IgCrawlRawRepository;
import com.kustaurant.kustaurant.restaurant.partnership.RestaurantPartnershipJpaRepository;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class IGPartnershipImportService {

    private final IgCrawlRawRepository rawRepo;
    private final RestaurantRepository restaurantRepo;
    private final RestaurantPartnershipJpaRepository partnershipRepo;
    private final PhoneNumberNormalizer phoneNumberNormalizer;

    @Transactional
    public int importFromRaw(String accountName, PartnershipTarget target) {

        // 1. raw 조회
        List<IgCrawlRawEntity> raws = rawRepo.findBySourceAccount(accountName);
        if (raws.isEmpty()) {
            return 0;
        }

        // 2. 기존 partnership postUrl 조회 (중복 skip)
        List<String> postUrls = raws.stream()
                .map(IgCrawlRawEntity::getPostUrl)
                .filter(url -> url != null && !url.isBlank())
                .distinct()
                .toList();

        Set<String> existingPostUrls = new HashSet<>(partnershipRepo.findExistingPostUrls(postUrls));

        // 3. raw 전화번호 정규화
        Set<String> normalizedPhones = new HashSet<>();
        Map<Long, String> rawIdToNormalizedPhone = new HashMap<>();

        for (IgCrawlRawEntity raw : raws) {
            String normalizedPhone = phoneNumberNormalizer.normalize(raw.getPhoneNumber());

            if (normalizedPhone == null || normalizedPhone.isBlank()) {
                continue;
            }

            normalizedPhones.add(normalizedPhone);
            rawIdToNormalizedPhone.put(raw.getId(), normalizedPhone);
        }

        // 4. restaurant 전화번호 매칭
        Map<String, Long> phoneToRestaurantId = new HashMap<>();

        if (!normalizedPhones.isEmpty()) {
            List<RestaurantRepository.RestaurantPhoneMatch> matches =
                    restaurantRepo.findIdsByPhoneNumbers(normalizedPhones);

            for (RestaurantRepository.RestaurantPhoneMatch match : matches) {
                phoneToRestaurantId.put(match.phoneNumber(), match.id());
            }
        }

        // 5. partnership 생성
        List<RestaurantPartnershipEntity> toSave = new ArrayList<>();

        for (IgCrawlRawEntity raw : raws) {
            String postUrl = raw.getPostUrl();

            // 이미 저장된 postUrl이면 skip
            if (postUrl == null || postUrl.isBlank() || existingPostUrls.contains(postUrl)) {
                continue;
            }

            String normalizedPhone = rawIdToNormalizedPhone.get(raw.getId());

            Long restaurantId = null;
            MatchStatus matchStatus = MatchStatus.UNMATCHED;

            if (normalizedPhone != null) {
                restaurantId = phoneToRestaurantId.get(normalizedPhone);

                if (restaurantId != null) {
                    matchStatus = MatchStatus.MATCHED;
                }
            }

            toSave.add(RestaurantPartnershipEntity.builder()
                    .restaurantId(restaurantId)
                    .partnerName(raw.getRestaurantName())
                    .benefit(raw.getBenefit())
                    .locationText(raw.getLocation())
                    .contactPhone(normalizedPhone)
                    .sourceAccount(accountName)
                    .postUrl(postUrl)
                    .matchStatus(matchStatus)
                    .target(target)
                    .build());
        }

        partnershipRepo.saveAll(toSave);
        return toSave.size();
    }
}