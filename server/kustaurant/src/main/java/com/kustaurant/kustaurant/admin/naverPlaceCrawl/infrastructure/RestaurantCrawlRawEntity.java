package com.kustaurant.kustaurant.admin.naverPlaceCrawl.infrastructure;

import com.kustaurant.jpa.common.entity.BaseTimeEntity;
import com.kustaurant.naverplace.CrawlScopeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "restaurant_crawl_raw")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestaurantCrawlRawEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_place_id", nullable = false, length = 64)
    private String sourcePlaceId;

    @Column(name = "source_url", nullable = false, length = 255)
    private String sourceUrl;

    @Column(name = "place_name", nullable = false, length = 128)
    private String placeName;

    @Column(name = "category", length = 128)
    private String category;

    @Column(name = "restaurant_address", length = 255)
    private String restaurantAddress;

    @Column(name = "phone_number", length = 32)
    private String phoneNumber;

    private Double latitude;

    private Double longitude;

    @Column(name = "image_url", length = 512)
    private String imageUrl;

    @Column(name = "crawl_scope", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private CrawlScopeType crawlScope;

    @Column(name = "crawl_status", nullable = false, length = 32)
    private String crawlStatus;

    @Column(name = "crawl_error_message", length = 500)
    private String crawlErrorMessage;

    private RestaurantCrawlRawEntity(
            String sourcePlaceId,
            String sourceUrl,
            String placeName,
            String category,
            String restaurantAddress,
            String phoneNumber,
            Double latitude,
            Double longitude,
            String imageUrl,
            CrawlScopeType crawlScope,
            String crawlStatus,
            String crawlErrorMessage
    ) {
        this.sourcePlaceId = sourcePlaceId;
        this.sourceUrl = sourceUrl;
        this.placeName = placeName;
        this.category = category;
        this.restaurantAddress = restaurantAddress;
        this.phoneNumber = phoneNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.crawlScope = crawlScope;
        this.crawlStatus = crawlStatus;
        this.crawlErrorMessage = crawlErrorMessage;
    }

    public static RestaurantCrawlRawEntity success(
            String sourcePlaceId,
            String sourceUrl,
            String placeName,
            String category,
            String restaurantAddress,
            String phoneNumber,
            Double latitude,
            Double longitude,
            String imageUrl,
            CrawlScopeType crawlScope
    ) {
        return new RestaurantCrawlRawEntity(
                sourcePlaceId,
                sourceUrl,
                placeName,
                category,
                restaurantAddress,
                phoneNumber,
                latitude,
                longitude,
                imageUrl,
                crawlScope,
                "SUCCESS",
                null
        );
    }
}
