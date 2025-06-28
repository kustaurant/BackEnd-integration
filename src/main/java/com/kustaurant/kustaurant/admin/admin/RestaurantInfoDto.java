package com.kustaurant.kustaurant.admin.admin;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RestaurantInfoDto {
    private String name;
    private String type; // 네이버에 등록된 카테고리
    private String cuisine; // 쿠스토랑에서 분류한 카테고리
    private String address;
    private String tel;
    private String url;
    private String imgUrl;
    private String position;
    private String partnershipInfo;
    private Double latitude;
    private Double longitude;
}
