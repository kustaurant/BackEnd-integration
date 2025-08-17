package com.kustaurant.kustaurant.post.post.infrastructure;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.post.comment.infrastructure.entity.PostCommentEntity;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.domain.enums.PostStatus;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.jpa.PostJpaRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostRepository;
import com.kustaurant.kustaurant.global.exception.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {
    private final PostJpaRepository postJpaRepository;

    @Override
    public List<Post> findAllById(List<Integer> ids) {
        return postJpaRepository.findAllById(ids).stream()
                .map(this::toDomainWithCounts)
                .collect(Collectors.toList());
    }
    
    @Override
    public Post findByStatusAndPostId(PostStatus status, Integer postId) {
        return postJpaRepository.findById(postId).orElseThrow(() -> new DataNotFoundException(POST_NOT_FOUND)).toModel();
    }

    @Override
    public Post save(Post post) {
        PostEntity postEntity;
        if (post.getId() == null) {
            postEntity = PostEntity.from(post);
        } else {
            postEntity = postJpaRepository.findById(post.getId())
                    .orElseThrow(() -> new DataNotFoundException(POST_NOT_FOUND, post.getId(), "게시물"));

            // 엔티티 상태 동기화
//            postEntity.setPostTitle(post.getTitle());
//            postEntity.setPostBody(post.getBody());
//            postEntity.setPostCategory(post.getCategory());
//            postEntity.setStatus(post.getStatus());
//            postEntity.setUpdatedAt(LocalDateTime.now());
//            // netLikes 필드 제거됨
//            postEntity.setPostVisitCount(post.getVisitCount());
        }

        PostEntity saved = postJpaRepository.save(postEntity);
        return saved.toModel();
    }

    @Override
    public Optional<Post> findById(Integer postId) {
        return postJpaRepository.findById(postId).map(this::toDomainWithCounts);
    }

    @Override
    public void increaseVisitCount(Integer postId) {
        PostEntity post = postJpaRepository.findById(postId)
                .orElseThrow(() -> new DataNotFoundException(POST_NOT_FOUND, postId, "게시글"));
//        post.setPostVisitCount(post.getPostVisitCount() + 1);
        postJpaRepository.save(post);
    }

    @Override
    public void delete(Integer id) {
        postJpaRepository.deleteById(id);
    }

    @Override
    public List<Post> findByUserId(Long userId) {
        return postJpaRepository.findActivePostsByUserId(userId).stream()
                .map(this::toDomainWithCounts)
                .toList();
    }

    // -------------------------------------------------------------

    @Override
    public Page<Post> findByStatusAndCategory(PostStatus status, String category, Pageable pageable) {
        Specification<PostEntity> spec = (root, query, cb) -> {
            // SQLRestriction에 의해 ACTIVE만 조회되므로 status 조건 제거
            return cb.equal(root.get("category"), category);
        };
        return postJpaRepository.findAll(spec, pageable).map(this::toDomainWithCounts);
    }

    @Override
    public Page<Post> findByStatusAndPopularCount(PostStatus status, int minLikeCount, Pageable pageable) {
        Specification<PostEntity> spec = (root, query, cb) -> {
            // SQLRestriction에 의해 ACTIVE만 조회되므로 status 조건 제거
            return cb.greaterThanOrEqualTo(root.get("netLikes"), minLikeCount);
        };
        return postJpaRepository.findAll(spec, pageable).map(this::toDomainWithCounts);
    }

    @Override
    public Page<Post> findByStatusAndCategoryAndPopularCount(PostStatus status, String category, int minLikeCount, Pageable pageable) {
        Specification<PostEntity> spec = (root, query, cb) -> {
            // SQLRestriction에 의해 ACTIVE만 조회되므로 status 조건 제거
            Predicate categoryPredicate = cb.equal(root.get("category"), category);
            Predicate likeCountPredicate = cb.greaterThanOrEqualTo(root.get("netLikes"), minLikeCount);
            return cb.and(categoryPredicate, likeCountPredicate);
        };
        return postJpaRepository.findAll(spec, pageable).map(this::toDomainWithCounts);
    }

    @Override
    public Page<Post> findByStatusAndSearchKeyword(PostStatus status, String keyword, String category, int minLikeCount, Pageable pageable) {
        Specification<PostEntity> spec = (root, query, cb) -> {
            query.distinct(true);
            
            // 조인
            Join<PostEntity, com.kustaurant.kustaurant.user.user.infrastructure.UserEntity> u1 = root.join("user", JoinType.LEFT);
            Join<PostEntity, PostCommentEntity> c = root.join("postCommentList", JoinType.LEFT);
            Join<PostCommentEntity, com.kustaurant.kustaurant.user.user.infrastructure.UserEntity> u2 = c.join("user", JoinType.LEFT);
            
            // SQLRestriction에 의해 ACTIVE만 조회되므로 status 조건 제거
            Predicate likeCountPredicate = cb.greaterThanOrEqualTo(root.get("netLikes"), minLikeCount);
            
            Predicate searchPredicate;
            if (!category.equals("전체")) {
                Predicate categoryPredicate = cb.equal(root.get("category"), category);
                searchPredicate = cb.and(categoryPredicate, cb.or(
                    cb.like(root.get("postTitle"), "%" + keyword + "%"),
                    cb.like(root.get("postBody"), "%" + keyword + "%"),
                    cb.like(u1.get("nickname").get("value"), "%" + keyword + "%")
                ));
            } else {
                searchPredicate = cb.or(
                    cb.like(root.get("postTitle"), "%" + keyword + "%"),
                    cb.like(root.get("postBody"), "%" + keyword + "%"),
                    cb.like(u1.get("nickname").get("value"), "%" + keyword + "%")
                );
            }
            
            return cb.and(likeCountPredicate, searchPredicate);
        };
        return postJpaRepository.findAll(spec, pageable).map(this::toDomainWithCounts);
    }

    // TODO: n+1 문제 추후 처리 예정
    private Post toDomainWithCounts(PostEntity postEntity) {
        // DDD 리팩토링으로 count 필드 제거, 기본 도메인 객체 반환
        return postEntity.toModel();
    }
}
