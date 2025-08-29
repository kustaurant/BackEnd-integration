package com.kustaurant.kustaurant.post.post.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum PostCategory {
    ALL("전체"),
    FREE("자유게시판"),
    COLUMN("칼럼게시판"),
    SUGGESTION("건의게시판");

    private final String koreanCategory;

    PostCategory(String koreanCategory) {
        this.koreanCategory = koreanCategory;
    }
    @JsonCreator
    public static PostCategory from(Object raw) {
        if (raw == null) return null;
        String s = raw.toString().trim();
        for (PostCategory c : values()) {
            if (c.name().equalsIgnoreCase(s) || c.koreanCategory.equals(s)) return c;
        }
        throw new IllegalArgumentException("Unknown PostCategory: " + raw);
    }
    @JsonValue
    public String toJson() {
        return koreanCategory;
    }
}
