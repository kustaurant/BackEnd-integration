package com.kustaurant.kustaurant.restaurant.restaurant.domain;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Position {
    ALL("전체"), L1("건입~중문"), L2("중문~어대"), L3("후문"), L4("정문"), L5("구의역");

    private final String value;

    Position(String value) {
        this.value = value;
    }

    public static Position find(String value) {
        return Arrays.stream(Position.values())
                .filter(v -> v.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(value + " Position은 존재하지 않습니다."));
    }
}
