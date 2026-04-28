package com.kustaurant.kustaurant.admin.RestaurantCrawl.service;

import com.kustaurant.kustaurant.admin.RestaurantCrawl.controller.command.RestaurantRawExistenceResponse;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure.RestaurantCrawlRawEntity;
import com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure.RestaurantCrawlRawRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantRawQueryService {
   private final RestaurantCrawlRawRepository rawRepository;

   @Transactional(readOnly = true)
   public RestaurantRawExistenceResponse getRawExistence(String sourcePlaceId) {
      return findExistingByPlaceId(sourcePlaceId)
              .map(entity -> new RestaurantRawExistenceResponse(
                      true,
                      entity.getSourcePlaceId(),
                      entity.getCrawlScope(),
                      entity.getCrawlScope() == null ? null : entity.getCrawlScope().getDescription(),
                      entity.getPlaceName()
              ))
              .orElseGet(() -> new RestaurantRawExistenceResponse(false, sourcePlaceId, null, null, null));
   }

   @Transactional(readOnly = true)
   public Optional<RestaurantCrawlRawEntity> findExistingByPlaceId(String sourcePlaceId) {
      if (sourcePlaceId == null || sourcePlaceId.isBlank()) {
         return Optional.empty();
      }
      return rawRepository.findBySourcePlaceId(sourcePlaceId);
   }
}
