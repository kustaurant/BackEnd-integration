package com.kustaurant.kustaurant.admin.RestaurantCrawl.service;

import com.kustaurant.kustaurant.admin.RestaurantCrawl.controller.command.RestaurantCrawlResponse;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.controller.command.RestaurantRawMenuResponse;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure.RestaurantCrawlRawEntity;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure.RestaurantCrawlRawRepository;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure.RestaurantCrawlerClient;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure.RestaurantMenuCrawlRawEntity;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure.RestaurantMenuRawRepository;
import com.kustaurant.map.ZoneType;
import com.kustaurant.restaurantSync.RestaurantRaw;
import com.kustaurant.restaurantSync.RestaurantRawMenu;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantRawSaveService {
   private static final int IMAGE_URL_MAX_LENGTH = 512;

   private final RestaurantCrawlerClient crawlerClient;
   private final RestaurantCrawlRawRepository rawRepository;
   private final RestaurantMenuRawRepository menuRawRepository;
   private final PlatformTransactionManager transactionManager;

   @Transactional
   public RestaurantCrawlResponse crawlAndSave(String placeId) {
      RestaurantRaw result = crawlerClient.crawlOne(placeId);
      ZoneType zoneType = result.crawlScope() == null ? ZoneType.OUT_OF_ZONE : result.crawlScope();
      return saveResult(result, zoneType);
   }

   @Transactional
   public RestaurantCrawlResponse saveResult(RestaurantRaw result, ZoneType crawlScope) {
      deleteExistingRawByPlaceIdOrUrl(result);

      ZoneType zoneType = crawlScope != null ? crawlScope : (result.crawlScope() == null ? ZoneType.OUT_OF_ZONE : result.crawlScope());
      String normalizedImageUrl = normalizeImageUrl(result.imageUrl());
      RestaurantCrawlRawEntity rawEntity = rawRepository.save(
              RestaurantCrawlRawEntity.success(
                      result.sourcePlaceId(),
                      result.sourceUrl(),
                      defaultIfBlank(result.placeName(), "UNKNOWN_PLACE"),
                      result.category(),
                      result.restaurantAddress(),
                      result.phoneNumber(),
                      result.latitude(),
                      result.longitude(),
                      normalizedImageUrl,
                      zoneType
              )
      );

      List<RestaurantRawMenuResponse> menus = saveMenus(rawEntity.getId(), result.menus());
      log.info(
              "단건 크롤 저장 완료. sourcePlaceId={}, placeName={}, lat={}, lng={}, zoneType={}, zoneDescription={}",
              rawEntity.getSourcePlaceId(),
              rawEntity.getPlaceName(),
              rawEntity.getLatitude(),
              rawEntity.getLongitude(),
              zoneType,
              zoneType.getDescription()
      );

      return new RestaurantCrawlResponse(
              rawEntity.getId(),
              rawEntity.getSourcePlaceId(),
              rawEntity.getSourceUrl(),
              rawEntity.getPlaceName(),
              rawEntity.getCategory(),
              rawEntity.getRestaurantAddress(),
              rawEntity.getPhoneNumber(),
              rawEntity.getLatitude(),
              rawEntity.getLongitude(),
              rawEntity.getImageUrl(),
              zoneType,
              zoneType.getDescription(),
              menus.size(),
              menus
      );
   }

   public BatchSaveResult saveResultsBatch(List<RestaurantRaw> results, ZoneType crawlScope) {
      if (results == null || results.isEmpty()) {
         return new BatchSaveResult(0, 0, List.of());
      }

      int savedCount = 0;
      int failedCount = 0;
      Set<String> failedPlaceIds = new LinkedHashSet<>();
      TransactionTemplate requiresNewTx = new TransactionTemplate(transactionManager);
      requiresNewTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

      for (RestaurantRaw result : results) {
         String placeId = result == null ? null : result.sourcePlaceId();
         try {
            if (result == null) {
               throw new IllegalArgumentException("naver place crawl result is null");
            }
            requiresNewTx.executeWithoutResult(status -> saveResult(result, crawlScope));
            savedCount++;
         } catch (Exception e) {
            failedCount++;
            failedPlaceIds.add(placeId == null ? "UNKNOWN" : placeId);
            log.warn("raw 배치 저장 실패. scope={}, placeId={}", crawlScope, placeId, e);
         }
      }

      return new BatchSaveResult(savedCount, failedCount, List.copyOf(failedPlaceIds));
   }

   private List<RestaurantRawMenuResponse> saveMenus(Long rawId, List<RestaurantRawMenu> menus) {
      if (menus == null || menus.isEmpty()) {
         return List.of();
      }

      List<RestaurantMenuCrawlRawEntity> entities = menus.stream()
              .map(menu -> RestaurantMenuCrawlRawEntity.of(
                      rawId,
                      defaultIfBlank(menu.menuName(), "UNKNOWN_MENU"),
                      menu.menuPrice(),
                      menu.menuImageUrl()
              ))
              .toList();
      menuRawRepository.saveAll(entities);

      return menus.stream()
              .map(menu -> new RestaurantRawMenuResponse(
                      menu.menuName(),
                      menu.menuPrice(),
                      menu.menuImageUrl()
              ))
              .toList();
   }

   private void deleteExistingRawByPlaceIdOrUrl(RestaurantRaw result) {
      List<RestaurantCrawlRawEntity> targets = new ArrayList<>();

      if (result.sourcePlaceId() != null && !result.sourcePlaceId().isBlank()) {
         rawRepository.findBySourcePlaceId(result.sourcePlaceId()).ifPresent(targets::add);
      }

      if (result.sourceUrl() != null && !result.sourceUrl().isBlank()) {
         rawRepository.findBySourceUrl(result.sourceUrl())
                 .filter(entity -> targets.stream().noneMatch(t -> t.getId().equals(entity.getId())))
                 .ifPresent(targets::add);
      }

      for (RestaurantCrawlRawEntity target : targets) {
         menuRawRepository.deleteByRestaurantRawId(target.getId());
         rawRepository.delete(target);
      }

      if (!targets.isEmpty()) {
         rawRepository.flush();
      }
   }

   private String defaultIfBlank(String value, String fallback) {
      return (value == null || value.isBlank()) ? fallback : value;
   }

   private String normalizeImageUrl(String imageUrl) {
      if (imageUrl == null) {
         return null;
      }
      return imageUrl.length() > IMAGE_URL_MAX_LENGTH ? null : imageUrl;
   }

   public record BatchSaveResult(
           int savedCount,
           int failedCount,
           List<String> failedPlaceIds
   ) {}
}
