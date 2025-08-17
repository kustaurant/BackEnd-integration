package com.kustaurant.kustaurant.user.rank.infrastructure;

import com.kustaurant.kustaurant.user.rank.controller.response.UserRank;
import com.kustaurant.kustaurant.user.rank.domain.SeasonRange;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class RankingRepository {
    @PersistenceContext
    private EntityManager em;

    // 누적 Top100 (users + user_stats)
    public List<UserRank> findTop100Cumulative() {
        String sql = """
            SELECT u.id AS user_id,
                   u.nickname,
                   u.icon_url,
                   us.eval_count,
                   RANK() OVER (ORDER BY us.eval_count DESC, u.id ASC) AS rnk
            FROM users u
            JOIN user_stats us ON us.user_id = u.id
            WHERE us.eval_count > 0
            ORDER BY us.eval_count DESC, u.id ASC
            LIMIT 100
        """;
        return mapRows(em.createNativeQuery(sql).getResultList());
    }

    // 시즌 Top100 (현재 시즌 범위로만: 외부에서 Range 보냄)
    public List<UserRank> findTop100Seasonal(SeasonRange range) {
        String sql = """
            WITH s AS (
              SELECT e.user_id, COUNT(*) AS season_count
              FROM evaluation e
              WHERE e.created_at >= :start AND e.created_at < :end
              GROUP BY e.user_id
            )
            SELECT u.id AS user_id,
                   u.nickname,
                   u.icon_url,
                   s.season_count AS eval_count,
                   RANK() OVER (ORDER BY s.season_count DESC, u.id ASC) AS rnk
            FROM users u
            JOIN s ON s.user_id = u.id
            ORDER BY s.season_count DESC, u.id ASC
            LIMIT 100
        """;
        return mapRows(
                em.createNativeQuery(sql)
                        .setParameter("start", toTs(range.startInclusive()))
                        .setParameter("end",   toTs(range.endExclusive()))
                        .getResultList()
        );
    }

    // 내 누적 랭크
    public Optional<UserRank> findMyCumulativeRank(Long userId) {
        String sql = """
            WITH ranked AS (
              SELECT u.id AS user_id,
                     u.nickname,
                     u.icon_url,
                     us.eval_count,
                     RANK() OVER (ORDER BY us.eval_count DESC, u.id ASC) AS rnk
              FROM users u
              JOIN user_stats us ON us.user_id = u.id
              WHERE us.eval_count > 0
            )
            SELECT user_id, nickname, icon_url, eval_count, rnk
            FROM ranked
            WHERE user_id = :uid
        """;
        var rows = em.createNativeQuery(sql)
                .setParameter("uid", userId)
                .getResultList();
        return rows.isEmpty() ? Optional.empty() : Optional.of(mapOne(rows.get(0)));
    }

    // 내 시즌 랭크
    public Optional<UserRank> findMySeasonalRank(Long userId, SeasonRange range) {
        String sql = """
            WITH s AS (
              SELECT e.user_id, COUNT(*) AS season_count
              FROM evaluation e
              WHERE e.created_at >= :start AND e.created_at < :end
              GROUP BY e.user_id
            ),
            ranked AS (
              SELECT u.id AS user_id,
                     u.nickname,
                     u.icon_url,
                     s.season_count AS eval_count,
                     RANK() OVER (ORDER BY s.season_count DESC, u.id ASC) AS rnk
              FROM users u
              JOIN s ON s.user_id = u.id
            )
            SELECT user_id, nickname, icon_url, eval_count, rnk
            FROM ranked
            WHERE user_id = :uid
        """;
        var rows = em.createNativeQuery(sql)
                .setParameter("uid", userId)
                .setParameter("start", toTs(range.startInclusive()))
                .setParameter("end",   toTs(range.endExclusive()))
                .getResultList();
        return rows.isEmpty() ? Optional.empty() : Optional.of(mapOne(rows.get(0)));
    }


    // ---------- 매핑 ----------
    private List<UserRank> mapRows(List<?> rows) {
        List<UserRank> list = new ArrayList<>();
        for (Object r : rows) list.add(mapOne(r));
        return list;
    }

    private UserRank mapOne(Object row) {
        Object[] a = (Object[]) row;
        Long   userId = ((Number) a[0]).longValue();
        String nick   = (String) a[1];
        String icon   = (String) a[2];
        int    cnt    = ((Number) a[3]).intValue();
        int    rank   = ((Number) a[4]).intValue();
        return new UserRank(userId, nick, icon, cnt, rank);
    }

    private Timestamp toTs(ZonedDateTime zdt) {
        return Timestamp.from(zdt.toInstant());
    }
}
