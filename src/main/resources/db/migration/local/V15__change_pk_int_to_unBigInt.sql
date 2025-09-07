/* restaurant, post, post_comment의 pk 를 int에서 unsigned big int로 변경 */

-- post comment id부분 변경


-- 현재 FK들 요약
SELECT TABLE_NAME, CONSTRAINT_NAME
FROM information_schema.TABLE_CONSTRAINTS
WHERE CONSTRAINT_SCHEMA = DATABASE()
  AND CONSTRAINT_TYPE = 'FOREIGN KEY';

-- post를 참조하는 FK
ALTER TABLE `post_comment`  DROP FOREIGN KEY `fk_post_comments_TBL_posts_TBL1`;
ALTER TABLE `post_photo`    DROP FOREIGN KEY `fk_post_photoes_TBL_posts_TBL1`;
ALTER TABLE `post_reaction` DROP FOREIGN KEY `fk_pur_post`;
ALTER TABLE `post_scrap`    DROP FOREIGN KEY `fk_post_scraps_post`;

-- post_comment를 참조하는 FK
ALTER TABLE post_comment_reaction DROP FOREIGN KEY fk_reaction_post_comment;

-- restaurant를 참조하는 FK
ALTER TABLE `evaluation_comment`              DROP FOREIGN KEY `fk_restaurant_comments_TBL_restaurant_TBL1`;
ALTER TABLE `evaluation`                      DROP FOREIGN KEY `fk_evaluations_TBL_restaurants_TBL1`;
ALTER TABLE `restaurant_favorite`             DROP FOREIGN KEY `fk_restaurant_favorite_TBL_restaurants_TBL1`;
ALTER TABLE `restaurant_menus`                DROP FOREIGN KEY `fk_restaurant_menus_TBL_restaurants_TBL1`;
ALTER TABLE `restaurant_rating`               DROP FOREIGN KEY `fk_rating_restaurant`;
ALTER TABLE `restaurant_situation_relation`   DROP FOREIGN KEY `fk_restaurant_categories_TBL_restaurant_TBL1`;

-- post 참조
ALTER TABLE `post_comment`  MODIFY COLUMN `post_id` BIGINT UNSIGNED NOT NULL;
ALTER TABLE `post_photo`    MODIFY COLUMN `post_id` BIGINT UNSIGNED NOT NULL;
ALTER TABLE `post_reaction`  MODIFY COLUMN `post_id` BIGINT UNSIGNED NOT NULL;
ALTER TABLE `post_scrap`    MODIFY COLUMN `post_id` BIGINT UNSIGNED NOT NULL;

-- post_comment 참조
ALTER TABLE post_comment_reaction MODIFY COLUMN post_comment_id BIGINT UNSIGNED NOT NULL;

-- restaurant 참조
ALTER TABLE `evaluation_comment`                 MODIFY COLUMN `restaurant_id` BIGINT UNSIGNED NOT NULL;
ALTER TABLE `evaluation`                         MODIFY COLUMN `restaurant_id` BIGINT UNSIGNED NOT NULL;
ALTER TABLE `restaurant_favorite`                MODIFY COLUMN `restaurant_id` BIGINT UNSIGNED NOT NULL;
ALTER TABLE `restaurant_menus`                   MODIFY COLUMN `restaurant_id` BIGINT UNSIGNED NOT NULL;
ALTER TABLE `restaurant_rating`                  MODIFY COLUMN `restaurant_id` BIGINT UNSIGNED NOT NULL;
ALTER TABLE `restaurant_situation_relation`      MODIFY COLUMN `restaurant_id` BIGINT UNSIGNED NOT NULL;

-- 부모 테이블 pk 컬럼 변경
ALTER TABLE `post` MODIFY COLUMN `post_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT;
ALTER TABLE post_comment CHANGE COLUMN comment_id post_comment_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT;
ALTER TABLE `restaurant` MODIFY COLUMN `restaurant_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT;

ALTER TABLE post_comment MODIFY COLUMN parent_comment_id BIGINT UNSIGNED NULL;
-- post 참조 재생성
ALTER TABLE `post_comment`
    ADD CONSTRAINT `fk_post_comment_TBL_post_TBL1`
        FOREIGN KEY (`post_id`) REFERENCES `post`(`post_id`)
            ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE `post_photo`
    ADD CONSTRAINT `fk_post_photoe_TBL_post_TBL1`
        FOREIGN KEY (`post_id`) REFERENCES `post`(`post_id`)
            ON DELETE NO ACTION ON UPDATE CASCADE;

ALTER TABLE `post_reaction`
    ADD CONSTRAINT `fk_pur_post`
        FOREIGN KEY (`post_id`) REFERENCES `post`(`post_id`)
            ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE `post_scrap`
    ADD CONSTRAINT `fk_post_scrap_post`
        FOREIGN KEY (`post_id`) REFERENCES `post`(`post_id`)
            ON DELETE NO ACTION ON UPDATE CASCADE;

-- post_comment 참조 재생성
ALTER TABLE post_comment_reaction
    ADD CONSTRAINT fk_reaction_post_comment
        FOREIGN KEY (post_comment_id)
            REFERENCES post_comment (post_comment_id)
            ON DELETE CASCADE;

-- restaurant참조 재생성
ALTER TABLE `evaluation_comment`
    ADD CONSTRAINT `fk_restaurant_comment_TBL_restaurant_TBL1`
        FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant`(`restaurant_id`)
            ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE `evaluation`
    ADD CONSTRAINT `fk_evaluations_TBL_restaurants_TBL1`
        FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant`(`restaurant_id`)
            ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE `restaurant_favorite`
    ADD CONSTRAINT `fk_restaurant_favorite_TBL_restaurants_TBL1`
        FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant`(`restaurant_id`)
            ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE `restaurant_menus`
    ADD CONSTRAINT `fk_restaurant_menus_TBL_restaurants_TBL1`
        FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant`(`restaurant_id`)
            ON DELETE NO ACTION ON UPDATE NO ACTION;

ALTER TABLE `restaurant_rating`
    ADD CONSTRAINT `fk_rating_restaurant`
        FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant`(`restaurant_id`)
            ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `restaurant_situation_relation`
    ADD CONSTRAINT `fk_restaurant_categories_TBL_restaurant_TBL1`
        FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant`(`restaurant_id`)
            ON DELETE NO ACTION ON UPDATE NO ACTION;

/* post_scrap 재정의 */
ALTER TABLE post_scrap
DROP PRIMARY KEY,
  MODIFY COLUMN post_id BIGINT UNSIGNED NOT NULL FIRST,
  MODIFY COLUMN user_id BIGINT UNSIGNED NOT NULL AFTER post_id,
  DROP COLUMN scrap_id,
  MODIFY COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER user_id,
  ADD PRIMARY KEY (post_id, user_id),
  ADD INDEX idx_user_post (user_id, post_id);