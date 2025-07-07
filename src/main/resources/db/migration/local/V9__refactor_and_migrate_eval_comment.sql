/*
    restaurant_commnets_like_tbl -> eval_comments_like_tbl 변경
    restaurant_comments_tbl -> eval_comments_tbl 변경

    구 eval_comments_like/dislike_tbl이 eval이랑 eval_comment에 대한 값을 동시에 적용중이라
    각 eval과 eval_comment로 분리

    또한 like/dislike 개별 테이블 하나의 like로 두고 enum (like,dislike)필드 추가
*/

ALTER TABLE restaurant_comment_likes_tbl RENAME TO eval_user_reaction;
ALTER TABLE restaurant_comments_tbl RENAME TO eval_comment;
ALTER TABLE restaurant_comment_reports_tbl RENAME TO report;

ALTER TABLE eval_user_reaction
DROP COLUMN created_at;

ALTER TABLE eval_user_reaction
CHANGE like_id id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY;

CREATE TABLE eval_comm_user_reaction(
    id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    eval_comment_id INT NOT NULL,
    user_id BIGINT NOT NULL,
    reaction ENUM('LIKE','DISLIKE') NOT NULL DEFAULT 'LIKE',
    UNIQUE KEY uq_eval_like (eval_comment_id, user_id),
    INDEX idx_eval_comment (eval_comment_id),
    CONSTRAINT fk_eval_comment FOREIGN KEY (eval_comment_id) REFERENCES eval_comment(id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users_tbl(user_id)
);

INSERT INTO eval_comm_user_reaction (user_id, eval_comment_id, reaction)
SELECT user_id, comment_id,'LIKE'
FROM eval_user_reaction
WHERE comment_id IS NOT NULL;

DELETE FROM eval_user_reaction
WHERE comment_id IS NOT NULL;


ALTER TABLE eval_user_reaction
ADD COLUMN reaction ENUM('LIKE','DISLIKE') NOT NULL AFTER evaluation_id;

UPDATE eval_user_reaction
SET reaction = 'LIKE';

ALTER TABLE eval_user_reaction
DROP COLUMN comment_id;

INSERT INTO eval_user_reaction (
    user_id,
    evaluation_id,
    reaction
)
SELECT
    user_id,
    evaluation_id,
    'DISLIKE'
FROM restaurant_comment_dislikes_tbl
WHERE evaluation_id IS NOT NULL;

INSERT INTO eval_comm_user_reaction (
    user_id,
    eval_comment_id,
    reaction
)
SELECT
    user_id,
    comment_id,
    'DISLIKE'
FROM restaurant_comment_dislikes_tbl
WHERE comment_id IS NOT NULL;

DROP TABLE restaurant_comment_dislikes_tbl;

/* eval_comment id이름, 타입 변경, 필드들 변경 및 추가*/
ALTER TABLE eval_comment
    CHANGE COLUMN comment_id id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    CHANGE comment_body body VARCHAR(1000),
    CHANGE comment_like_count like_count INT NOT NULL DEFAULT 0;

ALTER TABLE eval_comment
    ADD COLUMN dislike_count INT NOT NULL DEFAULT 0;

ALTER TABLE eval_comment
    MODIFY COLUMN status ENUM('ACTIVE','DELETED')
    NOT NULL DEFAULT 'ACTIVE';

/* evaluations_tbl */
ALTER TABLE evaluations_tbl
    CHANGE comment_body body VARCHAR(1000),
    CHANGE comment_img_url img_url VARCHAR(300),
    CHANGE comment_like_count like_count INT;

ALTER TABLE evaluations_tbl
    ADD COLUMN dislike_count INT NOT NULL DEFAULT 0;