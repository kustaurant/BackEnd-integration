ALTER TABLE restaurant_rating
    ADD COLUMN ai_adjusted_score DOUBLE NOT NULL DEFAULT 0.0,
    CHANGE score self_score DOUBLE NOT NULL,
    CHANGE normalized_score final_score DOUBLE NOT NULL;