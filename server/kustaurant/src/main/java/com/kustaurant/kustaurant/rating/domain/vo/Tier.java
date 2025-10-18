package com.kustaurant.kustaurant.rating.domain.vo;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum Tier {

    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    NONE(-1);

    private final int value;

    Tier(int value) {
        this.value = value;
    }

    public static Tier find(int tier) {
        return Arrays.stream(values())
                .filter(v -> v.getValue() == tier)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
