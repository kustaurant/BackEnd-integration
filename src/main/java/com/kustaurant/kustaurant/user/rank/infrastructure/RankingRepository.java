package com.kustaurant.kustaurant.user.rank.infrastructure;

import com.kustaurant.kustaurant.user.user.infrastructure.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface RankingRepository extends JpaRepository<UserEntity, Long> {

    // 누적 Top100 (동률 포함)
    @Query(value = """
        WITH r AS (
          SELECT
            us.user_id        AS userId,
            us.rated_rest_cnt AS evaluationCount,
            RANK() OVER (ORDER BY us.rated_rest_cnt DESC, us.user_id ASC) AS userRank
          FROM user_stats us
          JOIN users_tbl u ON u.user_id = us.user_id
          WHERE us.rated_rest_cnt > 0 AND u.status = 'ACTIVE'
        )
        SELECT
          u.user_id          AS userId,
          u.nickname         AS nickname,
          r.evaluationCount  AS evaluationCount,
          r.userRank             AS userRank
        FROM r
        JOIN users_tbl u ON u.user_id = r.userId
        WHERE r.userRank <= 100
        ORDER BY r.evaluationCount DESC, r.userId ASC
        """, nativeQuery = true)
    List<UserRankProjection> findTop100CumulativeRows();

    // 시즌 Top100 (동률 포함)
    @Query(value = """
        WITH s AS (
          SELECT e.user_id AS userId, COUNT(*) AS evaluationCount
          FROM evaluation e
          WHERE e.created_at >= :start AND e.created_at < :end
          GROUP BY e.user_id
        ),
        r AS (
          SELECT
            s.userId           AS userId,
            s.evaluationCount  AS evaluationCount,
            RANK() OVER (ORDER BY s.evaluationCount DESC, s.userId ASC) AS userRank
          FROM s
            JOIN users_tbl u ON u.user_id = s.userId
            WHERE u.status = 'ACTIVE'
        )
        SELECT
          u.user_id          AS userId,
          u.nickname         AS nickname,
          r.evaluationCount  AS evaluationCount,
          r.userRank         AS userRank
        FROM r
        JOIN users_tbl u ON u.user_id = r.userId
        WHERE r.userRank <= 100
        ORDER BY r.evaluationCount DESC, r.userId ASC
        """, nativeQuery = true)
    List<UserRankProjection> findTop100SeasonalRows(@Param("start") Timestamp start, @Param("end")   Timestamp end);

    // 내 누적 랭크 (Top100 안에 들면만)
    @Query(value = """
        WITH r AS (
          SELECT
            us.user_id        AS userId,
            us.rated_rest_cnt AS evaluationCount,
            RANK() OVER (ORDER BY us.rated_rest_cnt DESC, us.user_id ASC) AS userRank
          FROM user_stats us
          WHERE us.rated_rest_cnt > 0
        )
        SELECT
          u.user_id          AS userId,
          u.nickname         AS nickname,
          r.evaluationCount  AS evaluationCount,
          r.userRank         AS userRank
        FROM r
        JOIN users_tbl u ON u.user_id = r.userId
        WHERE r.userId = :uid
        """, nativeQuery = true)
    Optional<UserRankProjection> findMyCumulativeRow(@Param("uid") Long uid);

    // 내 시즌 랭크 (Top100 안에 들면만)
    @Query(value = """
        WITH s AS (
          SELECT e.user_id AS userId, COUNT(*) AS evaluationCount
          FROM evaluation e
          WHERE e.created_at >= :start AND e.created_at < :end
          GROUP BY e.user_id
        ),
        r AS (
          SELECT
            s.userId           AS userId,
            s.evaluationCount  AS evaluationCount,
            RANK() OVER (ORDER BY s.evaluationCount DESC, s.userId ASC) AS userRank
          FROM s
        )
        SELECT
          u.user_id          AS userId,
          u.nickname         AS nickname,
          r.evaluationCount  AS evaluationCount,
          r.userRank         AS userRank
        FROM r
        JOIN users_tbl u ON u.user_id = r.userId
        WHERE r.userId = :uid
        """, nativeQuery = true)
    Optional<UserRankProjection> findMySeasonalRow(@Param("uid") Long uid, @Param("start") Timestamp start, @Param("end") Timestamp end);
}
