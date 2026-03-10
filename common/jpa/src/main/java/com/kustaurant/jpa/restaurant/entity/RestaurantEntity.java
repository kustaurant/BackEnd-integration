package com.kustaurant.jpa.restaurant.entity;

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
    @Column(unique = true)
    private String restaurantUrl;
    private String restaurantImgUrl;
    private Integer visitCount;

    private String restaurantCuisine;
    private Double latitude;
    private Double longitude;
    private String partnershipInfo;

    private String status;
}
