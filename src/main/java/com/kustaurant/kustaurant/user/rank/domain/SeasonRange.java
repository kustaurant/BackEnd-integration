package com.kustaurant.kustaurant.user.rank.domain;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public record SeasonRange (ZonedDateTime startInclusive, ZonedDateTime endExclusive){
    public static SeasonRange of(int seasonYear, int term, ZoneId zone) {
        return switch (term) {
            case 1 -> new SeasonRange(
                    LocalDate.of(seasonYear, 3, 1).atStartOfDay(zone),
                    LocalDate.of(seasonYear, 9, 1).atStartOfDay(zone)
            );
            case 2 -> new SeasonRange(
                    LocalDate.of(seasonYear, 9, 1).atStartOfDay(zone),
                    LocalDate.of(seasonYear + 1, 3, 1).atStartOfDay(zone)
            );
            default -> throw new IllegalArgumentException("term must be 1 or 2");
        };
    }

    public static int currentTerm(LocalDate today) {
        int m = today.getMonthValue();
        return (m >= 3 && m <= 8) ? 1 : 2;
    }

    // 1~2월은 직전년도 T2에 속함
    public static int currentSeasonYear(LocalDate today) {
        int y = today.getYear();
        int m = today.getMonthValue();
        return (m >= 3) ? y : (y - 1);
    }

    public static SeasonRange current(ZoneId zone) {
        LocalDate today = LocalDate.now(zone);
        return of(currentSeasonYear(today), currentTerm(today), zone);
    }
}
