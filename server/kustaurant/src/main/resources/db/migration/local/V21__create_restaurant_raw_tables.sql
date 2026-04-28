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
