/* 구 post_like/dislike 테이블들 제거 */
DROP TABLE IF EXISTS post_likes_tbl;
DROP TABLE IF EXISTS post_dislikes_tbl;
DROP TABLE IF EXISTS post_comment_reaction;

/* 신규 post_comment_reaction 테이블 생성 */
CREATE TABLE post_comment_reaction (
    post_comment_id      INT             NOT NULL,
    user_id         BIGINT UNSIGNED NOT NULL,
    reaction        ENUM('LIKE','DISLIKE') NOT NULL,
    reacted_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_post_comment_reaction PRIMARY KEY (post_comment_id, user_id),

    CONSTRAINT fk_reaction_post_comment
        FOREIGN KEY (post_comment_id) REFERENCES post_comments_tbl(comment_id)
            ON DELETE CASCADE,
    CONSTRAINT fk_reaction_user
        FOREIGN KEY (user_id) REFERENCES users_tbl(user_id)
            ON DELETE CASCADE
);

/* 기존 좋아요 데이터 이관 */
INSERT INTO post_comment_reaction (post_comment_id, user_id, reaction, reacted_at)
SELECT comment_id, user_id, 'LIKE' AS reaction, COALESCE(created_at, NOW()) AS reacted_at
FROM post_comment_likes_tbl;

/* 기존 싫어요 데이터 이관 */
INSERT INTO post_comment_reaction (post_comment_id, user_id, reaction, reacted_at)
SELECT comment_id, user_id, 'DISLIKE' AS reaction, COALESCE(created_at, NOW()) AS reacted_at
FROM post_comment_dislikes_tbl;