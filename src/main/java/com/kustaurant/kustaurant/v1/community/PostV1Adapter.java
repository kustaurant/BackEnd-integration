//package com.kustaurant.kustaurant.v1.community;
//
//import com.kustaurant.kustaurant.common.dto.UserSummary;
//import com.kustaurant.kustaurant.common.enums.ReactionType;
//import com.kustaurant.kustaurant.post.community.controller.response.CommentReply;
//import com.kustaurant.kustaurant.post.community.controller.response.ParentComment;
//import com.kustaurant.kustaurant.post.community.controller.response.PostDetailResponse;
//import com.kustaurant.kustaurant.v1.community.dto.PostCommentDTO;
//import com.kustaurant.kustaurant.v1.community.dto.PostDTO;
//import com.kustaurant.kustaurant.v1.community.dto.UserDTO;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public final class PostV1Adapter {
//    private PostV1Adapter() {}
//
//    public static PostDTO toV1(PostDetailResponse d) {
//        PostDTO dto = new PostDTO();
//
//        // 기본 정보
//        dto.setPostId(Math.toIntExact(d.postId()));
//        dto.setPostTitle(d.title());
//        dto.setPostBody(d.body());
//        dto.setPostCategory(d.category().name());
//        dto.setCreatedAt(d.createdAt());
//        dto.setUpdatedAt(d.updatedAt());
//        dto.setTimeAgo(d.timeAgo());
//
//        // 사진: v1은 단일 문자열만 있으므로 첫 장만 매핑
//        dto.setPostPhotoImgUrl(
//                d.photoUrls() == null || d.photoUrls().isEmpty() ? null : d.photoUrls().get(0)
//        );
//
//        // 집계(타입 변환 주의)
//        dto.setLikeCount(Math.toIntExact(d.likeOnlyCount()));   // '좋아요 개수'만 사용
//        dto.setCommentCount(Math.toIntExact(d.commentCount()));
//        dto.setPostVisitCount(Math.toIntExact(d.visitCount()));
//        dto.setScrapCount(Math.toIntExact(d.scrapCount()));
//
//        // v1 status는 v2에 개념 없음
//        dto.setStatus(null);
//
//        // 상호작용
//        dto.setIsScraped(d.isScrapped()); // 철자: scrapped(v2) -> scraped(v1)
//        dto.setIsliked(d.myReaction() != null && d.myReaction().isLike());
//        dto.setIsPostMine(d.isPostMine());
//
//        // 작성자
//        dto.setUser(UserDtoMapper.from(d.writer()));
//
//        // 댓글
//        dto.setPostCommentList(CommentMapper.parentsToV1(d.comments()));
//
//        return dto;
//    }
//
//    // ----- 작성자 매핑 -----
//    public static final class UserDtoMapper {
//        private UserDtoMapper() {}
//
//        public static UserDTO from(UserSummary s) {
//            if (s == null) return null;
//            UserDTO u = new UserDTO();
//            // ⚠️ 프로젝트의 UserDTO 필드명에 맞게 setter 수정하세요.
//            // 예시:
//            try {
//                // 가장 흔한 케이스
//                u.getClass().getMethod("setUserId", Integer.class)
//                        .invoke(u, Math.toIntExact(s.userId()));
//            } catch (ReflectiveOperationException ignore) {
//                // 대체: setId(Integer) 있을 수 있음
//                try {
//                    u.getClass().getMethod("setId", Integer.class)
//                            .invoke(u, Math.toIntExact(s.userId()));
//                } catch (ReflectiveOperationException ignored) {}
//            }
//            safeSet(u, "setNickname", s.nickname());
//            // evalCount / iconUrl 도 가능하면 세팅
//            try {
//                u.getClass().getMethod("setEvalCount", Integer.class)
//                        .invoke(u, Math.toIntExact(s.evalCount()));
//            } catch (ReflectiveOperationException ignored) {}
//            safeSet(u, "setIconUrl", s.iconUrl());
//            safeSet(u, "setUserIconUrl", s.iconUrl()); // 다른 이름일 수 있어 중복 시도
//            return u;
//        }
//
//        private static void safeSet(Object target, String setter, Object val) {
//            try {
//                var m = target.getClass().getMethod(setter, val == null ? String.class : val.getClass());
//                m.invoke(target, val);
//            } catch (ReflectiveOperationException ignored) {}
//        }
//    }
//
//    // ----- 댓글 매핑 -----
//    public static final class CommentMapper {
//        private CommentMapper() {}
//
//        public static List<PostCommentDTO> parentsToV1(List<ParentComment> parents) {
//            if (parents == null || parents.isEmpty()) return List.of();
//            List<PostCommentDTO> out = new ArrayList<>();
//            for (ParentComment pc : parents) {
//                out.add(fromParent(pc));
//            }
//            return out;
//        }
//
//        private static PostCommentDTO fromParent(ParentComment pc) {
//            PostCommentDTO c = new PostCommentDTO();
//            c.setCommentId(Math.toIntExact(pc.commentId()));
//            c.setCommentBody(pc.body());
//            c.setStatus(pc.status());
//            c.setLikeCount(Math.toIntExact(pc.likeCount()));
//            c.setDislikeCount(Math.toIntExact(pc.dislikeCount()));
//            // v2엔 createdAt/updatedAt이 없으므로 null 유지 (필요하면 Projection에 추가)
//            c.setCreatedAt(null);
//            c.setUpdatedAt(null);
//            c.setTimeAgo(pc.timeAgo());
//            c.setIsLiked(pc.reactionType() != null && pc.reactionType().isLike());
//            c.setIsDisliked(pc.reactionType() == ReactionType.DISLIKE);
//            c.setIsCommentMine(pc.isCommentMine());
//
//            // 작성자
//            c.setUser(UserDtoMapper.from(pc.user()));
//
//            // 대댓글
//            c.setRepliesList(repliesToV1(pc.replies()));
//            return c;
//        }
//
//        private static List<PostCommentDTO> repliesToV1(List<CommentReply> replies) {
//            if (replies == null || replies.isEmpty()) return List.of();
//            List<PostCommentDTO> out = new ArrayList<>();
//            for (CommentReply r : replies) {
//                PostCommentDTO c = new PostCommentDTO();
//                c.setCommentId(Math.toIntExact(r.commentId()));
//                c.setCommentBody(r.body());
//                c.setStatus(r.status());
//                c.setLikeCount(Math.toIntExact(r.likeCount()));
//                c.setDislikeCount(Math.toIntExact(r.dislikeCount()));
//                c.setCreatedAt(null);
//                c.setUpdatedAt(null);
//                c.setTimeAgo(r.timeAgo());
//                c.setIsLiked(r.reactionType() != null && r.reactionType().isLike());
//                c.setIsDisliked(r.reactionType() == ReactionType.DISLIKE);
//                c.setIsCommentMine(r.isCommentMine());
//                c.setUser(UserDtoMapper.from(r.user()));
//                // v1은 parentCommentId 필드가 없으니 생략
//                c.setRepliesList(List.of()); // 대댓글의 대댓글은 미지원이므로 빈 리스트
//                return out.add(c), out; // trick을 피하려면 아래 두 줄로
//            }
//            return out;
//        }
//    }
//}
