package com.kustaurant.kustaurant.user.user.domain.enums;

import lombok.Getter;

@Getter
public enum UserRole {
    GUEST("ROLE_GUEST"),
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    OWNER("ROLE_OWNER");

    private final String value;

    UserRole(String value){
        this.value = value;
    }

    public static UserRole from(String value){
        for (UserRole role : values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("알 수 없는 권한: " + value);
    }
}
