package com.kustaurant.kustaurant.restaurant.presentation.enums;

import lombok.Getter;

@Getter
public enum EnumSortComment {
    POPULAR("popular"), LATEST("latest");

    private final String value;

    EnumSortComment(String value) {
        this.value = value;
    }

}
