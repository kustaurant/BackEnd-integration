
/* 신규 post_comment_reaction 테이블 생성 */
CREATE TABLE post_comment_reaction (
    post_comment_id      INT             NOT NULL,
    user_id         BIGINT UNSIGNED NOT NULL,
    reaction        ENUM('LIKE','DISLIKE') NOT NULL,
    reacted_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

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

-- postcomment 의 status 타입을 ENUM 으로 변경 + 기본값 지정
ALTER TABLE post_comments_tbl
    MODIFY COLUMN status ENUM('ACTIVE','DELETED')
    NOT NULL DEFAULT 'ACTIVE';

-- post_user_reaction 테이블 생성
CREATE TABLE post_reaction (
                                    post_id     INT             NOT NULL,
                                    user_id     BIGINT UNSIGNED NOT NULL,
                                    reaction    ENUM('LIKE','DISLIKE') NOT NULL,
                                    reacted_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                    PRIMARY KEY (post_id, user_id),
                                    INDEX idx_user_post (user_id, post_id),

                                    CONSTRAINT fk_pur_post FOREIGN KEY (post_id)
                                        REFERENCES posts_tbl(post_id),
                                    CONSTRAINT fk_pur_user FOREIGN KEY (user_id)
                                        REFERENCES users_tbl(user_id)
);

-- LIKE 데이터 이관
INSERT INTO post_reaction (post_id, user_id, reaction, reacted_at)
SELECT  post_id, user_id, 'LIKE' AS reaction, COALESCE(created_at, NOW()) AS reacted_at
FROM post_likes_tbl;

-- DISLIKE 데이터 이관
INSERT INTO post_reaction (post_id, user_id, reaction, reacted_at)
SELECT  post_id, user_id, 'DISLIKE' AS reaction, COALESCE(created_at, NOW()) AS reacted_at
FROM post_dislikes_tbl;

/* 구 post_like/dislike 테이블들 제거 */
DROP TABLE IF EXISTS post_likes_tbl_old;
DROP TABLE IF EXISTS post_dislikes_tbl_old;
DROP TABLE IF EXISTS post_comment_likes_tbl_old;
DROP TABLE IF EXISTS post_comment_dislikes_tbl_old;
DROP TABLE IF EXISTS post_likes_tbl;
DROP TABLE IF EXISTS post_dislikes_tbl;

/* 구 post_comment_like/dislike 테이블들 제거 */
DROP TABLE IF EXISTS post_comment_likes_tbl;
DROP TABLE IF EXISTS post_comment_dislikes_tbl;

/* eval, post 관련 테이블들 이름 변경 */
RENAME TABLE eval_comm_user_reaction TO evaluation_comment_reaction;
RENAME TABLE eval_user_reaction TO evaluation_reaction;
RENAME TABLE eval_comment To evaluation_comment;
RENAME TABLE post_comments_tbl TO post_comment;
RENAME TABLE post_photoes_tbl TO post_photo;
RENAME TABLE post_scraps_tbl TO post_scrap;
RENAME TABLE posts_tbl TO post;

/* 그외 이름들 변경 */
RENAME TABLE feedbacks_tbl TO admin_feedback;
RENAME TABLE home_modal_tbl TO admin_home_modal;
RENAME TABLE notice_tbl TO admin_notice;
RENAME TABLE report TO admin_report;

RENAME TABLE evaluation_situations_tbl TO evaluation_situation;
RENAME TABLE evaluations_tbl TO evaluation;

RENAME TABLE situations_tbl TO restaurant_situation;
RENAME TABLE restaurant_favorite_tbl TO restaurant_favorite;
RENAME TABLE restaurant_menus_tbl TO restaurant_menus;
RENAME TABLE restaurant_situation_relations_tbl TO restaurant_situation_relation;
RENAME TABLE restaurants_tbl TO restaurant;

RENAME TABLE user_stats_tbl TO user_stats;

/* post 필드 일부 수정 */
UPDATE post SET post_category = 'FREE' WHERE post_category = '자유게시판';
UPDATE post SET post_category = 'COLUMN' WHERE post_category = '칼럼게시판';
UPDATE post SET post_category = 'SUGGESTION' WHERE post_category = '건의게시판';

ALTER TABLE post MODIFY COLUMN post_category ENUM('FREE','COLUMN','SUGGESTION') NOT NULL;
ALTER TABLE post MODIFY COLUMN status VARCHAR(20) NOT NULL;

/* post_comment 필드 일부 수정 */
ALTER TABLE `post_comment` MODIFY COLUMN `status` ENUM('ACTIVE','PENDING','DELETED') NOT NULL DEFAULT 'ACTIVE';

