-- =============================================================
--  목적: login_api 값을 NAVER/APPLE 대문자로 통일하고 ENUM 타입으로 제약 걸기
-- =============================================================

-- 값 대문자로 정규화
UPDATE users_tbl
SET    login_api = UPPER(login_api)
WHERE  login_api IN ('naver', 'apple');

-- 열 타입을 ENUM으로 변경
ALTER TABLE users_tbl
    MODIFY login_api ENUM('NAVER','APPLE') NOT NULL;