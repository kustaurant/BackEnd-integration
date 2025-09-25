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