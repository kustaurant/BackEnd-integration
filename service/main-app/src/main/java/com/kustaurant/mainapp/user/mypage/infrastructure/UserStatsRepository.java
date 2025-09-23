package com.kustaurant.mainapp.user.mypage.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserStatsRepository extends JpaRepository<UserStatsEntity, Long> {
    @Modifying
    @Query(value = """
        UPDATE user_stats
        SET saved_rest_cnt = GREATEST(0, saved_rest_cnt + :delta)
        WHERE user_id = :userId
        """, nativeQuery = true)
    int addSavedRestCnt(@Param("userId") Long userId, @Param("delta") int delta);

    @Modifying
    @Query(value = """
        UPDATE user_stats
        SET rated_rest_cnt = GREATEST(0, rated_rest_cnt + :delta)
        WHERE user_id = :userId
        """, nativeQuery = true)
    int addRatedRestCnt(@Param("userId") Long userId, @Param("delta") int delta);

    @Modifying
    @Query(value = """
        UPDATE user_stats
        SET comm_post_cnt = GREATEST(0, comm_post_cnt + :delta)
        WHERE user_id = :userId
        """, nativeQuery = true)
    int addCommPostCnt(@Param("userId") Long userId, @Param("delta") int delta);

    @Modifying
    @Query(value = """
        UPDATE user_stats
        SET comm_comment_cnt = GREATEST(0, comm_comment_cnt + :delta)
        WHERE user_id = :userId
        """, nativeQuery = true)
    int addCommCommentCnt(@Param("userId") Long userId, @Param("delta") int delta);

    @Modifying
    @Query(value = """
        UPDATE user_stats
        SET comm_saved_post_cnt = GREATEST(0, comm_saved_post_cnt + :delta)
        WHERE user_id = :userId
        """, nativeQuery = true)
    int addCommSavedPostCnt(@Param("userId") Long userId, @Param("delta") int delta);
}
