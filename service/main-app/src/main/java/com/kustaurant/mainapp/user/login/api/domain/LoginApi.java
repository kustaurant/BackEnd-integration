package com.kustaurant.mainapp.user.login.api.domain;

public enum LoginApi {
    NAVER("NAVER"),
    APPLE("APPLE");

    private final String name;

    LoginApi(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
