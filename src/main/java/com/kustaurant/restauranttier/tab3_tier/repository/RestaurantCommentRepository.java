package com.kustaurant.restauranttier.tab3_tier.repository;

import com.kustaurant.restauranttier.tab3_tier.entity.Restaurant;
import com.kustaurant.restauranttier.tab3_tier.entity.RestaurantComment;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantCommentRepository extends JpaRepository<RestaurantComment, Long> {

    @Query("SELECT e, " +
            "(SELECT COUNT(a) FROM e.restaurantCommentlikeList a) - (SELECT COUNT(d) FROM e.restaurantCommentdislikeList d) as likeDislikeDiff ," +
            "false, false " +
            "FROM RestaurantComment e " +
            "WHERE e.restaurant = :restaurant " +
            "AND e.status = 'ACTIVE' " +
            "ORDER BY likeDislikeDiff DESC")
    List<Object[]> findOrderPopular(@Param("restaurant") Restaurant restaurant);

    @Query("SELECT e, " +
            "(SELECT COUNT(a) FROM e.restaurantCommentlikeList a) - " +
            "(SELECT COUNT(d) FROM e.restaurantCommentdislikeList d) as likeDislikeDiff, " +
            "CASE WHEN EXISTS (" +
            "  SELECT 1 FROM RestaurantCommentlike rcl " +
            "  WHERE rcl.user = :user AND rcl.restaurantComment = e" +
            ") THEN true ELSE false END, " +
            "CASE WHEN EXISTS (" +
            "  SELECT 1 FROM RestaurantCommentdislike rcl " +
            "  WHERE rcl.user = :user AND rcl.restaurantComment = e" +
            ") THEN true ELSE false END " +
            "FROM RestaurantComment e " +
            "WHERE e.restaurant = :restaurant " +
            "AND e.status = 'ACTIVE' " +
            "ORDER BY likeDislikeDiff DESC")
    List<Object[]> findOrderPopular(
            @Param("restaurant") Restaurant restaurant,
            @Param("user") User user
    );

    @Query("SELECT e, COUNT(a) - COUNT(d), false, false " +
            "FROM RestaurantComment e " +
            "LEFT JOIN e.restaurantCommentlikeList a " +
            "LEFT JOIN e.restaurantCommentdislikeList d " +
            "WHERE e.restaurant = :restaurant " +
            "AND e.status = 'ACTIVE' " +
            "GROUP BY e " +
            "ORDER BY e.createdAt DESC")
    List<Object[]> findOrderLatest(@Param("restaurant") Restaurant restaurant);

    @Query("SELECT e, COUNT(a) - COUNT(d), " +
            "CASE WHEN EXISTS (" +
            "  SELECT 1 FROM RestaurantCommentlike rcl " +
            "  WHERE rcl.user = :user AND rcl.restaurantComment = e" +
            ") THEN true ELSE false END, " +
            "CASE WHEN EXISTS (" +
            "  SELECT 1 FROM RestaurantCommentdislike rcl " +
            "  WHERE rcl.user = :user AND rcl.restaurantComment = e" +
            ") THEN true ELSE false END " +
            "FROM RestaurantComment e " +
            "LEFT JOIN e.restaurantCommentlikeList a " +
            "LEFT JOIN e.restaurantCommentdislikeList d " +
            "WHERE e.restaurant = :restaurant " +
            "AND e.status = 'ACTIVE' " +
            "GROUP BY e " +
            "ORDER BY e.createdAt DESC")
    List<Object[]> findOrderLatest(
            @Param("restaurant") Restaurant restaurant,
            @Param("user") User user
    );

    @Query("SELECT (SELECT COUNT(a) FROM e.restaurantCommentlikeList a) - (SELECT COUNT(d) FROM e.restaurantCommentdislikeList d) " +
            "FROM RestaurantComment e " +
            "WHERE e.commentId = :commentId " +
            "AND e.status = 'ACTIVE'")
    Integer findLikeDislikeDiffByCommentId(@Param("commentId") Integer commentId);

    Optional<RestaurantComment> findByCommentId(Integer commentId);
}