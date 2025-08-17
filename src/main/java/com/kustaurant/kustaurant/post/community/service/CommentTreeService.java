package com.kustaurant.kustaurant.post.community.service;

import com.kustaurant.kustaurant.common.dto.UserSummary;
import com.kustaurant.kustaurant.common.util.TimeAgoUtil;
import com.kustaurant.kustaurant.post.community.controller.response.CommentReply;
import com.kustaurant.kustaurant.post.community.controller.response.ParentComment;
import com.kustaurant.kustaurant.post.community.infrastructure.CommentQueryRepository;
import com.kustaurant.kustaurant.post.community.infrastructure.projection.PostCommentProjection;
import com.kustaurant.kustaurant.user.user.service.UserIconResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentTreeService {
    private final CommentQueryRepository commentQueryRepository;

    public List<ParentComment> getCommentTree(Integer postId, Long currentUserId) {
        List<PostCommentProjection> projections = commentQueryRepository.findComments(postId,currentUserId);
        // 부모 댓글 목록
        List<PostCommentProjection> parents = projections.stream()
                .filter(p -> p.parentCommentId() == null)
                .toList();
        // 자식 댓글 parentId별로 그룹핑
        Map<Integer, List<PostCommentProjection>> children = projections.stream()
                .filter(p -> p.parentCommentId() != null)
                .collect(Collectors.groupingBy(PostCommentProjection::parentCommentId,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
        return parents.stream()
                .map(parent -> {
                    UserSummary author = new UserSummary(
                            parent.writerId(),
                            parent.writerNickname(),
                            parent.writerEvalCount(),
                            UserIconResolver.resolve(parent.writerEvalCount())
                    );

                    List<CommentReply> replies = children
                            .getOrDefault(parent.commentId(), List.of())
                            .stream()
                            .sorted(Comparator.comparing(PostCommentProjection::createdAt)
                                    .thenComparing(PostCommentProjection::commentId))
                            .map(child -> new CommentReply(
                                    child.commentId(),
                                    child.parentCommentId(),
                                    child.body(),
                                    child.status().name(),
                                    child.likeCount(),
                                    child.dislikeCount(),
                                    TimeAgoUtil.toKor(child.createdAt()),
                                    child.myReaction(),
                                    currentUserId != null && currentUserId.equals(child.writerId()),
                                    new UserSummary(
                                            child.writerId(),
                                            child.writerNickname(),
                                            child.writerEvalCount(),
                                            UserIconResolver.resolve(child.writerEvalCount())
                                    )
                            ))
                            .toList();

                    return new ParentComment(
                            parent.commentId(),
                            parent.body(),
                            parent.status().name(),
                            parent.likeCount(),
                            parent.dislikeCount(),
                            TimeAgoUtil.toKor(parent.createdAt()),
                            parent.myReaction(),
                            currentUserId != null && currentUserId.equals(parent.writerId()),
                            replies.size(),
                            replies,
                            author
                    );
                })
                .toList();
    }
}
