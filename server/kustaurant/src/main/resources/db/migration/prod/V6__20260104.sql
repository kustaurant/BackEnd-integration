-- [local의 V19에 해당]

-- 인덱스 활용을 위해 rating 테이블 필드 추가
ALTER TABLE restaurant_rating
    ADD COLUMN has_tier BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE restaurant_rating
SET has_tier = (tier >= 0);

-- 인덱스 추가
CREATE INDEX idx_rr_temp_hastier_order
    ON restaurant_rating (is_temp, has_tier DESC, tier, final_score DESC, restaurant_id);

CREATE INDEX idx_rsr_dc_sid_rid
    ON restaurant_situation_relation (data_count, situation_id, restaurant_id);

CREATE INDEX idx_r_status_id
    ON restaurant (status, restaurant_id);

CREATE INDEX idx_r_status_cuisine_id
    ON restaurant (status, restaurant_cuisine, restaurant_id);

CREATE INDEX idx_r_status_position_id
    ON restaurant (status, restaurant_position, restaurant_id);