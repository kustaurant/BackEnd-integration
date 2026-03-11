package com.kustaurant.kustaurant.restaurant.partnership;

import com.kustaurant.jpa.restaurant.entity.RestaurantPartnershipEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface RestaurantPartnershipJpaRepository extends JpaRepository<RestaurantPartnershipEntity, Long> {
    @Query("""
        select p.postUrl
        from RestaurantPartnershipEntity p
        where p.postUrl in :urls
    """)
    List<String> findExistingPostUrls(Collection<String> urls);
}
