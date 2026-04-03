package com.kustaurant.kustaurant.admin.crawl.infrastructure;

import com.kustaurant.jpa.common.entity.BaseTimeEntity;
import com.kustaurant.jpa.restaurant.enums.PartnershipTarget;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ig_crawl_raw")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IgCrawlRawEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_account", nullable = false, length = 32)
    private String sourceAccount;

    @Column(name = "short_code", nullable = false, length = 32)
    private String shortCode;

    @Column(name = "post_url", nullable = false, length = 256)
    private String postUrl;

    @Column(name = "restaurant_name", nullable = false, length = 64)
    private String restaurantName;

    @Column(name = "benefit", nullable = false, length = 256)
    private String benefit;

    @Column(name = "location", nullable = false, length = 128)
    private String location;

    @Column(name = "phone_number", nullable = true, length = 16)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "target", nullable = false, length = 16)
    private PartnershipTarget target;

    private IgCrawlRawEntity(
            String sourceAccount,
            String shortCode,
            String postUrl,
            String restaurantName,
            String benefit,
            String location,
            String phoneNumber,
            PartnershipTarget target
    ) {
        this.sourceAccount = sourceAccount;
        this.shortCode = shortCode;
        this.postUrl = postUrl;
        this.restaurantName = restaurantName;
        this.benefit = benefit;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.target = target;
    }

    public static IgCrawlRawEntity of(
            String sourceAccount,
            String shortCode,
            String postUrl,
            String restaurantName,
            String benefit,
            String location,
            String phoneNumber,
            PartnershipTarget target
    ) {
        return new IgCrawlRawEntity(sourceAccount, shortCode, postUrl, restaurantName, benefit, location, phoneNumber, target);
    }
}
