package com.kustaurant.restauranttier.tab3_tier.etc;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum CuisineEnum {
    ALL("전체"), KO("한식"), JA("일식"), CH("중식"), WE("양식"),
    AS("아시안"), ME("고기"), CK("치킨"), SE("해산물"), HP("햄버거/피자"),
    BS("분식"), PU("술집"), CA("카페/디저트"), BA("베이커리"),
    SA("샐러드"), JH("제휴업체");

    private final String value;

    CuisineEnum(String value) {
        this.value = value;
    }

    public static CuisineEnum fromValue(String value) {
        return Arrays.stream(CuisineEnum.values())
                .filter(v -> v.value.equals(value))
                .findFirst()
                .orElse(null);
    }
}
