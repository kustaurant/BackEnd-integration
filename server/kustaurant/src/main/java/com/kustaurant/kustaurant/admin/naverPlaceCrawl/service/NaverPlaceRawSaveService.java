package com.kustaurant.kustaurant.admin.naverPlaceCrawl.service;

import com.kustaurant.kustaurant.admin.naverPlaceCrawl.controller.command.NaverPlaceRawCrawlResponse;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.controller.command.NaverPlaceRawMenuResponse;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.infrastructure.RestaurantCrawlRawEntity;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.infrastructure.RestaurantCrawlRawRepository;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.infrastructure.RestaurantCrawlerClient;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.infrastructure.RestaurantMenuCrawlRawEntity;
import com.kustaurant.kustaurant.admin.naverPlaceCrawl.infrastructure.RestaurantMenuRawRepository;
import com.kustaurant.naverplace.CrawlScopeType;
import com.kustaurant.naverplace.NaverPlaceCrawlResult;
import com.kustaurant.naverplace.NaverPlaceMenu;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Generated;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NaverPlaceRawSaveService {
   private final RestaurantCrawlerClient crawlerClient;
   private final RestaurantCrawlRawRepository rawRepository;
   private final RestaurantMenuRawRepository menuRawRepository;
   private final NaverPlaceUrlValidator urlValidator;

   @Transactional
   public NaverPlaceRawCrawlResponse crawlAndSave(String placeUrl) {
      this.urlValidator.validateOrThrow(placeUrl);
      NaverPlaceCrawlResult result = this.crawlerClient.crawlOne(placeUrl);
      return this.saveResult(result, CrawlScopeType.SINGLE);
   }

   @Transactional
   public NaverPlaceRawCrawlResponse saveResult(NaverPlaceCrawlResult result, CrawlScopeType crawlScope) {
      this.deleteExistingRawByPlaceIdOrUrl(result);
      RestaurantCrawlRawEntity rawEntity = (RestaurantCrawlRawEntity)this.rawRepository.save(RestaurantCrawlRawEntity.success(result.sourcePlaceId(), result.sourceUrl(), this.defaultIfBlank(result.placeName(), "UNKNOWN_PLACE"), result.category(), result.restaurantAddress(), result.phoneNumber(), result.latitude(), result.longitude(), result.imageUrl(), crawlScope));
      List<NaverPlaceRawMenuResponse> menus = this.saveMenus(rawEntity.getId(), result.menus());
      return new NaverPlaceRawCrawlResponse(rawEntity.getId(), rawEntity.getSourcePlaceId(), rawEntity.getSourceUrl(), rawEntity.getPlaceName(), rawEntity.getCategory(), rawEntity.getRestaurantAddress(), rawEntity.getPhoneNumber(), rawEntity.getLatitude(), rawEntity.getLongitude(), rawEntity.getImageUrl(), menus.size(), menus);
   }

   private List saveMenus(Long rawId, List menus) {
      if (menus != null && !menus.isEmpty()) {
         List<RestaurantMenuCrawlRawEntity> entities = menus.stream().map((menu) -> RestaurantMenuCrawlRawEntity.of(rawId, this.defaultIfBlank(menu.menuName(), "UNKNOWN_MENU"), menu.menuPrice(), menu.menuImageUrl())).toList();
         this.menuRawRepository.saveAll(entities);
         return menus.stream().map((menu) -> new NaverPlaceRawMenuResponse(menu.menuName(), menu.menuPrice(), menu.menuImageUrl())).toList();
      } else {
         return List.of();
      }
   }

   private String defaultIfBlank(String value, String fallback) {
      return value != null && !value.isBlank() ? value : fallback;
   }

   private void deleteExistingRawByPlaceIdOrUrl(NaverPlaceCrawlResult result) {
      List<RestaurantCrawlRawEntity> targets = new ArrayList();
      if (result.sourcePlaceId() != null && !result.sourcePlaceId().isBlank()) {
         Optional var10000 = this.rawRepository.findBySourcePlaceId(result.sourcePlaceId());
         Objects.requireNonNull(targets);
         var10000.ifPresent(targets::add);
      }

      if (result.sourceUrl() != null && !result.sourceUrl().isBlank()) {
         Optional var5 = this.rawRepository.findBySourceUrl(result.sourceUrl()).filter((entity) -> targets.stream().noneMatch((t) -> t.getId().equals(entity.getId())));
         Objects.requireNonNull(targets);
         var5.ifPresent(targets::add);
      }

      for(RestaurantCrawlRawEntity target : targets) {
         this.menuRawRepository.deleteByRestaurantRawId(target.getId());
         this.rawRepository.delete(target);
      }

      if (!targets.isEmpty()) {
         this.rawRepository.flush();
      }

   }

   @Generated
   public NaverPlaceRawSaveService(final RestaurantCrawlerClient crawlerClient, final RestaurantCrawlRawRepository rawRepository, final RestaurantMenuRawRepository menuRawRepository, final NaverPlaceUrlValidator urlValidator) {
      this.crawlerClient = crawlerClient;
      this.rawRepository = rawRepository;
      this.menuRawRepository = menuRawRepository;
      this.urlValidator = urlValidator;
   }
}
