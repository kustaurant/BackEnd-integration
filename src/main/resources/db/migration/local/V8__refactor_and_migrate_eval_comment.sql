/*
    restaurant_commnets_like_tbl -> eval_comments_like_tbl 변경
    restaurant_comments_tbl -> eval_comments_tbl 변경

    구 eval_comments_like/dislike_tbl이 eval이랑 eval_comment에 대한 값을 동시에 적용중이라
    각 eval과 eval_comment로 분리

    또한 like/dislike 개별 테이블 하나의 like로 두고 enum (like,dislike)필드 추가

*/
ALTER TABLE restaurant_comment_likes_tbl RENAME TO evaluation_like;
ALTER TABLE restaurant_comments_tbl RENAME TO eval_comment;
ALTER TABLE restaurant_comment_reports_tbl RENAME TO report;

ALTER TABLE evaluation_like
DROP COLUMN created_at;

ALTER TABLE evaluation_like
CHANGE like_id id INT AUTO_INCREMENT PRIMARY KEY;

CREATE TABLE eval_comment_like(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    eval_comment_id INT NOT NULL,
    user_id BIGINT NOT NULL,
    reaction ENUM('LIKE','DISLIKE') NOT NULL DEFAULT 'LIKE',
    UNIQUE KEY uq_eval_like (eval_comment_id, user_id),
    INDEX idx_eval_comment (eval_comment_id),
    CONSTRAINT fk_eval_comment FOREIGN KEY (eval_comment_id) REFERENCES eval_comment(id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users_tbl(user_id)
);

INSERT INTO eval_comment_like (user_id, eval_comment_id, reaction)
SELECT user_id, comment_id,'LIKE'
FROM evaluation_like
WHERE comment_id IS NOT NULL;

DELETE FROM evaluation_like
WHERE comment_id IS NOT NULL;


ALTER TABLE evaluation_like
ADD COLUMN reaction ENUM('LIKE','DISLIKE') NOT NULL AFTER evaluation_id;

UPDATE evaluation_like
SET reaction = 'LIKE';

ALTER TABLE evaluation_like
DROP COLUMN comment_id;

INSERT INTO evaluation_like (
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

INSERT INTO eval_comment_like (
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

ALTER TABLE eval_comment
    CHANGE comment_body body VARCHAR(1000);

ALTER TABLE eval_comment
    CHANGE comment_like_count like_count INT;

