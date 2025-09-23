-- created by skb
-- user stats tbl 테이블 생성 및 집계해서 데이터 세팅 해주는 마이그레이션 입니다.
-- saved_rest_cnt
-- rated_rest_cnt
-- comm_post_cnt
-- comm_comment_cnt
-- comm_saved_post_cnt
-- 모두 5개의 집계정보가 있습니다.

-- 1. user stats tbl 일괄 생성
INSERT INTO user_stats_tbl (
    user_id,
    saved_rest_cnt,
    rated_rest_cnt,
    comm_post_cnt,
    comm_comment_cnt,
    comm_saved_post_cnt
)
SELECT user_id, 0, 0, 0, 0, 0
FROM users_tbl;

-- 2. saved_rest_cnt 집계 정보 세팅
UPDATE user_stats_tbl ust
    JOIN (
    SELECT user_id, COUNT(*) AS cnt
    FROM restaurant_favorite_tbl
    WHERE status = 'ACTIVE'
    GROUP BY user_id
    ) fav ON ust.user_id = fav.user_id
    SET ust.saved_rest_cnt = fav.cnt;

-- 3. rated_rest_cnt 집계 정보 세팅
UPDATE user_stats_tbl ust
    JOIN (
    SELECT user_id, COUNT(*) AS cnt
    FROM evaluations_tbl
    WHERE status = 'ACTIVE'
    GROUP BY user_id
    ) ev ON ust.user_id = ev.user_id
    SET ust.rated_rest_cnt = ev.cnt;

-- 4. comm_post_cnt 집계 정보 세팅
UPDATE user_stats_tbl ust
    JOIN (
    SELECT user_id, COUNT(*) AS cnt
    FROM posts_tbl
    WHERE status = 'ACTIVE'
    GROUP BY user_id
    ) p ON ust.user_id = p.user_id
    SET ust.comm_post_cnt = p.cnt;

-- 5. comm_comment_cnt 집계 정보 세팅
UPDATE user_stats_tbl ust
    JOIN (
    SELECT user_id, COUNT(*) AS cnt
    FROM post_comments_tbl
    WHERE status = 'ACTIVE'
    GROUP BY user_id
    ) c ON ust.user_id = c.user_id
    SET ust.comm_comment_cnt = c.cnt;

-- 6. comm_saved_post_cnt 집계 정보 세팅
UPDATE user_stats_tbl ust
    JOIN (
    SELECT user_id, COUNT(*) AS cnt
    FROM post_scraps_tbl
    GROUP BY user_id
    ) s ON ust.user_id = s.user_id
    SET ust.comm_saved_post_cnt = s.cnt;