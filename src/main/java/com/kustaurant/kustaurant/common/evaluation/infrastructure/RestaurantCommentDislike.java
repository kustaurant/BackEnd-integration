package com.kustaurant.kustaurant.common.evaluation.infrastructure;

import com.kustaurant.kustaurant.common.user.infrastructure.User;
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
public class RestaurantCommentDislike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer likeId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="comment_id")
    private RestaurantComment restaurantComment;

    @ManyToOne
    @JoinColumn(name="evaluation_id")
    private Evaluation evaluation;

    public RestaurantCommentDislike(User user, RestaurantComment restaurantComment) {
        this.restaurantComment = restaurantComment;
        this.user = user;
        this.evaluation = null;
        this.createdAt = LocalDateTime.now();
    }

    public RestaurantCommentDislike(User user, Evaluation evaluation) {
        this.restaurantComment = null;
        this.user = user;
        this.evaluation = evaluation;
        this.createdAt = LocalDateTime.now();
    }

    private LocalDateTime createdAt;
}
