package com.kustaurant.kustaurant.v1.community.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDTO {
    Integer postId;
    String postTitle;
    String postBody;
    String status;
    String postCategory;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    Integer likeCount;
    UserDTO user;
    String timeAgo;
    Integer commentCount;
    List<PostCommentDTO> postCommentList;
    String postPhotoImgUrl;
    Integer postVisitCount;
    Integer scrapCount;
    Boolean isScraped =false;
    Boolean isliked =false;
    Boolean isPostMine =false;

//    public static PostDTO from(PostDetailResponse d) {
//        PostDTO dto = new PostDTO();
//
//        // ID/기본 정보
//        dto.setPostId(Math.toIntExact(d.postId()));
//        dto.setPostTitle(d.title());
//        dto.setPostBody(d.body());
//        dto.setPostCategory(d.category().name()); // 문자열이 필요하면 name()
//        dto.setCreatedAt(d.createdAt());
//        dto.setUpdatedAt(d.updatedAt());
//        dto.setTimeAgo(d.timeAgo());
//
//        // 사진: v1은 단일 문자열, v2는 List<String>
//        dto.setPostPhotoImgUrl(
//                d.photoUrls() == null || d.photoUrls().isEmpty()
//                        ? null
//                        : d.photoUrls().get(0) // 첫 장만 사용(호환 목적)
//        );
//
//        // 집계 수치: v1은 Integer, v2는 long → 안전 변환
//        // 좋아요는 'likeOnlyCount'(순좋아요가 아니라 "좋아요 개수")가 v1의 의미에 더 가깝습니다.
//        dto.setLikeCount(Math.toIntExact(d.likeOnlyCount()));
//        dto.setCommentCount(Math.toIntExact(d.commentCount()));
//        dto.setPostVisitCount(Math.toIntExact(d.visitCount()));
//        dto.setScrapCount(Math.toIntExact(d.scrapCount()));
//
//        // 상태값: v2엔 없음 → 필요 시 기본값 또는 null
//        dto.setStatus(null);
//
//        // 상호작용
//        dto.setIsScraped(d.isScrapped());
//        dto.setIsliked(d.myReaction() != null && d.myReaction().name().equals("LIKE"));
//        dto.setIsPostMine(d.isPostMine());
//
//        // 작성자: v2는 @JsonUnwrapped(prefix="writer")로 JSON이 평탄화되지만,
//        // 자바 객체에선 UserSummary로 들어있음. UserDTO로 재구성 필요.
//        UserDTO u = new UserDTO();
//        // ⚠️ UserSummary의 필드/접근자 이름에 맞춰 아래 부분 수정하세요.
//        // 예: writer().id(), writer().nickname(), writer().evalCount() 등
//        // u.setId(Math.toIntExact(d.writer().id()));
//        // u.setNickname(d.writer().nickname());
//        // u.setEvalCount(d.writer().evalCount());
//        // u.setIconUrl(d.writer().userIconUrl());
//        dto.setUser(u);
//
//        // 댓글: 타입이 다름(List<ParentComment> → List<PostCommentDTO>)
//        dto.setPostCommentList(CommentMapper.from(d.comments()));
//
//        return dto;
//    }
}
