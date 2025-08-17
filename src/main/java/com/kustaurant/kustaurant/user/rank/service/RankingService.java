package com.kustaurant.kustaurant.user.rank.service;

import com.kustaurant.kustaurant.user.rank.controller.response.UserRank;
import com.kustaurant.kustaurant.user.rank.domain.RankingSortOption;
import com.kustaurant.kustaurant.user.rank.domain.SeasonRange;
import com.kustaurant.kustaurant.user.rank.infrastructure.RankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankingService {
    private final RankingRepository repo;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    public List<UserRank> getTop100(RankingSortOption sort) {
        return switch (sort) {
            case CUMULATIVE -> repo.findTop100Cumulative();
            case SEASONAL   -> repo.findTop100Seasonal(SeasonRange.current(KST));
        };
    }

    public Optional<UserRank> getMyRank(RankingSortOption sort, Long userId) {
        return switch (sort) {
            case CUMULATIVE -> repo.findMyCumulativeRank(userId);
            case SEASONAL   -> repo.findMySeasonalRank(userId, SeasonRange.current(KST));
        };
    }
}
