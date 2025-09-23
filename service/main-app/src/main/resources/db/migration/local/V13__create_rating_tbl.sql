CREATE TABLE restaurant_rating (
    restaurant_id INT NOT NULL,
    score DOUBLE NOT NULL,
    tier INT NOT NULL,
    is_temp TINYINT(1) NOT NULL DEFAULT 0,
    rated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (restaurant_id),
    CONSTRAINT fk_rating_restaurant FOREIGN KEY (restaurant_id)
        REFERENCES restaurants_tbl(restaurant_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT chk_tier_range CHECK ((tier >= 1 AND tier <= 5) OR tier = -1)
);