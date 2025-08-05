-- 구 post old 테이블들 제거
DROP TABLE IF EXISTS post_likes_tbl_old;
DROP TABLE IF EXISTS post_dislikes_tbl_old;
DROP TABLE IF EXISTS post_comment_likes_tbl_old;
DROP TABLE IF EXISTS post_comment_dislikes_tbl_old;

-- postcomment 의 status 타입을 ENUM 으로 변경 + 기본값 지정
ALTER TABLE post_comments_tbl
    MODIFY COLUMN status ENUM('ACTIVE','DELETED')
    NOT NULL DEFAULT 'ACTIVE';

-- post_user_reaction 테이블 생성
CREATE TABLE post_user_reaction (
    post_id     INT             NOT NULL,
    user_id     BIGINT UNSIGNED NOT NULL,
    reaction    ENUM('LIKE','DISLIKE') NOT NULL,
    reacted_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (post_id, user_id),
    INDEX idx_user_post (user_id, post_id),

    CONSTRAINT fk_pur_post FOREIGN KEY (post_id)
        REFERENCES posts_tbl(post_id),
    CONSTRAINT fk_pur_user FOREIGN KEY (user_id)
        REFERENCES users_tbl(user_id)
);

-- LIKE 데이터 이관
INSERT INTO post_user_reaction (post_id, user_id, reaction, reacted_at)
SELECT  post_id, user_id, 'LIKE' AS reaction, COALESCE(created_at, NOW()) AS reacted_at
FROM post_likes_tbl;

-- DISLIKE 데이터 이관
INSERT INTO post_user_reaction (post_id, user_id, reaction, reacted_at)
SELECT  post_id, user_id, 'DISLIKE' AS reaction, COALESCE(created_at, NOW()) AS reacted_at
FROM post_dislikes_tbl
