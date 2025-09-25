package com.kustaurant.kustaurant.user.rank.service;

import com.kustaurant.kustaurant.user.rank.controller.response.UserRankResponse;
import com.kustaurant.kustaurant.user.rank.domain.RankingSortOption;
import com.kustaurant.kustaurant.user.rank.domain.SeasonRange;
import com.kustaurant.kustaurant.user.rank.infrastructure.RankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RankingService {
    private final RankingRepository repo;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    public List<UserRankResponse> getTop100(RankingSortOption sort) {
        return switch (sort) {
            case CUMULATIVE -> repo.findTop100CumulativeRows().stream().map(UserRankResponse::from).toList();

            case SEASONAL -> {
                var range = SeasonRange.current(KST);
                yield repo.findTop100SeasonalRows(ts(range.startInclusive()), ts(range.endExclusive()))
                        .stream().map(UserRankResponse::from).toList();
            }
        };
    }

    public Optional<UserRankResponse> getMyRank(RankingSortOption sort, Long userId) {
        return switch (sort) {
            case CUMULATIVE -> repo.findMyCumulativeRow(userId).stream().findFirst().map(UserRankResponse::from);

            case SEASONAL -> {
                var range = SeasonRange.current(KST);
                yield repo.findMySeasonalRow(userId, ts(range.startInclusive()), ts(range.endExclusive()))
                        .stream().findFirst().map(UserRankResponse::from);
            }
        };
    }
    private static Timestamp ts(ZonedDateTime zdt) {
        return Timestamp.from(zdt.toInstant());
    }
}
