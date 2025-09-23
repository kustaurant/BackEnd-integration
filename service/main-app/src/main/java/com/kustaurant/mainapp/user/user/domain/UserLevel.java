package com.kustaurant.mainapp.user.user.domain;

import java.util.Arrays;

public enum UserLevel {
    NEWBIE (0, 30,  "level1icon"),
    SILVER (31, 60, "level2icon"),
    GOLD   (61, Integer.MAX_VALUE, "level3icon");

    private final int min, max;
    private final String key;
    UserLevel(int min, int max, String key) { this.min = min; this.max = max; this.key = key; }

    public static UserLevel of(int cnt) {
        return Arrays.stream(values())
                .filter(lv -> cnt >= lv.min && cnt <= lv.max)
                .findFirst().orElse(NEWBIE);
    }
    public static UserLevel of(long cnt) {
        return Arrays.stream(values())
                .filter(lv -> cnt >= lv.min && cnt <= lv.max)
                .findFirst().orElse(NEWBIE);
    }

    /* 확장자 고정—여기선 .svg 하나만 */
    public String iconPath() {
        return "https://kustaurant.s3.ap-northeast-2.amazonaws.com/common/%s.svg".formatted(key);
    }
}
