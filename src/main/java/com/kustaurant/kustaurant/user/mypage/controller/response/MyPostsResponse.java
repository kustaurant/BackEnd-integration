package com.kustaurant.kustaurant.user.mypage.controller.response;

import com.kustaurant.kustaurant.post.post.infrastructure.projection.PostDTOProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jsoup.Jsoup;

import java.time.LocalDateTime;

public record MyPostsResponse (
        Integer postId,
        String postCategory,
        String postTitle,
        String postImgUrl,
        String postBody,
        Integer likeCount,
        Integer commentCount,
        String timeAgo
){
    /**
     * PostDTOProjection을 MyPostsResponse로 변환
     */
    public static MyPostsResponse from(PostDTOProjection projection) {
        return new MyPostsResponse(
                projection.postId(),
                projection.postCategory(),
                projection.postTitle(),
                projection.firstPhotoUrl(),
                extractShortBody(projection.postBody()),
                projection.getNetLikes(),
                projection.getCommentCount(),
                calculateTimeAgo(projection.createdAt())
        );
    }
    
    /**
     * HTML 태그 제거 및 본문 요약
     */
    private static String extractShortBody(String htmlBody) {
        if (htmlBody == null || htmlBody.isEmpty()) {
            return "";
        }
        
        // HTML 태그 제거
        String plainText = Jsoup.parse(htmlBody).text();
        
        // 50자로 제한
        if (plainText.length() > 50) {
            return plainText.substring(0, 50) + "...";
        }
        
        return plainText;
    }
    
    /**
     * 시간 경과 계산
     */
    private static String calculateTimeAgo(LocalDateTime createdAt) {
        if (createdAt == null) return "";
        
        LocalDateTime now = LocalDateTime.now();
        long diffInMinutes = java.time.Duration.between(createdAt, now).toMinutes();

        if (diffInMinutes < 1) {
            return "방금 전";
        } else if (diffInMinutes < 60) {
            return diffInMinutes + "분 전";
        } else if (diffInMinutes < 1440) { // 24시간
            return (diffInMinutes / 60) + "시간 전";
        } else {
            return (diffInMinutes / 1440) + "일 전";
        }
    }
}
