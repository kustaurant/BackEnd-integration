-- 1. users_tbl: user_id int -> bigint로 변경 (AUTO_INCREMENT 유지)
ALTER TABLE users_tbl
    MODIFY COLUMN user_id BIGINT NOT NULL AUTO_INCREMENT;

-- 2. feedbacks_tbl: feedback_content -> comment (자료형은 TEXT로 변경)
ALTER TABLE feedbacks_tbl
    CHANGE COLUMN feedback_content comment TEXT NOT NULL;
-- updated_at 컬럼 제거
ALTER TABLE feedbacks_tbl
DROP COLUMN updated_at;
-- status 컬럼 제거
ALTER TABLE feedbacks_tbl
DROP COLUMN status;

-- 기존 외래키 제약조건 제거
ALTER TABLE feedbacks_tbl
DROP FOREIGN KEY feedbacks_tbl_ibfk_1;
-- user_id 컬럼 타입 변경 (int → bigint)
ALTER TABLE feedbacks_tbl
    MODIFY COLUMN user_id BIGINT;
-- 외래키 제약조건 다시 추가
ALTER TABLE feedbacks_tbl
    ADD CONSTRAINT fk_feedbacks_user_id
        FOREIGN KEY (user_id) REFERENCES users(user_id);

--3. evaluations_tbl: user_id 왜래키 int -> bigint 변경
-- 기존 외래키 제약조건 삭제
ALTER TABLE evaluations_tbl
DROP FOREIGN KEY fk_evaluations_TBL_users_TBL;
-- user_id 컬럼 타입 변경 (int → bigint)
ALTER TABLE evaluations_tbl
    MODIFY COLUMN user_id BIGINT;
-- 외래키 제약조건 다시 추가
ALTER TABLE evaluations_tbl
    ADD CONSTRAINT fk_evaluations_TBL_users_TBL
        FOREIGN KEY (user_id) REFERENCES users(user_id);

--4. posts_tbl: user_id 외래키 int -> bigint변경
-- 기존 외래키 제약조건 제거
ALTER TABLE posts_tbl
DROP FOREIGN KEY fk_posts_TBL_users_TBL1;
-- user_id 컬럼 타입 변경 (int → bigint)
ALTER TABLE posts_tbl
    MODIFY COLUMN user_id BIGINT;
-- 외래키 제약조건 다시 추가
ALTER TABLE posts_tbl
    ADD CONSTRAINT fk_posts_user_id
        FOREIGN KEY (user_id) REFERENCES users(user_id);

--5. post_scraps_tbl: user_id 외래키 int -> bigint변경
-- 기존 외래키 제약조건 제거
ALTER TABLE post_scraps_tbl
DROP FOREIGN KEY fk_post_scraps_TBL_users_TBL1;
-- user_id 컬럼 타입 변경 (int → bigint)
ALTER TABLE post_scraps_tbl
    MODIFY COLUMN user_id BIGINT;
-- 외래키 제약조건 다시 추가
ALTER TABLE post_scraps_tbl
    ADD CONSTRAINT fk_post_scraps_user_id
        FOREIGN KEY (user_id) REFERENCES users(user_id);

-- 6. restaurant_comments_tbl: user_id 외래키 int -> bigint변경
-- 기존 외래키 제약조건 제거
ALTER TABLE restaurant_comments_tbl
DROP FOREIGN KEY fk_restaurant_comments_TBL_users_TBL1;

-- user_id 컬럼 타입 변경 (int → bigint)
ALTER TABLE restaurant_comments_tbl
    MODIFY COLUMN user_id BIGINT;

-- 외래키 제약조건 다시 추가
ALTER TABLE restaurant_comments_tbl
    ADD CONSTRAINT fk_restaurant_comments_user_id
        FOREIGN KEY (user_id) REFERENCES users(user_id);