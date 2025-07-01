-- 1. evaluations_tbl PK를 BIGINT로 변경
-- 1.1 참조 테이블 FK 제약조건 삭제
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
-- 1.2 evaluations_tbl PK를 BIGINT로 변경
ALTER TABLE evaluations_tbl
    MODIFY COLUMN evaluation_id BIGINT NOT NULL AUTO_INCREMENT;
-- 1.3 참조 테이블 FK를 BIGINT로 변경 + FK 제약조건 설정
--  restaurant_comments_tbl
ALTER TABLE restaurant_comments_tbl
    MODIFY COLUMN evaluation_id BIGINT NOT NULL;
ALTER TABLE restaurant_comments_tbl
    ADD CONSTRAINT fk_restaurant_comments_evaluation
        FOREIGN KEY (evaluation_id)
            REFERENCES evaluations_tbl(evaluation_id);
--  restaurant_comment_likes_tbl
ALTER TABLE restaurant_comment_likes_tbl
    MODIFY COLUMN evaluation_id BIGINT;
ALTER TABLE restaurant_comment_likes_tbl
    ADD CONSTRAINT fk_comment_likes_evaluation
        FOREIGN KEY (evaluation_id)
            REFERENCES evaluations_tbl(evaluation_id);
--  restaurant_comment_dislikes_tbl
ALTER TABLE restaurant_comment_dislikes_tbl
    MODIFY COLUMN evaluation_id BIGINT;
ALTER TABLE restaurant_comment_dislikes_tbl
    ADD CONSTRAINT fk_comment_dislikes_evaluation
        FOREIGN KEY (evaluation_id)
            REFERENCES evaluations_tbl(evaluation_id);
--  evaluation_situations_tbl
ALTER TABLE evaluation_situations_tbl
    MODIFY COLUMN evaluation_id BIGINT NOT NULL;
ALTER TABLE evaluation_situations_tbl
    ADD CONSTRAINT fk_evaluation_situations_evaluation
        FOREIGN KEY (evaluation_id)
            REFERENCES evaluations_tbl(evaluation_id);
--  restaurant_comment_reports_tbl
ALTER TABLE restaurant_comment_reports_tbl
    MODIFY COLUMN evaluation_id BIGINT;
ALTER TABLE restaurant_comment_reports_tbl
    ADD CONSTRAINT fk_comment_reports_evaluation
        FOREIGN KEY (evaluation_id)
            REFERENCES evaluations_tbl(evaluation_id);

-- 2. evaluation_situations_tbl PK를 BIGINT로 변경
-- 2.1 참조 테이블 FK 제약조건 삭제
ALTER TABLE evaluation_situations_tbl
DROP FOREIGN KEY fk_evaluation_item_scores_TBL_situation_categories_TBL1;
ALTER TABLE restaurant_situation_relations_tbl
DROP FOREIGN KEY fk_restaurant_categories_TBL_categories1;
-- 2.2 evaluation_situations_tbl PK를 BIGINT로 변경
ALTER TABLE situations_tbl
    MODIFY COLUMN situation_id BIGINT NOT NULL AUTO_INCREMENT;
-- 2.3 참조 테이블 FK를 BIGINT로 변경 + FK 제약조건 설정
--  evaluation_situations_tbl
ALTER TABLE evaluation_situations_tbl
    MODIFY COLUMN situation_id BIGINT NOT NULL;
ALTER TABLE evaluation_situations_tbl
    ADD CONSTRAINT fk_evaluation_situations_situation
        FOREIGN KEY (situation_id)
            REFERENCES situations_tbl(situation_id);
--  restaurant_situation_relations_tbl
ALTER TABLE restaurant_situation_relations_tbl
    MODIFY COLUMN situation_id BIGINT NOT NULL;
ALTER TABLE restaurant_situation_relations_tbl
    ADD CONSTRAINT fk_restaurant_situation_relations_situation
        FOREIGN KEY (situation_id)
            REFERENCES situations_tbl(situation_id);


-- 3. restaurant_situation_relations_tbl의 PK를 BIGINT로 변경
ALTER TABLE restaurant_situation_relations_tbl
    MODIFY COLUMN relation_id BIGINT NOT NULL AUTO_INCREMENT;
