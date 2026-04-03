package com.kustaurant.kustaurant.admin.crawl.service;

import com.kustaurant.jpa.restaurant.entity.RestaurantPartnershipEntity;
import com.kustaurant.jpa.restaurant.enums.MatchStatus;
import com.kustaurant.jpa.restaurant.enums.PartnershipTarget;
import com.kustaurant.kustaurant.admin.crawl.controller.command.IgImportResult;
import com.kustaurant.kustaurant.admin.crawl.dto.NameMatchDecision;
import com.kustaurant.kustaurant.admin.crawl.dto.RestaurantPhoneMatch;
import com.kustaurant.kustaurant.admin.crawl.infrastructure.IgCrawlRawEntity;
import com.kustaurant.kustaurant.admin.crawl.infrastructure.IgCrawlRawRepository;
import com.kustaurant.kustaurant.admin.crawl.service.matching.PhoneNumberNormalizer;
import com.kustaurant.kustaurant.admin.crawl.service.matching.RestaurantNameMatchService;
import com.kustaurant.kustaurant.restaurant.partnership.RestaurantCandidateRepository;
import com.kustaurant.kustaurant.restaurant.partnership.RestaurantPartnershipJpaRepository;
import com.kustaurant.kustaurant.restaurant.restaurant.service.port.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class IGPartnershipImportService {

    private final IgCrawlRawRepository rawRepo;
    private final RestaurantRepository restaurantRepo;
    private final RestaurantCandidateRepository candidateRepo;
    private final RestaurantPartnershipJpaRepository partnershipRepo;
    private final PhoneNumberNormalizer phoneNumberNormalizer;
    private final RestaurantNameMatchService restaurantNameMatchService;

    @Transactional
    public IgImportResult importFromRaw(String accountName, PartnershipTarget target) {

        // 1. raw 조회
        List<IgCrawlRawEntity> raws = rawRepo.findBySourceAccount(accountName);
        if (raws.isEmpty()) return new IgImportResult(0, 0);

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

            if (normalizedPhone == null || normalizedPhone.isBlank()) continue;

            normalizedPhones.add(normalizedPhone);
            rawIdToNormalizedPhone.put(raw.getId(), normalizedPhone);
        }

        // 4. restaurant 전화번호 매칭
        Map<String, Long> phoneToRestaurantId = new HashMap<>();

        if (!normalizedPhones.isEmpty()) {
            List<RestaurantPhoneMatch> matches = candidateRepo.findIdsByPhoneNumbers(normalizedPhones);

            for (RestaurantPhoneMatch match : matches) {
                phoneToRestaurantId.put(match.phoneNumber(), match.id());
            }
        }

        // 5. partnership 생성
        List<RestaurantPartnershipEntity> toSave = new ArrayList<>();

        int matchedCount = 0;
        int unmatchedCount = 0;

        for (IgCrawlRawEntity raw : raws) {
            String postUrl = raw.getPostUrl();

            // 이미 저장된 postUrl이면 skip
            if (postUrl == null || postUrl.isBlank() || existingPostUrls.contains(postUrl)) continue;

            String normalizedPhone = rawIdToNormalizedPhone.get(raw.getId());

            Long restaurantId = null;
            MatchStatus matchStatus = MatchStatus.UNMATCHED;

            // 1. 전화번호 exact match
            if (normalizedPhone != null) {
                restaurantId = phoneToRestaurantId.get(normalizedPhone);
                if (restaurantId != null) {
                    matchStatus = MatchStatus.MATCHED;
                    log.info("전화번호 매칭 성공 rawId={}, restaurantId={}", raw.getId(), restaurantId);
                }
            }

            // 2. 전화번호 실패 시 이름+주소 매칭
            if (restaurantId == null) {
                NameMatchDecision decision = restaurantNameMatchService.match(raw);

                if (decision.isMatched()) {
                    restaurantId = decision.restaurantId();
                    matchStatus = MatchStatus.MATCHED;
                    log.info("이름/주소 매칭 성공 rawId={}, restaurantId={}, reason={}",
                            raw.getId(), restaurantId, decision.reason());
                } else {
                    log.info("이름/주소 매칭 실패 rawId={}, restaurantId={}, reason={}",
                            raw.getId(), restaurantId, decision.reason());
                }
            }

            if (matchStatus == MatchStatus.MATCHED) matchedCount++;
            else unmatchedCount++;

            toSave.add(RestaurantPartnershipEntity.builder()
                    .restaurantId(restaurantId)
                    .restaurantName(raw.getRestaurantName())
                    .benefit(raw.getBenefit())
                    .locationText(raw.getLocation())
                    .contactPhone(normalizedPhone)
                    .sourceAccount(accountName)
                    .postUrl(postUrl)
                    .matchStatus(matchStatus)
                    .target(target)
                    .build());
        }

        try {
            partnershipRepo.saveAll(toSave);
        } catch (DataIntegrityViolationException e) {
            log.warn("duplicate postUrl ignored");
        }

        return new IgImportResult(matchedCount, unmatchedCount);
    }
}