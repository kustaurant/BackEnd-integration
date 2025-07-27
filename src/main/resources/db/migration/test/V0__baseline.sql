SET FOREIGN_KEY_CHECKS = 0;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `eval_comm_user_reaction` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `eval_comment_id` bigint unsigned NOT NULL,
  `user_id` bigint unsigned NOT NULL,
  `reaction` enum('LIKE','DISLIKE') NOT NULL DEFAULT 'LIKE',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_eval_like` (`eval_comment_id`,`user_id`),
  KEY `idx_eval_comment` (`eval_comment_id`),
  KEY `fk_user` (`user_id`),
  CONSTRAINT `fk_eval_comment` FOREIGN KEY (`eval_comment_id`) REFERENCES `eval_comment` (`id`),
  CONSTRAINT `fk_user` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `eval_comment` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `restaurant_id` int NOT NULL,
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
  CONSTRAINT `fk_restaurant_comments_evaluation` FOREIGN KEY (`evaluation_id`) REFERENCES `evaluations_tbl` (`evaluation_id`),
  CONSTRAINT `fk_restaurant_comments_TBL_restaurant_TBL1` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants_tbl` (`restaurant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `eval_user_reaction` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `evaluation_id` bigint DEFAULT NULL,
  `reaction` enum('LIKE','DISLIKE') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_likes_TBL_users_TBL1_idx` (`user_id`),
  KEY `fk_likes_TBL_evaluations_TBL1_idx` (`evaluation_id`),
  CONSTRAINT `fk_comment_likes_evaluation` FOREIGN KEY (`evaluation_id`) REFERENCES `evaluations_tbl` (`evaluation_id`),
  CONSTRAINT `fk_rest_cmt_likes_user` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluation_situations_tbl` (
  `evaluation_id` bigint NOT NULL,
  `situation_id` bigint NOT NULL,
  KEY `fk_evaluation_item_scores_TBL_evaluations_TBL1_idx` (`evaluation_id`),
  KEY `fk_evaluation_item_scores_TBL_situation_categories_TBL1_idx` (`situation_id`),
  CONSTRAINT `fk_evaluation_situations_evaluation` FOREIGN KEY (`evaluation_id`) REFERENCES `evaluations_tbl` (`evaluation_id`),
  CONSTRAINT `fk_evaluation_situations_situation` FOREIGN KEY (`situation_id`) REFERENCES `situations_tbl` (`situation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `evaluations_tbl` (
  `evaluation_id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `restaurant_id` int NOT NULL,
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
  CONSTRAINT `fk_evaluations_TBL_restaurants_TBL1` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants_tbl` (`restaurant_id`),
  CONSTRAINT `fk_evaluations_user` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedbacks_tbl` (
  `feedback_id` int NOT NULL AUTO_INCREMENT,
  `comment` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` bigint unsigned NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`feedback_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `fk_feedbacks_user` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `home_modal_tbl` (
  `modal_id` int NOT NULL AUTO_INCREMENT,
  `modal_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `modal_body` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `expired_at` datetime DEFAULT NULL,
  PRIMARY KEY (`modal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notice_tbl` (
  `notice_id` int NOT NULL AUTO_INCREMENT,
  `notice_title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `notice_href` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` date DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_comment_dislikes_tbl` (
  `comment_dislike_id` int NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `comment_id` int NOT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`comment_dislike_id`),
  KEY `user_id` (`user_id`),
  KEY `comment_id` (`comment_id`),
  CONSTRAINT `fk_post_cmt_dislikes_user` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`),
  CONSTRAINT `post_comment_dislikes_tbl_ibfk_2` FOREIGN KEY (`comment_id`) REFERENCES `post_comments_tbl` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_comment_dislikes_tbl_old` (
  `comment_id` int NOT NULL,
  `user_id` bigint unsigned NOT NULL,
  PRIMARY KEY (`comment_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `fk_post_cmt_dislikes_user_old` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`),
  CONSTRAINT `post_comment_dislikes_tbl_old_ibfk_1` FOREIGN KEY (`comment_id`) REFERENCES `post_comments_tbl` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_comment_likes_tbl` (
  `comment_like_id` int NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `comment_id` int NOT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`comment_like_id`),
  KEY `user_id` (`user_id`),
  KEY `comment_id` (`comment_id`),
  CONSTRAINT `fk_post_cmt_likes_user` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`),
  CONSTRAINT `post_comment_likes_tbl_ibfk_2` FOREIGN KEY (`comment_id`) REFERENCES `post_comments_tbl` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_comment_likes_tbl_old` (
  `comment_id` int NOT NULL,
  `user_id` bigint unsigned NOT NULL,
  PRIMARY KEY (`comment_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `fk_post_cmt_likes_user_old` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`),
  CONSTRAINT `post_comment_likes_tbl_old_ibfk_1` FOREIGN KEY (`comment_id`) REFERENCES `post_comments_tbl` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_comments_tbl` (
  `comment_id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL,
  `user_id` bigint unsigned NOT NULL,
  `comment_body` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `parent_comment_id` int DEFAULT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  `like_count` int DEFAULT '0',
  PRIMARY KEY (`comment_id`),
  KEY `fk_post_comments_TBL_users_TBL1_idx` (`user_id`),
  KEY `fk_post_comments_TBL_posts_TBL1_idx` (`post_id`),
  CONSTRAINT `fk_post_comments_TBL_posts_TBL1` FOREIGN KEY (`post_id`) REFERENCES `posts_tbl` (`post_id`),
  CONSTRAINT `fk_post_comments_user` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_dislikes_tbl` (
  `post_dislikes_id` int NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `post_id` int NOT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`post_dislikes_id`),
  KEY `user_id` (`user_id`),
  KEY `post_id` (`post_id`),
  CONSTRAINT `fk_post_dislikes_user` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`),
  CONSTRAINT `post_dislikes_tbl_ibfk_2` FOREIGN KEY (`post_id`) REFERENCES `posts_tbl` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_dislikes_tbl_old` (
  `post_id` int NOT NULL,
  `user_id` bigint unsigned NOT NULL,
  PRIMARY KEY (`post_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `fk_post_dislikes_user_old` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`),
  CONSTRAINT `post_dislikes_tbl_old_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts_tbl` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_likes_tbl` (
  `post_likes_id` int NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `post_id` int NOT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`post_likes_id`),
  KEY `user_id` (`user_id`),
  KEY `post_id` (`post_id`),
  CONSTRAINT `fk_post_likes_user` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`),
  CONSTRAINT `post_likes_tbl_ibfk_2` FOREIGN KEY (`post_id`) REFERENCES `posts_tbl` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_likes_tbl_old` (
  `post_id` int NOT NULL,
  `user_id` bigint unsigned NOT NULL,
  PRIMARY KEY (`post_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `fk_post_likes_user_old` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`),
  CONSTRAINT `post_likes_tbl_old_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts_tbl` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_photoes_tbl` (
  `photo_id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL,
  `photo_img_url` varchar(1000) NOT NULL,
  `status` varchar(20) NOT NULL,
  PRIMARY KEY (`photo_id`),
  KEY `fk_post_photoes_TBL_posts_TBL1_idx` (`post_id`),
  CONSTRAINT `fk_post_photoes_TBL_posts_TBL1` FOREIGN KEY (`post_id`) REFERENCES `posts_tbl` (`post_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_scraps_tbl` (
  `scrap_id` int NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `post_id` int NOT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`scrap_id`),
  UNIQUE KEY `uq_post_scraps_user_post` (`user_id`,`post_id`),
  KEY `fk_post_scraps_TBL_users_TBL1_idx` (`user_id`),
  KEY `fk_post_scraps_TBL_posts_TBL1_idx` (`post_id`),
  CONSTRAINT `fk_post_scraps_post` FOREIGN KEY (`post_id`) REFERENCES `posts_tbl` (`post_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_post_scraps_user` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `posts_tbl` (
  `post_id` int NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `post_title` varchar(150) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `post_body` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  `post_category` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `post_visit_count` int DEFAULT '0',
  `net_likes` int DEFAULT NULL,
  PRIMARY KEY (`post_id`),
  KEY `fk_posts_TBL_users_TBL1_idx` (`user_id`),
  CONSTRAINT `fk_posts_user` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `report` (
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
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `restaurant_favorite_tbl` (
  `favorite_id` int NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `restaurant_id` int NOT NULL,
  `status` varchar(20) NOT NULL,
  `created_at` datetime NOT NULL,
  `updated_at` datetime DEFAULT NULL,
  PRIMARY KEY (`favorite_id`),
  KEY `fk_restaurant_favorite_TBL_users_TBL1_idx` (`user_id`),
  KEY `fk_restaurant_favorite_TBL_restaurants_TBL1_idx` (`restaurant_id`),
  CONSTRAINT `fk_rest_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`),
  CONSTRAINT `fk_restaurant_favorite_TBL_restaurants_TBL1` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants_tbl` (`restaurant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `restaurant_menus_tbl` (
  `menu_id` int NOT NULL,
  `restaurant_id` int NOT NULL,
  `menu_name` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `menu_price` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `naver_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `menu_img_url` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`menu_id`),
  KEY `fk_restaurant_menus_TBL_restaurants_TBL1_idx` (`restaurant_id`),
  CONSTRAINT `fk_restaurant_menus_TBL_restaurants_TBL1` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants_tbl` (`restaurant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `restaurant_situation_relations_tbl` (
  `relation_id` bigint NOT NULL AUTO_INCREMENT,
  `restaurant_id` int NOT NULL,
  `situation_id` bigint NOT NULL,
  `data_count` int DEFAULT '0',
  PRIMARY KEY (`relation_id`),
  KEY `fk_restaurant_categories_TBL_categories1_idx` (`situation_id`),
  KEY `fk_restaurant_categories_TBL_restaurant_TBL1_idx` (`restaurant_id`),
  CONSTRAINT `fk_restaurant_categories_TBL_restaurant_TBL1` FOREIGN KEY (`restaurant_id`) REFERENCES `restaurants_tbl` (`restaurant_id`),
  CONSTRAINT `fk_restaurant_situation_relations_situation` FOREIGN KEY (`situation_id`) REFERENCES `situations_tbl` (`situation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `restaurants_tbl` (
  `restaurant_id` int NOT NULL AUTO_INCREMENT,
  `restaurant_name` varchar(200) NOT NULL,
  `restaurant_type` varchar(100) NOT NULL,
  `restaurant_position` varchar(60) DEFAULT NULL,
  `restaurant_address` varchar(300) DEFAULT NULL,
  `restaurant_tel` varchar(30) DEFAULT NULL,
  `restaurant_url` varchar(200) NOT NULL,
  `restaurant_img_url` varchar(1000) DEFAULT NULL,
  `restaurant_visit_count` int NOT NULL DEFAULT '0',
  `restaurant_cuisine` varchar(30) NOT NULL,
  `restaurant_latitude` varchar(45) NOT NULL,
  `restaurant_longitude` varchar(45) NOT NULL,
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
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `situations_tbl` (
  `situation_id` bigint NOT NULL AUTO_INCREMENT,
  `situation_name` varchar(45) NOT NULL,
  PRIMARY KEY (`situation_id`),
  UNIQUE KEY `category_name_UNIQUE` (`situation_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_stats_tbl` (
  `user_id` bigint unsigned NOT NULL,
  `saved_rest_cnt` int NOT NULL DEFAULT '0',
  `rated_rest_cnt` int NOT NULL DEFAULT '0',
  `comm_post_cnt` int NOT NULL DEFAULT '0',
  `comm_comment_cnt` int NOT NULL DEFAULT '0',
  `comm_saved_post_cnt` int NOT NULL DEFAULT '0',
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_user_stats_user` FOREIGN KEY (`user_id`) REFERENCES `users_tbl` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;
SET FOREIGN_KEY_CHECKS = 1;