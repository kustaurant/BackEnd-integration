package com.kustaurant.kustaurant.post.comment.infrastructure;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.COMMENT_NOT_FOUNT;

import com.kustaurant.kustaurant.post.comment.domain.PostComment;
import com.kustaurant.kustaurant.post.comment.service.port.PostCommentRepository;
import com.kustaurant.kustaurant.post.post.enums.ContentStatus;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
@Slf4j
public class PostCommentRepositoryImpl implements PostCommentRepository {
    private final PostCommentJpaRepository postCommentJpaRepository;

    @Override
    public List<PostComment> findActiveByUserId(Long userId) {
        return postCommentJpaRepository.findActiveByUserIdOrderByCreatedAtDesc(userId).stream().map(PostCommentEntity::toDomain).toList();
    }

    @Override
    public List<PostComment> findParentComments(Integer postId) {
        Specification<PostCommentEntity> spec = (root, query, cb) -> {
            Predicate postIdPredicate = cb.equal(root.get("post").get("postId"), postId);
            Predicate parentNull = cb.isNull(root.get("parentComment"));
            Predicate statusActive = cb.equal(root.get("status"), ContentStatus.ACTIVE);
            return cb.and(postIdPredicate, parentNull, statusActive);
        };
        List<PostCommentEntity> parentComments = postCommentJpaRepository.findAll(spec);

        return parentComments.stream()
                .map(PostCommentEntity::toDomain)
                .collect(Collectors.toList());
    }




    @Override
    public Optional<PostComment> findById(Integer comment_id) {
        return postCommentJpaRepository.findById(comment_id).map(PostCommentEntity::toDomain);
    }
    @Override
    public Optional<PostComment> findByIdWithReplies(Integer commentId) {
        return postCommentJpaRepository.findById(commentId)
                .map(PostCommentEntity::toDomain);
    }

    @Override
    public PostComment save(PostComment comment) {
        // 신규 댓글 (id가 null)
        if (comment.getCommentId() == null) {
            PostCommentEntity entity = PostCommentEntity.from(comment);
            postCommentJpaRepository.save(entity);
            return entity.toDomain();
        }

        // 기존 댓글 수정
        PostCommentEntity entity = postCommentJpaRepository.findById(comment.getCommentId())
                .orElseThrow(() -> new DataNotFoundException(COMMENT_NOT_FOUNT, comment.getCommentId(), "댓글"));

        entity.setStatus(comment.getStatus());
        entity.setLikeCount(comment.getNetLikes());

        // 대댓글 등 추가 수정 로직
        for (PostComment reply : comment.getReplies()) {
            PostCommentEntity replyEntity = entity.getRepliesList().stream()
                    .filter(r -> r.getCommentId().equals(reply.getCommentId()))
                    .findFirst()
                    .orElseThrow(() -> new DataNotFoundException(COMMENT_NOT_FOUNT, "대댓글이 존재하지 않습니다."));
            replyEntity.setStatus(reply.getStatus());
            replyEntity.setLikeCount(reply.getNetLikes());
        }

        postCommentJpaRepository.save(entity);
        return entity.toDomain();
    }


}
