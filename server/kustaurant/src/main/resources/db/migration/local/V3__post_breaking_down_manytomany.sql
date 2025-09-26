-- 0. NEW 테이블 데이터 초기화
DELETE FROM post_likes_tbl_new;
DELETE FROM post_dislikes_tbl_new;
DELETE FROM post_comments_likes_tbl_new;
DELETE FROM post_comments_dislikes_tbl_new;

-- 1. 기존 테이블에서 NEW 테이블로 데이터 이관
INSERT INTO post_likes_tbl_new (user_id, post_id, created_at)
SELECT user_id, post_id, NOW()
FROM post_likes_tbl;

INSERT INTO post_dislikes_tbl_new (user_id, post_id, created_at)
SELECT user_id, post_id, NOW()
FROM post_dislikes_tbl;

INSERT INTO post_comments_likes_tbl_new (user_id, comment_id, created_at)
SELECT user_id, comment_id, NOW()
FROM comment_likes_tbl;

INSERT INTO post_comments_dislikes_tbl_new (user_id, comment_id, created_at)
SELECT user_id, comment_id, NOW()
FROM comment_dislikes_tbl;

-- 3. 기존 테이블을 백업용으로 이름 변경
RENAME TABLE post_likes_tbl TO post_likes_tbl_old;
RENAME TABLE post_dislikes_tbl TO post_dislikes_tbl_old;
RENAME TABLE comment_likes_tbl TO post_comment_likes_tbl_old;
RENAME TABLE comment_dislikes_tbl TO post_comment_dislikes_tbl_old;

-- 4. NEW 테이블을 원래 이름으로 변경
RENAME TABLE post_likes_tbl_new TO post_likes_tbl;
RENAME TABLE post_dislikes_tbl_new TO post_dislikes_tbl;
RENAME TABLE post_comments_likes_tbl_new TO post_comment_likes_tbl;
RENAME TABLE post_comments_dislikes_tbl_new TO post_comment_dislikes_tbl;
