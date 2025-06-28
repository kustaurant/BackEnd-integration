package com.kustaurant.kustaurant.common.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public final class TimeAgoUtil {
    private TimeAgoUtil() { }

    /** 한국어 기준 “3시간 전” / “15초 전” 반환 */
    public static String toKor(LocalDateTime past) {

        LocalDateTime now = LocalDateTime.now();

        long years   = ChronoUnit.YEARS  .between(past, now);
        if (years   > 0) return years   + "년 전";

        long months  = ChronoUnit.MONTHS .between(past, now);
        if (months  > 0) return months  + "달 전";

        long days    = ChronoUnit.DAYS   .between(past, now);
        if (days    > 0) return days    + "일 전";

        long hours   = ChronoUnit.HOURS  .between(past, now);
        if (hours   > 0) return hours   + "시간 전";

        long minutes = ChronoUnit.MINUTES.between(past, now);
        if (minutes > 0) return minutes + "분 전";

        long seconds = ChronoUnit.SECONDS.between(past, now);
        return seconds + "초 전";
    }
}
