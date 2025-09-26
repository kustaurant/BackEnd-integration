-- user status 를 VARCHAR -> ENUM으로 타입 변경
ALTER TABLE users_tbl
    MODIFY COLUMN status ENUM('ACTIVE', 'DELETED') NOT NULL;

-- 토큰redis관리에 따른 access_token, refresh_token 컬럼 삭제
ALTER TABLE users_tbl
DROP COLUMN access_token,
DROP COLUMN refresh_token;