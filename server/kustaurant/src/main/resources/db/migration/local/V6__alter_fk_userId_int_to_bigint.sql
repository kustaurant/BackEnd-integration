/*
   1) users_tbl 을 참조하는 모든 FK 삭제(조건부)
   2) users_tbl.user_id → BIGINT UNSIGNED 로 확장
   3) 자식 테이블 user_id 컬럼 모두 BIGINT UNSIGNED 로 변경
   4) FK 재생성 (필요한 CASCADE 포함)
   5) feedbacks_tbl 컬럼·제약 정리
*/

/* ─── 0. FK 존재할 때만 지우는 헬퍼 ─── */
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

/* ─── 1. FK 삭제 (존재할 때만) ─── */
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

/* 추가: OLD 테이블 FK 삭제 */
CALL drop_fk_if_exists('post_comment_dislikes_tbl_old'  ,'post_comment_dislikes_tbl_old_ibfk_2');
CALL drop_fk_if_exists('post_comment_likes_tbl_old'     ,'post_comment_likes_tbl_old_ibfk_2');
CALL drop_fk_if_exists('post_dislikes_tbl_old'          ,'post_dislikes_tbl_old_ibfk_2');
CALL drop_fk_if_exists('post_likes_tbl_old'             ,'post_likes_tbl_old_ibfk_2');

/* ─── 2. 부모(users_tbl) 컬럼 타입 확장 ─── */
ALTER TABLE users_tbl
    MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT;

/* ─── 3. 자식 테이블 컬럼 타입 확장 ─── */
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

/* 추가: OLD 테이블 컬럼 타입 확장 */
ALTER TABLE post_comment_dislikes_tbl_old    MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL;
ALTER TABLE post_comment_likes_tbl_old       MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL;
ALTER TABLE post_dislikes_tbl_old            MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL;
ALTER TABLE post_likes_tbl_old               MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL;
/* ─── 4. FK 재생성 ─── */
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

/* 추가: OLD 테이블 FK 재생성 */
ALTER TABLE post_comment_dislikes_tbl_old
    ADD CONSTRAINT fk_post_cmt_dislikes_user_old FOREIGN KEY (user_id) REFERENCES users_tbl(user_id);

ALTER TABLE post_comment_likes_tbl_old
    ADD CONSTRAINT fk_post_cmt_likes_user_old FOREIGN KEY (user_id) REFERENCES users_tbl(user_id);

ALTER TABLE post_dislikes_tbl_old
    ADD CONSTRAINT fk_post_dislikes_user_old FOREIGN KEY (user_id) REFERENCES users_tbl(user_id);

ALTER TABLE post_likes_tbl_old
    ADD CONSTRAINT fk_post_likes_user_old FOREIGN KEY (user_id) REFERENCES users_tbl(user_id);

/* ─── 5. feedbacks_tbl 정리 ─── */
ALTER TABLE feedbacks_tbl
    CHANGE COLUMN feedback_content comment TEXT NOT NULL,
DROP COLUMN updated_at,
    DROP COLUMN status;

/* ─── 6. 헬퍼 프로시저 제거 ─── */
DROP PROCEDURE IF EXISTS drop_fk_if_exists;

