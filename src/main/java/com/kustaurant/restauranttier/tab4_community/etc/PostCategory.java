package com.kustaurant.restauranttier.tab4_community.etc;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum PostCategory {
    ALL("전체"),
    FREE("자유게시판"),
    COLUMN("칼럼게시판"),
    SUGGESTION("건의게시판");

    private final String koreanName;

    PostCategory(String koreanName) {
        this.koreanName = koreanName;
    }



    public static PostCategory fromStringToEnum(String category) {
        for (PostCategory pc : PostCategory.values()) {
            if (pc.name().equalsIgnoreCase(category)) {
                return pc;
            }
        }
        throw new IllegalArgumentException("PostCategory 파라미터가 유효하지 않습니다.");
    }
}
