package com.kustaurant.kustaurant.v1.common;

import com.kustaurant.kustaurant.post.post.domain.enums.PostCategory;
import org.mapstruct.Named;

public final class MapStructConverters {
    private MapStructConverters() {}

    @Named("longToIntegerExact")
    public static Integer longToIntegerExact(Long v) {
        if (v == null) return null;
        return Math.toIntExact(v);
    }

    @Named("longToIntegerExact")
    public static Integer longToIntegerExactP(long v) {
        return Math.toIntExact(v);
    }

    @Named("postCategoryToString")
    public static String postCategoryToString(PostCategory c) {
        return c == null ? null : c.getKoreanCategory();
    }
}