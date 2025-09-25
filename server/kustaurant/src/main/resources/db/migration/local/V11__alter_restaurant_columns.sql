DELETE FROM restaurants_tbl
WHERE restaurant_latitude = 'no_restaurant' OR restaurant_longitude = 'no_restaurant';

ALTER TABLE restaurants_tbl
    CHANGE COLUMN restaurant_latitude  latitude  DOUBLE,
    CHANGE COLUMN restaurant_longitude longitude DOUBLE,
    DROP COLUMN restaurant_visit_count;