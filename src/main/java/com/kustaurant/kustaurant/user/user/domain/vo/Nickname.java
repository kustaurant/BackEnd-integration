package com.kustaurant.kustaurant.user.user.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class Nickname {
    @Column(name = "nickname", nullable = false)
    private String value;

    protected Nickname() {}

    public Nickname(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("닉네임은 비어있을 수 없습니다.");
        }
        if (value.length() < 2 || value.length() > 10) {
            throw new IllegalArgumentException("닉네임은 2자 이상 10지 이하여야 합니다.");
        }
        this.value = value;
    }

    public static Nickname fromEmail(String email) {
        //이메일의 @ 앞부분을 최대 0글자까지 잘라 닉네임으로 사용
        String prefix = email.substring(0, email.indexOf('@'));
        if (prefix.length() > 10) {prefix = prefix.substring(0, 10);}
        return new Nickname(prefix);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Nickname nickname)) return false;
        return value.equals(nickname.value);
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
