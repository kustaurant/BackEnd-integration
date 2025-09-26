-- v2

ALTER TABLE evaluation_item_scores_tbl
    RENAME TO evaluation_situations_tbl;
ALTER TABLE restaurant_situation_relations_tbl
    MODIFY COLUMN relation_id INT NOT NULL AUTO_INCREMENT FIRST;
DROP TABLE IF EXISTS restaurant_hashtag_relations_tbl;
DROP TABLE IF EXISTS restaurant_hashtags_tbl;
ALTER TABLE users_tbl
    CHANGE COLUMN user_nickname nickname  VARCHAR(20)  NOT NULL UNIQUE,
    CHANGE COLUMN user_email    email     VARCHAR(40),
    CHANGE COLUMN user_role role ENUM('USER', 'ADMIN') NOT NULL;
ALTER TABLE users_tbl
DROP COLUMN user_password;

-- v3

DELETE FROM post_likes_tbl_new;
DELETE FROM post_dislikes_tbl_new;
DELETE FROM post_comments_likes_tbl_new;
DELETE FROM post_comments_dislikes_tbl_new;
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
RENAME TABLE post_likes_tbl TO post_likes_tbl_old;
RENAME TABLE post_dislikes_tbl TO post_dislikes_tbl_old;
RENAME TABLE comment_likes_tbl TO post_comment_likes_tbl_old;
RENAME TABLE comment_dislikes_tbl TO post_comment_dislikes_tbl_old;
RENAME TABLE post_likes_tbl_new TO post_likes_tbl;
RENAME TABLE post_dislikes_tbl_new TO post_dislikes_tbl;
RENAME TABLE post_comments_likes_tbl_new TO post_comment_likes_tbl;
RENAME TABLE post_comments_dislikes_tbl_new TO post_comment_dislikes_tbl;

-- v4
ALTER TABLE posts_tbl CHANGE like_count net_likes INT;

-- v5
ALTER TABLE users_tbl MODIFY COLUMN status ENUM('ACTIVE', 'DELETED') NOT NULL;
ALTER TABLE users_tbl
DROP COLUMN access_token,
DROP COLUMN refresh_token;

-- v6
DROP PROCEDURE IF EXISTS drop_fk_if_exists;
DELIMITER //
CREATE PROCEDURE drop_fk_if_exists(IN p_table VARCHAR(64), IN p_fk VARCHAR(64))
BEGIN
  IF EXISTS (
      SELECT 1
      FROM   information_schema.TABLE_CONSTRAINTS
      WHERE  TABLE_SCHEMA = DATABASE()
        AND  TABLE_NAME   = p_table
        AND  CONSTRAINT_NAME = p_fk
        AND  CONSTRAINT_TYPE = 'FOREIGN KEY'
  ) THEN
     SET @sql := CONCAT('ALTER TABLE `', p_table, '` DROP FOREIGN KEY `', p_fk, '`');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;
END IF;
END //
DELIMITER ;
CALL drop_fk_if_exists('evaluations_tbl'                ,'fk_evaluations_TBL_users_TBL');
CALL drop_fk_if_exists('feedbacks_tbl'                  ,'feedbacks_tbl_ibfk_1');
CALL drop_fk_if_exists('post_comments_tbl'              ,'fk_post_comments_TBL_users_TBL1');
CALL drop_fk_if_exists('post_scraps_tbl'                ,'fk_post_scraps_TBL_users_TBL1');
CALL drop_fk_if_exists('post_scraps_tbl'                ,'fk_post_scraps_TBL_posts_TBL1');
CALL drop_fk_if_exists('posts_tbl'                      ,'fk_posts_TBL_users_TBL1');
CALL drop_fk_if_exists('restaurant_comment_dislikes_tbl','restaurant_comment_dislikes_tbl_ibfk_1');
CALL drop_fk_if_exists('restaurant_comment_likes_tbl'   ,'fk_likes_TBL_users_TBL1');
CALL drop_fk_if_exists('restaurant_comment_reports_tbl' ,'restaurant_comment_reports_tbl_ibfk_1');
CALL drop_fk_if_exists('restaurant_comments_tbl'        ,'fk_restaurant_comments_TBL_users_TBL1');
CALL drop_fk_if_exists('restaurant_favorite_tbl'        ,'fk_restaurant_favorite_TBL_users_TBL1');
CALL drop_fk_if_exists('post_comment_dislikes_tbl' ,'post_comment_dislikes_tbl_ibfk_1');
CALL drop_fk_if_exists('post_comment_likes_tbl'    ,'post_comment_likes_tbl_ibfk_1');
CALL drop_fk_if_exists('post_dislikes_tbl'          ,'post_dislikes_tbl_ibfk_1');
CALL drop_fk_if_exists('post_likes_tbl'             ,'post_likes_tbl_ibfk_1');
CALL drop_fk_if_exists('post_comment_dislikes_tbl_old'  ,'post_comment_dislikes_tbl_old_ibfk_2');
CALL drop_fk_if_exists('post_comment_likes_tbl_old'     ,'post_comment_likes_tbl_old_ibfk_2');
CALL drop_fk_if_exists('post_dislikes_tbl_old'          ,'post_dislikes_tbl_old_ibfk_2');
CALL drop_fk_if_exists('post_likes_tbl_old'             ,'post_likes_tbl_old_ibfk_2');
ALTER TABLE users_tbl
    MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT;
ALTER TABLE evaluations_tbl                 MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL;
ALTER TABLE feedbacks_tbl                   MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL;
ALTER TABLE post_comments_tbl               MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL;
ALTER TABLE post_scraps_tbl                 MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL;
ALTER TABLE posts_tbl                       MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL;
ALTER TABLE restaurant_comment_dislikes_tbl MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL;
ALTER TABLE restaurant_comment_likes_tbl    MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL;
ALTER TABLE restaurant_comment_reports_tbl  MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL;
ALTER TABLE restaurant_comments_tbl         MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL;
ALTER TABLE restaurant_favorite_tbl         MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL;
ALTER TABLE post_comment_dislikes_tbl  MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL;
ALTER TABLE post_comment_likes_tbl     MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL;
ALTER TABLE post_dislikes_tbl           MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL;
ALTER TABLE post_likes_tbl              MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL;
ALTER TABLE post_comment_dislikes_tbl_old    MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL;
ALTER TABLE post_comment_likes_tbl_old       MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL;
ALTER TABLE post_dislikes_tbl_old            MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL;
ALTER TABLE post_likes_tbl_old               MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL;
ALTER TABLE evaluations_tbl
    ADD CONSTRAINT fk_evaluations_user FOREIGN KEY (user_id) REFERENCES users_tbl(user_id);
ALTER TABLE feedbacks_tbl
    ADD CONSTRAINT fk_feedbacks_user FOREIGN KEY (user_id) REFERENCES users_tbl(user_id);
ALTER TABLE post_comments_tbl
    ADD CONSTRAINT fk_post_comments_user FOREIGN KEY (user_id) REFERENCES users_tbl(user_id);
ALTER TABLE post_scraps_tbl
    ADD CONSTRAINT fk_post_scraps_user FOREIGN KEY (user_id) REFERENCES users_tbl(user_id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_post_scraps_post FOREIGN KEY (post_id) REFERENCES posts_tbl(post_id) ON DELETE CASCADE,
    ADD CONSTRAINT uq_post_scraps_user_post UNIQUE (user_id, post_id);
ALTER TABLE posts_tbl
    ADD CONSTRAINT fk_posts_user FOREIGN KEY (user_id) REFERENCES users_tbl(user_id);
ALTER TABLE restaurant_comment_dislikes_tbl
    ADD CONSTRAINT fk_rest_cmt_dislikes_user FOREIGN KEY (user_id) REFERENCES users_tbl(user_id);
ALTER TABLE restaurant_comment_likes_tbl
    ADD CONSTRAINT fk_rest_cmt_likes_user FOREIGN KEY (user_id) REFERENCES users_tbl(user_id);
ALTER TABLE restaurant_comment_reports_tbl
    ADD CONSTRAINT fk_rest_cmt_reports_user FOREIGN KEY (user_id) REFERENCES users_tbl(user_id);
ALTER TABLE restaurant_comments_tbl
    ADD CONSTRAINT fk_rest_comments_user FOREIGN KEY (user_id) REFERENCES users_tbl(user_id);
ALTER TABLE restaurant_favorite_tbl
    ADD CONSTRAINT fk_rest_favorite_user FOREIGN KEY (user_id) REFERENCES users_tbl(user_id);
ALTER TABLE post_comment_dislikes_tbl
    ADD CONSTRAINT fk_post_cmt_dislikes_user FOREIGN KEY (user_id) REFERENCES users_tbl(user_id);
ALTER TABLE post_comment_likes_tbl
    ADD CONSTRAINT fk_post_cmt_likes_user    FOREIGN KEY (user_id) REFERENCES users_tbl(user_id);
ALTER TABLE post_dislikes_tbl
    ADD CONSTRAINT fk_post_dislikes_user     FOREIGN KEY (user_id) REFERENCES users_tbl(user_id);
ALTER TABLE post_likes_tbl
    ADD CONSTRAINT fk_post_likes_user        FOREIGN KEY (user_id) REFERENCES users_tbl(user_id);
ALTER TABLE post_comment_dislikes_tbl_old
    ADD CONSTRAINT fk_post_cmt_dislikes_user_old FOREIGN KEY (user_id) REFERENCES users_tbl(user_id);
ALTER TABLE post_comment_likes_tbl_old
    ADD CONSTRAINT fk_post_cmt_likes_user_old FOREIGN KEY (user_id) REFERENCES users_tbl(user_id);
ALTER TABLE post_dislikes_tbl_old
    ADD CONSTRAINT fk_post_dislikes_user_old FOREIGN KEY (user_id) REFERENCES users_tbl(user_id);
ALTER TABLE post_likes_tbl_old
    ADD CONSTRAINT fk_post_likes_user_old FOREIGN KEY (user_id) REFERENCES users_tbl(user_id);
ALTER TABLE feedbacks_tbl
    CHANGE COLUMN feedback_content comment TEXT NOT NULL,
DROP COLUMN updated_at, DROP COLUMN status;
DROP PROCEDURE IF EXISTS drop_fk_if_exists;

-- v7
CREATE TABLE user_stats_tbl (
                                user_id              BIGINT UNSIGNED NOT NULL,
                                saved_rest_cnt       INT          NOT NULL DEFAULT 0,
                                rated_rest_cnt       INT          NOT NULL DEFAULT 0,
                                comm_post_cnt        INT          NOT NULL DEFAULT 0,
                                comm_comment_cnt     INT          NOT NULL DEFAULT 0,
                                comm_saved_post_cnt  INT          NOT NULL DEFAULT 0,

                                PRIMARY KEY (user_id),

                                CONSTRAINT fk_user_stats_user
                                    FOREIGN KEY (user_id)
                                        REFERENCES users_tbl(user_id)
                                        ON DELETE CASCADE
);

-- v8
ALTER TABLE restaurant_comments_tbl
DROP FOREIGN KEY fk_restaurant_comments_TBL_evaluations_TBL1;
ALTER TABLE restaurant_comment_likes_tbl
DROP FOREIGN KEY fk_likes_TBL_evaluations_TBL1;
ALTER TABLE restaurant_comment_dislikes_tbl
DROP FOREIGN KEY restaurant_comment_dislikes_tbl_ibfk_3;
ALTER TABLE evaluation_situations_tbl
DROP FOREIGN KEY fk_evaluation_item_scores_TBL_evaluations_TBL1;
ALTER TABLE restaurant_comment_reports_tbl
DROP FOREIGN KEY restaurant_comment_reports_tbl_ibfk_2;
ALTER TABLE evaluations_tbl
    MODIFY COLUMN evaluation_id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE restaurant_comments_tbl
    MODIFY COLUMN evaluation_id BIGINT NOT NULL;
ALTER TABLE restaurant_comments_tbl
    ADD CONSTRAINT fk_restaurant_comments_evaluation
        FOREIGN KEY (evaluation_id)
            REFERENCES evaluations_tbl(evaluation_id);
ALTER TABLE restaurant_comment_likes_tbl
    MODIFY COLUMN evaluation_id BIGINT;
ALTER TABLE restaurant_comment_likes_tbl
    ADD CONSTRAINT fk_comment_likes_evaluation
        FOREIGN KEY (evaluation_id)
            REFERENCES evaluations_tbl(evaluation_id);
ALTER TABLE restaurant_comment_dislikes_tbl
    MODIFY COLUMN evaluation_id BIGINT;
ALTER TABLE restaurant_comment_dislikes_tbl
    ADD CONSTRAINT fk_comment_dislikes_evaluation
        FOREIGN KEY (evaluation_id)
            REFERENCES evaluations_tbl(evaluation_id);
ALTER TABLE evaluation_situations_tbl
    MODIFY COLUMN evaluation_id BIGINT NOT NULL;
ALTER TABLE evaluation_situations_tbl
    ADD CONSTRAINT fk_evaluation_situations_evaluation
        FOREIGN KEY (evaluation_id)
            REFERENCES evaluations_tbl(evaluation_id);
ALTER TABLE restaurant_comment_reports_tbl
    MODIFY COLUMN evaluation_id BIGINT;
ALTER TABLE restaurant_comment_reports_tbl
    ADD CONSTRAINT fk_comment_reports_evaluation
        FOREIGN KEY (evaluation_id)
            REFERENCES evaluations_tbl(evaluation_id);
ALTER TABLE evaluation_situations_tbl
DROP FOREIGN KEY fk_evaluation_item_scores_TBL_situation_categories_TBL1;
ALTER TABLE restaurant_situation_relations_tbl
DROP FOREIGN KEY fk_restaurant_categories_TBL_categories1;
ALTER TABLE situations_tbl
    MODIFY COLUMN situation_id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE evaluation_situations_tbl
    MODIFY COLUMN situation_id BIGINT NOT NULL;
ALTER TABLE evaluation_situations_tbl
    ADD CONSTRAINT fk_evaluation_situations_situation
        FOREIGN KEY (situation_id)
            REFERENCES situations_tbl(situation_id);
ALTER TABLE restaurant_situation_relations_tbl
    MODIFY COLUMN situation_id BIGINT NOT NULL;
ALTER TABLE restaurant_situation_relations_tbl
    ADD CONSTRAINT fk_restaurant_situation_relations_situation
        FOREIGN KEY (situation_id)
            REFERENCES situations_tbl(situation_id);
ALTER TABLE restaurant_situation_relations_tbl
    MODIFY COLUMN relation_id BIGINT NOT NULL AUTO_INCREMENT;

-- v9

ALTER TABLE restaurant_comment_likes_tbl RENAME TO eval_user_reaction;
ALTER TABLE restaurant_comments_tbl RENAME TO eval_comment;
DROP TABLE IF EXISTS restaurant_comment_reports_tbl;
CREATE TABLE report (
                        id          BIGINT UNSIGNED     NOT NULL AUTO_INCREMENT,
                        user_id     BIGINT UNSIGNED     NOT NULL,
                        target_id   BIGINT UNSIGNED     NOT NULL,
                        target_type ENUM('POST', 'POST_COMMENT', 'EVALUATION', 'EVAL_COMMENT') NOT NULL,
                        reason      ENUM('SPAM','OFFENSIVE','INAPPROPRIATE','COPYRIGHT','OTHER') NOT NULL,
                        status      ENUM('PENDING','RESOLVED','REJECTED') NOT NULL DEFAULT 'PENDING',
                        created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        PRIMARY KEY (id),
                        KEY idx_report_user   (user_id),
                        KEY idx_report_target (target_type, target_id),
                        CONSTRAINT fk_report_user FOREIGN KEY (user_id) REFERENCES users_tbl(user_id)
);
ALTER TABLE eval_user_reaction
DROP COLUMN created_at,
  DROP FOREIGN KEY fk_comment_likes_evaluation,
  DROP FOREIGN KEY fk_likes_TBL_restaurant_comments_TBL1;
ALTER TABLE eval_user_reaction
    CHANGE COLUMN like_id
    id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    MODIFY COLUMN comment_id BIGINT UNSIGNED,
    MODIFY COLUMN evaluation_id BIGINT;
ALTER TABLE eval_user_reaction
    ADD CONSTRAINT fk_comment_likes_evaluation
        FOREIGN KEY (evaluation_id) REFERENCES evaluations_tbl(evaluation_id);
ALTER TABLE restaurant_comment_dislikes_tbl DROP FOREIGN KEY restaurant_comment_dislikes_tbl_ibfk_2;
ALTER TABLE eval_comment
    MODIFY comment_id BIGINT UNSIGNED,
DROP   PRIMARY KEY;
ALTER TABLE eval_comment
    CHANGE comment_id      id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    CHANGE comment_body    body        VARCHAR(1000),
    CHANGE comment_like_count like_count INT NOT NULL DEFAULT 0,
    ADD    dislike_count   INT NOT NULL DEFAULT 0,
    MODIFY status ENUM('ACTIVE','DELETED') NOT NULL DEFAULT 'ACTIVE';
CREATE TABLE eval_comm_user_reaction(
                                        id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                        eval_comment_id BIGINT UNSIGNED NOT NULL,
                                        user_id BIGINT UNSIGNED NOT NULL,
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
ALTER TABLE evaluations_tbl
    CHANGE comment_body body VARCHAR(1000),
    CHANGE comment_img_url img_url VARCHAR(300),
    CHANGE comment_like_count like_count INT;
ALTER TABLE evaluations_tbl
    ADD COLUMN dislike_count INT NOT NULL DEFAULT 0;

-- v10

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
UPDATE user_stats_tbl ust
    JOIN (
    SELECT user_id, COUNT(*) AS cnt
    FROM restaurant_favorite_tbl
    WHERE status = 'ACTIVE'
    GROUP BY user_id
    ) fav ON ust.user_id = fav.user_id
    SET ust.saved_rest_cnt = fav.cnt;
UPDATE user_stats_tbl ust
    JOIN (
    SELECT user_id, COUNT(*) AS cnt
    FROM evaluations_tbl
    WHERE status = 'ACTIVE'
    GROUP BY user_id
    ) ev ON ust.user_id = ev.user_id
    SET ust.rated_rest_cnt = ev.cnt;
UPDATE user_stats_tbl ust
    JOIN (
    SELECT user_id, COUNT(*) AS cnt
    FROM posts_tbl
    WHERE status = 'ACTIVE'
    GROUP BY user_id
    ) p ON ust.user_id = p.user_id
    SET ust.comm_post_cnt = p.cnt;
UPDATE user_stats_tbl ust
    JOIN (
    SELECT user_id, COUNT(*) AS cnt
    FROM post_comments_tbl
    WHERE status = 'ACTIVE'
    GROUP BY user_id
    ) c ON ust.user_id = c.user_id
    SET ust.comm_comment_cnt = c.cnt;
UPDATE user_stats_tbl ust
    JOIN (
    SELECT user_id, COUNT(*) AS cnt
    FROM post_scraps_tbl
    GROUP BY user_id
    ) s ON ust.user_id = s.user_id
    SET ust.comm_saved_post_cnt = s.cnt;

-- v11

DELETE FROM restaurants_tbl
WHERE restaurant_latitude = 'no_restaurant' OR restaurant_longitude = 'no_restaurant';
ALTER TABLE restaurants_tbl
    CHANGE COLUMN restaurant_latitude  latitude  DOUBLE,
    CHANGE COLUMN restaurant_longitude longitude DOUBLE,
DROP COLUMN restaurant_visit_count;

-- v12

UPDATE users_tbl
SET    login_api = UPPER(login_api)
WHERE  login_api IN ('naver', 'apple');
ALTER TABLE users_tbl
    MODIFY login_api ENUM('NAVER','APPLE') NOT NULL;

-- v13

CREATE TABLE restaurant_rating (
                                   restaurant_id INT NOT NULL,
                                   score DOUBLE NOT NULL,
                                   tier INT NOT NULL,
                                   is_temp TINYINT(1) NOT NULL DEFAULT 0,
                                   rated_at DATETIME(6) NOT NULL,
                                   PRIMARY KEY (restaurant_id),
                                   CONSTRAINT fk_rating_restaurant FOREIGN KEY (restaurant_id)
                                       REFERENCES restaurants_tbl(restaurant_id)
                                       ON DELETE CASCADE
                                       ON UPDATE CASCADE,
                                   CONSTRAINT chk_tier_range CHECK ((tier >= 1 AND tier <= 5) OR tier = -1)
);

-- v14

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
INSERT INTO post_comment_reaction (post_comment_id, user_id, reaction, reacted_at)
SELECT comment_id, user_id, 'LIKE' AS reaction, COALESCE(created_at, NOW()) AS reacted_at
FROM post_comment_likes_tbl;
INSERT INTO post_comment_reaction (post_comment_id, user_id, reaction, reacted_at)
SELECT comment_id, user_id, 'DISLIKE' AS reaction, COALESCE(created_at, NOW()) AS reacted_at
FROM post_comment_dislikes_tbl;
ALTER TABLE post_comments_tbl
    MODIFY COLUMN status ENUM('ACTIVE','DELETED')
    NOT NULL DEFAULT 'ACTIVE';
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
INSERT INTO post_reaction (post_id, user_id, reaction, reacted_at)
SELECT  post_id, user_id, 'LIKE' AS reaction, COALESCE(created_at, NOW()) AS reacted_at
FROM post_likes_tbl;
INSERT INTO post_reaction (post_id, user_id, reaction, reacted_at)
SELECT  post_id, user_id, 'DISLIKE' AS reaction, COALESCE(created_at, NOW()) AS reacted_at
FROM post_dislikes_tbl;
DROP TABLE IF EXISTS post_likes_tbl_old;
DROP TABLE IF EXISTS post_dislikes_tbl_old;
DROP TABLE IF EXISTS post_comment_likes_tbl_old;
DROP TABLE IF EXISTS post_comment_dislikes_tbl_old;
DROP TABLE IF EXISTS post_likes_tbl;
DROP TABLE IF EXISTS post_dislikes_tbl;
DROP TABLE IF EXISTS post_comment_likes_tbl;
DROP TABLE IF EXISTS post_comment_dislikes_tbl;
RENAME TABLE eval_comm_user_reaction TO evaluation_comment_reaction;
RENAME TABLE eval_user_reaction TO evaluation_reaction;
RENAME TABLE eval_comment To evaluation_comment;
RENAME TABLE post_comments_tbl TO post_comment;
RENAME TABLE post_photoes_tbl TO post_photo;
RENAME TABLE post_scraps_tbl TO post_scrap;
RENAME TABLE posts_tbl TO post;
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
UPDATE post SET post_category = 'FREE' WHERE post_category = '자유게시판';
UPDATE post SET post_category = 'COLUMN' WHERE post_category = '칼럼게시판';
UPDATE post SET post_category = 'SUGGESTION' WHERE post_category = '건의게시판';
ALTER TABLE post MODIFY COLUMN post_category ENUM('FREE','COLUMN','SUGGESTION') NOT NULL;
ALTER TABLE post MODIFY COLUMN status VARCHAR(20) NOT NULL;
ALTER TABLE `post_comment` MODIFY COLUMN `status` ENUM('ACTIVE','PENDING','DELETED') NOT NULL DEFAULT 'ACTIVE';

-- v15

SELECT TABLE_NAME, CONSTRAINT_NAME
FROM information_schema.TABLE_CONSTRAINTS
WHERE CONSTRAINT_SCHEMA = DATABASE()
  AND CONSTRAINT_TYPE = 'FOREIGN KEY';
ALTER TABLE `post_comment`  DROP FOREIGN KEY `fk_post_comments_TBL_posts_TBL1`;
ALTER TABLE `post_photo`    DROP FOREIGN KEY `fk_post_photoes_TBL_posts_TBL1`;
ALTER TABLE `post_reaction` DROP FOREIGN KEY `fk_pur_post`;
ALTER TABLE `post_scrap`    DROP FOREIGN KEY `fk_post_scraps_post`;
ALTER TABLE post_comment_reaction DROP FOREIGN KEY fk_reaction_post_comment;
ALTER TABLE `evaluation_comment`              DROP FOREIGN KEY `fk_restaurant_comments_TBL_restaurant_TBL1`;
ALTER TABLE `evaluation`                      DROP FOREIGN KEY `fk_evaluations_TBL_restaurants_TBL1`;
ALTER TABLE `restaurant_favorite`             DROP FOREIGN KEY `fk_restaurant_favorite_TBL_restaurants_TBL1`;
ALTER TABLE `restaurant_menus`                DROP FOREIGN KEY `fk_restaurant_menus_TBL_restaurants_TBL1`;
ALTER TABLE `restaurant_rating`               DROP FOREIGN KEY `fk_rating_restaurant`;
ALTER TABLE `restaurant_situation_relation`   DROP FOREIGN KEY `fk_restaurant_categories_TBL_restaurant_TBL1`;
ALTER TABLE `post_comment`  MODIFY COLUMN `post_id` BIGINT UNSIGNED NOT NULL;
ALTER TABLE `post_photo`    MODIFY COLUMN `post_id` BIGINT UNSIGNED NOT NULL;
ALTER TABLE `post_reaction`  MODIFY COLUMN `post_id` BIGINT UNSIGNED NOT NULL;
ALTER TABLE `post_scrap`    MODIFY COLUMN `post_id` BIGINT UNSIGNED NOT NULL;
ALTER TABLE post_comment_reaction MODIFY COLUMN post_comment_id BIGINT UNSIGNED NOT NULL;
ALTER TABLE `evaluation_comment`                 MODIFY COLUMN `restaurant_id` BIGINT UNSIGNED NOT NULL;
ALTER TABLE `evaluation`                         MODIFY COLUMN `restaurant_id` BIGINT UNSIGNED NOT NULL;
ALTER TABLE `restaurant_favorite`                MODIFY COLUMN `restaurant_id` BIGINT UNSIGNED NOT NULL;
ALTER TABLE `restaurant_menus`                   MODIFY COLUMN `restaurant_id` BIGINT UNSIGNED NOT NULL;
ALTER TABLE `restaurant_rating`                  MODIFY COLUMN `restaurant_id` BIGINT UNSIGNED NOT NULL;
ALTER TABLE `restaurant_situation_relation`      MODIFY COLUMN `restaurant_id` BIGINT UNSIGNED NOT NULL;
ALTER TABLE `post` MODIFY COLUMN `post_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT;
ALTER TABLE post_comment CHANGE COLUMN comment_id post_comment_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT;
ALTER TABLE `restaurant` MODIFY COLUMN `restaurant_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT;
ALTER TABLE post_comment MODIFY COLUMN parent_comment_id BIGINT UNSIGNED NULL;
ALTER TABLE `post_comment`
    ADD CONSTRAINT `fk_post_comment_TBL_post_TBL1`
        FOREIGN KEY (`post_id`) REFERENCES `post`(`post_id`)
            ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE `post_photo`
    ADD CONSTRAINT `fk_post_photoe_TBL_post_TBL1`
        FOREIGN KEY (`post_id`) REFERENCES `post`(`post_id`)
            ON DELETE NO ACTION ON UPDATE CASCADE;
ALTER TABLE `post_reaction`
    ADD CONSTRAINT `fk_pur_post`
        FOREIGN KEY (`post_id`) REFERENCES `post`(`post_id`)
            ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE `post_scrap`
    ADD CONSTRAINT `fk_post_scrap_post`
        FOREIGN KEY (`post_id`) REFERENCES `post`(`post_id`)
            ON DELETE NO ACTION ON UPDATE CASCADE;
ALTER TABLE post_comment_reaction
    ADD CONSTRAINT fk_reaction_post_comment
        FOREIGN KEY (post_comment_id)
            REFERENCES post_comment (post_comment_id)
            ON DELETE CASCADE;
ALTER TABLE `evaluation_comment`
    ADD CONSTRAINT `fk_restaurant_comment_TBL_restaurant_TBL1`
        FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant`(`restaurant_id`)
            ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE `evaluation`
    ADD CONSTRAINT `fk_evaluations_TBL_restaurants_TBL1`
        FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant`(`restaurant_id`)
            ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE `restaurant_favorite`
    ADD CONSTRAINT `fk_restaurant_favorite_TBL_restaurants_TBL1`
        FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant`(`restaurant_id`)
            ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE `restaurant_menus`
    ADD CONSTRAINT `fk_restaurant_menus_TBL_restaurants_TBL1`
        FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant`(`restaurant_id`)
            ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE `restaurant_rating`
    ADD CONSTRAINT `fk_rating_restaurant`
        FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant`(`restaurant_id`)
            ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE `restaurant_situation_relation`
    ADD CONSTRAINT `fk_restaurant_categories_TBL_restaurant_TBL1`
        FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant`(`restaurant_id`)
            ON DELETE NO ACTION ON UPDATE NO ACTION;
ALTER TABLE post_scrap
DROP PRIMARY KEY,
  MODIFY COLUMN post_id BIGINT UNSIGNED NOT NULL FIRST,
  MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL AFTER post_id,
  DROP COLUMN scrap_id,
  MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER user_id,
  ADD PRIMARY KEY (post_id, user_id),
  ADD INDEX idx_user_post (user_id, post_id);