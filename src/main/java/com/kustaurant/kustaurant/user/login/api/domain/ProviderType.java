package com.kustaurant.kustaurant.user.login.api.domain;

public enum ProviderType {
    NAVER("NAVER"),
    APPLE("APPLE");

    private final String name;

    ProviderType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
