package com.kustaurant.restauranttier.tab3_tier.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "restaurant_comment_dislikes_tbl")
public class RestaurantCommentdislike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer likeId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="comment_id")
    private RestaurantComment restaurantComment;

    public RestaurantCommentdislike(User user, RestaurantComment restaurantComment) {
        this.restaurantComment = restaurantComment;
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }

    private LocalDateTime createdAt;
}
