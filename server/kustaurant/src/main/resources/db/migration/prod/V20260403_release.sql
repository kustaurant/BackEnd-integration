-- local V20 ~ V20
CREATE TABLE ig_crawl_raw (
                              id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                              source_account VARCHAR(64) NOT NULL COMMENT '크롤링한 인스타 계정',
                              short_code VARCHAR(32) NOT NULL COMMENT '인스타 게시글 shortcode (고유 값) ',
                              post_url VARCHAR(255) NOT NULL COMMENT '게시글 전체 URL (변동 가능)',
                              restaurant_name VARCHAR(255) NOT NULL COMMENT '제휴 음식점명',
                              benefit VARCHAR(255) NOT NULL COMMENT '혜택 내용',
                              location VARCHAR(255) NOT NULL COMMENT '위치 정보',
                              phone_number VARCHAR(32) NULL COMMENT '전화번호',
                              target VARCHAR(32) NOT NULL COMMENT '제휴 대상 (enum)',
                              created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              CONSTRAINT uk_ig_crawl_raw_source_shortcode UNIQUE (source_account, short_code)
) COMMENT='인스타 크롤링 raw 결과 테이블';

CREATE INDEX idx_ig_crawl_raw_source ON ig_crawl_raw (source_account);
CREATE INDEX idx_ig_crawl_raw_target ON ig_crawl_raw (target);

CREATE TABLE restaurant_partnership (
                                        id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                        restaurant_id BIGINT NULL,
                                        restaurant_name VARCHAR(255) NOT NULL,
                                        benefit VARCHAR(255) NOT NULL,
                                        location_text VARCHAR(255) NOT NULL,
                                        contact_phone VARCHAR(32),
                                        source_account VARCHAR(64) NOT NULL,
                                        post_url VARCHAR(255) NOT NULL,
                                        match_status VARCHAR(32) NOT NULL,
                                        target VARCHAR(32) NOT NULL,
                                        created_at DATETIME NOT NULL,
                                        updated_at DATETIME NOT NULL,
                                        CONSTRAINT uk_restaurant_partnership_post_url UNIQUE (post_url)
);