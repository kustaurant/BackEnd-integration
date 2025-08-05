-- 구 post_like/dislike 테이블들 제거
DROP TABLE IF EXISTS post_likes_tbl;
DROP TABLE IF EXISTS post_dislikes_tbl;

-- 신규 post_comm_user_reaction 테이블 생성
CREATE TABLE post_comm_user_reaction (
    post_comm_id      INT             NOT NULL,
    user_id         BIGINT UNSIGNED NOT NULL,
    reaction        ENUM('LIKE','DISLIKE') NOT NULL,
    reacted_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_post_comment_user_reaction PRIMARY KEY (post_comm_id, user_id),

    CONSTRAINT fk_reaction_post_comment
        FOREIGN KEY (post_comm_id) REFERENCES post_comment_tbl(comment_id)
            ON DELETE CASCADE,
    CONSTRAINT fk_reaction_user
        FOREIGN KEY (user_id) REFERENCES user_tbl(user_id)
            ON DELETE CASCADE,
);

-- 기존 좋아요 데이터 이관
INSERT INTO post_comment_user_reaction (comment_id, user_id, reaction, created_at)
SELECT comment_id, user_id, 'LIKE' AS reaction, COALESCE(created_at, NOW()) AS reacted_at
FROM post_comment_likes_tbl;

--기존 싫어요 데이터 이관
INSERT INTO post_comment_user_reaction (comment_id, user_id, reaction, created_at)
SELECT comment_id, user_id, 'DISLIKE' AS reaction, COALESCE(created_at, NOW()) AS reacted_at
FROM post_comment_dislikes_tbl;