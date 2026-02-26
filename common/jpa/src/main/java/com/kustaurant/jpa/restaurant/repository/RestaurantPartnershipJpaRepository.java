package com.kustaurant.jpa.restaurant.repository;

import com.kustaurant.jpa.restaurant.entity.RestaurantPartnershipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantPartnershipJpaRepository extends JpaRepository<RestaurantPartnershipEntity, Long> {
    @Query("select p.postUrl from RestaurantPartnershipEntity p where p.postUrl in :postUrls")
    List<String> findExistingPostUrls(@Param("postUrls") List<String> postUrls);
}
