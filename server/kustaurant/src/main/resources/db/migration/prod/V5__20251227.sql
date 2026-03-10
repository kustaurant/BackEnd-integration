-- [local의 V18에 해당]

-- 칼럼 삭제
ALTER TABLE restaurant_rating
DROP COLUMN ai_review_count,
DROP COLUMN ai_positive_count,
DROP COLUMN ai_negative_count,
DROP COLUMN ai_score_sum,
DROP COLUMN ai_avg_score,
DROP COLUMN ai_adjusted_score,
DROP COLUMN ai_processed_at;

-- 식당 AI 분석 Job 테이블 생성
CREATE TABLE ai_analysis_job (
                                 id                BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

                                 version           BIGINT NOT NULL DEFAULT 0,

                                 restaurant_id     BIGINT UNSIGNED NOT NULL,

                                 status ENUM('PENDING', 'RUNNING', 'DONE', 'FAILED') NOT NULL DEFAULT 'PENDING',

                                 total_reviews     INT         NOT NULL DEFAULT 0,-- 이번 Job에서 처리해야 할 리뷰 개수
                                 processed_reviews INT         NOT NULL DEFAULT 0,-- 처리 완료된 리뷰 개수
                                 failed_reviews    INT         NOT NULL DEFAULT 0,-- 실패한 리뷰 개수

                                 started_at        DATETIME    NULL,              -- 실제 처리 시작 시간
                                 completed_at      DATETIME    NULL,              -- 완료(또는 실패) 시간

                                 error_message     TEXT        NULL,              -- 전체 Job이 FAILED일 때 요약 에러 메세지

                                 created_at        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- 인덱스
                                 INDEX idx_restaurant_created (restaurant_id, created_at),
                                 INDEX idx_status_created (status, created_at),

                                 CONSTRAINT fk_ai_analysis_job_restaurant_id
                                     FOREIGN KEY (restaurant_id) REFERENCES restaurant(restaurant_id)
                                         ON DELETE CASCADE
);

-- 각 리뷰 분석 결과 테이블
CREATE TABLE ai_analysis_review (
                                    id                 BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

                                    job_id             BIGINT UNSIGNED NOT NULL,           -- 어떤 ai_analysis_job에 속하는지
                                    restaurant_id      BIGINT UNSIGNED NOT NULL,           -- 어떤 식당에 대한 리뷰인지

                                    sentiment          ENUM('POSITIVE', 'NEGATIVE', 'NEUTRAL') NOT NULL,
                                    score              DOUBLE      NOT NULL,           -- AI가 계산한 점수

                                    analyzed_text      TEXT        NULL,               -- AI 분석에 사용된 텍스트 or 요약

                                    created_at         DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    updated_at         DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                    INDEX idx_job_id (job_id),
                                    INDEX idx_restaurant_id (restaurant_id),
                                    INDEX idx_restaurant_job (restaurant_id, job_id),

                                    CONSTRAINT fk_review_analysis_restaurant_id
                                        FOREIGN KEY (restaurant_id) REFERENCES restaurant(restaurant_id)
                                            ON DELETE CASCADE,

                                    CONSTRAINT fk_review_analysis_job_id
                                        FOREIGN KEY (job_id) REFERENCES ai_analysis_job(id)
);

-- AI 분석 결과 테이블
CREATE TABLE ai_summary (
                            restaurant_id           BIGINT UNSIGNED NOT NULL,      -- 식당 PK (Restaurant 테이블과 1:1)
                            last_job_id             BIGINT UNSIGNED NULL,          -- 마지막으로 반영된 ai_analysis_job.id

                            review_count            INT         NOT NULL,          -- 분석에 사용된 리뷰 개수
                            positive_review_count   INT         NOT NULL,          -- 긍정 리뷰 개수
                            negative_review_count   INT         NOT NULL,          -- 부정 리뷰 개수

                            total_score_sum         DOUBLE      NOT NULL,          -- 점수 합 (평균 재계산용)
                            avg_score               DOUBLE      NOT NULL,          -- 평균 점수 (서비스에서 바로 사용)

                            last_analyzed_at        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 마지막 분석/갱신 시점

                            created_at              DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at              DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                            PRIMARY KEY (restaurant_id),

                            CONSTRAINT fk_restaurant_ai_summary_restaurant_id
                                FOREIGN KEY (restaurant_id) REFERENCES restaurant(restaurant_id)
                                    ON DELETE CASCADE,

                            CONSTRAINT fk_restaurant_ai_summary_job_id
                                FOREIGN KEY (last_job_id) REFERENCES ai_analysis_job(id)
                                    ON DELETE SET NULL
);