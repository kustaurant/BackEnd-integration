package com.kustaurant.mainapp.global.auth.jwt;

public enum TokenType {
    ACCESS("AT"),
    REFRESH("RT"),
    YOLO("YO"); // 테스트용

    private final String type;
    TokenType(String type) { this.type = type; }
    public String getType() { return type; }
}
