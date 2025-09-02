SET FOREIGN_KEY_CHECKS = 0;

/* admin */ /* admin */ /* admin */

CREATE TABLE `admin_feedback` (
  `feedback_id` int NOT NULL AUTO_INCREMENT,
  `comment` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` bigint unsigned NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`feedback_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `fk_feedbacks_user` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `admin_home_modal` (
    `modal_id` int NOT NULL AUTO_INCREMENT,
    `modal_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `modal_body` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    `expired_at` datetime DEFAULT NULL,
    PRIMARY KEY (`modal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `admin_notice` (
    `notice_id` int NOT NULL AUTO_INCREMENT,
    `notice_title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `notice_href` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `created_at` date DEFAULT NULL,
    `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `admin_report` (
    `id` bigint unsigned NOT NULL AUTO_INCREMENT,
    `user_id` bigint unsigned NOT NULL,
    `target_id` bigint unsigned NOT NULL,
    `target_type` enum('POST','POST_COMMENT','EVALUATION','EVAL_COMMENT') NOT NULL,
    `reason` enum('SPAM','OFFENSIVE','INAPPROPRIATE','COPYRIGHT','OTHER') NOT NULL,
    `status` enum('PENDING','RESOLVED','REJECTED') NOT NULL DEFAULT 'PENDING',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_report_user` (`user_id`),
    KEY `idx_report_target` (`target_type`,`target_id`),
    CONSTRAINT `fk_report_user` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/* evaluation */ /* evaluation */ /* evaluation */

CREATE TABLE `evaluation` (
  `evaluation_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `restaurant_id` bigint unsigned NOT NULL,
  `evaluation_score` double NOT NULL,
  `status` varchar(20) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  `body` varchar(1000) DEFAULT NULL,
  `img_url` varchar(300) DEFAULT NULL,
  `like_count` int DEFAULT NULL,
  `dislike_count` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`evaluation_id`),
  KEY `fk_evaluations_TBL_users_TBL_idx` (`user_id`),
  KEY `fk_evaluations_TBL_restaurants_TBL1_idx` (`restaurant_id`),
  CONSTRAINT `fk_evaluations_TBL_restaurants_TBL1` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`restaurant_id`),
  CONSTRAINT `fk_evaluations_user` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `evaluation_comment` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `restaurant_id` bigint unsigned NOT NULL,
  `evaluation_id` bigint NOT NULL,
  `body` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('ACTIVE','DELETED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE',
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  `like_count` int NOT NULL DEFAULT '0',
  `dislike_count` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_restaurant_comments_TBL_users_TBL1_idx` (`user_id`),
  KEY `fk_restaurant_comments_TBL_restaurant_TBL1_idx` (`restaurant_id`),
  KEY `fk_restaurant_comments_TBL_evaluations_TBL1_idx` (`evaluation_id`),
  CONSTRAINT `fk_rest_comments_user` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`),
  CONSTRAINT `fk_restaurant_comment_TBL_restaurant_TBL1` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`restaurant_id`),
  CONSTRAINT `fk_restaurant_comments_evaluation` FOREIGN KEY (`evaluation_id`) REFERENCES `evaluation` (`evaluation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `evaluation_comment_reaction` (
   `id` bigint unsigned NOT NULL AUTO_INCREMENT,
   `eval_comment_id` bigint unsigned NOT NULL,
   `user_id` bigint unsigned NOT NULL,
   `reaction` enum('LIKE','DISLIKE') NOT NULL DEFAULT 'LIKE',
   PRIMARY KEY (`id`),
   UNIQUE KEY `uq_eval_like` (`eval_comment_id`,`user_id`),
   KEY `idx_eval_comment` (`eval_comment_id`),
   KEY `fk_user` (`user_id`),
   CONSTRAINT `fk_eval_comment` FOREIGN KEY (`eval_comment_id`) REFERENCES `evaluation_comment` (`id`),
   CONSTRAINT `fk_user` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `evaluation_reaction` (
   `id` bigint unsigned NOT NULL AUTO_INCREMENT,
   `user_id` bigint unsigned NOT NULL,
   `evaluation_id` bigint DEFAULT NULL,
   `reaction` enum('LIKE','DISLIKE') NOT NULL,
   PRIMARY KEY (`id`),
   KEY `fk_likes_TBL_users_TBL1_idx` (`user_id`),
   KEY `fk_likes_TBL_evaluations_TBL1_idx` (`evaluation_id`),
   CONSTRAINT `fk_comment_likes_evaluation` FOREIGN KEY (`evaluation_id`) REFERENCES `evaluation` (`evaluation_id`),
   CONSTRAINT `fk_rest_cmt_likes_user` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `evaluation_situation` (
                                        `evaluation_id` bigint NOT NULL,
                                        `situation_id` bigint NOT NULL,
                                        KEY `fk_evaluation_item_scores_TBL_evaluations_TBL1_idx` (`evaluation_id`),
                                        KEY `fk_evaluation_item_scores_TBL_situation_categories_TBL1_idx` (`situation_id`),
                                        CONSTRAINT `fk_evaluation_situations_evaluation` FOREIGN KEY (`evaluation_id`) REFERENCES `evaluation` (`evaluation_id`),
                                        CONSTRAINT `fk_evaluation_situations_situation` FOREIGN KEY (`situation_id`) REFERENCES `restaurant_situation` (`situation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;


/* post */ /* post */ /* post */


CREATE TABLE `post` (
    `post_id` bigint unsigned NOT NULL AUTO_INCREMENT,
    `user_id` bigint unsigned NOT NULL,
    `post_title` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `post_body` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
    `created_at` datetime NOT NULL,
    `updated_at` datetime DEFAULT NULL,
    `post_category` enum('FREE','COLUMN','SUGGESTION') COLLATE utf8mb4_unicode_ci NOT NULL,
    `post_visit_count` int DEFAULT '0',
    `net_likes` int DEFAULT NULL,
    PRIMARY KEY (`post_id`),
    KEY `fk_posts_TBL_users_TBL1_idx` (`user_id`),
    CONSTRAINT `fk_posts_user` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `post_comment` (
    `post_comment_id` bigint unsigned NOT NULL AUTO_INCREMENT,
    `post_id` bigint unsigned NOT NULL,
    `user_id` bigint unsigned NOT NULL,
    `comment_body` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `parent_comment_id` bigint unsigned DEFAULT NULL,
    `status` enum('ACTIVE','PENDING','DELETED') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE',
    `created_at` datetime NOT NULL,
    `updated_at` datetime DEFAULT NULL,
    `like_count` int DEFAULT '0',
    PRIMARY KEY (`post_comment_id`),
    KEY `fk_post_comments_TBL_users_TBL1_idx` (`user_id`),
    KEY `fk_post_comments_TBL_posts_TBL1_idx` (`post_id`),
    CONSTRAINT `fk_post_comment_TBL_post_TBL1` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`),
    CONSTRAINT `fk_post_comments_user` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `post_comment_reaction` (
 `post_comment_id` bigint unsigned NOT NULL,
 `user_id` bigint unsigned NOT NULL,
 `reaction` enum('LIKE','DISLIKE') NOT NULL,
 `reacted_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 PRIMARY KEY (`post_comment_id`,`user_id`),
 KEY `fk_reaction_user` (`user_id`),
 CONSTRAINT `fk_reaction_post_comment` FOREIGN KEY (`post_comment_id`) REFERENCES `post_comment` (`post_comment_id`) ON DELETE CASCADE,
 CONSTRAINT `fk_reaction_user` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `post_photo` (
  `photo_id` int NOT NULL AUTO_INCREMENT,
  `post_id` bigint unsigned NOT NULL,
  `photo_img_url` varchar(1000) NOT NULL,
  `status` varchar(20) NOT NULL,
  PRIMARY KEY (`photo_id`),
  KEY `fk_post_photoes_TBL_posts_TBL1_idx` (`post_id`),
  CONSTRAINT `fk_post_photoe_TBL_post_TBL1` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `post_reaction` (
 `post_id` bigint unsigned NOT NULL,
 `user_id` bigint unsigned NOT NULL,
 `reaction` enum('LIKE','DISLIKE') NOT NULL,
 `reacted_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
 PRIMARY KEY (`post_id`,`user_id`),
 KEY `idx_user_post` (`user_id`,`post_id`),
 CONSTRAINT `fk_pur_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`),
 CONSTRAINT `fk_pur_user` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `post_scrap` (
  `post_id` bigint unsigned NOT NULL,
  `user_id` bigint unsigned NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`post_id`,`user_id`),
  UNIQUE KEY `uq_post_scraps_user_post` (`user_id`,`post_id`),
  KEY `fk_post_scraps_TBL_users_TBL1_idx` (`user_id`),
  KEY `fk_post_scraps_TBL_posts_TBL1_idx` (`post_id`),
  KEY `idx_user_post` (`user_id`,`post_id`),
  CONSTRAINT `fk_post_scrap_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`post_id`) ON UPDATE CASCADE,
  CONSTRAINT `fk_post_scraps_user` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;


/* restaurant */ /* restaurant */ /* restaurant */


CREATE TABLE `restaurant` (
  `restaurant_id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `restaurant_name` varchar(200) NOT NULL,
  `restaurant_type` varchar(100) NOT NULL,
  `restaurant_position` varchar(60) DEFAULT NULL,
  `restaurant_address` varchar(300) DEFAULT NULL,
  `restaurant_tel` varchar(30) DEFAULT NULL,
  `restaurant_url` varchar(200) NOT NULL,
  `restaurant_img_url` varchar(1000) DEFAULT NULL,
  `restaurant_cuisine` varchar(30) NOT NULL,
  `latitude` double DEFAULT NULL,
  `longitude` double DEFAULT NULL,
  `status` varchar(20) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  `visit_count` int DEFAULT '0',
  `restaurant_evaluation_count` int NOT NULL DEFAULT '0',
  `restaurant_score_sum` double NOT NULL DEFAULT '0',
  `main_tier` int NOT NULL DEFAULT '-1',
  `partnership_info` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`restaurant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `restaurant_favorite` (
    `favorite_id` int NOT NULL AUTO_INCREMENT,
    `user_id` bigint unsigned NOT NULL,
    `restaurant_id` bigint unsigned NOT NULL,
    `status` varchar(20) NOT NULL,
    `created_at` datetime NOT NULL,
    `updated_at` datetime DEFAULT NULL,
    PRIMARY KEY (`favorite_id`),
    KEY `fk_restaurant_favorite_TBL_users_TBL1_idx` (`user_id`),
    KEY `fk_restaurant_favorite_TBL_restaurants_TBL1_idx` (`restaurant_id`),
    CONSTRAINT `fk_rest_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`),
    CONSTRAINT `fk_restaurant_favorite_TBL_restaurants_TBL1` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`restaurant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `restaurant_menus` (
    `menu_id` int NOT NULL,
    `restaurant_id` bigint unsigned NOT NULL,
    `menu_name` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `menu_price` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    `naver_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    `menu_img_url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (`menu_id`),
    KEY `fk_restaurant_menus_TBL_restaurants_TBL1_idx` (`restaurant_id`),
    CONSTRAINT `fk_restaurant_menus_TBL_restaurants_TBL1` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`restaurant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `restaurant_rating` (
 `restaurant_id` bigint unsigned NOT NULL,
 `score` double NOT NULL,
 `tier` int NOT NULL,
 `is_temp` tinyint(1) NOT NULL DEFAULT '0',
 `rated_at` datetime(6) NOT NULL,
 PRIMARY KEY (`restaurant_id`),
 CONSTRAINT `fk_rating_restaurant` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`restaurant_id`) ON DELETE CASCADE ON UPDATE CASCADE,
 CONSTRAINT `chk_tier_range` CHECK ((((`tier` >= 1) and (`tier` <= 5)) or (`tier` = -(1))))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `restaurant_situation` (
    `situation_id` bigint NOT NULL AUTO_INCREMENT,
    `situation_name` varchar(45) NOT NULL,
    PRIMARY KEY (`situation_id`),
    UNIQUE KEY `category_name_UNIQUE` (`situation_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `restaurant_situation_relation` (
 `relation_id` bigint NOT NULL AUTO_INCREMENT,
 `restaurant_id` bigint unsigned NOT NULL,
 `situation_id` bigint NOT NULL,
 `data_count` int DEFAULT '0',
 PRIMARY KEY (`relation_id`),
 KEY `fk_restaurant_categories_TBL_categories1_idx` (`situation_id`),
 KEY `fk_restaurant_categories_TBL_restaurant_TBL1_idx` (`restaurant_id`),
 CONSTRAINT `fk_restaurant_categories_TBL_restaurant_TBL1` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurant` (`restaurant_id`),
 CONSTRAINT `fk_restaurant_situation_relations_situation` FOREIGN KEY (`situation_id`) REFERENCES `restaurant_situation` (`situation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

/* user */ /* user */ /* user */


CREATE TABLE `users_tbl` (
 `user_id` bigint unsigned NOT NULL AUTO_INCREMENT,
 `provider_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
 `login_api` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
 `nickname` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
 `email` varchar(40) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
 `phone_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
 `status` enum('ACTIVE','DELETED') COLLATE utf8mb4_unicode_ci NOT NULL,
 `created_at` datetime NOT NULL,
 `updated_at` datetime DEFAULT NULL,
 `role` enum('USER','ADMIN') COLLATE utf8mb4_unicode_ci NOT NULL,
 PRIMARY KEY (`user_id`),
 UNIQUE KEY `user_nickname_UNIQUE` (`nickname`),
 UNIQUE KEY `user_id_UNIQUE` (`user_id`),
 UNIQUE KEY `nickname` (`nickname`),
 UNIQUE KEY `user_email_UNIQUE` (`email`),
 UNIQUE KEY `user_token_id_UNIQUE` (`provider_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `user_stats` (
  `user_id` bigint unsigned NOT NULL,
  `saved_rest_cnt` int NOT NULL DEFAULT '0',
  `rated_rest_cnt` int NOT NULL DEFAULT '0',
  `comm_post_cnt` int NOT NULL DEFAULT '0',
  `comm_comment_cnt` int NOT NULL DEFAULT '0',
  `comm_saved_post_cnt` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_user_stats_user` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;