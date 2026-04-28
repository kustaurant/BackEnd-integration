-- local V21 ~ V23

CREATE TABLE restaurant_crawl_raw (
                                      id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                      source_place_id VARCHAR(64) NOT NULL COMMENT '네이버 place id',
                                      source_url VARCHAR(255) NOT NULL COMMENT '크롤링한 네이버 식당 URL',
                                      place_name VARCHAR(128) NOT NULL COMMENT '크롤링된 식당명',
                                      category VARCHAR(128) NULL COMMENT '네이버 분류명',
                                      restaurant_address VARCHAR(255) NULL COMMENT '식당 주소',
                                      phone_number VARCHAR(32) NULL COMMENT '전화번호',
                                      latitude DOUBLE NULL COMMENT '위도',
                                      longitude DOUBLE NULL COMMENT '경도',
                                      image_url VARCHAR(512) NULL COMMENT '대표 이미지 URL',
                                      crawl_scope VARCHAR(32) NOT NULL COMMENT '건입~중문 / 중문~어대 / 건입 / 후문 / 구의역 / 단건',
                                      crawl_status VARCHAR(32) NOT NULL COMMENT 'SUCCESS / FAILED',
                                      crawl_error_message VARCHAR(500) NULL COMMENT '실패 시 에러 메시지',
                                      created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                      CONSTRAINT uk_restaurant_crawl_raw_place_id UNIQUE (source_place_id)
) COMMENT='식당 크롤 raw 테이블';

CREATE INDEX idx_restaurant_crawl_raw_scope ON restaurant_crawl_raw (crawl_scope);


CREATE TABLE restaurant_menu_crawl_raw (
                                           id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                           restaurant_raw_id BIGINT UNSIGNED NOT NULL COMMENT 'restaurant_crawl_raw.id',
                                           menu_name VARCHAR(128) NOT NULL COMMENT '메뉴명',
                                           menu_price VARCHAR(64) NULL COMMENT '메뉴 가격 텍스트',
                                           menu_image_url VARCHAR(512) NULL COMMENT '메뉴 이미지 URL',
                                           CONSTRAINT fk_restaurant_menu_crawl_raw_restaurant_crawl_raw
                                               FOREIGN KEY (restaurant_raw_id) REFERENCES restaurant_crawl_raw(id)
                                                   ON DELETE CASCADE,
                                           created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                           updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='식당 메뉴 raw 테이블';

CREATE INDEX idx_restaurant_menu_crawl_raw_restaurant_raw_id ON restaurant_menu_crawl_raw (restaurant_raw_id);


ALTER TABLE restaurant ADD COLUMN place_id VARCHAR(64) NULL AFTER restaurant_id;

UPDATE restaurant
SET place_id = COALESCE(
        NULLIF(
                REGEXP_REPLACE(
                        SUBSTRING_INDEX(SUBSTRING_INDEX(restaurant_url, '/place/', -1), '?', 1),
                        '[^0-9]',
                        ''
                ),
                ''
        ),
        CAST(restaurant_id AS CHAR)
               )
WHERE place_id IS NULL;

-- In case of duplicated extracted place_id, keep key uniqueness with legacy suffix.
UPDATE restaurant r
    JOIN (
    SELECT place_id
    FROM restaurant
    WHERE place_id IS NOT NULL
    GROUP BY place_id
    HAVING COUNT(*) > 1
    ) dup ON r.place_id = dup.place_id
    SET r.place_id = CONCAT(r.place_id, '_', r.restaurant_id);

ALTER TABLE restaurant MODIFY COLUMN place_id VARCHAR(64) NOT NULL AFTER restaurant_id;
ALTER TABLE restaurant ADD CONSTRAINT uk_restaurant_place_id UNIQUE (place_id);
ALTER TABLE restaurant DROP COLUMN restaurant_url;

ALTER TABLE restaurant
    ADD COLUMN content_hash VARCHAR(64) NULL AFTER place_id,
    ADD COLUMN menu_hash VARCHAR(64) NULL AFTER content_hash;

CREATE TABLE restaurant_sync_candidate (
                                           id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                           place_id VARCHAR(64) NOT NULL,
                                           candidate_type VARCHAR(16) NOT NULL COMMENT 'NEW/CLOSED',
                                           candidate_status VARCHAR(16) NOT NULL COMMENT 'PENDING/APPROVED/REJECTED',
                                           reason VARCHAR(255) NULL,
                                           reviewed_by VARCHAR(64) NULL,
                                           reviewed_at DATETIME NULL,
                                           applied_at DATETIME NULL,
                                           created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                           updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                           CONSTRAINT uk_restaurant_sync_candidate_pending UNIQUE (place_id, candidate_type, candidate_status)
);

CREATE INDEX idx_restaurant_sync_candidate_status_created_at
    ON restaurant_sync_candidate (candidate_status, created_at);

ALTER TABLE restaurant_menus MODIFY COLUMN menu_id INT NOT NULL AUTO_INCREMENT;

ALTER TABLE restaurant_menus DROP COLUMN naver_type;
