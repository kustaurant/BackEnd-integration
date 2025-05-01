-- 1. 테이블 이름 변경
ALTER TABLE evaluation_item_scores_tbl
    RENAME TO evaluation_situations_tbl;

-- 2. restaurant_situation_relations_tbl에서 relation_id를 가독성을 위해 맨 앞으로 이동
ALTER TABLE restaurant_situation_relations_tbl
    MODIFY COLUMN relation_id INT NOT NULL AUTO_INCREMENT FIRST;

-- 3. 사용하지 않는 테이블 제거
DROP TABLE IF EXISTS restaurant_hashtag_relations_tbl;
DROP TABLE IF EXISTS restaurant_hashtags_tbl;

-- 4. users_tbl 컬럼명 변경 + 패스워드 컬럼 제거
ALTER TABLE users_tbl
    CHANGE COLUMN user_nickname nickname  VARCHAR(20)  NOT NULL UNIQUE,
    CHANGE COLUMN user_email    email     VARCHAR(40),
    CHANGE COLUMN user_role role ENUM('USER', 'ADMIN') NOT NULL;

ALTER TABLE users_tbl
DROP COLUMN user_password;
