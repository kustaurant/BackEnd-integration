package com.kustaurant.kustaurant.admin.RestaurantCrawl.infrastructure;

import com.kustaurant.jpa.common.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "restaurant_menu_crawl_raw")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestaurantMenuCrawlRawEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "restaurant_raw_id", nullable = false)
    private Long restaurantRawId;

    @Column(name = "menu_name", nullable = false, length = 128)
    private String menuName;

    @Column(name = "menu_price", length = 64)
    private String menuPrice;

    @Column(name = "menu_image_url", length = 512)
    private String menuImageUrl;

    private RestaurantMenuCrawlRawEntity(
            Long restaurantRawId,
            String menuName,
            String menuPrice,
            String menuImageUrl
    ) {
        this.restaurantRawId = restaurantRawId;
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.menuImageUrl = menuImageUrl;
    }

    public static RestaurantMenuCrawlRawEntity of(
            Long restaurantRawId,
            String menuName,
            String menuPrice,
            String menuImageUrl
    ) {
        return new RestaurantMenuCrawlRawEntity(
                restaurantRawId,
                menuName,
                menuPrice,
                menuImageUrl
        );
    }
}
