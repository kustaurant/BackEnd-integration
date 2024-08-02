package com.kustaurant.restauranttier.tab5_mypage.repository;

import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantFavorite;
import com.kustaurant.restauranttier.tab4_community.entity.Post;
import com.kustaurant.restauranttier.tab4_community.entity.PostComment;
import com.kustaurant.restauranttier.tab4_community.entity.PostScrap;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserApiRepository extends JpaRepository<User,Integer> {

    Optional<User> findByUserTokenId(String userTokenId);

    Optional<User> findByUserNickname(String userNickname);

    @Query("SELECT u " +
            "FROM User u " +
            "WHERE SIZE(u.evaluationList) > 0 " +
            "ORDER BY SIZE(u.evaluationList) DESC")
    List<User> findUsersWithEvaluationCountDescending();

    // 유저가 즐겨찾기한 음식점 리스트들을 최신순으로 가져옴
    @Query("SELECT rf FROM RestaurantFavorite rf WHERE rf.user.userId = :userId ORDER BY rf.createdAt DESC")
    List<RestaurantFavorite> findFavoritesByUserId(@Param("userId") Integer userId);

    // 유저가 작성한 active 상태의 post 리스트들을 최신순으로 가져옴
    @Query("SELECT p FROM Post p WHERE p.user.userId = :userId AND p.status = 'ACTIVE' ORDER BY p.createdAt DESC")
    List<Post> findActivePostsByUserId(@Param("userId") Integer userId);

    // 유저가 스크랩한 post 리스트들을 최신순으로 가져옴
    @Query("SELECT ps FROM PostScrap ps WHERE ps.user.userId = :userId ORDER BY ps.post.createdAt DESC")
    List<PostScrap> findScrappedPostsByUserId(@Param("userId") Integer userId);

    // 유저가 작성한 active 상태의 post 댓글들을 최신순으로 가져옴
    @Query("SELECT pc FROM PostComment pc WHERE pc.user.userId = :userId AND pc.status = 'ACTIVE' ORDER BY pc.createdAt DESC")
    List<PostComment> findActivePostCommentsByUserId(@Param("userId") Integer userId);

}
