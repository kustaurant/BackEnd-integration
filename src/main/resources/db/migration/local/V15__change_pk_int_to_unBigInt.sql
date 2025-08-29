/* restaurant, post의 pk 를 int에서 unsigned big int로 변경 */

-- 현재 FK들 요약
SELECT TABLE_NAME, CONSTRAINT_NAME
FROM information_schema.TABLE_CONSTRAINTS
WHERE CONSTRAINT_SCHEMA = DATABASE()
  AND CONSTRAINT_TYPE = 'FOREIGN KEY';

-- posts_tbl를 참조하는 FK
ALTER TABLE `post_comments`  DROP FOREIGN KEY `fk_post_comments_TBL_posts_TBL1`;
ALTER TABLE `post_photos`    DROP FOREIGN KEY `fk_post_photoes_TBL_posts_TBL1`;
ALTER TABLE `post_reaction`  DROP FOREIGN KEY `fk_pur_post`;
ALTER TABLE `post_scraps`    DROP FOREIGN KEY `fk_post_scraps_post`;

-- restaurants_tbl를 참조하는 FK
ALTER TABLE `evaluation_comment`                 DROP FOREIGN KEY `fk_restaurant_comments_TBL_restaurant_TBL1`;
ALTER TABLE `evaluations_tbl`                    DROP FOREIGN KEY `fk_evaluations_TBL_restaurants_TBL1`;
ALTER TABLE `restaurant_favorite_tbl`            DROP FOREIGN KEY `fk_restaurant_favorite_TBL_restaurants_TBL1`;
ALTER TABLE `restaurant_menus_tbl`               DROP FOREIGN KEY `fk_restaurant_menus_TBL_restaurants_TBL1`;
ALTER TABLE `restaurant_rating`                  DROP FOREIGN KEY `fk_rating_restaurant`;
ALTER TABLE `restaurant_situation_relations_tbl` DROP FOREIGN KEY `fk_restaurant_categories_TBL_restaurant_TBL1`;

-- posts_tbl 참조
ALTER TABLE `post_comments`  MODIFY COLUMN `post_id` BIGINT UNSIGNED NOT NULL;
ALTER TABLE `post_photos`    MODIFY COLUMN `post_id` BIGINT UNSIGNED NOT NULL;
ALTER TABLE `post_reaction`  MODIFY COLUMN `post_id` BIGINT UNSIGNED NOT NULL;
ALTER TABLE `post_scraps`    MODIFY COLUMN `post_id` BIGINT UNSIGNED NOT NULL;

-- restaurants_tbl 참조
ALTER TABLE `evaluation_comment`                 MODIFY COLUMN `restaurant_id` BIGINT UNSIGNED NOT NULL;
ALTER TABLE `evaluations_tbl`                    MODIFY COLUMN `restaurant_id` BIGINT UNSIGNED NOT NULL;
ALTER TABLE `restaurant_favorite_tbl`            MODIFY COLUMN `restaurant_id` BIGINT UNSIGNED NOT NULL;
ALTER TABLE `restaurant_menus_tbl`               MODIFY COLUMN `restaurant_id` BIGINT UNSIGNED NOT NULL;
ALTER TABLE `restaurant_rating`                  MODIFY COLUMN `restaurant_id` BIGINT UNSIGNED NOT NULL;
ALTER TABLE `restaurant_situation_relations_tbl` MODIFY COLUMN `restaurant_id` BIGINT UNSIGNED NOT NULL;

-- 부모 테이블 pk 컬럼 변경
ALTER TABLE `posts_tbl`
    MODIFY COLUMN `post_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT;

ALTER TABLE `restaurants_tbl`
    MODIFY COLUMN `restaurant_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT;

-- posts_tbl 참조 재생성
ALTER TABLE `post_comments`
    ADD CONSTRAINT `fk_post_comments_TBL_posts_TBL1`
        FOREIGN KEY (`post_id`) REFERENCES `posts_tbl`(`post_id`)
            ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE `post_photos`
    ADD CONSTRAINT `fk_post_photoes_TBL_posts_TBL1`
        FOREIGN KEY (`post_id`) REFERENCES `posts_tbl`(`post_id`)
            ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE `post_reaction`
    ADD CONSTRAINT `fk_pur_post`
        FOREIGN KEY (`post_id`) REFERENCES `posts_tbl`(`post_id`)
            ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE `post_scraps`
    ADD CONSTRAINT `fk_post_scraps_post`
        FOREIGN KEY (`post_id`) REFERENCES `posts_tbl`(`post_id`)
            ON DELETE NO ACTION ON UPDATE CASCADE;

-- restaurants_tbl 참조 재생성
ALTER TABLE `evaluation_comment`
    ADD CONSTRAINT `fk_restaurant_comments_TBL_restaurant_TBL1`
        FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants_tbl`(`restaurant_id`)
            ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE `evaluations_tbl`
    ADD CONSTRAINT `fk_evaluations_TBL_restaurants_TBL1`
        FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants_tbl`(`restaurant_id`)
            ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE `restaurant_favorite_tbl`
    ADD CONSTRAINT `fk_restaurant_favorite_TBL_restaurants_TBL1`
        FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants_tbl`(`restaurant_id`)
            ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE `restaurant_menus_tbl`
    ADD CONSTRAINT `fk_restaurant_menus_TBL_restaurants_TBL1`
        FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants_tbl`(`restaurant_id`)
            ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE `restaurant_rating`
    ADD CONSTRAINT `fk_rating_restaurant`
        FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants_tbl`(`restaurant_id`)
            ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `restaurant_situation_relations_tbl`
    ADD CONSTRAINT `fk_restaurant_categories_TBL_restaurant_TBL1`
        FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants_tbl`(`restaurant_id`)
            ON DELETE NO ACTION ON UPDATE NO ACTION;
