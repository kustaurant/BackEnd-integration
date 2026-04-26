package com.kustaurant.restaurant.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kustaurant.jpa.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.*;

import org.hibernate.annotations.DynamicUpdate;

@Getter
@Entity
@DynamicUpdate
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIgnoreProperties({"createdAt", "updatedAt"})
@Table(name = "restaurant")
public class RestaurantEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long restaurantId;

    private String restaurantName;
    private String restaurantType;
    private String restaurantPosition;
    private String restaurantAddress;
    private String restaurantTel;
    @Column(unique = true, nullable = false, length = 64)
    private String placeId;
    private String restaurantImgUrl;
    private Integer visitCount;

    private String restaurantCuisine;
    private Double latitude;
    private Double longitude;
    private String partnershipInfo;

    private String status;
    private String contentHash;
    private String menuHash;

    public void applyRaw(
            String restaurantName,
            String restaurantType,
            String restaurantPosition,
            String restaurantAddress,
            String restaurantTel,
            String restaurantImgUrl,
            String restaurantCuisine,
            Double latitude,
            Double longitude
    ) {
        this.restaurantName = restaurantName;
        this.restaurantType = restaurantType;
        this.restaurantPosition = restaurantPosition;
        this.restaurantAddress = restaurantAddress;
        this.restaurantTel = restaurantTel;
        this.restaurantImgUrl = restaurantImgUrl;
        this.restaurantCuisine = restaurantCuisine;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void updateHashes(String contentHash, String menuHash) {
        this.contentHash = contentHash;
        this.menuHash = menuHash;
    }

    public void markInactive() {
        this.status = "INACTIVE";
    }

    public void markActive() {
        this.status = "ACTIVE";
    }
}
