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
    public Post save(Post post) {
        PostEntity postEntity;
        if (post.getId() == null) {
            postEntity = PostEntity.from(post);
        } else {
            postEntity = postJpaRepository.findById(post.getId())
                    .orElseThrow(() -> new DataNotFoundException(POST_NOT_FOUND, post.getId(), "게시물"));

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

    // TODO: n+1 문제 추후 처리 예정
    private Post toDomainWithCounts(PostEntity postEntity) {
        // DDD 리팩토링으로 count 필드 제거, 기본 도메인 객체 반환
        return postEntity.toModel();
    }
}
