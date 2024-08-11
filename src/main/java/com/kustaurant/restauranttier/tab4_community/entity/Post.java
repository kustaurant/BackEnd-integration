package com.kustaurant.restauranttier.tab4_community.entity;

import com.kustaurant.restauranttier.tab5_mypage.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "posts_tbl")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer postId;

    String postTitle;
    String postBody;
    String status;
    String postCategory;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Integer postVisitCount=0;
    Integer likeCount=0;
    public Post(String postTitle, String postBody, String postCategory, String status, LocalDateTime createdAt) {
        this.postTitle = postTitle;
        this.postBody = postBody;
        this.postCategory = postCategory;
        this.status = status;
        this.createdAt = createdAt;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @OneToMany(mappedBy = "post")
    List<PostComment> postCommentList = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    List<PostScrap> postScrapList = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    List<PostPhoto> postPhotoList= new ArrayList<>();

    public Post() {

    }

    @ManyToMany
    @JoinTable(name="post_dislikes_tbl",joinColumns = @JoinColumn(name="post_id"),inverseJoinColumns = @JoinColumn(name="user_id"))
    private List<User> dislikeUserList = new ArrayList<>();
    @ManyToMany
    @JoinTable(name="post_likes_tbl",joinColumns = @JoinColumn(name="post_id"),inverseJoinColumns = @JoinColumn(name="user_id"))
    private List<User> likeUserList = new ArrayList<>();
    public String calculateTimeAgo() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = this.getCreatedAt();

        // 연 차이 계산
        Long yearsDifference = ChronoUnit.YEARS.between(past, now);
        if (yearsDifference > 0) return yearsDifference.toString() + "년 전";

        // 월 차이 계산
        Long monthsDifference = ChronoUnit.MONTHS.between(past, now);
        if (monthsDifference > 0) return monthsDifference.toString() + "달 전";

        // 일 차이 계산
        Long daysDifference = ChronoUnit.DAYS.between(past, now);
        if (daysDifference > 0) return daysDifference.toString() + "일 전";

        // 시간 차이 계산
        Long hoursDifference = ChronoUnit.HOURS.between(past, now);
        if (hoursDifference > 0) return hoursDifference.toString() + "시간 전";

        // 분 차이 계산
        Long minutesDifference = ChronoUnit.MINUTES.between(past, now);
        if (minutesDifference > 0) return minutesDifference.toString() + "분 전";

        // 초 차이 계산
        Long secondsDifference = ChronoUnit.SECONDS.between(past, now);
        return secondsDifference.toString() + "초 전";
    }
}
