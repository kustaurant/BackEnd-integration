package com.kustaurant.kustaurant.restaurant.partnership;

import com.kustaurant.jpa.restaurant.entity.RestaurantPartnershipEntity;
import com.kustaurant.jpa.restaurant.enums.PartnershipTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface RestaurantPartnershipJpaRepository extends JpaRepository<RestaurantPartnershipEntity, Long> {
    @Query("""
        select p.postUrl
        from RestaurantPartnershipEntity p
        where p.postUrl in :urls
    """)
    List<String> findExistingPostUrls(Collection<String> urls);

    long countByTarget(PartnershipTarget target);

    @Modifying
    @Query("""
        delete from RestaurantPartnershipEntity p
        where p.target = :target
    """)
    int deleteAllByTarget(@Param("target") PartnershipTarget target);
}
