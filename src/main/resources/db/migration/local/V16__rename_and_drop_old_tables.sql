/* 구 post_comment_like/dislike 테이블들 제거 */
DROP TABLE IF EXISTS post_comment_likes_tbl;
DROP TABLE IF EXISTS post_comment_dislikes_tbl;

/* eval, post 관련 테이블들 이름 변경 */
RENAME TABLE eval_comm_user_reaction TO evaluation_comment_reaction;
RENAME TABLE eval_user_reaction TO evaluation_reaction;
RENAME TABLE eval_comment To evaluation_comment;
RENAME TABLE post_user_reaction TO post_reaction;
RENAME TABLE post_comments_tbl TO post_comments;
RENAME TABLE post_photoes_tbl TO post_photos;
RENAME TABLE post_scraps_tbl TO post_scraps;