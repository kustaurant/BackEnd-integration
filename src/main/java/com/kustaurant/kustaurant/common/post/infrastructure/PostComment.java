package com.kustaurant.kustaurant.common.post.infrastructure;

import com.kustaurant.kustaurant.common.user.infrastructure.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name="post_comments_tbl")
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer commentId;
    String commentBody;
    String status;
    @ManyToOne
    @JoinColumn(name="parent_comment_id")
    PostComment parentComment;

    @OneToMany(mappedBy = "parentComment")
    List<PostComment> repliesList = new ArrayList<>();
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    // 웹 버전을 위한 totallikeCount 를 말함. 모바일에선 사용하지 않음
    Integer likeCount=0;

    public PostComment(String commentBody, String status, LocalDateTime createdAt, PostEntity postEntity, User user) {
        this.commentBody = commentBody;
        this.status = status;
        this.createdAt = createdAt;
        this.postEntity = postEntity;
        this.user = user;
    }

    @ManyToOne
    @JoinColumn(name="post_id")
    PostEntity postEntity;
    @ManyToOne
    @JoinColumn(name="user_id")
    User user;

    public PostComment() {

    }

    @ManyToMany
    @JoinTable(name="comment_likes_tbl",joinColumns = @JoinColumn(name="comment_id"),inverseJoinColumns = @JoinColumn(name="user_id"))
    List<User> likeUserList = new ArrayList<>();
    @ManyToMany
    @JoinTable(name="comment_dislikes_tbl",joinColumns = @JoinColumn(name="comment_id"),inverseJoinColumns = @JoinColumn(name="user_id"))

    List<User> dislikeUserList = new ArrayList<>();

    public String calculateTimeAgo() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = this.getCreatedAt();

        // 연 차이 계산
        Long yearsDifference = ChronoUnit.YEARS.between(past, now);
        if (yearsDifference > 0) return yearsDifference + "년 전";

        // 월 차이 계산
        Long monthsDifference = ChronoUnit.MONTHS.between(past, now);
        if (monthsDifference > 0) return monthsDifference + "달 전";

        // 일 차이 계산
        Long daysDifference = ChronoUnit.DAYS.between(past, now);
        if (daysDifference > 0) return daysDifference + "일 전";

        // 시간 차이 계산
        Long hoursDifference = ChronoUnit.HOURS.between(past, now);
        if (hoursDifference > 0) return hoursDifference + "시간 전";

        // 분 차이 계산
        Long minutesDifference = ChronoUnit.MINUTES.between(past, now);
        if (minutesDifference > 0) return minutesDifference + "분 전";

        // 초 차이 계산
        Long secondsDifference = ChronoUnit.SECONDS.between(past, now);
        return secondsDifference + "초 전";
    }
}
