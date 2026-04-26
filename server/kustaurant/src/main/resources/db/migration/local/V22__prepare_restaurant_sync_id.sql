-- restaurant_url -> place_id
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
