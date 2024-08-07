package com.kustaurant.restauranttier.tab4_community.entity;

import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@Table(name="post_scraps_tbl")
public class PostScrap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer scrapId;

    public PostScrap(User user, Post post, LocalDateTime createdAt) {
        this.user = user;
        this.post = post;
        this.createdAt = createdAt;
    }
    public PostScrap(){

    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name="post_id")
    Post post;

    LocalDateTime createdAt;

}
