package com.kustaurant.kustaurant.restaurant.restaurant.infrastructure.repository;

import com.kustaurant.restaurant.entity.RestaurantEntity;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RestaurantJpaRepository extends JpaRepository<RestaurantEntity, Long> {

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    Optional<RestaurantEntity> findByRestaurantIdAndStatus(Long id, String status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("update RestaurantEntity r set r.visitCount = r.visitCount + 1 where r.restaurantId = :id")
    int incrementViews(@Param("id") long id);


    // 레스토랑 제휴 크롤링 전용
    @Query("""
        select r.restaurantId as id, r.restaurantTel as phoneNumber
        from RestaurantEntity r
        where r.restaurantTel in :phoneNumbers
    """)
    List<RestaurantPhoneProjection> findIdsByPhoneNumbers(@Param("phoneNumbers") Collection<String> phoneNumbers);

    interface RestaurantPhoneProjection {
        Long getId();
        String getPhoneNumber();
    }
}
