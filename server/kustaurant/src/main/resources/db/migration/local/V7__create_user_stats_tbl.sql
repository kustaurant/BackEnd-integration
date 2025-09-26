-- 신규 테이블 user_stats_tbl 생성 --
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
)