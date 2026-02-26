package com.kustaurant.jpa.restaurant.entity;

import com.kustaurant.jpa.common.entity.BaseTimeEntity;
import com.kustaurant.jpa.restaurant.enums.MatchStatus;
import com.kustaurant.jpa.restaurant.enums.PartnershipTarget;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "restaurant_partnership")
public class RestaurantPartnershipEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT
    private Long id;

    @Column(name = "restaurant_id")
    private Long restaurantId; // NULL 허용

    @Column(name = "partner_name", nullable = false, length = 255)
    private String partnerName;

    @Column(name = "benefit", nullable = false, length = 255)
    private String benefit;

    @Column(name = "location_text", nullable = false, length = 255)
    private String locationText;

    @Column(name = "contact_phone", length = 32)
    private String contactPhone;

    @Column(name = "source_account", nullable = false, length = 64)
    private String sourceAccount;

    @Column(name = "post_url", nullable = false, length = 255)
    private String postUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_status", nullable = false, length = 32)
    private MatchStatus matchStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "target", nullable = false, length = 32)
    private PartnershipTarget target;
}
