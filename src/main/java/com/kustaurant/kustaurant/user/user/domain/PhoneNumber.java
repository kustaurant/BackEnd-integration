package com.kustaurant.kustaurant.user.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class PhoneNumber {
    @Column(name = "phone_number", unique = true)
    private String value;

    protected PhoneNumber() {}

    public PhoneNumber(String value) {
        if (!value.matches("\\d{11}")) {
            throw new IllegalArgumentException("전화번호는 숫자로만 11자리여야 합니다.('-'제외)");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhoneNumber that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
