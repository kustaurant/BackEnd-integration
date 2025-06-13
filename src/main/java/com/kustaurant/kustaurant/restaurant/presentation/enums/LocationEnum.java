package com.kustaurant.kustaurant.restaurant.presentation.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum LocationEnum {
    ALL("전체"), L1("건입~중문"), L2("중문~어대"), L3("후문"), L4("정문"), L5("구의역");

    private final String value;

    LocationEnum(String value) {
        this.value = value;
    }

    public static LocationEnum fromValue(String value) {
        return Arrays.stream(LocationEnum.values())
                .filter(e -> e.value.equals(value))
                .findFirst()
                .orElse(null);
    }
}
