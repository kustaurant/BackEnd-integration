package com.kustaurant.kustaurant.post.post.infrastructure;

import static com.kustaurant.kustaurant.global.exception.ErrorCode.*;

import com.kustaurant.kustaurant.post.comment.infrastructure.entity.PostCommentEntity;
import com.kustaurant.kustaurant.post.post.domain.Post;
import com.kustaurant.kustaurant.post.post.enums.ContentStatus;
import com.kustaurant.kustaurant.post.post.infrastructure.entity.PostEntity;
import com.kustaurant.kustaurant.post.post.infrastructure.repositoryInterface.PostJpaRepository;
import com.kustaurant.kustaurant.post.post.infrastructure.repositoryInterface.PostLikeJpaRepository;
import com.kustaurant.kustaurant.post.post.infrastructure.repositoryInterface.PostDislikeJpaRepository;
import com.kustaurant.kustaurant.post.post.service.port.PostRepository;
import com.kustaurant.kustaurant.user.user.infrastructure.UserJpaRepository;
import com.kustaurant.kustaurant.global.exception.exception.business.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
    private final PostLikeJpaRepository postLikeJpaRepository;
    private final PostDislikeJpaRepository postDislikeJpaRepository;
    private final UserJpaRepository userJpaRepository;
    

    @Override
    public Page<Post> findAll(Pageable pageable) {
        return postJpaRepository.findAll(pageable).map(this::toDomainWithCounts);
    }

    @Override
    public List<Post> findAllById(List<Integer> ids) {
        return postJpaRepository.findAllById(ids).stream()
                .map(this::toDomainWithCounts)
                .collect(Collectors.toList());
    }
    
    @Override
    public Page<Post> findByStatus(ContentStatus status, Pageable pageable) {
        return postJpaRepository.findAll(pageable).map(this::toDomainWithCounts);
    }
    
    @Override
    public List<PostEntity> findActivePostsByUserId(Long userId) {
        return postJpaRepository.findActivePostsByUserId(userId);
    }
    
    @Override
    public Post findByStatusAndPostId(ContentStatus status, Integer postId) {
        return postJpaRepository.findById(postId).orElseThrow(() -> new DataNotFoundException(POST_NOT_FOUND)).toModel();
    }

    @Override
    public PostEntity save(PostEntity postEntity){
        return postJpaRepository.save(postEntity);
    }

    @Override
    public Post save(Post post) {
        PostEntity postEntity;
        if (post.getId() == null) {
            postEntity = PostEntity.from(post);
            postEntity.setCreatedAt(LocalDateTime.now());
            postEntity.setUpdatedAt(LocalDateTime.now());
        } else {
            postEntity = postJpaRepository.findById(post.getId())
                    .orElseThrow(() -> new DataNotFoundException(POST_NOT_FOUND, post.getId(), "게시물"));

            // 엔티티 상태 동기화
            postEntity.setPostTitle(post.getTitle());
            postEntity.setPostBody(post.getBody());
            postEntity.setPostCategory(post.getCategory());
            postEntity.setStatus(post.getStatus());
            postEntity.setUpdatedAt(LocalDateTime.now());
            // netLikes 필드 제거됨
            postEntity.setPostVisitCount(post.getVisitCount());
        }

        PostEntity saved = postJpaRepository.save(postEntity);
        return saved.toModel();
    }

    @Override
    public Optional<Post> findById(Integer postId) {
        return postJpaRepository.findById(postId).map(this::toDomainWithCounts);
    }

    @Override
    public Optional<Post> findByIdWithComments(Integer postId) {
        return postJpaRepository.findById(postId).map(this::toDomainWithCounts);
    }

    @Override
    public void increaseVisitCount(Integer postId) {
        PostEntity post = postJpaRepository.findById(postId)
                .orElseThrow(() -> new DataNotFoundException(POST_NOT_FOUND, postId, "게시글"));
        post.setPostVisitCount(post.getPostVisitCount() + 1);
        postJpaRepository.save(post);
    }

    @Override
    public void delete(Post post) {
        // SQLDelete 애노테이션에 의한 soft delete
        PostEntity postEntity = postJpaRepository.findById(post.getId())
                .orElseThrow(() -> new DataNotFoundException(POST_NOT_FOUND, post.getId(), "게시글"));
        postJpaRepository.delete(postEntity);
    }

    @Override
    public List<Post> findActiveByUserId(Long userId) {
        return postJpaRepository.findActivePostsByUserId(userId).stream()
                .map(this::toDomainWithCounts)
                .toList();
    }

    @Override
    public Page<Post> findByStatusAndCategory(ContentStatus status, String category, Pageable pageable) {
        Specification<PostEntity> spec = (root, query, cb) -> {
            // SQLRestriction에 의해 ACTIVE만 조회되므로 status 조건 제거
            return cb.equal(root.get("postCategory"), category);
        };
        return postJpaRepository.findAll(spec, pageable).map(this::toDomainWithCounts);
    }

    @Override
    public Page<Post> findByStatusAndPopularCount(ContentStatus status, int minLikeCount, Pageable pageable) {
        Specification<PostEntity> spec = (root, query, cb) -> {
            // SQLRestriction에 의해 ACTIVE만 조회되므로 status 조건 제거
            return cb.greaterThanOrEqualTo(root.get("netLikes"), minLikeCount);
        };
        return postJpaRepository.findAll(spec, pageable).map(this::toDomainWithCounts);
    }

    @Override
    public Page<Post> findByStatusAndCategoryAndPopularCount(ContentStatus status, String category, int minLikeCount, Pageable pageable) {
        Specification<PostEntity> spec = (root, query, cb) -> {
            // SQLRestriction에 의해 ACTIVE만 조회되므로 status 조건 제거
            Predicate categoryPredicate = cb.equal(root.get("postCategory"), category);
            Predicate likeCountPredicate = cb.greaterThanOrEqualTo(root.get("netLikes"), minLikeCount);
            return cb.and(categoryPredicate, likeCountPredicate);
        };
        return postJpaRepository.findAll(spec, pageable).map(this::toDomainWithCounts);
    }

    @Override
    public Page<Post> findByStatusAndSearchKeyword(ContentStatus status, String keyword, String category, int minLikeCount, Pageable pageable) {
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
                Predicate categoryPredicate = cb.equal(root.get("postCategory"), category);
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
