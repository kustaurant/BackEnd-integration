package com.kustaurant.mainapp.user.rank.domain;

public enum RankingSortOption {
    SEASONAL, // 현재 시즌(3/1~9/1 또는 9/1~다음해 3/1) 기준
    CUMULATIVE // 누적 (user_stats.eval_count 기준)
}
